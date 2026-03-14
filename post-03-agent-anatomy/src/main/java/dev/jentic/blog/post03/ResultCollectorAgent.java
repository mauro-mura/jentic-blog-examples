package dev.jentic.blog.post03;

import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticMessageHandler;
import dev.jentic.runtime.agent.BaseAgent;
import dev.jentic.core.Message;

/**
 * Agent that collects and displays validation results.
 *
 * Listens to "orders.validated" and logs the outcome
 * of each order processed by OrderValidatorAgent.
 *
 * Demonstrates how a third agent can join the flow
 * without touching the other two: pure pub/sub decoupling.
 */
@JenticAgent("result-collector")
public class ResultCollectorAgent extends BaseAgent {

    @JenticMessageHandler("orders.validated")
    public void collectResult(Message message) {
        String[] parts = message.content().toString().split(":");
        if (parts.length == 2) {
            String orderId = parts[0];
            boolean valid  = Boolean.parseBoolean(parts[1]);
            if (valid) {
                log.info("✔ Order ACCEPTED: {}", orderId);
            } else {
                log.warn("✘ Order REJECTED: {}", orderId);
            }
        }
    }
}
