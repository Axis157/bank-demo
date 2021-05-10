package ru.gurtovenko.bankdemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProperties {

    @Value("${application.jwt.accesstokensecret}")
    private String accessToken;

    @Value("${application.jwt.accesstoken-expiration}")
    private Integer accessTokenExpiration;

    @Value("${application.jwt.refreshtokensecret}")
    private String refreshToken;

    @Value("${application.jwt.refreshtoken-expiration}")
    private Integer refreshTokenExpiration;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public void setAccessTokenExpiration(Integer accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Integer getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public void setRefreshTokenExpiration(Integer refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }
}
