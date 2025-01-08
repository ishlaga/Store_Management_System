package report.model;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

/**
 * Model class representing a sales report with revenue
 * and product sales information.
 *
 * @author Hrishikesha
 */
public class SalesReport {
    private String storeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalRevenue;
    private int totalUnits;
    private Map<String, Integer> productSales; // productId -> units sold
    private Map<String, Double> productRevenue; // productId -> revenue

    public SalesReport(String storeId, LocalDate startDate, LocalDate endDate) {
        this.storeId = storeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalRevenue = 0.0;
        this.totalUnits = 0;
        this.productSales = new HashMap<>();
        this.productRevenue = new HashMap<>();
    }

    // Getters
    public String getStoreId() { return storeId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getTotalRevenue() { return totalRevenue; }
    public int getTotalUnits() { return totalUnits; }
    public Map<String, Integer> getProductSales() { return productSales; }
    public Map<String, Double> getProductRevenue() { return productRevenue; }

    // Methods to update report data
    public void addSale(String productId, int units, double revenue) {
        productSales.put(productId, productSales.getOrDefault(productId, 0) + units);
        productRevenue.put(productId, productRevenue.getOrDefault(productId, 0.0) + revenue);
        totalUnits += units;
        totalRevenue += revenue;
    }
}