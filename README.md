# Unimate – Complete University Digital Platform

> **L2T1 JavaFX Project** | Department of CSE, BUET

A unified, premium desktop application that simulates and manages university campus life — from student enrollment and course management to P2P networking and comprehensive administrative control — all built with **JavaFX** and **FXML**.

---

## Features

### 🔒 Advanced Authentication System
- Multi-role registration (Student, Teacher, Authority) with dynamic dashboards.
- Dedicated Campus Access module with secondary passwords for security context.
- Profile management with circular photo uploads and credential updates.
- Real-time Push Notifications upon successful login.

### 📘 Student MyCampus Dashboard
- **Split-View Notices:** Interactive notice reader with inline PDF viewing integration.
- **Gradesheet:** Comprehensive GPA tracking and letter grade evaluation.
- **Schedule:** Interactive weekly timeline management algorithm logic.
- **Community:** Embedded modules for Vending, Washing Machine, and Sports booking.
- **Payments:** Seamless interface for paying Hall, Lab, Semester, Exam, and Library fees.

### 🎓 Teacher Portal
- **Notice Publishing:** Complete Course notice publisher with quick PDF capabilities.
- **Assignment & Resources:** Easy interface for managing, uploading slides, and grading submission.
- **Chat & Messaging:** Dedicated module for group and P2P connection channels.
- **Manage Course Offerings:** Comprehensive listing of available and currently registered course spaces.

### ⚙️ Admin Control Panel
- **Student Database Engine:** Embedded real-time filterable grid view with inline CRUD capabilities.
- **University Management Sub-sections:** Separate sub-dashboards to manage Institution Details, Alumni, Staff, Faculty, Jobs, and Global Notices.
- **Payment Overview System:** Comprehensive tracking logs for financial reconciliation.

### 🏛 Dynamic University Interface
- **Sidebar Organization:** Clean layout wrapping About, Departments, Institutes, Faculty, Alumni, and Administration.
- **Data-Driven Flow:** Pulls live data from the Admin database dynamically rendering pages.
- **Live Search:** Start-page university search linking matching organizations.

### 🌐 Peer-to-Peer Networking
- Multi-threaded Java ServerSockets handle active P2P client interaction straight from the Application header.
- Status toggle indicators visible in real-time.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 25 |
| **UI Framework** | JavaFX 21.0.6 (Controls + FXML) |
| **System APIs** | Desktop, File I/O for PDF handling and Persistence |
| **Build Tool** | Apache Maven 3.8+ |
| **Architecture** | MVC Pattern (Model-View-Controller) |
| **Data Storage** | Pipe-delimited Persistent Files (`~/.moodle_data/`) |

---

## Project Setup & Quick Start

### For Mac Users (Automated Installer)
We provide a dedicated script that automatically clones, updates, and launches the application without needing manual setup.
1. Download the `mac-installer.sh` script or navigate to the repository directory.
2. Make it executable and run:
   ```bash
   chmod +x mac-installer.sh
   ./mac-installer.sh
   ```
This will install or update the app to `~/.moodle-app-v2` and launch it automatically.

### Manual Setup (All Environments)
1. **Clone & Compile**
   ```bash
   ./mvnw clean compile        # Linux/Mac
   mvnw.cmd clean compile      # Windows
   ```

2. **Run**
   ```bash
   ./mvnw javafx:run            # Linux/Mac
   mvnw.cmd javafx:run          # Windows
   ```

3. **Try It Out**
   - **Student Access:** Register an account, log in, go to `My Campus` and sign into Student Dashboard.
   - **Teacher Access:** Go to My Campus logic panel → switch Role → teacher email context. (Hint default pass: `teacher123`)
   - **Authority Access:** Switch Role → Authority. (Default pass: `admin123`)

---

## Data Structure Directory

All configuration states and user content are synced via local txt storage parsing across instances.
```
~/.moodle_data/
├── users.txt
├── courses.txt
├── profile_photos/
├── notifications.txt
└── admin/                 [NEW Authority Store]
    ├── staff.txt
    ├── departments.txt
    ├── alumni.txt
    ├── uni_details.txt
    └── ...
```
To hard-reset system behaviors, purge the inner contents of `~/.moodle_data/`.

---

## Author & Acknowledgements

**Developed By:**
Raihan Kabir
Department of Computer Science and Engineering
Bangladesh University of Engineering and Technology (BUET)
Level 2, Term 1 — 2024/2026

*Rights Reserved for Academic Showcase.*
