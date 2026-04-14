package dev.jentic.blog.post08;

import dev.jentic.core.Message;
import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticMessageHandler;
import dev.jentic.core.llm.LLMException;
import dev.jentic.core.llm.LLMMessage;
import dev.jentic.core.llm.LLMProvider;
import dev.jentic.core.llm.LLMRequest;
import dev.jentic.adapters.llm.LLMProviderFactory;
import dev.jentic.adapters.llm.openai.OpenAIProvider;
import dev.jentic.runtime.agent.LLMAgent;
import dev.jentic.runtime.behavior.advanced.RetryBehavior;

import java.time.Duration;
import java.util.List;

/**
 * Variant of {@link TicketClassifierAgent} that wraps the LLM call in a
 * {@link RetryBehavior} with exponential backoff.
 *
 * <p>Demonstrates:
 * <ul>
 *   <li>Using {@link RetryBehavior#withExponentialBackoff} with a lambda action</li>
 *   <li>Filtering retries to rate-limit errors only via {@code withRetryCondition}</li>
 *   <li>Using {@code onSuccess} / {@code onFailure} callbacks to continue the flow</li>
 *   <li>Adding a behavior dynamically from inside a message handler</li>
 * </ul>
 *
 * <p>Listens on {@code support.incoming}, publishes on {@code support.classified}.
 * Requires the {@code OPENAI_API_KEY} environment variable.
 */
@JenticAgent("resilient-classifier")
public class ResilientClassifierAgent extends LLMAgent {

    private final LLMProvider provider;

    public ResilientClassifierAgent() {
        this.provider = LLMProviderFactory.openai()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(OpenAIProvider.Models.GPT_4_1_MINI)
                .build();
    }

    @Override
    protected void onStart() {
        addConversationMessage(LLMMessage.system("""
                You are a support ticket classifier for an e-commerce platform.
                Categories: BILLING (invoices, payments, refunds),
                            TECHNICAL (bugs, errors, crashes),
                            ACCOUNT (login, password, profile),
                            OTHER (anything else).
                Respond with exactly one word from the list above.
                If the request is unclear, respond with OTHER.
                """)).join();
    }

    /**
     * Classifies an incoming ticket via the LLM, retrying up to 3 times with
     * exponential backoff if the provider returns a rate-limit error.
     * All other errors are propagated immediately without retrying.
     */
    @JenticMessageHandler("support.incoming")
    public void classify(Message message) {
        String text = message.getContent(String.class);

        addConversationMessage(LLMMessage.user(text)).join();
        List<LLMMessage> prompt = buildLLMPrompt(text, 2000).join();

        // Each message gets its own RetryBehavior instance (unique ID via nanoTime).
        // The behavior is added dynamically, executes once with retries, then stops.
        var retrying = RetryBehavior
                .withExponentialBackoff(
                        "classify-retry-" + System.nanoTime(),
                        3,                          // max 3 retry attempts
                        Duration.ofSeconds(2),       // initial delay: 2 s → 4 s → 8 s
                        () -> provider.chat(
                                LLMRequest.builder()
                                        .messages(prompt)
                                        .build()
                        ).join().content()
                )
                // Retry only on rate-limit errors; all other exceptions propagate immediately.
                .withRetryCondition(ex ->
                        ex instanceof LLMException llmEx
                        && llmEx.getErrorType() == LLMException.ErrorType.RATE_LIMIT)
                .onRetry(attempt ->
                        log.warn("Rate limit hit, retry attempt {} for ticket: {}", attempt, text))
                .onSuccess(category -> {
                    addConversationMessage(LLMMessage.assistant(category)).join();
                    log.info("Ticket classified as: {}", category.trim());
                    messageService.send(Message.builder()
                            .topic("support.classified")
                            .content(category.trim())
                            .build());
                })
                .onFailure(ex ->
                        log.error("Classification failed after all retries for ticket: {}", text, ex));

        addBehavior(retrying);
    }
}
