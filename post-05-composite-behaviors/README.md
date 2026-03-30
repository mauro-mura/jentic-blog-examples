# Post #05 — Composite Behaviors: Sequential, Parallel and FSM

Code examples for post #05 of [{bit Autonomi}](https://bitautonomi.substack.com).

## Prerequisites

- Java 21+
- Maven 3.9+
- Jentic installed locally (`mvn install` from the main repo root)

## Structure

| Package | Main class | What it shows |
|---|---|---|
| `sequential` | `SequentialExample` | One-shot pipeline + round-robin poller |
| `parallel` | `ParallelExample` | `ALL` (order validation) + `FIRST` (geo-lookup race) |
| `fsm` | `FsmExample` | Order lifecycle FSM + payment FSM with state timeout |
| `combined` | `CombinedExample` | FSM with nested `ParallelBehavior` (full OrderAgent) |

## How to run

```bash
# Clone and install the framework (once)
git clone --branch v0.14.1 https://github.com/mauro-mura/jentic.git
cd jentic
mvn clean install -DskipTests

# Clone the blog examples repo
git clone https://github.com/mauro-mura/jentic-blog-examples.git
cd jentic-blog-examples/post-05-composite-behaviors

# Example 1: SequentialBehavior
mvn exec:java -Dexec.mainClass="dev.jentic.blog.post05.sequential.SequentialExample"

# Example 2: ParallelBehavior
mvn exec:java -Dexec.mainClass="dev.jentic.blog.post05.parallel.ParallelExample"

# Example 3: FSMBehavior
mvn exec:java -Dexec.mainClass="dev.jentic.blog.post05.fsm.FsmExample"

# Example 4: FSM + Parallel composition
mvn exec:java -Dexec.mainClass="dev.jentic.blog.post05.combined.CombinedExample"
```

## Expected output

### SequentialExample
```
[report-pipeline-agent] Step 1/3 — extracting data from source...
[report-pipeline-agent] Step 1/3 — extraction complete
[report-pipeline-agent] Step 2/3 — aggregating metrics...
...
[queue-poller-agent] Polling queue NORTH
[queue-poller-agent] Polling queue CENTRAL
[queue-poller-agent] Polling queue SOUTH
[queue-poller-agent] Polling queue NORTH   <- round-robin restarts
```

### ParallelExample
```
[order-validation-agent] Checking stock availability...
[order-validation-agent] Checking payment...           <- concurrent
[order-validation-agent] Running fraud check...        <- concurrent
[order-validation-agent] Fraud check OK
[order-validation-agent] Stock OK
[order-validation-agent] Payment OK
---
[geo-lookup-agent] GeoProvider1 — request sent...
[geo-lookup-agent] GeoProvider2 — request sent...      <- concurrent
[geo-lookup-agent] GeoProvider2 — location resolved    <- wins the race
```

### FsmExample
```
[order-lifecycle-agent] [IDLE] Checking order queue...
[order-lifecycle-agent] [IDLE] New order #1 received
[order-lifecycle-agent] [PROCESSING] Processing order #1...
[order-lifecycle-agent] [PROCESSING] Order #1 processed
[order-lifecycle-agent] [DONE] Notifying customer for order #1. Cycle #1 complete.
[order-lifecycle-agent] [IDLE] Checking order queue...   <- FSM resets to IDLE
```

### CombinedExample
```
[order-agent] [WAITING] Listening for new orders...
[order-agent] *** New order #1 received ***
[order-agent] [VALIDATING] Checking stock availability...
[order-agent] [VALIDATING] Checking payment method...   <- concurrent
[order-agent] [VALIDATING] Running fraud detection...   <- concurrent
[order-agent] [VALIDATING] Fraud check passed
[order-agent] [VALIDATING] Stock OK
[order-agent] [VALIDATING] Payment OK
[order-agent] [FULFILLING] Preparing shipment for order #1...
[order-agent] [FULFILLING] Shipment ready
[order-agent] [COMPLETED] Notifying customer — order #1 shipped!
```

## References

- [Blog post](https://bitautonomi.substack.com)
- [Jentic Behaviors docs](https://www.jentic.dev/docs/behaviors/)
- [SequentialBehavior](https://www.jentic.dev/docs/behaviors/SequentialBehavior/)
- [ParallelBehavior](https://www.jentic.dev/docs/behaviors/ParallelBehavior/)
- [FSMBehavior](https://www.jentic.dev/docs/behaviors/FSMBehavior/)