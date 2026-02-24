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
    }

    // ==================== COURSES ====================

    public static void addCourse(Course c) {
        FileStore.appendLine(COURSES_FILE,
                c.getCode() + "|" + c.getName() + "|" + c.getTeacherEmail() + "|" + c.getSemester());
    }

    public static List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        for (String line : FileStore.loadLines(COURSES_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 4) list.add(new Course(p[0], p[1], p[2], p[3]));
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
            String[] p = line.split("\\|", -1);
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
}
