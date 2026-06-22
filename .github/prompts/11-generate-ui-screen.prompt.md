# Generate Compose Screen

Create a Compose Multiplatform screen for ExamenManejo.

Inputs to clarify or infer from context:

- screen name;
- route args;
- ViewModel state;
- events;
- effects;
- loading state;
- empty state;
- error state.

Rules:

- stateless Composable where practical;
- preview/mock state if supported;
- no repository/database calls in Composable;
- no exam scoring in Composable;
- accessible labels and large tap targets;
- long Spanish text should wrap/scroll;
- use project design tokens/components if present.

Deliver:

- `Screen.kt`;
- `UiState.kt`;
- `Event.kt` if separate;
- `ViewModel.kt` if needed;
- navigation effect handling if needed;
- basic tests for state if logic exists.
