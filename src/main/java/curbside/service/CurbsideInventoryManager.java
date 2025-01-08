package curbside.service;

import curbside.model.Product;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages inventory specifically for curbside pickup operations.
 * Handles stock checking, reservation, and updates for items
 * designated for curbside pickup orders.
 *
 * @author Hrishikesha Kyathsandra
 */
public class CurbsideInventoryManager {
    private Map<String, Product> inventory;
    
    public CurbsideInventoryManager() {
        this.inventory = new HashMap<>();
        initializeInventory();
    }
    
    private void initializeInventory() {
        // Add sample products
        addProduct(new Product("P001", "Milk", 3.99, 100, true));
        addProduct(new Product("P002", "Bread", 2.49, 50, false));
        addProduct(new Product("P003", "Eggs", 4.99, 75, true));
    }
    
    public void addProduct(Product product) {
        inventory.put(product.getProductId(), product);
    }
    
    public Product getProduct(String productId) {
        return inventory.get(productId);
    }
    
    public boolean checkAvailability(String productId, int quantity) {
        Product product = inventory.get(productId);
        return product != null && product.getQuantity() >= quantity;
    }
    
    public void deductStock(String productId, int quantity) {
        Product product = inventory.get(productId);
        if (product != null) {
            product.setQuantity(product.getQuantity() - quantity);
        }
    }
}