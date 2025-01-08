package inventory.interfaces;

/**
 * Interface defining the contract for head office operations
 * in the inventory management system.
 *
 * @author Akhilesh Nevatia
 */
public interface HeadOfficeInterface {
    /**
     * Approves inventory changes
     * @return boolean indicating approval status
     */
    boolean approveChanges();

    /**
     * Reviews store performance
     * @return boolean indicating review completion
     */
    boolean reviewPerformance();

    /**
     * Manages store operations
     * @return boolean indicating management success
     */
    boolean manageStores();
}