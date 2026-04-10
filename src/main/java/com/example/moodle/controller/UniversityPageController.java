package com.example.moodle.controller;

import com.example.moodle.model.UniversityInfo;
import com.example.moodle.service.DataStore;
import com.example.moodle.util.SceneManager;
import com.example.moodle.util.Session;
import com.example.moodle.util.UniversityDatabase;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class UniversityPageController {

    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        showAbout();
    }

    private void setScrollContent(VBox content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        contentArea.getChildren().setAll(scroll);
    }

    @FXML
    private void showAbout() {
        String uniName = Session.getSelectedUniversity();
        if (uniName == null) return;

        UniversityInfo info = UniversityDatabase.getUniversity(uniName);
        if (info == null) return;

        VBox box = new VBox(20);
        box.setPadding(new Insets(10));

        Label title = new Label(info.getName() + " (" + info.getShortName() + ")");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");

        // Institution details from admin
        String instName = DataStore.getInstitutionDetail(getResolvedUniversityName(), "Institution Name");
        String instAddress = DataStore.getInstitutionDetail(getResolvedUniversityName(), "Address");
        String instEstablished = DataStore.getInstitutionDetail(getResolvedUniversityName(), "Established");
        String instVc = DataStore.getInstitutionDetail(getResolvedUniversityName(), "Vice Chancellor");
        String instMotto = DataStore.getInstitutionDetail(getResolvedUniversityName(), "Motto");

        VBox aboutBox = new VBox(12);
        aboutBox.setStyle("-fx-padding: 20; -fx-background-color: #111a2e; -fx-background-radius: 10; -fx-border-color: rgba(0,229,255,0.2); -fx-border-radius: 10;");
        Label aTitle = new Label("\uD83C\uDFEB  About");
        aTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #00ff88;");
        aboutBox.getChildren().add(aTitle);

        if (!instName.isEmpty()) aboutBox.getChildren().add(new Label("Institution: " + instName));
        if (!instAddress.isEmpty()) aboutBox.getChildren().add(new Label("Address: " + instAddress));
        if (!instEstablished.isEmpty()) aboutBox.getChildren().add(new Label("Established: " + instEstablished));
        if (!instVc.isEmpty()) aboutBox.getChildren().add(new Label("Vice Chancellor: " + instVc));
        if (!instMotto.isEmpty()) aboutBox.getChildren().add(new Label("Motto: " + instMotto));

        VBox contactBox = new VBox(12);
        contactBox.setStyle("-fx-padding: 20; -fx-background-color: #111a2e; -fx-background-radius: 10; -fx-border-color: rgba(0,229,255,0.2); -fx-border-radius: 10;");
        Label cTitle = new Label("\uD83D\uDCDE  Contact Information");
        cTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #00ff88;");
        contactBox.getChildren().addAll(cTitle,
            new Label("Phone: " + info.getPhone()),
            new Label("Email: " + info.getEmail()),
            new Label("Website: " + info.getWebsite()));

        VBox locationBox = new VBox(12);
        locationBox.setStyle("-fx-padding: 20; -fx-background-color: #111a2e; -fx-background-radius: 10; -fx-border-color: rgba(0,229,255,0.2); -fx-border-radius: 10;");
        Label lTitle = new Label("\uD83D\uDCCD  Location");
        lTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #00ff88;");
        Label locBody = new Label(info.getLocation());
        locBody.setWrapText(true);
        locationBox.getChildren().addAll(lTitle, locBody);

        VBox noticesBox = new VBox(12);
        noticesBox.setStyle("-fx-padding: 20; -fx-background-color: #111a2e; -fx-background-radius: 10; -fx-border-color: #ffb300; -fx-border-radius: 10;");
        Label nTitle = new Label("\uD83D\uDCE2  Global Notices");
        nTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffb300;");
        noticesBox.getChildren().add(nTitle);
        for (int i = 0; i < info.getNotices().size(); i++) {
            Label notice = new Label((i + 1) + ".  " + info.getNotices().get(i));
            notice.setWrapText(true);
            notice.setStyle("-fx-padding: 8 12; -fx-background-color: #0d1b2a; -fx-background-radius: 8;");
            noticesBox.getChildren().add(notice);
        }

        // Also show admin-posted university notices
        // Admin-posted university notices
        List<String[]> adminNotices = DataStore.getAllUniversityNotices(getResolvedUniversityName());
        if (!adminNotices.isEmpty()) {
            for (String[] n : adminNotices) {
                VBox card = new VBox(4);
                card.setStyle("-fx-padding: 8 12; -fx-background-color: #0d1b2a; -fx-background-radius: 8;");
                Label tl = new Label("\uD83D\uDCCC " + n[0]);
                tl.setStyle("-fx-font-weight: bold; -fx-text-fill: #ffb300;");
                Label cl = new Label(n[1]); cl.setWrapText(true);
                Label dl = new Label(n[3]); dl.setStyle("-fx-text-fill: #5a6a7e; -fx-font-size: 11px;");
                card.getChildren().addAll(tl, cl, dl);
                noticesBox.getChildren().add(card);
            }
        }

        // ============ LATEST NEWS ============
        VBox newsBox = new VBox(12);
        newsBox.setStyle("-fx-padding: 20; -fx-background-color: #111a2e; -fx-background-radius: 10; -fx-border-color: #00e5ff; -fx-border-radius: 10;");
        Label newsTitle = new Label("\uD83D\uDCF0  Latest News");
        newsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");
        newsBox.getChildren().add(newsTitle);
        List<String[]> news = DataStore.getAllNews(getResolvedUniversityName());
        if (news.isEmpty()) {
            newsBox.getChildren().add(new Label("No recent news."));
        } else {
            for (String[] n : news) {
                VBox card = new VBox(4);
                card.setStyle("-fx-padding: 8 12; -fx-background-color: #0d1b2a; -fx-background-radius: 8;");
                Label hl = new Label(n[0]); hl.setStyle("-fx-font-weight: bold; -fx-text-fill: #00ff88;");
                Label cl = new Label(n[1]); cl.setWrapText(true);
                Label tl = new Label(n[2]); tl.setStyle("-fx-text-fill: #5a6a7e; -fx-font-size: 10px;");
                card.getChildren().addAll(hl, cl, tl);
                newsBox.getChildren().add(card);
            }
        }

        // ============ UPCOMING EVENTS ============
        VBox eventsBox = new VBox(12);
        eventsBox.setStyle("-fx-padding: 20; -fx-background-color: #111a2e; -fx-background-radius: 10; -fx-border-color: #ff3366; -fx-border-radius: 10;");
        Label eventsTitle = new Label("\uD83D\uDCC5  Upcoming Events");
        eventsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ff3366;");
        eventsBox.getChildren().add(eventsTitle);
        List<String[]> events = DataStore.getAllEvents(getResolvedUniversityName());
        if (events.isEmpty()) {
            eventsBox.getChildren().add(new Label("No upcoming events."));
        } else {
            for (String[] e : events) {
                VBox card = new VBox(4);
                card.setStyle("-fx-padding: 8 12; -fx-background-color: #0d1b2a; -fx-background-radius: 8;");
                Label tl = new Label(e[0]); tl.setStyle("-fx-font-weight: bold; -fx-text-fill: #ff3366;");
                Label dl = new Label(e[1]); dl.setStyle("-fx-text-fill: #8a9ab0; -fx-font-weight: bold;");
                Label desc = new Label(e.length > 2 ? e[2] : ""); desc.setWrapText(true);
                card.getChildren().addAll(tl, dl, desc);
                eventsBox.getChildren().add(card);
            }
        }

        box.getChildren().addAll(title, aboutBox, newsBox, eventsBox, contactBox, locationBox, noticesBox);
        setScrollContent(box);
    }

    @FXML
    private void showDepartments() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        Label title = new Label("\uD83C\uDFDB Departments");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");
        box.getChildren().add(title);

        List<String[]> depts = DataStore.getAllDepartments(getResolvedUniversityName());
        if (depts.isEmpty()) {
            box.getChildren().add(new Label("Department information will be available here."));
        } else {
            for (String[] d : depts) {
                VBox card = new VBox(6);
                card.setStyle("-fx-padding: 16; -fx-background-color: #111a2e; -fx-background-radius: 10; -fx-border-color: rgba(0,229,255,0.15); -fx-border-radius: 10;");
                Label name = new Label(d[0]);
                name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");
                Label head = new Label("Head: " + d[1]);
                head.setStyle("-fx-font-size: 13px; -fx-text-fill: #00ff88;");
                Label count = new Label("Faculty Members: " + d[2]);
                Label desc = new Label(d[3]);
                desc.setWrapText(true);
                desc.setStyle("-fx-text-fill: #8a9ab0;");
                card.getChildren().addAll(name, head, count, desc);
                box.getChildren().add(card);
            }
        }
        setScrollContent(box);
    }

    @FXML
    private void showInstitutes() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        Label title = new Label("\uD83C\uDFEB Institutes");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");
        box.getChildren().add(title);

        // Show institution details from admin panel
        List<String[]> details = DataStore.getAllInstitutionDetails(getResolvedUniversityName());
        if (details.isEmpty()) {
            box.getChildren().add(new Label("Institute information will be available here."));
        } else {
            VBox card = new VBox(10);
            card.setStyle("-fx-padding: 20; -fx-background-color: #111a2e; -fx-background-radius: 10; -fx-border-color: rgba(0,229,255,0.15); -fx-border-radius: 10;");
            for (String[] d : details) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                Label key = new Label(d[0] + ":");
                key.setStyle("-fx-font-weight: bold; -fx-min-width: 150; -fx-text-fill: #00e5ff;");
                Label val = new Label(d[1]);
                val.setWrapText(true);
                row.getChildren().addAll(key, val);
                card.getChildren().add(row);
            }
            box.getChildren().add(card);
        }
        setScrollContent(box);
    }

    @FXML
    private void showFacultyStaff() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        Label title = new Label("\uD83D\uDC68\u200D\uD83C\uDFEB Faculty & Staff");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");
        box.getChildren().add(title);

        // Faculty Members
        Label facTitle = new Label("Faculty Members");
        facTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffb300; -fx-padding: 10 0 4 0;");
        box.getChildren().add(facTitle);

        List<String[]> faculty = DataStore.getAllFacultyMembers(getResolvedUniversityName());
        if (faculty.isEmpty()) {
            box.getChildren().add(new Label("No faculty members listed."));
        } else {
            for (String[] f : faculty) {
                VBox card = new VBox(4);
                card.setStyle("-fx-padding: 12; -fx-background-color: #111a2e; -fx-background-radius: 8;");
                Label name = new Label(f[0]);
                name.setStyle("-fx-font-weight: bold; -fx-text-fill: #00e5ff;");
                Label detail = new Label(f[2] + " | " + f[1]);
                Label contact = new Label("Email: " + f[3] + " | Phone: " + f[4]);
                contact.setStyle("-fx-text-fill: #5a6a7e; -fx-font-size: 11px;");
                card.getChildren().addAll(name, detail, contact);
                box.getChildren().add(card);
            }
        }

        box.getChildren().add(new Separator());

        // Staff Members
        Label staffTitle = new Label("Staff Members");
        staffTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffb300; -fx-padding: 10 0 4 0;");
        box.getChildren().add(staffTitle);

        List<String[]> staff = DataStore.getAllStaffMembers(getResolvedUniversityName());
        if (staff.isEmpty()) {
            box.getChildren().add(new Label("No staff members listed."));
        } else {
            for (String[] s : staff) {
                VBox card = new VBox(4);
                card.setStyle("-fx-padding: 12; -fx-background-color: #111a2e; -fx-background-radius: 8;");
                Label name = new Label(s[0]);
                name.setStyle("-fx-font-weight: bold; -fx-text-fill: #00e5ff;");
                Label detail = new Label(s[1] + " | " + s[2]);
                Label contact = new Label("Phone: " + s[3] + " | Email: " + s[4]);
                contact.setStyle("-fx-text-fill: #5a6a7e; -fx-font-size: 11px;");
                card.getChildren().addAll(name, detail, contact);
                box.getChildren().add(card);
            }
        }
        setScrollContent(box);
    }

    @FXML
    private void showAlumni() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        Label title = new Label("\uD83C\uDF93 Alumni");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");
        box.getChildren().add(title);

        List<String[]> alumni = DataStore.getAllAlumni(getResolvedUniversityName());
        if (alumni.isEmpty()) {
            box.getChildren().add(new Label("No alumni records available."));
        } else {
            for (String[] a : alumni) {
                VBox card = new VBox(4);
                card.setStyle("-fx-padding: 12; -fx-background-color: #111a2e; -fx-background-radius: 8;");
                Label name = new Label(a[0]);
                name.setStyle("-fx-font-weight: bold; -fx-text-fill: #00e5ff;");
                Label batch = new Label("Batch: " + a[1] + " | Dept: " + a[2]);
                Label pos = new Label("Current Position: " + a[3]);
                pos.setStyle("-fx-text-fill: #00ff88;");
                card.getChildren().addAll(name, batch, pos);
                box.getChildren().add(card);
            }
        }
        setScrollContent(box);
    }

    @FXML
    private void showAdministration() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        Label title = new Label("\u2699 Administration");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00e5ff;");
        box.getChildren().add(title);

        List<String[]> admins = DataStore.getAllAdministration(getResolvedUniversityName());
        if (admins.isEmpty()) {
            box.getChildren().add(new Label("No administration details available."));
        } else {
            for (String[] a : admins) {
                VBox card = new VBox(4);
                card.setStyle("-fx-padding: 12; -fx-background-color: #111a2e; -fx-background-radius: 8;");
                Label name = new Label(a[0]);
                name.setStyle("-fx-font-weight: bold; -fx-text-fill: #00e5ff; -fx-font-size: 15px;");
                Label pos = new Label(a[1] + " | " + a[2]);
                pos.setStyle("-fx-text-fill: #00ff88;");
                Label contact = new Label("Phone: " + a[3] + " | Email: " + a[4]);
                contact.setStyle("-fx-text-fill: #5a6a7e; -fx-font-size: 11px;");
                card.getChildren().addAll(name, pos, contact);
                box.getChildren().add(card);
            }
        }
        setScrollContent(box);
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
    private void goBack() {
        SceneManager.goBack();
    }

    private String getResolvedUniversityName() {
        String uniName = Session.getSelectedUniversity();
        if (uniName == null) return "";
        UniversityInfo info = UniversityDatabase.getUniversity(uniName);
        return info != null ? info.getShortName() : uniName;
    }
}
