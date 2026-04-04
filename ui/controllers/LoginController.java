package ui.controllers;

import dao.UserDAO;
import model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    // These connect to fx:id fields in login.fxml
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginButton;

    private UserDAO userDAO = new UserDAO();

    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() && password.isEmpty()) {
            errorLabel.setText(
                    "Please enter your username and password!");
            return;
        }

        if (username.isEmpty()) {
            errorLabel.setText("Please enter your username!");
            return;
        }

        if (password.isEmpty()) {
            errorLabel.setText("Please enter your password!");
            return;
        }

        // Check credentials
        User user = userDAO.findByUsername(username);

        if (user == null) {
            errorLabel.setText(
                    "Username not found. Please check and try again!");
            return;
        }

        if (!user.getPassword().equals(password)) {
            errorLabel.setText(
                    "Incorrect password. Please try again!");
            return;
        }

        if (user.getRole().equals("ADMIN")) {
            loadDashboard("Admin", user);
        } else if (user.getRole().equals("CARETAKER")) {
            loadDashboard("Caretaker", user);
        }
    }

    private void loadDashboard(String role, User user) {
        try {
            String fxmlFile = role.equals("Admin") ? "/ui/views/admin_dashboard.fxml"
                    : "/ui/views/caretaker_dashboard.fxml";

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 900, 600);

            // Pass user to correct dashboard controller
            if (role.equals("Caretaker")) {
                CaretakerDashboardController controller = loader.getController();
                controller.setUser(user);
            } else if (role.equals("Admin")) {
                AdminDashboardController controller = loader.getController();
                controller.setUser(user);
            }

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(true);
        } catch (Exception e) {
            errorLabel.setText("Error loading dashboard: " + e.getMessage());
            System.out.println("Dashboard error: " + e.getMessage());
        }
    }
}