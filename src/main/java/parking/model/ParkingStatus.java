/**
 * Manages the parking lot status including spaces and cart stations.
 *
 * @author Chandrashekar Tirunagiri
 */
package parking.model;

import java.time.LocalDateTime;

public class ParkingStatus {
    // Parking section details
    private String sectionId;
    private int totalSpaces;
    private int occupied;
    private int disabled;
    private String status;
    private String lastUpdated;
    private boolean camerasOperational;
    private String weatherCondition;
    private String maintenanceStatus;
    private LocalDateTime nextMaintenance;
    private boolean sensorStatus;
    private String emergencyStatus;
    private int originalTotalSpaces; 
    

    // Cart station details
    private String stationId;
    private String location;
    private int capacity;
    private int currentCarts;
    private String stationStatus;
    private String lastCollected;

    // Constructor for parking section
    public ParkingStatus(String[] parts, boolean isCartStation) {
        if (!isCartStation) {
            this.sectionId = parts[0].trim();
            this.totalSpaces = Integer.parseInt(parts[1].trim());
            this.occupied = Integer.parseInt(parts[2].trim());
            this.disabled = Integer.parseInt(parts[3].trim());
            this.status = parts[4].trim();
            this.lastUpdated = parts[5].trim();
        } else {
            this.stationId = parts[0].trim();
            this.location = parts[1].trim();
            this.capacity = Integer.parseInt(parts[2].trim());
            this.currentCarts = Integer.parseInt(parts[3].trim());
            this.stationStatus = parts[4].trim();
            this.lastCollected = parts[5].trim();
        }

        this.originalTotalSpaces = totalSpaces;

        this.camerasOperational = true;
        this.weatherCondition = "Normal";
        this.maintenanceStatus = "OK";
        this.nextMaintenance = LocalDateTime.now().plusDays(7);
        this.sensorStatus = true;
        this.emergencyStatus = "None";


    }

    public void setTotalSpaces(int spaces) {
        this.totalSpaces = spaces;
    }

    public void restoreOriginalCapacity() {
        this.totalSpaces = this.originalTotalSpaces;
    }

    // Getters for parking section
    public String getSectionId() { return sectionId; }
    public int getTotalSpaces() { return totalSpaces; }
    public int getOccupied() { return occupied; }
    public int getDisabled() { return disabled; }
    public String getStatus() { return status; }
    public String getLastUpdated() { return lastUpdated; }
    //Getters for monitoring features
    public boolean isCamerasOperational() { return camerasOperational; }
    public String getWeatherCondition() { return weatherCondition; }
    public String getMaintenanceStatus() { return maintenanceStatus; }
    public LocalDateTime getNextMaintenance() { return nextMaintenance; }
    public boolean isSensorOperational() { return sensorStatus; }
    public String getEmergencyStatus() { return emergencyStatus; }

    // Getters for cart station
    public String getStationId() { return stationId; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public int getCurrentCarts() { return currentCarts; }
    public String getStationStatus() { return stationStatus; }
    public String getLastCollected() { return lastCollected; }

    // Setters for parking section
    
    public void setCamerasOperational(boolean operational) { this.camerasOperational = operational; }
    public void setWeatherCondition(String condition) { this.weatherCondition = condition; }
    public void setMaintenanceStatus(String status) { this.maintenanceStatus = status; }
    public void setNextMaintenance(LocalDateTime date) { this.nextMaintenance = date; }
    public void setSensorStatus(boolean operational) { this.sensorStatus = operational; }
    public void setEmergencyStatus(String status) { this.emergencyStatus = status; }
    public void setOccupied(int occupied) { this.occupied = occupied; }
    // Setters for cart station
    public void setStationStatus(String status) { this.stationStatus = status; }
    public void setStatus(String status) { this.status = status; }



    // Update methods
    public void updateOccupancy(int newOccupied) {
        if (newOccupied > totalSpaces) {
            System.out.println("Error: Occupied spaces cannot exceed total spaces.");
            return;
        }
        this.occupied = newOccupied;
    }

    public void updateCartCount(int newCount) {
        this.currentCarts = newCount;
        this.lastCollected = LocalDateTime.now().toString();
    }
}