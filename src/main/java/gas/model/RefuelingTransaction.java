package gas.model;

import java.time.LocalDateTime;

/**
 * Model class representing a fuel transaction at the gas station.
 * Tracks transaction details including pump usage, fuel type, and payment.
 *
 * @author Hrishikesha Kyathsandra
 */
public class RefuelingTransaction {
    private String transactionId;
    private int pumpNumber;
    private FuelType fuelType;
    private double gallons;
    private double amount;
    private LocalDateTime timestamp;

    /**
     * Creates a new refueling transaction with the specified details.
     * Automatically calculates total amount based on fuel price.
     *
     * @param transactionId Unique identifier for the transaction
     * @param pumpNumber Number of the pump used
     * @param fuelType Type of fuel dispensed
     * @param gallons Quantity of fuel dispensed
     */
    public RefuelingTransaction(String transactionId, int pumpNumber, 
                              FuelType fuelType, double gallons) {
        this.transactionId = transactionId;
        this.pumpNumber = pumpNumber;
        this.fuelType = fuelType;
        this.gallons = gallons;
        this.amount = gallons * fuelType.getPricePerGallon();
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public int getPumpNumber() { return pumpNumber; }
    public FuelType getFuelType() { return fuelType; }
    public double getGallons() { return gallons; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
}