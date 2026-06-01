package com.slim.utils;

import com.slim.domain.Admin;
import com.slim.domain.Book;
import com.slim.domain.Person;
import com.slim.repository.AdminRepository;
import com.slim.repository.BookRepository;
import com.slim.repository.PersonRepository;

public class DataSeeder {

    private final AdminRepository adminRepository;
    private final BookRepository bookRepository;
    private final PersonRepository personRepository;

    public DataSeeder(AdminRepository adminRepository, BookRepository bookRepository, PersonRepository personRepository) {
        this.adminRepository = adminRepository;
        this.bookRepository = bookRepository;
        this.personRepository = personRepository;
    }

    public void seed() {
        if (adminRepository.findByUsername("admin") == null) {
            adminRepository.save(new Admin("admin", "admin123", "Administrator", "Cluj-Napoca"));
        }

        if (bookRepository.findAll().isEmpty()) {
            bookRepository.save(new Book("The Great Gatsby", "F. Scott Fitzgerald", "Fiction"));
            bookRepository.save(new Book("To Kill a Mockingbird", "Harper Lee", "Fiction"));
            bookRepository.save(new Book("1984", "George Orwell", "Dystopia"));
            bookRepository.save(new Book("Brave New World", "Aldous Huxley", "Dystopia"));
            bookRepository.save(new Book("Clean Code", "Robert C. Martin", "Programming"));
            bookRepository.save(new Book("The Pragmatic Programmer", "David Thomas", "Programming"));
            bookRepository.save(new Book("Sapiens", "Yuval Noah Harari", "Non-Fiction"));
            bookRepository.save(new Book("Dune", "Frank Herbert", "Sci-Fi"));
        }

        if (personRepository.findAll().isEmpty()) {
            personRepository.save(new Person("1900101123456", "Ion Popescu"));
            personRepository.save(new Person("2950202234567", "Maria Ionescu"));
        }
    }
}
