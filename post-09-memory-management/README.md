# post-09 · Memory Management in LLM agents

Example project for [{bit Autonomi}](https://bitautonomi.substack.com) post `Memory Management negli agenti LLM`.

**Jentic version**: `0.17.0`  
**Java**: 21+  
**Maven**: 3.9+

---

## What this example shows

- **MemoryStore** — short-term and long-term key-value memory via `rememberShort` / `rememberLong`
- **Context window strategies** — `SLIDING` strategy to select the most relevant messages within a token budget
- **`LLMAgent`** — conversation history management, `buildLLMPrompt`, `storeFact`
- **`DefaultLLMMemoryManager`** — wiring `InMemoryStore` + `SimpleTokenEstimator` into the runtime

## Agents

| Agent | Role |
|---|---|
| `SupportAgent` | LLM-powered support agent. Handles `support.question`, publishes to `support.answer`. |
| `QuestionSenderAgent` | Sends three test questions on startup. Logs answers as they arrive. |

## Prerequisites

### 1. Build Jentic locally

Jentic is not yet published to Maven Central. Clone the repo and install it:

```bash
git clone --branch v0.17.0 https://github.com/mauro-mura/jentic.git
cd jentic
mvn clean install -DskipTests
```

### 2. Set your OpenAI API key

```bash
export OPENAI_API_KEY=...
```

## Run

```bash
mvn exec:java
```

You should see three questions sent by `QuestionSenderAgent` and three answers from `SupportAgent` logged to the console. The third question contains the word "prefer", which triggers `storeFact` in `SupportAgent` — a long-term fact stored in `MemoryStore`.

## Project structure

```
post-09-memory-management/
├── pom.xml
├── README.md
└── src/main/
    ├── java/dev/jentic/blog/post09/
    │   ├── SupportAgent.java           # LLM agent with memory
    │   ├── QuestionSenderAgent.java    # Test driver
    │   └── SupportAgentApplication.java
    └── resources/
        └── jentic.yaml
```

## Notes

- `InMemoryStore` is volatile — all memories are lost on JVM restart. For durable `LONG_TERM` storage, wire a custom `MemoryStore` implementation backed by a database.
- The `SLIDING` strategy scores messages by importance (system messages, function calls, user turns) rather than recency alone. Switch to `FIXED` for simpler behaviour, or `SUMMARIZED` for very long sessions.
- `hasLLMMemory()` is used as a guard in `onStart()`. If `LLMMemoryManager` is not injected (e.g. in unit tests), the agent starts without memory rather than throwing.
