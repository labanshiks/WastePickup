package ui.controllers;

import util.DBConnection;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import java.sql.*;
import java.io.FileWriter;
import java.io.IOException;

public class ReportsController {

    @FXML
    private TableView<String[]> statusTable;
    @FXML
    private TableColumn<String[], String> statusNameColumn;
    @FXML
    private TableColumn<String[], String> statusCountColumn;

    @FXML
    private TableView<String[]> categoryTable;
    @FXML
    private TableColumn<String[], String> categoryNameColumn;
    @FXML
    private TableColumn<String[], String> categoryCountColumn;

    @FXML
    private TableView<String[]> apartmentsTable;
    @FXML
    private TableColumn<String[], String> apartmentNameColumn;
    @FXML
    private TableColumn<String[], String> apartmentCountColumn;

    @FXML
    private TableView<String[]> vehiclesTable;
    @FXML
    private TableColumn<String[], String> vehiclePlateColumn;
    @FXML
    private TableColumn<String[], String> vehicleCountColumn;

    private User currentUser;
    private Connection connection = DBConnection.getConnection();

    // Store data for CSV export
    private ObservableList<String[]> statusData;
    private ObservableList<String[]> categoryData;
    private ObservableList<String[]> apartmentsData;
    private ObservableList<String[]> vehiclesData;

    @FXML
    public void initialize() {
        setupColumns();
        loadAllReports();
    }

    public void setUser(User user) {
        this.currentUser = user;
    }

    private void setupColumns() {
        // Status table
        statusNameColumn
                .setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        statusCountColumn
                .setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));

        // Category table
        categoryNameColumn
                .setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        categoryCountColumn
                .setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));

        // Apartments table
        apartmentNameColumn
                .setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        apartmentCountColumn
                .setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));

        // Vehicles table
        vehiclePlateColumn
                .setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        vehicleCountColumn
                .setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));
    }

    private void loadAllReports() {
        statusData = loadReport(
                "SELECT status, COUNT(*) as count " +
                        "FROM pickup_requests GROUP BY status");

        categoryData = loadReport(
                "SELECT category, COUNT(*) as count " +
                        "FROM pickup_requests " +
                        "GROUP BY category ORDER BY count DESC");

        apartmentsData = loadReport(
                "SELECT a.name, COUNT(r.id) as total " +
                        "FROM apartments a " +
                        "JOIN pickup_requests r " +
                        "ON a.id = r.apartment_id " +
                        "GROUP BY a.name ORDER BY total DESC");

        vehiclesData = loadReport(
                "SELECT v.plate, COUNT(s.id) as total " +
                        "FROM vehicles v " +
                        "JOIN schedules s ON v.id = s.vehicle_id " +
                        "GROUP BY v.plate ORDER BY total DESC");

        statusTable.setItems(statusData);
        categoryTable.setItems(categoryData);
        apartmentsTable.setItems(apartmentsData);
        vehiclesTable.setItems(vehiclesData);
    }

    private ObservableList<String[]> loadReport(String sql) {
        ObservableList<String[]> data = FXCollections.observableArrayList();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                data.add(new String[] {
                        rs.getString(1),
                        rs.getString(2)
                });
            }
        } catch (SQLException e) {
        }
        return data;
    }

    @FXML
    public void handleExport() {
        try {
            FileWriter writer = new FileWriter("report.csv");

            // Write status report
            writer.write("=== REQUESTS BY STATUS ===\n");
            writer.write("Status,Count\n");
            for (String[] row : statusData) {
                writer.write(row[0] + "," + row[1] + "\n");
            }

            // Write category report
            writer.write("\n=== REQUESTS BY CATEGORY ===\n");
            writer.write("Category,Count\n");
            for (String[] row : categoryData) {
                writer.write(row[0] + "," + row[1] + "\n");
            }

            // Write apartments report
            writer.write("\n=== MOST ACTIVE APARTMENTS ===\n");
            writer.write("Apartment,Total Requests\n");
            for (String[] row : apartmentsData) {
                writer.write(row[0] + "," + row[1] + "\n");
            }

            // Write vehicles report
            writer.write("\n=== MOST ACTIVE VEHICLES ===\n");
            writer.write("Vehicle,Total Schedules\n");
            for (String[] row : vehiclesData) {
                writer.write(row[0] + "," + row[1] + "\n");
            }

            writer.close();

            showAlert("Export Successful",
                    "Report exported to report.csv in your " +
                            "project folder!",
                    Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            showAlert("Export Failed",
                    "Error exporting report: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/ui/views/admin_dashboard.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);
            AdminDashboardController controller = loader.getController();
            controller.setUser(currentUser);
            Stage stage = (Stage) statusTable
                    .getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
        }
    }

    private void showAlert(String title, String message,
            Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}