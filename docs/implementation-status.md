# Implementation Status

Date: 2026-06-23  
Host: Windows

## Implemented Architecture

The MVP vertical slice is implemented in the shared Kotlin Multiplatform module with these architecture areas:

- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/model`: offline domain models for license classes, questions, exam sessions, snapshots, results, seed import result, progress, settings, and subscription entitlement placeholders.
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/repository`: repository interfaces consumed by use cases.
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/usecase`: use cases for seed import, onboarding routing support, Class B question retrieval, exam start, answer submission, finish/result calculation, review, history, and stats.
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/local`: SQLDelight-backed local data sources and database provider.
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/local/seed`: bundled seed JSON DTOs and resource reader.
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/repository`: repository implementations backed by local data sources, plus MVP fake subscription entitlement.
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/mapper`: SQLDelight row to domain mappers and exam snapshot JSON serialization helpers.
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/navigation`: primitive-ID app routes, navigator, and root coordinator.
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/di`: common Koin module wiring database, repositories, use cases, coordinators, and view models.
- `shared/src/androidMain/kotlin/com/etonealbert/examenmanejo/core/di` and `shared/src/iosMain/kotlin/com/etonealbert/examenmanejo/core/di`: platform Koin modules for SQLDelight driver factories.
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature`: Compose presentation screens, view models, UI states, UI events, UI effects, and feature coordinators for onboarding, home, study, exam, result, review, history, settings, subscription placeholder routes.
- `shared/src/commonMain/composeResources/files/question_packs`: bundled Class B demo seed JSON and checksum file.

The MVP is fully offline. There is no backend, login, real subscription, cloud sync, analytics, push notifications, admin panel, or remote question update flow in this implementation.

## SQLDelight Schema Status

SQLDelight is configured in `shared/build.gradle.kts` with database package `com.etonealbert.examenmanejo.db`, schema output under `shared/src/commonMain/sqldelight/databases`, and `verifyMigrations = true`.

Current schema files:

- `Content.sq`: question packs, content sources, jurisdictions, license classes, subclasses, categories, questions, question/license mappings, options, and import logs.
- `Exam.sq`: exam sessions, immutable question snapshots, answer snapshots, result completion state, and completed exam history query.
- `ProgressSettings.sq`: study progress and user settings.

After seed import, SQLDelight is the source of truth for app content. Bundled JSON is seed input only.

## Seed Import Behavior

App startup calls `RootCoordinator.start()`, which calls `CheckFirstLaunchSeedImportUseCase`, which delegates to `ImportSeedQuestionsUseCase`, which calls `QuestionPackRepository.importBundledSeedIfNeeded()`.

`SeedQuestionPackProvider` reads `argentina-class-b-demo-0001.json` and `argentina-class-b-demo-0001.sha256`. The checksum file is authoritative for the bundled JSON content and must match the actual SHA-256 of the JSON bytes before import proceeds; the repository stores that checksum for idempotency rather than trusting the DTO's internal `hash` placeholder.

`QuestionPackRepositoryImpl` reads the validated pack through `SeedQuestionPackProvider`, checks the stored `question_pack` hash for the same pack ID and version, and returns `AlreadyImported` when the stored hash matches. If the hash differs or is absent, it writes an `import_log` row with `STARTED`, imports content in a SQLDelight transaction, writes/updates the `question_pack` row, and finishes the log as `IMPORTED` or `FAILED`. Seed read, checksum, JSON decode, and import failures are converted to `ImportResult.Failed` so startup can route to the error screen.

If startup seed import returns `ImportResult.Failed`, `RootCoordinator.start()` routes to no-argument `AppRoute.StartupError` and does not continue to onboarding or home with missing or broken seed content.

The bundled JSON is not used as the runtime source after import. Runtime question, exam, review, result, and history reads come from SQLDelight.

## Class B Demo Disclaimer

The bundled Class B content is demo-only and non-official. Seed questions are marked with `contentStatus = "DEMO_UNVERIFIED"`, `reviewStatus = "APPROVED"`, and `questionStatus = "ACTIVE"` only to validate the offline MVP flow.

This content must not be represented as official exam material.

## Verification Results

Commands were run on Windows from the repository root.

### `./gradlew.bat :shared:compileKotlinMetadata`

Result: PASS

Summary:

```text
BUILD SUCCESSFUL in 1s
7 actionable tasks: 1 executed, 6 up-to-date
Configuration cache entry reused.
```

### `./gradlew.bat :shared:testAndroidHostTest`

Result: FAIL, known pre-existing `VS` blocker still present.

Failing task and error summary:

```text
> Task :shared:testAndroidHostTest FAILED
Error: Could not find or load main class VS
Caused by: java.lang.ClassNotFoundException: VS

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':shared:testAndroidHostTest'.
> Test process encountered an unexpected problem.
   > Process 'Gradle Test Executor 77' finished with non-zero exit value 1

BUILD FAILED in 3s
33 actionable tasks: 3 executed, 1 from cache, 29 up-to-date
Configuration cache entry reused.
```

The full tool output also showed repeated `Could not write standard input to Gradle Test Executor ... java.io.IOException: The pipe is being closed` messages after the `VS` class-load failures.

### `./gradlew.bat :androidApp:assembleDebug`

Result: PASS

Relevant host warning:

```text
Native task 'iosSimulatorArm64Test' is disabled
Task 'iosSimulatorArm64Test' for target 'ios_simulator_arm64' cannot run on the current host (windows-x86_64).
Reason: simulator tests require macOS
```

Summary:

```text
BUILD SUCCESSFUL in 1s
58 actionable tasks: 1 executed, 57 up-to-date
Configuration cache entry stored.
```

## Known Blocked Items

- iOS simulator tests require macOS and were not run on this Windows host.
- `:shared:testAndroidHostTest` is blocked by the existing `VS` test JVM classpath/main-class issue: `Could not find or load main class VS`, `Caused by: java.lang.ClassNotFoundException: VS`.
- No backend, login, real subscription, cloud sync, analytics, push notifications, admin panel, or remote question updates are implemented for MVP.
