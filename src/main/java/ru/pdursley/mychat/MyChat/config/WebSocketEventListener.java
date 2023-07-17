package ru.pdursley.mychat.MyChat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.pdursley.mychat.MyChat.chat.ChatMessage;
import ru.pdursley.mychat.MyChat.chat.MessageType;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    public final SimpMessageSendingOperations messageSendingOperations;

    @EventListener
    public void handleWebSocketDisconnectListener(
            SessionDisconnectEvent event
    ) // Информирование пользователей о том, что какой-то пользователь покинул чат
     {
         StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
         String username = (String) headerAccessor.getSessionAttributes().get("username");
         if (username != null) {
             log.info("User disconnected: {}", username);
             var chatMessage = ChatMessage.builder()
                     .type(MessageType.LEAVER)
                     .sender(username)
                     .build();
             messageSendingOperations.convertAndSend("/topic/public", chatMessage);
         }

    }
}
