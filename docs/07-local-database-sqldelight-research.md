# 07 — Local Database SQLDelight Research

**Project:** Kotlin Multiplatform driving-exam app for Argentina  
**Target runtime:** Android + iOS, offline-first/local-first MVP  
**Primary persistence:** SQLDelight 2.x over SQLite  
**Related document:** `06-data-parsing-import-pipeline.md`  
**Prepared:** 2026-06-22

---

## Executive summary

Use SQLDelight as the app's authoritative local database layer. Keep the database small, explicit, and deterministic:

1. Store **approved seed content** as normalized SQLite tables, not as raw JSON blobs.
2. Preserve **source metadata on every question** through a foreign key to `source_metadata`.
3. Keep **content data** and **user data** separate so question-pack upgrades do not erase progress.
4. Import versioned JSON packs idempotently inside a transaction.
5. Deprecate old questions instead of deleting them when they may appear in historical sessions.
6. Use SQLDelight-generated query APIs in repositories; do not leak SQLDelight row models directly into UI/domain layers.
7. Add full-text search only when search UX is needed; start with indexed `LIKE` search for MVP unless question volume or UX requires FTS5.

The recommended schema supports:

- License classes and subclasses.
- Questions and answer options.
- Source metadata.
- Exam sessions and answers.
- Study progress.
- Wrong-answer history and current wrong-answer queue.
- Favorite/bookmarked questions.
- User settings.
- Seed/import version tracking.
- Optional statistics cache.

---

## Research basis

SQLDelight generates type-safe Kotlin APIs from `.sq` files and verifies schema, SQL statements, and migrations at compile time. The current SQLDelight documentation shows version `2.3.2`, uses plugin id `app.cash.sqldelight`, and supports Android, Native/iOS, JVM, JS, and multiplatform SQLite drivers. SQLDelight's multiplatform setup places shared `.sq` files under `src/commonMain/sqldelight` and uses platform-specific drivers such as `android-driver` and `native-driver`. The official docs recommend a common `DriverFactory` using `expect/actual` to create the platform `SqlDriver`.

Relevant implementation facts:

- SQLDelight Gradle plugin: `id("app.cash.sqldelight") version "2.3.2"`.
- Shared KMP schema path: `src/commonMain/sqldelight`.
- Android driver dependency: `app.cash.sqldelight:android-driver:2.3.2`.
- iOS/native driver dependency: `app.cash.sqldelight:native-driver:2.3.2`.
- SQLDelight migration files are named `<version_to_upgrade_from>.sqm` and live in the same `sqldelight` source tree as `.sq` files.
- SQLDelight transactions roll back on exception and support `transaction` and `transactionWithResult`.
- SQLDelight supports defining FTS5 virtual tables, but FTS5 behavior depends on SQLite support on the target platform.
- SQLite FTS5 provides full-text search through virtual tables and `MATCH` queries; FTS tables have implicit `rowid` and do not use regular primary-key constraints.

---

## Recommended local database model

### Design principles

| Principle | Decision |
|---|---|
| Local-first | SQLite is the source of truth after import. The app can run without network access. |
| Deterministic seed import | JSON seed packs are imported with stable IDs, hashes, and upserts. |
| Content/user-state separation | Seed tables are versioned/deprecated; user tables reference stable question IDs. |
| Legal/source traceability | Every question points to `source_metadata`; source URL, license, jurisdiction, date, and hash are retained. |
| Historical session integrity | Exam sessions snapshot selected questions/options so later pack updates do not corrupt past exam results. |
| Low migration risk | Prefer additive schema migrations; keep destructive changes rare and backed by rebuild logic. |
| Small DB, fast startup | Import once, in batches, inside a transaction; do not parse large JSON on every launch. |

### Data ownership split

| Data type | Table examples | Update source | Delete policy |
|---|---|---|---|
| Seed/reference data | `license_class`, `source_metadata`, `question`, `answer_option`, `question_class_map` | App-bundled or remote question pack | Soft-deprecate, rarely hard-delete |
| User activity | `exam_session`, `exam_session_question`, `exam_answer`, `study_progress`, `wrong_answer_log`, `favorite_question` | Local app runtime | Never overwrite during seed import |
| App state/config | `user_setting`, `stats_cache`, `import_version` | Runtime + seed importer | Upsert by key/version |

---

## SQLDelight setup for Android and iOS

### Gradle setup

Use one shared SQLDelight database in the KMP shared module. Suggested generated database class name: `DrivingExamDatabase`.

```kotlin
plugins {
    kotlin("multiplatform")
    id("app.cash.sqldelight") version "2.3.2"
}

kotlin {
    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("app.cash.sqldelight:runtime:2.3.2")
                implementation("app.cash.sqldelight:coroutines-extensions:2.3.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:<version>")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("app.cash.sqldelight:android-driver:2.3.2")
            }
        }

        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("app.cash.sqldelight:native-driver:2.3.2")
            }
        }
    }
}

sqldelight {
    databases {
        create("DrivingExamDatabase") {
            packageName.set("com.yourapp.drivingexam.db")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
            verifyMigrations.set(true)
        }
    }
}
```

### Folder location

SQLDelight shared schema files should live under:

```text
shared/src/commonMain/sqldelight/com/yourapp/drivingexam/db/
```

Migration files should live under:

```text
shared/src/commonMain/sqldelight/migrations/
```

### iOS linker note

For native/iOS targets, SQLDelight can link SQLite automatically for dynamic frameworks through the Gradle `linkSqlite` behavior. For static frameworks, ensure the iOS/Xcode integration links `sqlite3` explicitly if required by your KMP framework configuration.

---

## Database driver factory pattern

Use `expect/actual` to hide platform driver creation from common code.

### commonMain

```kotlin
package com.yourapp.drivingexam.database

import app.cash.sqldelight.db.SqlDriver
import com.yourapp.drivingexam.db.DrivingExamDatabase

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): DrivingExamDatabase {
    val driver = driverFactory.createDriver()
    return DrivingExamDatabase(driver)
}
```

### androidMain

```kotlin
package com.yourapp.drivingexam.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.yourapp.drivingexam.db.DrivingExamDatabase

actual class DriverFactory(
    private val context: Context
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = DrivingExamDatabase.Schema,
            context = context,
            name = "driving_exam.db"
        )
    }
}
```

### iosMain

```kotlin
package com.yourapp.drivingexam.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.yourapp.drivingexam.db.DrivingExamDatabase

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = DrivingExamDatabase.Schema,
            name = "driving_exam.db"
        )
    }
}
```

### Recommended database provider

Create one `DrivingExamDatabase` instance per app process/session and inject repositories from it. Avoid creating many drivers or database instances from multiple screens.

```kotlin
class DatabaseProvider(driverFactory: DriverFactory) {
    val database: DrivingExamDatabase by lazy {
        createDatabase(driverFactory)
    }
}
```

---

## Schema organization

Split `.sq` files by bounded area. This keeps generated `*Queries` classes manageable.

```text
shared/src/commonMain/sqldelight/com/yourapp/drivingexam/db/
  Source.sq
  LicenseClass.sq
  Question.sq
  QuestionSearch.sq          # optional FTS5
  ImportVersion.sq
  ExamSession.sq
  StudyProgress.sq
  FavoriteQuestion.sq
  UserSetting.sq
  StatsCache.sq

shared/src/commonMain/sqldelight/migrations/
  1.sqm
  2.sqm
  3.sqm
```

Recommended generated query groups:

| File | Generated queries object | Responsibility |
|---|---|---|
| `Source.sq` | `SourceQueries` | Source metadata upserts/lookups. |
| `LicenseClass.sq` | `LicenseClassQueries` | License classes/subclasses. |
| `Question.sq` | `QuestionQueries` | Questions, options, categories, class mapping. |
| `QuestionSearch.sq` | `QuestionSearchQueries` | FTS5 search index, optional. |
| `ImportVersion.sq` | `ImportVersionQueries` | Seed/import version state. |
| `ExamSession.sq` | `ExamSessionQueries` | Exam lifecycle and answer recording. |
| `StudyProgress.sq` | `StudyProgressQueries` | Per-question practice metrics. |
| `FavoriteQuestion.sq` | `FavoriteQuestionQueries` | Bookmark state. |
| `UserSetting.sq` | `UserSettingQueries` | Lightweight settings. |
| `StatsCache.sq` | `StatsCacheQueries` | Optional dashboard cache. |

---

## Proposed `.sq` schema

The schema below uses conservative SQLite types (`TEXT`, `INTEGER`, `REAL`) and maps booleans as `0/1` integers in the repository layer. This avoids custom adapters in the first implementation.

```sql
-- Source.sq
CREATE TABLE source_metadata (
  source_id TEXT NOT NULL PRIMARY KEY,
  authority_name TEXT NOT NULL,
  jurisdiction_code TEXT NOT NULL,
  jurisdiction_level TEXT NOT NULL, -- national | provincial | municipal
  source_type TEXT NOT NULL,        -- law | official_page | pdf_manual | questionnaire | sign_resource | terms_page
  title TEXT NOT NULL,
  official_url TEXT NOT NULL,
  license_name TEXT,
  license_url TEXT,
  attribution_text TEXT,
  source_version_label TEXT,
  published_date TEXT,
  accessed_at_epoch_ms INTEGER NOT NULL,
  content_sha256 TEXT,
  legal_status TEXT NOT NULL,       -- approved | restricted | unknown | derived_only
  notes TEXT
);

upsertSource:
INSERT INTO source_metadata(
  source_id, authority_name, jurisdiction_code, jurisdiction_level, source_type,
  title, official_url, license_name, license_url, attribution_text,
  source_version_label, published_date, accessed_at_epoch_ms, content_sha256,
  legal_status, notes
)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
ON CONFLICT(source_id) DO UPDATE SET
  authority_name = excluded.authority_name,
  jurisdiction_code = excluded.jurisdiction_code,
  jurisdiction_level = excluded.jurisdiction_level,
  source_type = excluded.source_type,
  title = excluded.title,
  official_url = excluded.official_url,
  license_name = excluded.license_name,
  license_url = excluded.license_url,
  attribution_text = excluded.attribution_text,
  source_version_label = excluded.source_version_label,
  published_date = excluded.published_date,
  accessed_at_epoch_ms = excluded.accessed_at_epoch_ms,
  content_sha256 = excluded.content_sha256,
  legal_status = excluded.legal_status,
  notes = excluded.notes;

selectSourceById:
SELECT * FROM source_metadata WHERE source_id = ?;

selectSourcesForJurisdiction:
SELECT * FROM source_metadata
WHERE jurisdiction_code = ?
ORDER BY source_type, title;
```

```sql
-- LicenseClass.sq
CREATE TABLE license_class (
  class_id TEXT NOT NULL PRIMARY KEY,      -- A, B, C, D, E, F, G
  display_name TEXT NOT NULL,
  description TEXT,
  is_professional INTEGER NOT NULL DEFAULT 0,
  min_age INTEGER,
  sort_order INTEGER NOT NULL
);

CREATE TABLE license_subclass (
  subclass_id TEXT NOT NULL PRIMARY KEY,   -- A1.1, B1, C1, etc.
  class_id TEXT NOT NULL REFERENCES license_class(class_id) ON DELETE RESTRICT,
  display_name TEXT NOT NULL,
  description TEXT,
  min_age INTEGER,
  prerequisite_text TEXT,
  sort_order INTEGER NOT NULL
);

upsertLicenseClass:
INSERT INTO license_class(class_id, display_name, description, is_professional, min_age, sort_order)
VALUES (?, ?, ?, ?, ?, ?)
ON CONFLICT(class_id) DO UPDATE SET
  display_name = excluded.display_name,
  description = excluded.description,
  is_professional = excluded.is_professional,
  min_age = excluded.min_age,
  sort_order = excluded.sort_order;

upsertLicenseSubclass:
INSERT INTO license_subclass(subclass_id, class_id, display_name, description, min_age, prerequisite_text, sort_order)
VALUES (?, ?, ?, ?, ?, ?, ?)
ON CONFLICT(subclass_id) DO UPDATE SET
  class_id = excluded.class_id,
  display_name = excluded.display_name,
  description = excluded.description,
  min_age = excluded.min_age,
  prerequisite_text = excluded.prerequisite_text,
  sort_order = excluded.sort_order;

selectAllLicenseClasses:
SELECT * FROM license_class ORDER BY sort_order;

selectSubclassesForClass:
SELECT * FROM license_subclass WHERE class_id = ? ORDER BY sort_order;
```

```sql
-- Question.sq
CREATE TABLE question_category (
  category_id TEXT NOT NULL PRIMARY KEY,
  display_name TEXT NOT NULL,
  description TEXT,
  sort_order INTEGER NOT NULL
);

CREATE TABLE question (
  question_id TEXT NOT NULL PRIMARY KEY,
  pack_id TEXT NOT NULL,
  jurisdiction_code TEXT NOT NULL,
  category_id TEXT NOT NULL REFERENCES question_category(category_id) ON DELETE RESTRICT,
  source_id TEXT NOT NULL REFERENCES source_metadata(source_id) ON DELETE RESTRICT,

  stem TEXT NOT NULL,
  stem_normalized TEXT NOT NULL,
  explanation TEXT,
  image_asset_path TEXT,

  difficulty TEXT NOT NULL DEFAULT 'medium', -- easy | medium | hard
  is_eliminatory INTEGER NOT NULL DEFAULT 0,
  is_active INTEGER NOT NULL DEFAULT 1,
  deprecated_at_epoch_ms INTEGER,
  deprecation_reason TEXT,

  content_hash TEXT NOT NULL,
  source_revision_hash TEXT,
  created_at_epoch_ms INTEGER NOT NULL,
  updated_at_epoch_ms INTEGER NOT NULL
);

CREATE UNIQUE INDEX question_content_hash_unique ON question(content_hash);
CREATE INDEX question_jurisdiction_category_idx ON question(jurisdiction_code, category_id, is_active);
CREATE INDEX question_source_idx ON question(source_id);
CREATE INDEX question_pack_idx ON question(pack_id);

CREATE TABLE question_class_map (
  question_id TEXT NOT NULL REFERENCES question(question_id) ON DELETE CASCADE,
  class_id TEXT NOT NULL REFERENCES license_class(class_id) ON DELETE RESTRICT,
  subclass_id TEXT REFERENCES license_subclass(subclass_id) ON DELETE RESTRICT,
  PRIMARY KEY(question_id, class_id, subclass_id)
);

CREATE INDEX question_class_map_class_idx ON question_class_map(class_id, subclass_id);

CREATE TABLE answer_option (
  option_id TEXT NOT NULL PRIMARY KEY,
  question_id TEXT NOT NULL REFERENCES question(question_id) ON DELETE CASCADE,
  option_order INTEGER NOT NULL,
  option_text TEXT NOT NULL,
  option_text_normalized TEXT NOT NULL,
  is_correct INTEGER NOT NULL DEFAULT 0,
  option_image_asset_path TEXT,
  content_hash TEXT NOT NULL,
  UNIQUE(question_id, option_order),
  UNIQUE(question_id, content_hash)
);

CREATE INDEX answer_option_question_idx ON answer_option(question_id, option_order);

upsertCategory:
INSERT INTO question_category(category_id, display_name, description, sort_order)
VALUES (?, ?, ?, ?)
ON CONFLICT(category_id) DO UPDATE SET
  display_name = excluded.display_name,
  description = excluded.description,
  sort_order = excluded.sort_order;

upsertQuestion:
INSERT INTO question(
  question_id, pack_id, jurisdiction_code, category_id, source_id,
  stem, stem_normalized, explanation, image_asset_path,
  difficulty, is_eliminatory, is_active, deprecated_at_epoch_ms, deprecation_reason,
  content_hash, source_revision_hash, created_at_epoch_ms, updated_at_epoch_ms
)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
ON CONFLICT(question_id) DO UPDATE SET
  pack_id = excluded.pack_id,
  jurisdiction_code = excluded.jurisdiction_code,
  category_id = excluded.category_id,
  source_id = excluded.source_id,
  stem = excluded.stem,
  stem_normalized = excluded.stem_normalized,
  explanation = excluded.explanation,
  image_asset_path = excluded.image_asset_path,
  difficulty = excluded.difficulty,
  is_eliminatory = excluded.is_eliminatory,
  is_active = excluded.is_active,
  deprecated_at_epoch_ms = excluded.deprecated_at_epoch_ms,
  deprecation_reason = excluded.deprecation_reason,
  content_hash = excluded.content_hash,
  source_revision_hash = excluded.source_revision_hash,
  updated_at_epoch_ms = excluded.updated_at_epoch_ms;

insertQuestionClassMap:
INSERT OR IGNORE INTO question_class_map(question_id, class_id, subclass_id)
VALUES (?, ?, ?);

deleteClassMapForQuestion:
DELETE FROM question_class_map WHERE question_id = ?;

upsertAnswerOption:
INSERT INTO answer_option(
  option_id, question_id, option_order, option_text, option_text_normalized,
  is_correct, option_image_asset_path, content_hash
)
VALUES (?, ?, ?, ?, ?, ?, ?, ?)
ON CONFLICT(option_id) DO UPDATE SET
  question_id = excluded.question_id,
  option_order = excluded.option_order,
  option_text = excluded.option_text,
  option_text_normalized = excluded.option_text_normalized,
  is_correct = excluded.is_correct,
  option_image_asset_path = excluded.option_image_asset_path,
  content_hash = excluded.content_hash;

deleteOptionsForQuestion:
DELETE FROM answer_option WHERE question_id = ?;

selectQuestionById:
SELECT * FROM question WHERE question_id = ?;

selectOptionsForQuestion:
SELECT * FROM answer_option
WHERE question_id = ?
ORDER BY option_order;

selectActiveQuestionsForClassAndCategory:
SELECT DISTINCT q.*
FROM question q
JOIN question_class_map m ON m.question_id = q.question_id
WHERE q.is_active = 1
  AND q.jurisdiction_code = ?
  AND m.class_id = ?
  AND (? IS NULL OR q.category_id = ?)
ORDER BY q.category_id, q.question_id;

selectRandomPracticeQuestions:
SELECT DISTINCT q.*
FROM question q
JOIN question_class_map m ON m.question_id = q.question_id
WHERE q.is_active = 1
  AND q.jurisdiction_code = ?
  AND m.class_id = ?
  AND (? IS NULL OR q.category_id = ?)
ORDER BY RANDOM()
LIMIT ?;

selectRandomExamQuestions:
SELECT DISTINCT q.*
FROM question q
JOIN question_class_map m ON m.question_id = q.question_id
WHERE q.is_active = 1
  AND q.jurisdiction_code = ?
  AND m.class_id = ?
ORDER BY RANDOM()
LIMIT ?;

searchQuestionsLike:
SELECT * FROM question
WHERE is_active = 1
  AND jurisdiction_code = ?
  AND stem_normalized LIKE '%' || ? || '%'
ORDER BY updated_at_epoch_ms DESC
LIMIT ?;

softDeprecateQuestion:
UPDATE question
SET is_active = 0,
    deprecated_at_epoch_ms = ?,
    deprecation_reason = ?,
    updated_at_epoch_ms = ?
WHERE question_id = ?;

countActiveQuestions:
SELECT COUNT(*) FROM question
WHERE is_active = 1
  AND jurisdiction_code = ?;
```

```sql
-- QuestionSearch.sq, optional FTS5 module
CREATE VIRTUAL TABLE question_fts USING fts5(
  question_id UNINDEXED,
  jurisdiction_code UNINDEXED,
  stem,
  explanation,
  content=''
);

insertQuestionFts:
INSERT INTO question_fts(question_id, jurisdiction_code, stem, explanation)
VALUES (?, ?, ?, ?);

deleteQuestionFts:
DELETE FROM question_fts WHERE question_id = ?;

searchQuestionFts:
SELECT
  rowid AS rowid,
  question_id,
  jurisdiction_code,
  rank AS rank
FROM question_fts
WHERE question_fts MATCH ?
  AND jurisdiction_code = ?
ORDER BY rank
LIMIT ?;
```

```sql
-- ImportVersion.sq
CREATE TABLE import_version (
  pack_id TEXT NOT NULL PRIMARY KEY,
  pack_version INTEGER NOT NULL,
  jurisdiction_code TEXT NOT NULL,
  content_schema_version INTEGER NOT NULL,
  app_min_version TEXT,
  imported_at_epoch_ms INTEGER NOT NULL,
  pack_sha256 TEXT NOT NULL,
  question_count INTEGER NOT NULL,
  source_count INTEGER NOT NULL,
  status TEXT NOT NULL, -- imported | failed | rolled_back
  notes TEXT
);

CREATE TABLE import_log (
  import_log_id TEXT NOT NULL PRIMARY KEY,
  pack_id TEXT NOT NULL,
  started_at_epoch_ms INTEGER NOT NULL,
  finished_at_epoch_ms INTEGER,
  status TEXT NOT NULL, -- started | success | failed | skipped
  message TEXT
);

upsertImportVersion:
INSERT INTO import_version(
  pack_id, pack_version, jurisdiction_code, content_schema_version, app_min_version,
  imported_at_epoch_ms, pack_sha256, question_count, source_count, status, notes
)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
ON CONFLICT(pack_id) DO UPDATE SET
  pack_version = excluded.pack_version,
  jurisdiction_code = excluded.jurisdiction_code,
  content_schema_version = excluded.content_schema_version,
  app_min_version = excluded.app_min_version,
  imported_at_epoch_ms = excluded.imported_at_epoch_ms,
  pack_sha256 = excluded.pack_sha256,
  question_count = excluded.question_count,
  source_count = excluded.source_count,
  status = excluded.status,
  notes = excluded.notes;

selectImportedPack:
SELECT * FROM import_version WHERE pack_id = ?;

selectLatestPackForJurisdiction:
SELECT * FROM import_version
WHERE jurisdiction_code = ? AND status = 'imported'
ORDER BY pack_version DESC
LIMIT 1;

insertImportLog:
INSERT INTO import_log(import_log_id, pack_id, started_at_epoch_ms, finished_at_epoch_ms, status, message)
VALUES (?, ?, ?, ?, ?, ?);

updateImportLogFinished:
UPDATE import_log
SET finished_at_epoch_ms = ?, status = ?, message = ?
WHERE import_log_id = ?;
```

```sql
-- ExamSession.sq
CREATE TABLE exam_session (
  session_id TEXT NOT NULL PRIMARY KEY,
  jurisdiction_code TEXT NOT NULL,
  class_id TEXT NOT NULL REFERENCES license_class(class_id) ON DELETE RESTRICT,
  subclass_id TEXT REFERENCES license_subclass(subclass_id) ON DELETE RESTRICT,
  pack_id TEXT NOT NULL,
  mode TEXT NOT NULL, -- exam | practice | review_wrong | category_drill
  status TEXT NOT NULL, -- in_progress | completed | abandoned
  total_questions INTEGER NOT NULL,
  correct_count INTEGER NOT NULL DEFAULT 0,
  incorrect_count INTEGER NOT NULL DEFAULT 0,
  score_percent REAL,
  passed INTEGER,
  started_at_epoch_ms INTEGER NOT NULL,
  completed_at_epoch_ms INTEGER,
  duration_ms INTEGER
);

CREATE INDEX exam_session_status_idx ON exam_session(status, started_at_epoch_ms);
CREATE INDEX exam_session_jurisdiction_class_idx ON exam_session(jurisdiction_code, class_id, started_at_epoch_ms);

CREATE TABLE exam_session_question (
  session_id TEXT NOT NULL REFERENCES exam_session(session_id) ON DELETE CASCADE,
  question_id TEXT NOT NULL REFERENCES question(question_id) ON DELETE RESTRICT,
  question_order INTEGER NOT NULL,
  question_stem_snapshot TEXT NOT NULL,
  explanation_snapshot TEXT,
  option_order_json TEXT NOT NULL, -- JSON array of option IDs in display order
  correct_option_id_snapshot TEXT NOT NULL,
  PRIMARY KEY(session_id, question_id),
  UNIQUE(session_id, question_order)
);

CREATE TABLE exam_answer (
  session_id TEXT NOT NULL REFERENCES exam_session(session_id) ON DELETE CASCADE,
  question_id TEXT NOT NULL REFERENCES question(question_id) ON DELETE RESTRICT,
  selected_option_id TEXT REFERENCES answer_option(option_id) ON DELETE SET NULL,
  is_correct INTEGER NOT NULL,
  answered_at_epoch_ms INTEGER NOT NULL,
  elapsed_ms INTEGER,
  changed_count INTEGER NOT NULL DEFAULT 0,
  PRIMARY KEY(session_id, question_id)
);

insertExamSession:
INSERT INTO exam_session(
  session_id, jurisdiction_code, class_id, subclass_id, pack_id, mode, status,
  total_questions, correct_count, incorrect_count, score_percent, passed,
  started_at_epoch_ms, completed_at_epoch_ms, duration_ms
)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);

insertSessionQuestion:
INSERT INTO exam_session_question(
  session_id, question_id, question_order, question_stem_snapshot,
  explanation_snapshot, option_order_json, correct_option_id_snapshot
)
VALUES (?, ?, ?, ?, ?, ?, ?);

upsertExamAnswer:
INSERT INTO exam_answer(session_id, question_id, selected_option_id, is_correct, answered_at_epoch_ms, elapsed_ms, changed_count)
VALUES (?, ?, ?, ?, ?, ?, ?)
ON CONFLICT(session_id, question_id) DO UPDATE SET
  selected_option_id = excluded.selected_option_id,
  is_correct = excluded.is_correct,
  answered_at_epoch_ms = excluded.answered_at_epoch_ms,
  elapsed_ms = excluded.elapsed_ms,
  changed_count = exam_answer.changed_count + 1;

selectSessionQuestions:
SELECT * FROM exam_session_question
WHERE session_id = ?
ORDER BY question_order;

selectAnswersForSession:
SELECT * FROM exam_answer
WHERE session_id = ?;

completeExamSession:
UPDATE exam_session
SET status = 'completed',
    correct_count = ?,
    incorrect_count = ?,
    score_percent = ?,
    passed = ?,
    completed_at_epoch_ms = ?,
    duration_ms = ?
WHERE session_id = ?;

selectRecentSessions:
SELECT * FROM exam_session
ORDER BY started_at_epoch_ms DESC
LIMIT ?;
```

```sql
-- StudyProgress.sq
CREATE TABLE study_progress (
  question_id TEXT NOT NULL PRIMARY KEY REFERENCES question(question_id) ON DELETE CASCADE,
  seen_count INTEGER NOT NULL DEFAULT 0,
  correct_count INTEGER NOT NULL DEFAULT 0,
  incorrect_count INTEGER NOT NULL DEFAULT 0,
  streak_correct INTEGER NOT NULL DEFAULT 0,
  last_seen_at_epoch_ms INTEGER,
  last_answered_at_epoch_ms INTEGER,
  last_selected_option_id TEXT,
  last_is_correct INTEGER,
  mastery_level INTEGER NOT NULL DEFAULT 0, -- 0..5
  next_review_at_epoch_ms INTEGER
);

CREATE INDEX study_progress_next_review_idx ON study_progress(next_review_at_epoch_ms);
CREATE INDEX study_progress_mastery_idx ON study_progress(mastery_level, incorrect_count);

CREATE TABLE wrong_answer_log (
  wrong_answer_id TEXT NOT NULL PRIMARY KEY,
  question_id TEXT NOT NULL REFERENCES question(question_id) ON DELETE CASCADE,
  session_id TEXT REFERENCES exam_session(session_id) ON DELETE SET NULL,
  selected_option_id TEXT REFERENCES answer_option(option_id) ON DELETE SET NULL,
  correct_option_id TEXT REFERENCES answer_option(option_id) ON DELETE SET NULL,
  answered_at_epoch_ms INTEGER NOT NULL,
  resolved_at_epoch_ms INTEGER
);

CREATE INDEX wrong_answer_unresolved_idx ON wrong_answer_log(question_id, resolved_at_epoch_ms);

upsertProgressAfterAnswer:
INSERT INTO study_progress(
  question_id, seen_count, correct_count, incorrect_count, streak_correct,
  last_seen_at_epoch_ms, last_answered_at_epoch_ms, last_selected_option_id,
  last_is_correct, mastery_level, next_review_at_epoch_ms
)
VALUES (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?)
ON CONFLICT(question_id) DO UPDATE SET
  seen_count = study_progress.seen_count + 1,
  correct_count = study_progress.correct_count + excluded.correct_count,
  incorrect_count = study_progress.incorrect_count + excluded.incorrect_count,
  streak_correct = excluded.streak_correct,
  last_seen_at_epoch_ms = excluded.last_seen_at_epoch_ms,
  last_answered_at_epoch_ms = excluded.last_answered_at_epoch_ms,
  last_selected_option_id = excluded.last_selected_option_id,
  last_is_correct = excluded.last_is_correct,
  mastery_level = excluded.mastery_level,
  next_review_at_epoch_ms = excluded.next_review_at_epoch_ms;

insertWrongAnswerLog:
INSERT INTO wrong_answer_log(
  wrong_answer_id, question_id, session_id, selected_option_id,
  correct_option_id, answered_at_epoch_ms, resolved_at_epoch_ms
)
VALUES (?, ?, ?, ?, ?, ?, ?);

markWrongAnswerResolvedForQuestion:
UPDATE wrong_answer_log
SET resolved_at_epoch_ms = ?
WHERE question_id = ? AND resolved_at_epoch_ms IS NULL;

selectWrongAnswerQueue:
SELECT q.*
FROM question q
JOIN wrong_answer_log w ON w.question_id = q.question_id
WHERE q.is_active = 1
  AND w.resolved_at_epoch_ms IS NULL
GROUP BY q.question_id
ORDER BY MAX(w.answered_at_epoch_ms) DESC
LIMIT ?;

selectDueReviewQuestions:
SELECT q.*
FROM question q
JOIN study_progress p ON p.question_id = q.question_id
WHERE q.is_active = 1
  AND p.next_review_at_epoch_ms IS NOT NULL
  AND p.next_review_at_epoch_ms <= ?
ORDER BY p.next_review_at_epoch_ms ASC
LIMIT ?;
```

```sql
-- FavoriteQuestion.sq
CREATE TABLE favorite_question (
  question_id TEXT NOT NULL PRIMARY KEY REFERENCES question(question_id) ON DELETE CASCADE,
  created_at_epoch_ms INTEGER NOT NULL,
  note TEXT
);

upsertFavorite:
INSERT INTO favorite_question(question_id, created_at_epoch_ms, note)
VALUES (?, ?, ?)
ON CONFLICT(question_id) DO UPDATE SET
  note = excluded.note;

deleteFavorite:
DELETE FROM favorite_question WHERE question_id = ?;

isFavorite:
SELECT COUNT(*) FROM favorite_question WHERE question_id = ?;

selectFavoriteQuestions:
SELECT q.*
FROM question q
JOIN favorite_question f ON f.question_id = q.question_id
WHERE q.is_active = 1
ORDER BY f.created_at_epoch_ms DESC;
```

```sql
-- UserSetting.sq
CREATE TABLE user_setting (
  setting_key TEXT NOT NULL PRIMARY KEY,
  setting_value TEXT NOT NULL,
  value_type TEXT NOT NULL, -- string | int | bool | json
  updated_at_epoch_ms INTEGER NOT NULL
);

upsertSetting:
INSERT INTO user_setting(setting_key, setting_value, value_type, updated_at_epoch_ms)
VALUES (?, ?, ?, ?)
ON CONFLICT(setting_key) DO UPDATE SET
  setting_value = excluded.setting_value,
  value_type = excluded.value_type,
  updated_at_epoch_ms = excluded.updated_at_epoch_ms;

selectSetting:
SELECT * FROM user_setting WHERE setting_key = ?;

selectAllSettings:
SELECT * FROM user_setting ORDER BY setting_key;
```

```sql
-- StatsCache.sq
CREATE TABLE stats_cache (
  cache_key TEXT NOT NULL PRIMARY KEY,
  cache_value_json TEXT NOT NULL,
  computed_at_epoch_ms INTEGER NOT NULL,
  expires_at_epoch_ms INTEGER
);

upsertStatsCache:
INSERT INTO stats_cache(cache_key, cache_value_json, computed_at_epoch_ms, expires_at_epoch_ms)
VALUES (?, ?, ?, ?)
ON CONFLICT(cache_key) DO UPDATE SET
  cache_value_json = excluded.cache_value_json,
  computed_at_epoch_ms = excluded.computed_at_epoch_ms,
  expires_at_epoch_ms = excluded.expires_at_epoch_ms;

selectStatsCache:
SELECT * FROM stats_cache
WHERE cache_key = ?
  AND (expires_at_epoch_ms IS NULL OR expires_at_epoch_ms > ?);

clearExpiredStatsCache:
DELETE FROM stats_cache
WHERE expires_at_epoch_ms IS NOT NULL AND expires_at_epoch_ms <= ?;
```

---

## Query list

### Source queries

- `upsertSource`
- `selectSourceById`
- `selectSourcesForJurisdiction`

### License queries

- `upsertLicenseClass`
- `upsertLicenseSubclass`
- `selectAllLicenseClasses`
- `selectSubclassesForClass`

### Question/content queries

- `upsertCategory`
- `upsertQuestion`
- `insertQuestionClassMap`
- `deleteClassMapForQuestion`
- `upsertAnswerOption`
- `deleteOptionsForQuestion`
- `selectQuestionById`
- `selectOptionsForQuestion`
- `selectActiveQuestionsForClassAndCategory`
- `selectRandomPracticeQuestions`
- `selectRandomExamQuestions`
- `searchQuestionsLike`
- `softDeprecateQuestion`
- `countActiveQuestions`

### FTS queries, optional

- `insertQuestionFts`
- `deleteQuestionFts`
- `searchQuestionFts`

### Import/version queries

- `upsertImportVersion`
- `selectImportedPack`
- `selectLatestPackForJurisdiction`
- `insertImportLog`
- `updateImportLogFinished`

### Exam queries

- `insertExamSession`
- `insertSessionQuestion`
- `upsertExamAnswer`
- `selectSessionQuestions`
- `selectAnswersForSession`
- `completeExamSession`
- `selectRecentSessions`

### Progress/wrong-answer queries

- `upsertProgressAfterAnswer`
- `insertWrongAnswerLog`
- `markWrongAnswerResolvedForQuestion`
- `selectWrongAnswerQueue`
- `selectDueReviewQuestions`

### Favorite queries

- `upsertFavorite`
- `deleteFavorite`
- `isFavorite`
- `selectFavoriteQuestions`

### Settings/statistics queries

- `upsertSetting`
- `selectSetting`
- `selectAllSettings`
- `upsertStatsCache`
- `selectStatsCache`
- `clearExpiredStatsCache`

---

## Data access layer design

### Package layout

```text
shared/src/commonMain/kotlin/com/yourapp/drivingexam/
  database/
    DriverFactory.kt
    DatabaseProvider.kt
    DbDispatchers.kt
  data/local/
    SourceLocalDataSource.kt
    LicenseLocalDataSource.kt
    QuestionLocalDataSource.kt
    ExamLocalDataSource.kt
    ProgressLocalDataSource.kt
    SettingsLocalDataSource.kt
    ImportLocalDataSource.kt
  data/repository/
    SourceRepository.kt
    LicenseRepository.kt
    QuestionRepository.kt
    ExamRepository.kt
    StudyRepository.kt
    SettingsRepository.kt
    ImportRepository.kt
  domain/model/
    Question.kt
    AnswerOption.kt
    ExamSession.kt
    StudyProgress.kt
    QuestionPack.kt
  domain/usecase/
    StartExamUseCase.kt
    SubmitAnswerUseCase.kt
    ImportSeedPackUseCase.kt
    GetWrongAnswerQueueUseCase.kt
```

### Layer responsibilities

| Layer | Responsibility | Must not do |
|---|---|---|
| SQLDelight queries | Execute SQL and return generated row types | Business rules, JSON parsing, UI formatting |
| Local data source | Thin wrapper around generated queries and transactions | Expose SQLDelight models to UI |
| Repository | Map database rows to domain models; coordinate multi-table reads/writes | Parse official source PDFs; perform legal review |
| Use case | App workflow: start exam, import pack, submit answer, compute result | Contain SQL strings |

### Coroutine guidance

Use SQLDelight coroutine extensions for query observation where useful:

- `favorites` list.
- progress dashboard.
- current session answer changes.
- settings.

Use `asFlow().mapToList(Dispatchers.Default or database dispatcher)` for read flows. Keep imports and bulk writes on a background dispatcher.

---

## Repository mapping strategy

### Do not leak SQLDelight row classes

SQLDelight will generate classes from table/query result shapes. Treat those as persistence DTOs. Convert to domain models at repository boundaries.

```kotlin
data class Question(
    val id: String,
    val jurisdictionCode: String,
    val categoryId: String,
    val stem: String,
    val explanation: String?,
    val imageAssetPath: String?,
    val difficulty: Difficulty,
    val isEliminatory: Boolean,
    val source: SourceMetadata,
    val options: List<AnswerOption>,
)
```

Mapping example:

```kotlin
private fun QuestionRow.toDomain(
    source: SourceMetadata,
    options: List<AnswerOption>
): Question = Question(
    id = question_id,
    jurisdictionCode = jurisdiction_code,
    categoryId = category_id,
    stem = stem,
    explanation = explanation,
    imageAssetPath = image_asset_path,
    difficulty = Difficulty.fromDb(difficulty),
    isEliminatory = is_eliminatory == 1L,
    source = source,
    options = options
)
```

### Boolean and enum mapping

For MVP, store booleans as `INTEGER` values `0/1` and enums as validated `TEXT`. Repository functions should centralize conversion:

```kotlin
fun Boolean.toDbInt(): Long = if (this) 1L else 0L
fun Long.toBooleanFlag(): Boolean = this != 0L
```

For stricter typing later, add SQLDelight column adapters for domain-specific types such as `Difficulty`, `JurisdictionCode`, or `EpochMillis`.

### Snapshot strategy for exam sessions

When an exam starts, insert `exam_session_question` rows with snapshots:

- stem text at session start.
- explanation at session start.
- option display order.
- correct option ID at session start.

This prevents old exam results from changing meaning after a question pack update edits wording, removes an option, or changes the correct answer.

---

## Migration strategy

### Schema migration rules

1. `.sq` files always represent the latest schema for a new database.
2. Every released schema change requires a migration file in `migrations/<oldVersion>.sqm`.
3. Use `ALTER TABLE ADD COLUMN` with safe defaults for additive changes.
4. Avoid destructive migrations for user tables.
5. Never drop `exam_session`, `exam_answer`, `study_progress`, `wrong_answer_log`, or `favorite_question` without a staged export/import migration.
6. Do not wrap `.sqm` migration files in manual `BEGIN/END TRANSACTION`; SQLDelight handles migration transactions when the driver supports it.
7. Use SQLDelight migration verification in CI.
8. Generate and commit schema snapshots using `schemaOutputDirectory` before the first public release.
9. Keep content versioning separate from DB schema versioning:
   - DB schema version: SQLDelight `Schema.version`.
   - Question content version: `import_version.pack_version` and `pack_sha256`.

### Example migration file

`migrations/1.sqm`:

```sql
ALTER TABLE question ADD COLUMN source_revision_hash TEXT;
ALTER TABLE question ADD COLUMN deprecation_reason TEXT;
CREATE INDEX question_pack_idx ON question(pack_id);
```

### Release migration checklist

- Run unit tests against a fresh DB.
- Run migration tests from last production schema to current schema.
- Verify import of latest seed pack after migration.
- Verify old exam sessions still render.
- Verify favorites and progress survive.
- Verify deprecated questions are hidden from new exams but visible in old session review.

---

## Seed import algorithm

### Input package assumption

The production seed package should be a compressed JSON asset such as:

```text
composeApp/src/commonMain/composeResources/files/seed/argentina-pba-b-v0007.json.gz
```

Recommended top-level JSON fields:

```json
{
  "pack_id": "argentina-pba-b",
  "pack_version": 7,
  "content_schema_version": 1,
  "jurisdiction_code": "AR-B",
  "app_min_version": "1.0.0",
  "pack_sha256": "...",
  "sources": [],
  "license_classes": [],
  "categories": [],
  "questions": []
}
```

### Idempotent import rules

- Skip import when local `import_version.pack_id` exists with the same `pack_version` and `pack_sha256`.
- Run all writes in a single SQLDelight transaction.
- Upsert source metadata first.
- Upsert license classes/subclasses.
- Upsert categories.
- Upsert questions.
- Replace class mappings per question.
- Replace answer options per question.
- Update FTS rows if FTS is enabled.
- Mark old questions from the same `pack_id` as deprecated if they are not present in the new pack.
- Insert/update `import_version` last, after all content writes succeed.
- On exception, transaction rollback leaves the previous pack intact.

### Pseudocode

```kotlin
suspend fun importSeedPack(pack: QuestionPack) = withContext(dbDispatcher) {
    validatePackOrThrow(pack)

    val existing = importQueries
        .selectImportedPack(pack.packId)
        .executeAsOneOrNull()

    if (existing != null &&
        existing.pack_version == pack.version.toLong() &&
        existing.pack_sha256 == pack.sha256
    ) {
        return@withContext ImportResult.Skipped
    }

    val importLogId = uuid()
    importQueries.insertImportLog(
        import_log_id = importLogId,
        pack_id = pack.packId,
        started_at_epoch_ms = nowMs(),
        finished_at_epoch_ms = null,
        status = "started",
        message = null
    )

    try {
        database.transaction {
            pack.sources.forEach { source ->
                sourceQueries.upsertSource(source.toDb())
            }

            pack.licenseClasses.forEach { licenseClass ->
                licenseClassQueries.upsertLicenseClass(licenseClass.toDb())
                licenseClass.subclasses.forEach { subclass ->
                    licenseClassQueries.upsertLicenseSubclass(subclass.toDb())
                }
            }

            pack.categories.forEach { category ->
                questionQueries.upsertCategory(category.toDb())
            }

            val newQuestionIds = mutableSetOf<String>()

            pack.questions.forEach { question ->
                newQuestionIds += question.id

                questionQueries.upsertQuestion(question.toDb(pack))

                questionQueries.deleteClassMapForQuestion(question.id)
                question.classMappings.forEach { mapping ->
                    questionQueries.insertQuestionClassMap(
                        question_id = question.id,
                        class_id = mapping.classId,
                        subclass_id = mapping.subclassId
                    )
                }

                questionQueries.deleteOptionsForQuestion(question.id)
                question.options.forEachIndexed { index, option ->
                    questionQueries.upsertAnswerOption(option.toDb(index))
                }

                if (ftsEnabled) {
                    questionSearchQueries.deleteQuestionFts(question.id)
                    questionSearchQueries.insertQuestionFts(
                        question_id = question.id,
                        jurisdiction_code = question.jurisdictionCode,
                        stem = question.stem,
                        explanation = question.explanation.orEmpty()
                    )
                }
            }

            deprecateMissingQuestions(
                packId = pack.packId,
                keepQuestionIds = newQuestionIds,
                nowMs = nowMs()
            )

            importQueries.upsertImportVersion(pack.toDbImportVersion(nowMs()))
        }

        importQueries.updateImportLogFinished(
            finished_at_epoch_ms = nowMs(),
            status = "success",
            message = "Imported ${pack.questions.size} questions",
            import_log_id = importLogId
        )

        ImportResult.Imported
    } catch (t: Throwable) {
        importQueries.updateImportLogFinished(
            finished_at_epoch_ms = nowMs(),
            status = "failed",
            message = t.message,
            import_log_id = importLogId
        )
        throw t
    }
}
```

### First-launch strategy

On first launch:

1. Open database.
2. Read latest `import_version` for the default jurisdiction.
3. If missing, read bundled JSON seed pack from app resources.
4. Validate package hash and schema version.
5. Import in one transaction.
6. Save default user settings such as selected jurisdiction/class if missing.
7. Continue to home screen.

Avoid blocking the UI thread. Show a short loading screen if the first seed pack is large.

---

## Import performance

### Expected scale

For a driving-exam app, local data is likely small:

- Hundreds to low thousands of questions per jurisdiction/class.
- 3–5 answer options per question.
- Small source metadata table.
- User activity tables growing over time.

This is well within SQLite and SQLDelight comfort range.

### Performance rules

| Concern | Recommendation |
|---|---|
| Large JSON parse | Use streaming or chunked parsing if packs exceed several MB. For MVP, `kotlinx.serialization` full parse is acceptable if pack is small. |
| Many inserts | Wrap the whole import in `database.transaction {}`. |
| Option/class-map replacement | Delete and reinsert children per question within transaction. |
| Index overhead | Keep indexes targeted; excessive indexes slow import. |
| Random selection | OK with `ORDER BY RANDOM()` for small question banks; replace if table grows large. |
| FTS index | Build/update only on import, not on every UI search. |
| Images | Store image assets as bundled files/paths; do not store binary images in SQLite unless there is a strong reason. |

### When to optimize `ORDER BY RANDOM()`

`ORDER BY RANDOM()` is simple and acceptable for small tables. If a jurisdiction grows above roughly 10,000 active questions or random selection becomes slow on low-end devices, use one of these alternatives:

1. Add a `random_bucket INTEGER` column generated during pack build and sample by bucket.
2. Select candidate IDs using indexed filters, shuffle in Kotlin, then fetch selected rows by IDs.
3. Maintain per-category question ID lists in memory after app start.

For exam generation, correctness and balanced sampling matter more than micro-optimization. Prefer deterministic category quotas and then randomize inside each category.

---

## Random question selection strategy

### Practice mode

Practice can be simple:

```sql
SELECT DISTINCT q.*
FROM question q
JOIN question_class_map m ON m.question_id = q.question_id
WHERE q.is_active = 1
  AND q.jurisdiction_code = ?
  AND m.class_id = ?
  AND (? IS NULL OR q.category_id = ?)
ORDER BY RANDOM()
LIMIT ?;
```

### Exam mode with category quotas

For real exam simulation, do selection in repository code:

1. Load exam config for jurisdiction/class:
   - total questions.
   - pass threshold.
   - category distribution.
   - eliminatory-question requirements.
2. For each category quota, query candidate IDs.
3. Shuffle IDs in Kotlin.
4. Pick required count.
5. Ensure required eliminatory questions are included if applicable.
6. Insert session question snapshots in randomized display order.

This avoids a single opaque SQL query and makes exam rules easier to test.

---

## Full-text search possibility

### MVP recommendation

Start without FTS unless search is a primary feature. Use `stem_normalized LIKE '%' || query || '%'` with indexes on jurisdiction/category for early filtering.

### When to enable FTS5

Enable FTS5 if:

- Users need fast search across question stems and explanations.
- There are thousands of questions across several jurisdictions.
- You want ranked search results.
- You want prefix or token-based matching.

### FTS5 design

Use an external or contentless FTS table:

```sql
CREATE VIRTUAL TABLE question_fts USING fts5(
  question_id UNINDEXED,
  jurisdiction_code UNINDEXED,
  stem,
  explanation,
  content=''
);
```

Update it during seed import. Do not rely on SQLite triggers initially; explicit updates are easier to reason about during idempotent imports.

### FTS risks

- FTS5 support depends on the SQLite build available to each platform/driver.
- FTS virtual tables have different constraints than normal tables.
- SQLDelight requires a workaround for hidden/ranking columns: select `rank AS rank` and `rowid AS rowid` explicitly.
- Accent-insensitive Spanish search may require app-side normalization or tokenizer configuration. For MVP, keep `stem_normalized` and search both normalized LIKE and FTS if needed.

---

## Data versioning

Use three distinct versions:

| Version type | Stored where | Purpose |
|---|---|---|
| App version | App metadata | Controls app release compatibility. |
| DB schema version | SQLDelight `DrivingExamDatabase.Schema.version` | Controls migrations. |
| Content pack version | `import_version.pack_version` + `pack_sha256` | Controls seed/import updates. |

### Pack identity

Recommended naming:

```text
<country>-<jurisdiction>-<class>-v<version>
argentina-pba-b-v0007
argentina-caba-b-v0003
argentina-national-a-v0002
```

### Pack compatibility rules

- `content_schema_version` must be supported by the app.
- `app_min_version` must be less than or equal to the installed app version.
- If a pack is older than the installed local pack, do not downgrade unless explicitly requested for testing.
- If a pack hash does not match its manifest, reject it.

---

## Soft deletion and deprecation

Never hard-delete questions that may be referenced by historical user data.

### Deprecation fields

`question` includes:

- `is_active INTEGER NOT NULL DEFAULT 1`
- `deprecated_at_epoch_ms INTEGER`
- `deprecation_reason TEXT`

### Rules

| Scenario | Action |
|---|---|
| Typo fix, same meaning | Update question in place if ID remains stable and old sessions snapshot wording. |
| Correct answer changed | Create a new question ID or update question with a new `content_hash`; consider deprecating old question for future exams. |
| Source no longer valid | Mark affected questions inactive and set reason. |
| Duplicate discovered | Keep canonical active question; deprecate duplicate. |
| Legal concern | Mark inactive immediately; retain for local historical references if already imported. |
| Old exam session references inactive question | Still render from `exam_session_question` snapshot. |

### Canonical ID policy

Use stable IDs generated by the content pipeline, not local autoincrement IDs. Example:

```text
AR-PBA-B-Q-000123
AR-CABA-B-Q-000045
```

If a question changes materially, assign a new ID and deprecate the old ID. If a spelling/punctuation issue is fixed, keep the ID and update `content_hash`.

---

## Local backup concerns

### Android

Android Auto Backup includes many app data locations by default, including SQLite databases under the app database path. The platform applies a per-app backup quota and lets developers include/exclude data through backup rules.

Recommendation:

- Back up **user state** if useful: settings, favorites, progress, exam history.
- Avoid backing up large, recreatable seed content if it risks quota bloat.
- Prefer a future explicit app-level export format for user progress, not raw SQLite backup.
- Configure backup rules deliberately rather than relying on defaults.
- Do not back up device-specific identifiers or remote tokens.

### iOS

Apple guidance is to exclude files that can be recreated or redownloaded from iCloud backup, especially large app-generated/downloaded data. For this app, official seed content can be recreated from bundled or remote packs, while user progress is user data.

Recommendation:

- Keep seed packs/assets in the app bundle or a non-backed-up cache location when possible.
- Store the writable SQLite database in the app support location.
- If the database contains large recreatable seed content, consider excluding it from iCloud backup and providing separate user-progress export/sync later.
- If excluding the whole DB would lose user progress, either keep the DB small enough for backup or split user progress into a separate lightweight export/sync path.

### Practical MVP decision

For MVP, one SQLite database is acceptable. Keep the seed small and monitor database size. Later, if seed content grows, consider:

1. Bundled read-only prebuilt seed DB + writable user DB.
2. Main writable DB but backup excludes large seed assets.
3. Backend account sync of user progress/favorites only.

---

## Statistics cache

A statistics cache is optional. Most stats can be computed from `exam_session`, `exam_answer`, and `study_progress` at runtime.

Add `stats_cache` only if dashboard queries become slow or complex. Cache values should be invalidated after:

- completing an exam session.
- answering practice questions.
- resolving wrong answers.
- importing a new pack.
- changing selected jurisdiction/class.

Example cache keys:

```text
stats:home:AR-B:B
stats:category-progress:AR-B:B
stats:wrong-count:AR-B:B
```

---

## Test plan for database

### Unit tests

Use a JVM or in-memory SQLite driver for common tests where possible.

Test groups:

1. **Schema creation**
   - Fresh database creates all tables and indexes.
   - Foreign keys are enabled and constraints work.

2. **Seed import**
   - First import inserts sources, license classes, categories, questions, options.
   - Re-import same pack is idempotent.
   - New pack version updates changed content.
   - Missing questions are deprecated, not hard-deleted.
   - Failed import rolls back all content changes.

3. **Question selection**
   - Random practice returns only active questions.
   - Class/category filters work.
   - Deprecated questions are excluded.
   - Exam selection respects total question count and category quotas.

4. **Exam lifecycle**
   - Start session inserts session and snapshot rows.
   - Submit answer records answer exactly once per question.
   - Updated answer increments `changed_count`.
   - Complete session computes score and pass/fail.
   - Historical session review works after question deprecation.

5. **Study progress**
   - Correct answer increments `correct_count` and streak.
   - Wrong answer increments `incorrect_count`, resets/decreases mastery, and creates wrong log.
   - Later correct answer can mark unresolved wrong answers resolved.
   - Due-review query returns expected questions.

6. **Favorites/settings**
   - Favorite upsert is idempotent.
   - Removing favorite deletes only bookmark, not question.
   - Settings upsert preserves latest values.

7. **Migrations**
   - Migration from every released schema snapshot succeeds.
   - Migrated DB equals latest schema.
   - User data survives migration.

8. **FTS, if enabled**
   - Search returns expected rows.
   - Re-import updates FTS rows.
   - Deleted/deprecated question is removed from active search results.

### Instrumentation tests

Run on Android and iOS simulators/devices:

- Open database with platform driver.
- Run first-launch import from bundled asset/resource.
- Run large-ish synthetic import.
- Start and complete an exam.
- Kill and reopen app; verify state persists.
- Upgrade app build with migration; verify data survives.

### Performance tests

Measure:

- DB open time.
- First import time for 500, 2,000, and 10,000 questions.
- Random exam selection time.
- Search time with LIKE and FTS.
- Dashboard stats query time.
- Database file size after repeated sessions.

---

## Performance risks and mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| Import runs outside transaction | Very slow inserts, partial state on crash | Single transaction for each pack. |
| Over-indexing seed tables | Slower import | Add only indexes used by real queries. |
| `ORDER BY RANDOM()` on large table | Slow exam generation | Query IDs by filter, shuffle in Kotlin, or use random buckets. |
| Storing images in SQLite | Large DB, backup bloat | Store asset paths/files, not BLOBs. |
| Recomputing stats on every screen render | UI jank | Use cached flows or `stats_cache`. |
| FTS unsupported/misconfigured | Runtime failure | Feature-flag FTS and test on Android/iOS devices. |
| Deleting questions referenced by sessions | Broken history | Soft-deprecate and snapshot session content. |
| Backing up huge seed DB | Backup quota / iCloud review risk | Keep seed small, exclude recreatable data, add explicit export later. |
| Multiple DB instances | Locking/weird state | Inject singleton database provider. |

---

## Example SQLDelight queries

### Get active question with options and source

```sql
selectQuestionDetail:
SELECT
  q.question_id,
  q.stem,
  q.explanation,
  q.image_asset_path,
  q.difficulty,
  q.is_eliminatory,
  s.source_id,
  s.title AS source_title,
  s.official_url,
  s.attribution_text
FROM question q
JOIN source_metadata s ON s.source_id = q.source_id
WHERE q.question_id = ?
  AND q.is_active = 1;
```

Repository then calls `selectOptionsForQuestion(questionId)` and maps both to a domain `Question`.

### Count progress by category

```sql
selectCategoryProgress:
SELECT
  c.category_id,
  c.display_name,
  COUNT(q.question_id) AS total_questions,
  SUM(CASE WHEN p.mastery_level >= 4 THEN 1 ELSE 0 END) AS mastered_questions,
  SUM(CASE WHEN p.incorrect_count > 0 THEN 1 ELSE 0 END) AS has_mistakes
FROM question_category c
JOIN question q ON q.category_id = c.category_id
LEFT JOIN study_progress p ON p.question_id = q.question_id
JOIN question_class_map m ON m.question_id = q.question_id
WHERE q.is_active = 1
  AND q.jurisdiction_code = ?
  AND m.class_id = ?
GROUP BY c.category_id, c.display_name
ORDER BY c.sort_order;
```

### Select unresolved wrong-answer count

```sql
countUnresolvedWrongAnswers:
SELECT COUNT(DISTINCT w.question_id)
FROM wrong_answer_log w
JOIN question q ON q.question_id = w.question_id
WHERE w.resolved_at_epoch_ms IS NULL
  AND q.is_active = 1
  AND q.jurisdiction_code = ?;
```

### Select favorites with progress

```sql
selectFavoritesWithProgress:
SELECT
  q.question_id,
  q.stem,
  q.category_id,
  f.created_at_epoch_ms,
  p.seen_count,
  p.correct_count,
  p.incorrect_count,
  p.mastery_level
FROM favorite_question f
JOIN question q ON q.question_id = f.question_id
LEFT JOIN study_progress p ON p.question_id = q.question_id
WHERE q.is_active = 1
ORDER BY f.created_at_epoch_ms DESC;
```

---

## Recommended implementation sequence

1. Add SQLDelight Gradle setup and driver factory.
2. Implement schema files without FTS first.
3. Implement import tables and source/license/question tables.
4. Build seed importer and import tests.
5. Add question browsing/practice queries.
6. Add exam session tables and use cases.
7. Add progress, wrong answers, favorites, settings.
8. Add migration verification in CI.
9. Add optional `stats_cache` if dashboard queries need it.
10. Add FTS5 only after LIKE search is insufficient.

---

## Final recommendation

For the first production-ready offline MVP, use **one writable SQLDelight SQLite database** with explicit seed versioning and soft-deprecated content. Keep the schema normalized enough to query efficiently, but avoid premature complexity such as separate read-only seed databases or FTS until the product needs them.

The critical database invariants are:

- Every question has stable ID, source metadata, content hash, jurisdiction, category, class mapping, and active/deprecated status.
- Every answer option belongs to one question and exactly one or more options can be marked correct depending on future exam rules; for current single-choice exams, validate exactly one correct option in the seed pipeline.
- Every exam session snapshots the question and option order used at the time of the exam.
- Seed import never deletes user data.
- Content pack versioning is separate from SQLDelight schema migration.

---

## References

- SQLDelight Overview, official docs: <https://sqldelight.github.io/sqldelight/2.3.2/>
- SQLDelight SQLite Multiplatform Getting Started, official docs: <https://sqldelight.github.io/sqldelight/2.3.2/multiplatform_sqlite/>
- SQLDelight Android SQLite Getting Started, official docs: <https://sqldelight.github.io/sqldelight/2.3.2/android_sqlite/>
- SQLDelight Kotlin/Native SQLite Getting Started, official docs: <https://sqldelight.github.io/sqldelight/2.3.2/native_sqlite/>
- SQLDelight Migrations, official docs: <https://sqldelight.github.io/sqldelight/2.3.2/multiplatform_sqlite/migrations/>
- SQLDelight Transactions, official docs: <https://sqldelight.github.io/sqldelight/2.3.2/multiplatform_sqlite/transactions/>
- SQLDelight Coroutines, official docs: <https://sqldelight.github.io/sqldelight/2.3.2/multiplatform_sqlite/coroutines/>
- SQLDelight FTS5 Virtual Tables, official docs: <https://sqldelight.github.io/sqldelight/2.3.2/android_sqlite/fts5_virtual_tables/>
- SQLite FTS5 Extension, official docs: <https://www.sqlite.org/fts5.html>
- Kotlin Multiplatform tutorial using SQLDelight, JetBrains docs: <https://kotlinlang.org/docs/multiplatform/multiplatform-ktor-sqldelight.html>
- Android Auto Backup, official Android docs: <https://developer.android.com/identity/data/autobackup>
- Apple: Optimizing Your App's Data for iCloud Backup: <https://developer.apple.com/documentation/foundation/optimizing-your-app-s-data-for-icloud-backup>
- Apple: Using the file system effectively: <https://developer.apple.com/documentation/foundation/using-the-file-system-effectively>
