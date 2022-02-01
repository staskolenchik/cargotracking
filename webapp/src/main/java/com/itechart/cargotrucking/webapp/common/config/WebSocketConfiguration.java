package com.itechart.cargotrucking.webapp.common.config;

import com.itechart.cargotrucking.webapp.security.Role;
import com.itechart.cargotrucking.webapp.security.exception.InvalidJwtException;
import com.itechart.cargotrucking.webapp.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Collection;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    private JwtProvider jwtProvider;

    @Autowired
    public WebSocketConfiguration(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/api");
        registry.setApplicationDestinationPrefixes("/api");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/web-socket")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @SuppressWarnings("NullableProblems")
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && !StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String header = accessor.getFirstNativeHeader("Authorization");
                    if (StringUtils.isEmpty(header)) {
                        return sendErrorMessage("Authorization header is empty");
                    }

                    String jwt = header.substring(7);
                    try {
                        jwtProvider.validateToken(jwt);
                    } catch (InvalidJwtException e) {
                        return sendErrorMessage("Invalid jwt");
                    }

                    Authentication authentication = jwtProvider.getAuthentication(jwt);

                    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                    if (StompCommand.SEND.equals(accessor.getCommand()) && !authorities.contains(Role.DRIVER)) {
                        return sendErrorMessage("You should have driver authority for sending message");
                    }

                    if (StompCommand.MESSAGE.equals(accessor.getCommand()) && !authorities.contains(Role.MANAGER)) {
                        return sendErrorMessage("You should have manager authority for getting message");
                    }

                    accessor.setUser(authentication);
                }

                return message;
            }

            private Message<?> sendErrorMessage(String message) {
                StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
                headerAccessor.setMessage(message);
                return MessageBuilder.createMessage(new byte[0], headerAccessor.getMessageHeaders());
            }
        });
    }
}
