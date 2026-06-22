---
applyTo: "**/*.{kt,json,md,sql,sq,csv}"
---

# Data and Content Instructions

Argentina driving-exam content must be treated as regulated, source-sensitive content.

## Data principles

- Use official or high-trust sources first.
- Do not copy content from competitor apps.
- Do not blindly scrape and publish questions.
- Do not assume public PDFs are free for commercial reuse.
- Every content item must be traceable to a source.
- Prefer reviewed/paraphrased training questions unless reuse rights are confirmed.

## Required metadata per question

Each question should include:

- stable ID;
- jurisdiction;
- license class applicability;
- subclass applicability if needed;
- category;
- question text;
- answer options;
- correct option;
- explanation;
- source ID;
- source title;
- source URL or reference;
- source access date;
- content version;
- review status;
- reviewer notes if available;
- deprecation flag.

## Jurisdiction model

Support at least:

- national Argentina;
- CABA;
- Provincia de Buenos Aires;
- other province/municipality later.

Do not mix national and local rules without tagging.

## Content lifecycle

Use this lifecycle:

```text
draft -> parsed -> reviewed -> approved -> deprecated
```

Only approved content should be shipped in production seed packs.

## Import pipeline

Staging source can be JSON, CSV, Airtable, or spreadsheet, but production seed pack should be deterministic JSON.

Runtime storage is SQLDelight.

Import must be:

- idempotent;
- versioned;
- validated before insert;
- traceable;
- safe to run on first launch;
- prepared for future remote packs.

## Legal/product disclaimer

The app must not claim official government affiliation. Add clear wording in settings/about/legal when implementing those screens.
