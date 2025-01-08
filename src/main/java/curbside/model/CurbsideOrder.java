package curbside.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a curbside pickup order in the store's curbside service.
 * Tracks order details, customer information, pickup time, and order status
 * for efficient curbside order management.
 *
 * @author Hrishikesha Kyathsandra
 */
public class CurbsideOrder {
    private String orderId;
    private String customerId;
    private List<OrderItem> items;
    private LocalDateTime orderTime;
    private LocalDateTime scheduledPickupTime;
    private String status; // PENDING, PREPARING, READY, COMPLETED, CANCELLED
    private Vehicle customerVehicle;
    private boolean isPrepaid;
    private double totalAmount;
    
    public CurbsideOrder(String orderId, String customerId, LocalDateTime scheduledPickupTime) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = new ArrayList<>();
        this.orderTime = LocalDateTime.now();
        this.scheduledPickupTime = scheduledPickupTime;
        this.status = "PENDING";
    }
    
    // Getters and Setters
    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public List<OrderItem> getItems() { return items; }
    public void addItem(OrderItem item) { 
        this.items.add(item);
        this.totalAmount += item.getSubtotal();
    }
    public LocalDateTime getOrderTime() { return orderTime; }
    public LocalDateTime getScheduledPickupTime() { return scheduledPickupTime; }
    public void setScheduledPickupTime(LocalDateTime time) { this.scheduledPickupTime = time; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Vehicle getCustomerVehicle() { return customerVehicle; }
    public void setCustomerVehicle(Vehicle vehicle) { this.customerVehicle = vehicle; }
    public boolean isPrepaid() { return isPrepaid; }
    public void setPrepaid(boolean prepaid) { this.isPrepaid = prepaid; }
    public double getTotalAmount() { return totalAmount; }
}