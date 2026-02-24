package com.example.moodle.model;

import java.util.List;

public class UniversityInfo {

    private final String name;
    private final String shortName;
    private final String location;
    private final String phone;
    private final String email;
    private final String website;
    private final List<String> notices;

    public UniversityInfo(String name, String shortName, String location,
                          String phone, String email, String website,
                          List<String> notices) {
        this.name = name;
        this.shortName = shortName;
        this.location = location;
        this.phone = phone;
        this.email = email;
        this.website = website;
        this.notices = notices;
    }

    public String getName() { return name; }
    public String getShortName() { return shortName; }
    public String getLocation() { return location; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getWebsite() { return website; }
    public List<String> getNotices() { return notices; }
}
