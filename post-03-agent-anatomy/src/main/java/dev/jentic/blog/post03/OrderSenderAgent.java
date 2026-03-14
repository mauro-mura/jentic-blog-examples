package dev.jentic.blog.post03;

import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticBehavior;
import dev.jentic.runtime.agent.BaseAgent;
import dev.jentic.core.Message;

import static dev.jentic.core.BehaviorType.ONE_SHOT;


/**
 * Test agent: sends a batch of sample orders at startup.
 *
 * Uses a ONE_SHOT behavior to publish orders on "orders.incoming"
 * once the runtime starts. OrderValidatorAgent picks them up
 * and publishes the results on "orders.validated".
 *
 * Mix of valid (ORDER-NNNN) and invalid IDs to show both outcomes.
 */
@JenticAgent("order-sender")
public class OrderSenderAgent extends BaseAgent {

    private static final String[] SAMPLE_ORDERS = {
        "ORDER-1001",
        "ORDER-1002",
        "INVALID-999",
        "ORDER-1003",
        "BAD-FORMAT"
    };

    @JenticBehavior(type = ONE_SHOT)
    public void sendTestOrders() {
        log.info("Sending {} test orders...", SAMPLE_ORDERS.length);

        for (String orderId : SAMPLE_ORDERS) {
            messageService.send(Message.builder()
                    .topic("orders.incoming")
                    .content(orderId)
                    .build());
            log.info("Order dispatched: {}", orderId);
        }
    }
}
