package order.model;

import inventory.model.Product;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Model class representing an order with its items, payment status,
 * and related information.
 *
 * @author Akhilesh Nevatia
 * @author Hrishikesha
 */
public class Order {
    private String orderId;
    private String storeId;
    private Map<Product, Integer> items; // Product and quantity
    private double totalAmount;
    private String paymentMethod;
    private boolean isPaid;
    private LocalDateTime orderTime;

    public Order(String orderId, String storeId) {
        this.orderId = orderId;
        this.storeId = storeId;
        this.items = new HashMap<>();
        this.orderTime = LocalDateTime.now();
        this.isPaid = false;
    }

    // Getters and setters
    public String getOrderId() { return orderId; }
    public String getStoreId() { return storeId; }
    public Map<Product, Integer> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public boolean isPaid() { return isPaid; }
    public LocalDateTime getOrderTime() { return orderTime; }

    public void addItem(Product product, int quantity) {
        items.put(product, items.getOrDefault(product, 0) + quantity);
        calculateTotal();
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void markAsPaid() {
        this.isPaid = true;
    }

    private void calculateTotal() {
        this.totalAmount = items.entrySet().stream()
            .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
            .sum();
    }
}