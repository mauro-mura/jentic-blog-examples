package dev.jentic.blog.post03;

import dev.jentic.runtime.JenticRuntime;

/**
 * Entry point for post-03 example: agent anatomy.
 *
 * Scans the current package and starts all discovered agents:
 *   - order-sender      → publishes test orders at startup (ONE_SHOT)
 *   - order-validator   → validates incoming orders and publishes results
 *   - result-collector  → logs accepted / rejected orders
 *
 * Run with:
 *   mvn compile exec:java
 *
 * Or clone and build from the tagged version:
 *   git clone https://github.com/mauro-mura/jentic-blog-examples.git
 *   cd post-03-agent-anatomy
 *   mvn compile exec:java
 */
public class Application {

    public static void main(String[] args) throws InterruptedException {
        var runtime = JenticRuntime.builder()
                .scanPackage("dev.jentic.blog.post03")
                .build();

        runtime.start();

        // Give agents time to finish processing, then shut down cleanly.
        Thread.sleep(3_000);
        runtime.stop();
    }
}
