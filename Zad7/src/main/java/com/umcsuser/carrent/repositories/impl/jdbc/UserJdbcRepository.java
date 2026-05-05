package com.umcsuser.carrent.repositories.impl.jdbc;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.db.JdbcConnectionManager;
import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserJdbcRepository implements UserRepository
{
    @Override
    public List<User> findAll()
    {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try(Connection conn = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                users.add(resultSetToUser(rs));
            }

        }
        catch (SQLException e)
        {
            System.out.println("Błąd: "  + e.getMessage());
        }

        return users;
    }

    public User resultSetToUser(ResultSet rs) throws SQLException
    {
        String id =  rs.getString("id");
        String login =  rs.getString("login");
        String password_hash =  rs.getString("password_hash");
        String roleStr = rs.getString("role");
        Role role = Role.valueOf(roleStr);



        return new User(id,login,password_hash,role);

    }
    @Override
    public Optional<User> findById(String id)
    {

        String sql = "SELECT * FROM users WHERE id = ?";

        try(Connection conn = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1,id);

            try(ResultSet rs = ps.executeQuery())
            {
                if(rs.next())
                {
                    return Optional.of(resultSetToUser(rs));
                }
            }

        }
        catch (SQLException e)
        {
            System.out.println("Błąd: " + e.getMessage());
        }

        return Optional.empty();

    }
    @Override
    public Optional<User> findByLogin(String login)
    {

        String sql = "SELECT * FROM users WHERE login = ?";

        try(Connection conn = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1,login);

            try(ResultSet rs = ps.executeQuery())
            {
                if(rs.next())
                {
                    return Optional.of(resultSetToUser(rs));
                }
            }

        }
        catch (SQLException e)
        {
            System.out.println("Błąd: " + e.getMessage());
        }

        return Optional.empty();



    }
    @Override
    public User save(User user)
    {
        String sql = "INSERT INTO users (id, login, password_hash, role) VALUES (?, ?, ?, ?)";

        try(Connection conn = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {

            ps.setString(1,user.getId());
            ps.setString(2,user.getLogin());
            ps.setString(3,user.getPassword());
            ps.setString(4,user.getRole().name());

            ps.executeUpdate();

        }
        catch (SQLException e)
        {
            System.out.println("Błąd: " + e.getMessage());
        }

        return user;



    }
    @Override
    public void deleteById(String id)
    {

        String sql = "DELETE FROM users WHERE id = ?";

        try(Connection conn = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {

            ps.setString(1,id);
            ps.executeUpdate();

        }
        catch (SQLException e)
        {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

}
