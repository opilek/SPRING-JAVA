package com.umcsuser.carrent.repositories.impl.jdbc;

import com.umcsuser.carrent.db.JdbcConnectionManager;
import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.RentalRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RentalJdbcRepository implements RentalRepository
{
    @Override
    public List<Rental> findAll()
    {

        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental";

        try(Connection conn = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                rentals.add(resultSetToRental(rs));
            }

        }
        catch (SQLException e)
        {
            System.out.println("Błąd: " + e.getMessage());
        }

        return rentals;
    }

    public Rental resultSetToRental(ResultSet rs) throws SQLException
    {
        String id =  rs.getString("id");
        String vehicle_id =  rs.getString("vehicle_id");
        String user_id =  rs.getString("user_id");
        String rent_date_str = rs.getString("rent_date");
        LocalDateTime rent_date = LocalDateTime.parse(rent_date_str);
        String return_date_str = rs.getString("return_date");
        LocalDateTime return_date = (return_date_str != null) ? LocalDateTime.parse(return_date_str) : null;




        return new Rental(id,vehicle_id,user_id,rent_date,return_date);
    }


    @Override
    public Optional<Rental> findById(String id)
    {

        String sql = "SELECT * FROM rental WHERE id = ?";

        try(Connection conn = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1,id);

            try(ResultSet rs = ps.executeQuery())
            {
                if(rs.next())
                {
                    return Optional.of(resultSetToRental(rs));
                }
            }

        }
        catch (SQLException e)
        {
            System.out.println("Błąd: " + e.getMessage());
        }

        return Optional.empty();

    }
    @Override
    public Rental save(Rental rental)
    {

        String sql = "INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET return_date = EXCLUDED.return_date";

        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {

            ps.setString(1, rental.getId());
            ps.setString(2, rental.getVehicleId());
            ps.setString(3, rental.getUserId());
            ps.setString(4, rental.getStartDateTime().toString());


            if (rental.getReturnDateTime() != null)
            {
                ps.setString(5, rental.getReturnDateTime().toString());
            }
            else
            {
                ps.setNull(5, java.sql.Types.VARCHAR);
            }

            ps.executeUpdate();

        }
        catch (SQLException e)
        {
            System.out.println("Błąd zapisu wypożyczenia: " + e.getMessage());
        }

        return rental;
    }

}
