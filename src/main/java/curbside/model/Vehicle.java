package curbside.model;

/**
 * Represents a customer's vehicle for curbside pickup.
 * Stores vehicle details to help staff identify customers
 * when they arrive for their curbside pickup orders.
 *
 * @author Hrishikesha Kyathsandra
 */
public class Vehicle {
    private String make;
    private String model;
    private String color;
    private String licensePlate;

    public Vehicle(String make, String model, String color, String licensePlate) {
        this.make = make;
        this.model = model;
        this.color = color;
        this.licensePlate = licensePlate;
    }

    // Getters
    public String getMake() { return make; }
    public String getModel() { return model; }
    public String getColor() { return color; }
    public String getLicensePlate() { return licensePlate; }
}