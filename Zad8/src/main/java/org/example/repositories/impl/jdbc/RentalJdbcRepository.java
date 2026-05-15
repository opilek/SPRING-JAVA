package org.example.repositories.impl.jdbc;

import org.example.db.JdbcConnectionManager;
import org.example.models.Rental;
import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Profile("jdbc")
public class RentalJdbcRepository implements RentalRepository {
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public RentalJdbcRepository(VehicleRepository vehicleRepository, UserRepository userRepository){
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }
    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental";

        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){

            while(rs.next()){
                rentals.add(this.mapRow(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error occurred while reading rentals", e);
        }

        rentals.sort((r1, r2) -> r2.getRentDateTime().compareTo(r1.getRentDateTime()));
        return rentals;
    }

    @Override
    public Optional<Rental> findById(String id) {
        String sql = "SELECT * FROM rental WHERE id = ?";

        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){

            stmt.setString(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    Rental rental = this.mapRow(rs);
                    return Optional.of(rental);
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error occurred while reading rental", e);
        }
        return Optional.empty();
    }

    @Override
    public Rental save(Rental rental) {
        String sql;
        boolean add;

        if(rental.getId() == null || rental.getId().isBlank()){
            rental.setId(UUID.randomUUID().toString());
            add = true;
            sql = "INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date) VALUES (?, ?, ?, ?, ?)";
        }else{
            add = false;
            sql = "UPDATE rental SET vehicle_id = ?, user_id = ?, rent_date = ?, return_date = ? WHERE id = ?";
        }

        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){

            if(add){
                stmt.setString(1, rental.getId());
                stmt.setString(2, rental.getVehicleId());
                stmt.setString(3, rental.getUserId());
                stmt.setString(4, rental.getRentDateTime());
                stmt.setString(5, rental.getReturnDateTime());
            }else{
                stmt.setString(1, rental.getVehicleId());
                stmt.setString(2, rental.getUserId());
                stmt.setString(3, rental.getRentDateTime());
                stmt.setString(4, rental.getReturnDateTime());
                stmt.setString(5, rental.getId());
            }

            stmt.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException("Error occurred while saving rental", e);
        }
        return rental;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM rental WHERE id = ?";

        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){

            stmt.setString(1, id);
            stmt.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException("Error occurred while deleting rental", e);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        String sql = "SELECT * FROM rental WHERE vehicle_id = ? AND return_date IS NULL";

        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){

            stmt.setString(1, vehicleId);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    Rental rental = this.mapRow(rs);
                    return Optional.of(rental);
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error occurred while reading rental", e);
        }
        return Optional.empty();
    }

    private Rental mapRow(ResultSet rs) throws SQLException {
        return Rental.builder()
                .id(rs.getString("id"))
                .vehicle(this.vehicleRepository.findById(rs.getString("vehicle_id")).get())
                .user(this.userRepository.findById(rs.getString("user_id")).get())
                .rentDateTime(rs.getString("rent_date"))
                .returnDateTime(rs.getString("return_date"))
                .build();
    }
}
