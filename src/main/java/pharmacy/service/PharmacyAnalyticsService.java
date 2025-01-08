/**
 * Service class for generating and managing pharmacy analytics.
 * Handles inventory reports and prescription statistics.
 *
 * @author Hrishikesha Kyathsandra
 */
package pharmacy.service;

import pharmacy.model.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PharmacyAnalyticsService {
    private PharmacyManager pharmacyManager;
    private String storeId;

    public PharmacyAnalyticsService(PharmacyManager pharmacyManager, String storeId) {
        this.pharmacyManager = pharmacyManager;
        this.storeId = storeId;
    }

    public void generateAnalyticsReport() {
        String fileName = "./src/main/java/store/data/" + storeId + "_pharmacy_report_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".txt";
            
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("Pharmacy Analytics Report\n");
            writer.write("Generated: " + LocalDateTime.now() + "\n\n");
            
            writer.write("Inventory Status:\n");
            for (Medication med : pharmacyManager.getAllMedications()) {
                writer.write(String.format("%s: %d units in stock (Min: %d)\n",
                    med.getName(), med.getStockLevel(), med.getMinimumStockLevel()));
            }
            
            writer.write("\nExpiring Medications:\n");
            pharmacyManager.getExpiringMedications().forEach(med -> {
                try {
                    writer.write(String.format("%s expires on %s\n",
                        med.getName(), med.getExpirationDate()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }
}