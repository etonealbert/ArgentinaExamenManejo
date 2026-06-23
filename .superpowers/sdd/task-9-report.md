# Task 9 Report: Feature ViewModels And Simple Compose Screens

Status: DONE_WITH_CONCERNS

## Summary

Implemented Task 9 feature ViewModels, UI contracts, simple Compose screens, design components, Koin ViewModel bindings, and replaced the generated `App()` sample with a simple Koin-backed route switch over `AppRoute`.

Exam flow starts a local SQLDelight-backed session through existing use cases, submits selected answers, finishes the exam, and navigates to `Result(examSessionId)`. Result, review, and history read persisted results/snapshots through existing use cases.

## Files Changed

- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/App.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/di/AppModules.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/design/ExamenManejoTheme.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/design/AnswerOptionCard.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/onboarding/OnboardingUiState.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/onboarding/OnboardingUiEvent.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/onboarding/OnboardingUiEffect.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/onboarding/OnboardingViewModel.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/onboarding/OnboardingScreen.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/home/HomeUiState.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/home/HomeUiEvent.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/home/HomeUiEffect.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/home/HomeViewModel.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/home/HomeScreen.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/study/StudyUiState.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/study/StudyUiEvent.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/study/StudyUiEffect.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/study/StudyViewModel.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/study/StudyScreen.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamUiState.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamUiEvent.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamUiEffect.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamViewModel.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamScreen.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/result/ResultUiState.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/result/ResultUiEvent.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/result/ResultUiEffect.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/result/ResultViewModel.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/result/ResultScreen.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/review/ReviewUiState.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/review/ReviewUiEvent.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/review/ReviewUiEffect.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/review/ReviewViewModel.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/review/ReviewScreen.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/history/HistoryUiState.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/history/HistoryUiEvent.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/history/HistoryUiEffect.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/history/HistoryViewModel.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/history/HistoryScreen.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/settings/SettingsUiState.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/settings/SettingsUiEvent.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/settings/SettingsUiEffect.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/settings/SettingsViewModel.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/settings/SettingsScreen.kt`
- `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/onboarding/OnboardingViewModelTest.kt`
- `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/home/HomeViewModelTest.kt`
- `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamViewModelTest.kt`
- `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/result/ResultViewModelTest.kt`

## Commands Run

- `./gradlew.bat :shared:compileAndroidHostTest`
  - Result: FAILED as expected for RED step.
  - Key errors: unresolved references for `ExamViewModel`, `ExamUiEvent`, `ExamUiEffect`, `HomeViewModel`, `HomeUiEvent`, `HomeUiEffect`, `OnboardingViewModel`, `OnboardingUiEvent`, `OnboardingUiEffect`, `ResultViewModel`, `ResultUiEvent`, and `ResultUiEffect`.
- `./gradlew.bat :shared:compileAndroidHostTest`
  - Result: BUILD SUCCESSFUL in 13s.
  - Warnings: existing expect/actual beta warnings and one existing test warning about redundant `Json` creation.
- `./gradlew.bat :shared:androidHostTest --tests "*ExamViewModelTest"`
  - Result: FAILED because task `androidHostTest` does not exist in project `:shared`.
- `./gradlew.bat :shared:tasks --all | findstr /I "HostTest UnitTest android test"`
  - Result: found `testAndroidHostTest` and `compileAndroidHostTest` tasks.
- `./gradlew.bat :shared:testAndroidHostTest --tests "*ExamViewModelTest"`
  - Result: FAILED with known host test execution blocker.
  - Key error: `Error: Could not find or load main class VS` / `java.lang.ClassNotFoundException: VS`.
- `./gradlew.bat :shared:compileKotlinMetadata :androidApp:assembleDebug`
  - Result: BUILD SUCCESSFUL in 14s.
  - Note: `:shared:compileKotlinMetadata` was SKIPPED by Gradle; `:androidApp:assembleDebug` completed successfully.
- `git diff -- shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/App.kt shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/design`
  - Result: completed. Output showed the tracked `App.kt` replacement; new feature/design files are untracked in this checkout until added by git. Git also emitted the existing LF-to-CRLF warning for `App.kt`.
- `git status --short`
  - Result: completed. The worktree already contains many untracked Task 1-8 files/reports; Task 9 files are among the untracked `feature/`, `core/design/`, and `commonTest/feature/` paths.
- `./gradlew.bat :shared:compileAndroidHostTest :shared:compileKotlinMetadata :androidApp:assembleDebug`
  - Result: BUILD SUCCESSFUL in 3s.
  - Note: `:shared:compileAndroidHostTest` was loaded FROM-CACHE, `:shared:compileKotlinMetadata` was SKIPPED by Gradle, and `:androidApp:assembleDebug` was UP-TO-DATE.

## Concerns

- Host test execution remains blocked by the known Windows `Could not find or load main class VS` issue. The fallback `compileAndroidHostTest` passes.
- Several prior-task files appear untracked in this checkout. I did not revert or modify unrelated work.
- `HistoryViewModel` presents newest first by descending `sessionId` because the current `ExamResult` model does not expose a completion timestamp.
- No domain models were changed.

## Fix After Review

Files changed:

- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/App.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/di/AppModules.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/history/HistoryViewModel.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/study/StudyScreen.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamScreen.kt`
- `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/history/HistoryViewModelTest.kt`
- `.superpowers/sdd/task-9-report.md`

Changes made:

- Removed the extra `HistoryViewModel` descending `sessionId` sort so the UI preserves `GetExamHistoryUseCase` / repository ordering.
- Added `HistoryViewModelTest.preservesHistoryUseCaseOrdering` to cover repository/use-case order preservation.
- Replaced manual `remember { ViewModel(...) }` and regular `koinInject` ViewModel retrieval in `AppRouteHost` with `koinViewModel`.
- Added Koin `viewModel` bindings for all Task 9 ViewModels, including primitive route parameters via Koin parameters for `StudyViewModel`, `ExamViewModel`, `ResultViewModel`, and `ReviewViewModel`.
- Added route-parameter-specific Koin ViewModel keys while keeping route/effect arguments primitive-only.
- Made `StudyScreen` and `ExamScreen` vertically scrollable.
- Removed `@Preview` from the Koin-dependent root `App()`.

Commands run:

- `./gradlew.bat :shared:androidHostTest --tests com.etonealbert.examenmanejo.feature.history.HistoryViewModelTest`
  - Result: FAILED because task `androidHostTest` does not exist in project `:shared`.
- `./gradlew.bat :shared:tasks --all`
  - Result: BUILD SUCCESSFUL; confirmed available tasks include `testAndroidHostTest` and `compileAndroidHostTest`.
- `./gradlew.bat :shared:testAndroidHostTest --tests com.etonealbert.examenmanejo.feature.history.HistoryViewModelTest`
  - Result before production fix: FAILED with known host test execution blocker after compilation.
  - Key error: `Error: Could not find or load main class VS` / `java.lang.ClassNotFoundException: VS`.
- `./gradlew.bat :shared:testAndroidHostTest --tests com.etonealbert.examenmanejo.feature.history.HistoryViewModelTest`
  - Result after production fix: FAILED with the same known host test execution blocker after compilation.
  - Key error: `Error: Could not find or load main class VS` / `java.lang.ClassNotFoundException: VS`.
- `./gradlew.bat :shared:compileAndroidHostTest :shared:compileKotlinMetadata :androidApp:assembleDebug`
  - Result: BUILD SUCCESSFUL in 5s.
  - Note: `:shared:compileKotlinMetadata` was SKIPPED by Gradle; `:shared:compileAndroidHostTest` and `:androidApp:assembleDebug` completed successfully.
- `./gradlew.bat :shared:compileAndroidHostTest :shared:compileKotlinMetadata :androidApp:assembleDebug`
  - Result: BUILD SUCCESSFUL in 1s.
  - Note: final fresh verification rerun; `:shared:compileKotlinMetadata` was SKIPPED by Gradle, `:shared:compileAndroidHostTest` and `:androidApp:assembleDebug` were UP-TO-DATE.
- `git diff -- shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/App.kt shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/di/AppModules.kt shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/history/HistoryViewModel.kt shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/study/StudyScreen.kt shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamScreen.kt shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/history/HistoryViewModelTest.kt .superpowers/sdd/task-9-report.md`
  - Result: completed; only tracked `App.kt` appeared in this diff because Task 9 feature/report paths are untracked in this checkout.
- `git status --short`
  - Result: completed; existing prior-task untracked files remain present. I did not commit or revert unrelated work.

Remaining concerns:

- Targeted host test execution is still blocked by the known Windows `Could not find or load main class VS` issue, so the new test was compiled but not executed in this environment.
- The earlier concern about `HistoryViewModel` sorting by `sessionId` is resolved by preserving use-case ordering.

## Fix After Re-review

Files changed:

- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/home/HomeViewModel.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/study/StudyViewModel.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/history/HistoryViewModel.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/review/ReviewViewModel.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamViewModel.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/onboarding/OnboardingViewModel.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/settings/SettingsViewModel.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/result/ResultViewModel.kt`
- `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/EffectFlowTestUtils.kt`
- `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/home/HomeViewModelTest.kt`
- `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamViewModelTest.kt`
- `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/onboarding/OnboardingViewModelTest.kt`
- `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/result/ResultViewModelTest.kt`
- `.superpowers/sdd/task-9-report.md`

Changes made:

- Replaced every Task 9 feature ViewModel `MutableSharedFlow<...>(replay = 1)` one-shot effect flow with default `MutableSharedFlow<...>()`, preventing stale navigation effects from replaying to new collectors.
- Updated replay-dependent ViewModel tests to collect effects concurrently and added assertions that a new collector does not receive an old effect.
- Confirmed no remaining `replay = 1` or `replayCache` usages under `shared/src`.

Commands run:

- `./gradlew.bat :shared:androidHostTest --tests "*.HomeViewModelTest" --tests "*.ExamViewModelTest" --tests "*.OnboardingViewModelTest" --tests "*.ResultViewModelTest"`
  - Result: FAILED because task `androidHostTest` does not exist in project `:shared`.
- `./gradlew.bat :shared:tasks --all`
  - Result: BUILD SUCCESSFUL; confirmed available tasks include `testAndroidHostTest` and `compileAndroidHostTest`.
- `./gradlew.bat :shared:testAndroidHostTest --tests "*.HomeViewModelTest" --tests "*.ExamViewModelTest" --tests "*.OnboardingViewModelTest" --tests "*.ResultViewModelTest"`
  - Result: FAILED with known host test execution blocker.
  - Key error: `Error: Could not find or load main class VS` / `java.lang.ClassNotFoundException: VS`.
- `./gradlew.bat :shared:compileAndroidHostTest :shared:compileKotlinMetadata :androidApp:assembleDebug`
  - Result: BUILD SUCCESSFUL in 12s.
  - Note: `:shared:compileKotlinMetadata` was SKIPPED by Gradle; `:shared:compileAndroidHostTest` and `:androidApp:assembleDebug` completed successfully.
- `./gradlew.bat :shared:compileAndroidHostTest :shared:compileKotlinMetadata :androidApp:assembleDebug`
  - Result: BUILD SUCCESSFUL in 1s.
  - Note: final rerun after report update; `:shared:compileKotlinMetadata` was SKIPPED by Gradle, `:shared:compileAndroidHostTest` and `:androidApp:assembleDebug` were UP-TO-DATE.

Remaining concerns:

- Targeted host test execution remains blocked by the known Windows `Could not find or load main class VS` issue, so the updated effect tests were compiled but not executed in this environment.
