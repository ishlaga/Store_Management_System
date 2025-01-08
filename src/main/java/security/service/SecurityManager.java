/**
 * Manages security operations and incident monitoring in the retail system.
 * This class provides an interface for viewing and managing security incidents.
 * 
 * @author Raghuram Guddati
 */
package security.service;

import java.util.Scanner;
import security.model.SecurityAlertSystem;
import security.model.SecurityIncident;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SecurityManager {
    /** Security alert system instance */
    private SecurityAlertSystem alertSystem;
    /** Scanner for user input */
    private Scanner scanner;
    /** Formatter for date/time input parsing */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /** Format string for header display */
    private static final String HEADER_FORMAT = "%-4s| %-35s| %-9s| %-20s| %-12s%n";
    /** Format string for data display */
    private static final String DATA_FORMAT = "%-4s| %-35s| %-9s| %-20s| %-12s%n";
    /** Separator line for display formatting */
    private static final String SEPARATOR = "-".repeat(80);

    /**
     * Constructor initializes the security management system.
     * Creates new instances of SecurityAlertSystem and Scanner.
     * 
     * @author Raghuram Guddati
     */
    public SecurityManager() {
        this.alertSystem = new SecurityAlertSystem();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts the security monitoring interface.
     * Provides a menu-driven interface for security management operations.
     * 
     * @author Raghuram Guddati
     */
    public void startMonitoring() {
        while (true) {
            displayMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    viewIncidents();
                    break;
                case 2:
                    reportSecurityIssue();
                    break;
                case 3:
                    viewReportedIssues();
                    break;
                case 4:
                    updateIncidentStatus();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Displays the menu options for the security management system.
     * Shows available operations that can be performed.
     * 
     * @author Raghuram Guddati
     */
    private void displayMenu() {
        System.out.println("\nSecurity Management System");
        System.out.println("1. View Incidents (Security Manager)");
        System.out.println("2. Report Security Issue (Security Manager)");
        System.out.println("3. View Reported Issues (Security Manager)");
        System.out.println("4. Update Incident Status (Security Manager)");
        System.out.println("5. Return to Main Menu");
    }

    /**
     * Gets the user's choice from the menu.
     * Validates input to ensure it's a valid integer.
     * 
     * @return the user's choice as an integer
     * @author Raghuram Guddati
     */
    private int getUserChoice() {
        System.out.print("Choose an option: ");
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            scanner.next();
        }
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        return choice;
    }

    /**
     * Displays incidents within a specified date range.
     * Prompts user for start and end dates and shows matching incidents.
     * 
     * @author Raghuram Guddati
     */
    private void viewIncidents() {
        try {
            System.out.print("Enter start date (yyyy-MM-dd HH:mm:ss): ");
            String startDateStr = scanner.nextLine();
            System.out.print("Enter end date (yyyy-MM-dd HH:mm:ss): ");
            String endDateStr = scanner.nextLine();

            LocalDateTime startDate = LocalDateTime.parse(startDateStr, FORMATTER);
            LocalDateTime endDate = LocalDateTime.parse(endDateStr, FORMATTER);

            List<SecurityIncident> filteredIncidents = alertSystem.getIncidentsInRange(startDate, endDate);
            displayIncidents(filteredIncidents);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use: yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Displays the list of incidents in a formatted table.
     * 
     * @param incidents the list of incidents to display
     * @author Raghuram Guddati
     */
    private void displayIncidents(List<SecurityIncident> incidents) {
        if (incidents.isEmpty()) {
            System.out.println("No incidents found in the specified time range.");
            return;
        }

        // Print header
        System.out.println(SEPARATOR);
        System.out.printf(HEADER_FORMAT, "ID", "Description", "Severity", "Timestamp", "Status");
        System.out.println(SEPARATOR);

        // Print incidents
        for (SecurityIncident incident : incidents) {
            System.out.printf(DATA_FORMAT,
                incident.getId(),
                incident.getDescription(),
                incident.getSeverity(),
                incident.getTimestamp().format(FORMATTER),
                incident.isResolved() ? "Resolved" : "Not Resolved"
            );
        }
        System.out.println(SEPARATOR);
    }

    /**
     * Reports a new security issue based on user input.
     * Prompts for description and severity level.
     * 
     * @author Raghuram Guddati
     */
    private void reportSecurityIssue() {
        try {
            System.out.print("Enter description of the security issue: ");
            String description = scanner.nextLine();

            System.out.println("Select severity level:");
            System.out.println("1. Low");
            System.out.println("2. Medium");
            System.out.println("3. High");
            System.out.println("4. Severe");
            System.out.print("Enter choice (1-4): ");
            
            int severityChoice = Integer.parseInt(scanner.nextLine());
            String severity;
            switch (severityChoice) {
                case 1:
                    severity = "Low";
                    break;
                case 2:
                    severity = "Medium";
                    break;
                case 3:
                    severity = "High";
                    break;
                case 4:
                    severity = "Severe";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid severity level");
            }

            alertSystem.reportSecurityIssue(description, severity);
            System.out.println("Security issue reported successfully.");
        } catch (Exception e) {
            System.out.println("Error reporting security issue: " + e.getMessage());
        }
    }

    /**
     * Views all reported security issues.
     * Displays a list of all incidents in the system.
     * 
     * @author Raghuram Guddati
     */
    private void viewReportedIssues() {
        List<SecurityIncident> reportedIssues = alertSystem.getReportedIssues();
        displayIncidents(reportedIssues);
    }

    /**
     * Updates the status of a specific incident.
     * Prompts for incident ID and new status.
     * 
     * @author Raghuram Guddati
     */
    private void updateIncidentStatus() {
        try {
            System.out.print("Enter the ID of the incident to update: ");
            String id = scanner.nextLine();

            System.out.println("Select new status:");
            System.out.println("1. Resolved");
            System.out.println("2. Not Resolved");
           // System.out.println("3. Under Investigation");
            System.out.print("Enter choice (1-2): ");
            
            int statusChoice = Integer.parseInt(scanner.nextLine());
            String newStatus;
            switch (statusChoice) {
                case 1:
                    newStatus = "Resolved";
                    break;
                case 2:
                    newStatus = "Not Resolved";
                    break;
                case 3:
                    newStatus = "Under Investigation";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid status choice");
            }

            boolean updated = alertSystem.resolveIncident(id);
            if (updated) {
                System.out.println("Incident status updated successfully.");
                alertSystem.updateIncidentStatus(id, newStatus);
            } else {
                System.out.println("Incident with ID " + id + " not found.");
            }
        } catch (Exception e) {
            System.out.println("Error updating incident status: " + e.getMessage());
        }
    }
}
