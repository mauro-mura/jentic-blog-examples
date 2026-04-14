# Post 08 — LLM Integration

Example code for [{bit Autonomi}](https://bitautonomi.substack.com) post #08:
**"Jentic incontra l'AI: integrare un LLM nel tuo agente"**.

## What this example shows

- How to extend `LLMAgent` instead of `BaseAgent`
- How to configure an OpenAI provider using the `Models` enum
- How to seed the conversation with a system message in `onStart()`
- How to call the LLM via `addConversationMessage()`, `buildLLMPrompt()`, and `LLMRequest.builder()`
- How to wire a `DefaultLLMMemoryManager` into the runtime for conversation history

## Agents

| Agent | Topic in | Topic out | Description |
|---|---|---|---|
| `TicketClassifierAgent` | `support.incoming` | `support.classified` | Classifies free-text tickets using GPT-4.1 Mini |
| `ResilientClassifierAgent` | `support.incoming` | `support.classified` | Same, wrapped in `RetryBehavior` with exponential backoff on rate limits |
| `TicketSenderAgent` | — | — | Sends 5 sample tickets at startup, logs results |

## Prerequisites

- Java 21+
- Maven 3.9+
- An OpenAI API key

## Running

```bash
# 1. Clone and install the Jentic framework at v0.17.0
git clone --branch v0.17.0 https://github.com/mauro-mura/jentic.git
cd jentic
mvn clean install -DskipTests
cd ..

# 2. Unzip this example and enter the project directory
git clone https://github.com/mauro-mura/jentic-blog-examples.git
cd jentic-blog-examples/post-08-llm-integration

# 3. Set your API key and run
export OPENAI_API_KEY=sk-...
mvn exec:java
```

Expected output (order may vary):

```
[ticket-sender] Sending 5 sample tickets...
[ticket-sender]   → I can't access my account — the password reset email never arrived
[ticket-sender]   → My invoice shows the wrong amount for last month
...
[ticket-classifier] Ticket classified as: ACCOUNT
[ticket-sender]   ← Classification: ACCOUNT
[ticket-classifier] Ticket classified as: BILLING
[ticket-sender]   ← Classification: BILLING
...
```

## Jentic version

This example targets Jentic **0.17.0** (BOM: `dev.jentic:jentic-bom:0.17.0`).

## RetryBehavior example

`ResilientClassifierAgent` shows how to wrap an LLM call in a `RetryBehavior` with exponential backoff, retrying only on rate-limit errors:

```java
var retrying = RetryBehavior
        .withExponentialBackoff("classify-retry", 3, Duration.ofSeconds(2),
                () -> provider.chat(LLMRequest.builder().messages(prompt).build()).join().content())
        .withRetryCondition(ex ->
                ex instanceof LLMException llmEx
                && llmEx.getErrorType() == LLMException.ErrorType.RATE_LIMIT)
        .onSuccess(category -> { /* publish result */ })
        .onFailure(ex -> log.error("All retries failed", ex));

addBehavior(retrying);
```

`withExponentialBackoff` creates the behavior from a `CheckedSupplier` lambda — no subclass needed. `withRetryCondition` limits retries to rate-limit errors only; all other exceptions propagate immediately.

## Project structure

```
post-08-llm-integration/
├── pom.xml
├── README.md
└── src/main/java/dev/jentic/blog/post08/
    ├── TicketClassifierAgent.java      # Core LLMAgent example
    ├── ResilientClassifierAgent.java   # Variant with RetryBehavior
    ├── TicketSenderAgent.java          # Test ticket sender
    └── TicketClassifierRunner.java     # Main entry point
```

## Switching provider

To use Anthropic instead of OpenAI, replace the provider setup in `TicketClassifierAgent`:

```java
this.provider = LLMProviderFactory.anthropic()
        .apiKey(System.getenv("ANTHROPIC_API_KEY"))
        .modelName(AnthropicProvider.Models.CLAUDE_HAIKU_4_5)
        .build();
```

For local inference with Ollama (no API key required):

```java
this.provider = LLMProviderFactory.ollama()
        .baseUrl("http://localhost:11434")
        .modelName("llama3.2")
        .build();
```

## Related post

[Jentic incontra l'AI: integrare un LLM nel tuo agente](https://bitautonomi.substack.com)
