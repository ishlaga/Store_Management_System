package marketing.service;

import java.util.*;

public class InventoryService {
    private Map<String, Integer> productInventory;
    
    public InventoryService() {
        this.productInventory = new HashMap<>();
        initializeInventory();
    }
    
    private void initializeInventory() {
        // Initialize with some default inventory
        productInventory.put("PROD001", 100);  // 100 units
        productInventory.put("PROD002", 75);   // 75 units
        productInventory.put("PROD003", 50);   // 50 units
        productInventory.put("PROD004", 200);  // 200 units
        productInventory.put("PROD005", 150);  // 150 units
    }
    
    public int checkStock(String productId) {
        return productInventory.getOrDefault(productId, 0);
    }
    
    public boolean isLowStock(String productId) {
        return checkStock(productId) < 20;
    }
    
    public void updateStock(String productId, int quantity) {
        productInventory.put(productId, quantity);
    }
    
    public void decrementStock(String productId, int quantity) {
        int currentStock = checkStock(productId);
        updateStock(productId, Math.max(0, currentStock - quantity));
    }
    
    // Add method to show available products
    public void displayAvailableProducts() {
        System.out.println("\nAvailable Products:");
        System.out.println("--------------------------------------------------");
        System.out.printf("%-10s %-20s%n", "Product ID", "Stock Level");
        for (Map.Entry<String, Integer> entry : productInventory.entrySet()) {
            System.out.printf("%-10s %-20d%n", entry.getKey(), entry.getValue());
        }
    }
}