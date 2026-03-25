package com.elderly.apartment.websocket;

import com.elderly.apartment.security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

@Slf4j
@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        URI uri = request.getURI();
        String query = uri.getQuery();

        if (query != null && query.contains("token=")) {
            String token = extractTokenFromQuery(query);
            if (token != null && !token.isEmpty()) {
                try {
                    String username = jwtUtils.extractUsername(token);
                    if (username != null && jwtUtils.validateToken(token, username)) {
                        attributes.put("username", username);
                        log.info("WebSocket 认证成功，用户: {}", username);
                        return true;
                    }
                } catch (Exception e) {
                    log.warn("WebSocket token 验证失败: {}", e.getMessage());
                }
            }
        }

        log.warn("WebSocket 认证失败，拒绝连接");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }

    private String extractTokenFromQuery(String query) {
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2 && "token".equals(keyValue[0])) {
                return keyValue[1];
            }
        }
        return null;
    }
}
