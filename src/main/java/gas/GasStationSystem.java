package gas;

import gas.model.*;
import gas.service.*;
import payment.service.PaymentProcessor;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.List;

/**
 * Main system class for managing automated gas station operations.
 * Handles customer self-service, system monitoring, and management functions.
 *
 * @author Hrishikesha Kyathsandra
 */
public class GasStationSystem {
    private GasStationManager gasManager;
    private PaymentProcessor paymentProcessor;
    private Scanner scanner;
    private String storeId;
    private GasAnalyticsService analyticsService;

    /**
     * Initializes the gas station system with required components.
     * @param storeId Unique identifier for the gas station
     */
    public GasStationSystem(String storeId) {
        this.storeId = storeId;
        this.gasManager = new GasStationManager(storeId);
        this.paymentProcessor = new PaymentProcessor();
        this.analyticsService = new GasAnalyticsService(gasManager, storeId);
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts the main system interface and handles user interactions.
     * Provides options for customer self-service and system management.
     */
    public void start() {
        while (true) {
            System.out.println("\nGas Station Management System");
            System.out.println("1. Start New Refueling (Customer)");
            System.out.println("2. Check Tire Pressure (Customer)");
            System.out.println("3. View Fuel Stock Levels (Stock System)");
            System.out.println("4. View Pump Status (Pump System)");
            System.out.println("5. Fuel Inventory Management (Fuel Supplier)");
            System.out.println("6. Perform Safety Check (Safety System)");
            System.out.println("7. Generate Sales Report (Manager)");
            System.out.println("8. Return to Main Menu");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: processRefueling(); break;
                case 2: checkTirePressure(); break;
                case 3: viewFuelStock(); break;
                case 4: viewPumpStatus(); break;
                case 5: recordTankRefill(); break;
                case 6: performSafetyCheck(); break;
                case 7: 
                    analyticsService.generateSalesReport(); 
                    System.out.println("\nSales report generated successfully!");
                    System.out.println("Location: ./src/main/java/store/data/" + storeId + "_gas_sales_" + 
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".txt");
                    break;
                case 8: return;
                default: System.out.println("Invalid option");
            }
        }
    }

    /**
     * Processes a new refueling transaction.
     * Handles pump selection, payment, and fuel dispensing.
     */
    private void processRefueling() {
        System.out.print("Enter pump number (1-8): ");
        int pumpNumber = scanner.nextInt();
        scanner.nextLine();

        if (!gasManager.isPumpOperational(pumpNumber)) {
            System.out.println("Pump is not operational. Please choose another pump.");
            return;
        }

        System.out.println("\nSelect payment method:");
        System.out.println("1. Pre-pay at station");
        System.out.println("2. Pay at pump");
        System.out.println("3. Post-pay at counter");
        System.out.print("Choose an option: ");
        
        int paymentChoice = scanner.nextInt();
        scanner.nextLine();

        System.out.println("\nSelect fuel type:");
        System.out.println("1. Regular (87)");
        System.out.println("2. Plus (89)");
        System.out.println("3. Premium (93)");
        System.out.print("Choose an option: ");
        
        int fuelChoice = scanner.nextInt();
        scanner.nextLine();

        FuelType fuelType = FuelType.values()[fuelChoice - 1];
        
        System.out.print("Enter amount (gallons): ");
        double gallons = scanner.nextDouble();
        scanner.nextLine();

        // Create refueling transaction
        String transactionId = "GAS" + System.currentTimeMillis();
        RefuelingTransaction transaction = new RefuelingTransaction(
            transactionId, pumpNumber, fuelType, gallons
        );

        if (gasManager.startRefueling(transaction)) {
            double amount = transaction.getAmount();
            boolean paymentSuccess = processPayment(paymentChoice, amount);
            
            if (paymentSuccess) {
                simulateFueling(transaction);
                gasManager.completeRefueling(transaction);
                printReceipt(transaction);
            } else {
                gasManager.cancelRefueling(transaction);
                System.out.println("Transaction cancelled due to payment failure");
            }
        }
    }

    private boolean processPayment(int paymentChoice, double amount) {
        switch (paymentChoice) {
            case 1: // Pre-pay
            case 2: // Pay at pump
                System.out.println("Enter card number: ");
                String cardNumber = scanner.nextLine();
                return paymentProcessor.processCardPayment(cardNumber, amount);
            
            case 3: // Post-pay
                System.out.println("Collect payment at counter");
                return true;
            
            default:
                return false;
        }
    }

    private void printReceipt(RefuelingTransaction transaction) {
        System.out.println("\n=== Gas Station Receipt ===");
        System.out.println("Transaction ID: " + transaction.getTransactionId());
        System.out.println("Date: " + transaction.getTimestamp());
        System.out.println("Pump: " + transaction.getPumpNumber());
        System.out.println("Fuel Type: " + transaction.getFuelType());
        System.out.println("Gallons: " + transaction.getGallons());
        System.out.printf("Amount: $%.2f%n", transaction.getAmount());
        System.out.println("Thank you for your business!");
        System.out.println("========================");
    }

    private void viewFuelStock() {
        System.out.println("\nCurrent Fuel Stock Levels");
        System.out.println("--------------------------------------------------");
        System.out.printf("%-15s %-12s %-10s%n", "Fuel Type", "Octane", "Gallons");
        
        for (FuelType type : FuelType.values()) {
            System.out.printf("%-15s %-12d %.2f%n",
                type.getName(),
                type.getOctane(),
                gasManager.getFuelStock(type));
        }
    }

    private void recordTankRefill() {
        while (true) {
            System.out.println("\nFuel Tank Refill Management");
            System.out.println("1. Place New Order");
            System.out.println("2. View Pending Orders");
            System.out.println("3. Receive Fuel Delivery");
            System.out.println("4. View Fuel Stock Levels");
            System.out.println("5. Return to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    placeFuelOrder();
                    break;
                case 2:
                    viewPendingOrders();
                    break;
                case 3:
                    receiveFuelDelivery();
                    break;
                case 4:
                    viewFuelStock();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private void placeFuelOrder() {
        System.out.println("\nAvailable Suppliers:");
        List<FuelSupplier> suppliers = gasManager.getAvailableSuppliers();
        for (int i = 0; i < suppliers.size(); i++) {
            FuelSupplier supplier = suppliers.get(i);
            System.out.printf("%d. %s (%s)%n", i + 1, supplier.getName(), supplier.getContact());
        }
        
        System.out.print("Select supplier (1-" + suppliers.size() + "): ");
        int supplierChoice = scanner.nextInt();
        scanner.nextLine();
        
        if (supplierChoice < 1 || supplierChoice > suppliers.size()) {
            System.out.println("Invalid supplier selection");
            return;
        }

        System.out.println("\nSelect fuel type:");
        System.out.println("1. Regular (87)");
        System.out.println("2. Plus (89)");
        System.out.println("3. Premium (93)");
        System.out.print("Choose fuel type: ");
        int fuelChoice = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter amount (gallons): ");
        double gallons = scanner.nextDouble();
        scanner.nextLine();

        FuelSupplier supplier = suppliers.get(supplierChoice - 1);
        FuelType fuelType = FuelType.values()[fuelChoice - 1];
        
        FuelOrder order = gasManager.createFuelOrder(supplier.getSupplierId(), fuelType, gallons);
        System.out.println("\nOrder placed successfully!");
        System.out.println("Order ID: " + order.getOrderId());
        System.out.println("Status: " + order.getStatus());
    }

    private void viewPendingOrders() {
        System.out.println("\nPending Fuel Orders");
        System.out.println("--------------------------------------------------");
        List<FuelOrder> pendingOrders = gasManager.getPendingOrders();
        
        if (pendingOrders.isEmpty()) {
            System.out.println("No pending orders found.");
            return;
        }

        for (FuelOrder order : pendingOrders) {
            System.out.printf("Order ID: %s%n", order.getOrderId());
            System.out.printf("Fuel Type: %s%n", order.getFuelType().getName());
            System.out.printf("Gallons Ordered: %.2f%n", order.getGallonsOrdered());
            System.out.printf("Order Date: %s%n", 
                order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            System.out.printf("Status: %s%n", order.getStatus());
            System.out.println("--------------------------------------------------");
        }
    }

    private void receiveFuelDelivery() {
        System.out.println("\nReceive Fuel Delivery");
        System.out.print("Enter Order ID: ");
        String orderId = scanner.nextLine();

        System.out.print("Enter received amount (gallons): ");
        double gallonsReceived = scanner.nextDouble();
        scanner.nextLine();

        gasManager.receiveFuelDelivery(orderId, gallonsReceived);
        System.out.println("Delivery recorded successfully!");
    }

    private void performSafetyCheck() {
        System.out.println("\nPerforming Safety Check");
        System.out.println("--------------------------------------------------");
        
        for (int i = 1; i <= 8; i++) {
            boolean isOperational = gasManager.performPumpSafetyCheck(i);
            System.out.printf("Pump %d: %s%n", i, 
                isOperational ? "Passed safety check" : "Failed safety check");
        }
    }

    private void viewPumpStatus() {
        System.out.println("\nPump Status Overview");
        System.out.println("--------------------------------------------------");
        System.out.printf("%-8s %-15s %-10s %-15s%n", 
            "Pump #", "Status", "In Use", "Last Maintenance");
        
        for (int i = 1; i <= 8; i++) {
            PumpStatus status = gasManager.getPumpStatus(i);
            System.out.printf("%-8d %-15s %-10s %-15s%n",
                i,
                status.isOperational() ? "Operational" : "Out of Service",
                status.isInUse() ? "Yes" : "No",
                status.getLastMaintenance().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
    }

    private void checkTirePressure() {
        System.out.println("\nTire Pressure Check & Fill Service");
        System.out.println("--------------------------------------------------");
        
        int vehicleType = 0;
        boolean validInput = false;
        
        while (!validInput) {
            System.out.print("Enter vehicle type (1. Car, 2. SUV, 3. Truck): ");
            try {
                String input = scanner.nextLine();
                vehicleType = Integer.parseInt(input);
                if (vehicleType >= 1 && vehicleType <= 3) {
                    validInput = true;
                } else {
                    System.out.println("Error: Please enter a number between 1 and 3.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }

        double recommendedPressure;
        switch (vehicleType) {
            case 1:
                recommendedPressure = 32.0;
                break;
            case 2:
                recommendedPressure = 35.0;
                break;
            case 3:
                recommendedPressure = 40.0;
                break;
            default:
                recommendedPressure = 32.0;
        }

        System.out.printf("Recommended tire pressure: %.1f PSI%n", recommendedPressure);
        
        String[] tirePositions = {"Front Left", "Front Right", "Rear Left", "Rear Right"};
        Random random = new Random();

        while (true) {
            System.out.println("\nSelect tire to check:");
            for (int i = 0; i < tirePositions.length; i++) {
                System.out.println((i + 1) + ". " + tirePositions[i]);
            }
            System.out.println("5. Exit tire check");
            System.out.print("Choose an option: ");
            
            int tireChoice = scanner.nextInt();
            scanner.nextLine();
            
            if (tireChoice == 5) break;
            if (tireChoice < 1 || tireChoice > 4) {
                System.out.println("Invalid tire selection");
                continue;
            }

            String position = tirePositions[tireChoice - 1];
            System.out.printf("\nChecking %s tire...%n", position);
            System.out.println("   _________________");
            System.out.println("  /                 \\");
            System.out.println(" /                   \\");
            System.out.println("|      CHECKING       |");
            System.out.println(" \\                   /");
            System.out.println("  \\_________________/");
            
            int currentPSI = (int) (recommendedPressure + random.nextInt(7) - 10);
            
            for (int i = 0; i < 12; i++) {
                int fluctuation = currentPSI + random.nextInt(5) - 2;
                System.out.print("\rCurrent pressure: " + fluctuation + " PSI");
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            System.out.printf("%nFinal reading: %d PSI%n", currentPSI);
            System.out.printf("Recommended: %.0f PSI%n", recommendedPressure);
            
            if (currentPSI < recommendedPressure) {
                System.out.println("\nTire needs air. Adding air...");
                System.out.println("   _________________");
                System.out.println("  /                 \\");
                System.out.println(" /                   \\");
                System.out.println("|     FILLING AIR     |");
                System.out.println(" \\                   /");
                System.out.println("  \\_________________/");
                
                while (currentPSI < recommendedPressure) {
                    currentPSI++;
                    int displayPSI = currentPSI + random.nextInt(2);
                    System.out.print("\rPressure: " + displayPSI + " PSI");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.println("\nAir added successfully!");
            } else if (currentPSI > recommendedPressure) {
                System.out.println("\nTire is over-inflated. Releasing air...");
                System.out.println("   _________________");
                System.out.println("  /                 \\");
                System.out.println(" /                   \\");
                System.out.println("|   RELEASING AIR     |");
                System.out.println(" \\                   /");
                System.out.println("  \\_________________/");
                
                while (currentPSI > recommendedPressure) {
                    currentPSI--;
                    int displayPSI = currentPSI + random.nextInt(2);
                    System.out.print("\rPressure: " + displayPSI + " PSI");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.println("\nAir released successfully!");
            } else {
                System.out.println("Tire pressure is perfect!");
            }
            
            System.out.println("\nFinal pressure: " + currentPSI + " PSI ✓");
            System.out.println("--------------------------------------------------");
        }
        
        System.out.println("\nTire pressure check complete!");
    }

    private void simulateFueling(RefuelingTransaction transaction) {
        System.out.println("\nStarting fueling process...");
        double currentGallons = 0.0;
        double targetGallons = transaction.getGallons();
        Random random = new Random();
        int canHeight = 10;
        
        while (currentGallons < targetGallons) {
            currentGallons += 0.1;
            double fillPercentage = currentGallons / targetGallons;
            int filledLines = (int) (fillPercentage * canHeight);
            
            // Clear previous output
            System.out.print("\033[H\033[2J");
            System.out.flush();
            
            // Draw gas can with current fill level
            System.out.println("      _________");
            System.out.println("     /         \\");
            System.out.println("  ___\\         /___");
            System.out.println(" |                 |");
            
            // Fill level visualization (from bottom to top)
            for (int i = 0; i < canHeight; i++) {
                if (i >= (canHeight - filledLines)) {
                    System.out.println(" |    #########    |");  // Filled with #
                } else {
                    System.out.println(" |                 |");    // Empty
                }
            }
            
            System.out.println(" |_________________|");
            
            // Display pump information
            System.out.printf("\nPump #%d - %s\n", 
                transaction.getPumpNumber(), 
                transaction.getFuelType().getName());
            System.out.printf("Gallons: %.3f / %.3f\n", 
                currentGallons, targetGallons);
            System.out.printf("Amount: $%.2f\n", 
                currentGallons * transaction.getFuelType().getPricePerGallon());
            
            try {
                Thread.sleep(500); // Increased from 300ms to 500ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("\nFueling complete! Please remove nozzle.");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}