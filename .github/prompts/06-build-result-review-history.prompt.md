# Build Result, Review, and History

Implement the post-exam flow.

Screens:

1. Result screen.
2. Review answers screen.
3. History screen.

Result requirements:

- load by `examSessionId`;
- show pass/fail;
- show correct/wrong/unanswered counts;
- show percentage;
- offer review answers;
- offer retry;
- offer return home.

Review requirements:

- load session answers by `examSessionId`;
- show question text snapshot or current question with safe fallback;
- show selected answer;
- show correct answer;
- show explanation;
- show category/source label if available.

History requirements:

- list completed sessions newest first;
- show license class;
- show date/time;
- show pass/fail and score;
- open result by session ID.

Tests:

- result mapping;
- history ordering;
- review answer mapping;
- route args use IDs only.
