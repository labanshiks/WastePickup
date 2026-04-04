package ui.controllers;

import dao.ApartmentDAO;
import dao.PickupRequestDAO;
import dao.ScheduleDAO;
import dao.VehicleDAO;
import model.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchedulePickupController {

    @FXML
    private DatePicker pickupDatePicker;
    @FXML
    private ComboBox<String> timeSlotCombo;
    @FXML
    private TextField routeNameField;
    @FXML
    private ComboBox<Vehicle> vehicleCombo;
    @FXML
    private Label capacityLabel;
    @FXML
    private Label selectedWeightLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private TableView<PickupRequest> requestsTable;
    @FXML
    private TableColumn<PickupRequest, Boolean> selectColumn;
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

    private User currentUser;
    private PickupRequestDAO requestDAO = new PickupRequestDAO();
    private ApartmentDAO apartmentDAO = new ApartmentDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private ScheduleDAO scheduleDAO = new ScheduleDAO();

    // Track which requests are selected via checkboxes
    private Map<Integer, SimpleBooleanProperty> selectionMap = new HashMap<>();

    @FXML
    public void initialize() {
        setupTimeSlots();
        setupVehicleCombo();
        setupTableColumns();
        loadPendingRequests();
    }

    public void setUser(User user) {
        this.currentUser = user;
    }

    private void setupTimeSlots() {
        timeSlotCombo.setItems(FXCollections.observableArrayList(
                "6AM - 8AM",
                "8AM - 10AM",
                "10AM - 12PM",
                "12PM - 2PM",
                "2PM - 4PM",
                "4PM - 6PM"));
    }

    private void setupVehicleCombo() {
        List<Vehicle> vehicles = vehicleDAO.findAll();
        ObservableList<Vehicle> available = FXCollections.observableArrayList();

        for (Vehicle v : vehicles) {
            if (v.getVehicleStatus() == VehicleStatus.AVAILABLE) {
                available.add(v);
            }
        }

        vehicleCombo.setItems(available);

        // Show plate number in dropdown
        vehicleCombo.setCellFactory(lv -> new ListCell<Vehicle>() {
            @Override
            protected void updateItem(Vehicle v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty ? ""
                        : v.getPlate() +
                                " (" + v.getCapacityKg() + "kg)");
            }
        });

        vehicleCombo.setButtonCell(new ListCell<Vehicle>() {
            @Override
            protected void updateItem(Vehicle v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty ? ""
                        : v.getPlate() +
                                " (" + v.getCapacityKg() + "kg)");
            }
        });

        // Update capacity label when vehicle selected
        vehicleCombo.setOnAction(e -> {
            Vehicle selected = vehicleCombo.getValue();
            if (selected != null) {
                capacityLabel.setText(
                        selected.getCapacityKg() + " kg");
            }
        });
    }

    private void setupTableColumns() {
        // Checkbox column for selecting requests
        selectColumn.setCellValueFactory(cellData -> {
            int id = cellData.getValue().getId();
            selectionMap.putIfAbsent(id,
                    new SimpleBooleanProperty(false));
            SimpleBooleanProperty prop = selectionMap.get(id);

            // Update weight when checkbox changes
            prop.addListener((obs, oldVal, newVal) -> updateSelectedWeight());
            return prop;
        });
        selectColumn.setCellFactory(
                CheckBoxTableCell.forTableColumn(selectColumn));
        selectColumn.setEditable(true);
        requestsTable.setEditable(true);

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id"));
        categoryColumn.setCellValueFactory(
                new PropertyValueFactory<>("category"));
        weightColumn.setCellValueFactory(
                new PropertyValueFactory<>("estimatedWeightKg"));
        fromColumn.setCellValueFactory(
                new PropertyValueFactory<>("preferredFrom"));

        // Apartment name column
        apartmentColumn.setCellValueFactory(cellData -> {
            Apartment apt = cellData.getValue().getApartment();
            String name = apt != null ? apt.getName() : "Unknown";
            return new javafx.beans.property.SimpleStringProperty(name);
        });
    }

    private void loadPendingRequests() {
        List<PickupRequest> allRequests = requestDAO.findAll();
        List<Apartment> allApartments = apartmentDAO.findAll();
        ObservableList<PickupRequest> pending = FXCollections.observableArrayList();

        for (PickupRequest r : allRequests) {
            if (r.getStatus() == RequestStatus.PENDING) {
                // Resolve apartment name
                for (Apartment apt : allApartments) {
                    if (apt.getId() == r.getApartment().getId()) {
                        r.getApartment().setName(apt.getName());
                        break;
                    }
                }
                pending.add(r);
            }
        }
        requestsTable.setItems(pending);
    }

    private void updateSelectedWeight() {
        double total = 0;
        for (PickupRequest r : requestsTable.getItems()) {
            SimpleBooleanProperty prop = selectionMap.get(r.getId());
            if (prop != null && prop.get()) {
                total += r.getEstimatedWeightKg();
            }
        }
        selectedWeightLabel.setText(total + " kg");
    }

    @FXML
    public void handleCreateSchedule() {
        // Validate inputs
        if (pickupDatePicker.getValue() == null) {
            errorLabel.setText("Please select a pickup date!");
            return;
        }

        // Validate pickup date is not in the past
        if (pickupDatePicker.getValue().isBefore(LocalDate.now())) {
            errorLabel.setText(
                    "Pickup date cannot be in the past!");
            return;
        }

        if (timeSlotCombo.getValue() == null) {
            errorLabel.setText("Please select a time slot!");
            return;
        }

        if (routeNameField.getText().isEmpty()) {
            errorLabel.setText("Please enter a route name!");
            return;
        }

        if (vehicleCombo.getValue() == null) {
            errorLabel.setText("Please select a vehicle!");
            return;
        }

        // Get selected requests
        List<PickupRequest> selected = new ArrayList<>();
        for (PickupRequest r : requestsTable.getItems()) {
            SimpleBooleanProperty prop = selectionMap.get(r.getId());
            if (prop != null && prop.get()) {
                selected.add(r);
            }
        }

        if (selected.isEmpty()) {
            errorLabel.setText(
                    "Please select at least one request!");
            return;
        }

        // Check vehicle capacity
        Vehicle vehicle = vehicleCombo.getValue();
        double totalWeight = selected.stream()
                .mapToDouble(PickupRequest::getEstimatedWeightKg)
                .sum();

        if (totalWeight > vehicle.getCapacityKg()) {
            errorLabel.setText(
                    "Total weight exceeds vehicle capacity! " +
                            "Max: " + vehicle.getCapacityKg() + "kg");
            return;
        }

        // Create schedule object
        Schedule schedule = new Schedule(
                0,
                pickupDatePicker.getValue(),
                timeSlotCombo.getValue(),
                routeNameField.getText().trim(),
                vehicle);

        // Add selected requests to schedule
        for (PickupRequest r : selected) {
            schedule.addRequest(r);
        }

        // Save to database
        boolean success = scheduleDAO.create(schedule);

        if (success) {
            // Update request statuses to SCHEDULED
            for (PickupRequest r : selected) {
                r.setStatus(RequestStatus.SCHEDULED);
                requestDAO.update(r);
            }

            // Update vehicle status to ON_ROUTE
            vehicle.setVehicleStatus(VehicleStatus.ON_ROUTE);
            vehicleDAO.update(vehicle);

            showAlert("Success",
                    "Schedule created successfully!",
                    Alert.AlertType.INFORMATION);
            handleBack();
        } else {
            errorLabel.setText(
                    "Failed to create schedule. Try again!");
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
            Stage stage = (Stage) routeNameField
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