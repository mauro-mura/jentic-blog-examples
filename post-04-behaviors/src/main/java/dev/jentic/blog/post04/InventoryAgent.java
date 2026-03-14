package dev.jentic.blog.post04;

import dev.jentic.core.Message;
import dev.jentic.core.annotations.JenticAgent;
import dev.jentic.core.annotations.JenticBehavior;
import dev.jentic.core.annotations.JenticMessageHandler;
import dev.jentic.runtime.agent.BaseAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.jentic.core.BehaviorType.CYCLIC;
import static dev.jentic.core.BehaviorType.ONE_SHOT;

/**
 * Multi-behavior example — the closing snippet of post #04.
 *
 * <p>Combines three behavior types in a single agent:
 * <ul>
 *   <li>{@code ONE_SHOT} — loads the initial stock once at startup</li>
 *   <li>{@code CYCLIC}   — syncs with the warehouse every minute</li>
 *   <li>{@code @JenticMessageHandler} — reserves stock on every incoming order</li>
 * </ul>
 *
 * <p>Post: {bit Autonomi} #04 — Behaviors: the heartbeat of agents
 * Blog: https://bitautonomi.substack.com
 */
@JenticAgent(value = "inventory-agent", autoStart = true)
public class InventoryAgent extends BaseAgent {

    private static final Logger log = LoggerFactory.getLogger(InventoryAgent.class);

    public InventoryAgent() {
        super("inventory-agent", "Inventory Agent");
    }

    @JenticBehavior(type = ONE_SHOT)
    public void loadInitialStock() {
        log.info("Loading initial inventory from database...");
        // In a real implementation: query the DB and populate an in-memory map
    }

    @JenticBehavior(type = CYCLIC, interval = "1m")
    public void syncWithWarehouse() {
        log.info("Syncing inventory with warehouse...");
        // In a real implementation: call the warehouse API and reconcile differences
    }

    @JenticMessageHandler("order.placed")
    public void reserveStock(Message message) {
        log.info("Reserving stock for order: {}", message.content());
        // In a real implementation: decrement the stock counter and send a confirmation
    }
}
