package com.elderly.apartment.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ScheduleWebSocketHandler extends TextWebSocketHandler {

    // 存储用户ID和WebSocket会话的映射
    private static final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.put(userId, session);
            log.info("用户 {} 已连接WebSocket，当前在线用户数: {}", userId, userSessions.size());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.remove(userId);
            log.info("用户 {} 已断开WebSocket，当前在线用户数: {}", userId, userSessions.size());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 处理客户端发送的消息（如心跳包）
        String payload = message.getPayload();
        log.debug("收到WebSocket消息: {}", payload);
    }

    /**
     * 发送日程提醒给指定用户
     */
    public void sendScheduleReminder(Long userId, ScheduleReminderMessage message) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String jsonMessage = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(jsonMessage));
                log.info("已发送日程提醒给用户 {}: {}", userId, message.getTitle());
            } catch (IOException e) {
                log.error("发送WebSocket消息失败", e);
            }
        } else {
            log.warn("用户 {} 不在线，无法发送提醒", userId);
        }
    }

    /**
     * 广播消息给所有在线用户
     */
    public void broadcastReminder(ScheduleReminderMessage message) {
        String jsonMessage;
        try {
            jsonMessage = objectMapper.writeValueAsString(message);
        } catch (IOException e) {
            log.error("序列化消息失败", e);
            return;
        }

        userSessions.forEach((userId, session) -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(jsonMessage));
                } catch (IOException e) {
                    log.error("发送消息给用户 {} 失败", userId, e);
                }
            }
        });
    }

    /**
     * 从会话中获取用户ID
     */
    private Long getUserIdFromSession(WebSocketSession session) {
        // 从URL参数中获取用户ID，例如: /ws/schedule?userId=123
        String query = session.getUri().getQuery();
        if (query != null && query.contains("userId=")) {
            String userIdStr = query.replaceAll(".*userId=(\\d+).*", "$1");
            try {
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                log.error("解析用户ID失败: {}", userIdStr);
            }
        }
        return null;
    }

    /**
     * 获取在线用户数量
     */
    public int getOnlineUserCount() {
        return userSessions.size();
    }
}
