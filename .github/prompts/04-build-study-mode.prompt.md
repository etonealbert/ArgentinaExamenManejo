# Build Study Mode

Implement Study Mode for ExamenManejo.

Functional requirements:

- accepts `licenseClassId` route argument;
- loads approved questions for that class from repository;
- displays one question at a time;
- allows answer selection;
- reveals correct answer and explanation;
- tracks progress locally;
- supports next/previous where simple;
- handles loading, empty, and error states.

Architecture requirements:

- `StudyUiState`;
- `StudyEvent`;
- `StudyEffect` if needed;
- `StudyViewModel`;
- use cases instead of direct repository calls if business logic exists;
- no SQLDelight access from Composables.

Test requirements:

- answer selection state;
- reveal-answer behavior;
- progress update use case or repository mapping if touched.
