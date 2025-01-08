package store.model;

import java.time.LocalDate;

public class Store {
    private String storeId;
    private String name;
    private String location;
    private LocalDate openingDate;

    public Store(String storeId, String name, String location, LocalDate openingDate) {
        this.storeId = storeId;
        this.name = name;
        this.location = location;
        this.openingDate = openingDate;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    // Additional getters and setters as needed
} 