```mermaid
classDiagram
    class HeadOffice {
        -String companyId
        -String name
        +addNewStore()
        +removeStore()
        +launchDonationProgram()
        +generateLeaderboard()
        +generateReports()
    }

    class Store {
        -String storeId
        -String location
        -OperatingHours hours
        +getStoreDetails()
        +processHomeDelivery()
        +manageSelfCheckout()
        +hostDonationEvent()
    }

    class Employee {
        -String employeeId
        -String name
        -String role
        -Schedule schedule
        +clockIn()
        +clockOut()
        +takeBreak()
    }

    class InventoryManager {
        -String managerId
        -String storeId
        +prepareInitialInventory()
        +finalizeInventoryForClosure()
        +trackSurplusInventory()
    }

    class StoreManager {
        -String managerId
        -String storeId
        +overseeOperations()
        +managePharmacy()
        +manageGasStation()
        +coordinateDonation()
    }

    class Customer {
        -String customerId
        -String contactInfo
        +provideDeliveryDetails()
        +redeemLoyaltyPoints()
    }

    class DeliveryStaff {
        -String staffId
        -DeliverySchedule schedule
        +deliverGoods()
        +updateDeliveryStatus()
    }

    class Security {
        -String securityId
        -Alert[] alerts
        +monitorSelfCheckout()
        +trackParkingLotActivity()
    }

    class PaymentProcessor {
        +authorizePayment()
        +handleRefunds()
    }

    class CharityOrganization {
        -String orgId
        -String name
        -String contactInfo
        +confirmReceipt()
        +provideFeedback()
    }

    class LoyaltySystem {
        -String loyaltyId
        -int points
        +trackPoints()
        +redeemRewards()
        +sendExpiryNotifications()
    }

    class Pharmacy {
        -String pharmacyId
        -Medication[] medications
        +processPrescription()
        +manageInventory()
    }

    class GasStation {
        -String stationId
        -double fuelStock
        +manageRefueling()
        +trackFuelStock()
    }

    class ParkingLot {
        -String lotId
        -int availableSpaces
        +trackSpaceAvailability()
        +monitorCartReturns()
    }

    HeadOffice "1" --> "*" Store : manages
    HeadOffice "1" --> "*" CharityOrganization : partners with
    HeadOffice "1" --> "1" InventoryManager : oversees
    HeadOffice "1" --> "1" LoyaltySystem : manages
    HeadOffice "1" --> "*" StoreManager : directs

    Store "1" --> "1" InventoryManager : supported by
    Store "1" --> "*" Employee : employs
    Store "1" --> "*" DeliveryStaff : assigns
    Store "1" --> "1" Security : implements
    Store "1" --> "1" Pharmacy : hosts
    Store "1" --> "1" GasStation : operates
    Store "1" --> "1" ParkingLot : manages

    InventoryManager "1" --> "*" Supplier : orders from
    StoreManager "1" --> "1" Pharmacy : supervises
    StoreManager "1" --> "1" GasStation : monitors
    StoreManager "1" --> "1" CharityOrganization : coordinates with

    Customer "1" --> "1" LoyaltySystem : engages with
    DeliveryStaff "1" --> "*" Customer : delivers to
    Security "1" --> "*" Store : protects
    PaymentProcessor "1" --> "*" Store : integrates with

    Pharmacy "1" --> "*" Customer : serves
    GasStation "1" --> "*" Customer : provides fuel to
    ParkingLot "1" --> "*" Customer : accommodates