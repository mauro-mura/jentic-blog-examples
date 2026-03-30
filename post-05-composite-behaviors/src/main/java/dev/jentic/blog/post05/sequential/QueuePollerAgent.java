package dev.jentic.blog.post05.sequential;

import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.runtime.agent.BaseAgent;
import dev.jentic.runtime.behavior.OneShotBehavior;
import dev.jentic.runtime.behavior.composite.SequentialBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Queue poller agent: round-robin across three queues (repeating sequence).
 */
@JenticAgent("queue-poller-agent")
public class QueuePollerAgent extends BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(QueuePollerAgent.class);

    @Override
    protected void onStart() {
        // Round-robin across three queues: one step per scheduler tick, then wraps around
        SequentialBehavior roundRobin = new SequentialBehavior("queue-poller", Duration.ofSeconds(1));
        roundRobin.addChildBehavior(OneShotBehavior.from("poll-north",   this::pollNorth));
        roundRobin.addChildBehavior(OneShotBehavior.from("poll-central", this::pollCentral));
        roundRobin.addChildBehavior(OneShotBehavior.from("poll-south",   this::pollSouth));

        addBehavior(roundRobin);
    }

    private void pollNorth() {
        log.info("[{}] Polling queue NORTH", getAgentId());
        sleep(100);
    }

    private void pollCentral() {
        log.info("[{}] Polling queue CENTRAL", getAgentId());
        sleep(100);
    }

    private void pollSouth() {
        log.info("[{}] Polling queue SOUTH", getAgentId());
        sleep(100);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}