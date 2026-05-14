package org.example.services.impl;

import jakarta.transaction.Transactional;
import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class AuthService implements org.example.services.AuthServiceInterface {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private String hashPassword(String password) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }

    @Override
    public boolean register(String login, String password) {
        if(this.userRepository.findByLogin(login).isPresent())
            return false;

        if(login == null || login.isBlank() || password == null || password.isBlank())
            return false;

        String passwordHash = hashPassword(password);
        User user = new User(null, login, passwordHash, Role.USER);
        this.userRepository.save(user);
        return true;
    }

    @Override
    public Optional<User> login(String login, String password) {
        Optional<User> opt = this.userRepository.findByLogin(login);
        if(opt.isPresent()){
            User user = opt.get();
            if(!BCrypt.checkpw(password, user.getPasswordHash()))
                return Optional.empty();
        }
        return opt;
    }
}
