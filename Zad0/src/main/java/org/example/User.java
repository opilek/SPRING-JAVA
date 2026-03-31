package org.example;

public class User
{
    private String login;
    private String password;
    private String rentedVehicleId;
    private Role role;

    public String getLogin() {return login;}
    public String getPassword() {return password;}
    public String getRentedVehicleId() {return rentedVehicleId;}

    public Role getRole() {return role;}

    public void setLogin(String login) {this.login = login;}
    public void setPassword(String password) {this.password = password;}
    public void setRentedVehicleId(String rentedVehicleId) {this.rentedVehicleId = rentedVehicleId;}
    public void setRole(Role role) {this.role = role;}

    public User(String login, String password, Role role,String rentedVehicleId)
    {
        this.login = login;
        this.password = password;
        this.role = role;
        this.rentedVehicleId = rentedVehicleId;


    }

    public User(User other)
    {
        this.login = other.login;
        this.password = other.password;
        this.role = other.role;
        this.rentedVehicleId = other.rentedVehicleId;
    }

    public String toCSV()
    {
        return login + ";" + password + ";" + role + ";" + rentedVehicleId;
    }
}
