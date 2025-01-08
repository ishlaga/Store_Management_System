package marketing.service;

import marketing.model.*;
import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MarketingManager {
    private static final String PROMOTIONS_FILE = "src/main/java/marketing/data/promotions.txt";
    private Map<String, Promotion> activePromotions;
    private Map<String, PromotionPerformance> performances;
    private Scanner scanner;
    private DateTimeFormatter formatter;
    
    // Add services
    private NotificationService notificationService;
    private InventoryService inventoryService;
    private ReportService reportService;

    public MarketingManager() {
        this.activePromotions = new HashMap<>();
        this.performances = new HashMap<>();
        this.scanner = new Scanner(System.in);
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        // Initialize services
        this.notificationService = new NotificationService();
        this.inventoryService = new InventoryService();
        this.reportService = new ReportService();
        
        loadPromotions();
    }

    public void start() {
        while (true) {
            try {
                System.out.println("\nMarketing Management");
                System.out.println("1. Create Promotion (Marketing Manager)");
                System.out.println("2. View Promotions (Customer)");
                System.out.println("3. Add Sale to Promotion (Store Manager)");
                System.out.println("4. Monitor Promotion Performance (Store Manager)");
                System.out.println("5. Generate Performance Report (Head Office)");
                System.out.println("6. Check Inventory Levels (Store Manager)");
                System.out.println("7. Manage Promotion Status (Marketing Manager)");
                System.out.println("8. Exit");
                
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1: createPromotion(); break;
                    case 2: viewPromotions(); break;
                    case 3: addSale(); break;
                    case 4: monitorPerformance(); break;
                    case 5: generateReport(); break;
                    case 6: checkInventory(); break;
                    case 7: managePromotionStatus(); break;
                    case 8: return;
                    default: System.out.println("Invalid option");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number");
                scanner.nextLine();
            }
        }
    }

    private void createPromotion() {
        try {
            System.out.print("Enter promotion name: ");
            String name = scanner.nextLine();
            
            System.out.println("Available types: PERCENTAGE_OFF, BOGO, LOYALTY_POINTS_MULTIPLIER");
            System.out.print("Enter type: ");
            String type = scanner.nextLine().toUpperCase();
            
            if (!type.equals("PERCENTAGE_OFF") && !type.equals("BOGO") && !type.equals("LOYALTY_POINTS_MULTIPLIER")) {
                System.out.println("Invalid promotion type. Using PERCENTAGE_OFF as default.");
                type = "PERCENTAGE_OFF";
            }
            
            double discount = getDiscountInput();
            LocalDateTime startDate = getDateInput("start");
            LocalDateTime endDate = getDateInput("end");
            
            if (endDate.isBefore(startDate)) {
                System.out.println("End date must be after start date. Setting end date to start date + 7 days");
                endDate = startDate.plusDays(7);
            }

            // Get target products
            System.out.print("Enter target product IDs (comma-separated): ");
            String[] productIds = scanner.nextLine().split(",");
            
            Promotion promo = new Promotion(name, type, discount, startDate, endDate);
            for (String productId : productIds) {
                promo.addProduct(productId.trim());
                // Check inventory levels for each product
                if (inventoryService.isLowStock(productId.trim())) {
                    notificationService.notifyLowInventory(productId.trim(), 
                        inventoryService.checkStock(productId.trim()));
                }
            }
            
            activePromotions.put(promo.getId(), promo);
            performances.put(promo.getId(), new PromotionPerformance(promo.getId()));
            
            // Notify stakeholders
            notificationService.notifyStoreManager(promo);
            notificationService.notifyCustomers(promo);
            
            savePromotion(promo);
            System.out.println("Promotion created with ID: " + promo.getId());
            
        } catch (Exception e) {
            System.out.println("Error creating promotion: " + e.getMessage());
        }
    }

    private void monitorPerformance() {
        if (activePromotions.isEmpty()) {
            System.out.println("No promotions to monitor");
            return;
        }

        System.out.println("\nPromotion Performance Monitor");
        System.out.println("-----------------------------");

        for (Promotion promo : activePromotions.values()) {
            PromotionPerformance perf = performances.get(promo.getId());
            if (perf != null) {
                System.out.println(perf.getSalesReport());
                System.out.println("-----------------------------");
            }
        }
    }
    private void generateReport() {
        System.out.println("\nGenerating Comprehensive Performance Report");
        reportService.generateSummaryReport(new ArrayList<>(activePromotions.values()), performances);
        
        // Generate individual reports for each promotion
        for (Promotion promo : activePromotions.values()) {
            reportService.generatePromotionReport(promo, performances.get(promo.getId()));
        }
    }

    private void checkInventory() {
        if (activePromotions.isEmpty()) {
            System.out.println("\nNo active promotions to check inventory for");
            return;
        }
    
        System.out.println("\nChecking Inventory Levels for Promoted Products");
        System.out.println("--------------------------------------------------");
    
        for (Promotion promo : activePromotions.values()) {
            System.out.printf("\nPromotion: %s (%s)%n", promo.getName(), promo.getId());
            Set<String> targetProducts = promo.getTargetProducts();
            
            if (targetProducts.isEmpty()) {
                System.out.println("No target products defined for this promotion");
            } else {
                for (String productId : targetProducts) {
                    int stock = inventoryService.checkStock(productId);
                    System.out.printf("Product %s: %d units in stock%n", productId, stock);
                    if (inventoryService.isLowStock(productId)) {
                        System.out.println("! Low stock warning!");
                    }
                }
            }
        }
        System.out.println("--------------------------------------------------");
    }

    private void managePromotionStatus() {
        viewPromotions();
        System.out.print("\nEnter promotion ID to manage: ");
        String promoId = scanner.nextLine();
        
        Promotion promo = activePromotions.get(promoId);
        if (promo == null) {
            System.out.println("Promotion not found");
            return;
        }

        System.out.println("\n1. End Promotion Early");
        System.out.println("2. Extend Promotion");
        System.out.println("3. Adjust Discount");
        System.out.print("Choose action: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        switch (choice) {
            case 1:
                promo.setStatus("COMPLETED");
                System.out.println("Promotion ended");
                break;
            case 2:
                LocalDateTime newEndDate = getDateInput("new end");
                promo.setEndDate(newEndDate);
                System.out.println("Promotion extended");
                break;
            case 3:
                double newDiscount = getDiscountInput();
                promo.setDiscount(newDiscount);
                System.out.println("Discount adjusted");
                break;
        }
        updatePromotionsFile();
    }

    private double getDiscountInput() {
        while (true) {
            try {
                System.out.print("Enter discount percentage (without % symbol): ");
                String discountStr = scanner.nextLine().replace("%", "").trim();
                double discount = Double.parseDouble(discountStr);
                if (discount <= 0 || discount > 100) {
                    System.out.println("Please enter a valid discount between 0 and 100");
                    continue;
                }
                return discount;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        }
    }

    private LocalDateTime getDateInput(String dateType) {
        while (true) {
            try {
                System.out.printf("Enter %s date (yyyy-MM-dd HH:mm): ", dateType);
                String dateStr = scanner.nextLine();
                return LocalDateTime.parse(dateStr, formatter);
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use format: yyyy-MM-dd HH:mm");
            }
        }
    }

    private void viewPromotions() {
        if (activePromotions.isEmpty()) {
            System.out.println("No active promotions");
            return;
        }
    
        System.out.println("\nCurrent Promotions:");
        System.out.println("--------------------------------------------------");
        System.out.printf("%-12s %-20s %-15s %-10s %-10s %-12s%n", 
            "ID", "Name", "Type", "Discount", "Sales", "Revenue");
        System.out.println("--------------------------------------------------");
    
        for (Promotion promo : activePromotions.values()) {
            PromotionPerformance perf = performances.get(promo.getId());
            // Initialize performance if null
            if (perf == null) {
                perf = new PromotionPerformance(promo.getId());
                performances.put(promo.getId(), perf);
            }
            
            System.out.printf("%-12s %-20s %-15s %-10.1f%% %-10d $%-12.2f%n",
                promo.getId(),
                promo.getName(),
                promo.getType(),
                promo.getDiscount(),
                perf.getTotalSales(),
                perf.getRevenue());
        }
        System.out.println("--------------------------------------------------");
    }

    private void addSale() {
        try {
            // First show available promotions
            viewPromotions();
            
            System.out.print("\nEnter promotion ID: ");
            String promoId = scanner.nextLine();
            
            Promotion promo = activePromotions.get(promoId);
            if (promo == null) {
                System.out.println("Promotion not found");
                return;
            }

            // Show available products for this promotion
            System.out.println("\nTarget Products for " + promo.getName() + ":");
            for (String productId : promo.getTargetProducts()) {
                System.out.printf("%s (Current Stock: %d units)%n", 
                    productId, inventoryService.checkStock(productId));
            }

            System.out.print("\nEnter product ID: ");
            String productId = scanner.nextLine();

            if (!promo.getTargetProducts().contains(productId)) {
                System.out.println("This product is not part of the promotion");
                return;
            }

            System.out.print("Enter sale amount: $");
            double amount = Double.parseDouble(scanner.nextLine());
            
            if (amount < 0) {
                System.out.println("Sale amount cannot be negative");
                return;
            }

            // Process the sale
            PromotionPerformance perf = performances.get(promoId);
            if (perf == null) {
                perf = new PromotionPerformance(promoId);
                performances.put(promoId, perf);
            }

            perf.addSale(productId, amount);
            inventoryService.decrementStock(productId, 1);

            System.out.println("\nSale processed successfully!");
            System.out.printf("New total sales for %s: %d%n", 
                promo.getName(), perf.getTotalSales());
            System.out.printf("Total revenue: $%.2f%n", perf.getRevenue());
            System.out.printf("Current stock for %s: %d units%n", 
                productId, inventoryService.checkStock(productId));
            
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number for sale amount");
        }
    }

    private void loadPromotions() {
        // Create directory if it doesn't exist
        File directory = new File("src/main/java/marketing/data");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    
        try (BufferedReader reader = new BufferedReader(new FileReader(PROMOTIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length >= 6) {
                        // Parse the data
                        String id = parts[0];
                        String name = parts[1];
                        String type = parts[2];
                        double discount = Double.parseDouble(parts[3]);
                        LocalDateTime startDate = LocalDateTime.parse(parts[4], formatter);
                        LocalDateTime endDate = LocalDateTime.parse(parts[5], formatter);
    
                        // Create and store the promotion
                        Promotion promo = new Promotion(name, type, discount, startDate, endDate);
                        activePromotions.put(id, promo);
                        // Initialize performance data
                        performances.put(id, new PromotionPerformance(id));
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing promotion: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("No existing promotions found. Starting fresh.");
        }
    }

    private void savePromotion(Promotion promo) {
        try (FileWriter writer = new FileWriter(PROMOTIONS_FILE, true)) {
            // Format: ID,Name,Type,Discount,StartDate,EndDate
            writer.write(String.format("%s,%s,%s,%.2f,%s,%s%n",
                promo.getId(),
                promo.getName(),
                promo.getType(),
                promo.getDiscount(),
                promo.getStartDate().format(formatter),
                promo.getEndDate().format(formatter)));
        } catch (IOException e) {
            System.err.println("Error saving promotion: " + e.getMessage());
        }
    }

    // Optional: Method to update promotion file (removes old data and rewrites everything)
    private void updatePromotionsFile() {
        try (FileWriter writer = new FileWriter(PROMOTIONS_FILE, false)) {
            for (Promotion promo : activePromotions.values()) {
                writer.write(String.format("%s,%s,%s,%.2f,%s,%s%n",
                    promo.getId(),
                    promo.getName(),
                    promo.getType(),
                    promo.getDiscount(),
                    promo.getStartDate().format(formatter),
                    promo.getEndDate().format(formatter)));
            }
        } catch (IOException e) {
            System.err.println("Error updating promotions file: " + e.getMessage());
        }
    }
}