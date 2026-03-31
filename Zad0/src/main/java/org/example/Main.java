package org.example;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        IVehicleRepository vehicleRepo = new IVehicleRepositoryImpl();
        IUserRepository userRepo = new UserRepository();
        Authentication auth = new Authentication(userRepo);
        Scanner input = new Scanner(System.in);


        while (true)
        {



            while (auth.getLoggedUser() == null)
            {
                System.out.println("\n******* SYSTEM WYPOŻYCZALNI *******");
                System.out.println("1. Logowanie");
                System.out.println("2. Rejestracja");
                System.out.println("3. Wyjdź");
                System.out.print("Wybierz opcję: ");

                int option = input.nextInt();

                switch (option)
                {
                    case 1:
                        System.out.println("\n******* LOGOWANIE *******");
                        System.out.print("Wprowadź login: ");
                        String login = input.next();
                        System.out.print("Wprowadź hasło: ");
                        String password = input.next();

                        if (auth.authenticate(login, password) == null)
                        {
                            System.out.println("Błędny login lub hasło! Spróbuj ponownie.");
                        }
                        else
                        {
                            System.out.println("Zalogowano pomyślnie jako " + auth.getLoggedUser().getLogin());
                        }
                        break;

                    case 2:
                        System.out.println("\n******* REJESTRACJA *******");
                        System.out.print("Podaj nowy login: ");
                        String newLogin = input.next();


                        if (userRepo.getUser(newLogin) != null)
                        {
                            System.out.println("Błąd: Użytkownik o takim loginie już istnieje!");
                        }
                        else
                        {
                            System.out.print("Podaj hasło: ");
                            String newPassword = input.next();


                            String hashedPass = Authentication.hashPassword(newPassword);

                            User newUser = new User(newLogin, hashedPass, Role.USER, "");
                            userRepo.addUser(newUser);
                            System.out.println("Rejestracja zakończona sukcesem! Możesz się teraz zalogować.");
                        }
                        break;

                    case 3:
                        System.out.println("Zamykanie systemu...");
                        return;

                    default:
                        System.out.println("Nieprawidłowa opcja.");
                }
            }


            boolean stayInMenu = true;
            while (stayInMenu)
            {
                User loggedUser = auth.getLoggedUser();
                System.out.println("\n******* MENU (" + loggedUser.getRole() + ": " + loggedUser.getLogin() + ") *******");

                if (loggedUser.getRole() == Role.USER)
                {
                    System.out.println("1. Wypożycz pojazd");
                    System.out.println("2. Zwróć pojazd");
                    System.out.println("3. Wyświetl moje dane");
                    System.out.println("4. Wyloguj");
                    System.out.print("Wybierz opcję: ");

                    int choice = input.nextInt();
                    switch (choice)
                    {
                        case 1:
                            if (loggedUser.getRentedVehicleId() != null && !loggedUser.getRentedVehicleId().isEmpty())
                            {
                                System.out.println("Już coś wypożyczasz! Najpierw zwróć poprzedni pojazd.");
                                break;
                            }
                            System.out.print("Podaj ID pojazdu: ");
                            String idToRent = input.next();

                            if (vehicleRepo.rentVehicle(idToRent))
                            {
                                loggedUser.setRentedVehicleId(idToRent);
                                userRepo.update(loggedUser);
                                System.out.println("Pojazd wypożyczony!");
                            }
                            else
                            {
                                System.out.println("Nie udało się wypożyczyć (złe ID lub już zajęty).");
                            }
                            break;

                        case 2:
                            if (loggedUser.getRentedVehicleId() == null || loggedUser.getRentedVehicleId().isEmpty())
                            {
                                System.out.println("Nie masz nic do zwrócenia.");
                                break;
                            }

                            System.out.print("Podaj ID pojazdu do zwrotu: ");
                            String idToReturn = input.next();

                            if (!idToReturn.equals(loggedUser.getRentedVehicleId()))
                            {
                                System.out.println("To nie jest Twój pojazd!");
                            }
                            else if (vehicleRepo.returnVehicle(idToReturn))
                            {
                                loggedUser.setRentedVehicleId("");
                                userRepo.update(loggedUser);
                                System.out.println("Zwrócono pojazd.");
                            }
                            break;

                        case 3:
                            System.out.println("Użytkownik: " + loggedUser.getLogin());
                            System.out.println("Wypożyczone ID: " + (loggedUser.getRentedVehicleId().isEmpty() ? "Brak" : loggedUser.getRentedVehicleId()));
                            break;

                        case 4:
                            auth.loggout();
                            stayInMenu = false;
                            break;
                    }

                }
                else if (loggedUser.getRole() == Role.ADMIN)
                {
                    System.out.println("1. Dodaj pojazd");
                    System.out.println("2. Usuń pojazd");
                    System.out.println("3. Lista pojazdów");
                    System.out.println("4. Lista użytkowników");
                    System.out.println("5. Usuń użytkownika");
                    System.out.println("6. Wyloguj");
                    System.out.print("Wybierz opcję: ");

                    int choice = input.nextInt();

                    switch (choice)
                    {
                        case 1:
                            System.out.print("Rodzaj (1-Auto, 2-Moto): ");
                            int type = input.nextInt();
                            System.out.print("Podaj dane (id brand model year price): ");
                            String id = input.next();
                            String brand = input.next();
                            String model = input.next();
                            int year = input.nextInt();
                            double price = input.nextDouble();

                            if (type == 1)
                            {
                                vehicleRepo.addVehicle(new Car(id, brand, model, year, price, false));
                            }
                            else
                            {
                                System.out.print("Kategoria: ");
                                vehicleRepo.addVehicle(new Motorcycle(id, brand, model, year, price, false, input.next()));
                            }
                            break;

                        case 2:
                            System.out.print("ID do usunięcia: ");
                            vehicleRepo.deleteVehicle(input.next());
                            break;

                        case 3:
                            vehicleRepo.getVehicles().forEach(System.out::println);
                            break;

                        case 4:
                            userRepo.getUsers().forEach(u -> System.out.println(u.getLogin() + " [" + u.getRole() + "] - Auto: " + u.getRentedVehicleId()));
                            break;

                        case 5:
                            System.out.print("Podaj login użytkownika do usunięcia: ");
                            String targetLogin = input.next();
                            User userToDelete = userRepo.getUser(targetLogin);

                            if (userToDelete == null)
                            {
                                System.out.println("Błąd: Nie znaleziono takiego użytkownika.");
                            }
                            else if (userToDelete.getRole() == Role.ADMIN)
                            {
                                System.out.println("Błąd: Nie można usunąć innego administratora!");
                            }
                            else if (!userToDelete.getRentedVehicleId().isEmpty())
                            {
                                System.out.println("Błąd: Użytkownik ma wypożyczony pojazd (ID: " + userToDelete.getRentedVehicleId() + ").");
                            }
                            else
                            {
                                userRepo.deleteUser(userToDelete);
                                System.out.println("Użytkownik " + targetLogin + " został pomyślnie usunięty.");
                            }
                            break;

                        case 6:
                            auth.loggout();
                            stayInMenu = false;
                            break;
                    }
                }
            }
        }
    }
}