package ru.gurtovenko.bankdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.gurtovenko.bankdemo.config.JwtProperties;

@SpringBootApplication
@EnableJpaRepositories
public class BankDemoApplication {
    @Autowired
    private JwtProperties jwtProperties;

    public static void main(String[] args) {
        new SpringApplicationBuilder(BankDemoApplication.class)
                .logStartupInfo(false)
                .web(WebApplicationType.REACTIVE)
                .run(args);
    }

}
