package curbside.model;

/**
 * Represents a product available for curbside pickup orders.
 * Contains product details including identification, pricing,
 * and availability status for the curbside service.
 *
 * @author Hrishikesha Kyathsandra
 */
public class Product {
    private String productId;
    private String name;
    private double price;
    private int quantity;
    private boolean requiresTemperatureControl;

    public Product(String productId, String name, double price, int quantity, boolean requiresTemperatureControl) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.requiresTemperatureControl = requiresTemperatureControl;
    }

    // Getters
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public boolean requiresTemperatureControl() { return requiresTemperatureControl; }
} 