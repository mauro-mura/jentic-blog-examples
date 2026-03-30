package dev.jentic.blog.post05.combined;

import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.composite.CompletionStrategy;
import dev.jentic.runtime.agent.BaseAgent;
import dev.jentic.runtime.behavior.CyclicBehavior;
import dev.jentic.runtime.behavior.OneShotBehavior;
import dev.jentic.runtime.behavior.composite.FSMBehavior;
import dev.jentic.runtime.behavior.composite.ParallelBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Order agent: FSM with a nested ParallelBehavior in the VALIDATING state.
 *
 * Flow: WAITING → VALIDATING (parallel: stock + payment + fraud)
 *             → FULFILLING → COMPLETED → WAITING
 *
 * ParallelBehavior is scheduled natively via addBehavior().
 * FSMBehavior is on-demand: a CyclicBehavior driver ticks it at regular intervals.
 */
@JenticAgent("order-agent")
public class OrderAgent extends BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(OrderAgent.class);

    private final AtomicBoolean hasOrder        = new AtomicBoolean(false);
    private final AtomicBoolean validationOk    = new AtomicBoolean(false);
    private final AtomicBoolean validationFail  = new AtomicBoolean(false);
    private final AtomicBoolean fulfilled       = new AtomicBoolean(false);
    private final AtomicBoolean notified        = new AtomicBoolean(false);
    private final AtomicInteger validationCount = new AtomicInteger(0);
    private final AtomicInteger orderCounter    = new AtomicInteger(0);

    @Override
    protected void onStart() {
        // Parallel validators: all three must complete successfully
        ParallelBehavior validators = new ParallelBehavior(
                "order-validators", CompletionStrategy.ALL);
        validators.addChildBehavior(OneShotBehavior.from("stock-validation",   this::validateStock));
        validators.addChildBehavior(OneShotBehavior.from("payment-validation", this::validatePayment));
        validators.addChildBehavior(OneShotBehavior.from("fraud-check",        this::checkFraud));

        // The ParallelBehavior is assigned directly to the VALIDATING state
        FSMBehavior orderFsm = FSMBehavior.builder("order-fsm", "WAITING")
                .state("WAITING",    OneShotBehavior.from("wait-order", this::waitForOrder))
                .state("VALIDATING", validators)
                .state("FULFILLING", OneShotBehavior.from("fulfill",    this::fulfillOrder))
                .state("COMPLETED",  OneShotBehavior.from("notify",     this::notifyCustomer))
                .transition("WAITING",    "VALIDATING", fsm -> hasOrder.get())
                .transition("VALIDATING", "FULFILLING", fsm -> validationOk.get())
                .transition("VALIDATING", "WAITING",    fsm -> validationFail.get())
                .transition("FULFILLING", "COMPLETED",  fsm -> fulfilled.get())
                .transition("COMPLETED",  "WAITING",    fsm -> notified.get())
                .build();

        // FSMBehavior is on-demand: a CyclicBehavior driver ticks it at regular intervals
        CyclicBehavior driver = CyclicBehavior.from(
                "order-fsm-driver",
                Duration.ofMillis(300),
                () -> orderFsm.execute());

        addBehavior(driver);
        scheduleOrders();
    }

    private void waitForOrder() {
        log.info("[{}] [WAITING] Listening for new orders...", getAgentId());
        sleep(800);
    }

    private void validateStock() {
        log.info("[{}] [VALIDATING] Checking stock availability...", getAgentId());
        sleep(600);
        log.info("[{}] [VALIDATING] Stock OK", getAgentId());
        trySetValidationOk();
    }

    private void validatePayment() {
        log.info("[{}] [VALIDATING] Checking payment method...", getAgentId());
        sleep(900);
        log.info("[{}] [VALIDATING] Payment OK", getAgentId());
        trySetValidationOk();
    }

    private void checkFraud() {
        log.info("[{}] [VALIDATING] Running fraud detection...", getAgentId());
        sleep(500);
        log.info("[{}] [VALIDATING] Fraud check passed", getAgentId());
        trySetValidationOk();
    }

    // Set the flag only after all three validators have completed
    private void trySetValidationOk() {
        if (validationCount.incrementAndGet() == 3) {
            validationOk.set(true);
            validationCount.set(0);
        }
    }

    private void fulfillOrder() {
        log.info("[{}] [FULFILLING] Preparing shipment for order #{}...",
                getAgentId(), orderCounter.get());
        sleep(1200);
        fulfilled.set(true);
        log.info("[{}] [FULFILLING] Shipment ready", getAgentId());
    }

    private void notifyCustomer() {
        log.info("[{}] [COMPLETED] Customer notified — order #{} shipped!",
                getAgentId(), orderCounter.get());
        sleep(200);
        hasOrder.set(false);
        validationOk.set(false);
        fulfilled.set(false);
        notified.set(true);
    }

    private void scheduleOrders() {
        Thread.ofVirtual().start(() -> {
            sleep(1500);
            log.info("[{}] *** New order #1 received ***", getAgentId());
            orderCounter.set(1);
            hasOrder.set(true);

            sleep(6000);
            notified.set(false);
            sleep(500);
            log.info("[{}] *** New order #2 received ***", getAgentId());
            orderCounter.set(2);
            hasOrder.set(true);
            validationOk.set(true); // trusted customer — validation already on file
        });
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}