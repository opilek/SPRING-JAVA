package com.umcsuser.carrent;

import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.models.VehicleCategoryConfig;
import com.umcsuser.carrent.services.*;

import java.util.Scanner;

public class UI {
    private final VehicleCategoryConfigService configService;
    private final VehicleService vehicleService;
    private final UserService userService;
    private final RentalService rentalService;
    private final Scanner scanner = new Scanner(System.in);
    private User currentUser;

    public UI(VehicleCategoryConfigService configService,
              VehicleService vehicleService,
              UserService userService,
              RentalService rentalService) {
        this.configService = configService;
        this.vehicleService = vehicleService;
        this.userService = userService;
        this.rentalService = rentalService;
    }

    public void start() {
        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    // --- MENU STARTOWE ---
    private void showAuthMenu() {
        System.out.println("\n******* SYSTEM WYPOŻYCZALNI *******");
        System.out.println("1. Logowanie");
        System.out.println("2. Rejestracja");
        System.out.println("3. Wyjdź");
        System.out.print("Wybierz opcję: ");

        String choice = scanner.nextLine();
        try {
            switch (choice) {
                case "1" -> handleLogin();
                case "2" -> handleRegister();
                case "3" -> {
                    System.out.println("Zamykanie systemu...");
                    System.exit(0);
                }
                default -> System.out.println("Nieprawidłowa opcja.");
            }
        } catch (Exception e) {
            System.out.println("BŁĄD: " + e.getMessage());
        }
    }

    // --- LOGOWANIE I REJESTRACJA ---
    private void handleLogin() {
        System.out.print("Login: "); String login = scanner.nextLine();
        System.out.print("Hasło: "); String pass = scanner.nextLine();
        currentUser = userService.login(login, pass);
        System.out.println("\nZalogowano pomyślnie. Witaj " + currentUser.getLogin() + "!");
    }

    private void handleRegister() {
        System.out.print("Nowy login: ");
        String login = scanner.nextLine();
        System.out.print("Nowe hasło: ");
        String pass = scanner.nextLine();

        try {
            // Rola jest sztywno ustawiona na USER
            userService.register(login, pass, Role.USER);
            System.out.println("Rejestracja zakończona sukcesem.");
        } catch (IllegalArgumentException e) {
            System.out.println("BŁĄD: " + e.getMessage());
        }
    }

    // --- GŁÓWNE MENU ---
    private void showMainMenu() {
        System.out.println("\n--- ZALOGOWANO: " + currentUser.getLogin() + " (" + currentUser.getRole() + ") ---");

        if (currentUser.getRole() == Role.ADMIN) {
            showAdminMenu();
        } else {
            showUserMenu();
        }
    }

    private void showAdminMenu() {
        System.out.println("1. Dodaj pojazd");
        System.out.println("2. Usuń pojazd");
        System.out.println("3. Lista pojazdów");
        System.out.println("4. Lista użytkowników");
        System.out.println("5. Usuń użytkownika");
        System.out.println("6. Wyloguj");
        System.out.print("Wybór: ");
        handleAdminAction(scanner.nextLine());
    }

    private void showUserMenu() {
        System.out.println("1. Wyświetl listę dostępnych pojazdów do wypożyczenia");
        System.out.println("2. Wypożycz pojazd");
        System.out.println("3. Zwróć pojazd");
        System.out.println("4. Wyloguj");
        System.out.print("Wybór: ");
        handleUserAction(scanner.nextLine());
    }

    // --- AKCJE ADMINA ---
    private void handleAdminAction(String choice) {
        try {
            switch (choice) {
                case "1" -> handleAddVehicle();
                case "2" -> {
                    System.out.print("Podaj ID auta do usunięcia: ");
                    vehicleService.deleteVehicle(scanner.nextLine());
                    System.out.println("Pojazd usunięty.");
                }
                case "3" -> vehicleService.findAllVehicles().forEach(System.out::println);
                case "4" -> {
                    System.out.println("Lista użytkowników w systemie:");
                    userService.findAll().forEach(System.out::println); // Wymaga dodania findAll() w UserService
                }
                case "5" -> {
                    System.out.print("Podaj LOGIN użytkownika do usunięcia: ");
                    String loginToDelete = scanner.nextLine().trim();
                    userService.deleteUserByLogin(loginToDelete);
                    System.out.println("Użytkownik " + loginToDelete + " został usunięty.");
                }
                case "6" -> currentUser = null;
                default -> System.out.println("Nieprawidłowa opcja.");
            }
        } catch (Exception e) {
            System.out.println("BŁĄD: " + e.getMessage());
        }
    }

    // --- AKCJE USERA ---
    private void handleUserAction(String choice) {
        try {
            switch (choice) {
                case "1" -> {
                    System.out.println("Dostępne pojazdy:");
                    // Tutaj możesz filtrować tylko dostępne, ale na razie pokazujemy wszystkie
                    vehicleService.findAllVehicles().forEach(System.out::println);
                }
                case "2" -> {
                    System.out.print("Podaj ID pojazdu do wypożyczenia: ");
                    String vehicleId = scanner.nextLine();
                    // Tutaj poleci wyjątek z serwisu, jeśli user ma już auto
                    rentalService.rentVehicle(currentUser.getId(), vehicleId);
                    System.out.println("Pomyślnie wypożyczono pojazd.");
                }
                case "3" -> {
                    System.out.print("Podaj ID pojazdu do zwrotu: ");
                    rentalService.returnVehicle(scanner.nextLine());
                    System.out.println("Pojazd został zwrócony.");
                }
                case "4" -> currentUser = null;
                default -> System.out.println("Nieprawidłowa opcja.");
            }
        } catch (Exception e) {
            System.out.println("BŁĄD: " + e.getMessage());
        }
    }

    // --- DYNAMICZNE DODAWANIE POJAZDU (ZADANIE 5) ---
    private void handleAddVehicle() {
        System.out.println("\nKonfiguracja kategorii z JSON:");
        configService.findAllCategories().forEach(c -> System.out.print("[" + c.getCategory() + "] "));

        System.out.print("\nPodaj kategorię: ");
        String cat = scanner.nextLine().trim();
        VehicleCategoryConfig config = configService.getByCategory(cat);

        System.out.print("ID: "); String id = scanner.nextLine();
        System.out.print("Marka: "); String brand = scanner.nextLine();
        System.out.print("Model: "); String model = scanner.nextLine();
        System.out.print("Rok: "); int year = Integer.parseInt(scanner.nextLine());
        System.out.print("Numer Rej: "); String plate = scanner.nextLine();
        System.out.print("Cena: "); double price = Double.parseDouble(scanner.nextLine());

        Vehicle vehicle = Vehicle.builder()
                .id(id).category(cat).brand(brand).model(model)
                .year(year).plate(plate).price(price).build();

        config.getAttributes().forEach((name, type) -> {
            System.out.print("Podaj " + name + " (" + type + "): ");
            String val = scanner.nextLine();
            Object converted = switch (type.toLowerCase()) {
                case "integer" -> Integer.parseInt(val);
                case "boolean" -> Boolean.parseBoolean(val);
                case "number" -> Double.parseDouble(val);
                default -> val;
            };
            vehicle.addAttribute(name, converted);
        });

        vehicleService.addVehicle(vehicle);
        System.out.println("Pojazd został poprawnie dodany i zweryfikowany.");
    }
}