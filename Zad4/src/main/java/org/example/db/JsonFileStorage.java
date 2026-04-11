package org.example.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class JsonFileStorage<T>
{

    private final Gson gson;
    private final Path path;
    private final Type type;


    public JsonFileStorage(String filename, Type type, Gson gson)
    {
        this.path = Paths.get(filename);
        this.type = type;
        this.gson = gson;
    }

    public JsonFileStorage(String filename, Type type) {
        this(filename, type, new GsonBuilder().setPrettyPrinting().create());
    }

    public List<T> load()
    {
        if (!Files.exists(path)) return new ArrayList<>();

        try
        {
            String json = Files.readString(path);
            List<T> list = gson.fromJson(json, type);
            return list != null ? list : new ArrayList<>();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void save(List<T> data)
    {
        try
        {
            String json = gson.toJson(data);
            Files.writeString(path, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}