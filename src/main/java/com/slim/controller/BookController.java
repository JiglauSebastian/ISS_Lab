package com.slim.controller;

import com.slim.domain.Book;
import com.slim.service.BookService;

import java.io.File;
import java.util.List;

public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    public void addBook(String title, String author, String category) {
        bookService.addBook(title, author, category);
    }

    public void updateBook(Long id, String title, String author, String category) {
        bookService.updateBook(id, title, author, category);
    }

    public void deleteBook(Long id) {
        bookService.deleteBook(id);
    }

    public Book findById(Long id) {
        return bookService.findById(id);
    }

    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    public List<Book> getFilteredBooks(String category, String status, String sortBy) {
        List<Book> books;

        if (category != null && !category.equals("All")) {
            books = bookService.filterByCategory(category);
        } else {
            books = bookService.getAllBooks();
        }

        if (status != null && !status.equals("All")) {
            boolean borrowed = status.equals("Borrowed");
            books = books.stream().filter(b -> b.isBorrowed() == borrowed).toList();
        }

        if (sortBy != null && !sortBy.equals("Default")) {
            books = switch (sortBy) {
                case "Title" -> books.stream().sorted((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle())).toList();
                case "Author" -> books.stream().sorted((a, b) -> a.getAuthor().compareToIgnoreCase(b.getAuthor())).toList();
                case "Category" -> books.stream().sorted((a, b) -> a.getCategory().compareToIgnoreCase(b.getCategory())).toList();
                default -> books;
            };
        }

        return books;
    }

    public List<String> getAllCategories() {
        return bookService.getAllCategories();
    }

    public int importFromCsv(File file) throws Exception {
        return bookService.importFromCsv(file);
    }
}
