package org.example.services.impl;

import org.example.db.HibernateConfig;
import org.example.models.Vehicle;
import org.example.repositories.impl.RentalHibernateRepository;
import org.example.repositories.impl.VehicleHibernateRepository;
import org.example.services.VehicleServiceInterface;
import org.example.services.VehicleValidator;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VehicleHibernateService implements VehicleServiceInterface {
    private final VehicleHibernateRepository vehicleRepository;

    private final RentalHibernateRepository rentalRepository;

    private final VehicleValidator vehicleValidator;

    public VehicleHibernateService(VehicleHibernateRepository vehicleRepository, RentalHibernateRepository rentalRepository, VehicleValidator vehicleValidator) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.vehicleValidator = vehicleValidator;
    }

    @Override
    public List<Vehicle> findAllVehicles() {
        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            setSession(session);
            return this.vehicleRepository.findAll();
        }
    }

    @Override
    public List<Vehicle> findAvailableVehicles() {
        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            setSession(session);
            List<Vehicle> all = this.vehicleRepository.findAll();
            List<Vehicle> available = new ArrayList<>();

            for(Vehicle v : all){
                if(this.rentalRepository.findByVehicleIdAndReturnDateIsNull(v.getId()).isEmpty())
                    available.add(v);
            }
            return available;
        }
    }

    @Override
    public Vehicle findById(String id) {
        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            setSession(session);
            return this.vehicleRepository.findById(id).get();
        }
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        Transaction tx = null;
        Session session = HibernateConfig.getSessionFactory().openSession();

        try{
            tx = session.beginTransaction();
            setSession(session);

            this.vehicleValidator.validate(vehicle);

            if(vehicle.getId() == null || vehicle.getId().isBlank())
                vehicle.setId(UUID.randomUUID().toString());

            Vehicle saved = this.vehicleRepository.save(vehicle);
            tx.commit();
            return saved;
        }catch(RuntimeException e){
            rollback(tx);
            throw e;
        }finally{
            session.close();
        }
    }

    @Override
    public void removeVehicle(String vehicleId) {
        Transaction tx = null;
        Session session = HibernateConfig.getSessionFactory().openSession();

        try{
            tx = session.beginTransaction();
            setSession(session);

            if(this.rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent())
                throw new IllegalStateException("Ten pojazd jest wypożyczony");

            this.vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu o podanym ID"));

            this.vehicleRepository.deleteById(vehicleId);
            tx.commit();
        }catch(RuntimeException e){
            rollback(tx);
            throw e;
        }finally{
            session.close();
        }
    }

    @Override
    public boolean isVehicleRented(String vehicleId) {
        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            setSession(session);
            return this.rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        }
    }

    private void setSession(Session session) {
        rentalRepository.setSession(session);
        vehicleRepository.setSession(session);
    }

    private void rollback(Transaction tx){
        if(tx != null && tx.isActive()){
            tx.rollback();
        }
    }
}
