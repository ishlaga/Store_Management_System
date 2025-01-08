package supplier.model;

/**
 * Model class representing a supplier with their details
 * and performance metrics.
 *
 * @author Eshant Chintareddy
 */
public class Supplier {
    private String id;              // Unique supplier identifier
    private String name;            // Supplier name
    private String contactInfo;     // Contact information
    private String address;         // Physical address
    private double rating;          // Supplier rating (0-5)
    private int totalShipments;     // Total number of shipments
    private int lateShipments;      // Number of late shipments

    /**
     * Creates a new supplier with the specified details
     * @param id Supplier identifier
     * @param name Supplier name
     * @param contactInfo Contact information
     * @param address Physical address
     */
    public Supplier(String id, String name, String contactInfo, String address) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
        this.address = address;
        this.rating = 5.0;
        this.totalShipments = 0;
        this.lateShipments = 0;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getContactInfo() { return contactInfo; }
    public String getAddress() { return address; }
    public double getRating() { return rating; }
    
    public void updatePerformance(boolean isLateDelivery) {
        totalShipments++;
        if (isLateDelivery) lateShipments++;
        rating = 5.0 * (1 - ((double)lateShipments / totalShipments));
    }
}