# Copilot Instructions — ExamenManejo

You are working on **ExamenManejo**, a Kotlin Multiplatform app for Argentina driving-license theory exam training.

## Non-negotiable project constraints

- MVP is offline-first.
- Do not require backend, login, account creation, subscriptions, remote config, cloud sync, or analytics for MVP.
- Use Kotlin Multiplatform with common business logic.
- Prefer Compose Multiplatform shared UI where the current project setup supports it.
- Current project folders are `shared/`, `androidApp/`, and `iosApp/`. Do not rename modules unless explicitly asked.
- Target platform intent: iOS 26+ and the latest two stable Android major versions. Verify exact SDK/API values before changing Gradle/Xcode settings.
- Store all app data locally through SQLDelight.
- Use Ktor interfaces only as future placeholders until backend work is explicitly requested.
- Use Koin for dependency wiring.
- Use Coroutines, Flow, and StateFlow for async state.
- Use Clean Architecture-lite, MVVM, UDF, Repository pattern, and Coordinator pattern.



## Research docs are required context

The repository contains a `docs/` folder with the research papers for this app. Before making non-trivial changes, read the relevant research file(s). At minimum:

- Product/MVP work: `docs/00-product-mvp-research.md`
- Platform targets/build settings: `docs/01-platform-support-research.md.md`
- Architecture/package/layer changes: `docs/02-architecture-decision-record.md`
- Navigation/coordinator work: `docs/03-navigation-coordinator-research.md.md`
- Argentina content/data sourcing: `docs/04-argentina-data-source-research.md`
- Taxonomy/question model: `docs/05-content-taxonomy-exam-model.md`
- Parsing/import/question packs: `docs/06-data-parsing-import-pipeline.md`
- SQLDelight/database: `docs/07-local-database-sqldelight-research.md`
- Exam/study/result/history/stats: `docs/08-exam-training-feature-research.md`
- UI/UX/accessibility: `docs/09-compose-ui-ux-research.md`

Do not silently contradict `/docs`. If a requested implementation conflicts with research, explain the conflict and propose a small decision update.

## Code organization

Place shared app logic under `shared/src/commonMain/kotlin/...`.

Preferred package structure:

```text
core/
  di/
  navigation/
  design/
  platform/
  util/
feature/
  onboarding/
  home/
  study/
  exam/
  result/
  review/
  history/
  settings/
  subscription/        # future only; fake entitlement for MVP
domain/
  model/
  repository/
  usecase/
data/
  local/
  repository/
  mapper/
network/               # future client interfaces only in MVP
```

Do not put business logic inside Composables.

## Architecture rules

Dependency direction:

```text
feature/presentation -> domain -> data abstractions
                         data implementations -> SQLDelight / seed JSON / future Ktor
```

Allowed:

- ViewModel calls use cases.
- Use case calls repository interface.
- Repository implementation calls local data source / SQLDelight.
- UI observes `StateFlow<UiState>`.
- UI sends explicit events to ViewModel.
- ViewModel emits one-time effects for navigation.
- Coordinator translates effects into navigation actions.

Forbidden:

- Composable directly queries SQLDelight.
- Composable calculates exam score.
- ViewModel knows SQLDelight row classes.
- Domain layer imports Compose, SQLDelight, Ktor, Koin, or platform APIs.
- Repositories return SQLDelight generated row types to UI/domain.
- Passing full question/exam objects through navigation routes.
- Starting subscription/backend work before local MVP is complete.

## Navigation rules

Use a coordinator layer:

- `RootCoordinator` for global flows.
- `ExamCoordinator` for exam/result/review flow.
- `StudyCoordinator` for study flow.
- `SubscriptionCoordinator` later for paywall/restore/entitlements.

Routes should pass only stable IDs and primitive arguments:

```kotlin
ExamRoute(licenseClassId = "B")
ResultRoute(examSessionId = 123L)
ReviewRoute(examSessionId = 123L)
```

Do not pass full domain objects through routes.

## UI rules

- Build a clean, friendly educational UI.
- Prefer cards, large touch targets, readable question layouts, clear result states.
- Keep user-facing strings ready for Spanish localization.
- Do not copy competitor icons, images, branding, exact wording, or visual identity.
- Use accessible answer states: do not rely only on color.
- Respect iOS safe areas and Android edge-to-edge behavior.

## Content/data rules

Argentina exam content is legally sensitive.

- Prefer official sources and documented metadata.
- Do not copy third-party app content.
- Do not blindly scrape content into the app.
- Every question must have source metadata, jurisdiction, license class applicability, review status, and version.
- Prefer a verified/paraphrased question pipeline unless explicit reuse rights are confirmed.
- Keep source traceability in the local database.

## MVP vertical slice

When implementing features, prioritize this order:

1. Architecture skeleton.
2. SQLDelight schema and driver factory.
3. Seed import with a tiny verified Class B pack.
4. Home/license class selection.
5. Study mode.
6. Exam simulation.
7. Result screen.
8. Review answers.
9. History and basic stats.
10. Settings/about/legal.

## Testing expectations

For each feature, add at least one meaningful test for:

- domain/use-case logic;
- scoring or selection algorithm when touched;
- repository mapping when data layer is touched;
- ViewModel state transition when presentation logic is touched.

Do not mark work complete if Android and iOS builds are not considered.
