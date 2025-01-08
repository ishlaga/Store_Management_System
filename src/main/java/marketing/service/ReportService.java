package marketing.service;

import marketing.model.*;
import java.util.*;
import java.time.format.DateTimeFormatter;

public class ReportService {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public void generatePromotionReport(Promotion promo, PromotionPerformance perf) {
        System.out.println("\n=== Promotion Performance Report ===");
        System.out.println("Promotion Details:");
        System.out.printf("- ID: %s%n", promo.getId());
        System.out.printf("- Name: %s%n", promo.getName());
        System.out.printf("- Type: %s%n", promo.getType());
        System.out.printf("- Discount: %.2f%%%n", promo.getDiscount());
        System.out.printf("- Duration: %s to %s%n", 
            promo.getStartDate().format(formatter),
            promo.getEndDate().format(formatter));
        System.out.printf("- Status: %s%n", promo.getStatus());
        
        System.out.println("\nPerformance Metrics:");
        System.out.printf("- Total Sales: %d%n", perf.getTotalSales());
        System.out.printf("- Total Revenue: $%.2f%n", perf.getRevenue());
        if (perf.getTotalSales() > 0) {
            System.out.printf("- Average Sale Value: $%.2f%n", 
                perf.getRevenue() / perf.getTotalSales());
        }
        System.out.printf("- Customer Engagements: %d%n", perf.getCustomerEngagements());
        System.out.printf("- Inventory Turnover: %.1f%%%n", perf.getInventoryTurnover());
        
        System.out.println("\nTarget Products Performance:");
        for (String productId : promo.getTargetProducts()) {
            System.out.printf("- Product %s: Initial Stock: %d, Current Stock: %d%n",
                productId, perf.getInventoryStart(), perf.getInventoryCurrent());
        }
    }
    
    public void generateSummaryReport(List<Promotion> promotions, 
                                    Map<String, PromotionPerformance> performances) {
        System.out.println("\n=== Marketing Campaign Summary Report ===");
        System.out.println("Overall Performance Metrics:");
        
        double totalRevenue = 0;
        int totalSales = 0;
        int totalEngagements = 0;
        
        for (Promotion promo : promotions) {
            PromotionPerformance perf = performances.get(promo.getId());
            if (perf != null) {
                totalRevenue += perf.getRevenue();
                totalSales += perf.getTotalSales();
                totalEngagements += perf.getCustomerEngagements();
            }
        }
        
        System.out.printf("Total Revenue: $%.2f%n", totalRevenue);
        System.out.printf("Total Sales: %d%n", totalSales);
        System.out.printf("Total Customer Engagements: %d%n", totalEngagements);
        if (totalSales > 0) {
            System.out.printf("Average Sale Value: $%.2f%n", totalRevenue / totalSales);
        }
    }
}
