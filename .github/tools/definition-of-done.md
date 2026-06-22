# Definition of Done

A task is done only when the relevant checks below are satisfied.

## Architecture

- [ ] Code follows Clean Architecture-lite.
- [ ] Domain layer is framework-free.
- [ ] UI does not call SQLDelight/Ktor directly.
- [ ] Business logic is outside Composables.
- [ ] Route arguments are IDs/primitives only.
- [ ] Coordinator pattern is respected for navigation changes.

## MVP boundary

- [ ] No backend added unless explicitly requested.
- [ ] No login added unless explicitly requested.
- [ ] No real subscription/billing added unless explicitly requested.
- [ ] App remains capable of offline operation.

## Data/content

- [ ] Content has source metadata.
- [ ] Jurisdiction is tagged.
- [ ] License class applicability is tagged.
- [ ] Correct answer is validated.
- [ ] No competitor content/assets copied.

## UI

- [ ] Loading state handled.
- [ ] Empty state handled.
- [ ] Error state handled.
- [ ] Long text handled.
- [ ] Accessibility considered.
- [ ] iOS safe area / Android insets considered.

## Testing

- [ ] Critical logic has tests.
- [ ] Scoring/selection tested if touched.
- [ ] Import/database tested if touched.
- [ ] ViewModel state tested if meaningful.

## Build awareness

- [ ] Android impact considered.
- [ ] iOS impact considered.
- [ ] Gradle/source-set changes explained.
