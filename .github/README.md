# ExamenManejo Agent Workspace

This `.github` folder gives GitHub Copilot / coding agents a stable project brief, architecture rules, prompts, and implementation checklists for the **ExamenManejo** Kotlin Multiplatform app.

## Project summary

**ExamenManejo** is an offline-first Argentina driving-license theory training app for Android and iOS.

Core technical direction:

- Kotlin Multiplatform.
- Compose Multiplatform shared UI where practical.
- Current repository shape: `androidApp/`, `iosApp/`, `shared/`.
- Architecture: Clean Architecture-lite + MVVM + Unidirectional Data Flow + Repository pattern.
- Navigation: Coordinator pattern over typed routes and feature flows.
- Persistence: SQLDelight local-first database.
- Async/state: Coroutines, Flow, StateFlow.
- Dependency injection: Koin.
- Networking later: Ktor client and Ktor backend.
- MVP: no backend, no login, no subscription, fully offline.

## How agents should use this folder

Start with:

1. `.github/copilot-instructions.md`
2. `.github/instructions/00-project-context.instructions.md`
3. `.github/instructions/01-architecture.instructions.md`
4. `.github/instructions/02-navigation-coordinator.instructions.md`
5. `.github/instructions/09-research-docs.instructions.md`
6. `.github/tools/research-docs-index.md`
7. `.github/tools/definition-of-done.md`

For feature work, use the prompt files in `.github/prompts/`.

## Research docs expected in the repository

The implementation should be aligned with the research files in `/docs`, especially:

- `docs/00-product-mvp-research.md`
- `docs/01-platform-support-research.md.md`
- `docs/02-architecture-decision-record.md`
- `docs/03-navigation-coordinator-research.md.md`
- `docs/04-argentina-data-source-research.md`
- `docs/05-content-taxonomy-exam-model.md`
- `docs/06-data-parsing-import-pipeline.md`
- `docs/07-local-database-sqldelight-research.md`
- `docs/08-exam-training-feature-research.md`
- `docs/09-compose-ui-ux-research.md`

When research and code conflict, do not guess. Stop and propose a small architecture decision update.



## Research docs access policy

This `.github` folder assumes the repository root also contains a `docs/` folder with the research papers generated for this app. Agents should read the relevant `docs/` files before implementation, especially for architecture, navigation, platform targets, SQLDelight schema, Argentina exam data, parsing/import, exam features, and UI/UX.

Use `.github/instructions/09-research-docs.instructions.md` and `.github/tools/research-docs-index.md` as the quick map for which research file to inspect.

When an agent completes a non-trivial task, it should mention which research docs were checked and which decisions were applied.

## MVP rule

Build a working local vertical slice first:

```text
Class B -> local seed questions -> study mode -> exam simulation -> result -> review -> history
```

Do not start backend, subscriptions, login, analytics, remote sync, or admin tools until the local MVP flow works on Android and iOS.
