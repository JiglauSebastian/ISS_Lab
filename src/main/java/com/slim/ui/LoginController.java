package com.slim.ui;

import com.slim.controller.AuthController;
import com.slim.controller.PersonController;
import com.slim.domain.Person;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private TextField cnpField;

    private final AuthController authController = AppContext.getInstance().getAuthController();
    private final PersonController personController = AppContext.getInstance().getPersonController();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (authController.login(username, password)) {
            try {
                App.navigateTo("admin_dashboard.fxml", "SLIM - Admin Dashboard");
            } catch (Exception e) {
                showError("Navigation error: " + e.getMessage());
            }
        } else {
            showError("Invalid username or password.");
        }
    }

    @FXML
    private void handleUserLookup() {
        String cnp = cnpField.getText().trim();
        if (cnp.isBlank()) {
            showError("Please enter a CNP.");
            return;
        }
        Person person = personController.findByCnp(cnp);
        if (person == null) {
            showError("No user found with this CNP.");
            return;
        }
        try {
            UserProfileController.setTargetCnp(cnp);
            App.navigateTo("user_profile.fxml", "SLIM - User Profile: " + person.getName());
        } catch (Exception e) {
            showError("Navigation error: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
