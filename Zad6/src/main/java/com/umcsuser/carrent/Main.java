package com.umcsuser.carrent;

import com.umcsuser.carrent.repositories.*;
import com.umcsuser.carrent.repositories.impl.*;
import com.umcsuser.carrent.services.*;

public class Main {
    public static void main(String[] args) {

        VehicleCategoryConfigRepository configRepo = new VehicleCategoryConfigJsonRepository();
        VehicleRepository vehicleRepo = new VehicleJsonRepository(); 
        UserRepository userRepo = new UserJsonRepository();
        RentalRepository rentalRepo = new RentalJsonRepository();


        VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepo);
        VehicleValidator validator = new VehicleValidator(configService);


        VehicleService vehicleService = new VehicleService(validator, vehicleRepo, rentalRepo);
        UserService userService = new UserService(userRepo, rentalRepo);
        RentalService rentalService = new RentalService(rentalRepo, vehicleRepo);


        UI ui = new UI(configService, vehicleService, userService, rentalService);
        ui.start();
    }
}