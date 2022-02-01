package com.itechart.cargotrucking.core.user.repository;

import com.itechart.cargotrucking.core.common.jpainterface.QuerydslRepository;
import com.itechart.cargotrucking.core.user.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface UserRepository extends QuerydslRepository<User, Long> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE User SET deleteDate = now() WHERE id = ?1 AND deleteDate is null")
    void delete(long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE User SET password = ?2 WHERE id = ?1 AND deleteDate is null")
    void updatePassword(long id, String password);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE User SET email = ?2 WHERE id = ?1 AND deleteDate is null")
    void updateEmail(long id, String email);

    @Query("SELECT u.clientId FROM User u WHERE u.id = ?1 AND u.deleteDate is null")
    Long getClientId(long id);

    User findByLogin(String login);

    User findByEmail(String email);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    List<User> findAllByBornDateEqualsAndDeleteDateIsNull(LocalDate localDate);

    boolean existsByIdAndDeleteDateIsNull(long id);
}
