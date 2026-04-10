package com.example.moodle.controller;

import java.util.List;

import com.example.moodle.model.Course;
import com.example.moodle.model.Payment;
import com.example.moodle.model.User;
import com.example.moodle.service.DataStore;
import com.example.moodle.util.SceneManager;
import com.example.moodle.util.Session;
import com.example.moodle.util.UniversityDatabase;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class AuthorityDashboardController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Label uniNameLabel;

    @FXML
    public void initialize() {
        String uni = getResolvedUniversityName();
        if (uniNameLabel != null) {
            uniNameLabel.setText("Admin Control — " + uni);
        }
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

    private String getResolvedUniversityName() {
        String u = Session.getSelectedUniversity();
        if (u == null || u.isEmpty()) {
            // Fallback to login university
            u = Session.getUniversity();
        }
        if (u == null || u.isEmpty()) return "global";
        com.example.moodle.model.UniversityInfo info = com.example.moodle.util.UniversityDatabase.getUniversity(u);
        return info != null ? info.getShortName() : u;
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
        VBox editBox = new VBox(10); // Container for edit form

        Runnable refreshTable = () -> {
            tableBox.getChildren().clear();
            String query = searchField.getText().trim().toLowerCase();

            // Header row
            HBox headerRow = new HBox(2);
            String[] headers = {"Name", "University", "Student ID", "Email", "Role", "Action"};
            for (String h : headers) {
                Label cell = new Label(h);
                cell.setStyle("-fx-font-weight: bold; -fx-padding: 10 16 10 16; "
                        + "-fx-background-color: #0d1b2a; -fx-text-fill: #00e5ff; -fx-border-color: rgba(0,229,255,0.3); "
                        + "-fx-min-width: 130; -fx-pref-width: 130;");
                headerRow.getChildren().add(cell);
            }
            tableBox.getChildren().add(headerRow);

            List<User> users = UserStore.getAllUsers();
            String currentUni = UniversityDatabase.extractShortName(getResolvedUniversityName());
            java.util.List<User> reversed = new java.util.ArrayList<>();
            for (User u : users) {
                String userShort = UniversityDatabase.extractShortName(u.getUniversity());
                if ("global".equals(currentUni) || currentUni.equalsIgnoreCase(userShort)) {
                    reversed.add(u);
                }
            }
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
                row.setAlignment(Pos.CENTER_LEFT);
                String bg = count % 2 == 0 ? "#111a2e" : "#0d1b2a";
                String[] vals = {u.getName(), u.getUniversity(), u.getStudentId(),
                    u.getEmail(), u.getRole()};
                for (int i = 0; i < vals.length; i++) {
                    Label cell = new Label(vals[i] != null ? vals[i] : "");
                    String style = "-fx-padding: 8 16 8 16; -fx-background-color: " + bg
                            + "; -fx-min-width: 130; -fx-pref-width: 130; -fx-text-fill: #e6edf3;";
                    if (i == 0) {
                        style += " -fx-font-weight: bold; -fx-text-fill: #00e5ff;";
                    }
                    cell.setStyle(style);
                    row.getChildren().add(cell);
                }

                // Add Edit Button
                Button editBtn = new Button("Edit");
                editBtn.setStyle("-fx-background-color: #00e5ff; -fx-text-fill: #0a0e1a; -fx-font-size: 11px;");
                editBtn.setOnAction(e -> {
                    editBox.getChildren().clear();
                    Label editTitle = new Label("Editing: " + u.getName());
                    editTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #00ff88;");

                    TextField nameF = new TextField(u.getName());
                    nameF.setPromptText("Name");
                    TextField uniF = new TextField(u.getUniversity());
                    uniF.setPromptText("University");
                    TextField idF = new TextField(u.getStudentId());
                    idF.setPromptText("Student ID");
                    TextField emailF = new TextField(u.getEmail());
                    emailF.setPromptText("Email");
                    emailF.setDisable(true); // Don't edit email

                    ComboBox<String> roleBox = new ComboBox<>();
                    roleBox.getItems().addAll("STUDENT", "TEACHER", "AUTHORITY");
                    roleBox.setValue(u.getRole());
                    roleBox.setMaxWidth(Double.MAX_VALUE);

                    Button saveBtn = new Button("Save Changes");
                    saveBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #00ff88; -fx-text-fill: white;");
                    Label editMsg = new Label();
                    saveBtn.setOnAction(ev -> {
                        u.setName(nameF.getText().trim());
                        u.setUniversity(uniF.getText().trim());
                        u.setStudentId(idF.getText().trim());
                        u.setRole(roleBox.getValue());
                        UserStore.updateUser(u);
                        editMsg.setStyle("-fx-text-fill: #00ff88;");
                        editMsg.setText("Saved!");
                        // Refresh to show new data
                        // refreshTable.run(); // Need to call it later or re-invoke. 
                        // It's cleaner to just clear editBox and refresh search
                        editBox.getChildren().clear();
                        searchField.setText(searchField.getText() + " "); // trigger refresh
                        searchField.setText(searchField.getText().trim());
                    });

                    Button cancelBtn = new Button("Cancel");
                    cancelBtn.setOnAction(ev -> editBox.getChildren().clear());
                    
                    HBox btnBox = new HBox(10, saveBtn, cancelBtn);

                    editBox.setStyle("-fx-padding: 15; -fx-background-color: #111a2e; -fx-border-color: #00e5ff; -fx-border-radius: 8; -fx-background-radius: 8;");
                    editBox.getChildren().addAll(
                            editTitle,
                            new Label("Name:"), nameF,
                            new Label("University:"), uniF,
                            new Label("Student ID:"), idF,
                            new Label("Role:"), roleBox,
                            btnBox, editMsg
                    );
                });

                HBox actionCell = new HBox(editBtn);
                actionCell.setAlignment(Pos.CENTER);
                actionCell.setStyle("-fx-padding: 8 16 8 16; -fx-background-color: " + bg + "; -fx-min-width: 130; -fx-pref-width: 130;");
                row.getChildren().add(actionCell);

                tableBox.getChildren().add(row);
                count++;
            }
        };

        refreshTable.run();
        searchField.textProperty().addListener((obs, o, n) -> refreshTable.run());

        String curUni = getResolvedUniversityName();
        long filteredCount = UserStore.getAllUsers().stream()
                 .filter(u -> "global".equals(curUni) || (u.getUniversity() != null && u.getUniversity().equalsIgnoreCase(curUni)))
                 .count();
        Label countLabel = new Label("Total Students: " + filteredCount);
        countLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8a9ab0;");

        box.getChildren().addAll(title, countLabel, searchField, editBox, tableBox);
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

        final String[] pdfPath = {""};
        Label pdfLabel = new Label("No PDF selected");
        pdfLabel.setStyle("-fx-text-fill: #5a6a7e; -fx-font-size: 11px;");
        Button pdfBtn = new Button("\uD83D\uDCC4 Attach PDF");
        pdfBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #ffb300; -fx-text-fill: white; -fx-background-radius: 6;");
        pdfBtn.setOnAction(e -> {
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            java.io.File file = fc.showOpenDialog(contentArea.getScene().getWindow());
            if (file != null) {
                pdfPath[0] = file.getAbsolutePath();
                pdfLabel.setText("\u2705 " + file.getName());
                pdfLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 11px;");
            }
        });
        HBox pdfRow = new HBox(10, pdfBtn, pdfLabel);
        pdfRow.setAlignment(Pos.CENTER_LEFT);

        Label msgLabel = new Label();
        Button postBtn = new Button("Post Notice");
        postBtn.setStyle("-fx-background-color: #00e5ff; -fx-text-fill: #0a0e1a; -fx-font-weight: bold;");
        postBtn.setOnAction(e -> {
            String content = noticeArea.getText().trim();
            String course = courseBox.getValue();
            if (content.isEmpty() && pdfPath[0].isEmpty()) {
                msgLabel.setStyle("-fx-text-fill: #ff3366;");
                msgLabel.setText("Enter notice content or attach PDF.");
            } else {
                if (!pdfPath[0].isEmpty()) {
                    content += " [PDF:" + pdfPath[0] + "]";
                }
                DataStore.addCourseNotice(course, content, "authority");
                msgLabel.setStyle("-fx-text-fill: #00ff88;");
                msgLabel.setText("Notice posted!");
                noticeArea.clear();
                showManageNotices();
            }
        });

        box.getChildren().addAll(title, courseBox, noticeArea, pdfRow, postBtn,
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

        List<Payment> allPayments = DataStore.getAllPayments();
        String currentUni = UniversityDatabase.extractShortName(getResolvedUniversityName());
        List<Payment> payments = new java.util.ArrayList<>();
        for (Payment p : allPayments) {
            User u = UserStore.getUserByStudentId(p.getStudentEmail()); // Note: payment sometimes stores email or ID. Let's just check UserStore
            if (u == null) {
                // fall back to getting by email
                for (User temp : UserStore.getAllUsers()) {
                    if (p.getStudentEmail().equalsIgnoreCase(temp.getEmail())) {
                        u = temp; break;
                    }
                }
            }
            String userShort = (u != null) ? UniversityDatabase.extractShortName(u.getUniversity()) : "";
            if ("global".equals(currentUni) || currentUni.equalsIgnoreCase(userShort)) {
                payments.add(p);
            }
        }
        
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
            
            User sUser = UserStore.getUserByStudentId(studentId);
            String currentUni = getResolvedUniversityName();
            if (sUser == null || (!"global".equals(currentUni) && (sUser.getUniversity() == null || !sUser.getUniversity().equalsIgnoreCase(currentUni)))) {
                msgLabel.setStyle("-fx-text-fill: #ff3366;");
                msgLabel.setText("Student not found or belongs to another university.");
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

        String curUni = UniversityDatabase.extractShortName(getResolvedUniversityName());
        long totalUsers = UserStore.getAllUsers().stream()
                .filter(u -> "global".equals(curUni) || curUni.equalsIgnoreCase(UniversityDatabase.extractShortName(u.getUniversity())))
                .count();
        long totalCourses = DataStore.getAllCourses().stream()
                .filter(c -> "global".equals(curUni) || curUni.equalsIgnoreCase(UniversityDatabase.extractShortName(c.getUniversity())))
                .count();

        List<Payment> allPayments = DataStore.getAllPayments();
        long totalFilteredPayments = allPayments.stream().filter(p -> {
            User u = UserStore.getUserByStudentId(p.getStudentEmail());
            if (u == null) {
                for (User temp : UserStore.getAllUsers()) {
                    if (p.getStudentEmail().equalsIgnoreCase(temp.getEmail())) { u = temp; break; }
                }
            }
            String userShort = (u != null) ? UniversityDatabase.extractShortName(u.getUniversity()) : "";
            return "global".equals(curUni) || curUni.equalsIgnoreCase(userShort);
        }).count();

        int totalMessages = DataStore.getTotalMessageCount();

        String[][] stats = {
            {"\uD83D\uDC65", "Students", String.valueOf(totalUsers)},
            {"\uD83D\uDCDA", "Courses", String.valueOf(totalCourses)},
            {"\uD83D\uDCB0", "Payments", String.valueOf(totalFilteredPayments)},
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
            DataStore.addGame(getResolvedUniversityName(), sport, emojiField.getText().trim(), schedField.getText().trim(), venueField.getText().trim());
            gMsg.setStyle("-fx-text-fill: #00ff88;"); gMsg.setText("Added!");
            sportField.clear(); emojiField.clear(); schedField.clear(); venueField.clear();
            showManageGames();
        });

        box.getChildren().addAll(title, formLabel, sportField, emojiField, schedField, venueField, addBtn, gMsg, new Separator());

        // List current games
        Label listLabel = new Label("Current Sports:");
        listLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(listLabel);
        List<String[]> games = DataStore.getAllGames(getResolvedUniversityName());
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
                del.setOnAction(ev -> { DataStore.removeGame(getResolvedUniversityName(), g[0]); showManageGames(); });
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
            DataStore.addDoctor(getResolvedUniversityName(), name, specField.getText().trim(), daysField.getText().trim(), hoursField.getText().trim());
            hMsg.setStyle("-fx-text-fill: #00ff88;"); hMsg.setText("Added!");
            nameField.clear(); specField.clear(); daysField.clear(); hoursField.clear();
            showManageHospital();
        });

        box.getChildren().addAll(title, formLabel, nameField, specField, daysField, hoursField, addBtn, hMsg, new Separator());

        Label listLabel = new Label("Current Doctors:");
        listLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(listLabel);
        List<String[]> docs = DataStore.getAllDoctors(getResolvedUniversityName());
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
                del.setOnAction(ev -> { DataStore.removeDoctor(getResolvedUniversityName(), d[0]); showManageHospital(); });
                row.getChildren().addAll(info, del);
                box.getChildren().add(row);
            }
        }
        setScrollContent(box);
    }

    // ===================== MANAGE NEWS =====================
    @FXML
    private void showManageNews() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        Label title = new Label("\uD83D\uDCF0 Manage Latest News");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        TextField headField = new TextField(); headField.setPromptText("Headline");
        TextArea contentArea = new TextArea(); contentArea.setPromptText("News Content...");
        contentArea.setPrefRowCount(3);
        Label nMsg = new Label();
        Button addBtn = new Button("Post News");
        addBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #00ff88; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        addBtn.setOnAction(e -> {
            String h = headField.getText().trim();
            String c = contentArea.getText().trim();
            if (h.isEmpty() || c.isEmpty()) { nMsg.setStyle("-fx-text-fill: #ff3366;"); nMsg.setText("Headline & content required."); return; }
            DataStore.addNews(getResolvedUniversityName(), h, c);
            nMsg.setStyle("-fx-text-fill: #00ff88;"); nMsg.setText("News posted!");
            headField.clear(); contentArea.clear();
            showManageNews();
        });

        box.getChildren().addAll(title, headField, contentArea, addBtn, nMsg, new Separator());

        List<String[]> news = DataStore.getAllNews(getResolvedUniversityName());
        for (String[] n : news) {
            VBox row = new VBox(5);
            row.setStyle("-fx-padding: 10; -fx-background-color: #111a2e; -fx-background-radius: 8;");
            HBox header = new HBox(10);
            Label hLabel = new Label(n[0]); hLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #00e5ff;");
            Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
            Button del = new Button("\uD83D\uDDD1");
            del.setOnAction(ev -> { DataStore.removeNews(getResolvedUniversityName(), n[0]); showManageNews(); });
            header.getChildren().addAll(hLabel, spacer, del);
            Label cLabel = new Label(n[1]); cLabel.setWrapText(true);
            Label tLabel = new Label("Posted: " + n[2]); tLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #8a9ab0;");
            row.getChildren().addAll(header, cLabel, tLabel);
            box.getChildren().add(row);
        }
        setScrollContent(box);
    }

    // ===================== MANAGE EVENTS =====================
    @FXML
    private void showManageEvents() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        Label title = new Label("\uD83D\uDCC5 Manage Upcoming Events");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        TextField tField = new TextField(); tField.setPromptText("Event Title");
        TextField dField = new TextField(); dField.setPromptText("Date (e.g. Oct 15, 2025)");
        TextArea descArea = new TextArea(); descArea.setPromptText("Description...");
        descArea.setPrefRowCount(2);
        Label eMsg = new Label();
        Button addBtn = new Button("Add Event");
        addBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #00ff88; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        addBtn.setOnAction(e -> {
            String t = tField.getText().trim();
            String d = dField.getText().trim();
            if (t.isEmpty() || d.isEmpty()) { eMsg.setStyle("-fx-text-fill: #ff3366;"); eMsg.setText("Title & date required."); return; }
            DataStore.addEvent(getResolvedUniversityName(), t, d, descArea.getText().trim());
            eMsg.setStyle("-fx-text-fill: #00ff88;"); eMsg.setText("Event added!");
            tField.clear(); dField.clear(); descArea.clear();
            showManageEvents();
        });

        box.getChildren().addAll(title, tField, dField, descArea, addBtn, eMsg, new Separator());

        List<String[]> events = DataStore.getAllEvents(getResolvedUniversityName());
        for (String[] ev : events) {
            VBox row = new VBox(5);
            row.setStyle("-fx-padding: 10; -fx-background-color: #111a2e; -fx-background-radius: 8;");
            HBox header = new HBox(10);
            Label tLabel = new Label(ev[0]); tLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #00ff88;");
            Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
            Button del = new Button("\uD83D\uDDD1");
            del.setOnAction(e -> { DataStore.removeEvent(getResolvedUniversityName(), ev[0]); showManageEvents(); });
            header.getChildren().addAll(tLabel, spacer, del);
            Label dLabel = new Label("\uD83D\uDCC5 " + ev[1]); dLabel.setStyle("-fx-text-fill: #8a9ab0;");
            Label descLabel = new Label(ev.length > 2 ? ev[2] : ""); descLabel.setWrapText(true);
            row.getChildren().addAll(header, dLabel, descLabel);
            box.getChildren().add(row);
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
            DataStore.addHallRoom(getResolvedUniversityName(), hall, room, capField.getText().trim());
            rMsg.setStyle("-fx-text-fill: #00ff88;"); rMsg.setText("Added!");
            hallField.clear(); roomField.clear(); capField.clear();
            showManageHall();
        });

        box.getChildren().addAll(title, formLabel, hallField, roomField, capField, addBtn, rMsg, new Separator());

        Label listLabel = new Label("Current Hall Rooms:");
        listLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(listLabel);
        List<String[]> halls = DataStore.getAllHallRooms(getResolvedUniversityName());
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
                del.setOnAction(ev -> { DataStore.removeHallRoom(getResolvedUniversityName(), h[0], h.length > 1 ? h[1] : ""); showManageHall(); });
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
                boolean hasCapacity = DataStore.hasHallCapacity(getResolvedUniversityName(), hall, room);

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
                    if (!DataStore.hasHallCapacity(getResolvedUniversityName(), hall, room)) {
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

                DataStore.saveTeacherProfile(nName, nDept, nDesig, tType, nPass, nEmail, getResolvedUniversityName());

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

    // ===================== MANAGE FACULTY =====================
    @FXML
    private void showManageFaculty() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        Label title = new Label("\uD83D\uDCCB Manage Faculty Members");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        TextField nameF = new TextField(); nameF.setPromptText("Name");
        TextField deptF = new TextField(); deptF.setPromptText("Department");
        TextField desigF = new TextField(); desigF.setPromptText("Designation");
        TextField emailF = new TextField(); emailF.setPromptText("Email");
        TextField phoneF = new TextField(); phoneF.setPromptText("Phone");
        Label msg = new Label();
        Button addBtn = new Button("Add Faculty");
        addBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #00ff88; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        addBtn.setOnAction(e -> {
            if (nameF.getText().trim().isEmpty()) { msg.setStyle("-fx-text-fill: #ff3366;"); msg.setText("Name required."); return; }
            DataStore.addFacultyMember(getResolvedUniversityName(), nameF.getText().trim(), deptF.getText().trim(), desigF.getText().trim(), emailF.getText().trim(), phoneF.getText().trim());
            msg.setStyle("-fx-text-fill: #00ff88;"); msg.setText("Faculty added!");
            nameF.clear(); deptF.clear(); desigF.clear(); emailF.clear(); phoneF.clear();
            showManageFaculty();
        });

        box.getChildren().addAll(title, nameF, deptF, desigF, emailF, phoneF, addBtn, msg, new Separator());

        Label listTitle = new Label("Faculty Members:");
        listTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(listTitle);

        java.util.List<String[]> faculty = DataStore.getAllFacultyMembers(getResolvedUniversityName());
        if (faculty.isEmpty()) { box.getChildren().add(new Label("No faculty members added.")); }
        else {
            for (String[] f : faculty) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-padding: 8 12; -fx-background-color: #111a2e; -fx-background-radius: 8;");
                Label info = new Label(f[0] + " | " + f[1] + " | " + f[2] + " | " + f[3] + " | " + f[4]);
                info.setWrapText(true); info.setMaxWidth(500);
                Button del = new Button("\u274C");
                del.setOnAction(ev -> { DataStore.removeFacultyMember(getResolvedUniversityName(), f[0]); showManageFaculty(); });
                row.getChildren().addAll(info, del);
                box.getChildren().add(row);
            }
        }
        setScrollContent(box);
    }

    // ===================== MANAGE ALUMNI =====================
    @FXML
    private void showManageAlumni() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        Label title = new Label("\uD83C\uDF93 Manage Alumni");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        TextField nameF = new TextField(); nameF.setPromptText("Name");
        TextField batchF = new TextField(); batchF.setPromptText("Batch (e.g. 2020)");
        TextField deptF = new TextField(); deptF.setPromptText("Department");
        TextField posF = new TextField(); posF.setPromptText("Current Position");
        Label msg = new Label();
        Button addBtn = new Button("Add Alumni");
        addBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #00ff88; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        addBtn.setOnAction(e -> {
            if (nameF.getText().trim().isEmpty()) { msg.setStyle("-fx-text-fill: #ff3366;"); msg.setText("Name required."); return; }
            DataStore.addAlumni(getResolvedUniversityName(), nameF.getText().trim(), batchF.getText().trim(), deptF.getText().trim(), posF.getText().trim());
            msg.setStyle("-fx-text-fill: #00ff88;"); msg.setText("Alumni added!");
            nameF.clear(); batchF.clear(); deptF.clear(); posF.clear();
            showManageAlumni();
        });

        box.getChildren().addAll(title, nameF, batchF, deptF, posF, addBtn, msg, new Separator());

        Label listTitle = new Label("Alumni Records:");
        listTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(listTitle);

        java.util.List<String[]> alumni = DataStore.getAllAlumni(getResolvedUniversityName());
        if (alumni.isEmpty()) { box.getChildren().add(new Label("No alumni added.")); }
        else {
            for (String[] a : alumni) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-padding: 8 12; -fx-background-color: #111a2e; -fx-background-radius: 8;");
                Label info = new Label(a[0] + " | Batch: " + a[1] + " | " + a[2] + " | " + a[3]);
                info.setWrapText(true); info.setMaxWidth(500);
                Button del = new Button("\u274C");
                del.setOnAction(ev -> { DataStore.removeAlumni(getResolvedUniversityName(), a[0]); showManageAlumni(); });
                row.getChildren().addAll(info, del);
                box.getChildren().add(row);
            }
        }
        setScrollContent(box);
    }

    // ===================== UNIVERSITY NOTICES =====================
    @FXML
    private void showUniversityNotices() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        Label title = new Label("\uD83D\uDCE2 University Notices");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        TextField titleF = new TextField(); titleF.setPromptText("Notice Title");
        TextArea contentF = new TextArea(); contentF.setPromptText("Notice Content"); contentF.setPrefRowCount(3);
        Label msg = new Label();
        Button addBtn = new Button("Post Notice");
        addBtn.setStyle("-fx-background-color: #00e5ff; -fx-text-fill: #0a0e1a; -fx-font-weight: bold;");
        addBtn.setOnAction(e -> {
            if (titleF.getText().trim().isEmpty()) { msg.setStyle("-fx-text-fill: #ff3366;"); msg.setText("Title required."); return; }
            DataStore.addUniversityNotice(getResolvedUniversityName(), titleF.getText().trim(), contentF.getText().trim(), "admin");
            msg.setStyle("-fx-text-fill: #00ff88;"); msg.setText("Notice posted!");
            titleF.clear(); contentF.clear();
            showUniversityNotices();
        });

        box.getChildren().addAll(title, titleF, contentF, addBtn, msg, new Separator());

        Label listTitle = new Label("Posted University Notices:");
        listTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(listTitle);

        java.util.List<String[]> notices = DataStore.getAllUniversityNotices(getResolvedUniversityName());
        if (notices.isEmpty()) { box.getChildren().add(new Label("No university notices.")); }
        else {
            for (String[] n : notices) {
                VBox card = new VBox(6);
                card.setStyle("-fx-padding: 12; -fx-background-color: #111a2e; -fx-background-radius: 8;");
                Label tl = new Label(n[0]); tl.setStyle("-fx-font-weight: bold; -fx-text-fill: #ffb300;");
                Label cl = new Label(n[1]); cl.setWrapText(true);
                Label dl = new Label("By " + n[2] + " on " + n[3]); dl.setStyle("-fx-text-fill: #5a6a7e; -fx-font-size: 11px;");
                Button del = new Button("\u274C Remove");
                del.setOnAction(ev -> { DataStore.removeUniversityNotice(getResolvedUniversityName(), n[0]); showUniversityNotices(); });
                card.getChildren().addAll(tl, cl, dl, del);
                box.getChildren().add(card);
            }
        }
        setScrollContent(box);
    }

    // ===================== JOB NOTICES =====================
    @FXML
    private void showJobNotices() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        Label title = new Label("\uD83D\uDCBC Job Notices");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        TextField titleF = new TextField(); titleF.setPromptText("Job Title");
        TextArea descF = new TextArea(); descF.setPromptText("Job Description"); descF.setPrefRowCount(3);
        TextField companyF = new TextField(); companyF.setPromptText("Company / Organization");
        TextField deadlineF = new TextField(); deadlineF.setPromptText("Application Deadline");
        Label msg = new Label();
        Button addBtn = new Button("Post Job Notice");
        addBtn.setStyle("-fx-background-color: #00e5ff; -fx-text-fill: #0a0e1a; -fx-font-weight: bold;");
        addBtn.setOnAction(e -> {
            if (titleF.getText().trim().isEmpty()) { msg.setStyle("-fx-text-fill: #ff3366;"); msg.setText("Title required."); return; }
            DataStore.addJobNotice(getResolvedUniversityName(), titleF.getText().trim(), descF.getText().trim(), companyF.getText().trim(), deadlineF.getText().trim(), "admin");
            msg.setStyle("-fx-text-fill: #00ff88;"); msg.setText("Job notice posted!");
            titleF.clear(); descF.clear(); companyF.clear(); deadlineF.clear();
            showJobNotices();
        });

        box.getChildren().addAll(title, titleF, descF, companyF, deadlineF, addBtn, msg, new Separator());

        Label listTitle = new Label("Posted Job Notices:");
        listTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(listTitle);

        java.util.List<String[]> jobs = DataStore.getAllJobNotices(getResolvedUniversityName());
        if (jobs.isEmpty()) { box.getChildren().add(new Label("No job notices.")); }
        else {
            for (String[] j : jobs) {
                VBox card = new VBox(6);
                card.setStyle("-fx-padding: 12; -fx-background-color: #111a2e; -fx-background-radius: 8;");
                Label tl = new Label(j[0]); tl.setStyle("-fx-font-weight: bold; -fx-text-fill: #00ff88;");
                Label dl = new Label(j[1]); dl.setWrapText(true);
                Label cl = new Label("Company: " + j[2] + " | Deadline: " + j[3]);
                cl.setStyle("-fx-text-fill: #ffb300;");
                Label pl = new Label("By " + j[4] + " on " + j[5]); pl.setStyle("-fx-text-fill: #5a6a7e; -fx-font-size: 11px;");
                Button del = new Button("\u274C Remove");
                del.setOnAction(ev -> { DataStore.removeJobNotice(getResolvedUniversityName(), j[0]); showJobNotices(); });
                card.getChildren().addAll(tl, dl, cl, pl, del);
                box.getChildren().add(card);
            }
        }
        setScrollContent(box);
    }

    // ===================== STAFF DETAILS =====================
    @FXML
    private void showStaffDetails() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        Label title = new Label("\uD83D\uDC77 Staff Details");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        TextField nameF = new TextField(); nameF.setPromptText("Name");
        TextField roleF = new TextField(); roleF.setPromptText("Role / Position");
        TextField deptF = new TextField(); deptF.setPromptText("Department");
        TextField phoneF = new TextField(); phoneF.setPromptText("Phone");
        TextField emailF = new TextField(); emailF.setPromptText("Email");
        Label msg = new Label();
        Button addBtn = new Button("Add Staff");
        addBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #00ff88; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        addBtn.setOnAction(e -> {
            if (nameF.getText().trim().isEmpty()) { msg.setStyle("-fx-text-fill: #ff3366;"); msg.setText("Name required."); return; }
            DataStore.addStaffMember(getResolvedUniversityName(), nameF.getText().trim(), roleF.getText().trim(), deptF.getText().trim(), phoneF.getText().trim(), emailF.getText().trim());
            msg.setStyle("-fx-text-fill: #00ff88;"); msg.setText("Staff added!");
            nameF.clear(); roleF.clear(); deptF.clear(); phoneF.clear(); emailF.clear();
            showStaffDetails();
        });

        box.getChildren().addAll(title, nameF, roleF, deptF, phoneF, emailF, addBtn, msg, new Separator());

        Label listTitle = new Label("Staff Members:");
        listTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(listTitle);

        java.util.List<String[]> staff = DataStore.getAllStaffMembers(getResolvedUniversityName());
        if (staff.isEmpty()) { box.getChildren().add(new Label("No staff members.")); }
        else {
            for (String[] s : staff) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-padding: 8 12; -fx-background-color: #111a2e; -fx-background-radius: 8;");
                Label info = new Label(s[0] + " | " + s[1] + " | " + s[2] + " | " + s[3] + " | " + s[4]);
                info.setWrapText(true); info.setMaxWidth(500);
                Button del = new Button("\u274C");
                del.setOnAction(ev -> { DataStore.removeStaffMember(getResolvedUniversityName(), s[0]); showStaffDetails(); });
                row.getChildren().addAll(info, del);
                box.getChildren().add(row);
            }
        }
        setScrollContent(box);
    }

    // ===================== DEPARTMENT DETAILS =====================
    @FXML
    private void showDepartmentDetails() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        Label title = new Label("\uD83C\uDFDB Department Details");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        TextField nameF = new TextField(); nameF.setPromptText("Department Name");
        TextField headF = new TextField(); headF.setPromptText("Head of Department");
        TextField countF = new TextField(); countF.setPromptText("Total Faculty");
        TextArea descF = new TextArea(); descF.setPromptText("Description"); descF.setPrefRowCount(2);
        Label msg = new Label();
        Button addBtn = new Button("Add Department");
        addBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #00ff88; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        addBtn.setOnAction(e -> {
            if (nameF.getText().trim().isEmpty()) { msg.setStyle("-fx-text-fill: #ff3366;"); msg.setText("Name required."); return; }
            DataStore.addDepartment(getResolvedUniversityName(), nameF.getText().trim(), headF.getText().trim(), countF.getText().trim(), descF.getText().trim());
            msg.setStyle("-fx-text-fill: #00ff88;"); msg.setText("Department added!");
            nameF.clear(); headF.clear(); countF.clear(); descF.clear();
            showDepartmentDetails();
        });

        box.getChildren().addAll(title, nameF, headF, countF, descF, addBtn, msg, new Separator());

        Label listTitle = new Label("Departments:");
        listTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(listTitle);

        java.util.List<String[]> depts = DataStore.getAllDepartments(getResolvedUniversityName());
        if (depts.isEmpty()) { box.getChildren().add(new Label("No departments added.")); }
        else {
            for (String[] d : depts) {
                VBox card = new VBox(4);
                card.setStyle("-fx-padding: 12; -fx-background-color: #111a2e; -fx-background-radius: 8;");
                Label nl = new Label(d[0]); nl.setStyle("-fx-font-weight: bold; -fx-text-fill: #00e5ff;");
                Label hl = new Label("Head: " + d[1] + " | Faculty: " + d[2]);
                Label dl = new Label(d[3]); dl.setWrapText(true); dl.setStyle("-fx-text-fill: #8a9ab0;");
                Button del = new Button("\u274C Remove");
                del.setOnAction(ev -> { DataStore.removeDepartment(getResolvedUniversityName(), d[0]); showDepartmentDetails(); });
                card.getChildren().addAll(nl, hl, dl, del);
                box.getChildren().add(card);
            }
        }
        setScrollContent(box);
    }

    // ===================== INSTITUTION DETAILS =====================
    @FXML
    private void showInstitutionDetails() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        Label title = new Label("\uD83C\uDFEB Institution Details");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        String[] keys = {"Institution Name", "Address", "Phone", "Email", "Website", "Established", "Vice Chancellor", "Motto"};
        Label msg = new Label();

        for (String key : keys) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            Label lbl = new Label(key + ":"); lbl.setMinWidth(140); lbl.setStyle("-fx-font-weight: bold;");
            TextField val = new TextField(DataStore.getInstitutionDetail(getResolvedUniversityName(), key));
            val.setPromptText(key);
            val.setPrefWidth(400);
            Button saveBtn = new Button("Save");
            saveBtn.setStyle("-fx-background-color: #0d2a4a; -fx-text-fill: white; -fx-background-radius: 6;");
            saveBtn.setOnAction(e -> {
                DataStore.setInstitutionDetail(getResolvedUniversityName(), key, val.getText().trim());
                msg.setStyle("-fx-text-fill: #00ff88;"); msg.setText(key + " saved! \u2705");
            });
            row.getChildren().addAll(lbl, val, saveBtn);
            box.getChildren().add(row);
        }

        box.getChildren().add(msg);
        setScrollContent(box);
    }

    // ===================== ADMINISTRATION =====================
    @FXML
    private void showAdministration() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        Label title = new Label("\u2699 Administration");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        TextField nameF = new TextField(); nameF.setPromptText("Name");
        TextField posF = new TextField(); posF.setPromptText("Position");
        TextField deptF = new TextField(); deptF.setPromptText("Department");
        TextField phoneF = new TextField(); phoneF.setPromptText("Phone");
        TextField emailF = new TextField(); emailF.setPromptText("Email");
        Label msg = new Label();
        Button addBtn = new Button("Add Administration Member");
        addBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #00ff88; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        addBtn.setOnAction(e -> {
            if (nameF.getText().trim().isEmpty()) { msg.setStyle("-fx-text-fill: #ff3366;"); msg.setText("Name required."); return; }
            DataStore.addAdministration(getResolvedUniversityName(), nameF.getText().trim(), posF.getText().trim(), deptF.getText().trim(), phoneF.getText().trim(), emailF.getText().trim());
            msg.setStyle("-fx-text-fill: #00ff88;"); msg.setText("Administration member added!");
            nameF.clear(); posF.clear(); deptF.clear(); phoneF.clear(); emailF.clear();
            showAdministration();
        });

        box.getChildren().addAll(title, nameF, posF, deptF, phoneF, emailF, addBtn, msg, new Separator());

        Label listTitle = new Label("Administration Members:");
        listTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(listTitle);

        java.util.List<String[]> admins = DataStore.getAllAdministration(getResolvedUniversityName());
        if (admins.isEmpty()) { box.getChildren().add(new Label("No administration members.")); }
        else {
            for (String[] a : admins) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-padding: 8 12; -fx-background-color: #111a2e; -fx-background-radius: 8;");
                Label info = new Label(a[0] + " | " + a[1] + " | " + a[2] + " | " + a[3] + " | " + a[4]);
                info.setWrapText(true); info.setMaxWidth(500);
                Button del = new Button("\u274C");
                del.setOnAction(ev -> { DataStore.removeAdministration(getResolvedUniversityName(), a[0]); showAdministration(); });
                row.getChildren().addAll(info, del);
                box.getChildren().add(row);
            }
        }
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