---
applyTo: "**/*Test.kt"
---

# Testing and QA Instructions

## Testing priorities

Write tests for logic that can break user trust:

- exam scoring;
- pass/fail threshold;
- random question selection;
- duplicate avoidance;
- seed import validation;
- answer correctness mapping;
- history persistence;
- stats calculation;
- ViewModel state transitions.

## Test structure

Prefer common tests for pure logic:

```text
shared/src/commonTest/kotlin/...
```

Use fake repositories for ViewModel/use-case tests.

Do not require real network calls in MVP tests.

## Content QA

Before adding a question to approved seed data, verify:

- source is documented;
- jurisdiction is correct;
- license class applicability is correct;
- answer is correct;
- explanation is clear;
- text is not copied unlawfully;
- duplicate check was performed;
- legal/date/version metadata is present.

## Definition of done for feature PRs

A feature is not done until:

- state model is explicit;
- loading/empty/error states are considered;
- domain logic has tests;
- persistence logic has tests if touched;
- Android build impact is considered;
- iOS build impact is considered;
- no MVP boundary violations are introduced.
