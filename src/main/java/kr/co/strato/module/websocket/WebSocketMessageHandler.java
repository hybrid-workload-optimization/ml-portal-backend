package kr.co.strato.module.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketMessageHandler extends TextWebSocketHandler {
    private static Logger logger = LoggerFactory.getLogger(WebSocketMessageHandler.class);

    private Map<String, WebSocketMessageBroker> clientMap = new ConcurrentHashMap<>();

    @Value("${service.kubernetes-interface.wsUrl}")
    private String k8sUrl;

    @Value("${service.cloud-interface.wsUrl}")
    private String cloudUrl;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 클라이언트에서 접속을 하여 성공할 경우 발생하는 이벤트
        logger.info("Session connect success. Session ID: {}, Session Size: {}", session.getId(), clientMap.size());

        String sessionId = session.getId();
        if(!clientMap.containsKey(sessionId)) {
            String wsPath = (String)session.getAttributes().get("path");
            String path = wsPath.replace("/ws", "");
            if(path.startsWith("/")) {
                path = path.substring(1);
            }
            String serverUrl = getUrl(path);
            if(serverUrl == null) {
                logger.info("Unknown server type - URI: {}", path);
                session.close();
                return;
            }

            String url = String.format("%s%s", serverUrl, path);

            WebSocketMessageBroker client = new WebSocketMessageBroker(session, url);
            client.start();
            clientMap.put(sessionId, client);
        }
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //클라이언트에서 send를 이용해서 메시지 발송을 한 경우 이벤트 핸들링
        String command = message.getPayload();
        logger.info("Pod Exec - Session ID: {}", session.getId());
        logger.info("Pod Exec - Command: {}", command);

        WebSocketMessageBroker client = clientMap.get(session.getId());
        if(client != null) {
            client.send(command);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        //커넥션 에러 시 호출
        if (session.isOpen()) {
            logger.info("session [{}] is open, need close", session.getId());
            try {
                session.close();
            } catch (IOException e) {
                logger.error("", e);
            }
        }
        disconnect(session);
        clientMap.remove(session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        //커넥션 클로즈시 호출
        disconnect(session);
        clientMap.remove(session.getId());
    }

    private void disconnect(WebSocketSession session) {
        logger.info("Session [{}] is close", session.getId());
        WebSocketMessageBroker client = clientMap.get(session.getId());
        if(client != null) {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            };
        }
    }

    /**
     * 서비스 타입에 따라 I/F URL 반환
     * @param path
     * @return
     */
    private String getUrl(String path) {
        String[] arr = path.split("/");
        String type = arr[0];

        String url = null;
        if(type.equals("pod")) {
            url = k8sUrl;
        } else if(type.equals("cluster")) {
            url = cloudUrl;
        }
        return url;
    }

}
