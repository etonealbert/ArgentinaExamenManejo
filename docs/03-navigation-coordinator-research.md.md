# **Engineering Report on Navigation Architecture for Compose Multiplatform Applications**

## **Foundational Topology: Shared Navigation in commonMain vs. Platform Shells**

Selecting a navigation framework for a Compose Multiplatform application with an extensive screen topology requires a balance between code sharing and platform-specific user experiences1. The primary architectural decision centers on whether the navigation graph and back stack engine should live entirely in the shared commonMain source set or be delegated to platform-specific presentation layers1.  
This analysis recommends a fully shared navigation architecture implemented within commonMain1. By maintaining a single source of truth for the application's flow, state machine inconsistencies between Android and iOS are eliminated2. When navigation is fully shared, complex transactions—such as multi-step flow resets, deep-link routing, and conditional onboarding redirection—are written and tested once in pure Kotlin, ensuring behavioral consistency across all target platforms2.

| Architectural Parameter | Shared commonMain Navigation | Platform-Specific Shells (e.g., SwiftUI) |
| :---- | :---- | :---- |
| **Back Stack Consistency** | Guaranteed synchronization; the back stack is managed by a single engine5. | High risk of drift; requires manual bridging of transition events. |
| **Refactoring and Type Safety** | Compile-time validation across all platforms via KotlinX Serialization5. | Manual coordination of types across the Kotlin-Swift boundary. |
| **Gestural Alignment** | Platform-native gestures (such as the iOS swipe-to-back) are mapped internally8. | Managed natively, but synchronization with shared state is complex10. |
| **Testing and Verification** | Graph logic is unit-testable in pure Kotlin without platform runners2. | Requires platform-specific UI test suites (XCTest and Espresso). |

For applications targeting iOS 26 and contemporary versions, utilizing the official Compose Multiplatform Navigation library within a single-window SwiftUI hosting controller is highly superior to maintaining a native SwiftUI navigation shell4. A native SwiftUI shell hosting individual Compose screens creates boundary synchronization challenges10.  
Because each nested Compose view would require its own platform wrapper, maintaining structural operations like programmatic deep-linking or synchronized multi-tab resets becomes highly complex5. Additionally, cross-platform lifecycle propagation (such as managing ViewModel scopes and coroutine cancellations when views are popped) is handled automatically by the shared Compose Navigation runtime, preventing memory leaks and orphaned states11.

## **The Unidirectional MVI-Coordinator Paradigm**

To decouple visual presentation from navigation routing, this architecture integrates the Coordinator pattern with an MVI-style (Model-View-Intent) effect-emission pipeline14. ViewModels are designed to be view-agnostic, focusing strictly on business logic and state production16. When a navigation event is triggered by user interaction or a background transaction, the ViewModel emits a single-time, non-persistent navigation effect13. These effects are collected in the UI layer inside a safe composition lifecycle block and forwarded to the designated coordinator for execution13.

\+------------------+                   \+--------------------+  
|    ViewModel     | \--(NavEffect)--\>  | Composable Screen  |  
\+------------------+                   \+--------------------+  
                                                 |  
                                         (Forwards Action)  
                                                 v  
\+------------------+                   \+--------------------+  
|   NavController  | \<================ |  Flow Coordinator  |  
\+------------------+                   \+--------------------+

Under this structure, coordinators are organized hierarchically14. The RootCoordinator governs high-level flow changes, while specialized feature coordinators oversee nested functional sub-graphs14.

| Coordinator | Domain Responsibility | Managed Screens and Sub-Flows | Core Navigation Actions |
| :---- | :---- | :---- | :---- |
| **RootCoordinator** | App lifecycle, identity state, global overlays, and core flow switches14. | Onboarding, Home, Legal Pages, and Subscription paywalls14. | Orchestrates transitions between Onboarding and Home, intercepts subscription states14. |
| **ExamCoordinator** | Manages the isolated lifecycle of simulated test scenarios14. | License Class Selection, Exam Simulator, Results, and Answer Review. | Executes back stack resets on exam completion, manages back-button confirmation dialogs18. |
| **StudyCoordinator** | Oversees learning-path progression and statistical tracking. | Study Curriculum, Question History, Performance Statistics, and Contextual Tutorials. | Launches deep links into targeted modules, triggers contextual helper overlays19. |
| **SubscriptionCoordinator** | Governs transactional premium gates and access criteria20. | Paywall presentation screens and dynamic purchase dialogs20. | Intercepts premium requests, caches pending routes, restores flow on transaction success20. |

## **Technical Implementations and Serialization Schemas**

Implementing compile-time safety across complex application flows requires a centralized routing schema5. The following code blocks demonstrate the concrete Kotlin configurations used to define this type-safe navigation system within the common source set1.

### **Serializable Route Definitions**

The application's route topology is represented by a sealed class hierarchy7. Each route destination is modeled as a @Serializable object or data class9.

Kotlin  
package com.architecture.navigation.routes

import kotlinx.serialization.Serializable

@Serializable  
sealed interface AppRoute {  
    // Root level routes  
    @Serializable  
    data object Onboarding : AppRoute  
      
    @Serializable  
    data object Home : AppRoute  
      
    @Serializable  
    data class Legal(val documentId: String) : AppRoute  
      
    @Serializable  
    data class Paywall(val entryPoint: String) : AppRoute

    // Study flow routes  
    @Serializable  
    data object StudyHome : AppRoute  
      
    @Serializable  
    data class LicenseSelection(val selectionType: String) : AppRoute  
      
    @Serializable  
    data class StudySession(val categoryId: String) : AppRoute  
      
    @Serializable  
    data object History : AppRoute  
      
    @Serializable  
    data object Statistics : AppRoute

    // Exam simulator flow routes  
    @Serializable  
    data class ExamSimulator(val examId: String, val isMock: Boolean) : AppRoute  
      
    @Serializable  
    data class ExamResult(val examId: String, val score: Int, val passed: Boolean) : AppRoute  
      
    @Serializable  
    data class ReviewAnswers(val examId: String) : AppRoute  
}

### **Navigation Effects and Global Modal Contracts**

ViewModels emit discrete effects, and the global presenter observes structural overlay states through the following models13.

Kotlin  
package com.architecture.navigation.effects

import com.architecture.navigation.routes.AppRoute

sealed interface NavigationEffect {  
    data object NavigateBack : NavigationEffect  
    data class NavigateTo(val route: AppRoute) : NavigationEffect  
    data class ResetTo(val route: AppRoute) : NavigationEffect  
}

sealed interface GlobalModalState {  
    data object Idle : GlobalModalState  
    data class InfoDialog(val title: String, val body: String, val actionLabel: String) : GlobalModalState  
    data class TutorialOverlay(val anchorKey: String, val stepsCount: Int) : GlobalModalState  
    data object Loading : GlobalModalState  
}

### **Coordinator Interface Definitions**

The core structural behavior of flow coordination is codified through simple interfaces14.

Kotlin  
package com.architecture.navigation.coordinators

import com.architecture.navigation.routes.AppRoute  
import androidx.navigation.NavHostController

interface Coordinator {  
    val parent: Coordinator?  
    fun navigate(route: AppRoute) {  
        parent?.navigate(route)  
    }  
}

interface HostCoordinator : Coordinator {  
    val navController: NavHostController  
}

## **The RootCoordinator and Feature Implementations**

The coordination engine initializes the core Compose NavHostController and handles parent-child routing operations14.

Kotlin  
package com.architecture.navigation.coordinators

import androidx.compose.runtime.Composable  
import androidx.navigation.compose.NavHost  
import androidx.navigation.compose.composable  
import androidx.navigation.compose.rememberNavController  
import androidx.navigation.toRoute  
import com.architecture.navigation.routes.AppRoute

class AppRootCoordinator : HostCoordinator {  
    override val parent: Coordinator? \= null  
    override lateinit var navController: NavHostController

    private val examCoordinator by lazy { ExamCoordinator(this) }  
    private val studyCoordinator by lazy { StudyCoordinator(this) }  
    private val subscriptionCoordinator by lazy { SubscriptionCoordinator(this) }

    @Composable  
    fun SetupNavigationGraph() {  
        navController \= rememberNavController()  
        val root \= this  
          
        NavHost(  
            navController \= navController,  
            startDestination \= AppRoute.Onboarding  
        ) {  
            composable\<AppRoute.Onboarding\> {  
                OnboardingScreen(  
                    onOnboardingComplete \= { root.navigate(AppRoute.Home) }  
                )  
            }  
              
            composable\<AppRoute.Home\> {  
                HomeScreen(  
                    onNavigateToStudy \= { root.navigate(AppRoute.StudyHome) },  
                    onNavigateToExam \= { id \-\>  
                        if (subscriptionCoordinator.isSubscribed()) {  
                            root.navigate(AppRoute.ExamSimulator(examId \= id, isMock \= true))  
                        } else {  
                            root.navigate(AppRoute.Paywall(entryPoint \= "exam\_start"))  
                        }  
                    },  
                    onNavigateToSettings \= { root.navController.navigate(AppRoute.History) }  
                )  
            }  
              
            composable\<AppRoute.Paywall\> { backStackEntry \-\>  
                val paywallRoute: AppRoute.Paywall \= backStackEntry.toRoute()  
                subscriptionCoordinator.RenderPaywall(paywallRoute)  
            }

            composable\<AppRoute.Legal\> { backStackEntry \-\>  
                val legalRoute: AppRoute.Legal \= backStackEntry.toRoute()  
                LegalScreen(documentId \= legalRoute.documentId)  
            }

            // Register dynamic downstream graphs  
            registerStudySubGraph(studyCoordinator)  
            registerExamSubGraph(examCoordinator)  
        }  
    }  
}

The feature graph extensions define localized configurations, allowing individual modules to register their routes independently3.

Kotlin  
package com.architecture.navigation.coordinators

import androidx.navigation.NavGraphBuilder  
import androidx.navigation.compose.composable  
import androidx.navigation.toRoute  
import com.architecture.navigation.routes.AppRoute

class ExamCoordinator(override val parent: Coordinator) : Coordinator {  
    fun finishExam(examId: String, score: Int, passed: Boolean) {  
        navigate(AppRoute.ExamResult(examId \= examId, score \= score, passed \= passed))  
    }  
      
    fun exitExamFlow() {  
        navigate(AppRoute.Home)  
    }  
}

fun NavGraphBuilder.registerExamSubGraph(coordinator: ExamCoordinator) {  
    composable\<AppRoute.ExamSimulator\> { backStackEntry \-\>  
        val examRoute: AppRoute.ExamSimulator \= backStackEntry.toRoute()  
        ExamSimulatorScreen(  
            examId \= examRoute.examId,  
            onExamCompleted \= { score, passed \-\>  
                coordinator.finishExam(examRoute.examId, score, passed)  
            },  
            onQuit \= { coordinator.exitExamFlow() }  
        )  
    }

    composable\<AppRoute.ExamResult\> { backStackEntry \-\>  
        val resultRoute: AppRoute.ExamResult \= backStackEntry.toRoute()  
        ExamResultScreen(  
            examId \= resultRoute.examId,  
            score \= resultRoute.score,  
            passed \= resultRoute.passed,  
            onReviewAnswers \= { coordinator.navigate(AppRoute.ReviewAnswers(resultRoute.examId)) },  
            onClose \= { coordinator.exitExamFlow() }  
        )  
    }

    composable\<AppRoute.ReviewAnswers\> { backStackEntry \-\>  
        val reviewRoute: AppRoute.ReviewAnswers \= backStackEntry.toRoute()  
        ReviewAnswersScreen(  
            examId \= reviewRoute.examId,  
            onBackToResults \= { coordinator.exitExamFlow() }  
        )  
    }  
}

The StudyCoordinator controls learning workflows, statistics, history modules, and the licensing selection flow.

Kotlin  
package com.architecture.navigation.coordinators

import androidx.navigation.NavGraphBuilder  
import androidx.navigation.compose.composable  
import androidx.navigation.toRoute  
import com.architecture.navigation.routes.AppRoute

class StudyCoordinator(override val parent: Coordinator) : Coordinator {  
    fun selectLicense(classId: String) {  
        navigate(AppRoute.StudySession(categoryId \= classId))  
    }  
}

fun NavGraphBuilder.registerStudySubGraph(coordinator: StudyCoordinator) {  
    composable\<AppRoute.StudyHome\> {  
        StudyHomeScreen(  
            onSelectLicenseClass \= { coordinator.navigate(AppRoute.LicenseSelection(selectionType \= "study")) },  
            onViewHistory \= { coordinator.navigate(AppRoute.History) },  
            onViewStatistics \= { coordinator.navigate(AppRoute.Statistics) }  
        )  
    }

    composable\<AppRoute.LicenseSelection\> { backStackEntry \-\>  
        val selectionRoute: AppRoute.LicenseSelection \= backStackEntry.toRoute()  
        LicenseSelectionScreen(  
            type \= selectionRoute.selectionType,  
            onClassSelected \= { classId \-\> coordinator.selectLicense(classId) }  
        )  
    }

    composable\<AppRoute.StudySession\> { backStackEntry \-\>  
        val sessionRoute: AppRoute.StudySession \= backStackEntry.toRoute()  
        StudySessionScreen(categoryId \= sessionRoute.categoryId)  
    }

    composable\<AppRoute.History\> {  
        HistoryScreen()  
    }

    composable\<AppRoute.Statistics\> {  
        StatisticsScreen()  
    }  
}

The SubscriptionCoordinator monitors and controls paywall entry configurations and transactional updates20.

Kotlin  
package com.architecture.navigation.coordinators

import androidx.compose.runtime.Composable  
import com.architecture.navigation.routes.AppRoute

class SubscriptionCoordinator(override val parent: Coordinator) : Coordinator {  
    private var isPremiumUser: Boolean \= false  
    private var pendingNavigationRoute: AppRoute? \= null

    fun isSubscribed(): Boolean {  
        return isPremiumUser  
    }

    fun executeWithPremiumGuard(targetRoute: AppRoute, onAuthorized: () \-\> Unit) {  
        if (isPremiumUser) {  
            onAuthorized()  
        } else {  
            pendingNavigationRoute \= targetRoute  
            navigate(AppRoute.Paywall(entryPoint \= "premium\_gate"))  
        }  
    }

    @Composable  
    fun RenderPaywall(route: AppRoute.Paywall) {  
        PaywallScreen(  
            entryPoint \= route.entryPoint,  
            onPurchaseSuccess \= {  
                isPremiumUser \= true  
                val destination \= pendingNavigationRoute ?: AppRoute.Home  
                pendingNavigationRoute \= null  
                navigate(destination)  
            },  
            onDismiss \= {  
                pendingNavigationRoute \= null  
                navigate(AppRoute.Home)  
            }  
        )  
    }  
}

## **Data Transmission, Lifecycle Policies, and Deep Linking**

Designing a stable navigation topology across platforms requires strict separation between route state configurations and system bundle components5.

### **Route Arguments: Complete Objects vs. Identifiers**

Passing complete domain models as arguments within route arguments is a common architectural anti-pattern7. Instead, this system mandates passing only lightweight primitive identifiers7.

\[Exam List\] \--(Passes ID "EX-99")--\> \[Exam Screen\] \--(Fetches detail from Cache/DB)--\> \[Database\]

Passing serialized objects within routes introduces three primary technical vulnerabilities:

1. **Transaction Limits:** Mobile operating systems restrict transaction arguments (such as Android’s system Bundle limits) to 1MB7. Serializing large domain objects can exceed these boundaries, leading to application crashes7.  
2. **Process Death and State Restoration:** When an application is backgrounded or undergoes a configuration change, the navigation library serializes the back stack routes6. If the state of the domain model changes in the database while the app is in the background, the restored screen will display stale or corrupted data.  
3. **Recomposition Performance:** Passing large string representations of serialized objects requires constant decoding during recomposition cycles, resulting in frame drops and performance degradation24.

Using custom NavType serializers is reserved strictly for lightweight UI configuration models, such as sorting parameters, localized display parameters, or layout configurations21.

### **Dynamic Back Stack Lifecycle Policies**

The user path is protected from loopback navigation by applying strict pop rules during major status changes27.

Onboarding  \---\>  Home (Clear Onboarding)  
ExamSimulator  \---\>  ExamResult  \---\>  ReviewAnswers  \---\>  Home (Clear Exam History) \[cite: 29\]

To prevent users from navigating back into invalid states, this architecture enforces the following rules using popUpTo and inclusive \= true27:

* **Onboarding Transitions:** When the user completes onboarding, they navigate to the Home screen, and Onboarding is popped off the back stack to prevent back-button loops18.  
* **Exam Completion:** Once the user submits their answers, they are redirected to the Results page, and the ExamSimulator is popped from the back stack18. This prevents double-submission issues or returning to a completed exam timer27.  
* **Review to Home:** When exiting the Answer Review screen, the coordinator clears all intermediate result screens, returning the user to a clean Home state28.

Kotlin  
// Execution of the Onboarding to Home transition  
navController.navigate(AppRoute.Home) {  
    popUpTo(AppRoute.Onboarding) {  
        inclusive \= true  
    }  
}

// Execution of the Exam Result transition, popping the Simulator  
navController.navigate(AppRoute.ExamResult(examId \= "exam\_01", score \= 85, passed \= true)) {  
    popUpTo\<AppRoute.ExamSimulator\> {  
        inclusive \= true  
    }  
}

// Clearing the entire sub-graph on flow exit, returning cleanly to Home  
navController.navigate(AppRoute.Home) {  
    popUpTo(AppRoute.Home) {  
        inclusive \= true  
    }  
}

### **Deep Link Integration Strategy**

The application registers deep link schemas directly inside its type-safe graph5. The Navigation library automatically parses path arguments from URIs and maps them to route attributes31.

Kotlin  
import androidx.navigation.navDeepLink  
import androidx.navigation.compose.composable

// Deep-link template parsed automatically by Jetpack Navigation mapping  
composable\<AppRoute.StudySession\>(  
    deepLinks \= listOf(  
        navDeepLink\<AppRoute.StudySession\>(  
            basePath \= "https://drivingprep.com/study/{categoryId}"  
        )  
    )  
) { backStackEntry \-\>  
    val studySession: AppRoute.StudySession \= backStackEntry.toRoute()  
    StudySessionScreen(categoryId \= studySession.categoryId)  
}

By defining deep-link configurations inside the routing declarations, platform integration is simplified: developers configure system intent filters on Android or Universal Links on iOS, and the common navigation engine automatically routes incoming URIs to the correct screens5.

## **Global Modal Host and Reusable Overlays**

To support non-disruptive, global overlays—such as tutorials, warning dialogs, and subscription alerts—this architecture utilizes a root-level overlay host33. This overlay host sits outside the main navigation stack, ensuring that displaying a global dialog does not alter the underlying back stack or interrupt active user workflows9.

Kotlin  
package com.architecture.navigation.ui

import androidx.compose.foundation.layout.Box  
import androidx.compose.foundation.layout.fillMaxSize  
import androidx.compose.runtime.Composable  
import androidx.compose.ui.Modifier  
import com.architecture.navigation.effects.GlobalModalState

@Composable  
fun GlobalModalHost(  
    modalState: GlobalModalState,  
    onDismiss: () \-\> Unit,  
    content: @Composable () \-\> Unit  
) {  
    Box(modifier \= Modifier.fillMaxSize()) {  
        // Render target base navigation UI screen  
        content()

        // Overlay dialogs natively over active layout paths  
        when (modalState) {  
            is GlobalModalState.Idle \-\> { /\* Render Nothing \*/ }  
              
            is GlobalModalState.Loading \-\> {  
                LoadingSpinnerOverlay()  
            }  
              
            is GlobalModalState.InfoDialog \-\> {  
                InfoAlertDialog(  
                    title \= modalState.title,  
                    message \= modalState.body,  
                    actionText \= modalState.actionLabel,  
                    onDismiss \= onDismiss  
                )  
            }  
              
            is GlobalModalState.TutorialOverlay \-\> {  
                ContextualTutorialCard(  
                    anchor \= modalState.anchorKey,  
                    steps \= modalState.stepsCount,  
                    onDismiss \= onDismiss  
                )  
            }  
        }  
    }  
}

This pattern is highly effective for presenting contextual help and onboarding instructions throughout the app5. By passing a target coordinates anchor identifier, feature screens can request custom instructions without introducing structural coupling to the underlying study or exam layouts3.

## **MVP Implementation Roadmap**

To transition the application to this architecture, development is divided into four distinct engineering phases3.

Phase 1: Setup & Models \---\> Phase 2: Core Routing \---\> Phase 3: Feature Graphs \---\> Phase 4: Overlays & Links

### **Phase 1: Serialization and Base Infrastructure**

Integrate the baseline navigation libraries and establish core data models5.

1. Configure dependencies in the version catalog to incorporate AndroidX Navigation 2.8.0 or higher, along with kotlinx-serialization21.  
2. Register the serialization plugin in the module-level build files19.  
3. Define the complete AppRoute sealed hierarchy in commonMain to establish type-safe route objects1.

### **Phase 2: Core Routing and Onboarding Integration**

Set up the central navigation controller and establish core screen routes9.

1. Implement the AppRootCoordinator and integrate it into the root UI composable14.  
2. Build the main onboarding and dashboard views, and implement back stack rules to prevent navigating back into the splash or onboarding sequence18.  
3. Validate gesture behaviors and back-swipe interactions across Android and iOS platforms8.

### **Phase 3: Feature Sub-Graphs and Entitlements**

Modularize the navigation architecture by implementing isolated sub-graphs and subscription rules3.

1. Create the StudyCoordinator and ExamCoordinator with isolated nested route registrations14.  
2. Code back stack reset transitions to ensure that exam simulators are popped from the stack upon completion27.  
3. Configure the SubscriptionCoordinator to gate premium features, verify entitlement states, and display paywalls dynamically20.

### **Phase 4: Overlays, Global Dialogs, and Deep Links**

Finalize platform-level integrations and implement non-disruptive UI layers5.

1. Implement the root-level GlobalModalHost to handle contextual tutorials and loading indicators cleanly on top of active screens33.  
2. Add intent filters to the Android manifest and register custom URL schemas in the iOS Info.plist configuration5.  
3. Map system link URIs to type-safe route objects to enable seamless cross-platform deep-linking30.

#### **Works cited**

1. Type-safe Navigation in KMP+CMP \- Medium, [https://medium.com/@csabhionline/type-safe-navigation-in-kmp-cmp-950887dad65a](https://medium.com/@csabhionline/type-safe-navigation-in-kmp-cmp-950887dad65a)  
2. Navigation in Kotlin Multiplatform: A Complete Guide for Modern Developers, [https://haidrrrry.medium.com/navigation-in-kotlin-multiplatform-a-complete-guide-for-modern-developers-b372743aa021](https://haidrrrry.medium.com/navigation-in-kotlin-multiplatform-a-complete-guide-for-modern-developers-b372743aa021)  
3. Exploring Safe Navigation in Jetpack Compose Multiplatform | by Kerry Bisset | Medium, [https://medium.com/@kerry.bisset/exploring-safe-navigation-in-jetpack-compose-multiplatform-b567dcbdf1bf](https://medium.com/@kerry.bisset/exploring-safe-navigation-in-jetpack-compose-multiplatform-b567dcbdf1bf)  
4. Kotlin Multiplatform: 7 Patterns for Truly Shared UIs | by Thinking Loop | Medium, [https://medium.com/@ThinkingLoop/kotlin-multiplatform-7-patterns-for-truly-shared-uis-93b48d34beba](https://medium.com/@ThinkingLoop/kotlin-multiplatform-7-patterns-for-truly-shared-uis-93b48d34beba)  
5. Scaling Compose with Adaptive UIs, Testing, and Type-Safe Navigation \- Seven Peaks, [https://sevenpeakssoftware.com/blog/scaling-compose-with-adaptive-uis-testing-and-type-safe-navigation](https://sevenpeakssoftware.com/blog/scaling-compose-with-adaptive-uis-testing-and-type-safe-navigation)  
6. Navigation 3 Jetpack compose Android | by Dayanand Chauhan \- Medium, [https://medium.com/@dayanand1531/navigation-3-jetpack-compose-android-a14beef1c239](https://medium.com/@dayanand1531/navigation-3-jetpack-compose-android-a14beef1c239)  
7. Type-safe navigation in Compose — the right way to migrate \- Angad Singh, [https://singhangad.in/blog/type-safe-navigation-compose/](https://singhangad.in/blog/type-safe-navigation-compose/)  
8. What's new in Compose Multiplatform 1.7.3 \- Kotlin, [https://kotlinlang.org/docs/multiplatform/whats-new-compose-170.html](https://kotlinlang.org/docs/multiplatform/whats-new-compose-170.html)  
9. Navigation in Compose | Kotlin Multiplatform Documentation, [https://kotlinlang.org/docs/multiplatform/compose-navigation.html](https://kotlinlang.org/docs/multiplatform/compose-navigation.html)  
10. iOS Swift Coordinator pattern and back button of Navigation Controller \- Stack Overflow, [https://stackoverflow.com/questions/54156384/ios-swift-coordinator-pattern-and-back-button-of-navigation-controller](https://stackoverflow.com/questions/54156384/ios-swift-coordinator-pattern-and-back-button-of-navigation-controller)  
11. ViewModel Management with Jetpack Compose | by Kerry Bisset \- Stackademic, [https://blog.stackademic.com/viewmodel-management-with-jetpack-compose-6951906486e6](https://blog.stackademic.com/viewmodel-management-with-jetpack-compose-6951906486e6)  
12. Navigation and routing | Kotlin Multiplatform Documentation, [https://kotlinlang.org/docs/multiplatform/compose-navigation-routing.html](https://kotlinlang.org/docs/multiplatform/compose-navigation-routing.html)  
13. Jetpack Compose navigation architecture with ViewModels | by Tom Seifert | ProAndroidDev, [https://proandroiddev.com/jetpack-compose-navigation-architecture-with-viewmodels-1de467f19e1c](https://proandroiddev.com/jetpack-compose-navigation-architecture-with-viewmodels-1de467f19e1c)  
14. iamIcarus/kotlin-compose-coordinator-example: A flexible navigation pattern for Jetpack ... \- GitHub, [https://github.com/iamIcarus/kotlin-compose-coordinator-example](https://github.com/iamIcarus/kotlin-compose-coordinator-example)  
15. MVVM \+ MVI: A Practical Guide to Unidirectional Data Flow in Android | by Débora Deotti, [https://medium.com/@debora.deotti/mvvm-mvi-a-practical-guide-to-unidirectional-data-flow-in-android-bf4f61390204](https://medium.com/@debora.deotti/mvvm-mvi-a-practical-guide-to-unidirectional-data-flow-in-android-bf4f61390204)  
16. The 2026 iOS MVVM-C Playbook: SwiftUI @Observable, Coordinators, DI & Swift 6 Concurrency \- Fora Soft, [https://www.forasoft.com/blog/article/advanced-ios-app-architecture-explained-on-mvvm-977](https://www.forasoft.com/blog/article/advanced-ios-app-architecture-explained-on-mvvm-977)  
17. Navigation via the viewmodel in Jetpack Compose : r/androiddev \- Reddit, [https://www.reddit.com/r/androiddev/comments/1l2ow7e/navigation\_via\_the\_viewmodel\_in\_jetpack\_compose/](https://www.reddit.com/r/androiddev/comments/1l2ow7e/navigation_via_the_viewmodel_in_jetpack_compose/)  
18. Navigation in Android Jetpack Compose: A Practical Guide | by Sanjay Nelagadde, [https://levelup.gitconnected.com/navigation-in-android-jetpack-compose-a-practical-guide-4d8037b07a87](https://levelup.gitconnected.com/navigation-in-android-jetpack-compose-a-practical-guide-4d8037b07a87)  
19. Building a Fully Adaptive Navigation System in Compose Multiplatform (KMP) | by Meet, [https://proandroiddev.com/building-a-fully-adaptive-navigation-system-in-compose-multiplatform-kmp-e1a8921ca792](https://proandroiddev.com/building-a-fully-adaptive-navigation-system-in-compose-multiplatform-kmp-e1a8921ca792)  
20. Building Scalable Navigation System in Android | by Ahmed Abdelmeged \- ProAndroidDev, [https://proandroiddev.com/building-scalable-navigation-system-in-android-192bab6ddba3](https://proandroiddev.com/building-scalable-navigation-system-in-android-192bab6ddba3)  
21. Guide: Migrating to Type-Safe Navigation in Compose and Navigation 2 | App architecture, [https://developer.android.com/guide/navigation/type-safe-destinations](https://developer.android.com/guide/navigation/type-safe-destinations)  
22. Type safety in Kotlin DSL and Navigation Compose | App architecture \- Android Developers, [https://developer.android.com/guide/navigation/design/type-safety](https://developer.android.com/guide/navigation/design/type-safety)  
23. Jetpack compose navigation with custom NavType | by Yves Kalume | ProAndroidDev, [https://proandroiddev.com/jetpack-compose-navigation-with-custom-navtype-9b44dd8820e](https://proandroiddev.com/jetpack-compose-navigation-with-custom-navtype-9b44dd8820e)  
24. Thinking in Compose | Jetpack Compose \- Android Developers, [https://developer.android.com/develop/ui/compose/mental-model](https://developer.android.com/develop/ui/compose/mental-model)  
25. How to pass a custom Parcelable object in Jetpack Compose Type-Safe Navigation (Navigation 2.8.0+)? \- Stack Overflow, [https://stackoverflow.com/questions/79939630/how-to-pass-a-custom-parcelable-object-in-jetpack-compose-type-safe-navigation](https://stackoverflow.com/questions/79939630/how-to-pass-a-custom-parcelable-object-in-jetpack-compose-type-safe-navigation)  
26. Navigation Compose meet Type Safety | by Ian Lake | Android Developers \- Medium, [https://medium.com/androiddevelopers/navigation-compose-meet-type-safety-e081fb3cf2f8](https://medium.com/androiddevelopers/navigation-compose-meet-type-safety-e081fb3cf2f8)  
27. Circular navigation | App architecture \- Android Developers, [https://developer.android.com/guide/navigation/backstack/circular](https://developer.android.com/guide/navigation/backstack/circular)  
28. Navigation and the back stack | App architecture | Android Developers, [https://developer.android.com/guide/navigation/backstack](https://developer.android.com/guide/navigation/backstack)  
29. Mastering Navigation in Jetpack Compose: A Guide to Using the inclusive Attribute | by Leo N | ProAndroidDev, [https://proandroiddev.com/mastering-navigation-in-jetpack-compose-a-guide-to-using-the-inclusive-attribute-b66916a5f15c](https://proandroiddev.com/mastering-navigation-in-jetpack-compose-a-guide-to-using-the-inclusive-attribute-b66916a5f15c)  
30. Deep links | Kotlin Multiplatform Documentation, [https://kotlinlang.org/docs/multiplatform/compose-navigation-deep-links.html](https://kotlinlang.org/docs/multiplatform/compose-navigation-deep-links.html)  
31. A repo implementing the newly released type safe compose navigation \- GitHub, [https://github.com/ndiritumichael/Type-safe-Compose-Navigation](https://github.com/ndiritumichael/Type-safe-Compose-Navigation)  
32. Type Safe Deep Links in Compose \- Medium, [https://medium.com/@domen.lanisnik/type-safe-deep-links-in-compose-5156fc6fe650](https://medium.com/@domen.lanisnik/type-safe-deep-links-in-compose-5156fc6fe650)  
33. Dialog destinations | App architecture \- Android Developers, [https://developer.android.com/guide/navigation/design/dialog-destinations](https://developer.android.com/guide/navigation/design/dialog-destinations)  
34. DialogHost API Reference in Navigation Compose, [https://composables.com/jetpack-compose/androidx.navigation/navigation-compose/composable-functions/DialogHost/api](https://composables.com/jetpack-compose/androidx.navigation/navigation-compose/composable-functions/DialogHost/api)  
35. Dialog Recipe \- App architecture | Android Developers, [https://developer.android.com/guide/navigation/navigation-3/recipes/dialog](https://developer.android.com/guide/navigation/navigation-3/recipes/dialog)  
36. Jetpack Navigation 3: A New Era for Navigation in Compose-Driven Android Apps \- DZone, [https://dzone.com/articles/jetpack-navigation-3-compose-android-guide](https://dzone.com/articles/jetpack-navigation-3-compose-android-guide)  
37. Jetpack Compose Navigation \- Speednet, [https://speednetsoftware.com/jetpackcompose-navigation/](https://speednetsoftware.com/jetpackcompose-navigation/)