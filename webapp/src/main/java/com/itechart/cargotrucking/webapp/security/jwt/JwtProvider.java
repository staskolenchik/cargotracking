package com.itechart.cargotrucking.webapp.security.jwt;

import com.itechart.cargotrucking.core.client.service.ClientService;
import com.itechart.cargotrucking.core.user.User;
import com.itechart.cargotrucking.core.user.exception.UserNotFoundException;
import com.itechart.cargotrucking.core.user.service.UserService;
import com.itechart.cargotrucking.webapp.security.Role;
import com.itechart.cargotrucking.webapp.security.UserCredentials;
import com.itechart.cargotrucking.webapp.security.exception.InvalidJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class JwtProvider {
    @Value("${security.secret_key}")
    private String key;

    @Value("${security.validity_access_time}")
    private long validityAccessTime;

    @Value("${security.validity_refresh_time}")
    private long validityRefreshTime;

    private UserDetailsService userDetailsService;
    private UserService userService;
    private ClientService clientService;

    @Autowired
    public JwtProvider(UserDetailsService userDetailsService, UserService userService, ClientService clientService) {
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.clientService = clientService;
    }

    public String createAccessToken(String login, Set<Role> roles) {
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS512");
        header.put("typ", "JWT");

        User user = userService.findByLogin(login);

        Claims claims = Jwts.claims().setSubject(login);
        claims.put("auth", roles);
        claims.put("clientId", user.getClientId());

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityAccessTime);

        return Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    public String createRefreshToken(long userId) {
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS512");
        header.put("typ", "JWT");

        Claims claims = Jwts.claims();
        claims.put("userId", userId);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityRefreshTime);

        return Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtException(e);
        }
    }

    public void validateClientExist(User user) {
        if (user.getClientId() == null) {
            return;
        }
        if (!clientService.existsById(user.getClientId())) {
            throw new UserNotFoundException("User with id %s doesn't exists", user.getId());
        }
    }

    public Authentication getAuthentication(String token) {
        String login = getLogin(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(login);
        User user = userService.findByLogin(login);

        validateClientExist(user);
        UserCredentials userCredentials = new UserCredentials(user.getClientId(), user.getId());

        return new UsernamePasswordAuthenticationToken(userDetails, userCredentials, userDetails.getAuthorities());
    }

    private String getLogin(String token) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
    }

    String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
