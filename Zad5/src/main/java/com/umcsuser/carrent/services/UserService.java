package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Rental;
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

        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika o loginie: " + login));


        if (user.getRole() == Role.ADMIN) {
            throw new IllegalStateException("BŁĄD: Nie można usunąć użytkownika z uprawnieniami ADMIN.");
        }


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

    public boolean hasActiveRental(String userId) {
        return rentalRepository.findAll().stream()
                .anyMatch(r -> r.getUserId().equals(userId) && r.isActive());
    }

    public String getActiveVehicleId(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId) && r.isActive())
                .map(Rental::getVehicleId)
                .findFirst()
                .orElse(null);
    }
}