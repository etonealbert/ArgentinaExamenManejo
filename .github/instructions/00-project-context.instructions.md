---
applyTo: "**/*"
---

# Project Context Instructions

## Product

ExamenManejo is a mobile app for Argentina driving-license theory exam preparation.

The app should help users:

- choose a license class;
- study theory questions;
- practice by category;
- run simulated exams;
- review wrong answers;
- track local history and progress.

## MVP scope

MVP must be fully offline.

Included:

- onboarding;
- license class selection;
- Class B vertical slice first;
- local seed question pack;
- study mode;
- exam simulation;
- result screen;
- answer review;
- local history;
- basic statistics;
- settings/about/legal.

Excluded until explicitly requested:

- backend;
- subscriptions;
- login;
- cloud sync;
- remote question updates;
- admin panel;
- analytics;
- push notifications.

## Repository shape

Current root structure from the user:

```text
androidApp/
iosApp/
shared/
docs/
gradle/
```

Assume `shared` is the primary KMP module unless the project files show otherwise.

## Research dependency

Before large implementation changes, check relevant documents in `/docs`.

Important docs:

- product/MVP: `docs/00-product-mvp-research.md`
- platform support: `docs/01-platform-support-research.md.md`
- architecture: `docs/02-architecture-decision-record.md`
- navigation: `docs/03-navigation-coordinator-research.md.md`
- data sourcing: `docs/04-argentina-data-source-research.md`
- taxonomy: `docs/05-content-taxonomy-exam-model.md`
- parsing/import: `docs/06-data-parsing-import-pipeline.md`
- SQLDelight: `docs/07-local-database-sqldelight-research.md`
- training features: `docs/08-exam-training-feature-research.md`
- UI/UX: `docs/09-compose-ui-ux-research.md`

## Product safety rule

Do not present the app as official government software unless there is explicit authorization. Add disclaimers and attribution where required.
