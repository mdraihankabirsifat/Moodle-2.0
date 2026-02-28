package com.example.moodle.model;

public class Course {

    private String code;
    private String name;
    private String teacherEmail;
    private String semester;
    private String teacherName;
    private String batch; // e.g. "24" for 2024-batch students

    public Course(String code, String name, String teacherEmail, String semester) {
        this(code, name, teacherEmail, semester, "", "");
    }

    public Course(String code, String name, String teacherEmail, String semester,
                  String teacherName, String batch) {
        this.code = code;
        this.name = name;
        this.teacherEmail = teacherEmail;
        this.semester = semester;
        this.teacherName = teacherName != null ? teacherName : "";
        this.batch = batch != null ? batch : "";
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getTeacherEmail() { return teacherEmail; }
    public String getSemester() { return semester; }
    public String getTeacherName() { return teacherName; }
    public String getBatch() { return batch; }
}
