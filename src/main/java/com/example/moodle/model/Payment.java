package com.example.moodle.model;

public class Payment {

    private String studentEmail;
    private String type;
    private int amount;
    private String date;
    private String status;

    public Payment(String studentEmail, String type, int amount, String date, String status) {
        this.studentEmail = studentEmail;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.status = status;
    }

    public String getStudentEmail() { return studentEmail; }
    public String getType() { return type; }
    public int getAmount() { return amount; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
}
