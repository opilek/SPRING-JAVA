package com.umcsuser.carrent.repositories.impl.json;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.db.JsonFileStorage;
import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RentalJsonRepository implements RentalRepository {


    private final JsonFileStorage<Rental> storage = new JsonFileStorage<>("rentals.json",
            new TypeToken<List<Rental>>(){}.getType());

    private final List<Rental> rentals;

    public RentalJsonRepository() {
        this.rentals = new ArrayList<>(storage.load());
    }

    @Override
    public List<Rental> findAll() {

        return rentals.stream().map(Rental::copy).toList();
    }

    @Override
    public Optional<Rental> findById(String id) {
        return rentals.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .map(Rental::copy);
    }

    @Override
    public Rental save(Rental rental) {

        rentals.removeIf(r -> r.getId().equals(rental.getId()));
        rentals.add(rental.copy());
        storage.save(rentals);
        return rental;
    }
}