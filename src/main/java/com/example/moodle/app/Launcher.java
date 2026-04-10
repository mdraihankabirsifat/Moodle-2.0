package com.example.moodle.app;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Launcher {
    public static void main(String[] args) {
        if (launchDirect(args)) {
            return;
        }

        int exit = launchViaMavenWrapper();
        if (exit != 0) {
            System.err.println("Unable to start JavaFX app from plain Java runtime.");
            System.err.println("Run from project root with: .\\mvnw.cmd -DskipTests javafx:run");
            System.exit(exit == -1 ? 1 : exit);
        }
    }

    private static boolean launchDirect(String[] args) {
        try {
            Class<?> applicationClass = Class.forName("javafx.application.Application");
            Class<?> appClass = Class.forName("com.example.moodle.app.MoodleApp");
            Method launchMethod = applicationClass.getMethod("launch", Class.class, String[].class);
            launchMethod.invoke(null, appClass, (Object) args);
            return true;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                cause.printStackTrace();
            } else {
                e.printStackTrace();
            }
            return true;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return true;
        }
    }

    private static int launchViaMavenWrapper() {
        Path root = findProjectRoot();
        if (root == null) {
            return -1;
        }

        boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("win");
        List<String> command = new ArrayList<>();
        if (isWindows) {
            command.add("cmd");
            command.add("/c");
            command.add("mvnw.cmd");
        } else {
            command.add("./mvnw");
        }
        command.add("-DskipTests");
        command.add("javafx:run");

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(root.toFile());
        pb.inheritIO();
        try {
            Process p = pb.start();
            return p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static Path findProjectRoot() {
        Path current = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        while (current != null) {
            if (Files.exists(current.resolve("pom.xml"))) {
                return current;
            }
            current = current.getParent();
        }
        return null;
    }
}