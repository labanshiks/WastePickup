package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PickupRequest {

    private int id;
    private Apartment apartment;
    private WasteCategory category;
    private double estimatedWeightKg;
    private LocalDate preferredFrom;
    private LocalDate preferredTo;
    private RequestStatus status;
    private LocalDateTime createdAt;

    // Constructor
    public PickupRequest(int id, Apartment apartment, WasteCategory category,
            double estimatedWeightKg, LocalDate preferredFrom,
            LocalDate preferredTo) {
        this.id = id;
        this.apartment = apartment;
        this.category = category;
        this.estimatedWeightKg = estimatedWeightKg;
        this.preferredFrom = preferredFrom;
        this.preferredTo = preferredTo;
        this.status = RequestStatus.PENDING; // Always starts as PENDING
        this.createdAt = LocalDateTime.now(); // Automatically set to now
    }

    // Getters
    public int getId() {
        return id;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public WasteCategory getCategory() {
        return category;
    }

    public double getEstimatedWeightKg() {
        return estimatedWeightKg;
    }

    public LocalDate getPreferredFrom() {
        return preferredFrom;
    }

    public LocalDate getPreferredTo() {
        return preferredTo;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public void setCategory(WasteCategory category) {
        this.category = category;
    }

    public void setEstimatedWeightKg(double estimatedWeightKg) {
        this.estimatedWeightKg = estimatedWeightKg;
    }

    public void setPreferredFrom(LocalDate preferredFrom) {
        this.preferredFrom = preferredFrom;
    }

    public void setPreferredTo(LocalDate preferredTo) {
        this.preferredTo = preferredTo;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}