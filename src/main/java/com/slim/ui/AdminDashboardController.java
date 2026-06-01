package com.slim.ui;

import com.slim.controller.AuthController;
import com.slim.controller.BookController;
import com.slim.controller.PersonController;
import com.slim.controller.RentalController;
import com.slim.domain.Book;
import com.slim.domain.Person;
import com.slim.domain.Rental;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML private Label adminNameLabel;
    @FXML private StackPane contentArea;

    private final AuthController authController = AppContext.getInstance().getAuthController();
    private final BookController bookController = AppContext.getInstance().getBookController();
    private final PersonController personController = AppContext.getInstance().getPersonController();
    private final RentalController rentalController = AppContext.getInstance().getRentalController();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (authController.getLoggedInAdmin() != null) {
            adminNameLabel.setText("Hello, " + authController.getLoggedInAdmin().getName());
        }
        showCatalog();
    }

    @FXML
    private void handleLogout() {
        authController.logout();
        try {
            App.navigateTo("login.fxml", "SLIM - Library Management");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void showCatalog() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(buildCatalogPane());
    }

    @FXML
    private void showUsers() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(buildUsersPane());
    }

    @FXML
    private void showRentals() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(buildRentalsPane());
    }

    @FXML
    private void showImport() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(buildImportPane());
    }

    private VBox buildCatalogPane() {
        VBox pane = new VBox(12);
        pane.setPadding(new Insets(20));
        pane.getStyleClass().add("content-pane");

        Label title = new Label("Book Catalog");
        title.getStyleClass().add("section-title");

        HBox filters = new HBox(10);
        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.getItems().add("All");
        categoryFilter.getItems().addAll(bookController.getAllCategories());
        categoryFilter.setValue("All");
        categoryFilter.setPromptText("Category");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Available", "Borrowed");
        statusFilter.setValue("All");

        ComboBox<String> sortBox = new ComboBox<>();
        sortBox.getItems().addAll("Default", "Title", "Author", "Category");
        sortBox.setValue("Default");

        Button applyBtn = new Button("Apply");
        applyBtn.getStyleClass().add("btn-secondary");
        filters.getChildren().addAll(new Label("Category:"), categoryFilter,
                new Label("Status:"), statusFilter, new Label("Sort:"), sortBox, applyBtn);

        TableView<Book> table = buildBookTable();
        table.setItems(FXCollections.observableArrayList(bookController.getAllBooks()));
        VBox.setVgrow(table, Priority.ALWAYS);

        applyBtn.setOnAction(e -> {
            List<Book> filtered = bookController.getFilteredBooks(
                    categoryFilter.getValue(), statusFilter.getValue(), sortBox.getValue());
            table.setItems(FXCollections.observableArrayList(filtered));
        });

        HBox actions = buildBookActions(table);

        pane.getChildren().addAll(title, filters, table, actions);
        return pane;
    }

    private TableView<Book> buildBookTable() {
        TableView<Book> table = new TableView<>();
        table.getStyleClass().add("table-slim");

        TableColumn<Book, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(220);

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setPrefWidth(160);

        TableColumn<Book, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(120);

        TableColumn<Book, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isBorrowed() ? "Borrowed" : "Available"));
        statusCol.setPrefWidth(100);

        table.getColumns().addAll(idCol, titleCol, authorCol, categoryCol, statusCol);
        return table;
    }

    private HBox buildBookActions(TableView<Book> table) {
        HBox box = new HBox(8);

        Button addBtn = new Button("+ Add Book");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> {
            showBookDialog(null, table);
        });

        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("btn-secondary");
        editBtn.setOnAction(e -> {
            Book selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert(Alert.AlertType.WARNING, "Warning", "Select a book first."); return; }
            showBookDialog(selected, table);
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("btn-danger-small");
        deleteBtn.setOnAction(e -> {
            Book selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert(Alert.AlertType.WARNING, "Warning", "Select a book first."); return; }
            try {
                bookController.deleteBook(selected.getId());
                table.setItems(FXCollections.observableArrayList(bookController.getAllBooks()));
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book deleted.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });

        box.getChildren().addAll(addBtn, editBtn, deleteBtn);
        return box;
    }

    private void showBookDialog(Book existing, TableView<Book> table) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Book" : "Edit Book");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titleF = new TextField(existing != null ? existing.getTitle() : "");
        TextField authorF = new TextField(existing != null ? existing.getAuthor() : "");
        TextField categoryF = new TextField(existing != null ? existing.getCategory() : "");

        grid.add(new Label("Title:"), 0, 0); grid.add(titleF, 1, 0);
        grid.add(new Label("Author:"), 0, 1); grid.add(authorF, 1, 1);
        grid.add(new Label("Category:"), 0, 2); grid.add(categoryF, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    if (existing == null) {
                        bookController.addBook(titleF.getText(), authorF.getText(), categoryF.getText());
                    } else {
                        bookController.updateBook(existing.getId(), titleF.getText(), authorF.getText(), categoryF.getText());
                    }
                    table.setItems(FXCollections.observableArrayList(bookController.getAllBooks()));
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
                }
            }
        });
    }

    private VBox buildUsersPane() {
        VBox pane = new VBox(12);
        pane.setPadding(new Insets(20));
        pane.getStyleClass().add("content-pane");

        Label title = new Label("User Management");
        title.getStyleClass().add("section-title");

        TableView<Person> table = new TableView<>();
        table.getStyleClass().add("table-slim");

        TableColumn<Person, String> cnpCol = new TableColumn<>("CNP");
        cnpCol.setCellValueFactory(new PropertyValueFactory<>("cnp"));
        cnpCol.setPrefWidth(150);

        TableColumn<Person, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Person, String> bookCol = new TableColumn<>("Current Book");
        bookCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getBorrowedBook() != null ? d.getValue().getBorrowedBook().getTitle() : "-"));
        bookCol.setPrefWidth(200);

        TableColumn<Person, Integer> delaysCol = new TableColumn<>("Delays");
        delaysCol.setCellValueFactory(new PropertyValueFactory<>("delays"));
        delaysCol.setPrefWidth(70);

        TableColumn<Person, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().isBlacklisted() ? "Blacklisted until " + d.getValue().getBlacklistedUntil() : "Active"));
        statusCol.setPrefWidth(200);

        table.getColumns().addAll(cnpCol, nameCol, bookCol, delaysCol, statusCol);
        table.setItems(FXCollections.observableArrayList(personController.getAllPersons()));
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox actions = new HBox(8);
        Button addBtn = new Button("+ Add User");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> showPersonDialog(null, table));

        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("btn-secondary");
        editBtn.setOnAction(e -> {
            Person selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert(Alert.AlertType.WARNING, "Warning", "Select a user first."); return; }
            showPersonDialog(selected, table);
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("btn-danger-small");
        deleteBtn.setOnAction(e -> {
            Person selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert(Alert.AlertType.WARNING, "Warning", "Select a user first."); return; }
            try {
                personController.deletePerson(selected.getCnp());
                table.setItems(FXCollections.observableArrayList(personController.getAllPersons()));
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });

        Button profileBtn = new Button("View Profile");
        profileBtn.getStyleClass().add("btn-secondary");
        profileBtn.setOnAction(e -> {
            Person selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert(Alert.AlertType.WARNING, "Warning", "Select a user first."); return; }
            try {
                UserProfileController.setTargetCnp(selected.getCnp());
                App.navigateTo("user_profile.fxml", "SLIM - Profile: " + selected.getName());
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });

        Button borrowBtn = new Button("Borrow Book");
        borrowBtn.getStyleClass().add("btn-primary");
        borrowBtn.setOnAction(e -> {
            Person selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert(Alert.AlertType.WARNING, "Warning", "Select a user first."); return; }
            showBorrowDialog(selected, table);
        });

        Button returnBtn = new Button("Return Book");
        returnBtn.getStyleClass().add("btn-secondary");
        returnBtn.setOnAction(e -> {
            Person selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert(Alert.AlertType.WARNING, "Warning", "Select a user first."); return; }
            try {
                rentalController.returnBook(selected.getCnp());
                table.setItems(FXCollections.observableArrayList(personController.getAllPersons()));
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });

        actions.getChildren().addAll(addBtn, editBtn, deleteBtn, profileBtn, borrowBtn, returnBtn);
        pane.getChildren().addAll(title, table, actions);
        return pane;
    }

    private void showPersonDialog(Person existing, TableView<Person> table) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add User" : "Edit User");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField cnpF = new TextField(existing != null ? existing.getCnp() : "");
        cnpF.setDisable(existing != null);
        TextField nameF = new TextField(existing != null ? existing.getName() : "");

        grid.add(new Label("CNP:"), 0, 0); grid.add(cnpF, 1, 0);
        grid.add(new Label("Name:"), 0, 1); grid.add(nameF, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    if (existing == null) {
                        personController.addPerson(cnpF.getText(), nameF.getText());
                    } else {
                        personController.updatePerson(existing.getCnp(), nameF.getText());
                    }
                    table.setItems(FXCollections.observableArrayList(personController.getAllPersons()));
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
                }
            }
        });
    }

    private void showBorrowDialog(Person person, TableView<Person> table) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Borrow Book for " + person.getName());

        List<Book> available = bookController.getFilteredBooks("All", "Available", "Default");
        ComboBox<Book> bookCombo = new ComboBox<>(FXCollections.observableArrayList(available));
        bookCombo.setPromptText("Select a book");
        bookCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Book b, boolean empty) {
                super.updateItem(b, empty);
                setText(empty || b == null ? "" : b.getTitle() + " — " + b.getAuthor());
            }
        });
        bookCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Book b, boolean empty) {
                super.updateItem(b, empty);
                setText(empty || b == null ? "" : b.getTitle() + " — " + b.getAuthor());
            }
        });

        VBox content = new VBox(8, new Label("Available books:"), bookCombo);
        content.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                Book selected = bookCombo.getValue();
                if (selected == null) { showAlert(Alert.AlertType.WARNING, "Warning", "Select a book."); return; }
                try {
                    rentalController.borrowBook(person.getCnp(), selected.getId());
                    table.setItems(FXCollections.observableArrayList(personController.getAllPersons()));
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Book borrowed successfully.");
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
                }
            }
        });
    }

    private VBox buildRentalsPane() {
        VBox pane = new VBox(12);
        pane.setPadding(new Insets(20));
        pane.getStyleClass().add("content-pane");

        Label title = new Label("Active Rentals");
        title.getStyleClass().add("section-title");

        TableView<Rental> table = new TableView<>();
        table.getStyleClass().add("table-slim");

        TableColumn<Rental, String> personCol = new TableColumn<>("User");
        personCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPerson().getName()));
        personCol.setPrefWidth(180);

        TableColumn<Rental, String> cnpCol = new TableColumn<>("CNP");
        cnpCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPerson().getCnp()));
        cnpCol.setPrefWidth(140);

        TableColumn<Rental, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBook().getTitle()));
        bookCol.setPrefWidth(220);

        TableColumn<Rental, String> borrowedCol = new TableColumn<>("Borrowed");
        borrowedCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBorrowDate().toString()));
        borrowedCol.setPrefWidth(110);

        TableColumn<Rental, String> dueCol = new TableColumn<>("Due Date");
        dueCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDueDate().toString()));
        dueCol.setPrefWidth(110);

        table.getColumns().addAll(personCol, cnpCol, bookCol, borrowedCol, dueCol);
        table.setItems(FXCollections.observableArrayList(rentalController.getAllActiveRentals()));
        VBox.setVgrow(table, Priority.ALWAYS);

        pane.getChildren().addAll(title, table);
        return pane;
    }

    private VBox buildImportPane() {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(30));
        pane.getStyleClass().add("content-pane");

        Label title = new Label("Import Books from CSV");
        title.getStyleClass().add("section-title");

        Label info = new Label("CSV format: title,author,category (first row is header and will be skipped)");
        info.getStyleClass().add("info-label");

        Label example = new Label("Example:\ntitle,author,category\nDune,Frank Herbert,Sci-Fi\nFoundation,Isaac Asimov,Sci-Fi");
        example.getStyleClass().add("code-label");
        example.setStyle("-fx-font-family: monospace; -fx-padding: 10; -fx-background-color: #f0f0f0; -fx-border-radius: 4; -fx-background-radius: 4;");

        Label resultLabel = new Label();
        resultLabel.getStyleClass().add("info-label");

        Button chooseBtn = new Button("Choose CSV File");
        chooseBtn.getStyleClass().add("btn-primary");
        chooseBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select CSV File");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = chooser.showOpenDialog(App.getPrimaryStage());
            if (file != null) {
                try {
                    int count = bookController.importFromCsv(file);
                    resultLabel.setText("Successfully imported " + count + " books.");
                    resultLabel.setStyle("-fx-text-fill: green;");
                } catch (Exception ex) {
                    resultLabel.setText("Import failed: " + ex.getMessage());
                    resultLabel.setStyle("-fx-text-fill: red;");
                }
            }
        });

        pane.getChildren().addAll(title, info, example, chooseBtn, resultLabel);
        return pane;
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
