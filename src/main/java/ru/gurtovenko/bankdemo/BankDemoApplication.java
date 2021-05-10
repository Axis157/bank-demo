package ru.gurtovenko.bankdemo;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class BankDemoApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(BankDemoApplication.class)
                .logStartupInfo(false)
                .web(WebApplicationType.REACTIVE)
                .run(args);
    }

}
