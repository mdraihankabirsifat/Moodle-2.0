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
        TextField semField = new TextField();
        semField.setPromptText("Semester (e.g. Spring 2025)");

        Label msgLabel = new Label();
        Button addBtn = new Button("Add Course");
        addBtn.setOnAction(e -> {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            String sem = semField.getText().trim();
            if (code.isEmpty() || name.isEmpty() || sem.isEmpty()) {
                msgLabel.setStyle("-fx-text-fill: red;");
                msgLabel.setText("Fill all fields.");
            } else {
                DataStore.addCourse(new Course(code, name, teacherEmail(), sem));
                msgLabel.setStyle("-fx-text-fill: green;");
                msgLabel.setText("Course added!");
                codeField.clear();
                nameField.clear();
                semField.clear();
                showMyCourses();
            }
        });

        box.getChildren().addAll(title, new Separator(), formTitle,
                codeField, nameField, semField, addBtn, msgLabel, new Separator());

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
                        + " (" + c.getSemester() + ")");
                info.setStyle("-fx-font-weight: bold;");
                Label teacher = new Label("by " + c.getTeacherEmail());
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

        Button loadBtn = new Button("Load Submissions");
        loadBtn.setOnAction(e -> {
            submissionsBox.getChildren().clear();
            String sel = courseBox.getValue();
            if (sel == null) {
                msgLabel.setStyle("-fx-text-fill: red;");
                msgLabel.setText("Select a course first.");
                return;
            }
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
                            Label sContent = new Label("Answer: " + sub[3]);
                            sContent.setWrapText(true);
                            Label sMarks = new Label("Current Marks: " + sub[4]);
                            sMarks.setStyle("-fx-text-fill: #2a5298;");
                            info.getChildren().addAll(sId, sContent, sMarks);

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
                                    showEvaluate();
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
        });

        box.getChildren().addAll(title, courseBox, loadBtn, msgLabel,
                new Separator(), submissionsBox);
        setScrollContent(box);
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

    // ===================== MESSAGES =====================

    @FXML
    private void showMessages() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("Messages");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        // Compose
        Label composeTitle = new Label("Send Message");
        composeTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        TextField toField = new TextField();
        toField.setPromptText("Recipient Student ID or Email");
        TextArea msgArea = new TextArea();
        msgArea.setPromptText("Message content");
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
                DataStore.sendMessage(teacherEmail(), to, content);
                msgLabel.setStyle("-fx-text-fill: green;");
                msgLabel.setText("Message sent!");
                toField.clear();
                msgArea.clear();
                showMessages();
            }
        });

        box.getChildren().addAll(title, composeTitle, toField, msgArea,
                sendBtn, msgLabel, new Separator());

        // Inbox
        Label inboxTitle = new Label("Inbox:");
        inboxTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        box.getChildren().add(inboxTitle);

        List<Message> messages = DataStore.getMessagesFor(teacherEmail());
        if (messages.isEmpty()) {
            box.getChildren().add(new Label("No messages."));
        } else {
            for (int i = messages.size() - 1; i >= 0; i--) {
                Message m = messages.get(i);
                String direction = m.getFrom().equals(teacherEmail())
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

        chatRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> refreshChat.run()));
        chatRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        chatRefreshTimeline.play();

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
