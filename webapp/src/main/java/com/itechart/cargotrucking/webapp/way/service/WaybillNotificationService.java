package com.itechart.cargotrucking.webapp.way.service;

import com.itechart.cargotrucking.webapp.way.dto.WaybillNotificationDto;

public interface WaybillNotificationService {
    String createNotification(long userId, WaybillNotificationDto waybillNotification);
}
