package dev.jentic.blog.post04;

import dev.jentic.core.Message;
import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticBehavior;
import dev.jentic.core.annotations.JenticMessageHandler;
import dev.jentic.runtime.agent.BaseAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.jentic.core.BehaviorType.CYCLIC;

/**
 * CyclicBehavior example.
 *
 * <p>A health-monitor agent that checks a downstream service every 3 seconds
 * and publishes an alert when the check fails. A companion {@link AlertReceiverAgent}
 * logs every alert it receives.
 *
 * <p>Post: {bit Autonomi} #04 — Behaviors: the heartbeat of agents
 * Blog: https://bitautonomi.substack.com
 */
@JenticAgent(value = "health-monitor", autoStart = true)
public class HealthMonitorAgent extends BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(HealthMonitorAgent.class);

    private final SimulatedDownstreamService downstreamService = new SimulatedDownstreamService();
    private int checkCount = 0;

    public HealthMonitorAgent() {
        super("health-monitor", "Health Monitor");
    }

    @Override
    protected void onStart() {
        log.info("Health Monitor started — checking downstream service every 3 seconds");
    }

    @JenticBehavior(type = CYCLIC, interval = "3s")
    public void checkHealth() {
        checkCount++;
        boolean healthy = downstreamService.ping();

        if (!healthy) {
            log.warn("Check #{}: downstream service not responding — sending alert", checkCount);
            messageService.send(Message.builder()
                    .topic("system.alert")
                    .header("severity", "WARNING")
                    .content("Downstream service not responding (check #" + checkCount + ")")
                    .build());
        } else {
            log.info("Check #{}: downstream service OK", checkCount);
        }
    }

    // ------------------------------------------------------------------
    // Simulated dependency — reports unhealthy on every 4th call
    // ------------------------------------------------------------------

    static class SimulatedDownstreamService {
        private int callCount = 0;

        boolean ping() {
            callCount++;
            return callCount % 4 != 0;
        }
    }
}
