package com.itechart.cargotrucking.core.userprofile.service;

public interface ProfileService {
    void changePassword(long userId, String password);

    void changeEmail(long userId, String uuid);
}
