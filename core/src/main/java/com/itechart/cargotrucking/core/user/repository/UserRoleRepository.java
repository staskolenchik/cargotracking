package com.itechart.cargotrucking.core.user.repository;

import com.itechart.cargotrucking.core.user.UserRole;
import com.itechart.cargotrucking.core.user.UserRoleKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleKey> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserRole SET cancelDate = now() WHERE id.userId = ?1 AND cancelDate is null")
    void delete(long id);

    boolean existsByIdAndCancelDateIsNull(UserRoleKey id);
}
