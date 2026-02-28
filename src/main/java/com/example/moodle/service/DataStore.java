package com.example.moodle.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.example.moodle.model.Assignment;
import com.example.moodle.model.Course;
import com.example.moodle.model.Message;
import com.example.moodle.model.Payment;

public class DataStore {

    private static final String COURSES_FILE = "courses.txt";
    private static final String ASSIGNMENTS_FILE = "assignments.txt";
    private static final String SUBMISSIONS_FILE = "submissions.txt";
    private static final String SLIDES_FILE = "slides.txt";
    private static final String MESSAGES_FILE = "messages.txt";
    private static final String PAYMENTS_FILE = "payments.txt";
    private static final String NOTICES_FILE = "course_notices.txt";
    private static final String COMMUNITY_FILE = "community_posts.txt";
    private static final String GROUPS_FILE = "groups.txt";
    private static final String GROUP_MESSAGES_FILE = "group_messages.txt";
    private static final String SCHEDULE_FILE = "schedule.txt";
    private static final String GRADES_FILE = "student_grades.txt";
    private static final String HOSPITAL_FILE = "hospital_appointments.txt";
    private static final String CAMPUS_PASS_FILE = "campus_passwords.txt";
    private static final String TEACHER_PROFILES_FILE = "teacher_profiles.txt";
    private static final String PHOTOS_FILE = "profile_photos.txt";
    private static final String GAMES_FILE = "games_data.txt";
    private static final String DOCTORS_FILE = "hospital_doctors.txt";
    private static final String HALLS_FILE = "hall_data.txt";

    static {
        seedDefaults();
    }

    private static void seedDefaults() {
        if (FileStore.loadLines(COURSES_FILE).isEmpty()) {
            addCourse(new Course("CSE101", "Intro to Programming", "faculty@campus", "Spring 2025"));
            addCourse(new Course("CSE203", "Data Structures", "faculty@campus", "Spring 2025"));
            addCourse(new Course("MATH201", "Calculus II", "faculty@campus", "Spring 2025"));
            addCourse(new Course("PHY101", "Physics I", "faculty@campus", "Spring 2025"));
            addCourse(new Course("ENG102", "English Composition", "faculty@campus", "Spring 2025"));
        }
        if (FileStore.loadLines(GAMES_FILE).isEmpty()) {
            FileStore.appendLine(GAMES_FILE, "Cricket|\uD83C\uDFCF|Sunday & Wednesday 4-6 PM|Main Ground");
            FileStore.appendLine(GAMES_FILE, "Football|\u26BD|Monday & Thursday 4-6 PM|Football Field");
            FileStore.appendLine(GAMES_FILE, "Badminton|\uD83C\uDFF8|Tuesday & Friday 5-7 PM|Indoor Court");
            FileStore.appendLine(GAMES_FILE, "Table Tennis|\uD83C\uDFD3|Daily 3-5 PM|Recreation Room");
            FileStore.appendLine(GAMES_FILE, "Chess|\u265F\uFE0F|Daily Open Hours|Common Room");
            FileStore.appendLine(GAMES_FILE, "Basketball|\uD83C\uDFC0|Saturday 9-11 AM|Basketball Court");
        }
        if (FileStore.loadLines(DOCTORS_FILE).isEmpty()) {
            FileStore.appendLine(DOCTORS_FILE, "Dr. Rahman|General Physician|Sun, Tue, Thu|9:00 AM - 1:00 PM");
            FileStore.appendLine(DOCTORS_FILE, "Dr. Fatima|Dentist|Mon, Wed|10:00 AM - 2:00 PM");
            FileStore.appendLine(DOCTORS_FILE, "Dr. Karim|Dermatologist|Tue, Thu|2:00 PM - 5:00 PM");
            FileStore.appendLine(DOCTORS_FILE, "Dr. Sultana|Psychiatrist|Mon, Wed, Fri|11:00 AM - 3:00 PM");
            FileStore.appendLine(DOCTORS_FILE, "Dr. Hasan|Ophthalmologist|Wed, Fri|9:00 AM - 12:00 PM");
        }
        if (FileStore.loadLines(HALLS_FILE).isEmpty()) {
            FileStore.appendLine(HALLS_FILE, "Shahid Smriti Hall|Room 101|20");
            FileStore.appendLine(HALLS_FILE, "Shahid Smriti Hall|Room 205|15");
            FileStore.appendLine(HALLS_FILE, "Titumir Hall|Room 312|18");
            FileStore.appendLine(HALLS_FILE, "Sher-e-Bangla Hall|Room 408|22");
            FileStore.appendLine(HALLS_FILE, "Kabi Nazrul Hall|Room 503|16");
            FileStore.appendLine(HALLS_FILE, "Bangamata Hall|Room 107|20");
        }
    }

    // ==================== COURSES ====================

    public static void addCourse(Course c) {
        FileStore.appendLine(COURSES_FILE,
                c.getCode() + "|" + c.getName() + "|" + c.getTeacherEmail() + "|"
                        + c.getSemester() + "|" + c.getTeacherName() + "|" + c.getBatch());
    }

    public static List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        for (String line : FileStore.loadLines(COURSES_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 6) {
                list.add(new Course(p[0], p[1], p[2], p[3], p[4], p[5]));
            } else if (p.length >= 4) {
                list.add(new Course(p[0], p[1], p[2], p[3]));
            }
        }
        return list;
    }

    public static List<Course> getCoursesByTeacher(String email) {
        List<Course> result = new ArrayList<>();
        for (Course c : getAllCourses()) {
            if (c.getTeacherEmail().equals(email)) result.add(c);
        }
        return result;
    }

    // ==================== ASSIGNMENTS ====================

    public static void addAssignment(Assignment a) {
        FileStore.appendLine(ASSIGNMENTS_FILE,
                a.getCourseCode() + "|" + a.getTitle() + "|" + a.getDescription() + "|" + a.getTeacherEmail());
    }

    public static List<Assignment> getAssignmentsForCourse(String courseCode) {
        List<Assignment> list = new ArrayList<>();
        for (String line : FileStore.loadLines(ASSIGNMENTS_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 4 && p[0].equals(courseCode))
                list.add(new Assignment(p[0], p[1], p[2], p[3]));
        }
        return list;
    }

    public static List<Assignment> getAllAssignments() {
        List<Assignment> list = new ArrayList<>();
        for (String line : FileStore.loadLines(ASSIGNMENTS_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 4) list.add(new Assignment(p[0], p[1], p[2], p[3]));
        }
        return list;
    }

    // ==================== SUBMISSIONS ====================
    // Format: studentId|courseCode|assignmentTitle|content|marks

    public static void submitAssignment(String studentId, String courseCode,
                                        String title, String content) {
        FileStore.appendLine(SUBMISSIONS_FILE,
                studentId + "|" + courseCode + "|" + title + "|" + content + "|pending");
    }

    public static List<String[]> getSubmissionsForAssignment(String courseCode, String title) {
        List<String[]> list = new ArrayList<>();
        for (String line : FileStore.loadLines(SUBMISSIONS_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 5 && p[1].equals(courseCode) && p[2].equals(title)) list.add(p);
        }
        return list;
    }

    public static List<String[]> getSubmissionsByStudent(String studentId) {
        List<String[]> list = new ArrayList<>();
        for (String line : FileStore.loadLines(SUBMISSIONS_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 5 && p[0].equals(studentId)) list.add(p);
        }
        return list;
    }

    public static void gradeSubmission(String studentId, String courseCode,
                                       String title, String marks) {
        List<String> lines = FileStore.loadLines(SUBMISSIONS_FILE);
        for (int i = 0; i < lines.size(); i++) {
            String[] p = lines.get(i).split("\\|", -1);
            if (p.length >= 5 && p[0].equals(studentId)
                    && p[1].equals(courseCode) && p[2].equals(title)) {
                lines.set(i, p[0] + "|" + p[1] + "|" + p[2] + "|" + p[3] + "|" + marks);
                break;
            }
        }
        FileStore.saveLines(SUBMISSIONS_FILE, lines);
    }

    // ==================== SLIDES ====================
    // Format: courseCode|title|description|teacherEmail

    public static void addSlide(String courseCode, String title,
                                String description, String teacherEmail) {
        FileStore.appendLine(SLIDES_FILE,
                courseCode + "|" + title + "|" + description + "|" + teacherEmail);
    }

    public static List<String[]> getSlidesForCourse(String courseCode) {
        List<String[]> list = new ArrayList<>();
        for (String line : FileStore.loadLines(SLIDES_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 4 && p[0].equals(courseCode)) list.add(p);
        }
        return list;
    }

    // ==================== MESSAGES ====================

    public static void sendMessage(String from, String to, String content) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        FileStore.appendLine(MESSAGES_FILE, from + "|" + to + "|" + content + "|" + ts);
    }

    public static List<Message> getMessagesFor(String email) {
        List<Message> list = new ArrayList<>();
        for (String line : FileStore.loadLines(MESSAGES_FILE)) {
            String[] p = line.split("\\|", 4);
            if (p.length >= 4 && (p[0].equals(email) || p[1].equals(email))) {
                list.add(new Message(p[0], p[1], p[2], p[3]));
            }
        }
        return list;
    }

    public static int getTotalMessageCount() {
        return FileStore.loadLines(MESSAGES_FILE).size();
    }

    // ==================== PAYMENTS ====================

    public static void makePayment(String studentEmail, String type, int amount) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        FileStore.appendLine(PAYMENTS_FILE,
                studentEmail + "|" + type + "|" + amount + "|" + date + "|Paid");
    }

    public static List<Payment> getPayments(String studentEmail) {
        List<Payment> list = new ArrayList<>();
        for (String line : FileStore.loadLines(PAYMENTS_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 5 && p[0].equals(studentEmail)) {
                try {
                    list.add(new Payment(p[0], p[1], Integer.parseInt(p[2]), p[3], p[4]));
                } catch (NumberFormatException ignored) { }
            }
        }
        return list;
    }

    public static List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        for (String line : FileStore.loadLines(PAYMENTS_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 5) {
                try {
                    list.add(new Payment(p[0], p[1], Integer.parseInt(p[2]), p[3], p[4]));
                } catch (NumberFormatException ignored) { }
            }
        }
        return list;
    }

    // ==================== COURSE NOTICES ====================
    // Format: courseCode|content|teacherEmail|date

    public static void addCourseNotice(String courseCode, String content, String teacherEmail) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        FileStore.appendLine(NOTICES_FILE,
                courseCode + "|" + content + "|" + teacherEmail + "|" + date);
    }

    public static List<String[]> getNoticesForCourse(String courseCode) {
        List<String[]> list = new ArrayList<>();
        for (String line : FileStore.loadLines(NOTICES_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 4 && p[0].equals(courseCode)) list.add(p);
        }
        return list;
    }

    public static List<String[]> getAllNotices() {
        List<String[]> list = new ArrayList<>();
        for (String line : FileStore.loadLines(NOTICES_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 4) list.add(p);
        }
        return list;
    }

    // ==================== STUDENTS COMMUNITY ====================
    // Format: authorId|authorName|content|timestamp

    public static void addCommunityPost(String authorId, String authorName, String content) {
        addCommunityPost(authorId, authorName, content, "");
    }

    public static List<String[]> getAllCommunityPosts() {
        List<String[]> list = new ArrayList<>();
        for (String line : FileStore.loadLines(COMMUNITY_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 4) list.add(p);
        }
        return list;
    }

    // ==================== GROUP MESSAGING ====================
    // groups.txt format: groupName|creatorId|password|member1,member2,...

    public static void createGroup(String groupName, String creatorId,
                                   String password, String members) {
        FileStore.appendLine(GROUPS_FILE,
                groupName + "|" + creatorId + "|" + password + "|" + members);
    }

    public static List<String[]> getAllGroups() {
        List<String[]> list = new ArrayList<>();
        for (String line : FileStore.loadLines(GROUPS_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 4) {
                list.add(p); // groupName|creatorId|password|members
            } else if (p.length == 3) {
                // Old format without password: groupName|creatorId|members
                list.add(new String[]{p[0], p[1], "", p[2]});
            }
        }
        return list;
    }

    public static List<String[]> getGroupsForUser(String userId) {
        List<String[]> result = new ArrayList<>();
        for (String[] g : getAllGroups()) {
            String members = "," + g[1] + "," + g[3] + ",";
            if (members.contains("," + userId + ",")) {
                result.add(g);
            }
        }
        return result;
    }

    public static boolean joinGroup(String groupName, String password, String userId) {
        List<String> lines = FileStore.loadLines(GROUPS_FILE);
        for (int i = 0; i < lines.size(); i++) {
            String[] p = lines.get(i).split("\\|", -1);
            String gName, gCreator, gPass, gMembers;
            if (p.length >= 4) {
                gName = p[0]; gCreator = p[1]; gPass = p[2]; gMembers = p[3];
            } else if (p.length == 3) {
                gName = p[0]; gCreator = p[1]; gPass = ""; gMembers = p[2];
            } else continue;

            if (gName.equals(groupName)) {
                if (!gPass.isEmpty() && !gPass.equals(password)) return false;
                if (("," + gCreator + "," + gMembers + ",").contains("," + userId + ","))
                    return true; // already a member
                gMembers = gMembers.isEmpty() ? userId : gMembers + "," + userId;
                lines.set(i, gName + "|" + gCreator + "|" + gPass + "|" + gMembers);
                FileStore.saveLines(GROUPS_FILE, lines);
                return true;
            }
        }
        return false;
    }

    public static List<String[]> searchGroups(String query) {
        List<String[]> result = new ArrayList<>();
        String q = query.toLowerCase();
        for (String[] g : getAllGroups()) {
            if (g[0].toLowerCase().contains(q)) result.add(g);
        }
        return result;
    }

    // group_messages.txt format: groupName|senderId|senderName|content|timestamp

    public static void addGroupMessage(String groupName, String senderId,
                                       String senderName, String content) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        FileStore.appendLine(GROUP_MESSAGES_FILE,
                groupName + "|" + senderId + "|" + senderName + "|" + content + "|" + ts);
    }

    public static List<String[]> getGroupMessages(String groupName) {
        List<String[]> list = new ArrayList<>();
        for (String line : FileStore.loadLines(GROUP_MESSAGES_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 5 && p[0].equals(groupName)) list.add(p);
        }
        return list;
    }

    // ==================== SCHEDULE ====================
    // Format: batch|day|time|courseCode|courseName

    public static void addScheduleEntry(String batch, String day, String time,
                                        String courseCode, String courseName) {
        FileStore.appendLine(SCHEDULE_FILE,
                batch + "|" + day + "|" + time + "|" + courseCode + "|" + courseName);
    }

    public static List<String[]> getScheduleForBatch(String batch) {
        List<String[]> list = new ArrayList<>();
        for (String line : FileStore.loadLines(SCHEDULE_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 5 && p[0].equals(batch)) list.add(p);
        }
        return list;
    }

    public static List<String[]> getAllSchedule() {
        List<String[]> list = new ArrayList<>();
        for (String line : FileStore.loadLines(SCHEDULE_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 5) list.add(p);
        }
        return list;
    }

    public static void removeScheduleEntry(String batch, String day, String time) {
        List<String> lines = FileStore.loadLines(SCHEDULE_FILE);
        lines.removeIf(l -> {
            String[] p = l.split("\\|", -1);
            return p.length >= 5 && p[0].equals(batch) && p[1].equals(day) && p[2].equals(time);
        });
        FileStore.saveLines(SCHEDULE_FILE, lines);
    }

    // ==================== STUDENT GRADES ====================
    // Format: studentId|courseCode|gradePoint

    public static void setGrade(String studentId, String courseCode, double gradePoint) {
        List<String> lines = FileStore.loadLines(GRADES_FILE);
        boolean found = false;
        for (int i = 0; i < lines.size(); i++) {
            String[] p = lines.get(i).split("\\|", -1);
            if (p.length >= 3 && p[0].equals(studentId) && p[1].equals(courseCode)) {
                lines.set(i, studentId + "|" + courseCode + "|" + gradePoint);
                found = true;
                break;
            }
        }
        if (found) {
            FileStore.saveLines(GRADES_FILE, lines);
        } else {
            FileStore.appendLine(GRADES_FILE, studentId + "|" + courseCode + "|" + gradePoint);
        }
    }

    public static List<String[]> getGradesForStudent(String studentId) {
        List<String[]> list = new ArrayList<>();
        for (String line : FileStore.loadLines(GRADES_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 3 && p[0].equals(studentId)) list.add(p);
        }
        return list;
    }

    // ==================== HOSPITAL ====================
    // Format: studentId|doctorName|date|time|reason|status

    public static void bookAppointment(String studentId, String doctor,
                                       String date, String time, String reason) {
        FileStore.appendLine(HOSPITAL_FILE,
                studentId + "|" + doctor + "|" + date + "|" + time + "|" + reason + "|Booked");
    }

    public static List<String[]> getAppointmentsFor(String studentId) {
        List<String[]> list = new ArrayList<>();
        for (String line : FileStore.loadLines(HOSPITAL_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 6 && p[0].equals(studentId)) list.add(p);
        }
        return list;
    }

    public static List<String[]> getAllAppointments() {
        List<String[]> list = new ArrayList<>();
        for (String line : FileStore.loadLines(HOSPITAL_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 6) list.add(p);
        }
        return list;
    }

    // ==================== CAMPUS PASSWORDS ====================
    // Format: identifier|password

    public static String getCampusPassword(String identifier) {
        for (String line : FileStore.loadLines(CAMPUS_PASS_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 2 && p[0].equals(identifier)) return p[1];
        }
        return null;
    }

    public static void setCampusPassword(String identifier, String password) {
        List<String> lines = FileStore.loadLines(CAMPUS_PASS_FILE);
        boolean found = false;
        for (int i = 0; i < lines.size(); i++) {
            String[] p = lines.get(i).split("\\|", -1);
            if (p.length >= 2 && p[0].equals(identifier)) {
                lines.set(i, identifier + "|" + password);
                found = true;
                break;
            }
        }
        if (found) {
            FileStore.saveLines(CAMPUS_PASS_FILE, lines);
        } else {
            FileStore.appendLine(CAMPUS_PASS_FILE, identifier + "|" + password);
        }
    }

    // ==================== TEACHER PROFILES ====================
    // Format: name|dept|designation|type|password

    public static void saveTeacherProfile(String name, String dept,
                                          String designation, String type, String password) {
        List<String> lines = FileStore.loadLines(TEACHER_PROFILES_FILE);
        boolean found = false;
        for (int i = 0; i < lines.size(); i++) {
            String[] p = lines.get(i).split("\\|", -1);
            if (p.length >= 5 && p[0].equals(name) && p[1].equals(dept)) {
                lines.set(i, name + "|" + dept + "|" + designation + "|" + type + "|" + password);
                found = true;
                break;
            }
        }
        if (found) {
            FileStore.saveLines(TEACHER_PROFILES_FILE, lines);
        } else {
            FileStore.appendLine(TEACHER_PROFILES_FILE,
                    name + "|" + dept + "|" + designation + "|" + type + "|" + password);
        }
    }

    public static String[] getTeacherProfile(String name, String dept) {
        for (String line : FileStore.loadLines(TEACHER_PROFILES_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 5 && p[0].equals(name) && p[1].equals(dept)) return p;
        }
        return null;
    }

    // ==================== PROFILE PHOTOS ====================
    // Format: userId|photoPath

    public static String getProfilePhoto(String userId) {
        for (String line : FileStore.loadLines(PHOTOS_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 2 && p[0].equals(userId)) return p[1];
        }
        return null;
    }

    public static void setProfilePhoto(String userId, String photoPath) {
        List<String> lines = FileStore.loadLines(PHOTOS_FILE);
        boolean found = false;
        for (int i = 0; i < lines.size(); i++) {
            String[] p = lines.get(i).split("\\|", -1);
            if (p.length >= 2 && p[0].equals(userId)) {
                lines.set(i, userId + "|" + photoPath);
                found = true;
                break;
            }
        }
        if (found) {
            FileStore.saveLines(PHOTOS_FILE, lines);
        } else {
            FileStore.appendLine(PHOTOS_FILE, userId + "|" + photoPath);
        }
    }

    // ==================== ADMIN-MANAGED: GAMES ====================
    // Format: sport|emoji|schedule|venue

    public static void addGame(String sport, String emoji, String schedule, String venue) {
        FileStore.appendLine(GAMES_FILE, sport + "|" + emoji + "|" + schedule + "|" + venue);
    }

    public static List<String[]> getAllGames() {
        List<String[]> list = new ArrayList<>();
        for (String line : FileStore.loadLines(GAMES_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 4) list.add(p);
        }
        return list;
    }

    public static void removeGame(String sport) {
        List<String> lines = FileStore.loadLines(GAMES_FILE);
        lines.removeIf(l -> l.split("\\|", -1)[0].equals(sport));
        FileStore.saveLines(GAMES_FILE, lines);
    }

    // ==================== ADMIN-MANAGED: HOSPITAL DOCTORS ====================
    // Format: name|specialization|availableDays|hours

    public static void addDoctor(String name, String spec, String days, String hours) {
        FileStore.appendLine(DOCTORS_FILE, name + "|" + spec + "|" + days + "|" + hours);
    }

    public static List<String[]> getAllDoctors() {
        List<String[]> list = new ArrayList<>();
        for (String line : FileStore.loadLines(DOCTORS_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 4) list.add(p);
        }
        return list;
    }

    public static void removeDoctor(String name) {
        List<String> lines = FileStore.loadLines(DOCTORS_FILE);
        lines.removeIf(l -> l.split("\\|", -1)[0].equals(name));
        FileStore.saveLines(DOCTORS_FILE, lines);
    }

    // ==================== ADMIN-MANAGED: HALL ROOMS ====================
    // Format: hallName|roomNumber|capacity

    public static void addHallRoom(String hallName, String room, String capacity) {
        FileStore.appendLine(HALLS_FILE, hallName + "|" + room + "|" + capacity);
    }

    public static List<String[]> getAllHallRooms() {
        List<String[]> list = new ArrayList<>();
        for (String line : FileStore.loadLines(HALLS_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 3) list.add(p);
        }
        return list;
    }

    public static void removeHallRoom(String hallName, String room) {
        List<String> lines = FileStore.loadLines(HALLS_FILE);
        lines.removeIf(l -> {
            String[] p = l.split("\\|", -1);
            return p.length >= 2 && p[0].equals(hallName) && p[1].equals(room);
        });
        FileStore.saveLines(HALLS_FILE, lines);
    }

    // ==================== COMMUNITY (with images) ====================

    public static void addCommunityPost(String authorId, String authorName,
                                        String content, String imagePath) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        FileStore.appendLine(COMMUNITY_FILE,
                authorId + "|" + authorName + "|" + content + "|" + ts + "|"
                        + (imagePath != null ? imagePath : ""));
    }
}
