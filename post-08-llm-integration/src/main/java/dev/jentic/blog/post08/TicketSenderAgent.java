package dev.jentic.blog.post08;

import dev.jentic.core.BehaviorType;
import dev.jentic.core.Message;
import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticBehavior;
import dev.jentic.core.annotations.JenticMessageHandler;
import dev.jentic.runtime.agent.BaseAgent;

import java.util.List;

/**
 * Test agent that sends a fixed set of sample support tickets
 * and logs the classification result for each one.
 *
 * <p>Runs once at startup ({@link BehaviorType#ONE_SHOT}), then stays
 * alive to receive the async classification responses published by
 * {@link TicketClassifierAgent} on {@code support.classified}.
 */
@JenticAgent("ticket-sender")
public class TicketSenderAgent extends BaseAgent {

    private static final List<String> SAMPLE_TICKETS = List.of(
            "I can't access my account — the password reset email never arrived",
            "My invoice shows the wrong amount for last month",
            "The app keeps crashing every time I try to upload a file",
            "How do I change the email address on my account?",
            "I was charged twice for order #98765"
    );

    /**
     * Sends all sample tickets once the agent starts.
     * A short delay gives the classifier time to initialize its system message.
     */
    @JenticBehavior(type = BehaviorType.ONE_SHOT)
    public void sendSampleTickets() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        log.info("Sending {} sample tickets...", SAMPLE_TICKETS.size());

        for (String ticket : SAMPLE_TICKETS) {
            log.info("  → {}", ticket);
            messageService.send(Message.builder()
                    .topic("support.incoming")
                    .content(ticket)
                    .build());
        }
    }

    /**
     * Receives and logs the classification result produced by the classifier.
     */
    @JenticMessageHandler("support.classified")
    public void handleClassification(Message message) {
        log.info("  ← Classification: {}", message.getContent(String.class));
    }
}
