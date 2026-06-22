# Build Exam Simulator

Implement the exam simulation flow.

Functional requirements:

- accepts `licenseClassId`;
- creates a new exam session;
- selects a deterministic/randomized set of questions according to MVP rules;
- displays progress;
- records selected answers;
- handles unanswered questions according to defined rules;
- finishes session;
- calculates result;
- persists result and answer snapshots;
- navigates to result by `examSessionId`.

Architecture requirements:

- `ExamUiState`;
- `ExamEvent`;
- `ExamEffect`;
- `ExamViewModel`;
- `StartExamUseCase`;
- `SubmitExamAnswerUseCase`;
- `FinishExamUseCase`;
- `CalculateExamResultUseCase`.

Test requirements:

- score calculation;
- pass/fail threshold;
- unanswered answer handling;
- session persistence if data layer touched;
- ViewModel state transitions.
