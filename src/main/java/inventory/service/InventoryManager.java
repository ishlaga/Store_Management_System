/**
 * Service class that manages all inventory-related operations including
 * stock management, product tracking, and file operations.
 *
 * @author Akhilesh Nevatia
 */
package inventory.service;

import inventory.interfaces.InventoryInterface;
import inventory.model.Product;
import store.model.Store;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class InventoryManager implements InventoryInterface {
    // Map of store inventories: storeId -> (productId -> Product)
    private Map<String, Map<String, Product>> storeInventories;
    private HeadOfficeManager headOffice;
    private String currentStoreId;

    /**
     * Loads store inventory from files and initializes the manager
     * @param headOffice The head office manager instance for approvals
     */
    public InventoryManager(HeadOfficeManager headOffice) {
        this.storeInventories = new HashMap<>();
        this.headOffice = headOffice;
        loadStoresAndInventory();
    }

    public void setCurrentStore(String storeId) {
        this.currentStoreId = storeId;
        if (!storeInventories.containsKey(storeId)) {
            storeInventories.put(storeId, new HashMap<>());
        }
    }

    private void loadStoresAndInventory() {
        // Load from stores.txt and their respective inventory files
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/store/data/stores.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String storeId = parts[0];
                loadStoreInventory(storeId);
            }
        } catch (IOException e) {
            System.err.println("Error loading stores: " + e.getMessage());
        }
    }

    private void loadStoreInventory(String storeId) {
        String fileName = "src/main/java/store/data/" + storeId + "_inventory.txt";
        Map<String, Product> storeInventory = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    Product product = new Product(
                        parts[0],  // id
                        parts[1],  // name
                        Double.parseDouble(parts[2]),  // price
                        Integer.parseInt(parts[3]),    // stockLevel
                        parts[4],  // supplier
                        LocalDate.parse(parts[5])      // expirationDate
                    );
                    storeInventory.put(product.getId(), product);
                }
            }
        } catch (IOException e) {
            System.err.println("Creating new inventory file for store: " + storeId);
        }
        
        storeInventories.put(storeId, storeInventory);
    }

    public Map<String, Product> getCurrentInventory() {
        return storeInventories.getOrDefault(currentStoreId, new HashMap<>());
    }

    // Modify other methods to use currentStoreId
    @Override
    public boolean updateStock() {
        saveInventoryToFile(currentStoreId);
        return true;
    }

    @Override
    public List<Product> checkLowStock() {
        return getCurrentInventory().values().stream()
            .filter(p -> p.getStockLevel() < 5 && !p.isObsolete())
            .collect(Collectors.toList());
    }

    @Override
    public List<Product> trackExpiry() {
        LocalDate warningDate = LocalDate.now().plusMonths(1);
        return getCurrentInventory().values().stream()
            .filter(p -> p.getExpirationDate() != null && 
                        p.getExpirationDate().isBefore(warningDate) &&
                        !p.isObsolete())
            .collect(Collectors.toList());
    }

    @Override
    public boolean removeObsoleteProducts(List<Product> productsToRemove) {
        // Mark selected products as obsolete and remove them
        productsToRemove.forEach(product -> {
            product.markAsObsolete();
            getCurrentInventory().remove(product.getId());
        });
        return updateStock();
    }

    // Add this new method to get expired products
    public List<Product> getExpiredProducts() {
        LocalDate today = LocalDate.now();
        return getCurrentInventory().values().stream()
                .filter(product -> product.getExpirationDate().isBefore(today))
                .collect(Collectors.toList());
    }

    public Product getProduct(String productId) {
        return getCurrentInventory().get(productId);
    }

    // Additional methods for the main success scenario
    public boolean addNewProduct(Product product) {
        if (getCurrentInventory().containsKey(product.getId())) {
            return false;
        }
        if (headOffice.approveChanges()) {
            getCurrentInventory().put(product.getId(), product);
            return updateStock();
        }
        return false;
    }

    public boolean updateProductPrice(String productId, double newPrice) {
        Product product = getCurrentInventory().get(productId);
        if (product != null && headOffice.approveChanges()) {
            product.setPrice(newPrice);
            return updateStock();
        }
        return false;
    }

    public boolean restockProduct(String productId, int additionalQuantity) {
        Product product = getCurrentInventory().get(productId);
        if (product != null) {
            product.setStockLevel(product.getStockLevel() + additionalQuantity);
            return updateStock();
        }
        return false;
    }

    // File operations
    private void saveInventoryToFile(String storeId) {
        String fileName = "src/main/java/store/data/" + storeId + "_inventory.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Product product : getCurrentInventory().values()) {
                writer.write(String.format("%s,%s,%.2f,%d,%s,%s\n",
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getStockLevel(),
                    product.getSupplier(),
                    product.getExpirationDate()
                ));
            }
        } catch (IOException e) {
            System.err.println("Error saving inventory: " + e.getMessage());
        }
    }

    public Map<String, Product> getAllProducts() {
        return new HashMap<>(getCurrentInventory());
    }

}