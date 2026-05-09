package dev.jentic.blog.post09;

import dev.jentic.adapters.llm.LLMProviderFactory;
import dev.jentic.adapters.llm.anthropic.AnthropicProvider;
import dev.jentic.adapters.llm.openai.OpenAIProvider;
import dev.jentic.core.Message;
import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticMessageHandler;
import dev.jentic.core.llm.LLMMessage;
import dev.jentic.core.llm.LLMProvider;
import dev.jentic.core.llm.LLMRequest;
import dev.jentic.runtime.agent.LLMAgent;
import dev.jentic.runtime.memory.llm.ContextWindowStrategies;
import java.util.List;

/**
 * SupportAgent — LLM-powered agent with conversation memory and long-term fact storage.
 *
 * <p>Demonstrates:
 * <ul>
 *   <li>Context window management via {@code LLMAgent} (SLIDING strategy)</li>
 *   <li>Long-term fact storage via {@code storeFact} / {@code rememberLong}</li>
 *   <li>Graceful degradation when {@code LLMMemoryManager} is not injected</li>
 * </ul>
 *
 * <p>Listens on topic {@code support.question}, publishes answers on {@code support.answer}.
 *
 * <p>Requires the environment variable {@code OPENAI_API_KEY} to be set.
 */
@JenticAgent("support-agent")
public class SupportAgent extends LLMAgent {

    private final LLMProvider provider;

    public SupportAgent() {
        this.provider = LLMProviderFactory.openai()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(OpenAIProvider.Models.GPT_4O_MINI)
            .maxTokens(4096)
            .build();
    }

    @Override
    protected void onStart() {
        if (!hasLLMMemory()) {
            log.warn("LLMMemoryManager not injected — skipping memory setup");
            return;
        }
        setDefaultStrategy(ContextWindowStrategies.SLIDING);
        setDefaultConversationBudget(3000);
        setDefaultContextBudget(800);

        addConversationMessage(
            LLMMessage.system(
                "You are a helpful support agent. " +
                "Use the context provided to give consistent, personalized answers."
            )
        ).join();

        log.info("SupportAgent started — memory ready, strategy: SLIDING");
    }

    @JenticMessageHandler("support.question")
    public void handleQuestion(Message msg) {
        String question = msg.getContent(String.class);
        log.info("Received question: {}", question);

        // Append the user turn to the conversation history
        addConversationMessage(LLMMessage.user(question)).join();

        // Build the prompt within the configured token budget:
        // recent history (SLIDING) + relevant facts from MemoryStore
        List<LLMMessage> prompt = buildLLMPrompt(question, 2000).join();

        LLMRequest request = LLMRequest.builder()
            .messages(prompt)
            .build();

        String answer = provider.chat(request).join().content();

        // Record the assistant turn in the conversation history
        addConversationMessage(LLMMessage.assistant(answer)).join();

        // If the question touches preferences, store a long-term fact
        if (question.toLowerCase().contains("prefer")) {
            storeFact("last-preference-question", question).join();
        }

        messageService.send(Message.builder()
            .topic("support.answer")
            .content(answer)
            .build());
    }
}
