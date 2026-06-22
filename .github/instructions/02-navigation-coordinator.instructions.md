---
applyTo: "**/*.kt"
---

# Navigation and Coordinator Instructions

Use the Coordinator pattern to centralize navigation decisions.

## Goals

- Avoid `navController.navigate(...)` scattered across screens.
- Keep global flows accessible from different places.
- Keep exam/result/review flow consistent.
- Make future paywall/tutorial/legal screens reusable.

## Core components

Preferred packages:

```text
core/navigation/
  AppRoute.kt
  AppNavigator.kt
  NavigationEffect.kt
  RootCoordinator.kt
  AppModalHost.kt

feature/exam/
  ExamCoordinator.kt
```

## Route rules

Routes pass only stable IDs or primitives.

Allowed:

```kotlin
ExamRoute(licenseClassId = "B")
ResultRoute(examSessionId = 42L)
ReviewRoute(examSessionId = 42L)
TutorialRoute(source = "exam")
PaywallRoute(source = "history")
```

Forbidden:

```kotlin
ResultRoute(examResult = fullResultObject)
StudyRoute(question = fullQuestionObject)
```

Destination screens must load data from repository/use case by ID.

## Coordinator responsibilities

### RootCoordinator

Responsible for:

- app start routing;
- onboarding completion;
- settings;
- tutorial;
- legal screens;
- future paywall entry points;
- global back handling where needed.

### ExamCoordinator

Responsible for:

- start exam;
- finish exam;
- navigate to result;
- navigate to review;
- retry exam;
- close exam flow and return home.

### StudyCoordinator

Responsible for:

- open study mode;
- open category practice;
- return to license class/home.

### SubscriptionCoordinator later

Responsible for:

- paywall;
- restore purchase;
- premium entitlement decision;
- return to original destination after purchase.

Do not implement real subscription logic during MVP.

## ViewModel and navigation effects

Preferred pattern:

```text
Composable -> ViewModel event -> ViewModel emits effect -> screen/coordinator adapter -> Coordinator -> Navigator
```

ViewModel should not directly know about `NavController`.

## Global modals

Use a global modal host for:

- generic error;
- offline warning;
- tutorial overlay if modal;
- paywall sheet later;
- confirmation dialogs.

Use full-screen routes for:

- settings;
- legal pages;
- result/review;
- paywall if product design requires it.
