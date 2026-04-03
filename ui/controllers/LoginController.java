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

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password!");
            return;
        }

        // Debug line - check what's being searched
        System.out.println("Searching for username: " + username);

        User user = userDAO.findByUsername(username);

        // Debug line - check what was returned
        System.out.println("User found: " + (user != null ? user.getUsername() : "null"));

        if (user == null) {
            errorLabel.setText("User not found!");
            return;
        }

        if (!user.getPassword().equals(password)) {
            errorLabel.setText("Incorrect password!");
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

            // Pass user to dashboard controller
            if (role.equals("Caretaker")) {
                CaretakerDashboardController controller = loader.getController();
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