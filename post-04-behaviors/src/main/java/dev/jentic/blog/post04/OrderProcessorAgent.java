package dev.jentic.blog.post04;

import dev.jentic.core.Message;
import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticBehavior;
import dev.jentic.runtime.agent.BaseAgent;
import dev.jentic.runtime.behavior.EventDrivenBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.jentic.core.BehaviorType.CYCLIC;

/**
 * EventDrivenBehavior example.
 *
 * <p>{@link OrderProcessorAgent} listens on "orders.incoming" using an
 * {@link EventDrivenBehavior} registered programmatically in {@code onStart()}.
 * The behavior keeps an internal counter — a pattern that justifies the
 * programmatic approach over the simpler {@code @JenticMessageHandler}.
 *
 * <p>{@link OrderProducerAgent} publishes a new order every 2 seconds so the
 * processor always has something to react to.
 *
 * <p>Post: {bit Autonomi} #04 — Behaviors: the heartbeat of agents
 * Blog: https://bitautonomi.substack.com
 */
@JenticAgent(value = "order-processor", autoStart = true)
public class OrderProcessorAgent extends BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(OrderProcessorAgent.class);

    public OrderProcessorAgent() {
        super("order-processor", "Order Processor");
    }

    @Override
    protected void onStart() {
        log.info("Order Processor started");

        // EventDrivenBehavior with internal state (received counter).
        // Use the factory method here; extend EventDrivenBehavior as a separate
        // class when the behavior grows larger or needs more complex state.
        final int[] receivedCount = {0};

        addBehavior(EventDrivenBehavior.from("orders.incoming", message -> {
            receivedCount[0]++;
            String orderId = message.getContent(String.class);
            log.info("Order received [count={}]: {}", receivedCount[0], orderId);

            messageService.send(Message.builder()
                    .topic("orders.processed")
                    .content(orderId)
                    .build());
            return null;
        }));
    }

    // ------------------------------------------------------------------
    // Companion producer: generates one order every 2 seconds
    // ------------------------------------------------------------------

    @JenticAgent(value = "order-producer", autoStart = true)
    public static class OrderProducerAgent extends BaseAgent {

        private static final Logger log = LoggerFactory.getLogger(OrderProducerAgent.class);

        private int orderSeq = 0;

        public OrderProducerAgent() {
            super("order-producer", "Order Producer");
        }

        @JenticBehavior(type = CYCLIC, interval = "2s")
        public void produceOrder() {
            String orderId = "ORD-" + String.format("%04d", ++orderSeq);
            log.info("Sending order: {}", orderId);
            messageService.send(Message.builder()
                    .topic("orders.incoming")
                    .content(orderId)
                    .build());
        }
    }
}
