package greeter.model;

import java.util.Arrays;
import java.util.List;

public class GreeterReceipt {
    private String receiptId;
    private List<String> items;
    private double totalAmount;

    public GreeterReceipt(String receiptId, List<String> items, double totalAmount) {
        this.receiptId = receiptId;
        this.items = items;
        this.totalAmount = totalAmount;
    }

    public static GreeterReceipt fromLine(String line) {
        // Expected format: R001|Milk, Bread, Eggs|12.50
        String[] parts = line.split("\\|");
        if (parts.length < 3) return null;

        String id = parts[0].trim();
        String itemList = parts[1].trim();
        double amount = Double.parseDouble(parts[2].trim());
        List<String> items = Arrays.asList(itemList.split(","));
        // Trim extra spaces from each item
        items = items.stream().map(String::trim).toList();

        return new GreeterReceipt(id, items, amount);
    }

    public String getReceiptId() { return receiptId; }
    public List<String> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
}
