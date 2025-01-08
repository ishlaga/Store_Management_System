package supplier;

import supplier.service.SupplierManager;
import supplier.model.Shipment;
import inventory.service.InventoryManager;
import inventory.service.HeadOfficeManager;
import inventory.model.Product;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Main system class that handles the user interface and interaction
 * for supplier management and shipment operations.
 *
 * @author Eshant Chintareddy
 */
public class SupplierManagementSystem {
    // Manager for supplier operations
    private SupplierManager supplierManager;
    // Scanner for user input
    private Scanner scanner;
    // Store identifier
    private String storeId;

    /**
     * Constructor initializes the system with a specific store ID
     * @param storeId The unique identifier for the store
     */
    public SupplierManagementSystem(String storeId) {
        this.storeId = storeId;
        InventoryManager inventoryManager = new InventoryManager(new HeadOfficeManager());
        inventoryManager.setCurrentStore(storeId);
        this.supplierManager = new SupplierManager(inventoryManager);
        this.supplierManager.setCurrentStore(storeId);
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\nSupplier Management System");
            System.out.println("1. Schedule New Shipment (Supplier Manager)");
            System.out.println("2. Verify Received Shipment (Inventory Clerk)");
            System.out.println("3. View Suppliers and Ratings (Store Manager)");
            System.out.println("4. View Pending Shipments (Supplier Manager)");
            System.out.println("5. Return to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    scheduleShipment();
                    break;
                case 2:
                    verifyShipment();
                    break;
                case 3:
                    viewSupplierRatings();
                    break;
                case 4:
                    viewPendingShipments();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private void scheduleShipment() {
        System.out.println("\nSchedule New Shipment");
        System.out.print("Enter Supplier ID: ");
        String supplierId = scanner.nextLine();

        if (!supplierManager.supplierExists(supplierId)) {
            System.out.println("Supplier not found!");
            return;
        }

        LocalDateTime scheduledDate = LocalDateTime.now().plusDays(1);
        Shipment shipment = supplierManager.createShipment(supplierId, scheduledDate);

        while (true) {
            System.out.print("Enter Product ID (or 'done' to finish): ");
            String productId = scanner.nextLine();
            
            if (productId.equalsIgnoreCase("done")) {
                break;
            }

            System.out.print("Enter Quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (supplierManager.addItemToShipment(shipment, productId, quantity)) {
                System.out.println("Item added to shipment");
            } else {
                System.out.println("Failed to add item");
            }
        }

        if (supplierManager.confirmShipment(shipment)) {
            System.out.println("Shipment scheduled successfully. Please verify the shipment once received.");
            System.out.println("Shipment ID: " + shipment.getId());
        } else {
            System.out.println("Failed to schedule shipment");
        }
    }

    private void viewSupplierRatings() {
        System.out.println("\nSuppliers and Ratings");
        System.out.println("----------------------------------------");
        System.out.printf("%-10s %-20s %-10s%n", "ID", "Name", "Rating");
        System.out.println("----------------------------------------");
        
        for (Map.Entry<String, Double> rating : supplierManager.getAllSupplierRatings().entrySet()) {
            String supplierId = rating.getKey();
            String supplierName = supplierManager.getSupplierName(supplierId);
            System.out.printf("%-10s %-20s %.2f%n", 
                supplierId, supplierName, rating.getValue());
        }
        System.out.println("----------------------------------------");
    }

    private void viewPendingShipments() {
        System.out.println("\nPending Shipments");
        System.out.println("--------------------------------------------------");
        System.out.printf("%-10s %-10s %-20s %-15s%n", 
            "ID", "Supplier", "Scheduled Date", "Status");
        System.out.println("--------------------------------------------------");
        
        List<Shipment> pendingShipments = supplierManager.getPendingShipments();
        for (Shipment shipment : pendingShipments) {
            System.out.printf("%-10s %-10s %-20s %-15s%n",
                shipment.getId(),
                shipment.getSupplierId(),
                shipment.getScheduledDate(),
                shipment.getStatus());
        }
        System.out.println("--------------------------------------------------");
    }

    private void verifyShipment() {
        System.out.println("\nVerify Received Shipment");
        System.out.print("Enter Shipment ID: ");
        String shipmentId = scanner.nextLine();

        Shipment shipment = supplierManager.getShipment(shipmentId);
        if (shipment == null) {
            System.out.println("Shipment not found!");
            return;
        }

        System.out.println("Processing shipment verification...");
        try {
            // Simulate verification process
            Thread.sleep(5000); // 5 second delay
            System.out.println("Checking received items...");
            Thread.sleep(3000); // 3 second delay
            
            Map<String, Integer> receivedQuantities = new HashMap<>();
            for (Map.Entry<Product, Integer> entry : shipment.getItems().entrySet()) {
                Product product = entry.getKey();
                System.out.printf("Enter received quantity for %s (%s): ", 
                    product.getName(), product.getId());
                int quantity = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                receivedQuantities.put(product.getId(), quantity);
                
                System.out.println("Updating inventory...");
                Thread.sleep(2000); // 2 second delay
            }

            boolean verified = supplierManager.verifyShipment(shipmentId, receivedQuantities);
            if (verified) {
                System.out.println("\n✓ Shipment verified successfully");
                System.out.println("✓ Inventory updated");
                System.out.println("✓ Supplier rating updated");
            } else {
                System.out.println("\n⚠ Discrepancies found in shipment");
                System.out.println("✓ Inventory updated with received quantities");
                System.out.println("⚠ Supplier rating adjusted");
            }
        } catch (InterruptedException e) {
            System.out.println("Verification process interrupted");
        }
    }

    // Head Office Stuff!! - Implemented Basic Logic

    public void prepareInitialShipment() {
        System.out.println("\nPreparing initial shipment for store: " + storeId);
        // Notify supplier with order details
        System.out.println("Notifying supplier...");

        // Simulate supplier confirmation
        boolean shipmentConfirmed = simulateSupplierResponse();

        if (shipmentConfirmed) {
            System.out.println("Supplier confirmed shipment. Expected delivery in 7 days.");
        } else {
            System.out.println("Supplier delayed the shipment.");
            notifyHeadOfficeManager();
        }
    }

    private boolean simulateSupplierResponse() {
        // Simulate a supplier response with an 80% chance of success
        return Math.random() < 0.8;
    }

    private void notifyHeadOfficeManager() {
        System.out.println("Notifying Head Office Manager about the shipment delay.");
        // Implement notification logic, such as sending an email or alert
    }
}