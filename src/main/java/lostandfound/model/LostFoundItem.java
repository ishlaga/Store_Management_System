package lostandfound.model;

public class LostFoundItem {
    private String id;
    private String type;
    private String description;
    private String status;
    private String reportDate;
    private String location;
    private String contactInfo;

    public LostFoundItem(String[] parts) {
        this.id = parts[0].trim();
        this.type = parts[1].trim();
        this.description = parts[2].trim();
        this.status = parts[3].trim();
        this.reportDate = parts[4].trim();
        this.location = parts[5].trim();
        this.contactInfo = parts[6].trim();
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getReportDate() { return reportDate; }
    public String getLocation() { return location; }
    public String getContactInfo() { return contactInfo; }
    
    public void setStatus(String status) { this.status = status; }
}