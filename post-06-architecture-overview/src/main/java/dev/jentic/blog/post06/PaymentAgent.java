package dev.jentic.blog.post06;

import dev.jentic.core.Message;
import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticMessageHandler;
import dev.jentic.runtime.agent.BaseAgent;

/**
 * Agent responsible for payment processing and fraud detection.
 *
 * <p>Declaring capabilities on the annotation makes this agent discoverable
 * by any component that needs payment handling — without hardcoding agent IDs.
 */
@JenticAgent(
    value = "payment-agent",
    type = "processor",
    capabilities = {"payment.processing", "fraud.detection"},
    autoStart = true
)
public class PaymentAgent extends BaseAgent {

    private int paymentsHandled = 0;

    public PaymentAgent() {
        super("payment-agent", "Payment Agent");
    }

    @Override
    protected void onStart() {
        log.info("PaymentAgent started — capabilities: payment.processing, fraud.detection");
    }

    @JenticMessageHandler("payments.requested")
    public void handlePaymentRequest(Message message) {
        paymentsHandled++;
        String orderId = message.headers().getOrDefault("order-id", "unknown");
        double amount = Double.parseDouble(message.headers().getOrDefault("amount", "0.0"));

        log.info("Processing payment #{} for order {} — amount: {:.2f}", paymentsHandled, orderId, amount);

        // Simulate fraud check
        boolean suspicious = amount > 10_000;
        if (suspicious) {
            log.warn("Flagging payment for order {} for fraud review (amount: {:.2f})", orderId, amount);
        }

        Message result = Message.builder()
            .topic("payments.processed")
            .senderId(getAgentId())
            .content(suspicious ? "REVIEW" : "APPROVED")
            .header("order-id", orderId)
            .header("fraud-flag", String.valueOf(suspicious))
            .build();

        messageService.send(result);
    }

    @Override
    protected void onStop() {
        log.info("PaymentAgent stopped — {} payments handled", paymentsHandled);
    }
}
