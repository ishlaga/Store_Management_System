package loyalty;

import java.util.Scanner;
import loyalty.service.LoyaltyManager;
import java.util.Map;

public class LoyaltySystem {
    private final LoyaltyManager loyaltyManager;
    private final Scanner scanner;
    private final String storeId;

    public LoyaltySystem(String storeId) {
        this.storeId = storeId;
        this.loyaltyManager = new LoyaltyManager();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\nLoyalty Program Management");
            System.out.println("1. Register New Member");
            System.out.println("2. Add Points");
            System.out.println("3. Redeem Points");
            System.out.println("4. Check Balance");
            System.out.println("5. View All Members");
            System.out.println("6. Back to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: registerMember(); break;
                case 2: addPoints(); break;
                case 3: redeemPoints(); break;
                case 4: checkBalance(); break;
                case 5: viewAllMembers(); break;
                case 6: return;
                default: System.out.println("Invalid option");
            }
        }
    }

    private void registerMember() {
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();
        
        if (loyaltyManager.registerMember(name, phone)) {
            System.out.println("Member registered successfully!");
        } else {
            System.out.println("Registration failed. Phone number might already exist.");
        }
    }

    private void addPoints() {
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();
        System.out.print("Enter purchase amount: $");
        double amount = scanner.nextDouble();
        
        int points = (int)amount;
        if (loyaltyManager.addPoints(phone, points)) {
            System.out.println(points + " points added successfully!");
        } else {
            System.out.println("Failed to add points. Member not found.");
        }
    }

    private void redeemPoints() {
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();
        System.out.print("Enter points to redeem: ");
        int points = scanner.nextInt();

        if (loyaltyManager.redeemPoints(phone, points)) {
            double discount = points / 300.0 * 10.0;
            System.out.printf("Redeemed %d points for $%.2f discount!%n", points, discount);
        } else {
            System.out.println("Redemption failed. Insufficient points or member not found.");
        }
    }

    private void checkBalance() {
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();
        
        int points = loyaltyManager.getPoints(phone);
        if (points >= 0) {
            System.out.println("Current points balance: " + points);
            System.out.printf("Equivalent to: $%.2f in discounts%n", points / 300.0 * 10.0);
        } else {
            System.out.println("Member not found.");
        }
    }

    private void viewAllMembers() {
        Map<String, Integer> members = loyaltyManager.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("No members found.");
        } else {
            System.out.println("Members and their points:");
            for (Map.Entry<String, Integer> entry : members.entrySet()) {
                System.out.printf("Phone: %s, Name: %s, Points: %d%n", entry.getKey(), loyaltyManager.getNames().get(entry.getKey()), entry.getValue());
            }
        }
    }
} 