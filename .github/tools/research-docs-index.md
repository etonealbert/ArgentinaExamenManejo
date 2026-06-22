# Research Docs Index

Use this file to quickly decide which `/docs` research file to read before coding.

## Documents

| File | Purpose |
|---|---|
| `docs/00-product-mvp-research.md` | Product strategy, MVP scope, feature boundaries, vertical slice. |
| `docs/01-platform-support-research.md.md` | iOS 26+, Android latest two versions, SDK/API targets, device matrix. |
| `docs/02-architecture-decision-record.md` | Clean Architecture-lite, MVVM, UDF, Repository, Coordinator decisions. |
| `docs/03-navigation-coordinator-research.md.md` | Typed routes, RootCoordinator, feature coordinators, global screens. |
| `docs/04-argentina-data-source-research.md` | Official Argentina data sources, legal reuse risks, attribution. |
| `docs/05-content-taxonomy-exam-model.md` | License classes, jurisdictions, categories, question/source metadata. |
| `docs/06-data-parsing-import-pipeline.md` | Source extraction, cleaning, validation, import, question-pack workflow. |
| `docs/07-local-database-sqldelight-research.md` | SQLDelight schema, queries, migrations, local persistence. |
| `docs/08-exam-training-feature-research.md` | Study mode, simulator, scoring, history, review, statistics. |
| `docs/09-compose-ui-ux-research.md` | Compose UI, accessibility, screen behavior, visual design system. |

## Recommended task-to-doc mapping

- New feature: read product/MVP + matching feature doc.
- Architecture refactor: read architecture + affected feature docs.
- Navigation work: read navigation + architecture.
- Database work: read SQLDelight + taxonomy + import pipeline.
- Question content work: read data source + taxonomy + parsing pipeline.
- Exam/study/statistics work: read exam training + database.
- UI work: read UI/UX + product/MVP.
- Platform build work: read platform support + architecture.

## Rule

Do not treat `/docs` as optional background. For non-trivial changes, it is required project context.
