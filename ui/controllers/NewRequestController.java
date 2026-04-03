package ui.controllers;

import dao.PickupRequestDAO;
import model.Apartment;
import model.PickupRequest;
import model.User;
import model.WasteCategory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import java.time.LocalDate;

public class NewRequestController {

    @FXML
    private Label apartmentLabel;
    @FXML
    private ComboBox<WasteCategory> categoryCombo;
    @FXML
    private TextField weightField;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private Label errorLabel;

    private User currentUser;
    private Apartment currentApartment;
    private PickupRequestDAO requestDAO = new PickupRequestDAO();

    // Called by CaretakerDashboardController
    public void setData(User user, Apartment apartment) {
        this.currentUser = user;
        this.currentApartment = apartment;

        // Show apartment name
        if (apartment != null) {
            apartmentLabel.setText(apartment.getName() +
                    " - " + apartment.getEstate());
        } else {
            apartmentLabel.setText("No apartment assigned!");
        }

        // Populate category dropdown with all enum values
        categoryCombo.setItems(FXCollections.observableArrayList(
                WasteCategory.values()));
    }

    @FXML
    public void handleSubmit() {
        // Validate inputs
        if (categoryCombo.getValue() == null) {
            errorLabel.setText("Please select a waste category!");
            return;
        }

        if (weightField.getText().isEmpty()) {
            errorLabel.setText("Please enter estimated weight!");
            return;
        }

        if (fromDatePicker.getValue() == null) {
            errorLabel.setText("Please select a from date!");
            return;
        }

        if (toDatePicker.getValue() == null) {
            errorLabel.setText("Please select a to date!");
            return;
        }

        // Validate weight is a number
        double weight;
        try {
            weight = Double.parseDouble(weightField.getText().trim());
            if (weight <= 0) {
                errorLabel.setText("Weight must be greater than 0!");
                return;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Please enter a valid weight number!");
            return;
        }

        // Validate date range
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        if (toDate.isBefore(fromDate)) {
            errorLabel.setText("To date cannot be before From date!");
            return;
        }

        if (fromDate.isBefore(LocalDate.now())) {
            errorLabel.setText("From date cannot be in the past!");
            return;
        }

        // Create the request object
        PickupRequest request = new PickupRequest(
                0,
                currentApartment,
                categoryCombo.getValue(),
                weight,
                fromDate,
                toDate);

        // Save to database
        boolean success = requestDAO.create(request);

        if (success) {
            // Show success and go back to dashboard
            showAlert("Success",
                    "Pickup request submitted successfully!",
                    Alert.AlertType.INFORMATION);
            handleBack();
        } else {
            errorLabel.setText("Failed to submit request. Try again!");
        }
    }

    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/ui/views/caretaker_dashboard.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);
            CaretakerDashboardController controller = loader.getController();
            controller.setUser(currentUser);
            Stage stage = (Stage) apartmentLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("Error going back: " + e.getMessage());
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