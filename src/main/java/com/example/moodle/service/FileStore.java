package com.example.moodle.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileStore {

    private static final String DATA_DIR =
            System.getProperty("user.home") + File.separator + ".moodle_data";

    public static void ensureDir() {
        new File(DATA_DIR).mkdirs();
    }

    private static String getFilePath(String filename) {
        ensureDir();
        return DATA_DIR + File.separator + filename;
    }

    public static List<String> loadLines(String filename) {
        File file = new File(getFilePath(filename));
        if (!file.exists()) return new ArrayList<>();
        try {
            return new ArrayList<>(Files.readAllLines(file.toPath()));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void saveLines(String filename, List<String> lines) {
        try {
            Files.write(Paths.get(getFilePath(filename)), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendLine(String filename, String line) {
        try {
            ensureDir();
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(getFilePath(filename), true))) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
