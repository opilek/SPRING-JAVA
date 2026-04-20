package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.RentalRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.UUID;

public class UserService {
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    public UserService(UserRepository userRepository, RentalRepository rentalRepository) {
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    public User register(String login, String password, Role role) {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new IllegalArgumentException("Login jest już zajęty.");
        }
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        User newUser = new User(UUID.randomUUID().toString(), login, hashed, role);
        return userRepository.save(newUser);
    }

    public User login(String login, String password) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Nieprawidłowy login lub hasło."));

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new IllegalArgumentException("Nieprawidłowy login lub hasło.");
        }
        return user;
    }

    public void deleteUserByLogin(String login) {
        // 1. Szukamy użytkownika po loginie
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika o loginie: " + login));

        // 2. Blokada usuwania Admina (nawet po loginie)
        if (user.getRole() == Role.ADMIN) {
            throw new IllegalStateException("BŁĄD: Nie można usunąć użytkownika z uprawnieniami ADMIN.");
        }

        // 3. Sprawdzanie aktywnych wypożyczeń (korzystając z ID znalezionego usera)
        boolean hasActiveRentals = rentalRepository.findAll().stream()
                .anyMatch(r -> r.getUserId().equals(user.getId()) && r.isActive());

        if (hasActiveRentals) {
            throw new IllegalStateException("BŁĄD: " + login + " ma aktywne wypożyczenie. Musi najpierw zwrócić pojazd!");
        }

        userRepository.deleteById(user.getId());
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}