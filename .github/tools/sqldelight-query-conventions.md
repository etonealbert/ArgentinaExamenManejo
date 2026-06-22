# SQLDelight Query Conventions

## Naming

Use verb-first names:

```sql
selectQuestionById:
selectQuestionsByLicenseClass:
insertExamSession:
updateExamSessionFinished:
deleteDeprecatedQuestionPack:
```

## Query grouping

Group queries by table/domain:

1. sources;
2. license classes;
3. questions;
4. answer options;
5. question packs/imports;
6. exam sessions;
7. exam answers;
8. progress;
9. settings.

## Mapping

Never return generated SQLDelight row types to UI or domain.

Map like:

```text
SQLDelight row -> data mapper -> domain model -> UI mapper -> UiModel
```

## Transactions

Use transactions for:

- question pack import;
- finishing exam and inserting answers;
- resetting local data;
- migrations that update related tables.

## Versioning

Track:

- database schema version through SQLDelight migrations;
- content pack version in `question_pack` table;
- source access/version metadata in `source` table.
