package gas.service;

import gas.model.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Service class for generating and managing gas station analytics.
 * Handles sales reports and operational statistics.
 *
 * @author Hrishikesha Kyathsandra
 */
public class GasAnalyticsService {
    private GasStationManager gasManager;
    private String storeId;

    public GasAnalyticsService(GasStationManager gasManager, String storeId) {
        this.gasManager = gasManager;
        this.storeId = storeId;
    }

    public void generateSalesReport() {
        String fileName = "./src/main/java/store/data/" + storeId + "_gas_sales_" + 
                         LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("Gas Station Sales Report for Store: " + storeId + "\n");
            writer.write("Generated on: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");
            
            // Add current fuel stock levels
            writer.write("Current Fuel Stock Levels:\n");
            writer.write("--------------------------------------------------\n");
            for (FuelType type : FuelType.values()) {
                writer.write(String.format("%s (%d): %.2f gallons\n", 
                    type.getName(), type.getOctane(), gasManager.getFuelStock(type)));
            }
            
            // Add transaction summary
            writer.write("\nToday's Transactions:\n");
            writer.write("--------------------------------------------------\n");
            Map<String, RefuelingTransaction> transactions = gasManager.getTodayTransactions();
            double totalRevenue = 0.0;
            double totalGallons = 0.0;
            
            for (RefuelingTransaction t : transactions.values()) {
                writer.write(String.format("Transaction %s: %s - %.2f gallons - $%.2f\n",
                    t.getTransactionId(), t.getFuelType().getName(), 
                    t.getGallons(), t.getAmount()));
                totalRevenue += t.getAmount();
                totalGallons += t.getGallons();
            }
            
            writer.write("\nSummary:\n");
            writer.write(String.format("Total Transactions: %d\n", transactions.size()));
            writer.write(String.format("Total Gallons Sold: %.2f\n", totalGallons));
            writer.write(String.format("Total Revenue: $%.2f\n", totalRevenue));
            
        } catch (IOException e) {
            System.err.println("Error generating sales report: " + e.getMessage());
        }
    }
}