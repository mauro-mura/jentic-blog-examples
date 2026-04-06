package dev.jentic.blog.post07;

import dev.jentic.runtime.JenticRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point for the standalone post07 example.
 *
 * The runtime loads jentic.yml from the classpath (src/main/resources/jentic.yml).
 * Agents are discovered automatically from this package.
 *
 * Run with:
 *   mvn exec:java -pl standalone -Dexec.mainClass="dev.jentic.blog.post07.StandaloneMain"
 */
public class StandaloneMain {

    private static final Logger log = LoggerFactory.getLogger(StandaloneMain.class);

    public static void main(String[] args) throws InterruptedException {
        log.info("Starting standalone Jentic example (config from jentic.yml)");

        var runtime = JenticRuntime.builder()
                .fromClasspathConfig("jentic.yml")
                .build();

        runtime.start();

        // Run for 30 seconds, then shut down cleanly
        Thread.sleep(30_000);

        log.info("Shutting down");
        runtime.stop();
    }
}
