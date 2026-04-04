package dao;

import model.Apartment;
import model.PickupRequest;
import model.RequestStatus;
import model.WasteCategory;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;

public class PickupRequestDAO implements DaoInterface<PickupRequest> {

    private Connection connection;

    public PickupRequestDAO() {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public boolean create(PickupRequest request) {
        String sql = "INSERT INTO pickup_requests (apartment_id, category, " +
                "estimated_weight_kg, preferred_from, preferred_to) " +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, request.getApartment() != null ? request.getApartment().getId() : 0);
            stmt.setString(2, request.getCategory().name());
            stmt.setDouble(3, request.getEstimatedWeightKg());
            stmt.setDate(4, Date.valueOf(request.getPreferredFrom()));
            stmt.setDate(5, Date.valueOf(request.getPreferredTo()));
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error creating request: " + e.getMessage());
            return false;
        }
    }

    @Override
    public PickupRequest findById(int id) {
        String sql = "SELECT * FROM pickup_requests WHERE id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                PickupRequest request = new PickupRequest(
                        rs.getInt("id"),
                        new Apartment(rs.getInt("apartment_id"), "", "", "", 0),
                        WasteCategory.valueOf(rs.getString("category")),
                        rs.getDouble("estimated_weight_kg"),
                        rs.getDate("preferred_from").toLocalDate(),
                        rs.getDate("preferred_to").toLocalDate());
                request.setStatus(RequestStatus.valueOf(
                        rs.getString("status")));
                return request;
            }
        } catch (SQLException e) {
            System.out.println("Error finding request: " + e.getMessage());
        }
        return null;
    }

    // Find requests with schedule details for a caretaker
    public List<String[]> findRequestsWithSchedule(int apartmentId) {
        List<String[]> results = new ArrayList<>();
        String sql = "SELECT r.id, r.category, r.estimated_weight_kg, " +
                "r.preferred_from, r.preferred_to, r.status, " +
                "s.pickup_date, s.time_slot, v.plate " +
                "FROM pickup_requests r " +
                "LEFT JOIN schedule_requests sr ON r.id = sr.request_id " +
                "LEFT JOIN schedules s ON sr.schedule_id = s.id " +
                "LEFT JOIN vehicles v ON s.vehicle_id = v.id " +
                "WHERE r.apartment_id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, apartmentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(new String[] {
                        rs.getString("id"),
                        rs.getString("category"),
                        rs.getString("estimated_weight_kg"),
                        rs.getString("preferred_from"),
                        rs.getString("preferred_to"),
                        rs.getString("status"),
                        rs.getString("pickup_date") != null ? rs.getString("pickup_date") : "Not scheduled",
                        rs.getString("time_slot") != null ? rs.getString("time_slot") : "--",
                        rs.getString("plate") != null ? rs.getString("plate") : "--"
                });
            }
        } catch (SQLException e) {
            System.out.println("Error fetching requests: "
                    + e.getMessage());
        }
        return results;
    }

    @Override
    public List<PickupRequest> findAll() {
        List<PickupRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM pickup_requests";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                PickupRequest request = new PickupRequest(
                        rs.getInt("id"),
                        new Apartment(rs.getInt("apartment_id"), "", "", "", 0),
                        WasteCategory.valueOf(rs.getString("category")),
                        rs.getDouble("estimated_weight_kg"),
                        rs.getDate("preferred_from").toLocalDate(),
                        rs.getDate("preferred_to").toLocalDate());
                request.setStatus(RequestStatus.valueOf(
                        rs.getString("status")));
                requests.add(request);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching requests: " + e.getMessage());
        }
        return requests;
    }

    @Override
    public boolean update(PickupRequest request) {
        String sql = "UPDATE pickup_requests SET apartment_id = ?, " +
                "category = ?, estimated_weight_kg = ?, " +
                "preferred_from = ?, preferred_to = ?, " +
                "status = ? WHERE id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, request.getApartment() != null ? request.getApartment().getId() : 0);
            stmt.setString(2, request.getCategory().name());
            stmt.setDouble(3, request.getEstimatedWeightKg());
            stmt.setDate(4, Date.valueOf(request.getPreferredFrom()));
            stmt.setDate(5, Date.valueOf(request.getPreferredTo()));
            stmt.setString(6, request.getStatus().name());
            stmt.setInt(7, request.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating request: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM pickup_requests WHERE id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting request: " + e.getMessage());
            return false;
        }
    }

    // Check if a duplicate request exists
    public boolean duplicateExists(int apartmentId, String category) {
        String sql = "SELECT COUNT(*) FROM pickup_requests " +
                "WHERE apartment_id = ? AND category = ? " +
                "AND status IN ('PENDING', 'SCHEDULED')";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, apartmentId);
            stmt.setString(2, category);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking duplicate: "
                    + e.getMessage());
        }
        return false;
    }
}