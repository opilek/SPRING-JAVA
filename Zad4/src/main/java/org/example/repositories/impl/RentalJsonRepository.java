package org.example.repositories.impl;

import com.google.gson.reflect.TypeToken;
import org.example.db.JsonFileStorage;
import org.example.models.Rental;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RentalJsonRepository implements RentalRepository
{
    private final JsonFileStorage<Rental> storage;
    private final List<Rental> rentals;

    public RentalJsonRepository()
    {
        Type rentalListType = new TypeToken<ArrayList<Rental>>(){}.getType();

        this.storage = new JsonFileStorage<>("rentals.json", rentalListType);
        this.rentals = storage.load();
    }

        @Override
        public List<Rental> findAll()
        {
            return rentals;

        }
        @Override
        public Optional<Rental> findById(String id)
        {
            for(Rental r: rentals)
            {
                if(r.getId().equals(id))
                {
                    return Optional.of(r);
                }
            }

            return Optional.empty();
        }
        @Override
        public Rental save(Rental rental)
        {
            rentals.removeIf(r -> r.getId().equals(rental.getId()));
            rentals.add(rental);
            storage.save(rentals);

            return rental;

        }
        @Override
        public void deleteById(String id)
        {
            rentals.removeIf(r -> r.getId().equals(id));
            storage.save(rentals);

        }
        @Override
        public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId)
        {
            for(Rental r: rentals)
            {
                if(r.getVehicleId().equals(vehicleId) && r.isActive())
                {
                    return Optional.of(r);
                }
            }

            return Optional.empty();

        }
}
