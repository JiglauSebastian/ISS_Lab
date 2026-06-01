package com.slim.service;

import com.slim.domain.Person;
import com.slim.repository.PersonRepository;

import java.util.List;

public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public void addPerson(String cnp, String name) {
        if (cnp == null || cnp.isBlank()) throw new IllegalArgumentException("CNP is required.");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required.");
        if (personRepository.findByCnp(cnp) != null) throw new IllegalArgumentException("A person with this CNP already exists.");
        personRepository.save(new Person(cnp, name));
    }

    public void updatePerson(String cnp, String name) {
        Person person = personRepository.findByCnp(cnp);
        if (person == null) throw new IllegalArgumentException("Person not found.");
        person.setName(name);
        personRepository.save(person);
    }

    public void deletePerson(String cnp) {
        Person person = personRepository.findByCnp(cnp);
        if (person == null) throw new IllegalArgumentException("Person not found.");
        if (person.getBorrowedBook() != null) throw new IllegalStateException("Cannot delete a person with an active rental.");
        personRepository.delete(cnp);
    }

    public Person findByCnp(String cnp) {
        return personRepository.findByCnp(cnp);
    }

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }
}
