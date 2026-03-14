# post-04-behaviors

Example code for post **#04 — Behaviors: the heartbeat of agents**
on the [{bit Autonomi}](https://bitautonomi.substack.com) blog.

## Contents

| File                              | Behavior | Description |
|-----------------------------------|---|---|
| `HealthMonitorAgent.java`         | `CyclicBehavior` | Checks a downstream service every 3 s and publishes an alert on failure |
| `AlertReceiverAgent.java`         | `@JenticMessageHandler` | Receives and logs alerts from the monitor |
| `ServiceRegistryClientAgent.java` | `OneShotBehavior` | Broadcasts its own presence on the network exactly once at startup |
| `OrderProcessorAgent.java`        | `EventDrivenBehavior` | Reacts to orders arriving on `orders.incoming` |
| `OrderVerifierAgent.java`         | `WakerBehavior` | Verifies each order 5 s after it was placed |
| `InventoryAgent.java`             | all three | Closing snippet from the post: ONE_SHOT + CYCLIC + MessageHandler |
| `Application.java`                | — | Entry point: runs the four demos sequentially |

## Prerequisites

- Java 21+
- Maven 3.9+
- Jentic `0.11.0` installed in the local Maven repository

## Setup

```bash
# 1. Clone and install Jentic
git clone --branch v0.11.0 https://github.com/mauro-mura/jentic.git
cd jentic
mvn clean install -DskipTests

# 2. Return to this folder and run
cd ../post-04-behaviors
mvn exec:java
```

## Expected output

```
============================================================
DEMO 1 — CyclicBehavior
HealthMonitorAgent checks a service every 3 s
============================================================
Health Monitor started — checking every 3 seconds
Check #1: downstream service OK
Check #2: downstream service OK
Check #3: downstream service OK
Check #4: downstream service not responding — sending alert
[WARNING] Downstream service not responding (check #4)
...

============================================================
DEMO 2 — OneShotBehavior
ServiceRegistryClientAgent registers itself once at startup
============================================================
Service Registry Client started
Registering agent service-registry-client on the network...
Registry: agent 'service-registry-client' registered successfully

============================================================
DEMO 3 — EventDrivenBehavior
OrderProcessorAgent reacts to orders published every 2 s
============================================================
Sending order: ORD-0001
Order received [count=1]: ORD-0001
Sending order: ORD-0002
Order received [count=2]: ORD-0002
...

============================================================
DEMO 4 — WakerBehavior
OrderVerifierAgent checks each order 5 s after placement
Orders placed every 4 s; ~30% will be stuck (no completion sent)
============================================================
Placed order ORD-0001
Completing order ORD-0001                  <- after 3 s
Verification OK — order ORD-0001 was completed in time
Placed order ORD-0002
Order ORD-0002 will be stuck (no completion sent)
Order ORD-0002 not completed after timeout — sending retry
...
```

## Notes

- Demo timings are intentionally short (3 s instead of 30 s, 5 s instead of 60 s) to keep the output readable within seconds.
- `InventoryAgent` is not wired into `Post04Example` because it would require a warehouse stub; it is intended as a reference for the post's closing snippet.
- Companion agents (producer, dispatcher) are inner static classes of their primary agent, following the same pattern used in the `jentic-examples` module.
