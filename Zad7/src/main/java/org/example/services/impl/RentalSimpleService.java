package org.example.services.impl;

import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RentalSimpleService implements org.example.services.RentalServiceInterface {
    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public RentalSimpleService(RentalRepository rentalRepository, VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Rental> findAllRentals(){
        return this.rentalRepository.findAll();
    }

    @Override
    public List<Rental> findUserRentals(String userId){
        List<Rental> all = this.rentalRepository.findAll();
        List<Rental> user = new ArrayList<>();
        for (Rental r : all){
            if(r.getUserId().equals(userId))
                user.add(r);
        }
        return user;
    }

    @Override
    public Rental rentVehicle(String userId, String vehicleId){
        if(findActiveRentalByUserId(userId).isPresent())
            throw new IllegalArgumentException("user is already renting");

        if(this.rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent())
            throw new IllegalArgumentException("this vehicle is already rented");

        Vehicle vehicle = this.vehicleRepository.findById(vehicleId).get();
        User user = this.userRepository.findById(userId).get();

        Rental rental = new Rental(null, vehicle, user, LocalDateTime.now().toString(), null);
        return this.rentalRepository.save(rental);
    }

    @Override
    public Rental returnVehicle(String userId){
        Optional<Rental> opt = findActiveRentalByUserId(userId);
        if(opt.isEmpty())
            throw new IllegalArgumentException("user doesn't have rentals");

        Rental rental = opt.get();
        rental.setReturnDateTime(LocalDateTime.now().toString());
        return this.rentalRepository.save(rental);
    }

    @Override
    public boolean vehicleHasActiveRental(String vehicleId){
        return this.rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }

    @Override
    public boolean userHasActiveRental(String userId){
        return this.rentalRepository.findAll().stream()
                .anyMatch(rental -> rental.getUserId().equals(userId) && rental.isActive());
    }

    @Override
    public Optional<Rental> findActiveRentalByUserId(String userId){
        return this.rentalRepository.findAll().stream()
                .filter(rental -> rental.getUserId().equals(userId))
                .filter(Rental::isActive)
                .findFirst();
    }
}
