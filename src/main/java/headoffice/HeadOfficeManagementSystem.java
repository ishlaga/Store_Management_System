package headoffice;

import store.service.StoreManager;
import store.model.Store;
import inventory.InventoryManagementSystem;
import supplier.SupplierManagementSystem;
import java.util.Scanner;
import java.time.LocalDate;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

/**
 * Head Office Management System for managing store operations, performance tracking, and administration.
 * @author @AkhileshNevatia
 */
public class HeadOfficeManagementSystem {
    private StoreManager storeManager;
    private Scanner scanner;
    private static final String RECEIPTS_DIR = "src/main/java/store/data/receipts/";
    private StorePerformanceSystem performanceSystem;

    /**
     * Initializes the Head Office Management System with store management capabilities.
     * @param storeManager The store manager instance to handle store operations
     */
    public HeadOfficeManagementSystem(StoreManager storeManager) {
        this.storeManager = storeManager;
        this.scanner = new Scanner(System.in);
        this.performanceSystem = new StorePerformanceSystem(storeManager);
        // Ensure receipts directory exists
        File dir = new File(RECEIPTS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Starts the head office management interface with main menu options.
     * Provides options for adding/removing stores and viewing store information.
     */
    public void start() {
        while (true) {
            System.out.println("\n--- Head Office Management ---");
            System.out.println("1. Add New Store (Head Office Manager)");
            System.out.println("2. Remove Existing Store (Head Office Manager)");
            System.out.println("3. View All Stores (Head Office Manager)");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addNewStore();
                    break;
                case 2:
                    removeStore();
                    break;
                case 3:
                    viewAllStores();
                    break;
                case 4:
                    return; // Exit Head Office Management
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    /**
     * Displays information about all stores in the system including their IDs, names,
     * locations, opening dates, and assigned managers.
     */
    private void viewAllStores() {
        System.out.println("\n--- All Stores ---");
        for (Store store : storeManager.getAllStores()) {
            String manager = storeManager.getManagerByStoreId(store.getStoreId());
            System.out.printf("Store ID: %s%nName: %s%nLocation: %s%nOpening Date: %s%nManager: %s%n%n",
                    store.getStoreId(),
                    store.getName(),
                    store.getLocation(),
                    store.getOpeningDate(),
                    manager != null ? manager : "No Manager Assigned");
        }
    }

    /**
     * Handles the process of adding a new store to the system.
     * Includes store creation, inventory setup, supplier notification, and manager assignment.
     */
    private void addNewStore() {
        System.out.println("\n--- Add New Store ---");
        
        // Generate a unique Store ID
        String storeId = storeManager.generateUniqueStoreId();
        System.out.println("Generated Store ID: " + storeId);

        System.out.print("Enter Store Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Store Location: ");
        String location = scanner.nextLine().trim();

        System.out.print("Enter Opening Date (YYYY-MM-DD): ");
        String dateInput = scanner.nextLine().trim();
        LocalDate openingDate;
        try {
            openingDate = LocalDate.parse(dateInput);
        } catch (Exception e) {
            System.out.println("Invalid date format. Operation aborted.");
            return;
        }

        // Create and add the new store
        Store newStore = new Store(storeId, name, location, openingDate);
        storeManager.addStore(newStore);

        // Setup initial inventory
        InventoryManagementSystem inventorySystem = new InventoryManagementSystem(storeId);
        inventorySystem.setupInitialInventory();

        // Notify supplier to prepare and ship inventory
        SupplierManagementSystem supplierSystem = new SupplierManagementSystem(storeId);
        supplierSystem.prepareInitialShipment();

        // Assign a Store Manager
        assignStoreManager(newStore);

        System.out.println("Store setup complete and status updated.");
    }

    /**
     * Handles the process of removing a store from the system.
     * Manages inventory redistribution, physical removal, and data archiving.
     */
    private void removeStore() {
        System.out.println("\n--- Remove Store ---");
        System.out.print("Enter Store ID to remove: ");
        String storeId = scanner.nextLine().trim();

        Store store = storeManager.getStoreById(storeId);
        if (store == null) {
            System.out.println("Store ID not found. Operation aborted.");
            return;
        }

        System.out.print("Enter Closure Date (YYYY-MM-DD): ");
        String dateInput = scanner.nextLine().trim();
        LocalDate closureDate;
        try {
            closureDate = LocalDate.parse(dateInput);
        } catch (Exception e) {
            System.out.println("Invalid date format. Operation aborted.");
            return;
        }

        // Handle inventory redistribution or liquidation
        InventoryManagementSystem inventorySystem = new InventoryManagementSystem(storeId);
        inventorySystem.handleStoreClosure();

        // Oversee physical removal of inventory
        overseePhysicalRemoval(storeId);

        // Deactivate store and archive data
        boolean removed = storeManager.removeStore(storeId);
        if (removed) {
            System.out.println("Store removed successfully and data archived.");
        } else {
            System.out.println("Failed to remove store.");
        }
    }

    /**
     * Assigns a manager to a specific store.
     * @param store The store to assign a manager to
     */
    private void assignStoreManager(Store store) {
        System.out.print("Enter Manager Name for store " + store.getStoreId() + ": ");
        String managerName = scanner.nextLine().trim();
        
        // Check if the manager already exists for the store
        String existingManager = storeManager.getManagerByStoreId(store.getStoreId());
        if (existingManager != null) {
            System.out.println("Store already has a manager: " + existingManager);
        } else {
            storeManager.addManager(store.getStoreId(), managerName);
            System.out.println("Assigned Manager '" + managerName + "' to store: " + store.getStoreId());
        }
    }

    /**
     * Manages the physical removal process of store inventory.
     * @param storeId ID of the store being closed
     */
    private void overseePhysicalRemoval(String storeId) {
        System.out.println("\nOverseeing physical removal of inventory for store: " + storeId);
        // Implementation details can be expanded as needed
        System.out.println("Physical removal completed for store: " + storeId);
    }

    /**
     * Provides interface for viewing store performance metrics including leaderboard,
     * detailed metrics, and sales projections.
     */
    public void viewStorePerformance() {
        while (true) {
            System.out.println("\n--- Store Performance Analytics ---");
            System.out.println("1. View Store Leaderboard");
            System.out.println("2. View Detailed Store Metrics");
            System.out.println("3. Generate Sales Projections");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    performanceSystem.generateLeaderboard();
                    break;
                case 2:
                    System.out.print("Enter Store ID: ");
                    String storeId = scanner.nextLine();
                    performanceSystem.viewDetailedMetrics(storeId);
                    break;
                case 3:
                    System.out.print("Enter Store ID: ");
                    String projectionStoreId = scanner.nextLine();
                    performanceSystem.generateSalesProjections(projectionStoreId);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }
}