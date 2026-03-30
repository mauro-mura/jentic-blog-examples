package dev.jentic.blog.post05.parallel;

import dev.jentic.runtime.JenticRuntime;

import java.util.concurrent.TimeUnit;

/**
 * Example 2 — ParallelBehavior
 *
 * Runs two agents:
 *   - OrderValidationAgent: ALL strategy (stock + payment + fraud must all pass)
 *   - GeoLookupAgent:       FIRST strategy (race between two providers)
 */
public class ParallelExample {

    public static void main(String[] args) throws InterruptedException {
        var runtime = JenticRuntime.builder()
                .scanPackage("dev.jentic.blog.post05.parallel")
                .build();

        runtime.start();
        TimeUnit.SECONDS.sleep(8);
        runtime.stop();
    }
}