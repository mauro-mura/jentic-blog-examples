package dev.jentic.blog.post05.combined;

import dev.jentic.runtime.JenticRuntime;

import java.util.concurrent.TimeUnit;

/**
 * Example 4 — Composite behaviors combined
 *
 * Runs a single OrderAgent: the FSM governs order states,
 * and the VALIDATING state hosts a ParallelBehavior that runs
 * stock, payment and fraud checks concurrently.
 *
 * Flow: WAITING → VALIDATING → FULFILLING → COMPLETED → WAITING
 */
public class CombinedExample {

    public static void main(String[] args) throws InterruptedException {
        var runtime = JenticRuntime.builder()
                .scanPackage("dev.jentic.blog.post05.combined")
                .build();

        runtime.start();
        TimeUnit.SECONDS.sleep(25);
        runtime.stop();
    }
}