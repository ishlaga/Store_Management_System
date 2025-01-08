/**
 * Model class representing a medication in the pharmacy inventory.
 * Tracks medication details, stock levels, and pricing information.
 *
 * @author Hrishikesha Kyathsandra
 */
package pharmacy.model;

import java.time.LocalDateTime;

public class Medication {
    private String medicationId;
    private String name;
    private String type; // OTC or PRESCRIPTION
    private double price;
    private int stockLevel;
    private LocalDateTime expirationDate;
    private String manufacturer;
    private String warnings;
    private int minimumStockLevel;

    public Medication(String medicationId, String name, String type, double price, 
                     int stockLevel, LocalDateTime expirationDate, String manufacturer, 
                     String warnings, int minimumStockLevel) {
        this.medicationId = medicationId;
        this.name = name;
        this.type = type;
        this.price = price;
        this.stockLevel = stockLevel;
        this.expirationDate = expirationDate;
        this.manufacturer = manufacturer;
        this.warnings = warnings;
        this.minimumStockLevel = minimumStockLevel;
    }

    // Getters and setters
    public String getMedicationId() { return medicationId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public int getStockLevel() { return stockLevel; }
    public void setStockLevel(int stockLevel) { this.stockLevel = stockLevel; }
    public LocalDateTime getExpirationDate() { return expirationDate; }
    public String getManufacturer() { return manufacturer; }
    public String getWarnings() { return warnings; }
    public int getMinimumStockLevel() { return minimumStockLevel; }
}