package org.example;

import java.util.List;

public interface IVehicleRepository
{
    public boolean rentVehicle(String id);
    public boolean returnVehicle(String id);
    public List<Vehicle> getVehicles();
    public void addVehicle(Vehicle vehicle);
    public void deleteVehicle(String id);
}
