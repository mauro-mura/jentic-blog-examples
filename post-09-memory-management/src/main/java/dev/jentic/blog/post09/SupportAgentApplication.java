package dev.jentic.blog.post09;

import dev.jentic.runtime.JenticRuntime;
import dev.jentic.runtime.memory.InMemoryStore;
import dev.jentic.runtime.memory.llm.DefaultLLMMemoryManager;
import dev.jentic.runtime.memory.llm.SimpleTokenEstimator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point for post-09 — Memory Management in LLM agents.
 *
 * <p>Bootstraps a {@link JenticRuntime} with:
 * <ul>
 *   <li>{@link SupportAgent} — LLM-powered support agent with conversation memory</li>
 *   <li>{@link QuestionSenderAgent} — sends three test questions on startup</li>
 * </ul>
 *
 * <p><strong>Prerequisite:</strong> set the {@code OPENAI_API_KEY} environment variable
 * before running:
 * <pre>
 *   export OPENAI_API_KEY=sk-ant-...
 *   mvn exec:java
 * </pre>
 */
public class SupportAgentApplication {

    private static final Logger log = LoggerFactory.getLogger(SupportAgentApplication.class);

    public static void main(String[] args) throws InterruptedException {
        if (System.getenv("OPENAI_API_KEY") == null) {
            log.error("OPENAI_API_KEY environment variable is not set. Exiting.");
            System.exit(1);
        }

        // The runtime calls the factory once per LLMAgent, passing the agent ID.
        // Each agent gets its own DefaultLLMMemoryManager backed by a shared InMemoryStore.
        var sharedStore = new InMemoryStore();
        var estimator   = new SimpleTokenEstimator();

        var runtime = JenticRuntime.builder()
            .withDefaultConfig()
            .scanPackage("dev.jentic.blog.post09")
            .llmMemoryManagerFactory(agentId ->
                new DefaultLLMMemoryManager(sharedStore, estimator, agentId))
            .build();

        log.info("Starting runtime...");
        runtime.start();

        // Keep the process alive while the agents exchange messages
        Thread.sleep(30_000);

        log.info("Shutting down...");
        runtime.stop();
    }
}
