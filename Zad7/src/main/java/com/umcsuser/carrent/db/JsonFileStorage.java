package com.umcsuser.carrent.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JsonFileStorage<T> {
    private final String fileName;
    private final Type type;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();

    public JsonFileStorage(String fileName, Type type) {
        this.fileName = fileName;
        this.type = type;
    }

    public List<T> load() {
        File file = new File(fileName);
        if (!file.exists()) return new ArrayList<>();

        try (Reader reader = new FileReader(file)) {
            List<T> data = gson.fromJson(reader, type);
            return data != null ? data : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Błąd odczytu: " + fileName, e);
        }
    }

    public void save(List<T> data) {
        try (Writer writer = new FileWriter(fileName)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            throw new RuntimeException("Błąd zapisu: " + fileName, e);
        }
    }
}