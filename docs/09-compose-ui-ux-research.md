# **UI/UX Architectural Specification and Design System Framework for a Compose Multiplatform Driving Examination Application**

## **Unified UI/UX Principles for Educational Driver Preparation Platforms**

### **Visual Design Language and Cognitive Architecture**

To build an effective driving examination mobile application, the user interface must be engineered to alleviate test-induced anxiety while optimizing cognitive processing1.Sighted users who are preparing for high-stakes examinations experience elevated levels of stress, which can directly degrade working memory capacity and visual search efficiency3. The visual design language of this application addresses these factors by establishing a clean, light, and friendly educational environment2. Soft, welcoming color palettes are combined with rounded card layouts and clear typography to create a clean, accessible layout that helps lower cognitive friction1.  
By organizing informational layouts into structural visual hierarchies, the system reduces cognitive load6. The presentation of complex exam content, such as multi-choice question stems and corresponding illustrations of traffic situations, is broken down into easily readable steps7. This prevents visual crowding and helps the user focus entirely on the core educational task1.

### **Spatial Layout and Ergonomic Touch Targets**

The interface layout is designed to prioritize phone-first interaction models1. Standard mobile ergonomic guidelines focus on the dynamic "thumb zone," which represents the region of a smartphone screen that can be easily accessed with one hand1. Interactive components are positioned primarily within the lower two-thirds of the display viewport to accommodate the physical limitations of single-handed operation1. Display-only elements, such as static status banners or question text, are placed in the upper portion of the screen1.  
The design of touch targets is guided by Fitts’s Law, which defines the time (![][image1]) required to acquire an on-screen target of width (![][image2]) at a given distance (![][image3])3:  
![][image4]  
In this equation, ![][image5] and ![][image6] represent empirical regression coefficients determined by the input device and user context3. To minimize target acquisition times (![][image1]) and reduce selection errors, touch-sensitive UI components—such as option selection buttons—must maximize their physical width (![][image2]) and utilize accessible heights of at least ![][image7] on Android (![][image8] on iOS)1.

| UX/UI Principle | Technical Implementation | Cognitive Objective |
| :---- | :---- | :---- |
| **Cognitive Load Reduction** | Divide long study materials into progressive disclosure layouts7. | Minimizes text anxiety and maintains focus during long study blocks1. |
| **Ergonomic Layouts** | Position primary controls in the lower two-thirds of the screen1. | Allows easy, single-thumb access on modern, tall smartphone screens1. |
| **Fitts's Law Touch Scaling** | Ensure touch targets are at least ![][image7] (![][image8])1. | Reduces input errors, especially under high stress or motor fatigue1. |
| **Redundant Visual Cues** | Pair color changes with icons, text weights, and borders6. | Ensures color-blind users can easily understand success and error states4. |
| **Responsive Containers** | Avoid fixed layout containers; use dynamic scroll views7. | Prevents content clipping when using large system font scale settings7. |

## **Screen-by-Screen User Experience and Navigation Architecture**

The user flow is organized into ten core screen layouts, which are managed within a unified, type-safe navigation graph16.

### **1\. Onboarding Screen**

The onboarding experience is designed to be streamlined, helping users quickly configure their study profile19. Sighted users are immediately prompted to select their geographic region (e.g., their specific US state) to ensure study questions sync with the correct local driving manuals5. The screen layout uses a clean vertical list structure with a fixed primary proceed button23. Edge-to-edge window configurations prevent the system navigation bar or status bar from clipping the onboarding content23.

### **2\. Home Screen**

The home screen serves as the main user dashboard25. It displays topic mastery cards, study streak visual indicators, and a primary call-to-action to begin a study session or an exam2. Crucially, the navigation controls and main call-to-action buttons are kept separate from scrollable containers, avoiding interaction conflicts with system-level edge gestures1.

### **3\. License Class List Screen**

This layout features large, rounded cards representing vehicle classes, such as Car, Motorcycle, Commercial Driver’s License (CDL), and Moped25. Each license card has an explicit accessibility role mapped in the semantics tree, allowing screen readers to announce them as selectable toggle containers12. The layout uses a responsive grid system, preventing the cards from stretching awkwardly on larger mobile viewports1.

### **4\. Study Screen**

The study screen is designed as an untimed, highly interactive learning flow25. The question stem is rendered in large, highly readable typography, with answer choices structured as selectable buttons underneath7. Upon selecting an answer, the user receives immediate feedback22. Correct answers are highlighted with a distinct border shape and a checkmark symbol, while incorrect answers display a warning emblem6. A bottom drawer panel slides up to reveal memory hooks and explanations, allowing users to learn from their mistakes2.

### **5\. Exam Screen**

The exam screen replicates the conditions of a real written driving test21. A countdown timer is displayed in the top bar, and immediate feedback on answer selections is disabled to maintain testing integrity21. To prevent accidental navigation loss, the exam screen implements a custom back-stack confirmation dialog that intercepts the system back gesture, ensuring users do not forfeit active exam progress17.

### **6\. Result Screen**

The result screen provides a clear pass/fail evaluation once an exam is completed21. A large circular badge displays the scoring percentage24. Passing scores are celebrated with a soft, positive theme, while failing marks are displayed with clear, constructive feedback indicators6. The primary call-to-action prompts the user to review their incorrect answers, while a secondary action returns them to the home dashboard5.

### **7\. Review Screen**

This view automatically builds a target revision flow based on the user's historical study errors5. The interface matches the study screen layout but focuses entirely on targeted practice, helping users master the specific topics they struggled with22. Correct answers here dynamically update the user's overall mastery metrics5.

### **8\. History Screen**

The history screen presents a detailed timeline of the user's past exam performances25. Each historical entry displays the exam date, scoring breakdown, and overall result28. Tap actions on these entries expand details, allowing users to review their precise answer selections and compare performance trends across categories28.

### **9\. Settings Screen**

The settings screen provides profile controls, enabling users to reselect their targeted state handbook, adjust daily notifications, and scale system typography2. Setting controls leverage native OS toggles and switches to ensure a cohesive user experience31.

### **10\. Paywall Screen (Deferred)**

Designed with placeholders to support premium model upgrades20. It clearly outlines paid features—such as deep analytics, priority study recommendations, and ad removal—with high-contrast typography, while deferring transaction processing to native multiplatform store systems20.

| Target Screen | Primary Navigation Gestures | Layout Constraints | Dynamic Window Inset Behavior |
| :---- | :---- | :---- | :---- |
| **Onboarding** | Horizontal transition swipe, proceed action click. | Screen-locked container with no nested scroll areas1. | Consumes standard statusBarsPadding() and navigationBarsPadding()32. |
| **Home** | Deep-link routing clicks18; horizontal card list drag1. | Adaptive split-pane grid support on tablet viewports11. | Uses safeDrawingPadding() globally23. |
| **License Class** | Vertical scroll, card component click. | Responsive multi-column layout using relative weights1. | Pads top bars and uses standard navigation bar boundaries24. |
| **Study** | Left-to-right swipe for page changes1. | Scrollable vertical viewport with flexible spacers7. | Adjusts with imePadding() to handle active search filters32. |
| **Exam** | Click to select; custom back gesture interception block29. | Screen-locked, single-question layout with sticky top timer21. | Ignores native keyboard safe areas during active testing23. |
| **Result** | Scroll down to review breakdown cards. | Scrollable vertical canvas with fixed bottom action buttons7. | Bottom buttons apply standard system navigation offsets24. |
| **Review** | Left-to-right swipe; swipe to dismiss completed cards36. | Vertically scrollable question sheet with relative sizing7. | Employs custom target consumeWindowInsets32. |
| **History** | Vertical scroll; item click triggers detail expansion28. | Scrollable vertical timeline using native list containers9. | Keeps scrollable bounds behind system bars, applying offsets23. |
| **Settings** | Native system configuration clicks31. | Traditional form list wrapped in dynamic scroll views9. | Offsets top status margins and bottom touch interfaces24. |
| **Paywall** | Click to purchase, click to close. | Structured single-pane graphic layout11. | Uses complete edge-to-edge drawing without bottom padding23. |

## **Design System Component List and Theme Tokens**

The application’s design system is structured using a clean, light, and approachable theme37. To ensure scalability, the architecture uses a three-tier design token model37. Foundation-level variables (Reference Tokens) map to functional layout roles (System/Semantic Tokens) and specific UI parameters (Component Tokens)37.

### **Core Component List**

* **Cards**:  
  * LicenseSelectionCard: Rounded grid item featuring vehicle icons and vehicle class titles25.  
  * QuestionCard: Primary content block displaying the active question text7.  
  * ExplanationCard: Expandable informative card revealing correct answers and explanations22.  
* **Buttons**:  
  * AnswerOptionButton: Redundant option selector with clear state indications6.  
  * PrimaryActionButton: Large, rounded button positioned in the primary thumb zone1.  
  * DismissActionButton: Secondary button used to close alerts or discard actions36.  
* **Top Bars**:  
  * GlobalAppBar: Standard top bar with screen titles and dynamic navigation actions35.  
  * ExamTimerBar: specialized header display featuring active timers and progress metrics21.  
* **Bottom Sheets**:  
  * FeedbackExplanationSheet: Bottom sheet displaying visual explanations during study25.  
* **Dialogs**:  
  * ExamInterceptionDialog: High-contrast warning confirmation displayed when leaving exams29.  
* **Progress Indicators**:  
  * LinearStageTracker: Linear bar displaying current exam progression24.  
  * TopicMasteryGauge: Radial progress indicators showing completion rates for study topics5.  
* **Result Badges**:  
  * ExamStatusBadge: A high-contrast visual stamp displaying pass/fail indicators21.

### **Semantic Design Tokens Specification**

                      DESIGN TOKEN HIERARCHY  
┌──────────────────────────────────────────────────────────────┐  
│  Reference Tokens:                                           │  
│  \- color.palette.blue.500 \= \#1E50DE                          │  
│  \- spacing.scale.md       \= 16.dp                            │  
├──────────────────────────────────────────────────────────────┤  
│  System/Semantic Tokens:                                     │  
│  \- color.system.primary    \= color.palette.blue.500          │  
│  \- spacing.system.standard \= spacing.scale.md                │  
├──────────────────────────────────────────────────────────────┤  
│  Component Tokens:                                           │  
│  \- button.primary.fill     \= color.system.primary            │  
│  \- card.question.padding   \= spacing.system.standard         │  
└──────────────────────────────────────────────────────────────┘

#### **Color System Tokens (Light Palette, prepared for future Dark Mode)**

37

* color.system.background \= Neutral.LightGray (HEX \#F9F9FB)  
* color.system.surface \= Neutral.White (HEX \#FFFFFF)  
* color.system.primary \= Blue.Horizon (HEX \#1E50DE) (Primary brand color for buttons and selection states)44  
* color.system.onPrimary \= Neutral.White (HEX \#FFFFFF) (Text overlay for primary elements)44  
* color.system.secondary \= Teal.Accent (HEX \#007F8A) (Highlight and secondary actions)44  
* color.system.onSecondary \= Neutral.White (HEX \#FFFFFF)  
* color.system.textPrimary \= Neutral.SlateDark (HEX \#15161A) (Standard contrast reading typography)  
* color.system.textSecondary \= Neutral.SlateMuted (HEX \#4F525D) (Secondary body text and labels)  
* color.system.accentCorrect \= Lime.Emerald (HEX \#2D7A1E) (Correct indicators)6  
* color.system.accentIncorrect \= Red.Warm (HEX \#C92A2A) (Incorrect and alert states)6  
* color.system.outline \= Neutral.BorderGrey (HEX \#E5E7EB) (Standard visual borders and dividers)44

#### **Spacing, Dimensions, and Elevation Tokens**

10

* spacing.system.xs \= 4.dp  
* spacing.system.sm \= 8.dp  
* spacing.system.md \= 16.dp (Standard content padding)  
* spacing.system.lg \= 24.dp  
* spacing.system.xl \= 32.dp  
* radius.system.sm \= 8.dp (Borders and simple tags)  
* radius.system.md \= 16.dp (Buttons and option cards)10  
* radius.system.lg \= 24.dp (Main dialogs and selection plates)10  
* elevation.system.none \= 0.dp  
* elevation.system.low \= 2.dp (Default elevated cards to create visual depth)44

#### **Typographic Scales (Supports iOS/Android Dynamic Font Scaling)**

9

* typography.system.display \= FontFamily.Rounded, Size 32.sp, Weight Bold, LineHeight 1.15  
  \[cite: 46, 48\]  
* typography.system.headline \= FontFamily.Rounded, Size 24.sp, Weight SemiBold, LineHeight 1.2  
  \[cite: 8, 46\]  
* typography.system.titleLarge \= FontFamily.System, Size 20.sp, Weight Medium, LineHeight 1.25  
  \[cite: 8, 46\]  
* typography.system.bodyLarge \= FontFamily.System, Size 16.sp, Weight Normal, LineHeight 1.4 (Core text readability)8  
* typography.system.bodyMedium \= FontFamily.System, Size 14.sp, Weight Normal, LineHeight 1.4  
  \[cite: 8, 46\]  
* typography.system.labelLarge \= FontFamily.System, Size 14.sp, Weight SemiBold, LineHeight 1.15  
  \[cite: 46\]

## **Cross-Platform Behavior and OS Interoperability**

Developing with Compose Multiplatform requires careful handling of platform-specific layouts and gestures to ensure the app feels native on both iOS and Android18.

┌─────────────────────────────────────────────────────────────┐  
│ Android Native Edge-To-Edge drawing                         │  
│ \- Android target SDK 35 mandates drawing behind bars│  
│ \- Uses WindowInsets.safeDrawingPadding()  │  
├─────────────────────────────────────────────────────────────┤  
│ iOS SwiftUI Wrapper with full safeArea ignores              │  
│ \- Swift: ComposeView().ignoresSafeArea()      │  
│ \- Re-injects bounds using WindowInsets.safeDrawing│  
└─────────────────────────────────────────────────────────────┘

### **Edge-to-Edge and Safe Area Insets Integration**

To avoid layout issues on tall displays, the application must draw underneath system bars and programmatically inject safe area boundaries23. On Android 15 (API level 35\) and higher, targeted apps draw behind system bars by default24. Developers can configure this edge-to-edge drawing using the platform-specific Activity configuration23:

Kotlin  
// MainActivity.kt  
override fun onCreate(savedInstanceState: Bundle?) {  
    super.onCreate(savedInstanceState)  
    enableEdgeToEdge()  
    setContent {  
        App()  
    }  
}

On iOS, the application view must ignore native safe areas in Swift to allow Compose to manage drawing limits across the layout notch and home indicator region23:

Swift  
// ContentView.swift  
struct ContentView: View {  
    var body: some View {  
        ComposeView()  
            .ignoresSafeArea(edges: .all)  
            .ignoresSafeArea(.keyboard)  
    }  
}

Once native offsets are cleared, layouts can use Compose's standard safeDrawingPadding() or safeContentPadding() modifiers to prevent content from overlapping with system bars23.

### **Status Bar and Navigation Bar Controls**

System bars must remain clear and readable49. Sighted users should experience high contrast between system bar icons and background layouts6.

* **Android**: Transparent system bars are configured using enableEdgeToEdge()23. Icon visibility changes dynamically based on dark and light layouts, ensuring the status bar remains legible against light backgrounds24.  
* **iOS**: Status bar colors are configured in the UIKit host wrapper using dynamic preferences, keeping text readable throughout different layout transitions23.

### **Gestural Back-Stack Control and Platform Navigation Conflict**

Platform differences in navigation can lead to system-level conflicts17:

* **Android Predictive Back**: Android 15 mandates predictive back handling17. To ensure smooth back-page gestures, the navigation system must integrate with the native Android back-stack interface17.  
* **iOS Interactive Swipe-Back Gesture**: On iOS, users expect swipe-back gestures to navigate between pages16. When custom animations or blocked page handlers (such as leaving active exams) are defined in shared code, swipe gestures on iOS can bypass target validation, resulting in visual page-snapping bugs29.

Using a structured multiplatform navigation library, such as Decompose, helps manage these transitions seamlessly16. It uses native-like platform animations (predictiveBackAnimation) in shared code, ensuring consistent, robust back gestural behaviors on both iOS and Android17.

### **Dynamic Type and Font Scaling Integrations**

To support system font scaling and Dynamic Type on iOS, Compose Multiplatform must dynamically adapt to text changes45. Using scale-independent pixels (sp) in text sizes automatically scales typography based on system settings9.  
However, text scaling can alter the height of layout elements, causing content to clip or overlap7. To prevent this, text boxes should avoid fixed height dimensions, allowing containers to scale naturally7. The core layout should wrap content inside scrollable viewports (ScrollView or LazyColumn) to ensure all scaled text remains readable7.  
For elements that must remain a constant size (such as bottom sheets or navigation bars), developers can reverse system font scaling using LocalDensity51:

Kotlin  
// Reverse system scale mapping for layout-critical elements  
val fontScale \= LocalDensity.current.fontScale  
val fixedTextSize \= 14\.sp / fontScale

### **Native Feel Customizations**

To ensure the multiplatform app feels native, scrolling physics are configured to match the host platform's expectations18.

* **iOS Scroll Physics**: On iOS, list layouts use elastic bouncing physics18.  
* **Android Scroll Physics**: Android targets rely on native surface friction boundaries31.  
* **System Assistive Input Devices**: To accommodate assistive touch devices, buttons and toggles are designed to adapt to system features like AssistiveTouch and Full Keyboard Access53.  
* **iOS Large Content Viewer**: Under extreme text scaling, long pressing on navigation icons reveals a larger text preview to ensure readability52.

## **Technical Compose Component Architecture & State Management**

The application uses an architectural model that decouples state management from rendering, ensuring a clean separation of concerns17. This approach allows screens to remain stateless, making them highly testable across different platform targets16.

                         SCREEN MODEL STATEFLOW  
┌───────────────────────────────────────────────────────────────────────┐  
│  Stateful ScreenModel Container (e.g. Voyager / Decompose VM)        │  
│  \- Exposes StateFlow\<ExamUIState\>                                    │  
│  \- Receives intent triggers via handleAction(intent)                  │  
└───────────────────────────────────────────────────────────────────────┘  
                                  │  
                       Flows Immutable UIState  
                                  ▼  
┌───────────────────────────────────────────────────────────────────────┐  
│  Stateless Layout Composable                                          │  
│  \- Receives state parameters; exposes onClick lambda closures         │  
└───────────────────────────────────────────────────────────────────────┘

The system employs type-safe route configurations16:

Kotlin  
@Serializable  
sealed interface TargetRoute {  
    @Serializable data object Onboarding : TargetRoute  
    @Serializable data object Home : TargetRoute  
    @Serializable data object LicenseSelection : TargetRoute  
    @Serializable data class StudySession(val licenseClassId: String) : TargetRoute  
    @Serializable data class ActiveExam(val examSessionId: String) : TargetRoute  
    @Serializable data class ExamResult(val resultReportId: String) : TargetRoute  
}

The UI utilizes a state-hoisting pattern to keep layout components declarative17. Below are technical implementation examples of key UI components.

### **Implementation 1: LicenseSelectionCard**

This component displays license categories using adaptive cards and semantic annotations for screen reader navigation12.

Kotlin  
package com.drivingapp.designsystem.components

import androidx.compose.foundation.BorderStroke  
import androidx.compose.foundation.clickable  
import androidx.compose.foundation.layout.\*  
import androidx.compose.foundation.shape.RoundedCornerShape  
import androidx.compose.material3.Card  
import androidx.compose.material3.CardDefaults  
import androidx.compose.material3.Icon  
import androidx.compose.material3.Text  
import androidx.compose.runtime.Composable  
import androidx.compose.ui.Alignment  
import androidx.compose.ui.Modifier  
import androidx.compose.ui.graphics.Color  
import androidx.compose.ui.graphics.vector.ImageVector  
import androidx.compose.ui.semantics.Role  
import androidx.compose.ui.semantics.role  
import androidx.compose.ui.semantics.semantics  
import androidx.compose.ui.semantics.stateDescription  
import androidx.compose.ui.unit.dp  
import androidx.compose.ui.unit.sp

@Composable  
fun LicenseSelectionCard(  
    classTitle: String,  
    classDetail: String,  
    vectorIcon: ImageVector,  
    isSelected: Boolean,  
    onSelectAction: () \-\> Unit,  
    modifier: Modifier \= Modifier  
) {  
    val containerColor \= if (isSelected) Color(0xFFE8F0FE) else Color(0xFFFFFFFF)  
    val boundaryColor \= if (isSelected) Color(0xFF1E50DE) else Color(0xFFE5E7EB)  
    val strokeThickness \= if (isSelected) 3\.dp else 1\.dp

    Card(  
        shape \= RoundedCornerShape(24\.dp),  
        colors \= CardDefaults.cardColors(containerColor \= containerColor),  
        border \= BorderStroke(width \= strokeThickness, color \= boundaryColor),  
        modifier \= modifier  
            .fillMaxWidth()  
            .height(112\.dp)  
            .semantics {  
                role \= Role.Button  
                stateDescription \= if (isSelected) "Selected $classTitle Card" else "Unselected $classTitle option"  
            }  
            .clickable(onClick \= onSelectAction)  
    ) {  
        Row(  
            verticalAlignment \= Alignment.CenterVertically,  
            modifier \= Modifier  
                .fillMaxSize()  
                .padding(16\.dp)  
        ) {  
            Icon(  
                imageVector \= vectorIcon,  
                contentDescription \= null,  
                tint \= if (isSelected) Color(0xFF1E50DE) else Color(0xFF4F525D),  
                modifier \= Modifier.size(48\.dp)  
            )  
            Spacer(modifier \= Modifier.width(16\.dp))  
            Column(  
                verticalArrangement \= Arrangement.Center,  
                modifier \= Modifier.fillMaxHeight()  
            ) {  
                Text(  
                    text \= classTitle,  
                    fontSize \= 18\.sp,  
                    color \= if (isSelected) Color(0xFF1E50DE) else Color(0xFF15161A)  
                )  
                Spacer(modifier \= Modifier.height(4\.dp))  
                Text(  
                    text \= classDetail,  
                    fontSize \= 13\.sp,  
                    color \= Color(0xFF4F525D)  
                )  
            }  
        }  
    }  
}

### **Implementation 2: AnswerOptionButton**

This component provides clear correct and incorrect states, using icons and thick borders rather than color alone to communicate answers6.

Kotlin  
package com.drivingapp.designsystem.components

import androidx.compose.foundation.BorderStroke  
import androidx.compose.foundation.clickable  
import androidx.compose.foundation.layout.\*  
import androidx.compose.foundation.shape.RoundedCornerShape  
import androidx.compose.material3.Card  
import androidx.compose.material3.CardDefaults  
import androidx.compose.material3.Icon  
import androidx.compose.material3.Text  
import androidx.compose.material3.icons.Icons  
import androidx.compose.material3.icons.filled.Cancel  
import androidx.compose.material3.icons.filled.CheckCircle  
import androidx.compose.material3.icons.filled.RadioButtonChecked  
import androidx.compose.material3.icons.filled.RadioButtonUnchecked  
import androidx.compose.runtime.Composable  
import androidx.compose.ui.Alignment  
import androidx.compose.ui.Modifier  
import androidx.compose.ui.graphics.Color  
import androidx.compose.ui.semantics.Role  
import androidx.compose.ui.semantics.role  
import androidx.compose.ui.semantics.semantics  
import androidx.compose.ui.semantics.stateDescription  
import androidx.compose.ui.unit.dp  
import androidx.compose.ui.unit.sp

enum class OptionEvaluationState {  
    IDLE, TARGET\_SELECTED, RESOLVED\_CORRECT, RESOLVED\_INCORRECT  
}

@Composable  
fun AnswerOptionButton(  
    optionIndexLabel: String,  
    optionContent: String,  
    evaluation: OptionEvaluationState,  
    onClickTrigger: () \-\> Unit,  
    modifier: Modifier \= Modifier  
) {  
    val bgFill \= when (evaluation) {  
        OptionEvaluationState.IDLE \-\> Color(0xFFFFFFFF)  
        OptionEvaluationState.TARGET\_SELECTED \-\> Color(0xFFF3F4F6)  
        OptionEvaluationState.RESOLVED\_CORRECT \-\> Color(0xFFECFDF5)  
        OptionEvaluationState.RESOLVED\_INCORRECT \-\> Color(0xFFFEF2F2)  
    }

    val borderOutline \= when (evaluation) {  
        OptionEvaluationState.IDLE \-\> Color(0xFFE5E7EB)  
        OptionEvaluationState.TARGET\_SELECTED \-\> Color(0xFF1E50DE)  
        OptionEvaluationState.RESOLVED\_CORRECT \-\> Color(0xFF2D7A1E)  
        OptionEvaluationState.RESOLVED\_INCORRECT \-\> Color(0xFFC92A2A)  
    }

    val outlineThickness \= if (evaluation \== OptionEvaluationState.IDLE) 1\.dp else 3\.dp

    Card(  
        shape \= RoundedCornerShape(16\.dp),  
        colors \= CardDefaults.cardColors(containerColor \= bgFill),  
        border \= BorderStroke(width \= outlineThickness, color \= borderOutline),  
        modifier \= modifier  
            .fillMaxWidth()  
            .semantics {  
                role \= Role.RadioButton  
                stateDescription \= when (evaluation) {  
                    OptionEvaluationState.IDLE \-\> "Unselected option $optionIndexLabel"  
                    OptionEvaluationState.TARGET\_SELECTED \-\> "Selected option $optionIndexLabel"  
                    OptionEvaluationState.RESOLVED\_CORRECT \-\> "Correct option $optionIndexLabel"  
                    OptionEvaluationState.RESOLVED\_INCORRECT \-\> "Incorrect option $optionIndexLabel"  
                }  
            }  
            .clickable(onClick \= onClickTrigger)  
    ) {  
        Row(  
            verticalAlignment \= Alignment.CenterVertically,  
            modifier \= Modifier  
                .fillMaxWidth()  
                .padding(16\.dp)  
        ) {  
            val stateIcon \= when (evaluation) {  
                OptionEvaluationState.IDLE \-\> Icons.Default.RadioButtonUnchecked  
                OptionEvaluationState.TARGET\_SELECTED \-\> Icons.Default.RadioButtonChecked  
                OptionEvaluationState.RESOLVED\_CORRECT \-\> Icons.Default.CheckCircle  
                OptionEvaluationState.RESOLVED\_INCORRECT \-\> Icons.Default.Cancel  
            }

            val stateIconTint \= when (evaluation) {  
                OptionEvaluationState.IDLE \-\> Color(0xFF9CA3AF)  
                OptionEvaluationState.TARGET\_SELECTED \-\> Color(0xFF1E50DE)  
                OptionEvaluationState.RESOLVED\_CORRECT \-\> Color(0xFF2D7A1E)  
                OptionEvaluationState.RESOLVED\_INCORRECT \-\> Color(0xFFC92A2A)  
            }

            Icon(  
                imageVector \= stateIcon,  
                contentDescription \= null,  
                tint \= stateIconTint,  
                modifier \= Modifier.size(24\.dp)  
            )  
            Spacer(modifier \= Modifier.width(16\.dp))  
            Text(  
                text \= "$optionIndexLabel. $optionContent",  
                fontSize \= 16\.sp,  
                color \= Color(0xFF15161A)  
            )  
        }  
    }  
}

## **Accessibility Compliance Verification Framework**

Sighted users with visual color impairments require accessible design practices, such as maintaining strong contrast levels and using layout shapes rather than color changes alone to convey states6. The application undergoes formal contrast verification using the relative luminance formula30:  
![][image9]  
In this formula, ![][image10] and ![][image11] represent the relative luminance values of the foreground and background elements, calculated from their RGB coordinates30. Sighted users with Deuteranopia or Protanopia cannot differentiate between standard red and green elements6. Therefore, visual states like correct and incorrect answers are verified using shape, typography, and clear contrast ratios of at least 4.5:16.

                     ACCESSIBILITY LOGIC FLOW  
┌───────────────────────┐       Yes      ┌─────────────────────────┐  
│ Check Contrast ratio  ├───────────────\>│ Validates 4.5:1 / 3:1   │  
│ of standard elements  │                │ for normal/large text   │  
└───────────────────────┘                └─────────────────────────┘  
                                                      │  
                                                      ▼  
┌───────────────────────┐       Yes      ┌─────────────────────────┐  
│ Redundant indicators  ├───────────────\>│ Option button uses check│  
│ in use (no color only)│                │ and cross icon layouts  │  
└───────────────────────┘                └─────────────────────────┘  
                                                      │  
                                                      ▼  
┌───────────────────────┐       Yes      ┌─────────────────────────┐  
│ Interactive targets   ├───────────────\>│ Standard touch minimums │  
│ meet physical size    │                │ of 48x48 dp verified    │ \[cite: 3, 12\]  
└───────────────────────┘                └─────────────────────────┘

### **Accessibility Verification Checklist**

| Design Checklist Target | Compliance Standard | Specific Technical Verification Method |
| :---- | :---- | :---- |
| **Normal Body Typography** | WCAG 1.4.3 Level AA6 | Maintain contrast ratios above 4.5:1 against surrounding background elements1. |
| **Display Headers & Icons** | WCAG 1.4.11 Non-text Contrast30 | Maintain contrast ratios above 3.0:1 for structural lines, borders, and status icons30. |
| **Tap Target Bounds** | Mobile touch target minimums1 | Set the physical interactive width and height to at least ![][image7] (![][image8])1. |
| **Interactive Role Labels** | WCAG 1.3.1 Info & Relationships27 | Label selectable options with dynamic role properties to announce correct component roles12. |
| **Dynamic Option States** | WCAG 1.4.1 Color as Only Indicator4 | Verify option statuses with icons and bold borders, ensuring users do not rely on color alone6. |
| **Navigational Headings** | WCAG 2.4.6 Descriptive Headings36 | Mark core question panels and section text using the semantic heading() modifier13. |
| **Logical Structural Order** | WCAG 1.3.2 Meaningful Sequence27 | Assign traversal indexing controls (traversalIndex) on overlays to ensure a logical reading order27. |
| **Interactive Alerts** | WCAG 4.1.3 Status Messages36 | Verify dynamic exam timer changes are marked with liveRegion \= LiveRegionMode.Polite36. |
| **Dynamic Font Scaling** | WCAG 1.4.4 Resize Text7 | Wrap layout elements in scrollable containers to ensure expanded text blocks do not clip or drop content7. |
| **Sufficient Touch Area** | Focus and border padding12 | Pad small clickable components to prevent tap areas from overlapping with neighboring buttons12. |

## **Store Optimization and Screenshot Generation Checklist**

To prepare application screenshots for App Store Connect and Google Play console, assets must comply with strict, platform-specific resolution requirements49.

### **Store Screenshot Asset Checklist**

| Store Target Platforms | Required Dimensions | Targeted Aspect Ratio | Native Devices | Color and Alpha Limits |
| :---- | :---- | :---- | :---- | :---- |
| **App Store Connect** | **![][image12]** \[cite: 49, 59, 60\] | ![][image13] Portrait58 | iPhone 16 Pro Max, iPhone 17 Pro Max49 | RGB sRGB, flattened PNG or JPEG, no transparency49. |
| **App Store Connect** | **![][image14]** \[cite: 49, 59, 60\] | ![][image13] Portrait58 | iPhone 15 Plus, iPhone 16 Plus49 | RGB sRGB, flattened PNG or JPEG, no transparency49. |
| **App Store Connect** | **![][image15]** \[cite: 49, 59, 60\] | ![][image16] Portrait Pro60 | iPad Pro 13-inch (M4 Series)49 | RGB sRGB, flattened PNG or JPEG, no transparency49. |
| **Google Play Console** | **![][image17]** \[cite: 62, 63\] | ![][image18] Phone Standard50 | Standard Android (e.g. Pixel devices)63 | 24-bit PNG or JPEG format, no transparency50. |
| **Google Play Console** | **![][image19]** \[cite: 50, 62, 63\] | ![][image20] Feature Banner | Play Catalog Banner63 | 24-bit PNG or JPEG format, no transparency50. |
| **Google Play Console** | **![][image21]** \[cite: 63\] | ![][image22] Tablet Standard | Standard Android 10" Tablets50 | 24-bit PNG or JPEG format, no transparency50. |

### **Graphic Standards and Submission Guidelines**

* **Status Bar Cleanup**: All screenshots must display clean, standard status indicators49. This means showing full battery life, complete network signal bars, and removing any active carrier names49.  
* **True App Representation**: Visual layouts must show genuine, in-app screens, such as active study quizzes or the dashboard, rather than promotional marketing designs59.  
* **No Human Hands in Device Frames**: If device frames are used to contextualize screenshots, the templates must not display human hands holding the devices60.  
* **Metadata Alt-Text Descriptions**: Play Store uploads require explicit alternative text descriptions under 140 characters50. This ensures screen-reader users can understand screenshot details (e.g., "The onboarding region selection screen.")50.  
* **Localization Verification**: All textual descriptions, captions, and screenshots must match the local language settings of each target store listing59.  
* **Automated Screenshot Generation**: The UI testing suite is configured using tools like Fastlane Snapshot to automatically capture and generate clean screenshot sets across various localized targets59.

## **Conclusions**

To deliver a high-quality driving preparation application with Compose Multiplatform, designers and developers must coordinate native device layouts, dynamic text scaling, and clean color-blind accessibility systems6.  
By using a structured design token architecture, the development team can maintain a consistent visual language while ensuring the interface scales predictably37. Designing with clear, non-color-dependent status indicators guarantees that the application is readable and intuitive for all users6. Finally, building layouts that respect system safe areas and dynamic text preferences ensures a polished user experience that feels truly integrated on both modern iOS and Android devices7.

#### **Works cited**

1. Mobile UX/UI Design Assessments: Practice Questions (2026), [https://www.practiceaptitudetests.com/programming/mobile-uxui-design-tests/](https://www.practiceaptitudetests.com/programming/mobile-uxui-design-tests/)  
2. DMV Ready: Practice Test 2026 \- App Store \- Apple, [https://apps.apple.com/us/app/dmv-ready-practice-test-2026/id6764189214](https://apps.apple.com/us/app/dmv-ready-practice-test-2026/id6764189214)  
3. UI/UX Design Principles Assessments: Practice Questions (2026), [https://www.practiceaptitudetests.com/programming/uiux-design-principles-tests/](https://www.practiceaptitudetests.com/programming/uiux-design-principles-tests/)  
4. Understanding Success Criterion 1.4.1: Use of Color | WAI \- W3C, [https://www.w3.org/WAI/WCAG21/Understanding/use-of-color.html](https://www.w3.org/WAI/WCAG21/Understanding/use-of-color.html)  
5. DMV Practice Test 2026 OneDMV \- App Store \- Apple, [https://apps.apple.com/us/app/dmv-practice-test-2026-onedmv/id6451404633](https://apps.apple.com/us/app/dmv-practice-test-2026-onedmv/id6451404633)  
6. How to Design for Color Blindness: Best Practices for Accessible Websites \- AudioEye, [https://www.audioeye.com/post/8-ways-to-design-a-color-blind-friendly-website/](https://www.audioeye.com/post/8-ways-to-design-a-color-blind-friendly-website/)  
7. Guide to Supporting Dynamic Type | 2024.9.18 | Axe DevTools® for Mobile | Deque Docs, [https://docs.deque.com/devtools-mobile/2024.9.18/en/supports-dynamic-type/](https://docs.deque.com/devtools-mobile/2024.9.18/en/supports-dynamic-type/)  
8. Designing for scalable Dynamic Type in iOS for accessibility | by Bang Tran | UX Collective, [https://uxdesign.cc/designing-for-scalable-dynamic-type-in-ios-5d3e2ae554eb](https://uxdesign.cc/designing-for-scalable-dynamic-type-in-ios-5d3e2ae554eb)  
9. Guide for Mobile Text Scaling | 2025.7.2 | Axe DevTools® for Mobile | Deque Docs, [https://docs.deque.com/devtools-mobile/2025.7.2/en/text-scaling/](https://docs.deque.com/devtools-mobile/2025.7.2/en/text-scaling/)  
10. SDP & SSP: The Key to Seamless UI Scaling in Compose Multiplatform | by Chetansinh Rajput | Mobile Innovation Network | Medium, [https://medium.com/mobile-innovation-network/sdp-ssp-the-key-to-seamless-ui-scaling-in-compose-multiplatform-825dc6be79d7](https://medium.com/mobile-innovation-network/sdp-ssp-the-key-to-seamless-ui-scaling-in-compose-multiplatform-825dc6be79d7)  
11. Building a Production-Grade Adaptive Layout System in Kotlin Multiplatform — From Phone to Foldable \- ProAndroidDev, [https://proandroiddev.com/building-a-production-grade-adaptive-layout-system-in-kotlin-multiplatform-from-phone-to-foldable-26a50f3ba2d2](https://proandroiddev.com/building-a-production-grade-adaptive-layout-system-in-kotlin-multiplatform-from-phone-to-foldable-26a50f3ba2d2)  
12. Semantics and Accessibility in Jetpack Compose: Part-2 | by Kaushal Vasava | Medium, [https://medium.com/@KaushalVasava/semantics-and-accessibility-in-jetpack-compose-part-2-be080f39de81](https://medium.com/@KaushalVasava/semantics-and-accessibility-in-jetpack-compose-part-2-be080f39de81)  
13. Improving app accessibility with Jetpack Compose \- Surface Duo Blog, [https://devblogs.microsoft.com/surface-duo/jetpack-compose-accessibility/](https://devblogs.microsoft.com/surface-duo/jetpack-compose-accessibility/)  
14. Colour-Blind Accessibility: Design Colour Accessible Websites \- Recite Me, [https://reciteme.com/news/colour-blind-accessibility/](https://reciteme.com/news/colour-blind-accessibility/)  
15. Designing UI with color blind users in mind \- Secret Stache, [https://www.secretstache.com/blog/designing-for-color-blind-users/](https://www.secretstache.com/blog/designing-for-color-blind-users/)  
16. Navigation in Compose | Kotlin Multiplatform Documentation, [https://kotlinlang.org/docs/multiplatform/compose-navigation.html](https://kotlinlang.org/docs/multiplatform/compose-navigation.html)  
17. Workshop: Choosing Your Compose Multiplatform Navigation Stack \- DEV Community, [https://dev.to/software\_mvp-factory/workshop-choosing-your-compose-multiplatform-navigation-stack-4a84](https://dev.to/software_mvp-factory/workshop-choosing-your-compose-multiplatform-navigation-stack-4a84)  
18. Compose Multiplatform 1.8.0 Released: Compose Multiplatform for iOS Is Stable and Production-Ready \- The JetBrains Blog, [https://blog.jetbrains.com/kotlin/2025/05/compose-multiplatform-1-8-0-released-compose-multiplatform-for-ios-is-stable-and-production-ready/](https://blog.jetbrains.com/kotlin/2025/05/compose-multiplatform-1-8-0-released-compose-multiplatform-for-ios-is-stable-and-production-ready/)  
19. App screenshot sizes and guidelines for the Google Play Store in 2026 | MobileAction, [https://www.mobileaction.co/guide/app-screenshot-sizes-and-guidelines-for-the-google-play-store/](https://www.mobileaction.co/guide/app-screenshot-sizes-and-guidelines-for-the-google-play-store/)  
20. DMV Practice Test 2025 \- Exam \- App Store \- Apple, [https://apps.apple.com/pk/app/dmv-practice-test-2025-exam/id6753893761](https://apps.apple.com/pk/app/dmv-practice-test-2025-exam/id6753893761)  
21. USA DMV Driving Test Prep \- Apps on Google Play, [https://play.google.com/store/apps/details?id=com.oxorbit.us.dmv.test.prep](https://play.google.com/store/apps/details?id=com.oxorbit.us.dmv.test.prep)  
22. DMV Prueba de Práctica 2026 \- Apps on Google Play, [https://play.google.com/store/apps/details?id=es.dmv.app](https://play.google.com/store/apps/details?id=es.dmv.app)  
23. Compose Multiplatform draw behind System bars in IOS \- Stack Overflow, [https://stackoverflow.com/questions/78021855/compose-multiplatform-draw-behind-system-bars-in-ios](https://stackoverflow.com/questions/78021855/compose-multiplatform-draw-behind-system-bars-in-ios)  
24. About window insets | Jetpack Compose \- Android Developers, [https://developer.android.com/develop/ui/compose/system/insets](https://developer.android.com/develop/ui/compose/system/insets)  
25. Driving Theory Test: DMV Prep \- App Store \- Apple, [https://apps.apple.com/kz/app/driving-theory-test-dmv-prep/id6762512722](https://apps.apple.com/kz/app/driving-theory-test-dmv-prep/id6762512722)  
26. DMV Practice Test 2025 \- Exam \- App Store \- Apple, [https://apps.apple.com/us/app/dmv-practice-test-2025-exam/id6753893761](https://apps.apple.com/us/app/dmv-practice-test-2025-exam/id6753893761)  
27. Accessibility | Kotlin Multiplatform Documentation, [https://kotlinlang.org/docs/multiplatform/compose-accessibility.html](https://kotlinlang.org/docs/multiplatform/compose-accessibility.html)  
28. DMV Practice Test 2026 Easy Ed \- Apps on Google Play, [https://play.google.com/store/apps/details?id=com.thedvani.uspdd](https://play.google.com/store/apps/details?id=com.thedvani.uspdd)  
29. Compose Navigation on iOS: swipe-back gesture bypasses blocked onBack and briefly shows previous screen : CMP-9338 \- YouTrack, [https://youtrack.jetbrains.com/projects/CMP/issues/CMP-9338/Compose-Navigation-on-iOS-swipe-back-gesture-bypasses-blocked-onBack-and-briefly-shows-previous-screen](https://youtrack.jetbrains.com/projects/CMP/issues/CMP-9338/Compose-Navigation-on-iOS-swipe-back-gesture-bypasses-blocked-onBack-and-briefly-shows-previous-screen)  
30. Color Contrast Checker: Your Guide to WCAG 2.2 & Accessibility \- achecker.ca, [https://achecker.ca/contrast-checker](https://achecker.ca/contrast-checker)  
31. Default UI behavior on different platforms | Kotlin Multiplatform Documentation, [https://kotlinlang.org/docs/multiplatform/compose-platform-specifics.html](https://kotlinlang.org/docs/multiplatform/compose-platform-specifics.html)  
32. Set up window insets | Jetpack Compose \- Android Developers, [https://developer.android.com/develop/ui/compose/system/insets-ui](https://developer.android.com/develop/ui/compose/system/insets-ui)  
33. safeContentPadding API Reference in Compose Foundation Layout, [https://composables.com/jetpack-compose/androidx.compose.foundation/foundation-layout/modifiers/safeContentPadding/api](https://composables.com/jetpack-compose/androidx.compose.foundation/foundation-layout/modifiers/safeContentPadding/api)  
34. What's new in Compose Multiplatform 1.8.2 \- Kotlin, [https://kotlinlang.org/docs/multiplatform/whats-new-compose-180.html](https://kotlinlang.org/docs/multiplatform/whats-new-compose-180.html)  
35. iOS Status Bar Color And Padding Issue In Compose Multiplatform \#4121 \- GitHub, [https://github.com/JetBrains/compose-multiplatform/issues/4121](https://github.com/JetBrains/compose-multiplatform/issues/4121)  
36. Semantics | Jetpack Compose \- Android Developers, [https://developer.android.com/develop/ui/compose/accessibility/semantics](https://developer.android.com/develop/ui/compose/accessibility/semantics)  
37. Design Tokens \- SAP, [https://www.sap.com/design-system/fiori-design-android/v25-4/foundations/design-tokens](https://www.sap.com/design-system/fiori-design-android/v25-4/foundations/design-tokens)  
38. Design Tokens \- Serendie Design System, [https://serendie.design/en/foundations/design-tokens/](https://serendie.design/en/foundations/design-tokens/)  
39. Button accessibility tests | U.S. Web Design System (USWDS), [https://designsystem.digital.gov/components/button/accessibility-tests/](https://designsystem.digital.gov/components/button/accessibility-tests/)  
40. Dynamic Font Sizes With Jetpack Compose | by Oya Canlı \- Medium, [https://canlioya.medium.com/dynamic-font-sizes-with-jetpack-compose-2665c65d78f7](https://canlioya.medium.com/dynamic-font-sizes-with-jetpack-compose-2665c65d78f7)  
41. Compose Multiplatform 1.6.0 – Resources, UI Testing, iOS Accessibility, and Preview Annotation \- The JetBrains Blog, [https://blog.jetbrains.com/kotlin/2024/02/compose-multiplatform-1-6-0-release/](https://blog.jetbrains.com/kotlin/2024/02/compose-multiplatform-1-6-0-release/)  
42. Accessibility in Compose \- using semantic, [https://sii.pl/blog/en/accessibility-in-compose/](https://sii.pl/blog/en/accessibility-in-compose/)  
43. Material Design 3 \- Google's latest open source design system, [https://m3.material.io/](https://m3.material.io/)  
44. Theming application with Material 3 \- Angular.love, [https://angular.love/angular-material-theming-application-with-material-3](https://angular.love/angular-material-theming-application-with-material-3)  
45. Scaling fonts automatically | Apple Developer Documentation, [https://developer.apple.com/documentation/uikit/scaling-fonts-automatically](https://developer.apple.com/documentation/uikit/scaling-fonts-automatically)  
46. Typography | compose-multiplatform – Kotlin Programming Language, [https://kotlinlang.org/api/compose-multiplatform/material3/androidx.compose.material3/-typography/](https://kotlinlang.org/api/compose-multiplatform/material3/androidx.compose.material3/-typography/)  
47. Support Density.textSize on iOS · Issue \#2567 · JetBrains/compose-multiplatform · GitHub, [https://github.com/JetBrains/compose-multiplatform/issues/2567](https://github.com/JetBrains/compose-multiplatform/issues/2567)  
48. Dynamic Font Sizes with Jetpack Compose \- Dawn Technology, [https://dawn.tech/en/publication/dynamic-font-sizes-with-jetpack-compose](https://dawn.tech/en/publication/dynamic-font-sizes-with-jetpack-compose)  
49. App Store Screenshot Sizes 2026 — Complete iPhone & iPad Guide | AppScreenStudio, [https://www.appscreenstudio.com/en/app-store-screenshot-sizes](https://www.appscreenstudio.com/en/app-store-screenshot-sizes)  
50. Add preview assets to showcase your app \- Play Console Help \- Google Help, [https://support.google.com/googleplay/android-developer/answer/9866151?hl=en](https://support.google.com/googleplay/android-developer/answer/9866151?hl=en)  
51. Prevent Text in Jetpack Compose from enlarging when device font size is increases, [https://stackoverflow.com/questions/69108033/prevent-text-in-jetpack-compose-from-enlarging-when-device-font-size-is-increase](https://stackoverflow.com/questions/69108033/prevent-text-in-jetpack-compose-from-enlarging-when-device-font-size-is-increase)  
52. Beyond Font Scaling: Large Content Viewer with Compose \- ProAndroidDev, [https://proandroiddev.com/beyond-font-scaling-large-content-viewer-with-compose-a6d6f5c2a29c](https://proandroiddev.com/beyond-font-scaling-large-content-viewer-with-compose-a6d6f5c2a29c)  
53. Support for iOS accessibility features | Kotlin Multiplatform Documentation, [https://kotlinlang.org/docs/multiplatform/compose-ios-accessibility.html](https://kotlinlang.org/docs/multiplatform/compose-ios-accessibility.html)  
54. Adding Navigation support to Large Content Viewer with Compose | Eevis Panula, Mobile Accessibility Engineer, [https://eevis.codes/blog/2026-02-28/adding-navigation-support-to-large-content-viewer-with-compose/](https://eevis.codes/blog/2026-02-28/adding-navigation-support-to-large-content-viewer-with-compose/)  
55. Navigating the Waters of Kotlin Multiplatform: Exploring Navigation Solutions | by Thomas Kioko™ | ProAndroidDev, [https://proandroiddev.com/navigating-the-waters-of-kotlin-multiplatform-exploring-navigation-solutions-eef81aaa1a61](https://proandroiddev.com/navigating-the-waters-of-kotlin-multiplatform-exploring-navigation-solutions-eef81aaa1a61)  
56. Designing for Color Blindness: Best Practices for Accessible UI – Blog \- Atmos Style, [https://atmos.style/blog/color-blindness-in-ui-design](https://atmos.style/blog/color-blindness-in-ui-design)  
57. Web Accessibility Color Contrast Checker \- Conform to WCAG, [https://accessibleweb.com/color-contrast-checker/](https://accessibleweb.com/color-contrast-checker/)  
58. Apple App Store screenshot sizes & guidelines (2026) | MobileAction, [https://www.mobileaction.co/guide/app-screenshot-sizes-and-guidelines-for-the-app-store/](https://www.mobileaction.co/guide/app-screenshot-sizes-and-guidelines-for-the-app-store/)  
59. App Store Screenshot Sizes (2026): Every Pixel \+ Free Templates | AppDrift, [https://appdrift.co/blog/how-to-create-perfect-app-store-screenshot-sizes](https://appdrift.co/blog/how-to-create-perfect-app-store-screenshot-sizes)  
60. App Store Screenshot Sizes 2026: Every Device, Every Size \- ScreenSnap Pro, [https://www.screensnap.pro/blog/app-store-screenshot-sizes](https://www.screensnap.pro/blog/app-store-screenshot-sizes)  
61. App Store Screenshot Sizes & Requirements 2026 Guide \- ASO.dev, [https://aso.dev/app-store-connect/screenshots/](https://aso.dev/app-store-connect/screenshots/)  
62. App Store & Google Play Screenshot Sizes 2026 — Requirements \- AppLaunchFlow, [https://www.applaunchflow.com/tools/store-sizes](https://www.applaunchflow.com/tools/store-sizes)  
63. App Store & Play Store Screenshot Sizes (2026) \- Launch Shots, [https://www.launchshots.com/resources/screenshot-sizes](https://www.launchshots.com/resources/screenshot-sizes)  
64. Google Play Visual Assets & Specifications: Guidelines and Best Practices, [https://otherwayaround.co/blog/google-play-visual-assets-specifications-guidelines-and-best-practices](https://otherwayaround.co/blog/google-play-visual-assets-specifications-guidelines-and-best-practices)  
65. App Store Screenshot Sizes and Dimensions for 2026 \- Adapty, [https://adapty.io/blog/app-store-screenshot-sizes-dimensions/](https://adapty.io/blog/app-store-screenshot-sizes-dimensions/)

[image1]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA8AAAAaCAYAAABozQZiAAAAs0lEQVR4XmNgGLmABYj/E4G/APEjIC6GaIMAJSB+CMR5QGwGxJJAPI8BosEQygfhrVCxdIg2CIgGYnkkPgcDQiHIVTAAs8QFWWA3XBoCQJL/gPgbmrgmEO8HYhGYQAQQT4ZLQ0ArA8TWq2jiNkC8mgHiMpwA5uSl6BLEAJBGkJNN0SWIATAnw/1GLOBmQDiZEU2OIPBkoMDJoJAm28l7GEhwMj8Qn2DATMcwDDJwFIwC8gAAAXcwK3rYcZYAAAAASUVORK5CYII=>

[image2]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABYAAAAbCAYAAAB4Kn/lAAABMklEQVR4Xu2TPUpEQRCEa1HBwEwRTMULiJipCGJgYGJkYK43EDzEBqIHMPAHDcXEQPYGG4mxIHoCQ3+q6Glo2nnri5f3QQVbPd37eroH6BgL7qkfakhNBH+auiqxVWoqxHrUEvVCbcLO/uECljxIvjiGxRZygMxTy9mMnMCSn5OvrzorsVph/anONHIIS35L/h11U2IrwZ+kzqmD4FXZhSV/Jv+S2qG+qbXgr1MP1EzwqtQKz8K+UpKvM4462Q6/G/FkFXeOYPdXK6xO4oY0skh9wAqrWB9WWGhor7BBiX8HFvFkH9Ij7CrEHGxbTmF7+1T8VniyCqvNvRDTgAbUNWwTvJNWeHJeK0cP6AstB5bx11e7P8Vu0XJgGSW/Z7Oglzny6Y5in9rKZmED9U46OsaWX7bwPIpWiJi9AAAAAElFTkSuQmCC>

[image3]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABIAAAAaCAYAAAC6nQw6AAAA3UlEQVR4Xu2TPQrCQBCFRyy1sRFsbcSfQtBz2HgJb2CrhXcQPIedRWo9QGoFsRCxshFE32Oysg5ZEzuLfPA1+3aHeYSIFPxKG17gM+ADrmHHPchiK/pwBcuJdTiH98RcnEQHjWwAxqKbdW2QBodEsGrOHVO4gzUb+FREBy1s4MFBe9iwgU9TwrUcuQZxk0jCtViHtTai26fCgBe+1RrAG5yZ8w9Yi18sqxar92zgw014KVRrIprHNvDh40j0YogrPMOhDXxcrbRBJdG6B9g32Zus/+sIl7DlHhQU/D0v/l83NFZyb6cAAAAASUVORK5CYII=>

[image4]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAmwAAABCCAYAAADqrIpKAAAFOklEQVR4Xu3dTaitUxgH8CUUIUQ+QhehRCG5UoYMJBITSgwRI4owOSXJDDERyegaSIorX4NdBsTUTZG6ZHaTEoObfKynd++713ns95yXe857993n96unu/ez99n73aP7b613rVUKAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIfvmNxYEifnBgDATnR8rU9yc0nsrnVrbgIA7CQ313o8N0fwd62Lm+d31TrYPG+dWuvT3AQA2AmOK/0haTudWbrAlkXv/NyceqF04RIAYEeZ1LoyN0fwQK39uVm6wHZtbjYWhTwAgJUVN/MfidG1sL/WE6l3eukCWYy+9Xm51iO5CQCwqvaUIxd+FgWztbJ5gIxQ91duAgCsqghNR2LLjFhosGhqM3q7cnOBCGzn5SYAwKq5sdbPuTmCc2t9Xeud6eOoWKG6KMD1eap0994BAEeRA6X7Dz/Xb7Veb963bH4s3XXelPoRYqL/e+pvpTdrvZ2byTW5sSRiUYJpUQA4isSU3qvTx3mriLjf6dHm+ZjuyY0ecb0n5GbpQtt2BrYfSrdSc5F4bX8ZPup1X25ss1PK8GsDAJbAWvM4wlk7zXd2raua52N6NzcWiID5R25ObXdgixGq63IzGRqKJrkxgqHXBgAsmf21ns3NLfR8re9q3VG6o5w2MiSwxQjXx6ULmd+k13Jgi81kYwr111onNv2nSxdeHq71bRm+6jP+pj1lYJGhoWiSGyMYem0AwJIZEkIuK13w6avJoXeuF2HoitIdkB7fEycEbGRIYNtX5kdC3VZrb/NaG9h21fqpeS2+PwLj3WX+NzFSFyOKQ8VnxHdsZGgomuTGCIZeGwCwZLbrRvSXan01fRzTiBG0sjPKfNVj1EfpeR6Ri8DXho5YANCeldkGtu9Lt0hgJu4xW6t1SelG/EJ8VoTJoQ4nsB1b1v+2z9Pzs+ZvPSR+y3+tjfRdGwCwxGLxwd7cXODC0i1S6KuY9swmZb4rfwSrmMrMu/Rnm42wRbCJ4DUTYTMC2Ewb2CIwtr8tplBjZWncn3dO6UbnWnFQevyOt1K/dTiBLZvkxgiGXhsAsARi5CqCx3O1Hpo+3mpx4HgciRQiWMW063vzlxfaLLCF2YjgWq3Hmn6MYO0u66c5/5z+e3np9jELF037MZUbo3Oz3z4pXZiLUcF8msBMBJ6+MztjtDCmYeM9l5bFI2atSW6MQGADAP4ltt6IIBOGhMIhgS3EZw2dyoz3tlOrH9S6evo4RtViRK69j+2X0v/Z8d5bcvN/muTGCAQ2AOCoEFOjbSB7o3QjcCH6MV3aZ1/ZfFp3q8XIYQSt05reZ6UbMZwF0QicESZn4XiRvN8eAMBSi3vbYko0pkavb/ovlm6D2ahFYvuTSW6OIAetD8v6e/luL+u3LVkk7t+L7U0AAFZaLNLI4WkM7Xc+U+veMl9cEYsuLpi/3Cvuzes7pQEAYKUcLN3ChTFFYIuwGFOgd5buPrpZiHt/9qZNzD4DAGDlxakIe3Jzm0XYisUTce9aiJWqs8DWt0CiFffotZsIAwCstNmpDWOK77u/dEdthQhv0Xvl0Ds29kWtG3ITAGCVPVnrwdzcRhHO4vzUmdm9dLHX3WZiMUJsVQIAsOPECtMh05FbIRYY5O86kJ73+bJsvN0HAMBKi8Pt83mny2TZrw8AYBQRipbRa7VOyk0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPr9A1Vl517F9bhOAAAAAElFTkSuQmCC>

[image5]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAsAAAAbCAYAAACqenW9AAAAuklEQVR4Xu3OsQpBURzH8SOUQRkMlxgki9lmlGxWpTyCJ7B7Bk9ws1gNJmXzADYDdUsRgzIpfK/7P6c/DyDD/dVn+P/Ov3OOMXH+Okn0sMUVHfTR1Us2E1zQlPmEJ2ZuQ5LAAwPV+SZaHqnOpDDFWJdkgwOquixhh7YuyQ1zZHTZkIOPG0z0he/X3kvhc1nVVXBGXeTsgf1zTeYyltijiKH0LgUcsUCAFu5YY6X2XPLwkJY5/Jae4/woL6qxHwDObuP3AAAAAElFTkSuQmCC>

[image6]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAkAAAAaCAYAAABl03YlAAAArklEQVR4XmNgGLxAE4j/Q/ETNDkUEMEAUbQVXQIZtDJAFIFonOA5FCuhSyCDfwwQqzjQJZAByKqFQDwXiH8B8SoglkBWIAPEf4E4GEkMpAGkEQ5cGDCtwlBUxYDqK24g3sOApEgciO8yoPoKZDLII+9hApJA/BCIeWECQDCFAWLKHJiAIBCfBmIemAAQPAPiW0AsjyTGwM8AMXoDEH8BYgVkSWQgwACxGkSPAjIAAHwkI3aJ3Y2eAAAAAElFTkSuQmCC>

[image7]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFMAAAAWCAYAAAC8J6DfAAAB90lEQVR4Xu2Xv0scQRTHvyKCwaiNGAVBCy1EEMU0KcRGQRFrm+g/YGkR1EYJ6bRJSAoxRRAJsbMQEwgWKVKIjRAVbEQRG4ugaKEg+v3y9ry52b0fYHXLfODDuW9m97i3b+aNQCAQCASeSwVdojX+AKmkH+kyXYDNDeShhR7SB/rSiVfRdVgSXf7T9whJTUTJ2kM8mW/oOe1zYkJVuk8bvHiAbNIJxJM5FsXeOjExS09osxf3qfUDHqr8ej9YrujHfIUt10zi3GQ20n9R/C9tg92jLaE1Oy0v2n9Xo0+f33QR9rxUoIr7Ff2dlEzxml5EY3ewZa89tlRW6BZyK7CJziNFiWynu7Qrus6XTNEBG8s4kDtckBewhG5H13oRO7ATQmr4gdzKSEpmL6xzZ7p5Hf0Em3eDeGMqhL5L980gZacAJWkKtu+pichJ2I9Vxeq6mn6H7Y+v7LYnTmFzP3jxQvTAqlOnhlL227JhEJYQ12tYgs6ia81Rx15DvJL66S395sXzoX1Xz9XSnqYHucPp4x3iy/wnkitT588rOuzFkzimo15ML0cJ7fTiZY+qRU3hCyyZ3cgu8yF6D6tOlz+wfbRYN1Z1yyTG6RFs+acGJc7t1BnVkMQIvYQ1LCVwA/YfUNLZ0UUv47Mf9NCz5/xgIBAIBAKBIjwCVjlgkoVY0d8AAAAASUVORK5CYII=>

[image8]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFAAAAAWCAYAAABXEBvcAAABl0lEQVR4Xu2VQSuEURSGj1BEUUoppWSnlA0ldjaSbKZsZM1aKRs/QTYiZYENyV/QhOIHKCUlkoXCWhLv27237pyZ7+szu++b89RT95x7m5p3zr0jYhiGYWh64JVu1mAQzuhmozMFv+Cv3lAsijszpzcanXP4JukBDsE7+C4WYAVNcAseSnqAJ3ANliVbgK2wQzcVXbqRN/juXfj1gSQHyOkbhp2SPUAyCx91U1ywR+JCzi2cvHVx00eSAuSX3Pfr/wZIGOJYVIfw9qJeLhmHl7DX10kBLsAzv64nQMIpDCGG8HI/fduwJerVCnAXrkZ1vQGSefgN2/VGHhmFy3AA9nlPxQXINd9GhvwAS7Df9/kWXsMlX3dLNvhnwc+/gRNqL5dMw+fIF/gjLkDWx+LeqvgMffVnPny9KdlgeOHa3kvlm1gIwtXUV1jDqXuS7Fc4TJ5+7/gDMshC0AZH4K1UX+EY9ifhJ1zxddoV5gTzXd3RGx6GWIhJ5DQxuNiyuKmM0WcoA0qC//IbUj19Mdxv1k3DMAzDKCx/j85WshUBvfcAAAAASUVORK5CYII=>

[image9]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAmwAAAA/CAYAAABdEJRVAAAEiElEQVR4Xu3dT6imUxwH8COjiBIRopSk1IgSC0YJZYiNodQsLKaMJBvFZhTJxtJiFrOzmo2yIkm6ZSMWsyGlLEaykaaEBflzfp7n3Od3T+9939f9U+Z9P5/6dc85z/Pe+3ZX385zznlKAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFgXf3d11dbLe+pYrVO1Lu0vJC/VOlnrojT2aGofTm0AgLURQe2mfvA/uKcfmOGHWpeP7Y/zheS+MgW1b8p0/ztlCpR3jWMAAGvj+jIEod14pR/oxN/4NfUjdM2aycv3PFHr/bG96PcDAKy0CEUtGO3UokAV18+m/p21jqZ+k4Pj47X+GNvx+XiUerrWic07AADWRISkQ/3gKK8jm2cngW3WZ/rA1vq3pvEIcQdTHwBgpV1WhlB0oBu/pNZrtb7oxrPrUr3R9aOyCF8/p34Ethjr9YGtPSLNa+Qi+MWaNgCAtRDrxNpjx+ZIam+k9jyzZsuyWK+W/85D41jvp9Q+XqZHtTnIRYiLawAAKy9m176t9UGZZsU+qvVXumcjtedZFNhC7Pq8OrWbCGPt83eXYXYvnCvDdwxnxp9X1vpzbAMA7LsLaj1ThnPHQqwXu2a6/L+w0Q9sY5nAFh6o9XqtC/sLSTwKfbkM/5/srVr3d2MAAPviqVq/l62BJM4f64/W6B9VRv+5bmw/fVqG7/R9mX/QLQDASolHfhGC+tmji8vW2azov5v6IT73djcGAMAei9D1Qj9YhoCWT/CPRfmxk7K5twxrt/qgBwDAHoqzxCKwtYX082yU4d5W+Ryy7JZa382pjc07AQBYKB8E24sZtCzfFzNteefmXojdoTkQno+13ftIAQB27PYyBI1ePOaMg2ebK8rW++KstFmfC3EExpNz6sHpVgAAlhHHVcTBr+1Yiwhcd0yX/z0LLQ6L/bxMr4WKfjvxf7fv/AQAYAkRxJ4vQ3iLzQbLeKTWq/0gAADrIx65Plv2dxdqHJr7Sa3DtX7srjXx90/XerrWl2k83h0aZ9f9Mv4EAFgrMcsXgW2Z3avbWfSmgwhieR3e0dTOPkztN2sdG9sR2E6WYVcsAMDaiTPfdrsLdVFgO1SmdXghdr7mc+aaHOpiV+3Zsb3o9wMArLTYyLDbzQyLAlVcb+ErRFibNcvWB7b2eq74/KkyPC49sXkHAMCaiJAUM2CzPFzmv6i92Ulgm/WZPrC1fj44OELcwdQHAFhp7e0LWRw1Etp4vNN0uzctNLPCVxav3eofica5dL0+sMUBv/14BL9Y0wYAsBbiZfJfpX5sDjiX+uHrMoW47SwKbCGHruOpHTtGbxvb76Xx+G6xWzScSePxe25OfQCAlRWvdorw09dn6Z4jZfHsWlgmsMUBwTE79mIZQmCTA1uIcBbr1PKrp+I7xBq232o9lsYBANbajbWuLcOs2w3dtd4ygQ0AgD2WZ90OdNcAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADgPPQP+4HtiKgC/RMAAAAASUVORK5CYII=>

[image10]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABYAAAAbCAYAAAB4Kn/lAAAA2ElEQVR4Xu2QMQ5BURBFR2gkFKKyBAo6K9BSaO1AlHp7kNiAmlXYglYjEpVGotFwb+a//G/i/UgmCvJPcpKXd/Pum4xIwd+xhw+4hWWTubiIFk9s4IWlLG/bwENNtHhpAy+cksVjG3gZyRfWQLiCHazbwAOn5LSxNaxgJzn3RYc4p3EcFnK/sTVsYDU5T+Ec3tI4DidgccMGoAJn5m4oHxSHNbDY0oUH0fIsucUl2IQL0dIrbGVcJ/fvPswt7omWhccxT+FBhtxiD79VfIR30TXxPHiNCwosTyfONMSRZv0aAAAAAElFTkSuQmCC>

[image11]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABYAAAAbCAYAAAB4Kn/lAAABCklEQVR4Xu2UsY5BQRiFf7EKYSOi0mlXgkKr1Sq03kBUXsALbEU0Si1eAJ1OrdVpNVoRzsnMJHd/cyWbWc3mfsmXXPPfe8w9JkQS/h0HeIcrmFazIM5ignt6EApDGf6lByHkxQSP9SAU7pLBXT0IpSNvqIGwgh381IMQuEvuNq6GKaza6w0cwgXsw5S7yQcD2W9cDUuYhSVYs2sMvMG2u8kHa2BwUQ/ABxzY6ybcwpz9fJUXp8jVwGBNHR7FhPvw7pivwlcbiQm9wHLEuV33fSGpwG+YUevSEBPmHo7z5B6IUBDzIz6FhsCwiZje+Uf1J8eT9e3hzLqW+CP6K1hftCaeitaPOxISnngARho3RvBo1p4AAAAASUVORK5CYII=>

[image12]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHcAAAAWCAYAAAD6kQN1AAACoElEQVR4Xu2YzYuPURTHv5pRJgqRlyyUmiIWiqaUtySR2FhQ/gApZYWyt7GQLKUkG2GJlWQpFrMisfCyYDHJBnnJy/nOuWd+53ee5/bMpF+zmPOpb8/vnnu6t37f59yXB0iSJEmSZLbYJzoQg45loquiK6JDoc+YJ7oIzeNzqL97Esb2Qsc5DR03GQDvRH9FL8vzXH/3JBtFH0RnXOyY6KtoS2nT1Muia1MZwCromA9Fi0tsWPRZNFba80XXSywZIDVzT0D7KIOmsn2+tNeJPorGpzKUP9A8vgxkm+gu1GRju+gX9AVJBkTN3MOom2v5NO1Hifml2FYGq+gL6L0Qhr0Ya0K8jbZl3mMrRBKomRvhUnobuq/yt7FItMK1CU3juDR1ueg5mnOshr4EtX3ccwfNeQn38FfoLfdJoMtcVs0o9M/lcjudKuGYrGhWtpkY56jF2+CcZrAnje2gy1xW5XvRF+ghqWuP5CmYY56F5tZMrMVrmMFWvazaNLaDLnM9K6H5PCG3mXwEekjyy2fNxFq8iweib6KdsSNpMhNzedpl/ifRhtDHSuJeeyrEl4qeoTnHTPZczxvRE/Rfv5IKNXNPQu/BfHqY/1u0O8Rp7FH0KppmX4K+ELwGxTnMXF6JpstB6FK8UHQTzQNWEqiZ+xbaxyoxWIUWs4MVP1o8gn7pWluevN+yWo+XnE2i+6IFpU32Qw9obct7hKayYuMeS4PbTtFzGh56WDk0g2bxisPfS1wO99XXovUuxsr0X5poMA9ZHCOKe69VJQ3k6XlHafP6RLMnSruLF6KtMYheBcdT9JzmMZpmUDdcDv84fgf+Cf3z7pWcXS7HvjLFcai4L98SfYfefZ9Cjd3j+muw2jfHoGMEOmaSJEmSJEmS/B//AADenYGCLQKpAAAAAElFTkSuQmCC>

[image13]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADgAAAAWCAYAAACVIF9YAAAB4UlEQVR4Xu2WyytFURSHl1CUIqSM7kAppSgzc4qiMBHK3JCBP0DGHkOZMFEyoyTilolkSEkMTBmIMlEev599trPObp9zH7mXwfnqi7PWftx1z977bpGUlJT/Rj8ccIOKargK1+CEk4ujG07BJhWrgYPquaTcw094Hfydj6a/ycAbeKdiLfAI1quYjyEx42rPIy3KSFyBy2JyG06csXEn5sIC+eXNwRnYASsjLcpIXIF8c74cYzuwyolrWGAW1jnxP8FXBHkVf46xF9jlxDW2QC7lSTgi0f2YRDs8gYtuolh8RRC7T90cYyy+x4lrWCD7X8BpuAnfxRxYuVgXM8eHmygWXxFkS0yOE1oqglg+BT7B3uCZ/RbE7MlcsA3neHQTxRJXIBmFb2IOCRZ0KPktUR8cg325ZMtKUoGkFZ7BFdgmpv0VbNaNHFg85ZuzcJxcc5WEQidley7fpFOU+432qdi/K3BWwv1m6YS7sFbF7DLWpyQvCNyH+rdvGF6KuSwkYcfLOvGC4IfhN5oRU8R28H+DasMPxJNsL3huhMdi2ml4EeAY+rrHa92ShKcm+57CsZ8W8djxaNFkJRxEq28t/A3bh7dB/Fn81y3fG2RBBxL2fRDTX+/JOH7lDaakpKSUhC+Lf3rvHHyp6AAAAABJRU5ErkJggg==>

[image14]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHcAAAAWCAYAAAD6kQN1AAACsklEQVR4Xu2Yz4tOURjHH6GIQkSiJmJnYSE1ExYTIlmhlLJgwYodsZqa/ANTKKmZyYLMLLGyeGOjsVJISiixsJAFkfz4ft/nHPfc554z76WRxTyf+va+5znn3lvne5/znHNFHMdxHMf5X+yG9tpgwmroCnQJOmT6Uo6LjhuB5pg+MhfaKdp/Glpe73ZmitfQT+hZ+D1b7+6yC3oEbQjt+dAw9AHaGgeBA9D30B/5Bp2RyuR5Ur+OY0dDzPmHlMydhD5Cx5LYKtHx10UNWwk9DrGUh9AbaF1o94vej9dEtom+BLksd2aIkrkPRPvemjhjL0SN5nLO9qfaCJG7IX4ktC9A56vuLuuhd9AaE8/BJX06ltiAo5TM5dJJAxckMWYexzMzl4lelzN3PMRp8groiTSfwVrO8rDfxHNMiNbzdOknrOHPpV4mnISSuTm4zHL8UGjTmJy5nRDnbzTRPqMUz8HMjAanuLE9aGsuayM3VMzGuAzSbNZWW3NfhVhHyiaW4iWiwTF7mbVubA/amNsnmiX3bIfoZF+GzkFroaPQLamW5ZKJpXgv7kCfoR22w2nSxlyayqwpbVyY1WPQfegUdFv0vjzTsjazRttn/EnNTXkputm7ajucJr3MpaGcyIWhzePPjfBbgibzvodFN2E8BtlnRHN5JGrLPtGleBF0TZobLMdQMpcTNySatRtFJ/UgdFGqMysnOR57FnevUtg+mbQ3iWZzuvPeA/2QdudcmsqMtTWWBud20bMafvpj5rCW0oib4f/SZAy/MLEvJ55bCY0ZFf1CFU0akOaEs+8rtD20+SLQ7Pe/R0zPU2iLDUqVwXYXPavpSNMwajwZwzpp+6NOJOM46fwYwQnmRopGc9ItXMq/iL4YU6LGDtZG5GG2b7bBBJaL+LI5juM4juM4zt/zCxU+o2lDuV+xAAAAAElFTkSuQmCC>

[image15]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHcAAAAWCAYAAAD6kQN1AAAC3ElEQVR4Xu2YS8hNURTHl1BEhPIo5TGRIuRRJCOKZIKBMFDKyIQBGflKpsjAQCQMKCZSkgxuSMrEwKNEIlHKRCjksX6tve7Zd997P+f0xeRbv/p3z157331v57/X2vsckSAIgiAI/jcTVLtVp9PnlM7uNmNVp8TG7Sz6chi3VWzs9tTuB791T7W/7AiGzlPVqnQ9QszcX6ot7REi41W3VM+z2ETVM9XMLAYrxMwfndrvVT9Uq9sjOvmm+q06WHYEQ+e76rBUZpBlN1UPxAyEbWIGHE1th9jerI3RGD4tizGGxbI2izlk7QcJc/8JI8VuLNmzMotzoz+pFqX2VbFxm9sjDDKyJZbZMCA2LueQap9Ui8ehShxRXZBm5vKfB8MXZKBMlcochwylnM5N7Y9iBmxqjzC+JC1VLVd9TaKkP1K9Uc1rj64gY++o1qnOSzNzr0hn2XeoDGwbbAtBH1j5lORLqlEphoH9zPU44prYMbF55qT2Rv9Cgmw+IZa9Tc1lXjc4J4ytAadgbvasLFbHXE67XKO8ErxVvVTNzmJ3xSoGNDUX3GDPXrI2jP0Lt8WMmF/E65i7I11zeMpx85ibTD0gVUXI+5uY69wQ2wbWlB1BJ+NUD6X70QbYf/uZ63vuBqnKco6b11ItUb0SqwozkshA+jlcsRezAOrCXGwhZ8qOoILyxh42OYvRXpiuMb2fuX7w4mTNCXswcymfHLJy/Uz9n1WXxRZZHdjHKcWMvyjdB6xA2aV6kj4XiL1ZQvdVk9KY9WLl9mRqO5iSP+cOpFgO+yvfLR+jnJY0K8uYSsaWeywG9zpFD2s8c0rxbOt7Iy82rqvepTZMVz2W6mAE/hKDsuswf6+bPkasLDMHv3dc6pVl3qgtK4NSZXB5ih62YF5pqqvMUowjmzkV71G9kO6DFyxWvVadVV1TnZPepdYfnXK1pPuZO4cFwfz9YBGWb9GCIAiCIAiCoDl/AHMTu5PUNZScAAAAAElFTkSuQmCC>

[image16]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACEAAAAWCAYAAABOm/V6AAABDUlEQVR4XmNgGAWjADfgBuJQIJ4GxDFALIwqTXsgBsRTGCAOAQFLIP4JxP/gKugAXID4PxA3QPmSQPwQKsYIFaM5MGbA7YgBA80MEAfsQZfAAliB+AQQG6BLkANAwQ5KjPJA/BeIjwGxDIoK7ECfAeLgrUDMgSZHMgAlyhVA/AiILwCxLxAzo6jADkAOBTliAgOV0w8nEO9ggBgOYg8YaGWAOAKUc+gCPBkgloISGQyUM0AckYQkRjPAA8QHgPgJECtCxViAeA0DxBEwMVyAH4h/AXEsugSpIAOI7ZD45kD8lQGSSwgBWBkDys6wEpcsAIqGb0B8GohnMUB89hGIw5EV4QBUC4lRMAoGFQAAHEAtXzO49b0AAAAASUVORK5CYII=>

[image17]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHcAAAAWCAYAAAD6kQN1AAACqUlEQVR4Xu2YTYiPURTGj1BEIfIRRXbKgkgpLCSNfCzYUNZWMkVRVjZ2srCwkJo0TTHDSmMhi1lJFClfScmGJGSKTFN4njn39p73vO+dv2lGFnN+9TRzz7nv+9Z9/ud+iQRBEARB8L/YDe3xQcNc6DJ0BTrqcpZTon0uQbNdjsyEdonmu6HF9XQwVbyDfkOv0t8z9fQY86G70GsTWwC9hFaZ2Eap9yH3RJ/lO8gs6Cu0JbVpfk+KBf+QkrmHRXPnXZyx46bNSuwzbXIAGoY2pfZW6KaoyZlt0Cg0w8SCKaZkLs1g7qCL05Ah0arkVHsDemg7iBr3USpz+QM5W6XHWAt9gFa6eBv8znhwRglaKJn7WTS338W/J2XjaP4v6Bt0RNQImnYo5ZdAz6X5jRWiy4N/fxsDouu5X8u5hnNJyNN94CiZSwNL5to4B/xCilGcjnemHMkm+m+U4m2wMrPBljC2A5M1l7Bas7nUiRQjJRNL8RLZ4Fy9rNowtgOTMZebodPQl5Rje1/KUzw6lUwsxTtxB/oB7fCJoEnJXK6bJXPzmrte9DhzznYAt0SfHYQWQY+k+Y2JrLmWt9AD6KpPBE1K5tKQkrk0nrtdbqa4e+bu2LJM9Dz8VPT4w523/0Y21z87HntFp+J5UK80N1iBo2Rul+gumOdYC/vncy7PryPSfJ63T4+lqmhWOKt4Tu4g1fv/5pxLU1mxfo2lwW276GkNB5+Vs1rUrP70/0LTh9eOt6H3JrYcegYtTW0OKgeXU7M16aJo5fKdhDn+CLanNs/INPtTanfiBbTZB6WqYL+LntYMSX13m3XN9CG8ZrwPnYSOQW+gdbUeOsA90BPRQeY7+Mwa04dch36KXmjw0oPG2iNTCVb7Bh808Efob9GCIAiCIAiCYOL8Af63pX2H52+0AAAAAElFTkSuQmCC>

[image18]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACoAAAAWCAYAAAC2ew6NAAABHUlEQVR4XmNgGAWjYOgAZiA2B+IuIM4HYj5U6cEB+IH4KwPEoSDACcSbgfgWXMUgARlAfACIeZDEooH4PxJ/wAE3EO8B4oVo4p4MEIcKookPGJAE4ocMmA41ZoAkB5CD8QFWID4BxAboEtQGoOg+wIDpUF8GSIiCaHxAnwGibisQc6DJUR00MECiH5QMYKCcgTiHyjBA1E0AYkY0OZqA10B8ngES5auAuJuBuKinOwCVm6Dy8zAQmwCxCxD/A2JNZEWDEQQxQKIUVMYOGgArimDpURyIrwPxFbgK3ADkkV9AHIsuQQsASpfngFgeyp8DxO+B2AyuAjcA6QV5Ej0z0gSAysKXDJAiai4Q3wFiexQVuAFdQ3QUjIJRQCEAAPcNLfXsOl7aAAAAAElFTkSuQmCC>

[image19]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAG4AAAAWCAYAAAAhKqlXAAACg0lEQVR4Xu2YPWhUURCFj4igICgI/kBASCdoFYgI2qkkhU0MRFDCNqZKlYC2AbGxELGwEBtbsbOTYCKCgilEERSJhRZa2ZlCROOcnTu+m3l7XZ8mTd58cNjdmXl32Xd27s8DgiAIgqA9nBaN+mDGDtEt0W3RBZcztoiuQWv4unVtugbr50QPXTzowwfRquhter28Nt1lJ/TGvstiu0RvRAPpMw24IbrzuwLYDx1zHlrfi+fQmkUXDxpQMu4cNHfVxRmbTu8HRZ9FL6p0l5/QOo7hodnvEcb9NyXj7kNzYy7+HXrD2ZHHRN+gdfn0aB2dd6JxQnQRzYzb7gOOfvlNScm4L9DcGRdfSRpKn2ng3irdhV3ou5WddgU6tXLMJsaxw1+KDro4p+XHoksu3gpKxtGcknG94jnMsxPZkcZR0ROoyU2No+lmXo6ZxnzrWG/j9kDz/oZyo3MqvW9qHDHzrOvYbf47WsV6GncWugZuc/FhVDtR8i/GGTPQzc+ci7eOknG2TnmD/BpnnIReYztOw44Vh0UHkiahYz8T7UPd6D/Bo8Qj1KfN1lEybgll42gQjwI5jE2gmrpo5HXomsa17WOmr9Cxf4heiY6ka/rBruX0yB1sB/XNSqsoGTcCnZJuujjr867iWsMO4BMY3ki+8vxG489ndTn8viZTJQ1jp/ndYwe9d5ubFm4gOGXxB/MG3kvvd2c1fNT1QPQpi9Gk16i2/3wywickHMOLa93xVGewU3gtH6Gxhgf3v5kqF0SzqG9E+LmDFk2bi6jfaOpuVkP4T38K3RBMiZZFh7I8jaFBfhyK58C8lvDPYodzU6/10sMDuzfNYHzcB4MgCIIgCIIN5BcPTa1Me6uYVwAAAABJRU5ErkJggg==>

[image20]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAE8AAAAWCAYAAACBtcG5AAACQElEQVR4Xu2XP0hXURTHj4igGCQUphBE4RKBEA1iJDhoGOJii6CDUzg0NRgtgUhzEtTQ5iouIjg4iIS0tDTkH8QIJXJq0yGkP9+v5169Ht/50fb7Pbwf+OC75973rvdw3n33J5LJZDIXm4fwkQ0mNMF38D0cM32RevhGdMwUrDvbXcgafGaDZWAX/oVb4e/zs93HXILLcDuJXYab8HpoM7EL8MXJCJE7os9kIhuSeEqP+POWCm8RI6J9r0ycsafh+gH8BZdOu4/hGMa7TZxcgSviz1sqvEXMi/YNm/gRXBWtzJhgmnIYYva15Os8DWfEn7dUeIv4Kdo3ZOJMDL0X2i2i1ZTCBPPeURP/APvDtTevhQn/LJX35arhLSJWT1HyiuIp7P8ObyYxJoEVFz8m3ryWa6Jj1+FV01d1vEV4SfLikQ74Gz428S7YmrS9eS3cHjh2UfQDVVN4i/CS5MVZUZOir2x6VInxt0mMePOWCm8R+1KcJLvnRcZF98lBE78Lv8EJ2J7IZ/Pjwev/ORfWJF7yPomfPCb2lokzdj9pM1m0D+4ZuR/y2Qeh3RzuKR1e8gbgH9FfDikcH8955Db8AnvhDdFkj8MN0XNgEXEfK5rXwoM2/4+XUiMVyqMFXxculouYC9c8dkS4OXOT/pHE2kQTFTd+3sNfIHyGtag6G0Xn7RQd8zq0KyUlvuJfRb+8VWdVzi+WziZjCH+GfRQ97D6BO6KVFuE5zj4jWnS0YFXacZSV6FFzlZfJZDKZTEX+AYh2oIM95n06AAAAAElFTkSuQmCC>

[image21]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHcAAAAWCAYAAAD6kQN1AAACi0lEQVR4Xu2YzauNURTG1w1FFKIk6paJRCFSCgNJJGMDf4ChgYGYm0iSlJK6ycANGSEDg1MmYkxSEinFQCki+Xgea69z1rvO3p1L1zW461dP791rrb1vvc/ZX69IkiRJkiT/i73Q/hgMbIFOQ5eglSFnHBPNn4fmhBxhbI9o/ii0rJtOpotX0E/oWXke76b7LIbuQ+OlvUC0/lS/QmQz9AGa52KfoIvQ3NLmkzXbSpu1EyWW/ENa5tIQGvTDxVaI1nP2EdZcg771K5SbosZtKO3tJWZmkx2i/cZcLJlmWubSkK/QYxejEWeh1aW9BnoLvelXKJzZfly2Tw7Sv7G+q0K8Rm2Z93CFSSq0zH0gmqMpt6HXJeZf9JFSw2Xew/EY70HLoScl5uHezX4HQ7zGDdH93C/9hHv4cxks90mgZa7ty7egtdAi6AR0RgYv2Uxsmcu4mRj/RytegzPTDPaksSNomftRNMfDkcE9k3vwodKeKXOJGWw/LM7aNHYELXO5H/rDk8EYjd8oM2uucRf6DO2KiWSYlrkvpJ6z2cy77+HSbpnbg5aKHsriOH+y53peQg+hyzGRDFMzkPB+W8t5c+060zLXrj98xnHMXI4xVQ6ILsULoasyfMBKAjUDiV2F7oU46zlraNoYdEG6d2HCPv6ey+cdaH6/QmSfaD+OMQqayhkb91gaXDtFz2r46Y8zZ1zUrOvl7yWuxj5ifHcx1vCEyqexHnov3RfMPudkYByf/KHsLG2evGk2+02Fp9DWGJTBDI6n6FlNT9TUqCuuhvCEOil6SmXuHbSuU6Hw2/Qj0T2YHywmRF+8h+N8Ec2zlsbu7lTU4WzfFIMOfhL1n0OTJEmSJEmS5O/4Betlqk/qjbu5AAAAAElFTkSuQmCC>

[image22]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADMAAAAWCAYAAABtwKSvAAABLElEQVR4XmNgGAWjYBRQChiB2BNdEAlIAnExEE8CYisgZkaVHnjwEIj/A/ENIH4ExOWo0nDwHohnATErlG8OxF+BmAWuYpCBhQzYPQNy8G8gtkETWwPEOkhigwrg8owSED8BYhk08VYG7OoHBcDlmXQGSHIE5RlkAFJ7AIh50MTRwXIGiNn86BK0BLg8AxLD5Rls4ugAlCdBGBQodAO08sxfKHZFl6AloJVnBgTg8kw0A3ZHE5tnBgTg8gyoSMblGVDxPCjrGlyeYQTif0DsgiTGCcQ7GIirZ14zQCplNXQJagNhBkiIywPxLiBeBWWjxwLIQSA5WAvAgwGSqUEeJQRgpVkRugS1wQEGhGXoGBmA2mwfgfgUA6Sy/AHE81FU4AZ0i5lRMApGwQgEAK18RM99pefKAAAAAElFTkSuQmCC>