package com.example.moodle.util;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

public final class ThemeManager {

    public static final String NORMAL_THEME = "normal-theme";
    public static final String DARK_THEME = "dark-theme";
    public static final String WHITE_THEME = "white-theme";

    private static String currentTheme = DARK_THEME;

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
            case "dark" -> setTheme(DARK_THEME);
            case "light" -> setTheme(WHITE_THEME);
            default -> setTheme(DARK_THEME);
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

        root.getStyleClass().removeAll(NORMAL_THEME, DARK_THEME, WHITE_THEME);
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
        if (DARK_THEME.equals(themeClass)) return DARK_THEME;
        if (WHITE_THEME.equals(themeClass)) return WHITE_THEME;
        return DARK_THEME;
    }

    private static String toLabel(String themeClass) {
        if (DARK_THEME.equals(themeClass)) return "Dark";
        return "Light";
    }
}