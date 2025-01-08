package gas.model;

public class FuelSupplier {
    private String supplierId;
    private String name;
    private String contact;
    private boolean isAvailable;

    public FuelSupplier(String supplierId, String name, String contact) {
        this.supplierId = supplierId;
        this.name = name;
        this.contact = contact;
        this.isAvailable = true;
    }

    // Getters
    public String getSupplierId() { return supplierId; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
} 