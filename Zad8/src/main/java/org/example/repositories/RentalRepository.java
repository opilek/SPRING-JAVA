package org.example.repositories;

import org.example.models.Rental;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RentalRepository {
    List<Rental> findAll();
    Optional<Rental> findById(String id);
    Rental save(Rental rental);
    void deleteById(String id);
    @Query("SELECT r FROM Rental r WHERE r.vehicle.id = :vehicleId AND r.returnDateTime IS NULL")
    Optional<Rental> findByVehicleIdAndReturnDateIsNull(@Param("vehicleId") String vehicleId);
}

