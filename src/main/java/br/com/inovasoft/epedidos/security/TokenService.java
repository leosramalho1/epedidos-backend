package br.com.inovasoft.epedidos.security;

import br.com.inovasoft.epedidos.security.jwt.JwtCustomClaims;
import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.jwt.JwtClaims;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonNumber;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@RequestScoped
public class TokenService {

    public final static Integer TOKEN_EXPIRATION_TIME_IN_MINUTES = 360;

    @Inject
    JsonWebToken jsonWebToken;

    public Long getSystemId() {
        return ((JsonNumber) getJwtClaim(JwtCustomClaims.SYSTEM_ID)).longValue();
    }

    public String generateAdminToken(String email, String username) {
        return generateToken(email, username, JwtRoles.USER_ADMIN);
    }

    public String generateBackofficeToken(String email, String username, Long systemId, String systemKey) {
        return generateToken(email, username, systemId, systemKey, JwtRoles.USER_BACKOFFICE);
    }

    public boolean validateToken(String token) {
        return TokenUtil.validateToken(token);
    }

    public String generateToken(String subject, String name, String... roles) {
        try {
            JwtClaims jwtClaims = buildJwtClaims(subject, name, roles);
            String token = TokenUtil.generateTokenString(jwtClaims);
            log.info("TOKEN generated: " + token);
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String generateToken(String subject, String name, Long systemId, String systemKey, String... roles) {
        try {

            JwtClaims jwtClaims = buildJwtClaims(subject, name, roles);
            jwtClaims.setClaim(JwtCustomClaims.SYSTEM_ID, systemId);
            jwtClaims.setClaim(JwtCustomClaims.SYSTEM_KEY, systemKey);

            String token = TokenUtil.generateTokenString(jwtClaims);
            log.info("TOKEN generated: " + token);
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public JwtClaims buildJwtClaims(String subject, String name, String... roles) {
        try {
            JwtClaims jwtClaims = new JwtClaims();
            jwtClaims.setIssuer("Inovasoft"); // change to your company
            jwtClaims.setJwtId(UUID.randomUUID().toString());
            jwtClaims.setSubject(subject);
            jwtClaims.setClaim(Claims.upn.name(), subject);
            jwtClaims.setClaim(Claims.preferred_username.name(), name); // add more
            jwtClaims.setClaim(Claims.groups.name(), Arrays.asList(roles));
            jwtClaims.setAudience("using-jwt");
            jwtClaims.setExpirationTimeMinutesInTheFuture(TOKEN_EXPIRATION_TIME_IN_MINUTES);

            return jwtClaims;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Object getJwtClaim(String claimName) {
        Object claim = jsonWebToken.getClaim(claimName);

        if (claim == null) {
            throw new IllegalAccessError(String.format("%s not found.", claimName));
        }

        return claim;
    }

    public String getUserEmail() {
        return (String) getJwtClaim(Claims.upn.name());
    }

}