package dev.jentic.blog.post02;

import dev.jentic.runtime.JenticRuntime;

// Entry point: builds the Jentic runtime, registers the agents, and handles shutdown
public class App {
    public static void main(String[] args) {
        JenticRuntime runtime = JenticRuntime.builder()
                .build();

        runtime.registerAgent(new PingAgent());
        runtime.registerAgent(new PongAgent());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> runtime.stop().join()));

        runtime.start().join();
    }
}
