package com.umcsuser.carrent.repositories.impl.json;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.db.JsonFileStorage;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.VehicleRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehicleJsonRepository implements VehicleRepository {
    private final JsonFileStorage<Vehicle> storage = new JsonFileStorage<>("vehicles.json",
            new TypeToken<List<Vehicle>>(){}.getType());
    private final List<Vehicle> vehicles;

    public VehicleJsonRepository() {
        this.vehicles = new ArrayList<>(storage.load());
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicles.stream().map(Vehicle::copy).toList();
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return vehicles.stream().filter(v -> v.getId().equals(id)).findFirst().map(Vehicle::copy);
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        vehicles.removeIf(v -> v.getId().equals(vehicle.getId()));
        vehicles.add(vehicle.copy());
        storage.save(vehicles);
        return vehicle;
    }

    @Override
    public void deleteById(String id) {
        vehicles.removeIf(v -> v.getId().equals(id));
        storage.save(vehicles);
    }
}