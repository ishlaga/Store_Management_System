package returns.service;

import returns.model.Return;
import returns.model.ReturnItem;
import inventory.model.Product;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReturnAnalyticsService {
    private ReturnManager returnManager;
    private String storeId;

    public ReturnAnalyticsService(ReturnManager returnManager, String storeId) {
        this.returnManager = returnManager;
        this.storeId = storeId;
    }

    public void generateAnalyticsReport() {
        String fileName = "./src/main/java/store/data/" + storeId + "_returns_analytics_" + 
                         LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("Return Analytics Report for Store: " + storeId + "\n");
            writer.write("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("--------------------------------------------------\n\n");
            
            // Returns by category
            writer.write("Returns by Category:\n");
            Map<String, Integer> categoryReturns = getReturnsByCategory();
            for (Map.Entry<String, Integer> entry : categoryReturns.entrySet()) {
                writer.write(String.format("%-20s: %d items\n", entry.getKey(), entry.getValue()));
            }
            
            // Most returned products
            writer.write("\nTop 5 Most Returned Products:\n");
            List<Product> topReturned = getMostReturnedProducts(5);
            for (Product product : topReturned) {
                writer.write(String.format("%-20s (ID: %s)\n", product.getName(), product.getId()));
            }
            
            // Cost analysis
            writer.write("\nCost Analysis:\n");
            double totalCost = calculateTotalReturnCost();
            writer.write(String.format("Total Return Cost: $%.2f\n", totalCost));
            
            writer.write("\n--------------------------------------------------\n");
            System.out.println("Analytics report generated: " + fileName);
            
        } catch (IOException e) {
            System.err.println("Error generating analytics report: " + e.getMessage());
        }
    }

    private Map<String, Integer> getReturnsByCategory() {
        Map<String, Integer> categoryReturns = new HashMap<>();
        Map<String, Return> returns = returnManager.getAllReturns();
        
        for (Return ret : returns.values()) {
            for (Map.Entry<Product, ReturnItem> entry : ret.getItems().entrySet()) {
                String category = entry.getKey().getName().split(" ")[0];
                categoryReturns.merge(category, entry.getValue().getQuantity(), Integer::sum);
            }
        }
        return categoryReturns;
    }

    private List<Product> getMostReturnedProducts(int limit) {
        Map<Product, Integer> productReturns = new HashMap<>();
        Map<String, Return> returns = returnManager.getAllReturns();
        
        for (Return ret : returns.values()) {
            for (Map.Entry<Product, ReturnItem> entry : ret.getItems().entrySet()) {
                productReturns.merge(entry.getKey(), entry.getValue().getQuantity(), Integer::sum);
            }
        }

        return productReturns.entrySet().stream()
            .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    private double calculateTotalReturnCost() {
        return returnManager.getAllReturns().values().stream()
            .mapToDouble(Return::getRefundAmount)
            .sum();
    }
} 