---
applyTo: "**/*.{sq,kt,kts}"
---

# SQLDelight Database Instructions

Use SQLDelight as the single source of persisted app truth for MVP.

## Tables to plan

Minimum MVP tables:

- `source`
- `license_class`
- `question`
- `question_license_class`
- `answer_option`
- `question_pack`
- `exam_session`
- `exam_answer`
- `study_progress`
- `user_settings`

Good V1 additions:

- `favorite_question`
- `category_progress`
- `wrong_answer_queue`
- `content_review_status` or status fields inside `question`

## Schema rules

- Use stable IDs for content records.
- Use separate source metadata records.
- Do not delete old content directly; prefer soft deprecation.
- Store exam answer snapshots enough to preserve review history after content updates.
- Keep SQL query names explicit.
- Use transactions for seed import and exam completion.
- Map SQLDelight generated rows into domain models before returning to feature/domain layers.

## Query naming examples

```text
selectQuestionById
selectQuestionsByLicenseClass
selectAnswerOptionsByQuestionId
insertExamSession
insertExamAnswer
selectExamHistory
selectExamAnswersBySessionId
upsertStudyProgress
selectSeedPackVersion
```

## Import behavior

First launch:

1. Open database.
2. Check installed question pack version.
3. If empty, validate bundled seed JSON.
4. Insert source records.
5. Insert license classes.
6. Insert questions and answer options.
7. Mark pack as installed.
8. Never insert duplicate content for the same pack.

## Random question selection

For small MVP packs, simple random selection is acceptable.

For larger packs, avoid expensive `ORDER BY RANDOM()` on large datasets. Prefer selecting candidate IDs, shuffling in Kotlin, and loading selected records.
