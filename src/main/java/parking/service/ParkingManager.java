/**
 * Manages parking lot operations and monitoring.
 *
 * @author Chandrashekar Tirunagiri
 */
package parking.service;

import parking.model.ParkingStatus;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ParkingManager {
    private List<ParkingStatus> parkingSections;
    private List<ParkingStatus> cartStations;
    private List<ParkingIssue> activeIssues;
    private Scanner scanner;
    private static final String PARKING_FILE = "./src/main/java/parking/data/parking_spaces.txt";
    private static final String CART_FILE = "./src/main/java/parking/data/cart_stations.txt";
    private String currentWeather = "Normal";
    private boolean securitySystemActive = true;

    // Inner class to track issues
    private class ParkingIssue {
        private String id;
        private String sectionId;
        private String type;
        private String description;
        private String status;
        private LocalDateTime reportedTime;

        public ParkingIssue(String sectionId, String type, String description) {
            this.id = "ISSUE-" + System.currentTimeMillis();
            this.sectionId = sectionId;
            this.type = type;
            this.description = description;
            this.status = "Open";
            this.reportedTime = LocalDateTime.now();
        }
    }

    public ParkingManager() {
        this.parkingSections = new ArrayList<>();
        this.cartStations = new ArrayList<>();
        this.activeIssues = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        loadData();
    }

    // Data loading methods
    private void loadData() {
        loadParkingData();
        loadCartData();
    }


    private void updateCartStationStatus(ParkingStatus station) {
        double fillRate = (double) station.getCurrentCarts() / station.getCapacity() * 100;
        
        // Set collection thresholds based on weather
        double collectionThreshold = switch (currentWeather) {
            case "Rain" -> 70.0;    // Collect earlier in rain
            case "Snow" -> 60.0;    // Even earlier in snow
            case "Severe" -> 40.0;  // Very early in severe weather
            default -> 80.0;        // Normal weather threshold
        };
    
        // Update cart collection frequency
        int collectionFrequency = switch (currentWeather) {
            case "Rain" -> 30;    // Every 30 minutes
            case "Snow" -> 45;    // Every 45 minutes
            case "Severe" -> 20;  // Every 20 minutes if collection allowed
            default -> 120;       // Every 2 hours in normal weather
        };
    
        if (fillRate >= collectionThreshold) {
            station.setStatus("High");
            if (currentWeather.equals("Severe")) {
                System.out.println(" URGENT: Cart collection needed before suspending operations");
            }
        } else if (fillRate <= 20.0) {
            station.setStatus("Low");
            System.out.println(" Alert: Cart station running low at " + station.getLocation());
        } else {
            station.setStatus("Normal");
        }
    
        // Display appropriate messages
        if (currentWeather.equals("Severe")) {
            System.out.println("Note: Cart collection will be suspended after emergency clearance");
        } else if (fillRate >= collectionThreshold) {
            System.out.printf("*** ATTENTION: Collection needed at %s (%.1f%% full) ***%n", 
                station.getStationId(), fillRate);
            System.out.printf("Collection Frequency: Every %d minutes%n", collectionFrequency);
        }
    }

    private void loadParkingData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PARKING_FILE))) {
            reader.readLine(); // Skip header
            reader.readLine(); // Skip separator
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 6) {
                        parkingSections.add(new ParkingStatus(parts, false));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading parking data: " + e.getMessage());
        }
    }

    private void loadCartData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CART_FILE))) {
            reader.readLine(); // Skip header
            reader.readLine(); // Skip separator
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 6) {
                        cartStations.add(new ParkingStatus(parts, true));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading cart data: " + e.getMessage());
        }
    }

    // Main menu method
    public void startParkingManagement() {
        while (true) {
            System.out.println("\nParking Lot Management System");
            System.out.println("1. View Parking Status");
            System.out.println("2. View Cart Stations Status");
            System.out.println("3. Update Space Occupancy");
            System.out.println("4. Update Cart Station");
            System.out.println("5. Generate Lot Report");
            System.out.println("6. Security Camera Monitoring");
            System.out.println("7. Weather Condition Management");
            System.out.println("8. Maintenance Management");
            System.out.println("9. View Sensor Display");
            System.out.println("10. Return to Main Menu");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: viewParkingStatus(); break;
                case 2: viewCartStatus(); break;
                case 3: updateSpaceOccupancy(); break;
                case 4: updateCartStation(); break;
                case 5: generateLotReport(); break;
                case 6: viewCameraActivity(); break;
                case 7: manageWeatherConditions(); break;
                case 8: manageMaintenanceSchedule(); break;
                case 9: viewSensorDisplay(); break;
                case 10: return;
                default: System.out.println("Invalid option");
            }
        }
    }

    private void viewParkingStatus() {
        System.out.println("\nCurrent Parking Status:");
        System.out.println("-".repeat(80));
        for (ParkingStatus section : parkingSections) {
            // Calculate available spaces (total - occupied)
            int occupied = section.getOccupied();
            int total = section.getTotalSpaces();
            int available = total - occupied;
            
            // Calculate occupancy percentage based on occupied spaces
            double occupancyRate = ((double) occupied / total) * 100;
            
            System.out.printf("Section: %s | Occupied: %d/%d | Available: %d | Disabled: %d | Status: %s | Occupancy: %.1f%%%n",
                section.getSectionId(), 
                occupied,
                total,
                available,
                section.getDisabled(),
                section.getStatus(),
                occupancyRate);
        }
        System.out.println("-".repeat(80));
    }


    private void viewCartStatus() {
        System.out.println("\nCart Stations Status:");
        System.out.println("-".repeat(80));
        
        for (ParkingStatus station : cartStations) {
            double fillRate = (double) station.getCurrentCarts() / station.getCapacity() * 100;
            String status;
            
            // Determine status based on fill rate
            if (station.getCurrentCarts() == 0) {
                status = "CRITICAL - NO CARTS";
            } else if (fillRate >= 80) {
                status = "High";
            } else if (fillRate <= 20) {
                status = "Low";
            } else {
                status = "Normal";
            }
            
            // Display basic station information
            System.out.printf("Station: %s | Location: %s | Carts: %d/%d | Status: %s | Fill Rate: %.1f%%%n",
                station.getStationId(), station.getLocation(),
                station.getCurrentCarts(), station.getCapacity(),
                status, fillRate);
    
            // Show alerts based on weather and fill rate conditions
            if (currentWeather.equals("Severe")) {
                System.out.println("*** ALERT: Cart collection suspended due to severe weather ***");
                if (fillRate >= 40.0) {
                    System.out.println(" URGENT: Emergency collection required before full suspension");
                }
            } else {
                // Show cart-level alerts when not in severe weather
                if (station.getCurrentCarts() == 0) {
                    System.out.println(" URGENT: Station completely empty! Immediate cart replenishment required!");
                    System.out.printf("Recommendation: Check high-fill stations for cart redistribution%n");
                } else if (fillRate <= 20.0) {
                    System.out.println(" Alert: Low cart count - Monitoring required");
                } else if (fillRate >= getWeatherThreshold()) {
                    System.out.printf("*** Collection needed at %s (Weather: %s) ***%n", 
                        station.getStationId(), currentWeather);
                }
            }
        }
    
        // Show weather-specific information
        if (!currentWeather.equals("Normal")) {
            System.out.println("\nWeather-Adjusted Collection Protocols:");
            switch (currentWeather) {
                case "Rain" -> System.out.println("- Collection threshold lowered to 70%\n- Frequency: Every 30 minutes");
                case "Snow" -> System.out.println("- Collection threshold lowered to 60%\n- Frequency: Every 45 minutes");
                case "Severe" -> System.out.println("- Emergency collection only\n- Suspending regular operations");
            }
        }
    
        System.out.println("-".repeat(80));
    }
    private double getWeatherThreshold() {
        return switch (currentWeather) {
            case "Rain" -> 70.0;
            case "Snow" -> 60.0;
            case "Severe" -> 40.0;
            default -> 80.0;
        };
    }


    // Update methods
    private void updateSpaceOccupancy() {
        System.out.print("Enter section ID: ");
        String sectionId = scanner.nextLine();
    
        ParkingStatus section = parkingSections.stream()
            .filter(s -> s.getSectionId().equals(sectionId))
            .findFirst()
            .orElse(null);
    
        if (section == null) {
            System.out.println("Section not found.");
            return;
        }
    
        // Show current status first
        int currentOccupied = section.getOccupied();
        int total = section.getTotalSpaces();
        double currentOccupancy = ((double) currentOccupied / total) * 100;
        
        System.out.printf("\nCurrent Status for Section %s:%n", sectionId);
        System.out.printf("Occupied: %d/%d spaces%n", currentOccupied, total);
        System.out.printf("Occupancy Rate: %.1f%%%n", currentOccupancy);
        
        System.out.print("\nEnter new occupied spaces count: ");
        int newCount = scanner.nextInt();
        scanner.nextLine(); // Consume newline
    
        if (newCount > section.getTotalSpaces()) {
            System.out.println("Error: Count exceeds total spaces.");
            return;
        }
    
        section.updateOccupancy(newCount);
        
        // Show updated status
        double newOccupancy = ((double) newCount / total) * 100;
        System.out.println("\nOccupancy updated successfully.");
        System.out.printf("New Status: %d/%d spaces occupied (%.1f%%)%n", 
            newCount, total, newOccupancy);
    }

    private void updateCartStation() {
        System.out.print("Enter station ID: ");
        String stationId = scanner.nextLine();

        ParkingStatus station = cartStations.stream()
            .filter(s -> s.getStationId().equals(stationId))
            .findFirst()
            .orElse(null);

        if (station == null) {
            System.out.println("Station not found.");
            return;
        }

        System.out.print("Enter new cart count: ");
        int newCount = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (newCount > station.getCapacity()) {
            System.out.println("Error: Count exceeds station capacity.");
            return;
        }

        station.updateCartCount(newCount);
        System.out.println("Cart count updated successfully.");
    }

    // Security methods
    private void monitorSecurityCameras() {
        System.out.println("\nSecurity Camera Monitoring");
        System.out.println("=".repeat(50));
        
        boolean allOperational = true;
        for (ParkingStatus section : parkingSections) {
            System.out.printf("Section %s - Cameras: %s%n", 
                section.getSectionId(),
                section.isCamerasOperational() ? "Operational" : "FAILED");
            
            if (!section.isCamerasOperational()) {
                allOperational = false;
            }
        }

        if (!allOperational) {
            System.out.println("\n*** ALERT: Some cameras require maintenance! ***");
        }

        System.out.println("\n1. Report Camera Issue");
        System.out.println("2. Return to Main Menu");
        System.out.print("Choose an option: ");
        
        if (scanner.nextInt() == 1) {
            scanner.nextLine(); // Consume newline
            reportCameraIssue();
        }
    }

    private void reportCameraIssue() {
        System.out.print("Enter section ID with camera issue: ");
        String sectionId = scanner.nextLine();
        
        parkingSections.stream()
            .filter(s -> s.getSectionId().equals(sectionId))
            .findFirst()
            .ifPresent(section -> {
                section.setCamerasOperational(false);
                System.out.println("Camera issue reported for maintenance.");
            });
    }

    // Weather management methods
    private void manageWeatherConditions() {
        System.out.println("\nWeather Condition Management");
        System.out.println("Current Weather: " + currentWeather);
        System.out.println("\n1. Update Weather Condition");
        System.out.println("2. View Weather Protocols");
        System.out.println("3. Return to Main Menu");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1: updateWeatherCondition(); break;
            case 2: viewWeatherProtocols(); break;
        }
    }

    private void updateWeatherCondition() {
        System.out.println("\nSelect Weather Condition:");
        System.out.println("1. Normal");
        System.out.println("2. Rain");
        System.out.println("3. Snow");
        System.out.println("4. Severe Weather");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
    
        String oldWeather = currentWeather;
        
        switch (choice) {
            case 1:
                applyNormalWeather();
                break;
            case 2:
                applyRainWeather();
                break;
            case 3:
                applySnowWeather();
                break;
            case 4:
                applySevereWeather();
                break;
            default:
                System.out.println("Invalid choice");
                return;
        }
    
        // Show the impact
        System.out.println("\n=== Weather Impact Report ===");
        System.out.println("Previous Weather: " + oldWeather);
        System.out.println("New Weather: " + currentWeather);
        viewParkingStatus(); // Show updated status
    }

    
private void applySevereWeather() {
    currentWeather = "Severe";
    System.out.println("\nSEVERE WEATHER PROTOCOLS ACTIVATED ");
    
    for (ParkingStatus section : parkingSections) {
        // Calculate new capacity (50% reduction)
        int newTotal = section.getTotalSpaces() / 2;
        int currentOccupied = section.getOccupied();
        
        // If current occupancy exceeds new capacity
        if (currentOccupied > newTotal) {
            int excessCars = currentOccupied - newTotal;
            System.out.printf("\nSection %s: %d vehicles must be evacuated%n", 
                section.getSectionId(), excessCars);
            
            // Update to new maximum
            section.setOccupied(newTotal);
        }
        
        section.setTotalSpaces(newTotal);
        section.setStatus("Emergency Protocol");
    }
    
    System.out.println("\nEmergency Measures Implemented:");
    System.out.println("1. Parking Capacity:");
    System.out.println("   - All sections reduced to 50% capacity");
    System.out.println("   - Emergency routes established");
    
    System.out.println("\n2. Safety Protocols:");
    System.out.println("   - Emergency lighting activated");
    System.out.println("   - Weather warning systems enabled");
    
    System.out.println("\n3. Operations Changes:");
    System.out.println("   - Cart collection suspended");
    System.out.println("   - Additional security patrols activated");
}

private void applySnowWeather() {
    currentWeather = "Snow";
    System.out.println("\n SNOW PROTOCOLS ACTIVATED ");
    
    for (ParkingStatus section : parkingSections) {
        // Calculate new capacity (20% reduction)
        int newTotal = (int)(section.getTotalSpaces() * 0.8);
        int currentOccupied = section.getOccupied();
        
        if (currentOccupied > newTotal) {
            int excessCars = currentOccupied - newTotal;
            System.out.printf("\nSection %s: %d vehicles must be relocated%n", 
                section.getSectionId(), excessCars);
            
            section.setOccupied(newTotal);
        }
        
        section.setTotalSpaces(newTotal);
        section.setStatus("Snow Protocol");
    }
    
    System.out.println("\nSnow Measures Implemented:");
    System.out.println("1. Space Management:");
    System.out.println("   - 20% spaces reserved for snow storage");
    System.out.println("   - De-icing systems activated");
}

private void applyRainWeather() {
    currentWeather = "Rain";
    System.out.println("\n RAIN PROTOCOLS ACTIVATED ");
    
    for (ParkingStatus section : parkingSections) {
        section.setStatus("Wet Conditions");
    }
    
    System.out.println("\nRain Measures Implemented:");
    System.out.println("1. Safety Protocols:");
    System.out.println("   - Drainage monitoring active");
    System.out.println("   - Slip warning signs activated");
}

private void applyNormalWeather() {
    currentWeather = "Normal";
    System.out.println("\nReturning to Normal Operations");
    
    for (ParkingStatus section : parkingSections) {
        // Restore original capacity
        section.restoreOriginalCapacity();
        section.setStatus("Active");
    }
}

 private void applyWeatherProtocols() {
        System.out.println("\nApplying weather protocols for: " + currentWeather);
        
        switch (currentWeather) {
            case "Rain":
                System.out.println("- Increasing cart collection frequency");
                System.out.println("- Activating covered area monitoring");
                break;
            case "Snow":
                System.out.println("- Activating snow removal protocols");
                System.out.println("- Increasing space between parking guidance");
                break;
            case "Severe":
                System.out.println("- Activating emergency protocols");
                System.out.println("- Restricting certain parking areas");
                break;
        }
    }

    private void viewWeatherProtocols() {
        System.out.println("\nWeather Protocols:");
        System.out.println("Normal: Standard operations");
        System.out.println("Rain: Increased cart collection, covered area priority");
        System.out.println("Snow: Snow removal active, modified parking guidance");
        System.out.println("Severe: Emergency protocols, restricted areas");
    }

    // Maintenance management methods
    private void manageMaintenanceSchedule() {
        while (true) {
            System.out.println("\nMaintenance Management");
            System.out.println("1. View Maintenance Schedule");
            System.out.println("2. Schedule New Maintenance");
            System.out.println("3. Update Maintenance Status");
            System.out.println("4. View Reported Issues");
            System.out.println("5. Report New Issue");
            System.out.println("6. Resolve/Update Issue");
            System.out.println("7. Return to Main Menu");
            System.out.print("Choose an option: ");
    
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
    
            switch (choice) {
                case 1: viewMaintenanceSchedule(); break;
                case 2: scheduleNewMaintenance(); break;
                case 3: updateMaintenanceStatus(); break;
                case 4: viewReportedIssues(); break;
                case 5: reportMaintenanceIssue(); break;
                case 6: resolveMaintenanceIssue(); break;
                case 7: return;
                default: System.out.println("Invalid option");
            }
        }
    }

    private void updateMaintenanceStatus() {
        System.out.println("\nUpdate Maintenance Status");
        System.out.println("=".repeat(50));
        
        // First show current schedule
        viewMaintenanceSchedule();
        
        System.out.print("\nEnter section ID to update: ");
        String sectionId = scanner.nextLine();
        
        ParkingStatus section = findSection(sectionId);
        if (section == null) {
            System.out.println("Section not found.");
            return;
        }
    
        System.out.println("\nCurrent Status: " + section.getMaintenanceStatus());
        System.out.println("Select new status:");
        System.out.println("1. Completed - OK");
        System.out.println("2. In Progress");
        System.out.println("3. Delayed");
        System.out.println("4. Cancelled");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        String newStatus = switch (choice) {
            case 1 -> "OK";
            case 2 -> "IN_PROGRESS";
            case 3 -> "DELAYED";
            case 4 -> "CANCELLED";
            default -> null;
        };
    
        if (newStatus != null) {
            section.setMaintenanceStatus(newStatus);
            System.out.println("Maintenance status updated successfully.");
        }
    }

    private void viewReportedIssues() {
        System.out.println("\nReported Maintenance Issues");
        System.out.println("=".repeat(80));
        
        boolean hasIssues = false;
        for (ParkingStatus section : parkingSections) {
            if (!section.getMaintenanceStatus().equals("OK")) {
                hasIssues = true;
                System.out.printf("Section: %s%n", section.getSectionId());
                System.out.printf("Status: %s%n", section.getMaintenanceStatus());
                System.out.printf("Reported: %s%n", section.getLastUpdated());
                System.out.printf("Next Maintenance: %s%n", 
                    section.getNextMaintenance().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                System.out.println("-".repeat(50));
            }
        }
        
        if (!hasIssues) {
            System.out.println("No maintenance issues reported.");
        }
    }

    private void resolveMaintenanceIssue() {
        System.out.println("\nResolve/Update Maintenance Issue");
        System.out.println("=".repeat(50));
        
        System.out.print("Enter section ID: ");
        String sectionId = scanner.nextLine();
        
        ParkingStatus section = findSection(sectionId);
        if (section == null) {
            System.out.println("Section not found.");
            return;
        }
    
        if (section.getMaintenanceStatus().equals("OK")) {
            System.out.println("No active maintenance issues for this section.");
            return;
        }
    
        System.out.println("\nCurrent Issue Details:");
        System.out.printf("Status: %s%n", section.getMaintenanceStatus());
        System.out.printf("Next Scheduled: %s%n", 
            section.getNextMaintenance().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    
        System.out.println("\nSelect action:");
        System.out.println("1. Mark as Resolved");
        System.out.println("2. Reschedule Maintenance");
        System.out.println("3. Update Priority");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
    
        switch (choice) {
            case 1:
                section.setMaintenanceStatus("OK");
                System.out.println("Issue marked as resolved.");
                break;
            case 2:
                System.out.print("Enter days to reschedule: ");
                int days = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                section.setNextMaintenance(LocalDateTime.now().plusDays(days));
                System.out.println("Maintenance rescheduled.");
                break;
            case 3:
                updateMaintenancePriority(section);
                break;
        }
    }


    private void updateMaintenancePriority(ParkingStatus section) {
        System.out.println("\nSelect new priority:");
        System.out.println("1. Urgent");
        System.out.println("2. High");
        System.out.println("3. Medium");
        System.out.println("4. Low");
        
        int priority = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        String status = switch (priority) {
            case 1 -> "URGENT";
            case 2 -> "HIGH";
            case 3 -> "MEDIUM";
            case 4 -> "LOW";
            default -> section.getMaintenanceStatus();
        };
        
        section.setMaintenanceStatus(status);
        System.out.println("Priority updated successfully.");
    }
    

    private void viewMaintenanceSchedule() {
        System.out.println("\nCurrent Maintenance Schedule:");
        System.out.println("=".repeat(50));
        
        for (ParkingStatus section : parkingSections) {
            System.out.printf("Section %s - Next Maintenance: %s - Status: %s%n",
                section.getSectionId(),
                section.getNextMaintenance().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                section.getMaintenanceStatus());
        }
    }

    private void scheduleNewMaintenance() {
        System.out.println("\nSchedule New Maintenance");
        System.out.println("=".repeat(50));
        
        System.out.print("Enter section ID: ");
        String sectionId = scanner.nextLine();
        
        ParkingStatus section = findSection(sectionId);
        if (section == null) {
            System.out.println("Section not found.");
            return;
        }
        
        System.out.println("\nSelect maintenance type:");
        System.out.println("1. Routine Inspection");
        System.out.println("2. Equipment Maintenance");
        System.out.println("3. Surface Repairs");
        System.out.println("4. Lighting Maintenance");
        System.out.print("Choose type: ");
        
        int typeChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        System.out.print("Enter days from now for maintenance: ");
        int days = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        LocalDateTime maintenanceDate = LocalDateTime.now().plusDays(days);
        section.setNextMaintenance(maintenanceDate);
        section.setMaintenanceStatus("Scheduled");
        
        System.out.println("Maintenance scheduled for: " + 
            maintenanceDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    private void reportMaintenanceIssue() {
        System.out.println("\nReport Maintenance Issue");
        System.out.println("=".repeat(50));
        
        System.out.print("Enter section ID: ");
        String sectionId = scanner.nextLine();
        
        ParkingStatus section = findSection(sectionId);
        if (section == null) {
            System.out.println("Section not found.");
            return;
        }
        
        System.out.println("\nSelect issue priority:");
        System.out.println("1. Urgent - Immediate attention required");
        System.out.println("2. High - Needs attention within 24 hours");
        System.out.println("3. Medium - Needs attention within week");
        System.out.println("4. Low - Routine maintenance required");
        System.out.print("Choose priority: ");
        
        int priority = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        System.out.print("Enter issue description: ");
        String description = scanner.nextLine();
        
        // Update section status based on priority
        switch (priority) {
            case 1:
                section.setMaintenanceStatus("URGENT");
                section.setNextMaintenance(LocalDateTime.now()); // Immediate
                break;
            case 2:
                section.setMaintenanceStatus("HIGH");
                section.setNextMaintenance(LocalDateTime.now().plusDays(1));
                break;
            case 3:
                section.setMaintenanceStatus("MEDIUM");
                section.setNextMaintenance(LocalDateTime.now().plusWeeks(1));
                break;
            case 4:
                section.setMaintenanceStatus("LOW");
                section.setNextMaintenance(LocalDateTime.now().plusWeeks(2));
                break;
        }
        
        System.out.println("\nMaintenance issue reported successfully.");
        System.out.println("Current section status: " + section.getMaintenanceStatus());
        System.out.println("Next maintenance scheduled for: " + 
            section.getNextMaintenance().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

      // Emergency/Issue handling methods
      private void handleEmergencyIssues() {
        System.out.println("\nEmergency/Issue Management");
        System.out.println("1. Report New Issue");
        System.out.println("2. View Active Issues");
        System.out.println("3. Resolve Issue");
        System.out.println("4. Return to Main Menu");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1: reportNewIssue(); break;
            case 2: viewActiveIssues(); break;
            case 3: resolveIssue(); break;
        }
    }

    private void reportNewIssue() {
        System.out.println("\nReport New Issue");
        System.out.println("=".repeat(50));
        
        System.out.print("Enter section ID: ");
        String sectionId = scanner.nextLine();
        
        System.out.println("\nSelect issue type:");
        System.out.println("1. Safety Hazard");
        System.out.println("2. Equipment Malfunction");
        System.out.println("3. Security Concern");
        System.out.println("4. Structure Damage");
        System.out.println("5. Other");
        System.out.print("Choose type: ");
        
        int typeChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        String issueType = switch (typeChoice) {
            case 1 -> "Safety Hazard";
            case 2 -> "Equipment Malfunction";
            case 3 -> "Security Concern";
            case 4 -> "Structure Damage";
            default -> "Other";
        };
        
        System.out.print("Enter issue description: ");
        String description = scanner.nextLine();
        
        // Create and add the new issue
        ParkingIssue newIssue = new ParkingIssue(sectionId, issueType, description);
        activeIssues.add(newIssue);
        
        // Update section status if needed
        parkingSections.stream()
            .filter(s -> s.getSectionId().equals(sectionId))
            .findFirst()
            .ifPresent(section -> {
                if (issueType.equals("Safety Hazard") || issueType.equals("Structure Damage")) {
                    section.setStatus("Restricted");
                    section.setMaintenanceStatus("Urgent");
                }
            });
        
        System.out.println("Issue reported successfully. Issue ID: " + newIssue.id);
    }

    private void viewActiveIssues() {
        if (activeIssues.isEmpty()) {
            System.out.println("\nNo active issues found.");
            return;
        }
        
        System.out.println("\nActive Issues:");
        System.out.println("=".repeat(80));
        
        for (ParkingIssue issue : activeIssues) {
            if (issue.status.equals("Open")) {
                System.out.printf("ID: %s%n", issue.id);
                System.out.printf("Section: %s%n", issue.sectionId);
                System.out.printf("Type: %s%n", issue.type);
                System.out.printf("Description: %s%n", issue.description);
                System.out.printf("Reported: %s%n", 
                    issue.reportedTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                System.out.println("-".repeat(50));
            }
        }
    }

    private void resolveIssue() {
        System.out.print("\nEnter Issue ID to resolve: ");
        String issueId = scanner.nextLine();
        
        for (ParkingIssue issue : activeIssues) {
            if (issue.id.equals(issueId)) {
                System.out.println("\nIssue Details:");
                System.out.printf("Section: %s%n", issue.sectionId);
                System.out.printf("Type: %s%n", issue.type);
                System.out.printf("Description: %s%n", issue.description);
                
                System.out.print("\nMark as resolved? (Y/N): ");
                String confirm = scanner.nextLine();
                
                if (confirm.equalsIgnoreCase("Y")) {
                    issue.status = "Resolved";
                    
                    // Update section status if needed
                    parkingSections.stream()
                        .filter(s -> s.getSectionId().equals(issue.sectionId))
                        .findFirst()
                        .ifPresent(section -> {
                            section.setStatus("Active");
                            section.setMaintenanceStatus("OK");
                        });
                    
                    System.out.println("Issue marked as resolved.");
                    return;
                }
            }
        }
        System.out.println("Issue not found or could not be resolved.");
    }

    // Report generation method
    private void generateLotReport() {
        System.out.println("\nParking Lot Status Report");
        System.out.println("=".repeat(50));

        // Overall parking statistics
        int totalSpaces = parkingSections.stream().mapToInt(ParkingStatus::getTotalSpaces).sum();
        int totalOccupied = parkingSections.stream().mapToInt(ParkingStatus::getOccupied).sum();
        double overallOccupancy = (double) totalOccupied / totalSpaces * 100;

        System.out.printf("Total Spaces: %d%n", totalSpaces);
        System.out.printf("Occupied Spaces: %d%n", totalOccupied);
        System.out.printf("Overall Occupancy: %.1f%%%n", overallOccupancy);

        // Cart station statistics
        int totalCarts = cartStations.stream().mapToInt(ParkingStatus::getCurrentCarts).sum();
        int totalCapacity = cartStations.stream().mapToInt(ParkingStatus::getCapacity).sum();
        double cartUtilization = (double) totalCarts / totalCapacity * 100;

        System.out.println("\nCart Station Status");
        System.out.printf("Total Carts in Stations: %d%n", totalCarts);
        System.out.printf("Total Station Capacity: %d%n", totalCapacity);
        System.out.printf("Cart Station Utilization: %.1f%%%n", cartUtilization);

        System.out.println("\nOperational Status:");
        System.out.println("Weather Condition: " + currentWeather);
        System.out.println("Security System: " + (securitySystemActive ? "Active" : "Inactive"));

        // Alerts
        if (overallOccupancy >= 85) {
            System.out.println("\n*** ALERT: Lot nearing capacity! ***");
        }
        
        System.out.println("\nMaintenance Alerts:");
        parkingSections.stream()
            .filter(s -> !s.getMaintenanceStatus().equals("OK"))
            .forEach(s -> System.out.println("Section " + s.getSectionId() + 
                " needs maintenance: " + s.getMaintenanceStatus()));
                
        System.out.println("=".repeat(50));
    }

    // Helper method
    private ParkingStatus findSection(String sectionId) {
        return parkingSections.stream()
            .filter(s -> s.getSectionId().equals(sectionId))
            .findFirst()
            .orElse(null);
    }


    private void viewSensorDisplay() {
        System.out.println("\nReal-time Parking Space Sensor Display");
        System.out.println("=".repeat(100));

        for (ParkingStatus section : parkingSections) {
            System.out.printf("\nSection: %s | Status: %s%n", section.getSectionId(), section.getStatus());
            System.out.println("Legend: [O]=Occupied, [A]=Available, [D]=Disabled, [X]=Sensor Error");
            System.out.println("-".repeat(50));

            // Display parking spaces in a grid
            int totalSpaces = section.getTotalSpaces();
            int occupied = section.getOccupied();
            int disabled = section.getDisabled();

            for (int i = 0; i < totalSpaces; i++) {
                if (i % 10 == 0) System.out.println(); // New line every 10 spaces
                
                if (i < occupied) {
                    System.out.print("[O] "); // Occupied space
                } else if (i < (totalSpaces - disabled)) {
                    System.out.print("[A] "); // Available space
                } else {
                    System.out.print("[D] "); // Disabled space
                }
            }
            System.out.println("\n");
            
            // Show sensor statistics
            System.out.printf("Occupancy Rate: %.1f%% | ", 
                ((double)occupied/totalSpaces) * 100);
            System.out.printf("Available Spaces: %d | ", 
                totalSpaces - occupied - disabled);
            System.out.printf("Disabled Spaces: %d%n", disabled);
        }
    }

    private void viewCameraActivity() {
        System.out.println("\nLive Camera Activity Monitor");
        System.out.println("=".repeat(100));

        for (ParkingStatus section : parkingSections) {
            System.out.printf("\nSection: %s Camera Feed%n", section.getSectionId());
            System.out.println("-".repeat(50));

            // Simulate camera feed with ASCII art
            System.out.println("Entry Camera:");
            displayCameraFeed("Entry", section);

            System.out.println("\nExit Camera:");
            displayCameraFeed("Exit", section);

            System.out.println("\nOverview Camera:");
            displayCameraFeed("Overview", section);

            // Camera status and alerts
            System.out.println("\nCamera Status:");
            System.out.printf("- Entry Camera: %s%n", 
                section.isCamerasOperational() ? "✓ Online" : " Offline");
            System.out.printf("- Exit Camera: %s%n", 
                section.isCamerasOperational() ? "✓ Online" : " Offline");
            System.out.printf("- Overview Camera: %s%n", 
                section.isCamerasOperational() ? "✓ Online" : " Offline");

            // Show any detected activity
            showDetectedActivity(section);
        }
    }

    private void displayCameraFeed(String cameraType, ParkingStatus section) {

    }

    private void showDetectedActivity(ParkingStatus section) {
        // Simulate activity detection
        System.out.println("\nDetected Activity:");
        if (section.getOccupied() > 0) {
            System.out.println("- Vehicle movement detected in lanes");
            System.out.println("- Pedestrian activity near spots " + 
                (section.getOccupied() - 2) + "-" + section.getOccupied());
            if (section.getOccupied() > section.getTotalSpaces() * 0.8) {
                System.out.println(" High traffic alert");
            }
        } else {
            System.out.println("- No significant activity detected");
        }
    }
}