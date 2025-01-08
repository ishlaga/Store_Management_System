package lostandfound.service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import lostandfound.model.LostFoundItem;

public class LostFoundManager {
    private List<LostFoundItem> items;
    private Scanner scanner;
    private static final String ITEMS_FILE = "./src/main/java/lostandfound/data/lost_found_items.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LostFoundManager() {
        this.items = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        loadItems();
    }

    private void loadItems() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ITEMS_FILE))) {
            reader.readLine(); // Skip header
            reader.readLine(); // Skip separator
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 7) {
                        items.add(new LostFoundItem(parts));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading items: " + e.getMessage());
        }
    }

    public void startLostFoundManagement() {
        while (true) {
            System.out.println("\nLost and Found Management");
            System.out.println("1. Report Lost Item");
            System.out.println("2. Report Found Item");
            System.out.println("3. View All Items");
            System.out.println("4. Update Item Status");
            System.out.println("5. Search Items");
            System.out.println("6. Return to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: reportLostItem(); break;
                case 2: reportFoundItem(); break;
                case 3: viewAllItems(); break;
                case 4: updateItemStatus(); break;
                case 5: searchItems(); break;
                case 6: return;
                default: System.out.println("Invalid option");
            }
        }
    }

    private void reportLostItem() {
        System.out.println("\nReport Lost Item");
        System.out.print("Enter item description: ");
        String description = scanner.nextLine();
        
        System.out.print("Enter location last seen: ");
        String location = scanner.nextLine();
        
        System.out.print("Enter contact information: ");
        String contact = scanner.nextLine();

        String[] itemParts = {
            String.valueOf(items.size() + 1),
            "Lost",
            description,
            "Pending",
            LocalDateTime.now().format(FORMATTER),
            location,
            contact
        };

        items.add(new LostFoundItem(itemParts));
        System.out.println("Lost item reported successfully.");
    }

    private void reportFoundItem() {
        System.out.println("\nReport Found Item");
        System.out.print("Enter item description: ");
        String description = scanner.nextLine();
        
        System.out.print("Enter location found: ");
        String location = scanner.nextLine();

        String[] itemParts = {
            String.valueOf(items.size() + 1),
            "Found",
            description,
            "Unclaimed",
            LocalDateTime.now().format(FORMATTER),
            location,
            "Staff"
        };

        items.add(new LostFoundItem(itemParts));
        System.out.println("Found item reported successfully.");
    }

    private void viewAllItems() {
        if (items.isEmpty()) {
            System.out.println("No items in lost and found.");
            return;
        }

        System.out.println("\nLost and Found Items:");
        System.out.println("-".repeat(100));
        for (LostFoundItem item : items) {
            System.out.printf("%s | %s | %s | %s | %s | %s%n",
                item.getId(),
                item.getType(),
                item.getDescription(),
                item.getStatus(),
                item.getLocation(),
                item.getReportDate());
        }
        System.out.println("-".repeat(100));
    }

    private void updateItemStatus() {
        System.out.print("Enter item ID: ");
        String id = scanner.nextLine();

        LostFoundItem item = items.stream()
            .filter(i -> i.getId().equals(id))
            .findFirst()
            .orElse(null);

        if (item == null) {
            System.out.println("Item not found.");
            return;
        }

        System.out.println("Select new status:");
        System.out.println("1. Pending");
        System.out.println("2. Matched");
        System.out.println("3. Collected");
        System.out.println("4. Unclaimed");
        System.out.print("Enter choice (1-4): ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        String newStatus;
        switch (choice) {
            case 1:
                newStatus = "Pending";
                break;
            case 2:
                newStatus = "Matched";
                break;
            case 3:
                newStatus = "Collected";
                break;
            case 4:
                newStatus = "Unclaimed";
                break;
            default:
                newStatus = null;
                break;
        }

        if (newStatus != null) {
            item.setStatus(newStatus);
            System.out.println("Status updated successfully.");
        } else {
            System.out.println("Invalid status choice.");
        }
    }

    private void searchItems() {
        System.out.print("Enter search term: ");
        String search = scanner.nextLine().toLowerCase();

        List<LostFoundItem> found = items.stream()
            .filter(item -> 
                item.getDescription().toLowerCase().contains(search) ||
                item.getLocation().toLowerCase().contains(search))
            .collect(Collectors.toList());

        if (found.isEmpty()) {
            System.out.println("No matching items found.");
            return;
        }

        System.out.println("\nMatching Items:");
        System.out.println("-".repeat(100));
        for (LostFoundItem item : found) {
            System.out.printf("%s | %s | %s | %s | %s%n",
                item.getId(),
                item.getType(),
                item.getDescription(),
                item.getStatus(),
                item.getLocation());
        }
    }
}