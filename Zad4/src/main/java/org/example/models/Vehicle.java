package org.example.models;

import java.util.HashMap;
import java.util.Map;

public abstract class Vehicle
{
    private String id;
    private String category;
    private String brand;
    private String model;
    private int year;
    private String plate;
    private double price;
    private Map<String,Object> attributes;



    public String getId() { return id; }
    public String getCategory() {return category;}
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public String getPlate() {return plate;}
    public double getPrice() { return price; }
    public Map<String, Object> getAttributes() {return attributes;}

    public void setPrice(double price) { this.price = price; }


    public Vehicle(String id,String category, String brand, String model, int year,String plate, double price,Map<String,Object> attributes)
    {
        this.id = id;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plate = plate;
        this.price = price;
        this.attributes = (attributes != null) ? attributes : new HashMap<>();
    }
    //Konstruktor kopiujący
    public Vehicle(Vehicle other)
    {
        this.id = other.id;
        this.category = other.category;
        this.brand = other.brand;
        this.model = other.model;
        this.year = other.year;
        this.plate = other.plate;
        this.price = other.price;
        this.attributes = new HashMap<>(other.attributes);

    }
    // Metoda polimorficzna do głębokiej kopii
    public abstract Vehicle copy();


    @Override
    public String toString()
    {
        return String.format("[%s] %s %s (%d) - %.2f PLN",
                id, brand, model, year, price);
    }


    public Object getAttribute(String key)
    {
        return attributes.get(key);
    }

    public void addAttribute(String key,Object value)
    {
        this.attributes.put(key, value);
    }

    public void removeAttribute(String key)
    {
        this.attributes.remove(key);
    }
}