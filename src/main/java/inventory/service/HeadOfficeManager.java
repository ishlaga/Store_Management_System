package inventory.service;

import inventory.interfaces.HeadOfficeInterface;

/**
 * Service class that handles head office operations and approvals
 * for inventory management decisions.
 *
 * @author Akhilesh Nevatia
 */
public class HeadOfficeManager implements HeadOfficeInterface {
    /**
     * Approves changes to inventory
     * @return boolean indicating if changes are approved
     */
    @Override
    public boolean approveChanges() {
        return true;
    }

    /**
     * Reviews store performance
     * @return boolean indicating if performance review is complete
     */
    @Override
    public boolean reviewPerformance() {
        return true;
    }

    /**
     * Manages store operations
     * @return boolean indicating if store management is successful
     */
    @Override
    public boolean manageStores() {
        return true;
    }
}
