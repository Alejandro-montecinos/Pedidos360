package com.duocuc.bff.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class JwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // 1. Extraer 'roles' del token de Entra ID (Ej: ["Admin", "User"])
        List<String> roles = jwt.getClaimAsStringList("roles");
        if (roles != null) {
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                // También agregamos la versión sin prefijo para mayor flexibilidad
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }

        // 2. Extraer 'scp' (scopes) del token de Entra ID (Ej: "access_as_user")
        String scp = jwt.getClaimAsString("scp");
        if (scp != null && !scp.isBlank()) {
            String[] scopes = scp.split("\\s+");
            for (String scope : scopes) {
                authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
                authorities.add(new SimpleGrantedAuthority(scope));
            }
        }

        // 3. Extraer 'groups' si existen en Entra ID
        List<String> groups = jwt.getClaimAsStringList("groups");
        if (groups != null) {
            for (String group : groups) {
                authorities.add(new SimpleGrantedAuthority("GROUP_" + group));
            }
        }

        return authorities;
    }
}
