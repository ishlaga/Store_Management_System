package waste.model;

import java.time.LocalDateTime;

/**
 * Represents a waste item in the store's waste management system.
 * This class tracks product details, expiration, status, and disposal methods
 * for items that are flagged for waste management or donation.
 *
 * @author Hrishikesha Kyathsandra
 */
public class WasteItem {
    private String productId;
    private String name;
    private int quantity;
    private double value;
    private String category; // RECYCLABLE, COMPOSTABLE, GENERAL, HAZARDOUS
    private LocalDateTime expirationDate;
    private String disposalMethod;
    private String status; // FLAGGED, INSPECTED, DISPOSED, DONATED
    private String department;
    private String reason; // EXPIRED, DAMAGED, RECALLED

    public WasteItem(String productId, String name, int quantity, double value, 
                    String category, LocalDateTime expirationDate, String department) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.value = value;
        this.category = category;
        this.expirationDate = expirationDate;
        this.department = department;
        this.status = "FLAGGED";
        this.disposalMethod = "PENDING";
    }

    // Getters and setters
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getValue() { return value; }
    public String getCategory() { return category; }
    public LocalDateTime getExpirationDate() { return expirationDate; }
    public String getDisposalMethod() { return disposalMethod; }
    public void setDisposalMethod(String method) { this.disposalMethod = method; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDepartment() { return department; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
} 