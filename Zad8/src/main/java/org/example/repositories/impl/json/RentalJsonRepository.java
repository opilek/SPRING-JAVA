package org.example.repositories.impl.json;

import com.google.gson.reflect.TypeToken;
import org.example.db.JsonFileStorage;
import org.example.models.Rental;
import org.example.repositories.RentalRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("json")
public class RentalJsonRepository implements RentalRepository {
    private final List<Rental> rentals;
    private final JsonFileStorage<Rental> storage =
            new JsonFileStorage<>("src/main/resources/rentals.json", new TypeToken<List<Rental>>() {}.getType());

    public RentalJsonRepository() {
        this.rentals = new ArrayList<>(this.storage.load());
    }

    @Override
    public List<Rental> findAll() {
        List<Rental> copy = new ArrayList<>();
        for(Rental rental : this.rentals){
            copy.add(rental.copy());
        }
        return copy;
    }

    @Override
    public Optional<Rental> findById(String id) {
        return this.rentals.stream()
                .filter(rental -> rental.getId().equals(id))
                .findFirst()
                .map(Rental::copy);
    }

    @Override
    public Rental save(Rental rental) {
        if(rental == null)
            throw new IllegalArgumentException("rental cannot be null");

        Rental toSave = rental.copy();
        if(toSave.getId() == null || toSave.getId().isBlank())
            toSave.setId(UUID.randomUUID().toString());
        else
            this.rentals.removeIf(r -> r.getId().equals(toSave.getId()));

        this.rentals.add(toSave);
        this.storage.save(this.rentals);
        return toSave.copy();
    }

    @Override
    public void deleteById(String id) {
        this.rentals.removeIf(rental ->rental.getId().equals(id));
        this.storage.save(rentals);
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        return this.rentals.stream()
                .filter(rental -> rental.getVehicleId().equals(vehicleId))
                .filter(Rental::isActive)
                .findFirst()
                .map(Rental::copy);
    }
}
