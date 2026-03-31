package dev.jentic.blog.post06;

import dev.jentic.core.AgentDescriptor;
import dev.jentic.core.AgentDirectory;
import dev.jentic.core.AgentQuery;
import dev.jentic.core.AgentStatus;
import dev.jentic.runtime.directory.LocalAgentDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Custom AgentDirectory that delegates all operations to LocalAgentDirectory
 * and adds structured audit logging on every registration event.
 *
 * <p>This class exists to show one thing: implementing AgentDirectory and
 * plugging it into JenticRuntime requires no changes to any agent. The agents
 * depend on the interface, not on this class.
 *
 * <p>In a real system you might replace LocalAgentDirectory with a Consul-backed
 * or Redis-backed implementation following the same pattern.
 */
public class LoggingAgentDirectory implements AgentDirectory {

    private static final Logger log = LoggerFactory.getLogger(LoggingAgentDirectory.class);

    private final AgentDirectory delegate;

    public LoggingAgentDirectory() {
        this.delegate = new LocalAgentDirectory();
    }

    @Override
    public CompletableFuture<Void> register(AgentDescriptor descriptor) {
        return delegate.register(descriptor).thenRun(() ->
            log.info("[DIRECTORY] + registered  id={} type={} capabilities={}",
                descriptor.agentId(),
                descriptor.agentType(),
                descriptor.capabilities())
        );
    }

    @Override
    public CompletableFuture<Void> unregister(String agentId) {
        return delegate.unregister(agentId).thenRun(() ->
            log.info("[DIRECTORY] - unregistered id={}", agentId)
        );
    }

    @Override
    public CompletableFuture<Optional<AgentDescriptor>> findById(String agentId) {
        return delegate.findById(agentId);
    }

    @Override
    public CompletableFuture<List<AgentDescriptor>> findAgents(AgentQuery query) {
        return delegate.findAgents(query);
    }

    @Override
    public CompletableFuture<List<AgentDescriptor>> listAll() {
        return delegate.listAll();
    }

    @Override
    public CompletableFuture<Void> updateStatus(String agentId, AgentStatus status) {
        return delegate.updateStatus(agentId, status).thenRun(() ->
            log.info("[DIRECTORY] ~ status update id={} status={}", agentId, status)
        );
    }
}
