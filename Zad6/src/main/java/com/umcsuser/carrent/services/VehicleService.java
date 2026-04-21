package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;

import java.util.List;

public class VehicleService {

    private final VehicleValidator vehicleValidator;
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;


    public VehicleService(VehicleValidator vehicleValidator,
                          VehicleRepository vehicleRepository,
                          RentalRepository rentalRepository) {
        this.vehicleValidator = vehicleValidator;
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        vehicleRepository.save(vehicle);
        return vehicle;
    }

    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }


    public void deleteVehicle(String id) {

        boolean isRented = rentalRepository.findAll().stream()
                .anyMatch(r -> r.getVehicleId().equals(id) && r.isActive());

        if (isRented) {
            throw new IllegalStateException("BŁĄD: Nie można usunąć pojazdu o ID " + id + ", ponieważ jest obecnie wypożyczony!");
        }

        vehicleRepository.deleteById(id);
    }

    public List<Vehicle> findAvailableVehicles()
    {

        List<String> rentedVehicleIds = rentalRepository.findAll().stream()
                .filter(Rental::isActive)
                .map(Rental::getVehicleId)
                .toList();


        return vehicleRepository.findAll().stream()
                .filter(v -> !rentedVehicleIds.contains(v.getId()))
                .toList();
    }
}