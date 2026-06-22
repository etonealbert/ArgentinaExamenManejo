# Refactor to Target Architecture

Refactor an existing implementation to match the ExamenManejo architecture.

Target architecture:

- Clean Architecture-lite;
- MVVM;
- UDF;
- Repository pattern;
- Coordinator pattern;
- SQLDelight behind data sources/repositories;
- Koin DI.

Refactor rules:

- keep behavior unchanged unless a bug is identified;
- move business logic out of Composables;
- move persistence out of ViewModels;
- introduce use cases only where they clarify business logic;
- do not create unnecessary Gradle modules;
- do not start backend/subscription work;
- add tests around extracted logic.

Output a concise refactor plan before editing:

1. Current issue.
2. Target structure.
3. Files to modify.
4. Risk.
5. Test plan.
