package com.example.moodle.model;

public class User {

    private String name;
    private String university;
    private String studentId;
    private String email;
    private String password;

    public User(String name, String university,
                String studentId, String email, String password) {
        this.name = name;
        this.university = university;
        this.studentId = studentId;
        this.email = email;
        this.password = password;
    }

    public String getName() { return name; }
    public String getUniversity() { return university; }
    public String getStudentId() { return studentId; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}