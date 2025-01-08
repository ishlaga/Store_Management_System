package delivery.service;

import delivery.model.DeliveryOrder;
import delivery.model.DeliveryFeedback;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DeliveryManager {
    private List<DeliveryOrder> deliveries;
    private List<DeliveryFeedback> feedbacks;
    private Scanner scanner;
    private static final String DELIVERY_FILE = "./src/main/java/delivery/data/delivery_orders.txt";
    private static final String FEEDBACK_FILE = "./src/main/java/delivery/data/delivery_feedback.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DeliveryManager() {
        this.deliveries = new ArrayList<>();
        this.feedbacks = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        loadDeliveries();
        loadFeedbacks();
    }

    private void loadDeliveries() {
        Path path = Paths.get(DELIVERY_FILE);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                writeDeliveryHeaderToFile();
            } catch (IOException e) {
                System.err.println("Error creating delivery file: " + e.getMessage());
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(DELIVERY_FILE))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine || line.trim().isEmpty() || line.startsWith("-")) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length >= 7) {
                    DeliveryOrder order = new DeliveryOrder(parts);
                    deliveries.add(order);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading deliveries: " + e.getMessage());
        }
    }

    private void loadFeedbacks() {
        Path path = Paths.get(FEEDBACK_FILE);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                writeFeedbackHeaderToFile();
            } catch (IOException e) {
                System.err.println("Error creating feedback file: " + e.getMessage());
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FEEDBACK_FILE))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine || line.trim().isEmpty() || line.startsWith("-")) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    feedbacks.add(new DeliveryFeedback(
                        parts[0].trim(),
                        parts[1].trim(),
                        Integer.parseInt(parts[2].trim()),
                        parts[3].trim()
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading feedbacks: " + e.getMessage());
        }
    }

    private void writeDeliveryHeaderToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DELIVERY_FILE))) {
            writer.println("ID | CustomerName | Address | PhoneNumber | ScheduledTime | StaffID | Status |");
            writer.println("-".repeat(100));
        } catch (IOException e) {
            System.err.println("Error writing delivery header: " + e.getMessage());
        }
    }

    private void writeFeedbackHeaderToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FEEDBACK_FILE))) {
            writer.println("OrderID | CustomerName | Rating | Comments |");
            writer.println("-".repeat(100));
        } catch (IOException e) {
            System.err.println("Error writing feedback header: " + e.getMessage());
        }
    }

    private void saveDeliveries() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DELIVERY_FILE))) {
            writeDeliveryHeaderToFile();
            for (DeliveryOrder delivery : deliveries) {
                writer.printf("%s | %s | %s | %s | %s | %s | %s |%n",
                    delivery.getId(),
                    delivery.getCustomerName(),
                    delivery.getAddress(),
                    delivery.getPhone(),
                    delivery.getScheduledTime().format(formatter),
                    delivery.getStaffId(),
                    delivery.getStatus()
                );
            }
        } catch (IOException e) {
            System.err.println("Error saving deliveries: " + e.getMessage());
        }
    }

    private void saveFeedbacks() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FEEDBACK_FILE))) {
            writeFeedbackHeaderToFile();
            for (DeliveryFeedback feedback : feedbacks) {
                writer.printf("%s | %s | %d | %s |%n",
                    feedback.getOrderId(),
                    feedback.getCustomerName(),
                    feedback.getRating(),
                    feedback.getComments()
                );
            }
        } catch (IOException e) {
            System.err.println("Error saving feedbacks: " + e.getMessage());
        }
    }

    public void startDeliveryManagement() {
        while (true) {
            try {
                System.out.println("\nDelivery Management System");
                System.out.println("1. View All Deliveries");
                System.out.println("2. Create New Delivery");
                System.out.println("3. Update Delivery Status");
                System.out.println("4. Submit Delivery Feedback");
                System.out.println("5. View Delivery Feedback");
                System.out.println("6. Return to Main Menu");
                System.out.print("Choose an option: ");

                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        viewDeliveries();
                        break;
                    case 2:
                        createDelivery();
                        break;
                    case 3:
                        updateDeliveryStatus();
                        break;
                    case 4:
                        submitFeedback();
                        break;
                    case 5:
                        viewFeedback();
                        break;
                    case 6:
                        saveDeliveries();
                        saveFeedbacks();
                        return;
                    default:
                        System.out.println("Invalid option");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        }
    }

    private void createDelivery() {
        System.out.println("\nCreating New Delivery");
        System.out.println("-".repeat(30));

        try {
            String newId = String.format("%d", deliveries.size() + 1);

            System.out.print("Enter customer name: ");
            String customerName = scanner.nextLine().trim();
            if (customerName.isEmpty()) throw new IllegalArgumentException("Customer name cannot be empty");

            System.out.print("Enter delivery address: ");
            String address = scanner.nextLine().trim();
            if (address.isEmpty()) throw new IllegalArgumentException("Address cannot be empty");

            System.out.print("Enter phone number: ");
            String phone = scanner.nextLine().trim();
            if (phone.isEmpty()) throw new IllegalArgumentException("Phone number cannot be empty");

            System.out.print("Enter scheduled time (YYYY-MM-DD HH:mm:ss): ");
            String timeStr = scanner.nextLine().trim();
            LocalDateTime scheduledTime = LocalDateTime.parse(timeStr, formatter);

            System.out.print("Enter staff ID: ");
            String staffId = scanner.nextLine().trim();
            if (staffId.isEmpty()) throw new IllegalArgumentException("Staff ID cannot be empty");

            String[] deliveryParts = {
                newId,
                customerName,
                address,
                phone,
                timeStr,
                staffId,
                "PENDING"
            };

            DeliveryOrder newDelivery = new DeliveryOrder(deliveryParts);
            deliveries.add(newDelivery);
            saveDeliveries();
            System.out.println("Delivery created successfully! Delivery ID: " + newId);

        } catch (Exception e) {
            System.out.println("Error creating delivery: " + e.getMessage());
        }
    }

    private void viewDeliveries() {
        if (deliveries.isEmpty()) {
            System.out.println("No deliveries found.");
            return;
        }

        System.out.println("\nCurrent Deliveries:");
        System.out.println("-".repeat(100));
        for (DeliveryOrder delivery : deliveries) {
            System.out.printf("ID: %s | Customer: %s | Address: %s | Phone: %s | Time: %s | Staff: %s | Status: %s%n",
                delivery.getId(),
                delivery.getCustomerName(),
                delivery.getAddress(),
                delivery.getPhone(),
                delivery.getScheduledTime().format(formatter),
                delivery.getStaffId(),
                delivery.getStatus());
        }
        System.out.println("-".repeat(100));
    }

    private void updateDeliveryStatus() {
        System.out.print("Enter delivery ID: ");
        String id = scanner.nextLine().trim();

        DeliveryOrder delivery = deliveries.stream()
            .filter(d -> d.getId().equals(id))
            .findFirst()
            .orElse(null);

        if (delivery == null) {
            System.out.println("Delivery not found.");
            return;
        }

        System.out.println("Current status: " + delivery.getStatus());
        System.out.println("Select new status:");
        System.out.println("1. PENDING");
        System.out.println("2. EN_ROUTE");
        System.out.println("3. DELIVERED");
        System.out.print("Enter choice (1-3): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            String newStatus;
            switch (choice) {
                case 1:
                    newStatus = "PENDING";
                    break;
                case 2:
                    newStatus = "EN_ROUTE";
                    break;
                case 3:
                    newStatus = "DELIVERED";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid status choice");
            }

            delivery.setStatus(newStatus);
            saveDeliveries();
            System.out.println("Status updated successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void submitFeedback() {
        System.out.print("Enter delivery ID: ");
        String orderId = scanner.nextLine().trim();

        DeliveryOrder delivery = deliveries.stream()
            .filter(d -> d.getId().equals(orderId))
            .findFirst()
            .orElse(null);

        if (delivery == null) {
            System.out.println("Delivery not found.");
            return;
        }

        if (!delivery.getStatus().equals("DELIVERED")) {
            System.out.println("Feedback can only be submitted for delivered orders.");
            return;
        }

        if (feedbacks.stream().anyMatch(f -> f.getOrderId().equals(orderId))) {
            System.out.println("Feedback already exists for this delivery.");
            return;
        }

        try {
            System.out.print("Enter rating (1-5): ");
            int rating = Integer.parseInt(scanner.nextLine().trim());
            if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be between 1 and 5");

            System.out.print("Enter comments: ");
            String comments = scanner.nextLine().trim();

            DeliveryFeedback feedback = new DeliveryFeedback(
                orderId,
                delivery.getCustomerName(),
                rating,
                comments
            );

            feedbacks.add(feedback);
            saveFeedbacks();
            System.out.println("Feedback submitted successfully!");

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid rating number");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewFeedback() {
        if (feedbacks.isEmpty()) {
            System.out.println("No feedback found.");
            return;
        }

        System.out.println("\nDelivery Feedback:");
        System.out.println("-".repeat(100));
        for (DeliveryFeedback feedback : feedbacks) {
            System.out.printf("Order ID: %s | Customer: %s | Rating: %d/5 | Comments: %s%n",
                feedback.getOrderId(),
                feedback.getCustomerName(),
                feedback.getRating(),
                feedback.getComments());
        }
        System.out.println("-".repeat(100));
    }
}