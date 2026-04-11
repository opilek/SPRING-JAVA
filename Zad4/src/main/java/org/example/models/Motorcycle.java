package org.example.models;

import java.util.Map;

public class Motorcycle extends Vehicle {

    public Motorcycle(String id, String brand, String model, int year, String plate, double price, Map<String, Object> attributes) {
        super(id, "Motorcycle", brand, model, year, plate, price, attributes);
    }

    public Motorcycle(Motorcycle other)
    {
        super(other);
    }

    @Override
    public String getCategory() {
        return "Motorcycle";
    }

    @Override
    public Vehicle copy()
    {
        return new Motorcycle(this);
    }


}
