# Unimate - Complete University Digital Platform

> **L2T1 JavaFX Project** | Department of CSE, BUET

A complete desktop platform that simulates university life in one application: student and teacher workflows, authority administration, university information pages, community interaction, messaging, and peer-to-peer networking, built with **JavaFX** and **FXML**.

---

## Download From GitHub Release (v1.0.0)

Release page:

https://github.com/mdraihankabirsifat/Moodle-2.0/releases/tag/v1.0.0

### Windows (prebuilt executable)
1. Open the release link above.
2. In **Assets**, download: `Unimate-Windows-AppImage-2026-04-10.zip`.
3. Extract the zip file.
4. Open the extracted folder `Unimate`.
5. Run `Unimate.exe`.

### If you want source instead of prebuilt app
1. From the same release page, download **Source code (zip)** or **Source code (tar.gz)**.
2. Extract it.
3. Run with Maven using the manual setup commands below.

---

## Features (Complete)

### 1. Authentication and Access Control
- Student signup/login with validation, duplicate-email protection, and profile session creation.
- Teacher signup/login with university, department, designation, and type (Faculty/Guest).
- Campus Access verification for three roles: **Student**, **Teacher**, and **Authority**.
- Separate campus passwords for student and teacher entry (changeable from profile sections).
- Authority login scoped by selected university.
- Login activity logging and real-time notification creation.

### 2. Home and Discovery
- Live university search with dynamic suggestions.
- University page launcher from search results.
- Theme switching (Dark/Light).
- Notification bell with unread count and read-state behavior.
- Activity heatmap tracker with range filters: **Last 7 Days**, **Last 1 Month**, **Last 1 Year**.
- Dedicated news/vlog article routing from home cards.
- Community and campus navigation shortcuts from the top-level home flow.

### 3. Student Dashboard (My Campus)
- **My Profile**: photo upload, account identity view, and campus password update.
- **Internal Notices**: course and campus notice feed with PDF-aware content support.
- **My Courses**:
  - course list filtered by student batch.
  - assignment viewing and submission.
  - slide/resource viewing.
  - course notice viewing.
- **Assignment Submission**: text plus file attachment support and marks visibility.
- **Gradesheet**: subject-wise grade points, letter grades, and CGPA summary.
- **Schedule**: weekly class timetable grid by batch.
- **Payment**: Hall, Exam, Semester, Library, and Lab fee payment with history and totals.
- **Messages**:
  - direct chat by student ID or email.
  - group create/join (optional password).
  - live auto-refresh conversation layout.
- **Well Being Hub**:
  - Hall Management (availability requests + allocation status).
  - Games and Sports registration.
  - Hospital module (doctor list, appointment booking, tests, medicine info, appointment list).
  - Washing machine slot booking.
  - Vending machine wallet, recharge, and purchase simulation.
- **Community Feed**: post text/image content, auto-refresh feed, and direct-message shortcut from posts.

### 4. Teacher Dashboard
- **My Profile** with photo upload and campus password change.
- **My Courses** creation and listing with semester/batch metadata.
- **Upload Assignment** with optional file attachment.
- **Upload Slides** with optional file attachment.
- **Post Course Notice** with optional PDF attachment.
- **Evaluate Submissions** with mark assignment and auto-refresh.
- **Student List** view.
- **Edit Schedule** by batch in grid form (add/remove class entries per slot).
- **Hospital Access** (doctor table + appointment booking workflow).
- **Messages** (direct + group chat layout with live refresh).
- **Live Chat** dedicated direct messaging panel.

### 5. Authority Dashboard (Admin Control)
- **Student Database** with search/filter and inline editing.
- **Manage Notices** with optional PDF attachment.
- **Payment Overview** reconciliation panel.
- **Edit Gradesheet** administrative controls.
- **System Overview** statistics panel.
- **Campus Facilities Management**:
  - Manage Games.
  - Manage Hospital doctors.
  - Manage Hall rooms, requests, and allocations.
- **Manage Users** module.
- **University Page Management** modules:
  - Manage Faculty.
  - Manage Alumni.
  - University Notices.
  - Latest News.
  - Upcoming Events.
  - Job Notices.
  - Staff Details.
  - Department Details.
  - Institution Details.
  - Administration.

### 6. University Page and Community Module
- University page sections: **About**, **Departments**, **Institutes**, **Faculty/Staff**, **Alumni**, **Administration**.
- Dynamic content pulls from authority-managed records.
- Standalone Community Dashboard with universal feed, direct chat, group chat, friend discovery, and profile panel.

### 7. Messaging and Networking
- Local persistent messaging for direct and group channels.
- Alias-safe direct message identity matching (student ID/email support).
- Cross-device direct messaging via peer-to-peer bridge.
- Home network panel for:
  - server address input.
  - connect/disconnect controls.
  - connection status.
  - local IP/port hint.
- Default bridge server port: `50555` (auto-fallback range enabled).

### 8. Data and Persistence
- File-based persistence in `~/.moodle_data/`.
- Backward-compatible attachment markers (`[FILE:...]` and legacy `[PDF:...]`).
- Persisted modules include users, courses, assignments, submissions, slides, notices, payments, schedule, hospital, hall, games, community posts, groups, messages, notifications, and activity history.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 25 |
| **UI Framework** | JavaFX 21.0.6 (Controls + FXML) |
| **System APIs** | Desktop, File I/O, Local Persistence |
| **Build Tool** | Apache Maven 3.8+ |
| **Architecture** | MVC Pattern (Model-View-Controller) |
| **Data Storage** | Pipe-delimited persistent files in `~/.moodle_data/` |

---

## Project Setup and Quick Start

### Automated Installer Scripts

#### Windows
```bat
windows-installer.bat
```
This installs/updates to `~/.moodle-app-v2` and launches via Maven.

#### Mac/Linux
```bash
chmod +x mac-installer.sh
./mac-installer.sh
```
This installs/updates to `~/.moodle-app-v2` and launches via Maven.

### Manual Setup (All Environments)
1. **Compile**
   ```bash
   ./mvnw clean compile        # Linux/Mac
   mvnw.cmd clean compile      # Windows
   ```

2. **Run**
   ```bash
   ./mvnw javafx:run           # Linux/Mac
   mvnw.cmd javafx:run         # Windows
   ```

3. **Default Access Hints**
   - Student campus pass (default): `1234`
   - Teacher campus pass (default): `teacher123`
   - Authority pass (default): `admin123`

---

## Data Directory Structure

All local state and content are persisted under:

```text
~/.moodle_data/
├── users.txt
├── teacher_profiles.txt
├── campus_passwords.txt
├── courses.txt
├── assignments.txt
├── submissions.txt
├── slides.txt
├── course_notices.txt
├── messages.txt
├── groups.txt
├── group_messages.txt
├── payments.txt
├── schedule.txt
├── student_grades.txt
├── hospital_doctors.txt
├── hospital_appointments.txt
├── hall_data.txt
├── hall_room_requests.txt
├── hall_allocations.txt
├── games_data.txt
├── community_posts.txt
├── notifications.txt
├── student_activity.txt
├── profile_photos.txt
└── admin/
    ├── staff.txt
    ├── departments.txt
    ├── alumni.txt
    ├── uni_details.txt
    └── ...
```

To hard-reset behavior, clear the contents of `~/.moodle_data/`.

---

## Author and Acknowledgements

**Developed By:**

Md. Raihan Kabir Sifat  
Department of Computer Science and Engineering  
Bangladesh University of Engineering and Technology (BUET)  
Level 2, Term 1

Under the suprviosion of:  
Md. Nurul Muttakin   
Lecturer, CSE  
BUET  

*Rights reserved for academic showcase.*
