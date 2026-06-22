# **Systems Engineering and Product Design Specification for an Argentine Driving License Application**

## **Comprehensive Functional Domain and Regional Configurations**

The driving licensing landscape in Argentina is characterized by regulatory fragmentation due to its federal system, where municipal and provincial jurisdictions maintain autonomy over their specific examination parameters1. To develop a reliable study application, the software architecture must utilize a parameterized database design rather than hardcoded logical boundaries1. This strategy ensures that changes in municipal ordinances do not require structural updates to the core application code.  
A key factor in this functional design is the national standardization of specific educational materials. The Agencia Nacional de Seguridad Vial (ANSV) requires all first-time license applicants to complete the *Curso Nacional de Seguridad Vial*3. This program consists of ten core modules that cover basic traffic safety rules and include mandatory instruction on the *Estrellas Amarillas* traffic memorial program and gender perspective in driving5. The study database must integrate these standardized modules while adapting the final mock exams to match the specific rules of each local jurisdiction1.  
The following tables define the regional parameters across major Argentine jurisdictions, the structure of the national traffic safety curriculum, and critical regulatory variables that populate the database.

### **Unified Regional Exam Parameter Matrix**

| Jurisdiction | Total Questions (Q) | Time Limit (Tmax​) | Passing Score | Minimum Correct Answers (Cmin​) | Maximum Allowed Errors (Emax​) | Reattempt Cooling Period and Rules |
| :---- | :---- | :---- | :---- | :---- | :---- | :---- |
| **CABA (Ciudad Autónoma)** \[cite: 1, 8\] | 40 | 45 minutes | 85% | 34 | 6 | 5 business days; maximum of 3 attempts before restarting administrative files1. |
| **Provincia de Buenos Aires** \[cite: 1, 9\] | 40 | 30 minutes | 75% | 30 | 10 | Municipal variations apply; typically 30 days on failure1. |
| **Córdoba** \[cite: 1\] | 30 | 30 minutes | 85% | 26 | 4 | Municipal control; 15 to 30 days reattempt delay1. |
| **Mendoza** \[cite: 1, 11\] | 30 | 30 minutes | 85% | 26 | 4 | Provincial guideline; 50 questions per class if multiple licenses are requested1. |
| **Santa Fe** \[cite: 1\] | 20 | 30 minutes | 90% | 18 | 2 | Provincial guideline; longest reattempt cooling phase1. |
| **Tucumán** \[cite: 1\] | 30 | 30 minutes | 70% | 21 | 9 | Municipal guideline; lowest passing threshold1. |
| **Neuquén** \[cite: 1\] | 30 | 30 minutes | 85% | 26 | 4 | Standardized municipal guidelines apply1. |

### **Mandatory National Traffic Course Curriculum Modules**

| Module Number | Curriculum Topic | Educational Focus | Primary Target Classes |
| :---- | :---- | :---- | :---- |
| **Module 1** \[cite: 2\] | Civic Ethics and Education2 | Social coexistence, traffic principles, and responsibility2. | All classes (A, B, C, D, E, F, G)2. |
| **Module 2** \[cite: 2\] | National License Classifications2 | Categorization definitions and operational limits2. | All classes2. |
| **Module 3** \[cite: 2\] | The Vehicle and Safety Systems2 | Active and passive safety, mechanical checks, and tires2. | All classes2. |
| **Module 4** \[cite: 2\] | Principles of Safe Driving2 | Hazard perception, braking distance calculations, and overtaking2. | All classes2. |
| **Module 5** \[cite: 2\] | Speed Rules2 | Speed limits in urban, rural, highway, and school zones2. | All classes2. |
| **Module 6** \[cite: 2\] | Signaling2 | Vertical, horizontal, luminous, and manual traffic indicators2. | All classes2. |
| **Module 7** \[cite: 2\] | Driver Fitness and Conditions2 | Fatigue, distraction, and the effects of blood alcohol2. | All classes2. |
| **Module 8** \[cite: 2\] | Driver Responsibilities2 | Insurance, vehicle inspections (VTV), and civil liability8. | All classes2. |
| **Module 9** \[cite: 5\] | Gender Perspective in Driving5 | Reflection on driving stereotypes and promoting inclusive road safety5. | Mandatory for all classes5. |
| **Module 10** \[cite: 5\] | Estrellas Amarillas Program5 | Awareness of traffic accident victims and prevention5. | Mandatory for all classes5. |

## **Product Feature Requirements and Analytical Use Cases**

Designing the driving license application requires clear separation of the study and simulator modules to balance learning support with realistic test conditions8. While Study Mode is designed to reduce cognitive load by showing questions one at a time with instant explanations, the Exam Simulator enforces strict, realistic testing rules8.  
The application's core feature requirements are specified through the following structured software engineering use cases:

### **Use Case 1: Run Exam Simulation**

| Use Case Attribute | Specification |
| :---- | :---- |
| **Primary Actor** | Registered License Applicant. |
| **Preconditions** | The user has selected their target jurisdiction (e.g., CABA) and primary licensing class (e.g., B1)8. |
| **Trigger** | The user taps "Start Exam Simulation"15. |
| **Basic Flow** | 1\. The system queries the jurisdictions table to load parameters: ![][image1], ![][image2], and ![][image3]1. 2\. The system executes the Random Exam Generation Algorithm to select questions8. 3\. The interface displays the first question, showing only the text, three multiple-choice options, and any associated images8. 4\. The system starts a countdown timer set to ![][image2]8. 5\. The user selects an option or skips the question8. 6\. The system saves the response to local storage and displays the next question without revealing correctness8. 7\. The user taps "Submit Exam" or the timer ![][image2] expires8. 8\. The system executes the Scoring Algorithm to calculate the final result8. 9\. The application displays the summary screen, showing the pass/fail result, score, and elapsed time16. |
| **Alternative Flows** | **Alteration A: App Interruption / Crash** If the app is closed, the system immediately saves the remaining time and selected answers to SQLite. Upon restart, the user can resume the exam with their progress preserved. **Alteration B: Timer Expiration** When the timer hits zero, the system automatically stops input, marks unanswered questions as incorrect, and displays the results screen. |
| **Postconditions** | The system writes the performance data to the exam\_attempts and user\_responses tables, then triggers an analytics update18. |

### **Use Case 2: Daily Streak Maintenance and Daily Practice**

| Use Case Attribute | Specification |
| :---- | :---- |
| **Primary Actor** | App User. |
| **Preconditions** | The user has an active internet connection or local storage initialization, and streak tracking is enabled. |
| **Trigger** | The user opens the application. |
| **Basic Flow** | 1\. The system compares the current date with the user's last\_active\_date stored in the users table. 2\. If the current date is exactly one day after last\_active\_date, the system preserves the streak and increments the total count. 3\. If the user has not completed their daily study goal, the application sends a push notification with personalized recommendations based on their weakest categories20. 4\. The user completes a quick 10-question practice run in their lowest-performing category8. 5\. The system marks the day's goal as complete, updates last\_active\_date to the current timestamp, and increments the streak counter. |
| **Alternative Flows** | **Alteration A: Streak Break** If the current date is more than 24 hours past the last active window, the system resets current\_streak to zero but keeps longest\_streak unchanged in the database. |
| **Postconditions** | The updated streak metrics and daily task completion states are saved to the local SQLite database. |

### **Use Case 3: Wrong-Answer Queue Review**

| Use Case Attribute | Specification |
| :---- | :---- |
| **Primary Actor** | App User. |
| **Preconditions** | The user has recorded incorrect answers in either Study Mode or the Exam Simulator21. |
| **Trigger** | The user opens the "Wrong-Answer Review" section from the dashboard. |
| **Basic Flow** | 1\. The system queries the user\_responses table to identify questions with a status of is\_correct \= 018. 2\. The system filters these questions to exclude any that have been answered correctly in their last three consecutive reviews. 3\. The questions are prioritized based on safety-critical flags and cumulative error counts8. 4\. The user begins reviewing the queue, with questions displayed in a single-card format8. 5\. The user selects an option for a question8. 6\. The system updates the question's history in the database, decrements its priority in the queue if answered correctly, and displays the explanatory text16. |
| **Alternative Flows** | **Alteration A: Queue is Empty** If the user has zero incorrect answers, the system displays a screen celebrating their progress and suggests taking a full mock exam8. |
| **Postconditions** | The system updates the question states in the database, adjusting their priority for future review sessions. |

## **System State Transition Models**

To ensure consistent progress tracking and maintain data integrity during unexpected app exits or crashes, the application uses precise state transition rules24. These models govern the lifecycle of both individual study questions and active mock exam sessions.

### **Question Learning States**

The learning engine tracks the user's progress on each question using five distinct states. Transitions between these states are managed by database triggers in the SQLite layer, which monitor study activity and responses21.

| Starting State | Event / Trigger Conditions | Transition Criteria | Destination State |
| :---- | :---- | :---- | :---- |
| **New** | Question is loaded into the local database but has no entries in user\_responses. | The user views and answers the question for the first time18. | **Learning** |
| **Learning** | The question has been answered once21. | The user answers incorrectly (![][image4]) in a review session21. | **Re-Study** |
| **Learning** | The user reviews the card and gets it right21. | The user answers correctly (![][image5])21. | **Review** |
| **Review** | The question is in the regular spaced repetition cycle21. | The user answers incorrectly (![][image4]), resetting the intervals21. | **Re-Study** |
| **Review** | The user answers correctly over multiple review sessions21. | The consecutive correct count reaches five (![][image6]). | **Mastered** |
| **Mastered** | The question is temporarily removed from the active study pool. | The user answers incorrectly (![][image4]) during a random cumulative exam21. | **Re-Study** |
| **Re-Study** | The question has been missed and returned to active study21. | The user answers correctly (![][image5]), resetting the interval tracking21. | **Review** |

### **Exam Session States**

Mock exams follow a strict state flow to prevent users from altering their scores by closing the app or evading difficult questions25.

| Starting State | Event / Trigger Conditions | System Actions | Destination State |
| :---- | :---- | :---- | :---- |
| **Initialized** | The user taps "Start Exam Simulation"15. | The system queries parameters, runs the generator, and prepares the quiz session. | **Active** |
| **Active** | The exam interface loads and displays questions8. | The countdown timer starts, and answers are written to storage in real time8. | **Active** |
| **Active** | The user taps the "Submit Exam" button8. | The system stops the timer, freezes inputs, and writes the completion timestamp8. | **Completed** |
| **Active** | The countdown timer reaches zero (![][image7])8. | The system automatically stops the exam and marks unanswered questions as incorrect. | **Auto-Stopped** |
| **Completed** | The exam is submitted by the user. | The system runs the Scoring Algorithm to calculate the final score8. | **Evaluated** |
| **Auto-Stopped** | The exam is stopped by the system timer. | The system runs the Scoring Algorithm to calculate the final score8. | **Evaluated** |

## **Relational Schema and Persistence Architecture**

The application uses an offline-first data model to ensure study features, progress tracking, and exam simulations remain fully functional without an active network connection8. This architecture relies on an SQLite database to store user progress, track errors, and manage spaced repetition schedules locally on the device15.

SQL  
\-- Enforce referential integrity in SQLite  
PRAGMA foreign\_keys \= ON;

\-- Table: jurisdictions  
CREATE TABLE jurisdictions (  
    id INTEGER PRIMARY KEY AUTOINCREMENT,  
    name TEXT NOT NULL UNIQUE,  
    total\_questions INTEGER NOT NULL CHECK(total\_questions \> 0),  
    time\_limit\_secs INTEGER NOT NULL CHECK(time\_limit\_secs \> 0),  
    passing\_threshold REAL NOT NULL CHECK(passing\_threshold BETWEEN 0.50 AND 0.95),  
    max\_attempts INTEGER NOT NULL DEFAULT 3,  
    cooldown\_days INTEGER NOT NULL DEFAULT 5  
);

\-- Table: categories  
CREATE TABLE categories (  
    id INTEGER PRIMARY KEY AUTOINCREMENT,  
    code TEXT NOT NULL UNIQUE,  
    title\_es TEXT NOT NULL,  
    description\_es TEXT  
);

\-- Table: questions  
CREATE TABLE questions (  
    id INTEGER PRIMARY KEY AUTOINCREMENT,  
    category\_id INTEGER NOT NULL,  
    target\_classes TEXT NOT NULL, \-- Comma-separated list (e.g., 'A1.2,B1')  
    question\_text TEXT NOT NULL,  
    options\_json TEXT NOT NULL, \-- JSON array of exactly 3 multiple-choice options  
    correct\_option INTEGER NOT NULL CHECK(correct\_option IN (0, 1, 2)),  
    explanation TEXT NOT NULL,  
    source\_url TEXT,  
    is\_safety\_critical INTEGER NOT NULL DEFAULT 0 CHECK(is\_safety\_critical IN (0, 1)),  
    FOREIGN KEY (category\_id) REFERENCES categories(id) ON DELETE RESTRICT  
);

\-- Table: users  
CREATE TABLE users (  
    id INTEGER PRIMARY KEY AUTOINCREMENT,  
    email TEXT NOT NULL UNIQUE,  
    jurisdiction\_id INTEGER NOT NULL,  
    target\_class TEXT NOT NULL,  
    current\_streak INTEGER NOT NULL DEFAULT 0 CHECK(current\_streak \>= 0),  
    longest\_streak INTEGER NOT NULL DEFAULT 0 CHECK(longest\_streak \>= 0),  
    last\_active\_date TEXT, \-- Stored as ISO8601 string 'YYYY-MM-DD'  
    created\_at INTEGER DEFAULT (strftime('%s', 'now')),  
    FOREIGN KEY (jurisdiction\_id) REFERENCES jurisdictions(id) ON DELETE RESTRICT  
);

\-- Table: user\_responses  
CREATE TABLE user\_responses (  
    id INTEGER PRIMARY KEY AUTOINCREMENT,  
    user\_id INTEGER NOT NULL,  
    question\_id INTEGER NOT NULL,  
    selected\_option INTEGER NOT NULL CHECK(selected\_option IN (0, 1, 2)),  
    is\_correct INTEGER NOT NULL CHECK(is\_correct IN (0, 1)),  
    mode\_context TEXT NOT NULL CHECK(mode\_context IN ('STUDY', 'PRACTICE', 'EXAM')),  
    created\_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now')),  
    FOREIGN KEY (user\_id) REFERENCES users(id) ON DELETE CASCADE,  
    FOREIGN KEY (question\_id) REFERENCES questions(id) ON DELETE CASCADE  
);

\-- Table: exam\_attempts  
CREATE TABLE exam\_attempts (  
    id INTEGER PRIMARY KEY AUTOINCREMENT,  
    user\_id INTEGER NOT NULL,  
    jurisdiction\_id INTEGER NOT NULL,  
    score\_percentage REAL NOT NULL,  
    is\_passed INTEGER NOT NULL CHECK(is\_passed IN (0, 1)),  
    time\_spent\_secs INTEGER NOT NULL CHECK(time\_spent\_secs \> 0),  
    started\_at INTEGER NOT NULL,  
    completed\_at INTEGER NOT NULL,  
    FOREIGN KEY (user\_id) REFERENCES users(id) ON DELETE CASCADE,  
    FOREIGN KEY (jurisdiction\_id) REFERENCES jurisdictions(id) ON DELETE RESTRICT  
);

\-- Table: srs\_state  
CREATE TABLE srs\_state (  
    user\_id INTEGER NOT NULL,  
    question\_id INTEGER NOT NULL,  
    interval\_days REAL NOT NULL DEFAULT 1.0,  
    ease\_factor REAL NOT NULL DEFAULT 2.5,  
    repetitions INTEGER NOT NULL DEFAULT 0,  
    last\_reviewed INTEGER NOT NULL DEFAULT (strftime('%s', 'now')),  
    next\_review\_at INTEGER NOT NULL,  
    PRIMARY KEY (user\_id, question\_id),  
    FOREIGN KEY (user\_id) REFERENCES users(id) ON DELETE CASCADE,  
    FOREIGN KEY (question\_id) REFERENCES questions(id) ON DELETE CASCADE  
);

\-- Create essential indexes to speed up queries  
CREATE INDEX idx\_questions\_category ON questions(category\_id);  
CREATE INDEX idx\_responses\_user\_question ON user\_responses(user\_id, question\_id);  
CREATE INDEX idx\_srs\_next\_review ON srs\_state(user\_id, next\_review\_at);  
CREATE INDEX idx\_attempts\_user ON exam\_attempts(user\_id);

## **Algorithmic Foundations and Mathematical Models**

### **Weighted Scoring Algorithm**

The application uses a weighted scoring algorithm to evaluate mock exams8. This design grades safety-critical questions—such as priority rules10 or specific local blood alcohol limits8—with a higher weight. This penalty structure ensures that users must demonstrate a clear understanding of essential road safety rules to pass the simulation.  
Let ![][image1] represent the set of questions included in the active exam attempt. Let ![][image8] be the correctness of the response for question ![][image9] (where correct is ![][image10] and incorrect or unanswered is ![][image11]).  
Each question has an associated safety weight ![][image12], defined as:  
![][image13]  
The user's final weighted percentage score (![][image14]) is calculated as:  
![][image15]  
A mock exam is marked as passed (![][image16]) if the weighted score meets or exceeds the target jurisdiction's passing threshold (![][image3])1:  
![][image17]  
If the countdown timer reaches zero (![][image7]) before the user manually submits the exam, the system locks inputs, immediately labels all unanswered questions as incorrect (![][image18] for all unanswered ![][image19]), and runs the weighted scoring calculation.

### **Random Exam Generation Algorithm**

The exam generator selects questions dynamically to match both the user's licensing class and the category distribution of the official state exam8. It balances topic representation while prioritizing questions the user has previously answered incorrectly8.

Algorithm 1: Stratified Exam Generation with Seen Question Avoidance  
\===================================================================  
Input:  
  \- U: Active User record (contains selected jurisdiction\_id, target\_class)  
  \- DB: Database connection  
Output:  
  \- SelectedQuestions: Array of exactly Q questions

1\.  Query the jurisdictions parameters:  
      Q \<- DB.query("SELECT total\_questions FROM jurisdictions WHERE id \= ?", U.jurisdiction\_id)  
2\.  Define the target category distribution ratios (D\_c):  
      \- Circulation & Priorities  (CIRCULATION)   : 30%  
      \- Signaling & Signs         (SIGNALS)       : 25%  
      \- Coexistence & Vulnerability (VULNERABLE)  : 15%  
      \- Alcohol, Drugs & Safety   (SAFETY)        : 15%  
      \- Mechanical Checks & VTV   (VTV)           : 10%  
      \- Documentation & Legal     (DOCS)          : 5%

3\.  Initialize an empty list: SelectedQuestions \<- \[\]  
4\.  Identify and isolate "Seen Correct Questions" to avoid repetition:  
      SeenCorrectPool \<- DB.query("  
          SELECT DISTINCT question\_id FROM user\_responses   
          WHERE user\_id \= ? AND is\_correct \= 1 AND created\_at \> (strftime('%s','now') \- 259200\)  
      ", U.id) \-- Excludes questions answered correctly within the last 72 hours

5\.  For each category 'c' in distribution D\_c:  
      a. Calculate quota for the category: q\_c \<- round(Q \* D\_c)  
      b. Query candidate questions matching target license class and category,  
         excluding the SeenCorrectPool:  
         CandidatePool \<- DB.query("  
             SELECT \* FROM questions   
             WHERE category\_id \= c AND target\_classes LIKE %U.target\_class%  
             AND id NOT IN (SeenCorrectPool)  
         ")  
        
      c. Prioritize questions with historically high failure rates:  
         Sort CandidatePool by user failure rate desc (using user\_responses history) \[cite: 22\].  
           
         If Size(CandidatePool) \>= q\_c:  
             \-- Select the top half from high-error questions, and fill the rest randomly  
             SelectedQuestions.append(CandidatePool\[0 .. (q\_c/2)\])  
             SelectedQuestions.append(RandomSelect(CandidatePool\[(q\_c/2) .. End\], q\_c \- (q\_c/2)))  
         Else:  
             \-- If the pool is smaller than the category quota, take all candidate questions  
             \-- and fill the remaining spots from the general category pool  
             SelectedQuestions.append(CandidatePool)  
             RemainingQuota \<- q\_c \- Size(CandidatePool)  
             FallbackPool \<- DB.query("  
                 SELECT \* FROM questions   
                 WHERE category\_id \= c AND target\_classes LIKE %U.target\_class%  
                 AND id NOT IN (SelectedQuestions)  
             ")  
             SelectedQuestions.append(RandomSelect(FallbackPool, RemainingQuota))

6\.  If Size(SelectedQuestions) \< Q:  
      \-- Fill any final empty slots from the remaining pool  
      RemainingSlots \<- Q \- Size(SelectedQuestions)  
      GeneralBackup \<- DB.query("  
          SELECT \* FROM questions   
          WHERE target\_classes LIKE %U.target\_class%   
          AND id NOT IN (SelectedQuestions)  
      ")  
      SelectedQuestions.append(RandomSelect(GeneralBackup, RemainingSlots))

7\.  Shuffle the final SelectedQuestions array to randomize the order.  
8\.  Return SelectedQuestions

### **Stats Calculation and Progress Engine**

The progress engine monitors user performance using historical tables, providing a detailed breakdown of study activity, attempts per question, and category performance.

#### **Seen Question Metric**

Calculates the proportion of the question bank the user has encountered:  
![][image20]

#### **Historical Correctness Ratio**

Calculates the user's overall accuracy across all recorded responses:  
![][image21]

#### **Attempts per Question**

Evaluated using a database query that groups the user's historical responses by question:

SQL  
SELECT question\_id,   
       COUNT(id) AS total\_attempts,   
       SUM(is\_correct) AS successful\_attempts,  
       MAX(created\_at) AS last\_practiced\_timestamp  
FROM user\_responses  
WHERE user\_id \= :active\_user\_id  
GROUP BY question\_id;

#### **Time-Decayed Weak Category Detection Index**

To track learning progress over time, the system uses a time-decayed weak category index (![][image22]). This formula weights recent responses more heavily than older attempts.  
Let ![][image23] represent the set of all responses submitted by a user in category ![][image24]. Each response ![][image25] is associated with a correctness value ![][image26] and a submission timestamp ![][image27]. The time decay factor ![][image28] is calculated as:  
![][image29]  
Where ![][image30] is the temporal decay constant (set to 0.05 to halve a response's analytical weight every 14 days), and the divisor 86400 converts the time difference from seconds to days. The weighted category error index (![][image22]) is given by:  
![][image31]  
If a category's weighted error index ![][image22] exceeds 0.30 (equivalent to a failure rate above 30%), the platform highlights it as a "Weak Category" on the dashboard, prompting the user to run focused reviews20.

## **Study Mode and Review Flow Architecture**

The Study Mode is designed to optimize retention by focusing on interactive feedback and clear explanations26. It displays questions sequentially, rendering one card at a time to keep the user's focus on the active topic8.

                 STUDY MODE INTERFACE SEQUENCE  
                   
\+-------------------------------------------------------------+  
| \[Header\] Cat: Circulation | Class: B1        |  
\+-------------------------------------------------------------+  
| Question 12 of 40:                                          |  
| En una rotonda, ¿quién tiene prioridad de paso?   |  
\+-------------------------------------------------------------+  
| \[ \] A. El vehículo que intenta ingresar.     |  
| \[ \] B. El vehículo que ya circula por ella.   |  
| \[ \] C. El vehículo de mayor tamaño.              |  
\+-------------------------------------------------------------+  
|                       \[Reveal Answer\]                       |  
\+-------------------------------------------------------------+

                     (User selects choice B)  
                                |  
                                v  
\+-------------------------------------------------------------+  
| \[Header\] Cat: Circulation | Class: B1        |  
\+-------------------------------------------------------------+  
| Question 12 of 40:                                          |  
| En una rotonda, ¿quién tiene prioridad de paso?   |  
\+-------------------------------------------------------------+  
| \[ \] A. El vehículo que intenta ingresar.     |  
| \[X\] B. El vehículo que ya circula por ella (Correct) |  
| \[ \] C. El vehículo de mayor tamaño.              |  
\+-------------------------------------------------------------+  
| \[Explanation\]                                               |  
| La Ley Nacional de Tránsito 24.449 establece que en una     |  
| rotonda la prioridad absoluta es del vehículo que ya está   |  
| circulando por ella \[cite: 10, 23\].                         |  
| Source: Ley Nacional de Tránsito N° 24.449, Art 41|  
\+-------------------------------------------------------------+  
|                      \[Next Question\]                        |  
\+-------------------------------------------------------------+

### **Detailed Answer Submission and Tracking Flow**

When a user selects an answer during a study session, the application executes several database operations in the background to log progress18. This process records performance data instantly, even if the user exits the app immediately after answering.

\+-----------------------------------------------------------------------------+  
| START: User taps option index (0, 1, or 2\) in Study Mode                    |  
\+-----------------------------------------------------------------------------+  
                                       |  
                                       v  
\+-----------------------------------------------------------------------------+  
| 1\. Freeze UI inputs: Disable answer buttons to prevent multiple submissions |  
\+-----------------------------------------------------------------------------+  
                                       |  
                                       v  
\+-----------------------------------------------------------------------------+  
| 2\. Compare user selection with correct answer stored in database:            |  
|    Is selection index \== correct\_option?                                    |  
\+-----------------------------------------------------------------------------+  
                                       |  
                                       v  
\+-----------------------------------------------------------------------------+  
| 3\. Execute synchronous background database transaction:                      |  
|    \- Insert record into 'user\_responses' with timestamp, selected option,    |  
|      and correctness flag.                                    |  
|    \- Update last practiced date in 'users' table.                           |  
\+-----------------------------------------------------------------------------+  
                                       |  
                                       v  
\+-----------------------------------------------------------------------------+  
| 4\. Update the Spaced Repetition (SRS) state record:                          |  
|    \- If correct: Increment repetition count and expand review interval. |  
|    \- If incorrect: Reset repetition count and schedule next review for       |  
|      tomorrow.                                                    |  
\+-----------------------------------------------------------------------------+  
                                       |  
                                       v  
\+-----------------------------------------------------------------------------+  
| 5\. Check correctness: Was the answer correct?                              |  
\+-----------------------------------------------------------------------------+  
          |                                                 |  
          | Yes                                             | No  
          v                                                 v  
\+----------------------------+                  \+----------------------------+  
| Play subtle positive sound |                  | Play error alert vibration |  
| and highlight correct      |                  | and highlight selection in |  
| option in green \[cite: 22\]. |                  | red; correct option in     |  
|                            |                  | green \[cite: 22\].          |  
\+----------------------------+                  \+----------------------------+  
          \\                                                 /  
           \\                                               /  
            \+-----------------------+---------------------+  
                                    |  
                                    v  
\+-----------------------------------------------------------------------------+  
| 6\. Reveal details:                                                          |  
|    \- Display explanation and official reference source link.  |  
|    \- Show favorite/bookmark icon and add-to-wrong-answer queue button.      |  
\+-----------------------------------------------------------------------------+  
                                       |  
                                       v  
\+-----------------------------------------------------------------------------+  
| 7\. Enable input and activate the "Next Question" navigation button          |  
\+-----------------------------------------------------------------------------+  
                                       |  
                                       v  
\+-----------------------------------------------------------------------------+  
| END: Ready for next user interaction                                        |  
\+-----------------------------------------------------------------------------+

## **Product Deployment and Lifecycle Planning**

### **MVP vs. V2 Feature Categorization**

To build a reliable initial release, the product roadmap separates core exam functions into the Minimum Viable Product (MVP) and schedules advanced customization features for Version 2 (V2)20.

| System Module | Feature Component | Lifecycle Phase | Functional Specification & Implementation Depth |
| :---- | :---- | :---- | :---- |
| **Core Modes** | Study Mode8 | **MVP** | Sequentially displays questions one at a time with option selection, manual reveal toggles, and official source links8. |
|  | Category Drills7 | **MVP** | Allows users to focus on specific topics with dynamic, un-timed practice sessions7. |
|  | Exam Simulator1 | **MVP** | Timed mock exams built using specific regional parameters (CABA, Provincia de Buenos Aires, Santa Fe, Córdoba, Mendoza)1. |
|  | Wrong-Answer Review21 | **MVP** | A review queue containing all questions answered incorrectly in either practice or simulator modes21. |
|  | Bookmarks / Favorites | **MVP** | Allows users to save specific questions for manual review. |
|  | History & Progress16 | **MVP** | Displays a history of mock exams, showing past scores, dates, and times16. |
|  | Weekly Performance Stats16 | **MVP** | Displays overall accuracy metrics and average scores across mock exams16. |
|  | Weak Category Detection | **MVP** | Identifies the user's lowest-performing categories based on their error rates over the last 100 answered questions20. |
| **V2 Features** | Spaced Repetition (SRS)20 | **V2** | Schedules questions dynamically using the modified SM-2 algorithm, adjusting review intervals based on user feedback20. |
|  | Streaks and Daily Goals20 | **V2** | Tracks active study streaks to build consistency, sending push notifications based on performance20. |
|  | Recommended Practice20 | **V2** | Recommends specific study topics dynamically based on the weak category index (![][image22])20. |
|  | Cloud Synchronization27 | **V2** | Backs up local progress to a secure remote server, allowing users to sync study history across devices19. |

## **MVP Release Verification Criteria**

To ensure the application is ready for launch, the system must pass a set of verifiable quality metrics across its core modules18.

| Quality Check Area | Verification Scenario | Target Quality Metrics & Acceptable Performance Limits |
| :---- | :---- | :---- |
| **Simulator Accuracy** \[cite: 1, 15\] | Initialize exam simulations for both CABA and Provincia de Buenos Aires1. | **CABA**: Total questions must equal 40, time limit must be 45 minutes, and passing threshold must be 85%1. **PBA**: Total questions must equal 40, time limit must be 30 minutes, and passing threshold must be 75%1. |
| **Crash Protection** \[cite: 18\] | Terminate the application mid-exam during active testing. | Upon reopening the app, the previous exam state, elapsed timer, and selected options must reload instantly without data loss. |
| **Scoring Verification** \[cite: 8, 16\] | Run test submissions with both correct and incorrect responses. | The system must calculate scores accurately using the weighted safety-critical math model, correctly logging the final pass/fail state in the database18. |
| **Error Tracking** \[cite: 21, 22\] | Intentionally fail a question in study or exam modes18. | The question must appear in the wrong\_answer\_queue and its historical record must update in the database18. |
| **Database Speed** \[cite: 27, 29\] | Run exam queries on a database with 10,000 recorded user responses24. | Question retrieval and exam generation queries must execute in under 150 milliseconds to keep the interface smooth. |

## **Chronological Implementation Roadmap**

The development schedule is organized into distinct phases over a 60-day timeline, establishing a solid database foundation before implementing the user interface and advanced learning features.

                    CHRONOLOGICAL IMPLEMENTATION TIMELINE  
                      
Days 1 \- 10: Database Setup & Schema Verification  
|========================\>|  
                          Days 11 \- 25: Simulator Engine Development  
                          |=============================\>|  
                                                         Days 26 \- 40: User Interface Integration  
                                                         |=============================\>|  
                                                                                        Days 41 \- 60: Advanced V2 Launch  
                                                                                        |=============================\>|

### **Phase 1: Database Setup and Schema Verification (Days 1 \- 10\)**

* Create the local SQLite database and schemas, enabling all foreign key constraints27.  
* Set up database migration scripts and write unit tests to ensure schema updates do not cause data corruption or performance issues30.  
* Import the official question banks, categories, and regional exam parameters1.

### **Phase 2: Simulator Engine Development (Days 11 \- 25\)**

* Implement the random exam generator, verifying that questions are stratified accurately across all categories8.  
* Build a crash-safe timer service that runs in the background and saves progress automatically if interrupted.  
* Develop and test the scoring engine using the weighted safety-critical formulas8.

### **Phase 3: User Interface Integration (Days 26 \- 40\)**

* Design the user interface for Study Mode, featuring a single-card layout with tap-to-reveal answers and official reference links8.  
* Build the dynamic Wrong-Answer Queue and Category Practice filters7.  
* Run integration tests to verify that user interactions are written to the database in real time during practice sessions18.

### **Phase 4: Advanced Features and V2 Integration (Days 41 \- 60\)**

* Deploy the modified SM-2 spaced repetition scheduling engine20.  
* Build the streak tracking and daily practice reminders system20.  
* Create the analytics dashboard to display the weak category index (![][image22]) and historical progress trends16.

## **Technical Recommendations and Strategic Alignment**

To build a reliable and successful Argentine driving license application, the platform must be designed to accommodate the country's diverse and fragmented regional regulations1. A modular, parameter-driven design is essential to support varying question counts, timing rules, and passing thresholds across different jurisdictions without requiring core code updates1.  
By saving regional configurations in a dedicated jurisdictions table, the core testing engines can remain standardized1. The application can dynamically load regional rules based on the user's location, allowing the platform to adapt seamlessly when municipal regulations change.  
A local-first SQLite architecture ensures the application remains highly performant and accessible27. Users can continue studying, taking mock tests, and tracking their progress without requiring an active internet connection8.  
This offline capability is particularly valuable in Argentina, allowing applicants to study on commutes or at commune offices with spotty internet coverage1. It keeps historical performance data stored securely on the device, allowing the analytics engine to process records and calculate recommendations instantly16.  
For the learning experience, combining structured practice with targeted review features is highly effective20. The platform balances open study sessions with strict mock exams to prepare users for actual testing conditions8.  
Integrating the wrong-answer queue with safety-critical weighted scoring ensures that high-priority topics—such as priority of passage10 or CABA's blood alcohol limits8—receive extra focus in study schedules. This targeted practice helps users build a solid understanding of critical safety rules.  
Finally, the application's growth plan is structured to ensure stability and reliability at launch. By focusing the MVP on core features—including accurate municipal mock tests, intuitive study modes, and essential progress tracking—the development team can deliver a highly functional product quickly1.  
Once the core features are validated, advanced algorithms like the modified SM-2 spaced repetition and weak-category predictive models can be introduced in Phase 420. This phased roadmap ensures a stable release while building towards a highly personalized, adaptive study assistant that helps users confidently pass their Argentine driving exam33.

#### **Works cited**

1. ¿Cuántas preguntas tiene el examen teórico de conducir en Argentina? \- Trivorio, [https://www.trivorio.com/ar/licencia-conducir/blog/cuantas-preguntas-examen-conducir](https://www.trivorio.com/ar/licencia-conducir/blog/cuantas-preguntas-examen-conducir)  
2. 1-Curso Nacional de Educación Vial en 10 Módulos. Introduciòn \- YouTube, [https://www.youtube.com/watch?v=FT4IyWc3dpU](https://www.youtube.com/watch?v=FT4IyWc3dpU)  
3. Curso de Seguridad Vial \- Agencia Nacional de Seguridad Vial, [https://curso.seguridadvial.gob.ar/ansv/](https://curso.seguridadvial.gob.ar/ansv/)  
4. Curso Nacional de Seguridad Vial | Argentina.gob.ar, [https://www.argentina.gob.ar/servicio/curso-nacional-de-seguridad-vial](https://www.argentina.gob.ar/servicio/curso-nacional-de-seguridad-vial)  
5. Curso de seguridad vial: cómo hacerlo, qué preguntan, cuánto dura \- iProfesional, [https://www.iprofesional.com/autos/362039-curso-de-seguridad-vial-como-hacerlo-que-preguntan-cuanto-dura](https://www.iprofesional.com/autos/362039-curso-de-seguridad-vial-como-hacerlo-que-preguntan-cuanto-dura)  
6. Acerca del curso \- Curso de Seguridad Vial, [https://curso.seguridadvial.gob.ar/ansv/index.php/home/acerca\_del\_curso](https://curso.seguridadvial.gob.ar/ansv/index.php/home/acerca_del_curso)  
7. Examen teórico Argentina 2026 \- Apps en Google Play, [https://play.google.com/store/apps/details?id=com.licenciaconducir.argentina\&hl=es\_NI](https://play.google.com/store/apps/details?id=com.licenciaconducir.argentina&hl=es_NI)  
8. Examen Conducir CABA 2026 — 40 Preguntas \- Licencias \- Trivorio, [https://www.trivorio.com/licencia/caba](https://www.trivorio.com/licencia/caba)  
9. Simulador Examen de Conducir Buenos Aires 2026 — 40 Preguntas Reales \- Trivorio, [https://www.trivorio.com/licencia/buenos-aires/simulador](https://www.trivorio.com/licencia/buenos-aires/simulador)  
10. Material para rendir el Examen Teórico ORIGINAL \- Municipalidad de General Pueyrredon, [https://www.mardelplata.gob.ar/documentos/transporte\_y\_transito/original.pdf](https://www.mardelplata.gob.ar/documentos/transporte_y_transito/original.pdf)  
11. El examen de conducir en CABA ahora es 40 preguntas y necesitás el 85% ¿alguien lo sabía? \- Reddit, [https://www.reddit.com/r/BuenosAires/comments/1s4kzxj/el\_examen\_de\_conducir\_en\_caba\_ahora\_es\_40/](https://www.reddit.com/r/BuenosAires/comments/1s4kzxj/el_examen_de_conducir_en_caba_ahora_es_40/)  
12. Ley 24449 del 23/12/94 \- Jus.gob.ar \- Infoleg, [https://servicios.infoleg.gob.ar/infolegInternet/anexos/0-4999/818/texact.htm](https://servicios.infoleg.gob.ar/infolegInternet/anexos/0-4999/818/texact.htm)  
13. LICENCIAS, [https://lavallemendoza.gob.ar/public/wp-content/uploads/2025/07/Cuadernillo-Licencias-de-Conducir-definitvo-2.pdf](https://lavallemendoza.gob.ar/public/wp-content/uploads/2025/07/Cuadernillo-Licencias-de-Conducir-definitvo-2.pdf)  
14. Nuevas preguntas para el examen teórico de la licencia de conducir \- Autoescuela City, [https://autoescuelacity.com.ar/nuevas-preguntas-para-el-examen-teorico-de-la-licencia-de-conducir/](https://autoescuelacity.com.ar/nuevas-preguntas-para-el-examen-teorico-de-la-licencia-de-conducir/)  
15. Simulador Examen de Manejo Buenos Aires 2026 | Test Online, [https://examendemanejo.com.ar/simulador-examen-conducir-buenos-aires](https://examendemanejo.com.ar/simulador-examen-conducir-buenos-aires)  
16. Examen de manejo \- App Store \- Apple, [https://apps.apple.com/ca/app/examen-de-manejo/id6446789951](https://apps.apple.com/ca/app/examen-de-manejo/id6446789951)  
17. ¿Cuáles son las categorías de licencia de conducir en Argentina? Guía completa de Kavak, [https://www.kavak.com/ar/blog/guia-kavak-todo-acerca-de-los-tipos-de-licencia-de-conducir-en-argentina](https://www.kavak.com/ar/blog/guia-kavak-todo-acerca-de-los-tipos-de-licencia-de-conducir-en-argentina)  
18. Driving License Management System Report | PDF | Software Development Process, [https://www.scribd.com/document/602134618/Final-Report](https://www.scribd.com/document/602134618/Final-Report)  
19. A full-stack C\# .NET application that simulates a Driving & Vehicle Licensing Department using a 3-tier architecture, SQL Server, and WinForms. \- GitHub, [https://github.com/mohamedismail37/Driving-Vehicle-Licenses-Department](https://github.com/mohamedismail37/Driving-Vehicle-Licenses-Department)  
20. FORKSAI \- Free AI Flashcard Generator \- Peerlist, [https://peerlist.io/teamforksai/project/forksai--free-ai-flashcard-generator](https://peerlist.io/teamforksai/project/forksai--free-ai-flashcard-generator)  
21. Spaced Repetition From The Ground Up \- Control-Alt-Backspace, [https://controlaltbackspace.org/spacing-algorithm/](https://controlaltbackspace.org/spacing-algorithm/)  
22. Licencia de Conducir Ciudad Autónoma de Buenos Aires 2026 \- Examen de Manejo Argentina, [https://examendemanejo.com.ar/provincia/caba](https://examendemanejo.com.ar/provincia/caba)  
23. Ley Nacional de Tránsito Nº 24.449, actualizada al 1/1/07 (fuente: infoleg) Síntesis de la Ley pertinente a Transporte Escola, [https://www.unterseccionalroca.org.ar/imagenes/u3/Ley%2024449%20%28TRANSPORTE%20%20Escolar%20-%20ACTUALIZADA%20AL%20A%C3%91O%202007%29.pdf](https://www.unterseccionalroca.org.ar/imagenes/u3/Ley%2024449%20%28TRANSPORTE%20%20Escolar%20-%20ACTUALIZADA%20AL%20A%C3%91O%202007%29.pdf)  
24. Database Testing Guide: Types, Best Practices, AI Approach \- Virtuoso QA, [https://www.virtuosoqa.com/testing-guides/database-testing](https://www.virtuosoqa.com/testing-guides/database-testing)  
25. Cuestionario de preguntas y respuestas \- La Bella \- Escuela de manejo, [https://www.autoescuela-labella.com.ar/wp-content/uploads/2024/10/EXAMEN-TEORICO-POWER-POINT-1.pdf](https://www.autoescuela-labella.com.ar/wp-content/uploads/2024/10/EXAMEN-TEORICO-POWER-POINT-1.pdf)  
26. Best Spaced Repetition App in 2026: Anki, RemNote, Notelyn, and More, [https://www.notelyn.com/blog/spaced-repetition-app](https://www.notelyn.com/blog/spaced-repetition-app)  
27. A Comprehensive Php-Mysql Based Biometric- Integrated Online Driving License Generation and Management System \- RJPN, [https://www.rjpn.org/ijcspub/papers/IJCSP25B1111.pdf](https://www.rjpn.org/ijcspub/papers/IJCSP25B1111.pdf)  
28. Clases y subclases de licencias \- Argentina.gob.ar, [https://www.argentina.gob.ar/seguridadvial/licencianacional/clasesysubclases](https://www.argentina.gob.ar/seguridadvial/licencianacional/clasesysubclases)  
29. Database schema: SQL schema examples and best practices \- CockroachDB, [https://www.cockroachlabs.com/blog/database-schema-beginners-guide/](https://www.cockroachlabs.com/blog/database-schema-beginners-guide/)  
30. How do you (Unit) Test the database schema? \- Stack Overflow, [https://stackoverflow.com/questions/439855/how-do-you-unit-test-the-database-schema](https://stackoverflow.com/questions/439855/how-do-you-unit-test-the-database-schema)  
31. Test-Driven Database Development \- Rob Walters \- Medium, [https://rmhw.medium.com/test-driven-database-development-176c9e144c42](https://rmhw.medium.com/test-driven-database-development-176c9e144c42)  
32. Tutorial Database Testing using SQL, [https://ldo.gov.in/WriteReadData/CMS/Tutorial%20Database%20Testing%20using%20SQL.pdf](https://ldo.gov.in/WriteReadData/CMS/Tutorial%20Database%20Testing%20using%20SQL.pdf)  
33. Examen de Manejo Argentina 2026 | ExamenDeManejo.com.ar, [https://examendemanejo.com.ar/](https://examendemanejo.com.ar/)

[image1]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAbCAYAAAB1NA+iAAABN0lEQVR4Xu2TLUtEQRSGX0EFUTEYdEHQtCBGk2BU0GDaZBGbYnODgskfsGnDLojF4L8wiBbRYBLF/yAmi+LH+3LmunOP995Jtn3ggb3nzDkzOx9AnyoG6Bzt0BN6EGJJRukXbYXfGSr+pM90NornmKZXdBvFs53Sb9r1CTFBL2ADiorFCmx1Lz4htDwl13wiYpG+wSbJMRKCN7CVlFHaYCkED33CsQ4b96dBk37QZZ9wHMGKNfYXbdg5vaRjcaKAV1iDYxfHGdINNJGKH+mMy6FNr+m4T0QswE6q4RNiEtZ9lw7SHdgt3ISdUHaBhrKCIvT/5BZ6K5miD/Q9xCup01v0NkmP5x72kGphzDASq9BGqZGK9ugG8ke7T+ej7ySaObs4cjWfTqNjjRuUPbJK7ugT7ET6/Ac/t2w/B+5TRmEAAAAASUVORK5CYII=>

[image2]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACMAAAAXCAYAAACBMvbiAAABrElEQVR4Xu2WzytFQRzFj1BKkoiUUijJSsJGFrKRlAVl4S+wYKks7GQnC1GSshBiy0oslL2iRBaUxEIpbOTHOb4z3m2ehfdy3c079end+c7cO2fud+Z7H5BTTtlpnXz8gidy7e6JTbfkioyRascKuSQtrt1DdmCmYtU5qY20i2ATb5OCSLwOZjo2aYKqIKa38E7agngTOQhif6phkhfEpmHpqAjinWQriMUqnyKZCU3+u3phRl7CjiTkU3QadiShPZiZtbAjCfkUhScpEfkUhSdJ6iCDpIR0k0mST8rJCGlIDf2K95EFWKlQW9Iz2mGFVM+pd9c1rv9bxUil6KeT1EUOyTJphVVnVeo5WNG8Ic1u7AS5I41knyyRQpiZGfJGysgieYTVMJQi/Rvk0f6Rwag0iVbiFTWu6qx+L70NTbJLnmEL8NK8Gj8UiWWs0Mxq5DpqRnvuDLbyAaSbkV5h6c1avzVzTOZhb60fZkYV3Etp1Yf5AbaHMtYs7G/EPWxFR7Dc61dtpVb9G2QUtmemyDgs5Rdk092jsZXkxF3rnlilDes/wto/aueUsT4BLHtj6Fi+d7cAAAAASUVORK5CYII=>

[image3]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADMAAAAaCAYAAAAaAmTUAAACcUlEQVR4Xu2WS6hOURzFlzCQVx7FLcVApCSSiSgDV0pKHuVVtxiQlGKgDMxMzEhIZEQijDySOIyUgZRXF8VQkpFCeazVOrvzv/t+uuTrdk6dVb++8+29zz77v/+PvYFWrVp1Q5fJV3KGHCOfyWnylEwN42qvmeQxmZW19ZPzZERor7VGkbNkdt5BbSO78sY6az/5lTeWWoyGhZjyQsZMyTuoOWhQiEnJM+/JITJvYHfztAQ2KKJqtigOapKmke3kNiqDjg8Y0VDdg40pQtvk8DwcUpVdUP4OqYPkSN5Yaj0Ge2ZveB4OzSdXybi8o5Nu4M/GnCA/SC8ZTdbAObSRrIB3a2TZfpKsLP9L6te4DaQP1fk1gWyCi8xycg4+x6S55Ch8FKh6iofkFRz6Q0bFO/KITMzapY/wyS9DNNFmeOJojDz7AV6IwlLXII1fTZ7AV6Pr5C2cj7oWaWG6WVwhO2ADDpD7ZAZ5QbbC81/APxhzlywlL8l3eDEF7BHtYpRcXWRtkryhUn6TfIF3VpKhuh5FPUd1AH9CdQT8hI3W9+/AR0QPPEeBvwyzZK0WpEVosp3wLubqZIzKuXbuFFmHwcYU5XPSMzj0psPvpMSO70WlDZmUd/yvojG74Y8rbJRbiu+18KKWlWM6GaM2bZaKSsov6Ruq96TxcL/GJy/pm12VKpwWoqKhXd0D58xhsg8O29fkEhy2qoZxEcqdeCgrJ8fCnrpFHpBrZGE5XrmlDbsI51PXpftbvKsp4VNYajf1v5PGkC2o3tWC38CFJUnejx6T9L92F10ZrBxLklGqVroXNlIKqVXw2aJKlnuhVau66jeyo3oWnBWY9QAAAABJRU5ErkJggg==>

[image4]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACoAAAAXCAYAAAB9J90oAAABPElEQVR4Xu2UzytFURCARyh6haIk9YqNpGwkZSErqZeUFRs7JUvZsbO0E1nyB0j+C0t/gZKNlUSxxDfNvZl3inddh16cr756b2Y6zZ3zQySRSCT+LS04hdc4V59qHmp4mdka5EqjXz6DF/iM21jFNV9UkHZcFltLp6lrR0EXesXd7Lcyjy/Ylxc1oAt38AxHglw0xvAWh11sSaz5Nhf7iD18xMMwEROd4AGeSn1T+2KNNmIDj7A3TMRmAp9w0sVG8Q6vXOwz8m3XqQ4FuWjkjQ64WL7tOuWvUBH7wB85p4Nib5xv9Fis0U0XK0onroqtOS4Rb7zSjfd4jjdiz5Nue78vKoG+ndNiz9Ri9v/b9IhNtUPet73IjS+CTlWPg17aqJTd9l9Bb+4CbuEDruOsL2gWVsTOp/fEFyT+Em8UFTRCL4kukAAAAABJRU5ErkJggg==>

[image5]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACoAAAAXCAYAAAB9J90oAAABRklEQVR4Xu2UTysGcRCARyhSKEoShYMkLpKbHN+Lixy4OCjlpqQcuDm5yp0PIDn7Ao6+ACUXJ4ni5s8zzbuZ/dVbu/vrZdM+9dTbzGw7zbw7IhUVFaVhDyfCYBlpx3u8wvl0qry04g3e4mqQKx0tOCM24Q3sTKfj0Rcs4DW+4z6O4KYvyskFPuF2mCiKNvmFh/XfSg0/sT8pikCn+oFHYSIvU/iIYy62LNZ8m4vlpUtsmjrV6MugEzzBc0k3dSzWaFG68RVPcTTIFWIW33DOxSbFpnDnYlkYFnvuAPuCXDRJo4Mulqxdp5wFXWvy4ei6m8KQ2MH2jeq6tNEdF2vEuNjk16UJpyikB5/xEh/EzpO+fMAXlYVesal2yM/aY774XyHr2v8EPSdLuIsvuIWLvqABeiFWMjpdfyaKNbH/p/fMF1T8J74BQ2E4vmF9IO0AAAAASUVORK5CYII=>

[image6]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACwAAAAXCAYAAABwOa1vAAABQElEQVR4Xu2VsUtCURTGj6iQiyC5RDQ0BtIaSGOD0JYNQaODo0NUEP0HbZFzUxDoKkRj7Tm1t0VDBA2CROX3ce/Dy+EFvucVXnB/8OPp+R56Pe/cq0ggEPi37MEtmNNBVsnDoXVXZZmHXb6Hb7CjskyzDq/hOSyrbC4e4Dd8giV4DD/gCB7B4vTWVHThJ7zQQRo2YAVuwy/YdLJT+GuvPuCcj2EPLqlsZg7ttS1mcQUnO7O1eRfMJ3QAX8TTScIPuBGzOJcB/IE7qp4Ejhg3HzfhpspSE43Do6rzB1xJ8o4sw3cxs7umMi9E43Dp1NgZdrdh37ec7C9WZLrBuOiF0RezYP5bRXAzPsOqmC+vOVkcvI+P/kQ8H2Fx8Ai7E9PVCL5+FTMmt049E7CDcccM6zTpDAcWwSrcn9G6hKcW8MMEr780paLrW/cAAAAASUVORK5CYII=>

[image7]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAC0AAAAaCAYAAAAjZdWPAAABV0lEQVR4Xu2VsS4FQRSGj9AIiQiJRiKhUkgkWg9AQSFKvUalUSs0CoVCoeAFtF6ARKOQCJEIBb1CQqXg/+/Z2cw95lyruCOS+ZIvmz07u/fM3n92RAqFQjfph6vwAK7BgfbLacbhri1m5AZewSV4Cp/hbNuIBDvw0xYzMQjv4UR1PgTP4F09IsE0fIGP9kIG+uAJXDH1efghOqEky6JvmTfnZhTeisYiZg6+w0lTb82O2XkTbZrHazgTD+oy4V/2muYxSYjGmL2QgA/hRJv6U+RCc17Ttl4TosF85cZrzqvXsOlNW8yE15xXb8FMNY0G4Uefm0BTkz8aMQwv5fs4dyESfjniaByJrmiPKXj4SzvRA4/huqkviiYg+cnbqgzsiT4oJwtw39Q2pMNmx13oCZ6L7kJ/xQV8FV1bD6K7IaPrwjyPSP43HNMrmuPt6sjzQqFQKPwzvgBtkVFF//OevAAAAABJRU5ErkJggg==>

[image8]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGEAAAAaCAYAAACn4zKhAAADgElEQVR4Xu2ZTcgNURjH/0KRzxApEkoRIiGiXmJhQb4WSj7KggVZKizeko0d+cqKhY18bGSlTFkoC5ZsFBJJkgWJfDz/njma+7xzZs6cuXeuMr/6d+s5c+fOOc/Hec5coKWlpaWli6wWXRBdFY3qHGqEqaKHovuieaJhncPVmC/67dF30V3Rgr9X/xsMik7BP/Hpoiuic1Bn1WG4aK9oiR3IcAO6XrvsQBWmiB5DbzQZOjlG1wHR59Tum3A/uCbabI0p20XfRGtE+0SvRRM6rgiDi09nPoXO3/d75Bj0Gjo9mjmid9AbWXZD7avsQJ8YK0qQvyhLRZ+gWeBYKbokGpGxlbFM9AU67w/pZ97vOZwTGBzRnIbeJDF2chA6xgiLhVm0XvQM+qBcJGpnKpbEUIqcwEB6I5ph7Hx+LlQMCRpyAjeXvHQaKbqTjtmJVeGk6Lloox2IoMgJv0SvoGUkC5//JqplgyNBQ05wpWiLse8Q/YSmeCzMAjp5kh2IpMgJnIPPCQn0u1VJ0JATeIM8zUX9DZmlrptMg5a1vBJW5IQ8ewgJyp0wAA3WpNMczhjoj2QXa7zoOnRDY0mysHNgF8XPInjv29ZYA2bTLejz5gWHb7F99hASlDuBa8S1YjkcbcaCYGfEH7GlyHUI/LSwdWUbWNaDM/0fQNvEIu1xXyhgLTTaWNp85xbfYvvsISQodwJhUAxAz1ZPoAe4YJgBCYbWS3ZDX0XLjb0K3Ah5kOkmbp+yz0t+IH+xuYi93JjZtLBERu2dbpOzXRGhLS8TmHo8GYamHa/NK2mxjIO+LuBhzPJS9BFD9wsuYi9bVHeWYgmvjDuk2VLknJONqkOpnZlzD+GT4nf2I7+Gx1DUHZ2H1uUNGRuDhRG6MGNjlrNshLTdCcqdENUd+d4XZd99rEhtzIjjolnQVxnUWwyNtiI4WdZyvgJwr0ViKXIC2QR91cLoZMBw72KDkGUrtNQOGruDgccAtOvjy6goJ4SyTXRGdDRj4wLG1Fd2UtzIGYF2Ykcy15VR5gTCRWfw8LQ/04xlyVvQGHrqhDxmQzOGmbDOjDVBiBNCsWU4lsadwJdkZ0WHUa+s1IGTrfMui43CZWusAcu1r1T1jInW0DCM4BeixXYgkEXQ/yO6AQORGz/FAP1v4MTZlzP6TqA//6wxEC+K3oseoeLhrKWlpaWlxfEHanHq3iUx68oAAAAASUVORK5CYII=>

[image9]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADEAAAAaCAYAAAAe97TpAAACJUlEQVR4Xu2WPUhWURjH/1GBkBahlJARRUvgJgY1iSC4WINBQQiVi4PhICWCgw4NTjZHIE1+IE6KBFJBkzg0FmFgUQk2OBUk+PH/89wr933oPe95VYSX7g9+6D3Po/ec85yPC+Tk5IQ4Rh/Skz5wRBynX+k6HaXd9APdoacyeUFa6F9607UfBfV0kfahcBI1sMEkdibTXpQWuoDI5EPkGv1CN3wgoRpWjX4fOAy0/D7SH/QVfUFH6B1Yx2JIOziO8DJWzh/a7AMHQS8com2wku+XHlgFGn3AoUHIDh9IeUI36RbCs5GiCjyFrdODcB5WyQl6wsU8wUFcphdhZX2HuHI9o8O+cR+8hHXsig84NGnKW0OR3PvJzxuwkylmU88ibrAh0klT5/R7CFVMefO0ysUKGIYlxvAWdpZ/C9i1l/1vauh7xL2zHZZ3zweyKGmbzvhAEabpLd9YJppRzaw6p+VSDB0c2qvXfcCjjZWO9BHsxAihvCnEHQIhBmDvPZs868JTFcdgN/Ql+hk2iJJ8p6v0Amx2tD9CaA2/pg9ce7lcpT9hs63/OQnrvG5oTeYb2AB60z8IoUvqN11C3MZOaYB929ymtS5WDiuwivyCnXx6VudPZ3JK3kVKOJdYLvrOmoPdM+lZLh9nk0qg9zfB7h599OnE/JSJa9nWZZ4rAg1IK0OD0/J6XhiuDLQ3V2EV1bLqLIhWCDpy79Jl2po85+Tk/E/sAjxzafNplkPVAAAAAElFTkSuQmCC>

[image10]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAaCAYAAACO5M0mAAAAOklEQVR4XmNgGJqAEYg90QWRgTAQewDxLiAuR5PDChYyjCrEA+isEBS/kkAszwCJwlVQNkhsFAwEAAAZQQ6c3BjE3wAAAABJRU5ErkJggg==>

[image11]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAaCAYAAACO5M0mAAAAuElEQVR4XmNgGFqAGYhdgHgSEOcDsTCqNAJsB+JHQBwPxPOA+AsQe6OoAAJLIF4DxCxIYjZA/BuIGZHEGFqBuApZAAiUgPg5EEvDBDiAeCsQl8MEoEASiB8yQExGEcClEC6OIYBLHEMAlziGAC5xUJCAggaXQrhnQACkCBQjyEAfiD8xIAUPCOgwQIIIFFQw4AHE/xjQAhzE+QnEtlA+DwNE42u4CiQAUqwGxGVA7ATErKjSo4AaAADKvyIANPF0HQAAAABJRU5ErkJggg==>

[image12]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABkAAAAbCAYAAACJISRoAAABeUlEQVR4Xu2VOy9EURSFtyCRGIUQIlFNlBoRHSJBodBMQ6H1aNQS/4BCIdSi8AilaBQynVIlaiL8AqXHWvbZycl27oNMN/MlX2Htm33uOfvcIdKi6bmCX/Aetkd5FzwNtQnYGdXa4Ah8hDOiz+ZyLNqo7nKyJVob8gUwAMd8mMW2aKMHl/NtD0IttQhfgM+UYl200YvLL+F5qI1HeQc8hCtRVsiiaKN3l5/ABfgJJ6N8Cl7DSpQVklqkT/TtKXM+Y3CH89HfpbBGXMjYED3v1CLcYXzTSlGFb6KLsPGe6CKEA38SHTLJG/YHXPahYY1swDeix0X6RW/dvuh3cRvyFGuw24eGNeIiPIpaVONw6/BM9EbZDv+MNfJX1eDHyqPIGvYmXIVHkn2UP9hXn3qItQtJD3sQ7sAe0SPNhY1efRjgL0LWzwcX5hxG4Zyr/YK3YtaHgWlJ7zCGt67Xh42Ec3yGS1JiN/9lGN7BXUnPrWHw/0nRkbZoFr4BYlJFNNWjzHAAAAAASUVORK5CYII=>

[image13]: <data:image/png;base64,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>

[image14]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEYAAAAaCAYAAAAKYioIAAADDklEQVR4Xu2YS6hOURiGP6HINUcuUQ6RkFuSCAMDmTBwKdcBOqEkKcSAkQED5RaJJLnkEhNE4pCBmFAuJXJMKMKEkhLvY63VXmf1/+fYxzmivd96+tf+1t5r7/2ub132b1aqVKlSf1e9xFnxVSwRe8QnsVU8Er2zU4ulx+KBqI1ig8QPcUy0i+KFUQfxRgxNK8wZsyoNFkUbxN406HXXCjyMDon7oiatkJZbQYcRImMYMq/NTbbtG1cXW8/MmRPDqlRK6iuWim+WmdNaqjSxx6oV+8XDJP6nWikuidFpRUvEnuamOWO6JnUtEYa/9L/V1FFMFO/SikhjROc02Ix6ijViQlrRlDaLHWnQa661bsb8jvqbm+eq6Yq5c/JqtuU05rJVNoZViLT+7o+7i8FimBgphvtyP3880IOo2+UJq9kksdBc7yHaW2Buop8mjvp4MIZf9k7x/olsY86rE/OjOBl02NyQod0gMpD2Z4h5ltOYBnFP9Ejio8R7cztexNBaJ66L8WK9OCUGiFnmht1Yc0bcMmcSc8piLpamW/bCiE8M5jJ21ufECnMrYTCG9mlju+jjr8Hc1BiuZ7eOCVvEHR/nWrYf/GII5VzG3BBTzK1IlHG+Xuyzxu4HYdYIzxNzvUg5iGX/orl2IDYjLnNt2DR+sKyNdCjxMvELxW2EeeuVuXvRiTxfiB/x56HcQ4lMQPTWJstSspoaxFpzPfzcXLbE6c6cVe0B4pfiu4z0ZigeNPdJgpoz5q0Y4svh3ONZ9S+x+pBZPEtQbmPyil7hE4HPB8qwM6qfI6ZGx90s2yzGxvDQdADtxJvJ5oz54o85j9WyXpyP6hlSZCIZGRvW5saQpozXyUk51lVx21z2jfOx3eZWOLKMHiXVwz4JMJj4U398RmwUHz2rzYm2r4kT/pg5bZk5E06LbT6OQXQY2XxAXBCfLWunTRRnQVyORW+GSTMVq8giy1YszHuRVTcprqkRnZI496q036LzMInzw/3+WfGwbOSCeOCT0XGh1UXMNPdPIStSpawrVarU/6ufa/eWYGi3ZlIAAAAASUVORK5CYII=>

[image15]: <data:image/png;base64,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>

[image16]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFsAAAAaCAYAAADYMiBQAAADF0lEQVR4Xu2ZS8hNURiGX6EIuf2RUspAGbiUGOgfyAQDl1xKKRkZMaEoDI70D0wkhTLxGxKlMCDpxEBRLgMxkSgpkgxMyOV7+/Y69vnOXmevtfe26dhPvZ1a3zrr8u51+fY5QENDQ8N/xVjRStFp0ahoQle0HmaJ7omOihaIxnSHw1ki+tlHn0THO7Xrh2M4Bv8Et4rOiM6JJplYLHywO21hivGiS9AxFWZI9ADaCJ8iO+Xk+CBuJOVzO7XrhX2vt4UJ80SPofE1ojeipV01wuB8t0HbyjPyIPLr9GW+6B2yG5ktei5qmfI6mAy/2YzdghruuAsda8zCWCb6Inoq+oBsD9KUNnsE2kDblBP3IC6LxplYFtwRq0VvRReg25tnHbc7FYPPbI6D47GTHhZ9Q/Y88mBfbfS2aSlt9m1oA6dsQNgAjW23gQx4ph0RvYBuzbL4zOax9yyJpXGrlIsjltrMdkcIjU0zUXQtiU01sSwOQB/cDBsoiM/shaKPSSyNM5uKpTaz+WVmHaPQbU9dFX0W7YWaHkLLFpSE9wVNpblpnKl20r7yEELNXiX6Dq0fDdMldnAHv89Vp5ijgO0st4Ul4O64ItqP3rTPZ6qvPIRQs3lUnhWdQPgi7MALkB3ssIFIONj30PSrn0J4BV09PJKy8JnqKw8h1GzCh/9E9FX0yMT6wkzkoWi6DUTCDMGe+WXZAk3v7JblWDlma0wdFyTTSqaXK2wgD9fBefRu1SJchG6zqpgCTeWY0qXhWDlma8y6pKxtykMINZsnAOtE++Vy6LJHiOOmaBcKDMSDLxsha0U/TNkeaH1+OjZDX1jyXnRCzY7ORvhjCr9gtS9dqSCcFM/ajaKZKGd8P7MJ+2LGxHHvhm5vm7lsgu6Olil3zBG9Rq8XFI21RJv9p2EWcx16gdgJxJBnNuE5zTdUypc9cXVnGVeEf87sqggxO4STqO7yHlizCSfGlVkUXtjM1aviEAbYbObbL0WLbSCQRaguQ+LPuHzTpgYS/oLIi4+r6TD+zj8106B/UHAM96EJRkNDQ0PDgPELKavTJ4qDYFMAAAAASUVORK5CYII=>

[image17]: <data:image/png;base64,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>

[image18]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAD4AAAAaCAYAAADv/O9kAAACMklEQVR4Xu2XsUtVURzHf6GpoeaQixgINSlKhJYELoKLg0HlEDg0COkWImK4ObQ4RhGJVIuDFOjg4ODwoK1/wSVFEAoRhxqS1O+33z16/N17Lu+Rz3ep+4EPD77nPHi/e8/5nfNEcnJy/ifa4VHAX3AVdpzMziaTcB6+hFVmLJVm+EW02GvwEqyDo3A/ypllkYfwHeyDT+BH2HRmRgo34I5ogZYR0fyeHcgAt+EevOxlP+AbWO1lQV6IFlcwORkTHeOTzRLuZW2b3NUybfJE1kUnc4/48EkuR2PXzVilGYCHcNPkLJi/95PJE3HL/L7JH8Hfosspa7iVGCq8YPJEXBe33pTzaWrstK/hVgl+EG2wIVyBocJtHqNedCL3huMqXBRtEn7jcCzBDdhvBy6QUIGhPAabRNIy7xbtkPy08Jjj9uB3K0WowFAeg2+6ABtMzi7+E94xOfksZ1dIMXTB4RK8JenbjOf2gcQLLKq5sdiCxLs5YRZ649/ldIWMwx5vLAkW8FT0dlWsU5K+x1vhV7hr8gXRwlOPM3cW2mXuHgifZkuUsUD3Q9wy74WzcEXiK6bc8GG+Ej3SfNZET6FOk/8hdD9/7M25G2V88zOwLcobRVfCc1gr2vxKuh+fM4Oi123eMLn93os27L/iAZyDz7yMD21C9H78zcsryZDoC+LZXjbY9LjM+ebZ5K5EWY0/6V+DS4jXW8I/ATzn30r2rrNlwf/LxwaTdMHJycnJybkojgEz/5JT0UuGbgAAAABJRU5ErkJggg==>

[image19]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADUAAAAaCAYAAAAXHBSTAAACZklEQVR4Xu2WS0hVURSGV2SSj9AyqMgQJYTIUYRgBDURhKhBBREOm4izClIaRJNmCdGkCOHaIILImYFJhNQgsUEjMYLoMWkQEYQJKZX/z9qbu8/iPO4DuxXngw/u3Wvdc/c+e+11jkhOTs56sB0uwN9wFR6OhmvGBvgKLsPbcBDOwF9wZ5CXyiH4Fu6wgRrQBAvwuvvs4ULPwDewIxhP5AR8COts4A/DXXgGf4ouwsL5sapuuc+JbIHPpbrSa4cPRMvljvMmPA2PSsYEHH7CXFSLiYV8F807aQMh++AXuNsGyuAdHJVouZRLv+iZGbABg1/UiA2E8BAyyd/NbfAr/AQP+qQUekXPZDU0wGk4J+m7RDIXtRW+FO183LFHcK/ozvGHF4qpsbBkn9rBCuD/8P9KOQKZndqX3ns4JcWuMgnvS3b75GQu28EKuCs62V02YOCOMo+PIT6OYuFhYxJr+YDEd5w0WAJL8GOKE3Czy4+D/3lPdB7NJmbpEc27asYjsOv5rTwGf8DFSEY6PI8FO1gB7JScLDtxEvvhZ3jKBiy+9Nj5uPUf4DcX4y6cc5+T6IQv7GAF8DnJRXW57+yifBnwR4DN44nLyawmJvmHrl8UL8aL8Ixxu7O4KFrr1dAG5+GQ6FxuiC7sLLwGx0XnyreMVOpFy63Pfecd4ARXRFurbxqlcEV01y/BTSZWDlwYJ8958Vp8/+N5DxtI5vVb7YDoWOYPY9gDx0QnFfoYNgZ5afDGdsNh53HRd70wzo79T+OPBMubN/q8lPbK9VfDFj8rxV1nSf4XHIGvRZvGRhPLycnJKY81nNN9mD5yWSIAAAAASUVORK5CYII=>

[image20]: <data:image/png;base64,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>

[image21]: <data:image/png;base64,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>

[image22]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABYAAAAbCAYAAAB4Kn/lAAABUElEQVR4Xu2UvytGURjHH2EQkhQp8qMMShn8CwazbKzKbBGLNwYpg2RnMxltsljEoGS0kDIZmETi833PweMk3nuvQXo/9el9z3me89xzn3vvMavyb6jHDrzAF9zFTucALuNzzMvMrYXCk2kAanATm9JAJaioig+mgYguqAtkotVC4Q03V4c7bjzn/leMdqnC425uBM/cOBe6zScLu5rAVbzDPZ+UB7VAT/0ar/DRwh0s+KSsqA16aL4NXXgYf4Vytt+jFaI2aHf+bejFaQsPUJSimVAbVLg5DUT68BSH0sB3DOO9hcJfoff2AcfcnNpzgN24hlsuZrXYjosWiqq4/4x7cAqP8QhbwrLymnOcj3mXuB5jZfxOf3ImrhElvMH+OFb7tMnC7Ecb00BR9InrMHo7M0Zx6SOcH/X+BFcsHK+z2PApowDabZv9Um+r/BFeASsXQgChMBZFAAAAAElFTkSuQmCC>

[image23]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABcAAAAaCAYAAABctMd+AAABM0lEQVR4XmNgGAUDDTSB+D8O/AuItwCxFlw1GUAEiE8xQAwUBmJGIOYA4mQg/ggVB4mRBZSA+DkDxBB0EM0AEbdElyAWtDJADDiAJg4C6QwQuSB0CWLBHgaIAZPQxFmBeD1UTgZNjmgACxI/NPFgIP4LxO/RxEkC6KkEhpUZKIhIEOBmgBgECncY4APipUA8nQESNGQDUErBFiTGQPwVSpMNQC4+AMQ8aOKg1PENiE3RxIkGIAMPMGCmEhAAieFyOSionIC4CoiZ0eTgAJZ50IMEZulDIJaEimUwQHItCJwB4jgglgPicqgYHOAqTyKQ1JhBxUA+ALlQHohZgHg5EAtC1YBSEtmpKRCIu4A4H8oXB+K7CGnqAlgw0gyYAHExEGcB8XYGPBFKLhBjQBTNo2A4AwBJNUIBGkX4pQAAAABJRU5ErkJggg==>

[image24]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAkAAAAaCAYAAABl03YlAAAAfUlEQVR4XmNgGAU0A5JA3AvE/4H4LxAvRpWGCK4BYk4gFgHiqwwQxXCgDcTvgVgHyucB4tVAfBymgBGIpwDxViDmgAmiA30g/gTENugSyACmSAldAhnAHImuCOSMBGQBViD+A8SHgXghED8D4lwGiE9RAMjRoHAC4VFABgAAZPwS/RYafsgAAAAASUVORK5CYII=>

[image25]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADoAAAAaCAYAAADmF08eAAACL0lEQVR4Xu2Wz0tVQRTHv1JCP9SIpIgkwSJwF0RBURT9AULpopDauCjaBgkupI0boU2boAJ3LSKqRegm4q0SKlolghJlm2gRYmRQ0Y/vl/PGnkend99ceSrcD3w258ybd2fm3HMHKCgoWI800vO0wSfqwGf6J+IP+uTf0Pz0wSbe7RN1opW+oHfoDtiGt8Gea45eLcdyowl/w052NeigH2mXT5Be+p0e9Yl6oR3/Rh/T2/Qm7KF66MaKcVkYglVUk0+QS7DcWZ+oB6fpO7rVJxLQHE9hi/Gowh7RSdjGJnOAztOv9ILLxThCp+gxn0gklO1yC+2mv2D/mcQ2OkpP0O30JWzBWXgG+/1KEcrW+5zuQ84mdI7eg71LnbAW/3bRiDgDPpCTULZacKClHLuFFWyQ6nSa9IFPRFCZf6jipoXR1Qll6zuuKkwecvFkSvQnPe7iMUaQs5wcWmQJSzuu4urqh108GZXte7rHxWOM0/0+mIgWpwXp0+RRPHaiKmd1/lN0w+JUnFC2Wb99uqXcp5t9IgF13OXKNmzADOy2Ji/DXomD9BW9SB/S/vJv/osmrKVsA1rkIL1G96L2hhG731YyW47ptF/Tdthh6AuhL4VoRsbXSB13AnbXTOEG/YSlD7ylclAiuvOeocOwLix20bsLI6qg4w+7oGtVLWW72qjUdSXMhHb8JL0Ou8hnOvo1xDSsT1yB3QeizWiMfqFvYJ1rvaGF7YSVdkFBQcHa5i/yDHnZZida3AAAAABJRU5ErkJggg==>

[image26]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGMAAAAaCAYAAACjFuKcAAADYklEQVR4Xu2YT6gPURTHj1D+JlFS9Eo2KCnZiJQiFAoLC8qOBRuKwuJJNjaknoUNFgphxYKUX5SUBQsif/IohCwsrBDn053RzPn97vzu/HkzbzGf+jbcc9/vnrnn3nPPHZGWlpaWlpoYqxpSPVVdSJtq47LqkeqkOH8qZ4zquOqwNYwiBlV/xfnai4mqbaqzqh2qyWlzbtaq1tvGBPiBP4OmvTRLVT9VF61hFIFvvLyPZ6onqo2qW6oPqiWpHv1hgmeo1okb61Da3AV9GGuCNRRlvOqquB++JP6V1yRTVB3xBwP7K9VA9P9pqvuqF/97hPFe3Bgvo2dIMDrixq+Ex6pN4lIU/56eNpeCwDIhH1XnIh0Tl04WJPr1IysY41TXVFtM+wrVLyk+UY0Eg13B7mBgVsbstLkw/OZR1Ropf9BlBWOm6rm49JQkTr3zTHsotQdjUSRgZX2W4s4nYUccVN21hoJkBYMd9l38weBZhFqDwYolLcXE25pnWU5ItVXGLHHpjkm3xJPuC4ZtDyUkGF9Ur1VzrSEPHHCs2mSexXkcyHI+tGq4oVpmG0twXZxvB6xB/JPuaw8lJBj4Qz/8K8wecdXHTnGHKWJgfnh3ol8SAshlZ5I19OCe6qu48tInxu7HStU7cQtnobHF+Cbd1x5KSDAAv/APP/E3F+Tz09EzCVUUDpwx7UWgKKBCq4qtqj+qO9Ygzm/SrZ30Og5w/MEv/CsE0euV4ziEcKDXxW++uC0ZehhuV10RV1FVwVTVA3FnmoVFdV66dzS3Z96n6OEaEgz8wS/8yw2ppmMbI6jXcYAtZz8lUKJuUL017T6YgNuqXaa9KFnVFHBjtjt6r3T3/xYphJBg0KcjOQM+IO4PY3FJYvJj2ydjR/H2jg9tVh71fB7miPtEsVm602Ie+gUDHqp+qPar3oirvuzFksvnb/HfezrSPQ+oV7aAQsEoCxPJpxKCmJflqpvS/YL7kp36EBIMJpg0yg2fp2/Cq6zyGglGfMu1ebkuQoIRSpXv0Egw4hRFUJqi31fbEPgqMGQbS5CVwkYM0lPTX3QplSkjF1tDDk6JK2KqAD/wp8oS3gtOE3VyO5c0ytUmYSGsFrcah1VHUtb6YNxhcX6sSptGDl6ew5fbeZM7oqWlpaWlPv4BfOrVF5yYmbcAAAAASUVORK5CYII=>

[image27]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA8AAAAbCAYAAACjkdXHAAAA+klEQVR4Xu2TMQtBURiGP8WCMlAWZVMGGZhkNFEG+Q92i8VqkfgVJv6ATfwDZWNgl5LNwPt2Lk6fcGSTp57u6f3O7X7nu/eK/PmKBOzq0JUOPOvQhTCcwpPKnaiKeepYF15Rg1t4FHMzrwuYsTc9IwnrcCWm5RaswJC96R07uIZxXXCBLTd1aBGBExjUBU6aLRd1wcIP8zokafmiZQ6JXulDn7fm22jDspiHPMAbuYmUxHymVwYwIOZIOSu/wde1gXM4UzXOgzRgzC7Y8LxRubdrw2woZmgfw5b3OnSFLS916AJb5c/Ctp3JwgMcwZ6YiTvDIRVgylv/ChcoDiQeAYnXLQAAAABJRU5ErkJggg==>

[image28]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAbCAYAAAB836/YAAABJElEQVR4XmNgGAXUBrpA7IAuSClwBeL3QKyDLkEu4Afi/0Ccgy5BCQAZeACIedDEyQY7GCCGNqCJkw3SGSAGXkWXIBcoAfFzIP6HLkEuYAHi5QwQVzKiyZEFXgHxFCB+CMQRaHLIAJS8QGkXL7AC4llAzM4AMRTkUpCLsYEwIGZFF0QGLgwQW2GKPBggYQkKU5LBLyC+BcTyaOKgcAQlI04oHyQ/B4hNGSCpASsIYoC4zAxdAgi+QrExlN8HxCpALAjE5TBFyIAbiPcwQAzFBjYzQFzZzACJcZBBIFqTARJEWIEAugASAIWnOBALM6AmIZADyApbbADkytPogpQAkHefoQtSAqKB+Am6IKkAFnk1QHwYiGVQpckDoDRox0Agd4wwAACM3CoQKHFNywAAAABJRU5ErkJggg==>

[image29]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAmwAAAA3CAYAAACxQxY4AAADUklEQVR4Xu3dTahVVRQH8B2V9CFYVEY48AMnYZFQSkEzMZzUJMEQ1KFTmxRUgyYN/KDCiSAGVoMmDZQIKhpIAwlq0CBp5EQEESlHguBA9+Kc895x+e591+s97xn+fvDn7bP2fffy3mix9zn7lgIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADAaM+k6+fS9TQeKqPf59FcAABgtHdrNvWun6pZ07ue1ttl/Pt8kAsAANxpZc2ZVHuk/flCaZq3bjXstXYc9WdL87sx7sS4S7zum97c6d6480O5c2UPAIDk95rnU+3Nmn01H7bXp3rjj2qO1rxY81LN6219IdGwxfs8XJrmLltV808uAgBwu0u50PNnze6a/0qzEnag5ot27r2ak+14Ep/lQutiGb9tCgDwwPs+FwYQ26cbcrH1dc3+XAQAeFBsrtlb83HNutun5nRbnUOKLdRR4vOjaQMAGNRbpTnCYrkcT4ntynhw4I92/uky3zT9W/NkOw4LNWyHB05ffP6ZVAMAmLmNNTdzcZnFQwHdVmPc8N89+Zkt1LDdrRW5cBessAEAS+ZGzRu5uMzOleasszhn7Via60zaLO2oebw096Otq/m7rcdTonHe2vbSPDwQDyd83hsv5kSZTdMIALCoaDr+j0dUnM+FEaLhO9uOt9W8X5pt4LWladii8YuVvBjH/6IbLyaayldyEQBgKLEtuj4XR9hTc2FMDs2/dE4cRhuf0R1k+2NvblpXax7LxQVsaX/GGW3RqP3Vm4vGLO6bi9W3+Pu/7I3HiW3a+HwAgCXxW813NT+lehxM+0SqTSve+0ppHiy4XGbzoMOm0jRYi4kmsdviXN2f6IlvRQhxUG43HufTmldzEQBgCLGtGFuDcXL/pA8fxNc37RyTl+dfOudaaVa4Zu2X0tyfttR+zQUAgCHElt7WdN35pEx+j9gkYrWuvyL1c298r77NhYHFyh4AwOCiYdqVau+U5js2u/vC4qb6WYkt0Os1X9UcTHMAAEwhbrz3tUsAAPexaNaiaQMA4D4T54rFluWRPAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADM0i1NVGnTjIPGBAAAAABJRU5ErkJggg==>

[image30]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEEAAAAaCAYAAADovjFxAAACNElEQVR4Xu2Wz0sVURTHj6RgGSn9MIKgkhSEIiIiiFoGtahNUa1zYbRXN+E/ELiIsGgTFUGU0CLSoGghLtpF0KoIUvoBhbapSCXq++XckftOc2fevDdYr+4HPsybc+a9ue/MveeOSCTyr7PLBv5H9thAI7ETfoELcMYdk88/3efbsCn5QoCGLgLpgjdhG7wFu118P5yEh+FluNLF02j4ImyC1+Fqd+R5wjA8Dc/DIS9+BF71vGfOz8EVS1cvA2vgN/hEdACjsA+ekOynl5BVhAPwSiDn80dnQi98AdfZRAGyinAUXoTr4R13TKPmIvBmc84fsKcyncsW+ByesomCZBWh38l7XJBwgyxchOOif/qaibMbXxK9EddYa2X6N57CbTZYA34RHsP3Urk7PJT89b3WBvL4BD/DHSb+Fb4WHRSbUR43JPxkiuAXgbsDd4UJ0Zn2AG5durIkOGhWeAw2m9w0nIUn4UaTSyPZz7OsplekLYd9sF101rIxlgrXzqJo17WwCCzQfZsIMC468HpJKwLh7+8WnRHbXawU+I79Fm62CdEicKvbaxMBHsGDNlgDoSIcgndFt1kuC86MUuAW80H0Lc2Hy2RetC9U22mPwSm4wSYKEioCxzQCz8JB0WXR4nJ1MwC/w2fwHXwp2gf4Hs91zObItVgN7NpnRAfL1928HcXC6Z7sArw3jzx/5V3DYjP30eVWebm64GBZ8Y5AvCh8Um9EB+nb6V0TiUQikUgk8lfzC30jd5pkaUgSAAAAAElFTkSuQmCC>

[image31]: <data:image/png;base64,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>