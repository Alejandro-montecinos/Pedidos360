package com.duocuc.bff.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public class JwtAudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final List<String> allowedAudiences;

    public JwtAudienceValidator(List<String> allowedAudiences) {
        this.allowedAudiences = allowedAudiences;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        List<String> audience = jwt.getAudience();

        if (audience != null) {
            for (String allowedAudience : allowedAudiences) {
                if (allowedAudience != null && !allowedAudience.isBlank()) {
                    if (audience.contains(allowedAudience) || audience.stream().anyMatch(aud -> aud.equalsIgnoreCase(allowedAudience))) {
                        return OAuth2TokenValidatorResult.success();
                    }
                }
            }
        }

        OAuth2Error error = new OAuth2Error(
                "invalid_audience",
                "El campo 'aud' (audience) del token (" + audience + ") no es valido para este servicio.",
                null
        );
        return OAuth2TokenValidatorResult.failure(error);
    }
}
