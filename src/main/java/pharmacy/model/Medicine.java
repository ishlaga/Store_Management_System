package pharmacy.model;

public class Medicine {
    private String medicineId;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String manufacturer;
    private String category;
    private boolean prescriptionRequired;

    public Medicine(String medicineId, String name, String description, double price, 
                   int quantity, String manufacturer, String category, boolean prescriptionRequired) {
        this.medicineId = medicineId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.manufacturer = manufacturer;
        this.category = category;
        this.prescriptionRequired = prescriptionRequired;
    }

    // Getters and Setters
    public String getMedicineId() { return medicineId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getManufacturer() { return manufacturer; }
    public String getCategory() { return category; }
    public boolean isPrescriptionRequired() { return prescriptionRequired; }
} 