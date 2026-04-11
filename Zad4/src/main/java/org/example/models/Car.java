package org.example.models;

import java.util.Map;

public class Car extends Vehicle
{

    public Car(String id, String brand, String model, int year, String plate, double price, Map<String, Object> attributes) {
        super(id, "Car", brand, model, year, plate, price, attributes);

    }

    public Car(Car other)
    {
        super(other);
    }

    @Override
    public String getCategory() {
        return "Car";
    }

    @Override
    public Vehicle copy()
    {
        return new Car(this);
    }
}
