package pharmacy.model;

public class MedicineSupplier {
    private String supplierId;
    private String name;
    private String contact;
    private boolean isAvailable;
    private String licenseNumber;

    public MedicineSupplier(String supplierId, String name, String contact, String licenseNumber) {
        this.supplierId = supplierId;
        this.name = name;
        this.contact = contact;
        this.licenseNumber = licenseNumber;
        this.isAvailable = true;
    }

    // Getters
    public String getSupplierId() { return supplierId; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getLicenseNumber() { return licenseNumber; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
} 