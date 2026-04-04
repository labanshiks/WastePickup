package dao;

import model.Schedule;
import model.ScheduleStatus;
import model.Vehicle;
import model.PickupRequest;
import dao.VehicleDAO;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO implements DaoInterface<Schedule> {

    private Connection connection;
    private VehicleDAO vehicleDAO = new VehicleDAO();

    public ScheduleDAO() {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public boolean create(Schedule schedule) {
        String sql = "INSERT INTO schedules (pickup_date, time_slot, " +
                "route_name, vehicle_id, status) " +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            // RETURN_GENERATED_KEYS lets us get the new schedule's id
            PreparedStatement stmt = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setDate(1, Date.valueOf(schedule.getPickupDate()));
            stmt.setString(2, schedule.getTimeSlot());
            stmt.setString(3, schedule.getRouteName());
            stmt.setInt(4, schedule.getVehicle() != null ? schedule.getVehicle().getId() : 0);
            stmt.setString(5, schedule.getStatus().name());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Get the auto generated schedule id
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int scheduleId = generatedKeys.getInt(1);

                    // Insert each request into schedule_requests join table
                    for (PickupRequest request : schedule.getRequests()) {
                        String joinSql = "INSERT INTO schedule_requests " +
                                "(schedule_id, request_id) VALUES (?, ?)";
                        PreparedStatement joinStmt = connection.prepareStatement(joinSql);
                        joinStmt.setInt(1, scheduleId);
                        joinStmt.setInt(2, request.getId());
                        joinStmt.executeUpdate();
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Error creating schedule: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Schedule findById(int id) {
        String sql = "SELECT * FROM schedules WHERE id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int vehicleId = rs.getInt("vehicle_id");
                Vehicle vehicle = vehicleDAO.findById(vehicleId);
                Schedule schedule = new Schedule(
                        rs.getInt("id"),
                        rs.getDate("pickup_date").toLocalDate(),
                        rs.getString("time_slot"),
                        rs.getString("route_name"),
                        vehicle);
                schedule.setStatus(ScheduleStatus.valueOf(
                        rs.getString("status")));
                return schedule;
            }
        } catch (SQLException e) {
            System.out.println("Error finding schedule: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Schedule> findAll() {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM schedules";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int vehicleId = rs.getInt("vehicle_id");
                Vehicle vehicle = vehicleDAO.findById(vehicleId);
                Schedule schedule = new Schedule(
                        rs.getInt("id"),
                        rs.getDate("pickup_date").toLocalDate(),
                        rs.getString("time_slot"),
                        rs.getString("route_name"),
                        vehicle);
                schedule.setStatus(ScheduleStatus.valueOf(
                        rs.getString("status")));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching schedules: " + e.getMessage());
        }
        return schedules;
    }

    @Override
    public boolean update(Schedule schedule) {
        String sql = "UPDATE schedules SET pickup_date = ?, time_slot = ?, " +
                "route_name = ?, vehicle_id = ?, " +
                "status = ? WHERE id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setDate(1, Date.valueOf(schedule.getPickupDate()));
            stmt.setString(2, schedule.getTimeSlot());
            stmt.setString(3, schedule.getRouteName());
            stmt.setInt(4, schedule.getVehicle() != null ? schedule.getVehicle().getId() : 0);
            stmt.setString(5, schedule.getStatus().name());
            stmt.setInt(6, schedule.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating schedule: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM schedules WHERE id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting schedule: " + e.getMessage());
            return false;
        }
    }
}