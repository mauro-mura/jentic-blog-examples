# post-06-architecture-overview

Companion code for [{bit Autonomi} #06 — Architettura Jentic: interface-first e moduli](https://bitautonomi.substack.com).

## What this example shows

- How to declare agent capabilities with `@JenticAgent(capabilities = {...})`
- How to query the `AgentDirectory` by capability at runtime
- How to substitute a core service (`AgentDirectory`) via `JenticRuntime.builder()` without touching agent code

## Prerequisites

- Java 21+
- Maven 3.9+

## Run

```bash
git clone --branch v0.14.1 https://github.com/mauro-mura/jentic-blog-examples.git
cd post-06-architecture-overview
mvn compile exec:java -Dexec.mainClass="dev.jentic.blog.post06.ArchitectureDemo"
```

## Structure

```
src/main/java/dev/jentic/blog/post06/
├── OrderAgent.java             # Agent with capabilities: order.processing, inventory.check
├── PaymentAgent.java           # Agent with capabilities: payment.processing, fraud.detection
├── LoggingAgentDirectory.java  # Custom AgentDirectory — wraps LocalAgentDirectory, adds audit log
└── ArchitectureDemo.java       # Main class — two runs, same agents, different directory impl
```

## Expected output

**Run 1** (default in-memory setup):
```
Agents with capability 'payment.processing': [payment-agent]
Agents with capability 'inventory.check':    [order-agent]
```

**Run 2** (LoggingAgentDirectory plugged in):
```
[DIRECTORY] + registered  id=order-agent   type=OrderAgent  capabilities=[inventory.check, order.processing]
[DIRECTORY] + registered  id=payment-agent type=PaymentAgent capabilities=[fraud.detection, payment.processing]
Agents with capability 'fraud.detection': [payment-agent]
[DIRECTORY] - unregistered id=order-agent
[DIRECTORY] - unregistered id=payment-agent
```

The agents are identical in both runs — only the component passed to the builder changes.

## Jentic version

`0.14.1` — pinned via `jentic-bom` in `pom.xml`.
