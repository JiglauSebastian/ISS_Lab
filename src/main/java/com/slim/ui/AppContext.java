package com.slim.ui;

import com.slim.controller.AuthController;
import com.slim.controller.BookController;
import com.slim.controller.PersonController;
import com.slim.controller.RentalController;
import com.slim.repository.AdminRepository;
import com.slim.repository.BookRepository;
import com.slim.repository.PersonRepository;
import com.slim.repository.RentalRepository;
import com.slim.service.AuthService;
import com.slim.service.BookService;
import com.slim.service.PersonService;
import com.slim.service.RentalService;
import com.slim.utils.DataSeeder;

public class AppContext {

    private static AppContext instance;

    private final AuthController authController;
    private final BookController bookController;
    private final PersonController personController;
    private final RentalController rentalController;

    private AppContext() {
        AdminRepository adminRepository = new AdminRepository();
        BookRepository bookRepository = new BookRepository();
        PersonRepository personRepository = new PersonRepository();
        RentalRepository rentalRepository = new RentalRepository();

        AuthService authService = new AuthService(adminRepository);
        BookService bookService = new BookService(bookRepository);
        PersonService personService = new PersonService(personRepository);
        RentalService rentalService = new RentalService(rentalRepository, bookRepository, personRepository);

        authController = new AuthController(authService);
        bookController = new BookController(bookService);
        personController = new PersonController(personService);
        rentalController = new RentalController(rentalService);

        DataSeeder seeder = new DataSeeder(adminRepository, bookRepository, personRepository);
        seeder.seed();
    }

    public static AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public AuthController getAuthController() { return authController; }
    public BookController getBookController() { return bookController; }
    public PersonController getPersonController() { return personController; }
    public RentalController getRentalController() { return rentalController; }
}
