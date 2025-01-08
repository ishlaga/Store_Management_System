package greeter.service;

import greeter.model.GreeterReceipt;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GreeterManager {
    private static final String DATA_FILE = "./src/main/java/greeter/data/greeter_data.txt";
    private List<GreeterReceipt> receipts;
    private Scanner scanner;

    public GreeterManager() {
        this.scanner = new Scanner(System.in);
        loadReceipts();
    }

    private void loadReceipts() {
        receipts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            reader.readLine(); // Skip header line 1
            reader.readLine(); // Skip separator line 2
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    GreeterReceipt r = GreeterReceipt.fromLine(line);
                    if (r != null) {
                        receipts.add(r);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading receipts: " + e.getMessage());
        }
    }

    public void startGreeterVerification() {
        while (true) {
            System.out.println("\n=== Greeter Verification System ===");
            System.out.println("1. Verify Customer Receipt at Exit");
            System.out.println("2. View All Receipts");
            System.out.println("3. Return to Main Menu");
            System.out.print("Choose an option: ");
            
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input");
                continue;
            }

            switch (choice) {
                case 1:
                    verifyReceipt();
                    break;
                case 2:
                    viewAllReceipts();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private void verifyReceipt() {
        System.out.print("Enter Receipt ID: ");
        String id = scanner.nextLine().trim();
        GreeterReceipt receipt = findReceiptById(id);
    
        if (receipt == null) {
            System.out.println("Receipt not found. Please verify the ID or ask the customer for another proof of purchase.");
            return;
        }
    
        System.out.println("Receipt found. Items listed:");
        for (String item : receipt.getItems()) {
            System.out.println("- " + item);
        }
        System.out.printf("Total Amount Paid: $%.2f%n", receipt.getTotalAmount());
    
        System.out.println("\nNow, Greeter visually inspects the customer's items.");
        System.out.println("Enter the items the customer is carrying (comma-separated):");
        String customerItemsLine = scanner.nextLine().trim();
        List<String> customerItems = Arrays.stream(customerItemsLine.split(","))
                                           .map(String::trim)
                                           .toList();
    
        // Check for mismatch
        boolean mismatchFound = false;
        // Check if the customer has any item not in the receipt
        for (String cItem : customerItems) {
            if (!receipt.getItems().contains(cItem)) {
                mismatchFound = true;
                break;
            }
        }
    
        // If no mismatch found yet, also consider if the customer might be missing items or total count off
        // For simplicity, we assume the greeter only cares about extra items. 
        // If needed, you could also check if receipt items are all present.
    
        if (mismatchFound) {
            System.out.println("\nMismatch detected! The customer has items not listed on the receipt.");
            System.out.println("Alerting Security and Store Manager...");
            // Here you could call another method or system for Security handling, e.g.:
            // securityManager.handleTheftSuspicion();
            // For now, we just print a message.
            System.out.println("Security notified. Customer cannot exit until resolved.");
        } else {
            System.out.println("\nAll items match the receipt. Customer may exit. Have a nice day!");
        }
    }
    

    private void viewAllReceipts() {
        if (receipts.isEmpty()) {
            System.out.println("No receipts available.");
            return;
        }

        System.out.println("\nAvailable Receipts:");
        for (GreeterReceipt r : receipts) {
            System.out.printf("%s: %s - $%.2f%n", r.getReceiptId(),
                r.getItems().stream().collect(Collectors.joining(", ")),
                r.getTotalAmount());
        }
    }

    private GreeterReceipt findReceiptById(String id) {
        return receipts.stream()
            .filter(r -> r.getReceiptId().equalsIgnoreCase(id))
            .findFirst()
            .orElse(null);
    }
}
