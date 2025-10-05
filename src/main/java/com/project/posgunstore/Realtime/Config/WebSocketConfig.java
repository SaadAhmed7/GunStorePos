// src/main/java/com/project/posgunstore/Realtime/Config/WebSocketConfig.java
package com.project.posgunstore.Realtime.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");      // clients subscribe to /topic/inventory
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/api/realtime/inventory")
            .setAllowedOriginPatterns("*");   // adjust for prod
    registry.addEndpoint("/api/realtime/inventory")
            .setAllowedOriginPatterns("*")
            .withSockJS();
  }
}
