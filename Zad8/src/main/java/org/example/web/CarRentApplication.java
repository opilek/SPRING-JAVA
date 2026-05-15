package org.example.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "org.example")
@EnableJpaRepositories(basePackages = "org.example.repositories") // To zmusi skaner JPA do pracy
@EntityScan(basePackages = "org.example.models")
public class CarRentApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(CarRentApplication.class,args);

    }


}
