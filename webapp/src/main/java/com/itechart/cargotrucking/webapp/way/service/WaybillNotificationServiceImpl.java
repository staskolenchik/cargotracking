package com.itechart.cargotrucking.webapp.way.service;

import com.itechart.cargotrucking.core.user.service.UserService;
import com.itechart.cargotrucking.webapp.way.dto.WaybillNotificationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;

@Service
public class WaybillNotificationServiceImpl implements WaybillNotificationService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private UserService userService;

    @Autowired
    public WaybillNotificationServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public String createNotification(long userId, WaybillNotificationDto waybillNotification) {
        String fullName = userService.getUserFullName(userId);

        String notification = waybillNotification.getAchieveTime() != null
                ? waybillNotification.getAchieveTime().format(formatter) + ": " : "";
        notification += fullName + " reached checkpoint";
        notification += StringUtils.isEmpty(waybillNotification.getCheckpointAddress())
                ? "" : ' ' + waybillNotification.getCheckpointAddress();

        return notification;
    }
}
