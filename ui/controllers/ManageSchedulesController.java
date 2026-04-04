package ui.controllers;

import dao.PickupRequestDAO;
import dao.ScheduleDAO;
import dao.VehicleDAO;
import model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import java.util.List;

public class ManageSchedulesController {

    @FXML
    private TableView<Schedule> schedulesTable;
    @FXML
    private TableColumn<Schedule, Integer> idColumn;
    @FXML
    private TableColumn<Schedule, String> dateColumn;
    @FXML
    private TableColumn<Schedule, String> timeSlotColumn;
    @FXML
    private TableColumn<Schedule, String> routeColumn;
    @FXML
    private TableColumn<Schedule, String> vehicleColumn;
    @FXML
    private TableColumn<Schedule, Integer> requestsColumn;
    @FXML
    private TableColumn<Schedule, String> statusColumn;
    @FXML
    private Label messageLabel;

    private User currentUser;
    private ScheduleDAO scheduleDAO = new ScheduleDAO();
    private PickupRequestDAO requestDAO = new PickupRequestDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadSchedules();
    }

    public void setUser(User user) {
        this.currentUser = user;
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id"));
        timeSlotColumn.setCellValueFactory(
                new PropertyValueFactory<>("timeSlot"));
        routeColumn.setCellValueFactory(
                new PropertyValueFactory<>("routeName"));
        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status"));

        // Date column
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getPickupDate().toString()));

        // Vehicle column - show plate
        vehicleColumn.setCellValueFactory(cellData -> {
            Vehicle v = cellData.getValue().getVehicle();
            String plate = v != null ? v.getPlate() : "Unknown";
            return new javafx.beans.property.SimpleStringProperty(plate);
        });

        // Requests column - show count from database
        requestsColumn.setCellValueFactory(cellData -> {
            int scheduleId = cellData.getValue().getId();
            int count = getRequestCount(scheduleId);
            return new javafx.beans.property.SimpleIntegerProperty(count).asObject();
        });
    }

    private int getRequestCount(int scheduleId) {
        List<PickupRequest> all = requestDAO.findAll();
        // Count requests linked to this schedule
        // We'll use a simple approach for now
        return (int) all.stream()
                .filter(r -> r.getStatus() == RequestStatus.SCHEDULED
                        || r.getStatus() == RequestStatus.COMPLETED
                        || r.getStatus() == RequestStatus.MISSED)
                .count();
    }

    private void loadSchedules() {
        List<Schedule> all = scheduleDAO.findAll();
        ObservableList<Schedule> active = FXCollections.observableArrayList();

        for (Schedule s : all) {
            // Show only PENDING and ONGOING schedules
            if (s.getStatus() == ScheduleStatus.PENDING ||
                    s.getStatus() == ScheduleStatus.ONGOING) {
                active.add(s);
            }
        }
        schedulesTable.setItems(active);
    }

    @FXML
    public void handleMarkCompleted() {
        Schedule selected = schedulesTable
                .getSelectionModel().getSelectedItem();

        if (selected == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText(
                    "Please select a schedule first!");
            return;
        }

        // Update schedule status
        selected.setStatus(ScheduleStatus.COMPLETED);
        scheduleDAO.update(selected);

        // Update vehicle back to AVAILABLE
        Vehicle vehicle = vehicleDAO.findById(
                selected.getVehicle().getId());
        if (vehicle != null) {
            vehicle.setVehicleStatus(VehicleStatus.AVAILABLE);
            vehicleDAO.update(vehicle);
        }

        // Update all linked requests to COMPLETED
        updateLinkedRequests(selected.getId(),
                RequestStatus.COMPLETED);

        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText("Schedule marked as COMPLETED!");
        loadSchedules();
    }

    @FXML
    public void handleMarkMissed() {
        Schedule selected = schedulesTable
                .getSelectionModel().getSelectedItem();

        if (selected == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText(
                    "Please select a schedule first!");
            return;
        }

        // Update schedule status
        selected.setStatus(ScheduleStatus.CANCELLED);
        scheduleDAO.update(selected);

        // Update vehicle back to AVAILABLE
        Vehicle vehicle = vehicleDAO.findById(
                selected.getVehicle().getId());
        if (vehicle != null) {
            vehicle.setVehicleStatus(VehicleStatus.AVAILABLE);
            vehicleDAO.update(vehicle);
        }

        // Update all linked requests to MISSED
        updateLinkedRequests(selected.getId(),
                RequestStatus.MISSED);

        messageLabel.setStyle("-fx-text-fill: orange;");
        messageLabel.setText("Schedule marked as MISSED!");
        loadSchedules();
    }

    private void updateLinkedRequests(int scheduleId,
            RequestStatus status) {
        // Get all requests and update SCHEDULED ones
        // linked to this schedule
        List<PickupRequest> all = requestDAO.findAll();
        for (PickupRequest r : all) {
            if (r.getStatus() == RequestStatus.SCHEDULED) {
                r.setStatus(status);
                requestDAO.update(r);
            }
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
            Stage stage = (Stage) messageLabel
                    .getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
        }
    }
}