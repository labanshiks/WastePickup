package ui.controllers;

import dao.ApartmentDAO;
import dao.PickupRequestDAO;
import model.Apartment;
import model.PickupRequest;
import model.RequestStatus;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import java.util.List;

public class CaretakerDashboardController {

    // Navigation and summary labels
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label pendingCount;
    @FXML
    private Label scheduledCount;
    @FXML
    private Label completedCount;
    @FXML
    private Label actionMessageLabel;

    // Table and columns
    @FXML
    private TableView<String[]> requestsTable;
    @FXML
    private TableColumn<String[], String> idColumn;
    @FXML
    private TableColumn<String[], String> categoryColumn;
    @FXML
    private TableColumn<String[], String> weightColumn;
    @FXML
    private TableColumn<String[], String> fromColumn;
    @FXML
    private TableColumn<String[], String> toColumn;
    @FXML
    private TableColumn<String[], String> statusColumn;
    @FXML
    private TableColumn<String[], String> pickupDateColumn;
    @FXML
    private TableColumn<String[], String> timeSlotColumn;
    @FXML
    private TableColumn<String[], String> vehicleColumn;

    private User currentUser;
    private Apartment caretakerApartment;
    private PickupRequestDAO requestDAO = new PickupRequestDAO();
    private ApartmentDAO apartmentDAO = new ApartmentDAO();

    public void setUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getUsername());
        loadApartment();
        loadRequests();
    }

    private void loadApartment() {
        List<Apartment> all = apartmentDAO.findAll();
        for (Apartment apt : all) {
            if (apt.getCaretakerId() == currentUser.getId()) {
                caretakerApartment = apt;
                break;
            }
        }
    }

    private void loadRequests() {
        // Setup columns
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        categoryColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));
        weightColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));
        fromColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));
        toColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[4]));
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[5]));
        pickupDateColumn
                .setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[6]));
        timeSlotColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[7]));
        vehicleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[8]));

        // Load requests with schedule details
        if (caretakerApartment == null) {
            pendingCount.setText("0");
            scheduledCount.setText("0");
            completedCount.setText("0");
            return;
        }

        List<String[]> requests = requestDAO
                .findRequestsWithSchedule(caretakerApartment.getId());
        ObservableList<String[]> data = FXCollections.observableArrayList(requests);

        // Count by status
        int pending = 0, scheduled = 0, completed = 0;
        for (String[] row : requests) {
            switch (row[5]) {
                case "PENDING":
                    pending++;
                    break;
                case "SCHEDULED":
                    scheduled++;
                    break;
                case "COMPLETED":
                    completed++;
                    break;
            }
        }

        requestsTable.setItems(data);
        pendingCount.setText(String.valueOf(pending));
        scheduledCount.setText(String.valueOf(scheduled));
        completedCount.setText(String.valueOf(completed));
    }

    @FXML
    public void showApartment() {
        if (caretakerApartment != null) {
        } else {
        }
    }

    @FXML
    public void showNewRequest() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/ui/views/new_request.fxml"));
            Scene scene = new Scene(loader.load(), 600, 600);
            NewRequestController controller = loader.getController();
            controller.setData(currentUser, caretakerApartment);
            Stage stage = (Stage) welcomeLabel
                    .getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
        }
    }

    @FXML
    public void showMyRequests() {
        loadRequests();
    }

    @FXML
    public void handleCancelRequest() {
        String[] selected = requestsTable
                .getSelectionModel().getSelectedItem();

        if (selected == null) {
            actionMessageLabel.setStyle("-fx-text-fill: red;");
            actionMessageLabel.setText(
                    "Please select a request first!");
            return;
        }

        // Only PENDING requests can be cancelled
        if (!selected[5].equals("PENDING")) {
            actionMessageLabel.setStyle("-fx-text-fill: red;");
            actionMessageLabel.setText(
                    "Only PENDING requests can be cancelled!");
            return;
        }

        // Confirm cancellation
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Request");
        confirm.setHeaderText(null);
        confirm.setContentText(
                "Are you sure you want to cancel this " +
                        selected[1] + " request?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                int requestId = Integer.parseInt(selected[0]);
                PickupRequest request = requestDAO.findById(requestId);
                if (request != null) {
                    request.setStatus(RequestStatus.CANCELLED);
                    boolean success = requestDAO.update(request);
                    if (success) {
                        actionMessageLabel.setStyle(
                                "-fx-text-fill: green;");
                        actionMessageLabel.setText(
                                "Request cancelled successfully!");
                        loadRequests();
                    }
                }
            }
        });
    }

    @FXML
    public void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/ui/views/login.fxml"));
            Scene scene = new Scene(loader.load(), 500, 450);
            Stage stage = (Stage) welcomeLabel
                    .getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
        }
    }
}