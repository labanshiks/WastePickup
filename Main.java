import dao.ScheduleDAO;
import dao.VehicleDAO;
import model.Schedule;
import model.Vehicle;
import util.DBConnection;
import java.time.LocalDate;
import java.util.List;

public class Main {
        public static void main(String[] args) {

                ScheduleDAO scheduleDAO = new ScheduleDAO();
                VehicleDAO vehicleDAO = new VehicleDAO();

                // Test create
                System.out.println("=== CREATE SCHEDULE ===");
                Vehicle vehicle = vehicleDAO.findById(1);
                Schedule newSchedule = new Schedule(
                                0,
                                LocalDate.of(2025, 4, 1),
                                "8AM - 10AM",
                                "Kilimani Morning Route",
                                vehicle);
                boolean created = scheduleDAO.create(newSchedule);
                System.out.println("Schedule created: " + created);

                // Test findAll
                System.out.println("\n=== ALL SCHEDULES ===");
                List<Schedule> schedules = scheduleDAO.findAll();
                for (Schedule s : schedules) {
                        System.out.println(
                                        "ID: " + s.getId() +
                                                        " | Route: " + s.getRouteName() +
                                                        " | Date: " + s.getPickupDate() +
                                                        " | Status: " + s.getStatus());
                }

                DBConnection.closeConnection();
        }
}