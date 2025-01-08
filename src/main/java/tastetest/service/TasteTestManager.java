package tastetest.service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import tastetest.model.TasteTestItem;

public class TasteTestManager {
    private List<TasteTestItem> items;
    private Scanner scanner;
    private static final String ITEMS_FILE = "./src/main/java/tastetest/data/taste_test_items.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TasteTestManager() {
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
                    if (parts.length >= 9) {
                        items.add(new TasteTestItem(parts));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading items: " + e.getMessage());
        }
    }

    private void saveItems() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ITEMS_FILE))) {
            writer.println("ID | ProductName | Category | Status | TestDate | Location | Staff | Participants | AvgRating | R1 | R2 | R3 | R4 | R5 |");
            writer.println("-".repeat(120));
            for (TasteTestItem item : items) {
                int[] dist = item.getRatingDistribution();
                writer.printf("%s|%s|%s|%s|%s|%s|%s|%d|%.1f|%d|%d|%d|%d|%d|%n",
                    item.getId(),
                    item.getProductName(),
                    item.getCategory(),
                    item.getStatus(),
                    item.getTestDate(),
                    item.getLocation(),
                    item.getStaffMember(),
                    item.getParticipantCount(),
                    item.getAverageRating(),
                    dist[0], dist[1], dist[2], dist[3], dist[4]);
            }
        } catch (IOException e) {
            System.err.println("Error saving items: " + e.getMessage());
        }
    }

    public void startTasteTestManagement() {
        while (true) {
            System.out.println("\nTaste Test Zone Management");
            System.out.println("1. Set Up New Taste Test");
            System.out.println("2. Record Customer Feedback");
            System.out.println("3. View All Active Tests");
            System.out.println("4. Update Test Status");
            System.out.println("5. View Test Statistics");
            System.out.println("6. Return to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: setupNewTest(); break;
                case 2: recordFeedback(); break;
                case 3: viewActiveTests(); break;
                case 4: updateTestStatus(); break;
                case 5: viewStatistics(); break;
                case 6: return;
                default: System.out.println("Invalid option");
            }
        }
    }

    private void setupNewTest() {
        System.out.println("\nSet Up New Taste Test");
        System.out.print("Enter product name: ");
        String productName = scanner.nextLine();
        
        System.out.print("Enter product category: ");
        String category = scanner.nextLine();
        
        System.out.print("Enter station location: ");
        String location = scanner.nextLine();
        
        System.out.print("Enter staff member name: ");
        String staffMember = scanner.nextLine();

        String[] itemParts = {
            String.valueOf(items.size() + 1),
            productName,
            category,
            "Active",
            LocalDateTime.now().format(FORMATTER),
            location,
            staffMember,
            "0",  // Initial participant count
            "0.0", // Initial average rating
            "0", "0", "0", "0", "0" // Initial rating distribution
        };

        items.add(new TasteTestItem(itemParts));
        saveItems();
        System.out.println("New taste test set up successfully.");
    }

    private void recordFeedback() {
        System.out.print("Enter test ID: ");
        String id = scanner.nextLine();

        TasteTestItem item = items.stream()
            .filter(i -> i.getId().equals(id))
            .findFirst()
            .orElse(null);

        if (item == null || !item.getStatus().equals("Active")) {
            System.out.println("Test not found or not active.");
            return;
        }

        System.out.print("Enter customer rating (1-5): ");
        double rating = scanner.nextDouble();
        scanner.nextLine();

        if (rating < 1 || rating > 5) {
            System.out.println("Invalid rating. Must be between 1 and 5.");
            return;
        }

        item.updateRating(rating);
        saveItems();
        
        // Display immediate feedback statistics
        System.out.println("\nUpdated Statistics for " + item.getProductName());
        System.out.println("-".repeat(40));
        System.out.printf("Total Respondents: %d%n", item.getParticipantCount());
        System.out.printf("Average Rating: %.2f%n", item.getAverageRating());
        System.out.println("\nRating Distribution:");
        System.out.print(item.getRatingStats());
    }

    private void viewActiveTests() {
        List<TasteTestItem> activeTests = items.stream()
            .filter(item -> item.getStatus().equals("Active"))
            .toList();

        if (activeTests.isEmpty()) {
            System.out.println("No active taste tests.");
            return;
        }

        System.out.println("\nActive Taste Tests:");
        System.out.println("-".repeat(120));
        for (TasteTestItem item : activeTests) {
            System.out.printf("%s | %s | %s | %s | %s | Rating: %.1f (%d responses)%n",
                item.getId(),
                item.getProductName(),
                item.getCategory(),
                item.getLocation(),
                item.getStaffMember(),
                item.getAverageRating(),
                item.getParticipantCount());
        }
        System.out.println("-".repeat(120));
    }

    private void updateTestStatus() {
        System.out.print("Enter test ID: ");
        String id = scanner.nextLine();

        TasteTestItem item = items.stream()
            .filter(i -> i.getId().equals(id))
            .findFirst()
            .orElse(null);

        if (item == null) {
            System.out.println("Test not found.");
            return;
        }

        System.out.println("Select new status:");
        System.out.println("1. Active");
        System.out.println("2. Paused");
        System.out.println("3. Completed");
        System.out.print("Enter choice (1-3): ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        String newStatus = switch (choice) {
            case 1 -> "Active";
            case 2 -> "Paused";
            case 3 -> "Completed";
            default -> null;
        };

        if (newStatus != null) {
            item.setStatus(newStatus);
            saveItems();
            System.out.println("Status updated successfully.");
        } else {
            System.out.println("Invalid status choice.");
        }
    }

    private void viewStatistics() {
        System.out.println("\nTaste Test Statistics:");
        System.out.println("-".repeat(50));
        
        // Overall statistics
        double overallAverage = items.stream()
            .mapToDouble(TasteTestItem::getAverageRating)
            .average()
            .orElse(0.0);
            
        int totalParticipants = items.stream()
            .mapToInt(TasteTestItem::getParticipantCount)
            .sum();
            
        System.out.printf("Total Tests: %d%n", items.size());
        System.out.printf("Total Participants: %d%n", totalParticipants);
        System.out.printf("Overall Average Rating: %.2f%n", overallAverage);
        
        // Detailed statistics for each active test
        System.out.println("\nDetailed Statistics by Product:");
        System.out.println("-".repeat(50));
        items.stream()
            .filter(item -> item.getParticipantCount() > 0)
            .sorted((a, b) -> Double.compare(b.getAverageRating(), a.getAverageRating()))
            .forEach(item -> {
                System.out.printf("\n%s (Status: %s)%n", item.getProductName(), item.getStatus());
                System.out.printf("Location: %s, Staff: %s%n", item.getLocation(), item.getStaffMember());
                System.out.printf("Participants: %d, Average Rating: %.2f%n", 
                    item.getParticipantCount(), 
                    item.getAverageRating());
                System.out.println("Rating Distribution:");
                System.out.print(item.getRatingStats());
            });
    }
}