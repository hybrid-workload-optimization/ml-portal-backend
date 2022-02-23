package kr.co.strato.module.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WebSocketMessageBroker extends Thread implements MessageReceiver {
    //Kubernetes I/F쪽 세선
    private WebSocketSession serverSession;

    //Browser 세션
    private WebSocketSession clientSession;
    private String url;
    private boolean isConnected = false;


    public WebSocketMessageBroker(WebSocketSession clientSession, String url) {
        this.url = url;
        this.clientSession = clientSession;
    }

    @Override
    public void run() {
        CountDownLatch waitForEndOfMessage = new CountDownLatch(1);
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketClientHandler handler = new WebSocketClientHandler(waitForEndOfMessage, this);
        try {
            serverSession = webSocketClient.doHandshake(handler, url).get(10, TimeUnit.SECONDS);
            setConnected(serverSession != null && serverSession.isOpen());
            waitForEndOfMessage.await();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

    }

    /**
     * 메세지 전달
     * @param message
     * @throws IOException
     */
    public void send(String message) throws IOException {
        serverSession.sendMessage(new TextMessage(message));
    }

    /**
     * 연결 종료
     * @throws IOException
     */
    public void disconnect() throws IOException {
        if(serverSession != null) {
            serverSession.close();
        }
    }

    /**
     * 연결 유무 반환.
     * @return
     */
    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    @Override
    public void receive(String message) throws IOException {
        clientSession.sendMessage(new TextMessage(message));
    }
}