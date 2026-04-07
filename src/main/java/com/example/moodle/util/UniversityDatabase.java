package com.example.moodle.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.moodle.model.UniversityInfo;

public class UniversityDatabase {

    private static final Map<String, UniversityInfo> universities = new HashMap<>();

    static {
        loadUniversities();
    }

    private static void loadUniversities() {
        try (InputStream is = UniversityDatabase.class.getResourceAsStream("/com/example/moodle/news/unidata1.txt")) {
            if (is == null) {
                System.err.println("Warning: could not find unidata1.txt resource");
                return;
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                
                String name = "";
                String acronym = "";
                String phone = "";
                String email = "";
                String website = "";
                String location = "";
                List<String> notices = new ArrayList<>();
                String currentSection = "";

                while ((line = br.readLine()) != null) {
                    line = line.trim();

                    if (line.startsWith("UNIVERSITY:")) {
                        String fullName = line.substring("UNIVERSITY:".length()).trim();
                        if (fullName.endsWith(")")) {
                            int openParen = fullName.lastIndexOf("(");
                            if (openParen != -1) {
                                acronym = fullName.substring(openParen + 1, fullName.length() - 1).trim();
                                name = fullName.substring(0, openParen).trim();
                            }
                        }
                        if (name.isEmpty()) name = fullName;
                        if (acronym.isEmpty()) acronym = name.toUpperCase().replaceAll("[^A-Z]", "");
                        if (acronym.isEmpty()) acronym = name.toUpperCase();
                    } else if (line.startsWith("MEDICAL COLLEGE:")) {
                        name = line.substring("MEDICAL COLLEGE:".length()).trim();
                        acronym = name.toUpperCase();
                    } else if (line.equals("GLOBAL NOTICES:")) {
                        currentSection = "NOTICES";
                    } else if (line.equals("CONTACT INFORMATION:")) {
                        currentSection = "CONTACT";
                    } else if (line.equals("LOCATION:")) {
                        currentSection = "LOCATION";
                    } else if (line.startsWith("Location:") && currentSection.isEmpty()) {
                        location = line.substring("Location:".length()).trim();
                    } else if (line.startsWith("============================================================")) {
                        if (!name.isEmpty()) {
                            if (website.isEmpty() && !email.isEmpty() && email.contains("@")) {
                                website = "www." + email.substring(email.indexOf("@") + 1);
                            }
                            UniversityInfo info = new UniversityInfo(name, acronym, location, phone, email, website, new ArrayList<>(notices));
                            universities.put(acronym, info);
                        }
                        // Reset
                        name = ""; acronym = ""; phone = ""; email = ""; website = ""; location = "";
                        notices.clear(); currentSection = "";
                    } else {
                        if (currentSection.equals("NOTICES") && line.matches("^\\d+\\..*")) {
                            notices.add(line);
                        } else if (currentSection.equals("CONTACT")) {
                            if (line.startsWith("Phone:")) phone = line.substring("Phone:".length()).trim();
                            if (line.startsWith("Email:")) email = line.substring("Email:".length()).trim();
                            if (line.startsWith("Website:")) website = line.substring("Website:".length()).trim();
                        } else if (currentSection.equals("LOCATION") && !line.isEmpty() && !line.startsWith("-")) {
                            location = line;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getAllSearchableNames() {
        List<String> list = new ArrayList<>();
        for (UniversityInfo u : universities.values()) {
            if (!u.getShortName().equalsIgnoreCase(u.getName())) {
                list.add(u.getName() + " (" + u.getShortName() + ")");
            } else {
                list.add(u.getName());
            }
        }
        list.sort(String::compareToIgnoreCase);
        return list;
    }

    public static List<String> search(String query) {
        String[] tokens = query.toUpperCase().trim().split("\\s+");
        List<String> priority1 = new ArrayList<>();
        List<String> priority2 = new ArrayList<>();

        for (UniversityInfo u : universities.values()) {
            boolean allNameMatch = true;
            boolean allDeepMatch = true;

            String uName = u.getName() != null ? u.getName().toUpperCase() : "";
            String uAcronym = u.getShortName() != null ? u.getShortName().toUpperCase() : "";
            String uLoc = u.getLocation() != null ? u.getLocation().toUpperCase() : "";

            for (String t : tokens) {
                boolean nameMatch = uName.contains(t) || uAcronym.contains(t);
                if (!nameMatch) allNameMatch = false;

                boolean deepMatch = nameMatch || uLoc.contains(t);
                if (!deepMatch) {
                    if (u.getPhone() != null && u.getPhone().toUpperCase().contains(t)) deepMatch = true;
                    else if (u.getEmail() != null && u.getEmail().toUpperCase().contains(t)) deepMatch = true;
                    else if (u.getWebsite() != null && u.getWebsite().toUpperCase().contains(t)) deepMatch = true;
                    else if (u.getNotices() != null) {
                        for (String notice : u.getNotices()) {
                            if (notice != null && notice.toUpperCase().contains(t)) {
                                deepMatch = true;
                                break;
                            }
                        }
                    }
                }
                if (!deepMatch) allDeepMatch = false;
            }

            if (allDeepMatch) {
                String formattedName = (!u.getShortName().equalsIgnoreCase(u.getName())) 
                    ? u.getName() + " (" + u.getShortName() + ")" 
                    : u.getName();

                if (allNameMatch) {
                    priority1.add(formattedName);
                } else {
                    priority2.add(formattedName);
                }
            }
        }

        priority1.sort(String::compareToIgnoreCase);
        priority2.sort(String::compareToIgnoreCase);

        List<String> results = new ArrayList<>(priority1);
        results.addAll(priority2);

        if (results.size() > 8) {
            return results.subList(0, 8);
        }
        return results;
    }

    public static UniversityInfo getUniversity(String searchName) {
        for (UniversityInfo u : universities.values()) {
            String matchStr = u.getShortName().equalsIgnoreCase(u.getName()) ? 
                     u.getName() : u.getName() + " (" + u.getShortName() + ")";
            if (matchStr.equalsIgnoreCase(searchName)) return u;
            if (u.getShortName().equalsIgnoreCase(searchName) || u.getName().equalsIgnoreCase(searchName)) return u;
        }
        return null;
    }
}
