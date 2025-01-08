package deli.service;

import deli.model.DeliItem;
import deli.model.DeliOrder;
import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class DeliCounterManager {
    private static final String INVENTORY_FILE = "./src/main/java/deli/data/deli_inventory.txt";
    private static final String ORDERS_FILE = "./src/main/java/deli/data/deli_orders.txt";
    private static final int LABEL_START = 1000;
    private static final int TOKEN_START = 100;

    private List<DeliItem> inventory;
    private Map<Integer, DeliOrder> activeOrders;
    private Scanner scanner;
    private int currentLabel;
    private int currentToken;
    private DateTimeFormatter formatter;

    public DeliCounterManager() {
        this.inventory = new ArrayList<>();
        this.activeOrders = new HashMap<>();
        this.scanner = new Scanner(System.in);
        this.currentLabel = LABEL_START;
        this.currentToken = TOKEN_START;
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        loadInventory();
        loadActiveOrders();
    }

    public void startDeliOperations() {
        while (true) {
            System.out.println("\n=== Deli Counter Operations ===");
            System.out.println("1. View Deli Inventory");
            System.out.println("2. Take Customer Order");
            System.out.println("3. View All Orders");
            System.out.println("4. Check Inventory Levels");
            System.out.println("5. Restock Inventory");
            System.out.println("6. Mark Order Ready");
            System.out.println("7. Collect Order");
            System.out.println("8. View Active Orders");
            System.out.println("9. Return to Main Menu");
            System.out.print("Choose an option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1: viewInventory(); break;
                    case 2: takeCustomerOrder(); break;
                    case 3: viewAllOrders(); break;
                    case 4: checkInventoryLevels(); break;
                    case 5: restockInventory(); break;
                    case 6: markOrderReady(); break;
                    case 7: collectOrder(); break;
                    case 8: viewActiveOrders(); break;
                    case 9: return;
                    default: System.out.println("Invalid option");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void loadInventory() {
        try (BufferedReader reader = new BufferedReader(new FileReader(INVENTORY_FILE))) {
            reader.readLine(); // Skip header
            reader.readLine(); // Skip separator
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    DeliItem item = DeliItem.fromLine(line);
                    if (item != null) {
                        inventory.add(item);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading inventory: " + e.getMessage());
        }
    }

    private void loadActiveOrders() {
        List<DeliOrder> allOrders = loadOrders();
        for (DeliOrder order : allOrders) {
            if (!order.getStatus().equals("COLLECTED")) {
                activeOrders.put(order.getTokenNumber(), order);
                // Update current token if needed
                if (order.getTokenNumber() >= currentToken) {
                    currentToken = order.getTokenNumber() + 1;
                }
            }
        }
    }

    private void viewInventory() {
        if (inventory.isEmpty()) {
            System.out.println("No items in inventory.");
            return;
        }

        System.out.println("\nDeli Inventory:");
        System.out.println("-".repeat(80));
        for (DeliItem item : inventory) {
            System.out.printf("%s - %s: $%.2f per %s, Stock: %d%n",
                item.getItemId(), item.getName(), 
                item.getPricePerUnit(), item.getUnitType(), 
                item.getStockLevel());
            if (!item.getAllergens().isEmpty()) {
                System.out.printf("   Allergens: %s%n", 
                    String.join(", ", item.getAllergens()));
            }
        }
        System.out.println("-".repeat(80));
    }

    private void takeCustomerOrder() {
        List<String> orderedItems = new ArrayList<>();
        List<String> allergyWarnings = new ArrayList<>();
        double totalPrice = 0.0;

        System.out.println("\nDoes the customer have any allergies? (Y/N)");
        boolean checkAllergies = scanner.nextLine().trim().equalsIgnoreCase("Y");
        List<String> customerAllergies = new ArrayList<>();
        
        if (checkAllergies) {
            System.out.println("Enter allergies (comma-separated):");
            customerAllergies = Arrays.asList(scanner.nextLine().toLowerCase().split(","))
                .stream()
                .map(String::trim)
                .collect(Collectors.toList());
        }

        System.out.println("\nTaking new order. Enter 'done' when finished.");

        while (true) {
            System.out.print("\nEnter item ID or 'done': ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("done")) break;

            DeliItem item = findItemById(input);
            if (item == null) {
                System.out.println("Item not found.");
                continue;
            }

            if (checkAllergies && !item.getAllergens().isEmpty()) {
                boolean hasAllergen = false;
                for (String allergen : item.getAllergens()) {
                    if (customerAllergies.contains(allergen.toLowerCase())) {
                        hasAllergen = true;
                        allergyWarnings.add(item.getName() + " contains " + allergen);
                    }
                }
                if (hasAllergen) {
                    System.out.println(" WARNING: This item contains allergens!");
                    System.out.print("Proceed with order? (Y/N): ");
                    if (!scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                        continue;
                    }
                }
            }

            System.out.printf("Enter quantity (%s): ", item.getUnitType());
            double quantity;
            try {
                quantity = Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity.");
                continue;
            }

            if (quantity <= 0 || quantity > item.getStockLevel()) {
                System.out.println("Invalid quantity or insufficient stock. Available: " + 
                    item.getStockLevel());
                continue;
            }

            double cost = quantity * item.getPricePerUnit();
            totalPrice += cost;
            orderedItems.add(String.format("%s x%.1f %s", 
                item.getName(), quantity, item.getUnitType()));

            item.reduceStock((int)Math.ceil(quantity));
            System.out.printf("Added: %s - $%.2f%n", item.getName(), cost);
        }

        if (!orderedItems.isEmpty()) {
            int tokenNum = currentToken++;
            String orderId = "O" + System.currentTimeMillis();
            DeliOrder order = new DeliOrder(orderId, orderedItems, totalPrice, 
                currentLabel++, tokenNum);
            
            activeOrders.put(tokenNum, order);
            saveOrder(order);
            generateReceipt(order, allergyWarnings);
            updateInventoryFile();
        } else {
            System.out.println("Order cancelled - no items added.");
        }
    }

    private void generateReceipt(DeliOrder order, List<String> allergyWarnings) {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("          DELI ORDER RECEIPT          ");
        System.out.println("=".repeat(40));
        System.out.printf("Token #: %d%n", order.getTokenNumber());
        System.out.printf("Label #: %d%n", order.getLabelNumber());
        System.out.printf("Order ID: %s%n", order.getOrderId());
        System.out.printf("Date: %s%n", 
            order.getOrderTime().format(formatter));
        
        System.out.println("\nOrdered Items:");
        System.out.println("-".repeat(30));
        for (String item : order.getItems()) {
            System.out.println("  " + item);
        }

        if (!allergyWarnings.isEmpty()) {
            System.out.println("\n ALLERGY WARNINGS ");
            System.out.println("-".repeat(30));
            allergyWarnings.forEach(warning -> 
                System.out.println("  - " + warning));
        }

        System.out.printf("%nTotal Price: $%.2f%n", order.getTotalPrice());
        System.out.println("\nPlease proceed to:");
        System.out.println("1. Self-Checkout Stations");
        System.out.println("2. Regular Checkout Counters");
        System.out.println("\nKeep your token number to collect your order!");
        System.out.println("=".repeat(40));
    }

    private void markOrderReady() {
        System.out.print("Enter token number: ");
        try {
            int token = Integer.parseInt(scanner.nextLine());
            DeliOrder order = activeOrders.get(token);
            
            if (order != null && order.getStatus().equals("PREPARING")) {
                order.setStatus("READY");
                updateOrderInFile(order);
                System.out.println("Order " + token + " is now ready!");
                System.out.println("\n📢 ANNOUNCEMENT: Order number " + token + " is ready for collection!");
            } else {
                System.out.println("Order not found or already processed.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid token number.");
        }
    }

    private void collectOrder() {
        System.out.print("Enter token number: ");
        try {
            int token = Integer.parseInt(scanner.nextLine());
            DeliOrder order = activeOrders.get(token);
            
            if (order != null) {
                switch (order.getStatus()) {
                    case "READY":
                        order.setStatus("COLLECTED");
                        updateOrderInFile(order);
                        activeOrders.remove(token);
                        printCollectionReceipt(order);
                        break;
                    case "PREPARING":
                        System.out.println("Your order is still being prepared.");
                        System.out.println("Please wait for the announcement.");
                        break;
                    case "COLLECTED":
                        System.out.println("This order has already been collected.");
                        break;
                }
            } else {
                System.out.println("Order not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid token number.");
        }
    }

    private void printCollectionReceipt(DeliOrder order) {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("      ORDER COLLECTION RECEIPT       ");
        System.out.println("=".repeat(40));
        System.out.printf("Token #: %d%n", order.getTokenNumber());
        System.out.printf("Collection Time: %s%n", 
            LocalDateTime.now().format(formatter));
        System.out.println("\nCollected Items:");
        order.getItems().forEach(item -> System.out.println("  - " + item));
        System.out.println("=".repeat(40));
    }

    private void viewActiveOrders() {
        if (activeOrders.isEmpty()) {
            System.out.println("No active orders.");
            return;
        }

        System.out.println("\nActive Orders:");
        System.out.println("-".repeat(50));
        for (DeliOrder order : activeOrders.values()) {
            System.out.printf("Token #%d | Status: %s | Items: %d | Total: $%.2f%n",
                order.getTokenNumber(),
                order.getStatus(),
                order.getItems().size(),
                order.getTotalPrice());
        }
        System.out.println("-".repeat(50));
    }

    private void viewAllOrders() {
        List<DeliOrder> orders = loadOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }

        System.out.println("\nAll Orders:");
        System.out.println("-".repeat(80));
        for (DeliOrder order : orders) {
            System.out.printf("Token #%d | Label #%d | Status: %s | Items: %s | Total: $%.2f%n",
                order.getTokenNumber(),
                order.getLabelNumber(),
                order.getStatus(),
                String.join(", ", order.getItems()),
                order.getTotalPrice());
        }
        System.out.println("-".repeat(80));
    }

    private void checkInventoryLevels() {
        System.out.println("\nInventory Status Report");
        System.out.println("-".repeat(50));
        boolean needsRestock = false;

        for (DeliItem item : inventory) {
            if (item.needsRestocking()) {
                needsRestock = true;
                System.out.printf(" Low Stock: %s - Current Level: %d%n", 
                    item.getName(), item.getStockLevel());
            }
        }

        if (!needsRestock) {
            System.out.println("All items at adequate levels.");
        }
        System.out.println("-".repeat(50));
    }

    private void restockInventory() {
        System.out.println("\nRestock Inventory");
        System.out.println("-".repeat(50));
        boolean itemsRestocked = false;

        for (DeliItem item : inventory) {
            if (item.needsRestocking()) {
                System.out.printf("%s - Current Stock: %d%n", 
                    item.getName(), item.getStockLevel());
                System.out.print("Enter amount to add (0 to skip): ");
                
                try {
                    int amount = Integer.parseInt(scanner.nextLine());
                    if (amount > 0) {
                        item.addStock(amount);
                        itemsRestocked = true;
                        System.out.printf("Updated stock: %d%n", item.getStockLevel());
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid amount. Skipping.");
                }
            }
        }

        if (itemsRestocked) {
            updateInventoryFile();
            System.out.println("Inventory updated successfully.");
        } else {
            System.out.println("No items restocked.");
        }
    }

    private DeliItem findItemById(String id) {
        return inventory.stream()
            .filter(i -> i.getItemId().equalsIgnoreCase(id))
            .findFirst()
            .orElse(null);
    }

    private List<DeliOrder> loadOrders() {
        List<DeliOrder> orders = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ORDERS_FILE))) {
            reader.readLine(); // Skip header
            reader.readLine(); // Skip separator
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 6) {
                        String orderId = parts[0].trim();
                        List<String> items = Arrays.asList(parts[1].split(","))
                            .stream()
                            .map(String::trim)
                            .collect(Collectors.toList());
                        double price = Double.parseDouble(parts[2].trim());
                        int labelNumber = Integer.parseInt(parts[3].trim());
                        int tokenNumber = Integer.parseInt(parts[4].trim());
                        String status = parts[5].trim();
                        
                        DeliOrder order = new DeliOrder(orderId, items, price, labelNumber, tokenNumber);
                        order.setStatus(status);
                        orders.add(order);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading orders: " + e.getMessage());
        }
        return orders;
    }

    private void saveOrder(DeliOrder order) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDERS_FILE, true))) {
            writer.write(order.toLine());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving order: " + e.getMessage());
        }
    }

    private void updateOrderInFile(DeliOrder updatedOrder) {
        List<DeliOrder> orders = loadOrders();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDERS_FILE))) {
            // Write header
            writer.write("OrderID | Items | TotalPrice | LabelNumber | TokenNumber | Status");
            writer.newLine();
            writer.write("-".repeat(70));
            writer.newLine();
            
            // Update and write all orders
            for (DeliOrder order : orders) {
                if (order.getTokenNumber() == updatedOrder.getTokenNumber()) {
                    writer.write(updatedOrder.toLine());
                } else {
                    writer.write(order.toLine());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error updating orders file: " + e.getMessage());
        }
    }

    private void updateInventoryFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(INVENTORY_FILE))) {
            // Write header
            writer.write("ItemID | Name | PricePerUnit | UnitType | StockLevel | Allergens");
            writer.newLine();
            writer.write("-".repeat(70));
            writer.newLine();
            
            // Write updated inventory
            for (DeliItem item : inventory) {
                String allergenList = item.getAllergens().isEmpty() ? 
                    "None" : String.join(", ", item.getAllergens());
                String line = String.format("%s|%s|%.2f|%s|%d|%s",
                    item.getItemId(),
                    item.getName(),
                    item.getPricePerUnit(),
                    item.getUnitType(),
                    item.getStockLevel(),
                    allergenList);
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error updating inventory file: " + e.getMessage());
        }
    }

    public void shutdown() {
        updateInventoryFile();
        scanner.close();
        System.out.println("Deli Counter System shutting down...");
    }
}