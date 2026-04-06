package dev.jentic.blog.post07;

import dev.jentic.core.Message;
import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticMessageHandler;
import dev.jentic.runtime.agent.BaseAgent;

/**
 * Listens for heartbeat messages and replies with an acknowledgment.
 * Kept minimal to keep the focus on post07 rather than behavior logic.
 */
@JenticAgent("monitor-agent")
public class MonitorAgent extends BaseAgent {

    @JenticMessageHandler("heartbeat")
    public void handleHeartbeat(Message message) {
        log.info("Heartbeat received from {}", message.content());
        messageService.send(Message.builder()
                .topic("heartbeat.ack")
                .content("ack from " + getAgentId())
                .build());
    }
}
