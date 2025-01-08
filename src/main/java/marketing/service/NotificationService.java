package marketing.service;

import marketing.model.Promotion;
import java.time.format.DateTimeFormatter;

public class NotificationService {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void notifyStoreManager(Promotion promo) {
        System.out.println("\n=== Store Manager Notification ===");
        System.out.printf("New promotion '%s' starting on %s%n", 
            promo.getName(), 
            promo.getStartDate().format(formatter));
        System.out.println("Details:");
        System.out.printf("- Type: %s%n", promo.getType());
        System.out.printf("- Discount: %.2f%%%n", promo.getDiscount());
        System.out.printf("- Duration: %s to %s%n", 
            promo.getStartDate().format(formatter),
            promo.getEndDate().format(formatter));
        System.out.println("Action Required: Please prepare in-store displays and signage.");
        System.out.println("Note: Ensure adequate inventory for promoted items.");
    }

    public void notifyCustomers(Promotion promo) {
        System.out.println("\n=== Customer Notification ===");
        System.out.printf("🎉 Don't miss our %s!%n", promo.getName());
        switch(promo.getType()) {
            case "PERCENTAGE_OFF":
                System.out.printf("Get %.0f%% off on selected items!%n", promo.getDiscount());
                break;
            case "BOGO":
                System.out.println("Buy One Get One Free on selected items!");
                break;
            case "LOYALTY_POINTS_MULTIPLIER":
                System.out.printf("Earn %.0fX loyalty points on your purchase!%n", promo.getDiscount());
                break;
        }
        System.out.printf("Valid from %s to %s%n", 
            promo.getStartDate().format(formatter),
            promo.getEndDate().format(formatter));
    }

    public void notifyLowInventory(String productId, int currentStock) {
        System.out.println("\n=== Low Inventory Alert ===");
        System.out.printf("! Low stock warning for product %s%n", productId);
        System.out.printf("Current stock: %d units%n", currentStock);
        System.out.println("Please restock soon to maintain promotion effectiveness.");
    }
}