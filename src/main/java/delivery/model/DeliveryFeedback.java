package delivery.model;

public class DeliveryFeedback {
    private String orderId;
    private String customerName;
    private int rating;
    private String comments;

    public DeliveryFeedback(String orderId, String customerName, int rating, String comments) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be empty");
        }
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        this.orderId = orderId.trim();
        this.customerName = customerName.trim();
        this.rating = rating;
        this.comments = comments != null ? comments.trim() : "";
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public int getRating() { return rating; }
    public String getComments() { return comments; }
}