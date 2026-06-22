---
applyTo: "**/*.kt"
---

# Compose Multiplatform UI Instructions

## UI direction

The UI should be clean, friendly, and educational.

Use:

- card-based license selection;
- large readable question text;
- clear answer options;
- visible progress indicators;
- pass/fail result states;
- accessible review states;
- simple settings/about/legal screens.

## Do not copy competitor assets

Do not copy:

- app icons;
- screenshots;
- car illustrations;
- exact layouts;
- exact wording;
- branding;
- proprietary question content.

Create original UI and assets.

## Compose rules

- Keep Composables mostly stateless.
- Pass state and event callbacks down.
- Use previews where supported by module setup.
- Extract reusable components into `core/design` or local feature components.
- Do not perform database or repository calls inside Composables.
- Do not run scoring/business logic inside Composables.

## Accessibility rules

- Use readable type sizes.
- Support font scaling.
- Use large tap targets.
- Add semantic labels where needed.
- Do not rely only on color for correct/incorrect answer states.
- Use icons/text labels for result meaning.
- Ensure contrast is acceptable.

## Cross-platform rules

- Respect iOS safe areas.
- Respect Android edge-to-edge/insets.
- Keep back behavior explicit.
- Test long Spanish text and small screens.
- Keep screen layouts scrollable where content can overflow.
