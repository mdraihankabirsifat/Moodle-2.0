package com.example.moodle.model;

public class Course {

    private String code;
    private String name;
    private String teacherEmail;
    private String semester;

    public Course(String code, String name, String teacherEmail, String semester) {
        this.code = code;
        this.name = name;
        this.teacherEmail = teacherEmail;
        this.semester = semester;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getTeacherEmail() { return teacherEmail; }
    public String getSemester() { return semester; }
}
