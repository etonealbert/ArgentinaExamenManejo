---
applyTo: "**/*.kt,**/*.kts"
---

# Architecture Instructions

Use **Clean Architecture-lite + MVVM + Unidirectional Data Flow + Repository pattern**.

## Layer responsibilities

### Presentation / feature layer

Contains:

- Compose screens;
- ViewModels;
- UI state models;
- UI event models;
- one-time effects;
- UI mappers;
- screen-specific components.

Presentation may depend on domain.

Presentation must not depend directly on SQLDelight row types, Ktor DTOs, or platform APIs.

### Domain layer

Contains:

- pure Kotlin models;
- repository interfaces;
- use cases;
- exam scoring rules;
- question selection rules;
- stats calculation logic.

Domain must be platform-free and framework-light.

Domain must not import:

- Compose;
- SQLDelight;
- Ktor;
- Koin;
- Android APIs;
- iOS APIs.

### Data layer

Contains:

- repository implementations;
- SQLDelight local data sources;
- seed import logic;
- mappers between SQLDelight rows and domain models;
- future Ktor client implementations.

Data implements domain repository interfaces.

## UDF pattern

For screens, prefer:

```kotlin
data class ScreenUiState(...)

sealed interface ScreenEvent {
    data object BackClicked : ScreenEvent
}

sealed interface ScreenEffect {
    data object NavigateBack : ScreenEffect
}
```

ViewModel exposes:

```kotlin
val uiState: StateFlow<ScreenUiState>
val effects: SharedFlow<ScreenEffect>
fun onEvent(event: ScreenEvent)
```

## Use case naming

Use cases should be small and explicit:

- `GetLicenseClassesUseCase`
- `GetQuestionsByClassUseCase`
- `StartExamUseCase`
- `SubmitExamAnswerUseCase`
- `FinishExamUseCase`
- `CalculateExamResultUseCase`
- `GetExamHistoryUseCase`
- `GetWeakCategoriesUseCase`

## Repository naming

Domain interfaces:

- `QuestionRepository`
- `ExamRepository`
- `ProgressRepository`
- `SettingsRepository`
- `SubscriptionRepository` future/fake only in MVP.

Data implementations:

- `QuestionRepositoryImpl`
- `ExamRepositoryImpl`
- `ProgressRepositoryImpl`
- `SettingsRepositoryImpl`
- `FakeSubscriptionRepository`

## MVP anti-overengineering rule

Do not split into many Gradle modules yet unless the user explicitly asks. Use package boundaries first. Add modules only after the vertical slice is stable.
