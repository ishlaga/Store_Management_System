package store.service;

import store.model.Store;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
public class StoreManager {
    private List<Store> stores;
    private static final String STORES_FILE = "src/main/java/store/data/stores.txt";
    private static final String MANAGERS_FILE = "src/main/java/store/data/storemanagers.txt";

    public StoreManager() {
        this.stores = new ArrayList<>();
        loadStores();
    }

    private void loadStores() {
        try (BufferedReader br = new BufferedReader(new FileReader(STORES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue; // Skip invalid lines
                String storeId = parts[0].trim();
                String name = parts[1].trim();
                String location = parts[2].trim();
                LocalDate openingDate = LocalDate.parse(parts[3].trim());
                stores.add(new Store(storeId, name, location, openingDate));
            }
        } catch (IOException e) {
            System.out.println("Error loading stores: " + e.getMessage());
        }
    }

    public List<Store> getAllStores() {
        return stores;
    }

    public void addStore(Store newStore) {
        stores.add(newStore);
        appendStoreToFile(newStore);
        System.out.println("Store added successfully: " + newStore.getStoreId());
    }

    private void appendStoreToFile(Store store) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(STORES_FILE, true))) {
            String storeEntry = String.format("%s,%s,%s,%s",
                    store.getStoreId(),
                    store.getName(),
                    store.getLocation(),
                    store.getOpeningDate().toString());
            bw.newLine();
            bw.write(storeEntry);
        } catch (IOException e) {
            System.out.println("Error writing to stores file: " + e.getMessage());
        }
    }

    public boolean removeStore(String storeId) {
        boolean removed = stores.removeIf(store -> store.getStoreId().equals(storeId));
        if (removed) {
            rewriteStoresFile();
            removeManager(storeId);
        }
        return removed;
    }

    private void rewriteStoresFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(STORES_FILE))) {
            for (Store store : stores) {
                String storeEntry = String.format("%s,%s,%s,%s",
                        store.getStoreId(),
                        store.getName(),
                        store.getLocation(),
                        store.getOpeningDate().toString());
                bw.write(storeEntry);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error rewriting stores file: " + e.getMessage());
        }
    }

    public Store getStoreById(String storeId) {
        return stores.stream()
                .filter(store -> store.getStoreId().equals(storeId))
                .findFirst()
                .orElse(null);
    }

    // Store Managers Management
    public String getManagerByStoreId(String storeId) {
        try (BufferedReader br = new BufferedReader(new FileReader(MANAGERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue;
                if (parts[0].trim().equals(storeId)) {
                    return parts[1].trim();
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading managers file: " + e.getMessage());
        }
        return null;
    }

    public void addManager(String storeId, String managerName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MANAGERS_FILE, true))) {
            String managerEntry = String.format("%s,%s", storeId, managerName);
            bw.write(managerEntry);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to managers file: " + e.getMessage());
        }
    }

    private void removeManager(String storeId) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(MANAGERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(storeId + ",")) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading managers file: " + e.getMessage());
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MANAGERS_FILE))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error rewriting managers file: " + e.getMessage());
        }
    }

    // Generate a unique Store ID
    public String generateUniqueStoreId() {
        int maxId = 0;
        for (Store store : stores) {
            String idNumber = store.getStoreId().replaceAll("[^0-9]", "");
            try {
                int num = Integer.parseInt(idNumber);
                if (num > maxId) {
                    maxId = num;
                }
            } catch (NumberFormatException e) {
                // Ignore invalid format
            }
        }
        return String.format("ST%03d", maxId + 1);
    }
}
