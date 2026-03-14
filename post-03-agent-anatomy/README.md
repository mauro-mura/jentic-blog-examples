# post-03 · Agent Anatomy

Code example for post **#03** of the Jentic series on [{bit Autonomi}](https://bitautonomi.substack.com).

> **Post**: Anatomia di un agente Jentic — Lifecycle, @JenticAgent, MessageService e gestori di messaggi

---

## What this example does

Three agents collaborating via asynchronous messages on topics:

```
OrderSenderAgent  ──(orders.incoming)──▶  OrderValidatorAgent  ──(orders.validated)──▶  ResultCollectorAgent
```

- **order-sender** — sends 5 test orders at startup using a `ONE_SHOT` behavior
- **order-validator** — listens on `orders.incoming`, checks the order ID format, publishes the result on `orders.validated`
- **result-collector** — listens on `orders.validated` and prints `✔ ACCEPTED` / `✘ REJECTED`

No manual threads, no dispatcher — everything via annotations.

---

## Requirements

- Java 21+
- Maven 3.9+
- Jentic `0.11.0` installed locally (see below)

---

## Setup

### 1. Clone and install Jentic v0.11.0

```bash
git clone --branch v0.11.0 https://github.com/mauro-mura/jentic.git
cd jentic
mvn clean install -DskipTests
```

### 2. Clone the blog examples repo and navigate to this folder

```bash
git clone https://github.com/mauro-mura/jentic-blog-examples.git
cd jentic-blog-examples/post-03-agent-anatomy
```

### 3. Run

```bash
mvn compile exec:java
```

---

## Expected output

```
12:00:01.123 [main]  INFO  [OrderSenderAgent]      - Sending 5 test orders...
12:00:01.124 [main]  INFO  [OrderSenderAgent]      - Order dispatched: ORDER-1001
12:00:01.125 [main]  INFO  [OrderSenderAgent]      - Order dispatched: ORDER-1002
12:00:01.126 [main]  INFO  [OrderSenderAgent]      - Order dispatched: INVALID-999
12:00:01.127 [main]  INFO  [OrderSenderAgent]      - Order dispatched: ORDER-1003
12:00:01.128 [main]  INFO  [OrderSenderAgent]      - Order dispatched: BAD-FORMAT
12:00:01.140 [agent] INFO  [OrderValidatorAgent]   - Validating order: ORDER-1001
12:00:01.141 [agent] INFO  [OrderValidatorAgent]   - Validation result published: ORDER-1001:true
12:00:01.142 [agent] INFO  [ResultCollectorAgent]  - ✔ Order ACCEPTED: ORDER-1001
...
12:00:01.160 [agent] WARN  [ResultCollectorAgent]  - ✘ Order REJECTED: INVALID-999
```

---

## Project structure

```
post-03-agent-anatomy/
├── pom.xml
├── README.md
└── src/main/
    ├── java/dev/jentic/blog/post03/
    │   ├── Application.java            ← entry point
    │   ├── OrderSenderAgent.java       ← ONE_SHOT: sends test orders
    │   ├── OrderValidatorAgent.java    ← validates and publishes results
    │   └── ResultCollectorAgent.java   ← collects and logs outcomes
    └── resources/
        └── logback.xml                 ← logging configuration
```

---

## Concepts covered

| Concept | Where |
|---|---|
| `@JenticAgent` + `extends BaseAgent` | All three agents |
| `@JenticBehavior(type = ONE_SHOT)` | `OrderSenderAgent` |
| `messageService.send(...)` + `Message.builder()` | `OrderSenderAgent`, `OrderValidatorAgent` |
| `@JenticMessageHandler("topic")` | `OrderValidatorAgent`, `ResultCollectorAgent` |
| Pub/sub pattern and agent decoupling | Overall architecture |
| `JenticRuntime.builder().scanPackage(...)` | `Application` |
