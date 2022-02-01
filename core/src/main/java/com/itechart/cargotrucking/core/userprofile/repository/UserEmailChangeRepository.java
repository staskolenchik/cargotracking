package com.itechart.cargotrucking.core.userprofile.repository;

import com.itechart.cargotrucking.core.userprofile.UserEmailChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserEmailChangeRepository extends JpaRepository<UserEmailChange, Long> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserEmailChange SET uid = null WHERE user_id = ?1")
    void delete(long userId);

    @Query("SELECT CASE WHEN (count(u) > 0) THEN TRUE ELSE FALSE END FROM UserEmailChange u WHERE u.userId = ?1 AND u.uid = ?2 AND u.expDate > now()")
    boolean existsByUid(long userId, String uuid);

    UserEmailChange findByUid(String uuid);
}
