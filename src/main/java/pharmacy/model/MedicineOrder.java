package pharmacy.model;

import java.time.LocalDateTime;

public class MedicineOrder {
    private String orderId;
    private String supplierId;
    private Medicine medicine;
    private int quantityOrdered;
    private int quantityReceived;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private String status; // "PENDING", "IN_TRANSIT", "DELIVERED", "CANCELLED"
    private double orderTotal;
    
    public MedicineOrder(String orderId, String supplierId, Medicine medicine, int quantityOrdered) {
        this.orderId = orderId;
        this.supplierId = supplierId;
        this.medicine = medicine;
        this.quantityOrdered = quantityOrdered;
        this.orderDate = LocalDateTime.now();
        this.status = "PENDING";
        this.orderTotal = medicine.getPrice() * quantityOrdered;
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public Medicine getMedicine() { return medicine; }
    public int getQuantityOrdered() { return quantityOrdered; }
    public int getQuantityReceived() { return quantityReceived; }
    public void setQuantityReceived(int quantityReceived) { this.quantityReceived = quantityReceived; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public LocalDateTime getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDateTime deliveryDate) { this.deliveryDate = deliveryDate; }
    public double getOrderTotal() { return orderTotal; }
} 