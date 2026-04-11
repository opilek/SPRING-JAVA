package org.example.repositories.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.db.JsonFileStorage;
import org.example.db.RuntimeTypeAdapterFactory;
import org.example.models.Car;
import org.example.models.Motorcycle;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehicleJsonRepository implements VehicleRepository
{

    private final JsonFileStorage<Vehicle> storage;
    private final List<Vehicle> vehicles;

    public VehicleJsonRepository() {

        RuntimeTypeAdapterFactory<Vehicle> vehicleAdapter = RuntimeTypeAdapterFactory
                .of(Vehicle.class, "category")
                .registerSubtype(Car.class, "Car")
                .registerSubtype(Motorcycle.class, "Motorcycle");


        Gson customGson = new GsonBuilder()
                .registerTypeAdapterFactory(vehicleAdapter)
                .setPrettyPrinting()
                .create();

        Type vehicleListType = new TypeToken<ArrayList<Vehicle>>(){}.getType();


        this.storage = new JsonFileStorage<>("vehicles.json", vehicleListType, customGson);
        this.vehicles = storage.load();
    }

    @Override
    public List<Vehicle> findAll()
    {
        return vehicles;

    }
    @Override
    public Optional<Vehicle> findById(String id)
    {
        for(Vehicle v: vehicles)
        {
            if(v.getId().equals(id))
            {
                return Optional.of(v);
            }
        }

        return Optional.empty();
    }
    @Override
    public Vehicle save(Vehicle vehicle)
    {
        vehicles.removeIf(v -> v.getId().equals(vehicle.getId()));
        vehicles.add(vehicle);
        storage.save(vehicles);

        return vehicle;
    }
    @Override
    public void deleteById(String id)
    {
        vehicles.removeIf(v -> v.getId().equals(id));
        storage.save(vehicles);

    }
}
