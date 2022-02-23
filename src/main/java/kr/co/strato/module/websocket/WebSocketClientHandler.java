package kr.co.strato.module.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class WebSocketClientHandler extends TextWebSocketHandler {
    private static Logger logger = LoggerFactory.getLogger(WebSocketClientHandler.class);

    private CountDownLatch latch;
    private MessageReceiver receiver;


    public WebSocketClientHandler(CountDownLatch latch, MessageReceiver receiver) {
        this.latch = latch;
        this.receiver = receiver;
    }

    @Override
    public void afterConnectionEstablished(org.springframework.web.socket.WebSocketSession session) throws Exception {
        // 클라이언트에서 접속을 하여 성공할 경우 발생하는 이벤트
        logger.info("Session connect success. Session ID: {}", session.getId());

    }


    @Override
    protected void handleTextMessage(org.springframework.web.socket.WebSocketSession session, TextMessage message) throws Exception {
        //클라이언트에서 send를 이용해서 메시지 발송을 한 경우 이벤트 핸들링
        String msg = message.getPayload();
        logger.info("Received message: {}", msg);
        receiver.receive(msg);
    }

    @Override
    public void handleTransportError(org.springframework.web.socket.WebSocketSession session, Throwable exception) {
        //커넥션 에러 시 호출
        if (session.isOpen()) {
            logger.info("session [{}] is open, need close", session.getId());
            try {
                session.close();
            } catch (IOException e) {
                logger.error("", e);
            }
        }
        latch.countDown();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        //커넥션 클로즈시 호출
        logger.info("session [{}] is open, need close", session.getId());
        latch.countDown();
    }
}