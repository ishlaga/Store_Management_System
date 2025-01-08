package returns.model;

import inventory.model.Product;

/**
 * Model class representing an individual item in a return request.
 *
 * @author Hrishikesha
 */
public class ReturnItem {
    private Product product;      // Product being returned
    private int quantity;         // Quantity being returned
    private boolean damaged;      // Damage status
    private double refundAmount;  // Refund amount for this item

    /**
     * Creates a new return item
     * @param product Product being returned
     * @param quantity Quantity being returned
     * @param damaged Whether the item is damaged
     */
    public ReturnItem(Product product, int quantity, boolean damaged) {
        this.product = product;
        this.quantity = quantity;
        this.damaged = damaged;
        this.refundAmount = calculateRefund();
    }

    private double calculateRefund() {
        return product.getPrice() * quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isDamaged() {
        return damaged;
    }

    public Product getProduct() {
        return product;
    }

    public double getRefundAmount() {
        return refundAmount;
    }
}
