# Create SQLDelight Schema

Create or update the SQLDelight schema for the ExamenManejo offline MVP.

Required entities:

- source;
- license class;
- question;
- question/license class relation;
- answer option;
- question pack;
- exam session;
- exam answer;
- study progress;
- user settings.

Requirements:

- stable IDs for content;
- source metadata;
- jurisdiction field;
- review status field;
- content versioning;
- soft deprecation;
- exam answer snapshots for review history;
- explicit query names;
- import queries;
- history queries;
- question-by-class queries.

Also create/adjust:

- driver factory abstraction;
- Android actual driver;
- iOS actual driver;
- database provider;
- repository mapping plan;
- common tests if possible.

Do not leak SQLDelight row classes into domain or UI.
