package org.example.services.impl;

import org.example.db.HibernateConfig;
import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.impl.UserHibernateRepository;
import org.example.services.AuthServiceInterface;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.UUID;

public class AuthHibernateService implements AuthServiceInterface {
    private final UserHibernateRepository userRepository;

    public AuthHibernateService(UserHibernateRepository userRepository) {
        this.userRepository = userRepository;
    }

    private String hashPassword(String password) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }

    @Override
    public boolean register(String login, String rawPassword) {
        if(login == null || login.isBlank() || rawPassword == null || rawPassword.isBlank())
            return false;

        Transaction tx = null;
        Session session = HibernateConfig.getSessionFactory().openSession();

        try{
            tx = session.beginTransaction();
            setSession(session);

            if(this.userRepository.findByLogin(login).isPresent())
                return false;

            String passwordHash = hashPassword(rawPassword);
            User user = User.builder()
                    .id(UUID.randomUUID().toString())
                    .login(login)
                    .passwordHash(passwordHash)
                    .role(Role.USER)
                    .build();

            this.userRepository.save(user);
            tx.commit();
            return true;
        }catch(RuntimeException e){
            rollback(tx);
            throw e;
        }finally{
            session.close();
        }
    }

    @Override
    public Optional<User> login(String login, String rawPassword) {
        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            setSession(session);

            Optional<User> opt = this.userRepository.findByLogin(login);
            if(opt.isPresent()){
                User user = opt.get();
                if(!BCrypt.checkpw(rawPassword, user.getPasswordHash()))
                    return Optional.empty();
            }
            return opt;
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
