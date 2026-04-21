package com.umcsuser.carrent.repositories;


import com.umcsuser.carrent.models.Rental;
import java.util.List;
import java.util.Optional;

public interface RentalRepository {
    List<Rental> findAll();
    Optional<Rental> findById(String id);
    Rental save(Rental rental);
}
