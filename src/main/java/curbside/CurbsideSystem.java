package curbside;

import curbside.model.*;
import curbside.service.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Main system interface for curbside pickup operations.
 * Provides the user interface and coordinates between different components
 * of the curbside pickup service, including order management, inventory,
 * and customer service.
 *
 * @author Hrishikesha Kyathsandra
 */
public class CurbsideSystem {
    private CurbsidePickupManager curbsideManager;
    private Scanner scanner;
    private String storeId;

    public CurbsideSystem(String storeId, CurbsideInventoryManager inventoryManager) {
        this.storeId = storeId;
        this.curbsideManager = new CurbsidePickupManager(storeId, inventoryManager);
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\nCurbside Pickup Management");
            System.out.println("1. Create New Order");
            System.out.println("2. View Pending Orders");
            System.out.println("3. Mark Order Ready for Pickup");
            System.out.println("4. Process Customer Arrival & Delivery");
            System.out.println("5. Return to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    createCurbsideOrder();
                    break;
                case 2:
                    viewPendingCurbsideOrders();
                    break;
                case 3:
                    markCurbsideOrderReady();
                    break;
                case 4:
                    processCurbsideArrival();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private void createCurbsideOrder() {
        System.out.println("\nNew Curbside Order");
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine();
        
        System.out.print("Enter pickup time (HH:mm): ");
        String timeStr = scanner.nextLine();
        LocalDateTime pickupTime = LocalDateTime.now()
            .withHour(Integer.parseInt(timeStr.split(":")[0]))
            .withMinute(Integer.parseInt(timeStr.split(":")[1]));
        
        CurbsideOrder order = curbsideManager.createOrder(customerId, pickupTime);
        
        while (true) {
            System.out.print("\nEnter product ID (or 'done' to finish): ");
            String productId = scanner.nextLine();
            if (productId.equalsIgnoreCase("done")) break;
            
            System.out.print("Enter quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine();
            
            if (!curbsideManager.addItemToOrder(order.getOrderId(), productId, quantity)) {
                System.out.println("Failed to add item - check stock availability");
            }
        }
        
        // Vehicle details
        System.out.println("\nEnter vehicle details:");
        System.out.print("Make: ");
        String make = scanner.nextLine();
        System.out.print("Model: ");
        String model = scanner.nextLine();
        System.out.print("Color: ");
        String color = scanner.nextLine();
        System.out.print("License Plate: ");
        String licensePlate = scanner.nextLine();
        
        curbsideManager.setVehicleDetails(order.getOrderId(), make, model, color, licensePlate);
        
        // Payment
        System.out.println("\nSelect payment method:");
        System.out.println("1. Card (Pre-pay)");
        System.out.println("2. Pay at pickup");
        int paymentChoice = scanner.nextInt();
        scanner.nextLine();
        
        if (paymentChoice == 1) {
            System.out.print("Enter card number: ");
            String cardNumber = scanner.nextLine();
            System.out.print("Enter expiration date (MM/YY): ");
            String expDate = scanner.nextLine();
            System.out.print("Enter CVV: ");
            String cvv = scanner.nextLine();
            
            if (curbsideManager.processPayment(order.getOrderId(), "CARD", 
                                             cardNumber, expDate, cvv)) {
                System.out.println("Payment processed successfully");
            } else {
                System.out.println("Payment failed - order will require payment at pickup");
            }
        }
        
        System.out.println("\nOrder created successfully!");
        System.out.println("Order ID: " + order.getOrderId());
    }

    private void viewPendingCurbsideOrders() {
        System.out.println("\nPending Curbside Orders");
        System.out.println("--------------------------------------------------");
        
        List<CurbsideOrder> pendingOrders = curbsideManager.getPendingOrders();
        if (pendingOrders.isEmpty()) {
            System.out.println("No pending orders");
            return;
        }
        
        for (CurbsideOrder order : pendingOrders) {
            System.out.printf("Order ID: %s%n", order.getOrderId());
            System.out.printf("Customer ID: %s%n", order.getCustomerId());
            System.out.printf("Pickup Time: %s%n", 
                order.getScheduledPickupTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            System.out.printf("Status: %s%n", order.getStatus());
            System.out.println("Items:");
            for (OrderItem item : order.getItems()) {
                System.out.printf("- %s x%d ($%.2f each)%n", 
                    item.getProductName(), item.getQuantity(), item.getUnitPrice());
            }
            System.out.printf("Total Amount: $%.2f%n", order.getTotalAmount());
            System.out.println("--------------------------------------------------");
        }
    }

    private void processCurbsideArrival() {
        System.out.println("\nProcess Customer Arrival");
        System.out.print("Enter Order ID: ");
        String orderId = scanner.nextLine();
        
        CurbsideOrder order = curbsideManager.getOrder(orderId);
        if (order == null) {
            System.out.println("Order not found");
            return;
        }
        
        Vehicle vehicle = order.getCustomerVehicle();
        System.out.println("\nVerifying vehicle details:");
        System.out.printf("Expected: %s %s %s (Plate: %s)%n", 
            vehicle.getColor(), vehicle.getMake(), vehicle.getModel(), 
            vehicle.getLicensePlate());
        
        System.out.print("\nConfirm vehicle match? (yes/no): ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("yes")) {
            if (!order.isPrepaid()) {
                System.out.println("\nProcessing payment at pickup...");
                System.out.println("1. Card");
                System.out.println("2. Cash");
                System.out.print("Choose payment method: ");
                int paymentChoice = scanner.nextInt();
                scanner.nextLine();
                
                processPaymentAtPickup(order, paymentChoice);
            }
            
            curbsideManager.updateOrderStatus(orderId, "COMPLETED");
            System.out.println("Order completed successfully!");
        } else {
            System.out.println("Vehicle mismatch - please verify customer details");
        }
    }

    private void markCurbsideOrderReady() {
        System.out.println("\nMark Order as Ready");
        System.out.print("Enter Order ID: ");
        String orderId = scanner.nextLine();
        
        if (curbsideManager.updateOrderStatus(orderId, "READY")) {
            System.out.println("Order marked as ready for pickup");
        } else {
            System.out.println("Failed to update order status");
        }
    }

    private void processPaymentAtPickup(CurbsideOrder order, int paymentChoice) {
        boolean paymentSuccess = false;
        
        switch (paymentChoice) {
            case 1: // Card
                System.out.print("Enter card number: ");
                String cardNumber = scanner.nextLine();
                System.out.print("Enter expiration date (MM/YY): ");
                String expDate = scanner.nextLine();
                System.out.print("Enter CVV: ");
                String cvv = scanner.nextLine();
                
                paymentSuccess = curbsideManager.processPayment(
                    order.getOrderId(), "CARD", cardNumber, expDate, cvv);
                break;
                
            case 2: // Cash
                System.out.printf("Total amount due: $%.2f%n", order.getTotalAmount());
                System.out.print("Enter amount received: $");
                double cashReceived = scanner.nextDouble();
                scanner.nextLine();
                
                if (cashReceived >= order.getTotalAmount()) {
                    double change = cashReceived - order.getTotalAmount();
                    System.out.printf("Change due: $%.2f%n", change);
                    paymentSuccess = curbsideManager.processPayment(
                        order.getOrderId(), "CASH");
                } else {
                    System.out.println("Insufficient payment amount");
                }
                break;
        }
        
        if (paymentSuccess) {
            System.out.println("Payment processed successfully");
        } else {
            System.out.println("Payment failed");
        }
    }
}