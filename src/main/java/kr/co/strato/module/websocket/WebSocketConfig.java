package kr.co.strato.module.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private WebSocketMessageHandler execHandler;

    @Autowired
    private WebSocketMessageHandler logHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(execHandler, "/ws/pod/exec/*/*/*").addInterceptors(auctionInterceptor()).setAllowedOrigins("*");
        registry.addHandler(logHandler, "/ws/pod/log/*/*/*").addInterceptors(auctionInterceptor()).setAllowedOrigins("*");
        registry.addHandler(logHandler, "/ws/cluster/log/*/*").addInterceptors(auctionInterceptor()).setAllowedOrigins("*");
    }

    @Bean
    public HandshakeInterceptor auctionInterceptor() {
        return new WebSocketInterceptor();
    }
}
