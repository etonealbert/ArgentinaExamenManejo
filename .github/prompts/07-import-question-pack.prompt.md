# Build Local Question Pack Import

Implement local seed question pack import.

Requirements:

- bundled deterministic JSON pack;
- validated before insert;
- idempotent import;
- version tracking;
- transaction-based insert;
- source metadata inserted;
- license class relationships inserted;
- answer options inserted;
- correct answer integrity validated;
- duplicate IDs rejected;
- only approved content imported for production packs.

Suggested files:

```text
data/local/SeedDataImporter.kt
data/local/QuestionPackValidator.kt
data/local/QuestionPackDto.kt
data/mapper/QuestionPackMapper.kt
```

Validation checks:

- pack ID present;
- version present;
- jurisdiction present;
- every question has at least 2 options;
- exactly one correct option unless model explicitly supports multi-correct;
- every question has source metadata;
- every question has at least one license class;
- no duplicate IDs;
- no empty question/answer text.

Tests:

- valid pack imports;
- duplicate import is no-op;
- invalid pack fails before partial insert;
- missing correct answer fails validation.
