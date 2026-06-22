# Review Code Against Architecture

Review the current changes for ExamenManejo.

Check:

1. Does the change respect Clean Architecture-lite?
2. Did any Composable gain business logic?
3. Did UI import SQLDelight/Ktor/platform APIs directly?
4. Are route arguments only IDs/primitives?
5. Does domain remain framework-free?
6. Are repositories returning domain models, not SQL rows?
7. Is the MVP boundary respected?
8. Are loading/empty/error states handled?
9. Are tests added for critical logic?
10. Does the change affect Android and iOS builds?
11. Are source metadata/content legality rules respected?
12. Is there any copied competitor UI/content/asset risk?

Output:

- pass/fail summary;
- architecture violations;
- bugs or risks;
- missing tests;
- recommended fixes in priority order.
