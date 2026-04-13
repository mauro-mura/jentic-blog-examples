# post-07-configuration

Examples for blog post **#07 — Configuring Jentic: YAML, Spring Boot, and virtual threads**.

Jentic version: **0.14.1**

---

## What this project contains

Two Maven modules, one agent (`HeartbeatAgent` + `MonitorAgent`), two configurations.

| Module | Entry point | Configuration source |
|---|---|---|
| `standalone` | `StandaloneMain` | `jentic.yml` on classpath |
| `spring-boot` | `ConfigurationApplication` | `application.yml` via Spring Boot Starter |

The agent code is identical in both modules. Only the configuration layer changes.

---

## Prerequisites

- Java 21+
- Maven 3.9+
- Jentic 0.14.1 installed locally (`mvn install` from the Jentic root)

---

## Running

### Standalone

```bash
git clone --branch v0.14.1 https://github.com/mauro-mura/jentic.git
cd jentic
mvn clean install -DskipTests

# Clone the blog examples repo
git clone https://github.com/mauro-mura/jentic-blog-examples.git
cd jentic-blog-examples/post-07-configuration

mvn install -pl standalone

mvn exec:java -pl standalone \
  -Dexec.mainClass="dev.jentic.blog.post07.StandaloneMain"
```

Expected output (every 5 seconds):

```
INFO  HeartbeatAgent - Heartbeat sent
INFO  MonitorAgent   - Heartbeat received from ping from heartbeat-agent
INFO  HeartbeatAgent - Acknowledgment received: ack from monitor-agent
```

### Spring Boot

```bash
mvn spring-boot:run -pl spring-boot
```

Once running, check agent status:

```bash
curl http://localhost:8080/actuator/health
```

Expected response:

```json
{
  "status": "UP",
  "components": {
    "jentic": {
      "status": "UP",
      "details": {
        "runtime.name": "heartbeat-system",
        "agents.total": 2,
        "agents.running": 2
      }
    }
  }
}
```

---

## Key files

| File | What it shows |
|---|---|
| `standalone/src/main/resources/jentic.yml` | Full YAML structure with all optional keys |
| `standalone/src/main/java/.../StandaloneMain.java` | `fromClasspathConfig()` builder method |
| `spring-boot/src/main/resources/application.yml` | Spring Boot Starter configuration keys |
| `spring-boot/src/main/java/.../CustomJenticConfig.java` | `@ConditionalOnMissingBean` override pattern |

---

## What does NOT change between the two modules

`HeartbeatAgent.java` and `MonitorAgent.java` are identical line for line.
The runtime, the lifecycle management, and the configuration source are different.
The agents do not care.
