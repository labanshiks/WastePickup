import model.*;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        // Create a Caretaker
        Caretaker caretaker = new Caretaker(1, "john_doe", "pass123", "0712345678");

        // Create an Apartment and assign the Caretaker
        Apartment apartment = new Apartment(1, "Greenpark Apartments",
                "Kilimani", "Ngong Road", 24, caretaker);

        // Create a Vehicle
        Vehicle vehicle = new Vehicle(1, "KCA 123A", 500.0);

        // Create a PickupRequest
        PickupRequest request = new PickupRequest(1, apartment,
                WasteCategory.PLASTIC, 50.0,
                LocalDate.now(),
                LocalDate.now().plusDays(3));

        // Create a Schedule and add the request
        Schedule schedule = new Schedule(1, LocalDate.now(),
                "8AM - 10AM", "Kilimani Morning Route", vehicle);
        schedule.addRequest(request);

        // Print everything out
        System.out.println("=== WASTE PICKUP SYSTEM TEST ===");
        System.out.println("Caretaker: " + caretaker.getUsername());
        System.out.println("Apartment: " + apartment.getName());
        System.out.println("Location: " + apartment.getEstate());
        System.out.println("Vehicle: " + vehicle.getPlate());
        System.out.println("Vehicle Status: " + vehicle.getVehicleStatus());
        System.out.println("Request Category: " + request.getCategory());
        System.out.println("Request Status: " + request.getStatus());
        System.out.println("Schedule Route: " + schedule.getRouteName());
        System.out.println("Total Weight: " + schedule.getTotalWeight() + "kg");
        System.out.println("Vehicle Capacity: " + vehicle.getCapacityKg() + "kg");
        System.out.println("Can add more requests: " +
                (schedule.getTotalWeight() < vehicle.getCapacityKg()));
    }
}