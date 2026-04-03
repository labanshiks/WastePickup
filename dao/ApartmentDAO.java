package dao;

import model.Apartment;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApartmentDAO implements DaoInterface<Apartment> {

    private Connection connection;

    public ApartmentDAO() {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public boolean create(Apartment apartment) {
        String sql = "INSERT INTO apartments (name, estate, address, " +
                "number_of_units, caretaker_user_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, apartment.getName());
            stmt.setString(2, apartment.getEstate());
            stmt.setString(3, apartment.getAddress());
            stmt.setInt(4, apartment.getNumberOfUnits());
            stmt.setInt(5, apartment.getCaretaker() != null ? apartment.getCaretaker().getId() : 0);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error creating apartment: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Apartment findById(int id) {
        String sql = "SELECT * FROM apartments WHERE id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Apartment apartment = new Apartment(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("estate"),
                        rs.getString("address"),
                        rs.getInt("number_of_units"));
                apartment.setCaretakerId(rs.getInt("caretaker_user_id"));
                // Store caretaker_user_id for later lookup
                int caretakerId = rs.getInt("caretaker_user_id");
                System.out.println("Caretaker ID: " + caretakerId);
                return apartment;
            }
        } catch (SQLException e) {
            System.out.println("Error finding apartment: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Apartment> findAll() {
        List<Apartment> apartments = new ArrayList<>();
        String sql = "SELECT * FROM apartments";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Apartment apartment = new Apartment(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("estate"),
                        rs.getString("address"),
                        rs.getInt("number_of_units"));
                apartment.setCaretakerId(rs.getInt("caretaker_user_id"));
                apartments.add(apartment);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching apartments: " + e.getMessage());
        }
        return apartments;
    }

    @Override
    public boolean update(Apartment apartment) {
        String sql = "UPDATE apartments SET name = ?, estate = ?, " +
                "address = ?, number_of_units = ?, " +
                "caretaker_user_id = ? WHERE id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, apartment.getName());
            stmt.setString(2, apartment.getEstate());
            stmt.setString(3, apartment.getAddress());
            stmt.setInt(4, apartment.getNumberOfUnits());
            stmt.setInt(5, apartment.getCaretaker() != null ? apartment.getCaretaker().getId() : 0);
            stmt.setInt(6, apartment.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating apartment: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM apartments WHERE id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting apartment: " + e.getMessage());
            return false;
        }
    }
}