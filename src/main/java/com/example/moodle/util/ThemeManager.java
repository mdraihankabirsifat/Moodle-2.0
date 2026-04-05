package com.example.moodle.util;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

public final class ThemeManager {

    public static final String CYBER_DARK = "cyber-dark";
    public static final String CYBER_LIGHT = "cyber-light";

    private static String currentTheme = CYBER_DARK;

    private ThemeManager() {
    }

    public static String getCurrentTheme() {
        return currentTheme;
    }

    public static String getCurrentThemeLabel() {
        return toLabel(currentTheme);
    }

    public static void setThemeByLabel(String label) {
        if (label == null) return;
        switch (label.trim().toLowerCase()) {
            case "dark" -> setTheme(CYBER_DARK);
            case "light" -> setTheme(CYBER_LIGHT);
            default -> setTheme(CYBER_DARK);
        }
    }

    public static void setTheme(String themeClass) {
        String normalized = normalize(themeClass);
        if (normalized.equals(currentTheme)) {
            return;
        }
        currentTheme = normalized;
        applyToAllOpenWindows();
    }

    public static void applyTheme(Scene scene) {
        if (scene == null) {
            return;
        }
        Parent root = scene.getRoot();
        if (root == null) {
            return;
        }

        root.getStyleClass().removeAll(CYBER_DARK, CYBER_LIGHT);
        if (!root.getStyleClass().contains(currentTheme)) {
            root.getStyleClass().add(currentTheme);
        }
    }

    private static void applyToAllOpenWindows() {
        for (Window window : Window.getWindows()) {
            if (window instanceof Stage stage) {
                Scene scene = stage.getScene();
                applyTheme(scene);
            }
        }
    }

    private static String normalize(String themeClass) {
        if (CYBER_LIGHT.equals(themeClass)) return CYBER_LIGHT;
        return CYBER_DARK;
    }

    private static String toLabel(String themeClass) {
        if (CYBER_LIGHT.equals(themeClass)) return "Light";
        return "Dark";
    }
}