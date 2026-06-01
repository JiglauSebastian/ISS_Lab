package com.slim.controller;

import com.slim.domain.Rental;
import com.slim.service.RentalService;

import java.util.List;

public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    public Rental borrowBook(String cnp, Long bookId) {
        return rentalService.borrowBook(cnp, bookId);
    }

    public void returnBook(String cnp) {
        rentalService.returnBook(cnp);
    }

    public List<Rental> getRentalHistory(String cnp) {
        return rentalService.getRentalHistory(cnp);
    }

    public Rental getActiveRental(String cnp) {
        return rentalService.getActiveRental(cnp);
    }

    public long getDaysUntilDue(String cnp) {
        return rentalService.getDaysUntilDue(cnp);
    }

    public List<Rental> getAllActiveRentals() {
        return rentalService.getAllActiveRentals();
    }
}
