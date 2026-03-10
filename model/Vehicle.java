package model;

public class Vehicle {

    private int id;
    private String plate;
    private double capacityKg;
    private VehicleStatus vehicleStatus;

    // Constructor
    public Vehicle(int id, String plate, double capacityKg) {
        this.id = id;
        this.plate = plate;
        this.capacityKg = capacityKg;
        this.vehicleStatus = VehicleStatus.AVAILABLE;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getPlate() {
        return plate;
    }

    public double getCapacityKg() {
        return capacityKg;
    }

    public VehicleStatus getVehicleStatus() {
        return vehicleStatus;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public void setCapacityKg(double capacityKg) {
        this.capacityKg = capacityKg;
    }

    public void setVehicleStatus(VehicleStatus vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

}
