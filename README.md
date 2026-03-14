# jentic-blog-examples

Code examples for the **Jentic** series published on [{bit Autonomi}](https://bitautonomi.substack.com).

Each folder corresponds to an article in the series and contains a standalone Maven project, ready to clone and run.

---

## Repository Structure

```
jentic-blog-examples/
├── post-02-pingpong/          # #02 · Your first Jentic agent in 5 minutes
├── post-03-anatomy/           # #03 · Anatomy of a Jentic agent
├── post-04-behaviors/         # #04 · Behaviors: the beating heart of agents
└── ...
```

> Folders are added as articles are published.

---

## Getting Started

**Prerequisites**: Java 21+, Maven 3.9+

```bash
# Clone the repository
git clone https://github.com/mauro-mura/jentic-blog-examples.git

# Navigate to the example you want to explore
cd jentic-blog-examples/post-02-pingpong

# Build and run
mvn compile exec:java
```

Each folder has its own `README.md` with specific instructions and a link to the corresponding article.

---

## Article Index

| # | Title | Folder | Level |
|---|-------|--------|-------|
| 02 | [Your first Jentic agent in 5 minutes](https://bitautonomi.substack.com/p/il-tuo-primo-agente-jentic-in-5-minuti) | `post-02-pingpong` | ⭐ Entry |
| 03 | Anatomy of a Jentic agent | `post-03-anatomy` | ⭐ Entry |
| 04 | Behaviors: the beating heart of agents | `post-04-behaviors` | ⭐ Entry |
| 05 | Composite Behaviors: Sequential, Parallel and FSM | `post-05-composite-behaviors` | ⭐⭐ Mid |
| ... | ... | ... | ... |

The table is updated with each new article.

---

## Resources

- 📖 Blog: [bitautonomi.substack.com](https://bitautonomi.substack.com)
- 📦 Framework: [github.com/mauro-mura/jentic](https://github.com/mauro-mura/jentic)
- 📚 Docs: [jentic.dev/docs](https://www.jentic.dev/docs/)

---

*Author: Mauro Mura — {bit Autonomi}*
