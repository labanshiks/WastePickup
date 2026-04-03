package ui.controllers;

import dao.ApartmentDAO;
import dao.PickupRequestDAO;
import model.Apartment;
import model.PickupRequest;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import java.util.List;

public class CaretakerDashboardController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label pendingCount;
    @FXML
    private Label scheduledCount;
    @FXML
    private Label completedCount;
    @FXML
    private TableView<PickupRequest> requestsTable;
    @FXML
    private TableColumn<PickupRequest, Integer> idColumn;
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
    private Apartment caretakerApartment;
    private PickupRequestDAO requestDAO = new PickupRequestDAO();
    private ApartmentDAO apartmentDAO = new ApartmentDAO();

    // Called by LoginController to pass logged in user
    public void setUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getUsername());
        loadApartment();
        loadRequests();
    }

    private void loadApartment() {
        // Find apartment belonging to this caretaker
        List<Apartment> all = apartmentDAO.findAll();
        for (Apartment apt : all) {
            if (apt.getCaretakerId() == currentUser.getId()) {
                caretakerApartment = apt;
                break;
            }
        }
    }

    private void loadRequests() {
        // Set up table columns
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

        // Load requests for this caretaker's apartment
        List<PickupRequest> allRequests = requestDAO.findAll();
        ObservableList<PickupRequest> caretakerRequests = FXCollections.observableArrayList();

        int pending = 0, scheduled = 0, completed = 0;

        for (PickupRequest r : allRequests) {
            if (caretakerApartment != null &&
                    r.getApartment().getId() == caretakerApartment.getId()) {
                caretakerRequests.add(r);

                // Count by status
                switch (r.getStatus().name()) {
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
        }

        // Update table and counts
        requestsTable.setItems(caretakerRequests);
        pendingCount.setText(String.valueOf(pending));
        scheduledCount.setText(String.valueOf(scheduled));
        completedCount.setText(String.valueOf(completed));
    }

    @FXML
    public void showApartment() {
        if (caretakerApartment != null) {
            System.out.println("Apartment: " + caretakerApartment.getName());
        } else {
            System.out.println("No apartment assigned!");
        }
    }

    @FXML
    public void showNewRequest() {
        System.out.println("New Request - coming soon!");
    }

    @FXML
    public void showMyRequests() {
        loadRequests();
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
}