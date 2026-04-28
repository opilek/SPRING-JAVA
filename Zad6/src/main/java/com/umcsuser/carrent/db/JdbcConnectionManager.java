package com.umcsuser.carrent.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConnectionManager
{
    private static JdbcConnectionManager instance;
    private final String url;

    private JdbcConnectionManager()
    {

        this.url = System.getenv("DB_URL");

        if (this.url == null || this.url.isEmpty()) {
            throw new RuntimeException("BŁĄD: Zmienna środowiskowa DB_URL nie została znaleziona!");
        }
    }

    public static JdbcConnectionManager getInstance()
    {
        if (instance == null)
        {
            instance = new JdbcConnectionManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException
    {

        return DriverManager.getConnection(url);
    }
}