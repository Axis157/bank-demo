package ru.gurtovenko.bankdemo.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import ru.gurtovenko.bankdemo.model.role.ERole;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {

    private final CustomSecurityContextRepository customSecurityContextRepository;

    @Autowired
    public WebSecurityConfig(CustomSecurityContextRepository customSecurityContextRepository) {
        this.customSecurityContextRepository = customSecurityContextRepository;
    }

    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .logout()
                .logoutUrl("/logout")
                .and()
                .securityContextRepository(customSecurityContextRepository)

                .authorizeExchange()
                .pathMatchers( "/login", "/logout", "/registration/**")
                .permitAll()

                .anyExchange()
                .authenticated()

                .and()
                .build();
    }
}
