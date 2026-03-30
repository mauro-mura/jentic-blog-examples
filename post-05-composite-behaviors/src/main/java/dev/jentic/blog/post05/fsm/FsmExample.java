package dev.jentic.blog.post05.fsm;

import dev.jentic.runtime.JenticRuntime;

import java.util.concurrent.TimeUnit;

/**
 * Example 3 — FSMBehavior
 *
 * Runs two agents:
 *   - OrderLifecycleAgent: basic FSM (IDLE → PROCESSING → DONE → IDLE), three cycles
 *   - PaymentAgent:        FSM with state timeout (PENDING → CONFIRMED or FAILED)
 */
public class FsmExample {

    public static void main(String[] args) throws InterruptedException {
        var runtime = JenticRuntime.builder()
                .scanPackage("dev.jentic.blog.post05.fsm")
                .build();

        runtime.start();
        TimeUnit.SECONDS.sleep(20);
        runtime.stop();
    }
}