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

    private static final String ATTACHMENT_MARKER = "[FILE:";
    private static final String LEGACY_PDF_MARKER = "[PDF:";

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

        // File upload
        Label fileLabel = new Label("No file selected");
        fileLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
        final String[] attachmentPath = {""};
        Button fileBtn = new Button("\uD83D\uDCC2 Attach File");
        fileBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 6;");
        fileBtn.setOnAction(e -> {
            File file = chooseAttachmentFile("Select File");
            if (file != null) {
                attachmentPath[0] = file.getAbsolutePath();
                fileLabel.setText("\u2705 " + file.getName());
                fileLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
            }
        });
        HBox fileRow = new HBox(10, fileBtn, fileLabel);
        fileRow.setAlignment(Pos.CENTER_LEFT);

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
                String desc = d + (attachmentPath[0].isEmpty() ? "" : " " + ATTACHMENT_MARKER + attachmentPath[0] + "]");
                DataStore.addAssignment(new Assignment(code, t, desc, teacherEmail()));
                msgLabel.setStyle("-fx-text-fill: green;");
                msgLabel.setText("Assignment uploaded!" + (attachmentPath[0].isEmpty() ? "" : " (with attachment)"));
                titleField.clear();
                descField.clear();
                attachmentPath[0] = "";
                fileLabel.setText("No file selected");
                fileLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
            }
        });

        box.getChildren().addAll(title, courseBox, titleField, descField,
                fileRow, uploadBtn, msgLabel, new Separator());

        // Show existing assignments
        Label existTitle = new Label("Existing Assignments:");
        existTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(existTitle);

        for (Assignment a : DataStore.getAllAssignments()) {
            String[] parsed = splitContentAndAttachment(a.getDescription());
            Label item = new Label("\uD83D\uDCDD [" + a.getCourseCode() + "] "
                    + a.getTitle() + " — " + parsed[0]);
            item.setStyle("-fx-padding: 6; -fx-background-color: #fff8e1; -fx-background-radius: 6;");
            item.setWrapText(true);

            VBox itemBox = new VBox(6, item);
            if (!parsed[1].isEmpty()) {
                itemBox.getChildren().add(createAttachmentActions(parsed[1]));
            }
            box.getChildren().add(itemBox);
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

                            String[] parsedSubmission = splitContentAndAttachment(sub[3]);
                            String displayContent = parsedSubmission[0];
                            String submissionAttachment = parsedSubmission[1];
                            Label sContent = new Label("Answer: " + displayContent);
                            sContent.setWrapText(true);
                            Label sMarks = new Label("Current Marks: " + sub[4]);
                            sMarks.setStyle("-fx-text-fill: #2a5298;");
                            info.getChildren().addAll(sId, sContent, sMarks);

                            if (!submissionAttachment.isEmpty()) {
                                final String fp = submissionAttachment;
                                info.getChildren().add(createAttachmentActions(fp));
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

        // File upload
        Label fileLabel = new Label("No file selected");
        fileLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
        final String[] attachmentPath = {""};
        Button fileBtn = new Button("\uD83D\uDCC2 Attach File");
        fileBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 6;");
        fileBtn.setOnAction(e -> {
            File file = chooseAttachmentFile("Select File");
            if (file != null) {
                attachmentPath[0] = file.getAbsolutePath();
                fileLabel.setText("\u2705 " + file.getName());
                fileLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
            }
        });
        HBox fileRow = new HBox(10, fileBtn, fileLabel);
        fileRow.setAlignment(Pos.CENTER_LEFT);

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
                String desc = d + (attachmentPath[0].isEmpty() ? "" : " " + ATTACHMENT_MARKER + attachmentPath[0] + "]");
                DataStore.addSlide(code, t, desc, teacherEmail());
                msgLabel.setStyle("-fx-text-fill: green;");
                msgLabel.setText("Slide uploaded!" + (attachmentPath[0].isEmpty() ? "" : " (with attachment)"));
                titleField.clear();
                descField.clear();
                attachmentPath[0] = "";
                fileLabel.setText("No file selected");
                fileLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");
                showSlides();
            }
        });

        box.getChildren().addAll(title, courseBox, titleField, descField,
                fileRow, uploadBtn, msgLabel, new Separator());

        // Show existing slides
        Label listTitle = new Label("Uploaded Slides:");
        listTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(listTitle);

        boolean found = false;
        for (Course c : DataStore.getAllCourses()) {
            List<String[]> slides = DataStore.getSlidesForCourse(c.getCode());
            for (String[] s : slides) {
                found = true;
                String[] parsed = splitContentAndAttachment(s[2]);
                Label item = new Label("\uD83D\uDCCA [" + s[0] + "] " + s[1]
                        + (parsed[0].isEmpty() ? "" : " — " + parsed[0]));
                item.setStyle("-fx-padding: 6; -fx-background-color: #e8f5e9; -fx-background-radius: 6;");
                item.setWrapText(true);

                VBox itemBox = new VBox(6, item);
                if (!parsed[1].isEmpty()) {
                    itemBox.getChildren().add(createAttachmentActions(parsed[1]));
                }
                box.getChildren().add(itemBox);
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

        TextArea noticeField = new TextArea();
        noticeField.setPromptText("Notice Content");
        noticeField.setPrefRowCount(3);

        Label msgLabel = new Label();
        Button postBtn = new Button("Post Notice");
        postBtn.setOnAction(e -> {
            String sel = courseBox.getValue();
            String content = noticeField.getText().trim();
            if (sel == null || content.isEmpty()) {
                msgLabel.setStyle("-fx-text-fill: red;");
                msgLabel.setText("Select course and enter notice.");
            } else {
                String code = sel.split(" - ")[0];
                DataStore.addCourseNotice(code, content, teacherEmail());
                msgLabel.setStyle("-fx-text-fill: green;");
                msgLabel.setText("Notice posted!");
                noticeField.clear();
                showNotices();
            }
        });

        box.getChildren().addAll(title, courseBox, noticeField, postBtn,
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
        VBox box = new VBox(12);
        box.setPadding(new Insets(10));

        Label title = new Label("\uD83D\uDCE8 Messages");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        String myId = teacherEmail();

        // Start a conversation by entering an ID/email.
        HBox composeRow = new HBox(10);
        composeRow.setAlignment(Pos.CENTER_LEFT);
        TextField newChatField = new TextField();
        newChatField.setPromptText("Start new chat (enter email or ID)...");
        HBox.setHgrow(newChatField, Priority.ALWAYS);
        Button newChatBtn = new Button("\uD83D\uDCAC Open Chat");
        newChatBtn.setStyle("-fx-background-color: #2a5298; -fx-text-fill: white; -fx-background-radius: 8;");
        Button groupChatBtn = new Button("\uD83D\uDC65 Group Chat");
        groupChatBtn.setStyle("-fx-background-color: #1e3c72; -fx-text-fill: white; -fx-background-radius: 8;");
        groupChatBtn.setOnAction(e -> showGroupChat());
        composeRow.getChildren().addAll(newChatField, newChatBtn, groupChatBtn);

        // Left: conversation list
        VBox convList = new VBox(8);
        convList.setStyle("-fx-padding: 8;");
        ScrollPane convScroll = new ScrollPane(convList);
        convScroll.setFitToWidth(true);
        convScroll.setPrefWidth(300);
        convScroll.setMinWidth(260);
        convScroll.setStyle("-fx-background-color: #f8f9ff; -fx-background-radius: 10;");

        // Right: active chat panel
        Label chatTitle = new Label("Select a conversation");
        chatTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        VBox chatMessages = new VBox(8);
        chatMessages.setStyle("-fx-padding: 10;");

        ScrollPane chatScroll = new ScrollPane(chatMessages);
        chatScroll.setFitToWidth(true);
        chatScroll.setPrefHeight(420);
        chatScroll.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10;");

        TextArea chatInput = new TextArea();
        chatInput.setPromptText("Type a message...");
        chatInput.setPrefRowCount(2);
        HBox.setHgrow(chatInput, Priority.ALWAYS);
        chatInput.setDisable(true);

        Button sendBtn = new Button("Send \u27A1");
        sendBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 10 20 10 20;");
        sendBtn.setDisable(true);

        Label statusLabel = new Label();

        HBox inputRow = new HBox(10, chatInput, sendBtn);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        VBox chatPane = new VBox(10, chatTitle, chatScroll, inputRow, statusLabel);
        chatPane.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 10;");
        HBox.setHgrow(chatPane, Priority.ALWAYS);

        HBox mainRow = new HBox(12, convScroll, chatPane);
        HBox.setHgrow(mainRow, Priority.ALWAYS);

        final String[] activePartner = {null};
        final Runnable[] refreshConversationsRef = new Runnable[1];

        Runnable refreshChat = () -> {
            chatMessages.getChildren().clear();
            statusLabel.setText("");

            if (activePartner[0] == null || activePartner[0].isBlank()) {
                chatTitle.setText("Select a conversation");
                chatInput.setDisable(true);
                sendBtn.setDisable(true);
                Label hint = new Label("Choose a chat from the left, or start one above.");
                hint.setStyle("-fx-text-fill: #777; -fx-padding: 20;");
                chatMessages.getChildren().add(hint);
                return;
            }

            chatTitle.setText("\uD83D\uDCAC Chat with " + activePartner[0]);
            chatInput.setDisable(false);
            sendBtn.setDisable(false);

            List<Message> allMsgs = DataStore.getMessagesFor(myId);
            List<Message> filtered = new java.util.ArrayList<>();
            for (Message m : allMsgs) {
                boolean between = (DataStore.isSameMessagingUser(m.getFrom(), myId)
                        && DataStore.isSameMessagingUser(m.getTo(), activePartner[0]))
                        || (DataStore.isSameMessagingUser(m.getFrom(), activePartner[0])
                        && DataStore.isSameMessagingUser(m.getTo(), myId));
                if (between) {
                    filtered.add(m);
                }
            }

            for (Message m : filtered) {
                boolean isMine = DataStore.isSameMessagingUser(m.getFrom(), myId);
                HBox bubble = new HBox();
                bubble.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

                VBox msgBox = new VBox(2);
                msgBox.setMaxWidth(320);
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

            chatScroll.applyCss();
            chatScroll.layout();
            chatScroll.setVvalue(1.0);
        };

        java.util.function.Consumer<String> openChat = partnerRaw -> {
            String partner = DataStore.canonicalMessageId(partnerRaw);
            if (partner.isEmpty()) {
                return;
            }
            activePartner[0] = partner;
            refreshChat.run();
            if (refreshConversationsRef[0] != null) {
                refreshConversationsRef[0].run();
            }
        };

        newChatBtn.setOnAction(e -> {
            String to = DataStore.canonicalMessageId(newChatField.getText().trim());
            if (!to.isEmpty()) {
                openChat.accept(to);
                newChatField.clear();
            }
        });
        newChatField.setOnAction(e -> newChatBtn.fire());

        sendBtn.setOnAction(e -> {
            if (activePartner[0] == null || activePartner[0].isBlank()) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Select a conversation first.");
                return;
            }

            String content = chatInput.getText().trim();
            if (content.isEmpty()) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Type a message.");
                return;
            }

            DataStore.sendMessage(myId, activePartner[0], content);
            chatInput.clear();
            statusLabel.setText("");
            refreshChat.run();
            if (refreshConversationsRef[0] != null) {
                refreshConversationsRef[0].run();
            }
        });

        refreshConversationsRef[0] = () -> {
            convList.getChildren().clear();
            List<Message> messages = DataStore.getMessagesFor(myId);

            java.util.Map<String, Message> lastMessages = new java.util.LinkedHashMap<>();
            for (Message m : messages) {
                boolean sentByMe = DataStore.isSameMessagingUser(m.getFrom(), myId);
                String partner = DataStore.canonicalMessageId(sentByMe ? m.getTo() : m.getFrom());
                if (!partner.isEmpty()) {
                    lastMessages.put(partner, m);
                }
            }

            if (lastMessages.isEmpty()) {
                Label noMsg = new Label("No conversations yet. Start one above!");
                noMsg.setStyle("-fx-text-fill: #888; -fx-padding: 20;");
                convList.getChildren().add(noMsg);
            } else {
                List<java.util.Map.Entry<String, Message>> entries = new java.util.ArrayList<>(lastMessages.entrySet());
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
                    boolean fromMe = DataStore.isSameMessagingUser(lastMsg.getFrom(), myId);
                    String preview = (fromMe ? "You: " : "") + lastMsg.getContent();
                    if (preview.length() > 50) preview = preview.substring(0, 50) + "...";
                    Label previewLabel = new Label(preview);
                    previewLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
                    info.getChildren().addAll(nameLabel, previewLabel);

                    Label timeLabel = new Label(lastMsg.getTimestamp());
                    timeLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");

                    if (activePartner[0] != null && DataStore.isSameMessagingUser(activePartner[0], partner)) {
                        card.setStyle("-fx-padding: 12; -fx-background-color: #eaf2ff; -fx-background-radius: 10; "
                                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 2); -fx-cursor: hand;");
                    }

                    card.getChildren().addAll(avatar, info, timeLabel);
                    card.setOnMouseClicked(ev -> openChat.accept(partner));
                    convList.getChildren().add(card);
                }
            }
        };

        refreshConversationsRef[0].run();
        refreshChat.run();

        Label liveHint = new Label("\uD83D\uDFE2 Messages auto-refresh every 3 seconds");
        liveHint.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px;");

        box.getChildren().addAll(title, composeRow, new Separator(), mainRow, liveHint);
        setScrollContent(box);

        panelRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            refreshConversationsRef[0].run();
            refreshChat.run();
        }));
        panelRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        panelRefreshTimeline.play();
    }

    private void showTeacherDirectChat(String recipientId) {
        if (chatRefreshTimeline != null) chatRefreshTimeline.stop();

        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        String myId = teacherEmail();
        String targetId = DataStore.canonicalMessageId(recipientId);

        Button backBtn = new Button("\u2190 Back to Messages");
        backBtn.setStyle("-fx-background-color: #1e3c72; -fx-text-fill: white; -fx-background-radius: 8;");
        backBtn.setOnAction(e -> showMessages());

        Label title = new Label("\uD83D\uDCAC Chat with " + targetId);
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
                boolean between = (DataStore.isSameMessagingUser(m.getFrom(), myId)
                        && DataStore.isSameMessagingUser(m.getTo(), targetId))
                        || (DataStore.isSameMessagingUser(m.getFrom(), targetId)
                        && DataStore.isSameMessagingUser(m.getTo(), myId));
                if (between) {
                    filtered.add(m);
                }
            }

            chatMessages.getChildren().clear();
            for (Message m : filtered) {
                boolean isMine = DataStore.isSameMessagingUser(m.getFrom(), myId);
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
            DataStore.sendMessage(myId, targetId, content);
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

    private void showGroupChat() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(10));

        Label title = new Label("\uD83D\uDC65 Group Chat");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        String myId = teacherEmail();

        final String[] activeGroup = {null};
        final Runnable[] refreshGroupsRef = new Runnable[1];
        final Runnable[] refreshChatRef = new Runnable[1];

        java.util.function.Consumer<String> openGroup = groupName -> {
            if (groupName == null || groupName.isBlank()) return;
            activeGroup[0] = groupName;
            if (refreshChatRef[0] != null) refreshChatRef[0].run();
            if (refreshGroupsRef[0] != null) refreshGroupsRef[0].run();
        };

        Label groupsTitle = new Label("Your Groups");
        groupsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

        VBox groupList = new VBox(8);
        groupList.setStyle("-fx-padding: 8;");

        ScrollPane groupScroll = new ScrollPane(groupList);
        groupScroll.setFitToWidth(true);
        groupScroll.setPrefWidth(300);
        groupScroll.setMinWidth(260);
        groupScroll.setStyle("-fx-background-color: #f8f9ff; -fx-background-radius: 10;");

        VBox leftPane = new VBox(8, groupsTitle, groupScroll);
        leftPane.setPrefWidth(300);
        leftPane.setMinWidth(260);

        Label activeGroupTitle = new Label("Select a group");
        activeGroupTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        Button manageBtn = new Button("Join/Create Group");
        manageBtn.setStyle("-fx-font-size: 11px; -fx-padding: 6 10 6 10; -fx-background-radius: 8;");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        HBox rightHeader = new HBox(10, activeGroupTitle, headerSpacer, manageBtn);
        rightHeader.setAlignment(Pos.CENTER_LEFT);

        VBox managePanel = new VBox(10);
        managePanel.setStyle("-fx-padding: 12; -fx-background-color: #f8f9ff; -fx-background-radius: 10;");
        managePanel.setVisible(false);
        managePanel.setManaged(false);

        Label createTitle = new Label("Create New Group");
        createTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Group Name (e.g. CSE101 Study Group)");
        TextField groupPasswordField = new TextField();
        groupPasswordField.setPromptText("Group Password (optional)");
        TextField membersField = new TextField();
        membersField.setPromptText("Members (comma separated emails/IDs)");

        Label createMsg = new Label();
        Button createBtn = new Button("Create Group");
        createBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");

        Label searchTitle = new Label("Search & Join Group");
        searchTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search group name...");
        Button searchBtn = new Button("Search");
        VBox searchResults = new VBox(6);
        Label joinMsg = new Label();

        HBox searchRow = new HBox(10, searchField, searchBtn);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        managePanel.getChildren().addAll(
                createTitle, groupNameField, groupPasswordField, membersField, createBtn, createMsg,
                new Separator(), searchTitle, searchRow, searchResults, joinMsg
        );

        VBox chatMessages = new VBox(8);
        chatMessages.setStyle("-fx-padding: 10;");

        ScrollPane chatScroll = new ScrollPane(chatMessages);
        chatScroll.setFitToWidth(true);
        chatScroll.setPrefHeight(360);
        chatScroll.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10;");

        TextArea chatInput = new TextArea();
        chatInput.setPromptText("Type a message...");
        chatInput.setPrefRowCount(2);
        chatInput.setDisable(true);
        HBox.setHgrow(chatInput, Priority.ALWAYS);

        Button sendBtn = new Button("Send \u27A1");
        sendBtn.setDisable(true);
        sendBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; "
                + "-fx-background-radius: 20; -fx-padding: 10 20 10 20;");

        Label chatStatus = new Label();
        HBox inputRow = new HBox(10, chatInput, sendBtn);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        VBox rightPane = new VBox(10, rightHeader, managePanel, chatScroll, inputRow, chatStatus);
        rightPane.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 10;");
        HBox.setHgrow(rightPane, Priority.ALWAYS);

        HBox mainRow = new HBox(12, leftPane, rightPane);
        HBox.setHgrow(mainRow, Priority.ALWAYS);

        manageBtn.setOnAction(e -> {
            boolean show = !managePanel.isVisible();
            managePanel.setVisible(show);
            managePanel.setManaged(show);
        });

        createBtn.setOnAction(e -> {
            String gName = groupNameField.getText().trim();
            String gPass = groupPasswordField.getText().trim();
            String members = membersField.getText().trim();
            if (gName.isEmpty()) {
                createMsg.setStyle("-fx-text-fill: red;");
                createMsg.setText("Enter a group name.");
                return;
            }
            String memberList = members.isEmpty() ? myId : myId + "," + members;
            DataStore.createGroup(gName, myId, gPass, memberList);
            createMsg.setStyle("-fx-text-fill: green;");
            createMsg.setText("Group '" + gName + "' created! \u2705");
            groupNameField.clear();
            groupPasswordField.clear();
            membersField.clear();
            openGroup.accept(gName);
        });

        searchBtn.setOnAction(e -> {
            searchResults.getChildren().clear();
            String query = searchField.getText().trim();
            if (query.isEmpty()) return;

            List<String[]> found = DataStore.searchGroups(query);
            if (found.isEmpty()) {
                searchResults.getChildren().add(new Label("No groups found."));
            } else {
                for (String[] g : found) {
                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setStyle("-fx-padding: 8; -fx-background-color: white; -fx-background-radius: 6;");

                    Label gLabel = new Label("\uD83D\uDC65 " + g[0] + " (by " + g[1] + ")"
                            + (g[2].isEmpty() ? "" : " \uD83D\uDD12"));
                    gLabel.setStyle("-fx-font-weight: bold;");
                    HBox.setHgrow(gLabel, Priority.ALWAYS);

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
                            openGroup.accept(g[0]);
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

        refreshGroupsRef[0] = () -> {
            groupList.getChildren().clear();
            List<String[]> groups = DataStore.getGroupsForUser(myId);

            if (groups.isEmpty()) {
                activeGroup[0] = null;
                Label noGroup = new Label("No groups yet. Use Join/Create Group.");
                noGroup.setStyle("-fx-text-fill: #888; -fx-padding: 20;");
                groupList.getChildren().add(noGroup);
                return;
            }

            boolean activeExists = false;
            for (String[] g : groups) {
                String gName = g[0];
                if (gName.equals(activeGroup[0])) activeExists = true;

                HBox card = new HBox(10);
                card.setAlignment(Pos.CENTER_LEFT);
                card.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-background-radius: 10; "
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1); -fx-cursor: hand;");

                if (gName.equals(activeGroup[0])) {
                    card.setStyle("-fx-padding: 10; -fx-background-color: #eaf2ff; -fx-background-radius: 10; "
                            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1); -fx-cursor: hand;");
                }

                Label icon = new Label("\uD83D\uDC65");
                icon.setStyle("-fx-font-size: 18px;");

                Label name = new Label(gName + (g[2].isEmpty() ? "" : " \uD83D\uDD12"));
                name.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e3c72;");

                card.getChildren().addAll(icon, name);
                card.setOnMouseClicked(ev -> openGroup.accept(gName));
                groupList.getChildren().add(card);
            }

            if (!activeExists) {
                activeGroup[0] = null;
            }
        };

        refreshChatRef[0] = () -> {
            chatMessages.getChildren().clear();

            if (activeGroup[0] == null || activeGroup[0].isEmpty()) {
                activeGroupTitle.setText("Select a group");
                chatInput.setDisable(true);
                sendBtn.setDisable(true);
                Label hint = new Label("Pick a group from the left to start chatting.");
                hint.setStyle("-fx-text-fill: #777; -fx-padding: 20;");
                chatMessages.getChildren().add(hint);
                return;
            }

            activeGroupTitle.setText("\uD83D\uDC65 " + activeGroup[0]);
            chatInput.setDisable(false);
            sendBtn.setDisable(false);

            List<String[]> msgs = DataStore.getGroupMessages(activeGroup[0]);
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
                    msgBox.setMaxWidth(320);
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

            chatScroll.applyCss();
            chatScroll.layout();
            chatScroll.setVvalue(1.0);
        };

        sendBtn.setOnAction(e -> {
            String content = chatInput.getText().trim();
            if (activeGroup[0] == null || activeGroup[0].isEmpty()) {
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
            DataStore.addGroupMessage(activeGroup[0], myId, senderName, content);
            chatInput.clear();
            chatStatus.setText("");
            refreshChatRef[0].run();
            refreshGroupsRef[0].run();
        });

        refreshGroupsRef[0].run();
        refreshChatRef[0].run();

        Label liveHint = new Label("\uD83D\uDFE2 Groups and chat auto-refresh every 3 seconds");
        liveHint.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px;");

        box.getChildren().addAll(title, mainRow, liveHint);
        setScrollContent(box);

        panelRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            refreshGroupsRef[0].run();
            refreshChatRef[0].run();
        }));
        panelRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        panelRefreshTimeline.play();
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

    private HBox createAttachmentActions(String filePath) {
        Button openFile = new Button("\uD83D\uDCC4 Open File");
        openFile.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-size: 11px;");
        openFile.setOnAction(ev -> {
            try {
                java.awt.Desktop.getDesktop().open(new File(filePath));
            } catch (Exception ex) {
                // ignore open errors
            }
        });

        Button downloadFile = new Button("\u2B07 Download File");
        downloadFile.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-size: 11px;");
        downloadFile.setOnAction(ev -> downloadAttachmentFile(filePath));

        HBox actions = new HBox(8, openFile, downloadFile);
        actions.setAlignment(Pos.CENTER_LEFT);
        return actions;
    }

    private void downloadAttachmentFile(String filePath) {
        try {
            File source = new File(filePath);
            if (!source.exists()) {
                return;
            }

            FileChooser saveDialog = new FileChooser();
            saveDialog.setTitle("Save File");
            saveDialog.setInitialFileName(source.getName());
            saveDialog.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));

            File destination = saveDialog.showSaveDialog(contentArea.getScene().getWindow());
            if (destination != null) {
                java.nio.file.Files.copy(source.toPath(), destination.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            // ignore download errors
        }
    }

    private File chooseAttachmentFile(String title) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Allowed Files",
                "*.pdf", "*.zip", "*.jpg", "*.jpeg", "*.png", "*.gif",
                "*.cpp", "*.c", "*.h", "*.hpp", "*.java",
                "*.pptx", "*.xlsx", "*.xls", "*.doc", "*.docx", "*.txt"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        return fc.showOpenDialog(contentArea.getScene().getWindow());
    }

    private String[] splitContentAndAttachment(String rawContent) {
        String raw = rawContent == null ? "" : rawContent;
        int fileIdx = raw.indexOf(ATTACHMENT_MARKER);
        int legacyIdx = raw.indexOf(LEGACY_PDF_MARKER);

        int markerIdx = -1;
        int markerLength = 0;
        if (fileIdx >= 0 && (legacyIdx < 0 || fileIdx < legacyIdx)) {
            markerIdx = fileIdx;
            markerLength = ATTACHMENT_MARKER.length();
        } else if (legacyIdx >= 0) {
            markerIdx = legacyIdx;
            markerLength = LEGACY_PDF_MARKER.length();
        }

        if (markerIdx < 0) {
            return new String[]{raw, ""};
        }

        int endIdx = raw.indexOf("]", markerIdx);
        if (endIdx <= markerIdx + markerLength) {
            return new String[]{raw, ""};
        }

        String text = raw.substring(0, markerIdx).trim();
        String attachmentPath = raw.substring(markerIdx + markerLength, endIdx).trim();
        return new String[]{text, attachmentPath};
    }

    private void setScrollContent(VBox content) {
        if (panelRefreshTimeline != null) panelRefreshTimeline.stop();
        if (chatRefreshTimeline != null) chatRefreshTimeline.stop();
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        contentArea.getChildren().setAll(scroll);
    }
}
