package ru.gurtovenko.bankdemo.service;

import io.jsonwebtoken.JwtException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import reactor.core.publisher.Mono;
import ru.gurtovenko.bankdemo.config.JwtProperties;
import ru.gurtovenko.bankdemo.model.Account;
import ru.gurtovenko.bankdemo.repo.AccountRepository;
import ru.gurtovenko.bankdemo.service.request.LoginRequest;
import ru.gurtovenko.bankdemo.service.response.LoginResponse;
import ru.gurtovenko.jwt.JwtPacker;
import ru.gurtovenko.jwt.authentication.AccountAuthentication;
import ru.gurtovenko.jwt.dto.payload.AccountInfo;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AuthService {
    private final static Logger logger = LogManager.getLogger(AuthService.class);

    private final AccountRepository accountRepository;
    private final JwtProperties jwtProperties;

    @Autowired
    public AuthService(AccountRepository accountRepository,
                       JwtProperties jwtProperties) {
        this.accountRepository = accountRepository;
        this.jwtProperties = jwtProperties;
    }

    public Mono<LoginResponse> registration(Account account) {
        if (accountRepository.findAccountByUsername(account.getUsername()) != null) {
            return Mono.empty();
        }
        String md5password = DigestUtils.md5DigestAsHex(account.getPassword().getBytes());
        account.setPassword(md5password);
        accountRepository.save(account);

        return getLoginResponse(account);
    }

    public Mono<LoginResponse> login(LoginRequest loginRequest) {
        String username = loginRequest.getLogin();
        String password = loginRequest.getPassword();
        String md5password = DigestUtils.md5DigestAsHex(password.getBytes());
        Account account = accountRepository.findAccountByUsernameAndPassword(username, md5password);
        if (account == null || account.getId() == null) {
            return Mono.fromSupplier(() -> new LoginResponse("Account by this credentials not found."));
        }

        return getLoginResponse(account);
    }

    private Mono<LoginResponse> getLoginResponse(Account account) {
        LoginResponse loginResponse = new LoginResponse(createAccessToken(account), createRefreshToken(account));

        return Mono.just(loginResponse);
    }

    private String createAccessToken(Account account) {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setId(account.getId());
        accountInfo.setUsername(account.getUsername());
        accountInfo.setFirstName(account.getFirstName());
        accountInfo.setLastName(account.getLastName());

        List<GrantedAuthority> authorities = new ArrayList<>();
        account.getRoles().forEach(eRole -> authorities.add(new SimpleGrantedAuthority(eRole.name())));

        Date validUntil = Date.from(LocalDateTime.now().plusHours(jwtProperties.getAccessTokenExpiration())
                .toInstant(ZoneOffset.UTC));
        Date now = new Date();

        AccountAuthentication accountAuthentication = new AccountAuthentication(authorities, accountInfo,
                account.getUsername(), validUntil.getTime());
        accountAuthentication.setAuthenticated(true);

        try {
            return JwtPacker.compact(accountAuthentication, now, jwtProperties.getAccessToken());
        } catch (JwtException e) {
            logger.error(e);
        }

        return null;
    }

    private String createRefreshToken(Account account) {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setId(account.getId());
        accountInfo.setUsername(account.getUsername());
        accountInfo.setFirstName(account.getFirstName());
        accountInfo.setLastName(account.getLastName());

        Date validUntil = Date.from(LocalDateTime.now().plusDays(jwtProperties.getRefreshTokenExpiration())
                .toInstant(ZoneOffset.UTC));
        Date now = new Date();

        AccountAuthentication accountAuthentication = new AccountAuthentication(accountInfo,
                account.getUsername(), validUntil.getTime());
        accountAuthentication.setAuthenticated(true);

        try {
            return JwtPacker.compact(accountAuthentication, now, jwtProperties.getRefreshToken());
        } catch (JwtException e) {
            logger.error(e);
        }

        return null;
    }
}
