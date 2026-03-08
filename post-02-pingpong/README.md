# post-02-pingpong

PingPong example for article **#02 — Your first Jentic agent in 5 minutes**  
📖 [Read the article](https://bitautonomi.substack.com)

## Requirements

- Java 21
- Maven 3.9+
- Jentic `v0.10.0` installed in the local Maven repository (see below)

## Setup

### 1. Install Jentic in the local Maven repository

```bash
git clone --branch v0.10.0 https://github.com/mauro-mura/jentic.git
cd jentic
mvn clean install
```

### 2. Run the example

```bash
cd post-02-pingpong
mvn exec:java
```

## Expected output

```
[PING] Sending: ping
[PONG] Received: ping — replying with: pong
[PING] Received: pong
```

## Project structure

```
post-02-pingpong/
├── pom.xml
└── src/main/java/dev/jentic/blog/post02/
    ├── App.java          # Entry point — builder, registerAgent, shutdown hook
    ├── PingAgent.java    # Initiates communication with ONE_SHOT
    └── PongAgent.java    # Replies to the "ping" message
```
