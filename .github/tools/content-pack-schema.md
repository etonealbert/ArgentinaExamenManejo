# Content Pack Schema Guide

Use this as the target shape for local seed question packs.

## Example

```json
{
  "packId": "argentina-class-b-mvp-001",
  "version": "0.1.0",
  "jurisdiction": {
    "country": "AR",
    "region": "national",
    "municipality": null
  },
  "createdAt": "2026-06-22",
  "sources": [
    {
      "id": "ansv-license-classes",
      "title": "Official license class reference",
      "url": "https://example.gov.ar/source",
      "accessedAt": "2026-06-22",
      "usageNotes": "Verify reuse rights before production release."
    }
  ],
  "questions": [
    {
      "id": "AR-B-0001",
      "status": "approved",
      "jurisdiction": "national",
      "licenseClasses": ["B"],
      "categories": ["ROAD_RULES"],
      "difficulty": "basic",
      "sourceIds": ["ansv-license-classes"],
      "text": "Question text here.",
      "imageAsset": null,
      "explanation": "Explanation here.",
      "options": [
        { "id": "A", "text": "Option A", "isCorrect": true },
        { "id": "B", "text": "Option B", "isCorrect": false },
        { "id": "C", "text": "Option C", "isCorrect": false }
      ]
    }
  ]
}
```

## Required validation

- `packId` is non-empty.
- `version` is semantic or otherwise deterministic.
- Every question has a unique ID.
- Every question has at least two options.
- Every question has exactly one correct option unless multi-answer is explicitly added.
- Every question has at least one license class.
- Every question has at least one category.
- Every question references at least one source.
- Every production question has status `approved`.
- No empty question or option text.
