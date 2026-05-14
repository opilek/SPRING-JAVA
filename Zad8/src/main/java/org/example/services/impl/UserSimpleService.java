package org.example.services.impl;

import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserSimpleService implements org.example.services.UserServiceInterface {
    private final UserRepository userRepository;

    private final RentalSimpleService rentalService;

    public UserSimpleService(UserRepository userRepository, RentalSimpleService rentalService) {
        this.userRepository = userRepository;
        this.rentalService = rentalService;
    }

    @Override
    public List<User> findAllUsers(){
        return this.userRepository.findAll();
    }

    @Override
    public User findById(String userId){
        return this.userRepository.findById(userId).get();
    }

    @Override
    public void deleteUser(String userId, String adminId){
        if(!findById(adminId).getRole().equals(Role.ADMIN))
            throw new IllegalArgumentException("Deleting user must be admin");

        if(rentalService.userHasActiveRental(userId))
            throw new IllegalArgumentException("This user is renting");

        Optional<User> opt = this.userRepository.findById(userId);
        if(opt.isEmpty())
            throw new IllegalArgumentException("No user with such ID");

        this.userRepository.deleteById(userId);
    }
}
