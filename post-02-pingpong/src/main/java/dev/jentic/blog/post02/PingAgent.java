package dev.jentic.blog.post02;

import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticBehavior;
import dev.jentic.core.annotations.JenticMessageHandler;
import dev.jentic.runtime.agent.BaseAgent;
import dev.jentic.core.Message;

import static dev.jentic.core.BehaviorType.ONE_SHOT;

// PingAgent: sends a "ping" message on startup, then listens for the reply
@JenticAgent("ping-agent")
public class PingAgent extends BaseAgent {

    @JenticBehavior(type = ONE_SHOT)
    public void sendPing() {
        log.info("[PING] Sending: ping");
        messageService.send(Message.builder()
                .topic("ping")
                .content("ping")
                .build());
    }

    @JenticMessageHandler("pong")
    public void onPong(Message message) {
        log.info("[PING] Received: {}", message.content());
    }
}
