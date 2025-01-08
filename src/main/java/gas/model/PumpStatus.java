package gas.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Model class representing the status and maintenance state of a fuel pump.
 * Tracks operational status, usage, and maintenance requirements.
 *
 * @author Hrishikesha Kyathsandra
 */
public class PumpStatus {
    private int pumpNumber;
    private boolean operational;
    private boolean inUse;
    private LocalDateTime lastMaintenance;
    private int usageCount;
    private static final int MAINTENANCE_THRESHOLD = 100; // Maintenance needed after 100 uses
    private static final int MAX_DAYS_BETWEEN_MAINTENANCE = 30;

    public PumpStatus(int pumpNumber) {
        this.pumpNumber = pumpNumber;
        this.operational = true;
        this.inUse = false;
        this.lastMaintenance = LocalDateTime.now();
    }

    public boolean isOperational() { return operational; }
    public boolean isInUse() { return inUse; }
    public void setInUse(boolean inUse) { this.inUse = inUse; }
    public int getPumpNumber() { return pumpNumber; }
    public LocalDateTime getLastMaintenance() { return lastMaintenance; }
    public void setOperational(boolean operational) { this.operational = operational; }

    public void incrementUsage() {
        usageCount++;
        if (usageCount >= MAINTENANCE_THRESHOLD) {
            operational = false;
        }
    }

    public boolean needsMaintenance() {
        long daysSinceLastMaintenance = ChronoUnit.DAYS.between(lastMaintenance, LocalDateTime.now());
        return daysSinceLastMaintenance >= MAX_DAYS_BETWEEN_MAINTENANCE || usageCount >= MAINTENANCE_THRESHOLD;
    }

    public void performMaintenance() {
        lastMaintenance = LocalDateTime.now();
        usageCount = 0;
        operational = true;
    }
}