/**
 * Model class representing a product in the inventory system
 * with its properties and state.
 *
 * @author Akhilesh Nevatia
 */
package inventory.model;

import java.time.LocalDate;

public class Product {
    private String id;
    private String name;
    private double price;
    private int stockLevel;
    private String supplier;
    private LocalDate expirationDate;
    private boolean isObsolete;

    /**
     * Creates a new product with the specified details
     * @param id Unique identifier for the product
     * @param name Product name
     * @param price Product price
     * @param stockLevel Current stock level
     * @param supplier Supplier name
     * @param expirationDate Product expiration date
     */
    public Product(String id, String name, double price, int stockLevel, 
                  String supplier, LocalDate expirationDate) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockLevel = stockLevel;
        this.supplier = supplier;
        this.expirationDate = expirationDate;
        this.isObsolete = false;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStockLevel() { return stockLevel; }
    public String getSupplier() { return supplier; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public boolean isObsolete() { return isObsolete; }

    public void setPrice(double price) { this.price = price; }
    public void setStockLevel(int stockLevel) { this.stockLevel = stockLevel; }
    public void markAsObsolete() { this.isObsolete = true; }
}