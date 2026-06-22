Based on a comprehensive analysis of the Argentine traffic safety ecosystem, the federal architecture of *Ley Nacional de Tránsito N° 24.449*, the digital updates of *Decreto N° 196/2025*, and the localized constraints of non-adherent jurisdictions, here is the architectural blueprint for modeling Argentina's driver licensing examination data inside your application.

---

## 1. Core Structural Decisions & Domain Research

### 1. Representation of Classes & Subclasses

The platform must implement a hierarchical relationship where **Subclasses** inherit properties from a base **Class**. This architecture allows the exam engine to correctly isolate specific professional requirements—such as requiring a clean *Certificado de Antecedentes Penales* for Class D or mechanical instrumentation modules for Class C—while reusing common private driver modules.

### 2. & 3. Content Localization & Jurisdiction Tagging

Content must be separated into three explicit scopes to align with the real-world fragmentation of the *Sistema Nacional de Licencias de Conducir* (SINALIC):

* **National**: Standardized questions derived directly from the *Agencia Nacional de Seguridad Vial* (ANSV) curriculum.
* **Provincial**: Rules specific to regions operating outside the central remote registry, such as the *Provincia de Buenos Aires* (PBA) 40-question exam model.
* **Municipal**: Local hyper-specific municipal codes, such as the *Ciudad de Córdoba* and its *Ordenanza 9981/98* parameters.

Questions are tagged using a localized geographic composite identifier (`scope_level` + `jurisdiction_code`), enabling the app to load appropriate regional question pools dynamically based on the user's location.

### 4. & 5. Tagging Matrix for Multi-Class Questions

To eliminate database redundancy while supporting complex overlapping requirements, questions must follow a **Many-to-Many mapping strategy** using an intermediary intersection table.

* A question assessing the "Priority Pedestrian" rule or "Horizontal Road Markings" is mapped broadly to a cross-class collection (e.g., `["A1.1", "B1", "C1", "D1"]`).
* A question tracking specialized rules—like air brake pressure gauges—is explicitly restricted to a narrow target sub-array (e.g., `["C1", "C2", "C3"]`).

### 6. Legal Phase Tracking over Time

Given that federal resolutions can modify traffic regulations abruptly via the *Boletín Oficial* (such as variations introduced by *Disposición N° 219/2025*), a **Temporal Versioning Pattern** is required. Each question features an attached `schema_version`, an explicit reference to the underlying statutory article, and a lifecycle state to transition items seamlessly out of production when laws are superseded.

### 7. Intellectual Property & Traffic Sign Asset Strategy

To maintain compliance with Argentine Copyright Law (*Ley N° 11.723*) and shield your platform from database compilation claims, your application asset pipeline must enforce a strict **Derivative Asset Strategy**:

* **Raw Specifications**: Extract standard geometric layouts and technical colors directly from the public domain Annexes of *Ley N° 24.449*.
* **Vector Engine**: Render signs inside the application using custom vector graphics (`.svg` or native UI paths) based on those public specs. Do not scrape pixel-art configurations or raster image files from proprietary municipal websites.

### 8. & 9. Explanations & Source Metadata Verification

To satisfy open-data attribution mandates while retaining proprietary equity, explanations must utilize a **Statutory Citations Framework**:

* Explanations should combine paraphrased educational text (which guarantees clean intellectual property ownership under Article 26 of *Ley N° 11.723*) with explicit, non-copyrightable metadata keys pointing to the exact governing legal article.
* Source nodes map directly back to open government declarations like the National Portal's *Creative Commons Attribution 4.0* (CC BY 4.0) license terms.

---

## 2. Recommended Taxonomy & Models

### License Class Model

* **Particular (Private) Tier**
* `A`: Motorcycles and Mopeds (Subclasses: `A1.1`, `A1.2`, `A1.3`, `A1.4`, `A2.1`, `A2.2`, `A3`)
* `B`: Standard Cars and Light Trailers (Subclasses: `B1`, `B2`)
* `F`: Adapted Vehicles for Drivers with Physical Disabilities
* `G`: Agricultural Machinery and Utility Tractors (Subclasses: `G1`, `G2`)


* **Professional Tier** (Requires Age $\ge 21$, clean Criminal Record, and clinical psychophysical exams)
* `C`: Rigid Commercial Trucks and Large Motorhomes (Subclasses: `C1`, `C2`, `C3`)
* `D`: Professional Passenger Transport (Subclasses: `D1`, `D2`, `D3`, `D4`)
* `E`: Specialized Cargo, Articulated Trucks, and Heavy Machinery (Subclasses: `E1`, `E2`)



### Question Category Model

* `CITIZEN_ETHICS`: Social responsibility, public space use, and pedestrian absolute priority.
* `TRAFFIC_RULES`: Speed limits, passing maneuvers, right-of-way, and roundabout rules.
* `SIGNAGE_SYSTEMS`: Horizontal road markings, vertical regulatory signs, and temporary signals.
* `ACCIDENT_PREVENTION`: Defensive driving, alcohol/drug limits, distraction factors, and post-incident legal duties.
* `VEHICLE_SAFETY`: Active systems (brakes, tires) and passive systems (airbags, seatbelts).
* `BASIC_MECHANICS`: Heavy vehicle air systems, weight distribution restrictions, and professional cargo security controls.

### Jurisdiction Model

```
[JURISDICTION GLOBAL]
   ├── LEVEL: "NATIONAL"  -> ISO-AR: "AR"     (SINALIC Universal Pool)
   ├── LEVEL: "PROVINCIAL"-> ISO-AR: "AR-B"   (Provincia de Buenos Aires Pool)
   └── LEVEL: "MUNICIPAL" -> Code:   "AR-X-CBA"(Municipio de Córdoba Pool)

```

### Source Metadata Model

Maps content to legally defensive authorities:

* `source_id`: Unique token matching the internal matrix (e.g., `S01`, `S03`)
* `legal_authority`: Explicit regulatory group (e.g., "ANSV", "CABA Secretary of Transportation")
* `license_type`: Framework type (`CC-BY-4.0`, `CC-BY-2.5-AR`, `PUBLIC_DOMAIN_STATUTE`)

---

## 3. Question Lifecycle State Machine

```
   [DRAFT] ──(Parsing Hook)──> [PARSED] ──(QA Verification)──> [REVIEWED]
                                                                   │
 [DEPRECATED] <──(Legal Modification)── [APPROVED] <───────────────┘

```

* `DRAFT`: Raw extracted data or paraphrased content undergoing initial text editing.
* `PARSED`: Successfully transformed into the application JSON structural format; syntax and schema-validated.
* `REVIEWED`: Cross-verified by traffic safety specialists against active *Boletín Oficial* entries.
* `APPROVED`: Promoted to production; indexed by the active exam engine for user simulation runs.
* `DEPRECATED`: Permanently retired due to statutory modifications (e.g., changes to legal limits or exam thresholds). Saved for retrospective analytics or performance histories.

---

## 4. Recommended JSON Schema Document

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "title": "ArgentinaDriverExamQuestionPool",
  "type": "object",
  "properties": {
    "question_id": { "type": "string", "pattern": "^ARG-QA-[0-9]{5}$" },
    "lifecycle_status": { "type": "string", "enum": ["DRAFT", "PARSED", "REVIEWED", "APPROVED", "DEPRECATED"] },
    "schema_version": { "type": "string" },
    "jurisdiction": {
      "type": "object",
      "properties": {
        "level": { "type": "string", "enum": ["NATIONAL", "PROVINCIAL", "MUNICIPAL"] },
        "code": { "type": "string" },
        "sinalic_integrated": { "type": "boolean" }
      },
      "required": ["level", "code", "sinalic_integrated"]
    },
    "categories": {
      "type": "array",
      "items": { "type": "string", "enum": ["CITIZEN_ETHICS", "TRAFFIC_RULES", "SIGNAGE_SYSTEMS", "ACCIDENT_PREVENTION", "VEHICLE_SAFETY", "BASIC_MECHANICS"] }
    },
    "target_subclasses": {
      "type": "array",
      "items": { "type": "string", "pattern": "^[A-G](1|2|3|4)?(\\.\\d)?$" }
    },
    "exam_parameters": {
      "type": "object",
      "properties": {
        "is_eliminatory": { "type": "boolean" },
        "estimated_difficulty": { "type": "string", "enum": ["EASY", "MEDIUM", "HARD"] }
      },
      "required": ["is_eliminatory", "estimated_difficulty"]
    },
    "content": {
      "type": "object",
      "properties": {
        "question_text": { "type": "string" },
        "options": {
          "type": "array",
          "items": {
            "type": "object",
            "properties": {
              "option_id": { "type": "string" },
              "text": { "type": "string" }
            },
            "required": ["option_id", "text"]
          }
        },
        "correct_option_id": { "type": "string" },
        "vector_sign_asset_id": { "type": ["string", "null"] }
      },
      "required": ["question_text", "options", "correct_option_id"]
    },
    "educational_metadata": {
      "type": "object",
      "properties": {
        "paraphrased_explanation": { "type": "string" },
        "statutory_source_reference": {
          "type": "object",
          "properties": {
            "source_id": { "type": "string" },
            "governing_law": { "type": "string" },
            "article_section": { "type": "string" },
            "official_url": { "type": "string", "format": "uri" }
          },
          "required": ["source_id", "governing_law", "article_section"]
        }
      },
      "required": ["paraphrased_explanation", "statutory_source_reference"]
    }
  },
  "required": ["question_id", "lifecycle_status", "schema_version", "jurisdiction", "categories", "target_subclasses", "exam_parameters", "content", "educational_metadata"]
}

```

---

## 5. SQLDelight Schema Implementation Strategy

To handle the relational integrity constraints of multi-class routing, dynamic localized targeting, and version controls, implement the following relational schema architecture in SQLDelight:

```sql
-- 1. Jurisdiction Lookup Entity
CREATE TABLE Jurisdiction (
    code TEXT NOT NULL PRIMARY KEY,
    level TEXT AS String NOT NULL, -- 'NATIONAL', 'PROVINCIAL', 'MUNICIPAL'
    sinalic_integrated INTEGER AS Boolean NOT NULL DEFAULT 1
);

-- 2. Question Definition Entity
CREATE TABLE Question (
    id TEXT NOT NULL PRIMARY KEY,
    lifecycle_status TEXT NOT NULL, -- 'DRAFT', 'PARSED', 'REVIEWED', 'APPROVED', 'DEPRECATED'
    schema_version TEXT NOT NULL,
    jurisdiction_code TEXT NOT NULL,
    question_text TEXT NOT NULL,
    correct_option_id TEXT NOT NULL,
    vector_sign_asset_id TEXT,
    is_eliminatory INTEGER AS Boolean NOT NULL DEFAULT 0,
    estimated_difficulty TEXT NOT NULL,
    paraphrased_explanation TEXT NOT NULL,
    source_id TEXT NOT NULL,
    governing_law TEXT NOT NULL,
    article_section TEXT NOT NULL,
    official_url TEXT,
    FOREIGN KEY (jurisdiction_code) REFERENCES Jurisdiction(code) ON DELETE RESTRICT
);

-- 3. Options Entity (One-to-Many Relationship to Question)
CREATE TABLE QuestionOption (
    option_id TEXT NOT NULL,
    question_id TEXT NOT NULL,
    text TEXT NOT NULL,
    PRIMARY KEY (option_id, question_id),
    FOREIGN KEY (question_id) REFERENCES Question(id) ON DELETE CASCADE
);

-- 4. Categories Join Table (Normalized Category Mapping)
CREATE TABLE QuestionCategoryMapping (
    question_id TEXT NOT NULL,
    category TEXT NOT NULL, -- 'CITIZEN_ETHICS', 'TRAFFIC_RULES', etc.
    PRIMARY KEY (question_id, category),
    FOREIGN KEY (question_id) REFERENCES Question(id) ON DELETE CASCADE
);

-- 5. Class License Target Join Table (Many-to-Many Multi-Class Mapping Engine)
CREATE TABLE QuestionSubclassMapping (
    question_id TEXT NOT NULL,
    subclass_code TEXT NOT NULL, -- 'A1.1', 'B1', etc.
    PRIMARY KEY (question_id, subclass_code),
    FOREIGN KEY (question_id) REFERENCES Question(id) ON DELETE CASCADE
);

-- 6. Core Query Index: Fetches randomized exam pools matching current user criteria
getExamQuestionPool:
SELECT Q.* FROM Question AS Q
INNER JOIN QuestionSubclassMapping AS QSM ON Q.id = QSM.question_id
WHERE Q.lifecycle_status = 'APPROVED'
  AND QSM.subclass_code = :targetSubclass
  AND (Q.jurisdiction_code = :userLocationCode OR Q.jurisdiction_code = 'AR')
ORDER BY RANDOM()
LIMIT :limitSize;

```

---

## 6. Comprehensive Practical Example Records

### Example 1: National SINALIC Right-of-Way Rule (Multi-Class Private Tier)

```json
{
  "question_id": "ARG-QA-00104",
  "lifecycle_status": "APPROVED",
  "schema_version": "2026.01",
  "jurisdiction": {
    "level": "NATIONAL",
    "code": "AR",
    "sinalic_integrated": true
  },
  "categories": ["TRAFFIC_RULES", "ACCIDENT_PREVENTION"],
  "target_subclasses": ["A1.1", "A1.2", "A1.3", "A1.4", "A2.1", "A2.2", "A3", "B1", "B2"],
  "exam_parameters": {
    "is_eliminatory": true,
    "estimated_difficulty": "MEDIUM"
  },
  "content": {
    "question_text": "Al aproximarse a una encrucijada no semaforizada, usted observa un vehículo que se acerca por su derecha de forma simultánea. De acuerdo con las normas de tránsito nacionales, ¿cómo debe proceder?",
    "options": [
      { "option_id": "OPT-A", "text": "Debe ceder el paso obligatoriamente, ya que la prioridad de la derecha es absoluta." },
      { "option_id": "OPT-B", "text": "Puede avanzar de forma directa si circula por una avenida, sin importar el sentido del otro vehículo." },
      { "option_id": "OPT-C", "text": "Debe acelerar para liberar la bocacalle con rapidez antes de que arribe el otro conductor." }
    ],
    "correct_option_id": "OPT-A",
    "vector_sign_asset_id": null
  },
  "educational_metadata": {
    "paraphrased_explanation": "La normativa argentina establece que la prioridad de paso en intersecciones no reguladas pertenece siempre al vehículo que asoma por el lado derecho. Esta prerrogativa es de carácter absoluto y solo cede ante excepciones taxativas (ambulancias en emergencia, paso de trenes o vías pavimentadas frente a caminos de tierra).",
    "statutory_source_reference": {
      "source_id": "S01",
      "governing_law": "Ley Nacional de Tránsito N° 24.449",
      "article_section": "Artículo 41",
      "official_url": "https://servicios.infoleg.gob.ar/infolegInternet/anexos/30000-34999/34449/norma.htm"
    }
  }
}

```

### Example 2: Specialized Professional Tier Asset (Class C Truck Mechanics)

```json
{
  "question_id": "ARG-QA-00482",
  "lifecycle_status": "APPROVED",
  "schema_version": "2026.01",
  "jurisdiction": {
    "level": "NATIONAL",
    "code": "AR",
    "sinalic_integrated": true
  },
  "categories": ["BASIC_MECHANICS", "VEHICLE_SAFETY"],
  "target_subclasses": ["C1", "C2", "C3"],
  "exam_parameters": {
    "is_eliminatory": false,
    "estimated_difficulty": "HARD"
  },
  "content": {
    "question_text": "Durante la marcha de un camión equipado con sistema de frenos neumáticos, la alarma del manómetro de presión de aire en cabina se activa de forma persistente. ¿Qué indica esta condición crítica?",
    "options": [
      { "option_id": "OPT-A", "text": "Una fuga o pérdida de presión peligrosa en los tanques de aire, comprometiendo la capacidad de frenado del rodado." },
      { "option_id": "OPT-B", "text": "Un incremento estándar en la temperatura de fricción de las campanas traseras debido al uso regular." },
      { "option_id": "OPT-C", "text": "La necesidad rutinaria de purgar el filtro separador de agua al completar la jornada de distribución." }
    ],
    "correct_option_id": "OPT-A",
    "vector_sign_asset_id": null
  },
  "educational_metadata": {
    "paraphrased_explanation": "En camiones de gran porte comerciales (Clase C), los indicadores de presión neumática monitorizan la reserva de los depósitos del sistema de frenado. Una caída de presión por debajo del umbral seguro de operación activa alertas sonoras y visuales, advirtiendo al conductor profesional que el vehículo corre el riesgo de bloquearse o perder efectividad en el frenado de servicio.",
    "statutory_source_reference": {
      "source_id": "S02",
      "governing_law": "Disposición ANSV N° 54/2025",
      "article_section": "Módulo 5: Mecánica de Vehículos Pesados",
      "official_url": "https://www.argentina.gob.ar/normativa/nacional/disposici%C3%B3n-54-2025-410796/texto"
    }
  }
}

```