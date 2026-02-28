package com.example.moodle.controller;

import java.io.File;
import java.util.List;

import com.example.moodle.model.Assignment;
import com.example.moodle.model.Course;
import com.example.moodle.model.Message;
import com.example.moodle.model.User;
import com.example.moodle.service.DataStore;
import com.example.moodle.util.SceneManager;
import com.example.moodle.util.Session;
import com.example.moodle.util.UserStore;

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

public class TeacherDashboardController {

    @FXML
    private StackPane contentArea;

    private String teacherEmail() {
        return Session.getIdentifier();
    }

    @FXML
    public void initialize() {
        showMyCourses();
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

    // ===================== MY COURSES =====================

    @FXML
    private void showMyProfile() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("\uD83D\uDC64 My Profile");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        // Photo section
        javafx.scene.layout.StackPane photoArea = new javafx.scene.layout.StackPane();
        photoArea.setStyle("-fx-min-width: 120; -fx-min-height: 120; -fx-background-color: #e8e8e8; -fx-background-radius: 60;");

        String photoPath = DataStore.getProfilePhoto(teacherEmail());
        if (photoPath != null && !photoPath.isEmpty()) {
            try {
                javafx.scene.image.Image img = new javafx.scene.image.Image(photoPath, 120, 120, true, true);
                javafx.scene.image.ImageView iv = new javafx.scene.image.ImageView(img);
                iv.setFitWidth(120);
                iv.setFitHeight(120);
                javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(60, 60, 60);
                iv.setClip(clip);
                photoArea.getChildren().add(iv);
            } catch (Exception ex) {
                Label ph = new Label("\uD83D\uDC64");
                ph.setStyle("-fx-font-size: 50px;");
                photoArea.getChildren().add(ph);
            }
        } else {
            Label ph = new Label("\uD83D\uDC64");
            ph.setStyle("-fx-font-size: 50px;");
            photoArea.getChildren().add(ph);
        }

        Button uploadPhotoBtn = new Button("\uD83D\uDCF7 Upload Photo");
        uploadPhotoBtn.setStyle("-fx-background-color: #2a5298; -fx-text-fill: white; -fx-background-radius: 8;");
        uploadPhotoBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select Profile Photo");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            java.io.File file = fc.showOpenDialog(contentArea.getScene().getWindow());
            if (file != null) {
                DataStore.setProfilePhoto(teacherEmail(), file.toURI().toString());
                showMyProfile();
            }
        });

        // Info
        Label nameLabel = new Label("Name: " + Session.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");
        Label deptLabel = new Label("Department: " + (Session.getDepartment() != null ? Session.getDepartment() : "N/A"));
        deptLabel.setStyle("-fx-font-size: 14px;");
        Label desigLabel = new Label("Designation: " + (Session.getDesignation() != null ? Session.getDesignation() : "N/A"));
        desigLabel.setStyle("-fx-font-size: 14px;");
        Label typeLabel = new Label("Type: " + (Session.getTeacherType() != null ? Session.getTeacherType() : "N/A"));
        typeLabel.setStyle("-fx-font-size: 14px;");
        Label emailLabel = new Label("Identifier: " + teacherEmail());
        emailLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");

        // Change campus password
        Label passTitle = new Label("Change Campus Password");
        passTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 10 0 0 0;");

        javafx.scene.control.PasswordField currentPassField = new javafx.scene.control.PasswordField();
        currentPassField.setPromptText("Current Campus Password");
        javafx.scene.control.PasswordField newPassField = new javafx.scene.control.PasswordField();
        newPassField.setPromptText("New Campus Password");
        Label passMsg = new Label();

        Button changePassBtn = new Button("Change Password");
        changePassBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        changePassBtn.setOnAction(e -> {
            String current = currentPassField.getText().trim();
            String newPass = newPassField.getText().trim();
            if (current.isEmpty() || newPass.isEmpty()) {
                passMsg.setStyle("-fx-text-fill: red;");
                passMsg.setText("Fill both fields.");
                return;
            }
            String[] profile = DataStore.getTeacherProfile(Session.getName(),
                    Session.getDepartment() != null ? Session.getDepartment() : "");
            String expected = (profile != null) ? profile[4] : "teacher2026";
            if (!expected.equals(current)) {
                passMsg.setStyle("-fx-text-fill: red;");
                passMsg.setText("Current password is incorrect.");
                return;
            }
            if (profile != null) {
                DataStore.saveTeacherProfile(profile[0], profile[1], profile[2], profile[3], newPass);
            } else {
                DataStore.saveTeacherProfile(Session.getName(),
                        Session.getDepartment() != null ? Session.getDepartment() : "",
                        Session.getDesignation() != null ? Session.getDesignation() : "",
                        Session.getTeacherType() != null ? Session.getTeacherType() : "Faculty Teacher",
                        newPass);
            }
            passMsg.setStyle("-fx-text-fill: green;");
            passMsg.setText("Password changed! \u2705");
            currentPassField.clear();
            newPassField.clear();
        });

        box.getChildren().addAll(title, photoArea, uploadPhotoBtn,
                new Separator(), nameLabel, deptLabel, desigLabel, typeLabel, emailLabel,
                new Separator(), passTitle, currentPassField, newPassField, changePassBtn, passMsg);
        setScrollContent(box);
    }

    @FXML
    private void showMyCourses() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("My Courses");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        // Add course form
        Label formTitle = new Label("Add New Course");
        formTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

        TextField codeField = new TextField();
        codeField.setPromptText("Course Code (e.g. CSE301)");
        TextField nameField = new TextField();
        nameField.setPromptText("Course Name");
        TextField facultyNameField = new TextField();
        facultyNameField.setPromptText("Faculty Name (e.g. Dr. Rahman)");
        TextField semField = new TextField();
        semField.setPromptText("Semester (e.g. Spring 2025)");
        TextField batchField = new TextField();
        batchField.setPromptText("Batch (e.g. 24 for 2024 batch)");

        Label msgLabel = new Label();
        Button addBtn = new Button("Add Course");
        addBtn.setOnAction(e -> {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            String sem = semField.getText().trim();
            String facName = facultyNameField.getText().trim();
            String batch = batchField.getText().trim();
            if (code.isEmpty() || name.isEmpty() || sem.isEmpty()) {
                msgLabel.setStyle("-fx-text-fill: red;");
                msgLabel.setText("Fill code, name and semester.");
            } else {
                DataStore.addCourse(new Course(code, name, teacherEmail(), sem, facName, batch));
                msgLabel.setStyle("-fx-text-fill: green;");
                msgLabel.setText("Course added!");
                codeField.clear();
                nameField.clear();
                semField.clear();
                facultyNameField.clear();
                batchField.clear();
                showMyCourses();
            }
        });

        box.getChildren().addAll(title, new Separator(), formTitle,
                codeField, nameField, facultyNameField, semField, batchField, addBtn, msgLabel, new Separator());

        // List existing courses
        Label listTitle = new Label("Existing Courses:");
        listTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(listTitle);

        List<Course> courses = DataStore.getAllCourses();
        if (courses.isEmpty()) {
            box.getChildren().add(new Label("No courses yet."));
        } else {
            for (Course c : courses) {
                HBox row = new HBox(15);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-padding: 10; -fx-background-color: #f0f4ff; -fx-background-radius: 8;");
                Label info = new Label(c.getCode() + " - " + c.getName()
                        + " (" + c.getSemester() + ")"
                        + (c.getBatch().isEmpty() ? "" : " [Batch " + c.getBatch() + "]"));
                info.setStyle("-fx-font-weight: bold;");
                String teacherDisplay = c.getTeacherName().isEmpty() ? c.getTeacherEmail()
                        : c.getTeacherName() + " (" + c.getTeacherEmail() + ")";
                Label teacher = new Label("by " + teacherDisplay);
                teacher.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");
                row.getChildren().addAll(info, teacher);
                box.getChildren().add(row);
            }
        }
        setScrollContent(box);
    }

    // ===================== UPLOAD ASSIGNMENT =====================

    @FXML
    private void showAssignments() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Upload Assignment");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        ComboBox<String> courseBox = new ComboBox<>();
        courseBox.setPromptText("Select Course");
        courseBox.setMaxWidth(Double.MAX_VALUE);
        for (Course c : DataStore.getAllCourses()) {
            courseBox.getItems().add(c.getCode() + " - " + c.getName());
        }

        TextField titleField = new TextField();
        titleField.setPromptText("Assignment Title");
        TextArea descField = new TextArea();
        descField.setPromptText("Assignment Description / Instructions");
        descField.setPrefRowCount(3);

        // PDF upload
        Label pdfLabel = new Label("No PDF selected");
        pdfLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
        final String[] pdfPath = {""};
        Button pdfBtn = new Button("\uD83D\uDCC2 Attach PDF");
        pdfBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 6;");
        pdfBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select PDF File");
            fc.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fc.showOpenDialog(contentArea.getScene().getWindow());
            if (file != null) {
                pdfPath[0] = file.getAbsolutePath();
                pdfLabel.setText("\u2705 " + file.getName());
                pdfLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
            }
        });
        HBox pdfRow = new HBox(10, pdfBtn, pdfLabel);
        pdfRow.setAlignment(Pos.CENTER_LEFT);

        Label msgLabel = new Label();
        Button uploadBtn = new Button("Upload Assignment");
        uploadBtn.setOnAction(e -> {
            String sel = courseBox.getValue();
            String t = titleField.getText().trim();
            String d = descField.getText().trim();
            if (sel == null || t.isEmpty()) {
                msgLabel.setStyle("-fx-text-fill: red;");
                msgLabel.setText("Select course and enter title.");
            } else {
                String code = sel.split(" - ")[0];
                String desc = d + (pdfPath[0].isEmpty() ? "" : " [PDF:" + pdfPath[0] + "]");
                DataStore.addAssignment(new Assignment(code, t, desc, teacherEmail()));
                msgLabel.setStyle("-fx-text-fill: green;");
                msgLabel.setText("Assignment uploaded!" + (pdfPath[0].isEmpty() ? "" : " (with PDF)"));
                titleField.clear();
                descField.clear();
                pdfPath[0] = "";
                pdfLabel.setText("No PDF selected");
                pdfLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
            }
        });

        box.getChildren().addAll(title, courseBox, titleField, descField,
                pdfRow, uploadBtn, msgLabel, new Separator());

        // Show existing assignments
        Label existTitle = new Label("Existing Assignments:");
        existTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(existTitle);

        for (Assignment a : DataStore.getAllAssignments()) {
            Label item = new Label("\uD83D\uDCDD [" + a.getCourseCode() + "] "
                    + a.getTitle() + " — " + a.getDescription());
            item.setStyle("-fx-padding: 6; -fx-background-color: #fff8e1; -fx-background-radius: 6;");
            item.setWrapText(true);
            box.getChildren().add(item);
        }
        setScrollContent(box);
    }

    // ===================== EVALUATE SUBMISSIONS =====================

    @FXML
    private void showEvaluate() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Evaluate Submissions");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        ComboBox<String> courseBox = new ComboBox<>();
        courseBox.setPromptText("Select Course");
        courseBox.setMaxWidth(Double.MAX_VALUE);
        for (Course c : DataStore.getAllCourses()) {
            courseBox.getItems().add(c.getCode() + " - " + c.getName());
        }

        VBox submissionsBox = new VBox(10);
        Label msgLabel = new Label();

        Runnable loadSubmissions = () -> {
            submissionsBox.getChildren().clear();
            String sel = courseBox.getValue();
            if (sel == null) return;
            String code = sel.split(" - ")[0];
            List<Assignment> assignments = DataStore.getAssignmentsForCourse(code);
            if (assignments.isEmpty()) {
                submissionsBox.getChildren().add(new Label("No assignments for this course."));
            } else {
                for (Assignment a : assignments) {
                    Label aLabel = new Label("Assignment: " + a.getTitle());
                    aLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 0 4 0;");
                    submissionsBox.getChildren().add(aLabel);

                    List<String[]> subs = DataStore.getSubmissionsForAssignment(code, a.getTitle());
                    if (subs.isEmpty()) {
                        Label noSub = new Label("   No submissions yet.");
                        noSub.setStyle("-fx-text-fill: #888;");
                        submissionsBox.getChildren().add(noSub);
                    } else {
                        for (String[] sub : subs) {
                            HBox row = new HBox(10);
                            row.setAlignment(Pos.CENTER_LEFT);
                            row.setStyle("-fx-padding: 8; -fx-background-color: #f8f9ff; -fx-background-radius: 6;");

                            VBox info = new VBox(3);
                            Label sId = new Label("Student: " + sub[0]);
                            sId.setStyle("-fx-font-weight: bold;");

                            String rawContent = sub[3];
                            String displayContent = rawContent;
                            String subPdf = "";
                            if (rawContent.contains("[PDF:")) {
                                int idx = rawContent.indexOf("[PDF:");
                                int end = rawContent.indexOf("]", idx);
                                if (end > idx) {
                                    subPdf = rawContent.substring(idx + 5, end);
                                    displayContent = rawContent.substring(0, idx).trim();
                                }
                            }
                            Label sContent = new Label("Answer: " + displayContent);
                            sContent.setWrapText(true);
                            Label sMarks = new Label("Current Marks: " + sub[4]);
                            sMarks.setStyle("-fx-text-fill: #2a5298;");
                            info.getChildren().addAll(sId, sContent, sMarks);

                            if (!subPdf.isEmpty()) {
                                final String fp = subPdf;
                                Button openPdf = new Button("\uD83D\uDCC4 Open PDF");
                                openPdf.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-size: 11px;");
                                openPdf.setOnAction(ev2 -> {
                                    try { java.awt.Desktop.getDesktop().open(new java.io.File(fp)); }
                                    catch (Exception ex) { /* ignore */ }
                                });
                                info.getChildren().add(openPdf);
                            }

                            TextField marksField = new TextField();
                            marksField.setPromptText("Marks");
                            marksField.setPrefWidth(80);

                            Button gradeBtn = new Button("Grade");
                            final String studentId = sub[0];
                            final String aTitle = a.getTitle();
                            final String courseCode = code;
                            gradeBtn.setOnAction(ev -> {
                                String marks = marksField.getText().trim();
                                if (!marks.isEmpty()) {
                                    DataStore.gradeSubmission(studentId, courseCode, aTitle, marks);
                                    msgLabel.setStyle("-fx-text-fill: green;");
                                    msgLabel.setText("Graded " + studentId + " with " + marks);
                                }
                            });

                            VBox actions = new VBox(5);
                            actions.setAlignment(Pos.CENTER);
                            actions.getChildren().addAll(marksField, gradeBtn);

                            row.getChildren().addAll(info, actions);
                            submissionsBox.getChildren().add(row);
                        }
                    }
                }
            }
        };

        Button loadBtn = new Button("Load Submissions");
        loadBtn.setOnAction(e -> loadSubmissions.run());

        courseBox.setOnAction(e -> loadSubmissions.run());

        Label liveHint = new Label("\uD83D\uDFE2 Submissions auto-refresh every 3 seconds");
        liveHint.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px;");

        box.getChildren().addAll(title, courseBox, loadBtn, msgLabel,
                liveHint, new Separator(), submissionsBox);
        setScrollContent(box);

        // Auto-refresh submissions
        panelRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> loadSubmissions.run()));
        panelRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        panelRefreshTimeline.play();
    }

    // ===================== UPLOAD SLIDES =====================

    @FXML
    private void showSlides() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Upload Slides");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        ComboBox<String> courseBox = new ComboBox<>();
        courseBox.setPromptText("Select Course");
        courseBox.setMaxWidth(Double.MAX_VALUE);
        for (Course c : DataStore.getAllCourses()) {
            courseBox.getItems().add(c.getCode() + " - " + c.getName());
        }

        TextField titleField = new TextField();
        titleField.setPromptText("Slide Title");
        TextArea descField = new TextArea();
        descField.setPromptText("Slide Content / Description");
        descField.setPrefRowCount(4);

        // PDF upload
        Label pdfLabel = new Label("No PDF selected");
        pdfLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
        final String[] pdfPath = {""};
        Button pdfBtn = new Button("\uD83D\uDCC2 Attach PDF");
        pdfBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 6;");
        pdfBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select PDF File");
            fc.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fc.showOpenDialog(contentArea.getScene().getWindow());
            if (file != null) {
                pdfPath[0] = file.getAbsolutePath();
                pdfLabel.setText("\u2705 " + file.getName());
                pdfLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
            }
        });
        HBox pdfRow = new HBox(10, pdfBtn, pdfLabel);
        pdfRow.setAlignment(Pos.CENTER_LEFT);

        Label msgLabel = new Label();
        Button uploadBtn = new Button("Upload Slide");
        uploadBtn.setOnAction(e -> {
            String sel = courseBox.getValue();
            String t = titleField.getText().trim();
            String d = descField.getText().trim();
            if (sel == null || t.isEmpty()) {
                msgLabel.setStyle("-fx-text-fill: red;");
                msgLabel.setText("Select course and enter title.");
            } else {
                String code = sel.split(" - ")[0];
                String desc = d + (pdfPath[0].isEmpty() ? "" : " [PDF:" + pdfPath[0] + "]");
                DataStore.addSlide(code, t, desc, teacherEmail());
                msgLabel.setStyle("-fx-text-fill: green;");
                msgLabel.setText("Slide uploaded!" + (pdfPath[0].isEmpty() ? "" : " (with PDF)"));
                titleField.clear();
                descField.clear();
                pdfPath[0] = "";
                pdfLabel.setText("No PDF selected");
                pdfLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
                showSlides();
            }
        });

        box.getChildren().addAll(title, courseBox, titleField, descField,
                pdfRow, uploadBtn, msgLabel, new Separator());

        // Show existing slides
        Label listTitle = new Label("Uploaded Slides:");
        listTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(listTitle);

        boolean found = false;
        for (Course c : DataStore.getAllCourses()) {
            List<String[]> slides = DataStore.getSlidesForCourse(c.getCode());
            for (String[] s : slides) {
                found = true;
                Label item = new Label("\uD83D\uDCCA [" + s[0] + "] " + s[1]
                        + (s[2].isEmpty() ? "" : " — " + s[2]));
                item.setStyle("-fx-padding: 6; -fx-background-color: #e8f5e9; -fx-background-radius: 6;");
                item.setWrapText(true);
                box.getChildren().add(item);
            }
        }
        if (!found) box.getChildren().add(new Label("No slides uploaded yet."));
        setScrollContent(box);
    }

    // ===================== POST NOTICE =====================

    @FXML
    private void showNotices() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Post Course Notice");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        ComboBox<String> courseBox = new ComboBox<>();
        courseBox.setPromptText("Select Course");
        courseBox.setMaxWidth(Double.MAX_VALUE);
        for (Course c : DataStore.getAllCourses()) {
            courseBox.getItems().add(c.getCode() + " - " + c.getName());
        }

        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Notice Content");
        contentArea.setPrefRowCount(3);

        Label msgLabel = new Label();
        Button postBtn = new Button("Post Notice");
        postBtn.setOnAction(e -> {
            String sel = courseBox.getValue();
            String content = contentArea.getText().trim();
            if (sel == null || content.isEmpty()) {
                msgLabel.setStyle("-fx-text-fill: red;");
                msgLabel.setText("Select course and enter notice.");
            } else {
                String code = sel.split(" - ")[0];
                DataStore.addCourseNotice(code, content, teacherEmail());
                msgLabel.setStyle("-fx-text-fill: green;");
                msgLabel.setText("Notice posted!");
                contentArea.clear();
                showNotices();
            }
        });

        box.getChildren().addAll(title, courseBox, contentArea, postBtn,
                msgLabel, new Separator());

        // Show existing notices
        Label listTitle = new Label("Posted Notices:");
        listTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(listTitle);

        List<String[]> notices = DataStore.getAllNotices();
        if (notices.isEmpty()) {
            box.getChildren().add(new Label("No notices posted yet."));
        } else {
            for (String[] n : notices) {
                Label item = new Label("\uD83D\uDCCC [" + n[0] + "] " + n[1] + " (" + n[3] + ")");
                item.setStyle("-fx-padding: 8; -fx-background-color: #fff8e1; -fx-background-radius: 6;");
                item.setWrapText(true);
                box.getChildren().add(item);
            }
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

        String myId = teacherEmail();

        // New chat shortcut
        HBox composeRow = new HBox(10);
        composeRow.setAlignment(Pos.CENTER_LEFT);
        TextField newChatField = new TextField();
        newChatField.setPromptText("Start new chat (enter student email or ID)...");
        HBox.setHgrow(newChatField, Priority.ALWAYS);
        Button newChatBtn = new Button("\uD83D\uDCAC Chat");
        newChatBtn.setStyle("-fx-background-color: #2a5298; -fx-text-fill: white; -fx-background-radius: 8;");
        newChatBtn.setOnAction(e -> {
            String to = newChatField.getText().trim();
            if (!to.isEmpty()) showTeacherDirectChat(to);
        });
        composeRow.getChildren().addAll(newChatField, newChatBtn);

        VBox convList = new VBox(8);

        Runnable refreshConversations = () -> {
            convList.getChildren().clear();
            List<Message> messages = DataStore.getMessagesFor(myId);
            java.util.LinkedHashMap<String, Message> lastMessages = new java.util.LinkedHashMap<>();
            for (Message m : messages) {
                String partner = m.getFrom().equals(myId) ? m.getTo() : m.getFrom();
                lastMessages.put(partner, m);
            }
            if (lastMessages.isEmpty()) {
                convList.getChildren().add(new Label("No conversations yet."));
            } else {
                java.util.List<java.util.Map.Entry<String, Message>> entries = new java.util.ArrayList<>(lastMessages.entrySet());
                for (int i = entries.size() - 1; i >= 0; i--) {
                    java.util.Map.Entry<String, Message> entry = entries.get(i);
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
                    if (preview.length() > 50) preview = preview.substring(0, 50) + "...";
                    Label previewLabel = new Label(preview);
                    previewLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
                    info.getChildren().addAll(nameLabel, previewLabel);

                    Label timeLabel = new Label(lastMsg.getTimestamp());
                    timeLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");

                    card.getChildren().addAll(avatar, info, timeLabel);
                    card.setOnMouseClicked(ev -> showTeacherDirectChat(partner));
                    convList.getChildren().add(card);
                }
            }
        };

        refreshConversations.run();

        Label liveHint = new Label("\uD83D\uDFE2 Conversations auto-refresh every 3 seconds");
        liveHint.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px;");

        box.getChildren().addAll(title, composeRow, new Separator(), liveHint, convList);
        setScrollContent(box);

        panelRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> refreshConversations.run()));
        panelRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        panelRefreshTimeline.play();
    }

    private void showTeacherDirectChat(String recipientId) {
        if (chatRefreshTimeline != null) chatRefreshTimeline.stop();

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        String myId = teacherEmail();

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
            List<Message> filtered = new java.util.ArrayList<>();
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
                if (isMine) bubble.getChildren().addAll(spacer, msgBox);
                else bubble.getChildren().addAll(msgBox, spacer);
                chatMessages.getChildren().add(bubble);
            }
            if (chatMessages.getChildren().isEmpty()) {
                chatMessages.getChildren().add(new Label("No messages yet. Start the conversation!"));
            }
            chatScroll.applyCss();
            chatScroll.layout();
            chatScroll.setVvalue(1.0);
        };

        sendBtn.setOnAction(e -> {
            String content = chatInput.getText().trim();
            if (content.isEmpty()) { statusLabel.setStyle("-fx-text-fill: red;"); statusLabel.setText("Type a message."); return; }
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

        chatRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> refreshChat.run()));
        chatRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        chatRefreshTimeline.play();
    }

    // ===================== VIEW STUDENTS =====================

    @FXML
    private void showStudentList() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Student List");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setStyle("-fx-background-color: #ddd;");

        String[] headers = {"Name", "University", "Student ID", "Email"};
        for (int c = 0; c < headers.length; c++) {
            Label cell = new Label(headers[c]);
            cell.setStyle("-fx-font-weight: bold; -fx-padding: 10 16 10 16; "
                    + "-fx-background-color: #1e3c72; -fx-text-fill: white; -fx-min-width: 150;");
            cell.setMaxWidth(Double.MAX_VALUE);
            grid.add(cell, c, 0);
        }

        List<User> users = UserStore.getAllUsers();
        int row = 1;
        for (User u : users) {
            String[] vals = {u.getName(), u.getUniversity(), u.getStudentId(), u.getEmail()};
            for (int c = 0; c < vals.length; c++) {
                Label cell = new Label(vals[c] != null ? vals[c] : "");
                cell.setStyle("-fx-padding: 8 16 8 16; -fx-background-color: white; -fx-min-width: 150;");
                cell.setMaxWidth(Double.MAX_VALUE);
                grid.add(cell, c, row);
            }
            row++;
        }

        Label countLabel = new Label("Total: " + users.size() + " students");
        countLabel.setStyle("-fx-text-fill: #555; -fx-font-size: 13px;");

        box.getChildren().addAll(title, countLabel, grid);
        if (users.isEmpty()) box.getChildren().add(new Label("No students registered yet."));
        setScrollContent(box);
    }

    // ===================== LIVE CHAT =====================

    private Timeline chatRefreshTimeline;
    private Timeline panelRefreshTimeline;

    @FXML
    private void openNewWindow() {
        SceneManager.openNewWindow();
    }

    @FXML
    private void showLiveChat() {
        if (chatRefreshTimeline != null) chatRefreshTimeline.stop();

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        Label title = new Label("\uD83D\uDCAC Live Chat");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        String myId = teacherEmail();

        TextField recipientField = new TextField();
        recipientField.setPromptText("Chat with (student email or ID)...");
        recipientField.setStyle("-fx-padding: 8; -fx-background-radius: 20; -fx-border-radius: 20;");

        VBox chatMessages = new VBox(8);
        chatMessages.setStyle("-fx-padding: 10;");

        ScrollPane chatScroll = new ScrollPane(chatMessages);
        chatScroll.setFitToWidth(true);
        chatScroll.setPrefHeight(350);
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

        Label onlineHint = new Label("\uD83D\uDFE2 Auto-refreshes every 3 seconds");
        onlineHint.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px;");

        box.getChildren().addAll(title, recipientRow, onlineHint, chatScroll,
                inputRow, statusLabel);
        setScrollContent(box);

        // Auto-refresh — AFTER setScrollContent to avoid immediate stop
        chatRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> refreshChat.run()));
        chatRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        chatRefreshTimeline.play();
    }

    // ===================== EDIT SCHEDULE =====================

    private static final String[] SCHED_DAYS = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};
    private static final String[] SCHED_TIMES = {"08:00-09:00", "09:00-10:00", "10:00-11:00", "11:00-12:00",
            "12:00-01:00", "01:00-02:00", "02:00-03:00", "03:00-04:00"};

    @FXML
    private void showEditSchedule() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("\uD83D\uDCC5 Edit Class Schedule");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        // Batch selector
        HBox batchRow = new HBox(10);
        batchRow.setAlignment(Pos.CENTER_LEFT);
        TextField batchField = new TextField();
        batchField.setPromptText("Batch (e.g. 24)");
        batchField.setPrefWidth(120);
        Button loadGridBtn = new Button("Load Week Grid");
        loadGridBtn.setStyle("-fx-background-color: #2a5298; -fx-text-fill: white; -fx-background-radius: 8;");
        Label batchMsg = new Label();
        batchRow.getChildren().addAll(new Label("Batch:"), batchField, loadGridBtn, batchMsg);

        VBox gridContainer = new VBox(10);

        // Use a final array wrapper so the Runnable can reference itself
        final Runnable[] buildRef = {null};
        buildRef[0] = () -> {
            gridContainer.getChildren().clear();
            String batch = batchField.getText().trim();
            if (batch.isEmpty()) {
                batchMsg.setStyle("-fx-text-fill: red;");
                batchMsg.setText("Enter a batch number.");
                return;
            }
            batchMsg.setText("");
            java.util.Map<String, String[]> lookup = new java.util.HashMap<>();
            for (String[] e : DataStore.getScheduleForBatch(batch)) {
                lookup.put(e[1] + "|" + e[2], e);
            }
            GridPane grid = new GridPane();
            grid.setHgap(2);
            grid.setVgap(2);
            Label corner = new Label("Time \\ Day");
            corner.setStyle("-fx-font-weight: bold; -fx-padding: 8 10 8 10; -fx-background-color: #1e3c72; -fx-text-fill: white; -fx-min-width: 100;");
            corner.setMaxWidth(Double.MAX_VALUE);
            grid.add(corner, 0, 0);
            for (int d = 0; d < SCHED_DAYS.length; d++) {
                Label dayLabel = new Label(SCHED_DAYS[d]);
                dayLabel.setStyle("-fx-font-weight: bold; -fx-padding: 8 10 8 10; -fx-background-color: #1e3c72; -fx-text-fill: white; -fx-min-width: 110; -fx-alignment: center;");
                dayLabel.setMaxWidth(Double.MAX_VALUE);
                grid.add(dayLabel, d + 1, 0);
            }
            for (int t = 0; t < SCHED_TIMES.length; t++) {
                Label timeLabel = new Label(SCHED_TIMES[t]);
                timeLabel.setStyle("-fx-font-weight: bold; -fx-padding: 8 10 8 10; -fx-background-color: #34495e; -fx-text-fill: white; -fx-min-width: 100;");
                timeLabel.setMaxWidth(Double.MAX_VALUE);
                grid.add(timeLabel, 0, t + 1);
                for (int d = 0; d < SCHED_DAYS.length; d++) {
                    String key = SCHED_DAYS[d] + "|" + SCHED_TIMES[t];
                    String[] entry = lookup.get(key);
                    final String day = SCHED_DAYS[d];
                    final String time = SCHED_TIMES[t];
                    if (entry != null) {
                        VBox cell = new VBox(2);
                        cell.setAlignment(Pos.CENTER);
                        cell.setStyle("-fx-padding: 6; -fx-background-color: #d4edda; -fx-min-width: 110;");
                        Label code = new Label(entry[3]);
                        code.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #155724;");
                        Label cName = new Label(entry[4]);
                        cName.setStyle("-fx-font-size: 10px; -fx-text-fill: #155724;");
                        cName.setWrapText(true);
                        Button delBtn = new Button("\u274C");
                        delBtn.setStyle("-fx-font-size: 9px; -fx-padding: 2 6 2 6;");
                        delBtn.setOnAction(ev -> {
                            DataStore.removeScheduleEntry(batch, day, time);
                            buildRef[0].run();
                        });
                        cell.getChildren().addAll(code, cName, delBtn);
                        grid.add(cell, d + 1, t + 1);
                    } else {
                        Button voidCell = new Button("\u2014");
                        voidCell.setMaxWidth(Double.MAX_VALUE);
                        voidCell.setMaxHeight(Double.MAX_VALUE);
                        voidCell.setStyle("-fx-background-color: #f8f9ff; -fx-min-width: 110; -fx-min-height: 50; -fx-cursor: hand; -fx-text-fill: #ccc;");
                        voidCell.setOnAction(ev -> showAddClassDialog(batch, day, time, buildRef[0]));
                        grid.add(voidCell, d + 1, t + 1);
                    }
                }
            }
            gridContainer.getChildren().add(grid);
            Label hint = new Label("Click empty cells to add classes. Click \u274C to remove.");
            hint.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");
            gridContainer.getChildren().add(hint);
        };

        loadGridBtn.setOnAction(e -> buildRef[0].run());

        box.getChildren().addAll(title, batchRow, new Separator(), gridContainer);
        setScrollContent(box);
    }

    private void showAddClassDialog(String batch, String day, String time, Runnable onDone) {
        VBox dialog = new VBox(10);
        dialog.setStyle("-fx-padding: 20; -fx-background-color: white; -fx-background-radius: 12; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 4);");
        dialog.setMaxWidth(350);

        Label dTitle = new Label("Add Class: " + day + " " + time);
        dTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1e3c72;");

        TextField codeField = new TextField();
        codeField.setPromptText("Course Code (e.g. CSE101)");
        TextField nameField = new TextField();
        nameField.setPromptText("Course Name");
        Label dMsg = new Label();

        Button saveBtn = new Button("Add Class");
        saveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        Button cancelBtn = new Button("Cancel");

        saveBtn.setOnAction(e -> {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            if (code.isEmpty()) {
                dMsg.setStyle("-fx-text-fill: red;");
                dMsg.setText("Enter course code.");
                return;
            }
            DataStore.addScheduleEntry(batch, day, time, code, name.isEmpty() ? code : name);
            contentArea.getChildren().removeIf(n -> "schedule-dialog-overlay".equals(n.getId()));
            onDone.run();
        });

        cancelBtn.setOnAction(e -> {
            contentArea.getChildren().removeIf(n -> "schedule-dialog-overlay".equals(n.getId()));
        });

        HBox btnRow = new HBox(10, saveBtn, cancelBtn);
        dialog.getChildren().addAll(dTitle, codeField, nameField, btnRow, dMsg);

        javafx.scene.layout.StackPane overlay = new javafx.scene.layout.StackPane(dialog);
        overlay.setId("schedule-dialog-overlay");
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
        contentArea.getChildren().add(overlay);
    }

    // ===================== HOSPITAL =====================

    @FXML
    private void showHospital() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("\uD83C\uDFE5 Medical Center");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        String myId = teacherEmail();

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

        // Book Appointment
        Label bookTitle = new Label("Book Appointment");
        bookTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 10 0 0 0;");

        ComboBox<String> doctorBox = new ComboBox<>();
        doctorBox.setPromptText("Select Doctor");
        doctorBox.setMaxWidth(Double.MAX_VALUE);
        for (String[] d : doctors) doctorBox.getItems().add(d[0] + " - " + (d.length > 1 ? d[1] : ""));

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

        // My Appointments
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

        Label emergLabel = new Label("\uD83D\uDEA8 Emergency: Call 999 or visit Medical Center Ground Floor");
        emergLabel.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10 0 0 0;");

        box.getChildren().addAll(title, docTitle, docGrid, new Separator(),
                bookTitle, doctorBox, dateField, timeField, reasonField, bookBtn, bookMsg,
                new Separator(), apptTitle, apptList, emergLabel);
        setScrollContent(box);
    }

    // ===================== UTILITY =====================

    private void setScrollContent(VBox content) {
        if (panelRefreshTimeline != null) panelRefreshTimeline.stop();
        if (chatRefreshTimeline != null) chatRefreshTimeline.stop();
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        contentArea.getChildren().setAll(scroll);
    }
}
