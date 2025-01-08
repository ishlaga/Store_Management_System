package delivery.model;

import java.time.LocalDateTime;

public class DeliveryOrder {
    private String id;
    private String customerName;
    private String address;
    private String phone;
    private LocalDateTime scheduledTime;
    private String staffId;
    private String status;

    public DeliveryOrder(String[] parts) {
        if (parts.length >= 7) {
            this.id = parts[0].trim();
            this.customerName = parts[1].trim();
            this.address = parts[2].trim();
            this.phone = parts[3].trim();
            this.scheduledTime = LocalDateTime.parse(parts[4].trim().replace(" ", "T"));
            this.staffId = parts[5].trim();
            this.status = parts[6].trim();
        }
    }

    public String getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public String getStaffId() { return staffId; }
    public String getStatus() { return status; }
    
    public void setStatus(String status) { this.status = status; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
}