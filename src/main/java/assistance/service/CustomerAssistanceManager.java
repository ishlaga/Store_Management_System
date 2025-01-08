package assistance.service;

import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import assistance.model.AssistanceRequest;
import security.service.SecurityManager;
import inventory.service.InventoryManager;
import inventory.service.HeadOfficeManager;

public class CustomerAssistanceManager {
    private static final String ASSISTANCE_LOGS = "src/main/java/assistance/data/assistance_logs.txt";
    private static final String RESTRICTED_PRODUCTS = "src/main/java/assistance/data/restricted_products.txt";
    private static final String AUTHORIZED_ASSOCIATES = "src/main/java/assistance/data/authorized_associates.txt";
    private static final String STORES_FILE = "src/main/java/store/data/stores.txt";
    
    private List<AssistanceRequest> activeRequests;
    private Map<String, String> restrictedProducts;
    private Map<String, Set<String>> associateAuthorizations;
    private Scanner scanner;
    private String storeId;
    private String currentAssociateId;
    private DateTimeFormatter formatter;
    private InventoryManager inventoryManager;

    public CustomerAssistanceManager(String storeId) {
        System.out.println("Initializing CustomerAssistanceManager for store: " + storeId);
        this.storeId = storeId;
        this.activeRequests = new ArrayList<>();
        this.restrictedProducts = new HashMap<>();
        this.associateAuthorizations = new HashMap<>();
        this.scanner = new Scanner(System.in);
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.inventoryManager = new InventoryManager(new HeadOfficeManager());
        loadData();
        System.out.println("Initialization complete. Authorized associates: " + associateAuthorizations.keySet());
    }

    private void loadData() {
        System.out.println("Loading data files...");
        loadRestrictedProducts();
        loadAuthorizedAssociates();
        loadActiveRequests();
        System.out.println("Data loading complete");
    }

    private void loadAuthorizedAssociates() {
        System.out.println("Loading authorized associates...");
    
    // Hardcode CSA001 authorizations
        Set<String> csa001Categories = new HashSet<>(Arrays.asList("ELECTRONICS", "VALUABLES"));
    associateAuthorizations.put("CSA001", csa001Categories);
    
    // Hardcode CSA002 authorizations
    Set<String> csa002Categories = new HashSet<>(Arrays.asList("ALCOHOL", "TOBACCO"));
    associateAuthorizations.put("CSA002", csa002Categories);
    
    System.out.println("Loaded " + associateAuthorizations.size() + " authorized associates");
    System.out.println("Authorized associates: " + associateAuthorizations.keySet());
    }

    public void startAssistance() {
        System.out.print("Enter Associate ID: ");
        currentAssociateId = scanner.nextLine().trim();
        
        System.out.println("Checking authorization for: " + currentAssociateId);
        System.out.println("Available authorizations: " + associateAuthorizations.keySet());
        
        if (!associateAuthorizations.containsKey(currentAssociateId)) {
            System.out.println("Unauthorized associate. Access denied.");
            return;
        }

        System.out.println("Associate " + currentAssociateId + " authorized. Access granted.");
        System.out.println("Authorized categories: " + associateAuthorizations.get(currentAssociateId));

        while (true) {
            System.out.println("\nCustomer Assistance System");
            System.out.println("1. New Assistance Request (Customer Service)");
            System.out.println("2. View Active Requests (Service Manager)");
            System.out.println("3. Handle Restricted Product Access (Service Manager)");
            System.out.println("4. Complete Request (Service Manager)");
            System.out.println("5. View Request History (Service Manager)");
            System.out.println("6. Return to Main Menu");
            System.out.print("Choose an option: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1: createNewRequest(); break;
                    case 2: viewActiveRequests(); break;
                    case 3: handleRestrictedAccess(); break;
                    case 4: completeRequest(); break;
                    case 5: viewRequestHistory(); break;
                    case 6: return;
                    default: System.out.println("Invalid option");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    private void createNewRequest() {
        System.out.print("Enter customer name: ");
        String customerName = scanner.nextLine();
        
        System.out.println("Request types: LOCATION, RESTRICTED, GENERAL");
        System.out.print("Enter request type: ");
        String requestType = scanner.nextLine().toUpperCase();
        
        System.out.print("Enter product ID (or press Enter if none): ");
        String productId = scanner.nextLine();
        if (productId.isEmpty()) productId = "NONE";
        
        System.out.print("Enter request notes: ");
        String notes = scanner.nextLine();

        AssistanceRequest request = new AssistanceRequest(
            customerName, requestType, storeId, productId, notes);
        activeRequests.add(request);
        saveRequest(request);
        
        System.out.println("Request created with ID: " + request.getId());
    }

    private void handleRestrictedAccess() {
        System.out.print("Enter request ID: ");
        String requestId = scanner.nextLine();
        
        AssistanceRequest request = findRequest(requestId);
        if (request == null) {
            System.out.println("Request not found");
            return;
        }

        if (!request.getRequestType().equals("RESTRICTED")) {
            System.out.println("This is not a restricted access request");
            return;
        }

        String productCategory = restrictedProducts.get(request.getProductId());
        if (productCategory == null) {
            System.out.println("Product is not restricted");
            return;
        }

        Set<String> authorizedCategories = associateAuthorizations.get(currentAssociateId);
        if (authorizedCategories.contains(productCategory)) {
            request.setStatus("AUTHORIZED");
            saveRequest(request);
            System.out.println("Access authorized for restricted product");
        } else {
            System.out.println("Associate not authorized for this category");
        }
    }

    private void completeRequest() {
        System.out.print("Enter request ID: ");
        String requestId = scanner.nextLine();
        
        AssistanceRequest request = findRequest(requestId);
        if (request == null) {
            System.out.println("Request not found");
            return;
        }

        System.out.print("Enter completion notes: ");
        String completionNotes = scanner.nextLine();
        
        request.setStatus("COMPLETED");
        request.setNotes(request.getNotes() + " | Completed: " + completionNotes);
        saveRequest(request);
        activeRequests.remove(request);
        
        System.out.println("Request marked as completed");
    }

    private AssistanceRequest findRequest(String requestId) {
        return activeRequests.stream()
            .filter(r -> r.getId().equals(requestId))
            .findFirst()
            .orElse(null);
    }

    private void loadRestrictedProducts() {
        try (BufferedReader reader = new BufferedReader(new FileReader(RESTRICTED_PRODUCTS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    restrictedProducts.put(parts[0].trim(), parts[2].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading restricted products: " + e.getMessage());
        }
    }

    private void loadActiveRequests() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ASSISTANCE_LOGS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 8 && parts[6].equals("PENDING") && parts[1].equals(storeId)) {
                    AssistanceRequest request = new AssistanceRequest(
                        parts[0], // id
                        parts[3], // customerName
                        parts[4], // requestType
                        parts[1], // storeId
                        parts[5], // productId
                        parts[6], // status
                        parts[7], // notes
                        LocalDateTime.parse(parts[2], formatter)
                    );
                    activeRequests.add(request);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading assistance logs: " + e.getMessage());
        }
    }

    private void viewActiveRequests() {
        if (activeRequests.isEmpty()) {
            System.out.println("No active requests");
            return;
        }
        System.out.println("\nActive Requests:");
        for (AssistanceRequest request : activeRequests) {
            System.out.printf("ID: %s, Customer: %s, Type: %s, Status: %s%n",
                request.getId(),
                request.getCustomerName(),
                request.getRequestType(),
                request.getStatus());
        }
    }

    private void viewRequestHistory() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ASSISTANCE_LOGS))) {
            System.out.println("\nRequest History:");
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 8 && parts[1].equals(storeId)) {
                    System.out.printf("ID: %s, Customer: %s, Type: %s, Status: %s, Notes: %s%n",
                        parts[0], parts[3], parts[4], parts[6], parts[7]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading assistance logs: " + e.getMessage());
        }
    }

    private void saveRequest(AssistanceRequest request) {
        try (FileWriter writer = new FileWriter(ASSISTANCE_LOGS, true)) {
            writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s%n",
                request.getId(),
                request.getStoreId(),
                request.getTimestamp().format(formatter),
                request.getCustomerName(),
                request.getRequestType(),
                request.getProductId(),
                request.getStatus(),
                request.getNotes()
            ));
        } catch (IOException e) {
            System.err.println("Error saving request: " + e.getMessage());
        }
    }
}