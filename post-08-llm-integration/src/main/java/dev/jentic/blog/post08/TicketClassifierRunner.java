package dev.jentic.blog.post08;

import dev.jentic.runtime.JenticRuntime;
import dev.jentic.runtime.agent.LLMAgent;
import dev.jentic.runtime.memory.InMemoryStore;
import dev.jentic.runtime.memory.llm.DefaultLLMMemoryManager;
import dev.jentic.runtime.memory.llm.SimpleTokenEstimator;

/**
 * Entry point for the ticket classifier example.
 *
 * <p>Registers an LLM memory manager factory so that each {@link LLMAgent}
 * instance gets its own {@link DefaultLLMMemoryManager}, keyed by agent ID.
 * Both agents are discovered automatically via {@code scanPackage}.
 *
 * <p>Prerequisites:
 * <ul>
 *   <li>Set the {@code OPENAI_API_KEY} environment variable before running.</li>
 *   <li>Java 21+ and Maven 3.9+.</li>
 * </ul>
 *
 * <p>Run with:
 * <pre>{@code
 * OPENAI_API_KEY=sk-... mvn exec:java
 * }</pre>
 */
public class TicketClassifierRunner {

    public static void main(String[] args) throws InterruptedException {

        var runtime = JenticRuntime.builder()
                .scanPackage("dev.jentic.blog.post08")
                .llmMemoryManagerFactory(agentId -> new DefaultLLMMemoryManager(
                        new InMemoryStore(),
                        new SimpleTokenEstimator(),
                        agentId
                ))
                .build();

        runtime.start();

        // Allow enough time for all 5 tickets to be classified.
        // Each LLM call takes roughly 1–3 seconds; 30 s is a safe upper bound.
        Thread.sleep(30_000);

        runtime.stop();
    }
}
