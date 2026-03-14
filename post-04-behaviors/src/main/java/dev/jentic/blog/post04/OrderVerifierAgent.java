package dev.jentic.blog.post04;

import dev.jentic.core.Message;
import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticBehavior;
import dev.jentic.core.annotations.JenticMessageHandler;
import dev.jentic.runtime.agent.BaseAgent;
import dev.jentic.runtime.behavior.WakerBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.jentic.core.BehaviorType.CYCLIC;

/**
 * WakerBehavior example.
 *
 * <p>{@link OrderVerifierAgent} starts a {@link WakerBehavior#wakeAfter} for every
 * incoming order. After a short delay (5 seconds, reduced for demo purposes), it checks
 * whether the order was completed. If not, it sends a retry message.
 *
 * <p>{@link OrderDispatcherAgent} places one order every 4 seconds and randomly leaves
 * ~30% of orders stuck (no completion sent) so the verifier has timeouts to catch.
 *
 * <p>Post: {bit Autonomi} #04 — Behaviors: the heartbeat of agents
 * Blog: https://bitautonomi.substack.com
 */
@JenticAgent(value = "order-verifier", autoStart = true)
public class OrderVerifierAgent extends BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(OrderVerifierAgent.class);

    // Tracks which orders have been confirmed as completed
    private final Map<String, Boolean> completedOrders = new ConcurrentHashMap<>();

    public OrderVerifierAgent() {
        super("order-verifier", "Order Verifier");
    }

    @Override
    protected void onStart() {
        log.info("Order Verifier started — each order will be checked 5 s after placement");
    }

    // Triggered when a new order is placed
    @JenticMessageHandler("order.placed")
    public void onOrderPlaced(Message message) {
        String orderId = message.getContent(String.class);
        log.info("Received order.placed for {}, scheduling verification in 5 s", orderId);

        // One independent waker per order: orderId is captured by the lambda closure
        addBehavior(WakerBehavior.wakeAfter(
                Duration.ofSeconds(5),
                () -> checkOrderCompletion(orderId)
        ));
    }

    // Triggered when an order is confirmed as completed
    @JenticMessageHandler("order.completed")
    public void onOrderCompleted(Message message) {
        String orderId = message.getContent(String.class);
        completedOrders.put(orderId, true);
        log.info("Order {} marked as completed", orderId);
    }

    private void checkOrderCompletion(String orderId) {
        if (completedOrders.getOrDefault(orderId, false)) {
            log.info("Verification OK — order {} was completed in time", orderId);
        } else {
            log.warn("Order {} not completed after timeout — sending retry", orderId);
            messageService.send(Message.builder()
                    .topic("order.retry")
                    .content(orderId)
                    .build());
        }
    }

    // ------------------------------------------------------------------
    // Companion dispatcher: places one order every 4 s, completes ~70%
    // ------------------------------------------------------------------

    @JenticAgent(value = "order-dispatcher", autoStart = true)
    public static class OrderDispatcherAgent extends BaseAgent {

        private static final Logger log = LoggerFactory.getLogger(OrderDispatcherAgent.class);

        private final AtomicInteger orderSeq = new AtomicInteger(0);

        public OrderDispatcherAgent() {
            super("order-dispatcher", "Order Dispatcher");
        }

        @JenticBehavior(type = CYCLIC, interval = "4s")
        public void dispatchOrder() {
            String orderId = "ORD-" + String.format("%04d", orderSeq.incrementAndGet());

            messageService.send(Message.builder()
                    .topic("order.placed")
                    .content(orderId)
                    .build());

            log.info("Placed order {}", orderId);

            // ~70% of orders complete within 3 s (before the 5 s waker fires)
            boolean willComplete = orderSeq.get() % 3 != 0;
            if (willComplete) {
                scheduleCompletion(orderId);
            } else {
                log.info("Order {} will be stuck — no completion will be sent", orderId);
            }
        }

        private void scheduleCompletion(String orderId) {
            addBehavior(WakerBehavior.wakeAfter(
                    Duration.ofSeconds(3),
                    () -> {
                        log.info("Completing order {}", orderId);
                        messageService.send(Message.builder()
                                .topic("order.completed")
                                .content(orderId)
                                .build());
                    }
            ));
        }
    }
}
