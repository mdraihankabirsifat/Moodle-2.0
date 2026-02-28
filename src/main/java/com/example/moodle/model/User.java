package com.example.moodle.model;

public class User {

    private String name;
    private String university;
    private String studentId;
    private String email;
    private String password;
    private String role; // STUDENT, TEACHER, AUTHORITY

    public User(String name, String university,
                String studentId, String email, String password) {
        this.name = name;
        this.university = university;
        this.studentId = studentId;
        this.email = email;
        this.password = password;
        this.role = "STUDENT";
    }

    public String getName() { return name; }
    public String getUniversity() { return university; }
    public String getStudentId() { return studentId; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role != null ? role : "STUDENT"; }

    public void setName(String name) { this.name = name; }
    public void setUniversity(String university) { this.university = university; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
}