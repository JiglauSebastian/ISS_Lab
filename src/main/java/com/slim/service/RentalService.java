package com.slim.service;

import com.slim.domain.Book;
import com.slim.domain.Person;
import com.slim.domain.Rental;
import com.slim.repository.BookRepository;
import com.slim.repository.PersonRepository;
import com.slim.repository.RentalRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RentalService {

    private static final int MAX_DELAYS_BEFORE_BLACKLIST = 2;
    private static final int BLACKLIST_MONTHS = 6;

    private final RentalRepository rentalRepository;
    private final BookRepository bookRepository;
    private final PersonRepository personRepository;

    public RentalService(RentalRepository rentalRepository, BookRepository bookRepository, PersonRepository personRepository) {
        this.rentalRepository = rentalRepository;
        this.bookRepository = bookRepository;
        this.personRepository = personRepository;
    }

    public Rental borrowBook(String cnp, Long bookId) {
        Person person = personRepository.findByCnp(cnp);
        if (person == null) throw new IllegalArgumentException("Person not found.");

        if (person.isBlacklisted()) {
            throw new IllegalStateException("User is blacklisted until " + person.getBlacklistedUntil() + ".");
        }

        if (person.getBorrowedBook() != null) {
            throw new IllegalStateException("User already has an active borrowed book.");
        }

        Book book = bookRepository.findById(bookId);
        if (book == null) throw new IllegalArgumentException("Book not found.");
        if (book.isBorrowed()) throw new IllegalStateException("Book is already borrowed.");

        book.setBorrowed(true);
        bookRepository.save(book);

        person.setBorrowedBook(book);
        personRepository.save(person);

        Rental rental = new Rental(person, book, LocalDate.now());
        rentalRepository.save(rental);
        return rental;
    }

    public void returnBook(String cnp) {
        Person person = personRepository.findByCnp(cnp);
        if (person == null) throw new IllegalArgumentException("Person not found.");

        Rental rental = rentalRepository.findActiveByPerson(person);
        if (rental == null) throw new IllegalStateException("No active rental found for this user.");

        LocalDate today = LocalDate.now();
        rental.setReturnDate(today);

        if (rental.wasLate()) {
            person.setDelays(person.getDelays() + 1);
            if (person.getDelays() > MAX_DELAYS_BEFORE_BLACKLIST) {
                person.setBlacklistedUntil(today.plusMonths(BLACKLIST_MONTHS));
            }
        }

        Book book = rental.getBook();
        book.setBorrowed(false);
        bookRepository.save(book);

        person.setBorrowedBook(null);
        personRepository.save(person);

        rentalRepository.save(rental);
    }

    public List<Rental> getRentalHistory(String cnp) {
        Person person = personRepository.findByCnp(cnp);
        if (person == null) throw new IllegalArgumentException("Person not found.");
        return rentalRepository.findByPerson(person);
    }

    public Rental getActiveRental(String cnp) {
        Person person = personRepository.findByCnp(cnp);
        if (person == null) return null;
        return rentalRepository.findActiveByPerson(person);
    }

    public long getDaysUntilDue(String cnp) {
        Rental rental = getActiveRental(cnp);
        if (rental == null) return -1;
        return ChronoUnit.DAYS.between(LocalDate.now(), rental.getDueDate());
    }

    public List<Rental> getAllActiveRentals() {
        return rentalRepository.findActive();
    }
}
