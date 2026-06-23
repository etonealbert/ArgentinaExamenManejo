# MVP Vertical Slice

## Exact Flow

```text
App start -> seed import check -> startup error on import failure, otherwise onboarding status check -> onboarding or home -> Class B -> start exam -> answer -> finish -> result -> review -> history
```

## Flow Details

1. App start: `App()` injects the shared navigator and `RootCoordinator`.
2. Seed import check: `RootCoordinator.start()` calls `CheckFirstLaunchSeedImportUseCase`, which imports the bundled Class B seed pack into SQLDelight only when needed. The bundled `.sha256` checksum must match the actual SHA-256 of the JSON bytes before import, and that checksum is stored for idempotency.
3. Startup error: if seed import fails, the coordinator replaces the current route with no-argument `AppRoute.StartupError` and stops normal startup routing.
4. Onboarding status check: after a successful or already-imported seed check, `RootCoordinator.start()` asks `SettingsRepository.hasCompletedOnboarding()`.
5. Onboarding or home: the coordinator replaces the current route with `AppRoute.Onboarding` or `AppRoute.Home`.
6. Class B: home/study navigation uses `licenseClassId = "B"` and loads Class B content from SQLDelight.
7. Start exam: `StartExamUseCase` reads active Class B questions and creates an exam session with question snapshots. The MVP demo exam is configured for the three bundled demo questions.
8. Answer: `SubmitExamAnswerUseCase` records selected option IDs as answer snapshots for the session.
9. Finish: `FinishExamUseCase` calculates the result from exam question snapshots and answer snapshots, then completes the session.
10. Result: `GetExamResultUseCase` reads the completed session result by `examSessionId`.
11. Review: `GetReviewAnswersUseCase` reads question snapshots and answer snapshots by `examSessionId`.
12. History: `GetExamHistoryUseCase` reads completed exam sessions from SQLDelight; each route entry uses a route-scoped ViewModelStore so History reloads from the use case and old route view models are cleared.

## Snapshot Rule

Result, review, and history use exam session snapshots and answer snapshots. They do not depend on mutable question content after the exam starts. This keeps a completed exam stable even if seeded or future content changes later.

## MVP Scope

The vertical slice is fully offline. There is no backend, login, real subscription, cloud sync, analytics, push notifications, admin panel, or remote question update flow. The bundled Class B questions are `DEMO_UNVERIFIED` non-official demo content.
