package org.example;

import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleCategoryConfigRepository;
import org.example.repositories.VehicleRepository;
import org.example.repositories.impl.*;
import org.example.services.*;
import org.example.services.impl.*;

public class Main {
    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.SEVERE);

        VehicleCategoryConfigRepository categoryConfigRepository = new VehicleCategoryConfigJsonRepository();
        VehicleCategoryConfigService categoryConfigService = new VehicleCategoryConfigService(categoryConfigRepository);
        VehicleValidator vehicleValidator = new VehicleValidator(categoryConfigService);

        VehicleRepository vehicleRepository;
        UserRepository userRepository;
        RentalRepository rentalRepository;

        if(args.length == 0){
            throw new RuntimeException("No repository type given");
        }
        switch (args[0]) {
            case "json" -> {
                vehicleRepository = new VehicleJsonRepository();
                userRepository = new UserJsonRepository();
                rentalRepository = new RentalJsonRepository();
            }
            case "jdbc" -> {
                vehicleRepository = new VehicleJdbcRepository();
                userRepository = new UserJdbcRepository();
                rentalRepository = new RentalJdbcRepository(vehicleRepository, userRepository);
            }
            case "hibernate" -> {
                vehicleRepository = new VehicleHibernateRepository();
                userRepository = new UserHibernateRepository();
                rentalRepository = new RentalHibernateRepository();
            }
            default -> throw new RuntimeException("Wrong repository type given");
        }

        AuthServiceInterface authService;
        VehicleServiceInterface vehicleService;
        RentalServiceInterface rentalService;
        UserServiceInterface userService;

        if(args[0].equals("hibernate")){
            authService = new AuthHibernateService((UserHibernateRepository) userRepository);
            vehicleService = new VehicleHibernateService((VehicleHibernateRepository) vehicleRepository, (RentalHibernateRepository) rentalRepository, vehicleValidator);
            rentalService = new RentalHibernateService((RentalHibernateRepository) rentalRepository, (VehicleHibernateRepository) vehicleRepository, (UserHibernateRepository) userRepository);
            userService = new UserHibernateService((UserHibernateRepository) userRepository, (RentalHibernateService) rentalService);
        }else{
            authService = new AuthSimpleService(userRepository);
            vehicleService = new VehicleSimpleService(vehicleRepository, rentalRepository, vehicleValidator);
            rentalService = new RentalSimpleService(rentalRepository, vehicleRepository, userRepository);
            userService = new UserSimpleService(userRepository, (RentalSimpleService) rentalService);
        }

        UI ui = new UI(
                authService,
                vehicleService,
                rentalService,
                userService,
                categoryConfigService
        );

        ui.start();
    }
}