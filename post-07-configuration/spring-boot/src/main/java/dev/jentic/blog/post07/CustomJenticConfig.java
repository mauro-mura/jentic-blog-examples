package dev.jentic.blog.post07;

import dev.jentic.runtime.JenticRuntime;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Optional: shows how to override the auto-configured JenticRuntime bean.
 *
 * This class is intentionally excluded from the default Spring Boot scan
 * (see @Configuration below — it is present in the source but NOT active).
 * To activate it, remove the comment on @Configuration and ensure
 * ConfigurationApplication does not scan the default auto-post07.
 *
 * When active, this config loads Jentic from a custom YAML file on the classpath
 * instead of reading from application.yml via @ConfigurationProperties.
 * Every auto-configured bean is guarded by @ConditionalOnMissingBean,
 * so declaring jenticRuntime here suppresses the starter's default.
 */
// Uncomment @Configuration to activate this override:
// @Configuration
public class CustomJenticConfig {

    /**
     * Overrides the auto-configured JenticRuntime.
     * The starter's default bean is suppressed because of @ConditionalOnMissingBean.
     */
    @Bean(name = "jenticRuntime")
    public JenticRuntime jenticRuntime() {
        return JenticRuntime.builder()
                .fromClasspathConfig("jentic-custom.yml")
                .build();
    }

    /**
     * When overriding the runtime, the lifecycle bean must also be provided explicitly.
     * SmartLifecycle with phase Integer.MAX_VALUE - 1 ensures Jentic starts after
     * all infrastructure beans and stops before them.
     */
    @Bean
    public SmartLifecycle jenticRuntimeLifecycle(JenticRuntime jenticRuntime) {
        return new SmartLifecycle() {
            private volatile boolean running = false;

            @Override
            public void start() {
                jenticRuntime.start().join();
                running = true;
            }

            @Override
            public void stop() {
                jenticRuntime.stop().join();
                running = false;
            }

            @Override
            public boolean isRunning() {
                return running;
            }

            @Override
            public int getPhase() {
                return Integer.MAX_VALUE - 1;
            }
        };
    }
}
