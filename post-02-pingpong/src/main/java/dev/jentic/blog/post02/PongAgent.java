package dev.jentic.blog.post02;

import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticMessageHandler;
import dev.jentic.runtime.agent.BaseAgent;
import dev.jentic.core.Message;

// PongAgent: listens for "ping" messages and replies with "pong"
@JenticAgent("pong-agent")
public class PongAgent extends BaseAgent {

    @JenticMessageHandler("ping")
    public void onPing(Message message) {
        log.info("[PONG] Received: {} — replying with: pong", message.content());
        messageService.send(Message.builder()
                .topic("pong")
                .content("pong")
                .build());
    }
}
