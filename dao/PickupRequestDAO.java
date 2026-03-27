package dao;

import model.Apartment;
import model.PickupRequest;
import model.RequestStatus;
import model.WasteCategory;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
}