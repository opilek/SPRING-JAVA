package org.example.repositories.impl;

import com.google.gson.reflect.TypeToken;
import org.example.db.JsonFileStorage;
import org.example.models.User;
import org.example.repositories.UserRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserJsonRepository implements UserRepository
{
    private final JsonFileStorage<User> storage;
    private final List<User> users;

    public UserJsonRepository()
    {
        Type userListType = new TypeToken<ArrayList<User>>(){}.getType();

        this.storage = new JsonFileStorage<>("users.json",userListType);
        this.users = storage.load();
    }

    @Override
    public List<User> findAll()
    {
        return users;

    }

    @Override
    public Optional<User> findById(String id)
    {
        for(User u: users)
        {
            if(u.getId().equals(id))
            {
                return Optional.of(u);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findByLogin(String login)
    {
        for(User u: users)
        {
            if (u.getLogin().equals(login))
            {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    @Override
    public User save(User user)
    {
        users.removeIf(u -> u.getId().equals(user.getId()));
        users.add(user);
        storage.save(users);

        return user;
    }

    @Override
    public void deleteById(String id)
    {
        users.removeIf(u -> u.getId().equals(id));

        storage.save(users);
    }
}
