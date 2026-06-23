# Task 10 Report: Implementation Docs And Verification

## Status

Completed documentation and verification capture for Task 10. Task 10 changed docs/report files and `README.md` only; production implementation files shown in the final `git status` were pre-existing staged/modified files from Tasks 1-9. No commit was created.

## Files Changed

- Created `docs/implementation-status.md`.
- Created `docs/architecture-implemented.md`.
- Created `docs/mvp-vertical-slice.md`.
- Created `.superpowers/sdd/task-10-report.md`.
- Updated `README.md` with safe Windows/macOS verification command notes.

## Commands Run And Results

### `./gradlew.bat :shared:compileKotlinMetadata`

Result: PASS

```text
BUILD SUCCESSFUL in 1s
7 actionable tasks: 1 executed, 6 up-to-date
Configuration cache entry reused.
```

### `./gradlew.bat :shared:testAndroidHostTest`

Result: FAIL, known pre-existing `VS` blocker still present.

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

### `./gradlew.bat :androidApp:assembleDebug`

Result: PASS

```text
Native task 'iosSimulatorArm64Test' is disabled
Task 'iosSimulatorArm64Test' for target 'ios_simulator_arm64' cannot run on the current host (windows-x86_64).
Reason: simulator tests require macOS

BUILD SUCCESSFUL in 1s
58 actionable tasks: 1 executed, 57 up-to-date
Configuration cache entry stored.
```

### `git status --short`

Captured after creating Task 10 docs and report. This is the authoritative full worktree/index scope at the end of Task 10. Entries under `shared/src/...` are prior implementation work from Tasks 1-9 that was already present before Task 10; Task 10 added the three `docs/*.md` files, `.superpowers/sdd/task-10-report.md`, and the README command notes.

```text
 M .superpowers/sdd/progress.md
A  .superpowers/sdd/task-6-report.md
A  .superpowers/sdd/task-7-report.md
A  .superpowers/sdd/task-8-report.md
A  .superpowers/sdd/task-9-report.md
 M README.md
A  shared/src/androidMain/kotlin/com/etonealbert/examenmanejo/core/di/KoinStartup.android.kt
A  shared/src/androidMain/kotlin/com/etonealbert/examenmanejo/core/di/PlatformModules.android.kt
 M shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/App.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/design/AnswerOptionCard.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/design/ExamenManejoTheme.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/di/AppModules.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/navigation/AppNavigator.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/navigation/AppRoute.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/navigation/NavigationEffect.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/navigation/RootCoordinator.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/time/Clock.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/usecase/CheckFirstLaunchSeedImportUseCase.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/usecase/ClassBDemoExamConfig.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/usecase/FinishExamUseCase.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/usecase/GetExamHistoryUseCase.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/usecase/GetExamResultUseCase.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/usecase/GetLicenseClassesUseCase.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/usecase/GetQuestionsByLicenseClassUseCase.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/usecase/GetReviewAnswersUseCase.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/usecase/GetUserStatsUseCase.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/usecase/ImportSeedQuestionsUseCase.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/usecase/StartExamUseCase.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/usecase/SubmitExamAnswerUseCase.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamCoordinator.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamScreen.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamUiEffect.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamUiEvent.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamUiState.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamViewModel.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/history/HistoryScreen.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/history/HistoryUiEffect.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/history/HistoryUiEvent.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/history/HistoryUiState.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/history/HistoryViewModel.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/home/HomeScreen.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/home/HomeUiEffect.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/home/HomeUiEvent.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/home/HomeUiState.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/home/HomeViewModel.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/onboarding/OnboardingScreen.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/onboarding/OnboardingUiEffect.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/onboarding/OnboardingUiEvent.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/onboarding/OnboardingUiState.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/onboarding/OnboardingViewModel.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/result/ResultScreen.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/result/ResultUiEffect.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/result/ResultUiEvent.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/result/ResultUiState.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/result/ResultViewModel.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/review/ReviewScreen.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/review/ReviewUiEffect.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/review/ReviewUiEvent.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/review/ReviewUiState.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/review/ReviewViewModel.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/settings/SettingsScreen.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/settings/SettingsUiEffect.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/settings/SettingsUiEvent.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/settings/SettingsUiState.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/settings/SettingsViewModel.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/study/StudyCoordinator.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/study/StudyScreen.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/study/StudyUiEffect.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/study/StudyUiEvent.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/study/StudyUiState.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/study/StudyViewModel.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/subscription/SubscriptionCoordinator.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/network/NoOpQuestionPackApi.kt
A  shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/network/QuestionPackApi.kt
A  shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/core/navigation/InMemoryAppNavigatorTest.kt
A  shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/core/navigation/RootCoordinatorTest.kt
A  shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/data/mapper/DatabaseMappersTest.kt
A  shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/domain/usecase/StartExamUseCaseTest.kt
A  shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/EffectFlowTestUtils.kt
A  shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamCoordinatorTest.kt
A  shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamViewModelTest.kt
A  shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/history/HistoryViewModelTest.kt
A  shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/home/HomeViewModelTest.kt
A  shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/onboarding/OnboardingViewModelTest.kt
A  shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/result/ResultViewModelTest.kt
A  shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/study/StudyCoordinatorTest.kt
A  shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/subscription/SubscriptionCoordinatorTest.kt
A  shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/network/NoOpQuestionPackApiTest.kt
A  shared/src/iosMain/kotlin/com/etonealbert/examenmanejo/core/di/PlatformModules.ios.kt
?? .superpowers/sdd/task-10-report.md
?? docs/architecture-implemented.md
?? docs/implementation-status.md
?? docs/mvp-vertical-slice.md
```

### `git diff --stat`

Captured after creating Task 10 docs and report. This is the unstaged diff stat only; many prior Task 1-9 implementation files are staged and therefore appear in `git diff --cached --stat` rather than this command. Use the `git status --short` block above as the complete scope summary.

```text
warning: in the working copy of '.superpowers/sdd/progress.md', LF will be replaced by CRLF the next time Git touches it
 .superpowers/sdd/progress.md                       |   3 +
 README.md                                          |  10 +-
 .../kotlin/com/etonealbert/examenmanejo/App.kt     | 210 +++++++++++++++++----
 3 files changed, 183 insertions(+), 40 deletions(-)
warning: in the working copy of 'README.md', LF will be replaced by CRLF the next time Git touches it
warning: in the working copy of 'shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/App.kt', LF will be replaced by CRLF the next time Git touches it
```

## Concerns And Blockers

- iOS simulator tests require macOS and were not run on this Windows host.
- `:shared:testAndroidHostTest` remains blocked by `Could not find or load main class VS`, `Caused by: java.lang.ClassNotFoundException: VS`.
- The worktree already contained many staged/modified implementation files from earlier tasks. Task 10 only added docs/report files and updated README command documentation; no production implementation files were changed in Task 10.
- The MVP remains fully offline; no backend, login, real subscription, cloud sync, analytics, push notifications, admin panel, or remote question updates were added.

## Fix After Final Review

### Files Changed

- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/App.kt`: added route-entry Koin keys for fresh Exam and History view models, plus startup error display.
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/navigation/AppRoute.kt`: added no-argument `StartupError` route.
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/navigation/RootCoordinator.kt`: routes to `StartupError` on seed import failure and stops normal startup routing.
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/model/ImportResult.kt`: moved `ImportResult` ownership into domain.
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/local/seed/ImportResult.kt`: removed data-layer `ImportResult` definition.
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/repository/QuestionPackRepository.kt`, `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/usecase/ImportSeedQuestionsUseCase.kt`, `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/usecase/CheckFirstLaunchSeedImportUseCase.kt`, `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/repository/QuestionPackRepositoryImpl.kt`: updated imports to domain `ImportResult`.
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/local/seed/SeedQuestionPackProvider.kt`: reads bundled `.sha256` and fails if it differs from the DTO `hash`.
- `shared/src/commonMain/composeResources/files/question_packs/argentina-class-b-demo-0001.json` and `.sha256`: replaced placeholder hash with `argentina-class-b-demo-v1-20260623`.
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/usecase/ClassBDemoExamConfig.kt` and `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamViewModel.kt`: configured the MVP demo exam for the three bundled questions.
- `shared/src/iosMain/kotlin/com/etonealbert/examenmanejo/MainViewController.kt`: guarded iOS `initKoin()` with `GlobalContext.getOrNull()`.
- `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/core/navigation/RootCoordinatorTest.kt`: added failed seed import startup routing coverage.
- `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/data/local/seed/QuestionPackDtoTest.kt`: added checksum mismatch coverage.
- `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamViewModelTest.kt`: added demo question-count coverage.
- `docs/architecture-implemented.md`, `docs/implementation-status.md`, `docs/mvp-vertical-slice.md`: documented checksum validation, startup error routing, domain import result ownership, and fresh route-entry view models.

### Commands Run And Results

#### `./gradlew.bat :shared:compileCommonTestKotlinMetadata`

Result: FAIL, task does not exist in this Gradle project.

```text
Cannot locate tasks that match ':shared:compileCommonTestKotlinMetadata' as task 'compileCommonTestKotlinMetadata' not found in project ':shared'.

BUILD FAILED in 1s
```

#### `./gradlew.bat :shared:compileKotlinMetadata`

Result: PASS.

```text
BUILD SUCCESSFUL in 801ms
7 actionable tasks: 1 executed, 6 up-to-date
Configuration cache entry reused.
```

#### `./gradlew.bat :shared:compileAndroidHostTest`

Initial red result: FAIL for expected missing implementation after adding tests.

```text
e: RootCoordinatorTest.kt:62:31 Unresolved reference 'StartupError'.
e: RootCoordinatorTest.kt:63:31 Unresolved reference 'StartupError'.
e: QuestionPackDtoTest.kt:30:13 No parameter with name 'readBytes' found.
e: QuestionPackDtoTest.kt:30:27 Cannot infer type for value parameter 'path'. Specify it explicitly.

BUILD FAILED in 2s
```

Post-fix result: PASS.

```text
BUILD SUCCESSFUL in 15s
30 actionable tasks: 12 executed, 18 up-to-date
Configuration cache entry reused.
```

#### `./gradlew.bat :shared:androidHostTest`

Result: FAIL, task does not exist in this Gradle project.

```text
Cannot locate tasks that match ':shared:androidHostTest' as task 'androidHostTest' not found in project ':shared'.

BUILD FAILED in 1s
```

#### `./gradlew.bat :shared:compileAndroidHostTest :shared:compileKotlinMetadata :androidApp:assembleDebug`

Result: PASS.

```text
BUILD SUCCESSFUL in 6s
69 actionable tasks: 9 executed, 60 up-to-date
Configuration cache entry reused.
```

#### `./gradlew.bat :shared:testAndroidHostTest`

Result: FAIL, known `VS` host test runner blocker still present.

```text
> Task :shared:testAndroidHostTest FAILED
Error: Could not find or load main class VS
Caused by: java.lang.ClassNotFoundException: VS

* What went wrong:
Execution failed for task ':shared:testAndroidHostTest'.
> Test process encountered an unexpected problem.
   > Process 'Gradle Test Executor 94' finished with non-zero exit value 1

BUILD FAILED in 3s
33 actionable tasks: 4 executed, 29 up-to-date
Configuration cache entry reused.
```

## Fix After Final Re-review

### Files Changed

- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/local/QuestionPackLocalDataSource.kt`: added `QuestionPackContentStore` seam and kept `QuestionPackLocalDataSource` as the SQLDelight-backed implementation.
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/repository/QuestionPackRepositoryImpl.kt`: depends on `QuestionPackContentStore`, preserving provider read/checksum/decode inside the repository `try` so provider exceptions convert to `ImportResult.Failed`.
- `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/data/repository/QuestionPackRepositoryImplTest.kt`: added focused regression coverage for converting provider/resource failures to `ImportResult.Failed` without touching local storage.
- No checksum resource change was needed in this pass: `argentina-class-b-demo-0001.sha256` already equals the actual JSON SHA-256 `F81CC64DEB2F4F485E9BC467FFB86FA1DC28E6002C9BAEF71FC6941777BD4C2C`.
- No route ViewModel code change was needed in this pass: `App.kt` already uses `rememberRouteViewModelStoreOwner(route)` and clears its `ViewModelStore` on route disposal; no `koinViewModel` unique keys remain.
- No docs change was needed in this pass: `docs/architecture-implemented.md`, `docs/implementation-status.md`, and `docs/mvp-vertical-slice.md` already document authoritative `.sha256` validation and startup error routing.

### Commands Run And Results

#### `./gradlew.bat :shared:compileCommonTestKotlinMetadata`

Result: FAIL, task does not exist in this Gradle project.

```text
Cannot locate tasks that match ':shared:compileCommonTestKotlinMetadata' as task 'compileCommonTestKotlinMetadata' not found in project ':shared'.

BUILD FAILED in 10s
```

#### `./gradlew.bat :shared:compileAndroidHostTest`

Initial red result: FAIL for expected missing test seam after adding repository regression test first.

```text
e: QuestionPackRepositoryImplTest.kt:3:48 Unresolved reference 'QuestionPackContentStore'.
e: QuestionPackRepositoryImplTest.kt:21:31 Argument type mismatch: actual type is 'QuestionPackRepositoryImplTest.FakeQuestionPackContentStore', but 'QuestionPackLocalDataSource' was expected.
e: QuestionPackRepositoryImplTest.kt:31:50 Unresolved reference 'QuestionPackContentStore'.
> Task :shared:compileAndroidHostTest FAILED

BUILD FAILED in 9s
```

Post-fix result: PASS.

```text
BUILD SUCCESSFUL in 27s
30 actionable tasks: 4 executed, 26 up-to-date
Configuration cache entry reused.
```

#### `Get-FileHash -Algorithm SHA256 -LiteralPath "shared\src\commonMain\composeResources\files\question_packs\argentina-class-b-demo-0001.json"`

Result: PASS.

```text
SHA256          F81CC64DEB2F4F485E9BC467FFB86FA1DC28E6002C9BAEF71FC6941777BD4C2C
```

#### `./gradlew.bat :shared:testAndroidHostTest`

Result: FAIL, known `VS` host test runner blocker still present before tests execute.

```text
> Task :shared:testAndroidHostTest FAILED
Error: Could not find or load main class VS
Caused by: java.lang.ClassNotFoundException: VS

* What went wrong:
Execution failed for task ':shared:testAndroidHostTest'.
> Test process encountered an unexpected problem.
   > Process 'Gradle Test Executor 1' finished with non-zero exit value 1

BUILD FAILED in 4s
33 actionable tasks: 4 executed, 29 up-to-date
Configuration cache entry reused.
```

#### Domain import checks

Result: PASS, no forbidden domain imports found.

```text
grep pattern: ^import .*\.(data|db|network|feature|core\.di|android|ios|compose|sqldelight|ktor|koin)\b
Result: No files found

grep pattern: ^import .*com\.etonealbert\.examenmanejo\.data
Result: No files found
```

#### `./gradlew.bat :shared:compileAndroidHostTest :shared:compileKotlinMetadata :androidApp:assembleDebug`

Result: PASS.

```text
BUILD SUCCESSFUL in 7s
69 actionable tasks: 5 executed, 1 from cache, 63 up-to-date
Configuration cache entry reused.
```

#### `./gradlew.bat :shared:compileKotlinMetadata`

Result: PASS; Gradle marked the task skipped in this cached graph.

```text
> Task :shared:compileKotlinMetadata SKIPPED

BUILD SUCCESSFUL in 855ms
7 actionable tasks: 1 executed, 6 up-to-date
Configuration cache entry reused.
```

## Fix After DI Re-review

Final re-review found `QuestionPackContentStore` was required by `QuestionPackRepositoryImpl` but not bound in Koin.

Files changed:

- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/di/AppModules.kt`

Change:

- Added `single<QuestionPackContentStore> { get<QuestionPackLocalDataSource>() }` so Koin resolves the repository dependency through the data-source interface.

Command:

```powershell
.\gradlew.bat :shared:compileAndroidHostTest :shared:compileKotlinMetadata :androidApp:assembleDebug
```

Result:

```text
BUILD SUCCESSFUL in 21s
69 actionable tasks: 6 executed, 63 up-to-date
Configuration cache entry reused.
```
