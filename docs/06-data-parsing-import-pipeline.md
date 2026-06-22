# 06 — Data Parsing and Import Pipeline Research

**Project:** Kotlin Multiplatform driving-exam app for Argentina  
**Target runtime:** Android + iOS, local-first, offline MVP  
**Primary runtime database:** SQLDelight over SQLite  
**Production content format:** versioned JSON seed packages  
**Prepared:** 2026-06-22

---

## Executive summary

Build the content pipeline as a **source-controlled, review-gated data build system**, not as an ad-hoc scraper. The app should never treat extracted text from PDFs or official webpages as production-ready. Every question must pass through a staged lifecycle: `discovered -> extracted -> normalized -> reviewed -> approved -> packaged -> imported`.

The recommended approach is:

1. Maintain a **source registry** of official or explicitly approved sources.
2. Download and snapshot every source file with hash, URL, access date, and license/terms evidence.
3. Extract text and images with deterministic scripts.
4. Convert extracted material into a staging format for human review.
5. Normalize, deduplicate, and validate records with scripts.
6. Store approved content as a **versioned JSON question pack**.
7. Import the JSON pack into SQLDelight on first launch using idempotent upserts inside a transaction.
8. Keep source metadata on every question so the app can show attribution, source labels, and revision provenance.
9. Use a future backend only for remote pack distribution, entitlement checks, and admin upload/review; do not make the MVP depend on a backend.

The attached Argentina source-research file should be treated as the baseline for official source inventory, jurisdiction differences, and legal/commercial reuse risk. It identifies official national, provincial, and municipal source types, highlights that exam formats vary by jurisdiction, and warns that public PDFs/question banks should not automatically be treated as freely reusable app content.

---

## Recommended decision

Use a **hybrid manual + automated pipeline**:

- **Automated where deterministic:** source download, checksum, PDF text extraction, image extraction, JSON validation, duplicate clustering, pack build, SQLDelight import testing.
- **Manual where correctness/legal risk matters:** source approval, legal reuse decision, correct-answer verification, explanation quality, jurisdiction mapping, eliminatory-question flagging, and final approval.

Do **not** ship directly scraped official questionnaires without a legal review. Where official license terms are explicit and compatible, retain exact text only after approval. Where rights are unclear, use official legal rules and manuals as source references, then create **original/paraphrased questions** with clear attribution to the source rule/manual.

---

## Baseline source and legal assumptions

The existing Argentina source-research document establishes these important constraints:

- Argentina driving-license exam content is jurisdiction-sensitive. National rules exist, but provinces and cities can have different exam formats, local manuals, local procedures, and local question pools.
- Reliable sources include ANSV/national pages, official PDFs, government manuals, provincial questionnaires, official simulators, and traffic-sign resources.
- Theoretical exam themes include road-safety ethics, traffic rules, signage, mechanics/safety systems, and defensive driving.
- Direct reuse of manuals, diagrams, question banks, and compiled databases can carry copyright or database-compilation risk unless the source is clearly licensed or permission is obtained.
- The safer long-term strategy is to extract legal facts/rules, create original questions, and store source metadata for traceability.

This report converts that research into an engineering pipeline for the KMP app.

---

## Primary source-discovery strategy

### Source priority model

| Tier | Source type | Use in pipeline | Approval requirement |
|---|---|---|---|
| Tier 1 | Official national law/regulation pages | Legal facts, source metadata, change monitoring | Legal/source reviewer approval |
| Tier 1 | ANSV / Licencia Nacional de Conducir pages | National rules, classes, course content | Legal/source reviewer approval |
| Tier 1 | Official provincial/municipal pages | Jurisdiction-specific exam rules | Legal/source reviewer approval |
| Tier 2 | Official government PDFs/manuals | Question inspiration, categories, explanations, source references | Legal/source reviewer approval |
| Tier 2 | Official questionnaires | Possible staging input, not automatically shippable | Legal review + content review |
| Tier 2 | Official traffic sign references | Sign taxonomy and asset specifications | Asset/legal review |
| Tier 3 | News, blogs, unofficial simulators, competing apps | Discovery only; never direct content source | Exclude from production content |

### Initial Argentina source registry

The project should start with a `sources.yml` or `sources.json` registry. Each source entry should include:

- `source_id`
- `authority_name`
- `jurisdiction_level`: `national | provincial | municipal`
- `jurisdiction_code`
- `title`
- `source_type`: `law | official_page | pdf_manual | pdf_questionnaire | simulator | sign_resource | terms_page`
- `official_url`
- `download_url`
- `accessed_at`
- `published_or_updated_at`, if visible
- `license_type`
- `license_url`
- `reuse_decision`: `allowed_exact | allowed_with_attribution | derive_only | do_not_use | needs_permission`
- `notes`

Recommended seed registry entries:

| ID | Source | Pipeline use | URL |
|---|---|---|---|
| `SRC-ARG-ANSV-COURSE` | Curso Nacional de Seguridad Vial | National course/source reference | https://www.argentina.gob.ar/servicio/curso-nacional-de-seguridad-vial |
| `SRC-ARG-LICENSE-CLASSES` | Clases y subclases de licencias | License-class taxonomy | https://www.argentina.gob.ar/seguridadvial/licencianacional/clasesysubclases |
| `SRC-ARG-DEC-196-2025` | Decreto Reglamentario 196/2025 | Current national regulatory context | https://www.argentina.gob.ar/normativa/nacional/decreto-196-2025-410682/texto |
| `SRC-ARG-TERMS` | Argentina.gob.ar terms | National portal license evidence | https://www.argentina.gob.ar/terminos-y-condiciones |
| `SRC-CABA-STUDY` | CABA material de estudio | CABA study source | https://buenosaires.gob.ar/gcaba_historico/infraestructura/movilidad/curso-de-educacion-vial-para-otorgamiento-de-licencia/material-de-estudio-para-examen-teorico |
| `SRC-PBA-MANUAL` | PBA Manual del Conductor | PBA manual/source reference | https://www.gba.gob.ar/static/seguridadvial/docs/manual_del_conductor.pdf |
| `SRC-PBA-CUESTIONARIO` | PBA Cuestionario | PBA questionnaire staging input | https://www.gba.gob.ar/static/seguridadvial/docs/cuestionario.pdf |
| `SRC-PBA-NORMATIVA` | PBA normativa vial | PBA official document index | https://www.gba.gob.ar/seguridadvial/normativa_vial_0 |
| `SRC-SF-SIMULATOR` | Santa Fe simulator | Sample questions/exam behavior discovery | https://www.santafe.gob.ar/examenlicencia/examenETLC/listarCuestionarios.php |
| `SRC-CBA-GUIA-2025` | Córdoba Guía de estudio | Córdoba local manual | https://cordoba.gob.ar/wp-content/uploads/2025/11/Guia-de-estudio-Normativa-general.pdf |

### Discovery procedure

1. Search only official domains first: `argentina.gob.ar`, `gba.gob.ar`, `buenosaires.gob.ar`, `santafe.gob.ar`, `cordoba.gob.ar`, official municipal domains, and official boletines/normative portals.
2. Add each candidate to `data/sources/candidates/YYYY-MM-DD-discovery.md`.
3. Classify the source as national, provincial, municipal, or low-trust.
4. Capture license/terms page evidence before using content.
5. Save the source snapshot under `data/raw/<source_id>/`.
6. Generate checksum and metadata with the download script.
7. Promote to `approved_sources.yml` only after source/legal review.

---

## Recommended end-to-end pipeline

```text
Official/approved source
        |
        v
Source registry + raw snapshot + checksum
        |
        v
Text/image extraction sandbox
        |
        v
Parser-specific raw JSON
        |
        v
Normalized staging records
        |
        v
Automated validation + duplicate clustering
        |
        v
Human review in Airtable/Google Sheets/CSV
        |
        v
Approved canonical JSON question pack
        |
        v
Pack validation + import test database
        |
        v
App asset bundle
        |
        v
First-launch SQLDelight import / future remote update
```

### Pipeline stages

| Stage | Output | Owner | Automation level |
|---|---|---|---|
| Source discovery | `approved_sources.yml` | Product/content/legal | Semi-manual |
| Snapshot | raw PDF/HTML/image + metadata | Data engineer | Automated |
| Extraction | `extracted_raw.json` + image files | Data engineer | Automated with manual fallback |
| Parsing | question candidates | Data engineer | Semi-automated |
| Cleaning | normalized staging records | Data engineer | Automated |
| Verification | approved records | Reviewer/legal/content | Manual, assisted |
| Packaging | versioned JSON pack | Data engineer | Automated |
| Import | SQLDelight rows | App runtime | Automated |
| Monitoring | source/version alerts | Backend/data ops later | Automated later |

---

## Extraction strategy

### Extraction decision tree

```text
Is it HTML?
  -> Save HTML snapshot + parse structured text with trafilatura/readability/BeautifulSoup.

Is it a born-digital PDF with selectable text?
  -> Use PyMuPDF for text, links, metadata, images.
  -> Use pdfplumber for table-like or layout-sensitive sections.

Is it a scanned or image-heavy PDF?
  -> Run OCRmyPDF/Tesseract to add a searchable text layer.
  -> Then run PyMuPDF/pdfplumber on the OCR output.
  -> Mark OCR confidence and require manual review.

Does it contain traffic signs or embedded diagrams?
  -> Extract images with PyMuPDF.
  -> Store raw image, normalized asset, perceptual hash, and source metadata.

Does parser confidence drop below threshold?
  -> Send to manual copy/review queue.
```

### Tool recommendations for extraction

| Need | Recommended tool | Reason |
|---|---|---|
| Fast PDF text, images, metadata, links | PyMuPDF | High-performance PDF extraction/manipulation library; good default for born-digital PDFs. |
| Layout-sensitive PDFs/tables | pdfplumber | Exposes characters, rectangles, lines, tables, and visual debugging. |
| Scanned PDFs | OCRmyPDF + Tesseract | Adds searchable OCR text layer to scanned PDFs; Tesseract is the OCR engine. |
| HTML official pages | Python `requests` + BeautifulSoup/trafilatura | Stable snapshots, reproducible extraction. |
| Image normalization | Pillow + OpenCV | Resize, crop, transparency/background cleanup, perceptual hash. |
| JSON schema validation | Python `jsonschema` or `ajv` in CI | Enforce pack structure before shipping. |
| Dataframe cleaning | Python + pandas | Repeatable normalization/dedup scripts. |
| Manual review | Airtable first, Google Sheets second | Airtable is better for review status, attachments, linked sources; Sheets is simpler/cheaper. |

### PDF text extraction

Use **PyMuPDF first** for most official PDFs because it can extract text by page and images/metadata from the same source file. Keep page numbers for every text block.

Recommended raw extraction format:

```json
{
  "source_id": "SRC-PBA-CUESTIONARIO",
  "source_file_sha256": "...",
  "extracted_at": "2026-06-22T00:00:00Z",
  "tool": "pymupdf",
  "pages": [
    {
      "page_number": 1,
      "text": "...",
      "blocks": [
        {
          "block_id": "p001-b001",
          "bbox": [72.0, 124.0, 510.0, 180.0],
          "text": "..."
        }
      ]
    }
  ]
}
```

### OCR fallback rules

Use OCR only when needed because OCR introduces silent correctness risk.

Run OCR if:

- PDF text extraction returns near-empty text.
- Text is garbled due to embedded-font encoding.
- Page is a scanned image.
- Official source is an image-only questionnaire.

OCR output must include:

- `ocr_used: true`
- `ocr_tool`
- `ocr_language`: likely `spa` for Spanish, sometimes `eng+spa`
- `ocr_confidence`, if available
- `manual_review_required: true`

Do not approve OCR-derived records without human review.

### Extracting question/answer blocks

Use parser profiles per source because official PDFs rarely share one universal layout.

Example parser profiles:

```text
parsers/
  pba_cuestionario.py
  cordoba_guia_v3.py
  cordoba_guia_2025.py
  santa_fe_simulator.py
  caba_manual.py
```

Each parser should output `QuestionCandidate` records:

```json
{
  "candidate_id": "SRC-PBA-CUESTIONARIO-p014-q003",
  "source_id": "SRC-PBA-CUESTIONARIO",
  "source_page": 14,
  "raw_stem": "...",
  "raw_options": ["a) ...", "b) ...", "c) ..."],
  "raw_correct_marker": "X a)",
  "raw_explanation": null,
  "parser_confidence": 0.87,
  "parser_notes": ["correct answer detected by leading X marker"]
}
```

### Detecting answer options

Support these common patterns:

- `a)`, `b)`, `c)`
- `A.`, `B.`, `C.`
- leading `X a)` or `X` before correct answer
- bold/colored correct answer in PDFs, if layout metadata detects it
- separate answer key tables
- HTML radio inputs or simulator forms

Never trust the parser alone for correct answers. Parser-detected answers should be tagged as `detected_correct_option_id` and reviewed before becoming `correct_option_id`.

### Detecting explanations

Explanation extraction is lower confidence than option extraction. Treat explanations as a separate field with its own review status.

If source explanations are absent, reviewers should draft original explanations based on official source rules. Store both:

- `explanation`: user-facing original explanation.
- `explanation_source`: official source reference used to verify the explanation.

### Extracting traffic sign images

Preferred approach:

1. Prefer official vector/specification references where available.
2. If extracting from PDFs, save raw image and page context.
3. Normalize asset naming and dimensions.
4. Store image source and license metadata.
5. Generate your own clean app asset where legal/reuse risk is unclear.

Recommended asset metadata:

```json
{
  "asset_id": "sign_arg_regulatory_stop",
  "type": "traffic_sign",
  "file": "assets/signs/arg/regulatory/stop.svg",
  "source_id": "SRC-ARG-TRAFFIC-SIGNS",
  "source_page": 12,
  "license_type": "derived_from_public_spec",
  "sha256": "...",
  "phash": "...",
  "review_status": "approved"
}
```

---

## Cleaning strategy

### Canonical text normalization

Apply normalization before validation and duplicate detection:

1. Unicode normalization: `NFKC`.
2. Replace smart quotes with plain or normalized typographic style consistently.
3. Normalize Spanish punctuation spacing: `¿`, `?`, `¡`, `!`.
4. Normalize dashes: em/en dashes to `-` where needed.
5. Replace non-breaking spaces and control characters.
6. Collapse repeated whitespace.
7. Strip PDF line-break artifacts.
8. Fix common mojibake/encoding issues.
9. Standardize units: `km/h`, `m`, `kg`, `cm`, `g/l`.
10. Preserve legally meaningful accents.

### License class normalization

Canonical license classes:

```text
A, A1.1, A1.2, A1.3, A1.4, A2.1, A2.2, A3,
B, B1, B2,
C, C1, C2, C3,
D, D1, D2, D3, D4,
E, E1, E2,
F,
G, G1, G2
```

Store both:

- `license_class_codes`: normalized list used by app filtering.
- `source_license_label`: exact source label for provenance.

### Category normalization

Recommended initial taxonomy:

```text
rules_general
priority_right_of_way
traffic_signs
road_markings
speed_limits
parking_stopping
alcohol_drugs
documents_requirements
vehicle_safety
defensive_driving
pedestrians_cyclists
motorcycles
professional_transport
heavy_vehicles
agricultural_machinery
emergency_incidents
jurisdiction_specific
```

Each question can have one primary category and multiple secondary tags.

### Answer option normalization

Rules:

- Strip option labels from answer text in canonical content.
- Store option order explicitly: `position: 0, 1, 2, 3`.
- Store source label if needed: `source_label: "a"`.
- Require exactly one correct option for standard single-choice questions.
- Do not randomize stored order; randomize display order at runtime if the product requires it, and preserve answer identity.

### Duplicate removal

Duplicate handling must not simply delete records. It must cluster and review duplicates because similar questions may differ by jurisdiction, class, or correct answer.

See the duplicate detection section below.

---

## Verification strategy

### Verification gates

A record can enter the production seed package only when all gates pass:

| Gate | Required outcome |
|---|---|
| Source URL check | Official URL reachable or archived snapshot exists |
| Source authority check | Source belongs to approved authority/domain |
| License/legal check | `reuse_decision` is approved |
| Jurisdiction check | Correct national/province/city mapping |
| License class check | Relevant class/subclass mapping is correct |
| Category check | Primary category and tags are correct |
| Correct answer check | Correct option verified by human reviewer |
| Explanation check | Explanation is accurate and not misleading |
| Version/date check | Source date/access date stored |
| Duplicate check | Not duplicate, or intentionally linked as variant |
| Language check | Spanish text readable and normalized |
| App rendering check | Images/assets exist and fit UI |

### Recommended review roles

- **Data engineer:** extraction, parser maintenance, validation scripts, pack build.
- **Content reviewer:** Spanish language quality, question clarity, answer correctness.
- **Legal/source reviewer:** reuse decision, attribution requirements, source eligibility.
- **Product reviewer:** app relevance, category/license mapping, MVP scope.

For MVP, one person may cover multiple roles, but the review columns should still exist.

### Staging review fields

Use Airtable or Google Sheets with these columns:

```text
question_id
candidate_id
source_id
source_url
source_page
jurisdiction_code
license_class_codes
primary_category
stem
option_a
option_b
option_c
option_d
correct_option_key
explanation
asset_ids
parser_confidence
exact_copy_from_source
reuse_decision
attribution_text
content_review_status
legal_review_status
duplicate_status
reviewer_notes
approved_at
approved_by
```

Review status values:

```text
draft
needs_source_review
needs_legal_review
needs_content_review
needs_rewrite
approved
rejected
deprecated
```

---

## Storage strategy

### Staging format

Recommended ranking:

1. **Airtable** — best for linked records, review statuses, attachments, formulas, filtered views, and non-engineer review workflows.
2. **Google Sheets** — acceptable for MVP if budget/tooling is minimal; good for simple review but weaker for linked metadata.
3. **CSV** — good as an interchange/export format; weak as the primary review system.
4. **Raw JSON only** — good for engineers; poor for manual review.

Recommended practical setup:

- Use Airtable for staging/review if available.
- Export approved records to JSON via script.
- Commit production JSON pack to Git.
- Build app asset bundle from committed JSON and images.

### Production format

Production should be a **versioned JSON seed package** stored in the app bundle:

```text
composeApp/src/commonMain/composeResources/files/question_packs/
  argentina-base-0001.json
  argentina-base-0001.sha256
  argentina-base-0001.manifest.json
```

Pack naming:

```text
<country>-<jurisdiction-or-base>-<version>.json
argentina-base-0001.json
argentina-caba-0001.json
argentina-pba-0001.json
argentina-cordoba-0001.json
```

### Runtime format

Runtime data should live in SQLDelight tables. Production question data is imported from JSON into SQLite. User progress remains local and should not be overwritten by content updates.

Separate:

- **Content tables:** question packs, sources, questions, options, categories, classes, assets.
- **User tables:** attempts, favorites, progress, settings.
- **Import tables:** installed packs and import logs.

---

## Recommended folder structure

```text
project-root/
  docs/
    04-argentina-data-source-research.md
    06-data-parsing-import-pipeline.md

  data/
    sources/
      candidates/
      approved_sources.yml
      rejected_sources.yml
    raw/
      SRC-PBA-CUESTIONARIO/
        original.pdf
        metadata.json
        sha256.txt
    extracted/
      SRC-PBA-CUESTIONARIO/
        extracted_raw.json
        images/
    staging/
      question_candidates.jsonl
      normalized_questions.csv
      duplicate_clusters.csv
    approved/
      approved_questions.jsonl
      approved_assets.jsonl
    packs/
      argentina-base-0001.json
      argentina-base-0001.manifest.json
      argentina-base-0001.sha256
    reports/
      validation-report-0001.md
      import-test-report-0001.md

  tools/
    content_pipeline/
      pyproject.toml
      README.md
      download_sources.py
      extract_pdf.py
      extract_html.py
      parse_questions.py
      normalize_records.py
      detect_duplicates.py
      validate_pack.py
      build_pack.py
      test_import_sqlite.py
      exporters/
        airtable_export.py
        google_sheets_export.py
      parsers/
        pba_cuestionario.py
        cordoba_guia.py
        santa_fe_simulator.py
      schemas/
        question_pack.schema.json
        staging_question.schema.json
      tests/
        fixtures/
        test_normalization.py
        test_duplicate_detection.py
        test_pack_validation.py

  composeApp/
    src/commonMain/
      sqldelight/com/example/drivingexam/db/
        DrivingExamDatabase.sq
        1.sqm
        2.sqm
      composeResources/files/question_packs/
        argentina-base-0001.json
        argentina-base-0001.sha256
      kotlin/com/example/drivingexam/data/importer/
        QuestionPackImporter.kt
        QuestionPackDto.kt
        ImportResult.kt
```

---

## JSON production schema

Use one pack-level JSON file containing sources, taxonomy references, questions, options, assets, and pack metadata. This keeps imports deterministic and self-contained.

### Example question pack

```json
{
  "schema_version": 1,
  "pack_id": "argentina-base",
  "pack_version": 1,
  "country_code": "AR",
  "locale": "es-AR",
  "title": "Argentina Base Driving Exam Questions",
  "created_at": "2026-06-22T00:00:00Z",
  "content_hash": "sha256:...",
  "sources": [
    {
      "source_id": "SRC-ARG-LICENSE-CLASSES",
      "authority_name": "Agencia Nacional de Seguridad Vial",
      "jurisdiction_level": "national",
      "jurisdiction_code": "AR",
      "title": "Clases y subclases de licencias",
      "source_type": "official_page",
      "official_url": "https://www.argentina.gob.ar/seguridadvial/licencianacional/clasesysubclases",
      "accessed_at": "2026-06-22",
      "license_type": "CC-BY-4.0",
      "license_url": "https://www.argentina.gob.ar/terminos-y-condiciones",
      "reuse_decision": "derive_only",
      "attribution_text": "Fuente: Agencia Nacional de Seguridad Vial, Argentina.gob.ar"
    }
  ],
  "questions": [
    {
      "question_id": "ARG-AR-B-000001",
      "stable_hash": "sha256:...",
      "status": "approved",
      "jurisdiction_code": "AR",
      "jurisdiction_level": "national",
      "license_class_codes": ["B", "B1"],
      "primary_category": "priority_right_of_way",
      "secondary_tags": ["intersection", "right_priority"],
      "difficulty": "easy",
      "is_eliminatory": false,
      "stem": "Al llegar a una intersección sin señalización, ¿qué vehículo tiene prioridad de paso como regla general?",
      "options": [
        {
          "option_id": "ARG-AR-B-000001-A",
          "position": 0,
          "text": "El vehículo que circula por la derecha.",
          "is_correct": true
        },
        {
          "option_id": "ARG-AR-B-000001-B",
          "position": 1,
          "text": "El vehículo de mayor tamaño.",
          "is_correct": false
        },
        {
          "option_id": "ARG-AR-B-000001-C",
          "position": 2,
          "text": "El vehículo que llega a mayor velocidad.",
          "is_correct": false
        }
      ],
      "explanation": "Como regla general, en una intersección sin señalización se debe ceder el paso al vehículo que se aproxima por la derecha, salvo excepciones previstas por la normativa.",
      "assets": [],
      "sources": [
        {
          "source_id": "SRC-ARG-LAW-24449",
          "source_page": null,
          "source_section": "Ley Nacional de Tránsito, prioridad de paso",
          "verification_status": "verified",
          "verified_at": "2026-06-22",
          "verified_by": "content-reviewer"
        }
      ],
      "legal": {
        "content_origin": "derived",
        "exact_copy_from_source": false,
        "reuse_decision": "derived_from_rule",
        "attribution_required": true
      },
      "review": {
        "content_review_status": "approved",
        "legal_review_status": "approved",
        "approved_at": "2026-06-22T00:00:00Z",
        "approved_by": "reviewer"
      }
    }
  ]
}
```

### JSON Schema requirements

The formal schema should enforce:

- Required pack identifiers: `schema_version`, `pack_id`, `pack_version`, `country_code`, `locale`.
- Unique `source_id` values.
- Unique `question_id` values.
- `question_id` pattern: `^ARG-[A-Z0-9-]+-[0-9]{6}$` or similar.
- At least two options per question.
- Exactly one `is_correct: true` for `single_choice` questions.
- Non-empty `stem`, `option.text`, and `explanation` for approved questions.
- Every question references at least one approved source.
- Every referenced source exists in `sources`.
- Every asset reference exists in the pack asset list or app asset folder.
- `license_class_codes` must be known canonical codes.
- `jurisdiction_code` must be known canonical code.
- `status` must be one of `draft`, `approved`, `deprecated` in production packs; production should usually include only `approved` and intentionally retained `deprecated` records.

---

## SQLDelight schema

The schema below focuses on content import. User progress tables can be added in a separate local-database research document, but some minimal fields are included for integrity.

```sql
-- DrivingExamDatabase.sq

CREATE TABLE installed_question_pack (
  pack_id TEXT NOT NULL,
  pack_version INTEGER NOT NULL,
  schema_version INTEGER NOT NULL,
  country_code TEXT NOT NULL,
  locale TEXT NOT NULL,
  title TEXT NOT NULL,
  content_hash TEXT NOT NULL,
  installed_at_epoch_ms INTEGER NOT NULL,
  PRIMARY KEY (pack_id, pack_version)
);

CREATE TABLE source_document (
  source_id TEXT NOT NULL PRIMARY KEY,
  authority_name TEXT NOT NULL,
  jurisdiction_level TEXT NOT NULL,
  jurisdiction_code TEXT NOT NULL,
  title TEXT NOT NULL,
  source_type TEXT NOT NULL,
  official_url TEXT NOT NULL,
  accessed_at TEXT NOT NULL,
  published_or_updated_at TEXT,
  license_type TEXT NOT NULL,
  license_url TEXT,
  reuse_decision TEXT NOT NULL,
  attribution_text TEXT,
  source_hash TEXT
);

CREATE TABLE jurisdiction (
  jurisdiction_code TEXT NOT NULL PRIMARY KEY,
  jurisdiction_level TEXT NOT NULL,
  name TEXT NOT NULL,
  parent_code TEXT
);

CREATE TABLE license_class (
  class_code TEXT NOT NULL PRIMARY KEY,
  parent_class_code TEXT,
  display_name TEXT NOT NULL,
  sort_order INTEGER NOT NULL
);

CREATE TABLE category (
  category_code TEXT NOT NULL PRIMARY KEY,
  display_name TEXT NOT NULL,
  sort_order INTEGER NOT NULL
);

CREATE TABLE question (
  question_id TEXT NOT NULL PRIMARY KEY,
  stable_hash TEXT NOT NULL UNIQUE,
  pack_id TEXT NOT NULL,
  pack_version INTEGER NOT NULL,
  status TEXT NOT NULL,
  jurisdiction_code TEXT NOT NULL,
  jurisdiction_level TEXT NOT NULL,
  primary_category_code TEXT NOT NULL,
  difficulty TEXT NOT NULL,
  is_eliminatory INTEGER AS Boolean NOT NULL DEFAULT 0,
  question_type TEXT NOT NULL DEFAULT 'single_choice',
  stem TEXT NOT NULL,
  explanation TEXT NOT NULL,
  content_origin TEXT NOT NULL,
  exact_copy_from_source INTEGER AS Boolean NOT NULL DEFAULT 0,
  attribution_required INTEGER AS Boolean NOT NULL DEFAULT 1,
  approved_at TEXT,
  deprecated_at TEXT,
  replacement_question_id TEXT,
  FOREIGN KEY (jurisdiction_code) REFERENCES jurisdiction(jurisdiction_code),
  FOREIGN KEY (primary_category_code) REFERENCES category(category_code)
);

CREATE INDEX question_by_jurisdiction ON question(jurisdiction_code);
CREATE INDEX question_by_category ON question(primary_category_code);
CREATE INDEX question_by_status ON question(status);

CREATE TABLE answer_option (
  option_id TEXT NOT NULL PRIMARY KEY,
  question_id TEXT NOT NULL,
  position INTEGER NOT NULL,
  text TEXT NOT NULL,
  is_correct INTEGER AS Boolean NOT NULL DEFAULT 0,
  source_label TEXT,
  FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX answer_option_question_position
ON answer_option(question_id, position);

CREATE TABLE question_license_class (
  question_id TEXT NOT NULL,
  class_code TEXT NOT NULL,
  PRIMARY KEY (question_id, class_code),
  FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE,
  FOREIGN KEY (class_code) REFERENCES license_class(class_code)
);

CREATE INDEX question_license_class_by_class
ON question_license_class(class_code);

CREATE TABLE question_category_tag (
  question_id TEXT NOT NULL,
  category_code TEXT NOT NULL,
  PRIMARY KEY (question_id, category_code),
  FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE,
  FOREIGN KEY (category_code) REFERENCES category(category_code)
);

CREATE TABLE question_source_reference (
  question_id TEXT NOT NULL,
  source_id TEXT NOT NULL,
  source_page INTEGER,
  source_section TEXT,
  verification_status TEXT NOT NULL,
  verified_at TEXT,
  verified_by TEXT,
  PRIMARY KEY (question_id, source_id, source_page, source_section),
  FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE,
  FOREIGN KEY (source_id) REFERENCES source_document(source_id)
);

CREATE TABLE content_asset (
  asset_id TEXT NOT NULL PRIMARY KEY,
  asset_type TEXT NOT NULL,
  file_path TEXT NOT NULL,
  sha256 TEXT NOT NULL,
  phash TEXT,
  source_id TEXT,
  source_page INTEGER,
  license_type TEXT NOT NULL,
  review_status TEXT NOT NULL,
  FOREIGN KEY (source_id) REFERENCES source_document(source_id)
);

CREATE TABLE question_asset (
  question_id TEXT NOT NULL,
  asset_id TEXT NOT NULL,
  position INTEGER NOT NULL,
  PRIMARY KEY (question_id, asset_id),
  FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE,
  FOREIGN KEY (asset_id) REFERENCES content_asset(asset_id)
);

CREATE TABLE import_log (
  import_id TEXT NOT NULL PRIMARY KEY,
  pack_id TEXT NOT NULL,
  pack_version INTEGER NOT NULL,
  started_at_epoch_ms INTEGER NOT NULL,
  finished_at_epoch_ms INTEGER,
  status TEXT NOT NULL,
  message TEXT
);

-- Example queries

selectInstalledPack:
SELECT * FROM installed_question_pack
WHERE pack_id = ?;

selectQuestionsForPractice:
SELECT DISTINCT q.*
FROM question q
JOIN question_license_class qlc ON q.question_id = qlc.question_id
WHERE q.status = 'approved'
  AND q.jurisdiction_code = ?
  AND qlc.class_code = ?
  AND q.primary_category_code = ?
ORDER BY RANDOM()
LIMIT ?;

selectQuestionWithOptions:
SELECT q.*, ao.option_id, ao.position, ao.text, ao.is_correct
FROM question q
JOIN answer_option ao ON q.question_id = ao.question_id
WHERE q.question_id = ?
ORDER BY ao.position ASC;

upsertSource:
INSERT OR REPLACE INTO source_document (
  source_id, authority_name, jurisdiction_level, jurisdiction_code, title,
  source_type, official_url, accessed_at, published_or_updated_at,
  license_type, license_url, reuse_decision, attribution_text, source_hash
) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);

upsertQuestion:
INSERT OR REPLACE INTO question (
  question_id, stable_hash, pack_id, pack_version, status,
  jurisdiction_code, jurisdiction_level, primary_category_code, difficulty,
  is_eliminatory, question_type, stem, explanation, content_origin,
  exact_copy_from_source, attribution_required, approved_at, deprecated_at,
  replacement_question_id
) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
```

### SQLDelight implementation notes

- Use SQLDelight because it generates type-safe Kotlin APIs from SQL and verifies schema/statements/migrations at compile time.
- Use migration files (`1.sqm`, `2.sqm`) for database schema changes, not for routine content-pack updates.
- Treat content packs as data, not schema. A new question pack should not require a database migration unless the table shape changes.
- Import inside a single transaction for speed and atomicity.
- Keep `stable_hash` to detect content changes independent of question ID.
- Use soft deprecation instead of deleting questions that users may have history for.

---

## Validation script requirements

Create `tools/content_pipeline/validate_pack.py` that exits non-zero in CI when any required check fails.

### Required validation checks

| Check | Rule |
|---|---|
| JSON schema | Pack conforms to `question_pack.schema.json` |
| Unique IDs | No duplicate source, question, option, or asset IDs |
| Source references | Every question references at least one existing source |
| Approved source only | Production pack uses only approved sources |
| Correct answer | Single-choice questions have exactly one correct option |
| Option count | Minimum two options; recommended three or four |
| Empty text | No empty stem, option, explanation, or title |
| Canonical classes | All license class codes are known |
| Canonical categories | All categories/tags are known |
| Jurisdiction | Jurisdiction code exists and matches source scope |
| Legal decision | Approved questions must have approved legal/reuse decision |
| Attribution | Attribution required where source license requires it |
| Asset existence | Referenced image files exist and match hash |
| Duplicate threshold | No unreviewed high-similarity duplicate clusters |
| Language quality | Basic Spanish punctuation/encoding checks pass |
| Source URL | URL format valid; optional live check in non-offline CI |
| Pack hash | Manifest hash matches file content |
| Import test | Pack imports into a fresh SQLite test DB |

### Validation report

Each validation run should write:

```text
data/reports/validation-report-<pack_id>-v<version>.md
```

Include:

- total questions
- total approved questions
- questions by jurisdiction
- questions by license class
- questions by category
- exact-copy count
- derived-question count
- OCR-derived count
- missing explanation count
- duplicate clusters
- legal issues
- failed checks

---

## Duplicate detection strategy

Use multiple duplicate signals. No single method is enough for exam questions because wording can vary while the underlying rule is the same.

### Level 1: exact canonical duplicate

Normalize the stem and option texts, then compute:

```text
canonical_question_hash = sha256(
  normalize(stem) + "|" + sorted(normalize(options))
)
```

If hash matches, mark as exact duplicate.

### Level 2: near-text duplicate

Compute text similarity between normalized stems:

- token-set Jaccard similarity
- character 3-gram similarity
- Levenshtein ratio

Suggested thresholds:

- `>= 0.95`: probable duplicate
- `0.85 - 0.95`: possible duplicate, manual review
- `< 0.85`: usually not duplicate

### Level 3: semantic duplicate

Optional but useful after MVP:

- Generate embeddings for Spanish text.
- Cluster questions with cosine similarity above threshold.
- Require manual review before merging.

### Level 4: legal/rule duplicate

Even if wording differs, two questions may test the same rule. Store a `rule_key` or `source_section` cluster:

```text
AR-LAW-24449-PRIORITY-RIGHT
AR-PBA-DOCS-REQUIRED
AR-CABA-SCORING-SYSTEM
```

Use this to avoid overloading exams with repeated rule concepts.

### Duplicate review outcomes

| Outcome | Action |
|---|---|
| Exact duplicate, same jurisdiction/class/answer | Keep one, link duplicate candidate to canonical question |
| Same concept, different jurisdiction | Keep both as variants; mark `variant_group_id` |
| Same question, conflicting answers | Block approval until source verification |
| Similar but materially different | Keep both; document reason |
| Unofficial duplicate of official/derived question | Reject unofficial source record |

---

## Review checklist

Use this checklist before a question enters `approved` status.

### Source and legal

- [ ] Source is official or explicitly approved.
- [ ] Source URL is stored.
- [ ] Source access date is stored.
- [ ] Source authority is stored.
- [ ] License/terms evidence is stored.
- [ ] Reuse decision is set.
- [ ] Attribution text is present if required.
- [ ] Exact-copy flag is correct.
- [ ] If rights are unclear, question is original/derived rather than copied.

### Content correctness

- [ ] Stem is clear and grammatically correct Spanish.
- [ ] Question tests one concept.
- [ ] No ambiguous wording.
- [ ] Correct answer is verified against official source.
- [ ] Incorrect options are plausible but not misleading.
- [ ] Explanation is accurate and concise.
- [ ] Jurisdiction is correct.
- [ ] License class/subclass mapping is correct.
- [ ] Category and tags are correct.
- [ ] Eliminatory flag is justified, if used.

### Technical readiness

- [ ] Question ID is stable and unique.
- [ ] Stable hash is generated.
- [ ] Options have stable IDs and positions.
- [ ] Image assets exist and render correctly.
- [ ] No encoding artifacts.
- [ ] No unresolved duplicate cluster.
- [ ] JSON validation passes.
- [ ] Import test passes.

---

## Import algorithm pseudocode

### First-launch import behavior

Rules:

- Import only if the bundled pack is not installed or has a higher version.
- Import inside a transaction.
- Upsert sources, taxonomy, questions, options, mappings, assets.
- Never delete user progress.
- Deprecate old questions via `status = 'deprecated'` instead of deleting when replacing content.
- Record import log.

### Kotlin-style pseudocode

```kotlin
class QuestionPackImporter(
    private val database: DrivingExamDatabase,
    private val resourceReader: ResourceReader,
    private val json: Json,
    private val clock: Clock
) {
    suspend fun importBundledPack(packFileName: String): ImportResult {
        val rawJson = resourceReader.readText("question_packs/$packFileName")
        val expectedHash = resourceReader.readText("question_packs/$packFileName.sha256").trim()
        val actualHash = sha256(rawJson)

        if (actualHash != expectedHash) {
            return ImportResult.Failed("Pack hash mismatch")
        }

        val pack = json.decodeFromString<QuestionPackDto>(rawJson)
        validatePackOrThrow(pack)

        val installed = database.installedQuestionPackQueries
            .selectInstalledPack(pack.packId)
            .executeAsOneOrNull()

        if (installed != null && installed.pack_version >= pack.packVersion) {
            return ImportResult.Skipped("Pack already installed")
        }

        val importId = uuid()
        val startedAt = clock.nowEpochMillis()

        database.transaction {
            database.importLogQueries.insertImportLog(
                import_id = importId,
                pack_id = pack.packId,
                pack_version = pack.packVersion.toLong(),
                started_at_epoch_ms = startedAt,
                finished_at_epoch_ms = null,
                status = "running",
                message = null
            )

            pack.sources.forEach { source ->
                database.sourceDocumentQueries.upsertSource(source.toDbParams())
            }

            pack.jurisdictions.forEach { jurisdiction ->
                database.jurisdictionQueries.upsertJurisdiction(jurisdiction.toDbParams())
            }

            pack.licenseClasses.forEach { licenseClass ->
                database.licenseClassQueries.upsertLicenseClass(licenseClass.toDbParams())
            }

            pack.categories.forEach { category ->
                database.categoryQueries.upsertCategory(category.toDbParams())
            }

            pack.assets.forEach { asset ->
                database.contentAssetQueries.upsertAsset(asset.toDbParams())
            }

            pack.questions.forEach { question ->
                database.questionQueries.upsertQuestion(question.toDbParams(pack))

                database.answerOptionQueries.deleteOptionsForQuestion(question.questionId)
                question.options.forEach { option ->
                    database.answerOptionQueries.insertOption(option.toDbParams(question.questionId))
                }

                database.questionLicenseClassQueries.deleteForQuestion(question.questionId)
                question.licenseClassCodes.forEach { classCode ->
                    database.questionLicenseClassQueries.insert(question.questionId, classCode)
                }

                database.questionCategoryTagQueries.deleteForQuestion(question.questionId)
                question.allCategoryCodes().forEach { categoryCode ->
                    database.questionCategoryTagQueries.insert(question.questionId, categoryCode)
                }

                database.questionSourceReferenceQueries.deleteForQuestion(question.questionId)
                question.sources.forEach { ref ->
                    database.questionSourceReferenceQueries.insert(ref.toDbParams(question.questionId))
                }

                database.questionAssetQueries.deleteForQuestion(question.questionId)
                question.assets.forEachIndexed { index, assetId ->
                    database.questionAssetQueries.insert(question.questionId, assetId, index.toLong())
                }
            }

            database.installedQuestionPackQueries.insertInstalledPack(
                pack_id = pack.packId,
                pack_version = pack.packVersion.toLong(),
                schema_version = pack.schemaVersion.toLong(),
                country_code = pack.countryCode,
                locale = pack.locale,
                title = pack.title,
                content_hash = actualHash,
                installed_at_epoch_ms = clock.nowEpochMillis()
            )

            database.importLogQueries.finishImportLog(
                finished_at_epoch_ms = clock.nowEpochMillis(),
                status = "success",
                message = "Imported ${pack.questions.size} questions",
                import_id = importId
            )
        }

        return ImportResult.Success(pack.packId, pack.packVersion)
    }
}
```

### Idempotence rules

The importer is idempotent if:

- Re-importing the same pack version produces the same database state.
- Question rows are keyed by stable `question_id`.
- Option rows are replaced per question during import.
- Mappings are deleted/reinserted per question to avoid stale links.
- Installed pack table prevents duplicate work.
- User progress references `question_id` and survives content table upserts.

---

## Migration strategy

### Database schema migrations

Use SQLDelight `.sqm` files only when the schema changes:

```text
src/commonMain/sqldelight/.../DrivingExamDatabase.sq
src/commonMain/sqldelight/.../1.sqm
src/commonMain/sqldelight/.../2.sqm
```

Examples requiring schema migration:

- Add `question_type` for multi-select questions.
- Add FTS tables.
- Add `variant_group_id`.
- Add new progress/statistics tables.

Examples not requiring schema migration:

- Add new questions.
- Fix an explanation.
- Deprecate a question.
- Add a new pack version if schema shape is unchanged.

### Content migrations

Use content pack versions:

```text
argentina-base v1 -> v2
argentina-pba v1 -> v2
```

For content updates:

- Upsert changed questions.
- Insert new questions.
- Mark old questions as `deprecated` when they are obsolete.
- Preserve old question IDs if correcting minor text/typos.
- Create new question IDs if the meaning/correct answer changes materially.
- Use `replacement_question_id` when one question replaces another.

---

## Future remote update strategy

MVP should bundle packs locally. Later, a backend can distribute signed pack manifests.

### Remote manifest

```json
{
  "manifest_version": 1,
  "country_code": "AR",
  "available_packs": [
    {
      "pack_id": "argentina-base",
      "latest_version": 2,
      "schema_version": 1,
      "download_url": "https://api.example.com/packs/argentina-base-0002.json",
      "sha256": "...",
      "min_app_version": "1.0.0",
      "signature": "...",
      "released_at": "2026-08-01T00:00:00Z"
    }
  ]
}
```

### Remote update rules

- App downloads manifest only when online.
- App verifies HTTPS, hash, and signature.
- App imports pack in a transaction.
- App keeps the previous pack if import fails.
- App never blocks offline usage because an update check failed.
- App can display “content last updated” from installed pack metadata.

---

## Future backend upload workflow

The future backend should manage pack creation and review, not just raw file upload.

```text
Admin uploads source/PDF/manual
        |
        v
Backend stores raw source + checksum + metadata
        |
        v
Extraction job creates candidates
        |
        v
Reviewer edits/approves candidates in admin UI
        |
        v
Legal/source reviewer approves reuse decision
        |
        v
Backend builds signed JSON pack
        |
        v
CI/import test validates pack
        |
        v
Pack is published to remote manifest
        |
        v
App downloads and imports pack
```

### Backend entities

- `SourceDocument`
- `RawSourceSnapshot`
- `QuestionCandidate`
- `Question`
- `AnswerOption`
- `ContentAsset`
- `ReviewTask`
- `DuplicateCluster`
- `QuestionPack`
- `PackRelease`
- `AuditLog`

### Backend API outline

```text
POST /admin/sources
POST /admin/sources/{id}/extract
GET  /admin/question-candidates?status=needs_review
PATCH /admin/questions/{id}
POST /admin/questions/{id}/approve
POST /admin/packs/build
POST /admin/packs/{id}/release
GET  /public/question-pack-manifest.json
GET  /public/packs/{pack_file}
```

### Release gates

- JSON schema validation passes.
- Duplicate report has no unresolved blocking duplicates.
- Legal review is complete.
- Import test passes on Android/iOS-compatible SQLite.
- Pack hash and signature generated.
- Rollback pack remains available.

---

## Implementation plan

### Phase 1 — MVP content pipeline

1. Create folder structure.
2. Build source registry.
3. Add first approved sources for Argentina national/base and one jurisdiction.
4. Implement PDF/HTML snapshot scripts.
5. Implement manual-first staging CSV/JSONL.
6. Implement normalization and validation scripts.
7. Create 20–50 approved Class B questions.
8. Build `argentina-base-0001.json`.
9. Implement SQLDelight import.
10. Add import test against a fresh SQLite DB.

### Phase 2 — Parser automation

1. Add parser profiles for official questionnaires/manuals.
2. Add duplicate clustering report.
3. Add asset extraction and image validation.
4. Add Airtable/Google Sheets exporter/importer.
5. Add CI validation.

### Phase 3 — Multi-jurisdiction packs

1. Split packs by jurisdiction.
2. Add exam configuration table/JSON for jurisdiction-specific exam parameters.
3. Add deprecation/replacement workflow.
4. Add source monitoring checklist.

### Phase 4 — Backend-ready workflow

1. Add pack manifest schema.
2. Add signature verification design.
3. Add backend admin data model.
4. Add remote pack update flow.

---

## Risks and mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| Copying protected government manuals/question banks without clear rights | Legal/commercial risk | Legal review; derive original questions from legal rules when uncertain |
| Outdated source material | Wrong exam prep | Store access/version dates; monitor official sources; deprecate old questions |
| OCR errors | Incorrect answers/questions | OCR only as fallback; require manual review |
| Parser misdetects correct answer | High product risk | Correct-answer review gate; validation cannot infer correctness alone |
| Duplicate or near-duplicate questions | Poor exam quality | Duplicate clustering + manual review |
| Jurisdiction mismatch | User studies wrong rules | Strict jurisdiction metadata and app filtering |
| SQL schema tied too tightly to content | Hard updates | Version content packs separately from DB migrations |
| Remote update corruption later | Broken app data | Hash/signature verification and transaction rollback |

---

## Open questions

1. Which jurisdiction is MVP after national/base: CABA, PBA, Córdoba, or Santa Fe?
2. Will the app ship exact official questions where allowed, or only derived/original questions?
3. Who performs legal review for questionable PDFs/questionnaires?
4. Should traffic signs be SVG originals created from official specifications, or extracted assets from official PDFs?
5. Should the MVP support only `single_choice`, or reserve schema support for multi-select now?
6. Will source attribution be visible on every question screen or only in a source/details sheet?
7. Should remote packs be free updates, subscription-gated, or jurisdiction-gated later?

---

## Final checklist

- [ ] Source registry exists.
- [ ] Raw source snapshots are saved with hashes.
- [ ] License/reuse decision exists for every source.
- [ ] Extraction scripts produce raw JSON with page references.
- [ ] Normalization script is deterministic.
- [ ] Duplicate detector produces reviewable clusters.
- [ ] Staging review workflow exists.
- [ ] Production JSON schema exists.
- [ ] `argentina-base-0001.json` validates.
- [ ] SQLDelight schema is implemented.
- [ ] Importer is idempotent.
- [ ] Importer runs inside a transaction.
- [ ] Old user progress survives pack updates.
- [ ] Attribution/source metadata is available in-app.
- [ ] Future remote manifest design is documented.

---

## Sources

Access date for web sources: 2026-06-22.

1. Attached project source baseline: `04-argentina-data-source-research.md`.
2. Argentina.gob.ar, “Clases y subclases de licencias”: https://www.argentina.gob.ar/seguridadvial/licencianacional/clasesysubclases
3. Argentina.gob.ar, “Curso Nacional de Seguridad Vial”: https://www.argentina.gob.ar/servicio/curso-nacional-de-seguridad-vial
4. Argentina.gob.ar, “Decreto Reglamentario 196/2025”: https://www.argentina.gob.ar/normativa/nacional/decreto-196-2025-410682/texto
5. Argentina.gob.ar, “Términos y condiciones”: https://www.argentina.gob.ar/terminos-y-condiciones
6. Buenos Aires Ciudad, “Material de estudio para examen teórico”: https://buenosaires.gob.ar/gcaba_historico/infraestructura/movilidad/curso-de-educacion-vial-para-otorgamiento-de-licencia/material-de-estudio-para-examen-teorico
7. Provincia de Buenos Aires, “Manual del Conductor”: https://www.gba.gob.ar/static/seguridadvial/docs/manual_del_conductor.pdf
8. Provincia de Buenos Aires, “Preguntas Examen Teórico Licencia de Conducir”: https://www.gba.gob.ar/static/seguridadvial/docs/cuestionario.pdf
9. Provincia de Buenos Aires, “Normativa vial”: https://www.gba.gob.ar/seguridadvial/normativa_vial_0
10. Gobierno de Santa Fe, “Simulador de Examen Teórico”: https://www.santafe.gob.ar/examenlicencia/examenETLC/listarCuestionarios.php
11. Municipalidad de Córdoba, “Guía de estudio - Normativa general”: https://cordoba.gob.ar/wp-content/uploads/2025/11/Guia-de-estudio-Normativa-general.pdf
12. SQLDelight documentation: https://sqldelight.github.io/sqldelight/2.3.2/
13. SQLDelight migrations documentation: https://sqldelight.github.io/sqldelight/2.0.2/multiplatform_sqlite/migrations/
14. Kotlin serialization documentation: https://kotlinlang.org/docs/serialization.html
15. PyMuPDF documentation: https://pymupdf.readthedocs.io/
16. pdfplumber repository/documentation: https://github.com/jsvine/pdfplumber
17. OCRmyPDF documentation: https://ocrmypdf.readthedocs.io/
18. Tesseract OCR documentation: https://tesseract-ocr.github.io/
19. Airtable support, importing third-party data: https://support.airtable.com/docs/importing-third-party-data-into-airtable
20. Google Sheets API documentation: https://developers.google.com/workspace/sheets/api/guides/concepts
