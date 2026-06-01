package com.slim.ui;

import com.slim.controller.PersonController;
import com.slim.controller.RentalController;
import com.slim.domain.Person;
import com.slim.domain.Rental;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserProfileController implements Initializable {

    private static String targetCnp;

    @FXML private Label userNameLabel;
    @FXML private Label delaysLabel;
    @FXML private Label blacklistLabel;
    @FXML private Label currentBookLabel;
    @FXML private Label dueDateLabel;
    @FXML private Label daysRemainingLabel;
    @FXML private TableView<Rental> historyTable;
    @FXML private TableColumn<Rental, String> bookTitleCol;
    @FXML private TableColumn<Rental, String> bookAuthorCol;
    @FXML private TableColumn<Rental, String> borrowDateCol;
    @FXML private TableColumn<Rental, String> dueDateCol;
    @FXML private TableColumn<Rental, String> returnDateCol;
    @FXML private TableColumn<Rental, String> statusCol;

    private final PersonController personController = AppContext.getInstance().getPersonController();
    private final RentalController rentalController = AppContext.getInstance().getRentalController();

    public static void setTargetCnp(String cnp) {
        targetCnp = cnp;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();

        if (targetCnp == null) return;

        Person person = personController.findByCnp(targetCnp);
        if (person == null) return;

        userNameLabel.setText(person.getName() + " (" + person.getCnp() + ")");
        delaysLabel.setText("Delays: " + person.getDelays());

        if (person.isBlacklisted()) {
            blacklistLabel.setText("⛔ Blacklisted until: " + person.getBlacklistedUntil());
            blacklistLabel.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");
        } else {
            blacklistLabel.setText("✅ Account standing: Good");
            blacklistLabel.setStyle("-fx-text-fill: #27ae60;");
        }

        Rental active = rentalController.getActiveRental(targetCnp);
        if (active != null) {
            currentBookLabel.setText("📖 " + active.getBook().getTitle());
            dueDateLabel.setText("Due: " + active.getDueDate());
            long days = rentalController.getDaysUntilDue(targetCnp);
            if (days < 0) {
                daysRemainingLabel.setText("⚠ Overdue by " + Math.abs(days) + " days");
                daysRemainingLabel.setStyle("-fx-text-fill: #c0392b;");
            } else {
                daysRemainingLabel.setText("Days remaining: " + days);
            }
        } else {
            currentBookLabel.setText("No active rental.");
            dueDateLabel.setText("");
            daysRemainingLabel.setText("");
        }

        List<Rental> history = rentalController.getRentalHistory(targetCnp);
        historyTable.setItems(FXCollections.observableArrayList(history));
    }

    private void setupTableColumns() {
        bookTitleCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBook().getTitle()));
        bookAuthorCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBook().getAuthor()));
        borrowDateCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBorrowDate().toString()));
        dueDateCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDueDate().toString()));
        returnDateCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getReturnDate() != null ? d.getValue().getReturnDate().toString() : "-"));
        statusCol.setCellValueFactory(d -> {
            Rental r = d.getValue();
            if (r.isActive()) return new SimpleStringProperty("Active");
            return new SimpleStringProperty(r.wasLate() ? "Returned Late" : "Returned");
        });
    }

    @FXML
    private void handleBack() {
        try {
            if (AppContext.getInstance().getAuthController().isLoggedIn()) {
                App.navigateTo("admin_dashboard.fxml", "SLIM - Admin Dashboard");
            } else {
                App.navigateTo("login.fxml", "SLIM - Library Management");
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }
}
