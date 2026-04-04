package com.example.moodle.controller;

import com.example.moodle.util.SceneManager;
import com.example.moodle.util.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class NewsVlogController {

    @FXML private Label vlogTitle;
    @FXML private Label vlogSummary;
    @FXML private Label vlogStory;
    @FXML private ImageView vlogImage;

    @FXML
    public void initialize() {
        String selected = Session.getSelectedNewsVlog();
        if (selected == null || selected.isBlank()) {
            selected = "buet-icpc";
        }

        switch (selected) {
            case "buet-icpc" -> applyVlog(
                    "BUET Participated in ICPC Regional Final",
                    "/com/example/moodle/news/BUET_Team_BUET_Silver_hawks.jpg",
                    "BUET reached the ICPC regional final with strong team performance.",
                    "1. Team BUET Silver Hawks qualified after competitive regional rounds.\n"
                            + "2. Their preparation focused on advanced algorithm practice and speed.\n"
                            + "3. Final-round participation highlights BUET's strong programming culture.\n"
                            + "4. Future plans include stronger global ranking and wider student engagement.");

            case "dhaka-medical-beds" -> applyVlog(
                    "Dhaka Medical College Hospital Expansion Plan Announced",
                    "/com/example/moodle/news/dhaka-medical-beds.jpg",
                    "A new government redesign will transform Dhaka Medical College Hospital into a state-of-the-art medical facility.",
                    "1. The master plan includes construction of 27 new multi-storey buildings.\n"
                        + "2. Bed capacity will rise to 5,000 so more people can receive treatment.\n"
                        + "3. The expansion is designed to improve quality of care and hospital service delivery.\n"
                        + "4. It will also strengthen medical and nursing education through upgraded facilities.");

            case "future-vlog-1" -> applyVlog(
                    "KUET Graduate Mahin Rahman Joins ASU with Full Scholarship",
                    "/com/example/moodle/news/future-vlog-1.jpg",
                    "Mahin Rahman (URP 2K18, KUET) is pursuing a Master's in Urban and Environmental Planning at Arizona State University, Class of 2026.",
                    "1. Mahin Rahman, a graduate from URP 2K18 at KUET, has started his Master's journey at ASU.\n"
                        + "2. He received a fully funded scholarship for the program.\n"
                        + "3. He is currently serving as a Graduate Research Assistant.\n"
                        + "4. His achievement reflects the global potential of KUET graduates.");

            case "future-vlog-2" -> applyVlog(
                    "BAU Master's Student Builds AI System for Livestock Farms",
                    "/com/example/moodle/news/future-vlog-2.jpg",
                    "Al Momen Pranta developed a sensor-based AI system to improve livestock and poultry farm management.",
                    "1. The innovation automatically monitors temperature and humidity in farm environments.\n"
                        + "2. AI is used to reduce heat stress in animals and improve welfare outcomes.\n"
                        + "3. Better environmental control can increase farm productivity and resilience.\n"
                        + "4. This has strong potential for climate-resilient livestock farming in Bangladesh and beyond.");

            default -> applyVlog(
                    "News Vlog",
                    "/com/example/moodle/news/buet-icpc.jpg",
                    "No specific vlog was selected.",
                    "Return to Home and select a vlog card to open details.");
        }
    }

    private void applyVlog(String title, String imagePath, String summary, String story) {
        vlogTitle.setText(title);
        vlogSummary.setText(summary);
        vlogStory.setText(story);

        java.net.URL imageUrl = getClass().getResource(imagePath);
        if (imageUrl != null) {
            vlogImage.setImage(new Image(imageUrl.toExternalForm()));
        } else {
            vlogImage.setImage(null);
        }
    }

    @FXML
    private void goHome() {
        SceneManager.switchScene("home.fxml");
    }

    @FXML
    private void goBack() {
        SceneManager.goBack();
    }
}
