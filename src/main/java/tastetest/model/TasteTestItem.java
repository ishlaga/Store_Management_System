package tastetest.model;

public class TasteTestItem {
    private String id;
    private String productName;
    private String category;
    private String status;
    private String testDate;
    private String location;
    private String staffMember;
    private int participantCount;
    private double averageRating;
    private int[] ratingDistribution;

    public TasteTestItem(String[] parts) {
        this.id = parts[0].trim();
        this.productName = parts[1].trim();
        this.category = parts[2].trim();
        this.status = parts[3].trim();
        this.testDate = parts[4].trim();
        this.location = parts[5].trim();
        this.staffMember = parts[6].trim();
        this.participantCount = Integer.parseInt(parts[7].trim());
        this.averageRating = Double.parseDouble(parts[8].trim());
        this.ratingDistribution = new int[5];
        if (parts.length >= 14) {
            for (int i = 0; i < 5; i++) {
                this.ratingDistribution[i] = Integer.parseInt(parts[9 + i].trim());
            }
        }
    }

    // Getters
    public String getId() { return id; }
    public String getProductName() { return productName; }
    public String getCategory() { return category; }
    public String getStatus() { return status; }
    public String getTestDate() { return testDate; }
    public String getLocation() { return location; }
    public String getStaffMember() { return staffMember; }
    public int getParticipantCount() { return participantCount; }
    public double getAverageRating() { return averageRating; }
    public int[] getRatingDistribution() { return ratingDistribution; }

    // Setters
    public void setStatus(String status) { this.status = status; }

    public void updateRating(double rating) {
        int ratingIndex = (int)rating - 1;
        ratingDistribution[ratingIndex]++;
        this.participantCount++;
        
        // Recalculate average
        double sum = 0;
        for (int i = 0; i < 5; i++) {
            sum += (i + 1) * ratingDistribution[i];
        }
        this.averageRating = sum / participantCount;
    }

    public String getRatingStats() {
        StringBuilder stats = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            stats.append(String.format("%d stars: %d responses (%d%%)%n", 
                i + 1, 
                ratingDistribution[i],
                participantCount > 0 ? (ratingDistribution[i] * 100 / participantCount) : 0));
        }
        return stats.toString();
    }
}