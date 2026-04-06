package com.example.moodle.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class CommunityDashboardController {

    @FXML private StackPane contentArea;
    private Timeline refreshTimeline;

    @FXML
    public void initialize() {
        showCommunity();
    }

    private void setScrollContent(VBox content) {
        if (refreshTimeline != null) refreshTimeline.stop();
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        contentArea.getChildren().setAll(scroll);
    }

    // ===================== COMMUNITY FEED =====================
    @FXML
    private void showCommunity() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("\uD83C\uDF10 Universal Community");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        // Post Form
        VBox formBox = new VBox(10);
        formBox.setStyle("-fx-padding: 16; -fx-background-color: #111a2e; -fx-background-radius: 10; "
                + "-fx-border-color: rgba(0,229,255,0.15); -fx-border-radius: 10;");

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label userAvatar = new Label("\uD83D\uDC64");
        userAvatar.setStyle("-fx-font-size: 28px; -fx-background-color: #0a1628; "
                + "-fx-padding: 6 10 6 10; -fx-background-radius: 50;");
        Label userName = new Label(Session.getName() != null ? Session.getName() : "You");
        userName.setStyle("-fx-font-weight: bold; -fx-text-fill: #00e5ff; -fx-font-size: 14px;");
        topRow.getChildren().addAll(userAvatar, userName);

        TextArea postArea = new TextArea();
        postArea.setPromptText("What's on your mind?");
        postArea.setPrefRowCount(3);
        postArea.setWrapText(true);

        Label postMsg = new Label();
        Button postBtn = new Button("\uD83D\uDCE8 Post");
        postBtn.setStyle("-fx-background-color: #00e5ff; -fx-text-fill: #0a0e1a; -fx-font-weight: 900; "
                + "-fx-background-radius: 8; -fx-padding: 8 24 8 24; -fx-cursor: hand; -fx-font-size: 13px;");

        HBox actionRow = new HBox(12, new Region(), postBtn, postMsg);
        actionRow.setAlignment(Pos.CENTER_RIGHT);

        formBox.getChildren().addAll(topRow, postArea, actionRow);

        VBox feed = new VBox(12);

        Runnable refreshFeed = () -> {
            feed.getChildren().clear();
            List<String[]> posts = DataStore.getAllCommunityPosts();
            String currentUser = Session.getIdentifier();
            if (posts.isEmpty()) {
                Label noPost = new Label("No posts yet. Be the first to share!");
                noPost.setStyle("-fx-text-fill: #5a6a7e; -fx-padding: 20; -fx-font-size: 14px;");
                feed.getChildren().add(noPost);
            } else {
                for (int i = posts.size() - 1; i >= 0; i--) {
                    String[] p = posts.get(i);
                    final int postIndex = i;
                    String postAuthorId = p[0];
                    String postAuthorName = p[1];
                    String postContent = p[2];
                    String postTime = p[3];

                    VBox postCard = new VBox(8);
                    postCard.setStyle("-fx-padding: 16; -fx-background-color: #111a2e; -fx-background-radius: 10; "
                            + "-fx-border-color: rgba(0,229,255,0.12); -fx-border-radius: 10;");

                    HBox header = new HBox(10);
                    header.setAlignment(Pos.CENTER_LEFT);
                    Label avatar = new Label("\uD83D\uDC64");
                    avatar.setStyle("-fx-font-size: 24px; -fx-background-color: #0a1628; "
                            + "-fx-padding: 4 8 4 8; -fx-background-radius: 50;");
                    VBox nameTime = new VBox(1);
                    Label authorLabel = new Label(postAuthorName);
                    authorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #00e5ff;");
                    Label timeLabel = new Label(postTime);
                    timeLabel.setStyle("-fx-text-fill: #4a5a6e; -fx-font-size: 10px;");
                    nameTime.getChildren().addAll(authorLabel, timeLabel);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    header.getChildren().addAll(avatar, nameTime, spacer);

                    if (postAuthorId.equals(currentUser)) {
                        Button deleteBtn = new Button("\uD83D\uDDD1");
                        deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff3366; "
                                + "-fx-cursor: hand; -fx-font-size: 14px;");
                        deleteBtn.setOnAction(e -> {
                            DataStore.deleteCommunityPost(postIndex);
                            showCommunity();
                        });
                        header.getChildren().add(deleteBtn);
                    }

                    // Message button for other users
                    if (!postAuthorId.equals(currentUser)) {
                        Button msgBtn = new Button("\uD83D\uDCAC Message");
                        msgBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #00e5ff; "
                                + "-fx-border-color: rgba(0,229,255,0.3); -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 11px;");
                        msgBtn.setOnAction(e -> showDirectChat(postAuthorId));
                        header.getChildren().add(msgBtn);
                    }

                    Label contentLabel = new Label(postContent);
                    contentLabel.setWrapText(true);
                    contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #d0d8e8;");

                    postCard.getChildren().addAll(header, contentLabel);
                    feed.getChildren().add(postCard);
                }
            }
        };

        postBtn.setOnAction(e -> {
            String text = postArea.getText();
            if (text == null || text.trim().isEmpty()) {
                postMsg.setStyle("-fx-text-fill: #ff3366;");
                postMsg.setText("Write something first.");
                return;
            }
            String id = Session.getIdentifier();
            String name = Session.getName() != null ? Session.getName() : id;
            DataStore.addCommunityPost(id, name, text.trim());
            postArea.clear();
            postMsg.setText("");
            refreshFeed.run();
        });

        refreshFeed.run();

        box.getChildren().addAll(title, formBox, new Separator(), feed);
        setScrollContent(box);
    }

    // ===================== MESSAGES =====================
    @FXML
    private void showMessages() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(10));

        Label title = new Label("\uD83D\uDCE8 Messages");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        String myId = Session.getIdentifier();

        HBox composeRow = new HBox(10);
        composeRow.setAlignment(Pos.CENTER_LEFT);
        TextField newChatField = new TextField();
        newChatField.setPromptText("Start new chat (enter email or ID)...");
        HBox.setHgrow(newChatField, Priority.ALWAYS);
        Button newChatBtn = new Button("\uD83D\uDCAC Open Chat");
        newChatBtn.setStyle("-fx-background-color: #0d2a4a; -fx-text-fill: white; -fx-background-radius: 8;");

        Button groupChatBtn = new Button("\uD83D\uDC65 Join/Create Group");
        groupChatBtn.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: #00e5ff; -fx-border-color: rgba(0,229,255,0.3); -fx-background-radius: 8;");

        composeRow.getChildren().addAll(newChatField, newChatBtn, groupChatBtn);

        // Group management panel
        VBox managePanel = new VBox(10);
        managePanel.setStyle("-fx-padding: 12; -fx-background-color: #0a1628; -fx-background-radius: 10;");
        managePanel.setVisible(false);
        managePanel.setManaged(false);

        Label createTitle2 = new Label("Create New Group");
        createTitle2.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Group Name");
        TextField groupPasswordField = new TextField();
        groupPasswordField.setPromptText("Group Password (optional)");
        TextField membersField = new TextField();
        membersField.setPromptText("Members (comma separated emails/IDs)");
        Label createMsg = new Label();
        Button createBtn = new Button("Create Group");
        createBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #00ff88; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");

        Label searchTitle2 = new Label("Search & Join Group");
        searchTitle2.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        TextField searchField = new TextField();
        searchField.setPromptText("Search group name...");
        Button searchBtn = new Button("Search");
        VBox searchResults = new VBox(6);
        Label joinMsg = new Label();
        HBox searchRow = new HBox(10, searchField, searchBtn);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        managePanel.getChildren().addAll(createTitle2, groupNameField, groupPasswordField, membersField, createBtn, createMsg,
                new Separator(), searchTitle2, searchRow, searchResults, joinMsg);

        // Conversation list & chat pane
        VBox convList = new VBox(8);
        convList.setStyle("-fx-padding: 8;");
        ScrollPane convScroll = new ScrollPane(convList);
        convScroll.setFitToWidth(true);
        convScroll.setPrefWidth(300);
        convScroll.setMinWidth(260);
        convScroll.setStyle("-fx-background-color: #0a1628; -fx-background-radius: 10;");

        VBox leftPane = new VBox(10, convScroll);
        leftPane.setPrefWidth(300);
        leftPane.setMinWidth(260);

        Label chatTitle2 = new Label("Select a conversation");
        chatTitle2.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        VBox chatMessages = new VBox(8);
        chatMessages.setStyle("-fx-padding: 10;");
        ScrollPane chatScroll = new ScrollPane(chatMessages);
        chatScroll.setFitToWidth(true);
        chatScroll.setPrefHeight(420);
        chatScroll.setStyle("-fx-background-color: #0a1628; -fx-background-radius: 10;");

        TextArea chatInput = new TextArea();
        chatInput.setPromptText("Type a message...");
        chatInput.setPrefRowCount(2);
        HBox.setHgrow(chatInput, Priority.ALWAYS);
        chatInput.setDisable(true);

        Button sendBtn = new Button("Send \u27A1");
        sendBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #00ff88; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 10 20 10 20;");
        sendBtn.setDisable(true);

        chatInput.setOnKeyPressed(ev -> {
            if (ev.getCode() == javafx.scene.input.KeyCode.ENTER && !ev.isShiftDown()) {
                ev.consume();
                sendBtn.fire();
            }
        });

        Label statusLabel = new Label();
        HBox inputRow = new HBox(10, chatInput, sendBtn);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        VBox chatPane = new VBox(10, chatTitle2, chatScroll, inputRow, statusLabel);
        chatPane.setStyle("-fx-background-color: #111a2e; -fx-background-radius: 12; -fx-padding: 10;");
        HBox.setHgrow(chatPane, Priority.ALWAYS);

        HBox mainRow = new HBox(12, leftPane, chatPane);
        HBox.setHgrow(mainRow, Priority.ALWAYS);

        final String[] activePartner = {null};
        final boolean[] isGroupChat = {false};
        final Runnable[] refreshConversationsRef = new Runnable[1];

        groupChatBtn.setOnAction(e -> {
            boolean show = !managePanel.isVisible();
            managePanel.setVisible(show);
            managePanel.setManaged(show);
        });

        java.util.function.Consumer<String> openChat = partnerRaw -> {
            String partner = DataStore.canonicalMessageId(partnerRaw);
            if (partner.isEmpty()) return;
            activePartner[0] = partner;
            isGroupChat[0] = false;
        };

        java.util.function.Consumer<String> openGroup = groupName -> {
            if (groupName == null || groupName.isBlank()) return;
            activePartner[0] = groupName;
            isGroupChat[0] = true;
        };

        createBtn.setOnAction(e -> {
            String gName = groupNameField.getText().trim();
            String gPass = groupPasswordField.getText().trim();
            String members = membersField.getText().trim();
            if (gName.isEmpty()) { createMsg.setStyle("-fx-text-fill: #ff3366;"); createMsg.setText("Enter a group name."); return; }
            String memberList = members.isEmpty() ? myId : myId + "," + members;
            DataStore.createGroup(gName, myId, gPass, memberList);
            createMsg.setStyle("-fx-text-fill: #00ff88;"); createMsg.setText("Group '" + gName + "' created! \u2705");
            groupNameField.clear(); groupPasswordField.clear(); membersField.clear();
            openGroup.accept(gName);
            if (refreshConversationsRef[0] != null) refreshConversationsRef[0].run();
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
                    row.setStyle("-fx-padding: 8; -fx-background-color: #111a2e; -fx-background-radius: 6;");
                    Label gLabel = new Label("\uD83D\uDC65 " + g[0] + " (by " + g[1] + ")" + (g[2].isEmpty() ? "" : " \uD83D\uDD12"));
                    gLabel.setStyle("-fx-font-weight: bold;");
                    HBox.setHgrow(gLabel, Priority.ALWAYS);
                    TextField passField = new TextField();
                    passField.setPromptText("Password"); passField.setPrefWidth(120);
                    passField.setVisible(!g[2].isEmpty()); passField.setManaged(!g[2].isEmpty());
                    Button joinBtn = new Button("Join");
                    joinBtn.setOnAction(ev -> {
                        boolean ok = DataStore.joinGroup(g[0], passField.getText().trim(), myId);
                        if (ok) { joinMsg.setStyle("-fx-text-fill: #00ff88;"); joinMsg.setText("Joined '" + g[0] + "'!"); openGroup.accept(g[0]); if (refreshConversationsRef[0] != null) refreshConversationsRef[0].run(); }
                        else { joinMsg.setStyle("-fx-text-fill: #ff3366;"); joinMsg.setText("Wrong password."); }
                    });
                    row.getChildren().addAll(gLabel, passField, joinBtn);
                    searchResults.getChildren().add(row);
                }
            }
        });

        Runnable refreshChat = () -> {
            chatMessages.getChildren().clear();
            statusLabel.setText("");
            if (activePartner[0] == null || activePartner[0].isBlank()) {
                chatTitle2.setText("Select a conversation");
                chatInput.setDisable(true); sendBtn.setDisable(true);
                Label hint = new Label("Choose a chat or group from the left, or start one above.");
                hint.setStyle("-fx-text-fill: #6a7a8e; -fx-padding: 20;");
                chatMessages.getChildren().add(hint);
                return;
            }
            chatInput.setDisable(false); sendBtn.setDisable(false);

            if (isGroupChat[0]) {
                chatTitle2.setText("\uD83D\uDC65 Chat " + activePartner[0]);
                List<String[]> msgs = DataStore.getGroupMessages(activePartner[0]);
                if (msgs.isEmpty()) {
                    chatMessages.getChildren().add(new Label("No messages yet."));
                } else {
                    for (String[] m : msgs) {
                        boolean isMine = m[1].equals(myId);
                        HBox bubble = new HBox();
                        bubble.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
                        VBox msgBox = new VBox(2);
                        msgBox.setMaxWidth(320);
                        msgBox.setStyle("-fx-padding: 10 14 10 14; -fx-background-radius: 14; "
                                + (isMine ? "-fx-background-color: #0d2a4a;" : "-fx-background-color: #111a2e; -fx-border-color: rgba(0,229,255,0.2); -fx-border-radius: 14;"));
                        Label sender = new Label(isMine ? "You" : m[2]);
                        sender.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; " + (isMine ? "-fx-text-fill: rgba(0,229,255,0.6);" : "-fx-text-fill: #00e5ff;"));
                        Label content = new Label(m[3]);
                        content.setWrapText(true);
                        content.setStyle(isMine ? "-fx-text-fill: #00e5ff; -fx-font-size: 13px;" : "-fx-text-fill: #d0d8e8; -fx-font-size: 13px;");
                        Label ts = new Label(m[4]);
                        ts.setStyle("-fx-font-size: 10px; " + (isMine ? "-fx-text-fill: rgba(0,229,255,0.5);" : "-fx-text-fill: #4a5a6e;"));
                        msgBox.getChildren().addAll(sender, content, ts);
                        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
                        if (isMine) bubble.getChildren().addAll(sp, msgBox); else bubble.getChildren().addAll(msgBox, sp);
                        chatMessages.getChildren().add(bubble);
                    }
                }
            } else {
                chatTitle2.setText("\uD83D\uDCAC Chat with " + activePartner[0]);
                List<Message> allMsgs = DataStore.getMessagesFor(myId);
                List<Message> filtered = new ArrayList<>();
                for (Message m : allMsgs) {
                    boolean between = (DataStore.isSameMessagingUser(m.getFrom(), myId) && DataStore.isSameMessagingUser(m.getTo(), activePartner[0]))
                            || (DataStore.isSameMessagingUser(m.getFrom(), activePartner[0]) && DataStore.isSameMessagingUser(m.getTo(), myId));
                    if (between) filtered.add(m);
                }
                for (Message m : filtered) {
                    boolean isMine = DataStore.isSameMessagingUser(m.getFrom(), myId);
                    HBox bubble = new HBox();
                    bubble.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
                    VBox msgBox = new VBox(2);
                    msgBox.setMaxWidth(320);
                    msgBox.setStyle("-fx-padding: 10 14 10 14; -fx-background-radius: 14; "
                            + (isMine ? "-fx-background-color: #0d2a4a;" : "-fx-background-color: #111a2e; -fx-border-color: rgba(0,229,255,0.2); -fx-border-radius: 14;"));
                    Label content = new Label(m.getContent());
                    content.setWrapText(true);
                    content.setStyle(isMine ? "-fx-text-fill: #00e5ff; -fx-font-size: 13px;" : "-fx-text-fill: #d0d8e8; -fx-font-size: 13px;");
                    Label ts = new Label(m.getTimestamp());
                    ts.setStyle("-fx-font-size: 10px; " + (isMine ? "-fx-text-fill: rgba(0,229,255,0.5);" : "-fx-text-fill: #4a5a6e;"));
                    msgBox.getChildren().addAll(content, ts);
                    Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
                    if (isMine) bubble.getChildren().addAll(sp, msgBox); else bubble.getChildren().addAll(msgBox, sp);
                    chatMessages.getChildren().add(bubble);
                }
                if (chatMessages.getChildren().isEmpty()) {
                    chatMessages.getChildren().add(new Label("No messages yet. Start the conversation!"));
                }
            }
            chatScroll.applyCss();
            chatScroll.layout();
            chatScroll.setVvalue(1.0);
        };

        newChatBtn.setOnAction(e -> {
            String to = DataStore.canonicalMessageId(newChatField.getText().trim());
            if (!to.isEmpty()) {
                activePartner[0] = to; isGroupChat[0] = false;
                newChatField.clear(); refreshChat.run();
                if (refreshConversationsRef[0] != null) refreshConversationsRef[0].run();
            }
        });
        newChatField.setOnAction(e -> newChatBtn.fire());

        sendBtn.setOnAction(e -> {
            if (activePartner[0] == null || activePartner[0].isBlank()) return;
            String content = chatInput.getText().trim();
            if (content.isEmpty()) return;
            if (isGroupChat[0]) {
                String senderName = Session.getName() != null ? Session.getName() : myId;
                DataStore.addGroupMessage(activePartner[0], myId, senderName, content);
            } else {
                DataStore.sendMessage(myId, activePartner[0], content);
            }
            chatInput.clear();
            refreshChat.run();
            if (refreshConversationsRef[0] != null) refreshConversationsRef[0].run();
        });

        refreshConversationsRef[0] = () -> {
            convList.getChildren().clear();
            // Groups
            List<String[]> groups = DataStore.getGroupsForUser(myId);
            for (String[] g : groups) {
                String gName = g[0];
                HBox card = new HBox(12);
                card.setAlignment(Pos.CENTER_LEFT);
                card.setStyle("-fx-padding: 12; -fx-background-color: #111a2e; -fx-background-radius: 10; -fx-cursor: hand;");
                Label av = new Label("\uD83D\uDC65"); av.setStyle("-fx-font-size: 28px;");
                VBox info = new VBox(3); HBox.setHgrow(info, Priority.ALWAYS);
                Label nameL = new Label(gName); nameL.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #00e5ff;");
                info.getChildren().add(nameL);
                card.getChildren().addAll(av, info);
                card.setOnMouseClicked(ev -> { activePartner[0] = gName; isGroupChat[0] = true; refreshChat.run(); refreshConversationsRef[0].run(); });
                convList.getChildren().add(card);
            }
            // Direct messages
            List<Message> messages = DataStore.getMessagesFor(myId);
            Map<String, Message> lastMessages = new LinkedHashMap<>();
            for (Message m : messages) {
                boolean sentByMe = DataStore.isSameMessagingUser(m.getFrom(), myId);
                String partner = DataStore.canonicalMessageId(sentByMe ? m.getTo() : m.getFrom());
                if (!partner.isEmpty()) lastMessages.put(partner, m);
            }
            List<Map.Entry<String, Message>> entries = new ArrayList<>(lastMessages.entrySet());
            for (int i = entries.size() - 1; i >= 0; i--) {
                Map.Entry<String, Message> entry = entries.get(i);
                String partner = entry.getKey();
                Message lastMsg = entry.getValue();
                HBox card = new HBox(12);
                card.setAlignment(Pos.CENTER_LEFT);
                card.setStyle("-fx-padding: 12; -fx-background-color: #111a2e; -fx-background-radius: 10; -fx-cursor: hand;");
                Label av = new Label("\uD83D\uDC64"); av.setStyle("-fx-font-size: 28px;");
                VBox info = new VBox(3); HBox.setHgrow(info, Priority.ALWAYS);
                Label nameL = new Label(partner); nameL.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #00e5ff;");
                boolean fromMe = DataStore.isSameMessagingUser(lastMsg.getFrom(), myId);
                String preview = (fromMe ? "You: " : "") + lastMsg.getContent();
                if (preview.length() > 50) preview = preview.substring(0, 50) + "...";
                Label prevL = new Label(preview); prevL.setStyle("-fx-text-fill: #7a8a9e; -fx-font-size: 12px;");
                info.getChildren().addAll(nameL, prevL);
                Label timeL = new Label(lastMsg.getTimestamp()); timeL.setStyle("-fx-text-fill: #4a5a6e; -fx-font-size: 11px;");
                card.getChildren().addAll(av, info, timeL);
                card.setOnMouseClicked(ev -> { activePartner[0] = partner; isGroupChat[0] = false; refreshChat.run(); refreshConversationsRef[0].run(); });
                convList.getChildren().add(card);
            }
            if (convList.getChildren().isEmpty()) {
                convList.getChildren().add(new Label("No conversations yet. Start one above!"));
            }
        };

        refreshConversationsRef[0].run();
        refreshChat.run();

        Label liveHint = new Label("\uD83D\uDFE2 Messages auto-refresh every 3 seconds");
        liveHint.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 11px;");

        box.getChildren().addAll(title, composeRow, managePanel, new Separator(), mainRow, liveHint);
        setScrollContent(box);

        if (refreshTimeline != null) refreshTimeline.stop();
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> { refreshConversationsRef[0].run(); refreshChat.run(); }));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    // ===================== DIRECT CHAT (from Community feed) =====================
    private void showDirectChat(String recipientId) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        String myId = Session.getIdentifier();

        Button backBtn = new Button("\u2190 Back to Community");
        backBtn.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: #00e5ff; -fx-border-color: rgba(0,229,255,0.3); -fx-background-radius: 8;");
        backBtn.setOnAction(e -> showCommunity());

        Label chatTitleL = new Label("\uD83D\uDCAC Chat with " + recipientId);
        chatTitleL.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        VBox chatMessages = new VBox(8);
        chatMessages.setStyle("-fx-padding: 10;");
        ScrollPane chatScroll = new ScrollPane(chatMessages);
        chatScroll.setFitToWidth(true);
        chatScroll.setPrefHeight(350);
        chatScroll.setStyle("-fx-background-color: #0a1628; -fx-background-radius: 10;");

        TextArea chatInput = new TextArea();
        chatInput.setPromptText("Type a message...");
        chatInput.setPrefRowCount(2);
        HBox.setHgrow(chatInput, Priority.ALWAYS);

        Button sendBtn = new Button("Send \u27A1");
        sendBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #00ff88; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 10 20 10 20;");

        chatInput.setOnKeyPressed(ev -> { if (ev.getCode() == javafx.scene.input.KeyCode.ENTER && !ev.isShiftDown()) { ev.consume(); sendBtn.fire(); } });

        Label statusLabel = new Label();
        HBox inputRow = new HBox(10, chatInput, sendBtn);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        Runnable refreshChat = () -> {
            List<Message> allMsgs = DataStore.getMessagesFor(myId);
            List<Message> filtered = new ArrayList<>();
            for (Message m : allMsgs) {
                if ((DataStore.isSameMessagingUser(m.getFrom(), myId) && DataStore.isSameMessagingUser(m.getTo(), recipientId))
                        || (DataStore.isSameMessagingUser(m.getFrom(), recipientId) && DataStore.isSameMessagingUser(m.getTo(), myId))) {
                    filtered.add(m);
                }
            }
            chatMessages.getChildren().clear();
            for (Message m : filtered) {
                boolean isMine = DataStore.isSameMessagingUser(m.getFrom(), myId);
                HBox bubble = new HBox();
                bubble.setAlignment(isMine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
                VBox msgBox = new VBox(2);
                msgBox.setMaxWidth(320);
                msgBox.setStyle("-fx-padding: 10 14 10 14; -fx-background-radius: 14; "
                        + (isMine ? "-fx-background-color: #0d2a4a;" : "-fx-background-color: #111a2e; -fx-border-color: rgba(0,229,255,0.2); -fx-border-radius: 14;"));
                Label content = new Label(m.getContent());
                content.setWrapText(true);
                content.setStyle(isMine ? "-fx-text-fill: #00e5ff; -fx-font-size: 13px;" : "-fx-text-fill: #d0d8e8; -fx-font-size: 13px;");
                Label ts = new Label(m.getTimestamp());
                ts.setStyle("-fx-font-size: 10px; " + (isMine ? "-fx-text-fill: rgba(0,229,255,0.5);" : "-fx-text-fill: #4a5a6e;"));
                msgBox.getChildren().addAll(content, ts);
                Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
                if (isMine) bubble.getChildren().addAll(sp, msgBox); else bubble.getChildren().addAll(msgBox, sp);
                chatMessages.getChildren().add(bubble);
            }
            if (chatMessages.getChildren().isEmpty()) {
                chatMessages.getChildren().add(new Label("No messages yet. Start the conversation!"));
            }
            chatScroll.applyCss(); chatScroll.layout(); chatScroll.setVvalue(1.0);
        };

        sendBtn.setOnAction(e -> {
            String content = chatInput.getText().trim();
            if (content.isEmpty()) return;
            DataStore.sendMessage(myId, recipientId, content);
            chatInput.clear();
            refreshChat.run();
        });

        refreshChat.run();

        box.getChildren().addAll(backBtn, chatTitleL, chatScroll, inputRow, statusLabel);
        setScrollContent(box);

        if (refreshTimeline != null) refreshTimeline.stop();
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> refreshChat.run()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    // ===================== FRIENDS =====================
    @FXML
    private void showFriends() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        Label title = new Label("\uD83D\uDC65 Friends & People");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name...");

        VBox userList = new VBox(10);
        String myId = Session.getIdentifier();

        Runnable renderUsers = () -> {
            userList.getChildren().clear();
            String query = searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
            List<User> allUsers = UserStore.getAllUsers();
            int count = 0;
            for (User u : allUsers) {
                if (u.getEmail().equals(myId)) continue; // skip self
                String displayName = u.getName() != null ? u.getName() : u.getEmail();
                if (!query.isEmpty() && !displayName.toLowerCase().contains(query)
                        && !u.getEmail().toLowerCase().contains(query)) continue;

                HBox card = new HBox(12);
                card.setAlignment(Pos.CENTER_LEFT);
                card.setStyle("-fx-padding: 14; -fx-background-color: #111a2e; -fx-background-radius: 10; "
                        + "-fx-border-color: rgba(0,229,255,0.1); -fx-border-radius: 10;");

                Label avatar = new Label("\uD83D\uDC64");
                avatar.setStyle("-fx-font-size: 30px; -fx-background-color: #0a1628; "
                        + "-fx-padding: 6 10 6 10; -fx-background-radius: 50;");

                VBox info = new VBox(3);
                HBox.setHgrow(info, Priority.ALWAYS);
                Label nameLabel = new Label(displayName);
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #00e5ff;");
                Label roleLabel = new Label(u.getRole() + " • " + (u.getUniversity() != null ? u.getUniversity() : "N/A"));
                roleLabel.setStyle("-fx-text-fill: #5a6a7e; -fx-font-size: 11px;");
                Label emailLabel = new Label(u.getEmail());
                emailLabel.setStyle("-fx-text-fill: #4a5a6e; -fx-font-size: 11px;");
                info.getChildren().addAll(nameLabel, roleLabel, emailLabel);

                Button msgBtn = new Button("\uD83D\uDCAC Message");
                msgBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #00e5ff; "
                        + "-fx-border-color: rgba(0,229,255,0.3); -fx-border-radius: 8; -fx-background-radius: 8;");
                msgBtn.setOnAction(e -> showDirectChat(u.getEmail()));

                card.getChildren().addAll(avatar, info, msgBtn);
                userList.getChildren().add(card);
                count++;
            }
            if (count == 0) {
                Label noResult = new Label(query.isEmpty() ? "No other registered users yet." : "No users matching '" + query + "'.");
                noResult.setStyle("-fx-text-fill: #5a6a7e; -fx-padding: 20;");
                userList.getChildren().add(noResult);
            }
        };

        searchField.textProperty().addListener((obs, o, n) -> renderUsers.run());
        renderUsers.run();

        box.getChildren().addAll(title, searchField, new Separator(), userList);
        setScrollContent(box);
    }

    // ===================== PROFILE =====================
    @FXML
    private void showProfile() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("\uD83D\uDC64 My Profile");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        // Photo
        StackPane photoArea = new StackPane();
        photoArea.setMinSize(120, 120);
        photoArea.setMaxSize(120, 120);

        String photoUrl = DataStore.getProfilePhoto(Session.getIdentifier());
        if (photoUrl != null && !photoUrl.isEmpty()) {
            try {
                ImageView iv = new ImageView(new Image(photoUrl, 120, 120, true, true));
                iv.setFitWidth(120); iv.setFitHeight(120);
                Circle clip = new Circle(60, 60, 60);
                iv.setClip(clip);
                photoArea.getChildren().add(iv);
            } catch (Exception ex) {
                Label ph = new Label("\uD83D\uDC64"); ph.setStyle("-fx-font-size: 50px;");
                photoArea.getChildren().add(ph);
            }
        } else {
            Label ph = new Label("\uD83D\uDC64"); ph.setStyle("-fx-font-size: 50px;");
            photoArea.getChildren().add(ph);
        }

        Button uploadPhotoBtn = new Button("\uD83D\uDCF7 Upload Photo");
        uploadPhotoBtn.setStyle("-fx-background-color: #0d2a4a; -fx-text-fill: white; -fx-background-radius: 8;");
        uploadPhotoBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select Profile Photo");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            java.io.File file = fc.showOpenDialog(contentArea.getScene().getWindow());
            if (file != null) {
                DataStore.setProfilePhoto(Session.getIdentifier(), file.toURI().toString());
                showProfile();
            }
        });

        // Info
        Label nameLabel = new Label("Name: " + (Session.getName() != null ? Session.getName() : "N/A"));
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");
        Label roleLabel = new Label("Role: " + (Session.getRole() != null ? Session.getRole() : "N/A"));
        roleLabel.setStyle("-fx-font-size: 14px;");
        Label uniLabel = new Label("University: " + (Session.getUniversity() != null ? Session.getUniversity() : "N/A"));
        uniLabel.setStyle("-fx-font-size: 14px;");
        Label emailLabel = new Label("Identifier: " + Session.getIdentifier());
        emailLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7a8a9e;");

        String dept = Session.getDepartment();
        String desig = Session.getDesignation();
        Label deptLabel = new Label("Department: " + (dept != null ? dept : "N/A"));
        deptLabel.setStyle("-fx-font-size: 14px;");
        Label desigLabel = new Label("Designation: " + (desig != null ? desig : "N/A"));
        desigLabel.setStyle("-fx-font-size: 14px;");

        VBox profileCard = new VBox(10);
        profileCard.setMaxWidth(500);
        profileCard.setAlignment(Pos.CENTER);
        profileCard.setStyle("-fx-padding: 30; -fx-background-color: #111a2e; -fx-background-radius: 14; "
                + "-fx-border-color: rgba(0,229,255,0.15); -fx-border-radius: 14;");
        profileCard.getChildren().addAll(photoArea, uploadPhotoBtn,
                new Separator(), nameLabel, roleLabel, uniLabel, emailLabel,
                new Separator(), deptLabel, desigLabel);

        box.getChildren().addAll(title, profileCard);
        setScrollContent(box);
    }

    // ===================== NAVIGATION =====================
    @FXML private void goHome() { SceneManager.switchScene("home.fxml"); }
    @FXML private void goBack() { SceneManager.goBack(); }
    @FXML private void openNewWindow() { SceneManager.openNewWindow(); }
    @FXML private void signOut() { Session.logout(); SceneManager.switchScene("home.fxml"); }
}
