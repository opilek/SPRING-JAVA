package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class IVehicleRepositoryImpl implements IVehicleRepository
{
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final String FILE_NAME = "vehicles.txt";

    public IVehicleRepositoryImpl()
    {
        load(FILE_NAME);
    }

    @Override
    public boolean rentVehicle(String id)
    {
        for (Vehicle v : vehicles)
        {
            if (v.getId().equals(id) && !v.isRented())
            {
                v.setRented(true);
                save(FILE_NAME);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean returnVehicle(String id)
    {
        for (Vehicle v : vehicles)
        {
            if (v.getId().equals(id) && v.isRented())
            {
                v.setRented(false);
                save(FILE_NAME);
                return true;
            }
        }
        return false;
    }
    @Override
    public List<Vehicle> getVehicles()
    {
        List<Vehicle> finalList = new ArrayList<>();

        for (Vehicle v : vehicles)
        {
            finalList.add(v.copy());
        }

        return finalList;
    }

    public void save(String fileName)
    {
        try(PrintWriter writer = new PrintWriter(new FileWriter(fileName)))
        {
            for(Vehicle v: vehicles)
            {
                writer.println(v.toCSV());
            }
        }
        catch (IOException  e)
        {
            System.err.println("Problem z zapisem do pliku: " + e.getMessage());
        }
    }

    public void load(String fileName)
    {
        File file = new File(fileName);
        if(!file.exists())
        {
            return;
        }
        vehicles.clear();

        try(BufferedReader reader = new BufferedReader(new FileReader(fileName)))
        {
            String line;
            while((line = reader.readLine()) != null)
            {
                String parts[] = line.split(";");
                String type = parts[0];
                String id = parts[1];
                String brand = parts[2];
                String model = parts[3];
                int year = Integer.parseInt(parts[4]);
                double price = Double.parseDouble(parts[5]);
                boolean rented = Boolean.parseBoolean(parts[6]);

                if(type.equals("CAR"))
                {
                    vehicles.add(new Car(id,brand,model,year,price,rented));
                }
                else if(type.equals("MOTORCYCLE"))
                {
                    String category = parts[7];

                    vehicles.add(new Motorcycle(id,brand,model,year,price,rented,category));
                }
            }
        }
        catch (IOException e)
        {
            System.err.println("Problem z odczytem pliku: " + e.getMessage());
        }
    }

    @Override
    public void addVehicle(Vehicle vehicle)
    {
        this.vehicles.add(vehicle);
        save(FILE_NAME);
    }

    @Override
    public void deleteVehicle(String id)
    {
        vehicles.removeIf(v -> v.getId().equals(id));
        save(FILE_NAME);
    }

}
