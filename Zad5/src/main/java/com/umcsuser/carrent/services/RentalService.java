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
        // 1. Sprawdzenie, czy ten użytkownik ma już aktywne wypożyczenie
        // (używamy Twojej metody isActive(), która sprawdza czy returnDateTime == null)
        boolean hasActiveRental = rentalRepository.findAll().stream()
                .anyMatch(r -> r.getUserId().equals(userId) && r.isActive());

        if (hasActiveRental) {
            throw new IllegalStateException("Masz już wypożyczony pojazd. Najpierw musisz go zwrócić.");
        }

        // 2. Sprawdzenie, czy pojazd nie jest zajęty przez kogoś innego
        boolean isVehicleBusy = rentalRepository.findAll().stream()
                .anyMatch(r -> r.getVehicleId().equals(vehicleId) && r.isActive());

        if (isVehicleBusy) {
            throw new IllegalStateException("Ten pojazd jest obecnie wypożyczony.");
        }

        // 3. Tworzymy obiekt Rental zgodnie z Twoją kolejnością pól:
        // id, vehicleId, userId, startDateTime, returnDateTime
        Rental rental = new Rental(
                UUID.randomUUID().toString(), // id
                vehicleId,                   // vehicleId (u Ciebie jest drugie!)
                userId,                      // userId (u Ciebie jest trzecie!)
                LocalDateTime.now(),          // startDateTime
                null                         // returnDateTime (null oznacza, że auto jest w trasie)
        );

        rentalRepository.save(rental);
    }

    public void returnVehicle(String vehicleId) {
        Rental rental = rentalRepository.findAll().stream()
                .filter(r -> r.getVehicleId().equals(vehicleId) && r.isActive())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono aktywnego wypożyczenia dla tego auta."));

        // Zamiast setActive(false), ustawiamy datę zwrotu.
        // Twoja metoda isActive() automatycznie zwróci wtedy 'false'.
        rental.setReturnDateTime(LocalDateTime.now());
        rentalRepository.save(rental);
    }
}