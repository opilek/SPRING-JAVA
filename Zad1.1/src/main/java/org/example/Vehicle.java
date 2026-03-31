package org.example;

public abstract class Vehicle
{
    private String id;
    private String brand;
    private String model;
    private int year;
    private double price;
    private boolean rented;



    public String getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public double getPrice() { return price; }
    public boolean isRented() { return rented; }

    public void setPrice(double price) { this.price = price; }
    public void setRented(boolean rented) { this.rented = rented; }

    public Vehicle(String id, String brand, String model, int year, double price, boolean rented)
    {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.rented = rented;
    }
    //Konstruktor kopiujący
    public Vehicle(Vehicle other) {
        this.id = other.id;
        this.brand = other.brand;
        this.model = other.model;
        this.year = other.year;
        this.price = other.price;
        this.rented = other.rented;
    }
    // Metoda polimorficzna do głębokiej kopii
    public abstract Vehicle copy();

    public String toCSV()
    {
        return id + ";" + brand + ";" + model + ";" + year + ";" + price + ";" + rented;
    }

    @Override
    public String toString()
    {
        return String.format("[%s] %s %s (%d) - %.2f PLN [Wypożyczony: %b]",
                id, brand, model, year, price, rented);
    }
}