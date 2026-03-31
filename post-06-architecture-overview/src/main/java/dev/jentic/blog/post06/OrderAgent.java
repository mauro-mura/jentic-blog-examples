package dev.jentic.blog.post06;

import dev.jentic.core.Message;
import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticBehavior;
import dev.jentic.core.annotations.JenticMessageHandler;
import dev.jentic.runtime.agent.BaseAgent;

import static dev.jentic.core.BehaviorType.CYCLIC;

/**
 * Agent responsible for order processing and inventory checks.
 *
 * <p>Capabilities are declared on the annotation: any other agent can find this
 * agent via the AgentDirectory without knowing its ID — just by querying for
 * a required capability.
 */
@JenticAgent(
    value = "order-agent",
    type = "processor",
    capabilities = {"order.processing", "inventory.check"},
    autoStart = true
)
public class OrderAgent extends BaseAgent {

    private int ordersProcessed = 0;

    public OrderAgent() {
        super("order-agent", "Order Agent");
    }

    @Override
    protected void onStart() {
        log.info("OrderAgent started — capabilities: order.processing, inventory.check");
    }

    @JenticBehavior(type = CYCLIC, interval = "8s", autoStart = true)
    public void checkPendingOrders() {
        log.info("Checking pending orders... ({} processed so far)", ordersProcessed);
    }

    @JenticMessageHandler("orders.new")
    public void handleNewOrder(Message message) {
        ordersProcessed++;
        String orderId = message.headers().getOrDefault("order-id", "unknown");
        log.info("Processing new order #{}: {}", ordersProcessed, orderId);

        Message confirmation = Message.builder()
            .topic("orders.confirmed")
            .senderId(getAgentId())
            .content("Order " + orderId + " confirmed")
            .header("order-id", orderId)
            .build();

        messageService.send(confirmation);
    }

    @Override
    protected void onStop() {
        log.info("OrderAgent stopped — {} orders processed", ordersProcessed);
    }
}
