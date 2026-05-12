package org.example.repositories.impl;

import com.google.gson.reflect.TypeToken;
import org.example.db.JsonFileStorage;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserJsonRepository implements UserRepository {
    private final List<User> users;
    private final JsonFileStorage<User> storage =
            new JsonFileStorage<>("src/main/resources/users.json", new TypeToken<List<User>>() {}.getType());

    public UserJsonRepository() {
        this.users = new ArrayList<>(this.storage.load());
    }

    @Override
    public List<User> findAll() {
        List<User> copy = new ArrayList<>();
        for(User user : this.users){
            copy.add(user.copy());
        }
        return copy;
    }

    @Override
    public Optional<User> findById(String id) {
        return this.users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .map(User::copy);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return this.users.stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst()
                .map(User::copy);
    }

    @Override
    public User save(User user) {
        if(user == null)
            throw new IllegalArgumentException("user cannot be null");

        User toSave = user.copy();
        if(toSave.getId() == null || toSave.getId().isBlank())
            toSave.setId(UUID.randomUUID().toString());
        else
            this.users.removeIf(u -> u.getId().equals(toSave.getId()));

        this.users.add(toSave);
        this.storage.save(this.users);
        return toSave.copy();
    }

    @Override
    public void deleteById(String id) {
        this.users.removeIf(user ->user.getId().equals(id));
        this.storage.save(this.users);
    }
}
