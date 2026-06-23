# Task 5 Report

status: DONE_WITH_CONCERNS

## Files Changed

- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/repository/LicenseClassRepository.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/repository/QuestionRepository.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/repository/ExamRepository.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/repository/StatsRepository.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/repository/SettingsRepository.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/repository/SubscriptionRepository.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/repository/QuestionPackRepository.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/mapper/DatabaseMappers.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/local/LicenseClassLocalDataSource.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/local/QuestionLocalDataSource.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/local/ExamLocalDataSource.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/local/SettingsLocalDataSource.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/local/QuestionPackLocalDataSource.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/repository/LicenseClassRepositoryImpl.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/repository/QuestionRepositoryImpl.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/repository/ExamRepositoryImpl.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/repository/StatsRepositoryImpl.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/repository/SettingsRepositoryImpl.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/repository/FakeSubscriptionRepository.kt`
- `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/repository/QuestionPackRepositoryImpl.kt`

## Commands Run And Results

- `.\gradlew.bat :shared:generateCommonMainExamenManejoDatabaseInterface`
- Result: `BUILD SUCCESSFUL in 1s`; task was `UP-TO-DATE`.

- `.\gradlew.bat :shared:compileKotlinMetadata`
- Result: `BUILD SUCCESSFUL in 981ms`; `:shared:compileKotlinMetadata SKIPPED` due Gradle task behavior/cache.

- `.\gradlew.bat :shared:compileKotlinMetadata --rerun-tasks`
- Result: `BUILD SUCCESSFUL in 2s`; `:shared:compileKotlinMetadata SKIPPED` even with rerun tasks.

- `.\gradlew.bat :shared:compileCommonMainKotlinMetadata --rerun-tasks`
- First result: `BUILD FAILED in 7s` with unresolved `Clock.System` references in Task 5 files.
- Fixed by using `kotlin.time.Clock`.

- `.\gradlew.bat :shared:compileCommonMainKotlinMetadata --rerun-tasks`
- Final result: `BUILD SUCCESSFUL in 6s`; warning only: existing `expect`/`actual` beta warning in `DriverFactory.kt`.

- `.\gradlew.bat :shared:compileKotlinMetadata`
- Final requested command result: `BUILD SUCCESSFUL in 1s`; `:shared:compileKotlinMetadata SKIPPED`.

## Concerns

- `QuestionLocalDataSource` maps questions from the existing `selectQuestionsByLicenseClass` query. That generated row does not include joined category display names or content source fields, so the mapper currently uses the category ID as the display name and fills source metadata with empty strings except the source ID. This keeps Task 5 within repository/data scope without changing SQLDelight schema.
- `StatsRepositoryImpl` computes MVP stats from completed exam history only: completed exam count and integer average score percentage.
- `FakeSubscriptionRepository` returns no premium access with source `MVP_FAKE`, keeping the MVP offline and subscription-free.
- The requested `:shared:compileKotlinMetadata` task reports success but is skipped by Gradle in this checkout. `:shared:compileCommonMainKotlinMetadata --rerun-tasks` was run to verify the new common source files compile.

## Fix After Review

- Fixed `QuestionLocalDataSource.getQuestionsByLicenseClass()` to load the persisted `question_category` and `content_source` rows for each selected question, then pass that content metadata into `DatabaseMappers.kt`.
- Added minimal SQLDelight read queries in `Content.sq` for selecting category and source rows by id; no table schema changes were made.
- Updated question mapping so domain `Question.category.displayName` and `Question.source` metadata come from imported local content data, which makes exam snapshots use the imported human-readable category display name.
- Added a mapper regression test covering imported category/source metadata and exam snapshot category text.
- Red check: `.\gradlew.bat :shared:testAndroidHostTest --rerun-tasks` failed before implementation with missing `category` and `source` mapper parameters.
- Test compile check after fix: `.\gradlew.bat :shared:compileAndroidHostTest --rerun-tasks` result: `BUILD SUCCESSFUL in 9s`; existing warnings only.
- Requested verification: `.\gradlew.bat :shared:compileCommonMainKotlinMetadata --rerun-tasks` result: `BUILD SUCCESSFUL in 4s`; existing `expect`/`actual` beta warning only in `DriverFactory.kt`.
