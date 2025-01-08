/**
 * Represents a donation program in the retail system.
 *
 * @author Raghuram Guddati
 */
package donation.model;

public class DonationProgram {
    private String id;
    private String charityName;
    private String goal;
    private String timeline;
    private String status;
    private String items;
    private double value;

    public DonationProgram(String[] parts) {
        this.id = parts[0].trim();
        this.charityName = parts[1].trim();
        this.goal = parts[2].trim();
        this.timeline = parts[3].trim();
        this.status = parts[4].trim();
        this.items = parts[5].trim();
        this.value = Double.parseDouble(parts[6].trim());
    }

    // Getters
    public String getId() { return id; }
    public String getCharityName() { return charityName; }
    public String getGoal() { return goal; }
    public String getTimeline() { return timeline; }
    public String getStatus() { return status; }
    public String getItems() { return items; }
    public double getValue() { return value; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setValue(double value) { this.value = value; }
}