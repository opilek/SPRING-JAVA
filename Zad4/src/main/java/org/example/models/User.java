package org.example.models;

public class User
{
    private String id;
    private String login;
    private String passwordHash;
    private Role role;

    public String getId() {return id;}
    public String getLogin() {return login;}
    public String getPasswordHash() {return passwordHash;}
    public Role getRole() {return role;}

    public void setId(String id) {this.id = id;}
    public void setLogin(String login) {this.login = login;}
    public void setPasswordHash(String passwordHash) {this.passwordHash = passwordHash;}
    public void setRole(Role role) {this.role = role;}

    public User(String id,String login, String passwordHash, Role role)
    {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;


    }

    public User(User other)
    {
        this.id = other.id;
        this.login = other.login;
        this.passwordHash = other.passwordHash;
        this.role = other.role;
    }

}
