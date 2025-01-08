package report;

import report.service.ReportManager;
import report.model.SalesReport;
import inventory.model.Product;
import inventory.service.InventoryManager;
import inventory.service.HeadOfficeManager;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Main system class that handles the generation and management
 * of sales reports.
 *
 * @author Hrishikesha
 */
public class SalesReportSystem {
    private Scanner scanner;
    private String storeId;
    private InventoryManager inventoryManager;

    public SalesReportSystem(String storeId) {
        this.storeId = storeId;
        this.inventoryManager = new InventoryManager(new HeadOfficeManager());
        this.inventoryManager.setCurrentStore(storeId);
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\nSales Report System");
            System.out.println("1. Generate Daily Report (Store Manager)");
            System.out.println("2. Generate Custom Period Report (Store Manager)");
            System.out.println("3. Return to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    generateDailyReport();
                    break;
                case 2:
                    generateCustomReport();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private void generateDailyReport() {
        LocalDate today = LocalDate.now();
        generateReport(today, today);
        System.out.println("Daily report generated successfully");
    }

    private void generateCustomReport() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        System.out.print("Enter start date (yyyy-MM-dd): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine(), formatter);
        
        System.out.print("Enter end date (yyyy-MM-dd): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine(), formatter);

        if (endDate.isBefore(startDate)) {
            System.out.println("End date cannot be before start date");
            return;
        }

        generateReport(startDate, endDate);
        System.out.println("Custom period report generated successfully");
    }

    private void generateReport(LocalDate startDate, LocalDate endDate) {
        ReportManager reportManager = new ReportManager(storeId, inventoryManager);
        SalesReport report = reportManager.generateReport(startDate, endDate);

        // Print report to terminal
        System.out.println("\n=== Sales Report ===");
        System.out.println("Store ID: " + storeId);
        System.out.println("Period: " + startDate + " to " + endDate);
        System.out.println("----------------------------------------");
        System.out.println("Total Revenue: $" + String.format("%.2f", report.getTotalRevenue()));
        System.out.println("Total Units Sold: " + report.getTotalUnits());
        
        if (!report.getProductSales().isEmpty()) {
            System.out.println("\nProduct Sales Breakdown:");
            System.out.println("----------------------------------------");
            for (Map.Entry<String, Integer> entry : report.getProductSales().entrySet()) {
                Product product = inventoryManager.getProduct(entry.getKey());
                if (product != null) {
                    System.out.printf("%s (%s): %d units, $%.2f%n",
                        product.getName(),
                        entry.getKey(),
                        entry.getValue(),
                        report.getProductRevenue().get(entry.getKey()));
                }
            }
        } else {
            System.out.println("\nNo sales recorded for this period.");
        }
        System.out.println("----------------------------------------");

        // Save report to file
        String fileName = String.format("./src/main/java/store/data/%s_report_%s_%s.txt", 
            storeId, startDate, endDate);
        System.out.println("\nReport saved to: " + fileName);
    }
}