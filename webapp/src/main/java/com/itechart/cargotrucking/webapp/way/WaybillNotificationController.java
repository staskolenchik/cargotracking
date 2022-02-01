package com.itechart.cargotrucking.webapp.way;

import com.itechart.cargotrucking.webapp.security.UserCredentials;
import com.itechart.cargotrucking.webapp.security.exception.AccessException;
import com.itechart.cargotrucking.webapp.way.dto.WaybillNotificationDto;
import com.itechart.cargotrucking.webapp.way.service.WaybillNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
public class WaybillNotificationController {
    private WaybillNotificationService waybillNotificationService;

    @Autowired
    public WaybillNotificationController(WaybillNotificationService waybillNotificationService) {
        this.waybillNotificationService = waybillNotificationService;
    }

    @MessageMapping("/checkpoint-reached/{clientId}")
    @SendTo("/api/waybill-notifications/{clientId}")
    public String notify(
            @DestinationVariable long clientId,
            WaybillNotificationDto notification,
            Authentication authentication
    ) {
        UserCredentials userCredentials = (UserCredentials) authentication.getCredentials();

        if (userCredentials.getClientId() != clientId) {
            throw new AccessException("You haven't permissions to use channel with id %s", clientId);
        }

        return waybillNotificationService.createNotification(userCredentials.getUserId(), notification);
    }

    @MessageExceptionHandler
    public String handleAccessException(AccessException e) {
        return e.getMessage();
    }
}
