# MVP Architecture And Class B Vertical Slice Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the initial scalable local-first architecture and first Class B exam/result/review/history vertical slice for ExamenManejo.

**Architecture:** Keep the current `:shared` and `:androidApp` modules. Implement package boundaries in `shared` using Clean Architecture-lite, MVVM/UDF, repository interfaces, SQLDelight-backed local storage, Koin wiring, and coordinator navigation over primitive typed routes.

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform, SQLDelight, Koin, kotlinx.coroutines `Flow`/`StateFlow`, kotlinx.serialization JSON seed import, Ktor client interfaces with local/fake MVP bindings, Kotlin common tests.

## Global Constraints

- Keep Android `minSdk=24`; do not change platform reach in this implementation.
- Do not attempt `:shared:iosSimulatorArm64Test` on Windows; document that macOS is required.
- MVP is fully offline: no backend, login, real subscription, cloud sync, analytics, push notifications, admin panel, or remote question updates.
- SQLDelight is the source of truth after seed import; bundled JSON is seed input only.
- Domain must not import Compose, SQLDelight, Ktor, Koin, Android APIs, or iOS APIs.
- Route arguments must be primitive IDs only: `licenseClassId: String`, `examSessionId: Long`, `source: String`, `documentId: String`.
- Demo questions must be marked `DEMO_UNVERIFIED` and must not be represented as official content.
- Do not commit changes unless the user explicitly requests a commit.

---

## File Structure

Create these focused areas under `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/`:

- `core/result`: reusable `AppResult` if needed for import/startup failures.
- `core/platform`: platform resource/database abstractions.
- `core/time`: `Clock` and default implementation.
- `core/di`: Koin module declarations.
- `core/navigation`: `AppRoute`, `AppNavigator`, `NavigationEffect`, `RootCoordinator`, modal/global route models.
- `core/design`: minimal theme tokens and reusable Compose UI components.
- `domain/model`: one file per domain model.
- `domain/repository`: one file per repository interface.
- `domain/usecase`: one file per use case.
- `data/local`: SQLDelight driver/provider and data sources.
- `data/local/seed`: seed DTOs, JSON seed reader, seed importer, import result.
- `data/repository`: SQLDelight-backed repository implementations and fake subscription repository.
- `data/mapper`: row/DTO-to-domain mappers.
- `network`: future API interfaces and local no-op client provider.
- `feature/*`: one package per screen with `Screen`, `ViewModel`, `UiState`, `UiEvent`, `UiEffect` files.

Create SQLDelight files under:

- `shared/src/commonMain/sqldelight/com/etonealbert/examenmanejo/db/Content.sq`
- `shared/src/commonMain/sqldelight/com/etonealbert/examenmanejo/db/Exam.sq`
- `shared/src/commonMain/sqldelight/com/etonealbert/examenmanejo/db/ProgressSettings.sq`

Create bundled seed resources under:

- `shared/src/commonMain/composeResources/files/question_packs/argentina-class-b-demo-0001.json`
- `shared/src/commonMain/composeResources/files/question_packs/argentina-class-b-demo-0001.sha256`

Use tests under:

- `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/domain/usecase/`
- `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/data/local/seed/`
- `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/feature/exam/`

---

### Task 1: Gradle Dependencies And SQLDelight Configuration

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `build.gradle.kts`
- Modify: `shared/build.gradle.kts`

**Interfaces:**
- Produces: SQLDelight generated database class `com.etonealbert.examenmanejo.db.ExamenManejoDatabase`.
- Produces dependencies used by all later tasks: SQLDelight runtime/drivers/coroutines, Koin core/Compose ViewModel support, Ktor core, kotlinx serialization JSON, kotlinx datetime if available.

- [ ] **Step 1: Add version catalog entries**

Add these aliases to `gradle/libs.versions.toml` using versions compatible with the current Kotlin/AGP stack:

```toml
[versions]
sqldelight = "2.3.2"
koin = "4.1.1"
ktor = "3.3.2"
kotlinx-coroutines = "1.10.2"
kotlinx-serialization = "1.9.0"
kotlinx-datetime = "0.7.1"

[libraries]
sqldelight-runtime = { module = "app.cash.sqldelight:runtime", version.ref = "sqldelight" }
sqldelight-coroutines = { module = "app.cash.sqldelight:coroutines-extensions", version.ref = "sqldelight" }
sqldelight-android-driver = { module = "app.cash.sqldelight:android-driver", version.ref = "sqldelight" }
sqldelight-native-driver = { module = "app.cash.sqldelight:native-driver", version.ref = "sqldelight" }
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-compose = { module = "io.insert-koin:koin-compose", version.ref = "koin" }
koin-compose-viewmodel = { module = "io.insert-koin:koin-compose-viewmodel", version.ref = "koin" }
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "kotlinx-coroutines" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinx-serialization" }
kotlinx-datetime = { module = "org.jetbrains.kotlinx:kotlinx-datetime", version.ref = "kotlinx-datetime" }

[plugins]
sqldelight = { id = "app.cash.sqldelight", version.ref = "sqldelight" }
kotlinxSerialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

- [ ] **Step 2: Add root plugin aliases**

Add these plugin aliases to the root `build.gradle.kts` plugin block:

```kotlin
alias(libs.plugins.sqldelight) apply false
alias(libs.plugins.kotlinxSerialization) apply false
```

- [ ] **Step 3: Configure `shared` plugins and dependencies**

In `shared/build.gradle.kts`, add plugins:

```kotlin
alias(libs.plugins.sqldelight)
alias(libs.plugins.kotlinxSerialization)
```

Add `commonMain` dependencies:

```kotlin
implementation(libs.kotlinx.coroutines.core)
implementation(libs.kotlinx.serialization.json)
implementation(libs.kotlinx.datetime)
implementation(libs.sqldelight.runtime)
implementation(libs.sqldelight.coroutines)
implementation(libs.koin.core)
implementation(libs.koin.compose)
implementation(libs.koin.compose.viewmodel)
implementation(libs.ktor.client.core)
```

Add platform dependencies:

```kotlin
androidMain.dependencies {
    implementation(libs.compose.uiToolingPreview)
    implementation(libs.sqldelight.android.driver)
}
iosMain.dependencies {
    implementation(libs.sqldelight.native.driver)
}
```

Add SQLDelight configuration at the end of `shared/build.gradle.kts`:

```kotlin
sqldelight {
    databases {
        create("ExamenManejoDatabase") {
            packageName.set("com.etonealbert.examenmanejo.db")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
            verifyMigrations.set(true)
        }
    }
}
```

- [ ] **Step 4: Run Gradle metadata compile**

Run: `./gradlew.bat :shared:compileKotlinMetadata`

Expected: the task may fail because SQLDelight schema files do not exist yet. Accept only failures that clearly indicate missing SQLDelight schema; dependency resolution failures must be fixed in this task.

- [ ] **Step 5: Review diff checkpoint**

Run: `git diff -- gradle/libs.versions.toml build.gradle.kts shared/build.gradle.kts`

Expected: only dependency/plugin/configuration changes are shown. Do not commit unless the user explicitly requests a commit.

---

### Task 2: Domain Models And Pure Scoring Tests

**Files:**
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/model/*.kt`
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/usecase/CalculateExamResultUseCase.kt`
- Test: `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/domain/usecase/CalculateExamResultUseCaseTest.kt`

**Interfaces:**
- Produces: domain model contracts used by repositories, use cases, ViewModels, and mappers.
- Produces: `CalculateExamResultUseCase.calculate(config: ExamConfig, snapshots: List<ExamQuestionSnapshot>, answers: List<ExamAnswer>): ExamResult`.

- [ ] **Step 1: Write failing scoring tests**

Create `CalculateExamResultUseCaseTest.kt`:

```kotlin
package com.etonealbert.examenmanejo.domain.usecase

import com.etonealbert.examenmanejo.domain.model.ExamAnswer
import com.etonealbert.examenmanejo.domain.model.ExamConfig
import com.etonealbert.examenmanejo.domain.model.ExamOptionSnapshot
import com.etonealbert.examenmanejo.domain.model.ExamQuestionSnapshot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CalculateExamResultUseCaseTest {
    private val config = ExamConfig(
        licenseClassId = "B",
        questionCount = 3,
        passingPercentage = 70,
        timeLimitMinutes = null,
    )

    private val snapshots = listOf(
        snapshot("q1", "q1-a"),
        snapshot("q2", "q2-a"),
        snapshot("q3", "q3-a"),
    )

    @Test
    fun allAnswersCorrectPasses() {
        val result = CalculateExamResultUseCase().calculate(
            config = config,
            snapshots = snapshots,
            answers = listOf(answer("q1", "q1-a"), answer("q2", "q2-a"), answer("q3", "q3-a")),
        )

        assertEquals(3, result.correctCount)
        assertEquals(0, result.incorrectCount)
        assertEquals(100, result.scorePercentage)
        assertTrue(result.passed)
    }

    @Test
    fun unansweredQuestionsCountAsIncorrect() {
        val result = CalculateExamResultUseCase().calculate(
            config = config,
            snapshots = snapshots,
            answers = listOf(answer("q1", "q1-a"), answer("q2", "q2-b")),
        )

        assertEquals(1, result.correctCount)
        assertEquals(2, result.incorrectCount)
        assertEquals(33, result.scorePercentage)
        assertFalse(result.passed)
    }

    private fun snapshot(questionId: String, correctOptionId: String) = ExamQuestionSnapshot(
        questionId = questionId,
        questionText = "Question $questionId",
        category = "Reglas de tránsito",
        explanation = "Explicación demo",
        options = listOf(
            ExamOptionSnapshot(optionId = correctOptionId, text = "Correcta", position = 0),
            ExamOptionSnapshot(optionId = questionId + "-b", text = "Incorrecta", position = 1),
        ),
        correctOptionId = correctOptionId,
        order = 0,
    )

    private fun answer(questionId: String, optionId: String) = ExamAnswer(
        sessionId = 1L,
        questionId = questionId,
        selectedOptionId = optionId,
        answeredAtEpochMillis = 1L,
    )
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew.bat :shared:testAndroidHostTest --tests "*.CalculateExamResultUseCaseTest"`

Expected: FAIL because domain models and use case are not defined.

- [ ] **Step 3: Add domain model files**

Create these files with the listed contracts:

```kotlin
// ExamConfig.kt
package com.etonealbert.examenmanejo.domain.model

data class ExamConfig(
    val licenseClassId: String,
    val questionCount: Int,
    val passingPercentage: Int,
    val timeLimitMinutes: Int?,
)
```

```kotlin
// ContentStatus.kt
package com.etonealbert.examenmanejo.domain.model

enum class ContentStatus { DEMO_UNVERIFIED, VERIFIED, DEPRECATED }
```

```kotlin
// ReviewStatus.kt
package com.etonealbert.examenmanejo.domain.model

enum class ReviewStatus { DRAFT, NEEDS_REVIEW, APPROVED, REJECTED }
```

```kotlin
// QuestionStatus.kt
package com.etonealbert.examenmanejo.domain.model

enum class QuestionStatus { ACTIVE, INACTIVE }
```

```kotlin
// LicenseClass.kt
package com.etonealbert.examenmanejo.domain.model

data class LicenseClass(
    val id: String,
    val displayName: String,
    val description: String,
    val isEnabled: Boolean,
)
```

```kotlin
// LicenseSubclass.kt
package com.etonealbert.examenmanejo.domain.model

data class LicenseSubclass(
    val id: String,
    val licenseClassId: String,
    val displayName: String,
    val description: String,
)
```

```kotlin
// Jurisdiction.kt
package com.etonealbert.examenmanejo.domain.model

data class Jurisdiction(
    val id: String,
    val displayName: String,
    val level: String,
)
```

```kotlin
// QuestionCategory.kt
package com.etonealbert.examenmanejo.domain.model

data class QuestionCategory(
    val id: String,
    val displayName: String,
)
```

```kotlin
// QuestionSource.kt
package com.etonealbert.examenmanejo.domain.model

data class QuestionSource(
    val id: String,
    val title: String,
    val url: String,
    val accessedAt: String,
    val attribution: String,
)
```

```kotlin
// AnswerOption.kt
package com.etonealbert.examenmanejo.domain.model

data class AnswerOption(
    val id: String,
    val questionId: String,
    val text: String,
    val isCorrect: Boolean,
    val position: Int,
)
```

```kotlin
// Question.kt
package com.etonealbert.examenmanejo.domain.model

data class Question(
    val id: String,
    val licenseClassIds: List<String>,
    val jurisdictionId: String,
    val category: QuestionCategory,
    val source: QuestionSource,
    val text: String,
    val explanation: String,
    val options: List<AnswerOption>,
    val contentStatus: ContentStatus,
    val reviewStatus: ReviewStatus,
    val status: QuestionStatus,
)
```

```kotlin
// ExamOptionSnapshot.kt
package com.etonealbert.examenmanejo.domain.model

data class ExamOptionSnapshot(
    val optionId: String,
    val text: String,
    val position: Int,
)
```

```kotlin
// ExamQuestionSnapshot.kt
package com.etonealbert.examenmanejo.domain.model

data class ExamQuestionSnapshot(
    val questionId: String,
    val questionText: String,
    val category: String,
    val explanation: String,
    val options: List<ExamOptionSnapshot>,
    val correctOptionId: String,
    val order: Int,
)
```

```kotlin
// ExamAnswer.kt
package com.etonealbert.examenmanejo.domain.model

data class ExamAnswer(
    val sessionId: Long,
    val questionId: String,
    val selectedOptionId: String?,
    val answeredAtEpochMillis: Long,
)
```

```kotlin
// ExamSession.kt
package com.etonealbert.examenmanejo.domain.model

data class ExamSession(
    val id: Long,
    val licenseClassId: String,
    val status: String,
    val startedAtEpochMillis: Long,
    val completedAtEpochMillis: Long?,
)
```

```kotlin
// ExamResult.kt
package com.etonealbert.examenmanejo.domain.model

data class ExamResult(
    val sessionId: Long,
    val licenseClassId: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val scorePercentage: Int,
    val passed: Boolean,
)
```

```kotlin
// StudyProgress.kt
package com.etonealbert.examenmanejo.domain.model

data class StudyProgress(
    val questionId: String,
    val seenCount: Int,
    val correctCount: Int,
    val incorrectCount: Int,
)
```

```kotlin
// UserStats.kt
package com.etonealbert.examenmanejo.domain.model

data class UserStats(
    val completedExamCount: Int,
    val averageScorePercentage: Int,
)
```

```kotlin
// SubscriptionEntitlement.kt
package com.etonealbert.examenmanejo.domain.model

data class SubscriptionEntitlement(
    val hasPremiumAccess: Boolean,
    val source: String,
)
```

- [ ] **Step 4: Add minimal scoring implementation**

Create `CalculateExamResultUseCase.kt`:

```kotlin
package com.etonealbert.examenmanejo.domain.usecase

import com.etonealbert.examenmanejo.domain.model.ExamAnswer
import com.etonealbert.examenmanejo.domain.model.ExamConfig
import com.etonealbert.examenmanejo.domain.model.ExamQuestionSnapshot
import com.etonealbert.examenmanejo.domain.model.ExamResult

class CalculateExamResultUseCase {
    fun calculate(
        config: ExamConfig,
        snapshots: List<ExamQuestionSnapshot>,
        answers: List<ExamAnswer>,
        sessionId: Long = answers.firstOrNull()?.sessionId ?: 0L,
    ): ExamResult {
        val answersByQuestionId = answers.associateBy { it.questionId }
        val correctCount = snapshots.count { snapshot ->
            answersByQuestionId[snapshot.questionId]?.selectedOptionId == snapshot.correctOptionId
        }
        val total = snapshots.size
        val score = if (total == 0) 0 else (correctCount * 100) / total
        return ExamResult(
            sessionId = sessionId,
            licenseClassId = config.licenseClassId,
            totalQuestions = total,
            correctCount = correctCount,
            incorrectCount = total - correctCount,
            scorePercentage = score,
            passed = score >= config.passingPercentage,
        )
    }
}
```

- [ ] **Step 5: Run scoring tests**

Run: `./gradlew.bat :shared:testAndroidHostTest --tests "*.CalculateExamResultUseCaseTest"`

Expected: PASS.

- [ ] **Step 6: Review diff checkpoint**

Run: `git diff -- shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/domain`

Expected: only domain model/use-case/test files are shown. Do not commit unless the user explicitly requests a commit.

---

### Task 3: SQLDelight Schema And Platform Driver Factory

**Files:**
- Create: `shared/src/commonMain/sqldelight/com/etonealbert/examenmanejo/db/Content.sq`
- Create: `shared/src/commonMain/sqldelight/com/etonealbert/examenmanejo/db/Exam.sq`
- Create: `shared/src/commonMain/sqldelight/com/etonealbert/examenmanejo/db/ProgressSettings.sq`
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/local/DriverFactory.kt`
- Create: `shared/src/androidMain/kotlin/com/etonealbert/examenmanejo/data/local/DriverFactory.android.kt`
- Create: `shared/src/iosMain/kotlin/com/etonealbert/examenmanejo/data/local/DriverFactory.ios.kt`
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/local/DatabaseProvider.kt`

**Interfaces:**
- Consumes: SQLDelight Gradle setup from Task 1.
- Produces: `DriverFactory.createDriver(): SqlDriver` and `DatabaseProvider.database: ExamenManejoDatabase`.

- [ ] **Step 1: Add content schema**

Create `Content.sq`:

```sql
CREATE TABLE question_pack (
  pack_id TEXT NOT NULL,
  version INTEGER NOT NULL,
  hash TEXT NOT NULL,
  title TEXT NOT NULL,
  installed_at_epoch_ms INTEGER,
  PRIMARY KEY(pack_id, version)
);

CREATE TABLE content_source (
  source_id TEXT NOT NULL PRIMARY KEY,
  title TEXT NOT NULL,
  url TEXT NOT NULL,
  accessed_at TEXT NOT NULL,
  attribution TEXT NOT NULL
);

CREATE TABLE jurisdiction (
  jurisdiction_id TEXT NOT NULL PRIMARY KEY,
  display_name TEXT NOT NULL,
  level TEXT NOT NULL
);

CREATE TABLE license_class (
  license_class_id TEXT NOT NULL PRIMARY KEY,
  display_name TEXT NOT NULL,
  description TEXT NOT NULL,
  is_enabled INTEGER NOT NULL
);

CREATE TABLE license_subclass (
  subclass_id TEXT NOT NULL PRIMARY KEY,
  license_class_id TEXT NOT NULL,
  display_name TEXT NOT NULL,
  description TEXT NOT NULL,
  FOREIGN KEY(license_class_id) REFERENCES license_class(license_class_id)
);

CREATE TABLE question_category (
  category_id TEXT NOT NULL PRIMARY KEY,
  display_name TEXT NOT NULL
);

CREATE TABLE question (
  question_id TEXT NOT NULL PRIMARY KEY,
  pack_id TEXT NOT NULL,
  jurisdiction_id TEXT NOT NULL,
  category_id TEXT NOT NULL,
  source_id TEXT NOT NULL,
  text TEXT NOT NULL,
  explanation TEXT NOT NULL,
  content_status TEXT NOT NULL,
  review_status TEXT NOT NULL,
  question_status TEXT NOT NULL,
  content_hash TEXT NOT NULL,
  FOREIGN KEY(jurisdiction_id) REFERENCES jurisdiction(jurisdiction_id),
  FOREIGN KEY(category_id) REFERENCES question_category(category_id),
  FOREIGN KEY(source_id) REFERENCES content_source(source_id)
);

CREATE TABLE question_license_class (
  question_id TEXT NOT NULL,
  license_class_id TEXT NOT NULL,
  PRIMARY KEY(question_id, license_class_id),
  FOREIGN KEY(question_id) REFERENCES question(question_id),
  FOREIGN KEY(license_class_id) REFERENCES license_class(license_class_id)
);

CREATE TABLE question_option (
  option_id TEXT NOT NULL PRIMARY KEY,
  question_id TEXT NOT NULL,
  text TEXT NOT NULL,
  is_correct INTEGER NOT NULL,
  position INTEGER NOT NULL,
  FOREIGN KEY(question_id) REFERENCES question(question_id)
);

CREATE TABLE import_log (
  import_log_id TEXT NOT NULL PRIMARY KEY,
  pack_id TEXT NOT NULL,
  version INTEGER NOT NULL,
  started_at_epoch_ms INTEGER NOT NULL,
  finished_at_epoch_ms INTEGER,
  status TEXT NOT NULL,
  message TEXT NOT NULL
);

selectQuestionPack:
SELECT * FROM question_pack WHERE pack_id = ? AND version = ?;

insertQuestionPack:
INSERT OR REPLACE INTO question_pack(pack_id, version, hash, title, installed_at_epoch_ms)
VALUES (?, ?, ?, ?, ?);

upsertContentSource:
INSERT OR REPLACE INTO content_source(source_id, title, url, accessed_at, attribution)
VALUES (?, ?, ?, ?, ?);

upsertJurisdiction:
INSERT OR REPLACE INTO jurisdiction(jurisdiction_id, display_name, level)
VALUES (?, ?, ?);

upsertLicenseClass:
INSERT OR REPLACE INTO license_class(license_class_id, display_name, description, is_enabled)
VALUES (?, ?, ?, ?);

upsertLicenseSubclass:
INSERT OR REPLACE INTO license_subclass(subclass_id, license_class_id, display_name, description)
VALUES (?, ?, ?, ?);

upsertQuestionCategory:
INSERT OR REPLACE INTO question_category(category_id, display_name)
VALUES (?, ?);

upsertQuestion:
INSERT OR REPLACE INTO question(
  question_id, pack_id, jurisdiction_id, category_id, source_id, text, explanation,
  content_status, review_status, question_status, content_hash
) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);

deleteQuestionLicenseClasses:
DELETE FROM question_license_class WHERE question_id = ?;

insertQuestionLicenseClass:
INSERT OR IGNORE INTO question_license_class(question_id, license_class_id)
VALUES (?, ?);

deleteQuestionOptions:
DELETE FROM question_option WHERE question_id = ?;

upsertQuestionOption:
INSERT OR REPLACE INTO question_option(option_id, question_id, text, is_correct, position)
VALUES (?, ?, ?, ?, ?);

insertImportLog:
INSERT INTO import_log(import_log_id, pack_id, version, started_at_epoch_ms, finished_at_epoch_ms, status, message)
VALUES (?, ?, ?, ?, ?, ?, ?);

finishImportLog:
UPDATE import_log SET finished_at_epoch_ms = ?, status = ?, message = ? WHERE import_log_id = ?;

selectAllLicenseClasses:
SELECT * FROM license_class ORDER BY license_class_id;

selectQuestionsByLicenseClass:
SELECT DISTINCT q.* FROM question q
JOIN question_license_class qlc ON q.question_id = qlc.question_id
WHERE qlc.license_class_id = ? AND q.question_status = 'ACTIVE'
ORDER BY q.question_id;

selectQuestionOptions:
SELECT * FROM question_option WHERE question_id = ? ORDER BY position;
```

- [ ] **Step 2: Add exam schema**

Create `Exam.sq`:

```sql
CREATE TABLE exam_session (
  session_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  license_class_id TEXT NOT NULL,
  status TEXT NOT NULL,
  question_count INTEGER NOT NULL,
  passing_percentage INTEGER NOT NULL,
  started_at_epoch_ms INTEGER NOT NULL,
  completed_at_epoch_ms INTEGER,
  score_percentage INTEGER,
  passed INTEGER
);

CREATE TABLE exam_session_question_snapshot (
  session_id INTEGER NOT NULL,
  question_id TEXT NOT NULL,
  question_order INTEGER NOT NULL,
  question_text_snapshot TEXT NOT NULL,
  category_snapshot TEXT NOT NULL,
  explanation_snapshot TEXT NOT NULL,
  options_snapshot_json TEXT NOT NULL,
  correct_option_id_snapshot TEXT NOT NULL,
  PRIMARY KEY(session_id, question_id),
  FOREIGN KEY(session_id) REFERENCES exam_session(session_id)
);

CREATE TABLE exam_session_answer (
  session_id INTEGER NOT NULL,
  question_id TEXT NOT NULL,
  selected_option_id_snapshot TEXT,
  answered_at_epoch_ms INTEGER NOT NULL,
  PRIMARY KEY(session_id, question_id),
  FOREIGN KEY(session_id) REFERENCES exam_session(session_id)
);

insertExamSession:
INSERT INTO exam_session(
  license_class_id, status, question_count, passing_percentage, started_at_epoch_ms,
  completed_at_epoch_ms, score_percentage, passed
) VALUES (?, ?, ?, ?, ?, ?, ?, ?);

lastInsertedSessionId:
SELECT last_insert_rowid();

insertQuestionSnapshot:
INSERT INTO exam_session_question_snapshot(
  session_id, question_id, question_order, question_text_snapshot, category_snapshot,
  explanation_snapshot, options_snapshot_json, correct_option_id_snapshot
) VALUES (?, ?, ?, ?, ?, ?, ?, ?);

upsertExamAnswer:
INSERT OR REPLACE INTO exam_session_answer(session_id, question_id, selected_option_id_snapshot, answered_at_epoch_ms)
VALUES (?, ?, ?, ?);

selectExamSession:
SELECT * FROM exam_session WHERE session_id = ?;

selectQuestionSnapshots:
SELECT * FROM exam_session_question_snapshot WHERE session_id = ? ORDER BY question_order;

selectExamAnswers:
SELECT * FROM exam_session_answer WHERE session_id = ?;

completeExamSession:
UPDATE exam_session
SET status = 'COMPLETED', completed_at_epoch_ms = ?, score_percentage = ?, passed = ?
WHERE session_id = ?;

selectCompletedExamHistory:
SELECT * FROM exam_session WHERE status = 'COMPLETED' ORDER BY completed_at_epoch_ms DESC;
```

- [ ] **Step 3: Add progress/settings schema**

Create `ProgressSettings.sq`:

```sql
CREATE TABLE study_progress (
  question_id TEXT NOT NULL PRIMARY KEY,
  seen_count INTEGER NOT NULL,
  correct_count INTEGER NOT NULL,
  incorrect_count INTEGER NOT NULL,
  updated_at_epoch_ms INTEGER NOT NULL
);

CREATE TABLE user_settings (
  setting_key TEXT NOT NULL PRIMARY KEY,
  setting_value TEXT NOT NULL,
  updated_at_epoch_ms INTEGER NOT NULL
);

upsertStudyProgress:
INSERT OR REPLACE INTO study_progress(question_id, seen_count, correct_count, incorrect_count, updated_at_epoch_ms)
VALUES (?, ?, ?, ?, ?);

selectStudyProgress:
SELECT * FROM study_progress WHERE question_id = ?;

upsertUserSetting:
INSERT OR REPLACE INTO user_settings(setting_key, setting_value, updated_at_epoch_ms)
VALUES (?, ?, ?);

selectUserSetting:
SELECT * FROM user_settings WHERE setting_key = ?;

selectAllUserSettings:
SELECT * FROM user_settings ORDER BY setting_key;
```

- [ ] **Step 4: Add driver factory files**

Create common `DriverFactory.kt`:

```kotlin
package com.etonealbert.examenmanejo.data.local

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory {
    fun createDriver(): SqlDriver
}
```

Create Android actual:

```kotlin
package com.etonealbert.examenmanejo.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.etonealbert.examenmanejo.db.ExamenManejoDatabase

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver = AndroidSqliteDriver(
        schema = ExamenManejoDatabase.Schema,
        context = context,
        name = "examen_manejo.db",
    )
}
```

Create iOS actual:

```kotlin
package com.etonealbert.examenmanejo.data.local
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.etonealbert.examenmanejo.db.ExamenManejoDatabase

actual class DriverFactory {
    actual fun createDriver(): SqlDriver = NativeSqliteDriver(
        schema = ExamenManejoDatabase.Schema,
        name = "examen_manejo.db",
    )
}
```

Create `DatabaseProvider.kt`:

```kotlin
package com.etonealbert.examenmanejo.data.local

import com.etonealbert.examenmanejo.db.ExamenManejoDatabase

class DatabaseProvider(driverFactory: DriverFactory) {
    val database: ExamenManejoDatabase by lazy { ExamenManejoDatabase(driverFactory.createDriver()) }
}
```

- [ ] **Step 5: Run SQLDelight compile**

Run: `./gradlew.bat :shared:compileKotlinMetadata`

Expected: PASS. If the SQL syntax fails, fix the schema in this task before moving on.

- [ ] **Step 6: Review diff checkpoint**

Run: `git diff -- shared/src/commonMain/sqldelight shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/local shared/src/androidMain/kotlin/com/etonealbert/examenmanejo/data/local shared/src/iosMain/kotlin/com/etonealbert/examenmanejo/data/local`

Expected: only schema and database factory files are shown. Do not commit unless the user explicitly requests a commit.

---

### Task 4: Seed Pack DTOs, Demo Content, And Importer Contract

**Files:**
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/local/seed/ImportResult.kt`
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/local/seed/QuestionPackDto.kt`
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/local/seed/SeedQuestionPackProvider.kt`
- Create: `shared/src/commonMain/composeResources/files/question_packs/argentina-class-b-demo-0001.json`
- Create: `shared/src/commonMain/composeResources/files/question_packs/argentina-class-b-demo-0001.sha256`
- Test: `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/data/local/seed/QuestionPackDtoTest.kt`

**Interfaces:**
- Produces: `ImportResult` and serializable DTOs consumed by the importer/repository task.
- Produces: small Class B demo pack with non-official status metadata.

- [ ] **Step 1: Add DTO decoding test**

Create `QuestionPackDtoTest.kt`:

```kotlin
package com.etonealbert.examenmanejo.data.local.seed

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QuestionPackDtoTest {
    @Test
    fun decodesDemoQuestionPack() {
        val pack = Json { ignoreUnknownKeys = false }.decodeFromString<QuestionPackDto>(demoJson)

        assertEquals("argentina-class-b-demo", pack.packId)
        assertEquals(1, pack.version)
        assertEquals("B", pack.licenseClasses.single().id)
        assertTrue(pack.questions.all { it.contentStatus == "DEMO_UNVERIFIED" })
        assertTrue(pack.questions.all { it.reviewStatus == "APPROVED" })
    }

    private val demoJson = """
        {
          "packId":"argentina-class-b-demo",
          "version":1,
          "hash":"dev-hash",
          "title":"Preguntas demo Clase B",
          "sources":[{"id":"demo-source","title":"Contenido demo no oficial","url":"local-demo","accessedAt":"2026-06-22","attribution":"Contenido original demo para pruebas"}],
          "jurisdictions":[{"id":"AR-DEMO","displayName":"Argentina demo","level":"demo"}],
          "licenseClasses":[{"id":"B","displayName":"Clase B","description":"Autos particulares demo","isEnabled":true}],
          "categories":[{"id":"traffic_rules","displayName":"Reglas de tránsito"}],
          "questions":[{"id":"AR-DEMO-B-000001","licenseClassIds":["B"],"jurisdictionId":"AR-DEMO","categoryId":"traffic_rules","sourceId":"demo-source","text":"Pregunta demo","explanation":"Explicación demo","contentStatus":"DEMO_UNVERIFIED","reviewStatus":"APPROVED","questionStatus":"ACTIVE","contentHash":"q1","options":[{"id":"AR-DEMO-B-000001-A","text":"Correcta","isCorrect":true,"position":0},{"id":"AR-DEMO-B-000001-B","text":"Incorrecta","isCorrect":false,"position":1}]}]
        }
    """.trimIndent()
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew.bat :shared:testAndroidHostTest --tests "*.QuestionPackDtoTest"`

Expected: FAIL because DTOs do not exist.

- [ ] **Step 3: Add import result and DTO files**

Create `ImportResult.kt`:

```kotlin
package com.etonealbert.examenmanejo.data.local.seed

sealed interface ImportResult {
    data object AlreadyImported : ImportResult
    data class Imported(val questionCount: Int) : ImportResult
    data class Failed(val reason: String) : ImportResult
}
```

Create `QuestionPackDto.kt`:

```kotlin
package com.etonealbert.examenmanejo.data.local.seed

import kotlinx.serialization.Serializable

@Serializable
data class QuestionPackDto(
    val packId: String,
    val version: Int,
    val hash: String,
    val title: String,
    val sources: List<SourceDto>,
    val jurisdictions: List<JurisdictionDto>,
    val licenseClasses: List<LicenseClassDto>,
    val categories: List<CategoryDto>,
    val questions: List<QuestionDto>,
)

@Serializable data class SourceDto(val id: String, val title: String, val url: String, val accessedAt: String, val attribution: String)
@Serializable data class JurisdictionDto(val id: String, val displayName: String, val level: String)
@Serializable data class LicenseClassDto(val id: String, val displayName: String, val description: String, val isEnabled: Boolean)
@Serializable data class CategoryDto(val id: String, val displayName: String)
@Serializable data class QuestionDto(
    val id: String,
    val licenseClassIds: List<String>,
    val jurisdictionId: String,
    val categoryId: String,
    val sourceId: String,
    val text: String,
    val explanation: String,
    val contentStatus: String,
    val reviewStatus: String,
    val questionStatus: String,
    val contentHash: String,
    val options: List<OptionDto>,
)
@Serializable data class OptionDto(val id: String, val text: String, val isCorrect: Boolean, val position: Int)
```

Create `SeedQuestionPackProvider.kt`:

```kotlin
package com.etonealbert.examenmanejo.data.local.seed

import examenmanejo.shared.generated.resources.Res
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi

class SeedQuestionPackProvider(
    private val json: Json = Json { ignoreUnknownKeys = false },
) {
    @OptIn(ExperimentalResourceApi::class)
    suspend fun readBundledClassBDemoPack(): QuestionPackDto {
        val raw = Res.readBytes("files/question_packs/argentina-class-b-demo-0001.json").decodeToString()
        return json.decodeFromString(raw)
    }
}
```

- [ ] **Step 4: Add bundled demo pack**

Create `argentina-class-b-demo-0001.json` with three Class B demo questions. Use original wording and non-official metadata:

```json
{
  "packId": "argentina-class-b-demo",
  "version": 1,
  "hash": "sha256-dev-replace-with-file-hash",
  "title": "Preguntas demo Clase B",
  "sources": [{ "id": "demo-source", "title": "Contenido demo no oficial", "url": "local-demo", "accessedAt": "2026-06-22", "attribution": "Contenido original demo para validar el flujo; no es material oficial." }],
  "jurisdictions": [{ "id": "AR-DEMO", "displayName": "Argentina demo", "level": "demo" }],
  "licenseClasses": [{ "id": "B", "displayName": "Clase B", "description": "Autos particulares; contenido demo para MVP", "isEnabled": true }],
  "categories": [{ "id": "traffic_rules", "displayName": "Reglas de tránsito" }, { "id": "road_safety", "displayName": "Seguridad vial" }],
  "questions": [
    { "id": "AR-DEMO-B-000001", "licenseClassIds": ["B"], "jurisdictionId": "AR-DEMO", "categoryId": "traffic_rules", "sourceId": "demo-source", "text": "Contenido demo: en una intersección sin señales, ¿qué conducta es más segura?", "explanation": "Esta pregunta es demo y no reemplaza material oficial. La conducta segura es reducir la velocidad y ceder el paso según la prioridad aplicable.", "contentStatus": "DEMO_UNVERIFIED", "reviewStatus": "APPROVED", "questionStatus": "ACTIVE", "contentHash": "demo-b-001", "options": [{ "id": "AR-DEMO-B-000001-A", "text": "Reducir la velocidad y verificar la prioridad antes de avanzar.", "isCorrect": true, "position": 0 }, { "id": "AR-DEMO-B-000001-B", "text": "Acelerar para cruzar antes que otros vehículos.", "isCorrect": false, "position": 1 }, { "id": "AR-DEMO-B-000001-C", "text": "Ignorar la prioridad si no hay semáforo.", "isCorrect": false, "position": 2 }] },
    { "id": "AR-DEMO-B-000002", "licenseClassIds": ["B"], "jurisdictionId": "AR-DEMO", "categoryId": "road_safety", "sourceId": "demo-source", "text": "Contenido demo: antes de iniciar un viaje, ¿qué elemento básico conviene revisar?", "explanation": "Esta pregunta es demo. Revisar neumáticos, luces y documentación ayuda a prevenir riesgos básicos.", "contentStatus": "DEMO_UNVERIFIED", "reviewStatus": "APPROVED", "questionStatus": "ACTIVE", "contentHash": "demo-b-002", "options": [{ "id": "AR-DEMO-B-000002-A", "text": "Neumáticos, luces y documentación del vehículo.", "isCorrect": true, "position": 0 }, { "id": "AR-DEMO-B-000002-B", "text": "Solo el color del vehículo.", "isCorrect": false, "position": 1 }, { "id": "AR-DEMO-B-000002-C", "text": "Solo el volumen de la radio.", "isCorrect": false, "position": 2 }] },
    { "id": "AR-DEMO-B-000003", "licenseClassIds": ["B"], "jurisdictionId": "AR-DEMO", "categoryId": "traffic_rules", "sourceId": "demo-source", "text": "Contenido demo: si una señal vial indica detenerse, ¿qué debe hacer el conductor?", "explanation": "Esta pregunta es demo. Una señal de detención exige detener el vehículo y comprobar que sea seguro continuar.", "contentStatus": "DEMO_UNVERIFIED", "reviewStatus": "APPROVED", "questionStatus": "ACTIVE", "contentHash": "demo-b-003", "options": [{ "id": "AR-DEMO-B-000003-A", "text": "Detenerse y continuar solo cuando sea seguro.", "isCorrect": true, "position": 0 }, { "id": "AR-DEMO-B-000003-B", "text": "Continuar sin detenerse si no hay otros autos.", "isCorrect": false, "position": 1 }, { "id": "AR-DEMO-B-000003-C", "text": "Tocar bocina y avanzar.", "isCorrect": false, "position": 2 }] }
  ]
}
```

Set `argentina-class-b-demo-0001.sha256` to the same `hash` string for MVP validation consistency:

```text
sha256-dev-replace-with-file-hash
```

- [ ] **Step 5: Run DTO tests**

Run: `./gradlew.bat :shared:testAndroidHostTest --tests "*.QuestionPackDtoTest"`

Expected: PASS.

- [ ] **Step 6: Review diff checkpoint**

Run: `git diff -- shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/local/seed shared/src/commonMain/composeResources/files/question_packs shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/data/local/seed`

Expected: only seed DTO/provider/resource/test files are shown. Do not commit unless the user explicitly requests a commit.

---

### Task 5: Repository Interfaces, Local Data Sources, And SQLDelight Repositories

**Files:**
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/repository/*.kt`
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/local/*LocalDataSource.kt`
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/repository/*RepositoryImpl.kt`
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data/mapper/DatabaseMappers.kt`

**Interfaces:**
- Consumes: domain models from Task 2, database from Task 3, DTOs from Task 4.
- Produces repositories consumed by use cases and ViewModels.

- [ ] **Step 1: Add repository interfaces**

Create these interfaces:

```kotlin
package com.etonealbert.examenmanejo.domain.repository

import com.etonealbert.examenmanejo.domain.model.*
import kotlinx.coroutines.flow.Flow

interface LicenseClassRepository { fun observeLicenseClasses(): Flow<List<LicenseClass>> }
interface QuestionRepository { suspend fun getQuestionsByLicenseClass(licenseClassId: String): List<Question> }
interface ExamRepository {
    suspend fun startExam(config: ExamConfig, questions: List<Question>, startedAtEpochMillis: Long): Long
    suspend fun submitAnswer(sessionId: Long, questionId: String, selectedOptionId: String?, answeredAtEpochMillis: Long)
    suspend fun getQuestionSnapshots(sessionId: Long): List<ExamQuestionSnapshot>
    suspend fun getAnswers(sessionId: Long): List<ExamAnswer>
    suspend fun completeExam(result: ExamResult, completedAtEpochMillis: Long)
    suspend fun getExamResult(sessionId: Long): ExamResult
    suspend fun getExamHistory(): List<ExamResult>
}
interface StatsRepository { suspend fun getUserStats(): UserStats }
interface SettingsRepository {
    suspend fun hasCompletedOnboarding(): Boolean
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun getSelectedJurisdiction(): String?
    suspend fun setSelectedJurisdiction(jurisdictionId: String)
}
interface SubscriptionRepository { suspend fun getEntitlement(): SubscriptionEntitlement }
interface QuestionPackRepository { suspend fun importBundledSeedIfNeeded(): com.etonealbert.examenmanejo.data.local.seed.ImportResult }
```

- [ ] **Step 2: Add local data sources and mappers**

Implement local data sources as thin wrappers around generated queries. `QuestionLocalDataSource` returns domain `Question` by loading options and mapping through `DatabaseMappers`. `ExamLocalDataSource` owns session/snapshot/answer transactions. `SettingsLocalDataSource` reads/writes `user_settings` keys.

Use these setting keys exactly:

```kotlin
const val HAS_COMPLETED_ONBOARDING = "has_completed_onboarding"
const val SELECTED_JURISDICTION = "selected_jurisdiction"
const val SEED_IMPORT_VERSION = "seed_import_version"
```

- [ ] **Step 3: Add repository implementations**

Implement:

- `LicenseClassRepositoryImpl`
- `QuestionRepositoryImpl`
- `ExamRepositoryImpl`
- `StatsRepositoryImpl`
- `SettingsRepositoryImpl`
- `FakeSubscriptionRepository`
- `QuestionPackRepositoryImpl`

`QuestionPackRepositoryImpl.importBundledSeedIfNeeded()` must:

1. Read bundled DTO.
2. Check `selectQuestionPack(packId, version)`.
3. Return `ImportResult.AlreadyImported` if stored hash matches DTO hash.
4. Insert `import_log` with `status = "STARTED"`.
5. Run all content inserts inside `database.transaction {}`.
6. Insert `question_pack` only after all content rows succeed.
7. On success, finish import log with `status = "IMPORTED"` and return `Imported(questionCount)`.
8. On failure, finish import log with `status = "FAILED"`, return `Failed(reason)`, and do not insert `question_pack`.

- [ ] **Step 4: Run metadata compile**

Run: `./gradlew.bat :shared:compileKotlinMetadata`

Expected: PASS.

- [ ] **Step 5: Review diff checkpoint**

Run: `git diff -- shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/repository shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/data`

Expected: repository/data source/mapper files only. Do not commit unless the user explicitly requests a commit.

---

### Task 6: Use Cases And Selection Tests

**Files:**
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/usecase/*.kt`
- Test: `shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/domain/usecase/StartExamUseCaseTest.kt`

**Interfaces:**
- Consumes repositories from Task 5.
- Produces use cases consumed by ViewModels.

- [ ] **Step 1: Add failing start exam tests**

Test that `StartExamUseCase` filters only active Class B questions and creates snapshots before answers. Use fake repositories in the test file with in-memory lists.

Run: `./gradlew.bat :shared:testAndroidHostTest --tests "*.StartExamUseCaseTest"`

Expected: FAIL because use case is missing.

- [ ] **Step 2: Add use cases**

Create these files with constructor-injected dependencies:

- `GetLicenseClassesUseCase(repository: LicenseClassRepository)` with `operator fun invoke(): Flow<List<LicenseClass>>`.
- `GetQuestionsByLicenseClassUseCase(repository: QuestionRepository)` with `suspend operator fun invoke(licenseClassId: String): List<Question>`.
- `StartExamUseCase(questionRepository: QuestionRepository, examRepository: ExamRepository, clock: Clock)` with `suspend operator fun invoke(config: ExamConfig): Long`.
- `SubmitExamAnswerUseCase(examRepository: ExamRepository, clock: Clock)` with `suspend operator fun invoke(sessionId: Long, questionId: String, selectedOptionId: String?)`.
- `FinishExamUseCase(examRepository: ExamRepository, calculate: CalculateExamResultUseCase, clock: Clock)` with `suspend operator fun invoke(sessionId: Long, config: ExamConfig): ExamResult`.
- `GetExamResultUseCase(examRepository: ExamRepository)` with `suspend operator fun invoke(sessionId: Long): ExamResult`.
- `GetExamHistoryUseCase(examRepository: ExamRepository)` with `suspend operator fun invoke(): List<ExamResult>`.
- `GetReviewAnswersUseCase(examRepository: ExamRepository)` with `suspend operator fun invoke(sessionId: Long): List<Pair<ExamQuestionSnapshot, ExamAnswer?>>`.
- `GetUserStatsUseCase(statsRepository: StatsRepository)` with `suspend operator fun invoke(): UserStats`.
- `ImportSeedQuestionsUseCase(questionPackRepository: QuestionPackRepository)` with `suspend operator fun invoke(): ImportResult`.
- `CheckFirstLaunchSeedImportUseCase(importSeedQuestions: ImportSeedQuestionsUseCase)` with `suspend operator fun invoke(): ImportResult`.

Create `Clock.kt`:

```kotlin
package com.etonealbert.examenmanejo.core.time

interface Clock { fun nowEpochMillis(): Long }
class SystemClock : Clock { override fun nowEpochMillis(): Long = kotlin.time.Clock.System.now().toEpochMilliseconds() }
```

Use this MVP exam config helper:

```kotlin
package com.etonealbert.examenmanejo.domain.usecase

import com.etonealbert.examenmanejo.domain.model.ExamConfig

fun classBDemoExamConfig(questionCount: Int) = ExamConfig(
    licenseClassId = "B",
    questionCount = questionCount,
    passingPercentage = 70,
    timeLimitMinutes = null,
)
```

- [ ] **Step 3: Run use-case tests**

Run: `./gradlew.bat :shared:testAndroidHostTest --tests "*.StartExamUseCaseTest" --tests "*.CalculateExamResultUseCaseTest"`

Expected: PASS.

- [ ] **Step 4: Review diff checkpoint**

Run: `git diff -- shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/domain/usecase shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/time shared/src/commonTest/kotlin/com/etonealbert/examenmanejo/domain/usecase`

Expected: use cases, clock, and tests only. Do not commit unless the user explicitly requests a commit.

---

### Task 7: Koin Modules, App Startup, And Network Stubs

**Files:**
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/di/AppModules.kt`
- Create: `shared/src/androidMain/kotlin/com/etonealbert/examenmanejo/core/di/PlatformModules.android.kt`
- Create: `shared/src/iosMain/kotlin/com/etonealbert/examenmanejo/core/di/PlatformModules.ios.kt`
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/network/QuestionPackApi.kt`
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/network/NoOpQuestionPackApi.kt`
- Modify: `androidApp/src/main/kotlin/com/etonealbert/examenmanejo/MainActivity.kt`
- Modify: `shared/src/iosMain/kotlin/com/etonealbert/examenmanejo/MainViewController.kt`

**Interfaces:**
- Consumes repositories/use cases from Tasks 5 and 6.
- Produces Koin dependency graph and no-op Ktor-facing interfaces.

- [ ] **Step 1: Add network no-op interface**

Create:

```kotlin
package com.etonealbert.examenmanejo.network

interface QuestionPackApi { suspend fun fetchManifest(): Result<Unit> }
class NoOpQuestionPackApi : QuestionPackApi { override suspend fun fetchManifest(): Result<Unit> = Result.success(Unit) }
```

- [ ] **Step 2: Add Koin modules**

Create `AppModules.kt` with Koin modules for database provider, local data sources, repositories, use cases, ViewModels, coordinators, fake subscription, and `NoOpQuestionPackApi`.

Use `single` for repositories/use cases/coordinators and `factory` for ViewModels.

- [ ] **Step 3: Add platform modules**

Android module must provide `DriverFactory(context)`. iOS module must provide `DriverFactory()`.

- [ ] **Step 4: Wire app startup**

Modify Android `MainActivity` to start Koin before `setContent` if Koin has not started. Modify iOS `MainViewController` to start shared Koin through a common initialization function.

- [ ] **Step 5: Run compile**

Run: `./gradlew.bat :shared:compileKotlinMetadata :androidApp:compileDebugKotlin`

Expected: PASS.

- [ ] **Step 6: Review diff checkpoint**

Run: `git diff -- shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/di shared/src/androidMain/kotlin/com/etonealbert/examenmanejo/core/di shared/src/iosMain/kotlin/com/etonealbert/examenmanejo/core/di shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/network androidApp/src/main/kotlin/com/etonealbert/examenmanejo/MainActivity.kt shared/src/iosMain/kotlin/com/etonealbert/examenmanejo/MainViewController.kt`

Expected: DI/network/startup wiring only. Do not commit unless the user explicitly requests a commit.

---

### Task 8: Navigation Coordinators And Typed Routes

**Files:**
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/navigation/AppRoute.kt`
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/navigation/AppNavigator.kt`
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/navigation/NavigationEffect.kt`
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/navigation/RootCoordinator.kt`
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamCoordinator.kt`
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/study/StudyCoordinator.kt`
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/subscription/SubscriptionCoordinator.kt`

**Interfaces:**
- Produces primitive-only routes consumed by screens and ViewModels.

- [ ] **Step 1: Add route contracts**

Create:

```kotlin
package com.etonealbert.examenmanejo.core.navigation

sealed interface AppRoute {
    data object Onboarding : AppRoute
    data object Home : AppRoute
    data class Study(val licenseClassId: String) : AppRoute
    data class Exam(val licenseClassId: String) : AppRoute
    data class Result(val examSessionId: Long) : AppRoute
    data class Review(val examSessionId: Long) : AppRoute
    data object History : AppRoute
    data object Settings : AppRoute
    data class Paywall(val source: String) : AppRoute
    data class Legal(val documentId: String) : AppRoute
    data class Tutorial(val source: String) : AppRoute
}
```

Create:

```kotlin
package com.etonealbert.examenmanejo.core.navigation

interface AppNavigator {
    val currentRoute: kotlinx.coroutines.flow.StateFlow<AppRoute>
    fun navigate(route: AppRoute)
    fun replace(route: AppRoute)
    fun back()
}

sealed interface NavigationEffect {
    data class NavigateTo(val route: AppRoute) : NavigationEffect
    data class ReplaceWith(val route: AppRoute) : NavigationEffect
    data object NavigateBack : NavigationEffect
}
```

- [ ] **Step 2: Add simple in-memory navigator and coordinators**

Implement `RootCoordinator` with `suspend fun start(): AppRoute` that runs seed import and onboarding checks, then calls navigator `replace` with `Onboarding` or `Home`.

Implement `ExamCoordinator` methods:

```kotlin
fun openExam(licenseClassId: String)
fun openResult(examSessionId: Long)
fun openReview(examSessionId: Long)
fun closeToHome()
fun openHistory()
```

Implement `StudyCoordinator.openStudy(licenseClassId: String)` and `SubscriptionCoordinator.openPaywall(source: String)` with no billing logic.

- [ ] **Step 3: Run compile**

Run: `./gradlew.bat :shared:compileKotlinMetadata`

Expected: PASS.

- [ ] **Step 4: Review diff checkpoint**

Run: `git diff -- shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/navigation shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/exam/ExamCoordinator.kt shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/study/StudyCoordinator.kt shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/subscription/SubscriptionCoordinator.kt`

Expected: navigation/coordinator files only. Do not commit unless the user explicitly requests a commit.

---

### Task 9: Feature ViewModels And Simple Compose Screens

**Files:**
- Modify: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/App.kt`
- Create/modify files under `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature/{onboarding,home,study,exam,result,review,history,settings}/`
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/design/ExamenManejoTheme.kt`
- Create: `shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/design/AnswerOptionCard.kt`

**Interfaces:**
- Consumes use cases and navigation from prior tasks.
- Produces user-visible MVP flow.

- [ ] **Step 1: Add ViewModel state contracts**

For each feature, create `UiState`, `UiEvent`, `UiEffect`, and `ViewModel` files. Use this pattern:

```kotlin
class HomeViewModel(
    private val getLicenseClasses: GetLicenseClassesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val _effects = MutableSharedFlow<HomeUiEffect>()
    val effects: SharedFlow<HomeUiEffect> = _effects.asSharedFlow()
    fun onEvent(event: HomeUiEvent) { /* use explicit when branches */ }
}
```

Effects must use routes or primitive IDs only.

- [ ] **Step 2: Add simple screens**

Create screens with Spanish-first copy:

- Onboarding: offline/local-first explanation, complete button.
- Home: Class B card, start exam, study, history, settings.
- Study: list/first Class B demo question with immediate feedback.
- Exam: one question at a time, selected answer state, finish button.
- Result: pass/fail, score, review/history/home buttons.
- Review: snapshots with selected/correct answer.
- History: completed sessions newest first.
- Settings: local-data explanation, demo-content disclaimer, non-affiliation disclaimer.

- [ ] **Step 3: Replace generated `App()` sample**

Use Koin to obtain `AppNavigator`/coordinators and render the current route with a simple `when(route)` switch. This is acceptable for MVP coordinator skeleton if adding a full navigation library would be too disruptive.

- [ ] **Step 4: Run app compile**

Run: `./gradlew.bat :shared:compileKotlinMetadata :androidApp:assembleDebug`

Expected: PASS.

- [ ] **Step 5: Review diff checkpoint**

Run: `git diff -- shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/App.kt shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/feature shared/src/commonMain/kotlin/com/etonealbert/examenmanejo/core/design`

Expected: App root, feature ViewModels/screens, and design components only. Do not commit unless the user explicitly requests a commit.

---

### Task 10: Implementation Docs And Verification

**Files:**
- Create: `docs/implementation-status.md`
- Create: `docs/architecture-implemented.md`
- Create: `docs/mvp-vertical-slice.md`
- Modify: `README.md` only if build/test command updates are useful and safe.

**Interfaces:**
- Consumes completed implementation and verification outputs.
- Produces required project docs.

- [ ] **Step 1: Add implementation status doc**

Document:

- implemented architecture folders/files;
- SQLDelight schema status;
- seed import behavior;
- Class B demo content disclaimer;
- verification commands and results;
- known blocked items, including iOS simulator tests on Windows.

- [ ] **Step 2: Add architecture implemented doc**

Document:

- dependency direction;
- domain/data/presentation boundaries;
- repository and mapper responsibilities;
- Koin modules;
- coordinator route rules;
- why `minSdk=24` is retained.

- [ ] **Step 3: Add MVP vertical slice doc**

Document the exact user flow:

```text
App start -> seed import check -> onboarding status check -> onboarding or home -> Class B -> start exam -> answer -> finish -> result -> review -> history
```

State that result/review/history use snapshots and not mutable question content.

- [ ] **Step 4: Run final verification**

Run these commands on Windows:

```powershell
.\gradlew.bat :shared:compileKotlinMetadata
.\gradlew.bat :shared:testAndroidHostTest
.\gradlew.bat :androidApp:assembleDebug
```

Expected: PASS for all three. If a command fails, capture the exact failing task and error summary in `docs/implementation-status.md`.

- [ ] **Step 5: Confirm iOS verification boundary**

Do not run `:shared:iosSimulatorArm64Test` on Windows. Document: `iOS simulator tests require macOS and were not run on this Windows host.`

- [ ] **Step 6: Review final diff checkpoint**

Run: `git status --short` and `git diff --stat`

Expected: implementation and docs files only. Do not commit unless the user explicitly requests a commit.

---

## Plan Self-Review

Spec coverage:

- Architecture skeleton: Tasks 1, 2, 5, 6, 7, 8, 9.
- SQLDelight local source of truth: Tasks 1, 3, 5.
- Seed import/idempotency/demo content: Tasks 4, 5.
- Exam config and snapshots: Tasks 2, 3, 5, 6.
- Coordinator primitive routes: Task 8.
- Feature screens/ViewModels: Task 9.
- Koin and Ktor no-op interfaces: Task 7.
- Tests: Tasks 2, 4, 6, final Task 10 verification.
- Docs: Task 10.
- Windows verification and no iOS simulator on Windows: Task 10.

Type consistency:

- `ExamConfig`, `ExamQuestionSnapshot`, `ExamOptionSnapshot`, `ExamAnswer`, and `ExamResult` are defined before use-case/repository tasks consume them.
- Route types match approved primitive arguments.
- `ImportResult` matches the approved sealed interface.
- Settings keys match the approved `user_settings` persistence approach.

Commit policy:

- This plan uses review diff checkpoints instead of commit steps because commits require an explicit user request in this environment.
