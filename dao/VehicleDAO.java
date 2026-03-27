package dao;

import model.Vehicle;
import model.VehicleStatus;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO implements DaoInterface<Vehicle> {

    private Connection connection;

    public VehicleDAO() {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public boolean create(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (plate, capacity_kg, status) " +
                "VALUES (?, ?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, vehicle.getPlate());
            stmt.setDouble(2, vehicle.getCapacityKg());
            stmt.setString(3, vehicle.getVehicleStatus().name());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error creating vehicle: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Vehicle findById(int id) {
        String sql = "SELECT * FROM vehicles WHERE id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Vehicle vehicle = new Vehicle(
                        rs.getInt("id"),
                        rs.getString("plate"),
                        rs.getDouble("capacity_kg"));
                vehicle.setVehicleStatus(VehicleStatus.valueOf(rs.getString("status")));
                return vehicle;
            }
        } catch (SQLException e) {
            System.out.println("Error finding vehicle: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Vehicle vehicle = new Vehicle(
                        rs.getInt("id"),
                        rs.getString("plate"),
                        rs.getDouble("capacity_kg"));
                vehicle.setVehicleStatus(VehicleStatus.valueOf(rs.getString("status")));
                vehicles.add(vehicle);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching vehicles: " + e.getMessage());
        }
        return vehicles;
    }

    @Override
    public boolean update(Vehicle vehicle) {
        String sql = "UPDATE vehicles SET plate = ?, " +
                "capacity_kg = ?, status = ? WHERE id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, vehicle.getPlate());
            stmt.setDouble(2, vehicle.getCapacityKg());
            stmt.setString(3, vehicle.getVehicleStatus().name());
            stmt.setInt(4, vehicle.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating vehicle: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting vehicle: " + e.getMessage());
            return false;
        }
    }
}