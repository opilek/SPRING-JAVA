package org.example.services.impl;

import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.VehicleRepository;
import org.example.services.VehicleValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehicleSimpleService implements org.example.services.VehicleServiceInterface {
    private final VehicleRepository vehicleRepository;

    private final RentalRepository rentalRepository;

    private final VehicleValidator vehicleValidator;

    public VehicleSimpleService(VehicleRepository vehicleRepository, RentalRepository rentalRepository, VehicleValidator vehicleValidator) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.vehicleValidator = vehicleValidator;
    }

    @Override
    public List<Vehicle> findAllVehicles(){
        return this.vehicleRepository.findAll();
    }

    @Override
    public List<Vehicle> findAvailableVehicles(){
        List<Vehicle> all = this.vehicleRepository.findAll();
        List<Vehicle> available = new ArrayList<>();
        for(Vehicle v : all){
            if(!isVehicleRented(v.getId())){
                available.add(v);
            }
        }
        return available;
    }

    @Override
    public Vehicle findById(String vehicleId){
        return this.vehicleRepository.findById(vehicleId).get();
    }

    @Override
    public boolean isVehicleRented(String vehicleId){
        return this.rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle){
        this.vehicleValidator.validate(vehicle);
        return this.vehicleRepository.save(vehicle);
    }

    @Override
    public void removeVehicle(String vehicleId) {
        if(isVehicleRented(vehicleId))
            throw new IllegalArgumentException("This vehicle is rented");

        Optional<Vehicle> opt = this.vehicleRepository.findById(vehicleId);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("No vehicle with such ID");
        }
        this.vehicleRepository.deleteById(vehicleId);
    }
}
