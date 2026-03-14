package dev.jentic.blog.post03;

import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticMessageHandler;
import dev.jentic.runtime.agent.BaseAgent;
import dev.jentic.core.Message;

/**
 * Agent that validates incoming orders and publishes the result.
 *
 * Listens on topic "orders.incoming", checks that the order ID
 * follows the expected format (ORDER-NNNN), then publishes
 * the validation outcome on "orders.validated".
 *
 */
@JenticAgent("order-validator")
public class OrderValidatorAgent extends BaseAgent {

    @JenticMessageHandler("orders.incoming")
    public void validateOrder(Message message) {
        String orderId = message.content().toString();
        log.info("Validating order: {}", orderId);

        boolean valid = orderId != null && orderId.startsWith("ORDER-");

        String outcome = orderId + ":" + valid;
        messageService.send(Message.builder()
                .topic("orders.validated")
                .content(outcome)
                .build());

        log.info("Validation result published: {}", outcome);
    }
}
