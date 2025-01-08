package deli.model;

import java.util.List;
import java.util.ArrayList;

public class DeliItem {
    private String itemId;
    private String name;
    private double pricePerUnit;
    private String unitType;
    private int stockLevel;
    private List<String> allergens;
    private boolean needsRestocking;
    private int reorderLevel;

    public DeliItem(String itemId, String name, double pricePerUnit, String unitType, int stockLevel, List<String> allergens) {
        this.itemId = itemId;
        this.name = name;
        this.pricePerUnit = pricePerUnit;
        this.unitType = unitType;
        this.stockLevel = stockLevel;
        this.allergens = allergens != null ? allergens : new ArrayList<>();
        this.reorderLevel = stockLevel / 4;
        this.needsRestocking = stockLevel <= reorderLevel;
    }

    public static DeliItem fromLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 6) return null;
        
        String id = parts[0].trim();
        String name = parts[1].trim();
        double price = Double.parseDouble(parts[2].trim());
        String utype = parts[3].trim();
        int stock = Integer.parseInt(parts[4].trim());
        List<String> allergens = new ArrayList<>();
        if (!parts[5].trim().equalsIgnoreCase("None")) {
            String[] allergensArray = parts[5].trim().split(",");
            for (String allergen : allergensArray) {
                allergens.add(allergen.trim());
            }
        }

        return new DeliItem(id, name, price, utype, stock, allergens);
    }

    // Getters
    public String getItemId() { return itemId; }
    public String getName() { return name; }
    public double getPricePerUnit() { return pricePerUnit; }
    public String getUnitType() { return unitType; }
    public int getStockLevel() { return stockLevel; }
    public List<String> getAllergens() { return allergens; }
    public boolean needsRestocking() { return stockLevel <= reorderLevel; }

    // Stock management methods
    public void reduceStock(int amount) {
        this.stockLevel = Math.max(0, this.stockLevel - amount);
        checkReorderLevel();
    }

    public void addStock(int amount) {
        this.stockLevel += amount;
        checkReorderLevel();
    }

    private void checkReorderLevel() {
        this.needsRestocking = stockLevel <= reorderLevel;
    }
}

