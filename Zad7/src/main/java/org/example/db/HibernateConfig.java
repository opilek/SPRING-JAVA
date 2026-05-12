package org.example.db;

import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import lombok.Getter;


public class HibernateConfig {
    @Getter
    private static final SessionFactory sessionFactory;

    static{
        try{
            Configuration configuration = new Configuration();

            configuration.setProperty("hibernate.connection.driver-class", "org.postgresql.Driver");
            configuration.setProperty("hibernate.connection.url", System.getenv("DB_URL"));

            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            configuration.setProperty("hibernate.show_sql", "false");
            configuration.setProperty("hibernate.format_sql", "false");
            configuration.setProperty("hibernate.hbm2ddl.auto", "validate");

            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Vehicle.class);
            configuration.addAnnotatedClass(Rental.class);

            sessionFactory = configuration.buildSessionFactory();
        }catch(Throwable e){
            throw new ExceptionInInitializerError("Initialize Hibernate ERROR: " + e);
        }
    }

    private HibernateConfig(){
    }
}
