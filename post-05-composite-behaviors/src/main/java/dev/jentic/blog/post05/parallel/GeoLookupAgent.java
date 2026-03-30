package dev.jentic.blog.post05.parallel;

import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.composite.CompletionStrategy;
import dev.jentic.runtime.agent.BaseAgent;
import dev.jentic.runtime.behavior.OneShotBehavior;
import dev.jentic.runtime.behavior.composite.ParallelBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Geo-lookup agent: race between two providers.
 * The first to respond wins; the other is cancelled (FIRST strategy).
 */
@JenticAgent("geo-lookup-agent")
public class GeoLookupAgent extends BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(GeoLookupAgent.class);

    @Override
    protected void onStart() {
        // Race between two providers: first to respond stops the others
        ParallelBehavior geoRace = new ParallelBehavior(
                "geo-lookup", CompletionStrategy.FIRST);
        geoRace.addChildBehavior(OneShotBehavior.from("geo-provider-1", this::lookupProvider1));
        geoRace.addChildBehavior(OneShotBehavior.from("geo-provider-2", this::lookupProvider2));

        addBehavior(geoRace);
    }

    private void lookupProvider1() {
        log.info("[{}] GeoProvider1 — request sent...", getAgentId());
        sleep(700);
        log.info("[{}] GeoProvider1 — location resolved: 41.9028, 12.4964", getAgentId());
    }

    private void lookupProvider2() {
        log.info("[{}] GeoProvider2 — request sent...", getAgentId());
        sleep(400); // faster: wins the race
        log.info("[{}] GeoProvider2 — location resolved: 41.9028, 12.4964", getAgentId());
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}