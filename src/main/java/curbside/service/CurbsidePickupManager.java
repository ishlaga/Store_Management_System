package curbside.service;

import curbside.model.*;
import java.util.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Core manager for curbside pickup operations.
 * Handles order processing, customer arrivals, and pickup coordination
 * for the store's curbside service. Integrates with inventory management
 * to ensure accurate order fulfillment.
 *
 * @author Hrishikesha Kyathsandra
 */
public class CurbsidePickupManager {
    private Map<String, CurbsideOrder> orders;
    private CurbsideInventoryManager inventoryManager;
    private String storeId;
    
    public CurbsidePickupManager(String storeId, CurbsideInventoryManager inventoryManager) {
        this.storeId = storeId;
        this.orders = new HashMap<>();
        this.inventoryManager = inventoryManager;
    }
    
    public CurbsideOrder createOrder(String customerId, LocalDateTime pickupTime) {
        String orderId = "CURB" + System.currentTimeMillis();
        CurbsideOrder order = new CurbsideOrder(orderId, customerId, pickupTime);
        orders.put(orderId, order);
        return order;
    }
    
    public boolean addItemToOrder(String orderId, String productId, int quantity) {
        CurbsideOrder order = orders.get(orderId);
        if (order == null) return false;
        
        // Check inventory availability
        if (!inventoryManager.checkAvailability(productId, quantity)) {
            return false;
        }
        
        Product product = inventoryManager.getProduct(productId);
        OrderItem item = new OrderItem(
            productId, 
            product.getName(), 
            quantity, 
            product.getPrice(),
            product.requiresTemperatureControl()
        );
        
        order.addItem(item);
        return true;
    }
    
    public boolean updateOrderStatus(String orderId, String status) {
        CurbsideOrder order = orders.get(orderId);
        if (order == null) return false;
        
        order.setStatus(status);
        if (status.equals("COMPLETED")) {
            // Update inventory
            for (OrderItem item : order.getItems()) {
                inventoryManager.deductStock(item.getProductId(), item.getQuantity());
            }
        }
        return true;
    }
    
    public boolean processPayment(String orderId, String paymentMethod, String... paymentDetails) {
        CurbsideOrder order = orders.get(orderId);
        if (order == null) return false;
        
        double amount = order.getTotalAmount();
        System.out.printf("\nProcessing %s payment of $%.2f\n", paymentMethod, amount);
        
        boolean success = false;
        switch (paymentMethod.toUpperCase()) {
            case "CARD":
                if (paymentDetails.length >= 3) {
                    String cardNumber = paymentDetails[0];
                    String expDate = paymentDetails[1];
                    String cvv = paymentDetails[2];
                    
                    // Basic card validation
                    if (cardNumber.length() >= 13 && 
                        expDate.matches("\\d{2}/\\d{2}") && 
                        cvv.matches("\\d{3}")) {
                        System.out.println("Card payment authorized");
                        success = true;
                    } else {
                        System.out.println("Invalid card details");
                    }
                }
                break;
                
            case "CASH":
                System.out.println("Cash payment received");
                success = true;
                break;
        }
        
        if (success) {
            order.setPrepaid(true);
        }
        return success;
    }
    
    public List<CurbsideOrder> getPendingOrders() {
        return orders.values().stream()
                .filter(order -> order.getStatus().equals("PENDING"))
                .collect(Collectors.toList());
    }
    
    public List<CurbsideOrder> getReadyOrders() {
        return orders.values().stream()
                .filter(order -> order.getStatus().equals("READY"))
                .collect(Collectors.toList());
    }
    
    public CurbsideOrder getOrder(String orderId) {
        return orders.get(orderId);
    }
    
    public void setVehicleDetails(String orderId, String make, String model, 
                                String color, String licensePlate) {
        CurbsideOrder order = orders.get(orderId);
        if (order != null) {
            Vehicle vehicle = new Vehicle(make, model, color, licensePlate);
            order.setCustomerVehicle(vehicle);
        }
    }
}