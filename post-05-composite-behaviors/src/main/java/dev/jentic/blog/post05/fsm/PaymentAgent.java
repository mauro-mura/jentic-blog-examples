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

/**
 * Payment agent: FSM with a 5 s state timeout on PENDING.
 * A simulated gateway callback arrives after 2 s and triggers the CONFIRMED transition.
 *
 * FSMBehavior is on-demand: a CyclicBehavior driver ticks it at regular intervals.
 */
@JenticAgent("payment-agent")
public class PaymentAgent extends BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(PaymentAgent.class);

    private final AtomicBoolean confirmed = new AtomicBoolean(false);
    private final AtomicBoolean failed    = new AtomicBoolean(false);
    private final AtomicBoolean started   = new AtomicBoolean(false);

    @Override
    protected void onStart() {
        FSMBehavior paymentFsm = FSMBehavior.builder("payment-fsm", "PENDING",
                        Duration.ofSeconds(5))
                .state("PENDING",   OneShotBehavior.from("await-payment", this::awaitPayment))
                .state("CONFIRMED", OneShotBehavior.from("fulfill-order", this::fulfillOrder))
                .state("FAILED",    OneShotBehavior.from("refund",        this::processRefund))
                .transition("PENDING", "CONFIRMED", fsm -> confirmed.get())
                .transition("PENDING", "FAILED",    fsm -> failed.get())
                .build();

        // FSMBehavior is on-demand: a CyclicBehavior driver ticks it at regular intervals
        CyclicBehavior driver = CyclicBehavior.from(
                "payment-fsm-driver",
                Duration.ofMillis(300),
                () -> paymentFsm.execute());

        addBehavior(driver);

        // Simulate a payment confirmation arriving after 2 s
        schedulePaymentConfirmation();
    }

    private void awaitPayment() {
        if (!started.getAndSet(true)) {
            log.info("[{}] [PENDING] Waiting for payment gateway callback...", getAgentId());
        }
        sleep(300);
    }

    private void fulfillOrder() {
        log.info("[{}] [CONFIRMED] Payment confirmed — fulfilling order", getAgentId());
        sleep(800);
        log.info("[{}] [CONFIRMED] Order fulfilled successfully", getAgentId());
    }

    private void processRefund() {
        log.info("[{}] [FAILED] Payment failed — initiating refund", getAgentId());
        sleep(500);
        log.info("[{}] [FAILED] Refund processed", getAgentId());
    }

    private void schedulePaymentConfirmation() {
        Thread.ofVirtual().start(() -> {
            sleep(2000);
            log.info("[{}] Payment gateway callback received", getAgentId());
            confirmed.set(true);
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