/**
 * Represents a security incident in the retail system.
 * This class manages security-related incidents including their severity,
 * resolution status, and timing information.
 *
 * @author Raghuram Guddati
 */
package security.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SecurityIncident {
    /** Formatter for standardizing timestamp format */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /** Unique identifier for the incident */
    private String id;
    /** Description of the security incident */
    private String description;
    /** Severity level of the incident */
    private String severity;
    /** When the incident occurred */
    private LocalDateTime timestamp;
    /** Whether the incident has been resolved */
    private boolean isResolved;
    /** Current status of the incident */
    private String status;

    /**
     * Constructor for creating a security incident.
     *
     * @param id Unique identifier for the incident
     * @param description Detailed description of the incident
     * @param severity Level of incident severity (Low, Medium, High, Severe)
     * @param timestamp When the incident occurred
     * @param isResolved Resolution status of the incident
     * @param status Current handling status
     * @author Raghuram Guddati
     */
    public SecurityIncident(String id, String description, String severity, String timestamp, boolean isResolved, String status) {
        this.id = id.trim();
        this.description = description.trim();
        this.severity = severity.trim();
        this.timestamp = parseTimestamp(timestamp.trim());
        this.isResolved = isResolved;
        this.status = status.trim();
        validateIncident();
    }

    /**
     * Parses the timestamp string into a LocalDateTime object.
     *
     * @param timestamp String representation of the timestamp
     * @return LocalDateTime object representing the parsed timestamp
     * @throws IllegalArgumentException if timestamp format is invalid
     * @author Raghuram Guddati
     */
    private LocalDateTime parseTimestamp(String timestamp) {
        try {
            return LocalDateTime.parse(timestamp, FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid timestamp format. Expected: yyyy-MM-dd HH:mm:ss");
        }
    }

    /**
     * Validates all fields of the incident to ensure data integrity.
     *
     * @throws IllegalArgumentException if any field is invalid
     * @author Raghuram Guddati
     */
    private void validateIncident() {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (!isValidSeverity(severity)) {
            throw new IllegalArgumentException("Invalid severity level: " + severity);
        }
        if (status == null || status.isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
    }

    /**
     * Checks if the provided severity level is valid.
     *
     * @param severity Severity level to validate
     * @return boolean indicating if severity is valid
     * @author Raghuram Guddati
     */
    private boolean isValidSeverity(String severity) {
        return severity != null && List.of("Low", "Medium", "High", "Severe").contains(severity);
    }

    // Getter methods with documentation
    /**
     * Gets the incident ID.
     * @return String incident ID
     * @author Raghuram Guddati
     */
    public String getId() { return id; }

    /**
     * Gets the incident description.
     * @return String incident description
     * @author Raghuram Guddati
     */
    public String getDescription() { return description; }

    /**
     * Gets the incident severity level.
     * @return String severity level
     * @author Raghuram Guddati
     */
    public String getSeverity() { return severity; }

    /**
     * Gets the incident timestamp.
     * @return LocalDateTime timestamp
     * @author Raghuram Guddati
     */
    public LocalDateTime getTimestamp() { return timestamp; }

    /**
     * Gets the incident resolution status.
     * @return boolean indicating if incident is resolved
     * @author Raghuram Guddati
     */
    public boolean isResolved() { return isResolved; }

    /**
     * Gets the current incident status.
     * @return String current status
     * @author Raghuram Guddati
     */
    public String getStatus() { return status; }

    /**
     * Updates the status of the incident.
     *
     * @param status New status to set
     * @author Raghuram Guddati
     */
    public void setStatus(String status) { 
        if (status != null && !status.trim().isEmpty()) {
            this.status = status.trim();
        }
    }

    /**
     * Updates the resolution status of the incident.
     *
     * @param resolved New resolution status
     * @author Raghuram Guddati
     */
    public void setResolved(boolean resolved) {
        this.isResolved = resolved;
    }

    /**
     * Generates a formatted string representation of the incident.
     *
     * @return Formatted string containing incident details
     * @author Raghuram Guddati
     */
    @Override
    public String toString() {
        return String.format("%-4s| %-35s| %-9s| %-20s| %-12s",
            id, description, severity, timestamp.format(FORMATTER), status);
    }
}