package com.itechart.cargotrucking.core.scheduler.service;

import com.itechart.cargotrucking.core.user.User;

public interface TemplateService {
    String getMessage(User user);

    void saveTemplate(String template, long clientId);
}
