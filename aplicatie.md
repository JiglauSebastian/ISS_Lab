# Library Management System - Project Specification

**Project Code:** SLIM (Simple Library Interface Management)

## Overview

A simplified library management system designed strictly for end-users, allowing them to browse, sort, and rent books. The system focuses on the user experience and does not include complex administrative interfaces. Data is managed through basic operations and file imports.

## Entities (Data Model)

- **Book:** `id`, `title`, `author`, `isBorrowed` (boolean), `category`
- **Person (User):** `id`, `name`, `borrowedBook` (link to Book), `delays` (count of late returns)
- **Rental (Transaction):** `id`, `person_id`, `book_id`, `borrow_date`, `due_date`, `return_date`

## Features

### F1. Data Management (CRUD)
- **Books & Persons:** Basic Create, Read, Update, and Delete operations to manage book records and user accounts within the application.

### F2. Authentication
- **Login:** Users must log in with their credentials to access the library catalog and their personal profile.
- **Logout:** Users can securely end their active session.

### F3. Catalog & Sorting
- **View Books:** Users can browse the library's catalog.
- **Sorting & Filtering:** Users can sort and filter the book list by current status (available/borrowed) and by specific categories or genres.

### F4. Book Rental
- **Single Book Limit:** A user can only borrow a maximum of **1 book** at any given time.
- **Fixed Period:** Books are rented for a period of 2 weeks.
- **Validation:** The system blocks the rental action if the user already has an active borrowed book.

### F5. Blacklist System
- **Delay Tracking:** If a user returns a book after the due date, their `delays` counter increases by 1.
- **Borrowing Ban:** If a user accumulates more than 2 delays (`delays > 2`), they are added to a Blacklist. Blacklisted users are permanently blocked from borrowing new books.

### F6. Book Import
- **File Import:** The book inventory can be easily populated or updated by importing external files (e.g., CSV or JSON formats).

## Non-Functional Requirements
