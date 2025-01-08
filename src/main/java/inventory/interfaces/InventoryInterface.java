package inventory.interfaces;

import inventory.model.Product;
import java.util.List;

/**
 * Interface defining the core inventory management operations
 * that must be implemented by inventory managers.
 *
 * @author Akhilesh Nevatia
 */
public interface InventoryInterface {
    /**
     * Updates the stock levels in the inventory
     * @return boolean indicating update success
     */
    boolean updateStock();

    /**
     * Checks for products with low stock levels
     * @return List of products that are low in stock
     */
    List<Product> checkLowStock();

    /**
     * Tracks products nearing expiration
     * @return List of products expiring within 30 days
     */
    List<Product> trackExpiry();

    /**
     * Removes obsolete products from inventory
     * @return boolean indicating removal success
     */
    boolean removeObsoleteProducts(List<Product> productsToRemove);
}