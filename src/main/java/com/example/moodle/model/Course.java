package com.example.moodle.model;

public class Course {

    private String code;
    private String name;
    private String teacherEmail;
    private String semester;
    private String teacherName;
    private String batch;
    private String university; // Link course to a specific university

    public Course(String code, String name, String teacherEmail, String semester) {
        this(code, name, teacherEmail, semester, "", "", "global");
    }

    public Course(String code, String name, String teacherEmail, String semester,
                  String teacherName, String batch) {
        this(code, name, teacherEmail, semester, teacherName, batch, "global");
    }

    public Course(String code, String name, String teacherEmail, String semester,
                  String teacherName, String batch, String university) {
        this.code = code;
        this.name = name;
        this.teacherEmail = teacherEmail;
        this.semester = semester;
        this.teacherName = teacherName != null ? teacherName : "";
        this.batch = batch != null ? batch : "";
        this.university = university != null ? university : "global";
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getTeacherEmail() { return teacherEmail; }
    public String getSemester() { return semester; }
    public String getTeacherName() { return teacherName; }
    public String getBatch() { return batch; }
    public String getUniversity() { return university; }
}
