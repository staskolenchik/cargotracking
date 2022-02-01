package com.itechart.cargotrucking.core.user.service;

import com.itechart.cargotrucking.core.user.User;
import com.itechart.cargotrucking.core.user.dto.UserAddDto;
import com.itechart.cargotrucking.core.user.dto.UserFilterDto;
import com.itechart.cargotrucking.core.user.dto.UserInfoDto;
import com.itechart.cargotrucking.core.user.dto.UserPersonDataUpdateDto;
import com.itechart.cargotrucking.core.user.dto.UserUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    @Transactional
    long add(UserAddDto user);

    @Transactional
    void update(long id, UserUpdateDto user);

    @Transactional
    void updatePersonData(long id, UserPersonDataUpdateDto user);

    @Transactional
    void updateUserPassword(long id, String password);

    @Transactional
    void updateUserEmail(long id, String email);

    @Transactional
    void delete(long... ids);

    Page<UserInfoDto> find(long userId, UserFilterDto filter, Pageable pageable);

    User findByLogin(String login);

    User findByEmail(String email);

    User findById(long id);

    UserInfoDto findInfoById(long id);

    Long getClientId(long id);

    String getUserFullName(long id);

    boolean existsById(long id);

    boolean existsByEmail(String email);

    boolean existsByIdAndDriverRole(long id);

    boolean existsByIdAndManagerRole(long id);

    boolean existsByIdAndDispatcherRole(long id);
}
