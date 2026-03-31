package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IUserRepository
{
    private final String FILE_NAME = "users.csv";
    private final List<User> users = new ArrayList<>();

    public UserRepository()
    {
        load(FILE_NAME);
    }

    @Override
    public User getUser(String login)
    {
        for(User u: users)
        {
            if(u.getLogin().equals(login))
            {
                return new User(u);
            }
        }

        return null;

    }

    @Override
    public List<User> getUsers()
    {
        List<User> result = new ArrayList<>();

        for(User u: users)
        {
            result.add(new User(u));

        }

        return result;

    }

    public void save(String fileName)
    {
        try(PrintWriter writer = new PrintWriter(new FileWriter(fileName)))
        {
            for(User u : users)
            {
                writer.write(u.toCSV());
            }
        }
        catch(IOException e)
        {
            System.err.println("Problem z zapisem do pliku: " + e.getMessage());
        }
    }


    public void load(String fileName)
    {
        File file = new File(fileName);
        if(!file.exists())
        {
            return;
        }
        users.clear();

        try(BufferedReader reader = new BufferedReader(new FileReader(fileName)))
        {
            String line;
            while((line = reader.readLine()) != null)
            {
                String parts[] = line.split(";",-1);
                String login = parts[0];
                String password = parts[1];
                Role role = Role.valueOf(parts[2]);
                String rentedVehicleId = parts[3];

                users.add(new User(login,password,role,rentedVehicleId));
            }

        }
        catch(IOException e)
        {
            System.err.println("Problem z odczytem  pliku: " + e.getMessage());
        }

    }

    @Override
    public void update(User user)
    {
        for(int i=0 ;i<users.size();i++)
        {
            if(users.get(i).getLogin().equals(user.getLogin()))
            {
                users.set(i,user);
                save(FILE_NAME);

                return;
            }
        }
    }

    @Override
    public void addUser(User user)
    {
        users.add(user);
        save(FILE_NAME);
    }

    @Override
    public void deleteUser(User user)
    {
        users.removeIf(u -> u.getLogin().equals(user.getLogin()));
        save(FILE_NAME);
    }
}
