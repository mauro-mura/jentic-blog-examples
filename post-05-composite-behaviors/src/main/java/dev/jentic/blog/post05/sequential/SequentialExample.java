package dev.jentic.blog.post05.sequential;

import dev.jentic.runtime.JenticRuntime;

import java.util.concurrent.TimeUnit;

/**
 * Example 1 — SequentialBehavior
 *
 * Runs two agents:
 *   - ReportPipelineAgent: one-shot sequence (extract → aggregate → publish)
 *   - QueuePollerAgent:    repeating round-robin across three queues
 */
public class SequentialExample {

    public static void main(String[] args) throws InterruptedException {
        var runtime = JenticRuntime.builder()
                .scanPackage("dev.jentic.blog.post05.sequential")
                .build();

        runtime.start();
        TimeUnit.SECONDS.sleep(12);
        runtime.stop();
    }
}