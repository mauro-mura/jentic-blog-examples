package dev.jentic.blog.post05.sequential;

import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.runtime.agent.BaseAgent;
import dev.jentic.runtime.behavior.OneShotBehavior;
import dev.jentic.runtime.behavior.composite.SequentialBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Report pipeline agent: extract → aggregate → publish (one-shot sequence).
 */
@JenticAgent("report-pipeline-agent")
public class ReportPipelineAgent extends BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(ReportPipelineAgent.class);

    @Override
    protected void onStart() {
        // Report pipeline: extract → aggregate → publish (runs once, then stops)
        SequentialBehavior reportPipeline = new SequentialBehavior("report-pipeline");
        reportPipeline.addChildBehavior(OneShotBehavior.from("extract-data",   this::extractData));
        reportPipeline.addChildBehavior(OneShotBehavior.from("aggregate",      this::aggregateMetrics));
        reportPipeline.addChildBehavior(OneShotBehavior.from("publish-report", this::publishReport));

        addBehavior(reportPipeline);
    }

    private void extractData() {
        log.info("[{}] Step 1/3 — extracting data from source...", getAgentId());
        sleep(400);
        log.info("[{}] Step 1/3 — extraction complete", getAgentId());
    }

    private void aggregateMetrics() {
        log.info("[{}] Step 2/3 — aggregating metrics...", getAgentId());
        sleep(300);
        log.info("[{}] Step 2/3 — aggregation complete", getAgentId());
    }

    private void publishReport() {
        log.info("[{}] Step 3/3 — publishing report...", getAgentId());
        sleep(200);
        log.info("[{}] Step 3/3 — report published. Pipeline done.", getAgentId());
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}