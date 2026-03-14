package dev.jentic.blog.post04;

import dev.jentic.runtime.JenticRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point for post #04 examples.
 *
 * <p>Runs four independent demos, one per behavior type, each for a short window:
 *
 * <pre>
 *   1. CyclicBehavior      — HealthMonitorAgent + AlertReceiverAgent         (12 s)
 *   2. OneShotBehavior     — ServiceRegistryClientAgent + RegistryAgent      ( 3 s)
 *   3. EventDrivenBehavior — OrderProcessorAgent + OrderProducerAgent        (12 s)
 *   4. WakerBehavior       — OrderVerifierAgent + OrderDispatcherAgent       (20 s)
 * </pre>
 *
 * <p>Run with Maven:
 * <pre>
 *   mvn exec:java
 * </pre>
 *
 * <p>Post: {bit Autonomi} #04 — Behaviors: the heartbeat of agents
 * Blog: https://bitautonomi.substack.com
 */
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws InterruptedException {

        // ----------------------------------------------------------------
        // Demo 1 — CyclicBehavior
        // ----------------------------------------------------------------
        log.info("");
        log.info("=".repeat(60));
        log.info("DEMO 1 — CyclicBehavior");
        log.info("HealthMonitorAgent checks a service every 3 s");
        log.info("=".repeat(60));

        JenticRuntime runtime1 = JenticRuntime.builder().build();
        runtime1.registerAgent(new HealthMonitorAgent());
        runtime1.registerAgent(new AlertReceiverAgent());
        runtime1.start().join();

        Thread.sleep(12_000);

        runtime1.stop().join();
        log.info("Demo 1 completed.");

        // ----------------------------------------------------------------
        // Demo 2 — OneShotBehavior
        // ----------------------------------------------------------------
        log.info("");
        log.info("=".repeat(60));
        log.info("DEMO 2 — OneShotBehavior");
        log.info("ServiceRegistryClientAgent registers itself once at startup");
        log.info("=".repeat(60));

        JenticRuntime runtime2 = JenticRuntime.builder().build();
        runtime2.registerAgent(new ServiceRegistryClientAgent());
        runtime2.registerAgent(new ServiceRegistryClientAgent.RegistryAgent());
        runtime2.start().join();

        Thread.sleep(3_000);

        runtime2.stop().join();
        log.info("Demo 2 completed.");

        // ----------------------------------------------------------------
        // Demo 3 — EventDrivenBehavior
        // ----------------------------------------------------------------
        log.info("");
        log.info("=".repeat(60));
        log.info("DEMO 3 — EventDrivenBehavior");
        log.info("OrderProcessorAgent reacts to orders published every 2 s");
        log.info("=".repeat(60));

        JenticRuntime runtime3 = JenticRuntime.builder().build();
        runtime3.registerAgent(new OrderProcessorAgent());
        runtime3.registerAgent(new OrderProcessorAgent.OrderProducerAgent());
        runtime3.start().join();

        Thread.sleep(12_000);

        runtime3.stop().join();
        log.info("Demo 3 completed.");

        // ----------------------------------------------------------------
        // Demo 4 — WakerBehavior
        // ----------------------------------------------------------------
        log.info("");
        log.info("=".repeat(60));
        log.info("DEMO 4 — WakerBehavior");
        log.info("OrderVerifierAgent checks each order 5 s after placement");
        log.info("Orders placed every 4 s — ~30% will be stuck (no completion sent)");
        log.info("=".repeat(60));

        JenticRuntime runtime4 = JenticRuntime.builder().build();
        runtime4.registerAgent(new OrderVerifierAgent());
        runtime4.registerAgent(new OrderVerifierAgent.OrderDispatcherAgent());
        runtime4.start().join();

        Thread.sleep(20_000);

        runtime4.stop().join();
        log.info("Demo 4 completed.");

        log.info("");
        log.info("=".repeat(60));
        log.info("All demos finished.");
        log.info("=".repeat(60));
    }
}
