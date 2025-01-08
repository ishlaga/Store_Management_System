package gas.model;

import java.time.LocalDateTime;

public class FuelOrder {
    private String orderId;
    private String supplierId;
    private FuelType fuelType;
    private double gallonsOrdered;
    private double gallonsReceived;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private String status; // "PENDING", "IN_TRANSIT", "DELIVERED", "CANCELLED"
    
    public FuelOrder(String orderId, String supplierId, FuelType fuelType, double gallonsOrdered) {
        this.orderId = orderId;
        this.supplierId = supplierId;
        this.fuelType = fuelType;
        this.gallonsOrdered = gallonsOrdered;
        this.orderDate = LocalDateTime.now();
        this.status = "PENDING";
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public FuelType getFuelType() { return fuelType; }
    public double getGallonsOrdered() { return gallonsOrdered; }
    public double getGallonsReceived() { return gallonsReceived; }
    public void setGallonsReceived(double gallonsReceived) { this.gallonsReceived = gallonsReceived; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public void setDeliveryDate(LocalDateTime deliveryDate) { this.deliveryDate = deliveryDate; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public LocalDateTime getDeliveryDate() { return deliveryDate; }
} 