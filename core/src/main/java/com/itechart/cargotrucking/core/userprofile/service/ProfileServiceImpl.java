package com.itechart.cargotrucking.core.userprofile.service;

import com.itechart.cargotrucking.core.user.exception.DuplicatedEmailException;
import com.itechart.cargotrucking.core.user.service.UserService;
import com.itechart.cargotrucking.core.userprofile.UserEmailChange;
import com.itechart.cargotrucking.core.userprofile.exception.UUIDNotFoundException;
import com.itechart.cargotrucking.core.userprofile.repository.UserEmailChangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ProfileServiceImpl implements ProfileService {
    private UserService userService;
    private UserEmailChangeRepository userEmailChangeRepository;

    @Autowired
    public ProfileServiceImpl(UserService userService, UserEmailChangeRepository userEmailChangeRepository) {
        this.userService = userService;
        this.userEmailChangeRepository = userEmailChangeRepository;
    }

    @Override
    public void changePassword(long userId, String password) {
        userService.updateUserPassword(userId, password);
    }

    @Override
    public void changeEmail(long userId, String uuid) {
        validateUUID(userId, uuid);

        UserEmailChange userEmailChange = userEmailChangeRepository.findByUid(uuid);
        if (userService.existsByEmail(userEmailChange.getEmail())) {
            throw new DuplicatedEmailException("Email %s already used", userEmailChange.getEmail());
        }
        userService.updateUserEmail(userId, userEmailChange.getEmail());
    }

    private void validateUUID(long userId, String uuid) {
        if (!userEmailChangeRepository.existsByUid(userId, uuid)) {
            throw new UUIDNotFoundException();
        }
    }
}
