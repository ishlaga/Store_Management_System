package returns;

import returns.model.Return;
import returns.model.ReturnItem;
import returns.service.ReturnManager;
import returns.service.ReturnAnalyticsService;
import inventory.service.InventoryManager;
import inventory.service.HeadOfficeManager;
import inventory.model.Product;
import java.util.Map;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Main system class that handles the user interface and interaction
 * for processing product returns and refunds.
 *
 * @author Hrishikesha
 */
public class ReturnManagementSystem {
    // Manager for handling return operations
    private ReturnManager returnManager;
    // Manager for inventory operations
    private InventoryManager inventoryManager;
    // Scanner for user input
    private Scanner scanner;
    // Store identifier
    private String storeId;
    // Analytics service for generating reports
    private ReturnAnalyticsService analyticsService;

    /**
     * Constructor initializes the system with a specific store ID
     * @param storeId The unique identifier for the store
     */
    public ReturnManagementSystem(String storeId) {
        this.storeId = storeId;
        this.inventoryManager = new InventoryManager(new HeadOfficeManager());
        this.inventoryManager.setCurrentStore(storeId);
        this.returnManager = new ReturnManager(storeId, inventoryManager);
        this.analyticsService = new ReturnAnalyticsService(returnManager, storeId);
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\nReturn Management System");
            // Customer Service Operations
            System.out.println("1. Process New Return (Customer Service)");
            
            // Manager Operations
            System.out.println("2. Approve Pending Returns (Store Manager)");
            System.out.println("3. Update Inventory for Returns (Store Manager)");
            
            // Reporting and History
            System.out.println("4. View Return History (Store Manager)");
            System.out.println("5. Generate Returns Analytics Report (Store Manager)");
            
            System.out.println("6. Return to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    processNewReturn();
                    break;
                case 2:
                    approvePendingReturns();
                    break;
                case 3:
                    updateInventoryForReturns();
                    break;
                case 4:
                    viewReturnHistory();
                    break;
                case 5:
                    analyticsService.generateAnalyticsReport();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private boolean verifyOrder(String orderId) {
        String orderFileName = "./src/main/java/store/data/" + storeId + "_orders.txt";
        File orderFile = new File(orderFileName);
        
        if (!orderFile.exists()) {
            return false;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(orderFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[1].equals(orderId)) {
                    LocalDate orderDate = LocalDate.parse(parts[0]);
                    // Check if return is within 30 days
                    return !orderDate.plusDays(30).isBefore(LocalDate.now());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading order data: " + e.getMessage());
        }
        return false;
    }

    private void processNewReturn() {
        System.out.print("Enter original order ID: ");
        String orderId = scanner.nextLine();
        
        if (!verifyOrder(orderId)) {
            System.out.println("Invalid order ID or order not eligible for return (30-day return policy)");
            return;
        }

        String returnId = "RET" + System.currentTimeMillis();
        Return returnOrder = new Return(returnId, orderId, storeId);

        while (true) {
            System.out.print("Enter product ID to return (or 'done'): ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("done")) break;

            System.out.print("Enter quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Is item damaged? (y/n): ");
            boolean isDamaged = scanner.nextLine().equalsIgnoreCase("y");

            Product product = inventoryManager.getProduct(input);
            if (product != null) {
                returnOrder.addItem(product, quantity, isDamaged);
            }
        }

        System.out.println("Select refund method:");
        System.out.println("1. Cash");
        System.out.println("2. Card");
        System.out.println("3. Store Credit");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String refundMethod;
        switch (choice) {
            case 1:
                refundMethod = "CASH";
                break;
            case 2:
                refundMethod = "CARD";
                break;
            case 3:
                refundMethod = "STORE_CREDIT";
                break;
            default:
                refundMethod = null;
        }

        if (refundMethod != null) {
            returnOrder.setRefundMethod(refundMethod);
            if (returnManager.processReturn(returnOrder)) {
                System.out.println("Return request processed successfully");
                System.out.println("Refund amount: $" + 
                    String.format("%.2f", returnOrder.getRefundAmount()) + " (pending approval)");
                // Print and save receipt
                returnManager.printReturnReceipt(returnOrder);
            }
        }
    }

        private void viewReturnHistory() {
        System.out.println("\nReturn History:");
        System.out.println("--------------------------------------------------");
        System.out.printf("%-12s %-12s %-10s %-8s %s%n", 
            "Return ID", "Order ID", "Amount", "Status", "Date");
        System.out.println("--------------------------------------------------");
        
        Map<String, Return> returns = returnManager.getAllReturns();
        for (Return ret : returns.values()) {
            System.out.printf("%-12s %-12s $%-9.2f %-8s %s%n",
                ret.getReturnId(),
                ret.getOriginalOrderId(),
                ret.getRefundAmount(),
                ret.isApproved() ? "Approved" : "Pending",
                ret.getReturnDate().toLocalDate());
        }
        System.out.println("--------------------------------------------------");
    }

    private void approvePendingReturns() {
        System.out.println("\nPending Returns:");
        Map<String, Return> returns = returnManager.getPendingReturns();
        
        if (returns.isEmpty()) {
            System.out.println("No pending returns");
            return;
        }

        for (Return ret : returns.values()) {
            System.out.println("\nReturn ID: " + ret.getReturnId());
            System.out.println("Order ID: " + ret.getOrderId());
            System.out.println("Items:");
            
            for (Map.Entry<Product, ReturnItem> entry : ret.getItems().entrySet()) {
                Product product = entry.getKey();
                ReturnItem item = entry.getValue();
                System.out.printf("- %s (Quantity: %d, Damaged: %s)%n",
                    product.getName(), item.getQuantity(), item.isDamaged() ? "Yes" : "No");
            }
            
            System.out.print("Approve this return? (yes/no): ");
            String response = scanner.nextLine();
            
            if (response.equalsIgnoreCase("yes")) {
                // Update inventory for undamaged items
                for (Map.Entry<Product, ReturnItem> entry : ret.getItems().entrySet()) {
                    Product product = entry.getKey();
                    ReturnItem item = entry.getValue();
                    
                    if (!item.isDamaged()) {
                        boolean updated = inventoryManager.restockProduct(product.getId(), item.getQuantity());
                        if (updated) {
                            System.out.printf("Inventory updated: Added %d %s back to stock%n",
                                item.getQuantity(), product.getName());
                        } else {
                            System.out.printf("Failed to update inventory for %s%n", product.getName());
                        }
                    }
                }
                returnManager.approveReturn(ret.getReturnId());
                System.out.println("Return approved and inventory updated");
            } else {
                System.out.println("Return not approved");
            }
        }
    }

    private void updateInventoryForReturns() {
        if (returnManager.processApprovedReturns()) {
            System.out.println("Inventory updated successfully for approved returns");
        } else {
            System.out.println("No pending inventory updates");
        }
    }
}