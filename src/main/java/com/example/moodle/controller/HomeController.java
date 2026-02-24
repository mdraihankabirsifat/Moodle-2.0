package com.example.moodle.controller;

//import com.example.moodle.util.BackgroundAnimator;
import com.example.moodle.util.SceneManager;
import com.example.moodle.util.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;

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
    private Pane floatingLayer;
    @FXML
    private Button signupButton;

    @FXML
    public void initialize() {

        if (Session.isLoggedIn()) {

            loginButton.setVisible(false);
            signupButton.setVisible(false);
            campusButton.setVisible(true);
            profileMenu.setVisible(true);

            nameItem.setText("Name: " + Session.getName());
            universityItem.setText("University: " + Session.getUniversity());
            emailItem.setText("Email: " + Session.getEmail());

        } else {

            loginButton.setVisible(true);
            signupButton.setVisible(true);
            campusButton.setVisible(false);
            profileMenu.setVisible(false);
        }
    }

    @FXML
    private void goHome() {
        SceneManager.switchScene("home.fxml");
    }

    @FXML
    private void goToSignup() {
        //System.out.println("Signup clicked");
        SceneManager.switchScene("signup.fxml");
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

    @FXML
    private void goBack() {
        SceneManager.goBack();
    }
}
