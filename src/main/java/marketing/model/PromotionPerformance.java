package marketing.model;


import java.time.LocalDateTime;
import java.util.*;

public class PromotionPerformance {
    private String promotionId;
    private int totalSales;
    private double revenue;
    private int customerEngagements;
    private int inventoryStart;
    private int inventoryCurrent;
    private Map<String, Integer> productSales;        // Track sales by product
    private List<SaleRecord> salesHistory;           // Track individual sales
 
    public class SaleRecord {
        private LocalDateTime timestamp;
        private String productId;
        private double amount;
        private String customerId;  // Optional: if you want to track customer details

        public SaleRecord(String productId, double amount) {
            this.timestamp = LocalDateTime.now();
            this.productId = productId;
            this.amount = amount;
        }

        public LocalDateTime getTimestamp() { return timestamp; }
        public String getProductId() { return productId; }
        public double getAmount() { return amount; }
    }

    
    public PromotionPerformance(String promotionId) {
        this.promotionId = promotionId;
        this.totalSales = 0;
        this.revenue = 0.0;
        this.customerEngagements = 0;
        this.inventoryStart = 0;
        this.inventoryCurrent = 0;
        this.productSales = new HashMap<>();
        this.salesHistory = new ArrayList<>();
    }

   // Updated addSale method to track product-specific sales
   public void addSale(String productId, double amount) {
    this.totalSales++;
    this.revenue += amount;
    this.customerEngagements++;  // Each sale counts as an engagement
    
    // Track product-specific sales
    productSales.put(productId, productSales.getOrDefault(productId, 0) + 1);
    
    // Record the sale
    salesHistory.add(new SaleRecord(productId, amount));
    
    // Update inventory
    this.inventoryCurrent--;  // Decrease current inventory by 1
}


    public int getTotalSales() { return totalSales; }
    public double getRevenue() { return revenue; }
    public String getPromotionId() { return promotionId; }


    public void setInitialInventory(int amount) {
        this.inventoryStart = amount;
        this.inventoryCurrent = amount;
    }

    public void updateInventory(int newAmount) {
        this.inventoryCurrent = newAmount;
    }

    public void incrementEngagement() {
        this.customerEngagements++;
    }

    public int getCustomerEngagements() { 
        return customerEngagements; 
    }

    public int getInventoryStart() { 
        return inventoryStart; 
    }

    public int getInventoryCurrent() { 
        return inventoryCurrent; 
    }

    public double getInventoryTurnover() {
        if (inventoryStart == 0) return 0;
        return ((inventoryStart - inventoryCurrent) / (double)inventoryStart) * 100;
    }

    public Map<String, Integer> getProductSales() {
        return new HashMap<>(productSales);
    }

    public List<SaleRecord> getSalesHistory() {
        return new ArrayList<>(salesHistory);
    }

    public double getAverageSaleValue() {
        return totalSales > 0 ? revenue / totalSales : 0.0;
    }

    public String getProductPerformanceReport() {
        StringBuilder report = new StringBuilder();
        report.append("\nProduct Sales Breakdown:\n");
        for (Map.Entry<String, Integer> entry : productSales.entrySet()) {
            report.append(String.format("Product %s: %d units sold%n", 
                entry.getKey(), entry.getValue()));
        }
        return report.toString();
    }

    public String getSalesReport() {
        StringBuilder report = new StringBuilder();
        report.append(String.format("Promotion ID: %s%n", promotionId));
        report.append(String.format("Total Sales: %d%n", totalSales));
        report.append(String.format("Total Revenue: $%.2f%n", revenue));
        report.append(String.format("Average Sale Value: $%.2f%n", getAverageSaleValue()));
        report.append(String.format("Customer Engagements: %d%n", customerEngagements));
        report.append(String.format("Inventory Turnover: %.1f%%%n", getInventoryTurnover()));
        report.append(getProductPerformanceReport());
        return report.toString();
    }



}