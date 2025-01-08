package employee.model;

import java.time.LocalDate;

public class Leave {
    private String employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveType type;
    private LeaveStatus status;
    private String reason;

    public Leave(String employeeId, LocalDate startDate, LocalDate endDate, LeaveType type, String reason) {
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.status = LeaveStatus.PENDING;
        this.reason = reason;
    }

    // Getters
    public String getEmployeeId() { return employeeId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public LeaveType getType() { return type; }
    public LeaveStatus getStatus() { return status; }
    public String getReason() { return reason; }

    // Setters
    public void setStatus(LeaveStatus status) { this.status = status; }
}

