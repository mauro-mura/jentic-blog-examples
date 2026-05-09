package dev.jentic.blog.post09;

import dev.jentic.core.Message;
import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticBehavior;
import dev.jentic.core.annotations.JenticMessageHandler;
import dev.jentic.runtime.agent.BaseAgent;

import static dev.jentic.core.BehaviorType.ONE_SHOT;

/**
 * QuestionSenderAgent — drives the demo by sending a sequence of questions to SupportAgent.
 *
 * <p>Sends three questions on startup (ONE_SHOT behavior), then listens for answers.
 * The third question contains the word "prefer" to trigger long-term fact storage
 * in {@link SupportAgent}.
 */
@JenticAgent("question-sender")
public class QuestionSenderAgent extends BaseAgent {

    private static final String[] QUESTIONS = {
        "Hello! My name is Alice. Can you help me with your return policy?",
        "What is the standard processing time for a refund?",
        "I prefer to receive refunds via bank transfer — is that possible?"
    };

    @JenticBehavior(type = ONE_SHOT)
    public void sendQuestions() throws InterruptedException {
        for (String question : QUESTIONS) {
            log.info("Sending: {}", question);
            messageService.send(Message.builder()
                .topic("support.question")
                .content(question)
                .build());
            // Short pause so answers arrive in order
            Thread.sleep(3_000);
        }
    }

    @JenticMessageHandler("support.answer")
    public void handleAnswer(Message msg) {
        log.info("Answer received:\n{}", msg.getContent(String.class));
    }
}
