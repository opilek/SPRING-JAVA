package com.umcsuser.carrent;

import com.umcsuser.carrent.db.JdbcConnectionManager;
import com.umcsuser.carrent.repositories.*;
import com.umcsuser.carrent.repositories.impl.json.RentalJsonRepository;
import com.umcsuser.carrent.repositories.impl.json.UserJsonRepository;
import com.umcsuser.carrent.repositories.impl.json.VehicleCategoryConfigJsonRepository;
import com.umcsuser.carrent.repositories.impl.json.VehicleJsonRepository;
import com.umcsuser.carrent.repositories.impl.jdbc.RentalJdbcRepository;
import com.umcsuser.carrent.repositories.impl.jdbc.UserJdbcRepository;
import com.umcsuser.carrent.repositories.impl.jdbc.VehicleJdbcRepository;
import com.umcsuser.carrent.services.*;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {


        String mode = (args.length > 0) ? args[0].toLowerCase() : "json";

        VehicleRepository vehicleRepo;
        UserRepository userRepo;
        RentalRepository rentalRepo;

        VehicleCategoryConfigRepository configRepo = new VehicleCategoryConfigJsonRepository();

        if (mode.equals("jdbc"))
        {
            System.out.println("Uruchamianie w trybie: JDBC (Neon.tech)");


            try (Connection conn = JdbcConnectionManager.getInstance().getConnection())
            {
                if (conn != null)
                {
                    System.out.println("SUKCES! Połączono z bazą danych.");
                }
            }
            catch (SQLException e)
            {
                System.err.println("BŁĄD POŁĄCZENIA: " + e.getMessage());
                return;
            }

            vehicleRepo = new VehicleJdbcRepository();
            userRepo = new UserJdbcRepository();
            rentalRepo = new RentalJdbcRepository();
        }
        else
        {
            System.out.println("Uruchamianie w trybie: JSON (Pliki lokalne)");
            vehicleRepo = new VehicleJsonRepository();
            userRepo = new UserJsonRepository();
            rentalRepo = new RentalJsonRepository();
        }

        VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepo);
        VehicleValidator validator = new VehicleValidator(configService);
        VehicleService vehicleService = new VehicleService(validator, vehicleRepo, rentalRepo);
        UserService userService = new UserService(userRepo, rentalRepo);
        RentalService rentalService = new RentalService(rentalRepo, vehicleRepo);

        UI ui = new UI(configService, vehicleService, userService, rentalService);
        ui.start();
    }
}