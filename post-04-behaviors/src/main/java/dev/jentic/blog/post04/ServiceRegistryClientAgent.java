package dev.jentic.blog.post04;

import dev.jentic.core.Message;
import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticBehavior;
import dev.jentic.core.annotations.JenticMessageHandler;
import dev.jentic.runtime.agent.BaseAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.jentic.core.BehaviorType.ONE_SHOT;

/**
 * OneShotBehavior example.
 *
 * <p>At startup, {@link ServiceRegistryClientAgent} broadcasts its own presence once
 * on "agent.started". The companion {@link RegistryAgent} listens and confirms receipt.
 *
 * <p>Post: {bit Autonomi} #04 — Behaviors: the heartbeat of agents
 * Blog: https://bitautonomi.substack.com
 */
@JenticAgent(value = "service-registry-client", autoStart = true)
public class ServiceRegistryClientAgent extends BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(ServiceRegistryClientAgent.class);

    public ServiceRegistryClientAgent() {
        super("service-registry-client", "Service Registry Client");
    }

    @Override
    protected void onStart() {
        log.info("Service Registry Client started");
    }

    // Executed exactly once, right after onStart() completes.
    @JenticBehavior(type = ONE_SHOT)
    public void registerSelf() {
        log.info("Registering agent {} on the network...", getAgentId());

        messageService.send(Message.builder()
                .topic("agent.started")
                .content(getAgentId())
                .build());
    }

    // ------------------------------------------------------------------
    // Companion: confirms that the registration message was received
    // ------------------------------------------------------------------

    @JenticAgent(value = "registry", autoStart = true)
    public static class RegistryAgent extends BaseAgent {

        private static final Logger log = LoggerFactory.getLogger(RegistryAgent.class);

        public RegistryAgent() {
            super("registry", "Registry");
        }

        @JenticMessageHandler("agent.started")
        public void onAgentStarted(Message message) {
            String agentId = message.getContent(String.class);
            log.info("Registry: agent '{}' registered successfully", agentId);
        }
    }
}
