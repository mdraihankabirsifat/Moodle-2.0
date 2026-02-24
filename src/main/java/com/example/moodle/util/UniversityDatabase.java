package com.example.moodle.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.moodle.model.UniversityInfo;

public class UniversityDatabase {

    private static final Map<String, UniversityInfo> universities = new HashMap<>();

    static {
        universities.put("BUET", new UniversityInfo(
                "Bangladesh University of Engineering and Technology",
                "BUET",
                "Palashi, Dhaka-1000, Bangladesh",
                "+880-2-9665650",
                "registrar@buet.ac.bd",
                "www.buet.ac.bd",
                Arrays.asList(
                        "Admission Test 2026 scheduled for March 15",
                        "Annual Sports Week starts April 1",
                        "New research lab inaugurated in CSE department",
                        "Inter-department Programming Contest on Feb 28",
                        "Library hours extended during exam period"
                )
        ));

        universities.put("DU", new UniversityInfo(
                "University of Dhaka",
                "DU",
                "Nilkhet Road, Dhaka-1000, Bangladesh",
                "+880-2-9661900",
                "info@du.ac.bd",
                "www.du.ac.bd",
                Arrays.asList(
                        "Convocation Ceremony 2026 on April 20",
                        "New hostel block construction begins",
                        "Faculty recruitment notice published",
                        "Cultural Week celebration March 10-15",
                        "Research grant applications due March 30"
                )
        ));

        universities.put("DHAKA MEDICAL COLLEGE", new UniversityInfo(
                "Dhaka Medical College",
                "DMC",
                "Secretariat Road, Dhaka-1000, Bangladesh",
                "+880-2-8626812",
                "info@dmc.edu.bd",
                "www.dmc.edu.bd",
                Arrays.asList(
                        "Clinical rotation schedule released",
                        "Free health camp in Mirpur on March 5",
                        "International Medical Conference 2026",
                        "New anatomy lab upgrade completed",
                        "Student scholarship applications open"
                )
        ));

        universities.put("BAU", new UniversityInfo(
                "Bangladesh Agricultural University",
                "BAU",
                "Mymensingh-2202, Bangladesh",
                "+880-91-67401",
                "registrar@bau.edu.bd",
                "www.bau.edu.bd",
                Arrays.asList(
                        "Agricultural Fair 2026 announced for May",
                        "New dairy science research project launched",
                        "Farmer training program registration open",
                        "Campus beautification drive this weekend",
                        "International collaboration with FAO signed"
                )
        ));

        universities.put("RUET", new UniversityInfo(
                "Rajshahi University of Engineering and Technology",
                "RUET",
                "Rajshahi-6204, Bangladesh",
                "+880-721-750742",
                "registrar@ruet.ac.bd",
                "www.ruet.ac.bd",
                Arrays.asList(
                        "Techfest 2026 registration open",
                        "New workshop building inaugurated",
                        "Campus Wi-Fi upgrade completed",
                        "National Robotics Competition team selected",
                        "Mid-term exam schedule published"
                )
        ));
    }

    public static UniversityInfo getUniversity(String name) {
        return universities.get(name.toUpperCase());
    }

    public static List<String> getAllNames() {
        return Arrays.asList("BUET", "DU", "Dhaka Medical College", "BAU", "RUET");
    }

    public static List<String> search(String query) {
        String q = query.toUpperCase().trim();
        return getAllNames().stream()
                .filter(n -> n.toUpperCase().contains(q))
                .collect(java.util.stream.Collectors.toList());
    }
}
