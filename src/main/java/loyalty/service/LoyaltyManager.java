package loyalty.service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LoyaltyManager {
    private Map<String, Integer> memberPoints;
    private Map<String, String> memberNames;
    private static final String DATA_FILE = "./src/main/java/store/data/loyalty_members.txt";

    public LoyaltyManager() {
        memberPoints = new HashMap<>();
        memberNames = new HashMap<>();
        loadMemberData();
    }

    public boolean registerMember(String name, String phone) {
        if (memberPoints.containsKey(phone)) {
            return false;
        }
        memberPoints.put(phone, 0);
        memberNames.put(phone, name);
        saveMemberData();
        return true;
    }

    public boolean addPoints(String phone, int points) {
        if (!memberPoints.containsKey(phone)) {
            return false;
        }
        memberPoints.put(phone, memberPoints.get(phone) + points);
        saveMemberData();
        return true;
    }

    public boolean redeemPoints(String phone, int points) {
        if (!memberPoints.containsKey(phone)) {
            return false;
        }
        int currentPoints = memberPoints.get(phone);
        if (currentPoints < points) {
            return false;
        }
        memberPoints.put(phone, currentPoints - points);
        saveMemberData();
        return true;
    }

    public int getPoints(String phone) {
        return memberPoints.getOrDefault(phone, -1);
    }

    public Map<String, Integer> getAllMembers() {
        return new HashMap<>(memberPoints);
    }

    public Map<String, String> getNames() {
        return new HashMap<>(memberNames);
    }

    private void loadMemberData() {
        File dir = new File("./src/main/java/store/data");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                memberNames.put(parts[0], parts[1]);
                memberPoints.put(parts[0], Integer.parseInt(parts[2]));
            }
        } catch (IOException e) {
            System.out.println("No existing member data found. Starting fresh.");
        }
    }

    private void saveMemberData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (String phone : memberPoints.keySet()) {
                writer.write(String.format("%s,%s,%d%n", 
                    phone, 
                    memberNames.get(phone), 
                    memberPoints.get(phone)));
            }
        } catch (IOException e) {
            System.out.println("Error saving member data: " + e.getMessage());
        }
    }
} 