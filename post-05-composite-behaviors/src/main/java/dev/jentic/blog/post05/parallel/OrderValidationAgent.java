package dev.jentic.blog.post05.parallel;

import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.composite.CompletionStrategy;
import dev.jentic.runtime.agent.BaseAgent;
import dev.jentic.runtime.behavior.OneShotBehavior;
import dev.jentic.runtime.behavior.composite.ParallelBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Order validation agent: stock, payment and fraud checks run concurrently.
 * All three must complete before the parallel behavior finishes (ALL strategy).
 */
@JenticAgent("order-validation-agent")
public class OrderValidationAgent extends BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(OrderValidationAgent.class);

    @Override
    protected void onStart() {
        // All three validators run concurrently; proceeds only when all complete
        ParallelBehavior validation = new ParallelBehavior(
                "order-validation", CompletionStrategy.ALL);
        validation.addChildBehavior(OneShotBehavior.from("stock-check",   this::checkStock));
        validation.addChildBehavior(OneShotBehavior.from("payment-check", this::checkPayment));
        validation.addChildBehavior(OneShotBehavior.from("fraud-check",   this::checkFraud));

        addBehavior(validation);
    }

    private void checkStock() {
        log.info("[{}] Checking stock availability...", getAgentId());
        sleep(600);
        log.info("[{}] Stock OK", getAgentId());
    }

    private void checkPayment() {
        log.info("[{}] Checking payment method...", getAgentId());
        sleep(800);
        log.info("[{}] Payment OK", getAgentId());
    }

    private void checkFraud() {
        log.info("[{}] Running fraud detection...", getAgentId());
        sleep(400);
        log.info("[{}] Fraud check passed", getAgentId());
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}