/**
 * Service class that manages supplier relationships, shipments,
 * and inventory updates from suppliers.
 *
 * @author Eshant Chintareddy
 */
package supplier.service;

import supplier.model.Supplier;
import supplier.model.Shipment;
import inventory.service.InventoryManager;
import inventory.model.Product;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SupplierManager {
    // Map of all suppliers
    private Map<String, Supplier> suppliers;
    // Map of shipments per store
    private Map<String, List<Shipment>> storeShipments;
    // Manager for inventory operations
    private InventoryManager inventoryManager;
    // Current store being managed
    private String currentStoreId;

    /**
     * Creates a new supplier manager with inventory management capabilities
     * @param inventoryManager The inventory manager instance
     */
    public SupplierManager(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
        this.suppliers = new HashMap<>();
        this.storeShipments = new HashMap<>();
        loadSuppliers();
    }

    public void setCurrentStore(String storeId) {
        this.currentStoreId = storeId;
        if (!storeShipments.containsKey(storeId)) {
            storeShipments.put(storeId, new ArrayList<>());
        }
    }

    public boolean supplierExists(String supplierId) {
        return suppliers.containsKey(supplierId);
    }

    public String getSupplierName(String supplierId) {
        Supplier supplier = suppliers.get(supplierId);
        return supplier != null ? supplier.getName() : null;
    }

    public Map<String, Double> getAllSupplierRatings() {
        Map<String, Double> ratings = new HashMap<>();
        for (Map.Entry<String, Supplier> entry : suppliers.entrySet()) {
            ratings.put(entry.getKey(), entry.getValue().getRating());
        }
        return ratings;
    }

    public boolean addItemToShipment(Shipment shipment, String productId, int quantity) {
        Product product = inventoryManager.getProduct(productId);
        if (product != null) {
            shipment.addItem(product, quantity);
            return true;
        }
        return false;
    }

    public boolean confirmShipment(Shipment shipment) {
        shipment.setStatus("CONFIRMED");
        return true;
    }

    public Shipment getShipment(String shipmentId) {
        return storeShipments.get(currentStoreId).stream()
                .filter(s -> s.getId().equals(shipmentId))
                .findFirst()
                .orElse(null);
    }

    public List<Shipment> getPendingShipments() {
        return storeShipments.get(currentStoreId).stream()
                .filter(s -> !s.getStatus().equals("VERIFIED"))
                .collect(Collectors.toList());
    }

    private void loadSuppliers() {
        try (BufferedReader reader = new BufferedReader(new FileReader("./src/main/java/supplier/data/suppliers.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                suppliers.put(parts[0], new Supplier(parts[0], parts[1], parts[2], parts[3]));
            }
        } catch (IOException e) {
            System.err.println("Error loading suppliers: " + e.getMessage());
        }
    }

    public Shipment createShipment(String supplierId, LocalDateTime scheduledDate) {
        if (!suppliers.containsKey(supplierId)) {
            return null;
        }
        String shipmentId = "SH" + System.currentTimeMillis();
        Shipment shipment = new Shipment(shipmentId, currentStoreId, supplierId, scheduledDate);
        storeShipments.get(currentStoreId).add(shipment);
        return shipment;
    }

    public boolean verifyShipment(String shipmentId, Map<String, Integer> receivedQuantities) {
        Shipment shipment = getShipment(shipmentId);
        if (shipment == null) return false;

        boolean hasDiscrepancies = false;
        for (Map.Entry<Product, Integer> entry : shipment.getItems().entrySet()) {
            Product product = entry.getKey();
            int expectedQty = entry.getValue();
            int receivedQty = receivedQuantities.getOrDefault(product.getId(), 0);
            
            if (receivedQty != expectedQty) {
                shipment.recordDiscrepancy(product.getId(), receivedQty - expectedQty);
                hasDiscrepancies = true;
            }
            
            // Update inventory regardless of discrepancy
            inventoryManager.restockProduct(product.getId(), receivedQty);
        }

        shipment.setStatus("VERIFIED");
        shipment.setDeliveryDate(LocalDateTime.now());
        
        // Update supplier rating
        Supplier supplier = suppliers.get(shipment.getSupplierId());
        supplier.updatePerformance(hasDiscrepancies);
        
        return !hasDiscrepancies;
    }
}