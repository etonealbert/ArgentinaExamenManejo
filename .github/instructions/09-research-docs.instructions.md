---
applyTo: "**/*"
---

# Research Docs Usage Instructions

The repository has a `/docs` folder containing the research papers and architecture decisions for ExamenManejo. Treat these documents as the primary project context before making non-trivial implementation changes.

## Mandatory research check

Before changing any of the following areas, inspect the matching `/docs` files first:

| Change area | Required docs to read |
|---|---|
| MVP scope, product decisions, feature boundaries | `docs/00-product-mvp-research.md` |
| iOS/Android platform targets, Gradle/Xcode settings | `docs/01-platform-support-research.md.md` |
| Architecture, layers, package structure, dependency direction | `docs/02-architecture-decision-record.md` |
| Navigation, coordinators, routes, global screens | `docs/03-navigation-coordinator-research.md.md` |
| Argentina exam data sources, legal/content reuse | `docs/04-argentina-data-source-research.md` |
| License classes, categories, question taxonomy | `docs/05-content-taxonomy-exam-model.md` |
| Parsing, cleaning, import pipeline, question packs | `docs/06-data-parsing-import-pipeline.md` |
| SQLDelight schema, migrations, local persistence | `docs/07-local-database-sqldelight-research.md` |
| Study mode, simulator, scoring, review, stats | `docs/08-exam-training-feature-research.md` |
| Compose UI/UX, accessibility, visual system | `docs/09-compose-ui-ux-research.md` |

The filenames above intentionally match the current repository state, including the `.md.md` suffix on platform and navigation documents. Do not rename research files unless explicitly requested.

## How to use the research

For each task, briefly identify:

1. Which `/docs` files were checked.
2. Which decisions from those docs affect the implementation.
3. Whether the requested change conflicts with any research decision.
4. Whether a new decision record or docs update is needed.

## Conflict rule

If code, user request, and `/docs` disagree:

1. Prefer the explicit user request for the current task.
2. Preserve MVP constraints unless the user explicitly expands scope.
3. Do not silently override an architecture/content/legal decision from `/docs`.
4. State the conflict and propose the smallest safe change.

## Content safety rule

For Argentina exam content, never add or transform questions without source metadata and review status. Before implementing content import or seed data, check:

- `docs/04-argentina-data-source-research.md`
- `docs/05-content-taxonomy-exam-model.md`
- `docs/06-data-parsing-import-pipeline.md`

Do not copy competitor questions, screenshots, icons, branding, or paid content.

## Agent output requirement

When completing a task that used research docs, include a short note in the final response or PR summary:

```text
Research checked:
- docs/...

Decisions applied:
- ...
```
