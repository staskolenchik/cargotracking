package com.itechart.cargotrucking.core.security.repository;

import com.itechart.cargotrucking.core.security.RefreshToken;
import com.itechart.cargotrucking.core.security.RefreshTokenKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, RefreshTokenKey> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE RefreshToken SET token = null WHERE id = ?1 AND token is not null")
    void delete(RefreshTokenKey id);

    boolean existsByIdUserId(long userId);
}
