package com.slim.service;

import com.slim.domain.Book;
import com.slim.repository.BookRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void addBook(String title, String author, String category) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title is required.");
        if (author == null || author.isBlank()) throw new IllegalArgumentException("Author is required.");
        if (category == null || category.isBlank()) throw new IllegalArgumentException("Category is required.");
        bookRepository.save(new Book(title, author, category));
    }

    public void updateBook(Long id, String title, String author, String category) {
        Book book = bookRepository.findById(id);
        if (book == null) throw new IllegalArgumentException("Book not found.");
        book.setTitle(title);
        book.setAuthor(author);
        book.setCategory(category);
        bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id);
        if (book == null) throw new IllegalArgumentException("Book not found.");
        if (book.isBorrowed()) throw new IllegalStateException("Cannot delete a book that is currently borrowed.");
        bookRepository.delete(id);
    }

    public Book findById(Long id) {
        return bookRepository.findById(id);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findAvailable();
    }

    public List<Book> filterByCategory(String category) {
        if (category == null || category.isBlank()) return bookRepository.findAll();
        return bookRepository.findByCategory(category);
    }

    public List<Book> filterByStatus(String status) {
        return switch (status) {
            case "Available" -> bookRepository.findAll().stream()
                    .filter(b -> !b.isBorrowed()).collect(Collectors.toList());
            case "Borrowed" -> bookRepository.findAll().stream()
                    .filter(Book::isBorrowed).collect(Collectors.toList());
            default -> bookRepository.findAll();
        };
    }

    public List<Book> getSortedBooks(String sortBy) {
        List<Book> books = bookRepository.findAll();
        return switch (sortBy) {
            case "Title" -> books.stream().sorted(Comparator.comparing(Book::getTitle)).collect(Collectors.toList());
            case "Author" -> books.stream().sorted(Comparator.comparing(Book::getAuthor)).collect(Collectors.toList());
            case "Category" -> books.stream().sorted(Comparator.comparing(Book::getCategory)).collect(Collectors.toList());
            default -> books;
        };
    }

    public List<String> getAllCategories() {
        return bookRepository.findAllCategories();
    }

    public int importFromCsv(File file) throws Exception {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                String[] parts = line.split(",");
                if (parts.length < 3) continue;
                String title = parts[0].trim();
                String author = parts[1].trim();
                String category = parts[2].trim();
                if (!title.isBlank() && !author.isBlank() && !category.isBlank()) {
                    bookRepository.save(new Book(title, author, category));
                    count++;
                }
            }
        }
        return count;
    }
}
