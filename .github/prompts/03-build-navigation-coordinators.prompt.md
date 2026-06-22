# Build Navigation Coordinators

Implement navigation infrastructure for ExamenManejo.

Preferred components:

- `AppRoute` typed route model;
- `AppNavigator` abstraction;
- `RootCoordinator`;
- `ExamCoordinator`;
- `StudyCoordinator`;
- `GlobalModal` model;
- `AppModalHost` placeholder;
- one-time ViewModel effects pattern.

Rules:

- route args must be IDs/primitives only;
- do not pass domain objects through navigation;
- do not inject raw NavController into every ViewModel;
- keep coordinator logic focused on navigation decisions;
- business logic stays in use cases.

Flows to support:

```text
App start -> onboarding or home
Home -> study(classId)
Home -> exam(classId)
Exam -> result(sessionId)
Result -> review(sessionId)
Result -> retry exam(classId)
Any main screen -> settings
Any main screen -> tutorial(source)
Future premium gate -> paywall(source)
```
