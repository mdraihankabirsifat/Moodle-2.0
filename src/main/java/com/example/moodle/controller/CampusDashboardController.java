package com.example.moodle.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        List<String[]> halls = DataStore.getAllHallRooms();

        VBox hallList = new VBox(10);
        Label msgLabel = new Label();

        if (halls.isEmpty()) {
            hallList.getChildren().add(new Label("No hall rooms available at the moment."));
        } else {
            for (int i = 0; i < halls.size(); i++) {
                String[] h = halls.get(i);
                final String hallName = h[0];
                final String roomNum = h.length > 1 ? h[1] : "Room";
                String capacity = h.length > 2 ? h[2] : "?";

                HBox row = new HBox(15);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-padding: 10; -fx-background-color: #f8f9ff; -fx-background-radius: 8;");

                VBox info = new VBox(3);
                Label hName = new Label(hallName);
                hName.setStyle("-fx-font-weight: bold;");
                Label avail = new Label(roomNum + "  |  Capacity: " + capacity);
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
        }

        box.getChildren().addAll(title, statusLabel, new Separator(), subTitle, hallList, msgLabel);
        setScrollContent(box);
    }

    // ===================== SCHEDULE =====================
    private static final String[] SCHED_DAYS = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};
    private static final String[] SCHED_TIMES = {"08:00-09:00", "09:00-10:00", "10:00-11:00", "11:00-12:00",
            "12:00-01:00", "01:00-02:00", "02:00-03:00", "03:00-04:00"};

    @FXML
    private void showSchedule() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Class Schedule");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        String studentId = Session.getStudentId();
        String batch = (studentId != null && studentId.length() >= 2) ? studentId.substring(0, 2) : "";

        Label batchLabel = new Label("Batch: " + batch + " | Student ID: " + studentId);
        batchLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

        // Build schedule lookup: day+time -> entry
        java.util.Map<String, String[]> lookup = new java.util.HashMap<>();
        for (String[] e : DataStore.getScheduleForBatch(batch)) {
            lookup.put(e[1] + "|" + e[2], e); // day|time -> entry
        }

        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);

        // Corner cell
        Label corner = new Label("Time \\ Day");
        corner.setStyle("-fx-font-weight: bold; -fx-padding: 8 10 8 10; -fx-background-color: #1e3c72; -fx-text-fill: white; -fx-min-width: 100;");
        corner.setMaxWidth(Double.MAX_VALUE);
        grid.add(corner, 0, 0);

        // Day headers
        for (int d = 0; d < SCHED_DAYS.length; d++) {
            Label dayLabel = new Label(SCHED_DAYS[d]);
            dayLabel.setStyle("-fx-font-weight: bold; -fx-padding: 8 10 8 10; -fx-background-color: #1e3c72; -fx-text-fill: white; -fx-min-width: 110; -fx-alignment: center;");
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            grid.add(dayLabel, d + 1, 0);
        }

        // Time rows with cells
        for (int t = 0; t < SCHED_TIMES.length; t++) {
            Label timeLabel = new Label(SCHED_TIMES[t]);
            timeLabel.setStyle("-fx-font-weight: bold; -fx-padding: 8 10 8 10; -fx-background-color: #34495e; -fx-text-fill: white; -fx-min-width: 100;");
            timeLabel.setMaxWidth(Double.MAX_VALUE);
            grid.add(timeLabel, 0, t + 1);

            for (int d = 0; d < SCHED_DAYS.length; d++) {
                String key = SCHED_DAYS[d] + "|" + SCHED_TIMES[t];
                String[] entry = lookup.get(key);

                if (entry != null) {
                    VBox cell = new VBox(2);
                    cell.setAlignment(Pos.CENTER);
                    cell.setStyle("-fx-padding: 6; -fx-background-color: #d4edda; -fx-min-width: 110; -fx-min-height: 50;");
                    Label code = new Label(entry[3]);
                    code.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #155724;");
                    Label cName = new Label(entry[4]);
                    cName.setStyle("-fx-font-size: 10px; -fx-text-fill: #155724;");
                    cName.setWrapText(true);
                    cell.getChildren().addAll(code, cName);
                    grid.add(cell, d + 1, t + 1);
                } else {
                    Label voidCell = new Label("\u2014");
                    voidCell.setAlignment(Pos.CENTER);
                    voidCell.setStyle("-fx-padding: 6; -fx-background-color: #f8f9ff; -fx-min-width: 110; -fx-min-height: 50; -fx-text-fill: #ccc; -fx-alignment: center;");
                    voidCell.setMaxWidth(Double.MAX_VALUE);
                    grid.add(voidCell, d + 1, t + 1);
                }
            }
        }

        box.getChildren().addAll(title, batchLabel, grid);
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

        // Read notices from DataStore (posted by authority/teachers)
        List<String[]> dsNotices = DataStore.getAllNotices();
        if (dsNotices.isEmpty()) {
            Label noNotice = new Label("No notices posted yet.");
            noNotice.setStyle("-fx-text-fill: #888; -fx-padding: 10;");
            noticeList.getChildren().add(noNotice);
        } else {
            for (int i = dsNotices.size() - 1; i >= 0; i--) {
                String[] n = dsNotices.get(i);
                Label notice = new Label("\uD83D\uDCCC  [" + n[0] + "] " + n[1]);
                notice.setWrapText(true);
                notice.setStyle("-fx-padding: 10 14 10 14; -fx-background-color: #fff8e1; "
                        + "-fx-background-radius: 8; -fx-border-color: #ffe082; -fx-border-radius: 8; -fx-font-size: 14px;");
                Label dateLabel = new Label("Posted: " + n[3] + " by " + n[2]);
                dateLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 11px; -fx-padding: 0 0 0 14;");
                noticeList.getChildren().addAll(notice, dateLabel);
            }
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

        String studentId = Session.getStudentId();
        Label studentInfo = new Label("Student: " + Session.getName() + "  |  ID: " + studentId);
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

        List<String[]> gradeData = DataStore.getGradesForStudent(studentId != null ? studentId : "");
        int row = 1;
        double totalPoints = 0;
        for (String[] g : gradeData) {
            // g[0]=studentId, g[1]=courseCode, g[2]=gradePoint
            double gp = 0;
            try {
                gp = Double.parseDouble(g[2]);
            } catch (NumberFormatException ignored) {
            }

            Label courseCell = new Label(g[1]);
            courseCell.setStyle("-fx-padding: 8 16 8 16; -fx-background-color: white; -fx-min-width: 180;");
            courseCell.setMaxWidth(Double.MAX_VALUE);

            Label gpCell = new Label(String.format("%.2f", gp));
            gpCell.setStyle("-fx-padding: 8 16 8 16; -fx-background-color: white; -fx-min-width: 180;");
            gpCell.setMaxWidth(Double.MAX_VALUE);

            Label lgCell = new Label(getLetterGrade(gp));
            lgCell.setStyle("-fx-padding: 8 16 8 16; -fx-background-color: white; -fx-min-width: 180;");
            lgCell.setMaxWidth(Double.MAX_VALUE);

            grid.add(courseCell, 0, row);
            grid.add(gpCell, 1, row);
            grid.add(lgCell, 2, row);
            totalPoints += gp;
            row++;
        }

        double cgpa = gradeData.isEmpty() ? 0 : totalPoints / gradeData.size();
        Label cgpaLabel = new Label(gradeData.isEmpty()
                ? "No grades assigned yet."
                : String.format("CGPA: %.2f", cgpa));
        cgpaLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e3c72; -fx-padding: 10 0 0 0;");

        box.getChildren().addAll(title, studentInfo, grid, cgpaLabel);
        setScrollContent(box);
    }

    private String getLetterGrade(double gp) {
        if (gp >= 4.0) {
            return "A+";
        }
        if (gp >= 3.75) {
            return "A";
        }
        if (gp >= 3.50) {
            return "A-";
        }
        if (gp >= 3.25) {
            return "B+";
        }
        if (gp >= 3.0) {
            return "B";
        }
        if (gp >= 2.75) {
            return "B-";
        }
        if (gp >= 2.50) {
            return "C+";
        }
        if (gp >= 2.25) {
            return "C";
        }
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
            itemBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; "
                    + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2); -fx-min-width: 110;");

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

        List<String[]> games = DataStore.getAllGames();
        Label msgLabel = new Label();

        if (games.isEmpty()) {
            box.getChildren().addAll(title, new Label("No games/sports available at the moment."));
        } else {
            GridPane grid = new GridPane();
            grid.setHgap(12);
            grid.setVgap(12);

            for (int i = 0; i < games.size(); i++) {
                String[] g = games.get(i);
                final String sport = g[0];
                String emoji = g.length > 1 ? g[1] : "\uD83C\uDFC6";
                String schedule = g.length > 2 ? g[2] : "";
                String venue = g.length > 3 ? g[3] : "";

                VBox card = new VBox(8);
                card.setAlignment(Pos.CENTER);
                card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 18; "
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2); -fx-min-width: 160;");

                Label emojiL = new Label(emoji);
                emojiL.setStyle("-fx-font-size: 32px;");
                Label name = new Label(sport);
                name.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
                Label sched = new Label(schedule);
                sched.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");
                sched.setWrapText(true);
                Label venueL = new Label("\uD83D\uDCCD " + venue);
                venueL.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");

                boolean registered = gameRegistrations.contains(sport);
                Button regBtn = new Button(registered ? "\u2705 Registered" : "Register");
                if (registered) {
                    regBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 6;");
                    regBtn.setDisable(true);
                }
                regBtn.setOnAction(e -> {
                    gameRegistrations.add(sport);
                    msgLabel.setStyle("-fx-text-fill: green;");
                    msgLabel.setText("Registered for " + sport + "! \uD83C\uDF89");
                    showGames(); // Refresh
                });

                card.getChildren().addAll(emojiL, name, sched, venueL, regBtn);
                grid.add(card, i % 3, i / 3);
            }

            box.getChildren().addAll(title, grid, msgLabel);
        }
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

        String studentId = Session.getStudentId();
        String myBatch = (studentId != null && studentId.length() >= 2) ? studentId.substring(0, 2) : "";

        List<Course> courses = DataStore.getAllCourses();
        // Filter by batch
        List<Course> filtered = new ArrayList<>();
        for (Course c : courses) {
            if (c.getBatch().isEmpty() || c.getBatch().equals(myBatch)) {
                filtered.add(c);
            }
        }

        if (filtered.isEmpty()) {
            box.getChildren().add(new Label("No courses available for your batch (" + myBatch + ")."));
        } else {
            for (Course c : filtered) {
                VBox courseCard = new VBox(10);
                courseCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; "
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

                Label cName = new Label("\uD83D\uDCDA " + c.getCode() + " - " + c.getName());
                cName.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
                String teacherInfo = c.getTeacherName().isEmpty() ? c.getTeacherEmail()
                        : c.getTeacherName() + " (" + c.getTeacherEmail() + ")";
                Label cInfo = new Label("Semester: " + c.getSemester() + " | Teacher: " + teacherInfo
                        + (c.getBatch().isEmpty() ? "" : " | Batch: " + c.getBatch()));
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

                            String rawDesc = a.getDescription();
                            String displayDesc = rawDesc;
                            String pdfFile = "";
                            if (rawDesc.contains("[PDF:")) {
                                int idx = rawDesc.indexOf("[PDF:");
                                int end = rawDesc.indexOf("]", idx);
                                if (end > idx) {
                                    pdfFile = rawDesc.substring(idx + 5, end);
                                    displayDesc = rawDesc.substring(0, idx).trim();
                                }
                            }
                            Label aDesc = new Label(displayDesc);
                            aDesc.setWrapText(true);
                            aDesc.setStyle("-fx-text-fill: #555;");
                            aBox.getChildren().addAll(aTitle, aDesc);

                            // PDF open button
                            if (!pdfFile.isEmpty()) {
                                final String fp = pdfFile;
                                Button openPdf = new Button("\uD83D\uDCC4 Open PDF");
                                openPdf.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-size: 11px;");
                                openPdf.setOnAction(ev -> {
                                    try {
                                        java.awt.Desktop.getDesktop().open(new File(fp));
                                    } catch (Exception ex) {
                                        // fallback
                                    }
                                });
                                aBox.getChildren().add(openPdf);
                            }

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
                                aBox.getChildren().add(status);
                            } else {
                                TextArea subArea = new TextArea();
                                subArea.setPromptText("Your submission...");
                                subArea.setPrefRowCount(2);

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
                                aBox.getChildren().addAll(subArea, pdfRow, subBtn, subMsg);
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
                            String rawSlide = s[2];
                            String displaySlide = rawSlide;
                            String slidePdf = "";
                            if (rawSlide.contains("[PDF:")) {
                                int idx = rawSlide.indexOf("[PDF:");
                                int end = rawSlide.indexOf("]", idx);
                                if (end > idx) {
                                    slidePdf = rawSlide.substring(idx + 5, end);
                                    displaySlide = rawSlide.substring(0, idx).trim();
                                }
                            }
                            Label sDesc = new Label(displaySlide);
                            sDesc.setWrapText(true);
                            sDesc.setStyle("-fx-text-fill: #555;");
                            sBox.getChildren().addAll(sTitle, sDesc);

                            if (!slidePdf.isEmpty()) {
                                final String fp = slidePdf;
                                Button openPdf = new Button("\uD83D\uDCC4 Open PDF");
                                openPdf.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-size: 11px;");
                                openPdf.setOnAction(ev -> {
                                    try {
                                        java.awt.Desktop.getDesktop().open(new File(fp));
                                    } catch (Exception ex) {
                                        /* ignore */ }
                                });
                                sBox.getChildren().add(openPdf);
                            }

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
            {"Lab Fees", "2000", "\uD83D\uDD2C"},};

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

    // ===================== MESSAGES (Messenger Style) =====================
    @FXML
    private void showMessages() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("\uD83D\uDCE8 Messages");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        String myId = Session.getIdentifier();

        // Compose new message
        HBox composeRow = new HBox(10);
        composeRow.setAlignment(Pos.CENTER_LEFT);
        TextField newChatField = new TextField();
        newChatField.setPromptText("Start new chat (enter email or ID)...");
        HBox.setHgrow(newChatField, Priority.ALWAYS);
        Button newChatBtn = new Button("\uD83D\uDCAC Chat");
        newChatBtn.setStyle("-fx-background-color: #2a5298; -fx-text-fill: white; -fx-background-radius: 8;");
        newChatBtn.setOnAction(e -> {
            String to = newChatField.getText().trim();
            if (!to.isEmpty()) {
                showDirectChat(to);
            }
        });
        composeRow.getChildren().addAll(newChatField, newChatBtn);

        // Conversation list
        VBox convList = new VBox(8);

        Runnable refreshConversations = () -> {
            convList.getChildren().clear();
            List<Message> messages = DataStore.getMessagesFor(myId);
            // Build unique conversation partners
            Map<String, Message> lastMessages = new java.util.LinkedHashMap<>();
            for (Message m : messages) {
                String partner = m.getFrom().equals(myId) ? m.getTo() : m.getFrom();
                lastMessages.put(partner, m); // last message wins
            }
            if (lastMessages.isEmpty()) {
                Label noMsg = new Label("No conversations yet. Start one above!");
                noMsg.setStyle("-fx-text-fill: #888; -fx-padding: 20;");
                convList.getChildren().add(noMsg);
            } else {
                // Show conversations newest first
                List<Map.Entry<String, Message>> entries = new ArrayList<>(lastMessages.entrySet());
                for (int i = entries.size() - 1; i >= 0; i--) {
                    Map.Entry<String, Message> entry = entries.get(i);
                    String partner = entry.getKey();
                    Message lastMsg = entry.getValue();

                    HBox card = new HBox(12);
                    card.setAlignment(Pos.CENTER_LEFT);
                    card.setStyle("-fx-padding: 12; -fx-background-color: white; -fx-background-radius: 10; "
                            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 2); -fx-cursor: hand;");

                    Label avatar = new Label("\uD83D\uDC64");
                    avatar.setStyle("-fx-font-size: 28px;");

                    VBox info = new VBox(3);
                    HBox.setHgrow(info, Priority.ALWAYS);
                    Label nameLabel = new Label(partner);
                    nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1e3c72;");
                    boolean fromMe = lastMsg.getFrom().equals(myId);
                    String preview = (fromMe ? "You: " : "") + lastMsg.getContent();
                    if (preview.length() > 50) {
                        preview = preview.substring(0, 50) + "...";
                    }
                    Label previewLabel = new Label(preview);
                    previewLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
                    info.getChildren().addAll(nameLabel, previewLabel);

                    Label timeLabel = new Label(lastMsg.getTimestamp());
                    timeLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");

                    card.getChildren().addAll(avatar, info, timeLabel);
                    card.setOnMouseClicked(ev -> showDirectChat(partner));
                    convList.getChildren().add(card);
                }
            }
        };

        refreshConversations.run();

        Label liveHint = new Label("\uD83D\uDFE2 Conversations auto-refresh every 3 seconds");
        liveHint.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px;");

        box.getChildren().addAll(title, composeRow, new Separator(), liveHint, convList);
        setScrollContent(box);

        // Auto-refresh conversations
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> refreshConversations.run()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    /**
     * Open direct live chat with a specific recipient
     */
    private void showDirectChat(String recipientId) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        String myId = Session.getIdentifier();

        Button backBtn = new Button("\u2190 Back to Messages");
        backBtn.setStyle("-fx-background-color: #1e3c72; -fx-text-fill: white; -fx-background-radius: 8;");
        backBtn.setOnAction(e -> showMessages());

        Label title = new Label("\uD83D\uDCAC Chat with " + recipientId);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        VBox chatMessages = new VBox(8);
        chatMessages.setStyle("-fx-padding: 10;");

        ScrollPane chatScroll = new ScrollPane(chatMessages);
        chatScroll.setFitToWidth(true);
        chatScroll.setPrefHeight(350);
        chatScroll.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10;");

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
            List<Message> allMsgs = DataStore.getMessagesFor(myId);
            List<Message> filtered = new ArrayList<>();
            for (Message m : allMsgs) {
                if ((m.getFrom().equals(myId) && m.getTo().equals(recipientId))
                        || (m.getFrom().equals(recipientId) && m.getTo().equals(myId))) {
                    filtered.add(m);
                }
            }

            chatMessages.getChildren().clear();
            for (Message m : filtered) {
                boolean isMine = m.getFrom().equals(myId);
                HBox bubble = new HBox();
                bubble.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

                VBox msgBox = new VBox(2);
                msgBox.setMaxWidth(300);
                msgBox.setStyle("-fx-padding: 10 14 10 14; -fx-background-radius: 14; "
                        + (isMine ? "-fx-background-color: #2a5298;" : "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 14;"));

                Label content = new Label(m.getContent());
                content.setWrapText(true);
                content.setStyle(isMine ? "-fx-text-fill: white; -fx-font-size: 13px;" : "-fx-text-fill: #333; -fx-font-size: 13px;");

                Label ts = new Label(m.getTimestamp());
                ts.setStyle("-fx-font-size: 10px; " + (isMine ? "-fx-text-fill: rgba(255,255,255,0.6);" : "-fx-text-fill: #999;"));

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
            if (chatMessages.getChildren().isEmpty()) {
                Label noMsg = new Label("No messages yet. Start the conversation!");
                noMsg.setStyle("-fx-text-fill: #888; -fx-padding: 20;");
                chatMessages.getChildren().add(noMsg);
            }
            // Scroll to bottom
            chatScroll.applyCss();
            chatScroll.layout();
            chatScroll.setVvalue(1.0);
        };

        sendBtn.setOnAction(e -> {
            String content = chatInput.getText().trim();
            if (content.isEmpty()) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Type a message.");
                return;
            }
            DataStore.sendMessage(myId, recipientId, content);
            chatInput.clear();
            statusLabel.setText("");
            refreshChat.run();
        });

        refreshChat.run();

        Label onlineHint = new Label("\uD83D\uDFE2 Auto-refreshes every 3 seconds");
        onlineHint.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px;");

        inputRow.getChildren().addAll(chatInput, sendBtn);
        box.getChildren().addAll(backBtn, title, onlineHint, chatScroll, inputRow, statusLabel);
        setScrollContent(box);

        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> refreshChat.run()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    // ===================== LIVE CHAT =====================
    private Timeline refreshTimeline;

    @FXML
    private void showLiveChat() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        Label title = new Label("\uD83D\uDCAC Live Chat");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        String myId = Session.getIdentifier();

        // Recipient selector
        TextField recipientField = new TextField();
        recipientField.setPromptText("Chat with (email or ID)...");
        recipientField.setStyle("-fx-padding: 8; -fx-background-radius: 20; -fx-border-radius: 20;");

        VBox chatMessages = new VBox(8);
        chatMessages.setStyle("-fx-padding: 10;");

        ScrollPane chatScroll = new ScrollPane(chatMessages);
        chatScroll.setFitToWidth(true);
        chatScroll.setPrefHeight(350);
        chatScroll.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10;");

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
            if (recipient.isEmpty()) {
                return;
            }
            List<Message> allMsgs = DataStore.getMessagesFor(myId);
            List<Message> filtered = new ArrayList<>();
            for (Message m : allMsgs) {
                if ((m.getFrom().equals(myId) && m.getTo().equals(recipient))
                        || (m.getFrom().equals(recipient) && m.getTo().equals(myId))) {
                    filtered.add(m);
                }
            }

            chatMessages.getChildren().clear();
            for (Message m : filtered) {
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

            if (chatMessages.getChildren().isEmpty()) {
                Label noMsg = new Label("No messages yet. Start the conversation!");
                noMsg.setStyle("-fx-text-fill: #888; -fx-padding: 20;");
                chatMessages.getChildren().add(noMsg);
            }
            // Scroll to bottom
            chatScroll.applyCss();
            chatScroll.layout();
            chatScroll.setVvalue(1.0);
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

        Label onlineHint = new Label("\uD83D\uDFE2 Auto-refreshes every 3 seconds");
        onlineHint.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px;");

        box.getChildren().addAll(title, recipientRow, onlineHint, chatScroll,
                inputRow, statusLabel);
        setScrollContent(box);

        // Auto-refresh — AFTER setScrollContent
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> refreshChat.run()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    // ===================== UTILITY =====================
    private void setScrollContent(VBox content) {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        contentArea.getChildren().setAll(scroll);
    }

    // ===================== OPEN NEW WINDOW =====================
    @FXML
    private void openNewWindow() {
        SceneManager.openNewWindow();
    }

    // ===================== STUDENTS COMMUNITY =====================
    @FXML
    private void showCommunity() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("\uD83C\uDF10 Students Community");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        // New Post Form
        Label formTitle = new Label("Share something with your community");
        formTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #555;");

        TextArea postArea = new TextArea();
        postArea.setPromptText("What's on your mind?");
        postArea.setPrefRowCount(3);
        postArea.setWrapText(true);

        // Photo attachment
        final String[] attachedImage = {""};
        Label imgLabel = new Label("No image attached");
        imgLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");
        Button imgBtn = new Button("\uD83D\uDCF7 Attach Photo");
        imgBtn.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
        imgBtn.setOnAction(e -> {
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.setTitle("Select Image");
            fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            java.io.File file = fc.showOpenDialog(contentArea.getScene().getWindow());
            if (file != null) {
                attachedImage[0] = file.toURI().toString();
                imgLabel.setText("\u2705 " + file.getName());
                imgLabel.setStyle("-fx-text-fill: green; -fx-font-size: 11px;");
            }
        });
        HBox imgRow = new HBox(10, imgBtn, imgLabel);
        imgRow.setAlignment(Pos.CENTER_LEFT);

        Label postMsg = new Label();
        Button postBtn = new Button("\uD83D\uDCE8 Post");
        postBtn.setStyle("-fx-background-color: #2a5298; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 20 8 20; -fx-cursor: hand;");

        postBtn.setOnAction(e -> {
            String content = postArea.getText().trim();
            if (content.isEmpty() && attachedImage[0].isEmpty()) {
                postMsg.setStyle("-fx-text-fill: red;");
                postMsg.setText("Write something or attach a photo!");
            } else {
                DataStore.addCommunityPost(Session.getIdentifier(),
                        Session.getName() != null ? Session.getName() : "Anonymous",
                        content.isEmpty() ? "\uD83D\uDCF7 Photo" : content, attachedImage[0]);
                postMsg.setStyle("-fx-text-fill: green;");
                postMsg.setText("Posted successfully! \u2705");
                postArea.clear();
                attachedImage[0] = "";
                imgLabel.setText("No image attached");
                imgLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");
            }
        });

        VBox formBox = new VBox(8, formTitle, postArea, imgRow, new HBox(10, postBtn, postMsg));
        formBox.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-background-radius: 10; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        // Feed (dynamic, auto-refreshed)
        VBox feed = new VBox(10);
        feed.setId("community-feed");

        Runnable refreshFeed = () -> {
            feed.getChildren().clear();
            List<String[]> posts = DataStore.getAllCommunityPosts();
            if (posts.isEmpty()) {
                Label noPost = new Label("No posts yet. Be the first to share!");
                noPost.setStyle("-fx-text-fill: #888; -fx-padding: 20;");
                feed.getChildren().add(noPost);
            } else {
                for (int i = posts.size() - 1; i >= 0; i--) {
                    String[] p = posts.get(i);
                    VBox postCard = new VBox(6);
                    postCard.setStyle("-fx-padding: 14; -fx-background-color: white; -fx-background-radius: 10; "
                            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 2);");

                    HBox header = new HBox(10);
                    header.setAlignment(Pos.CENTER_LEFT);
                    Label avatar = new Label("\uD83D\uDC64");
                    avatar.setStyle("-fx-font-size: 22px;");
                    VBox nameTime = new VBox(2);
                    Label authorLabel = new Label(p[1]);
                    authorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1e3c72;");
                    Label timeLabel = new Label(p[3]);
                    timeLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");
                    nameTime.getChildren().addAll(authorLabel, timeLabel);
                    header.getChildren().addAll(avatar, nameTime);

                    Label contentLabel = new Label(p[2]);
                    contentLabel.setWrapText(true);
                    contentLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #333; -fx-padding: 5 0 0 32;");

                    postCard.getChildren().addAll(header, contentLabel);

                    // Display attached image if present
                    if (p.length >= 5 && p[4] != null && !p[4].isEmpty()) {
                        try {
                            String imageUri = p[4];
                            javafx.scene.image.Image img = new javafx.scene.image.Image(imageUri, 400, 300, true, true);
                            javafx.scene.image.ImageView imgView = new javafx.scene.image.ImageView(img);
                            imgView.setPreserveRatio(true);
                            imgView.setFitWidth(400);
                            imgView.setStyle("-fx-cursor: hand;");

                            // Click to maximize
                            imgView.setOnMouseClicked(ev -> {
                                javafx.scene.image.Image fullImg = new javafx.scene.image.Image(imageUri);
                                javafx.scene.image.ImageView fullView = new javafx.scene.image.ImageView(fullImg);
                                fullView.setPreserveRatio(true);
                                fullView.fitWidthProperty().bind(contentArea.widthProperty().multiply(0.85));
                                fullView.fitHeightProperty().bind(contentArea.heightProperty().multiply(0.85));

                                Button closeBtn = new Button("\u2715 Close");
                                closeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; "
                                        + "-fx-background-radius: 20; -fx-padding: 8 20 8 20; -fx-cursor: hand;");

                                Button downloadBtn = new Button("\u2B07 Download");
                                downloadBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; "
                                        + "-fx-background-radius: 20; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
                                downloadBtn.setOnAction(de -> {
                                    try {
                                        java.net.URI uri = new java.net.URI(imageUri);
                                        java.io.File srcFile = new java.io.File(uri);
                                        FileChooser saveDlg = new FileChooser();
                                        saveDlg.setTitle("Save Image");
                                        saveDlg.setInitialFileName(srcFile.getName());
                                        saveDlg.getExtensionFilters().add(
                                                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
                                        java.io.File dest = saveDlg.showSaveDialog(contentArea.getScene().getWindow());
                                        if (dest != null) {
                                            java.nio.file.Files.copy(srcFile.toPath(), dest.toPath(),
                                                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                                        }
                                    } catch (Exception ex) { /* ignore errors */ }
                                });

                                HBox btnRow = new HBox(15, closeBtn, downloadBtn);
                                btnRow.setAlignment(Pos.CENTER);

                                VBox overlayContent = new VBox(10, fullView, btnRow);
                                overlayContent.setAlignment(Pos.CENTER);

                                StackPane overlay = new StackPane(overlayContent);
                                overlay.setStyle("-fx-background-color: rgba(0,0,0,0.85);");
                                overlay.setAlignment(Pos.CENTER);

                                closeBtn.setOnAction(ce -> contentArea.getChildren().remove(overlay));
                                overlay.setOnMouseClicked(oe -> {
                                    if (oe.getTarget() == overlay) contentArea.getChildren().remove(overlay);
                                });

                                contentArea.getChildren().add(overlay);
                            });

                            postCard.getChildren().add(imgView);
                        } catch (Exception ex) { /* invalid image, skip */ }
                    }

                    feed.getChildren().add(postCard);
                }
            }
        };

        refreshFeed.run();

        Label liveHint = new Label("\uD83D\uDFE2 Live feed — auto-refreshes every 3 seconds");
        liveHint.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px;");

        box.getChildren().addAll(title, formBox, new Separator(), liveHint, feed);
        setScrollContent(box);

        // Auto-refresh the feed
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> refreshFeed.run()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    // ===================== GROUP CHAT =====================
    @FXML
    private void showGroupChat() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("\uD83D\uDC65 Group Chat");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        String myId = Session.getIdentifier();

        // ---- Create Group section ----
        Label createTitle = new Label("Create New Group");
        createTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Group Name (e.g. CSE101 Study Group)");
        TextField groupPasswordField = new TextField();
        groupPasswordField.setPromptText("Group Password (optional)");
        TextField membersField = new TextField();
        membersField.setPromptText("Members (comma separated emails/IDs)");

        Label createMsg = new Label();
        Button createBtn = new Button("Create Group");
        createBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        createBtn.setOnAction(e -> {
            String gName = groupNameField.getText().trim();
            String gPass = groupPasswordField.getText().trim();
            String members = membersField.getText().trim();
            if (gName.isEmpty()) {
                createMsg.setStyle("-fx-text-fill: red;");
                createMsg.setText("Enter a group name.");
            } else {
                String memberList = members.isEmpty() ? myId : myId + "," + members;
                DataStore.createGroup(gName, myId, gPass, memberList);
                createMsg.setStyle("-fx-text-fill: green;");
                createMsg.setText("Group '" + gName + "' created! \u2705");
                groupNameField.clear();
                groupPasswordField.clear();
                membersField.clear();
            }
        });

        VBox createBox = new VBox(8, createTitle, groupNameField, groupPasswordField, membersField, createBtn, createMsg);
        createBox.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-background-radius: 10; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        // ---- Search & Join Group section ----
        Label searchTitle = new Label("Search & Join Group");
        searchTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

        TextField searchField = new TextField();
        searchField.setPromptText("\uD83D\uDD0D Search group name...");
        VBox searchResults = new VBox(6);
        Label joinMsg = new Label();

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> {
            searchResults.getChildren().clear();
            String query = searchField.getText().trim();
            if (query.isEmpty()) {
                return;
            }
            List<String[]> found = DataStore.searchGroups(query);
            if (found.isEmpty()) {
                searchResults.getChildren().add(new Label("No groups found."));
            } else {
                for (String[] g : found) {
                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setStyle("-fx-padding: 8; -fx-background-color: #f8f9ff; -fx-background-radius: 6;");
                    Label gLabel = new Label("\uD83D\uDC65 " + g[0] + " (by " + g[1] + ")"
                            + (g[2].isEmpty() ? "" : " \uD83D\uDD12"));
                    gLabel.setStyle("-fx-font-weight: bold;");

                    TextField passField = new TextField();
                    passField.setPromptText("Password");
                    passField.setPrefWidth(120);
                    passField.setVisible(!g[2].isEmpty());
                    passField.setManaged(!g[2].isEmpty());

                    Button joinBtn = new Button("Join");
                    joinBtn.setOnAction(ev -> {
                        boolean ok = DataStore.joinGroup(g[0], passField.getText().trim(), myId);
                        if (ok) {
                            joinMsg.setStyle("-fx-text-fill: green;");
                            joinMsg.setText("Joined '" + g[0] + "'!");
                        } else {
                            joinMsg.setStyle("-fx-text-fill: red;");
                            joinMsg.setText("Wrong password for '" + g[0] + "'.");
                        }
                    });
                    row.getChildren().addAll(gLabel, passField, joinBtn);
                    searchResults.getChildren().add(row);
                }
            }
        });

        HBox searchRow = new HBox(10, searchField, searchBtn);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        VBox searchBox = new VBox(8, searchTitle, searchRow, searchResults, joinMsg);
        searchBox.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-background-radius: 10; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        // ---- Group selector ----
        Label selectTitle = new Label("Your Groups");
        selectTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

        ComboBox<String> groupSelector = new ComboBox<>();
        groupSelector.setPromptText("Select a group to chat");
        groupSelector.setMaxWidth(Double.MAX_VALUE);

        VBox chatMessages = new VBox(8);
        chatMessages.setStyle("-fx-padding: 10;");

        ScrollPane chatScroll = new ScrollPane(chatMessages);
        chatScroll.setFitToWidth(true);
        chatScroll.setPrefHeight(250);
        chatScroll.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10;");
        chatScroll.vvalueProperty().bind(chatMessages.heightProperty());

        HBox inputRow = new HBox(10);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        TextArea chatInput = new TextArea();
        chatInput.setPromptText("Type a message...");
        chatInput.setPrefRowCount(2);
        chatInput.setPrefWidth(400);
        HBox.setHgrow(chatInput, Priority.ALWAYS);

        Button sendBtn = new Button("Send \u27A1");
        sendBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; "
                + "-fx-background-radius: 20; -fx-padding: 10 20 10 20;");

        Label chatStatus = new Label();

        Runnable refreshGroups = () -> {
            String prevSelection = groupSelector.getValue();
            groupSelector.getItems().clear();
            List<String[]> groups = DataStore.getGroupsForUser(myId);
            for (String[] g : groups) {
                groupSelector.getItems().add(g[0]);
            }
            if (prevSelection != null && groupSelector.getItems().contains(prevSelection)) {
                groupSelector.setValue(prevSelection);
            }
        };

        Runnable refreshChat = () -> {
            String selected = groupSelector.getValue();
            if (selected == null || selected.isEmpty()) {
                return;
            }
            chatMessages.getChildren().clear();
            List<String[]> msgs = DataStore.getGroupMessages(selected);
            if (msgs.isEmpty()) {
                Label noMsg = new Label("No messages yet. Start the conversation!");
                noMsg.setStyle("-fx-text-fill: #888; -fx-padding: 20;");
                chatMessages.getChildren().add(noMsg);
            } else {
                for (String[] m : msgs) {
                    boolean isMine = m[1].equals(myId);
                    HBox bubble = new HBox();
                    bubble.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

                    VBox msgBox = new VBox(2);
                    msgBox.setMaxWidth(300);
                    msgBox.setStyle("-fx-padding: 10 14 10 14; -fx-background-radius: 14; "
                            + (isMine
                                    ? "-fx-background-color: #2a5298;"
                                    : "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 14;"));

                    Label sender = new Label(isMine ? "You" : m[2]);
                    sender.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; "
                            + (isMine ? "-fx-text-fill: rgba(255,255,255,0.7);" : "-fx-text-fill: #1e3c72;"));

                    Label content = new Label(m[3]);
                    content.setWrapText(true);
                    content.setStyle(isMine
                            ? "-fx-text-fill: white; -fx-font-size: 13px;"
                            : "-fx-text-fill: #333; -fx-font-size: 13px;");

                    Label ts = new Label(m[4]);
                    ts.setStyle("-fx-font-size: 10px; "
                            + (isMine ? "-fx-text-fill: rgba(255,255,255,0.6);" : "-fx-text-fill: #999;"));

                    msgBox.getChildren().addAll(sender, content, ts);

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
        };

        groupSelector.setOnAction(e -> refreshChat.run());

        sendBtn.setOnAction(e -> {
            String selected = groupSelector.getValue();
            String content = chatInput.getText().trim();
            if (selected == null || selected.isEmpty()) {
                chatStatus.setStyle("-fx-text-fill: red;");
                chatStatus.setText("Select a group first.");
                return;
            }
            if (content.isEmpty()) {
                chatStatus.setStyle("-fx-text-fill: red;");
                chatStatus.setText("Type a message.");
                return;
            }
            String senderName = Session.getName() != null ? Session.getName() : myId;
            DataStore.addGroupMessage(selected, myId, senderName, content);
            chatInput.clear();
            chatStatus.setText("");
            refreshChat.run();
        });

        refreshGroups.run();

        Label liveHint = new Label("\uD83D\uDFE2 Auto-refreshes every 3 seconds");
        liveHint.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px;");

        inputRow.getChildren().addAll(chatInput, sendBtn);

        box.getChildren().addAll(title, createBox, new Separator(), searchBox,
                new Separator(), selectTitle, groupSelector, liveHint, chatScroll, inputRow, chatStatus);
        setScrollContent(box);

        // Auto-refresh groups + chat
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            refreshGroups.run();
            refreshChat.run();
        }));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    // ===================== HOSPITAL =====================
    @FXML
    private void showHospital() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("\uD83C\uDFE5 Medical Center");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        String myId = Session.getIdentifier();

        // ---- Available Doctors (from admin-managed DataStore) ----
        Label docTitle = new Label("Available Doctors");
        docTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1e3c72;");

        List<String[]> doctors = DataStore.getAllDoctors();

        GridPane docGrid = new GridPane();
        docGrid.setHgap(2);
        docGrid.setVgap(2);
        String[] docHeaders = {"Doctor", "Specialization", "Available Days", "Hours"};
        for (int c = 0; c < docHeaders.length; c++) {
            Label cell = new Label(docHeaders[c]);
            cell.setStyle("-fx-font-weight: bold; -fx-padding: 8 12 8 12; -fx-background-color: #1e3c72; -fx-text-fill: white; -fx-min-width: 140;");
            cell.setMaxWidth(Double.MAX_VALUE);
            docGrid.add(cell, c, 0);
        }
        for (int r = 0; r < doctors.size(); r++) {
            String[] dr = doctors.get(r);
            for (int c = 0; c < 4 && c < dr.length; c++) {
                Label cell = new Label(dr[c]);
                String bg = r % 2 == 0 ? "white" : "#f8f9ff";
                cell.setStyle("-fx-padding: 8 12 8 12; -fx-background-color: " + bg + "; -fx-min-width: 140;");
                cell.setMaxWidth(Double.MAX_VALUE);
                docGrid.add(cell, c, r + 1);
            }
        }

        // ---- Book Appointment ----
        Label bookTitle = new Label("Book Appointment");
        bookTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 10 0 0 0;");

        ComboBox<String> doctorBox = new ComboBox<>();
        doctorBox.setPromptText("Select Doctor");
        doctorBox.setMaxWidth(Double.MAX_VALUE);
        for (String[] d : doctors) {
            doctorBox.getItems().add(d[0] + " - " + (d.length > 1 ? d[1] : ""));
        }

        TextField dateField = new TextField();
        dateField.setPromptText("Date (e.g. 2025-07-15)");
        TextField timeField = new TextField();
        timeField.setPromptText("Time (e.g. 10:00 AM)");
        TextArea reasonField = new TextArea();
        reasonField.setPromptText("Reason for visit...");
        reasonField.setPrefRowCount(2);

        Label bookMsg = new Label();
        Button bookBtn = new Button("Book Appointment");
        bookBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        bookBtn.setOnAction(e -> {
            String doc = doctorBox.getValue();
            String date = dateField.getText().trim();
            String time = timeField.getText().trim();
            String reason = reasonField.getText().trim();
            if (doc == null || date.isEmpty() || time.isEmpty()) {
                bookMsg.setStyle("-fx-text-fill: red;");
                bookMsg.setText("Select doctor, date and time.");
            } else {
                DataStore.bookAppointment(myId, doc.split(" - ")[0], date, time, reason.isEmpty() ? "General Checkup" : reason);
                bookMsg.setStyle("-fx-text-fill: green;");
                bookMsg.setText("Appointment booked! \u2705");
                dateField.clear();
                timeField.clear();
                reasonField.clear();
            }
        });

        // ---- Tests Available ----
        Label testTitle = new Label("\uD83E\uDDEA Tests Available");
        testTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 10 0 0 0;");

        String[][] tests = {
            {"Blood Test (CBC)", "\u09F3200", "Available Daily"},
            {"Urine Test", "\u09F3150", "Available Daily"},
            {"X-Ray", "\u09F3500", "Mon-Fri"},
            {"ECG", "\u09F3400", "Mon, Wed, Fri"},
            {"Eye Test", "\u09F3300", "Tue, Thu"},
            {"Blood Sugar", "\u09F3100", "Available Daily"},};

        VBox testList = new VBox(6);
        for (String[] t : tests) {
            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-padding: 8; -fx-background-color: #f0f4ff; -fx-background-radius: 6;");
            Label tName = new Label("\uD83E\uDDEA " + t[0]);
            tName.setStyle("-fx-font-weight: bold; -fx-min-width: 180;");
            Label tPrice = new Label(t[1]);
            tPrice.setStyle("-fx-text-fill: #2a5298; -fx-min-width: 80;");
            Label tAvail = new Label(t[2]);
            tAvail.setStyle("-fx-text-fill: #666;");
            row.getChildren().addAll(tName, tPrice, tAvail);
            testList.getChildren().add(row);
        }

        // ---- Medicine ----
        Label medTitle = new Label("\uD83D\uDC8A Medicine Counter");
        medTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 10 0 0 0;");

        String[][] meds = {
            {"Paracetamol", "Fever / Pain", "\u09F35"},
            {"Antacid", "Acidity", "\u09F38"},
            {"Cetirizine", "Allergy", "\u09F36"},
            {"ORS Packet", "Dehydration", "\u09F310"},
            {"Bandage Roll", "First Aid", "\u09F315"},
            {"Antiseptic Cream", "Wounds", "\u09F320"},};

        VBox medList = new VBox(6);
        for (String[] m : meds) {
            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-padding: 8; -fx-background-color: #e8f5e9; -fx-background-radius: 6;");
            Label mName = new Label("\uD83D\uDC8A " + m[0]);
            mName.setStyle("-fx-font-weight: bold; -fx-min-width: 160;");
            Label mUse = new Label(m[1]);
            mUse.setStyle("-fx-text-fill: #555; -fx-min-width: 140;");
            Label mPrice = new Label(m[2]);
            mPrice.setStyle("-fx-text-fill: #27ae60;");
            row.getChildren().addAll(mName, mUse, mPrice);
            medList.getChildren().add(row);
        }

        // ---- My Appointments ----
        Label apptTitle = new Label("My Appointments");
        apptTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 10 0 0 0;");

        VBox apptList = new VBox(6);
        List<String[]> myAppts = DataStore.getAppointmentsFor(myId);
        if (myAppts.isEmpty()) {
            apptList.getChildren().add(new Label("No appointments booked."));
        } else {
            for (String[] a : myAppts) {
                Label aItem = new Label("\uD83D\uDCC5 " + a[1] + " | " + a[2] + " at " + a[3]
                        + " | Reason: " + a[4] + " | Status: " + a[5]);
                aItem.setStyle("-fx-padding: 6; -fx-background-color: #fff8e1; -fx-background-radius: 6;");
                aItem.setWrapText(true);
                apptList.getChildren().add(aItem);
            }
        }

        // Emergency info
        Label emergLabel = new Label("\uD83D\uDEA8 Emergency: Call 999 or visit Medical Center Ground Floor");
        emergLabel.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10 0 0 0;");

        box.getChildren().addAll(title, docTitle, docGrid, new Separator(),
                bookTitle, doctorBox, dateField, timeField, reasonField, bookBtn, bookMsg,
                new Separator(), testTitle, testList,
                new Separator(), medTitle, medList,
                new Separator(), apptTitle, apptList,
                emergLabel);
        setScrollContent(box);
    }
}