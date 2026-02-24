package com.example.moodle.model;

public class Message {

    private String from;
    private String to;
    private String content;
    private String timestamp;

    public Message(String from, String to, String content, String timestamp) {
        this.from = from;
        this.to = to;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }
}
