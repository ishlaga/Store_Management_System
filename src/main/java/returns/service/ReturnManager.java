package returns.service;

import returns.model.Return;
import returns.model.ReturnItem;
import inventory.model.Product;
import inventory.service.InventoryManager;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Service class that manages return operations and inventory updates
 * for returned products.
 *
 * @author Hrishikesha
 */
public class ReturnManager {
    private Map<String, Return> returns;
    private String storeId;
    private InventoryManager inventoryManager;

    /**
     * Creates a new return manager for a store
     * @param storeId Store identifier
     * @param inventoryManager Inventory manager instance
     */
    public ReturnManager(String storeId, InventoryManager inventoryManager) {
        this.storeId = storeId;
        this.inventoryManager = inventoryManager;
        this.returns = new HashMap<>();
    }

    public Map<String, Return> getPendingReturns() {
        return returns.entrySet().stream()
            .filter(entry -> !entry.getValue().isApproved())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Return> getAllReturns() {
        return new HashMap<>(returns);
    }

    public boolean processReturn(Return returnOrder) {
        returns.put(returnOrder.getReturnId(), returnOrder);
        return true;
    }

    public void approveReturn(String returnId) {
        Return ret = returns.get(returnId);
        if (ret != null) {
            ret.setApproved(true);
        }
    }

    public boolean processApprovedReturns() {
        boolean anyUpdates = false;
        for (Return ret : returns.values()) {
            if (ret.isApproved()) {
                anyUpdates = true;
            }
        }
        return anyUpdates;
    }

    public void printReturnReceipt(Return returnOrder) {
        String fileName = "./src/main/java/store/data/receipts/" + 
                         returnOrder.getReturnId() + "_receipt.txt";
        
        // Create receipts directory if it doesn't exist
        new File("./src/main/java/store/data/receipts").mkdirs();
        
        // Format the receipt content
        StringBuilder receipt = new StringBuilder();
        receipt.append("=================================================\n");
        receipt.append("                 RETURN RECEIPT                   \n");
        receipt.append("=================================================\n\n");
        receipt.append(String.format("Return ID: %s\n", returnOrder.getReturnId()));
        receipt.append(String.format("Original Order ID: %s\n", returnOrder.getOriginalOrderId()));
        receipt.append(String.format("Date: %s\n", returnOrder.getReturnDate().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        receipt.append("\nItems Returned:\n");
        receipt.append("--------------------------------------------------\n");
        
        for (Map.Entry<Product, ReturnItem> entry : returnOrder.getItems().entrySet()) {
            Product product = entry.getKey();
            ReturnItem item = entry.getValue();
            receipt.append(String.format("%-30s x%d\n", product.getName(), item.getQuantity()));
            receipt.append(String.format("  Unit Price: $%-8.2f", product.getPrice()));
            receipt.append(String.format("  Subtotal: $%-8.2f\n", 
                product.getPrice() * item.getQuantity()));
            if (item.isDamaged()) {
                receipt.append("  (Damaged Item)\n");
            }
            receipt.append("\n");
        }
        
        receipt.append("--------------------------------------------------\n");
        receipt.append(String.format("Total Refund Amount: $%.2f\n", returnOrder.getRefundAmount()));
        receipt.append(String.format("Refund Method: %s\n", returnOrder.getRefundMethod()));
        receipt.append("\nStatus: Pending Approval\n");
        receipt.append("\nThank you for shopping with us!\n");
        receipt.append("=================================================\n");
        
        // Print to console
        System.out.println(receipt.toString());
        
        // Save to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(receipt.toString());
            System.out.println("\nReceipt saved to: " + fileName);
        } catch (IOException e) {
            System.err.println("Error saving receipt: " + e.getMessage());
        }
    }
}