package energy;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class EnergyConsumptionSystem {
    private final String storeId;
    private final Scanner scanner;
    private final String dataFilePath;
    private final String alertsFilePath;
    private Map<String, List<EnergyReading>> deviceReadings;
    private List<EnergyAlert> alerts;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public EnergyConsumptionSystem(String storeId) {
        this.storeId = storeId;
        this.scanner = new Scanner(System.in);
        this.dataFilePath = "src/main/java/energy/data/energy_readings_" + storeId + ".txt";
        this.alertsFilePath = "src/main/java/energy/data/energy_alerts_" + storeId + ".txt";
        loadData();
    }

    private void loadData() {
        deviceReadings = new HashMap<>();
        alerts = new ArrayList<>();
        
        // Load energy readings
        try {
            File file = new File(dataFilePath);
            if (file.exists()) {
                List<String> lines = Files.readAllLines(Paths.get(dataFilePath));
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        String deviceId = parts[0];
                        LocalDateTime timestamp = LocalDateTime.parse(parts[1], DATE_FORMAT);
                        double consumption = Double.parseDouble(parts[2]);
                        String deviceType = parts[3];
                        
                        deviceReadings.computeIfAbsent(deviceId, k -> new ArrayList<>())
                            .add(new EnergyReading(deviceId, timestamp, consumption, deviceType));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading energy readings: " + e.getMessage());
        }

        // Load alerts
        try {
            File file = new File(alertsFilePath);
            if (file.exists()) {
                List<String> lines = Files.readAllLines(Paths.get(alertsFilePath));
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        String deviceId = parts[0];
                        LocalDateTime timestamp = LocalDateTime.parse(parts[1], DATE_FORMAT);
                        String alertType = parts[2];
                        String status = parts[3];
                        alerts.add(new EnergyAlert(deviceId, timestamp, alertType, status));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading alerts: " + e.getMessage());
        }
    }

    private void saveData() {
        // Save energy readings
        try {
            StringBuilder content = new StringBuilder();
            for (List<EnergyReading> readings : deviceReadings.values()) {
                for (EnergyReading reading : readings) {
                    content.append(String.format("%s,%s,%f,%s%n",
                        reading.deviceId,
                        reading.timestamp.format(DATE_FORMAT),
                        reading.consumption,
                        reading.deviceType));
                }
            }
            Files.write(Paths.get(dataFilePath), content.toString().getBytes());
        } catch (IOException e) {
            System.out.println("Error saving energy readings: " + e.getMessage());
        }

        // Save alerts
        try {
            StringBuilder content = new StringBuilder();
            for (EnergyAlert alert : alerts) {
                content.append(String.format("%s,%s,%s,%s%n",
                    alert.deviceId,
                    alert.timestamp.format(DATE_FORMAT),
                    alert.alertType,
                    alert.status));
            }
            Files.write(Paths.get(alertsFilePath), content.toString().getBytes());
        } catch (IOException e) {
            System.out.println("Error saving alerts: " + e.getMessage());
        }
    }

    public void start() {
        while (true) {
            displayMenu();
            int choice = getValidMenuChoice();
            if (choice == -1) continue;

            switch (choice) {
                case 1:
                    viewEnergyDashboard();
                    break;
                case 2:
                    addEnergyReading();
                    break;
                case 3:
                    viewAlerts();
                    break;
                case 4:
                    generateMonthlyReport();
                    break;
                case 5:
                    manageDevices();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid option. Please choose a number between 1 and 6.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\nEnergy Consumption Tracking System");
        System.out.println("1. View Energy Dashboard (Store Manager/Staff)");
        System.out.println("2. Add Energy Reading (IoT Device/Manual Entry)");
        System.out.println("3. View and Manage Alerts (Store Manager)");
        System.out.println("4. Generate Monthly Report (Store Manager)");
        System.out.println("5. Manage IoT Devices (Store Manager)");
        System.out.println("6. Return to Main Menu");
    }

    private int getValidMenuChoice() {
        System.out.print("Choose an option (1-6): ");
        try {
            String input = scanner.nextLine().trim();
            int choice = Integer.parseInt(input);
            if (choice >= 1 && choice <= 6) {
                return choice;
            } else {
                System.out.println("Please enter a number between 1 and 6.");
                return -1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number between 1 and 6.");
            return -1;
        }
    }

    private void viewEnergyDashboard() {
        System.out.println("\nEnergy Consumption Dashboard");
        System.out.println("----------------------------");
        
        if (deviceReadings.isEmpty()) {
            System.out.println("No energy readings available.");
            return;
        }

        Map<String, Double> totalByDevice = new HashMap<>();
        Map<String, Integer> countByDevice = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        // Calculate averages by device
        for (Map.Entry<String, List<EnergyReading>> entry : deviceReadings.entrySet()) {
            String deviceId = entry.getKey();
            List<EnergyReading> readings = entry.getValue();
            
            // Sort readings by timestamp
            readings.sort((a, b) -> b.timestamp.compareTo(a.timestamp));
            
            // Calculate daily average (last 24 hours)
            LocalDateTime oneDayAgo = now.minusDays(1);
            double dailyTotal = readings.stream()
                .filter(r -> r.timestamp.isAfter(oneDayAgo))
                .mapToDouble(r -> r.consumption)
                .sum();
            int dailyCount = (int) readings.stream()
                .filter(r -> r.timestamp.isAfter(oneDayAgo))
                .count();

            // Calculate total consumption
            double total = readings.stream()
                .mapToDouble(r -> r.consumption)
                .sum();
            
            totalByDevice.put(deviceId, total);
            countByDevice.put(deviceId, readings.size());
            
            EnergyReading latest = readings.get(0); // First reading after sorting
            LocalDateTime oldest = readings.get(readings.size() - 1).timestamp;
            
            System.out.printf("\nDevice: %s (%s)%n", deviceId, latest.deviceType);
            System.out.printf("Latest Reading: %.2f kWh (at %s)%n", 
                latest.consumption, 
                latest.timestamp.format(DATE_FORMAT));
            
            if (dailyCount > 0) {
                System.out.printf("24-Hour Average: %.2f kWh/reading%n", 
                    dailyTotal / dailyCount);
            }
            
            System.out.printf("Overall Average: %.2f kWh/reading (over %d readings from %s to %s)%n",
                total / readings.size(),
                readings.size(),
                oldest.format(DATE_FORMAT),
                latest.timestamp.format(DATE_FORMAT));
        }

        // Show total store consumption
        double totalConsumption = totalByDevice.values().stream().mapToDouble(Double::doubleValue).sum();
        System.out.printf("%nTotal Store Consumption: %.2f kWh%n", totalConsumption);
        
        // Show consumption by device type
        System.out.println("\nConsumption by Device Type:");
        Map<String, Double> consumptionByType = new HashMap<>();
        for (Map.Entry<String, List<EnergyReading>> entry : deviceReadings.entrySet()) {
            List<EnergyReading> readings = entry.getValue();
            String deviceType = readings.get(0).deviceType;
            double total = readings.stream().mapToDouble(r -> r.consumption).sum();
            consumptionByType.merge(deviceType, total, Double::sum);
        }
        
        consumptionByType.forEach((type, total) -> 
            System.out.printf("%s: %.2f kWh (%.1f%% of total)%n",
                type,
                total,
                (total / totalConsumption) * 100));
    }

    private void addEnergyReading() {
        System.out.println("\nAdd Energy Reading");
        System.out.println("------------------");

        String deviceId = getValidDeviceId();
        String deviceType = getValidDeviceType();
        double consumption = getValidConsumption();

        EnergyReading reading = new EnergyReading(
            deviceId,
            LocalDateTime.now(),
            consumption,
            deviceType
        );

        deviceReadings.computeIfAbsent(deviceId, k -> new ArrayList<>()).add(reading);
        
        // Check for energy spike
        checkForEnergySpike(reading);
        
        saveData();
        System.out.println("Energy reading added successfully.");
    }

    private void checkForEnergySpike(EnergyReading newReading) {
        List<EnergyReading> deviceHistory = deviceReadings.get(newReading.deviceId);
        if (deviceHistory.size() < 2) return;

        // Calculate average of previous readings
        double avgConsumption = deviceHistory.subList(0, deviceHistory.size() - 1)
            .stream()
            .mapToDouble(r -> r.consumption)
            .average()
            .orElse(0.0);

        // If new reading is 50% higher than average, create alert
        if (newReading.consumption > avgConsumption * 1.5) {
            EnergyAlert alert = new EnergyAlert(
                newReading.deviceId,
                LocalDateTime.now(),
                "ENERGY_SPIKE",
                "PENDING"
            );
            alerts.add(alert);
            saveData();
            System.out.println(" Alert: Energy spike detected for device " + newReading.deviceId);
        }
    }

    private void viewAlerts() {
        System.out.println("\nEnergy Alerts");
        System.out.println("-------------");

        if (alerts.isEmpty()) {
            System.out.println("No alerts found.");
            return;
        }

        for (int i = 0; i < alerts.size(); i++) {
            EnergyAlert alert = alerts.get(i);
            System.out.printf("\n%d. Device: %s%n", i + 1, alert.deviceId);
            System.out.printf("   Time: %s%n", alert.timestamp.format(DATE_FORMAT));
            System.out.printf("   Type: %s%n", alert.alertType);
            System.out.printf("   Status: %s%n", alert.status);
        }

        System.out.print("\nEnter alert number to update status (or 0 to return): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice > 0 && choice <= alerts.size()) {
                updateAlertStatus(choice - 1);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private void updateAlertStatus(int index) {
        System.out.println("\nUpdate Alert Status");
        System.out.println("1. Mark as Resolved");
        System.out.println("2. Mark as In Progress");
        System.out.println("3. Mark as Pending");
        
        System.out.print("Choose status: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            String newStatus = switch (choice) {
                case 1 -> "RESOLVED";
                case 2 -> "IN_PROGRESS";
                case 3 -> "PENDING";
                default -> null;
            };

            if (newStatus != null) {
                alerts.get(index).status = newStatus;
                saveData();
                System.out.println("Alert status updated successfully.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private void generateMonthlyReport() {
        System.out.println("\nGenerating Monthly Energy Consumption Report...");

        if (deviceReadings.isEmpty()) {
            System.out.println("No data available for report generation.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
        String reportFileName = String.format("src/main/java/energy/reports/energy_report_%s_%s.txt", storeId, timestamp);

        try {
            StringBuilder report = new StringBuilder();
            report.append("Energy Consumption Report\n");
            report.append("------------------------\n");
            report.append("Store ID: ").append(storeId).append("\n");
            report.append("Generated on: ").append(now.format(DATE_FORMAT)).append("\n\n");

            // Calculate consumption by device type
            Map<String, Double> consumptionByType = new HashMap<>();
            Map<String, Integer> readingsByType = new HashMap<>();
            
            for (List<EnergyReading> readings : deviceReadings.values()) {
                for (EnergyReading reading : readings) {
                    consumptionByType.merge(reading.deviceType, reading.consumption, Double::sum);
                    readingsByType.merge(reading.deviceType, 1, Integer::sum);
                }
            }

            // Device Type Analysis
            report.append("Consumption by Device Type:\n");
            report.append("---------------------------\n");
            consumptionByType.forEach((type, total) -> {
                int readings = readingsByType.get(type);
                double average = total / readings;
                report.append(String.format("%s:\n", type));
                report.append(String.format("  Total Consumption: %.2f kWh\n", total));
                report.append(String.format("  Number of Readings: %d\n", readings));
                report.append(String.format("  Average per Reading: %.2f kWh\n\n", average));
            });

            // Calculate and display total cost
            double totalConsumption = consumptionByType.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
            double totalCost = totalConsumption * 0.12; // Assuming $0.12 per kWh

            report.append("\nSummary:\n");
            report.append("--------\n");
            report.append(String.format("Total Consumption: %.2f kWh\n", totalConsumption));
            report.append(String.format("Estimated Cost: $%.2f\n", totalCost));

            // Alert Analysis
            report.append("\nAlert Analysis:\n");
            report.append("--------------\n");
            if (alerts.isEmpty()) {
                report.append("No alerts recorded in this period.\n");
            } else {
                Map<String, Long> alertsByStatus = alerts.stream()
                    .collect(Collectors.groupingBy(a -> a.status, Collectors.counting()));
                
                report.append("Alert Status Summary:\n");
                alertsByStatus.forEach((status, count) -> 
                    report.append(String.format("  %s: %d\n", status, count)));
                
                report.append("\nDetailed Alerts:\n");
                alerts.forEach(alert -> report.append(String.format("  %s: %s (%s) - %s\n",
                    alert.deviceId,
                    alert.alertType,
                    alert.timestamp.format(DATE_FORMAT),
                    alert.status)));
            }

            // Recommendations
            report.append("\nRecommendations:\n");
            report.append("----------------\n");
            generateRecommendations(report, consumptionByType, totalConsumption);

            // Save the report
            Files.write(Paths.get(reportFileName), report.toString().getBytes());
            
            System.out.println("Report generated successfully!");
            System.out.println("Report saved as: " + reportFileName);
            System.out.println("\nReport Preview:");
            System.out.println("---------------");
            System.out.println(report.toString());

        } catch (IOException e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    private void generateRecommendations(StringBuilder report, Map<String, Double> consumptionByType, double totalConsumption) {
        // Analyze HVAC usage
        if (consumptionByType.containsKey("HVAC")) {
            double hvacPercentage = (consumptionByType.get("HVAC") / totalConsumption) * 100;
            if (hvacPercentage > 35) {
                report.append("- Consider optimizing HVAC schedule during off-peak hours\n");
                report.append("- Recommend maintenance check for HVAC efficiency\n");
            }
        }

        // Analyze Lighting usage
        if (consumptionByType.containsKey("Lighting")) {
            double lightingPercentage = (consumptionByType.get("Lighting") / totalConsumption) * 100;
            if (lightingPercentage > 20) {
                report.append("- Consider upgrading to more energy-efficient LED lighting\n");
                report.append("- Review lighting schedule and motion sensor implementation\n");
            }
        }

        // Analyze Refrigeration usage
        if (consumptionByType.containsKey("Refrigeration")) {
            double refPercentage = (consumptionByType.get("Refrigeration") / totalConsumption) * 100;
            if (refPercentage > 50) {
                report.append("- Schedule maintenance check for refrigeration units\n");
                report.append("- Consider upgrading to energy-efficient refrigeration systems\n");
            }
        }

        // General recommendations
        report.append("- Regular maintenance of all equipment is recommended\n");
        report.append("- Consider implementing an energy management system\n");
        report.append("- Train staff on energy-saving practices\n");
    }

    private void manageDevices() {
        System.out.println("\nIoT Device Management");
        System.out.println("--------------------");
        System.out.println("1. List All Devices");
        System.out.println("2. Add New Device");
        System.out.println("3. Remove Device");
        System.out.println("4. Return");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1:
                    listDevices();
                    break;
                case 2:
                    addDevice();
                    break;
                case 3:
                    removeDevice();
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private void listDevices() {
        System.out.println("\nRegistered IoT Devices:");
        if (deviceReadings.isEmpty()) {
            System.out.println("No devices registered.");
            return;
        }

        deviceReadings.forEach((deviceId, readings) -> {
            EnergyReading latest = readings.get(readings.size() - 1);
            System.out.printf("\nDevice ID: %s%n", deviceId);
            System.out.printf("Type: %s%n", latest.deviceType);
            System.out.printf("Last Reading: %.2f kWh%n", latest.consumption);
            System.out.printf("Last Updated: %s%n", latest.timestamp.format(DATE_FORMAT));
        });
    }

    private void addDevice() {
        String deviceId = getValidDeviceId();
        if (deviceReadings.containsKey(deviceId)) {
            System.out.println("Device already exists!");
            return;
        }

        String deviceType = getValidDeviceType();
        deviceReadings.put(deviceId, new ArrayList<>());
        System.out.println("Device added successfully. Ready to receive readings.");
    }

    private void removeDevice() {
        String deviceId = getValidDeviceId();
        if (deviceReadings.remove(deviceId) != null) {
            System.out.println("Device removed successfully.");
            saveData();
        } else {
            System.out.println("Device not found.");
        }
    }

    private String getValidDeviceId() {
        while (true) {
            System.out.print("Enter Device ID: ");
            String deviceId = scanner.nextLine().trim();
            if (deviceId.isEmpty()) {
                System.out.println("Device ID cannot be empty.");
                continue;
            }
            if (!deviceId.matches("[a-zA-Z0-9_-]+")) {
                System.out.println("Device ID can only contain letters, numbers, hyphens, and underscores.");
                continue;
            }
            return deviceId;
        }
    }

    private String getValidDeviceType() {
        while (true) {
            System.out.println("\nDevice Types:");
            System.out.println("1. HVAC");
            System.out.println("2. Refrigeration");
            System.out.println("3. Lighting");
            System.out.println("4. Other");
            
            System.out.print("Choose device type (1-4): ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                return switch (choice) {
                    case 1 -> "HVAC";
                    case 2 -> "Refrigeration";
                    case 3 -> "Lighting";
                    case 4 -> "Other";
                    default -> {
                        System.out.println("Invalid choice. Please select 1-4.");
                        yield null;
                    }
                };
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private double getValidConsumption() {
        while (true) {
            System.out.print("Enter energy consumption (kWh): ");
            try {
                double consumption = Double.parseDouble(scanner.nextLine().trim());
                if (consumption < 0) {
                    System.out.println("Consumption cannot be negative.");
                    continue;
                }
                return consumption;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static class EnergyReading {
        private final String deviceId;
        private final LocalDateTime timestamp;
        private final double consumption;
        private final String deviceType;

        public EnergyReading(String deviceId, LocalDateTime timestamp, double consumption, String deviceType) {
            this.deviceId = deviceId;
            this.timestamp = timestamp;
            this.consumption = consumption;
            this.deviceType = deviceType;
        }
    }

    private static class EnergyAlert {
        private final String deviceId;
        private final LocalDateTime timestamp;
        private final String alertType;
        private String status;

        public EnergyAlert(String deviceId, LocalDateTime timestamp, String alertType, String status) {
            this.deviceId = deviceId;
            this.timestamp = timestamp;
            this.alertType = alertType;
            this.status = status;
        }
    }
} 