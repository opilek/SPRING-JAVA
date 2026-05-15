package org.example.repositories.impl.hibernate;

import org.example.models.Rental;
import org.example.repositories.RentalRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("jpa")
public class RentalHibernateRepository implements RentalRepository {
    private Session session;

    public void setSession(Session session){
        this.session = session;
    }

    @Override
    public List<Rental> findAll() {
        return this.session.createQuery("FROM Rental", Rental.class).list();
    }

    @Override
    public Optional<Rental> findById(String id) {
        return Optional.ofNullable(this.session.get(Rental.class, id));
    }

    @Override
    public Rental save(Rental rental) {
        return this.session.merge(rental);
    }

    @Override
    public void deleteById(String id) {
        Rental rental = this.session.get(Rental.class, id);
        if(rental != null)
            this.session.remove(rental);
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        Query<Rental> query = this.session.createQuery("""
                FROM Rental r
                WHERE r.vehicle.id = :vehicleId
                AND r.returnDateTime IS NULL
                """, Rental.class);

        query.setParameter("vehicleId", vehicleId);
        return query.uniqueResultOptional();
    }

    public Optional<Rental> findByUserIdAndReturnDateIsNull(String userId) {
        Query<Rental> query = this.session.createQuery("""
                FROM Rental r
                WHERE r.user.id = :userId
                AND r.returnDateTime IS NULL
                """, Rental.class);

        query.setParameter("userId", userId);
        return query.uniqueResultOptional();
    }
}
