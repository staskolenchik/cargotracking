package com.itechart.cargotrucking.webapp.user;

import com.itechart.cargotrucking.core.user.dto.UserAddDto;
import com.itechart.cargotrucking.core.user.dto.UserFilterDto;
import com.itechart.cargotrucking.core.user.dto.UserInfoDto;
import com.itechart.cargotrucking.core.user.dto.UserUpdateDto;
import com.itechart.cargotrucking.core.user.exception.UserBadCredentialsException;
import com.itechart.cargotrucking.core.user.service.UserService;
import com.itechart.cargotrucking.webapp.security.UserCredentials;
import com.itechart.cargotrucking.webapp.security.exception.AccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserService userService;
    private static PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<String> addUser(@Valid @RequestBody UserAddDto user, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();
        user.setClientId(userCredentials.getClientId());

        validatePassword(user.getPassword(), user.getPasswordConfirm());
        String encryptPassword = passwordEncryption(user.getPassword());
        user.setPassword(encryptPassword);
        user.setPasswordConfirm(encryptPassword);

        long id = userService.add(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable long id, @Valid @RequestBody UserUpdateDto user, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();
        Long clientId = userService.getClientId(id);

        if (!userCredentials.getClientId().equals(clientId)) {
            throw new AccessException("Authenticated user haven't privileges to update users of company with id %s", clientId);
        }

        if (user.getIsChangePassword()) {
            validatePassword(user.getPassword(), user.getPasswordConfirm());
            String encryptPassword = passwordEncryption(user.getPassword());
            user.setPassword(encryptPassword);
            user.setPasswordConfirm(encryptPassword);
        }
        userService.update(id, user);
    }


    @DeleteMapping
    public void deleteUser(Authentication authentication, @RequestBody long... ids) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();
        for (long id : ids) {
            Long clientId = userService.getClientId(id);

            if (!userCredentials.getClientId().equals(clientId)) {
                throw new AccessException("Authenticated user haven't privileges to delete users of company with id %s", clientId);
            }
        }

        userService.delete(ids);
    }

    @GetMapping
    public Page<UserInfoDto> userListByFilters(
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.ASC) Pageable pageable,
            UserFilterDto filter,
            Authentication authentication
    ) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        return userService.find(userCredentials.getUserId(), filter, pageable);
    }

    @GetMapping("/{id}")
    public UserInfoDto userById(@PathVariable long id, Authentication authentication) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();
        Long clientId = userService.getClientId(id);

        if (!userCredentials.getClientId().equals(clientId)) {
            throw new AccessException("Authenticated user haven't privileges to see users of company with id %s", clientId);
        }

        return userService.findInfoById(id);
    }

    private void validatePassword(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new UserBadCredentialsException("User passwords does not match. Password = %s. PasswordConfirm = %s",
                    password, passwordConfirm);
        }
    }
    public static String passwordEncryption(String password){
        return passwordEncoder.encode(password);
    }
}
