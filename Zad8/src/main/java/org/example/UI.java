package org.example;

import org.example.models.*;
import org.example.services.*;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UI {
    private final AuthServiceInterface authService;
    private final VehicleServiceInterface vehicleService;
    private final RentalServiceInterface rentalService;
    private final UserServiceInterface userService;
    private final VehicleCategoryConfigService categoryConfigService;
    private final Scanner scanner = new Scanner(System.in);
    private User currentUser;

    public UI(AuthServiceInterface authService, VehicleServiceInterface vehicleService, RentalServiceInterface rentalService,
              UserServiceInterface userService, VehicleCategoryConfigService categoryConfigService) {
        this.authService = authService;
        this.vehicleService = vehicleService;
        this.rentalService = rentalService;
        this.userService = userService;
        this.categoryConfigService = categoryConfigService;
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

    // --- TWOJA WIZUALIZACJA MENU STARTOWEGO ---
    private void showAuthMenu() {
        System.out.println("\n******* SYSTEM WYPOŻYCZALNI *******");
        System.out.println("1. Logowanie");
        System.out.println("2. Rejestracja");
        System.out.println("3. Wyjdź");
        System.out.print("Wybierz opcję: ");

        switch (scanner.nextLine().trim()) {
            case "1" -> {
                currentUser = login();
                if (currentUser != null) {
                    System.out.println("\nZalogowano pomyślnie. Witaj " + currentUser.getLogin() + "!");
                } else {
                    System.out.println("BŁĄD: Nieprawidłowy login lub hasło.");
                }
            }
            case "2" -> register();
            case "3" -> {
                System.out.println("Zamykanie systemu...");
                System.exit(0);
            }
            default -> System.out.println("Nieprawidłowa opcja.");
        }
    }

    private void showMainMenu() {
        System.out.println("\n--- ZALOGOWANO: " + currentUser.getLogin() + " (" + currentUser.getRole() + ") ---");
        if (currentUser.getRole() == Role.ADMIN) adminMenu();
        else userMenu();
    }

    // --- MENU ADMINA W TWOIM STYLU ---
    private void adminMenu() {
        System.out.println("1. Dodaj pojazd\n2. Usuń pojazd\n3. Lista pojazdów\n4. Lista użytkowników\n5. Lista wypożyczeń\n6. Wyloguj");
        System.out.print("Wybór: ");

        switch (scanner.nextLine().trim()) {
            case "1" -> addVehicle();
            case "2" -> deleteVehicle();
            case "3" -> {
                System.out.println("\n--- LISTA POJAZDÓW ---");
                vehicleService.findAllVehicles().forEach(v ->
                        System.out.println(v + " [Wypożyczony: " + rentalService.vehicleHasActiveRental(v.getId()) + "]"));
            }
            case "4" -> showAllUsers();
            case "5" -> {
                System.out.println("\n--- HISTORIA WYPOŻYCZEŃ ---");
                showRentalHistory();
            }
            case "6" -> currentUser = null;
            default -> System.out.println("Nieprawidłowa opcja.");
        }
    }

    // --- MENU USERA W TWOIM STYLU ---
    private void userMenu() {
        System.out.println("1. Lista dostępnych aut\n2. Wypożycz pojazd\n3. Zwróć pojazd\n4. Moje dane\n5. Wyloguj");
        System.out.print("Wybór: ");

        switch (scanner.nextLine().trim()) {
            case "1" -> {
                System.out.println("\n--- DOSTĘPNE AUTA ---");
                vehicleService.findAvailableVehicles().forEach(System.out::println);
            }
            case "2" -> rentVehicle();
            case "3" -> returnVehicle();
            case "4" -> showCurrentUserData();
            case "5" -> currentUser = null;
            default -> System.out.println("Nieprawidłowa opcja.");
        }
    }

    // --- LOGIKA Z PROJEKTU KOLEGI (W TWOIM OPAKOWANIU) ---

    private void register() {
        System.out.println("\n=== REJESTRACJA ===");
        if (authService.register(readText("Nowy login: "), readText("Nowe hasło: "))) {
            System.out.println("Rejestracja zakończona sukcesem. Możesz się zalogować.");
        } else {
            System.out.println("BŁĄD: Rejestracja nieudana (użytkownik już istnieje).");
        }
    }

    private User login() {
        System.out.println("\n=== LOGOWANIE ===");
        return authService.login(readText("Login: "), readText("Hasło: ")).orElse(null);
    }

    private void addVehicle() {
        System.out.println("\n--- DODAWANIE POJAZDU ---");
        List<VehicleCategoryConfig> categories = categoryConfigService.findAllCategories();
        if (categories.isEmpty()) {
            System.out.println("BŁĄD: Brak skonfigurowanych kategorii.");
            return;
        }

        System.out.print("Dostępne kategorie: ");
        categories.forEach(c -> System.out.print("[" + c.getCategory() + "] "));
        System.out.println();

        try {
            VehicleCategoryConfig config = categoryConfigService.getByCategory(readText("Wybierz kategorię: "));
            Vehicle vehicle = Vehicle.builder()
                    .category(config.getCategory())
                    .brand(readText("Marka: "))
                    .model(readText("Model: "))
                    .year(readInt("Rok produkcji: "))
                    .plate(readText("Nr rejestracyjny: "))
                    .price(readDouble("Cena za dobę: "))
                    .build();

            for (Map.Entry<String, String> entry : config.getAttributes().entrySet()) {
                vehicle.addAttribute(entry.getKey(), readAttributeValue(entry.getKey(), entry.getValue()));
            }

            Vehicle saved = vehicleService.addVehicle(vehicle);
            System.out.println(">>> Dodano pojazd o ID: " + saved.getId());
        } catch (Exception e) {
            System.out.println("BŁĄD: " + e.getMessage());
        }
    }

    private void deleteVehicle() {
        try {
            vehicleService.removeVehicle(readText("ID pojazdu do usunięcia: "));
            System.out.println("Pojazd usunięty pomyślnie.");
        } catch (Exception e) {
            System.out.println("BŁĄD: " + e.getMessage());
        }
    }

    private void showAllUsers() {
        System.out.println("\n--- LISTA UŻYTKOWNIKÓW W SYSTEMIE ---");
        userService.findAllUsers().forEach(user -> {
            String status = rentalService.userHasActiveRental(user.getId()) ? "[POSIADA AUTO]" : "[BRAK AKTYWNYCH]";
            System.out.println(user.getLogin() + " | " + user.getRole() + " | " + status);
        });
    }

    private void rentVehicle() {
        try {
            String vId = readText("ID pojazdu do wypożyczenia: ");
            rentalService.rentVehicle(currentUser.getId(), vId);
            System.out.println("Pojazd został wypożyczony!");
        } catch (Exception e) {
            System.out.println("BŁĄD: " + e.getMessage());
        }
    }

    private void returnVehicle() {
        try {
            rentalService.returnVehicle(currentUser.getId());
            System.out.println("Pojazd zwrócony pomyślnie.");
        } catch (Exception e) {
            System.out.println("BŁĄD: " + e.getMessage());
        }
    }

    private void showCurrentUserData() {
        System.out.println("\n--- TWOJE DANE ---");
        System.out.println("Login: " + currentUser.getLogin() + " | ID: " + currentUser.getId());
        rentalService.findActiveRentalByUserId(currentUser.getId()).ifPresentOrElse(
                r -> System.out.println("Aktualnie wypożyczasz: " + r.getVehicleId()),
                () -> System.out.println("Brak aktywnych wypożyczeń.")
        );
    }

    private void showRentalHistory() {
        List<Rental> rentals = rentalService.findAllRentals();
        if (rentals.isEmpty()) System.out.println("Historia jest pusta.");
        else rentals.forEach(r -> {
            String status = (r.getReturnDateTime() == null) ? "AKTYWNE" : "ZWRÓCONE (" + r.getReturnDateTime() + ")";
            System.out.println("Pojazd: " + r.getVehicleId() + " | User: " + r.getUserId() + " | " + status);
        });
    }

    // --- METODY POMOCNICZE KOLEGI (ZWALCZAJĄ CRASHE) ---

    private String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("BŁĄD: Pole nie może być puste.");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            try { return Integer.parseInt(readText(prompt)); }
            catch (NumberFormatException e) { System.out.println("BŁĄD: Wpisz liczbę!"); }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            try { return Double.parseDouble(readText(prompt).replace(",", ".")); }
            catch (NumberFormatException e) { System.out.println("BŁĄD: Wpisz poprawną kwotę!"); }
        }
    }

    private Object readAttributeValue(String name, String type) {
        return switch (type.toLowerCase()) {
            case "string" -> readText(name + " (tekst): ");
            case "number", "integer" -> readDouble(name + " (liczba): ");
            case "boolean" -> {
                System.out.print(name + " (true/false): ");
                yield Boolean.parseBoolean(scanner.nextLine());
            }
            default -> readText(name + ": ");
        };
    }
}