package waste.service;

import waste.model.*;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.io.*;

/**
 * Core system for managing waste and disposal operations in the store.
 * Handles item tracking, disposal scheduling, donations, and reporting
 * for the store's waste management processes. Integrates with vendors
 * for proper disposal and maintains compliance records.
 *
 * @author Hrishikesha Kyathsandra
 */
public class WasteManagementSystem {
    private List<WasteItem> wasteItems;
    private List<DisposalSchedule> schedules;
    private Scanner scanner;
    private String storeId;
    private static final String WASTE_LOG_FILE = "src/main/java/waste/data/waste_log.txt";
    private static final String DISPOSAL_SCHEDULE_FILE = "src/main/java/waste/data/disposal_schedules.txt";
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public WasteManagementSystem(String storeId) {
        this.storeId = storeId;
        this.wasteItems = new ArrayList<>();
        this.schedules = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        loadData();
    }

    public void start() {
        while (true) {
            System.out.println("\nWaste Management System");
            System.out.println("1. View Flagged Items");
            System.out.println("2. Process Expired Items");
            System.out.println("3. Schedule Disposal Pickup");
            System.out.println("4. View Disposal Schedule");
            System.out.println("5. Generate Waste Report");
            System.out.println("6. Manage Donations");
            System.out.println("7. View Loss Analysis");
            System.out.println("8. Return to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: viewFlaggedItems(); break;
                case 2: processExpiredItems(); break;
                case 3: scheduleDisposalPickup(); break;
                case 4: viewDisposalSchedule(); break;
                case 5: generateWasteReport(); break;
                case 6: manageDonations(); break;
                case 7: viewLossAnalysis(); break;
                case 8: return;
                default: System.out.println("Invalid option");
            }
        }
    }

    private void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(WASTE_LOG_FILE))) {
            String line;
            boolean firstLine = true;  // Added to skip header
            while ((line = reader.readLine()) != null) {
                if (firstLine) {  // Skip the header line
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length >= 7) {
                    try {
                        WasteItem item = new WasteItem(
                            parts[0].trim(), // productId
                            parts[1].trim(), // name
                            Integer.parseInt(parts[2].trim()), // quantity
                            Double.parseDouble(parts[3].trim()), // value
                            parts[4].trim(), // category
                            LocalDateTime.parse(parts[5].trim(), dateFormatter), // expirationDate
                            parts[6].trim() // department
                        );
                        if (parts.length > 7) {
                            item.setStatus(parts[7].trim());
                        }
                        if (parts.length > 8) {
                            item.setDisposalMethod(parts[8].trim());
                        }
                        wasteItems.add(item);
                    } catch (NumberFormatException | DateTimeParseException e) {
                        System.err.println("Error parsing line: " + line);
                        continue;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading waste log: " + e.getMessage());
        }

        // Load disposal schedules
        try (BufferedReader reader = new BufferedReader(new FileReader(DISPOSAL_SCHEDULE_FILE))) {
            String line;
            boolean firstLine = true;  // Added to skip header
            while ((line = reader.readLine()) != null) {
                if (firstLine) {  // Skip the header line
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    try {
                        DisposalSchedule schedule = new DisposalSchedule(
                            parts[0].trim(), // vendorId
                            parts[1].trim(), // vendorName
                            LocalDateTime.parse(parts[2].trim(), dateFormatter), // pickupTime
                            parts[3].trim() // wasteCategory
                        );
                        if (parts.length > 4) {
                            schedule.setStatus(parts[4].trim());
                        }
                        schedules.add(schedule);
                    } catch (DateTimeParseException e) {
                        System.err.println("Error parsing line: " + line);
                        continue;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading disposal schedules: " + e.getMessage());
        }
    }

    private void processExpiredItems() {
        System.out.println("\nProcess Expired Items:");
        viewFlaggedItems();
        
        System.out.print("Enter Product ID to process (or 'done' to finish): ");
        String input = scanner.nextLine();
        
        while (!input.equalsIgnoreCase("done")) {
            WasteItem item = findWasteItem(input);
            if (item != null) {
                System.out.println("\nDisposal Methods:");
                System.out.println("1. Recycle");
                System.out.println("2. Compost");
                System.out.println("3. General Waste");
                System.out.println("4. Hazardous Waste");
                System.out.println("5. Mark for Donation");
                System.out.print("Choose disposal method: ");
                
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                switch (choice) {
                    case 1: item.setDisposalMethod("RECYCLE"); break;
                    case 2: item.setDisposalMethod("COMPOST"); break;
                    case 3: item.setDisposalMethod("GENERAL"); break;
                    case 4: item.setDisposalMethod("HAZARDOUS"); break;
                    case 5: item.setDisposalMethod("DONATION"); break;
                    default: System.out.println("Invalid choice");
                }
                
                item.setStatus("INSPECTED");
                saveWasteLog();
                System.out.println("Item processed successfully");
            } else {
                System.out.println("Item not found");
            }
            
            System.out.print("Enter next Product ID (or 'done' to finish): ");
            input = scanner.nextLine();
        }
    }

    private void scheduleDisposalPickup() {
        System.out.println("\nSchedule Disposal Pickup:");
        System.out.println("Available Vendors:");
        System.out.println("1. EcoWaste Solutions (Recyclables)");
        System.out.println("2. GreenCompost Co. (Compostables)");
        System.out.println("3. SafeDisposal Inc. (Hazardous)");
        System.out.println("4. General Waste Management");
        
        System.out.print("Select vendor (1-4): ");
        int vendorChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        System.out.print("Enter pickup date (yyyy-MM-dd HH:mm): ");
        String dateStr = scanner.nextLine();
        LocalDateTime pickupTime = LocalDateTime.parse(dateStr, dateFormatter);
        
        String vendorId, vendorName, category;
        switch (vendorChoice) {
            case 1:
                vendorId = "ECO001";
                vendorName = "EcoWaste Solutions";
                category = "RECYCLABLE";
                break;
            case 2:
                vendorId = "GRN001";
                vendorName = "GreenCompost Co.";
                category = "COMPOSTABLE";
                break;
            case 3:
                vendorId = "SAF001";
                vendorName = "SafeDisposal Inc.";
                category = "HAZARDOUS";
                break;
            default:
                vendorId = "GEN001";
                vendorName = "General Waste Management";
                category = "GENERAL";
        }
        
        DisposalSchedule schedule = new DisposalSchedule(vendorId, vendorName, pickupTime, category);
        schedules.add(schedule);
        saveDisposalSchedule();
        System.out.println("Pickup scheduled successfully. Manifest ID: " + schedule.getManifestId());
    }

    private void viewDisposalSchedule() {
        System.out.println("\nUpcoming Disposal Pickups:");
        for (DisposalSchedule schedule : schedules) {
            if (schedule.getStatus().equals("SCHEDULED")) {
                System.out.printf("Vendor: %s, Category: %s, Time: %s, Manifest: %s%n",
                    schedule.getVendorName(),
                    schedule.getWasteCategory(),
                    schedule.getPickupTime().format(dateFormatter),
                    schedule.getManifestId());
            }
        }
    }

    private void generateWasteReport() {
        System.out.println("\nWaste Report Summary:");
        
        Map<String, Double> categoryLoss = new HashMap<>();
        Map<String, Integer> categoryCount = new HashMap<>();
        
        for (WasteItem item : wasteItems) {
            String category = item.getCategory();
            categoryLoss.merge(category, item.getValue(), Double::sum);
            categoryCount.merge(category, 1, Integer::sum);
        }
        
        System.out.println("\nLoss by Category:");
        categoryLoss.forEach((category, loss) -> {
            System.out.printf("%s: $%.2f (Items: %d)%n",
                category, loss, categoryCount.get(category));
        });
        
        // Calculate total loss
        double totalLoss = categoryLoss.values().stream().mapToDouble(Double::doubleValue).sum();
        System.out.printf("\nTotal Loss: $%.2f%n", totalLoss);
    }

    private void manageDonations() {
        System.out.println("\nItems Available for Donation:");
        List<WasteItem> donatable = new ArrayList<>();
        
        for (WasteItem item : wasteItems) {
            if (item.getStatus().equals("FLAGGED") && 
                item.getExpirationDate().isAfter(LocalDateTime.now())) {
                donatable.add(item);
            }
        }
            
        if (donatable.isEmpty()) {
            System.out.println("No items available for donation");
            return;
        }
        
        for (int i = 0; i < donatable.size(); i++) {
            WasteItem item = donatable.get(i);
            System.out.printf("%d. %s (Expires: %s, Value: $%.2f)%n",
                i + 1,
                item.getName(),
                item.getExpirationDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                item.getValue());
        }
        
        System.out.print("\nSelect item number to mark for donation (or 0 to cancel): ");
        String input = scanner.nextLine();
        
        try {
            int choice = Integer.parseInt(input);
            if (choice > 0 && choice <= donatable.size()) {
                WasteItem item = donatable.get(choice - 1);
                item.setStatus("DONATED");
                item.setDisposalMethod("DONATION");
                saveWasteLog();
                System.out.println("Item successfully marked for donation: " + item.getName());
            } else if (choice != 0) {
                System.out.println("Invalid selection");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private void viewLossAnalysis() {
        System.out.println("\nLoss Analysis Report:");
        
        // Analysis by department
        Map<String, Double> departmentLoss = new HashMap<>();
        for (WasteItem item : wasteItems) {
            departmentLoss.merge(item.getDepartment(), item.getValue(), Double::sum);
        }
        
        System.out.println("\nLoss by Department:");
        departmentLoss.forEach((dept, loss) -> 
            System.out.printf("%s: $%.2f%n", dept, loss));
        
        // Identify high-loss products
        System.out.println("\nTop Loss Items:");
        wasteItems.stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(5)
            .forEach(item -> System.out.printf("%s: $%.2f%n", 
                item.getName(), item.getValue()));
    }

    private WasteItem findWasteItem(String productId) {
        return wasteItems.stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst()
            .orElse(null);
    }

    private void saveWasteLog() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(WASTE_LOG_FILE))) {
            writer.println("ID|Name|Quantity|Value|Category|ExpirationDate|Department|Status|DisposalMethod");
            for (WasteItem item : wasteItems) {
                writer.printf("%s|%s|%d|%.2f|%s|%s|%s|%s|%s%n",
                    item.getProductId(),
                    item.getName(),
                    item.getQuantity(),
                    item.getValue(),
                    item.getCategory(),
                    item.getExpirationDate().format(dateFormatter),
                    item.getDepartment(),
                    item.getStatus(),
                    item.getDisposalMethod());
            }
        } catch (IOException e) {
            System.err.println("Error saving waste log: " + e.getMessage());
        }
    }

    private void saveDisposalSchedule() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DISPOSAL_SCHEDULE_FILE))) {
            writer.println("VendorID|VendorName|PickupTime|Category|Status|ManifestID");
            for (DisposalSchedule schedule : schedules) {
                writer.printf("%s|%s|%s|%s|%s|%s%n",
                    schedule.getVendorId(),
                    schedule.getVendorName(),
                    schedule.getPickupTime().format(dateFormatter),
                    schedule.getWasteCategory(),
                    schedule.getStatus(),
                    schedule.getManifestId());
            }
        } catch (IOException e) {
            System.err.println("Error saving disposal schedules: " + e.getMessage());
        }
    }

    private void viewFlaggedItems() {
        System.out.println("\nFlagged Items for Review:");
        boolean found = false;
        
        for (WasteItem item : wasteItems) {
            if (item.getStatus().equals("FLAGGED")) {
                found = true;
                System.out.printf("ID: %s | Name: %s | Expires: %s | Quantity: %d | Value: $%.2f | Department: %s%n",
                    item.getProductId(),
                    item.getName(),
                    item.getExpirationDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    item.getQuantity(),
                    item.getValue(),
                    item.getDepartment());
            }
        }
        
        if (!found) {
            System.out.println("No flagged items found.");
        }
    }
} 