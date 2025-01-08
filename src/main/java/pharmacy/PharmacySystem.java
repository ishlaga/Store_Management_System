/**
 * Main system class for managing pharmacy operations.
 * Handles prescription processing, inventory management, and reporting.
 *
 * @author Hrishikesha Kyathsandra
 */
package pharmacy;

import pharmacy.model.*;
import pharmacy.service.*;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controls the main operations of a pharmacy system.
 * Manages prescriptions, medication inventory, and OTC sales.
 */
public class PharmacySystem {
    private PharmacyManager pharmacyManager;
    private PaymentProcessor paymentProcessor;
    private Scanner scanner;
    private String storeId;
    private PharmacyAnalyticsService analyticsService;

    public PharmacySystem(String storeId) {
        this.storeId = storeId;
        this.pharmacyManager = new PharmacyManager(storeId);
        this.paymentProcessor = new PaymentProcessor();
        this.analyticsService = new PharmacyAnalyticsService(pharmacyManager, storeId);
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\nPharmacy Management System");
            System.out.println("1. Process Prescription (Pharmacist)");
            System.out.println("2. View Medication Inventory (Staff)");
            System.out.println("3. Pharmacy Supply Management (Supply Chain)");
            System.out.println("4. Process OTC Purchase (Staff)");
            System.out.println("5. Generate Inventory Report (Pharmacy Manager)");
            System.out.println("6. Return to Main Menu");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: processPrescription(); break;
                case 2: viewInventory(); break;
                case 3: recordInventoryRestock(); break;
                case 4: processOTCPurchase(); break;
                case 5: generateReport(); break;
                case 6: return;
                default: System.out.println("Invalid option");
            }
        }
    }

    private void processPrescription() {
        System.out.println("\nPrescription Processing");
        System.out.print("Enter prescription ID: ");
        String prescriptionId = scanner.nextLine();

        if (!pharmacyManager.verifyPrescription(prescriptionId)) {
            System.out.println("Invalid or expired prescription");
            return;
        }

        Prescription prescription = pharmacyManager.getPrescription(prescriptionId);
        System.out.println("\nPrescription Details:");
        System.out.println("Patient ID: " + prescription.getCustomerId());
        System.out.println("Medication: " + prescription.getMedicationName());
        System.out.println("Dosage: " + prescription.getDosage());
        System.out.println("Quantity: " + prescription.getQuantity());
        System.out.println("Instructions: " + prescription.getInstructions());
        System.out.println("Doctor: " + prescription.getDoctorName());
        System.out.printf("Price: $%.2f%n", prescription.getPrice());

        System.out.print("\nPharmacist verification required. Approve? (yes/no): ");
        String approval = scanner.nextLine();
        
        if (!approval.equalsIgnoreCase("yes")) {
            System.out.println("Prescription rejected by pharmacist");
            return;
        }

        // Get medication ID from name
        String medicationId = null;
        for (Medication med : pharmacyManager.getAllMedications()) {
            if (med.getName().equalsIgnoreCase(prescription.getMedicationName())) {
                medicationId = med.getMedicationId();
                break;
            }
        }

        if (medicationId == null) {
            System.out.println("Medication not found in inventory");
            return;
        }

        if (!pharmacyManager.hasSufficientStock(medicationId, prescription.getQuantity())) {
            System.out.println("Insufficient medication stock");
            return;
        }

        // Payment Processing
        System.out.println("\nSelect Payment Method:");
        System.out.println("1. Insurance");
        System.out.println("2. Cash");
        System.out.println("3. Card");
        System.out.print("Choose payment method: ");
        
        int paymentChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        boolean paymentSuccess = false;
        String[] paymentDetails;
        
        switch (paymentChoice) {
            case 1:
                System.out.println("\nAvailable Insurance Providers:");
                System.out.println("1. BlueCross BlueShield");
                System.out.println("2. Aetna");
                System.out.println("3. UnitedHealthcare");
                System.out.println("4. Cigna");
                System.out.println("5. Other");
                System.out.print("Select insurance provider: ");
                
                int providerChoice = scanner.nextInt();
                scanner.nextLine();
                
                System.out.print("Enter insurance member ID: ");
                String memberId = scanner.nextLine();
                
                System.out.println("\nVerifying insurance coverage...");
                paymentSuccess = paymentProcessor.processPayment(
                    "INSURANCE",
                    prescription.getPrice(),
                    memberId,
                    prescription.getMedicationName()
                );
                break;
                
            case 2:
                System.out.printf("Total amount due: $%.2f%n", prescription.getPrice());
                System.out.print("Enter amount received: $");
                double cashReceived = scanner.nextDouble();
                scanner.nextLine();
                
                if (cashReceived >= prescription.getPrice()) {
                    double change = cashReceived - prescription.getPrice();
                    System.out.printf("Change due: $%.2f%n", change);
                    paymentSuccess = paymentProcessor.processPayment("CASH", prescription.getPrice());
                } else {
                    System.out.println("Insufficient payment amount");
                }
                break;
                
            case 3:
                System.out.print("Enter card number: ");
                String cardNumber = scanner.nextLine();
                
                System.out.print("Enter expiration date (MM/YY): ");
                String expDate = scanner.nextLine();
                
                System.out.print("Enter CVV: ");
                String cvv = scanner.nextLine();
                
                paymentSuccess = paymentProcessor.processPayment(
                    "CARD",
                    prescription.getPrice(),
                    cardNumber,
                    expDate,
                    cvv
                );
                break;
                
            default:
                System.out.println("Invalid payment method selected");
        }

        if (paymentSuccess) {
            pharmacyManager.updateStock(medicationId, prescription.getQuantity());
            System.out.println("\nPrescription filled successfully!");
            System.out.println("Please provide usage instructions to the patient:");
            System.out.println("- " + prescription.getInstructions());
            System.out.println("- " + pharmacyManager.getMedication(medicationId).getWarnings());
        } else {
            System.out.println("Payment processing failed. Please try again.");
        }
    }

    private void recordInventoryRestock() {
        while (true) {
            System.out.println("\nPharmacy Inventory Management");
            System.out.println("1. Place New Order");
            System.out.println("2. View Pending Orders");
            System.out.println("3. Receive Medicine Delivery");
            System.out.println("4. View Current Inventory");
            System.out.println("5. Return to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    placeMedicineOrder();
                    break;
                case 2:
                    viewPendingOrders();
                    break;
                case 3:
                    receiveMedicineDelivery();
                    break;
                case 4:
                    viewInventory();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private void placeMedicineOrder() {
        System.out.println("\nAvailable Suppliers:");
        List<MedicineSupplier> suppliers = pharmacyManager.getAvailableSuppliers();
        for (int i = 0; i < suppliers.size(); i++) {
            MedicineSupplier supplier = suppliers.get(i);
            System.out.printf("%d. %s (License: %s)%n", 
                i + 1, supplier.getName(), supplier.getLicenseNumber());
        }
        
        System.out.print("Select supplier (1-" + suppliers.size() + "): ");
        int supplierChoice = scanner.nextInt();
        scanner.nextLine();
        
        if (supplierChoice < 1 || supplierChoice > suppliers.size()) {
            System.out.println("Invalid supplier selection");
            return;
        }

        System.out.println("\nAvailable Medicines:");
        List<Medicine> medicines = pharmacyManager.getAllMedicines();
        for (int i = 0; i < medicines.size(); i++) {
            Medicine med = medicines.get(i);
            System.out.printf("%d. %s (Current Stock: %d)%n", 
                i + 1, med.getName(), med.getQuantity());
        }

        System.out.print("Select medicine to order (1-" + medicines.size() + "): ");
        int medicineChoice = scanner.nextInt();
        scanner.nextLine();

        if (medicineChoice < 1 || medicineChoice > medicines.size()) {
            System.out.println("Invalid medicine selection");
            return;
        }

        System.out.print("Enter quantity to order: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        MedicineSupplier supplier = suppliers.get(supplierChoice - 1);
        Medicine medicine = medicines.get(medicineChoice - 1);
        
        MedicineOrder order = pharmacyManager.createMedicineOrder(
            supplier.getSupplierId(), medicine, quantity);
        
        System.out.println("\nOrder placed successfully!");
        System.out.println("Order ID: " + order.getOrderId());
        System.out.println("Total Cost: $" + order.getOrderTotal());
        System.out.println("Status: " + order.getStatus());
    }

    private void viewPendingOrders() {
        System.out.println("\nPending Medicine Orders");
        System.out.println("--------------------------------------------------");
        List<MedicineOrder> pendingOrders = pharmacyManager.getPendingOrders();
        
        if (pendingOrders.isEmpty()) {
            System.out.println("No pending orders found.");
            return;
        }

        for (MedicineOrder order : pendingOrders) {
            System.out.printf("Order ID: %s%n", order.getOrderId());
            System.out.printf("Medicine: %s%n", order.getMedicine().getName());
            System.out.printf("Quantity Ordered: %d%n", order.getQuantityOrdered());
            System.out.printf("Order Total: $%.2f%n", order.getOrderTotal());
            System.out.printf("Order Date: %s%n", 
                order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            System.out.printf("Status: %s%n", order.getStatus());
            System.out.println("--------------------------------------------------");
        }
    }

    private void receiveMedicineDelivery() {
        System.out.println("\nReceive Medicine Delivery");
        System.out.print("Enter Order ID: ");
        String orderId = scanner.nextLine();

        System.out.print("Enter received quantity: ");
        int quantityReceived = scanner.nextInt();
        scanner.nextLine();

        pharmacyManager.receiveMedicineDelivery(orderId, quantityReceived);
        System.out.println("Delivery recorded successfully!");
    }

    private void generateReport() {
        System.out.println("\nGenerating Pharmacy Report...");
        analyticsService.generateAnalyticsReport();
        System.out.println("Report generated successfully!");
        System.out.println("Location: ./src/main/java/store/data/" + storeId + "_pharmacy_report_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".txt");
    }

    private boolean processPayment(int paymentChoice, double amount) {
        String paymentMethod;
        switch (paymentChoice) {
            case 1:
                paymentMethod = "INSURANCE";
                break;
            case 2:
                paymentMethod = "CASH";
                break;
            case 3:
                paymentMethod = "CARD";
                break;
            default:
                return false;
        }
        
        return paymentProcessor.processPayment(paymentMethod, amount);
    }

    private void viewInventory() {
        System.out.println("\nCurrent Medication Inventory");
        System.out.println("--------------------------------------------------");
        System.out.printf("%-15s %-20s %-10s %-15s %-10s%n", 
            "ID", "Name", "Stock", "Category", "Price");
        
        for (Medicine med : pharmacyManager.getAllMedicines()) {
            System.out.printf("%-15s %-20s %-10d %-15s $%-9.2f%n",
                med.getMedicineId(),
                med.getName(),
                med.getQuantity(),
                med.getCategory(),
                med.getPrice());
        }
    }

    private void processOTCPurchase() {
        System.out.println("\nOver-the-Counter Purchase");
        System.out.print("Enter medication ID: ");
        String medicationId = scanner.nextLine();

        Medication medication = pharmacyManager.getMedication(medicationId);
        if (medication == null || !medication.getType().equals("OTC")) {
            System.out.println("Invalid OTC medication ID");
            return;
        }

        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        if (!pharmacyManager.hasSufficientStock(medicationId, quantity)) {
            System.out.println("Insufficient stock");
            return;
        }

        double amount = medication.getPrice() * quantity;
        System.out.printf("Total amount: $%.2f%n", amount);

        System.out.println("\nSelect payment method:");
        System.out.println("1. Cash");
        System.out.println("2. Card");
        System.out.print("Choose an option: ");
        
        int paymentChoice = scanner.nextInt();
        scanner.nextLine();

        boolean paymentSuccess = false;
        
        switch (paymentChoice) {
            case 1:
                System.out.printf("Total amount due: $%.2f%n", amount);
                System.out.print("Enter amount received: $");
                double cashReceived = scanner.nextDouble();
                scanner.nextLine();
                
                if (cashReceived >= amount) {
                    double change = cashReceived - amount;
                    System.out.printf("Change due: $%.2f%n", change);
                    paymentSuccess = paymentProcessor.processPayment("CASH", amount);
                } else {
                    System.out.println("Insufficient payment amount");
                }
                break;
                
            case 2:
                System.out.print("Enter card number: ");
                String cardNumber = scanner.nextLine();
                
                System.out.print("Enter expiration date (MM/YY): ");
                String expDate = scanner.nextLine();
                
                System.out.print("Enter CVV: ");
                String cvv = scanner.nextLine();
                
                paymentSuccess = paymentProcessor.processPayment(
                    "CARD",
                    amount,
                    cardNumber,
                    expDate,
                    cvv
                );
                break;
                
            default:
                System.out.println("Invalid payment method selected");
        }

        if (paymentSuccess) {
            pharmacyManager.updateStock(medicationId, quantity);
            System.out.println("Purchase completed successfully!");
        } else {
            System.out.println("Payment failed. Transaction cancelled.");
        }
    }
}