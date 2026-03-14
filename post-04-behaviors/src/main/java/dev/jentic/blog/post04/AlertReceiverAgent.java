package dev.jentic.blog.post04;

import dev.jentic.core.Message;
import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticMessageHandler;
import dev.jentic.runtime.agent.BaseAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Companion agent for the CyclicBehavior example.
 *
 * <p>Listens for alerts published by {@link HealthMonitorAgent} on "system.alert"
 * and logs each one with its severity header.
 *
 * <p>Post: {bit Autonomi} #04 — Behaviors: the heartbeat of agents
 * Blog: https://bitautonomi.substack.com
 */
@JenticAgent(value = "alert-receiver", autoStart = true)
public class AlertReceiverAgent extends BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(AlertReceiverAgent.class);

    public AlertReceiverAgent() {
        super("alert-receiver", "Alert Receiver");
    }

    @Override
    protected void onStart() {
        log.info("Alert Receiver started — listening on system.alert");
    }

    @JenticMessageHandler("system.alert")
    public void onAlert(Message message) {
        String severity = message.headers().getOrDefault("severity", "UNKNOWN");
        log.warn("[{}] {}", severity, message.getContent(String.class));
    }
}
