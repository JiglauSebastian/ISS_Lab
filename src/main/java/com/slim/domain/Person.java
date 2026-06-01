package com.slim.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "persons")
public class Person {

    @Id
    @Column(nullable = false, unique = true)
    private String cnp;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "borrowed_book_id")
    private Book borrowedBook;

    @Column(nullable = false)
    private int delays = 0;

    @Column
    private LocalDate blacklistedUntil;

    public Person() {}

    public Person(String cnp, String name) {
        this.cnp = cnp;
        this.name = name;
    }

    public String getCnp() { return cnp; }
    public void setCnp(String cnp) { this.cnp = cnp; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Book getBorrowedBook() { return borrowedBook; }
    public void setBorrowedBook(Book borrowedBook) { this.borrowedBook = borrowedBook; }
    public int getDelays() { return delays; }
    public void setDelays(int delays) { this.delays = delays; }
    public LocalDate getBlacklistedUntil() { return blacklistedUntil; }
    public void setBlacklistedUntil(LocalDate blacklistedUntil) { this.blacklistedUntil = blacklistedUntil; }

    public boolean isBlacklisted() {
        if (blacklistedUntil == null) return false;
        return LocalDate.now().isBefore(blacklistedUntil);
    }
}
