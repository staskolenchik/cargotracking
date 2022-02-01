package com.itechart.cargotrucking.webapp.user.profile;

import com.itechart.cargotrucking.core.email.UserEmailChangeMessage;
import com.itechart.cargotrucking.core.email.service.EmailService;
import com.itechart.cargotrucking.core.user.User;
import com.itechart.cargotrucking.core.user.dto.UserInfoDto;
import com.itechart.cargotrucking.core.user.dto.UserPersonDataUpdateDto;
import com.itechart.cargotrucking.core.user.service.UserService;
import com.itechart.cargotrucking.core.userprofile.dto.PasswordChangeDto;
import com.itechart.cargotrucking.core.userprofile.service.ProfileService;
import com.itechart.cargotrucking.webapp.security.UserCredentials;
import com.itechart.cargotrucking.webapp.user.exception.PasswordsNotEqualsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private UserService userService;
    private ProfileService profileService;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;

    @Autowired
    public ProfileController(
            UserService userService,
            ProfileService profileService,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.userService = userService;
        this.profileService = profileService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @GetMapping
    public UserInfoDto getUserInfo(Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        return userService.findInfoById(userCredentials.getUserId());
    }

    @PutMapping("/change-email")
    public void changeEmail(@RequestBody @Valid UserEmailChangeMessage message, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        validatePassword(userCredentials.getUserId(), message.getPassword());
        emailService.sendChangeEmail(userCredentials.getUserId(), message);
    }

    @PutMapping("/change-password")
    public void changePassword(@RequestBody @Valid PasswordChangeDto passwordChangeDto, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        validatePassword(userCredentials.getUserId(), passwordChangeDto.getOldPassword());
        profileService.changePassword(userCredentials.getUserId(), passwordEncoder.encode(passwordChangeDto.getNewPassword()));
    }

    @GetMapping("/confirm-change-email/{uuid}")
    public void confirmChangeEmail(@PathVariable String uuid, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        profileService.changeEmail(userCredentials.getUserId(), uuid);
    }

    @PutMapping
    public void updateUserPersonData(
            @Valid @RequestBody UserPersonDataUpdateDto userDto,
            Authentication authentication
    ) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        userService.updatePersonData(userCredentials.getUserId(), userDto);
    }

    private void validatePassword(long userId, String password) {
        User user = userService.findById(userId);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordsNotEqualsException();
        }
    }
}
