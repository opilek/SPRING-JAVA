package org.example.models;

public class Rental
{
    private String id;
    private String vehicleId;
    private String userId;
    private String rentDateTime;
    private String returnDateTime;

    public String getId() {return id;}
    public String getVehicleId() {return vehicleId;}
    public String getUserId() {return userId;}
    public String getRentDateTime() {return rentDateTime;}
    public String getReturnDateTime() {return returnDateTime;}

    public void setId(String id) {this.id = id;}
    public void setVehicleId(String vehicleId) {this.vehicleId = vehicleId;}
    public void setUserId(String userId) {this.userId = userId;}
    public void setRentDateTime(String rentDateTime) {this.rentDateTime = rentDateTime;}
    public void setReturnDateTime(String returnDateTime) {this.returnDateTime = returnDateTime;}

    public Rental(String id,String vehicleId,String userId,String rentDateTime,String returnDateTime)
    {
        this.id = id;
        this.vehicleId = vehicleId;
        this.userId = userId;
        this.rentDateTime = rentDateTime;
        this.returnDateTime = returnDateTime;
    }

    public Rental(Rental other)
    {
        this.id = other.id;
        this.vehicleId = other.vehicleId;
        this.userId = other.userId;
        this.rentDateTime = other.rentDateTime;
        this.returnDateTime = other.returnDateTime;

    }

    public Rental copy() { return new Rental(this); }

    public boolean isActive()
    {
        return this.returnDateTime == null;
    }

}
