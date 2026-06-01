# SLIM - Simple Library Interface Management

## Requirements
- Java 17+
- Maven 3.6+

## Build & Run

```bash
mvn clean package -DskipTests
java -jar target/slim-1.0-SNAPSHOT-shaded.jar
```

Or with the JavaFX plugin:
```bash
mvn javafx:run
```

## Default Admin Credentials
- **Username:** `admin`
- **Password:** `admin123`

## Project Structure

```
src/main/java/com/slim/
├── domain/         # JPA entities: Book, Person, Admin, Rental
├── repository/     # Hibernate data access layer
├── service/        # Business logic layer
├── controller/     # Presentation controllers (bridge between UI and service)
├── ui/             # JavaFX App + FXML controllers + AppContext
└── utils/          # HibernateUtil, PasswordUtil, DataSeeder

src/main/resources/
├── hibernate.cfg.xml
└── com/slim/ui/
    ├── login.fxml
    ├── admin_dashboard.fxml
    ├── user_profile.fxml
    └── style.css
```

## Features by Iteration

### Iteration 1 (Lab 4–5): F1 + F2
- F1: CRUD for Books and Persons (admin side)
- F2: Admin login/logout with session

### Iteration 2 (Lab 6): F3 + F4
- F3: Catalog view with sorting and filtering (by category, status, title, author)
- F4: Book rental — borrow with single-book limit + 2-week fixed period

### Iteration 3 (Lab 7 – to complete): F5 + F6 + F7
- F5: Blacklist system — delay tracking, >2 delays → blacklisted 6 months
- F6: User profile — current rental, days remaining, reading history, account standing
- F7: CSV book import

## Database
SQLite file `slim_library.db` is created automatically in the working directory on first run.

## CSV Import Format
```
title,author,category
Dune,Frank Herbert,Sci-Fi
Foundation,Isaac Asimov,Sci-Fi
```
