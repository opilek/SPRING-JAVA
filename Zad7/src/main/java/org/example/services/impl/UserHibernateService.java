package org.example.services.impl;

import org.example.db.HibernateConfig;
import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.impl.UserHibernateRepository;
import org.example.services.UserServiceInterface;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserHibernateService implements UserServiceInterface {
    private final UserHibernateRepository userRepository;

    private final RentalHibernateService rentalService;

    public UserHibernateService(UserHibernateRepository userRepository, RentalHibernateService rentalService) {
        this.userRepository = userRepository;
        this.rentalService = rentalService;
    }

    @Override
    public List<User> findAllUsers() {
        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            setSession(session);

            return this.userRepository.findAll();
        }
    }

    @Override
    public User findById(String id) {
        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            setSession(session);

            return this.userRepository.findById(id).get();
        }
    }

    @Override
    public void deleteUser(String id, String loggedUserId) {
        Transaction tx = null;
        Session session = HibernateConfig.getSessionFactory().openSession();

        try{
            if(this.rentalService.userHasActiveRental(id))
                throw new IllegalStateException("Użytkownik ma aktywne wypożyczenie");

            tx = session.beginTransaction();
            setSession(session);

            this.userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika z podanym ID"));

            if(!this.userRepository.findById(loggedUserId).get().getRole().equals(Role.ADMIN))
                throw new IllegalStateException("Musisz być adminem żeby to zrobić");

            this.userRepository.deleteById(id);
            tx.commit();
        }catch(RuntimeException e){
            rollback(tx);
            throw e;
        }finally{
            session.close();
        }
    }

    private void setSession(Session session) {
        userRepository.setSession(session);
    }

    private void rollback(Transaction tx){
        if(tx != null && tx.isActive()){
            tx.rollback();
        }
    }
}
