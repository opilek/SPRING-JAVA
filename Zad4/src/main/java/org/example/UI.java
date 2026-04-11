package org.example;

import org.example.models.*;
import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleRepository;
import org.example.services.AuthService;

import java.time.LocalDateTime;
import java.util.*;

public class UI
{
    private final VehicleRepository vehicleRepo;
    private final UserRepository userRepo;
    private final RentalRepository rentalRepo;
    private final AuthService authService;
    private final Scanner scanner;
    private User loggedUser;

    public UI(VehicleRepository vehicleRepo,
              UserRepository userRepo,
              RentalRepository rentalRepo,
              AuthService authService)
    {
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
        this.rentalRepo = rentalRepo;
        this.authService = authService;
        this.scanner = new Scanner(System.in);
        this.loggedUser = null;
    }

    public void start()
    {
        while (true)
        {

            if (loggedUser == null)
            {
                System.out.println("\n******* SYSTEM WYPOŻYCZALNI *******");
                System.out.println("1. Logowanie");
                System.out.println("2. Rejestracja");
                System.out.println("3. Wyjdź");
                System.out.print("Wybierz opcję: ");


                String input = scanner.nextLine();
                int option;
                try
                {
                    option = Integer.parseInt(input);
                }
                catch (NumberFormatException e)
                {
                    System.out.println("Błąd: Podaj cyfrę!");
                    continue;
                }

                switch (option)
                {
                    case 1:
                        System.out.println("\n******* LOGOWANIE *******");
                        System.out.print("Wprowadź login: ");
                        String login = scanner.nextLine();
                        System.out.print("Wprowadź hasło: ");
                        String password = scanner.nextLine();

                        Optional<User> user = authService.login(login, password);

                        if (user.isPresent())
                        {
                            this.loggedUser = user.get();
                            System.out.println("Zalogowano pomyślnie jako: " + loggedUser.getLogin());
                        }
                        else
                        {
                            System.out.println("Błąd: Niepoprawny login lub hasło!");
                        }
                        break;

                    case 2:
                        System.out.println("\n******* REJESTRACJA *******");
                        System.out.print("Podaj nowy login: ");
                        String newLogin = scanner.nextLine();
                        System.out.print("Podaj nowe hasło: ");
                        String newPassword = scanner.nextLine();

                        if(authService.register(newLogin,newPassword))
                        {
                            System.out.println("Sukces utworzono nowego użytkownika");

                             this.loggedUser = authService.login(newLogin, newPassword).get();

                            System.out.println("Zostałeś automatycznie zalogowany jako: " + loggedUser.getLogin());
                        }
                        else
                        {
                            System.out.println("Błąd login jest zajęty");
                        }

                        break;

                    case 3:
                        System.out.println("Zamykanie systemu...");
                        return;

                }
            }
            else
            {

                System.out.println("\nZalogowano jako " + loggedUser.getLogin() + "[" + loggedUser.getRole() + "]");

                if(loggedUser.getRole().equals(Role.ADMIN))
                {
                    System.out.println("\n******* MENU *******");
                    System.out.println("1. Dodaj pojazd");
                    System.out.println("2. Usuń pojazd");
                    System.out.println("3. Lista pojazdów");
                    System.out.println("4. Lista użytkowników");
                    System.out.println("5. Usuń użytkownika");
                    System.out.println("6. Wyloguj");
                    System.out.print("Wybierz opcję: ");

                    String input = scanner.nextLine();
                    int choice;

                    try
                    {
                        choice = Integer.parseInt(input);
                    }
                    catch (NumberFormatException e)
                    {
                        System.out.println("Błąd: Podaj cyfrę!");
                        continue;
                    }

                    switch (choice)
                    {
                        case 1:
                            System.out.print("\n******* RODZAJ POJAZDU *******\n");
                            System.out.println("1. Auto");
                            System.out.println("2. Motocykl");
                            int type = Integer.parseInt(scanner.nextLine());

                            switch (type)
                            {
                                case 1:
                                    System.out.println("\n******* DODAWANIE SAMOCHODU *******");

                                    System.out.println("Podaj ID: ");
                                    String id = scanner.nextLine();

                                    System.out.println("Marka: ");
                                    String brand = scanner.nextLine();

                                    System.out.println("Model: ");
                                    String model = scanner.nextLine();

                                    System.out.println("Rok produkcji: ");
                                    int year = Integer.parseInt(scanner.nextLine());

                                    System.out.println("Numer rejestracyjny: ");
                                    String plate = scanner.nextLine();

                                    System.out.println("Cena za dobę: ");
                                    double price = Double.parseDouble(scanner.nextLine());

                                    Map<String, Object> attributes = new HashMap<>();

                                    Vehicle v = new Car(id,brand,model,year,plate,price,attributes);


                                    vehicleRepo.save(v);

                                    System.out.println("Pojazd pomyślnie został dodany do bazy pojazdów");
                                    break;

                                case 2:

                                    System.out.println("\n******* DODAWANIE MOTOCYKLA *******\n");

                                    System.out.println("Podaj ID: ");
                                    String idMoto = scanner.nextLine();

                                    System.out.println("Marka: ");
                                    String brandMoto = scanner.nextLine();

                                    System.out.println("Model: ");
                                    String modelMoto = scanner.nextLine();

                                    System.out.println("Rok produkcji: ");
                                    int yearMoto = Integer.parseInt(scanner.nextLine());

                                    System.out.println("Numer rejestracyjny: ");
                                    String plateMoto = scanner.nextLine();

                                    System.out.println("Cena za dobę: ");
                                    double priceMoto = Double.parseDouble(scanner.nextLine());

                                    Map<String, Object> attributesMoto = new HashMap<>();


                                    Vehicle vMoto = new Motorcycle(idMoto,brandMoto,modelMoto,yearMoto,plateMoto,priceMoto,attributesMoto);

                                    vehicleRepo.save(vMoto);

                                    System.out.println("Pojazd pomyślnie został dodany do bazy pojazdów");

                                    break;


                            }
                            break;


                        case 2: {
                            System.out.print("\n******* USUWANIE POJAZDU *******\n");
                            System.out.print("Podaj ID pojazdu do usunięcia: ");
                            String idToDelete = scanner.nextLine();


                            if (vehicleRepo.findById(idToDelete).isPresent())
                            {


                                boolean isBusy = false;
                                List<Rental> allRentals = rentalRepo.findAll();

                                for (Rental r : allRentals)
                                {
                                    if (r.getVehicleId().equals(idToDelete) && r.isActive())
                                    {
                                        isBusy = true;
                                        break;
                                    }
                                }

                                if (isBusy)
                                {
                                    System.out.println("Błąd: Nie można usunąć pojazdu! Jest on obecnie WYPOŻYCZONY.");
                                    System.out.println("Najpierw musi zostać zwrócony do bazy.");
                                }
                                else
                                {

                                    vehicleRepo.deleteById(idToDelete);
                                    System.out.println("Pojazd pomyślnie został usunięty z bazy pojazdów.");
                                }

                            }
                            else
                            {
                                System.out.println("Błąd: Nie znaleziono pojazdu o ID: " + idToDelete);
                            }
                            break;
                        }
                        case 3:

                            System.out.print("\n******* RODZAJ LISTY *******\n");

                            System.out.println("1. Lista wszystkich pojazdów");
                            System.out.println("2. Lista wypożyczonych pojazdów");
                            int listType;
                            System.out.println("Wybierz opcję: ");

                            try {
                                listType = Integer.parseInt(scanner.nextLine());
                            } catch (NumberFormatException e) {
                                System.out.println("Błąd: Podaj cyfrę 1 lub 2!");
                                break;
                            }


                            switch (listType)
                            {

                                case 1:

                                    System.out.print("\n******* WSZYSTKIE POJAZDY *******\n");

                                    List<Vehicle> allVehicles = vehicleRepo.findAll();

                                    if (allVehicles.isEmpty())
                                    {
                                        System.out.println("Baza pojazdów jest pusta.");
                                    }
                                    else
                                    {
                                        for(Vehicle v: allVehicles)
                                        {
                                            System.out.println(v);
                                        }
                                    }



                                    break;

                                case 2:

                                    System.out.print("\n******* WYPOŻYCZONE POJAZDY *******\n");

                                    List<Rental> allRentals = rentalRepo.findAll();
                                    boolean anyActive = false;

                                    for (Rental r : allRentals) {

                                        if (r.isActive()) {
                                            anyActive = true;
                                            vehicleRepo.findById(r.getVehicleId()).ifPresentOrElse(
                                                    v -> System.out.println("AUTO: [" + v.getId() + "] " + v.getBrand() + " " + v.getModel() +
                                                            " | KLIENT: " + r.getUserId() +
                                                            " | OD: " + r.getRentDateTime()),
                                                    () -> System.out.println("ID POJAZDU: " + r.getVehicleId() +
                                                            " [BŁĄD: Pojazd usunięty z bazy!] | KLIENT: " + r.getUserId())
                                            );
                                        }
                                    }

                                    if (!anyActive) {
                                        System.out.println("Obecnie wszystkie pojazdy są dostępne w garażu.");
                                    }
                                    break;

                                default:
                                    System.out.println("Nieprawidłowy rodzaj listy.");
                                    break;

                            }
                            break;


                        case 4:

                            System.out.print("\n******* LISTA UŻYTKOWNIKÓW *******\n");

                            List<User> allUsers = userRepo.findAll();

                            if (allUsers.isEmpty())
                            {
                                System.out.println("Baza użytkowników jest pusta.");
                            }
                            else
                            {
                                for(User u: allUsers)
                                {

                                    System.out.println("Login: " + u.getLogin() + " Rola: " + u.getRole());
                                }
                            }


                            break;


                        case 5: {
                            System.out.print("\n******* USUWANIE UŻYTKOWNIKA *******\n");
                            System.out.print("Podaj login użytkownika, którego chcesz usunąć: ");

                            String loginToDelete = scanner.nextLine();


                            if (loginToDelete.equals(loggedUser.getLogin())) {
                                System.out.println("Błąd: Nie możesz usunąć własnego konta!");
                            } else {
                                Optional<User> userOptional = userRepo.findByLogin(loginToDelete);

                                if (userOptional.isPresent()) {
                                    User userFound = userOptional.get();
                                    String targetUserId = userFound.getId();


                                    boolean hasActiveRental = false;
                                    List<Rental> allRentals = rentalRepo.findAll();

                                    for (Rental r : allRentals) {
                                        if (r.getUserId().equals(targetUserId) && r.isActive()) {
                                            hasActiveRental = true;
                                            break;
                                        }
                                    }

                                    if (hasActiveRental) {
                                        System.out.println("Błąd: Nie można usunąć użytkownika " + loginToDelete + "!");
                                        System.out.println("Ten użytkownik posiada obecnie aktywny wynajem. Musi najpierw zwrócić pojazd.");
                                    } else {
                                        // 3. Jeśli nie ma aktywnych wynajmów - usuwamy
                                        userRepo.deleteById(targetUserId);
                                        System.out.println("Użytkownik " + loginToDelete + " pomyślnie został usunięty.");
                                    }
                                } else {
                                    System.out.println("Błąd: Użytkownik o loginie '" + loginToDelete + "' nie istnieje!");
                                }
                            }
                            break;
                        }

                        case 6:

                            this.loggedUser = null;
                            System.out.println("Wylogowano pomyślnie.");
                            break;




                    }

                }
                else
                {

                    System.out.println("\n******* MENU *******");
                    System.out.println("1. Wyświetl listę dostępnych pojazdów do wypożyczenia");
                    System.out.println("2. Wypożycz pojazd");
                    System.out.println("3. Zwróć pojazd");
                    System.out.println("4. Wyloguj");
                    System.out.println("Wybierz opcję: ");

                    String input = scanner.nextLine();
                    int choice;

                    try
                    {
                        choice = Integer.parseInt(input);
                    }
                    catch (NumberFormatException e)
                    {
                        System.out.println("Błąd: Podaj cyfrę!");
                        continue;
                    }

                    switch (choice)
                    {

                        case 1: {

                            System.out.println("\n******* LISTA DOSTĘPNYCH POJAZDÓW *******");

                            List<Vehicle> allVehicles = vehicleRepo.findAll();
                            List<Rental> allRentals = rentalRepo.findAll();


                            for (Vehicle v : allVehicles) {
                                boolean isCurrentlyRented = false;

                                for (Rental r : allRentals) {
                                    if (r.getVehicleId().equals(v.getId()) && r.isActive()) {
                                        isCurrentlyRented = true;
                                        break;
                                    }
                                }

                                if (!isCurrentlyRented)
                                {
                                    System.out.println("ID:" + v.getId() +
                                            " | Kategoria: " + v.getCategory() +
                                            " | Marka: " + v.getBrand() +
                                            " | Model: " + v.getModel() +
                                            " | Rok: " + v.getYear() +
                                            " | Tablica rejstracyjna: " + v.getPlate() +
                                            " | Cena: " + v.getPrice() + " zł/doba" +
                                            " | Atrybuty: " + v.getAttribute(""));
                                }
                            }

                            break;
                        }

                        case 2: {

                            System.out.println("\n******* WYPOŻYCZ POJAZD *******");
                            System.out.println("Podaj ID pojazdu do wypożyczenia: ");
                            String idToRent = scanner.nextLine();
                            List<Rental> allRentals = rentalRepo.findAll();

                            boolean userHasActiveRental = false;

                            for(Rental r: allRentals)
                            {

                                if(r.getUserId().equals(loggedUser.getId()) && r.isActive())
                                {
                                    userHasActiveRental = true;
                                    break;

                                }
                            }

                            if (userHasActiveRental)
                            {
                                System.out.println("Błąd: Masz już wypożyczony pojazd! Najpierw go zwróć.");
                            }
                            else
                            {
                                if(!vehicleRepo.findById(idToRent).isPresent())
                                {
                                    System.out.println("Błąd: Pojazd o takim ID nie istnieje.");
                                }
                                else
                                {
                                    boolean vehicleIsBusy = false;

                                    for(Rental r: allRentals)
                                    {

                                        if(r.getVehicleId().equals(idToRent) && r.isActive())
                                        {
                                            vehicleIsBusy = true;
                                            break;

                                        }
                                    }

                                    if(vehicleIsBusy)
                                    {
                                        System.out.println("Błąd: Ten pojazd jest obecnie wypożyczony przez innego klienta.");
                                    }
                                    else
                                    {
                                        Rental newRental = new Rental(
                                                UUID.randomUUID().toString(),
                                                idToRent,
                                                loggedUser.getId(),
                                                LocalDateTime.now().toString(),
                                                null
                                        );

                                        rentalRepo.save(newRental);
                                        System.out.println("Sukces! Pojazd o ID " + idToRent + " został wypożyczony.");
                                    }

                                }
                            }
                            break;


                        }
                        case 3: {

                            System.out.println("\n******* ZWRÓĆ POJAZD *******");
                            List<Rental> allRentals = rentalRepo.findAll();
                            boolean found = false;

                            for(Rental r: allRentals)
                            {
                                if(r.getUserId().equals(loggedUser.getId()) && r.isActive())
                                {
                                    r.setReturnDateTime(LocalDateTime.now().toString());
                                    rentalRepo.save(r);

                                    System.out.println("Sukces! Pojazd został zwrócony.");
                                    found = true;
                                    break;
                                }

                            }

                            if (!found)
                            {
                                System.out.println("Błąd: Nie masz obecnie żadnych aktywnych wypożyczeń do zwrotu.");
                            }

                            break;



                        }
                        case 4:
                        {

                            this.loggedUser = null;
                            System.out.println("Wylogowano pomyślnie.");
                            break;
                            }


                    }

                }

            }
        }
    }
}
