package org.example.services.impl;

import org.example.db.HibernateConfig;
import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.impl.RentalHibernateRepository;
import org.example.repositories.impl.UserHibernateRepository;
import org.example.repositories.impl.VehicleHibernateRepository;
import org.example.services.RentalServiceInterface;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RentalHibernateService implements RentalServiceInterface {
    private final RentalHibernateRepository rentalRepository;
    private final VehicleHibernateRepository vehicleRepository;
    private final UserHibernateRepository userRepository;

    public RentalHibernateService(RentalHibernateRepository rentalRepository, VehicleHibernateRepository vehicleRepository, UserHibernateRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Rental rentVehicle(String userId, String vehicleId) {
        Transaction tx = null;
        Session session = HibernateConfig.getSessionFactory().openSession();

        try{
            tx = session.beginTransaction();
            setSession(session);

            User user = this.userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika o podanym ID"));

            Vehicle vehicle = this.vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu o podanym ID"));

            boolean userHasActiveRental = this.rentalRepository.findByUserIdAndReturnDateIsNull(userId).isPresent();
            if(userHasActiveRental)
                throw new IllegalStateException("Masz aktywne wypożyczenie");


            boolean vehicleIsRented = this.rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
            if(vehicleIsRented)
                throw new IllegalStateException("Pojazd jest już wypożyczony");

            Rental rental = Rental.builder()
                    .id(UUID.randomUUID().toString())
                    .vehicle(vehicle)
                    .user(user)
                    .rentDateTime(LocalDateTime.now().toString())
                    .returnDateTime(null)
                    .build();

            Rental savedRental = rentalRepository.save(rental);
            tx.commit();

            return savedRental;
        }catch(RuntimeException e){
            rollback(tx);
            throw e;
        }finally{
            session.close();
        }
    }

    @Override
    public Rental returnVehicle(String userId) {
        Transaction tx = null;
        Session session = HibernateConfig.getSessionFactory().openSession();

        try{
            tx = session.beginTransaction();
            setSession(session);

            Rental rental = this.rentalRepository.findByUserIdAndReturnDateIsNull(userId)
                    .orElseThrow(() -> new IllegalStateException("Nie masz aktualnie wypożyczonego pojazdu"));

            rental.setReturnDateTime(LocalDateTime.now().toString());
            Rental savedRental = rentalRepository.save(rental);

            tx.commit();
            return savedRental;
        }catch(RuntimeException e){
            rollback(tx);
            throw(e);
        }finally{
            session.close();
        }
    }

    @Override
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            setSession(session);

            return this.rentalRepository.findByUserIdAndReturnDateIsNull(userId);
        }
    }

    @Override
    public List<Rental> findAllRentals() {
        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            setSession(session);

            return this.rentalRepository.findAll();
        }
    }

    @Override
    public List<Rental> findUserRentals(String userId) {
        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            setSession(session);

            return this.rentalRepository.findAll().stream()
                    .filter(r -> r.getUserId().equals(userId))
                    .toList();
        }
    }

    @Override
    public boolean userHasActiveRental(String userId) {
        return findActiveRentalByUserId(userId).isPresent();
    }

    @Override
    public boolean vehicleHasActiveRental(String vehicleId) {
        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            setSession(session);

            return this.rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        }
    }

    private void setSession(Session session) {
        rentalRepository.setSession(session);
        vehicleRepository.setSession(session);
        userRepository.setSession(session);
    }

    private void rollback(Transaction tx){
        if(tx != null && tx.isActive()){
            tx.rollback();
        }
    }
}
