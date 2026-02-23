package com.example.moodle.controller;

import com.example.moodle.util.SceneManager;
import com.example.moodle.util.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

public class HomeController {

    @FXML
    private Button loginButton;

    @FXML
    private Button campusButton;

    @FXML
    private MenuButton profileMenu;

    @FXML
    private MenuItem nameItem;

    @FXML
    private MenuItem universityItem;

    @FXML
    private MenuItem emailItem;

    @FXML
    public void initialize() {

        if (Session.isLoggedIn()) {

            loginButton.setVisible(false);
            campusButton.setVisible(true);
            profileMenu.setVisible(true);

            nameItem.setText("Name: " + Session.getName());
            universityItem.setText("University: " + Session.getUniversity());
            emailItem.setText("Email: " + Session.getEmail());
        }
    }

    @FXML
    private void goToLogin() {
        SceneManager.switchScene("login.fxml");
    }

    @FXML
    private void goToCampus() {
        SceneManager.switchScene("campus-access.fxml");
    }

    @FXML
    private void logout() {
        Session.logout();
        SceneManager.switchScene("home.fxml");
    }
}