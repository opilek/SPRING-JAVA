package org.example;

import org.example.models.*;
import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleRepository;
import org.example.repositories.impl.RentalJsonRepository;
import org.example.repositories.impl.UserJsonRepository;
import org.example.repositories.impl.VehicleJsonRepository;
import org.example.services.AuthService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {

        UserRepository userRepo = new UserJsonRepository();
        VehicleRepository vehicleRepo = new VehicleJsonRepository();
        RentalRepository rentalRepo = new RentalJsonRepository();


        AuthService authService = new AuthService(userRepo);
        UI ui = new UI(vehicleRepo, userRepo, rentalRepo, authService);
        ui.start();


    }
}