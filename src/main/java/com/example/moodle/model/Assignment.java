package com.example.moodle.model;

public class Assignment {

    private String courseCode;
    private String title;
    private String description;
    private String teacherEmail;

    public Assignment(String courseCode, String title, String description, String teacherEmail) {
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
        this.teacherEmail = teacherEmail;
    }

    public String getCourseCode() { return courseCode; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTeacherEmail() { return teacherEmail; }
}
