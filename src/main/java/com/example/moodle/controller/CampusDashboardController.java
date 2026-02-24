package com.example.moodle.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.example.moodle.model.Assignment;
import com.example.moodle.model.Course;
import com.example.moodle.model.Message;
import com.example.moodle.model.Payment;
import com.example.moodle.service.DataStore;
import com.example.moodle.util.SceneManager;
import com.example.moodle.util.Session;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class CampusDashboardController {

    @FXML
    private StackPane contentArea;

    // Persistent data stores for simulation
    private static final List<String> submittedProjects = new ArrayList<>();
    private static final List<String> notices = new ArrayList<>();
    private static String hallRoom = null;
    private static final Map<String, Double> grades = new HashMap<>();
    private static int vendingBalance = 500;
    private static final List<String> washingSlots = new ArrayList<>();
    private static final List<String> gameRegistrations = new ArrayList<>();

    static {
        // Seed some default data
        if (notices.isEmpty()) {
            notices.add("Welcome to your campus dashboard!");
            notices.add("Mid-term exam schedule has been published.");
            notices.add("Library will remain open 24/7 during exam week.");
            notices.add("Campus maintenance scheduled for Saturday.");
        }
        if (grades.isEmpty()) {
            grades.put("CSE 101 - Intro to Programming", 3.75);
            grades.put("MATH 201 - Calculus II", 3.50);
            grades.put("PHY 101 - Physics I", 3.25);
            grades.put("ENG 102 - English Composition", 3.75);
            grades.put("CSE 203 - Data Structures", 4.00);
        }
    }

    @FXML
    public void initialize() {
        showNotices();
    }

    @FXML
    private void goHome() {
        SceneManager.switchScene("home.fxml");
    }

    @FXML
    private void goToCampus() {
        if (Session.isCampusVerified()) {
            SceneManager.switchScene(Session.getCampusDashboardFxml());
        } else {
            SceneManager.switchScene("campus-access.fxml");
        }
    }

    @FXML
    private void signOutCampus() {
        Session.logout();
        SceneManager.switchScene("home.fxml");
    }

    @FXML
    private void goBack() {
        SceneManager.goBack();
    }

    // ===================== PROJECT SUBMISSION =====================

    @FXML
    private void showProjects() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Project Submission");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        TextField projectName = new TextField();
        projectName.setPromptText("Project Title");

        ComboBox<String> courseBox = new ComboBox<>();
        courseBox.setPromptText("Select Course");
        courseBox.getItems().addAll("CSE 101", "CSE 203", "MATH 201", "PHY 101", "ENG 102");
        courseBox.setMaxWidth(Double.MAX_VALUE);

        TextArea descArea = new TextArea();
        descArea.setPromptText("Project Description");
        descArea.setPrefRowCount(3);

        Label msgLabel = new Label();

        Button submitBtn = new Button("Submit Project");
        submitBtn.setOnAction(e -> {
            String pName = projectName.getText().trim();
            String course = courseBox.getValue();
            if (pName.isEmpty() || course == null) {
                msgLabel.setStyle("-fx-text-fill: red;");
                msgLabel.setText("Please fill project title and select course.");
            } else {
                submittedProjects.add(pName + " (" + course + ")");
                msgLabel.setStyle("-fx-text-fill: green;");
                msgLabel.setText("Project \"" + pName + "\" submitted successfully!");
                projectName.clear();
                courseBox.setValue(null);
                descArea.clear();
                refreshProjectList(box);
            }
        });

        box.getChildren().addAll(title, projectName, courseBox, descArea, submitBtn, msgLabel, new Separator());

        // Show previously submitted projects
        refreshProjectList(box);

        setScrollContent(box);
    }

    private void refreshProjectList(VBox parent) {
        // Remove old list if present
        parent.getChildren().removeIf(n -> "project-list".equals(n.getId()));

        VBox listBox = new VBox(6);
        listBox.setId("project-list");
        Label listTitle = new Label("Submitted Projects:");
        listTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        listBox.getChildren().add(listTitle);

        if (submittedProjects.isEmpty()) {
            listBox.getChildren().add(new Label("No projects submitted yet."));
        } else {
            for (int i = 0; i < submittedProjects.size(); i++) {
                Label item = new Label((i + 1) + ". " + submittedProjects.get(i));
                item.setStyle("-fx-padding: 4 8 4 8; -fx-background-color: #f0f4ff; -fx-background-radius: 6;");
                listBox.getChildren().add(item);
            }
        }
        parent.getChildren().add(listBox);
    }

    // ===================== HALL MANAGEMENT =====================

    @FXML
    private void showHall() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Hall Management");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        Label statusLabel = new Label();
        if (hallRoom != null) {
            statusLabel.setText("Current Room: " + hallRoom);
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
        } else {
            statusLabel.setText("No room allocated yet.");
            statusLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 14px;");
        }

        Label subTitle = new Label("Available Halls:");
        subTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

        String[] halls = {"Shahid Smriti Hall", "Titumir Hall", "Sher-e-Bangla Hall", "Kabi Nazrul Hall", "Bangamata Hall"};
        String[] rooms = {"Room 101", "Room 205", "Room 312", "Room 408", "Room 503"};

        VBox hallList = new VBox(10);
        Label msgLabel = new Label();
        Random rand = new Random();

        for (int i = 0; i < halls.length; i++) {
            final String hallName = halls[i];
            final String roomNum = rooms[i];
            int available = 5 + rand.nextInt(20);

            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-padding: 10; -fx-background-color: #f8f9ff; -fx-background-radius: 8;");

            VBox info = new VBox(3);
            Label hName = new Label(hallName);
            hName.setStyle("-fx-font-weight: bold;");
            Label avail = new Label("Available: " + available + " rooms");
            avail.setStyle("-fx-text-fill: #666;");
            info.getChildren().addAll(hName, avail);

            Button allocBtn = new Button("Request " + roomNum);
            allocBtn.setOnAction(e -> {
                hallRoom = hallName + " - " + roomNum;
                statusLabel.setText("Current Room: " + hallRoom);
                statusLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
                msgLabel.setStyle("-fx-text-fill: green;");
                msgLabel.setText("Room allocated: " + hallRoom);
            });

            row.getChildren().addAll(info, allocBtn);
            hallList.getChildren().add(row);
        }

        box.getChildren().addAll(title, statusLabel, new Separator(), subTitle, hallList, msgLabel);
        setScrollContent(box);
    }

    // ===================== SCHEDULE =====================

    @FXML
    private void showSchedule() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Class Schedule");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setStyle("-fx-background-color: #ddd;");

        String[] days = {"Time", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};
        String[][] schedule = {
                {"08:00", "CSE 101", "MATH 201", "CSE 101", "MATH 201", "PHY 101"},
                {"09:30", "PHY 101", "ENG 102", "PHY Lab", "ENG 102", "CSE 203"},
                {"11:00", "CSE 203", "CSE 101", "MATH 201", "CSE 203", "ENG 102"},
                {"02:00", "Free", "CSE Lab", "Free", "CSE Lab", "Free"},
        };

        // Header
        for (int c = 0; c < days.length; c++) {
            Label cell = new Label(days[c]);
            cell.setStyle("-fx-font-weight: bold; -fx-padding: 8 12 8 12; -fx-background-color: #1e3c72; -fx-text-fill: white; -fx-min-width: 100;");
            cell.setMaxWidth(Double.MAX_VALUE);
            grid.add(cell, c, 0);
        }

        // Data
        for (int r = 0; r < schedule.length; r++) {
            for (int c = 0; c < schedule[r].length; c++) {
                Label cell = new Label(schedule[r][c]);
                String bg = c == 0 ? "#e8ecf4" : (schedule[r][c].equals("Free") ? "#f5f5f5" : "white");
                cell.setStyle("-fx-padding: 8 12 8 12; -fx-background-color: " + bg + "; -fx-min-width: 100;");
                cell.setMaxWidth(Double.MAX_VALUE);
                grid.add(cell, c, r + 1);
            }
        }

        box.getChildren().addAll(title, grid);
        setScrollContent(box);
    }

    // ===================== INTERNAL NOTICES =====================

    @FXML
    private void showNotices() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Internal Notices");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        VBox noticeList = new VBox(8);
        for (int i = 0; i < notices.size(); i++) {
            Label notice = new Label("📌  " + notices.get(i));
            notice.setWrapText(true);
            notice.setStyle("-fx-padding: 10 14 10 14; -fx-background-color: #fff8e1; " +
                    "-fx-background-radius: 8; -fx-border-color: #ffe082; -fx-border-radius: 8; -fx-font-size: 14px;");
            noticeList.getChildren().add(notice);
        }

        box.getChildren().addAll(title, noticeList);
        setScrollContent(box);
    }

    // ===================== GRADESHEET =====================

    @FXML
    private void showGradesheet() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Gradesheet");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        Label studentInfo = new Label("Student: " + Session.getName() + "  |  ID: " + Session.getStudentId());
        studentInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setStyle("-fx-background-color: #ddd;");

        // Header
        String[] headers = {"Course", "Grade Point", "Letter Grade"};
        for (int c = 0; c < headers.length; c++) {
            Label cell = new Label(headers[c]);
            cell.setStyle("-fx-font-weight: bold; -fx-padding: 10 16 10 16; -fx-background-color: #1e3c72; -fx-text-fill: white; -fx-min-width: 180;");
            cell.setMaxWidth(Double.MAX_VALUE);
            grid.add(cell, c, 0);
        }

        int row = 1;
        double totalPoints = 0;
        for (Map.Entry<String, Double> entry : grades.entrySet()) {
            Label courseCell = new Label(entry.getKey());
            courseCell.setStyle("-fx-padding: 8 16 8 16; -fx-background-color: white; -fx-min-width: 180;");
            courseCell.setMaxWidth(Double.MAX_VALUE);

            Label gpCell = new Label(String.format("%.2f", entry.getValue()));
            gpCell.setStyle("-fx-padding: 8 16 8 16; -fx-background-color: white; -fx-min-width: 180;");
            gpCell.setMaxWidth(Double.MAX_VALUE);

            Label lgCell = new Label(getLetterGrade(entry.getValue()));
            lgCell.setStyle("-fx-padding: 8 16 8 16; -fx-background-color: white; -fx-min-width: 180;");
            lgCell.setMaxWidth(Double.MAX_VALUE);

            grid.add(courseCell, 0, row);
            grid.add(gpCell, 1, row);
            grid.add(lgCell, 2, row);
            totalPoints += entry.getValue();
            row++;
        }

        double cgpa = grades.isEmpty() ? 0 : totalPoints / grades.size();
        Label cgpaLabel = new Label(String.format("CGPA: %.2f", cgpa));
        cgpaLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e3c72; -fx-padding: 10 0 0 0;");

        box.getChildren().addAll(title, studentInfo, grid, cgpaLabel);
        setScrollContent(box);
    }

    private String getLetterGrade(double gp) {
        if (gp >= 4.0) return "A+";
        if (gp >= 3.75) return "A";
        if (gp >= 3.50) return "A-";
        if (gp >= 3.25) return "B+";
        if (gp >= 3.0) return "B";
        if (gp >= 2.75) return "B-";
        if (gp >= 2.50) return "C+";
        if (gp >= 2.25) return "C";
        return "F";
    }

    // ===================== VENDING MACHINE =====================

    @FXML
    private void showVending() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Vending Machine");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        Label balanceLabel = new Label("Balance: ৳" + vendingBalance);
        balanceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: green;");

        String[] items = {"Coffee", "Tea", "Chips", "Chocolate", "Water", "Juice", "Sandwich", "Biscuits"};
        int[] prices = {50, 30, 40, 60, 20, 45, 80, 25};
        String[] emojis = {"☕", "🍵", "🍿", "🍫", "💧", "🧃", "🥪", "🍪"};

        Label msgLabel = new Label();
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);

        for (int i = 0; i < items.length; i++) {
            final int price = prices[i];
            final String item = items[i];

            VBox itemBox = new VBox(5);
            itemBox.setAlignment(Pos.CENTER);
            itemBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2); -fx-min-width: 110;");

            Label emoji = new Label(emojis[i]);
            emoji.setStyle("-fx-font-size: 28px;");
            Label name = new Label(item);
            name.setStyle("-fx-font-weight: bold;");
            Label priceLabel = new Label("৳" + price);
            priceLabel.setStyle("-fx-text-fill: #2a5298;");

            Button buyBtn = new Button("Buy");
            buyBtn.setStyle("-fx-background-color: #2a5298; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
            buyBtn.setOnAction(e -> {
                if (vendingBalance >= price) {
                    vendingBalance -= price;
                    balanceLabel.setText("Balance: ৳" + vendingBalance);
                    msgLabel.setStyle("-fx-text-fill: green;");
                    msgLabel.setText("Purchased " + item + "! Enjoy! 🎉");
                } else {
                    msgLabel.setStyle("-fx-text-fill: red;");
                    msgLabel.setText("Insufficient balance for " + item + ".");
                }
            });

            itemBox.getChildren().addAll(emoji, name, priceLabel, buyBtn);
            grid.add(itemBox, i % 4, i / 4);
        }

        // Recharge button
        Button rechargeBtn = new Button("Recharge ৳500");
        rechargeBtn.setOnAction(e -> {
            vendingBalance += 500;
            balanceLabel.setText("Balance: ৳" + vendingBalance);
            msgLabel.setStyle("-fx-text-fill: green;");
            msgLabel.setText("Recharged ৳500 successfully!");
        });

        box.getChildren().addAll(title, balanceLabel, grid, rechargeBtn, msgLabel);
        setScrollContent(box);
    }

    // ===================== WASHING MACHINE =====================

    @FXML
    private void showWashing() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Washing Machine Booking");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        Label subTitle = new Label("Book a washing slot for today:");
        subTitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        String[] slots = {"08:00 - 09:00 AM", "09:00 - 10:00 AM", "10:00 - 11:00 AM",
                "11:00 - 12:00 PM", "02:00 - 03:00 PM", "03:00 - 04:00 PM",
                "04:00 - 05:00 PM", "05:00 - 06:00 PM"};
        String[] machines = {"Machine A", "Machine B", "Machine C"};

        Label msgLabel = new Label();

        ComboBox<String> machineBox = new ComboBox<>();
        machineBox.setPromptText("Select Machine");
        machineBox.getItems().addAll(machines);
        machineBox.setMaxWidth(Double.MAX_VALUE);

        VBox slotList = new VBox(8);
        Label slotTitle = new Label("Available Slots:");
        slotTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        slotList.getChildren().add(slotTitle);

        for (String slot : slots) {
            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-padding: 8 12 8 12; -fx-background-color: #f8f9ff; -fx-background-radius: 6;");

            Label slotLabel = new Label("🕐  " + slot);
            slotLabel.setStyle("-fx-min-width: 180;");

            boolean booked = washingSlots.contains(slot);
            if (booked) {
                Label bookedLabel = new Label("✅ Booked");
                bookedLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                row.getChildren().addAll(slotLabel, bookedLabel);
            } else {
                Button bookBtn = new Button("Book");
                bookBtn.setOnAction(e -> {
                    if (machineBox.getValue() == null) {
                        msgLabel.setStyle("-fx-text-fill: red;");
                        msgLabel.setText("Please select a machine first.");
                        return;
                    }
                    washingSlots.add(slot);
                    msgLabel.setStyle("-fx-text-fill: green;");
                    msgLabel.setText("Booked " + slot + " on " + machineBox.getValue() + "!");
                    showWashing(); // Refresh view
                });
                row.getChildren().addAll(slotLabel, bookBtn);
            }
            slotList.getChildren().add(row);
        }

        // Show booked slots summary
        VBox bookedBox = new VBox(6);
        Label bookedTitle = new Label("Your Bookings:");
        bookedTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 10 0 0 0;");
        bookedBox.getChildren().add(bookedTitle);
        if (washingSlots.isEmpty()) {
            bookedBox.getChildren().add(new Label("No bookings yet."));
        } else {
            for (String s : washingSlots) {
                Label bk = new Label("✅  " + s);
                bk.setStyle("-fx-text-fill: #2a5298;");
                bookedBox.getChildren().add(bk);
            }
        }

        box.getChildren().addAll(title, subTitle, machineBox, slotList, bookedBox, msgLabel);
        setScrollContent(box);
    }

    // ===================== GAMES & SPORTS =====================

    @FXML
    private void showGames() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Games & Sports");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        String[] sports = {"Cricket", "Football", "Badminton", "Table Tennis", "Chess", "Basketball"};
        String[] sportEmojis = {"🏏", "⚽", "🏸", "🏓", "♟️", "🏀"};
        String[] schedules = {"Sunday & Wednesday 4-6 PM", "Monday & Thursday 4-6 PM",
                "Tuesday & Friday 5-7 PM", "Daily 3-5 PM",
                "Daily Open Hours", "Saturday 9-11 AM"};
        String[] venues = {"Main Ground", "Football Field", "Indoor Court", "Recreation Room",
                "Common Room", "Basketball Court"};

        Label msgLabel = new Label();

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);

        for (int i = 0; i < sports.length; i++) {
            final String sport = sports[i];

            VBox card = new VBox(8);
            card.setAlignment(Pos.CENTER);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 18; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2); -fx-min-width: 160;");

            Label emoji = new Label(sportEmojis[i]);
            emoji.setStyle("-fx-font-size: 32px;");
            Label name = new Label(sport);
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
            Label sched = new Label(schedules[i]);
            sched.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");
            sched.setWrapText(true);
            Label venue = new Label("📍 " + venues[i]);
            venue.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");

            boolean registered = gameRegistrations.contains(sport);
            Button regBtn = new Button(registered ? "✅ Registered" : "Register");
            if (registered) {
                regBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 6;");
                regBtn.setDisable(true);
            }
            regBtn.setOnAction(e -> {
                gameRegistrations.add(sport);
                msgLabel.setStyle("-fx-text-fill: green;");
                msgLabel.setText("Registered for " + sport + "! 🎉");
                showGames(); // Refresh
            });

            card.getChildren().addAll(emoji, name, sched, venue, regBtn);
            grid.add(card, i % 3, i / 3);
        }

        box.getChildren().addAll(title, grid, msgLabel);
        setScrollContent(box);
    }

    // ===================== REMOVED OLD METHODS =====================
    // showActivities removed - replaced by showGames

    // ===================== MY COURSES =====================

    @FXML
    private void showCourses() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("My Courses");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");
        box.getChildren().add(title);

        List<Course> courses = DataStore.getAllCourses();
        if (courses.isEmpty()) {
            box.getChildren().add(new Label("No courses available."));
        } else {
            for (Course c : courses) {
                VBox courseCard = new VBox(10);
                courseCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; "
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

                Label cName = new Label("\uD83D\uDCDA " + c.getCode() + " - " + c.getName());
                cName.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
                Label cInfo = new Label("Semester: " + c.getSemester() + " | Teacher: " + c.getTeacherEmail());
                cInfo.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

                HBox btnRow = new HBox(10);
                Button assignBtn = new Button("Assignments");
                Button slidesBtn = new Button("Slides");
                Button noticesBtn = new Button("Notices");

                VBox detailBox = new VBox(8);
                detailBox.setStyle("-fx-padding: 10 0 0 0;");

                final String courseCode = c.getCode();

                assignBtn.setOnAction(e -> {
                    detailBox.getChildren().clear();
                    List<Assignment> assignments = DataStore.getAssignmentsForCourse(courseCode);
                    if (assignments.isEmpty()) {
                        detailBox.getChildren().add(new Label("No assignments yet."));
                    } else {
                        for (Assignment a : assignments) {
                            VBox aBox = new VBox(5);
                            aBox.setStyle("-fx-padding: 8; -fx-background-color: #f8f9ff; -fx-background-radius: 6;");
                            Label aTitle = new Label("\uD83D\uDCDD " + a.getTitle());
                            aTitle.setStyle("-fx-font-weight: bold;");
                            Label aDesc = new Label(a.getDescription());
                            aDesc.setWrapText(true);
                            aDesc.setStyle("-fx-text-fill: #555;");

                            // Check if already submitted
                            List<String[]> mySubs = DataStore.getSubmissionsByStudent(Session.getStudentId());
                            boolean submitted = false;
                            String marks = "";
                            for (String[] sub : mySubs) {
                                if (sub[1].equals(courseCode) && sub[2].equals(a.getTitle())) {
                                    submitted = true;
                                    marks = sub[4];
                                    break;
                                }
                            }

                            if (submitted) {
                                Label status = new Label("\u2705 Submitted | Marks: " + marks);
                                status.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                                aBox.getChildren().addAll(aTitle, aDesc, status);
                            } else {
                                TextArea subArea = new TextArea();
                                subArea.setPromptText("Your submission...");
                                subArea.setPrefRowCount(2);

                                // PDF attachment for submission
                                Label pdfLabel = new Label("No PDF attached");
                                pdfLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");
                                final String[] pdfPath = {""};
                                Button pdfBtn = new Button("\uD83D\uDCC2 Attach PDF");
                                pdfBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-size: 11px;");
                                pdfBtn.setOnAction(ev2 -> {
                                    FileChooser fc = new FileChooser();
                                    fc.setTitle("Select PDF File");
                                    fc.getExtensionFilters().add(
                                            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
                                    File file = fc.showOpenDialog(contentArea.getScene().getWindow());
                                    if (file != null) {
                                        pdfPath[0] = file.getAbsolutePath();
                                        pdfLabel.setText("\u2705 " + file.getName());
                                        pdfLabel.setStyle("-fx-text-fill: green; -fx-font-size: 11px;");
                                    }
                                });
                                HBox pdfRow = new HBox(8, pdfBtn, pdfLabel);
                                pdfRow.setAlignment(Pos.CENTER_LEFT);

                                Button subBtn = new Button("Submit");
                                Label subMsg = new Label();
                                final String assignmentTitle = a.getTitle();
                                subBtn.setOnAction(ev -> {
                                    String content = subArea.getText().trim();
                                    if (content.isEmpty() && pdfPath[0].isEmpty()) {
                                        subMsg.setStyle("-fx-text-fill: red;");
                                        subMsg.setText("Enter your submission or attach a PDF.");
                                    } else {
                                        String submission = content
                                                + (pdfPath[0].isEmpty() ? "" : " [PDF:" + pdfPath[0] + "]");
                                        DataStore.submitAssignment(Session.getStudentId(),
                                                courseCode, assignmentTitle, submission);
                                        subMsg.setStyle("-fx-text-fill: green;");
                                        subMsg.setText("Submitted!" + (pdfPath[0].isEmpty() ? "" : " (with PDF)"));
                                        subArea.setDisable(true);
                                        subBtn.setDisable(true);
                                        pdfBtn.setDisable(true);
                                    }
                                });
                                aBox.getChildren().addAll(aTitle, aDesc, subArea, pdfRow, subBtn, subMsg);
                            }
                            detailBox.getChildren().add(aBox);
                        }
                    }
                });

                slidesBtn.setOnAction(e -> {
                    detailBox.getChildren().clear();
                    List<String[]> slides = DataStore.getSlidesForCourse(courseCode);
                    if (slides.isEmpty()) {
                        detailBox.getChildren().add(new Label("No slides uploaded yet."));
                    } else {
                        for (String[] s : slides) {
                            VBox sBox = new VBox(3);
                            sBox.setStyle("-fx-padding: 8; -fx-background-color: #e8f5e9; -fx-background-radius: 6;");
                            Label sTitle = new Label("\uD83D\uDCCA " + s[1]);
                            sTitle.setStyle("-fx-font-weight: bold;");
                            Label sDesc = new Label(s[2]);
                            sDesc.setWrapText(true);
                            sDesc.setStyle("-fx-text-fill: #555;");
                            sBox.getChildren().addAll(sTitle, sDesc);
                            detailBox.getChildren().add(sBox);
                        }
                    }
                });

                noticesBtn.setOnAction(e -> {
                    detailBox.getChildren().clear();
                    List<String[]> courseNotices = DataStore.getNoticesForCourse(courseCode);
                    if (courseNotices.isEmpty()) {
                        detailBox.getChildren().add(new Label("No notices for this course."));
                    } else {
                        for (String[] n : courseNotices) {
                            Label nLabel = new Label("\uD83D\uDCCC " + n[1] + " (" + n[3] + ")");
                            nLabel.setStyle("-fx-padding: 6; -fx-background-color: #fff8e1; -fx-background-radius: 6;");
                            nLabel.setWrapText(true);
                            detailBox.getChildren().add(nLabel);
                        }
                    }
                });

                btnRow.getChildren().addAll(assignBtn, slidesBtn, noticesBtn);
                courseCard.getChildren().addAll(cName, cInfo, btnRow, detailBox);
                box.getChildren().add(courseCard);
            }
        }
        setScrollContent(box);
    }

    // ===================== PAYMENT =====================

    @FXML
    private void showPayment() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Payment");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        String studentEmail = Session.getIdentifier();
        Label msgLabel = new Label();

        String[][] fees = {
                {"Hall Fees", "5000", "\uD83C\uDFE0"},
                {"Exam Fees", "3000", "\uD83D\uDCDD"},
                {"Semester Fees", "15000", "\uD83C\uDF93"},
                {"Library Fees", "1000", "\uD83D\uDCDA"},
                {"Lab Fees", "2000", "\uD83D\uDD2C"},
        };

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        for (int i = 0; i < fees.length; i++) {
            final String type = fees[i][0];
            final int amount = Integer.parseInt(fees[i][1]);

            VBox card = new VBox(8);
            card.setAlignment(Pos.CENTER);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 18; "
                    + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2); -fx-min-width: 140;");

            Label emoji = new Label(fees[i][2]);
            emoji.setStyle("-fx-font-size: 28px;");
            Label name = new Label(type);
            name.setStyle("-fx-font-weight: bold;");
            Label price = new Label("\u09F3" + amount);
            price.setStyle("-fx-text-fill: #2a5298; -fx-font-size: 16px;");

            Button payBtn = new Button("Pay Now");
            payBtn.setOnAction(e -> {
                DataStore.makePayment(studentEmail, type, amount);
                msgLabel.setStyle("-fx-text-fill: green;");
                msgLabel.setText(type + " - \u09F3" + amount + " paid successfully! \u2705");
                showPayment();
            });

            card.getChildren().addAll(emoji, name, price, payBtn);
            grid.add(card, i % 3, i / 3);
        }

        box.getChildren().addAll(title, grid, msgLabel, new Separator());

        // Payment history
        Label histTitle = new Label("Payment History:");
        histTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(histTitle);

        List<Payment> payments = DataStore.getPayments(studentEmail);
        if (payments.isEmpty()) {
            box.getChildren().add(new Label("No payment records."));
        } else {
            int total = 0;
            for (Payment p : payments) {
                Label item = new Label("\u2705 " + p.getType() + " | \u09F3" + p.getAmount() + " | " + p.getDate());
                item.setStyle("-fx-padding: 6; -fx-background-color: #e8f5e9; -fx-background-radius: 6;");
                box.getChildren().add(item);
                total += p.getAmount();
            }
            Label totalLabel = new Label("Total Paid: \u09F3" + total);
            totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1e3c72;");
            box.getChildren().add(totalLabel);
        }
        setScrollContent(box);
    }

    // ===================== MESSAGES =====================

    @FXML
    private void showMessages() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Messages");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        String myId = Session.getIdentifier();

        Label composeTitle = new Label("Send Message");
        composeTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        TextField toField = new TextField();
        toField.setPromptText("Recipient (email or ID)");
        TextArea msgArea = new TextArea();
        msgArea.setPromptText("Your message...");
        msgArea.setPrefRowCount(3);

        Label msgLabel = new Label();
        Button sendBtn = new Button("Send");
        sendBtn.setOnAction(e -> {
            String to = toField.getText().trim();
            String content = msgArea.getText().trim();
            if (to.isEmpty() || content.isEmpty()) {
                msgLabel.setStyle("-fx-text-fill: red;");
                msgLabel.setText("Fill all fields.");
            } else {
                DataStore.sendMessage(myId, to, content);
                msgLabel.setStyle("-fx-text-fill: green;");
                msgLabel.setText("Message sent!");
                toField.clear();
                msgArea.clear();
                showMessages();
            }
        });

        box.getChildren().addAll(title, composeTitle, toField, msgArea,
                sendBtn, msgLabel, new Separator());

        Label inboxTitle = new Label("Messages:");
        inboxTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(inboxTitle);

        List<Message> messages = DataStore.getMessagesFor(myId);
        if (messages.isEmpty()) {
            box.getChildren().add(new Label("No messages."));
        } else {
            for (int i = messages.size() - 1; i >= 0; i--) {
                Message m = messages.get(i);
                String direction = m.getFrom().equals(myId)
                        ? "\u27A1 To: " + m.getTo()
                        : "\u2B05 From: " + m.getFrom();
                VBox msgCard = new VBox(4);
                msgCard.setStyle("-fx-padding: 10; -fx-background-color: #f0f8ff; -fx-background-radius: 8;");
                Label dir = new Label(direction);
                dir.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #555;");
                Label content = new Label(m.getContent());
                content.setWrapText(true);
                Label ts = new Label(m.getTimestamp());
                ts.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");
                msgCard.getChildren().addAll(dir, content, ts);
                box.getChildren().add(msgCard);
            }
        }
        setScrollContent(box);
    }

    // ===================== LIVE CHAT =====================

    private Timeline chatRefreshTimeline;

    @FXML
    private void showLiveChat() {
        // Stop any previous chat refresh
        if (chatRefreshTimeline != null) chatRefreshTimeline.stop();

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        Label title = new Label("\uD83D\uDCAC Live Chat");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        String myId = Session.getIdentifier();

        // Recipient selector
        TextField recipientField = new TextField();
        recipientField.setPromptText("Chat with (email or ID)...");
        recipientField.setStyle("-fx-padding: 8; -fx-background-radius: 20; -fx-border-radius: 20;");

        // Chat messages area
        VBox chatMessages = new VBox(8);
        chatMessages.setStyle("-fx-padding: 10;");

        ScrollPane chatScroll = new ScrollPane(chatMessages);
        chatScroll.setFitToWidth(true);
        chatScroll.setPrefHeight(350);
        chatScroll.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10;");
        chatScroll.vvalueProperty().bind(chatMessages.heightProperty());

        // Message input
        HBox inputRow = new HBox(10);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        TextArea chatInput = new TextArea();
        chatInput.setPromptText("Type a message...");
        chatInput.setPrefRowCount(2);
        chatInput.setPrefWidth(400);
        HBox.setHgrow(chatInput, Priority.ALWAYS);

        Button sendBtn = new Button("Send \u27A1");
        sendBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 10 20 10 20;");

        Label statusLabel = new Label();

        Runnable refreshChat = () -> {
            String recipient = recipientField.getText().trim();
            if (recipient.isEmpty()) return;
            chatMessages.getChildren().clear();

            List<Message> messages = DataStore.getMessagesFor(myId);
            for (Message m : messages) {
                if (m.getFrom().equals(myId) && m.getTo().equals(recipient)
                        || m.getFrom().equals(recipient) && m.getTo().equals(myId)) {
                    boolean isMine = m.getFrom().equals(myId);
                    HBox bubble = new HBox();
                    bubble.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

                    VBox msgBox = new VBox(2);
                    msgBox.setMaxWidth(300);
                    msgBox.setStyle("-fx-padding: 10 14 10 14; -fx-background-radius: 14; "
                            + (isMine
                            ? "-fx-background-color: #2a5298;"
                            : "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 14;"));

                    Label content = new Label(m.getContent());
                    content.setWrapText(true);
                    content.setStyle(isMine
                            ? "-fx-text-fill: white; -fx-font-size: 13px;"
                            : "-fx-text-fill: #333; -fx-font-size: 13px;");

                    Label ts = new Label(m.getTimestamp());
                    ts.setStyle("-fx-font-size: 10px; "
                            + (isMine ? "-fx-text-fill: rgba(255,255,255,0.6);" : "-fx-text-fill: #999;"));

                    msgBox.getChildren().addAll(content, ts);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    if (isMine) {
                        bubble.getChildren().addAll(spacer, msgBox);
                    } else {
                        bubble.getChildren().addAll(msgBox, spacer);
                    }
                    chatMessages.getChildren().add(bubble);
                }
            }

            if (chatMessages.getChildren().isEmpty()) {
                Label noMsg = new Label("No messages yet. Start the conversation!");
                noMsg.setStyle("-fx-text-fill: #888; -fx-padding: 20;");
                chatMessages.getChildren().add(noMsg);
            }
        };

        sendBtn.setOnAction(e -> {
            String recipient = recipientField.getText().trim();
            String content = chatInput.getText().trim();
            if (recipient.isEmpty()) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Enter a recipient first.");
                return;
            }
            if (content.isEmpty()) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Type a message.");
                return;
            }
            DataStore.sendMessage(myId, recipient, content);
            chatInput.clear();
            statusLabel.setText("");
            refreshChat.run();
        });

        recipientField.setOnAction(e -> refreshChat.run());

        Button loadChatBtn = new Button("Load Chat");
        loadChatBtn.setOnAction(e -> refreshChat.run());

        HBox recipientRow = new HBox(10, recipientField, loadChatBtn);
        recipientRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(recipientField, Priority.ALWAYS);

        inputRow.getChildren().addAll(chatInput, sendBtn);

        // Auto-refresh every 3 seconds
        chatRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> refreshChat.run()));
        chatRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        chatRefreshTimeline.play();

        // Online users hint
        Label onlineHint = new Label("\uD83D\uDFE2 Auto-refreshes every 3 seconds");
        onlineHint.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px;");

        box.getChildren().addAll(title, recipientRow, onlineHint, chatScroll,
                inputRow, statusLabel);
        setScrollContent(box);
    }

    // ===================== UTILITY =====================

    private void setScrollContent(VBox content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        contentArea.getChildren().setAll(scroll);
    }
}