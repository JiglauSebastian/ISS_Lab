package com.slim.controller;

import com.slim.domain.Person;
import com.slim.service.PersonService;

import java.util.List;

public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    public void addPerson(String cnp, String name) {
        personService.addPerson(cnp, name);
    }

    public void updatePerson(String cnp, String name) {
        personService.updatePerson(cnp, name);
    }

    public void deletePerson(String cnp) {
        personService.deletePerson(cnp);
    }

    public Person findByCnp(String cnp) {
        return personService.findByCnp(cnp);
    }

    public List<Person> getAllPersons() {
        return personService.getAllPersons();
    }
}
