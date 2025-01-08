package employee.model;

import java.time.LocalDateTime;

/**
 * Model class representing a single time record entry for an employee,
 * including clock-in/out times and break information.
 * @author Akhilesh Nevatia
 */
public class TimeRecord {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isBreak;
    private boolean isOvertime;
    private boolean isApproved;
    private int breakDuration; // in minutes

    public TimeRecord(LocalDateTime startTime) {
        this.startTime = startTime;
        this.isBreak = false;
        this.isOvertime = false;
        this.isApproved = false;
        this.breakDuration = 0;
    }

    // Getters
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public boolean isBreak() { return isBreak; }
    public boolean isOvertime() { return isOvertime; }
    public boolean isApproved() { return isApproved; }
    public int getBreakDuration() { return breakDuration; }
    
    // Setters
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setBreak(boolean isBreak) { this.isBreak = isBreak; }
    public void setOvertime(boolean isOvertime) { this.isOvertime = isOvertime; }
    public void setApproved(boolean isApproved) { this.isApproved = isApproved; }
    public void setBreakDuration(int breakDuration) { this.breakDuration = breakDuration; }

    public double getHoursWorked() {
        if (endTime == null) return 0;
        long minutes = java.time.Duration.between(startTime, endTime).toMinutes() - breakDuration;
        return minutes / 60.0;
    }
}