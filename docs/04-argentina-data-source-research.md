# **Strategic Compliance, Regulatory, and Technical Analysis of Argentina's Driver Licensing Theoretical and Practical Examination Framework**

The constitutional architecture of the Argentine Republic establishes a federal system where powers not explicitly delegated to the national government are reserved by the provinces1. This decentralization is highly visible in the regulation of traffic safety, police power, and driver licensing1. While the federal legislature enacted the *Ley Nacional de Tránsito N° 24.449* to establish a uniform normative base1, the execution, evaluation, and issuance of driver's licenses remain dependent on local adherence1. The *Agencia Nacional de Seguridad Vial* (ANSV) administers the *Sistema Nacional de Licencias de Conducir* (SINALIC) to integrate these fragmented jurisdictions into a unified national carnet, the *Licencia Nacional de Conducir* (LNC)4.  
The regulatory landscape underwent a significant transition with the enactment of *Decreto N° 196/2025*, which introduced a digital-first framework for license issuance, renewal, and validation across the republic5. However, because provinces must individually opt into federal digital initiatives, integration with SINALIC remains uneven6. This regulatory divergence impacts how software developers design theoretical and practical prep systems, as they must adapt software parameters to match the user's specific location.

## **National Integration and Jurisdiction Coverage Map**

The implementation of SINALIC and its digital-first remote processing under *Decreto N° 196/2025* has divided the country into three distinct jurisdictional categories5.

* **Full Digital SINALIC Integration**: Twenty-two jurisdictions have fully integrated their municipal and provincial registries with SINALIC6. In these territories, drivers can manage renewals, data modifications, and initial administrative procedures through centralized remote applications6.  
* **Partial Integration (CABA)**: The *Ciudad Autónoma de Buenos Aires* (CABA) operates a split model6. Professional licenses (Classes C, D, and E) are fully integrated into SINALIC to comply with interjurisdictional safety requirements6. For particular (private) licenses (Classes A and B), CABA maintains its own independent system accessed via the *miBA* portal6. Psychophysical evaluations are routed through 19 authorized municipal centers6.  
* **Non-Adherent Jurisdictions (PBA and Formosa)**: The *Provincia de Buenos Aires* (PBA) and the Province of Formosa have not integrated their licensing systems with the remote national framework6. In these jurisdictions, licensing remains traditional and presencial6. Applicants must attend a local *Centro Emisor de Licencias* (CEL) within their specific municipality to complete the course, medical checks, and physical testing6.

| Jurisdiction / Province | SINALIC Integration Status | Applicable Licensing Platform | Remote Processing Availability | Medical Evaluation Framework |
| :---- | :---- | :---- | :---- | :---- |
| **Federal Capital (CABA)** | Partial6 | SINALIC (Professional) / miBA (Particular)6 | Professional classes only6 | 19 Authorized Municipal Psychophysical Centers6 |
| **Provincia de Buenos Aires** | Non-Adherent6 | Provincial Licensing Database (CEL Municipalities)11 | None; fully presencial6 | Municipal CEL Staff / Authorized Local Doctors11 |
| **Formosa** | Non-Adherent6 | Provincial Registry | None; fully presencial6 | Local Municipal Register clinics |
| **Santa Fe** | Full7 | SINALIC / Provincial Driver Portal4 | Yes; remote renewals6 | Certified Provincial Medical Exam Centers14 |
| **Córdoba** | Full8 | SINALIC / Municipal VeDi Platform8 | Yes; remote renewals8 | Certified Municipal / National Examiners16 |
| **Other 19 Provinces** | Full6 | SINALIC4 | Yes; via SINALIC central portal6 | ANSV-Homologated CEL medical boards4 |

## **Official Source Inventory and Trust Matrix**

To ensure study materials are accurate and legally compliant, developers must source information directly from verified government channels. The following tables catalog these primary sources and evaluate them across key operational risk metrics.

### **Sourced Documents Inventory**

| Source Identifier | Publishing Authority | Official URL | Jurisdiction | Document Scope & Target Content |
| :---- | :---- | :---- | :---- | :---- |
| **S01** | National Road Safety Agency (ANSV) | https://www.argentina.gob.ar/servicio/curso-nacional-de-seguridad-vial \[cite: 18\] | National (SINALIC) | National Safety Course, PDF instruction manuals, interactive educational slides18. |
| **S02** | Federal Executive Branch | https://www.argentina.gob.ar/normativa/nacional/disposici%C3%B3n-54-2025-410796/texto \[cite: 4\] | National (SINALIC) | Core SINALIC integration parameters, exam limits, and module structures4. |
| **S03** | CABA Secretary of Transportation | https://documentosboletinoficial.buenosaires.gob.ar/publico/PE-RES-MDUYTGC-SECTRANS-301-19-ANX.pdf \[cite: 19\] | CABA | CABA Driver Manual, sustainable mobility, scoring system, and traffic laws19. |
| **S04** | PBA Directorate of Road Safety | https://www.gba.gob.ar/static/seguridadvial/docs/manual\_del\_conductor.pdf \[cite: 20\] | Provincia de Buenos Aires | Provincial driver manual, safety standards, and exam guidelines12. |
| **S05** | PBA Directorate of Road Safety | https://www.gba.gob.ar/static/seguridadvial/docs/cuestionario.pdf \[cite: 21\] | Provincia de Buenos Aires | Official multiple-choice question database for provincial theoretical exams12. |
| **S06** | Santa Fe Safety Agency | https://www.santafe.gob.ar/examenlicencia/examenETLC/listarCuestionarios.php \[cite: 22\] | Provincia de Santa Fe | Official online exam engine and sample questions15. |
| **S07** | Santa Fe Ministry of Education | https://www.santafe.gov.ar/index.php/educacion/content/download/234217/1232006/file/Manual%20Conductor.pdf \[cite: 23\] | Provincia de Santa Fe | Provincial manual, defensive driving, and technical rules23. |
| **S08** | Córdoba Municipality | https://documentos.cordoba.gob.ar/MUNCBA/AreasGob/Mov/Manual-del-buen-conductorV3.pdf \[cite: 24\] | Córdoba Municipality | Traffic rules and sample questions based on Ordinance 9981/9824. |

### **Source Trust and Risk Metrics**

The trust ranking of each source is calculated using four parameters scored from 1 (lowest) to 5 (highest):

* **Authority**: The institutional capacity of the publisher to draft and enforce the regulations.  
* **Update Frequency**: The frequency of document updates to match active laws, such as *Decreto N° 196/2025*5.  
* **Legal Accessibility**: The presence of explicit licensing terms (e.g., Creative Commons) allowing commercial or public use25.  
* **Direct Data Safety**: The technical reliability and stability of the hosting government servers.

![][image1]

| Source ID | Authority | Update Frequency | Legal Accessibility | Direct Data Safety | Overall Score | Trust Designation & Operational Notes |
| :---- | :---- | :---- | :---- | :---- | :---- | :---- |
| **S01** | 5 | 5 | 5 | 5 | **5.00** | **Highest Trust**: Under National CC BY 4.0 license; regularly updated18. |
| **S02** | 5 | 4 | 5 | 5 | **4.75** | **Highest Trust**: Central legislative decree with high legal weight4. |
| **S03** | 5 | 4 | 5 | 5 | **4.75** | **High Trust**: Under CABA CC BY 2.5 AR license; highly structured19. |
| **S04** | 5 | 4 | 4 | 4 | **4.25** | **High Trust**: Official provincial resource; hosted on stable servers20. |
| **S05** | 5 | 3 | 4 | 4 | **4.00** | **High Trust**: Official question database, but updates can lag behind national changes21. |
| **S06** | 5 | 4 | 3 | 4 | **4.00** | **Medium-High Trust**: Direct Santa Fe portal, but licensing terms are less explicit22. |
| **S07** | 5 | 3 | 3 | 4 | **3.75** | **Medium-High Trust**: Valid manual, but relies on legacy formatting23. |
| **S08** | 4 | 3 | 3 | 4 | **3.50** | **Medium Trust**: Limited to municipal jurisdiction under Ordinance 9981/9824. |

## **National License Classes, Subclasses, and Prerequisites**

To construct a compliant driving exam app, developers must align study modules with SINALIC's national licensing categories28. The classification system determines age limits, experience prerequisites, and whether professional-grade clinical documentation is required10.

                                \[LICENCIA DE CONDUCIR\]  
                                          │  
                  ┌───────────────────────┴───────────────────────┐  
                  ▼                                               ▼  
         \[Particular Classes\]                           \[Professional Classes\]  
         \- Classes A, B, F, G                           \- Classes C, D, E  
         \- Min. Age 16/18 \[cite: 14, 30\]                \- Min. Age 21  
         \- Self-declared Medical DDJJ \[cite: 31\]        \- Required Criminal Record Check \[cite: 10, 11\]  
         \- Validity up to 5 Years            \- Higher Psychophysical Checks \[cite: 10, 32\]

Under *Decreto N° 196/2025*, professional classes are subject to stricter oversight5. For example, Class D applicants must present a clean *Certificado de Antecedentes Penales* (criminal record check)10. This check must verify the absence of convictions for offenses against persons, sexual integrity, or drug-related crimes as outlined in the Argentine Criminal Code (*Código Penal*)5.

| Class | Primary Target Vehicles | Subclass Definitions | Age Prerequisites & Experience Requirements | Validity & Medical Oversight |
| :---- | :---- | :---- | :---- | :---- |
| **A** | Motorcycles, mopeds, and motor-quads28. | **A 1.1**: ![][image2] / ![][image3] \[cite: 28\] **A 1.2**: ![][image4] \[cite: 28\] **A 1.3**: ![][image4] \[cite: 28\] **A 1.4**: ![][image5] \[cite: 28\] **A 2.1**: non-cabin ![][image4] \[cite: 28\] **A 2.2**: non-cabin ![][image5] \[cite: 28\] **A 3**: cabin-steered28 | **A 1.1**: 16 years30. **A 1.2 / A 2.1**: 18 years14. **A 1.3 / A 1.4 / A 2.2**: Requires 2 years of lower subclass experience, waived if ![][image6] years old28. | Up to 5 years30. Medical check requires standard vision and hearing verification11. |
| **B** | Standard cars, utility vehicles, and light trailers28. | **B 1**: Rigid ![][image7] \[cite: 28\] **B 2**: B 1 vehicle with trailer up to ![][image8] or caravan28 | **B 1**: 18 years14. **B 2**: Requires 1 year of active experience in B 128. | Up to 5 years30. Declines to 3 years after age 655. |
| **C** | Rigid commercial trucks and large motorhomes28. | **C 1**: up to ![][image9] \[cite: 28\] **C 2**: up to ![][image10] \[cite: 28\] **C 3**: ![][image11] \[cite: 28, 29\] | **All Subclasses**: 21 years12. Requires 1 year of Class B experience5. | Max 2 years (reduced with age)5. Professional clinical labs, ECG, and EEG are required15. |
| **D** | Professional passenger transport (taxis, buses, school transport)28. | **D 1**: up to 8 seats28 **D 2**: 8 to 20 seats28 **D 3**: ![][image6] seats29 **D 4**: Emergency services29 | **All Subclasses**: 21 years12. Requires 1 year of Class B experience5. | Requires a clean criminal record (Certificado de Antecedentes Penales)5. |
| **E** | Specialized cargo, articulated trucks, and heavy machinery29. | **E 1**: Articulated trucks29 **E 2**: Non-agricultural machinery29 | **All Subclasses**: 21 years12. Requires 1 year of Class B experience5. | Standard professional medical requirements; includes hazardous materials endorsements5. |
| **F** | Vehicles adapted for drivers with physical disabilities29. | Custom code matching modification29 | 18 years (or 21 for professional classes)5. | Must match the corresponding base vehicle class (A, B, C, D, or E)5. |
| **G** | Agricultural machinery and utility tractors29. | **G 1**: Standard tractors29 **G 2**: Specialty agricultural harvesters29 | 18 years14. | Standard particular medical checks; limited to agricultural use4. |

## **Theoretical Examination Curricula and Jurisdictional Exam Structures**

National driving safety guidelines require theoretical exams to cover five primary themes: road safety ethics, traffic rules, signage systems, vehicle mechanics, and defensive driving4.

### **Mandatory Thematic Content and Safety Education Modules**

1. **Citizen Ethics and Sustainable Coexistence**: Promotes the responsible use of public space and views traffic behavior as a social action4. This module focuses on protecting vulnerable road users, such as pedestrians and cyclists19. It emphasizes the "priority pedestrian" rule, which grants absolute right-of-way to pedestrians at intersections1. It also covers sustainable urban mobility, transit plans, and the demerit point system (*Scoring*)19.  
2. **Defensive Driving and Accident Prevention**: Teaches techniques to prevent collisions and highlights the distinction between unpreventable "accidents" and preventable "traffic incidents"19. It addresses key risk factors, such as driving under the influence of alcohol or drugs, speeding, and using mobile devices while driving19. It also outlines a driver's legal duties during an incident, including stopping, verifying safety, and providing identification1.  
3. **Comprehensive Signage System**: Covers the signaling categories defined under *Ley N° 24.449*3. This includes horizontal painted road markings (e.g., continuous lines that prohibit lane changes versus dashed lines that allow passing)24, active vertical warning and regulatory signs, and temporary signals3.  
4. **Active and Passive Safety Systems**: Explains active safety features (e.g., braking systems, steering, and tire tread depth) and passive safety features (e.g., crumple zones, seat belts, and airbags)4. It also covers basic vehicle maintenance, including checking fluid levels, tire pressure, and ensuring correct headlight alignment12.  
5. **Basic Mechanics and Professional Procedures**: Focuses on professional classes12. This covers heavy vehicle instrumentation (such as air brake pressure gauges and standard cab layouts), weight distribution constraints, cargo securement, and emergency protocols for handling hazardous materials35.

### **Structural Variations in Regional Examinations**

The theoretical examination parameters change significantly once an applicant moves past the national SINALIC guidelines and enters regional jurisdictions4.

\[National SINALIC\] ────► 30 Questions ──► 80% Pass ──► Includes Eliminatory Items  
\[Provincia de B.A.\] ───► 40 Questions ──► 75% Pass  ──► 5 Eliminatory (100% correct required)  
\[Santa Fe\] ────────────► 20 Questions ──► 90% Pass  ──► Max 2 errors permitted  
\[Córdoba\] ─────────────► 20 Questions ──► 30 Mins \[cite: 16\]   ──► Local Ordinance 9981/98

#### **National SINALIC Standard**

Under *Disposición ANSV N° 54/2025* and its modifications, standard theoretical evaluations must consist of at least 30 multiple-choice questions4. It requires a minimum passing score of 80% and includes critical "eliminatory" questions regarding high-risk maneuvers or key traffic safety rules4. If an applicant fails any of these eliminatory questions, they fail the entire exam4.  
The regulatory framework experienced some shifts when *Disposición N° 219/2025* rolled back parts of *Disposición N° 54/2025*32. The earlier decree had set a 40-question, 90% passing threshold for professional classes (C, D, E), but this was adjusted due to localized administrative constraints32. As a result, the evaluation framework remains subject to regulatory updates that developers must track32.

#### **Provincia de Buenos Aires (PBA)**

Operating independently of remote SINALIC digital systems, PBA enforces its own evaluation structure under *Disposición N° 46/19*27. The exam is designed around a 40-question matrix corresponding to the requested class12. The passing grade is set at a minimum of 75%20.  
PBA requires applicants to answer 5 specific "eliminatory questions" with 100% accuracy20. A single error in these five core questions results in an automatic fail, regardless of the overall score20. The maximum time permitted to complete the exam is 2 hours12.

#### **Provincia de Santa Fe**

Santa Fe manages theoretical exams through direct localized digital terminals14. The standard exam consists of 20 randomized questions selected from the provincial database38. The passing threshold is 90% (allowing a maximum of 2 errors)38.

#### **Municipio de Córdoba**

Utilizing municipal autonomy, Córdoba bases its exams on the *Ordenanza Municipal de Tránsito N° 9981/98*36. The examination comprises 20 questions to be resolved within a 30-minute limit, pulling questions directly from the municipal "Guía del Buen Conductor" database16.

## **Practical Driving Examination Criteria**

The practical driving test is the final phase of licensing and measures physical driving skills11. Under *Disposición N° 54/2025* and provincial regulations, this evaluation consists of two parts: a pre-drive safety inspection and an on-road maneuver assessment4.

| Practical Evaluation Phase | Specific Performance Criteria Tested | Passing Thresholds & Requirements | Automatic Failure Conditions (Reprobación Automática) |
| :---- | :---- | :---- | :---- |
| **Pre-Drive Technical Inspection** | Checking tires, lights, and horn; adjusting mirrors and the driver's seat; verifying emergency equipment12. | Confirming that the applicant can identify and operate all primary dashboard instruments12. | Inability to locate or operate safety controls; vehicle does not meet standard safety requirements12. |
| **Basic Control Maneuvers** | Smooth starting, proper clutch-to-throttle coordination, steady low-speed driving, and straight-line reversing12. | Maintaining continuous control without engine stalls or jerking maneuvers12. | Striking test cones; stalling the engine more than three times; mounting the curb12. |
| **Parallel Parking** | Reverse-angle parking into a marked space within a set number of maneuvers and time limit12. | Parking the vehicle parallel to the curb, leaving between ![][image12] and ![][image13] of clearance12. | Colliding with barrier posts; parking too far from the curb; exceeding the maximum maneuver limit12. |
| **Professional Simulator Test** | Handling cargo, managing technical or mechanical failures, and driving under adverse weather conditions4. | Successfully navigating simulated hazards, maintaining safe following distances, and demonstrating correct emergency reactions35. | Causing a simulated high-impact collision; ignoring major safety warnings; failing critical system tests35. |

The practical exam includes specific automatic failure triggers12. Committing any of these actions results in an immediate fail, ending the test regardless of prior performance12.

* **Seatbelt Non-Compliance**: Driving the test vehicle without securing the seatbelt before starting the engine12.  
* **Curb Contact**: Mounting the curb or striking pedestrian walkways with any tire during parking or turning maneuvers12.  
* **Unescorted Arrival**: Arriving at the test site without a licensed driver accompanying the test vehicle12.  
* **Missing Vehicle Documentation**: Failing to present the vehicle's registration (*Cédula Verde* or *Azul*), valid proof of insurance, or a current vehicle inspection certificate (*VTV*)39.

## **Intellectual Property, Licensing, and Database Rights**

Integrating government-published questions, manuals, and images into a commercial mobile or web application presents distinct intellectual property challenges in Argentina.

                                \[LEY N° 11.723\]  
                                       │  
                  ┌────────────────────┴────────────────────┐  
                  ▼                                         ▼  
         \[Standard Protected Works\]                \[Exempted Public Data\]  
         \- Didactic Manuals                        \- Raw Legislative Texts  
         \- Compiled Databases                      \- Technical Specifications  
         \- Public Sector Diagrams                  \- Public Space Signage  
                  │                                         │  
                  ▼                                         ▼  
        Subject to Copyright Restrictions          Free Commercial Reuse  
        Requires Explicit CC Licensing             Requires Citation Only

### **Scope of National Copyright Law (Ley N° 11.723)**

Argentine copyright is governed by *Ley N° 11.723 (Régimen Legal de la Propiedad Intelectual)*, which protects literary, scientific, educational, and artistic works from the moment of their creation41. The law grants authors exclusive exploitation rights, including reproduction, translation, adaptation, and commercial licensing41.  
Government publications, including driver education manuals, diagrams, and question collections, are protected under this statute45. *Ley N° 11.723* does not contain a broad "fair use" exception for commercial educational applications45. While Article 10 permits short citations for educational or scientific purposes (up to 1,000 words from literary works)43, this exception does not cover the complete copy-paste integration of driving manuals, proprietary databases, or graphic design assets into a commercial product45.

### **Exploitation of Creative Commons Licensing in Public Portals**

The primary mitigating factor for software developers is the open licensing policy adopted by several Argentine government portals.

* **National Portal (argentina.gob.ar)**: The terms and conditions of the central national portal state that the site licenses all its content under the **Creative Commons Attribution 4.0 International (CC BY 4.0)** license, except where explicitly stated otherwise25. CC BY 4.0 permits commercial reproduction, redistribution, adaptation, and transformation of the material, provided proper attribution is given25.  
* **CABA Portal (buenosaires.gob.ar)**: CABA's terms and conditions license their portal content under the **Creative Commons Attribution 2.5 Argentina (CC BY 2.5 AR)** license26. This license also permits commercial adaptation and redistribution under attribution mandates45.  
* **Buenos Aires Province Open Data (catalogo.datos.gba.gob.ar)**: Datasets published under the provincial open-data platform are licensed under **Creative Commons Attribution 4.0 (CC BY 4.0)**, confirming that public data collections are available for commercial applications48.

### **Paying Public Domain (Dominio Público Pagante)**

Argentina enforces a *Dominio Público Pagante* (Paying Public Domain) system under *Decreto-Ley N° 1224/58* and *Resolución FNA N° 15.850/77*51. When standard copyrighted works pass into the public domain (typically 70 years after the author's death or 50 years after publication for corporate/institutional works)41, commercial exploitation still requires the payment of a tax to the *Fondo Nacional de las Artes* (FNA)2.  
For digital applications, *Resolución FNA N° 270121/2022* updated this framework by adding **"Rubro 12.3: Derechos de edición – Entorno digital"**58. This rule charges a licensing fee of 3 *Módulos* (the FNA's standard tax unit) for distributing public domain literary works in digital environments58. While raw legislative texts (laws and decrees) are exempt from this tax, any copyrighted explanatory guide or educational manual that enters the public domain remains subject to these commercial fees42.

### **Database Compilation Rights**

Under *Ley N° 11.723* (as amended by *Ley N° 25.036*), database compilations are protected as intellectual creations44. The law protects the structure, organization, and selection of the data, even if the individual data points are public facts44.  
The database creator can oppose the systematic extraction or commercial reuse of a substantial part of their compilation63. This means that while individual traffic regulations are public facts, copying an entire structured question-and-answer database from a municipality like Santa Fe or Córdoba without authorization can infringe database compilation rights44.

### **Paraphrasing and Derivative Works as a Compliance Strategy**

To minimize copyright and database compilation risks, developers should apply Article 26 of *Ley N° 11.723*42. Under this article, any person who adapts, modifies, or parodies a work that is not in the private domain becomes the exclusive owner of that adaptation42.  
By extracting raw legal rules from *Ley N° 24.449* or municipal ordinances (which are public texts exempt from copyright) and writing original multiple-choice questions around them, developers create new, protected works56. This approach avoids database copying claims and provides clean intellectual property ownership for the application42.

## **Strategic Implementation and Compliance Framework**

### **Recommended Data Acquisition Strategy**

Developers can establish a legally secure data pipeline by following a structured acquisition strategy:

\[Official Law Source\] ──► \[Extract Core Legal Rule\] ──► \[Draft Original Question Schema\]  
                                                                │  
 \[Verify Local CC License\] ◄── \[Document Attribution Data\] ◄────┘

1. **Extract Raw Legislative Texts**: Retrieve official, uncopyrighted laws (such as *Ley N° 24.449*, *Decreto N° 779/95*, and municipal traffic ordinances) from *InfoLEG* or official municipal registers3.  
2. **Retrieve CC-Licensed Manuals**: Download official manuals from *argentina.gob.ar* and *buenosaires.gob.ar*18. Document the date of acquisition, URL, and the active Creative Commons license to maintain a clear compliance record25.  
3. **Draft Unique, Paraphrased Questions**: Do not copy municipal question banks directly44. Instead, identify the core traffic rules and construct original, high-quality multiple-choice questions42.  
4. **Develop Vector Traffic Signs**: Design standard traffic signs using official specifications from the national transit code3. This ensures consistent visual assets and avoids using low-resolution or copyrighted municipal images2.

### **Paraphrasing and Derived Question Pipeline Example**

To demonstrate how developers can transform raw statutory rules into unique, proprietary questions, consider this implementation of Article 41 of *Ley N° 24.449* (Right-of-Way rules)34:

* **Raw Statutory Fact**: Under *Ley N° 24.449*, priority of passage at intersections belongs to the driver approaching from the right34. This priority is absolute and is only lost under specific exceptions, such as when crossing a railroad, joining a paved road from a dirt road, or entering a roundabout24.  
* **Proprietary Question Design**:  
  * *Question Stem*: "While approaching an uncontrolled intersection, you observe another vehicle approaching from your right. According to national traffic laws, how should you proceed?"  
  * *Option A (Correct)*: "Bring your vehicle to a complete stop and yield the right-of-way, as the driver on the right holds absolute priority24."  
  * *Option B (Incorrect)*: "Proceed with caution without stopping, as right-side priority does not apply at uncontrolled intersections24."  
  * *Option C (Incorrect)*: "Accelerate to clear the intersection first, as right-side priority only applies on major avenues24."  
* **Compliance Benefit**: The question tests the exact legal standard of the national code without copying the wording, layout, or design of existing municipal questionnaires42.

## **Technical Source Version Metadata Schema**

To track database changes and automate updates when driving laws change, developers should implement a JSON-based version tracking schema.

JSON  
{  
  "$schema": "https://json-schema.org/draft/2020-12/schema",  
  "title": "ArgentinaTransitQuestionMetadata",  
  "type": "object",  
  "properties": {  
    "question\_id": {  
      "type": "string",  
      "pattern": "^ARG-QA-\[0-9\]{5}$"  
    },  
    "jurisdiction": {  
      "type": "object",  
      "properties": {  
        "level": { "type": "string", "enum": \["National", "Provincial", "Municipal"\] },  
        "name": { "type": "string" },  
        "sinalic\_integrated": { "type": "boolean" }  
      },  
      "required": \["level", "name", "sinalic\_integrated"\]  
    },  
    "license\_mapping": {  
      "type": "object",  
      "properties": {  
        "class": { "type": "string", "enum": \["A", "B", "C", "D", "E", "F", "G"\] },  
        "subclass": { "type": "string" }  
      },  
      "required": \["class"\]  
    },  
    "regulatory\_source": {  
      "type": "object",  
      "properties": {  
        "law\_name": { "type": "string" },  
        "article\_section": { "type": "string" },  
        "last\_verified\_date": { "type": "string", "format": "date" },  
        "official\_url": { "type": "string", "format": "uri" }  
      },  
      "required": \["law\_name", "article\_section", "last\_verified\_date"\]  
    },  
    "licensing": {  
      "type": "object",  
      "properties": {  
        "license\_type": { "type": "string", "enum": \["CC-BY-4.0", "CC-BY-2.5-AR", "Public-Domain-Statute", "Proprietary-Derived"\] },  
        "attribution\_required": { "type": "boolean" },  
        "attribution\_text": { "type": "string" }  
      },  
      "required": \["license\_type", "attribution\_required"\]  
    },  
    "exam\_parameters": {  
      "type": "object",  
      "properties": {  
        "is\_eliminatory": { "type": "boolean" },  
        "estimated\_difficulty": { "type": "string", "enum": \["Easy", "Medium", "Hard"\] }  
      },  
      "required": \["is\_eliminatory"\]  
    }  
  },  
  "required": \["question\_id", "jurisdiction", "license\_mapping", "regulatory\_source", "licensing", "exam\_parameters"\]  
}

## **Manual Verification and Update Workflow**

Argentine driving regulations can be revised overnight through federal resolutions published in the *Boletín Oficial*31. To maintain accuracy, developers must run a continuous verification and update workflow:

\[National Bulletin Alert\] ──► \[Assess Question Impact\] ──► \[Draft Paraphrased Updates\]  
                                                                     │  
 \[Production Hot-Reload\] ◄── \[QA Sandbox Validation\] ◄───────────────┘

1. **Set Up Automated Alerts**: Monitor the *Boletín Oficial de la República Argentina* and key provincial bulletins for terms like "Seguridad Vial", "SINALIC", "Tránsito", "Licencia", or "ANSV"31.  
2. **Review Legislative Changes**: When a change is detected (such as new speed limits, blood-alcohol limits, or safety equipment requirements)34, compile the raw legal text3.  
3. **Identify Impacted Content**: Query the metadata database to find all questions linked to the modified law or article:  
   ![][image14]  
4. **Draft and Verify Derived Questions**: Rewrite the affected questions to align with the new regulations while ensuring clean IP ownership42.  
5. **Update Exam Configuration Parameters**: If a jurisdiction changes its evaluation rules (for example, if a province shifts from 30 to 40 questions), update the corresponding parameters in the app's regional exam configuration table4.  
6. **Deploy Staged Updates**: Load updated questions and settings into a testing environment, verify functionality, and deploy the updates to production users.

## **Low-Trust and Avoidance Registry**

To maintain data quality and secure intellectual property, developers must avoid sourcing exam prep material from unofficial channels:

* **Unofficial Simulator Portals**: Dozens of monetization-driven websites host driving simulators for Argentine municipalities. These portals often rely on outdated question pools, contain unverified traffic advice, and lack the legal rights to distribute municipal exam material41.  
* **Scraped Databases from Competing Applications**: Direct database exports from third-party applications present significant legal risks. These datasets are often compiled without proper licensing and can expose developers to copyright or database compilation claims44.  
* **Unsecured Community Forums and Shared Cloud Drives**: Public forums and shared cloud folders (such as Google Drive or MediaFire links) often contain outdated driving manuals or unofficial question sheets45. Relying on these sources can lead to inaccurate study material and presents potential cybersecurity risks25.

By relying strictly on official government portals (such as ANSV and SINALIC) and using the compliance and paraphrasing strategies outlined in this report, developers can build a technically accurate and legally secure driver preparation platform for the Argentine market4.

#### **Works cited**

1. MANUAL DE LEGISLACIÓN VIAL MODULO LEGISLACIÓN VIAL NIVEL 1 (BÁSICO), [https://cdi.mecon.gob.ar/bases/docelec/az2114.pdf](https://cdi.mecon.gob.ar/bases/docelec/az2114.pdf)  
2. PROPIEDAD INTELECTUAL E INSTITUCIONES CULTURALES, [https://espigas.org.ar/uploads/Presentaciones%20y%20bibliograf%C3%ADa.pdf](https://espigas.org.ar/uploads/Presentaciones%20y%20bibliograf%C3%ADa.pdf)  
3. LEY NACIONAL DE TRANSITO Nº 24449 (Texto con las Leyes modificatorias Nros. 24788, 25456, 25857, 25965, 2636 \- Digesto Municipal \- Municipalidad de Paraná, [https://digesto.parana.gob.ar/norma/4933/mostrarPDF](https://digesto.parana.gob.ar/norma/4933/mostrarPDF)  
4. TEXTO ORIGINAL \- Disposición 54 / 2025 \- AGENCIA NACIONAL DE SEGURIDAD VIAL, [https://www.argentina.gob.ar/normativa/nacional/disposici%C3%B3n-54-2025-410796/texto](https://www.argentina.gob.ar/normativa/nacional/disposici%C3%B3n-54-2025-410796/texto)  
5. TEXTO ORIGINAL \- Decreto Reglamentario 196 / 2025 \- PODER EJECUTIVO NACIONAL, [https://www.argentina.gob.ar/normativa/nacional/decreto-196-2025-410682/texto](https://www.argentina.gob.ar/normativa/nacional/decreto-196-2025-410682/texto)  
6. ¿Quiénes no podrán acceder a la licencia digital de conducir en Argentina? \- Infocielo, [https://www.infocielo.com/sociedad/quienes-no-podran-acceder-a-la-licencia-digital-de-conducir-en-argentina](https://www.infocielo.com/sociedad/quienes-no-podran-acceder-a-la-licencia-digital-de-conducir-en-argentina)  
7. Licencia de conducir digital: Salvo en dos provincias, ya se puede tramitar en gran parte del país, [https://elregionaldigital.com.ar/licencia-de-conducir-digital-salvo-en-dos-provincias-ya-se-puede-tramitar-en-gran-parte-del-pais/](https://elregionaldigital.com.ar/licencia-de-conducir-digital-salvo-en-dos-provincias-ya-se-puede-tramitar-en-gran-parte-del-pais/)  
8. Licencia de conducir digital: cuáles son las dos provincias que aún no la incorporaron y cómo se hace el trámite en línea \- Infobae, [https://www.infobae.com/economia/2025/05/23/licencia-de-conducir-digital-cuales-son-las-dos-provincias-que-aun-no-la-incorporaron-y-como-se-hace-el-tramite-en-linea/](https://www.infobae.com/economia/2025/05/23/licencia-de-conducir-digital-cuales-son-las-dos-provincias-que-aun-no-la-incorporaron-y-como-se-hace-el-tramite-en-linea/)  
9. Licencia de conducir digital: qué grupo no podrá hacer el trámite online, [https://www.ambito.com/informacion-general/licencia-conducir-digital-que-grupo-no-podra-hacer-el-tramite-online-n6156431](https://www.ambito.com/informacion-general/licencia-conducir-digital-que-grupo-no-podra-hacer-el-tramite-online-n6156431)  
10. Renovación de Licencia de Conducir | Buenos Aires Ciudad, [https://buenosaires.gob.ar/gcaba\_historico/tramites/renovacion-de-licencia-de-conducir](https://buenosaires.gob.ar/gcaba_historico/tramites/renovacion-de-licencia-de-conducir)  
11. Licencias de conducir | Provincia de Buenos Aires, [https://www.gba.gob.ar/seguridadvial/licencias\_de\_conducir](https://www.gba.gob.ar/seguridadvial/licencias_de_conducir)  
12. MANUAL PARA LA EMISIÓN Y TRAMITACION DE LICENCIAS DE CONDUCIR \- Provincia de Buenos Aires |, [https://www.gba.gob.ar/static/seguridadvial/docs/manual\_tramitacion.pdf](https://www.gba.gob.ar/static/seguridadvial/docs/manual_tramitacion.pdf)  
13. PORTAL DE TRAMITES \- Gobierno de Santa Fe, [https://www.santafe.gob.ar/index.php/tramites/modul1/index](https://www.santafe.gob.ar/index.php/tramites/modul1/index)  
14. obtención de licencia en Centro de Habilitación de Conductores ¿En qué consiste? \- Gobierno de Santa Fe, [https://www.santafe.gov.ar/index.php/tramites/modul1/index?m=descripcion\&imprimir=1\&id=155907](https://www.santafe.gov.ar/index.php/tramites/modul1/index?m=descripcion&imprimir=1&id=155907)  
15. Paso a paso: cómo hacer el trámite para sacar la licencia de conducir en Santa Fe Capital, [https://www.airedesantafe.com.ar/santa-fe/licencia-conducir-santa-fe-capital-paso-paso-como-hacer-tramite](https://www.airedesantafe.com.ar/santa-fe/licencia-conducir-santa-fe-capital-paso-paso-como-hacer-tramite)  
16. Mi Licencia Digital \- Preguntas Frecuentes \- Municipalidad de Córdoba., [https://cordoba.gob.ar/mi-licencia-digital-preguntas-frecuentes/](https://cordoba.gob.ar/mi-licencia-digital-preguntas-frecuentes/)  
17. M-GP-LC01.2. Manual de Renovación de Licencia de Conducir.docx \- Municipalidad de Córdoba., [https://documentos.cordoba.gob.ar/MUNCBA/AreasGob/Mov/M-GP-LC01.2.%20Manual%20de%20Renovaci%C3%B3n%20de%20Licencia%20de%20Conducir.docx%20(3).pdf](https://documentos.cordoba.gob.ar/MUNCBA/AreasGob/Mov/M-GP-LC01.2.%20Manual%20de%20Renovaci%C3%B3n%20de%20Licencia%20de%20Conducir.docx%20\(3\).pdf)  
18. Curso Nacional de Seguridad Vial | Argentina.gob.ar, [https://www.argentina.gob.ar/servicio/curso-nacional-de-seguridad-vial](https://www.argentina.gob.ar/servicio/curso-nacional-de-seguridad-vial)  
19. Manual del conductor, [https://documentosboletinoficial.buenosaires.gob.ar/publico/PE-RES-MDUYTGC-SECTRANS-301-19-ANX.pdf](https://documentosboletinoficial.buenosaires.gob.ar/publico/PE-RES-MDUYTGC-SECTRANS-301-19-ANX.pdf)  
20. MANUAL DEL CONDUCTOR DE LA PROVINCIA DE BUENOS AIRES, [https://www.gba.gob.ar/static/seguridadvial/docs/manual\_del\_conductor.pdf](https://www.gba.gob.ar/static/seguridadvial/docs/manual_del_conductor.pdf)  
21. Preguntas Examen Teórico Licencia de Conducir \- Provincia de Buenos Aires |, [https://www.gba.gob.ar/static/seguridadvial/docs/cuestionario.pdf](https://www.gba.gob.ar/static/seguridadvial/docs/cuestionario.pdf)  
22. Examen \- Gobierno de Santa Fe, [https://www.santafe.gob.ar/examenlicencia/examenETLC/listarCuestionarios.php](https://www.santafe.gob.ar/examenlicencia/examenETLC/listarCuestionarios.php)  
23. Manual Conductor.pdf \- Gobierno de Santa Fe, [https://www.santafe.gov.ar/index.php/educacion/content/download/234217/1232006/file/Manual%20Conductor.pdf](https://www.santafe.gov.ar/index.php/educacion/content/download/234217/1232006/file/Manual%20Conductor.pdf)  
24. GUÍA DEL BUEN CONDUCTOR \- Municipalidad de Córdoba., [https://documentos.cordoba.gob.ar/MUNCBA/AreasGob/Mov/Manual-del-buen-conductorV3.pdf](https://documentos.cordoba.gob.ar/MUNCBA/AreasGob/Mov/Manual-del-buen-conductorV3.pdf)  
25. Términos y condiciones \- Argentina.gob.ar, [https://www.argentina.gob.ar/terminos-y-condiciones](https://www.argentina.gob.ar/terminos-y-condiciones)  
26. Términos y Condiciones | Buenos Aires Ciudad, [https://buenosaires.gob.ar/gcaba\_historico/terminos-y-condiciones](https://buenosaires.gob.ar/gcaba_historico/terminos-y-condiciones)  
27. Normativa vial | Provincia de Buenos Aires, [https://www.gba.gob.ar/seguridadvial/normativa\_vial\_0](https://www.gba.gob.ar/seguridadvial/normativa_vial_0)  
28. Clases y subclases de licencias \- Argentina.gob.ar, [https://www.argentina.gob.ar/seguridadvial/licencianacional/clasesysubclases](https://www.argentina.gob.ar/seguridadvial/licencianacional/clasesysubclases)  
29. TEXTO ORIGINAL \- Decreto 26 / 2019 \- TRANSITO Y SEGURIDAD VIAL | Argentina.gob.ar, [https://www.argentina.gob.ar/normativa/nacional/decreto-26-2019-318584/texto](https://www.argentina.gob.ar/normativa/nacional/decreto-26-2019-318584/texto)  
30. TEXTO ACTUALIZADO \- LEY 532 \- LEYES PROVINCIALES DE TRÁNSITO-REGLAMENTO GENERAL DE TRÁNSITO-SEGURIDAD VIAL-TRÁNSITO AUTOMOTOR-LIBERTAD DE CIRCULACIÓN-AUTOMOTORES-AUTOPISTAS-EDUCACIÓN VIAL, [https://www.argentina.gob.ar/normativa/provincial/ley-532-123456789-0abc-235-0000-9002bvorpced/actualizacion](https://www.argentina.gob.ar/normativa/provincial/ley-532-123456789-0abc-235-0000-9002bvorpced/actualizacion)  
31. Créase la Agencia Nacional de Seguridad Vial. Funciones. Modificaciones a la Ley Nº 24.449. Disposiciones Transitorias., [https://servicios.infoleg.gob.ar/infolegInternet/anexos/140000-144999/140098/norma.htm](https://servicios.infoleg.gob.ar/infolegInternet/anexos/140000-144999/140098/norma.htm)  
32. Licencias de conducir: el Gobierno da marcha atrás con una reforma y lanza nuevas reglas, [https://www.primeraedicion.com.ar/nota/101052876/ansv-marcha-atras-reforma-licencias-profesionales-requisitos/](https://www.primeraedicion.com.ar/nota/101052876/ansv-marcha-atras-reforma-licencias-profesionales-requisitos/)  
33. TEXTO ACTUALIZADO \- Disposición 48 / 2019 \- AGENCIA NACIONAL DE SEGURIDAD VIAL | Argentina.gob.ar, [https://www.argentina.gob.ar/normativa/nacional/disposici%C3%B3n-48-2019-320834/actualizacion](https://www.argentina.gob.ar/normativa/nacional/disposici%C3%B3n-48-2019-320834/actualizacion)  
34. Ley Nacional de Tránsito Nº 24.449, actualizada al 1/1/07 (fuente: infoleg) Síntesis de la Ley pertinente a Transporte Escola, [https://www.unterseccionalroca.org.ar/imagenes/u3/Ley%2024449%20%28TRANSPORTE%20%20Escolar%20-%20ACTUALIZADA%20AL%20A%C3%91O%202007%29.pdf](https://www.unterseccionalroca.org.ar/imagenes/u3/Ley%2024449%20%28TRANSPORTE%20%20Escolar%20-%20ACTUALIZADA%20AL%20A%C3%91O%202007%29.pdf)  
35. TEXTO ORIGINAL \- Disposición 56 / 2025 \- AGENCIA NACIONAL DE SEGURIDAD VIAL, [https://www.argentina.gob.ar/normativa/nacional/disposici%C3%B3n-56-2025-410798/texto](https://www.argentina.gob.ar/normativa/nacional/disposici%C3%B3n-56-2025-410798/texto)  
36. Mi Licencia de Conducir \- Municipalidad de Córdoba., [https://documentos.cordoba.gob.ar/MILICENCIA/RESUMEN-APUNTES-ORDENANZA-MUNICIPAL-DE-TRANSITO-nro-9981-98.pdf](https://documentos.cordoba.gob.ar/MILICENCIA/RESUMEN-APUNTES-ORDENANZA-MUNICIPAL-DE-TRANSITO-nro-9981-98.pdf)  
37. InfoLEG \- Ministerio de Justicia y Derechos Humanos \- Argentina, [https://servicios.infoleg.gob.ar/infolegInternet/anexos/325000-329999/326195/norma.htm](https://servicios.infoleg.gob.ar/infolegInternet/anexos/325000-329999/326195/norma.htm)  
38. LICENCIA DE CONDUCIR \- Municipalidad de Funes., [https://funes.gob.ar/views/assets/instructivos/licencias/instructivo\_licencias\_02\_2026.pdf](https://funes.gob.ar/views/assets/instructivos/licencias/instructivo_licencias_02_2026.pdf)  
39. Licencias \- Municipio de Marcos Paz, [https://www.marcospaz.net/municipiomarcospaz/tr%C3%A1mites-y-servicios/tramitesyservicios1/licencias-de-conducir/itemlist/tag/Licencias.html?start=10](https://www.marcospaz.net/municipiomarcospaz/tr%C3%A1mites-y-servicios/tramitesyservicios1/licencias-de-conducir/itemlist/tag/Licencias.html?start=10)  
40. Licencias de Conducir \- Municipio Pilar, [https://pilar.gov.ar/tramites/licencia-de-conducir/](https://pilar.gov.ar/tramites/licencia-de-conducir/)  
41. Propiedad intelectual \- Ley simple \- Argentina.gob.ar, [https://www.argentina.gob.ar/justicia/derechofacil/leysimple/propiedad-intelectual](https://www.argentina.gob.ar/justicia/derechofacil/leysimple/propiedad-intelectual)  
42. TEXTO ORIGINAL \- Ley 11723 \- PROPIEDAD INTELECTUAL | Argentina.gob.ar, [https://www.argentina.gob.ar/normativa/nacional/ley-11723-42755/texto](https://www.argentina.gob.ar/normativa/nacional/ley-11723-42755/texto)  
43. ABC sobre derecho de autor \- derechodeautor.org.ar, [https://www.derechodeautor.org.ar/wp-content/uploads/2021/08/derechodeautor.org\_.ar\_abc\_2021-11-17.pdf](https://www.derechodeautor.org.ar/wp-content/uploads/2021/08/derechodeautor.org_.ar_abc_2021-11-17.pdf)  
44. ABC sobre derecho de autor \- derechodeautor.org.ar, [https://www.derechodeautor.org.ar/wp-content/uploads/2021/08/03.-ABC-sobre-derechos-de-autor-17-08-21.pdf](https://www.derechodeautor.org.ar/wp-content/uploads/2021/08/03.-ABC-sobre-derechos-de-autor-17-08-21.pdf)  
45. Guía básica para la gestión de derechos de autor en educación a distancia \- UNDEF, [https://undef.edu.ar/wp-content/uploads/2025/10/Derechos-de-autor\_V2.pdf](https://undef.edu.ar/wp-content/uploads/2025/10/Derechos-de-autor_V2.pdf)  
46. LEY 11.723 \- REGIMEN LEGAL DE LA PROPIEDAD INTELECTUAL DECRETO REGLAMENTARIO Nº 41.233/34 SANCIONADA: 26-9-1933 PROMULGACIÓN: \- CAMZA, [https://camza.org.ar/wp-content/uploads/2021/11/11723.pdf](https://camza.org.ar/wp-content/uploads/2021/11/11723.pdf)  
47. Términos y Condiciones | Buenos Aires Ciudad, [https://buenosaires.gob.ar/gcaba\_historico/jefaturadegabinete/innovacionytransformaciondigital/quarkid/protocolo-quarkid/documentacion/terminos-y-condiciones](https://buenosaires.gob.ar/gcaba_historico/jefaturadegabinete/innovacionytransformaciondigital/quarkid/protocolo-quarkid/documentacion/terminos-y-condiciones)  
48. Insumos comprados por trámites de excepción \- Datos Abiertos PBA, [https://catalogo.datos.gba.gob.ar/dataset/insumos-comprados-por-tramites-de-excepcion](https://catalogo.datos.gba.gob.ar/dataset/insumos-comprados-por-tramites-de-excepcion)  
49. Reconocimientos \- Datos Abiertos PBA \- Provincia de Buenos Aires |, [https://catalogo.datos.gba.gob.ar/dataset/reconocimientos](https://catalogo.datos.gba.gob.ar/dataset/reconocimientos)  
50. Establecimientos culturales \- Datos Abiertos PBA \- Provincia de Buenos Aires |, [https://catalogo.datos.gba.gob.ar/dataset/cultura](https://catalogo.datos.gba.gob.ar/dataset/cultura)  
51. Derechos de autor y conexos y régimen de dominio público pagante | Buenos Aires Ciudad, [https://buenosaires.gob.ar/gcaba\_historico/economia-creativa/derechos-de-autor-y-conexos-y-dominio-publico](https://buenosaires.gob.ar/gcaba_historico/economia-creativa/derechos-de-autor-y-conexos-y-dominio-publico)  
52. La Justicia Federal confirmó la constitucionalidad del régimen de "Dominio Público Pagantes" \- Palabras del Derecho, [https://www.palabrasdelderecho.com.ar/articulo/4341/La-Justicia-Federal-confirmo-la-constitucionalidad-del-regimen-de-Dominio-Publico-Pagantes](https://www.palabrasdelderecho.com.ar/articulo/4341/La-Justicia-Federal-confirmo-la-constitucionalidad-del-regimen-de-Dominio-Publico-Pagantes)  
53. Resolución 15850/77 | Buenos Aires Ciudad \- Gobierno de la Ciudad Autónoma de Buenos Aires, [https://buenosaires.gob.ar/gcaba\_historico/economia-creativa/resolucion-1585077](https://buenosaires.gob.ar/gcaba_historico/economia-creativa/resolucion-1585077)  
54. Propiedad intelectual. Ley 11723 \- Ley de Propiedad Intelectual y su reglamentación en Argentina \- BIBLIOTECOLOGÍA Y EDUCACIÓN, [https://bibliotecologiayeducacion.blogspot.com/2019/01/propiedad-intelectual-ley-11723-ley-de.html](https://bibliotecologiayeducacion.blogspot.com/2019/01/propiedad-intelectual-ley-11723-ley-de.html)  
55. OAS \- LEY 11.723 \- REGIMEN LEGAL DE LA PROPIEDAD INTELECTUAL Ver Antecedentes Normativos, [https://www.oas.org/juridico/PDFs/arg\_ley11723.pdf](https://www.oas.org/juridico/PDFs/arg_ley11723.pdf)  
56. ley nacional 11723 1933 \- Boletín Oficial del Gobierno de la Ciudad de Buenos Aires, [https://boletinoficial.buenosaires.gob.ar/normativaba/norma/53623](https://boletinoficial.buenosaires.gob.ar/normativaba/norma/53623)  
57. (Presentacion Derechos de Autor y Vias de publicación) \- CIC Digital, [https://digital.cic.gba.gob.ar/bitstreams/8f1b9224-ff3d-477c-89a7-3e80962dda89/download](https://digital.cic.gba.gob.ar/bitstreams/8f1b9224-ff3d-477c-89a7-3e80962dda89/download)  
58. FONDO NACIONAL DE LAS ARTES \- Resolución 625/2022 \- BOLETIN OFICIAL REPUBLICA ARGENTINA, [https://www.boletinoficial.gob.ar/detalleAviso/primera/270121/20220824](https://www.boletinoficial.gob.ar/detalleAviso/primera/270121/20220824)  
59. TEXTO ACTUALIZADO \- Ley 11723 \- PROPIEDAD INTELECTUAL | Argentina.gob.ar, [https://www.argentina.gob.ar/normativa/nacional/ley-11723-42755/actualizacion](https://www.argentina.gob.ar/normativa/nacional/ley-11723-42755/actualizacion)  
60. Ley 11 \- Jus.gob.ar \- Infoleg, [https://servicios.infoleg.gob.ar/infolegInternet/anexos/40000-44999/42755/texact.htm](https://servicios.infoleg.gob.ar/infolegInternet/anexos/40000-44999/42755/texact.htm)  
61. guia sobre derechos de autor \- Hospital Italiano, [https://www.hospitalitaliano.org.ar/docencia/biblioteca/attachs/guia-sobre%20derechos%20de%20autor.pdf](https://www.hospitalitaliano.org.ar/docencia/biblioteca/attachs/guia-sobre%20derechos%20de%20autor.pdf)  
62. Tesis de Maestría \- FLACSO Argentina, [https://flacso.org.ar/wp-content/uploads/2013/07/Lima-Maria-Clara-C07-08.pdf](https://flacso.org.ar/wp-content/uploads/2013/07/Lima-Maria-Clara-C07-08.pdf)  
63. DERECHOS DE AUTOR SOBRE SOFTWARE Y BASES DE DATOS \- UTN, [https://www.utn.edu.ar/images/Secretarias/SCTYP/UGEPI/dnda/Mod-VIII-Software-y-Bases-de-Datos---F-Andreucci.pdf](https://www.utn.edu.ar/images/Secretarias/SCTYP/UGEPI/dnda/Mod-VIII-Software-y-Bases-de-Datos---F-Andreucci.pdf)  
64. Ministerio de Justicia y Derechos Humanos \- Argentina \- InfoLEG, [https://servicios.infoleg.gob.ar/infolegInternet/anexos/350000-354999/351297/norma.htm](https://servicios.infoleg.gob.ar/infolegInternet/anexos/350000-354999/351297/norma.htm)

[image1]: <data:image/png;base64,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>

[image2]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADoAAAAWCAYAAACR1Y9lAAAArUlEQVR4XmNgGAWjYBSMgiEEWIH4KhCfAmInNLlhATiBOAmIXwKxHhAzokoPD8ANxG+BeB0Qq6PJDXkgCcTfgHgqlD3sgCIQzwfiT0DMhyY3rMAuIH4HxL7oEsMVgAqb40D8AIgjUKWGHwB5FlTCgmIZVOKCSt5hD2D5tgZdYjgDkGdB1c0kdInhCEB1awq64CgYBQMH8oB4FpF4SOdRByAOIRIHQLSMglEwkAAA8+UdUNWP+CMAAAAASUVORK5CYII=>

[image3]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACoAAAAWCAYAAAC2ew6NAAACJ0lEQVR4Xu2VMUgcURCGfyFCJGliRAkIMWIKQYtgFbA0pTY2go1dGisFgxCIEIR0EQmWBi0Sg0KKoAgWLhpIwMLGkFKwEQJiY4oQ1Pz/zXt3797tqnccqfaHD3Zn5u3Om5m3C+TKVbUayH3ygDyOfP9NSuArmYgdgV6QS4eus9RHfqMUKxJy1/lCe+wL16XqD65PwEsxN4n7CHvmz8iuzrxzvs7IJ8mWZi9U8xfqn+go7JkXsYMag/lGIrs0RG7FRu3uNVlG/RNVVY5R2UYlsebsqnqYlPJ5H9wXpErukGdkCbUleoccBfT6ICdf1f7Apip+IMPOpwp6DZDPwX1B02QOtotaE1U1tO6cfCLNPsipm5yQ2cC2CEv2EWyt5lU5SPPkubsuape0uutaE20ke6Sj6C3XbbIOi7nnbHqvxsJvUoetjbSQbdjmClL2Uy7Qq9pE35BVVM5fmnpQerberXHw8jOsSqrq44EPT8gheQj7eAv/Uh0sza5vRZr0QsWqSmp5V7m7QvpGKj6BxRYrhtKh+kG2YN/SojSw4QEQeqEWnJEV2CHJkhJ9CeuINvaFNJVFVErPPiWTsHHw0jzK95dswDZ1pRJU1/rB4F5/EyWhTmXpO0rJhvIzLN/TyFcmBar1B7Dgt8huvXbbThbIK9g6rf8GW7uP7LUzsJjNyC5pLuXzhy1VqoyCQhKkt8DPZki8PkH6WlVLv+m0jumwxZXOlStXvfQPrjCW7BJPTawAAAAASUVORK5CYII=>

[image4]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEMAAAAWCAYAAACbiSE3AAAArklEQVR4Xu3WIQoCURSF4WcwiGA3mmyuQeyCwWAWsytwFYLF4g5cgBsQ7HarQYPB7H9RkLlpisVzfvhgmMuUYXh3SnHOOffTmjjjhFGaydTCHFcM0KiOdWrjhj36aSZRF09sPteS9bDDA500k+uAO8Z5oFwckEdcMKuONIsXEpsjvpbYJLFRXPmeI6s8UC9eSKzadR6oFv8ei3zTOY2W2Nb092fGENOaJu9HnFPpBWpbHVA8DqlSAAAAAElFTkSuQmCC>

[image5]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEMAAAAWCAYAAACbiSE3AAAAnElEQVR4Xu3WIQ7CQBRF0U8IAs0icFgSJAkaZHUdgiWxDhIUDonCV5EgEOygdzIg+hUC995Jruh81Uln0ggzM7N/2dAsL6ra05OOeaCuoY6uNBqONI1pSzdaf57lLaN+IR1NhiNtc7rQO63LKXfHiR7UppmMsgnfo1IuVrmjUl54R3dahfDlOY36v3GmRZrJKJtwoFfUi9LMzOx3PecnEZAPXeemAAAAAElFTkSuQmCC>

[image6]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACUAAAAWCAYAAABHcFUAAAAAkklEQVR4XmNgGAWjYBQMLGABYmF0wYEGIEe9BOK5QKyIJjcoACsQPwDi40Bsjio1sIAZiP2B+DoQO0H5gwYwMkBC7AEQR6BKDR5wAIg/MgySaAWFmB4QPwPiJCDmRJWmPwCFCiwKQZlgwEAAEF9lgDhmwBM5KFpA0bOLARJdoGgbUJALxG+BeB26xCgYBaOABgAAvoERqthrBMsAAAAASUVORK5CYII=>

[image7]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAE8AAAAWCAYAAACBtcG5AAAAtklEQVR4Xu3WoQrCUBSH8RkMItiNJpvPIHbBYFgWs0/gUwgWy95gD+ALCHa71bAFg9n/QQ07ODyKsPL94IOxw8pl3HuTBACAP2qrkzqqiZuhRkct1EWNVKs6Rp2uKlSuhm6GN/rqprbPZwQMVKauqudm+GCvSjX1A8TZgXBQZ5VWR4iwBbST1f5GO2ntxMUPXvvg2g/wHVtAu7ps/AAxdvdb+pcAmrBSu2Dsec5YzYPNHp8AaMYdXZkdUODtT3MAAAAASUVORK5CYII=>

[image8]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAWCAYAAACCAs+RAAACtElEQVR4Xu2WTchNURSGX6GI/BYpP5GJkCIGolD+BpSBIjJEBlKK+jKhTJQJJv4GRiIlSQyUExNSpJSSCRMDmSgK+Xnfb+39nXXWPefk1jc5+p56O2evve++a9219toXGGGE/55Z1GxqRZzoh/vUEdhG2tBrJjWWGkftp5alz4hR1GFqurNlTlKXqDOwdW3I+T9JRXWqP76i3ChKziiQibAv8XM/qPGosp36iarzv6ijblyHgpEfRbD3xQtqKXqz8ZKakdbkQM7CfuWt1OQ0l9H4KfUx2J9Tb2DZbWJYAjkdDWQxtcqNcyDbnC2yBpaNJ8F+C5ZBZauJYQnEo5I4Rt0Idh/IDmoPeg/mOdTX+bVk17OJGMhB6oOTxhmdZZWr9vxGnXdzQygLb6klwZ4DeUQNwA75e2qRW5MdLpytze6JgaxPY5X3IZTfMw/mn4IRq6ln6b3CXeo6NSbYcyBXnG0hrPbzpk0ON9k9MZBNsEz4pqH3C7C9vM2PB5HzStXKONGCNtFhnopmh5vsnhzIY1jZ3IR1S4+ugHvodTyOMR/WcRbECTKJ2pyeHm2iElOXU9Ooc7ifM6J18kHPvZUVhhqGAs0VozvsUzltaFF2KnIbtrlKz+MDUQNoC6SuO2ZyIOp4a6nvqHGQzKFew9Zdpd5RGyorYF9YwM5DROdGl6MPUuvkYO4ouhwfUJ+HVhg6R/96j+Ss6U5SmcWL9HiyNzKBeojmQNTNXlHL01iHbDfsM/5i3AhLfT6kev5GtX1GdBZ2obyDpiTbKVhw+1D+COtg+/vWfDHNDaKa08UlxxRURA7tpL7AMlPAHJzm1gitU7u8A3PuMnUCvQfXo2wos1nKij8zkt7FXFhz8eulTnEAljX9g+g0akaqgrpu1inUTFR2KjNfqlvce+cYDftn3tYJu8dfnoC83OmvhHAAAAAASUVORK5CYII=>

[image9]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAE0AAAAWCAYAAACFQBGEAAADgklEQVR4Xu1YTchMYRR+vlBEIfJT9CEWYiNFipQQC5KIYmWBsKLYWIxkYyX5CyUrvyVJSRYTJbEQ0Sc/hcJCkgX5yc955rxn5tx37p25M/qmmZqnnu7c8577/jz3fc85d4Auuuiii7bCKOF44bS4oVksFS6PjQ4c7KTwmHBt1NYIhkD7YF8bozaPfVCfg8KeqM2wWHgY6kdB6uGvY9N4A+3gWbjuSTaXsET4SDg13A8S7hd+Fs4xpxwYJrwpfO5sw4V9wgnOtlL4C0mhfgt3ufuBwuNIjn8GOqdZzpYGrvG/RPPIEu2y8Itwk7ONhfqfgy4gD9ZDnzkQ2WnbEX5TxHvCD5XmEh5AxeW4xDzhDyTHng8V+4izpaElonERbHsf2Wl7hcpC6oHi85nVkZ0LLUJ3oi38jndA5VnuQoLCxwufAhX7dWSP0RLReBwpzGBn4xumP3fASGevhU/QZ1ZE9q+Bs6HxiT5F7yA4G+y8jhY+DfcejLkWbmrBizZUeF74NvCxOQkGCN9Bfcntrq2MLNHSMBnqX4jstUBhskQzu4lT9A6RPUucLHsMLxpffgEaMy8I1wU74+lu4WaoeExef0JbAnlFY4dMBLegMSgv2lE0S2qTyq2KmdCk4kPPN/e7jDyi9UKz3+24IQfaTbRL4ZpW9rCU4c5iKDBwnlXIIxrF4mCN7DADg3SWaBbTLMAXvQOSMY0xlLE0FqdR0dgHj2Wc4Agrj5ilDan91hONQp2Gnm9iDDSI8poHttA00Sgos98G1BaNojIOWTb1MNGYfWvBRGM/PJr8bWvyoB+PKMe+Cq0Dq5AlGs99AbrL+PnBgnKN8Ch08lYrsZT4iWSh6rEMuuWZIT04rtVpnPwNaKb16Au0GGMxx2d067/ROm0uqotivoAX7j4B+xbrhXZ0Mfwe4XyYRdiWRl+o2m7Y4mweFOQaksdhnPAJkruVXyA8Nj3hnleKsbXsobZDwgXOdl34UTjD2WLwhfITjvPkuik6XwTvH0JDBPum/Qp0rlaOMPGVUES1ECQFMNixSqMXaBU0wxScLQYnfVe4E5rOXwqnJzx00tugR4JfEaeEe6E73oPh4jv0xfFYU7BFCY9qxPNnqLCdZ2Q8Y5nBOcT+/Qar2jsVVjwz9LQEE6GFbyeDO60ovB/Z+wU8PidiY4diITQ5+A2Q56+nLgK485g0umgG/wB+ehoo5QhIngAAAABJRU5ErkJggg==>

[image10]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAE0AAAAWCAYAAACFQBGEAAADsElEQVR4Xu2YXaiOWRTHl1BExlc+ihiZCyFJEZHSKC5oGooiKcX4uKK4UY7kxoUkIZLmCqFJk5JcvKUQF0qY8lEoXExDTdEgw/qdtdf7rmef5z3nvOdEnN5//XuevfY6e6+9nrXWXu8RaaKJJpr4pjBMOVr5Uz7RKAYp1ymPpScLd4QJysW5sJPorzwstt/qbC5it5jOPmWvbM7xs/KgmF5n7P4U2GXcV85J7xiG0/5XLqtqtAUHZdMl+UQHGKi8rHwQZD8o/1KOCbKlyg9SdNRH5bYw7qM8opwZZCeVr5XTg6wMO6SbTnuv3KXsm8ZEwSXlDbED5Zgodsh/pHGnrRQzdm8mR7YlvbMne7+sTbfilti+I9N4tvKdmPMcc8WcfSjIytAtp/UW+2M2xwgHi/6rnBZkjjPK7cqKNO60c2L7/ZrJOWhFLBL94FejgtT+ligEOD4/OCUDZz/J5Dm65TQwQszYCAxic4yIIMomi+lXpHGnEZ1laf0mcYZYfUKnEhUUvyc5z+HKe2kcQYF/WiLPEZ02QHla+SzxjiuJBdVzMV24OcwV4OlxSoqhT/qeSO9ddRqOqec0l7tzKlEhk9dzTj15jug0ztgiVjPJohVJTj0lo9aLOY+yRa0vhRf5cZmcxc6n957kNIJhj3J8ddYwRexS8RoK3ob3Kq4oHysnZfKjUry5eorTzqZnWdtDK0NkUQoc2FkAuc0NFa9+xyPlcrE5DKO2XVeuSePBNdV2QZ2s5zSvaV7gK1FBijVtiJituXMadRprkJYvitOt8PYoXpCFdQlRmsOhQcZ4anr3IulkExZ4lcb7k15H8IOWOc0vnlXSvtNwKnXIb9MIdxq3b3twp7EOqck7NSsHeqQoe18Q6wNbsVbsJuJJHhNR8JrYFy2DG5cfnlaCvq8sWsEisZDnhozAaO/TvE/kpo2gR4t9mtecflWN2vqN9mmzpG1TzBkfhnEBhCcL5ORLxtvTwWL0UmyyMY09PT0aNqRxDhzypxTTYZTyrljr41goZlevNOaJM36rapjsgHJekF1U/i3WFtUDH5SfcNiJ7TidD8H4tliJYG3kf4jZ6hlGzW91Su4sZx4NjlwP4izwi9gN05LGZcBoonir2HVOvcwvHozeJJYS/Io4rtwptV8tDtqj/8RSlrTGYQsKGm2R2062eOQ5qWe0GdiQ638xeNf+vcKbZ7Ltq2Cs8sdc+J3BW6qbmfyLgPShp+sJmC9Wt2MAdOZfT00kEHlcGk10BZ8BWjAdfzHQB4YAAAAASUVORK5CYII=>

[image11]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGAAAAAWCAYAAAA/45nkAAAAoklEQVR4Xu3XoQrCUBSH8SNiWPYhbKuCcWDWaLYZ9kg+h2CyGU32JcFg8A38H66Gnb4j6PeDL2ynXdg9zAwAAOCfLdU0vkSenbqrfRwg30Z16qxG/RGyjNVKXVTzfsYXzK18CZ2a9EfINlMn9QzvkcB3wUHd1DbMMCA/+M815MuZayiBH/JaXdXCWMCpKiv/A0dVhxkG5AffqoeVZQsAAPDbXredEZBxn5KyAAAAAElFTkSuQmCC>

[image12]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAC4AAAAVCAYAAAA5BNxZAAACMUlEQVR4Xu2WTahNcRTFtyRElOSjXikzI0qRiUwkAwZMjCkfZUQvU5IylAyVUkoZSkkGd2RiYkD0UiJRCikU8rF+9t7n7XM65ym9noG7anXvXnefc9Z///d/n2s2xhj/BzaJz8Sr4mHxqfhdnF+ThHXiQ3GPuEu8ZX7tPwNmLolLIt4mfhH3NRlmS8U74lTRlouPxYmizSl+iD/F0xGvFZ+L18R5oR2InHMRJ9COd7Q5w0Zxp7g44s3iJ2sbemRukjapIO+1uL6j92GVeVHYvVkHFT5rbpJWSLwNrc84ZLFDoCCT4kfxRXzm2WAxb8zvPRLPi+8jvmDevsvEr6HxuYALEyRcF9+JD6zf4JDxPj1x0vz3y0W7aG4yK587TF7F59COFo3n3C1xAyp+yLzv1xR9yOCQnuDw8vveorEDVDGRxtnVir425DmjErfAjW+LN+M7GDI4pCe+2Z9bKY0zECpS41wkWsafmE+L2junzA0djPh+xF2DfVWpYOdm0zi5I76sNDfUvTkLQd8d8ZWIjzQZDrSRDU+Kl+Y59Z0AVth0of7KOPggbm9+MttqftH+otEytM6ronEGeHkxGYbAZOIwYT7bDsNMLs4Tb2d2kZZimnCvReLq0Jg4W8SFoR0zf+ZvTJqfYNrhhvnIYTH58knwhrwnnrDpvwYbWhn94CAySXIcYiarTTVZVJIKs5A8O8kdHa0BlaEtzphXvzUrC6gQOXCmvu0DJiEVHWOMMWbALxt0mp8tV5StAAAAAElFTkSuQmCC>

[image13]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAC4AAAAVCAYAAAA5BNxZAAACIElEQVR4Xu2VP0hXURzFT1hoJAUGaRQIbW1GYEQgbS7aEARtbpnQVKhzRNAotgbhEM0iLRHyJgdrLARpKImCQFrKoUg75/e931/fd31PIeS39A58+P3uufe9e979CzRq9H9ogewkPpHf6f9AbEQNkrdknIySF2So1KLDisGdx6UWQC95SdaDd4KskbPB66gU/Bm5T26Q/nJ1SzdhH/Qw8+XdybyOScFnczPTO1hILZOoH+QLOZf5VTpFTsNm70DkwY+RCTJCjpRaAJuoDy4uZn7UUTJNvpON9Ot7Qx/zFfbugjwi31J5DpbpOPmZPP22syn4MulL5ZNklVzwBrBwdcGrfNc9WP2T4M3DQvrI66P9PVFbybsdPPXzyguXYCdGlNbyc3I4lesC1vkubV7VXwueZkCj6PLgmtWoqmWofopQ3qVJlB+qC1jnu35h/6XkwT9mvnvaF65ScHWs81nrzaUGscPXqA5YNSpR2zjY4Gpb6M8hWKAP5Mzf+tZmjd5TWDvNRJS8AvUnhS40tbme+dpPvsn+Kbj0hoy1q4ArsIfiute6XCKfg6ebNZ+pXLqktJkUXu+QFPgBbNC6YLOoJaXTRO/qgd0l8nTiDJPu5E3B+mzpMmy6C7IIu/JXvDJIN6T8u+QWeU/Ol1pUSxtRJ4kfhwrjo63R1Ec5GmFfptG/mnlt6bzUAzOwMBqJKsnXDSv2WrdVUkihEW3UqNEe+gP1SJozid8ZAwAAAABJRU5ErkJggg==>

[image14]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAmwAAAA6CAYAAAAN3QXmAAANv0lEQVR4Xu2beci22RjArwlFjD07831kl1HKLi9Zs5WlIUIJYcgWYeKbP6Zs2bKUMCE0DClrk7gt2bOULVFIFA2ZIkOW+9e5r3mu9zz3/XzP+33vO/ON9/er03uecy/nnGs7133u+40QERERERERERERERERERERERERERERERERERERERERERERERERERERERERERERERERERERERERERERkcrVx3LTvrHjKmN5+VjeM5Yjuw8del4/ll+O5eb9gQPkSDRd3LFrP6xcLZoNL+ngWmO5e994CnLdsdxiLFftD8gaNx7LXfpGOVBOi+Znd+sPFDjn3Ghrxl7h3tfvGw8Y/I34TRyXQ8Ywlg9GM7xHj+W/0QzihVP9VITF7i99Y+FNY/lq+X0k2lxwzP3kGlNJrhmtn1N9ob089YrM/z2Wu06/CW70jw73m51S329dMI8fjOWb0QI7Dw2viTa3E4F7/Cqaz81xZUnYGCdy5u/xuGffcMg4My5f37sygp99OpqcrtMde9LU/qiu/Xi8aywfmOqpg7RX4tCl0ZLpL0zH9sJ9Y95PM15QDiLWwV7HKv8H/LDUMbxqBBeW+qnG0DdMPGYs/+kbR546lr/1jSfJ/WJ9pw/HP9U5WUe/c7TgyY4ZwXATJNZnd233j3kdnSw/7X7vty6GqVR+F+sLy7YMceVP2GDbhC0XzcNKJreyGezks2N5R9d+QZzYQxg+Vm2vxgWODeX3jUp9Gzb56TCVg0JbOoR8qtT7hO0lpc6OwoksgCzudasZh6i/X1nqlRvE5ieToW+Y+NdYzusboz3x7MXA+3FCJmfZTjLSJ2w9zGGvQaDvZ7/Zixx6+qRoE7eO1tfc6zLaH9w3LjBnC7yKwyZPn37fZyy/WR1eBBs+UbkOU6nQZ/ULAjhjm6O3lSGu2IQNOWyyzZtF26k8Hn3Cxjx7feHnSwnbNsneicAY5myn18OJwn36XXvmsjSfuYSN8S3Zy0GwH/1tspn9ADvBp6qs+H10auv9YpPPQZ+wVTYdA/S7ad3b5KfDVJY4WV30tiSHjD5hS34ylrdOdZKU/A7pKdHO55XqJdESvC+O5eHRXhVy7F5jeWm0XYh8jfm8WH3P9OGxPGEsj52OEVx5zYSjPDLa/YBvEP40teO4Szs09Lm0CObcnlnqfEPE09ww/Z4bJ+Scz4kWqBkvyeGzoo0fdqKNPR2Y++SCx+sv7k1/XxvLt8byiGiLZk1q/jD9zX56cHLal8o2yUivY2T+z2jXvjfWjycfjfUFahOviOV7ITt0j1x/Eaugma89ctGbs4WUEeMlYeI1K30hb3TBPXdity7uMZYPTXVez+a90oa/HOu66BmmkuRrV2CMzIm/3CfrgA1lcnruWB4/1YdYttVNC0HlJmP57VQnmcYPezj+vWj95niZP0kuY6QNG8g6vovsWKguipWMGBOyfn/sTo6rvqhzH+6deko/5zMF9JPf/KRc6A95AT5B398dy/ui+Q1tl47lndF0g41gr5v4cax8gXsAc5zTw6Z48OzpWJXdu6NdD4x1p9SZC5+UZAyp9AkbdR5oM1YiN+rENl6370S75wumdr5bItYu2edONDl9IlqcuEPsfqsw199OrF/Db14PArEKeUPGxmozPSRzfUyqJX1iEx+Y/jKOhLgE9Jt+scnnkOG1o43xH7G6Z+oxbfn1Y/l5NLtMO0+wz7OmOmPJnXT86SFT/Sux7KfDVOaY0wX1N0YbOzHq1dH0h//whojzqu7rWOUQspSw4WgYEzwudhsh59en8Ho9xpbGfHG0V4hJnsdCWyE5xHGSPI+/1yvtdcGocN7SIljHVut1W3xunOzk8OovnSplwfyQTYVx5ZxZpBJ2mzIA0d+vV4cuuw/9VOfNfvaTN8cqcUkY19GpztjRwRzn9w3HYZuEDTgvAypwDQH1hrFuCykjdn9gZ/rLuHubqLpIuSYkE/cux1ioYU6nyRCr4E7ZKcdIwPGNhHHThlz/XtohZTLEsq1uk7AxH+6VQRw5vn11+DKQbS54wAKQCRJwDdeSGKUMh6ktSZ1AL+t67IEL7b2Oe7mcF+2BALg3Y6nwEMjrZ7hLbN71ABY9rgHmC3l9UmW3FA+gHsMm6+8zo+kBGV5Y2okjPX3CVuMMiVHawrFYyQJfRDZA8nw8hljXW7LU3xDr16Sv0J4PNzU2ps0cBGknJIrYCZw9/WVs6RdLPkdiVeNGb3vVLpd03ev5OdHuwZh4mEiIFUt+OkxljiVdQNplHQPjTXtOSCiJ53JIWUrYeGKk/VVjeVrMG3jy+7HccqpXx6f+sNj9tAW903MefdTz+kAHdcGosBBlgKvk4pbUenXapXHydMsxSj5pzS3ujAs5sqDUIMF52WcfJOp9WKj6fvaL247lr2O5U2ljMauyIIkiOM3BNyVVLlU+c2z7SnQpoCKn3hagyujzU1ufREDVxZz9HJvq2R/M6TQZpjIH96hBl/ql0Xad6y4H5FiGOLmEDT3lLhaQkGRyUkG21c+okyxVudIfuxG5c81OSn31VWXUy7oe+1g0X2FHs7b3Ou7lwnFiB6TeevJ+3+kPzMDuCbtTXJO7cb0N8PuMUk96/+yP9foExvzxWLfVSh/HnhxtjE+M9s8saQt11+XFsdLJsenvJoZYX/yTpf6GUod6DXoZSnuNjanb/SbthPmz00eiQh0YQ30Im/M5/lb76W2v2uWSrmmnXvXJ68sh1uP6nK3CMJUKiRp9L+kCmDO+TbwlcWf+x8rx5AHR4vmt+gNyOFhK2GjL3R4C7RC7n3gq34i2aPQLEYtJfWpmpwRyIcnzeUKij6S+qtlmh63+08FZ0f6pguvZUq6vKfogPEz1uXHWwPSgWDlsLu41QNfF5rPTX0AmOa4+SOR96IfxQ+2n8rpoT1ZL5TarUxfp515lScLLk90cBJa9gszTVtid4tXF/WP3f1cuBVQS1t4WTo+VjIAADTWJGKa/VRfcM20OLo5VwlgD+PESNp6G52AnpCa61Gmrek9S/kOs+0myTcKGzHq5zcE5NWE7GuvnshixU/7QaGPK14lJldGmhC1fpWU7tnQkduuYv71c2LFJf6l6q7CAnR+rV3SbGEr9wmj3Q+eVahO9Twzldz3GTuyc7I7FaicKTiv1pCZs/fx5bUu/vJ4FEvE3RHvY+dlYnh7bJUhDzCdfS/3BUOpQ51cTtj42zn1/9bZYj0m13GB16iK9TX+m+522seRz6Ohkd9iIPb2eeQjgXiezw5b+MaeL1D0PusTNj0R72ETun5uOVfrxySGB4ILh5fdDZ8TuxY22I1OdRff7Y/l6tGvy/PyeAeOi8J6fAJaLXzoAiR/9ZXC+d7R7XzD95j6cx/cHQD/w+LF8aarnjh/GPMc50ZwqnZRx8JRdgygLfTotOwIs1OwozI2T80j+gL/5/QLJJb9JBgmsXP/HWC14JCU5D5wy5fqyaN/YELyYA0kSyQMJaSYytZ/9pjo688xE9tzuWA9z2mZ3o4IMkf3zp9+fjNZH2gtgA8NUv0+0YEZSRn+9LaALZJS65PsUQG8ZBAl0vS6wn3wFy+KF/CFtmG9Gqi7q+IDzuAZZzSV0nM+46IuxcZ+8B/bHzgQci/ZNJ7r/QTRbqL6WbJOw8Q1MLgB8H1Z32y6JVVL16Wiv9OuO2edj9R0Wtob8sGFkge/yoFP7x6fztfG3o8kaeVX5MW/sHNAj8uDb06PRdMwxZJIJV8old/a4PvXGOX1CgK1uss8K57HAArLhWubY6yFZigcZa3rZpS1hK5wD2DlyAM4BriUOMbfbT7+RGfejTjtj+/NYnhGrnRTm/7Opzq5Mn2zOgU1ho2lTOXb+LvW36Rrk8ZZodsp5NYlJm9lv6BfZ81CHL2F32A/1tLWnx+qfSZZ8LmNExmFsD3vKe6AL5vvcaPOjPY+lPt8dKz1jz2dMde5Xbeu1sRwv6r0vipX85nRxbDqWx3N9Y17ErJ68l8gaGE8G0D6QVnCMXExxaJwvDQ8IHP31cwsgDkmppNNmYN8E5xD0eEplsZp7Os054Wz9GOo4cz6c0+88cF7fVqHfOv9NZICZ62c/6R2dfumTxac+PS5BIOIeWbYBGeR3L7ec2vrds9QB59YkptpCygjd9DZAe9/WQx99cN1P5uwWUq/bsk3CBsyFOSPb87pjx6Mf04ui/RMNcF8WG17HJPTD3PrrKr0vVT1S730/dbktJOMJuz1PmCm0I79+LLBp7JviQc/SOXvxd6j2mEkAYOc1ZtX6Y2N9zhQS7uOx1N9emJv3FcmSzzHOfEOyF51UMjbmGpBUX9iL/VY26aLOZ+n+28ZekUXe0f0+P+afDi5P6H8vycX/O0tyYLemvko4CG4YK12QZMg82yZsCTt/vEo5GdhNrgsbuyj1ldMVxY+iJfckj3MPXiKHkaU4LrJnTrWnMFlxu2jOzispOTXZa8K2n7ArcZA7vCJy4hC3id/56lZEREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREREDg3/AxN4Jr/prk9IAAAAAElFTkSuQmCC>