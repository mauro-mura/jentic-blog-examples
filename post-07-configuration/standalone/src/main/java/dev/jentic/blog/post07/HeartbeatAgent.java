package dev.jentic.blog.post07;

import dev.jentic.core.Message;
import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticBehavior;
import dev.jentic.core.annotations.JenticMessageHandler;
import dev.jentic.runtime.agent.BaseAgent;

import static dev.jentic.core.BehaviorType.CYCLIC;

/**
 * Publishes a heartbeat every 5 seconds and logs acknowledgments from other agents.
 * The same class is used unchanged in both the standalone and Spring Boot modules,
 * demonstrating that post07 is independent from agent code.
 */
@JenticAgent("heartbeat-agent")
public class HeartbeatAgent extends BaseAgent {

    @JenticBehavior(type = CYCLIC, interval = "5s")
    public void sendHeartbeat() {
        messageService.send(Message.builder()
                .topic("heartbeat")
                .content("ping from " + getAgentId())
                .build());
        log.info("Heartbeat sent");
    }

    @JenticMessageHandler("heartbeat.ack")
    public void handleAck(Message message) {
        log.info("Acknowledgment received: {}", message.content());
    }
}
