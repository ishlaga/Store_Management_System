package deli.model;

import java.util.List;
import java.time.LocalDateTime;

public class DeliOrder {
    private String orderId;
    private List<String> items;
    private double totalPrice;
    private LocalDateTime orderTime;
    private int labelNumber;
    private String status;
    private int tokenNumber;

    public DeliOrder(String orderId, List<String> items, double totalPrice, int labelNumber, int tokenNumber) {
        this.orderId = orderId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.orderTime = LocalDateTime.now();
        this.labelNumber = labelNumber;
        this.status = "PREPARING";
        this.tokenNumber = tokenNumber;
    }

    // Getters
    public String getOrderId() { return orderId; }
    public List<String> getItems() { return items; }
    public double getTotalPrice() { return totalPrice; }
    public LocalDateTime getOrderTime() { return orderTime; }
    public int getLabelNumber() { return labelNumber; }
    public String getStatus() { return status; }
    public int getTokenNumber() { return tokenNumber; }

    // Setters
    public void setStatus(String status) { this.status = status; }

    public String toLine() {
        return String.format("%s|%s|%.2f|%d|%d|%s", 
            orderId, 
            String.join(", ", items), 
            totalPrice, 
            labelNumber, 
            tokenNumber, 
            status);
    }
}