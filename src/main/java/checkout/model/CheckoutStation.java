/**
 * Represents a self-checkout station in the retail system.
 *
 * @author Chandrashekar Tirunagir
 */
package checkout.model;

import java.util.ArrayList;
import java.util.List;


public class CheckoutStation {
    private String id;
    private String status;
    private String cashLevel;
    private String transactionStatus;
    private String lastMaintenance;
    private boolean needsAssistance;
    private double currentWeight;
    private double expectedWeight;
    private double totalAmount;
    private List<TransactionItem> currentItems;

    public static class TransactionItem {
        private String name;
        private double price;
        private double weight;

        public TransactionItem(String name, double price, double weight) {
            this.name = name;
            this.price = price;
            this.weight = weight;
        }

        public String getName() { return name; }
        public double getPrice() { return price; }
        public double getWeight() { return weight; }
    }

    public CheckoutStation(String[] parts) {
        this.id = parts[0].trim();
        this.status = parts[1].trim();
        this.cashLevel = parts[2].trim();
        this.transactionStatus = parts[3].trim();
        this.lastMaintenance = parts[4].trim();
        this.needsAssistance = Boolean.parseBoolean(parts[5].trim());
        this.currentWeight = 0.0;
        this.expectedWeight = 0.0;
        this.totalAmount = 0.0;
        this.currentItems = new ArrayList<>();
    }
    // Getters
    public String getId() { return id; }
    public String getStatus() { return status; }
    public String getCashLevel() { return cashLevel; }
    public String getTransactionStatus() { return transactionStatus; }
    public String getLastMaintenance() { return lastMaintenance; }
    public boolean getNeedsAssistance() { return needsAssistance; }
    public double getTotalAmount() { return totalAmount; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setCashLevel(String level) { this.cashLevel = level; }
    public void setTransactionStatus(String status) { this.transactionStatus = status; }
    public void setNeedsAssistance(boolean needs) { this.needsAssistance = needs; }

    public void addItem(String name, double price, double weight) {
        currentItems.add(new TransactionItem(name, price, weight));
        expectedWeight += weight;
        totalAmount += price;
    }

    public boolean verifyWeight(double weight) {
        currentWeight = weight;
        return Math.abs(currentWeight - expectedWeight) < 0.1; // Tolerance of 0.1 kg
    }

    public void resetTransaction() {
        currentWeight = 0.0;
        expectedWeight = 0.0;
        totalAmount = 0.0;
        currentItems.clear();
        setTransactionStatus("Idle");
        setNeedsAssistance(false);
    }

    public List<TransactionItem> getCurrentItems() {
        return new ArrayList<>(currentItems);
    }

    // Add these helper methods
    public boolean isActive() {
        return "Active".equals(status);
    }

    public boolean hasActiveTransaction() {
        return "InProgress".equals(transactionStatus) || 
               "AgeVerification".equals(transactionStatus) ||
               "WeightMismatch".equals(transactionStatus);
    }
}
