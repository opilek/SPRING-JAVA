package org.example.services;

import org.apache.commons.codec.digest.DigestUtils;
import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService
{
    private UserRepository userRepo;

    public AuthService(UserRepository userRepo)
    {
        this.userRepo = userRepo;
    }

    public boolean register(String login, String password)
    {
        if( userRepo.findByLogin(login).isPresent())
        {
            return false;
        }

        String id = java.util.UUID.randomUUID().toString();

        String hashedSubmarine = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());

        User newUser = new User(id,login,hashedSubmarine, Role.USER);

        userRepo.save(newUser);

        return true;
    }

    public Optional<User> login(String login, String password)
    {
        Optional<User> userOpt = userRepo.findByLogin(login);

        if(userOpt.isPresent())
        {
           User user = userOpt.get();

            if(org.mindrot.jbcrypt.BCrypt.checkpw(password, user.getPasswordHash()))
            {
                return Optional.of(user);
            }

        }

        return Optional.empty();

    }
}
