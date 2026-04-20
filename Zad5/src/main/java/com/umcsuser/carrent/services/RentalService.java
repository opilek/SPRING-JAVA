package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;
import java.time.LocalDateTime;
import java.util.UUID;

public class RentalService {
    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;

    public RentalService(RentalRepository rr, VehicleRepository vr) {
        this.rentalRepository = rr;
        this.vehicleRepository = vr;
    }

    public void rentVehicle(String userId, String vehicleId) {

        boolean hasActiveRental = rentalRepository.findAll().stream()
                .anyMatch(r -> r.getUserId().equals(userId) && r.isActive());

        if (hasActiveRental) {
            throw new IllegalStateException("Masz już wypożyczony pojazd. Najpierw musisz go zwrócić.");
        }


        boolean isVehicleBusy = rentalRepository.findAll().stream()
                .anyMatch(r -> r.getVehicleId().equals(vehicleId) && r.isActive());

        if (isVehicleBusy) {
            throw new IllegalStateException("Ten pojazd jest obecnie wypożyczony.");
        }


        Rental rental = new Rental(
                UUID.randomUUID().toString(),
                vehicleId,
                userId,
                LocalDateTime.now(),
                null
        );

        rentalRepository.save(rental);
    }

    public void returnVehicle(String vehicleId) {
        Rental rental = rentalRepository.findAll().stream()
                .filter(r -> r.getVehicleId().equals(vehicleId) && r.isActive())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono aktywnego wypożyczenia dla tego auta."));


        rental.setReturnDateTime(LocalDateTime.now());
        rentalRepository.save(rental);
    }
}