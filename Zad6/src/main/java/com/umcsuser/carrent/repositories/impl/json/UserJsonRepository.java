package com.umcsuser.carrent.repositories.impl.json;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.db.JsonFileStorage;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserJsonRepository implements UserRepository {
    private final JsonFileStorage<User> storage = new JsonFileStorage<>("users.json",
            new TypeToken<List<User>>(){}.getType());
    private final List<User> users;

    public UserJsonRepository() {
        this.users = new ArrayList<>(storage.load());
    }

    @Override
    public List<User> findAll() {
        return users.stream().map(User::copy).toList();
    }

    @Override
    public Optional<User> findById(String id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst().map(User::copy);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.stream().filter(u -> u.getLogin().equals(login)).findFirst().map(User::copy);
    }

    @Override
    public User save(User user) {
        users.removeIf(u -> u.getId().equals(user.getId()));
        users.add(user.copy());
        storage.save(users);
        return user;
    }

    @Override
    public void deleteById(String id) {
        users.removeIf(u -> u.getId().equals(id));
        storage.save(users);
    }
}