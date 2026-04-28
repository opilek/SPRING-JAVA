package com.umcsuser.carrent.repositories.impl.jdbc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.db.JdbcConnectionManager;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.VehicleRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VehicleJdbcRepository implements VehicleRepository
{

    private final Gson gson = new Gson();


    @Override
    public List<Vehicle> findAll()
    {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicle ORDER BY id ASC";

        try(Connection conn = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {


            try(ResultSet rs = ps.executeQuery())
            {

                while (rs.next())
                {
                    vehicles.add(mapResultSetToVehicle(rs));
                }
            }

        }
        catch (SQLException e)
        {
            System.err.println("Błąd bazy danych: " + e.getMessage());
        }

        return vehicles;

    }

    public Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException
    {
       String id =  rs.getString("id");
        String category =  rs.getString("category");
        String brand =  rs.getString("brand");
        String model =  rs.getString("model");
        int year =  rs.getInt("year");
        String plate =  rs.getString("plate");
        double price =  rs.getDouble("price");
         String attributesJson = rs.getString("attributes");

        Map<String,Object> attributesMap = gson.fromJson(attributesJson,
                new TypeToken<Map<String,Object>>(){}.getType());

        return new Vehicle(id,category,brand,model,year,plate,price,attributesMap);

    }
    @Override
    public Optional<Vehicle> findById(String id)
    {
        String sql = "SELECT * FROM vehicle WHERE id = ?";

        try(Connection conn = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {


            ps.setString(1,id);

            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    return Optional.of(mapResultSetToVehicle(rs));
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
    public Vehicle save(Vehicle vehicle)
    {
        String sql = "INSERT INTO vehicle (id, category, brand, model, year, plate, price, attributes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "category = EXCLUDED.category, brand = EXCLUDED.brand, model = EXCLUDED.model, " +
                "year = EXCLUDED.year, plate = EXCLUDED.plate, price = EXCLUDED.price, " +
                "attributes = EXCLUDED.attributes";

        String attrJson = gson.toJson(vehicle.getAttributes());

        try(Connection conn = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {


            ps.setString(1,vehicle.getId());
            ps.setString(2,vehicle.getCategory());
            ps.setString(3,vehicle.getBrand());
            ps.setString(4,vehicle.getModel());
            ps.setInt(5,vehicle.getYear());
            ps.setString(6,vehicle.getPlate());
            ps.setDouble(7,vehicle.getPrice());
            ps.setString(8,attrJson);

            ps.executeUpdate();


        }
        catch (SQLException e)
        {
            System.out.println("Błąd: " + e.getMessage());
        }

        return vehicle;

    }
    @Override
    public void deleteById(String id)
    {

        String sql = "DELETE FROM vehicle WHERE id = ?";

        try(Connection conn = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1,id);

            ps.executeUpdate();


        }
        catch (SQLException e)
        {
            System.out.println("Błąd: " + e.getMessage());
        }

    }
}
