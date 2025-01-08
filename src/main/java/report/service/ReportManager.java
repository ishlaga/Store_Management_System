package report.service;

import report.model.SalesReport;
import inventory.service.InventoryManager;
import inventory.model.Product;
import java.time.*;
import java.util.*;
import java.io.*;
import java.io.File;

/**
 * Service class that manages the generation and storage
 * of sales reports.
 *
 * @author Hrishikesha
 */
public class ReportManager {
    private String storeId;
    private InventoryManager inventoryManager;

    /**
     * Creates a new report manager for a store
     * @param storeId Store identifier
     * @param inventoryManager Inventory manager instance
     */
    public ReportManager(String storeId, InventoryManager inventoryManager) {
        this.storeId = storeId;
        this.inventoryManager = inventoryManager;
    }

    public SalesReport generateReport(LocalDate startDate, LocalDate endDate) {
        SalesReport report = new SalesReport(storeId, startDate, endDate);
        loadSalesData(report, startDate, endDate);
        saveReport(report);
        return report;
    }

    private void loadSalesData(SalesReport report, LocalDate startDate, LocalDate endDate) {
        String fileName = "./src/main/java/store/data/" + storeId + "_sales.txt";
        File salesFile = new File(fileName);
        
        if (!salesFile.exists()) {
            System.out.println("No sales data file found. Creating new file.");
            try {
                salesFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating sales file: " + e.getMessage());
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(salesFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    LocalDate saleDate = LocalDate.parse(parts[0]);
                    
                    if (!saleDate.isBefore(startDate) && !saleDate.isAfter(endDate)) {
                        String productId = parts[1];
                        int quantity = Integer.parseInt(parts[2]);
                        double revenue = Double.parseDouble(parts[3]);
                        report.addSale(productId, quantity, revenue);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading sales data: " + e.getMessage());
        }
    }

    private void saveReport(SalesReport report) {
        String fileName = "./src/main/java/store/data/" + storeId + "_report_" + 
                         report.getStartDate() + "_" + report.getEndDate() + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("Sales Report for Store: " + storeId + "\n");
            writer.write("Period: " + report.getStartDate() + " to " + report.getEndDate() + "\n");
            writer.write("Total Revenue: $" + String.format("%.2f", report.getTotalRevenue()) + "\n");
            writer.write("Total Units Sold: " + report.getTotalUnits() + "\n\n");
            
            writer.write("Product Sales Breakdown:\n");
            for (Map.Entry<String, Integer> entry : report.getProductSales().entrySet()) {
                Product product = inventoryManager.getProduct(entry.getKey());
                if (product != null) {
                    writer.write(String.format("%s (%s): %d units, $%.2f\n",
                        product.getName(),
                        entry.getKey(),
                        entry.getValue(),
                        report.getProductRevenue().get(entry.getKey())));
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving report: " + e.getMessage());
        }
    }
}