package com.example.moodle.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Minimal peer-to-peer bridge for direct messages between two app instances.
 */
public final class MessageNetworkBridge {

    public static final int DEFAULT_PORT = 50555;

    private static final int CONNECT_TIMEOUT_MS = 2500;
    private static final int IO_TIMEOUT_MS = 4000;

    private static final ExecutorService SEND_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "moodle-message-sender");
        t.setDaemon(true);
        return t;
    });

    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

    private static volatile ServerSocket serverSocket;
    private static volatile Thread serverThread;
    private static volatile boolean running;

    private static volatile InetSocketAddress connectedPeer;
    private static volatile String connectionStatus = "Not connected";
    private static volatile String serverStatus = "Server stopped";

    private MessageNetworkBridge() {
    }

    public static synchronized void startServer() {
        if (running) {
            return;
        }

        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            running = true;
            serverStatus = "Listening on " + getLocalAddressHint();

            serverThread = new Thread(MessageNetworkBridge::acceptLoop, "moodle-message-server");
            serverThread.setDaemon(true);
            serverThread.start();
        } catch (IOException e) {
            running = false;
            serverStatus = "Server start failed: " + safeError(e);
        }
    }

    public static synchronized void shutdown() {
        running = false;

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
                // Ignore close errors during shutdown.
            }
            serverSocket = null;
        }

        if (serverThread != null) {
            serverThread.interrupt();
            serverThread = null;
        }

        connectedPeer = null;
        connectionStatus = "Not connected";
        serverStatus = "Server stopped";

        SEND_EXECUTOR.shutdownNow();
    }

    public static boolean isServerRunning() {
        return running;
    }

    public static String getServerStatus() {
        return serverStatus;
    }

    public static String getLocalAddressHint() {
        return detectLocalIPv4() + ":" + DEFAULT_PORT;
    }

    public static synchronized boolean connectToPeer(String rawAddress) {
        InetSocketAddress target = parseAddress(rawAddress);
        if (target == null) {
            connectionStatus = "Invalid server address. Use host or host:port";
            return false;
        }

        if (target.isUnresolved()) {
            connectionStatus = "Cannot resolve host: " + target.getHostString();
            return false;
        }

        if (!ping(target)) {
            connectionStatus = "Unable to connect to " + formatAddress(target);
            connectedPeer = null;
            return false;
        }

        connectedPeer = target;
        connectionStatus = "Connected to " + formatAddress(target);
        return true;
    }

    public static synchronized void disconnectPeer() {
        connectedPeer = null;
        connectionStatus = "Not connected";
    }

    public static boolean isConnected() {
        return connectedPeer != null;
    }

    public static String getConnectionStatus() {
        return connectionStatus;
    }

    public static String getConnectedPeer() {
        InetSocketAddress peer = connectedPeer;
        return peer == null ? "" : formatAddress(peer);
    }

    public static void sendDirectMessage(String from, String to, String content, String timestamp) {
        InetSocketAddress peer = connectedPeer;
        if (peer == null) {
            return;
        }

        SEND_EXECUTOR.submit(() -> sendNow(peer, from, to, content, timestamp));
    }

    private static void acceptLoop() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                handleConnection(socket);
            } catch (SocketException e) {
                if (running) {
                    serverStatus = "Server socket error: " + safeError(e);
                }
                break;
            } catch (IOException e) {
                if (running) {
                    serverStatus = "Server IO error: " + safeError(e);
                }
            }
        }
    }

    private static void handleConnection(Socket socket) {
        try (socket;
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

            socket.setSoTimeout(IO_TIMEOUT_MS);
            String line = reader.readLine();
            if (line == null || line.isBlank()) {
                writer.write("ERR");
                writer.newLine();
                writer.flush();
                return;
            }

            if ("PING".equals(line)) {
                writer.write("PONG");
                writer.newLine();
                writer.flush();
                return;
            }

            if (!line.startsWith("MSG|")) {
                writer.write("ERR");
                writer.newLine();
                writer.flush();
                return;
            }

            String[] parts = line.split("\\|", 5);
            if (parts.length != 5) {
                writer.write("ERR");
                writer.newLine();
                writer.flush();
                return;
            }

            String from = decode(parts[1]);
            String to = decode(parts[2]);
            String content = decode(parts[3]);
            String timestamp = decode(parts[4]);

            if (connectedPeer == null) {
                InetAddress remoteAddress = socket.getInetAddress();
                if (remoteAddress != null) {
                    connectedPeer = new InetSocketAddress(remoteAddress.getHostAddress(), DEFAULT_PORT);
                    connectionStatus = "Connected to " + formatAddress(connectedPeer) + " (auto)";
                }
            }

            DataStore.storeIncomingNetworkMessage(from, to, content, timestamp);

            writer.write("OK");
            writer.newLine();
            writer.flush();

        } catch (IOException ignored) {
            // Incoming connection failed or closed early.
        }
    }

    private static void sendNow(InetSocketAddress target,
                                String from,
                                String to,
                                String content,
                                String timestamp) {

        try (Socket socket = new Socket()) {
            socket.connect(target, CONNECT_TIMEOUT_MS);
            socket.setSoTimeout(IO_TIMEOUT_MS);

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

                writer.write("MSG|" + encode(from) + "|" + encode(to) + "|" + encode(content) + "|" + encode(timestamp));
                writer.newLine();
                writer.flush();

                String ack = reader.readLine();
                if (!"OK".equals(ack)) {
                    connectionStatus = "Connected to " + formatAddress(target) + " (last send unconfirmed)";
                }
            }
        } catch (IOException e) {
            connectionStatus = "Disconnected from " + formatAddress(target) + ": " + safeError(e);
            connectedPeer = null;
        }
    }

    private static boolean ping(InetSocketAddress target) {
        try (Socket socket = new Socket()) {
            socket.connect(target, CONNECT_TIMEOUT_MS);
            socket.setSoTimeout(IO_TIMEOUT_MS);

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

                writer.write("PING");
                writer.newLine();
                writer.flush();

                String pong = reader.readLine();
                return "PONG".equals(pong);
            }
        } catch (IOException e) {
            return false;
        }
    }

    private static InetSocketAddress parseAddress(String raw) {
        if (raw == null) {
            return null;
        }

        String value = raw.trim();
        if (value.isEmpty()) {
            return null;
        }

        String host = value;
        int port = DEFAULT_PORT;

        if (value.startsWith("[") && value.contains("]")) {
            int closing = value.indexOf(']');
            host = value.substring(1, closing);
            if (value.length() > closing + 1) {
                if (value.charAt(closing + 1) != ':') {
                    return null;
                }
                String portPart = value.substring(closing + 2);
                port = parsePort(portPart);
                if (port <= 0) {
                    return null;
                }
            }
        } else {
            int firstColon = value.indexOf(':');
            int lastColon = value.lastIndexOf(':');
            if (firstColon > 0 && firstColon == lastColon) {
                host = value.substring(0, firstColon).trim();
                String portPart = value.substring(firstColon + 1).trim();
                port = parsePort(portPart);
                if (port <= 0) {
                    return null;
                }
            }
        }

        if (host.isEmpty()) {
            return null;
        }

        return new InetSocketAddress(host, port);
    }

    private static int parsePort(String rawPort) {
        if (rawPort == null || rawPort.isBlank()) {
            return -1;
        }
        try {
            int value = Integer.parseInt(rawPort.trim());
            return (value >= 1 && value <= 65535) ? value : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String formatAddress(InetSocketAddress address) {
        return address.getHostString() + ":" + address.getPort();
    }

    private static String encode(String value) {
        String safe = value == null ? "" : value;
        return BASE64_ENCODER.encodeToString(safe.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        try {
            byte[] decoded = BASE64_DECODER.decode(value);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    private static String detectLocalIPv4() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface network = interfaces.nextElement();
                if (!network.isUp() || network.isLoopback()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = network.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException ignored) {
            // Fall through to host lookup.
        }

        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (IOException ignored) {
            return "127.0.0.1";
        }
    }

    private static String safeError(Exception e) {
        String msg = e.getMessage();
        return (msg == null || msg.isBlank()) ? e.getClass().getSimpleName() : msg;
    }
}
