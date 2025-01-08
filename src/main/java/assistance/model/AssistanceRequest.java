package assistance.model;

import java.time.LocalDateTime;

public class AssistanceRequest {
    private String id;
    private String customerName;
    private String requestType;
    private String status;
    private String storeId;
    private String productId;
    private String notes;
    private LocalDateTime timestamp;

    public AssistanceRequest(String customerName, String requestType, String storeId, String productId, String notes) {
        this.id = "REQ" + System.currentTimeMillis();
        this.customerName = customerName;
        this.requestType = requestType;
        this.storeId = storeId;
        this.productId = productId;
        this.notes = notes;
        this.status = "PENDING";
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for loading from file
    public AssistanceRequest(String id, String customerName, String requestType, 
                           String storeId, String productId, String status, 
                           String notes, LocalDateTime timestamp) {
        this.id = id;
        this.customerName = customerName;
        this.requestType = requestType;
        this.storeId = storeId;
        this.productId = productId;
        this.status = status;
        this.notes = notes;
        this.timestamp = timestamp;
    }

    // Add getters and setters
    public String getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getRequestType() { return requestType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getStoreId() { return storeId; }
    public String getProductId() { return productId; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}