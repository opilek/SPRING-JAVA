package org.example.repositories.impl.hibernate;

import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class VehicleHibernateRepository implements VehicleRepository {
    private Session session;

    public void setSession(Session session){
        this.session = session;
    }

    @Override
    public List<Vehicle> findAll() {
        return this.session.createQuery("FROM Vehicle", Vehicle.class).list();
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return Optional.ofNullable(this.session.get(Vehicle.class, id));
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        return this.session.merge(vehicle);
    }

    @Override
    public void deleteById(String id) {
        Vehicle vehicle = this.session.get(Vehicle.class, id);
        if(vehicle != null)
            this.session.remove(vehicle);
    }
}
