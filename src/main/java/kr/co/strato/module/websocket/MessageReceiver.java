package kr.co.strato.module.websocket;

import java.io.IOException;

public interface MessageReceiver {
    public void receive(String message) throws IOException;
}
