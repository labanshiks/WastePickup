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
        if (currentApartment == null) {
            errorLabel.setText(
                    "No apartment assigned to your account. " +
                            "Contact admin!");
            return;
        }

        if (categoryCombo.getValue() == null) {
            errorLabel.setText(
                    "Please select a waste category " +
                            "before submitting!");
            return;
        }

        if (weightField.getText().isEmpty()) {
            errorLabel.setText(
                    "Please enter the estimated weight in kg!");
            return;
        }

        if (fromDatePicker.getValue() == null) {
            errorLabel.setText(
                    "Please select your preferred pickup start date!");
            return;
        }

        if (toDatePicker.getValue() == null) {
            errorLabel.setText(
                    "Please select your preferred pickup end date!");
            return;
        }

        double weight;
        try {
            weight = Double.parseDouble(
                    weightField.getText().trim());
            if (weight <= 0) {
                errorLabel.setText(
                        "Weight must be greater than 0 kg!");
                return;
            }
            if (weight > 1000) {
                errorLabel.setText(
                        "Weight seems too high. " +
                                "Maximum allowed is 1000 kg!");
                return;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText(
                    "Invalid weight! Please enter a " +
                            "valid number e.g 50 or 50.5");
            return;
        }

        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        if (fromDate.isBefore(LocalDate.now())) {
            errorLabel.setText(
                    "Pickup start date cannot be in the past!");
            return;
        }

        if (toDate.isBefore(fromDate)) {
            errorLabel.setText(
                    "End date cannot be before start date!");
            return;
        }

        if (toDate.isAfter(fromDate.plusDays(7))) {
            errorLabel.setText(
                    "Pickup window cannot exceed 7 days!");
            return;
        }

        // Check for duplicate
        boolean duplicate = requestDAO.duplicateExists(
                currentApartment.getId(),
                categoryCombo.getValue().name());

        if (duplicate) {
            errorLabel.setText(
                    "A PENDING or SCHEDULED " +
                            categoryCombo.getValue() +
                            " request already exists " +
                            "for your apartment!");
            return;
        }

        // Create request
        PickupRequest request = new PickupRequest(
                0,
                currentApartment,
                categoryCombo.getValue(),
                weight,
                fromDate,
                toDate);

        boolean success = requestDAO.create(request);

        if (success) {
            showAlert("Request Submitted!",
                    "Your " + categoryCombo.getValue() +
                            " pickup request has been submitted " +
                            "successfully!\n\n" +
                            "Preferred dates: " + fromDate +
                            " to " + toDate,
                    Alert.AlertType.INFORMATION);
            handleBack();
        } else {
            errorLabel.setText(
                    "Failed to submit request. " +
                            "Please try again or contact admin!");
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