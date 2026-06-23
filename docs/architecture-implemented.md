# Implemented Architecture

## Dependency Direction

The implemented vertical slice follows this direction:

```text
presentation -> domain <- data
core navigation/DI wires dependencies at the app boundary
platform source sets provide platform-specific SQLDelight drivers
```

Domain owns app concepts and repository interfaces. Data implements those interfaces using SQLDelight and seed resources. Presentation consumes use cases and coordinators through Koin. Platform APIs are isolated to platform source sets such as Android and iOS driver factories.

The domain package does not import Compose, SQLDelight, Ktor, Koin, Android APIs, or iOS APIs. It depends on domain models, repository interfaces, and small app-level abstractions such as `Clock`.

## Boundaries

### Domain

`shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain` contains:

- Models for questions, license classes, answer options, exam config, snapshots, answers, result, seed import result, settings/progress summaries, and placeholder subscription entitlement.
- Repository interfaces for license classes, questions, question packs, exams, settings, stats, and subscription entitlement.
- Use cases for seed import checks, Class B content reads, exam lifecycle, result calculation, review, history, and stats.

Domain does not know about Compose screens, SQLDelight generated row types, Koin modules, Ktor clients, Android contexts, or iOS classes.

### Data

`shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data` contains:

- SQLDelight database provider and local data sources.
- Seed DTOs and the bundled seed provider, including SHA-256 checksum resource validation of the JSON bytes before import.
- Repository implementations that satisfy domain repository interfaces.
- Mappers between SQLDelight rows and domain models.

SQLDelight is the runtime source of truth after the bundled seed pack is imported. The JSON pack under `composeResources/files/question_packs` is seed input only.

### Presentation

`shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature` contains Compose screens, view models, UI states, UI events, UI effects, and simple feature coordinators. View models call domain use cases and emit primitive route effects through coordinators/navigator abstractions.

`App.kt` contains the Compose route host that injects view models with Koin, passes primitive IDs into parameterized view models, uses a route-scoped `ViewModelStoreOwner` that clears when the route leaves composition, and displays unsupported MVP placeholder screens for non-MVP routes.

## Repository And Mapper Responsibilities

Repositories are the boundary between domain use cases and persistence or seed details:

- `QuestionPackRepositoryImpl` reads bundled seed input after checksum validation, checks existing pack hash/version using the actual JSON SHA-256, logs imports, imports content transactionally, and converts seed read/checksum/decode/import failures into `ImportResult.Failed`.
- `LicenseClassRepositoryImpl` exposes enabled license class data from SQLDelight.
- `QuestionRepositoryImpl` exposes active questions and their options/category/source data from SQLDelight.
- `ExamRepositoryImpl` creates exam sessions with immutable snapshots, records answer snapshots, completes exams, and reads result/review/history data.
- `SettingsRepositoryImpl` stores onboarding and user setting values locally.
- `StatsRepositoryImpl` derives user stats from local data.
- `FakeSubscriptionRepository` keeps the MVP offline and avoids real subscription integration.

Mappers in `data/mapper/DatabaseMappers.kt` convert generated SQLDelight rows to domain models, convert booleans to database integer flags, create exam question snapshots from current questions, and serialize/deserialize snapshot options JSON. Snapshot mapping keeps result, review, and history independent from later mutable question content changes.

## Koin Modules

`appModule` in `core/di/AppModules.kt` registers:

- `DatabaseProvider` and `ExamenManejoDatabase`.
- Common services such as `Clock` and `SeedQuestionPackProvider`.
- Local data sources.
- Domain repository implementations.
- Domain use cases.
- `NoOpQuestionPackApi` for the offline MVP question-pack API boundary.
- Navigator and coordinators.
- View models, including parameterized view models for `licenseClassId` and `examSessionId`.

`platformModule` is an expected Koin module. Android provides `DriverFactory(Context)`, and iOS provides `DriverFactory()`.

## Coordinator Route Rules

Routes are defined in `core/navigation/AppRoute.kt`. Route arguments are primitive IDs only:

- `Study(licenseClassId: String)`
- `Exam(licenseClassId: String)`
- `Result(examSessionId: Long)`
- `Review(examSessionId: Long)`
- `Paywall(source: String)`
- `Legal(documentId: String)`
- `Tutorial(source: String)`

No full domain objects are passed through routes. Screens and view models reload needed state from repositories/use cases by ID.

`RootCoordinator.start()` always performs the seed import check before routing. Failed seed import replaces the route with no-argument `StartupError` and does not continue to onboarding or home. Successful or already-imported seed checks continue to onboarding completion and replace the route with either onboarding or home.

Feature coordinators and UI effects navigate by emitting `AppRoute` values. `AppRouteHost` wires those route effects into the shared navigator.

## Why `minSdk=24` Is Retained

Android `minSdk` remains `24` through `gradle/libs.versions.toml` and both Android build files read that shared version. This preserves the existing platform reach while supporting the current Kotlin Multiplatform, Compose, SQLDelight, and Android application configuration. Task 10 makes documentation-only changes and intentionally does not broaden or narrow Android platform support.
