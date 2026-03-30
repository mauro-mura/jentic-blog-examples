package dev.jentic.blog.post05.fsm;

import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.runtime.agent.BaseAgent;
import dev.jentic.runtime.behavior.CyclicBehavior;
import dev.jentic.runtime.behavior.OneShotBehavior;
import dev.jentic.runtime.behavior.composite.FSMBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Order lifecycle agent: FSM cycling through IDLE → PROCESSING → DONE → IDLE.
 * Processes three simulated orders then keeps waiting.
 *
 * FSMBehavior is on-demand: a CyclicBehavior driver ticks it at regular intervals.
 */
@JenticAgent("order-lifecycle-agent")
public class OrderLifecycleAgent extends BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(OrderLifecycleAgent.class);

    private final AtomicInteger orderCount = new AtomicInteger(0);
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private final AtomicBoolean done       = new AtomicBoolean(false);
    private final AtomicInteger cycleCount = new AtomicInteger(0);

    @Override
    protected void onStart() {
        FSMBehavior orderFsm = FSMBehavior.builder("order-lifecycle", "IDLE")
                .state("IDLE",       OneShotBehavior.from("wait-order", this::waitForOrder))
                .state("PROCESSING", OneShotBehavior.from("process",    this::processOrder))
                .state("DONE",       OneShotBehavior.from("notify",     this::notifyCompletion))
                .transition("IDLE",       "PROCESSING", fsm -> processing.get())
                .transition("PROCESSING", "DONE",       fsm -> done.get())
                .transition("DONE",       "IDLE",       fsm -> true)
                .build();

        // FSMBehavior is on-demand: a CyclicBehavior driver ticks it at regular intervals
        CyclicBehavior driver = CyclicBehavior.from(
                "order-fsm-driver",
                Duration.ofMillis(300),
                () -> orderFsm.execute());

        addBehavior(driver);
    }

    private void waitForOrder() {
        if (orderCount.get() >= 3) {
            log.info("[{}] [IDLE] No more orders in queue. Waiting...", getAgentId());
            sleep(2000);
            return;
        }
        log.info("[{}] [IDLE] Checking order queue...", getAgentId());
        sleep(500);
        orderCount.incrementAndGet();
        processing.set(true);
        done.set(false);
        log.info("[{}] [IDLE] New order #{} received", getAgentId(), orderCount.get());
    }

    private void processOrder() {
        log.info("[{}] [PROCESSING] Processing order #{}...", getAgentId(), orderCount.get());
        sleep(1000);
        processing.set(false);
        done.set(true);
        log.info("[{}] [PROCESSING] Order #{} processed", getAgentId(), orderCount.get());
    }

    private void notifyCompletion() {
        log.info("[{}] [DONE] Customer notified for order #{}. Cycle #{} complete.",
                getAgentId(), orderCount.get(), cycleCount.incrementAndGet());
        sleep(300);
        done.set(false);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}