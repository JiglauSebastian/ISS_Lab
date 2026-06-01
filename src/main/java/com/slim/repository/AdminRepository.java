package com.slim.repository;

import com.slim.domain.Admin;
import com.slim.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class AdminRepository {

    public void save(Admin admin) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(admin);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public Admin findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Admin.class, username);
        }
    }
}
