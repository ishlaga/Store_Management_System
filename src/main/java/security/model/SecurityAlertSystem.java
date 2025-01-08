/**
 * Manages the security alert system for the retail environment.
 * This class handles loading and tracking security incidents and alerts.
 *
 * @author Raghuram Guddati
 */
package security.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityAlertSystem {
    /** Formatter for standardizing date/time format */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /** File path for security incidents */
    private static final String INCIDENTS_FILE = "./src/main/java/security/data/security_incidents.txt";
    /** File path for security alerts */
    private static final String ALERTS_FILE = "./src/main/java/security/data/security_alerts.txt";
    
    /** Thread-safe list to store incidents */
    private final List<SecurityIncident> incidents;
    /** Thread-safe list to store alerts */
    private final List<SecurityIncident> alerts;

    /**
     * Constructor initializes the system and loads existing data.
     *
     * @author Raghuram Guddati
     */
    public SecurityAlertSystem() {
        this.incidents = Collections.synchronizedList(new ArrayList<>());
        this.alerts = Collections.synchronizedList(new ArrayList<>());
        loadData();
    }

    /**
     * Loads both incidents and alerts data.
     *
     * @author Raghuram Guddati
     */
    private void loadData() {
        loadIncidents();
        loadAlerts();
    }

    /**
     * Loads incidents from the incidents file.
     *
     * @author Raghuram Guddati
     */
    private void loadIncidents() {
        try (BufferedReader reader = new BufferedReader(new FileReader(INCIDENTS_FILE))) {
            reader.readLine(); // Skip header
            reader.readLine(); // Skip separator
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.startsWith("-")) {
                    try {
                        String[] parts = line.split("\\|");
                        if (parts.length >= 5) {
                            SecurityIncident incident = createIncidentFromParts(parts);
                            incidents.add(incident);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing incident line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading incidents file: " + e.getMessage());
        }
    }

    /**
     * Loads alerts from the alerts file.
     *
     * @author Raghuram Guddati
     */
    private void loadAlerts() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ALERTS_FILE))) {
            reader.readLine(); // Skip header
            reader.readLine(); // Skip separator
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.startsWith("-")) {
                    try {
                        String[] parts = line.split("\\|");
                        if (parts.length >= 4) {
                            SecurityIncident alert = createAlertFromParts(parts);
                            alerts.add(alert);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing alert line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading alerts file: " + e.getMessage());
        }
    }

    /**
     * Creates a SecurityIncident object from parsed file data.
     *
     * @param parts Array of strings containing incident data
     * @return New SecurityIncident object
     * @author Raghuram Guddati
     */
    private SecurityIncident createIncidentFromParts(String[] parts) {
        String id = parts[0].trim();
        String description = parts[1].trim();
        String severity = parts[2].trim();
        String timestamp = parts[3].trim();
        boolean isResolved = parts[4].trim().equals("Resolved");
        String status = isResolved ? "Resolved" : "Not Resolved";
        
        return new SecurityIncident(id, description, severity, timestamp, isResolved, status);
    }

    /**
     * Creates a SecurityIncident object from parsed alert data.
     *
     * @param parts Array of strings containing alert data
     * @return New SecurityIncident object
     * @author Raghuram Guddati
     */
    private SecurityIncident createAlertFromParts(String[] parts) {
        String id = parts[0].trim();
        String description = parts[1].trim();
        String severity = parts[2].trim();
        String timestamp = parts[3].trim();
        
        return new SecurityIncident(id, description, severity, timestamp, false, "Not Resolved");
    }

    /**
     * Gets all incidents in the system.
     *
     * @return Unmodifiable list of all incidents
     * @author Raghuram Guddati
     */
    public List<SecurityIncident> getIncidents() {
        return Collections.unmodifiableList(incidents);
    }

    /**
     * Gets all alerts in the system.
     *
     * @return Unmodifiable list of all alerts
     * @author Raghuram Guddati
     */
    public List<SecurityIncident> getAlerts() {
        return Collections.unmodifiableList(alerts);
    }

    /**
     * Gets incidents within specified time range.
     *
     * @param start Start time for range
     * @param end End time for range
     * @return List of incidents within range
     * @throws IllegalArgumentException if start or end is null or invalid
     * @author Raghuram Guddati
     */
    public List<SecurityIncident> getIncidentsInRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null");
        }
        
        return incidents.stream()
            .filter(incident -> {
                LocalDateTime timestamp = incident.getTimestamp();
                return !timestamp.isBefore(start) && !timestamp.isAfter(end);
            })
            .collect(Collectors.toList());
    }

    /**
     * Reports a new security issue.
     *
     * @param description Description of the issue
     * @param severity Severity level of the issue
     * @author Raghuram Guddati
     */
    public void reportSecurityIssue(String description, String severity) {
        String id = String.valueOf(getNextId());
        LocalDateTime now = LocalDateTime.now();
        
        SecurityIncident newIncident = new SecurityIncident(
            id,
            description,
            severity,
            now.format(FORMATTER),
            false,
            "Not Resolved"
        );
        
        synchronized(incidents) {
            incidents.add(newIncident);
        }
    }

    /**
     * Gets the next available ID for new incidents.
     *
     * @return Next available ID
     * @author Raghuram Guddati
     */
    private int getNextId() {
        return Math.max(
            incidents.stream().mapToInt(i -> Integer.parseInt(i.getId().trim())).max().orElse(0),
            alerts.stream().mapToInt(i -> Integer.parseInt(i.getId().trim())).max().orElse(0)
        ) + 1;
    }

    /**
     * Gets all reported issues.
     *
     * @return List of all reported incidents
     * @author Raghuram Guddati
     */
    public List<SecurityIncident> getReportedIssues() {
        return Collections.unmodifiableList(incidents);
    }

    /**
     * Resolves a specific incident.
     *
     * @param id ID of incident to resolve
     * @return boolean indicating if incident was found and resolved
     * @author Raghuram Guddati
     */
    public boolean resolveIncident(String id) {
        return incidents.stream()
            .filter(incident -> incident.getId().equals(id))
            .findFirst()
            .map(incident -> {
                incident.setResolved(true);
                incident.setStatus("Resolved");
                return true;
            })
            .orElse(false);
    }

    /**
     * Updates the status of a specific incident.
     *
     * @param id Incident ID to update
     * @param newStatus New status to set
     * @author Raghuram Guddati
     */
    public void updateIncidentStatus(String id, String newStatus) {
        incidents.stream()
            .filter(incident -> incident.getId().equals(id))
            .findFirst()
            .ifPresent(incident -> {
                incident.setStatus(newStatus);
                if (newStatus.equals("Resolved")) {
                    incident.setResolved(true);
                }
            });
    }
}