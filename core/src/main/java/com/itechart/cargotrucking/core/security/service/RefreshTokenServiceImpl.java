package com.itechart.cargotrucking.core.security.service;

import com.itechart.cargotrucking.core.security.RefreshToken;
import com.itechart.cargotrucking.core.security.RefreshTokenKey;
import com.itechart.cargotrucking.core.security.exception.DuplicatedRefreshTokenIdException;
import com.itechart.cargotrucking.core.security.exception.InvalidRefreshTokenException;
import com.itechart.cargotrucking.core.security.exception.RefreshTokenNotFoundException;
import com.itechart.cargotrucking.core.security.repository.RefreshTokenRepository;
import com.itechart.cargotrucking.core.user.exception.UserNotFoundException;
import com.itechart.cargotrucking.core.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class RefreshTokenServiceImpl implements RefreshTokenService{
    private UserRepository userRepository;
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenServiceImpl(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void add(RefreshToken refreshToken) {
        validateAdd(refreshToken);

        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void update(RefreshToken refreshToken) {
        validateUpdate(refreshToken);

        RefreshToken token = refreshTokenRepository.getOne(refreshToken.getId());
        token.setToken(refreshToken.getToken());
    }

    @Override
    public void delete(RefreshTokenKey... ids) {
        for(RefreshTokenKey id : ids){
            validateDelete(id);
            refreshTokenRepository.delete(id);
        }
    }

    @Override
    public RefreshToken find(RefreshTokenKey id) {
        if (refreshTokenRepository.existsById(id)) {
            return refreshTokenRepository.getOne(id);
        } else {
            return null;
        }
    }

    private void validateAdd(RefreshToken refreshToken) {
        if (!userRepository.existsByIdAndDeleteDateIsNull(refreshToken.getId().getUserId())) {
            throw new UserNotFoundException("User with id %s does not exist", refreshToken.getId().getUserId());
        }

        String[] blocks = refreshToken.getToken().split("\\.");
        if (blocks.length != 3) {
            throw new InvalidRefreshTokenException();
        }

        if (refreshTokenRepository.existsById(refreshToken.getId())) {
            throw new DuplicatedRefreshTokenIdException(
                    "Refresh token with user id %s and ip %s already exists",
                    refreshToken.getId().getUserId(),
                    refreshToken.getId().getIp()
            );
        }
    }

    private void validateUpdate(RefreshToken refreshToken) {
        if (!refreshTokenRepository.existsByIdUserId(refreshToken.getId().getUserId())) {
            throw new RefreshTokenNotFoundException(
                    "Refresh token with user id %s doesn't exists",
                    refreshToken.getId().getUserId()
            );
        }

        String[] blocks = refreshToken.getToken().split("\\.");
        if (blocks.length != 3) {
            throw new InvalidRefreshTokenException();
        }
    }

    private void validateDelete(RefreshTokenKey id) {
        if (!refreshTokenRepository.existsById(id)) {
            throw new RefreshTokenNotFoundException(
                    "Refresh token with user id %s and ip %s doesn't exists",
                    id.getUserId(),
                    id.getIp()
            );
        }
    }
}
