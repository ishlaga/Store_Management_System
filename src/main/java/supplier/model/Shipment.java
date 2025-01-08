package supplier.model;

import inventory.model.Product;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Model class representing a shipment from a supplier
 * with its items and status.
 *
 * @author Eshant Chintareddy
 */
public class Shipment {
    private String id;                          // Unique shipment identifier
    private String storeId;                     // Destination store
    private String supplierId;                  // Supplier sending shipment
    private Map<Product, Integer> items;        // Products and quantities
    private LocalDateTime scheduledDate;        // Scheduled delivery date
    private LocalDateTime actualDeliveryDate;   // Actual delivery date
    private String status;                      // Current shipment status

    /**
     * Creates a new shipment with the specified details
     * @param id Shipment identifier
     * @param storeId Store identifier
     * @param supplierId Supplier identifier
     * @param scheduledDate Scheduled delivery date
     */
    public Shipment(String id, String storeId, String supplierId, LocalDateTime scheduledDate) {
        this.id = id;
        this.storeId = storeId;
        this.supplierId = supplierId;
        this.scheduledDate = scheduledDate;
        this.items = new HashMap<>();
        this.status = "SCHEDULED";
    }

    public void addItem(Product product, int quantity) {
        items.put(product, quantity);
    }

    public void recordDiscrepancy(String productId, int difference) {
        // ... rest of the implementation
    }

    public LocalDateTime getScheduledDate() {
        return scheduledDate;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getStoreId() { return storeId; }
    public String getSupplierId() { return supplierId; }
    public Map<Product, Integer> getItems() { return items; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public void setDeliveryDate(LocalDateTime date) { this.actualDeliveryDate = date; }
}