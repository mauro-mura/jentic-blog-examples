package dev.jentic.blog.post06;

import dev.jentic.core.AgentDescriptor;
import dev.jentic.core.AgentQuery;
import dev.jentic.runtime.JenticRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Demonstrates the interface-first architecture of Jentic.
 *
 * <p>Two runs, same agents, different AgentDirectory implementation:
 * <ol>
 *   <li>Default setup — all services run in-memory (LocalAgentDirectory, etc.)</li>
 *   <li>Custom directory — LoggingAgentDirectory plugged in via the builder</li>
 * </ol>
 *
 * <p>The agents (OrderAgent, PaymentAgent) are identical in both runs.
 * Only the component passed to the builder changes.
 */
public class ArchitectureDemo {

    private static final Logger log = LoggerFactory.getLogger(ArchitectureDemo.class);

    public static void main(String[] args) throws InterruptedException {
        runWithDefaultSetup();
        runWithCustomDirectory();
    }

    // -------------------------------------------------------------------------
    // Run 1: default in-memory setup
    // -------------------------------------------------------------------------

    private static void runWithDefaultSetup() throws InterruptedException {
        log.info("=== Run 1: default in-memory setup ===");

        JenticRuntime runtime = JenticRuntime.builder()
            .scanPackage("dev.jentic.blog.post06")
            .build();

        runtime.start().join();

        queryByCapability(runtime, "payment.processing");
        queryByCapability(runtime, "inventory.check");

        Thread.sleep(3_000);
        runtime.stop().join();
        log.info("=== Run 1 complete ===\n");
    }

    // -------------------------------------------------------------------------
    // Run 2: same agents, custom AgentDirectory plugged in
    // -------------------------------------------------------------------------

    private static void runWithCustomDirectory() throws InterruptedException {
        log.info("=== Run 2: custom LoggingAgentDirectory ===");

        JenticRuntime runtime = JenticRuntime.builder()
            .scanPackage("dev.jentic.blog.post06")
            .agentDirectory(new LoggingAgentDirectory())  // only this line changes
            .build();

        runtime.start().join();

        queryByCapability(runtime, "fraud.detection");

        Thread.sleep(3_000);
        runtime.stop().join();
        log.info("=== Run 2 complete ===");
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private static void queryByCapability(JenticRuntime runtime, String capability) {
        List<AgentDescriptor> found = runtime.getAgentDirectory()
            .findAgents(AgentQuery.withCapabilities(java.util.Set.of(capability)))
            .join();

        log.info("Agents with capability '{}': {}", capability,
            found.stream().map(AgentDescriptor::agentId).toList());
    }
}
