package model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Schedule {

    private int id;
    private LocalDate pickupDate;
    private String timeSlot;
    private String routeName;
    private Vehicle vehicle;
    private ScheduleStatus status;
    private ArrayList<PickupRequest> requests;

    // Constructor
    public Schedule(int id, LocalDate pickupDate, String timeSlot,
            String routeName, Vehicle vehicle) {
        this.id = id;
        this.pickupDate = pickupDate;
        this.timeSlot = timeSlot;
        this.routeName = routeName;
        this.vehicle = vehicle;
        this.status = ScheduleStatus.PENDING;
        this.requests = new ArrayList<>(); // Start with empty list
    }

    // Getters
    public int getId() {
        return id;
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public String getRouteName() {
        return routeName;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ScheduleStatus getStatus() {
        return status;
    }

    public ArrayList<PickupRequest> getRequests() {
        return requests;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void setStatus(ScheduleStatus status) {
        this.status = status;
    }

    // Special methods for managing requests list
    public void addRequest(PickupRequest request) {
        requests.add(request);
    }

    public void removeRequest(PickupRequest request) {
        requests.remove(request);
    }

    // Calculate total weight of all requests in this schedule
    public double getTotalWeight() {
        double total = 0;
        for (PickupRequest request : requests) {
            total += request.getEstimatedWeightKg();
        }
        return total;
    }
}