package ru.gurtovenko.bankdemo.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.gurtovenko.bankdemo.model.Account;
import ru.gurtovenko.bankdemo.service.AuthService;
import ru.gurtovenko.bankdemo.service.request.LoginRequest;
import ru.gurtovenko.bankdemo.service.response.LoginResponse;

@RestController
public class SystemController {
    private final AuthService authService;

    public SystemController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registration")
    public Mono<LoginResponse> registration(@RequestBody Account account) {
        return authService.registration(account);
    }

    @PostMapping(path = "/login")
    public Mono<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
