# SLIM - Diagrama de Secventa pentru Imprumut Carte (Borrow Book)

Acest document prezinta diagrama de secventa pentru procesul de imprumut al unei carti in sistemul SLIM, ilustrand interactiunile dintre administratorul bibliotecii, nivelul de prezentare, logica de business (servicii) si accesul la baza de date.

## Cazuri de Utilizare Asociate

- **UC-3: Imprumuta Carte** - Fluxul principal
- **UC-4: Verificare Stare Utilizator (Blacklist)** - Inclus in fluxul de imprumut

## Diagrama de Secventa

```mermaid
sequenceDiagram
    actor Admin as Utilizator (Admin)
    participant UI as Prezentare (UI)
    participant RentalCtrl as Nivel Control<br/>(RentalController)
    participant RentalSvc as Nivel Business<br/>(RentalService)
    participant PersonRepo as Nivel Date<br/>(PersonRepository)
    participant BookRepo as Nivel Date<br/>(BookRepository)
    participant RentalRepo as Nivel Date<br/>(RentalRepository)
    participant DB as Baza de Date

    Admin->>UI: Selecteaza utilizatorul (CNP) si cartea (bookId), apasa "Imprumuta"
    activate UI
    UI->>RentalCtrl: borrowBook(cnp, bookId)
    activate RentalCtrl

    RentalCtrl->>RentalSvc: borrowBook(cnp, bookId)
    activate RentalSvc

    Note over RentalSvc,DB: 1. Validare Utilizator (Person)
    RentalSvc->>PersonRepo: findByCnp(cnp)
    activate PersonRepo
    PersonRepo->>DB: SELECT * FROM persons WHERE cnp = ?
    activate DB
    DB-->>PersonRepo: Inregistrare Person
    deactivate DB
    PersonRepo-->>RentalSvc: Obiect Person
    deactivate PersonRepo

    alt Utilizatorul nu a fost gasit
        RentalSvc-->>RentalCtrl: throw IllegalArgumentException("Person not found.")
        RentalCtrl-->>UI: Eroare: Utilizator inexistent
        UI-->>Admin: Afiseaza mesaj de eroare
    else Utilizator pe lista neagra (Blacklisted)
        RentalSvc->>RentalSvc: check person.isBlacklisted()
        RentalSvc-->>RentalCtrl: throw IllegalStateException("User is blacklisted...")
        RentalCtrl-->>UI: Eroare: Utilizator blocat
        UI-->>Admin: Afiseaza mesaj blacklist
    else Utilizatorul are deja o carte
        RentalSvc->>RentalSvc: check person.getBorrowedBook() != null
        RentalSvc-->>RentalCtrl: throw IllegalStateException("User already has an active borrowed book.")
        RentalCtrl-->>UI: Eroare: Limita atinsa
        UI-->>Admin: Afiseaza mesaj de eroare
    else Utilizator Valid

        Note over RentalSvc,DB: 2. Validare Carte (Book)
        RentalSvc->>BookRepo: findById(bookId)
        activate BookRepo
        BookRepo->>DB: SELECT * FROM books WHERE id = ?
        activate DB
        DB-->>BookRepo: Inregistrare Book
        deactivate DB
        BookRepo-->>RentalSvc: Obiect Book
        deactivate BookRepo

        alt Cartea nu a fost gasita
            RentalSvc-->>RentalCtrl: throw IllegalArgumentException("Book not found.")
            RentalCtrl-->>UI: Eroare: Carte inexistenta
            UI-->>Admin: Afiseaza mesaj de eroare
        else Cartea este deja imprumutata
            RentalSvc->>RentalSvc: check book.isBorrowed()
            RentalSvc-->>RentalCtrl: throw IllegalStateException("Book is already borrowed.")
            RentalCtrl-->>UI: Eroare: Carte indisponibila
            UI-->>Admin: Afiseaza mesaj de eroare
        else Carte Disponibila

            Note over RentalSvc,DB: 3. Finalizare Imprumut si Salvare Date
            RentalSvc->>RentalSvc: book.setBorrowed(true)
            RentalSvc->>BookRepo: save(book)
            activate BookRepo
            BookRepo->>DB: UPDATE books SET isBorrowed = true WHERE id = ?
            BookRepo-->>RentalSvc: Succes
            deactivate BookRepo

            RentalSvc->>RentalSvc: person.setBorrowedBook(book)
            RentalSvc->>PersonRepo: save(person)
            activate PersonRepo
            PersonRepo->>DB: UPDATE persons SET borrowedBook_id = ? WHERE id = ?
            PersonRepo-->>RentalSvc: Succes
            deactivate PersonRepo

            RentalSvc->>RentalSvc: Creare instanta noua Rental(person, book, now)
            RentalSvc->>RentalRepo: save(rental)
            activate RentalRepo
            RentalRepo->>DB: INSERT INTO rentals (person_id, book_id, borrowDate, ...)
            RentalRepo-->>RentalSvc: Succes
            deactivate RentalRepo

            RentalSvc-->>RentalCtrl: Returneaza obiectul Rental creat
            RentalCtrl-->>UI: Succes
            UI-->>Admin: Afiseaza confirmarea imprumutului si actualizeaza interfata
            deactivate UI
        end
    end
    deactivate RentalSvc
    deactivate RentalCtrl
```

## Descrierea Secventei

### Fluxul Normal (Succes)

1. **Initierea imprumutului**
    - Administratorul selecteaza un utilizator pe baza CNP-ului, o carte pe baza ID-ului si apasa butonul de imprumut.
    - Nivelul UI apeleaza metoda `borrowBook` din `RentalController`.

2. **Validarea Utilizatorului**
    - `RentalService` obtine utilizatorul din baza de date prin `PersonRepository.findByCnp()`.
    - Se verifica daca utilizatorul exista.
    - Se verifica starea contului: nu trebuie sa fie pe lista neagra (`isBlacklisted() == false`).
    - Se verifica limita de imprumut: utilizatorul nu are voie sa aiba deja o carte imprumutata (`getBorrowedBook() == null`).

3. **Validarea Cartii**
    - `RentalService` obtine cartea din baza de date prin `BookRepository.findById()`.
    - Se verifica daca obiectul carte exista.
    - Se confirma disponibilitatea: cartea nu trebuie sa fie deja imprumutata (`isBorrowed() == false`).

4. **Actualizarea Starii si Persistenta**
    - Starea cartii se modifica in "imprumutata" (`book.setBorrowed(true)`) si este salvata prin `BookRepository`.
    - Utilizatorului i se asociaza cartea curenta (`person.setBorrowedBook(book)`) si modificarea este salvata prin `PersonRepository`.
    - Se instantiaza un nou obiect `Rental` care retine legatura dintre persoana, carte si data curenta (`LocalDate.now()`).
    - Noul imprumut este salvat definitiv in baza de date prin `RentalRepository`.

5. **Afisarea Rezultatului**
    - Obiectul creat se intoarce la UI, care informeaza administratorul de succesul operatiunii si actualizeaza datele pe ecran.

### Fluxuri de Exceptie (Erori)

#### E1: Utilizator Invalid sau Blocat
- Daca persoana nu e gasita, se arunca `IllegalArgumentException`.
- Daca e pe lista neagra, se arunca `IllegalStateException`.
- Operatiunea se intrerupe, iar UI-ul afiseaza eroarea.

#### E2: Limita de Carti Atinsa
- Daca utilizatorul are deja o carte, se arunca `IllegalStateException`. Operatiunea este respinsa automat.

#### E3: Carte Indisponibila
- Daca entitatea Book nu e gasita sau este deja marcata ca fiind imprumutata de altcineva, procesul este anulat printr-o exceptie, protejand consistenta bazei de date.

## Componente Cheie

### Nivelul Business (Business Layer)
- **RentalService**: Contine logica centrala, orchestrarea verificarilor si modificarea starilor pe trei entitati diferite (Person, Book, Rental).

### Nivelul de Date (Data Layer)
- **PersonRepository**: Gestioneaza citirea si scrierea detaliilor abonatilor.
- **BookRepository**: Gestioneaza inventarul bibliotecii si statusul cartilor.
- **RentalRepository**: Retine istoricul de tranzactii si imprumuturile active.

## Reguli de Business Enforced (Implementate)

1. **Restrictie la imprumut (Limita stricta)**: Un utilizator poate imprumuta **o singura carte simultan**.
2. **Preventia penalizarilor (Blacklist)**: Utilizatorii aflati sub incidenta penalizarii (delays depasite) nu pot face imprumuturi noi pana la expirarea perioadei.
3. **Consistenta Datelor**: Starea cartii si starea utilizatorului sunt sincronizate in acelasi context inainte de crearea fisei de imprumut (Rental).