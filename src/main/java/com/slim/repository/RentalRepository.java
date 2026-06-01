package com.slim.repository;

import com.slim.domain.Person;
import com.slim.domain.Rental;
import com.slim.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class RentalRepository {

    public void save(Rental rental) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(rental);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public Rental findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Rental.class, id);
        }
    }

    public List<Rental> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Rental", Rental.class).list();
        }
    }

    public List<Rental> findByPerson(Person person) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Rental where person = :p", Rental.class)
                    .setParameter("p", person)
                    .list();
        }
    }

    public Rental findActiveByPerson(Person person) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Rental where person = :p and returnDate is null", Rental.class)
                    .setParameter("p", person)
                    .uniqueResult();
        }
    }

    public List<Rental> findActive() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Rental where returnDate is null", Rental.class).list();
        }
    }
}
