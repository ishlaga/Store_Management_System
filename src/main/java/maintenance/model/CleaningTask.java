/**
 * Represents a cleaning task in the system.
 * Manages scheduled cleaning activities and their completion status.
 *
 * @author Chandrashekar Tirunagiri
 */
package maintenance.model;

import java.time.LocalDateTime;

public class CleaningTask {
    private String taskId; // Unique identifier for the task
    private String description; // Description of the cleaning task
    private String area; // Area where the task is to be performed
    private String assignedTo; // Staff assigned to the task
    private TaskStatus status; // Current status of the task
    private TaskFrequency frequency; // Frequency of the task (DAILY, WEEKLY, MONTHLY)
    private LocalDateTime scheduledTime; // Scheduled time for the task
    private LocalDateTime completedTime; // Time when the task was completed
    private String notes; // Additional notes about the task

    public CleaningTask(String taskId, String description, String area, 
                       TaskFrequency frequency, LocalDateTime scheduledTime) {
        this.taskId = taskId;
        this.description = description;
        this.area = area;
        this.frequency = frequency;
        this.scheduledTime = scheduledTime;
        this.status = TaskStatus.PENDING; // Set initial status to PENDING
    }

    // Getters and setters
    public String getTaskId() { return taskId; }
    public String getDescription() { return description; }
    public String getArea() { return area; }
    public String getAssignedTo() { return assignedTo; }
    public TaskStatus getStatus() { return status; }
    public TaskFrequency getFrequency() { return frequency; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public LocalDateTime getCompletedTime() { return completedTime; }
    public String getNotes() { return notes; }

    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public void setCompletedTime(LocalDateTime completedTime) { this.completedTime = completedTime; }
    public void setNotes(String notes) { this.notes = notes; }
}