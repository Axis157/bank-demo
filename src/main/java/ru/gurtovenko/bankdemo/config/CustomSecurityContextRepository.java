package ru.gurtovenko.bankdemo.config;

import io.jsonwebtoken.JwtException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.gurtovenko.jwt.JwtUnpacker;
import ru.gurtovenko.jwt.util.CustomUserDetails;

import java.util.List;

@Component
public class CustomSecurityContextRepository implements ServerSecurityContextRepository {
    private final static Logger logger = LogManager.getLogger(CustomSecurityContextRepository.class);

    private static List<String> excludePaths = List.of("/login", "/registration", "/refresh", "/logout");

    private final JwtProperties jwtProperties;

    @Autowired
    public CustomSecurityContextRepository(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        return null;
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        String path = request.getURI().getPath();
        for (String excludePath: excludePaths) {
            if (path.startsWith(excludePath)) {
                return Mono.empty();
            }
        }

        Authentication authentication = null;
        try {
            authentication = JwtUnpacker.fetchBasicAuthentication(serverWebExchange,
                    jwtProperties.getAccessToken());
        } catch (JwtException e) {
            logger.error(e);
        }

        if (authentication == null) {
            return Mono.empty();
        }


        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getDetails();

        customUserDetails.setRequestId(request.getId());

        return Mono.just(new SecurityContextImpl(authentication));
    }
}
