package eyetest.model;

public class EyeTestAppointment {
    private String id;
    private String patientName;
    private String appointmentDate;
    private String timeSlot;
    private String optometrist;
    private String status;
    private String contactNumber;
    private String testType;
    private String notes;
    private boolean isPrescriptionIssued;

    public EyeTestAppointment(String[] parts) {
        this.id = parts[0].trim();
        this.patientName = parts[1].trim();
        this.appointmentDate = parts[2].trim();
        this.timeSlot = parts[3].trim();
        this.optometrist = parts[4].trim();
        this.status = parts[5].trim();
        this.contactNumber = parts[6].trim();
        this.testType = parts[7].trim();
        this.notes = parts[8].trim();
        this.isPrescriptionIssued = Boolean.parseBoolean(parts[9].trim());
    }

    // Getters
    public String getId() { return id; }
    public String getPatientName() { return patientName; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getTimeSlot() { return timeSlot; }
    public String getOptometrist() { return optometrist; }
    public String getStatus() { return status; }
    public String getContactNumber() { return contactNumber; }
    public String getTestType() { return testType; }
    public String getNotes() { return notes; }
    public boolean isPrescriptionIssued() { return isPrescriptionIssued; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setPrescriptionIssued(boolean issued) { this.isPrescriptionIssued = issued; }
}