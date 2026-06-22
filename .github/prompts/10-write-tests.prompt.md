# Write Tests

Add tests for the touched ExamenManejo feature.

Prioritize:

- pure domain tests in `commonTest`;
- use-case tests with fake repositories;
- ViewModel state tests;
- repository mapping tests;
- seed import validation tests.

Do not add brittle UI tests before core logic is stable.

Minimum useful tests for exam features:

- all answers correct -> pass;
- enough wrong answers -> fail;
- unanswered handling;
- selected answer stored;
- review data maps correctly;
- repeated import does not duplicate data.

Output:

- test files added;
- logic covered;
- uncovered risks.
