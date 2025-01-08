package order;

import inventory.service.InventoryManager;
import inventory.service.HeadOfficeManager;
import inventory.model.Product;
import order.model.Order;
import java.util.Map;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.io.File;

/**
 * Main system class that handles the user interface and interaction
 * for order management operations.
 *
 * @author Akhilesh Nevatia
 * @author Hrishikesha
 */
public class OrderManagementSystem {
    // Manager instance to handle inventory operations
    private InventoryManager inventoryManager;
    // Scanner for user input
    private Scanner scanner;
    // Current active order being processed
    private Order currentOrder;
    // Store identifier
    private String storeId;

    /**
     * Constructor initializes the system with a specific store ID
     * @param storeId The unique identifier for the store
     */
    public OrderManagementSystem(String storeId) {
        this.storeId = storeId;
        this.inventoryManager = new InventoryManager(new HeadOfficeManager());
        this.inventoryManager.setCurrentStore(storeId);
        this.scanner = new Scanner(System.in);
        
        // Create data directory if it doesn't exist
        new File("./src/main/java/store/data").mkdirs();
    }

    public void start() {
        while (true) {
            System.out.println("\nOrder Management System");
            System.out.println("1. New Order ( Customer )");
            System.out.println("2. View Available Products ( Management System)");
            System.out.println("3. Add Item to Order ( Cashier)");
            System.out.println("4. Process Payment (Payment Processor )");
            System.out.println("5. Cancel Order ( Cashier )");
            System.out.println("6. View Store Orders ( Store Manager )");
            System.out.println("7. Return to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    createNewOrder();
                    break;
                case 2:
                    viewAvailableProducts();
                    break;
                case 3:
                    addItemToOrder();
                    break;
                case 4:
                    processPayment();
                    break;
                case 5:
                    cancelOrder();
                    break;
                case 6:
                    viewStoreOrders();
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    /**
     * Adds an item to the current order
     * @param productId The ID of the product to add
     * @param quantity The quantity to add
     */
    private void addItemToOrder() {
        if (currentOrder == null) {
            System.out.println("Please create a new order first");
            return;
        }

        System.out.print("Enter product ID: ");
        String productId = scanner.nextLine();
        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Product product = inventoryManager.getProduct(productId);
        if (product == null) {
            System.out.println("Product not found");
            return;
        }

        if (product.getStockLevel() < quantity) {
            System.out.println("Insufficient stock. Available: " + product.getStockLevel());
            return;
        }

        // Only add to order, don't update inventory yet
        currentOrder.addItem(product, quantity);
        System.out.println("Item added to order. Current total: $" + 
            String.format("%.2f", currentOrder.getTotalAmount()));
    }

    private void createNewOrder() {
        if (currentOrder != null) {
            System.out.println("There's already an active order. Please complete or cancel it first.");
            return;
        }
        System.out.print("Enter order ID: ");
        String orderId = scanner.nextLine();
        currentOrder = new Order(orderId, storeId);
        System.out.println("New order created successfully");
    }

    private void cancelOrder() {
        if (currentOrder == null) {
            System.out.println("No active order to cancel");
            return;
        }
        currentOrder = null;
        System.out.println("Order cancelled successfully");
    }

    private void updateInventory() {
        for (Map.Entry<Product, Integer> entry : currentOrder.getItems().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            inventoryManager.restockProduct(product.getId(), -quantity); // Deduct stock
        }
    }

    private boolean verifyStockLevels() {
        for (Map.Entry<Product, Integer> entry : currentOrder.getItems().entrySet()) {
            Product orderedProduct = entry.getKey();
            int orderedQuantity = entry.getValue();
            
            // Get fresh product data from inventory
            Product currentProduct = inventoryManager.getProduct(orderedProduct.getId());
            if (currentProduct == null || currentProduct.getStockLevel() < orderedQuantity) {
                System.out.println("Error: Insufficient stock for " + orderedProduct.getName() + 
                                 ". Available: " + currentProduct.getStockLevel() + 
                                 ", Ordered: " + orderedQuantity);
                return false;
            }
        }
        return true;
    }

    private void viewAvailableProducts() {
        System.out.println("\nAvailable Products:");
        System.out.println("--------------------------------------------------");
        System.out.printf("%-8s %-15s %-10s %-8s %s%n", 
            "ID", "Name", "Price", "Stock", "Expiry Date");
        System.out.println("--------------------------------------------------");
        
        Map<String, Product> products = inventoryManager.getAllProducts();
        for (Product product : products.values()) {
            if (!product.isObsolete() && product.getStockLevel() > 0) {
                System.out.printf("%-8s %-15s $%-9.2f %-8d %s%n",
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getStockLevel(),
                    product.getExpirationDate());
            }
        }
        System.out.println("--------------------------------------------------");
    }

    private void processPayment() {
        if (currentOrder == null) {
            System.out.println("No active order to process");
            return;
        }

        // Verify stock levels before processing payment
        if (!verifyStockLevels()) {
            System.out.println("Cannot process payment due to insufficient stock");
            return;
        }

        System.out.println("Total amount: $" + String.format("%.2f", currentOrder.getTotalAmount()));
        System.out.println("Select payment method:");
        System.out.println("1. Cash");
        System.out.println("2. Card");
        System.out.println("3. Mobile");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String paymentMethod;
        switch (choice) {
            case 1:
                paymentMethod = "CASH";
                break;
            case 2:
                paymentMethod = "CARD";
                break;
            case 3:
                paymentMethod = "MOBILE";
                break;
            default:
                paymentMethod = null;
        }

        if (paymentMethod != null) {
            // Update inventory only after payment is confirmed
            for (Map.Entry<Product, Integer> entry : currentOrder.getItems().entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                inventoryManager.restockProduct(product.getId(), -quantity);
            }
            
            currentOrder.setPaymentMethod(paymentMethod);
            currentOrder.markAsPaid();
            saveSalesData();
            System.out.println("Payment processed successfully");
            currentOrder = null;
        } else {
            System.out.println("Invalid payment method");
        }
    }

    private void saveSalesData() {
        // Save sales data
        String salesFileName = "./src/main/java/store/data/" + storeId + "_sales.txt";
        // Save order data
        String orderFileName = "./src/main/java/store/data/" + storeId + "_orders.txt";
        
        try (BufferedWriter salesWriter = new BufferedWriter(new FileWriter(salesFileName, true));
             BufferedWriter orderWriter = new BufferedWriter(new FileWriter(orderFileName, true))) {
            
            // Save order details
            orderWriter.write(String.format("%s,%s,%s,%s,%.2f%n",
                LocalDate.now(),
                currentOrder.getOrderId(),
                storeId,
                currentOrder.getPaymentMethod(),
                currentOrder.getTotalAmount()));
            
            // Save individual sales
            for (Map.Entry<Product, Integer> entry : currentOrder.getItems().entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                double revenue = product.getPrice() * quantity;
                
                salesWriter.write(String.format("%s,%s,%d,%.2f%n",
                    LocalDate.now(),
                    product.getId(),
                    quantity,
                    revenue));
            }
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    private void viewStoreOrders() {
        String orderFileName = "./src/main/java/store/data/" + storeId + "_orders.txt";
        File orderFile = new File(orderFileName);
        
        if (!orderFile.exists()) {
            System.out.println("No orders found for store " + storeId);
            return;
        }

        System.out.println("\nStore Orders for " + storeId);
        System.out.println("--------------------------------------------------");
        System.out.printf("%-12s %-15s %-10s %-10s %s%n", 
            "Date", "Order ID", "Store ID", "Payment", "Amount");
        System.out.println("--------------------------------------------------");

        try (Scanner fileScanner = new Scanner(orderFile)) {
            while (fileScanner.hasNextLine()) {
                String[] orderData = fileScanner.nextLine().split(",");
                if (orderData.length == 5) {
                    System.out.printf("%-12s %-15s %-10s %-10s $%.2f%n",
                        orderData[0],    // Date
                        orderData[1],    // Order ID
                        orderData[2],    // Store ID
                        orderData[3],    // Payment Method
                        Double.parseDouble(orderData[4])); // Amount
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading orders: " + e.getMessage());
        }
        System.out.println("--------------------------------------------------");
    }
}