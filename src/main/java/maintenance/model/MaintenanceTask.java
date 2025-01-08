package maintenance.model;

import java.time.LocalDateTime;

public class MaintenanceTask {
    private String taskId;
    private String description;
    private String area;
    private String assignedTo;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime reportedTime;
    private LocalDateTime completedTime;
    private String notes;
    private boolean requiresHeadOfficeApproval;

    public MaintenanceTask(String taskId, String description, String area, 
                          TaskPriority priority, LocalDateTime reportedTime) {
        this.taskId = taskId;
        this.description = description;
        this.area = area;
        this.priority = priority;
        this.reportedTime = reportedTime;
        this.status = TaskStatus.PENDING;
        this.requiresHeadOfficeApproval = false;
    }

    // Getters and setters
    public String getTaskId() { return taskId; }
    public String getDescription() { return description; }
    public String getArea() { return area; }
    public String getAssignedTo() { return assignedTo; }
    public TaskStatus getStatus() { return status; }
    public TaskPriority getPriority() { return priority; }
    public LocalDateTime getReportedTime() { return reportedTime; }
    public LocalDateTime getCompletedTime() { return completedTime; }
    public String getNotes() { return notes; }
    public boolean requiresHeadOfficeApproval() { return requiresHeadOfficeApproval; }

    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public void setCompletedTime(LocalDateTime completedTime) { this.completedTime = completedTime; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setRequiresHeadOfficeApproval(boolean requires) { this.requiresHeadOfficeApproval = requires; }
}