import assistance.service.CustomerAssistanceManager;
import checkout.service.CheckoutManager;
import curbside.CurbsideSystem;
import curbside.service.CurbsideInventoryManager;
import deli.service.DeliCounterManager;
import delivery.service.DeliveryManager;
import donation.service.DonationManager;
import employee.HealthcareManagementSystem;
import employee.PayrollSystem;
import energy.EnergyConsumptionSystem;
import eyetest.service.EyeTestManager;
import gas.GasStationSystem;
import greeter.service.GreeterManager;
import headoffice.HeadOfficeManagementSystem;
import inventory.InventoryManagementSystem;
import java.util.Scanner;
import lostandfound.service.LostFoundManager;
import loyalty.LoyaltySystem;
import maintenance.MaintenanceSystem;
import marketing.service.MarketingManager;
import order.OrderManagementSystem;
import parking.service.ParkingManager;
import pharmacy.PharmacySystem;
import report.SalesReportSystem;
import returns.ReturnManagementSystem;
import security.service.SecurityManager;
import store.model.Store;
import store.service.StoreManager;
import supplier.SupplierManagementSystem;
import tastetest.service.TasteTestManager;
import training.TrainingSystem;
import waste.service.WasteManagementSystem;



public class Main {
    private static StoreManager storeManager = new StoreManager();
    private static HeadOfficeManagementSystem headOfficeManagementSystem = new HeadOfficeManagementSystem(storeManager);
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        String storeId = null;
        
        while (true) {
            if (storeId == null) {
                storeId = selectStore();   
                if (storeId == null) {
                    System.out.println("Exiting...");
                    break;
                }
            }

            System.out.println("\nGrocery Store Management System");
            System.out.println("1. Inventory Management (Store Manager)");
            System.out.println("2. Order Management (Store Cashier)");
            System.out.println("3. Procurement Oversight (Store Employee)");
            System.out.println("4. Sales Report (Store Manager)");
            System.out.println("5. Returns and Refunds (Store Cashier)");
            System.out.println("6. Employee Payroll (HR Manager)");
            System.out.println("7. Store Cleaning and Maintenance (Store Manager)");
            System.out.println("8. Customer Assistance (Service Associate)");
            System.out.println("9. Security Management (Security Officer)");
            System.out.println("10. Marketing Management (Marketing Manager)");
            System.out.println("11. Gas Station Management (Station Manager)");
            System.out.println("12. Pharmacy Management (Pharmacy Technician)");
            System.out.println("13. Head Office Management (Head Office Manager)");
            System.out.println("14. Store Performance Analytics (Head Office Manager)");
            System.out.println("15. Lost and Found Management (Customer Service)");
            System.out.println("16. Loyalty Program Management (Store Cashier)");
            System.out.println("17. Self-Checkout Management (Checkout Supervisor)");
            System.out.println("18. Delivery Management (Delivery Manager)");
            System.out.println("19. Donation Management (Community Manager)");
            System.out.println("20. Parking Management (Parking Supervisor)");         
            System.out.println("21. Greeter Verification (Greeter)");
            System.out.println("22. Deli Counter Operations (Deli Clerk)");
            System.out.println("23. Employee Healthcare Management (HR Manager)");
            System.out.println("24. Energy Consumption Tracking (Store Manager)");
            System.out.println("25. Taste Test Management (Product Specialist)");
            System.out.println("26. Eye Test Management (Optometry Manager)");
            System.out.println("27. Curbside Pickup Management (Curbside Associate)");
            System.out.println("28. Training System (HR Manager)");
            System.out.println("29. Waste Management (Waste Manager)");
            System.out.println("30. External Vendor Management (Store Employee)");
            System.out.println("31. Change Store");
            System.out.println("32. Exit");
            System.out.print("Choose a use case: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    new InventoryManagementSystem(storeId).start();
                    break;
                case 2:
                    new OrderManagementSystem(storeId).start();
                    break;
                case 3:
                    new SupplierManagementSystem(storeId).start();
                    break;
                case 4:
                    new SalesReportSystem(storeId).start();
                    break;
                case 5:
                    new ReturnManagementSystem(storeId).start();
                    break;
                case 6:
                    new PayrollSystem(storeId).start();
                    break;
                case 7:
                    new MaintenanceSystem(storeId).start();
                    break;    
                case 8:
                    new CustomerAssistanceManager(storeId).startAssistance();
                    break;
                case 9:
                    new SecurityManager().startMonitoring();
                    break;
                case 10:
                    new MarketingManager().start();
                    break;
                case 11:
                    new GasStationSystem(storeId).start();
                    break;
                case 12:
                    new PharmacySystem(storeId).start();
                    break;
                case 13:
                    headOfficeManagementSystem.start(); // Access Head Office Management
                    break;
                case 14:
                    headOfficeManagementSystem.viewStorePerformance();
                    break;
                case 15:
                    new LostFoundManager().startLostFoundManagement();
                    break;
                case 16:
                    new LoyaltySystem(storeId).start();
                    break;
                case 17:
                    new CheckoutManager().startCheckoutManagement();
                    break;
                case 18:
                    new DeliveryManager().startDeliveryManagement();
                    break;
                case 19:
                    new DonationManager().startDonationManagement();
                    break;
                case 20:
                    new ParkingManager().startParkingManagement();
                    break;
                case 21:
                    new GreeterManager().startGreeterVerification();
                    break;
                case 22:
                    new DeliCounterManager().startDeliOperations();
                    break;
                case 23:
                    new HealthcareManagementSystem(storeId).start();
                    break;
                case 24:
                    new EnergyConsumptionSystem(storeId).start();
                    break;
                case 25:
                    new TasteTestManager().startTasteTestManagement();
                    break;
                case 26:
                    new EyeTestManager().startEyeTestManagement();
                    break;
                case 27:
                    CurbsideInventoryManager curbsideInventoryManager = new CurbsideInventoryManager();
                     new CurbsideSystem(storeId, curbsideInventoryManager).start();
                     break;                  
                case 28:
                    TrainingSystem.main(args);
                  break;
                case 29:
                    new WasteManagementSystem(storeId).start();
                    break;

                case 30:
                    new SupplierManagementSystem(storeId).start();
                    break;

                case 31:
                    storeId = null;
                    break;
                case 32:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option");
            }   
        }
    }

    private static String selectStore() {
        System.out.println("\nAvailable Stores:");
        for (Store store : storeManager.getAllStores()) {
            System.out.printf("%s - %s (%s)%n", 
                store.getStoreId(), 
                store.getName(), 
                store.getLocation());
        }
        System.out.print("Enter Store ID (or 'exit' to quit): ");
        String input = scanner.nextLine();
        return input.equalsIgnoreCase("exit") ? null : input;
    }
}