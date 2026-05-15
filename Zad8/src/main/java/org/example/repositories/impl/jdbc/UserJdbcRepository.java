package org.example.repositories.impl.jdbc;

import org.example.db.JdbcConnectionManager;
import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jdbc")
public class UserJdbcRepository implements UserRepository {
    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){

            while(rs.next()){
                users.add(this.mapRow(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException("Error occurred while reading users", e);
        }
        return users;
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){

            stmt.setString(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    User user = mapRow(rs);
                    return Optional.of(user);
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error occurred while reading user", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";

        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){

            stmt.setString(1, login);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    User user = mapRow(rs);
                    return Optional.of(user);
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error occurred while reading user", e);
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        String sql;
        boolean add;

        if(user.getId() == null || user.getId().isBlank()){
            add = true;
            user.setId(UUID.randomUUID().toString());
            sql = "INSERT INTO users (id, login, password_hash, role) VALUES (?, ?, ?, ?)";
        }else{
            add = false;
            sql = "UPDATE users SET login = ?, password_hash = ?, role = ? WHERE id = ?";
        }

        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){

            if(add){
                stmt.setString(1, user.getId());
                stmt.setString(2, user.getLogin());
                stmt.setString(3, user.getPasswordHash());
                stmt.setString(4, user.getRole().toString());
            }else{
                stmt.setString(1, user.getLogin());
                stmt.setString(2, user.getPasswordHash());
                stmt.setString(3, user.getRole().toString());
                stmt.setString(4, user.getId());
            }

            stmt.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException("Error occurred while saving user", e);
        }
        return user;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM users WHERE ID = ?";

        try(Connection connection = JdbcConnectionManager.getInstance().getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){

            stmt.setString(1, id);
            stmt.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException("Error occurred while deleting user", e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getString("id"))
                .login(rs.getString("login"))
                .passwordHash(rs.getString("password_hash"))
                .role(Role.valueOf(rs.getString("role")))
                .build();
    }
}
