/**
 * Model class representing a medical prescription.
 * Contains prescription details, patient information, and medication instructions.
 *
 * @author Hrishikesha Kyathsandra
 */
package pharmacy.model;

import java.time.LocalDateTime;

public class Prescription {
    private String prescriptionId;
    private String customerId;
    private String medicationName;
    private String dosage;
    private int quantity;
    private String instructions;
    private String doctorName;
    private LocalDateTime expirationDate;
    private boolean isApproved;
    private String insuranceProvider;
    private double price;

    public Prescription(String prescriptionId, String customerId, String medicationName, 
                       String dosage, int quantity, String instructions, String doctorName, 
                       LocalDateTime expirationDate, String insuranceProvider, double price) {
        this.prescriptionId = prescriptionId;
        this.customerId = customerId;
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.quantity = quantity;
        this.instructions = instructions;
        this.doctorName = doctorName;
        this.expirationDate = expirationDate;
        this.isApproved = false;
        this.insuranceProvider = insuranceProvider;
        this.price = price;
    }

    // Getters and setters
    public String getPrescriptionId() { return prescriptionId; }
    public String getCustomerId() { return customerId; }
    public String getMedicationName() { return medicationName; }
    public String getDosage() { return dosage; }
    public int getQuantity() { return quantity; }
    public String getInstructions() { return instructions; }
    public String getDoctorName() { return doctorName; }
    public LocalDateTime getExpirationDate() { return expirationDate; }
    public boolean isApproved() { return isApproved; }
    public void setApproved(boolean approved) { isApproved = approved; }
    public String getInsuranceProvider() { return insuranceProvider; }
    public double getPrice() { return price; }
}