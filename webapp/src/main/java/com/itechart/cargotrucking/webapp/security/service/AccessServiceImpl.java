package com.itechart.cargotrucking.webapp.security.service;

import com.itechart.cargotrucking.core.security.RefreshToken;
import com.itechart.cargotrucking.core.security.RefreshTokenKey;
import com.itechart.cargotrucking.core.security.service.RefreshTokenService;
import com.itechart.cargotrucking.core.user.User;
import com.itechart.cargotrucking.core.user.exception.UserBadCredentialsException;
import com.itechart.cargotrucking.core.user.service.UserService;
import com.itechart.cargotrucking.webapp.security.Role;
import com.itechart.cargotrucking.webapp.security.dto.LoginDto;
import com.itechart.cargotrucking.webapp.security.dto.LogoutDto;
import com.itechart.cargotrucking.webapp.security.dto.RefreshDto;
import com.itechart.cargotrucking.webapp.security.exception.ExpiredRefreshTokenException;
import com.itechart.cargotrucking.webapp.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.stream.Collectors;

@Service
@Validated
public class AccessServiceImpl implements AccessService {
    private JwtProvider jwtProvider;
    private AuthenticationManager authenticationManager;
    private UserService userService;
    private RefreshTokenService refreshTokenService;

    @Autowired
    public AccessServiceImpl(
            JwtProvider jwtProvider,
            AuthenticationManager authenticationManager,
            UserService userService,
            RefreshTokenService refreshTokenService
    ) {
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public String signin(LoginDto loginDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getLogin(),
                    loginDto.getPassword())
            );
            User user = userService.findByLogin(loginDto.getLogin());

            jwtProvider.validateClientExist(user);

            String accessToken = jwtProvider.createAccessToken(loginDto.getLogin(), user.getUserRoles().stream()
                    .map(userRole -> Role.valueOf(userRole.getId().getRole().name()))
                    .collect(Collectors.toSet())
            );
            String refreshToken = jwtProvider.createRefreshToken(user.getId());
            if (refreshTokenService.find(new RefreshTokenKey(user.getId(), loginDto.getIp())) == null) {
                refreshTokenService.add(new RefreshToken(user.getId(), loginDto.getIp(), refreshToken));
            } else {
                refreshTokenService.update(new RefreshToken(user.getId(), loginDto.getIp(), refreshToken));
            }

            return accessToken + ' ' +  refreshToken;
        } catch (BadCredentialsException e) {
            throw new UserBadCredentialsException(e, "Incorrect user password with login %s", loginDto.getLogin());
        }
    }

    @Override
    public void logout(LogoutDto logoutDto) {
        refreshTokenService.delete(new RefreshTokenKey(logoutDto.getUserId(), logoutDto.getIp()));
    }

    @Override
    public String refreshAccessToken(RefreshDto refreshDto) {
        RefreshTokenKey id = new RefreshTokenKey();
        id.setUserId(refreshDto.getUserId());
        id.setIp(refreshDto.getIp());

        RefreshToken tokenFromDB = refreshTokenService.find(id);

        if (tokenFromDB != null && jwtProvider.validateToken(tokenFromDB.getToken())) {
            String refreshToken = jwtProvider.createRefreshToken(refreshDto.getUserId());

            tokenFromDB.setToken(refreshDto.getToken());
            refreshTokenService.update(tokenFromDB);

            User user = userService.findById(refreshDto.getUserId());

            jwtProvider.validateClientExist(user);

            String accessToken = jwtProvider.createAccessToken(user.getLogin(), user.getUserRoles().stream()
                    .map(userRole -> Role.valueOf(userRole.getId().getRole().name()))
                    .collect(Collectors.toSet())
            );
            return accessToken + ' ' +  refreshToken;
        }

        throw new ExpiredRefreshTokenException();
    }
}
