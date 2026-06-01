# SLIM - Login Sequence Diagram

This document presents the sequence diagram for the login process in the SLIM system, illustrating the interactions between the admin, presentation layer, business layer, and data layer.

## Related Use Cases

- **UC-1: Admin Login** - Primary use case

## Sequence Diagram

```mermaid
sequenceDiagram
    actor Admin
    participant UI as Presentation Layer<br/>(LoginController)
    participant AuthCtrl as Controller Layer<br/>(AuthController)
    participant AuthService as Business Layer<br/>(AuthService)
    participant AdminRepo as Data Layer<br/>(AdminRepository)
    participant DB as Database

    Admin->>UI: Navigate to login page
    activate UI
    UI-->>Admin: Display login form
    deactivate UI

    Admin->>UI: Enter username and password
    activate UI
    UI->>AuthCtrl: login(username, password)
    activate AuthCtrl

    AuthCtrl->>AuthService: login(username, password)
    activate AuthService

    AuthService->>AdminRepo: findByUsername(username)
    activate AdminRepo
    AdminRepo->>DB: SELECT * FROM admins WHERE username = ?
    activate DB
    DB-->>AdminRepo: Admin record
    deactivate DB
    AdminRepo-->>AuthService: Admin object
    deactivate AdminRepo

    alt Admin not found
        AuthService-->>AuthCtrl: return false
        AuthCtrl-->>UI: return false
        UI-->>Admin: Display error message
        Note over Admin,UI: Return to login form
    else Valid Admin found
        AuthService->>AuthService: validatePassword(password, storedPassword)

        alt Password invalid
            AuthService-->>AuthCtrl: return false
            AuthCtrl-->>UI: return false
            UI-->>Admin: Display error message
            Note over Admin,UI: Return to login form
        else Password valid
            AuthService->>AuthService: Set loggedInAdmin = admin
            
            AuthService-->>AuthCtrl: return true
            AuthCtrl-->>UI: return true

            UI->>UI: App.navigateTo("admin_dashboard.fxml")
            UI-->>Admin: Display main dashboard
            deactivate UI
        end
    end
    deactivate AuthService
    deactivate AuthCtrl
```

## Sequence Description

### Normal Flow

1. **Admin navigates to login page**
    - Admin requests access to the SLIM system
    - Presentation layer displays the login form

2. **Admin enters credentials**
    - Admin inputs username and password
    - Presentation layer sends credentials to AuthController, which delegates them to the AuthService

3. **User lookup**
    - AuthService requests AdminRepository to find the admin by username
    - AdminRepository queries the database for the admin record
    - Database returns admin data (if found)

4. **Credential validation**
    - AuthService validates the provided password against the stored password
    - If valid, the admin object is retained in memory as the currently active user

5. **Display results**
    - Successful authentication boolean is returned back through the controller to the UI
    - Presentation layer triggers navigation to the admin dashboard
    - Main dashboard is displayed

### Exception Flows

#### E1: Invalid Credentials

- Admin not found in the database or the password doesn't match
- Authentication process returns false
- Generic error message ("Invalid username or password.") is displayed to the admin
- Use case terminates, returning the user to the login form

## Key Components

### Presentation Layer

- **UI**: Handles user interface, form display, and triggers system navigation via LoginController
- **AuthController**: Intermediary controller routing the authentication requests

### Business Layer

- **AuthService**: Manages core authentication logic, credential validation, and maintains the `loggedInAdmin` state session

### Data Layer

- **AdminRepository**: Data access for admin entities
- **Database**: Persistent storage

## Business Rules Enforced

1. **Session State**: System access requires a successfully validated admin to be stored in the AuthService's `loggedInAdmin` state.
2. **Simplified Validation**: For local testing and simplified environments, plain password matching logic is utilized.

## Security Considerations

- Generic error messages are enforced on the UI layer to prevent username enumeration, ensuring attackers cannot distinguish whether the username or the password was the incorrect element.