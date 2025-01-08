/**
 * Manages self-checkout operations in the retail system.
 *
 * @author Chandrashekar Tirunagiri
 */
package checkout.service;

import checkout.model.CheckoutStation;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class CheckoutManager {
    private List<CheckoutStation> stations;
    private Scanner scanner;
    private static final String STATIONS_FILE = "./src/main/java/checkout/data/checkout_stations.txt";

    public CheckoutManager() {
        this.stations = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        loadStations();
    }

    private void loadStations() {
        try (BufferedReader reader = new BufferedReader(new FileReader(STATIONS_FILE))) {
            reader.readLine(); // Skip header
            reader.readLine(); // Skip separator
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 6) {
                        stations.add(new CheckoutStation(parts));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading stations: " + e.getMessage());
        }
    }

    public void startCheckoutManagement() {
        while (true) {
            System.out.println("\nSelf-Checkout Management System");
            System.out.println("1. View All Stations");
            System.out.println("2. Update Station Status");
            System.out.println("3. Handle Age Verification");
            System.out.println("4. Manage Cash Levels");
            System.out.println("5. View Alerts");
            System.out.println("6. Simulate Customer Transaction");
            System.out.println("7. Handle Special Items");
            System.out.println("8. Handle Error Resolution");
            System.out.println("9. Return to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewStations();
                    break;
                case 2:
                    updateStationStatus();
                    break;
                case 3:
                    handleAgeVerification();
                    break;
                case 4:
                    manageCashLevels();
                    break;
                case 5:
                    viewAlerts();
                    break;
                case 6:
                    simulateCustomerTransaction();
                    break;
                case 7:
                    handleSpecialItems();
                    break;
                case 8:
                    handleErrorResolution();
                    break;
                case 9:
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private void viewStations() {
        System.out.println("\nSelf-Checkout Stations Status:");
        System.out.println("-".repeat(100));
        for (CheckoutStation station : stations) {
            System.out.printf("Station: %s | Status: %s | Cash Level: %s | Transaction: %s | Needs Assistance: %s%n",
                station.getId(), station.getStatus(), station.getCashLevel(),
                station.getTransactionStatus(), station.getNeedsAssistance() ? "YES" : "No");
        }
        System.out.println("-".repeat(100));
    }

    private void updateStationStatus() {
        System.out.print("Enter station ID: ");
        String stationId = scanner.nextLine();

        CheckoutStation station = findStation(stationId);
        if (station == null) {
            System.out.println("Station not found.");
            return;
        }

        System.out.println("Select new status:");
        System.out.println("1. Active");
        System.out.println("2. Inactive");
        System.out.println("3. Maintenance");
        System.out.print("Enter choice (1-3): ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String newStatus;
        switch (choice) {
            case 1:
                newStatus = "Active";
                break;
            case 2:
                newStatus = "Inactive";
                break;
            case 3:
                newStatus = "Maintenance";
                break;
            default:
                newStatus = null;
                break;
        }

        if (newStatus != null) {
            station.setStatus(newStatus);
            System.out.println("Station status updated successfully.");
        } else {
            System.out.println("Invalid status choice.");
        }
    }

    private void handleAgeVerification() {
        List<CheckoutStation> needVerification = stations.stream()
            .filter(s -> s.getTransactionStatus().equals("AgeVerification"))
            .collect(Collectors.toList());

        if (needVerification.isEmpty()) {
            System.out.println("No stations currently need age verification.");
            return;
        }

        System.out.println("\nStations Requiring Age Verification:");
        for (CheckoutStation station : needVerification) {
            System.out.println("Station: " + station.getId());
            
            System.out.print("Verify age? (Y/N): ");
            String verify = scanner.nextLine();
            
            if (verify.equalsIgnoreCase("Y")) {
                station.setTransactionStatus("InProgress");
                station.setNeedsAssistance(false);
                System.out.println("Age verified for station " + station.getId());
            }
        }
    }

    private void manageCashLevels() {
        List<CheckoutStation> lowCash = stations.stream()
            .filter(s -> s.getCashLevel().equals("Low"))
            .collect(Collectors.toList());

        if (lowCash.isEmpty()) {
            System.out.println("All stations have normal cash levels.");
            return;
        }

        System.out.println("\nStations with Low Cash:");
        for (CheckoutStation station : lowCash) {
            System.out.println("Station: " + station.getId());
            
            System.out.print("Refill cash? (Y/N): ");
            String refill = scanner.nextLine();
            
            if (refill.equalsIgnoreCase("Y")) {
                station.setCashLevel("Normal");
                System.out.println("Cash refilled for station " + station.getId());
            }
        }
    }

    private void viewAlerts() {
        List<CheckoutStation> alertStations = stations.stream()
            .filter(CheckoutStation::getNeedsAssistance)
            .collect(Collectors.toList());

        if (alertStations.isEmpty()) {
            System.out.println("No stations currently need assistance.");
            return;
        }

        System.out.println("\nStations Needing Assistance:");
        System.out.println("-".repeat(50));
        for (CheckoutStation station : alertStations) {
            System.out.printf("Station: %s | Status: %s | Issue: %s%n",
                station.getId(), station.getStatus(), station.getTransactionStatus());
        }
    }

    private void simulateCustomerTransaction() {
        System.out.print("Enter station ID: ");
        String stationId = scanner.nextLine();
        CheckoutStation station = findStation(stationId);

        if (station == null || !station.getStatus().equals("Active")) {
            System.out.println("Invalid station or station not active.");
            return;
        }

        station.setTransactionStatus("InProgress");
        System.out.println("Transaction started at station " + stationId);

        while (true) {
            System.out.println("\nTransaction Menu:");
            System.out.println("1. Scan item");
            System.out.println("2. View current items");
            System.out.println("3. Finish and pay");
            System.out.println("4. Cancel transaction");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    scanItem(station);
                    break;
                case 2:
                    viewCurrentItems(station);
                    break;
                case 3:
                    processPayment(station);
                    return;
                case 4:
                    station.resetTransaction();
                    System.out.println("Transaction cancelled.");
                    return;
                default:
                    System.out.println("Invalid option");
            }

            if (station.getNeedsAssistance()) {
                System.out.println("Transaction needs assistance. Please wait for help.");
                return;
            }
        }
    }

    private void scanItem(CheckoutStation station) {
        System.out.print("Enter item name: ");
        String name = scanner.nextLine();
        System.out.print("Enter item price: ");
        double price = scanner.nextDouble();
        System.out.print("Enter item weight: ");
        double weight = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        station.addItem(name, price, weight);

        System.out.print("Enter current weight on scale: ");
        double currentWeight = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        if (!station.verifyWeight(currentWeight)) {
            System.out.println("Weight mismatch detected! Alerting supervisor.");
            station.setTransactionStatus("WeightMismatch");
            station.setNeedsAssistance(true);
        } else {
            System.out.printf("Item scanned successfully: %s - $%.2f%n", name, price);
        }
    }

    private void viewCurrentItems(CheckoutStation station) {
        List<CheckoutStation.TransactionItem> items = station.getCurrentItems();
        if (items.isEmpty()) {
            System.out.println("No items scanned yet.");
            return;
        }

        System.out.println("\nCurrent Items:");
        System.out.println("-".repeat(50));
        for (CheckoutStation.TransactionItem item : items) {
            System.out.printf("%s - $%.2f (%.2f kg)%n", 
                item.getName(), item.getPrice(), item.getWeight());
        }
        System.out.println("-".repeat(50));
        System.out.printf("Total Amount: $%.2f%n", station.getTotalAmount());
    }

    private void processPayment(CheckoutStation station) {
        System.out.println("Total amount: $" + station.getTotalAmount());
        System.out.println("Select payment method:");
        System.out.println("1. Cash");
        System.out.println("2. Card");
        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        boolean paymentSuccessful = false;
        if (choice == 1) {
            paymentSuccessful = processCashPayment(station);
        } else if (choice == 2) {
            paymentSuccessful = processCardPayment(station);
        } else {
            System.out.println("Invalid payment method. Transaction cancelled.");
            station.resetTransaction();
            return;
        }

        if (paymentSuccessful) {
            generateReceipt(station);
            station.resetTransaction();
        }
    }

    private boolean processCashPayment(CheckoutStation station) {
        System.out.print("Enter cash amount: ");
        double cashGiven = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        double change = cashGiven - station.getTotalAmount();
        if (change < 0) {
            System.out.println("Insufficient cash. Payment failed.");
            return false;
        }

        System.out.printf("Change: $%.2f%n", change);
        if (station.getCashLevel().equals("Low")) {
            System.out.println("Cash level low. Alerting supervisor for refill.");
            station.setNeedsAssistance(true);
        }
        return true;
    }

    private boolean processCardPayment(CheckoutStation station) {
        System.out.println("Processing card payment...");
        // Simulate card processing
        if (Math.random() < 0.9) { // 90% success rate
            System.out.println("Card payment successful.");
            return true;
        } else {
            System.out.println("Card payment failed. Please try again or use another payment method.");
            station.setTransactionStatus("PaymentError");
            return false;
        }
    }

    private void generateReceipt(CheckoutStation station) {
        System.out.println("\n--- Receipt ---");
        System.out.println("Station: " + station.getId());
        System.out.println("Date: " + LocalDate.now());
        System.out.println("\nItems:");
        for (CheckoutStation.TransactionItem item : station.getCurrentItems()) {
            System.out.printf("%s - $%.2f%n", item.getName(), item.getPrice());
        }
        System.out.println("-".repeat(20));
        System.out.printf("Total Amount: $%.2f%n", station.getTotalAmount());
        System.out.println("Thank you for shopping with us!");
        System.out.println("-".repeat(20));
    }

    private void handleSpecialItems() {
        System.out.println("\nSpecial Item Handling:");
        System.out.println("1. Age-restricted items");
        System.out.println("2. Items without barcodes");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                handleAgeVerification();
                break;
            case 2:
                handleNoBarcodeItems();
                break;
            default:
                System.out.println("Invalid option");
        }
    }

    private void handleNoBarcodeItems() {
        System.out.print("Enter station ID: ");
        String stationId = scanner.nextLine();
        CheckoutStation station = findStation(stationId);

        if (station == null) {
            System.out.println("Station not found.");
            return;
        }

        System.out.print("Enter item name: ");
        String name = scanner.nextLine();
        System.out.print("Enter item price: ");
        double price = scanner.nextDouble();
        System.out.print("Enter item weight: ");
        double weight = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        station.addItem(name, price, weight);
        System.out.println("Item added successfully.");
    }

    private void transferTransaction() {
        System.out.print("Enter source station ID: ");
        String sourceId = scanner.nextLine();
        System.out.print("Enter target station ID: ");
        String targetId = scanner.nextLine();

        CheckoutStation sourceStation = findStation(sourceId);
        CheckoutStation targetStation = findStation(targetId);

        if (sourceStation == null || targetStation == null) {
            System.out.println("Invalid station ID(s).");
            return;
        }

        if (!targetStation.getStatus().equals("Active")) {
            System.out.println("Target station is not active.");
            return;
        }

        if (!sourceStation.hasActiveTransaction()) {
            System.out.println("No active transaction to transfer.");
            return;
        }

        // Transfer all items from source to target
        targetStation.resetTransaction();
        List<CheckoutStation.TransactionItem> items = sourceStation.getCurrentItems();
        for (CheckoutStation.TransactionItem item : items) {
            targetStation.addItem(item.getName(), item.getPrice(), item.getWeight());
        }
        
        targetStation.setTransactionStatus("InProgress");
        sourceStation.resetTransaction();
        
        System.out.println("Transaction transferred successfully from station " + sourceId + " to " + targetId);
    }

    private CheckoutStation findStation(String id) {
        return stations.stream()
            .filter(s -> s.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    private void handleErrorResolution() {
        while (true) {
            System.out.println("\n=== Error Resolution Menu ===");
            System.out.println("1. View Error Details");
            System.out.println("2. Reset Station");
            System.out.println("3. Transfer Transaction");
            System.out.println("4. Override Error");
            System.out.println("5. Return to Main Menu");
            System.out.println("===============================");
            System.out.print("Choose an option: ");
    
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
    
            switch (choice) {
                case 1:
                    viewErrorDetails();
                    break;
                case 2:
                    resetStation();
                    break;
                case 3:
                    handleTransferWithDetails();
                    break;
                case 4:
                    overrideError();
                    break;
                case 5:
                    return; // Returns to main menu
                default:
                    System.out.println("Invalid option");
            }
            
            // Optional: Add a brief pause to read results
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
    
    // Enhanced error details view:
    private void viewErrorDetails() {
        System.out.print("\nEnter station ID: ");
        String stationId = scanner.nextLine();
        
        CheckoutStation station = findStation(stationId);
        if (station == null) {
            System.out.println("Station not found.");
            return;
        }
    
        System.out.println("\n=== Station " + stationId + " Error Details ===");
        System.out.println("Status: " + station.getStatus());
        System.out.println("Transaction Status: " + station.getTransactionStatus());
        System.out.println("Current Transaction Amount: $" + String.format("%.2f", station.getTotalAmount()));
        System.out.println("\nItems in Transaction:");
        for (CheckoutStation.TransactionItem item : station.getCurrentItems()) {
            System.out.printf("  * %s - $%.2f (%.2f kg)%n", 
                item.getName(), item.getPrice(), item.getWeight());
        }
        System.out.println("\nError Type: " + station.getTransactionStatus());
        System.out.println("Needs Assistance: " + (station.getNeedsAssistance() ? "Yes" : "No"));
        System.out.println("=====================================");
    }
    
    // Enhanced transfer handling:
    private void handleTransferWithDetails() {
        System.out.print("\nEnter source station ID: ");
        String sourceId = scanner.nextLine();
        System.out.print("Enter target station ID: ");
        String targetId = scanner.nextLine();
    
        CheckoutStation sourceStation = findStation(sourceId);
        CheckoutStation targetStation = findStation(targetId);
    
        if (sourceStation == null || targetStation == null) {
            System.out.println("Invalid station ID(s).");
            return;
        }
    
        if (!targetStation.getStatus().equals("Active")) {
            System.out.println("Target station is not active.");
            return;
        }
    
        System.out.println("\n=== Transferring Transaction ===");
        System.out.println("Source Station: " + sourceId);
        System.out.println("Target Station: " + targetId);
        System.out.println("\nTransferring Items:");
        
        for (CheckoutStation.TransactionItem item : sourceStation.getCurrentItems()) {
            System.out.printf("- Moving: %s - $%.2f%n", item.getName(), item.getPrice());
            targetStation.addItem(item.getName(), item.getPrice(), item.getWeight());
        }
    
        sourceStation.resetTransaction();
        sourceStation.setNeedsAssistance(false);
        
        System.out.println("\n=== Transfer Summary ===");
        System.out.println("Source station: Reset and ready");
        System.out.printf("Target station: Active with $%.2f total%n", targetStation.getTotalAmount());
        System.out.println("Transfer completed successfully!");
    }
    
    // Enhanced reset station function:
    private void resetStation() {
        System.out.print("\nEnter station ID to reset: ");
        String stationId = scanner.nextLine();
        
        CheckoutStation station = findStation(stationId);
        if (station == null) {
            System.out.println("Station not found.");
            return;
        }
    
        System.out.println("\n=== Resetting Station " + stationId + " ===");
        System.out.println("Previous Status: " + station.getStatus());
        System.out.println("Previous Transaction Status: " + station.getTransactionStatus());
        
        station.resetTransaction();
        station.setNeedsAssistance(false);
        station.setStatus("Active");
        
        System.out.println("\nStation has been reset successfully!");
        System.out.println("New Status: " + station.getStatus());
        System.out.println("New Transaction Status: " + station.getTransactionStatus());
    }
    
    // Enhanced override error function:
    private void overrideError() {
        System.out.print("\nEnter station ID: ");
        String stationId = scanner.nextLine();
        
        CheckoutStation station = findStation(stationId);
        if (station == null) {
            System.out.println("Station not found.");
            return;
        }
    
        System.out.println("\n=== Overriding Error for Station " + stationId + " ===");
        System.out.println("Current Error Status: " + station.getTransactionStatus());
        
        station.setNeedsAssistance(false);
        station.setTransactionStatus("InProgress");
        
        System.out.println("Error has been overridden.");
        System.out.println("New Status: InProgress");
        System.out.println("Station returned to normal operation.");
    }

}