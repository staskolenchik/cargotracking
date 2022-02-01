package com.itechart.cargotrucking.webapp.security.service;

import com.itechart.cargotrucking.core.user.User;
import com.itechart.cargotrucking.core.user.exception.UserNotFoundException;
import com.itechart.cargotrucking.core.user.service.UserService;
import com.itechart.cargotrucking.webapp.security.UserDetailsImpl;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByLogin(username);

        if (user == null) {
            throw new UserNotFoundException("User with login %s doesn't exist", username);
        }
        
        Hibernate.initialize(user.getUserRoles());

        return new UserDetailsImpl(user);
    }
}
