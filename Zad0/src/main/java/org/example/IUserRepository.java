package org.example;

import java.util.List;

public interface IUserRepository
{
    public User getUser(String login);
    public List<User> getUsers();
    public void update(User user);
    public void addUser(User user);
    public void deleteUser(User user);
}
