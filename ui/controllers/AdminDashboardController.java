package ui.controllers;

import dao.ApartmentDAO;
import dao.PickupRequestDAO;
import dao.VehicleDAO;
import model.Apartment;
import model.PickupRequest;
import model.User;
import model.Vehicle;
import model.VehicleStatus;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import java.util.List;
import javafx.scene.control.*;

public class AdminDashboardController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label pendingCount;
    @FXML
    private Label scheduledCount;
    @FXML
    private Label vehiclesCount;
    @FXML
    private TableView<PickupRequest> requestsTable;
    @FXML
    private TableColumn<PickupRequest, Integer> idColumn;
    @FXML
    private TableColumn<PickupRequest, String> apartmentColumn;
    @FXML
    private TableColumn<PickupRequest, String> categoryColumn;
    @FXML
    private TableColumn<PickupRequest, Double> weightColumn;
    @FXML
    private TableColumn<PickupRequest, String> fromColumn;
    @FXML
    private TableColumn<PickupRequest, String> toColumn;
    @FXML
    private TableColumn<PickupRequest, String> statusColumn;

    private User currentUser;
    private PickupRequestDAO requestDAO = new PickupRequestDAO();
    private ApartmentDAO apartmentDAO = new ApartmentDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();

    // Called by LoginController to pass logged in user
    public void setUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getUsername());
        loadDashboard();
    }

    private void loadDashboard() {
        setupTableColumns();
        loadRequests();
        loadVehicleCount();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id"));
        categoryColumn.setCellValueFactory(
                new PropertyValueFactory<>("category"));
        weightColumn.setCellValueFactory(
                new PropertyValueFactory<>("estimatedWeightKg"));
        fromColumn.setCellValueFactory(
                new PropertyValueFactory<>("preferredFrom"));
        toColumn.setCellValueFactory(
                new PropertyValueFactory<>("preferredTo"));
        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status"));

        // Apartment column needs special handling
        // since apartment name is inside the Apartment object
        apartmentColumn.setCellValueFactory(cellData -> {
            Apartment apt = cellData.getValue().getApartment();
            String name = apt != null ? apt.getName() : "Unknown";
            return new javafx.beans.property.SimpleStringProperty(name);
        });
    }

    private void loadRequests() {
        List<PickupRequest> allRequests = requestDAO.findAll();
        ObservableList<PickupRequest> pendingRequests = FXCollections.observableArrayList();

        // Load apartment names for each request
        List<Apartment> allApartments = apartmentDAO.findAll();

        int pending = 0, scheduled = 0;

        for (PickupRequest r : allRequests) {
            // Match apartment name to request
            for (Apartment apt : allApartments) {
                if (apt.getId() == r.getApartment().getId()) {
                    r.getApartment().setName(apt.getName());
                    break;
                }
            }

            // Count by status
            switch (r.getStatus().name()) {
                case "PENDING":
                    pending++;
                    pendingRequests.add(r);
                    break;
                case "SCHEDULED":
                    scheduled++;
                    break;
            }
        }

        requestsTable.setItems(pendingRequests);
        pendingCount.setText(String.valueOf(pending));
        scheduledCount.setText(String.valueOf(scheduled));
    }

    private void loadVehicleCount() {
        List<Vehicle> vehicles = vehicleDAO.findAll();
        long available = vehicles.stream()
                .filter(v -> v.getVehicleStatus() == VehicleStatus.AVAILABLE)
                .count();
        vehiclesCount.setText(String.valueOf(available));
    }

    @FXML
    public void showPendingRequests() {
        loadRequests();
    }

    @FXML
    public void showSchedule() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/ui/views/schedule_pickup.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 600);
            SchedulePickupController controller = loader.getController();
            controller.setUser(currentUser);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("Error opening schedule: "
                    + e.getMessage());
        }
    }

    @FXML
    public void showVehicles() {
        System.out.println("Vehicles - coming soon!");
    }

    @FXML
    public void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ui/views/login.fxml"));
            Scene scene = new Scene(loader.load(), 500, 450);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("Error logging out: " + e.getMessage());
        }
    }

    @FXML
    public void showManageSchedules() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/ui/views/manage_schedules.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);
            ManageSchedulesController controller = loader.getController();
            controller.setUser(currentUser);
            Stage stage = (Stage) welcomeLabel
                    .getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("Error opening schedules: "
                    + e.getMessage());
        }
    }

    @FXML
    public void showReports() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/ui/views/reports.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);
            ReportsController controller = loader.getController();
            controller.setUser(currentUser);
            Stage stage = (Stage) welcomeLabel
                    .getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("Error opening reports: "
                    + e.getMessage());
        }
    }
}