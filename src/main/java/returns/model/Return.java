package returns.model;

import inventory.model.Product;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Model class representing a return request with its items
 * and processing status.
 *
 * @author Hrishikesha
 */
public class Return {
    private String returnId;          // Unique identifier for the return
    private String orderId;           // Original order ID
    private String storeId;           // Store identifier
    private Map<Product, ReturnItem> items;  // Items being returned
    private boolean approved;         // Return approval status
    private String refundMethod;      // Method of refund
    private double refundAmount;      // Total refund amount
    private LocalDateTime returnDate; // Date and time of return

    /**
     * Creates a new return request
     * @param returnId Unique identifier for the return
     * @param orderId Original order identifier
     * @param storeId Store identifier
     */
    public Return(String returnId, String orderId, String storeId) {
        this.returnId = returnId;
        this.orderId = orderId;
        this.storeId = storeId;
        this.items = new HashMap<>();
        this.approved = false;
        this.returnDate = LocalDateTime.now();
        this.refundAmount = 0.0;
    }

    public void addItem(Product product, int quantity, boolean isDamaged) {
        ReturnItem item = new ReturnItem(product, quantity, isDamaged);
        items.put(product, item);
        this.refundAmount += item.getRefundAmount();
    }

    public String getReturnId() { return returnId; }
    public String getOrderId() { return orderId; }
    public String getOriginalOrderId() { return orderId; }
    public Map<Product, ReturnItem> getItems() { return items; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public double getRefundAmount() { return refundAmount; }
    public LocalDateTime getReturnDate() { return returnDate; }
    public String getRefundMethod() { return refundMethod; }
    public void setRefundMethod(String refundMethod) { this.refundMethod = refundMethod; }
}