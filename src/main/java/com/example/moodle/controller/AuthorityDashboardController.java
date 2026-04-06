package com.example.moodle.controller;

import java.util.List;

import com.example.moodle.model.Course;
import com.example.moodle.model.Payment;
import com.example.moodle.model.User;
import com.example.moodle.service.DataStore;
import com.example.moodle.util.SceneManager;
import com.example.moodle.util.Session;
import com.example.moodle.util.UserStore;

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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class AuthorityDashboardController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        showStudentDatabase();
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
    private void openNewWindow() {
        SceneManager.openNewWindow();
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

    // ===================== STUDENT DATABASE =====================
    @FXML
    private void showStudentDatabase() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Student Database");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        // Search
        TextField searchField = new TextField();
        searchField.setPromptText("\uD83D\uDD0D Search by name, ID, or email...");
        searchField.setStyle("-fx-padding: 8 16 8 16; -fx-background-radius: 20; -fx-border-radius: 20;");

        VBox tableBox = new VBox(2);

        Runnable refreshTable = () -> {
            tableBox.getChildren().clear();
            String query = searchField.getText().trim().toLowerCase();

            // Header row
            HBox headerRow = new HBox(2);
            String[] headers = {"Name", "University", "Student ID", "Email", "Role"};
            for (String h : headers) {
                Label cell = new Label(h);
                cell.setStyle("-fx-font-weight: bold; -fx-padding: 10 16 10 16; "
                        + "-fx-background-color: #0d1b2a; -fx-text-fill: #00e5ff; -fx-border-color: rgba(0,229,255,0.3); "
                        + "-fx-min-width: 130; -fx-pref-width: 130;");
                headerRow.getChildren().add(cell);
            }
            tableBox.getChildren().add(headerRow);

            List<User> users = UserStore.getAllUsers();
            // Show newest first (reverse order)
            java.util.List<User> reversed = new java.util.ArrayList<>(users);
            java.util.Collections.reverse(reversed);
            int count = 0;
            for (User u : reversed) {
                if (!query.isEmpty()) {
                    String combined = ((u.getName() != null ? u.getName() : "")
                            + (u.getStudentId() != null ? u.getStudentId() : "")
                            + (u.getEmail() != null ? u.getEmail() : "")).toLowerCase();
                    if (!combined.contains(query)) {
                        continue;
                    }
                }
                HBox row = new HBox(2);
                String bg = count % 2 == 0 ? "white" : "#f8f9ff";
                String[] vals = {u.getName(), u.getUniversity(), u.getStudentId(),
                    u.getEmail(), u.getRole()};
                for (int i = 0; i < vals.length; i++) {
                    Label cell = new Label(vals[i] != null ? vals[i] : "");
                    String style = "-fx-padding: 8 16 8 16; -fx-background-color: " + bg
                            + "; -fx-min-width: 130; -fx-pref-width: 130;";
                    if (i == 0) {
                        style += " -fx-font-weight: bold; -fx-text-fill: #00e5ff;"; // Name column bold

                                        }cell.setStyle(style);
                    row.getChildren().add(cell);
                }
                tableBox.getChildren().add(row);
                count++;
            }
        };

        refreshTable.run();
        searchField.textProperty().addListener((obs, o, n) -> refreshTable.run());

        Label countLabel = new Label("Total Students: " + UserStore.getAllUsers().size());
        countLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8a9ab0;");

        box.getChildren().addAll(title, countLabel, searchField, tableBox);
        setScrollContent(box);
    }

    // ===================== EDIT STUDENT =====================
    @FXML
    private void showEditStudent() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Edit Student Record");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        TextField searchField = new TextField();
        searchField.setPromptText("Enter student email to find...");
        Button findBtn = new Button("Find Student");

        VBox editBox = new VBox(10);
        Label msgLabel = new Label();

        findBtn.setOnAction(e -> {
            editBox.getChildren().clear();
            String email = searchField.getText().trim();
            User user = UserStore.getUser(email);
            if (user == null) {
                msgLabel.setStyle("-fx-text-fill: #ff3366;");
                msgLabel.setText("Student not found.");
                return;
            }
            msgLabel.setText("");

            Label editTitle = new Label("Editing: " + user.getName());
            editTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #0088cc;");

            TextField nameF = new TextField(user.getName());
            nameF.setPromptText("Name");
            TextField uniF = new TextField(user.getUniversity());
            uniF.setPromptText("University");
            TextField idF = new TextField(user.getStudentId());
            idF.setPromptText("Student ID");
            TextField emailF = new TextField(user.getEmail());
            emailF.setPromptText("Email");
            emailF.setDisable(true);

            ComboBox<String> roleBox = new ComboBox<>();
            roleBox.getItems().addAll("STUDENT", "TEACHER", "AUTHORITY");
            roleBox.setValue(user.getRole());
            roleBox.setMaxWidth(Double.MAX_VALUE);

            Button saveBtn = new Button("Save Changes");
            Label editMsg = new Label();
            saveBtn.setOnAction(ev -> {
                user.setName(nameF.getText().trim());
                user.setUniversity(uniF.getText().trim());
                user.setStudentId(idF.getText().trim());
                user.setRole(roleBox.getValue());
                UserStore.updateUser(user);
                editMsg.setStyle("-fx-text-fill: #00ff88;");
                editMsg.setText("Student record updated successfully!");
            });

            editBox.getChildren().addAll(
                    editTitle,
                    new Label("Name:"), nameF,
                    new Label("University:"), uniF,
                    new Label("Student ID:"), idF,
                    new Label("Email:"), emailF,
                    new Label("Role:"), roleBox,
                    saveBtn, editMsg
            );
        });

        // Quick student list for reference
        Label listTitle = new Label("Registered Students:");
        listTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 0 0 0;");
        VBox quickList = new VBox(4);
        for (User u : UserStore.getAllUsers()) {
            Label item = new Label(u.getEmail() + " — " + u.getName() + " (" + u.getStudentId() + ")");
            item.setStyle("-fx-padding: 4 8 4 8; -fx-background-color: #0d1b2a; -fx-background-radius: 4;");
            quickList.getChildren().add(item);
        }

        box.getChildren().addAll(title, searchField, findBtn, msgLabel,
                new Separator(), editBox, new Separator(), listTitle, quickList);
        setScrollContent(box);
    }

    // ===================== MANAGE NOTICES =====================
    @FXML
    private void showManageNotices() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Campus Notices Management");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        TextArea noticeArea = new TextArea();
        noticeArea.setPromptText("Enter new campus notice...");
        noticeArea.setPrefRowCount(3);

        ComboBox<String> courseBox = new ComboBox<>();
        courseBox.setPromptText("Select Course (or General)");
        courseBox.getItems().add("GENERAL");
        for (Course c : DataStore.getAllCourses()) {
            courseBox.getItems().add(c.getCode());
        }
        courseBox.setValue("GENERAL");

        Label msgLabel = new Label();
        Button postBtn = new Button("Post Notice");
        postBtn.setOnAction(e -> {
            String content = noticeArea.getText().trim();
            String course = courseBox.getValue();
            if (content.isEmpty()) {
                msgLabel.setStyle("-fx-text-fill: #ff3366;");
                msgLabel.setText("Enter notice content.");
            } else {
                DataStore.addCourseNotice(course, content, "authority");
                msgLabel.setStyle("-fx-text-fill: #00ff88;");
                msgLabel.setText("Notice posted!");
                noticeArea.clear();
                showManageNotices();
            }
        });

        box.getChildren().addAll(title, courseBox, noticeArea, postBtn,
                msgLabel, new Separator());

        Label listTitle = new Label("All Notices:");
        listTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(listTitle);

        List<String[]> notices = DataStore.getAllNotices();
        if (notices.isEmpty()) {
            box.getChildren().add(new Label("No notices posted yet."));
        } else {
            for (String[] n : notices) {
                Label item = new Label("\uD83D\uDCCC [" + n[0] + "] " + n[1] + " (" + n[3] + ")");
                item.setStyle("-fx-padding: 8; -fx-background-color: #1a1a0a; -fx-background-radius: 6;");
                item.setWrapText(true);
                box.getChildren().add(item);
            }
        }
        setScrollContent(box);
    }

    // ===================== PAYMENT OVERVIEW =====================
    @FXML
    private void showPaymentOverview() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Payment Overview");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        List<Payment> payments = DataStore.getAllPayments();
        int totalAmount = 0;
        for (Payment p : payments) {
            totalAmount += p.getAmount();
        }

        Label summary = new Label("Total Collections: " + totalAmount
                + "/- from " + payments.size() + " payments");
        summary.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #00ff88;");

        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setStyle("-fx-background-color: transparent;");

        String[] headers = {"Student", "Type", "Amount", "Date", "Status"};
        for (int c = 0; c < headers.length; c++) {
            Label cell = new Label(headers[c]);
            cell.setStyle("-fx-font-weight: bold; -fx-padding: 10 16 10 16; "
                    + "-fx-background-color: #0d1b2a; -fx-text-fill: #00e5ff; -fx-border-color: rgba(0,229,255,0.3); -fx-min-width: 120;");
            cell.setMaxWidth(Double.MAX_VALUE);
            grid.add(cell, c, 0);
        }

        int row = 1;
        for (Payment p : payments) {
            String[] vals = {p.getStudentEmail(), p.getType(),
                p.getAmount() + "/-", p.getDate(), p.getStatus()};
            for (int c = 0; c < vals.length; c++) {
                Label cell = new Label(vals[c]);
                cell.setStyle("-fx-padding: 8 16 8 16; -fx-background-color: #111a2e; -fx-min-width: 120;");
                cell.setMaxWidth(Double.MAX_VALUE);
                grid.add(cell, c, row);
            }
            row++;
        }

        box.getChildren().addAll(title, summary, grid);
        if (payments.isEmpty()) {
            box.getChildren().add(new Label("No payments recorded yet."));
        }
        setScrollContent(box);
    }

    // ===================== EDIT GRADESHEET =====================
    @FXML
    private void showEditGradesheet() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("\uD83D\uDCDD Edit Gradesheet");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        // Search student
        TextField studentField = new TextField();
        studentField.setPromptText("Enter Student ID (e.g. 2405144)...");
        Button loadBtn = new Button("Load Grades");

        VBox gradeBox = new VBox(10);
        Label msgLabel = new Label();

        loadBtn.setOnAction(e -> {
            gradeBox.getChildren().clear();
            String studentId = studentField.getText().trim();
            if (studentId.isEmpty()) {
                msgLabel.setStyle("-fx-text-fill: #ff3366;");
                msgLabel.setText("Enter a student ID.");
                return;
            }
            msgLabel.setText("");

            Label studentTitle = new Label("Grades for: " + studentId);
            studentTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #00e5ff;");
            gradeBox.getChildren().add(studentTitle);

            // Show existing grades
            List<String[]> existing = DataStore.getGradesForStudent(studentId);
            if (!existing.isEmpty()) {
                Label existTitle = new Label("Current Grades:");
                existTitle.setStyle("-fx-font-weight: bold;");
                gradeBox.getChildren().add(existTitle);
                for (String[] g : existing) {
                    Label gLabel = new Label("   " + g[1] + " — Grade: " + g[2]);
                    gLabel.setStyle("-fx-padding: 4; -fx-background-color: #0a1a12; -fx-background-radius: 4;");
                    gradeBox.getChildren().add(gLabel);
                }
                gradeBox.getChildren().add(new Separator());
            }

            // Set grade form
            Label setTitle = new Label("Set/Update Grade:");
            setTitle.setStyle("-fx-font-weight: bold;");

            ComboBox<String> courseBox = new ComboBox<>();
            courseBox.setPromptText("Select Course");
            courseBox.setMaxWidth(Double.MAX_VALUE);
            for (Course c : DataStore.getAllCourses()) {
                courseBox.getItems().add(c.getCode() + " - " + c.getName());
            }

            TextField gradeField = new TextField();
            gradeField.setPromptText("Grade Point (e.g. 3.75)");

            Label setMsg = new Label();
            Button setBtn = new Button("Set Grade");
            setBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #00ff88; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
            setBtn.setOnAction(ev -> {
                String course = courseBox.getValue();
                String gp = gradeField.getText().trim();
                if (course == null || gp.isEmpty()) {
                    setMsg.setStyle("-fx-text-fill: #ff3366;");
                    setMsg.setText("Select course and enter grade.");
                    return;
                }
                try {
                    double grade = Double.parseDouble(gp);
                    String courseCode = course.split(" - ")[0];
                    DataStore.setGrade(studentId, courseCode, grade);
                    setMsg.setStyle("-fx-text-fill: #00ff88;");
                    setMsg.setText("Grade set for " + courseCode + ": " + grade + " \u2705");
                    gradeField.clear();
                    // Refresh view
                    loadBtn.fire();
                } catch (NumberFormatException ex) {
                    setMsg.setStyle("-fx-text-fill: #ff3366;");
                    setMsg.setText("Invalid grade point format.");
                }
            });

            gradeBox.getChildren().addAll(setTitle, courseBox, gradeField, setBtn, setMsg);
        });

        box.getChildren().addAll(title, studentField, loadBtn, msgLabel,
                new Separator(), gradeBox);
        setScrollContent(box);
    }

    // ===================== SYSTEM OVERVIEW =====================
    @FXML
    private void showSystemOverview() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("System Overview");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        int totalUsers = UserStore.getAllUsers().size();
        int totalCourses = DataStore.getAllCourses().size();
        int totalAssignments = DataStore.getAllAssignments().size();
        int totalPayments = DataStore.getAllPayments().size();
        int totalMessages = DataStore.getTotalMessageCount();

        String[][] stats = {
            {"\uD83D\uDC65", "Registered Students", String.valueOf(totalUsers)},
            {"\uD83D\uDCDA", "Active Courses", String.valueOf(totalCourses)},
            {"\uD83D\uDCDD", "Assignments", String.valueOf(totalAssignments)},
            {"\uD83D\uDCB0", "Payment Records", String.valueOf(totalPayments)},
            {"\uD83D\uDCE8", "Messages", String.valueOf(totalMessages)},};

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        for (int i = 0; i < stats.length; i++) {
            VBox card = new VBox(8);
            card.setAlignment(Pos.CENTER);
            card.setStyle("-fx-background-color: #111a2e; -fx-background-radius: 12; -fx-padding: 20; "
                    + "-fx-effect: dropshadow(gaussian, rgba(0,229,255,0.08), 8, 0, 0, 2); -fx-min-width: 160;");

            Label emoji = new Label(stats[i][0]);
            emoji.setStyle("-fx-font-size: 28px;");
            Label name = new Label(stats[i][1]);
            name.setStyle("-fx-font-weight: bold; -fx-text-fill: #8a9ab0;");
            Label value = new Label(stats[i][2]);
            value.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");
            card.getChildren().addAll(emoji, name, value);
            grid.add(card, i % 3, i / 3);
        }

        box.getChildren().addAll(title, grid);
        setScrollContent(box);
    }

    // ===================== MANAGE GAMES =====================
    @FXML
    private void showManageGames() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        Label title = new Label("\uD83C\uDFDF Manage Games & Sports");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        // Add form
        Label formLabel = new Label("Add New Sport");
        formLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        TextField sportField = new TextField(); sportField.setPromptText("Sport Name (e.g. Cricket)");
        TextField emojiField = new TextField(); emojiField.setPromptText("Emoji (e.g. \uD83C\uDFCF)");
        TextField schedField = new TextField(); schedField.setPromptText("Schedule (e.g. Sun & Wed 4-6 PM)");
        TextField venueField = new TextField(); venueField.setPromptText("Venue (e.g. Main Ground)");
        Label gMsg = new Label();
        Button addBtn = new Button("Add Sport");
        addBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #00ff88; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        addBtn.setOnAction(e -> {
            String sport = sportField.getText().trim();
            if (sport.isEmpty()) { gMsg.setStyle("-fx-text-fill: #ff3366;"); gMsg.setText("Sport name required."); return; }
            DataStore.addGame(sport, emojiField.getText().trim(), schedField.getText().trim(), venueField.getText().trim());
            gMsg.setStyle("-fx-text-fill: #00ff88;"); gMsg.setText("Added!");
            sportField.clear(); emojiField.clear(); schedField.clear(); venueField.clear();
            showManageGames();
        });

        box.getChildren().addAll(title, formLabel, sportField, emojiField, schedField, venueField, addBtn, gMsg, new Separator());

        // List current games
        Label listLabel = new Label("Current Sports:");
        listLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(listLabel);
        List<String[]> games = DataStore.getAllGames();
        if (games.isEmpty()) {
            box.getChildren().add(new Label("No games/sports data."));
        } else {
            for (String[] g : games) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-padding: 8 12 8 12; -fx-background-color: #111a2e; -fx-background-radius: 8;");
                Label info = new Label((g.length > 1 ? g[1] + " " : "") + g[0]
                        + (g.length > 2 ? " | " + g[2] : "") + (g.length > 3 ? " | " + g[3] : ""));
                info.setWrapText(true);
                info.setMaxWidth(500);
                Button del = new Button("\u274C");
                del.setOnAction(ev -> { DataStore.removeGame(g[0]); showManageGames(); });
                row.getChildren().addAll(info, del);
                box.getChildren().add(row);
            }
        }
        setScrollContent(box);
    }

    // ===================== MANAGE HOSPITAL =====================
    @FXML
    private void showManageHospital() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        Label title = new Label("\uD83C\uDFE5 Manage Hospital Doctors");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        Label formLabel = new Label("Add Doctor");
        formLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        TextField nameField = new TextField(); nameField.setPromptText("Doctor Name");
        TextField specField = new TextField(); specField.setPromptText("Specialization");
        TextField daysField = new TextField(); daysField.setPromptText("Available Days (e.g. Sun, Tue, Thu)");
        TextField hoursField = new TextField(); hoursField.setPromptText("Hours (e.g. 9:00 AM - 1:00 PM)");
        Label hMsg = new Label();
        Button addBtn = new Button("Add Doctor");
        addBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #00ff88; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        addBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) { hMsg.setStyle("-fx-text-fill: #ff3366;"); hMsg.setText("Name required."); return; }
            DataStore.addDoctor(name, specField.getText().trim(), daysField.getText().trim(), hoursField.getText().trim());
            hMsg.setStyle("-fx-text-fill: #00ff88;"); hMsg.setText("Added!");
            nameField.clear(); specField.clear(); daysField.clear(); hoursField.clear();
            showManageHospital();
        });

        box.getChildren().addAll(title, formLabel, nameField, specField, daysField, hoursField, addBtn, hMsg, new Separator());

        Label listLabel = new Label("Current Doctors:");
        listLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(listLabel);
        List<String[]> docs = DataStore.getAllDoctors();
        if (docs.isEmpty()) {
            box.getChildren().add(new Label("No doctors registered."));
        } else {
            for (String[] d : docs) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-padding: 8 12 8 12; -fx-background-color: #111a2e; -fx-background-radius: 8;");
                Label info = new Label(d[0] + " | " + (d.length > 1 ? d[1] : "") + " | "
                        + (d.length > 2 ? d[2] : "") + " | " + (d.length > 3 ? d[3] : ""));
                info.setWrapText(true);
                info.setMaxWidth(500);
                Button del = new Button("\u274C");
                del.setOnAction(ev -> { DataStore.removeDoctor(d[0]); showManageHospital(); });
                row.getChildren().addAll(info, del);
                box.getChildren().add(row);
            }
        }
        setScrollContent(box);
    }

    // ===================== MANAGE HALL =====================
    @FXML
    private void showManageHall() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        Label title = new Label("\uD83C\uDFE0 Manage Hall Rooms");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        Label formLabel = new Label("Add Hall Room");
        formLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        TextField hallField = new TextField(); hallField.setPromptText("Hall Name");
        TextField roomField = new TextField(); roomField.setPromptText("Room Number");
        TextField capField = new TextField(); capField.setPromptText("Capacity");
        Label rMsg = new Label();
        Button addBtn = new Button("Add Room");
        addBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #00ff88; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        addBtn.setOnAction(e -> {
            String hall = hallField.getText().trim();
            String room = roomField.getText().trim();
            if (hall.isEmpty() || room.isEmpty()) { rMsg.setStyle("-fx-text-fill: #ff3366;"); rMsg.setText("Hall & room required."); return; }
            DataStore.addHallRoom(hall, room, capField.getText().trim());
            rMsg.setStyle("-fx-text-fill: #00ff88;"); rMsg.setText("Added!");
            hallField.clear(); roomField.clear(); capField.clear();
            showManageHall();
        });

        box.getChildren().addAll(title, formLabel, hallField, roomField, capField, addBtn, rMsg, new Separator());

        Label listLabel = new Label("Current Hall Rooms:");
        listLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(listLabel);
        List<String[]> halls = DataStore.getAllHallRooms();
        if (halls.isEmpty()) {
            box.getChildren().add(new Label("No hall rooms defined."));
        } else {
            for (String[] h : halls) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-padding: 8 12 8 12; -fx-background-color: #111a2e; -fx-background-radius: 8;");
                Label info = new Label(h[0] + " - " + (h.length > 1 ? h[1] : "")
                        + (h.length > 2 ? " (Capacity: " + h[2] + ")" : ""));
                info.setWrapText(true);
                info.setMaxWidth(500);
                Button del = new Button("\u274C");
                del.setOnAction(ev -> { DataStore.removeHallRoom(h[0], h.length > 1 ? h[1] : ""); showManageHall(); });
                row.getChildren().addAll(info, del);
                box.getChildren().add(row);
            }
        }

        box.getChildren().add(new Separator());

        Label reqTitle = new Label("Evaluate Room Availability Requests");
        reqTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(reqTitle);

        List<String[]> requests = DataStore.getPendingHallAvailabilityRequests();
        Label evalMsg = new Label();

        if (requests.isEmpty()) {
            box.getChildren().add(new Label("No pending requests."));
        } else {
            for (String[] r : requests) {
                String requesterId = r[0];
                String requesterName = r[1];
                String hall = r[2];
                String room = r[3];
                String ts = r[5];

                int occupancy = DataStore.getHallRoomOccupancy(hall, room);
                boolean hasCapacity = DataStore.hasHallCapacity(hall, room);

                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-padding: 8 12 8 12; -fx-background-color: #0a1628; -fx-background-radius: 8;");

                Label info = new Label("Student: " + requesterName + " (" + requesterId + ")"
                        + " | Requested: " + hall + " - " + room
                        + " | Occupied: " + occupancy
                        + " | Requested At: " + ts);
                info.setWrapText(true);
                info.setMaxWidth(520);

                Button approveBtn = new Button("Approve");
                approveBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #00ff88; -fx-text-fill: white; -fx-background-radius: 6;");
                approveBtn.setDisable(!hasCapacity);
                approveBtn.setOnAction(ev -> {
                    if (!DataStore.hasHallCapacity(hall, room)) {
                        evalMsg.setStyle("-fx-text-fill: #ff3366;");
                        evalMsg.setText("Cannot approve " + requesterId + ": room is full.");
                        return;
                    }
                    DataStore.assignHallRoom(requesterId, hall, room);
                    DataStore.updateHallAvailabilityRequestStatus(requesterId, hall, room, "APPROVED");
                    evalMsg.setStyle("-fx-text-fill: #00ff88;");
                    evalMsg.setText("Approved request for " + requesterId + " (" + hall + " - " + room + ").");
                    showManageHall();
                });

                Button rejectBtn = new Button("Reject");
                rejectBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #ff3366; -fx-text-fill: white; -fx-background-radius: 6;");
                rejectBtn.setOnAction(ev -> {
                    DataStore.updateHallAvailabilityRequestStatus(requesterId, hall, room, "REJECTED");
                    evalMsg.setStyle("-fx-text-fill: #ff3366;");
                    evalMsg.setText("Rejected request for " + requesterId + " (" + hall + " - " + room + ").");
                    showManageHall();
                });

                row.getChildren().addAll(info, approveBtn, rejectBtn);
                box.getChildren().add(row);
            }
        }

        box.getChildren().add(evalMsg);
        setScrollContent(box);
    }

    // ===================== MANAGE USERS =====================

    @FXML
    private void showManageUsers() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(20));

        Label title = new Label("👥 USER MANAGEMENT");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        // === STUDENTS SECTION ===
        Label studentHeader = new Label("📋 STUDENTS");
        studentHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: #ffb300; -fx-padding: 10 0 4 0;");

        VBox studentList = new VBox(8);
        List<User> allUsers = UserStore.getAllUsers();

        for (User u : allUsers) {
            if (!"STUDENT".equalsIgnoreCase(u.getRole())) continue;

            VBox card = new VBox(8);
            card.setStyle("-fx-background-color: #111a2e; -fx-background-radius: 8; "
                    + "-fx-border-color: rgba(0,229,255,0.2); -fx-border-radius: 8; -fx-padding: 12;");

            HBox nameRow = new HBox(8);
            nameRow.setAlignment(Pos.CENTER_LEFT);
            Label nameLabel = new Label("Name:");
            nameLabel.setStyle("-fx-text-fill: #5a6a7e; -fx-font-size: 11px; -fx-min-width: 80;");
            TextField nameField = new TextField(u.getName());
            nameField.setStyle("-fx-background-color: #0a1628; -fx-text-fill: #d0d8e8; -fx-border-color: rgba(0,229,255,0.15); -fx-border-radius: 4; -fx-background-radius: 4;");
            nameRow.getChildren().addAll(nameLabel, nameField);

            HBox emailRow = new HBox(8);
            emailRow.setAlignment(Pos.CENTER_LEFT);
            Label emailLabel = new Label("Email:");
            emailLabel.setStyle("-fx-text-fill: #5a6a7e; -fx-font-size: 11px; -fx-min-width: 80;");
            TextField emailField = new TextField(u.getEmail());
            emailField.setStyle("-fx-background-color: #0a1628; -fx-text-fill: #d0d8e8; -fx-border-color: rgba(0,229,255,0.15); -fx-border-radius: 4; -fx-background-radius: 4;");
            emailRow.getChildren().addAll(emailLabel, emailField);

            HBox passRow = new HBox(8);
            passRow.setAlignment(Pos.CENTER_LEFT);
            Label passLabel = new Label("Password:");
            passLabel.setStyle("-fx-text-fill: #5a6a7e; -fx-font-size: 11px; -fx-min-width: 80;");
            TextField passField = new TextField(u.getPassword());
            passField.setStyle("-fx-background-color: #0a1628; -fx-text-fill: #d0d8e8; -fx-border-color: rgba(0,229,255,0.15); -fx-border-radius: 4; -fx-background-radius: 4;");
            passRow.getChildren().addAll(passLabel, passField);

            HBox uniRow = new HBox(8);
            uniRow.setAlignment(Pos.CENTER_LEFT);
            Label uniLabel = new Label("University:");
            uniLabel.setStyle("-fx-text-fill: #5a6a7e; -fx-font-size: 11px; -fx-min-width: 80;");
            TextField uniField = new TextField(u.getUniversity());
            uniField.setStyle("-fx-background-color: #0a1628; -fx-text-fill: #d0d8e8; -fx-border-color: rgba(0,229,255,0.15); -fx-border-radius: 4; -fx-background-radius: 4;");
            uniRow.getChildren().addAll(uniLabel, uniField);

            HBox idRow = new HBox(8);
            idRow.setAlignment(Pos.CENTER_LEFT);
            Label idLabel = new Label("Student ID:");
            idLabel.setStyle("-fx-text-fill: #5a6a7e; -fx-font-size: 11px; -fx-min-width: 80;");
            TextField idField = new TextField(u.getStudentId());
            idField.setStyle("-fx-background-color: #0a1628; -fx-text-fill: #d0d8e8; -fx-border-color: rgba(0,229,255,0.15); -fx-border-radius: 4; -fx-background-radius: 4;");
            idRow.getChildren().addAll(idLabel, idField);

            Label saveMsg = new Label();
            saveMsg.setStyle("-fx-font-size: 11px;");

            Button saveBtn = new Button("Save Changes");
            saveBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #00ff88; "
                    + "-fx-border-color: #00ff88; -fx-border-radius: 6; -fx-background-radius: 6; "
                    + "-fx-font-weight: 800; -fx-cursor: hand;");

            final String oldEmail = u.getEmail();
            saveBtn.setOnAction(e -> {
                String newName = nameField.getText().trim();
                String newEmail = emailField.getText().trim();
                String newPass = passField.getText().trim();
                String newUni = uniField.getText().trim();
                String newId = idField.getText().trim();

                if (newName.isEmpty() || newEmail.isEmpty() || newPass.isEmpty()) {
                    saveMsg.setStyle("-fx-text-fill: #ff3366; -fx-font-size: 11px;");
                    saveMsg.setText("Name, email and password cannot be empty.");
                    return;
                }

                // Remove old entry and add updated
                UserStore.removeUser(oldEmail);
                User updated = new User(newName, newUni, newId, newEmail, newPass);
                updated.setRole("STUDENT");
                UserStore.addUser(updated);

                saveMsg.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 11px;");
                saveMsg.setText("✓ Saved");
            });

            card.getChildren().addAll(nameRow, emailRow, passRow, uniRow, idRow, saveBtn, saveMsg);
            studentList.getChildren().add(card);
        }

        if (studentList.getChildren().isEmpty()) {
            studentList.getChildren().add(new Label("No students registered."));
        }

        // === TEACHERS SECTION ===
        Label teacherHeader = new Label("📋 TEACHERS");
        teacherHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: #ffb300; -fx-padding: 10 0 4 0;");

        VBox teacherList = new VBox(8);
        List<String[]> teachers = DataStore.getAllTeacherProfiles();

        for (String[] t : teachers) {
            // Format: name|dept|designation|type|password|email
            String tName = t[0];
            String tDept = t[1];
            String tDesig = t.length > 2 ? t[2] : "";
            String tType = t.length > 3 ? t[3] : "";
            String tPass = t.length > 4 ? t[4] : "";
            String tEmail = t.length > 5 ? t[5] : "";

            VBox card = new VBox(8);
            card.setStyle("-fx-background-color: #111a2e; -fx-background-radius: 8; "
                    + "-fx-border-color: rgba(255,179,0,0.2); -fx-border-radius: 8; -fx-padding: 12;");

            HBox tnameRow = new HBox(8);
            tnameRow.setAlignment(Pos.CENTER_LEFT);
            Label tnl = new Label("Name:");
            tnl.setStyle("-fx-text-fill: #5a6a7e; -fx-font-size: 11px; -fx-min-width: 80;");
            TextField tnf = new TextField(tName);
            tnf.setStyle("-fx-background-color: #0a1628; -fx-text-fill: #d0d8e8; -fx-border-color: rgba(0,229,255,0.15); -fx-border-radius: 4; -fx-background-radius: 4;");
            tnameRow.getChildren().addAll(tnl, tnf);

            HBox temailRow = new HBox(8);
            temailRow.setAlignment(Pos.CENTER_LEFT);
            Label tel = new Label("Email:");
            tel.setStyle("-fx-text-fill: #5a6a7e; -fx-font-size: 11px; -fx-min-width: 80;");
            TextField tef = new TextField(tEmail);
            tef.setStyle("-fx-background-color: #0a1628; -fx-text-fill: #d0d8e8; -fx-border-color: rgba(0,229,255,0.15); -fx-border-radius: 4; -fx-background-radius: 4;");
            temailRow.getChildren().addAll(tel, tef);

            HBox tpassRow = new HBox(8);
            tpassRow.setAlignment(Pos.CENTER_LEFT);
            Label tpl = new Label("Password:");
            tpl.setStyle("-fx-text-fill: #5a6a7e; -fx-font-size: 11px; -fx-min-width: 80;");
            TextField tpf = new TextField(tPass);
            tpf.setStyle("-fx-background-color: #0a1628; -fx-text-fill: #d0d8e8; -fx-border-color: rgba(0,229,255,0.15); -fx-border-radius: 4; -fx-background-radius: 4;");
            tpassRow.getChildren().addAll(tpl, tpf);

            HBox tdeptRow = new HBox(8);
            tdeptRow.setAlignment(Pos.CENTER_LEFT);
            Label tdl = new Label("Dept:");
            tdl.setStyle("-fx-text-fill: #5a6a7e; -fx-font-size: 11px; -fx-min-width: 80;");
            TextField tdf = new TextField(tDept);
            tdf.setStyle("-fx-background-color: #0a1628; -fx-text-fill: #d0d8e8; -fx-border-color: rgba(0,229,255,0.15); -fx-border-radius: 4; -fx-background-radius: 4;");
            tdeptRow.getChildren().addAll(tdl, tdf);

            HBox tdesigRow = new HBox(8);
            tdesigRow.setAlignment(Pos.CENTER_LEFT);
            Label tdesl = new Label("Designation:");
            tdesl.setStyle("-fx-text-fill: #5a6a7e; -fx-font-size: 11px; -fx-min-width: 80;");
            TextField tdesf = new TextField(tDesig);
            tdesf.setStyle("-fx-background-color: #0a1628; -fx-text-fill: #d0d8e8; -fx-border-color: rgba(0,229,255,0.15); -fx-border-radius: 4; -fx-background-radius: 4;");
            tdesigRow.getChildren().addAll(tdesl, tdesf);

            Label tMsg = new Label();
            tMsg.setStyle("-fx-font-size: 11px;");

            Button tSaveBtn = new Button("Save Changes");
            tSaveBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #00ff88; "
                    + "-fx-border-color: #00ff88; -fx-border-radius: 6; -fx-background-radius: 6; "
                    + "-fx-font-weight: 800; -fx-cursor: hand;");

            tSaveBtn.setOnAction(e -> {
                String nName = tnf.getText().trim();
                String nEmail = tef.getText().trim();
                String nPass = tpf.getText().trim();
                String nDept = tdf.getText().trim();
                String nDesig = tdesf.getText().trim();

                if (nName.isEmpty() || nPass.isEmpty()) {
                    tMsg.setStyle("-fx-text-fill: #ff3366; -fx-font-size: 11px;");
                    tMsg.setText("Name and password cannot be empty.");
                    return;
                }

                DataStore.saveTeacherProfile(nName, nDept, nDesig, tType, nPass, nEmail);

                tMsg.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 11px;");
                tMsg.setText("✓ Saved");
            });

            card.getChildren().addAll(tnameRow, temailRow, tpassRow, tdeptRow, tdesigRow, tSaveBtn, tMsg);
            teacherList.getChildren().add(card);
        }

        if (teacherList.getChildren().isEmpty()) {
            teacherList.getChildren().add(new Label("No teachers registered."));
        }

        box.getChildren().addAll(title, new Separator(), studentHeader, studentList,
                new Separator(), teacherHeader, teacherList);

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