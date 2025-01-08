package marketing.model;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

public class Promotion {
    private String id;
    private String name;
    private String type;        // PERCENTAGE_OFF, BOGO
    private double discount;
    private Set<String> targetProducts;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;     // ACTIVE, COMPLETED
    private String objective; 

    public Promotion(String name, String type, double discount, 
                    LocalDateTime startDate, LocalDateTime endDate) {
        this.id = "PROMO" + System.currentTimeMillis();
        this.name = name;
        this.type = type;
        this.discount = discount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = "ACTIVE";
        this.targetProducts = new HashSet<>();
    }

    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public void addProduct(String productId) { targetProducts.add(productId); }
    // Added missing getters
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public String getObjective() { return objective; }
    public void setObjective(String objective) { this.objective = objective; }


    public Set<String> getTargetProducts() { 
        return targetProducts; 
    }

    public void addTargetProducts(Set<String> products) {
        this.targetProducts.addAll(products);
    }

}