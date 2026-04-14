package dev.jentic.blog.post08;

import dev.jentic.core.Message;
import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticMessageHandler;
import dev.jentic.core.llm.LLMMessage;
import dev.jentic.core.llm.LLMProvider;
import dev.jentic.core.llm.LLMRequest;
import dev.jentic.adapters.llm.LLMProviderFactory;
import dev.jentic.adapters.llm.openai.OpenAIProvider;
import dev.jentic.runtime.agent.LLMAgent;

import java.util.List;

/**
 * LLM-powered support ticket classifier.
 *
 * <p>Listens on the {@code support.incoming} topic, classifies each ticket
 * into one of four categories using GPT-4.1 Mini, and publishes the result
 * on {@code support.classified}.
 *
 * <p>Requires the {@code OPENAI_API_KEY} environment variable to be set.
 * Requires a {@link dev.jentic.core.memory.llm.LLMMemoryManager} to be
 * registered in the runtime via {@code llmMemoryManagerFactory(...)}.
 *
 * <p>Demonstrates:
 * <ul>
 *   <li>Extending {@link LLMAgent} instead of {@code BaseAgent}</li>
 *   <li>Seeding the conversation with a system message in {@code onStart()}</li>
 *   <li>Using {@code addConversationMessage()} and {@code buildLLMPrompt()}</li>
 *   <li>{@code LLMRequest.builder()} without explicit model name (0.16.0+)</li>
 * </ul>
 */
@JenticAgent("ticket-classifier")
public class TicketClassifierAgent extends LLMAgent {

    private final LLMProvider provider;

    public TicketClassifierAgent() {
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
     * Receives an incoming support ticket, classifies it via the LLM,
     * and publishes the single-word category on {@code support.classified}.
     */
    @JenticMessageHandler("support.incoming")
    public void classify(Message message) {
        String text = message.getContent(String.class);

        addConversationMessage(LLMMessage.user(text)).join();

        List<LLMMessage> prompt = buildLLMPrompt(text, 2000).join();

        String category = provider.chat(
                LLMRequest.builder()
                        .messages(prompt)
                        .build()
        ).join().content();

        addConversationMessage(LLMMessage.assistant(category)).join();

        log.info("Ticket classified as: {}", category.trim());

        messageService.send(Message.builder()
                .topic("support.classified")
                .content(category.trim())
                .build());
    }
}
