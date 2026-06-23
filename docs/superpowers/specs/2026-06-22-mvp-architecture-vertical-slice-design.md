# ExamenManejo MVP Architecture And Class B Vertical Slice Design

Date: 2026-06-22

## Context Checked

Repository context:

- Modules: `:shared` and `:androidApp`; `iosApp/` exists as the Xcode entry point.
- Shared UI: Compose Multiplatform is already configured in `shared`.
- Existing app code: generated Compose sample plus platform expect/actual files.
- Missing stack pieces: SQLDelight, Koin, Ktor, kotlinx serialization, and explicit coroutines dependencies are not yet configured in the version catalog.
- Current Android platform config: `compileSdk=36`, `targetSdk=36`, `minSdk=24`.

Research and instruction context checked:

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
- `.github/README.md`
- `.github/copilot-instructions.md`
- `.github/instructions/*`
- `.github/tools/*`
- Relevant `.github/prompts/*` for kickoff, Class B vertical slice, SQLDelight, navigation, exam, result/review/history, and tests.

## Approved Approach

Use approach 1: implement the full local-first MVP architecture and Class B vertical slice in the existing `shared` module.

The implementation should be minimal but real:

- Use package boundaries inside `shared`; do not add new Gradle modules.
- Keep Android `minSdk=24`; document the platform-target mismatch instead of changing it.
- Do not attempt iOS simulator tests on Windows.
- Keep the MVP fully offline and local-data-first.
- Do not add backend, login, subscriptions, cloud sync, analytics, push notifications, admin tooling, or remote question updates.

## Architecture Design

Use Clean Architecture-lite, MVVM, Unidirectional Data Flow, repository pattern, and coordinator navigation.

Dependency direction:

```text
UI / Presentation
-> ViewModel
-> UseCase
-> Repository interface
-> Repository implementation
-> Local data source / SQLDelight / seed data
```

Domain rules:

- Domain models, repository interfaces, and use cases live under `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain`.
- Domain must not import Compose, SQLDelight, Ktor, Koin, Android APIs, or iOS APIs.
- Exam scoring, selection rules, and result mapping belong in domain/use cases or domain services, not Composables.

Data rules:

- Data implementations live under `data/local`, `data/local/seed`, `data/repository`, and `data/mapper`.
- SQLDelight generated row types must not leak into domain or UI.
- Repository implementations map SQLDelight rows into domain models.

Presentation rules:

- Feature packages live under `feature/onboarding`, `feature/home`, `feature/study`, `feature/exam`, `feature/result`, `feature/review`, `feature/history`, and `feature/settings`.
- Each feature uses separate `Screen`, `ViewModel`, `UiState`, `UiEvent`, and `UiEffect` files where applicable.
- Screens stay mostly stateless.
- ViewModels expose immutable `StateFlow<UiState>` and one-time effects via `SharedFlow` or equivalent.
- ViewModels do not know `NavController`.
- Composables do not call repositories or SQLDelight directly.

## Package Structure

Use the existing base package `com.etonealbert.examenmanejo`.

Target structure:

```text
shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/
  App.kt
  core/
    di/
    navigation/
    design/
    result/
    platform/
    time/
  feature/
    onboarding/
    home/
    study/
    exam/
    result/
    review/
    history/
    settings/
    subscription/
  domain/
    model/
    repository/
    usecase/
  data/
    local/
    local/seed/
    repository/
    mapper/
  network/
```

Platform-specific code remains behind source-set boundaries:

```text
shared/src/androidMain/kotlin/...  # Android SQLDelight driver, Android Koin entry support if needed
shared/src/iosMain/kotlin/...      # iOS SQLDelight driver, iOS entry support if needed
```

## Local Storage And Seed Import Design

SQLDelight is the source of truth after import.

The bundled JSON pack is only seed input, not runtime data. Runtime reads go through SQLDelight repositories.

Add SQLDelight carefully to `shared`:

- SQLDelight plugin and dependencies.
- Android driver.
- Native/iOS driver.
- Coroutines extensions.
- `DriverFactory` expect/actual.
- Database provider.

Content versioning tables:

```text
question_pack
question
question_option
content_source
import_log
```

User data tables:

```text
exam_session
exam_session_question_snapshot
exam_session_answer
study_progress
user_settings
```

Onboarding and MVP app state persist in `user_settings`; do not add DataStore for this MVP. Required settings include:

```text
has_completed_onboarding
selected_jurisdiction
seed_import_version
```

Import requirements:

- Seed import is idempotent.
- Idempotency uses `question_pack_id`, version, and hash.
- Import writes source metadata, jurisdictions, license classes/subclasses, categories, questions, options, and mappings in one transaction.
- Import never deletes user progress or history.
- If import fails, return a clear `ImportResult.Failed(reason)` and do not mark the pack as imported.
- Demo content is clearly marked as demo and unverified.
- Every question has source, jurisdiction, content status, review status, and version metadata from day one.
- Future Ktor API interfaces can exist, but Koin binds fake/local implementations for MVP.

Use this import result contract:

```kotlin
sealed interface ImportResult {
    data object AlreadyImported : ImportResult
    data class Imported(val questionCount: Int) : ImportResult
    data class Failed(val reason: String) : ImportResult
}
```

Exam snapshot requirements:

- Exam session is created before answering.
- Question order and displayed option order are snapshotted at exam start.
- Result calculation uses snapshots and answers, not mutable live content tables.
- Review/history never depend on mutable question content.
- Snapshot data stores:
  - `question_id`
  - `question_text_snapshot`
  - `category_snapshot`
  - `explanation_snapshot`
  - option text snapshots or options JSON
  - `correct_option_snapshot`
  - `selected_option_snapshot` in answers

## MVP Vertical Slice Flow

Approved flow:

```text
App start
-> seed import check
-> onboarding status check
-> onboarding or home
-> Class B
-> start exam
-> answer questions
-> finish
-> result
-> review
-> history
```

Functional requirements:

- `CheckFirstLaunchSeedImportUseCase` runs before or during root app startup.
- `RootCoordinator` performs seed import check, onboarding status check, then routes to `Onboarding` or `Home`.
- `HomeViewModel` loads license classes and exposes Class B.
- `ExamConfig` defines exam rules instead of hardcoding them in exam use cases.
- `StartExamUseCase` selects active Class B questions from local storage and creates an exam session plus question snapshots.
- `SubmitExamAnswerUseCase` writes or updates `exam_session_answer`.
- `FinishExamUseCase` calculates deterministic result and completes the session.
- `GetExamResultUseCase`, `GetReviewAnswersUseCase`, and `GetExamHistoryUseCase` read from local snapshot/history tables.
- Study mode can load Class B questions and show answer feedback, but the first complete persistence path focuses on exam/result/review/history.
- Fallback fake repositories are allowed only to keep UI compiling if SQLDelight setup is temporarily blocked, not as final storage.

## Navigation Design

Use typed routes with primitive-only arguments.

Routes:

```text
Onboarding
Home
Study(licenseClassId: String)
Exam(licenseClassId: String)
Result(examSessionId: Long)
Review(examSessionId: Long)
History
Settings
Paywall(source: String)        # future/fake MVP route only
Legal(documentId: String)      # future/global route
Tutorial(source: String)       # future/global route
```

Rules:

- Route arguments are IDs/primitives only: `String`, `Long`, or equivalent simple values.
- Do not pass full domain objects through routes.
- ViewModels emit navigation effects.
- Screens or coordinator wiring translates effects into navigator actions.
- Coordinators do not contain business logic.

Coordinator responsibilities:

- `RootCoordinator`: app start, seed import check, onboarding status check, home/onboarding routing, settings, history, legal, tutorial, future global paywall entry points.
- `ExamCoordinator`: `Home/Class B -> Exam -> Result -> Review -> Result/Home -> History`, retry exam, close flow.
- `StudyCoordinator`: future-ready study/category flows.
- `SubscriptionCoordinator`: fake/future-ready only; no billing or real entitlement logic.

Global UI rule:

- Paywall, Legal, Tutorial, and Settings must be globally reachable.
- Billing/legal/subscription logic remains fake or future-ready for MVP.

## UI Design

Replace the generated sample `App()` with app root wiring and simple real screens.

Screens:

- Onboarding
- Home / license class selection
- Study
- Exam
- Result
- Review answers
- History
- Settings

UI rules:

- Use friendly, clean, educational Spanish-first copy.
- Use card-based license selection.
- Use large readable question text.
- Use clear answer options and visible progress.
- Use explicit pass/fail result states.
- Use large tap targets.
- Use accessible answer states that do not rely only on color.
- Respect iOS safe areas and Android edge-to-edge/insets.
- Handle loading, empty, and error states.
- Keep user-facing strings centralized where practical without over-engineering a full localization system in the first pass.

## Domain Models

Create each meaningful domain model in its own file:

- `LicenseClass`
- `LicenseSubclass`
- `Jurisdiction`
- `ExamConfig`
- `Question`
- `AnswerOption`
- `QuestionCategory`
- `QuestionSource`
- `QuestionStatus`
- `ContentStatus`
- `ReviewStatus`
- `ExamSession`
- `ExamQuestionSnapshot`
- `ExamOptionSnapshot`
- `ExamAnswer`
- `ExamResult`
- `UserStats`
- `StudyProgress`
- `SubscriptionEntitlement`

## Repository Interfaces

Create each repository interface in its own file:

- `LicenseClassRepository`
- `QuestionRepository`
- `ExamRepository`
- `StatsRepository`
- `SettingsRepository`
- `SubscriptionRepository`
- `QuestionPackRepository`

MVP binds `SubscriptionRepository` to `FakeSubscriptionRepository`.

## Use Cases

Create each use case in its own file:

- `GetLicenseClassesUseCase`
- `GetQuestionsByLicenseClassUseCase`
- `StartExamUseCase`
- `SubmitExamAnswerUseCase`
- `FinishExamUseCase`
- `GetExamResultUseCase`
- `GetExamHistoryUseCase`
- `GetReviewAnswersUseCase`
- `GetUserStatsUseCase`
- `ImportSeedQuestionsUseCase`
- `CheckFirstLaunchSeedImportUseCase`

MVP exam config:

```kotlin
data class ExamConfig(
    val licenseClassId: String,
    val questionCount: Int,
    val passingPercentage: Int,
    val timeLimitMinutes: Int?,
)
```

For the Class B demo exam, use the small demo pack count, `passingPercentage = 70`, and `timeLimitMinutes = null`, documenting these as placeholder values until verified jurisdiction rules replace demo content.

## Dependency Injection And Network Placeholders

Use Koin modules for:

- database
- local data sources
- repositories
- use cases
- ViewModels
- coordinators
- fake subscription
- future Ktor client placeholder

Network design:

- Define future API-facing interfaces under `network`.
- Do not perform real network calls.
- Do not require network permission or remote configuration for MVP.
- Koin binds local/fake implementations for MVP.

## Demo Content Rules

The seed pack may contain a tiny number of Class B demo questions sufficient to test the flow.

Rules:

- Do not claim demo questions are official.
- Do not copy competitor or unapproved official question banks.
- Mark demo questions as demo/unverified.
- Include source/status fields and jurisdiction/license-class metadata.
- Include a non-affiliation and demo-content disclaimer in settings/docs.

## Testing Design

Prioritize pure `commonTest` domain/use-case tests.

Add tests for:

- exam scoring and pass/fail thresholds;
- unanswered questions counted as incorrect;
- question selection returning only active Class B demo questions;
- seed import idempotency where SQLDelight test setup supports it;
- review/history mapping from snapshots;
- ViewModel state transitions where lightweight fakes are practical.

If SQLDelight in-memory or host test setup is disruptive under this AGP/KMP configuration:

- Keep pure domain/use-case tests.
- Document the blocked SQLDelight integration tests clearly in implementation docs.
- Do not block the architecture skeleton on heavy test infrastructure.

## Documentation Design

Do not skip docs updates.

Add or update:

- `docs/implementation-status.md`
- `docs/architecture-implemented.md`
- `docs/mvp-vertical-slice.md`

Docs must explain:

- what was implemented;
- where files are located;
- what remains to do;
- known risks;
- demo-content disclaimer;
- platform-target note;
- how to run/build/test.

Root `README.md` may be updated only if useful and safe.

## Verification Design

Run available Windows-compatible Gradle checks:

```powershell
.\gradlew.bat :shared:compileKotlinMetadata
.\gradlew.bat :shared:testAndroidHostTest
.\gradlew.bat :androidApp:assembleDebug
```

Do not attempt iOS simulator tests on Windows. Document that `:shared:iosSimulatorArm64Test` requires macOS.

## Known Risks

- Adding SQLDelight/Koin/Ktor dependencies may expose compatibility issues with Kotlin `2.4.0`, AGP `9.0.1`, and Compose Multiplatform `1.11.1`.
- SQLDelight host/in-memory tests may require additional driver setup that could be disruptive; pure tests are preferred if needed.
- Demo questions are not official and must not be marketed as official content.
- The docs target iOS 26+ and latest two Android majors, but current Gradle uses `minSdk=24`; keep this as an intentional MVP compatibility decision until a separate platform ADR changes it.
- iOS verification cannot be completed on this Windows host.

## Out Of Scope

- Real backend.
- Real subscription or billing.
- Login or account creation.
- Cloud sync.
- Analytics.
- Push notifications.
- Admin panel.
- Remote question updates.
- Large official content ingestion.
- Platform target/minSdk changes.
