# **Architecture Decision Record: Kotlin Multiplatform Mobile System Architecture**

## **Executive Summary**

This architecture decision record formalizes the engineering blueprint for a multiplatform mobile application targeting iOS and Android. The selected design utilizes Kotlin Multiplatform for cross-platform business logic and Compose Multiplatform for a shared declarative user interface1. This foundation is supported by a Clean Architecture-lite layout, Model-View-ViewModel with Unidirectional Data Flow, a decoupled Coordinator pattern for routing, SQLDelight for local-first reactive storage, Ktor for platform-agnostic networking, and Koin for dependency injection3.  
This report provides a comprehensive analysis of the selected stack, compares it against seven alternative configurations, defines clear layer boundaries, outlines strict dependency rules, identifies development anti-patterns, and maps out a reactive data-write lifecycle.

## **Technical Context and Chosen System Architecture**

Developing a cross-platform mobile app requires balancing fast feature delivery with long-term code maintainability. This architecture is designed to maximize code reuse across Android and iOS without compromising platform performance or system stability1.  
By using Kotlin Multiplatform as the base and Compose Multiplatform for the user interface, the system achieves over 90% code reuse1. Compose Multiplatform on iOS utilizes the Skia graphics engine via Skiko to render UI elements directly to a Metal-backed canvas, ensuring visual consistency across platforms and eliminating the need to write separate UI codebases in Jetpack Compose and SwiftUI1.

\+-----------------------------------------------------------------------------------+  
|                                     UI LAYER                                      |  
|    Compose Multiplatform Views (Renders via Skia/Skiko directly to Metal/OpenGL)    |  
\+-----------------------------------------------------------------------------------+  
                                          |  
                                          | Enpatches User Events  
                                          v  
\+-----------------------------------------------------------------------------------+  
|                            PRESENTATION & ROUTING LAYER                           |  
|      TaskCoordinator (Navigation Flow)  \<---\>  TaskViewModel (UDF State Holder)   |  
\+-----------------------------------------------------------------------------------+  
                                          |  
                                          | Executes Business Use Case  
                                          v  
\+-----------------------------------------------------------------------------------+  
|                                 DOMAIN LAYER (LITE)                               |  
|        TaskUseCase (Business Rules)  \<---\>  TaskRepository (Abstract Gateway)     |  
\+-----------------------------------------------------------------------------------+  
                                          |  
                                          | Maps to Implementation  
                                          v  
\+-----------------------------------------------------------------------------------+  
|                                     DATA LAYER                                    |  
|   TaskRepositoryImpl (Source of Truth)  \<---\>  SQLDelight Cache  / Ktor Client    |  
\+-----------------------------------------------------------------------------------+

To prevent the system from becoming bogged down by excessive boilerplate, a Clean Architecture-lite layout is used11. This pattern maintains a clean separation of concerns while permitting direct ViewModel-to-Repository interaction for basic CRUD features, reserving dedicated Use Case classes for complex business logic11.  
The presentation layer combines MVVM and Unidirectional Data Flow4. The ViewModel processes incoming events and exposes an immutable, single source of truth StateFlow to the Compose views1. This prevents common UI synchronization issues and simplifies state tracking1.  
Navigation is handled by the Coordinator pattern, which separates routing logic from individual UI views3. The coordinator manages the screen stack and handles view transitions3.  
This decoupling makes the navigation layer highly adaptable17. If the application later needs to support platform-specific UI patterns—such as the native iOS "Liquid Glass" tab bar transitions—the navigation layer can be redirected to a native SwiftUI shell17. The SwiftUI coordinator can then wrap the shared Compose screens inside individual host view controllers, providing a native look and feel without requiring changes to the core business logic17.  
The data layer uses a local-first architecture powered by SQLDelight and Ktor7. SQLDelight compiles standard SQL files into type-safe Kotlin interfaces, catching database query and migration errors at compile time rather than at runtime8.  
SQLDelight also supports reactive database queries6. When a write operation updates a table, active query listeners automatically receive an invalidation signal and emit the updated data down the pipeline6. Ktor handles remote network communications in a platform-agnostic manner, using Kotlinx Serialization to process JSON responses on background threads22.  
Koin manages dependency injection across the system5. Relying on a pure Kotlin DSL rather than complex runtime code generation, Koin keeps compilation times fast5. Using the koin-compose-viewmodel library, ViewModels can be defined in shared code and injected directly into Composable views, aligning their lifecycles automatically with the host platform5.

## **Architectural Comparison Matrix**

The table below evaluates the selected architecture against seven recognized alternatives across eight key engineering metrics.

| Architecture Option | Complexity | Learning Curve | iOS Feel | Android Feel | Dev Speed | Long-term Maintainability | Testability | Solo/Small Team Risk |
| :---- | :---- | :---- | :---- | :---- | :---- | :---- | :---- | :---- |
| **Chosen: CMP \+ Clean-Lite \+ MVVM (UDF) \+ Coordinator** | Moderate | Moderate | High | Native | Fast | Very High | High | Low |
| **1\. Shared Business Logic \+ Native UI** | High | Low | Native | Native | Slow | High | High | Moderate |
| **2\. Pure Compose Multiplatform (CMP Nav)** | Low | Low | Moderate | Native | Very Fast | Moderate | Moderate | Low |
| **3\. Simple Layered Architecture** | Very Low | Very Low | Moderate | Native | Very Fast | Low | Low | High |
| **4\. Strict MVI (MVIKotlin/Ballast)** | High | High | Moderate | Native | Slow | High | Very High | Moderate |
| **5\. Decompose / Component Architecture** | Very High | High | Native | Native | Slow | Very High | Very High | High |
| **6\. Voyager Navigation** | Low | Low | Moderate | Native | Fast | Moderate | Moderate | Moderate |
| **7\. Official Compose Navigation (Nav3)** | Moderate | Moderate | Moderate | Native | Fast | High | Moderate | Low |

## **Comprehensive Evaluation of Alternatives**

### **Alternative 1: Shared Business Logic Only \+ Native UI (SwiftUI / Jetpack Compose)**

This approach restricts code sharing to the data, domain, and presentation layers (such as shared ViewModels), while requiring the user interface to be built twice: once in Jetpack Compose for Android, and once in SwiftUI for iOS1.

* **Complexity and Learning Curve**: The architecture relies on KMP-to-Swift compilation via Objective-C or Swift bindings, which introduces integration complexity27. However, the learning curve remains low because developers write platform UIs using standard, familiar frameworks1.  
* **Platform Feel (iOS & Android)**: This model offers the highest level of platform polish2. SwiftUI transitions, system gestures, and physics animations run directly on native rendering stacks with zero abstraction overhead2.  
* **Development Speed and Small Team Risk**: Development is slow because every layout change, state binding, and custom animation must be implemented and tested twice2. For a solo developer or small team, maintaining two separate UI codebases can easily become a major bottleneck10.  
* **Maintainability and Testability**: Shared business logic remains highly testable through standard Kotlin unit tests10. However, maintainability can degrade as platform-specific binding layers (such as Combine on iOS and StateFlow on Android) diverge over time26.

### **Alternative 2: Full Shared UI with Compose Multiplatform (Pure CMP Navigation)**

This strategy places both the UI views and the navigation stack entirely in the shared commonMain source set, running the application inside a single host container on both platforms1.

* **Complexity and Learning Curve**: This is one of the simplest KMP configurations, requiring almost no platform-specific code or custom interop wrappers25.  
* **Platform Feel (iOS & Android)**: Android performance is native1. However, the iOS user experience can feel slightly unpolished2. Because Compose renders directly to a Skia canvas, native system behaviors—such as spring physics, edge swipe-to-back gestures, and dynamic text accessibility—must be approximated in code1.  
* **Development Speed and Small Team Risk**: This approach provides rapid development speeds1. Layouts, transitions, and business logic are written once in Kotlin, making it an ideal choice for validating prototypes and launching MVPs quickly1.  
* **Maintainability and Testability**: A single shared codebase is highly maintainable, but can become difficult to manage if the application later requires deep integration with native iOS components (such as Apple Maps or system-level sheet presenters)1.

### **Alternative 3: Simple Layered Architecture**

This structure organizes the codebase into a simple set of horizontal layers (such as UI, Business Logic, and Database) without the strict boundaries, domain interfaces, or data mappers used in Clean Architecture4.

* **Complexity and Learning Curve**: Extremely low complexity and highly intuitive12. Features are built quickly without needing to manage complex abstractions or design patterns11.  
* **Platform Feel (iOS & Android)**: Visual quality is determined by Compose Multiplatform's rendering engine, which provides a standard cross-platform appearance1.  
* **Development Speed and Small Team Risk**: Initial development is fast11. However, the risk of technical debt grows quickly12. As features are added, the lack of strict boundaries often leads to tightly coupled code, making the app fragile and difficult to update11.  
* **Maintainability and Testability**: Long-term maintainability is low11. Mixing business logic with database access and UI views makes isolating and testing components in unit tests highly challenging11.

### **Alternative 4: Strict MVI Architecture (MVIKotlin / Ballast)**

Strict MVI architectures (such as MVIKotlin or Ballast) treat the entire application state as a formal, deterministic state machine7. Every user interaction is modeled as a discrete "Intent", which passes through a centralized reducer to produce a new, immutable "State"4.

* **Complexity and Learning Curve**: High. The pattern requires a strict separation of states, intents, side effects, and reducers, which introduces significant boilerplate and demands a strong understanding of reactive programming concepts11.  
* **Platform Feel (iOS & Android)**: Renders using Compose Multiplatform, while the state machine runs in KMP1. This ensures predictable updates and prevents impossible UI states4.  
* **Development Speed and Small Team Risk**: Slow. The significant boilerplate required to define and map states and intents for every screen can slow down feature delivery, making it less practical for solo developers or small teams11.  
* **Maintainability and Testability**: Highly maintainable and testable4. The pure input-to-output nature of reducer functions allows for robust unit testing4.

### **Alternative 5: Decompose / Component Architecture**

Decompose is a specialized multiplatform library that breaks down application logic into a tree structure of lifecycle-aware business components (commonly referred to as Business Logic Components, or BLoCs)1. It handles both navigation and dependency injection independently of any specific UI framework1.

* **Complexity and Learning Curve**: Very High. The library requires developers to completely restructure how they manage component lifecycles, navigation routing, and constructor dependency injections36.  
* **Platform Feel (iOS & Android)**: Native navigation behavior can be achieved on both platforms35. Because routing and lifecycles are decoupled from the UI framework, Decompose can easily coordinate native platform transitions on both iOS and Android35.  
* **Development Speed and Small Team Risk**: Slow. Setting up component contexts, handling child configurations, and managing dependency structures requires significant boilerplate36. The high complexity can easily slow down progress for a small engineering team36.  
* **Maintainability and Testability**: Excellent. Component trees are highly decoupled, making the codebase modular, easy to scale, and straightforward to validate using pure unit tests37.

### **Alternative 6: Voyager Navigation**

Voyager is a pragmatic, Compose-first navigation library designed specifically for Compose Multiplatform, using a simple Screen-based routing API1.

* **Complexity and Learning Curve**: Very low complexity with a gentle learning curve36. Developers define screens using simple Composable interfaces, making it easy to set up routing38.  
* **Platform Feel (iOS & Android)**: Moderate. Screen transitions are rendered within the Compose canvas, which can feel slightly less polished on iOS compared to native transitions18.  
* **Development Speed and Small Team Risk**: Fast35. However, Voyager carries some technical risk for long-term projects36. The library's smaller maintenance ecosystem and historical issues with features like SavedStateHandle integration can introduce stability challenges37. Additionally, Voyager's TabNavigator lacks native backstack support, requiring developers to manually handle complex back-press events39.  
* **Maintainability and Testability**: Moderate. Voyager is convenient for smaller projects, but its tight coupling to the Compose UI layer makes testing navigation paths independently of the UI highly challenging39.

### **Alternative 7: Official Compose Navigation (Navigation 3 / Nav3)**

The official Jetpack Navigation library, adapted for Compose Multiplatform by JetBrains, uses type-safe routes serialized via kotlinx.serialization40.

* **Complexity and Learning Curve**: Moderate40. Setting up navigation requires defining serialized data classes or objects for every route, along with a centralized navigation host40.  
* **Platform Feel (iOS & Android)**: Moderate. Transition animations are rendered in the shared canvas, but the library automatically translates basic back-swipe gestures on iOS and physical back button presses on Android40.  
* **Development Speed and Small Team Risk**: Fast, with low integration risk36. Backed directly by Google and JetBrains, Nav3 is built on user-owned serializable screen backstacks that simplify multiplatform layout adjustments10.  
* **Maintainability and Testability**: Long-term maintainability is high due to strong ecosystem backing41. However, testing deep routing paths can be difficult, as navigation remains tied to the UI framework40.

### **Rationale for the Chosen Architecture**

The chosen architecture is designed to balance developer productivity, system testability, and long-term maintainability.

               \+----------------------------------------+  
               |        TASK SYSTEM ARCHITECTURE        |  
               \+----------------------------------------+  
                                   |  
        \+--------------------------+--------------------------+  
        |                                                     |  
        v                                                     v  
\+------------------------------------+                \+------------------------------------+  
|         PRESENTATION & UI          |                |         DATA PERSISTENCE           |  
\+------------------------------------+                \+------------------------------------+  
| \* Compose Multiplatform Shared UI  |                | \* SQLDelight Local-First Database  |  
| \* MVVM \+ Unidirectional Data Flow  |                | \* Ktor Extensible Network Client   |  
| \* TaskCoordinator Screen Routing   |                | \* Koin DSL Dependency Injection   |  
\+------------------------------------+                \+------------------------------------+

Rather than building the user interface twice, Compose Multiplatform allows the team to maintain a single UI codebase, drastically increasing development speed1. Integrating MVVM and Unidirectional Data Flow prevents impossible UI states and ensures the UI remains highly responsive1.  
The Coordinator pattern keeps navigation logic separate from individual screens3. This ensures the app's routing behavior remains highly adaptable, allowing developers to implement platform-specific navigation flows in the future without needing to restructure the core business logic17.  
For data persistence, SQLDelight's compile-time verification catches query errors early in the build cycle, protecting the app from runtime database crashes8. This is paired with Ktor's asynchronous networking to support efficient background execution and future cloud synchronization22. Finally, Koin's lightweight DSL simplifies dependency management across platforms, keeping build times fast and deployment straightforward5.

## **Formal System Design and Layer Responsibilities**

### **Presentation Layer (Compose Multiplatform & ViewModels)**

The presentation layer is responsible for rendering the user interface and managing UI-related state4. It contains zero business logic, relying entirely on ViewModels and Coordinators to coordinate views and handle navigation3.

* **TaskScreen (Composable)**: A declarative view that renders the UI based on the current state1. It registers user gestures and passes them as actions to the ViewModel, updating itself automatically when the ViewModel emits a new state4.  
* **TaskViewModel**: Serves as the state machine for the screen4. It processes incoming actions, communicates with use cases or repositories, and exposes an immutable state flow1.  
* **TaskCoordinator**: Manages screen navigation, instantiates view controllers, and coordinates transitions independently of the UI views3.

### **Domain Layer (Lite \- Pure Kotlin)**

The domain layer contains the pure, framework-free business rules and core models of the application13. It has no dependencies on database libraries, networking tools, or UI frameworks30.

* **Task (Domain Model)**: A simple Kotlin data class representing a task.  
* **TaskUseCase**: An optional component containing specific business rules, such as validating user inputs or sorting task lists30.  
* **TaskRepository (Interface)**: Defines the data-access contract used by the domain layer, which the data layer must implement13.

### **Data Layer (Repositories & Drivers)**

The data layer is responsible for managing data persistence and network communication, exposing clean domain models to the upper layers of the application7.

* **TaskRepositoryImpl**: Implements the domain repository interface13. It serves as the single source of truth, coordinating local cache databases and remote network APIs7.  
* **SQLDelight Data Source**: Handles database writes, manages migrations, and exposes reactive query listeners to stream updates automatically6.  
* **Ktor Network Client**: Manages remote API requests, parses JSON payloads on background threads, and handles common API requirements such as connection timeouts and authentication22.

## **Technical Dependency and Architecture Rules**

\+--------------------------------------------------------------------------+  
|  Presentation Layer (Compose UI, Coordinators, ViewModels)               |  
\+--------------------------------------------------------------------------+  
       |                                                           |  
       | Depends on                                                | Depends on  
       v                                                           v  
\+-------------------------------------------------+        \+---------------+  
|  Domain Layer (Domain Models, Interfaces)       | \<----- |  Data Layer   |  
\+-------------------------------------------------+        \+---------------+  
                                                Depends on  (Repositories,   
                                                            SQLDelight, Ktor)

To ensure strict separation of concerns, compile-time dependencies must follow a single direction:

* **Domain Isolation**: The domain layer is completely independent30. It must never depend on any other layer or platform-specific APIs30.  
* **Data-to-Domain Flow**: The data layer depends on the domain layer13. Repositories in the data layer implement the abstract interfaces defined in the domain13.  
* **Presentation-to-Domain Flow**: ViewModels and Coordinators in the presentation layer depend directly on the domain layer to execute business rules and use core domain models13.  
* **Data-Presentation Decoupling**: The presentation layer must never have a direct compile-time dependency on the concrete implementations of the data layer12. All data access must occur through the abstract interfaces defined in the domain layer, with Koin handling the dependency injection at runtime12.

## **Architectural Anti-patterns to Avoid**

* **Tightly Coupling Navigation to UI Views**: Avoid hardcoding navigation actions directly inside Composable views3. Doing so makes it difficult to reuse screens across different user flows or switch to native platform transitions in the future3.  
* **Monolithic ViewModels**: ViewModels should not contain database operations, raw network calls, or direct navigation logic4. They must delegate these tasks to Repositories and Coordinators, keeping the ViewModel focused on managing UI state4.  
* **Leaking Database Entities into the UI**: Database schema classes generated by SQLDelight should remain isolated within the data layer13. Passing raw database models directly to the UI couples the views to the database schema, making schema migrations difficult to implement without breaking the UI8.  
* **Blocking the Main Thread with Database Queries**: SQLite operations must run on background dispatchers, such as Dispatchers.Default43. Performing database queries on the main thread will block the UI, causing visible frame drops and lag43.  
* **Writing Duplicate UI Code for Each Platform**: Avoid writing custom native UI implementations for standard layouts1. Expect/Actual declarations should be reserved for platform-specific system APIs (such as Bluetooth, biometric authentication, or native share sheets)2.

## **Concrete Package Structure**

The package structure below separates the codebase into logical modules and layers, ensuring clear compile-time boundaries across KMP targets1.

shared/  
├── src/  
│   ├── commonMain/  
│   │   └── kotlin/  
│   │       └── com/  
│   │           └── app/  
│   │               ├── di/  
│   │               │   └── Koin.kt                 \# Shared Koin dependency configurations  
│   │               ├── domain/  
│   │               │   ├── model/  
│   │               │   │   └── Task.kt             \# Pure, framework-free Domain Models  
│   │               │   └── repository/  
│   │               │       └── TaskRepository.kt   \# Abstract Repository Interfaces  
│   │               ├── data/  
│   │               │   ├── cache/  
│   │               │   │   └── SqlDelightTaskDataSource.kt \# SQLDelight implementation  
│   │               │   ├── network/  
│   │               │   │   ├── KtorTaskApi.kt      \# Ktor Networking implementation  
│   │               │   │   └── model/  
│   │               │   │       └── TaskDto.kt      \# Network Data Transfer Objects  
│   │               │   └── repository/  
│   │               │       └── TaskRepositoryImpl.kt \# Concrete Repository \[cite: 7, 25\]  
│   │               └── presentation/  
│   │                   ├── navigation/  
│   │                   │   ├── TaskCoordinator.kt  \# Routing and Navigation Coordinator  
│   │                   │   └── TaskRoutes.kt       \# Type-safe Navigation Routes \[cite: 45\]  
│   │                   └── task/  
│   │                       ├── TaskViewModel.kt    \# UI State-Machine (MVVM \+ UDF)  
│   │                       ├── TaskUiState.kt      \# Immutable View States  
│   │                       └── TaskScreen.kt       \# Shared UI views (Compose)  
│   ├── iosMain/  
│   │   └── kotlin/  
│   │       └── com/  
│   │           └── app/  
│   │               └── di/  
│   │                   └── PlatformModule.ios.kt   \# iOS Database Driver configuration \[cite: 22, 46\]  
│   └── androidMain/  
│       └── kotlin/  
│           └── com/  
│               └── app/  
│                   └── di/  
│                       └── PlatformModule.android.kt \# Android Database Driver configuration \[cite: 22, 46\]

## **Data Flow: UI Click to SQLDelight Database Write**

The section below traces the complete lifecycle of a write operation, demonstrating how a user action flows from a Compose view, through the ViewModel, and down to SQLDelight3.

### **Step 1: User Actions in the Composable View**

The user taps a checkbox to toggle task completion, dispatching an event directly to the ViewModel4:

Kotlin  
// presentation/task/TaskScreen.kt  
package com.app.presentation.task

import androidx.compose.foundation.lazy.LazyColumn  
import androidx.compose.foundation.lazy.items  
import androidx.compose.material3.Checkbox  
import androidx.compose.material3.Text  
import androidx.compose.runtime.Composable  
import androidx.compose.runtime.collectAsState  
import androidx.compose.runtime.getValue  
import androidx.compose.ui.Modifier  
import koinViewModel // Injected using Koin

@Composable  
fun TaskScreen(  
    viewModel: TaskViewModel \= koinViewModel()  
) {  
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn {  
        items(uiState.taskList) { task \-\>  
            Checkbox(  
                checked \= task.isCompleted,  
                onCheckedChange \= {   
                    // Dispatches UI events directly to the ViewModel  
                    viewModel.onAction(TaskAction.ToggleTask(task.id))   
                }  
            )  
            Text(text \= task.title)  
        }  
    }  
}

### **Step 2: Event Handling in the ViewModel**

The ViewModel processes the action, launches a non-blocking coroutine, and calls the repository to update the database on a background thread4:

Kotlin  
// presentation/task/TaskViewModel.kt  
package com.app.presentation.task

import androidx.lifecycle.ViewModel  
import androidx.lifecycle.viewModelScope  
import com.app.domain.repository.TaskRepository  
import kotlinx.coroutines.flow.MutableStateFlow  
import kotlinx.coroutines.flow.StateFlow  
import kotlinx.coroutines.flow.asStateFlow  
import kotlinx.coroutines.flow.update  
import kotlinx.coroutines.launch

class TaskViewModel(  
    private val repository: TaskRepository  
) : ViewModel() {

    private val \_uiState \= MutableStateFlow(TaskUiState())  
    val uiState: StateFlow\<TaskUiState\> \= \_uiState.asStateFlow()

    init {  
        observeTasks()  
    }

    fun onAction(action: TaskAction) {  
        when (action) {  
            is TaskAction.ToggleTask \-\> {  
                // Launches a coroutine in the background \[cite: 10, 26\]  
                viewModelScope.launch {  
                    repository.toggleTaskCompletion(action.taskId)  
                }  
            }  
        }  
    }

    private fun observeTasks() {  
        // Automatically listens for database updates  
        viewModelScope.launch {  
            repository.observeAllTasks().collect { tasks \-\>  
                \_uiState.update { it.copy(taskList \= tasks, isLoading \= false) }  
            }  
        }  
    }  
}

### **Step 3: Persistence and Update Loop in the Repository**

The repository implements the domain interface13. It maps the domain model to a database entity, processes the write query, and updates the local SQLite database7:

Kotlin  
// data/repository/TaskRepositoryImpl.kt  
package com.app.data.repository

import com.app.domain.model.Task  
import com.app.domain.repository.TaskRepository  
import com.app.data.cache.AppDatabase // SQLDelight generated class \[cite: 8, 22\]  
import kotlinx.coroutines.CoroutineDispatcher  
import kotlinx.coroutines.Dispatchers  
import kotlinx.coroutines.flow.Flow  
import kotlinx.coroutines.flow.map  
import kotlinx.coroutines.withContext  
import app.cash.sqldelight.coroutines.asFlow  
import app.cash.sqldelight.coroutines.mapToList

class TaskRepositoryImpl(  
    private val database: AppDatabase,  
    private val ioDispatcher: CoroutineDispatcher \= Dispatchers.Default // Offloads thread execution  
) : TaskRepository {

    override fun observeAllTasks(): Flow\<List\<Task\>\> {  
        // Listens for table updates and emits updated records  
        return database.taskQueries.selectAllTasks()  
            .asFlow()  
            .mapToList(ioDispatcher)  
            .map { list \-\> list.map { Task(id \= it.id, title \= it.title, isCompleted \= it.completed \== 1L) } }  
    }

    override suspend fun toggleTaskCompletion(taskId: String) {  
        withContext(ioDispatcher) {  
            database.transaction {  
                val task \= database.taskQueries.selectTaskById(taskId).executeAsOne()  
                // Executes the write query to SQLite disk storage \[cite: 20, 43\]  
                database.taskQueries.updateTaskCompletion(  
                    completed \= if (task.completed \== 1L) 0L else 1L,  
                    id \= taskId  
                )  
            }  
        }  
    }  
}

### **Step 4: Reactive Update Cycle**

The SQLDelight driver processes the SQLite write query8. Once the write is complete, the driver sends an invalidation signal to all active query listeners6.  
The active Flow query in the repository detects this signal, re-runs the SELECT query, and streams the updated data up to the ViewModel6. The ViewModel receives the new records, updates its immutable TaskUiState, and emits the state change1. The Composable view observes this state update and triggers a visual recomposition, displaying the updated task list to the user1. Enforcing this loop ensures the UI always displays accurate data, preventing common visual synchronization errors1. Enforcing this strict Unidirectional Data Flow ensures the UI remains predictable, testable, and highly responsive4.

#### **Works cited**

1. Kotlin Multiplatform (KMP): The Ultimate Guide (2026) \- commonMain.dev, [https://commonmain.dev/kotlin-multiplatform/](https://commonmain.dev/kotlin-multiplatform/)  
2. Flutter vs. Kotlin Multiplatform: 2026 Architecture Guide \- Shorebird, [https://shorebird.dev/blog/flutter-vs-kotlin-multiplatform](https://shorebird.dev/blog/flutter-vs-kotlin-multiplatform)  
3. iamIcarus/kotlin-compose-coordinator-example: A flexible navigation pattern for Jetpack ... \- GitHub, [https://github.com/iamIcarus/kotlin-compose-coordinator-example](https://github.com/iamIcarus/kotlin-compose-coordinator-example)  
4. Understanding Android App Architecture: MVVM vs. MVI | by Mudasir Mukhtar \- Medium, [https://medium.com/@mudasir.chohan/understanding-android-app-architecture-mvvm-vs-mvi-9f01f066a618](https://medium.com/@mudasir.chohan/understanding-android-app-architecture-mvvm-vs-mvi-9f01f066a618)  
5. Dependency Injection in Compose Multiplatform with Koin: A Modern Approach \- Medium, [https://medium.com/@prakash\_ranjan/dependency-injection-in-compose-multiplatform-with-koin-a-modern-approach-e696415bdcaf](https://medium.com/@prakash_ranjan/dependency-injection-in-compose-multiplatform-with-koin-a-modern-approach-e696415bdcaf)  
6. SQLiteNow 0.9.0 for KMP: SQLite-first codegen, reactive flows, and optional sync \- Reddit, [https://www.reddit.com/r/androiddev/comments/1taytt5/sqlitenow\_090\_for\_kmp\_sqlitefirst\_codegen/](https://www.reddit.com/r/androiddev/comments/1taytt5/sqlitenow_090_for_kmp_sqlitefirst_codegen/)  
7. GitHub \- momintahir/KMP-News-App: This application demonstrates modern Android development with Koin, Ktor, Coroutines, Flows, SQLDelight, Voyager based on Clean Architecture., [https://github.com/momintahir/KMP-News-App](https://github.com/momintahir/KMP-News-App)  
8. Local Database: Comparing Realm, SQLDelight, and Room. | by Tosin (Matt) Onikute, [https://proandroiddev.com/which-local-database-should-you-choose-in-2025-comparing-realm-sqldelight-and-room-4221b354c899](https://proandroiddev.com/which-local-database-should-you-choose-in-2025-comparing-realm-sqldelight-and-room-4221b354c899)  
9. One Codebase to Rule Them All: Cross-Platform Apps with Kotlin and Compose Multiplatform | by Andrea Della Porta | Stackademic, [https://blog.stackademic.com/one-codebase-to-rule-them-all-cross-platform-apps-with-kotlin-and-compose-multiplatform-14035b6fccc5](https://blog.stackademic.com/one-codebase-to-rule-them-all-cross-platform-apps-with-kotlin-and-compose-multiplatform-14035b6fccc5)  
10. GSOC 2026:- Compose Multiplatform Migration \- MetaBrainz Community Discourse, [https://community.metabrainz.org/t/gsoc-2026-compose-multiplatform-migration/814478](https://community.metabrainz.org/t/gsoc-2026-compose-multiplatform-migration/814478)  
11. MVVM vs MVI vs Clean Architecture: Android Guide 2025 \- Medium, [https://medium.com/@hiren6997/the-architecture-war-why-73-of-android-apps-choose-wrong-and-how-to-pick-the-right-one-76008986a2ab](https://medium.com/@hiren6997/the-architecture-war-why-73-of-android-apps-choose-wrong-and-how-to-pick-the-right-one-76008986a2ab)  
12. Clean Architecture on Android and KMP: A pragmatic template | by Marco Salis | Medium, [https://medium.com/@marco.salis/clean-architecture-on-android-and-kmp-a-pragmatic-template-b17468c99a70](https://medium.com/@marco.salis/clean-architecture-on-android-and-kmp-a-pragmatic-template-b17468c99a70)  
13. How to make a Clean Architecture App with Kotlin and Compose Multiplatform \- Reddit, [https://www.reddit.com/r/androiddev/comments/191mhj7/how\_to\_make\_a\_clean\_architecture\_app\_with\_kotlin/](https://www.reddit.com/r/androiddev/comments/191mhj7/how_to_make_a_clean_architecture_app_with_kotlin/)  
14. Understanding MVVM and MVI Architectures in Android Kotlin Development: A Comparative Analysis | by Mami Dağ | Medium, [https://medium.com/@mamidag6/understanding-mvvm-and-mvi-architectures-in-android-kotlin-development-a-comparative-analysis-cd818ed22b75](https://medium.com/@mamidag6/understanding-mvvm-and-mvi-architectures-in-android-kotlin-development-a-comparative-analysis-cd818ed22b75)  
15. Coordinator \- Compose UI Architecture, [https://levinzonr.github.io/compose-ui-arch-docs/coordinator/](https://levinzonr.github.io/compose-ui-arch-docs/coordinator/)  
16. In-App Navigation with Coordinators : r/androiddev \- Reddit, [https://www.reddit.com/r/androiddev/comments/8hfxtq/inapp\_navigation\_with\_coordinators/](https://www.reddit.com/r/androiddev/comments/8hfxtq/inapp_navigation_with_coordinators/)  
17. Liquid Glass in a Compose Multiplatform app \- Kotlin, [https://kotlinlang.org/docs/multiplatform/ios-liquid-glass.html](https://kotlinlang.org/docs/multiplatform/ios-liquid-glass.html)  
18. Compose navigation is really what lets iOS down when buildin kotlinlang \#compose-ios \- Kotlin Slack, [https://slack-chats.kotlinlang.org/t/30070609/compose-navigation-is-really-what-lets-ios-down-when-buildin](https://slack-chats.kotlinlang.org/t/30070609/compose-navigation-is-really-what-lets-ios-down-when-buildin)  
19. y9vad9/cadento: Kotlin multiplatform productivity application built with Kotlin, Compose, SQLDelight, Coroutines and Ktor \- GitHub, [https://github.com/timemates/app](https://github.com/timemates/app)  
20. I Came Back to Kotlin for KMP — Here's What Broke First \- DEV Community, [https://dev.to/rarroyo00/i-came-back-to-kotlin-for-kmp-heres-what-broke-first-hfn](https://dev.to/rarroyo00/i-came-back-to-kotlin-for-kmp-heres-what-broke-first-hfn)  
21. SQLiteNow 0.9.0 for KMP: SQLite-first codegen, reactive flows, and optional sync \- Reddit, [https://www.reddit.com/r/KotlinMultiplatform/comments/1tayx6h/sqlitenow\_090\_for\_kmp\_sqlitefirst\_codegen/](https://www.reddit.com/r/KotlinMultiplatform/comments/1tayx6h/sqlitenow_090_for_kmp_sqlitefirst_codegen/)  
22. Create a multiplatform app using Ktor and SQLDelight \- Kotlin, [https://kotlinlang.org/docs/multiplatform/multiplatform-ktor-sqldelight.html](https://kotlinlang.org/docs/multiplatform/multiplatform-ktor-sqldelight.html)  
23. Kotlin Multiplatform for Clean Architecture \- inovex GmbH, [https://www.inovex.de/de/blog/kotlin-multiplatform-for-clean-architecture/](https://www.inovex.de/de/blog/kotlin-multiplatform-for-clean-architecture/)  
24. Kotlin Multiplatform Course Certificate | Free & Fast Course \- Elevify, [https://www.elevify.com/en/courses/engineering-construction-and-technology/technology/kotlin-multiplatform-course-bcdee](https://www.elevify.com/en/courses/engineering-construction-and-technology/technology/kotlin-multiplatform-course-bcdee)  
25. Koin in Kotlin Multiplatform: A Complete Guide | by Arnaud Giuliani, [https://blog.insert-koin.io/koin-in-kotlin-multiplatform-a-complete-guide-576a24ffeeab](https://blog.insert-koin.io/koin-in-kotlin-multiplatform-a-complete-guide-576a24ffeeab)  
26. Multiplatform ViewModel \- Kotlin, [https://kotlinlang.org/docs/multiplatform/compose-viewmodel.html](https://kotlinlang.org/docs/multiplatform/compose-viewmodel.html)  
27. Set up ViewModel for KMP | Kotlin \- Android Developers, [https://developer.android.com/kotlin/multiplatform/viewmodel](https://developer.android.com/kotlin/multiplatform/viewmodel)  
28. cerezo074/DailyPulse: A Kotlin Multiplatform news app for Android and iOS \- GitHub, [https://github.com/cerezo074/DailyPulse](https://github.com/cerezo074/DailyPulse)  
29. Flutter vs Kotlin vs Swift: Which Mobile Tech to Choose in 2026 \- Startup House, [https://startup-house.com/blog/flutter-vs-kotlin-vs-swift](https://startup-house.com/blog/flutter-vs-kotlin-vs-swift)  
30. Mejores Prácticas de Arquitectura en Kotlin Multiplatform para Aplicaciones Móviles, [https://carrion.dev/es/posts/kmp-architecture/](https://carrion.dev/es/posts/kmp-architecture/)  
31. iOS vs Android Development: What's the Difference? \- Squareboat, [https://www.squareboat.com/blog/ios-vs-android-development](https://www.squareboat.com/blog/ios-vs-android-development)  
32. Kotlin Multiplatform – Bridging Compose & iOS UI Frameworks | Infinum, [https://infinum.com/blog/kotlin-multiplatform-swiftui/](https://infinum.com/blog/kotlin-multiplatform-swiftui/)  
33. MVI Library Comparison \- GitHub Pages, [https://copper-leaf.github.io/ballast/wiki/feature-comparison/](https://copper-leaf.github.io/ballast/wiki/feature-comparison/)  
34. MVI Architecture Explained On Android \- Stackademic, [https://blog.stackademic.com/mvi-architecture-explained-on-android-e36ee66bceaa](https://blog.stackademic.com/mvi-architecture-explained-on-android-e36ee66bceaa)  
35. Navigating the Waters of Kotlin Multiplatform: Exploring Navigation Solutions | by Thomas Kioko™ | ProAndroidDev, [https://proandroiddev.com/navigating-the-waters-of-kotlin-multiplatform-exploring-navigation-solutions-eef81aaa1a61](https://proandroiddev.com/navigating-the-waters-of-kotlin-multiplatform-exploring-navigation-solutions-eef81aaa1a61)  
36. Fixing Problems of Jetpack Compose Navigation | by Vitaly Peryatin \- Better Programming, [https://betterprogramming.pub/realize-jetpack-compose-navigation-2889401f52b](https://betterprogramming.pub/realize-jetpack-compose-navigation-2889401f52b)  
37. Which navigation library for compose do you suggest? : r/androiddev \- Reddit, [https://www.reddit.com/r/androiddev/comments/101bbvu/which\_navigation\_library\_for\_compose\_do\_you/](https://www.reddit.com/r/androiddev/comments/101bbvu/which_navigation_library_for_compose_do_you/)  
38. Compose Multiplatform Navigation Solutions \- Voyager \- Michal Konkel Developer Blog, [https://michalkonkel.dev/compose-multiplatform-navigation-solutions-voyager](https://michalkonkel.dev/compose-multiplatform-navigation-solutions-voyager)  
39. Voyager Navigation \- Speednet, [https://speednetsoftware.com/voyager-navigation/](https://speednetsoftware.com/voyager-navigation/)  
40. Navigation in Compose | Kotlin Multiplatform Documentation, [https://kotlinlang.org/docs/multiplatform/compose-navigation.html](https://kotlinlang.org/docs/multiplatform/compose-navigation.html)  
41. Navigation and routing | Kotlin Multiplatform Documentation, [https://kotlinlang.org/docs/multiplatform/compose-navigation-routing.html](https://kotlinlang.org/docs/multiplatform/compose-navigation-routing.html)  
42. Using Navigation 3 with Compose Multiplatform \- John O'Reilly, [https://johnoreilly.dev/posts/navigation3-cmp/](https://johnoreilly.dev/posts/navigation3-cmp/)  
43. Kotlin SDK \- PowerSync Docs, [https://docs.powersync.com/client-sdks/reference/kotlin](https://docs.powersync.com/client-sdks/reference/kotlin)  
44. FlowStacks allows you to hoist SwiftUI navigation and presentation state into a Coordinator \- GitHub, [https://github.com/johnpatrickmorgan/FlowStacks](https://github.com/johnpatrickmorgan/FlowStacks)  # **Architectural Decision Record: Unified Cross-Platform System Topology for iOS and Android**

## **Executive Context and Problem Definition**

The maturation of mobile software engineering has initiated a paradigm shift from decoupled, platform-specific codebases toward unified cross-platform systems architectures1. Historically, cross-platform frameworks forced an unacceptable trade-off between development velocity and platform fidelity, often producing non-native user experiences or unmaintainable, tightly coupled execution paths1.  
With the stabilization of Kotlin Multiplatform and Compose Multiplatform, it is now possible to share both core business logic and interface layouts within a single, compile-safe codebase1. This report formalizes an Architecture Decision Record evaluating a unified production system designed for a data-intensive, offline-first mobile application4.

\+-----------------------------------------------------------------------------+  
|                            PRESENTATION LAYER                               |  
|   \[Compose Multiplatform UI\] \<--- collectAsState() \--- \[ViewModel (KMP)\]    |  
|              |                                                |             |  
|        (User Actions)                                  (Executes UDF)       |  
|              v                                                v             |  
|   \[Composable Coordinator\]                          \[Domain Use Cases\]      |  
\+---------------------------------------------------------------+-------------+  
                                                                |  
                                                     (Dependency Inversion)  
                                                                v  
\+-----------------------------------------------------------------------------+  
|                              DOMAIN LAYER                                   |  
|               \[Use Cases\] \----\> \[Repository Interfaces\]                     |  
\+-----------------------------------------------------------------------------+  
                                                                ^  
                                                     (Dependency Inversion)  
                                                                |  
\+---------------------------------------------------------------+-------------+  
|                               DATA LAYER                                    |  
|   \[Repository Implementations\] \----\> \[SQLDelight\] / \[Ktor Network Client\]   |  
\+-----------------------------------------------------------------------------+

Developing a resilient, offline-first architecture requires a reliable local cache5. The database engine must support background multi-threaded queries without blocking the primary render loop, maintain relational database integrity, and run without runtime reflection or heavy execution overhead on resource-constrained devices4.  
The user interface must adapt dynamically to various form factors while preserving platform-specific behaviors—such as the predictive back-swipe gesture on iOS, platform-specific keyboard layouts, and smooth transition animations8. To address these requirements, the proposed stack combines:

* Kotlin Multiplatform10  
* Compose Multiplatform shared UI1  
* Clean Architecture-lite11  
* Model-View-ViewModel12  
* Unidirectional Data Flow12  
* Repository pattern6  
* Composable Coordinator navigation pattern16  
* SQLDelight offline storage17  
* Ktor remote API connectivity17  
* Koin dependency injection17

This evaluation compares this proposed architecture against seven alternative architectural configurations across eight critical evaluation dimensions.

## **Comprehensive Evaluation of Architectural Alternatives**

Selecting a system-wide mobile architecture requires a quantitative and qualitative assessment of competing design patterns. The selected alternatives are evaluated against the following criteria:

* **Complexity:** The structural overhead, number of active source files, mapping layers, and framework boilerplate code required to support basic operations14.  
* **Learning Curve:** The training duration and conceptual adjustment required for developers entering the codebase14.  
* **iOS Feel:** The fidelity of the user interface on iOS, including gesture responsiveness, rendering smoothness, and native components integration3.  
* **Android Feel:** The alignment with Material Design standards, Jetpack integrations, and system-level back behavior8.  
* **Development Speed:** The rate at which developer teams can deliver a feature from conception to production1.  
* **Long-Term Maintainability:** The resistance of the codebase to architectural degradation, modular leakage, and regression errors over a multi-year lifecycle22.  
* **Testability:** The ease of verifying business and rendering logic in automated headless testing environments15.  
* **Small Team Risk:** The risk profile of solo developers or small engineering teams, considering resource constraints, framework deprecations, and integration bottlenecks22.

### **Evaluation Matrix**

To establish a baseline for comparison, each architectural alternative is scored from 1 (poor/high risk) to 5 (excellent/low risk) across all evaluation dimensions.

| Architectural Configuration | Complexity | Learning Curve | iOS Feel | Android Feel | Dev Speed | Maintainability | Testability | Team Risk |
| :---- | :---- | :---- | :---- | :---- | :---- | :---- | :---- | :---- |
| **Proposed Stack (CMP \+ Clean-Lite \+ MVVM \+ Coordinator \+ SQLDelight)** | 3 | 3 | 4 | 5 | 4 | 5 | 5 | 5 |
| **Alt 1: Shared Logic Only \+ Native UI (SwiftUI / Jetpack Compose)** | 5 | 4 | 5 | 5 | 2 | 5 | 4 | 3 |
| **Alt 2: Full Shared UI with Compose Multiplatform (Unstructured)** | 1 | 1 | 3 | 5 | 5 | 2 | 2 | 3 |
| **Alt 3: Simple Layered Architecture** | 1 | 1 | 4 | 5 | 5 | 2 | 2 | 2 |
| **Alt 4: Strict Model-View-Intent (MVI)** | 4 | 4 | 4 | 5 | 3 | 5 | 5 | 3 |
| **Alt 5: Decompose / Component-Based BLoC Architecture** | 5 | 5 | 5 | 5 | 2 | 5 | 5 | 2 |
| **Alt 6: Voyager Navigation Library** | 2 | 2 | 3 | 5 | 5 | 3 | 3 | 3 |
| **Alt 7: Official Compose Navigation (AndroidX port)** | 3 | 3 | 3 | 5 | 4 | 4 | 3 | 4 |

### **Alternative 1: Shared Business Logic Only \+ Native SwiftUI/Jetpack Compose UI**

This architectural pattern maintains a single shared Kotlin core for domain logic and data management, while building separate user interfaces in platform-native UI frameworks1.

* **Complexity and Learning Curve:** This model introduces high complexity by maintaining two distinct presentation layers1. Developers must be proficient in Swift, SwiftUI, Combine, Kotlin, Jetpack Compose, and coroutines, creating a steep learning curve for mixed-platform product teams1.  
* **iOS and Android Feel:** This approach ensures native UI fidelity1. Using SwiftUI on iOS guarantees that features like native spring animations, haptic feedback, accessibility voiceovers, and standard system navigation behave correctly3.  
* **Development Speed:** Development speed is reduced because every interface modification, visual component, localized asset, and navigation path must be implemented and tested twice—once in Kotlin and Jetpack Compose, and once in Swift and SwiftUI1.  
* **Long-Term Maintainability and Testability:** Maintainability is strong because business logic remains centralized in the shared module20. However, testing remains fragmented because view behaviors and states must be verified using separate platform-specific test runners20.  
* **Solo Developer/Small Team Risk:** The need to implement duplicate code for both presentation layers introduces substantial delivery risks for small or solo teams, often leading to visual and behavioral drift between the platforms3.

\+---------------------------------------------------------------------+  
| PLATFORM-SPECIFIC PRESENTATION LAYERS                               |  
|   \[Android (Compose UI)\]                 \[iOS (SwiftUI / Swift Store)\]  |  
\+---------------------------------------------------------------------+  
                                   |  
                         (Consumes Shared Logic)  
                                   v  
\+---------------------------------------------------------------------+  
| CENTRALIZED KOTLIN MULTIPLATFORM SYSTEM CORE                        |  
|   \[Use Cases\] \----\> \[Repositories\] \----\> \[SQLDelight / Ktor\]        |  
\+---------------------------------------------------------------------+

### **Alternative 2: Unstructured Shared UI with Compose Multiplatform**

This approach shares both user interfaces and business logic across platforms using Compose Multiplatform, but lacks formal structural patterns like Clean Architecture or the Coordinator pattern1.

* **Complexity and Learning Curve:** This model has low initial complexity and a minimal learning curve, as standard Jetpack Compose concepts map directly to iOS, desktop, and web environments2.  
* **iOS and Android Feel:** While rendering is performant, complex iOS gestures and transitions can feel non-native without structured integration with Apple's platform-specific APIs3.  
* **Development Speed:** Initial development speed is exceptionally high, as screens and components are written once and run immediately on both platforms1.  
* **Long-Term Maintainability and Testability:** Over time, the lack of defined code boundaries can lead to architectural degradation22. Data access and business logic can easily leak into presentation composables, complicating unit testing and leading to a highly coupled codebase22.  
* **Solo Developer/Small Team Risk:** While appealing for quick prototypes, the long-term maintenance costs and technical debt make this a risky choice for production systems22.

### **Alternative 3: Simple Layered Architecture**

This approach organizes the codebase into generic technical layers (UI, Business Logic, and Data) without further separating business requirements into dedicated Use Cases11.

* **Complexity and Learning Curve:** Extremely lightweight with standard abstractions, making it highly accessible to junior engineers14.  
* **iOS and Android Feel:** Interface rendering is consistent across platforms1.  
* **Development Speed:** Eliminating mapping abstractions between layers accelerates initial feature development23.  
* **Long-Term Maintainability and Testability:** As the codebase grows, direct dependencies between the UI and data services can form, creating highly coupled modules23. This complicates unit testing because data queries cannot be easily mocked15.  
* **Solo Developer/Small Team Risk:** For simple CRUD applications, the low overhead of a layered architecture is often highly productive. However, for complex or offline-first applications, the lack of isolation between layers can make maintenance difficult over time22.

### **Alternative 4: Strict Model-View-Intent (MVI)**

This architecture enforces unidirectional data flow by modeling all operations as explicit Intents, immutable States, and reactive Side-Effects12.

* **Complexity and Learning Curve:** MVI introduces high complexity14. Developers must write significant boilerplate code—including sealed intent classes, state classes, reducer engines, and effect channels—for every screen, requiring a steep learning curve14.  
* **iOS and Android Feel:** High-quality rendering is maintained1.  
* **Development Speed:** Initial development velocity is reduced by the boilerplate code required to implement simple interactions13.  
* **Long-Term Maintainability and Testability:** Maintainability and testability are exceptional12. Consolidating all screen state into a single immutable flow allows for deterministic testing, simple state logging, and straightforward debugging of complex UI transitions12.  
* **Solo Developer/Small Team Risk:** The high boilerplate overhead can slow down small or solo teams, often making a lightweight hybrid MVVM/MVI model a more pragmatic choice13.

\+-------------------------------------------------------------+  
| UNIDIRECTIONAL MVI EXECUTION LOOP                           |  
|   User Interaction \===\> Intent (Sealed Class)               |  
|                           |                                 |  
|                           v                                 |  
|   State Rendering  \<=== Reducer (Current State \+ Intent)    |  
\+-------------------------------------------------------------+

### **Alternative 5: Decompose / Component-Based BLoC Architecture**

Decompose separates the application into a tree of platform-agnostic, lifecycle-aware components (Business Logic Components)24.

* **Complexity and Learning Curve:** Decompose is highly robust but complex, with a steep learning curve due to its unique component-driven architecture22.  
* **iOS and Android Feel:** Excellent UI fidelity; Decompose manages native back gestures, state restoration, and multi-pane transitions cleanly across platforms19.  
* **Development Speed:** Development velocity is slow initially due to the boilerplate required to configure lifecycle owners, component contexts, child stacks, and custom DI factories19.  
* **Long-Term Maintainability and Testability:** Maintainability and testability are exceptionally high22. The system can run and be fully verified in headless environments without launching emulator interfaces22.  
* **Solo Developer/Small Team Risk:** The high structural overhead and verbose APIs pose a significant risk for small teams, who may find the framework's complexity distracts from core feature development22.

### **Alternative 6: Voyager Navigation Library**

Voyager is a pragmatic, multiplatform navigation library designed specifically for Compose Multiplatform applications21.

* **Complexity and Learning Curve:** Low complexity and an intuitive API, allowing teams to quickly build dynamic screens25.  
* **iOS and Android Feel:** Good, but Voyager lacks built-in support for native iOS back-swipe gestures and handles deep links poorly compared to first-party solutions21.  
* **Development Speed:** High initial development speed25.  
* **Long-Term Maintainability and Testability:** Because navigation logic is often embedded directly inside Compose screens, decoupled unit testing and overall maintainability can be compromised as the codebase scales26.  
* **Solo Developer/Small Team Risk:** Moderate risk; while highly productive early on, the project faces maintenance risks due to historical gaps in active community support22.

### **Alternative 7: Official Compose Navigation (AndroidX Jetpack Navigation)**

The multiplatform port of AndroidX Navigation brings type-safe routing directly into the common codebase8.

* **Complexity and Learning Curve:** Moderate complexity; relies on a centralized declarative navigation graph8.  
* **iOS and Android Feel:** Good, but can introduce visual transition artifacts on iOS unless transition animations are customized8.  
* **Development Speed:** High, especially for teams already familiar with the Android Jetpack ecosystem21.  
* **Long-Term Maintainability and Testability:** Strong compile-time safety prevents typical runtime routing crashes21. However, maintainability can degrade if ViewModels or Composable screens directly reference the global NavController, creating tight coupling29.  
* **Solo Developer/Small Team Risk:** Low risk due to direct backing from JetBrains and Google, though it requires disciplined pattern enforcement to avoid code entanglement21.

## **Final Recommended Architecture & Rationale**

To balance development velocity, runtime performance, and codebase maintainability, the system adopts the **Proposed Stack** as the definitive production standard9.

\+--------------------------------------------------------------------------+  
|                       PRESENTATION COMPONENT                             |  
|                                                                          |  
|   \+-----------------------+              \+---------------------------+   |  
|   | Composable View       |             | ViewModel                 |   |  
|   |                       |              |                           |   |  
|   | Renders StateFlow \<----------------------- Exposes StateFlow     |   |  
|   |                       |              |                           |   |  
|   | Emits User Interaction \------------------\> Receives Function Call |   |  
|   \+-----------|-----------+              \+-------------|-------------+   |  
|               |                                        |                 |  
|      (Bubbles Callback)                      (Invokes Use Case)          |  
|               v                                        v                 |  
|   \+-----------------------+              \+---------------------------+   |  
|   | Composable            |              | Domain Use Case           |   |  
|   | Coordinator           |              |                           |   |  
|   \+-----------------------+              \+---------------------------+   |  
\+--------------------------------------------------------|-----------------+  
                                                         |  
                                               (Dependency Inversion)  
                                                         v  
\+--------------------------------------------------------------------------+  
|                          DOMAIN COMPONENT                                |  
|                                                                          |  
|                \+---------------------------------------+                 |  
|                | Repository Abstract Interface         |                 |  
|                \+---------------------------------------+                 |  
\+---------------------------------------------------|----------------------+  
                                                    ^  
                                          (Dependency Inversion)  
                                                    |  
\+---------------------------------------------------|----------------------+  
|                           DATA COMPONENT                                 |  
|                                                                          |  
|                \+---------------------------------------+                 |  
|                | Repository Implementation             |                 |  
|                \+---------------------------------------+                 |  
|                                    |                                     |  
|                       \+------------+------------+                        |  
|                       v                         v                        |  
|             \+-------------------+     \+-------------------+              |  
|             | SQLDelight Engine |     | Ktor Engine       |              |  
|             \+-------------------+     \+-------------------+              |  
\+--------------------------------------------------------------------------+

### **Architectural Decisions & Rationale**

#### **Compose Multiplatform for Shared UI**

Compose for iOS achieved stability with the release of Compose Multiplatform 1.8.0, making the sharing of UI components highly robust across both platforms3. Sharing layouts cuts development time nearly in half and eliminates visual and behavioral drifts between Android and iOS implementations1. In instances where native system UI views—such as maps, video players, or web views—are required, platform interop engines like ComposeUIViewController and UIKitView allow for easy embedding, providing developers with reliable native escape hatches3.

#### **Clean Architecture-Lite with Use Case Isolation**

Placing business rules inside dedicated Use Cases isolates business logic from the UI and databases, preventing architectural decay11. This isolation makes core logic highly testable through JVM unit tests, which can be run with mocked repository interfaces without requiring device emulators15.

#### **MVVM with Unidirectional Data Flow (UDF)**

This system standardizes ViewModels using the KMP-compatible androidx.lifecycle.ViewModel12. UI states are exposed as single, immutable StateFlow streams12. Unidirectional Data Flow prevents concurrent state mutations and inconsistent UI behaviors, while keeping the presentation layer lightweight and easily testable12.

#### **Composable Coordinator Pattern for Navigation**

Rather than hardcoding navigation logic directly into screens or ViewModels, this system uses a decoupled, hierarchical Coordinator navigation pattern11. Screens remain pure and stateless, delegating navigation events up to parent Coordinators via simple lambda callbacks16. This makes individual screens highly modular, easily previewable, and reusable in different parts of the application11.

#### **SQLDelight Local-First Storage**

SQLDelight operates on an SQL-first approach, compiling standard raw SQL statements into type-safe Kotlin code7. This avoids the runtime reflective mapping costs of traditional ORMs, providing high query performance and stable migrations7.  
The engine's package transition to app.cash.sqldelight has stabilized its multiplatform support, making it a reliable solution for offline-first applications7. Platform-specific drivers are resolved cleanly using expect/actual factory functions and registered with the dependency injection system5.

\+-------------------------------------------------------------------------+  
| DATABASE DRIVER CREATION FLOW                                           |  
|   DatabaseDriverFactory (expect interface)                              |  
|          |                                                              |  
|          \+---\> AndroidDatabaseDriverFactory (actual class)              |  
|          |         └── AndroidSqliteDriver(Schema, context, "app.db")   |  
|          |                                                              |  
|          \+---\> IOSDatabaseDriverFactory (actual class)                  |  
|                    └── NativeSqliteDriver(Schema, "app.db")             |  
\+-------------------------------------------------------------------------+

#### **Ktor Engine & Koin Integration**

The network client is configured with Ktor's Darwin engine on iOS and the OkHttp engine on Android, allowing the app to leverage platform-specific performance optimizations17. Koin manages dependency injection with low compilation-time impact, using Kotlin's inline functions and reified type arguments to perform runtime dependency resolution36.  
This avoids the build-time overhead of annotation-processing DI frameworks while keeping the system highly testable15.

## **Layer Boundaries and Modular Responsibilities**

This architecture enforces strict separation of concerns across its boundaries11. To maintain long-term scalability, each layer is governed by a distinct set of operational and structural rules11.

\+-------------------------------------------------------------------------+  
| SYSTEM STRICTURE MATRIX AND LAYER BOUNDARIES                            |  
\+-------------------------------------------------------------------------+  
| Presentation Layer: Compose Multiplatform UI, ViewModels, Coordinators  |  
|                       |                                                 |  
|                       | (Depends upon)                                  |  
|                       v                                                 |  
| Domain Layer: Pure Kotlin Entities, Use Cases, Repositories Contracts   |  
|                       ^                                                 |  
|                       | (Dependency Inversion Boundary)                 |  
|                       |                                                 |  
| Data Layer: Repository Implementations, SQLDelight Cache, Ktor API      |  
\+-------------------------------------------------------------------------+

### **Presentation Layer Responsibilities**

The presentation layer handles rendering view state, processing user input events, and managing navigation flows12. It operates under the following guidelines:

* **State Rendering:** User interface screens must be built as stateless, declarative Composable functions1. Views observe the ViewModel's state flow using collectAsState() and communicate user interactions up to the ViewModel via simple callback functions12.  
* **State Management:** ViewModels process incoming user events, update the UI state, and expose it as an immutable StateFlow12. ViewModels communicate with the domain layer solely by invoking domain use cases. They do not access raw data sources, manage databases, or handle navigation.  
* **Navigation Management:** Composable screens must remain agnostic of their navigation context16. When a navigation event occurs (such as an item selection), the screen must bubble the event up to its parent Coordinator using a simple callback lambda16.

### **Domain Layer Responsibilities**

The domain layer represents the core business logic of the application. It must be written in pure Kotlin and remain completely free of external dependencies, platform APIs, or database frameworks11.

* **Business Logic:** Use cases (or Interactors) must enforce a single, highly focused business operation (such as FetchItemDetails or UpdateInventory). Each use case acts as a coordinator between repository interfaces.  
* **Entity Definitions:** Entities must be pure Kotlin data structures, free of database or network serialization annotations23.  
* **Repository Abstractions:** The domain layer defines the abstract interfaces for data operations11. Their concrete implementations are located in the data layer, enforcing dependency inversion11.

### **Data Layer Responsibilities**

The data layer is responsible for data persistence, network operations, caching, and external system integrations11.

* **Data Orchestration:** Concrete repository implementations manage data access logic, deciding when to query the local database cache and when to fetch fresh data from the remote API6.  
* **Persistence Management:** Manages local database operations using SQLDelight17. The data layer executes SQL statements and handles platform-specific database drivers using factories resolved via dependency injection5.  
* **Network Integration:** Manages HTTP requests, configurations, and API clients using Ktor17.

## **Dependency Inversion and Direction Rules**

To keep the shared codebase decoupled and maintainable, dependencies must flow inward toward the domain layer11.

Presentation Module (UI / ViewModel) \===\> \[ Domain Module (Use Cases / Interfaces) \]  
                                                       ^  
                                                       |  
Data Module (SQLDelight / Ktor Repositories) \----------/

### **Rule 1: The Domain Inversion Axiom**

The domain module must be completely self-contained and must never import or depend on dependencies from the presentation or data modules11. All communication with external layers is handled via dependency inversion, using repository interfaces defined in the domain module11.

### **Rule 2: Explicit DTO Mapping Boundaries**

Network response models (Ktor JSON models) and local database models (SQLDelight entities) must never leak into the presentation or domain layers23. The data layer must map these implementation-specific Data Transfer Objects into clean domain models before returning them to Use Cases23.

### **Rule 3: Thread-Safe Boundary Containment**

Use cases and repositories must manage their own threading contexts4. ViewModels must initiate coroutines on the main thread, while database operations and network queries must run on background threads using coroutine dispatchers (such as Dispatchers.Default or a dedicated database dispatcher thread) to avoid blocking the UI4.

## **Architecture Code Smell and Anti-Pattern Mitigation**

* **Leakage of the Global Navigation Controller:** Passing the NavController directly into a ViewModel or a child Composable creates tight coupling, making it difficult to preview, test, or reuse those views29. To avoid this, use the Coordinator pattern to delegate navigation events up to the parent Coordinator via lambda callbacks16.  
* **Bypassing the Domain Layer:** Bypassing use cases and calling repositories directly from ViewModels to save time leads to distributed business logic. This compromises the role of the domain layer as the single source of truth and complicates testing23.  
* **Leaking Database Schemas:** Exposing database entities directly to the user interface creates tight coupling between the UI and database schemas23. If the schema or database engine changes, these updates can ripple through the codebase and break the UI23.  
* **Direct Database Driver Creation:** Instantiating SqliteDriver objects directly inside common code bypasses the dependency injection container17. Platform-specific database initialization should always be managed using abstract factories resolved via Koin17.  
* **Unvalidated Koin Dependency Graphs:** Unlike compile-time DI frameworks, Koin resolves dependencies at runtime36. Failing to write Koin configuration validation tests (KoinTest with checkModules) can result in runtime crashes due to missing dependencies in production36.  
* **Swift-Kotlin Lifecycle Mismatches on iOS:** On Android, the ViewModel lifecycle is managed automatically by the system31. On iOS, Kotlin-derived ViewModels can experience memory leaks or fail to release resources because Swift does not automatically call the onCleared() lifecycle method20. To prevent this, use a dedicated Swift wrapper (a "Store" object) or helper libraries (such as SKIE) to bind the Kotlin ViewModel's lifecycle to the corresponding Swift UI ViewController or Composable10.

## **Concrete Code Specification: Standardized Directory Structure**

The structure below organizes the application's packages to maintain clear boundaries between features, domain abstractions, data implementations, and DI modules11.

shared/  
├── src/  
│   ├── commonMain/  
│   │   └── kotlin/  
│   │       └── com/  
│   │           └── system/  
│   │               └── app/  
│   │                   ├── core/  
│   │                   │   ├── di/  
│   │                   │   │   └── SharedModule.kt           \# Koin dependency injection configuration  
│   │                   │   ├── network/  
│   │                   │   │   └── KtorClientBuilder.kt      \# Common Ktor engine setups and API paths  
│   │                   │   └── database/  
│   │                   │       └── DatabaseWrapper.kt        \# SQLDelight database wrapper  
│   │                   ├── domain/  
│   │                   │   ├── model/  
│   │                   │   │   └── InventoryItem.kt          \# Clean domain entity definitions  
│   │                   │   ├── repository/  
│   │                   │   │   └── InventoryRepository.kt    \# Abstract repository interface contracts  
│   │                   │   └── usecase/  
│   │                   │       └── UpdateInventoryUseCase.kt \# Focused business logic rules  
│   │                   ├── data/  
│   │                   │   ├── model/  
│   │                   │   │   └── NetworkInventoryDto.kt    \# API serialization data transfer objects  
│   │                   │   └── repository/  
│   │                   │       └── InventoryRepositoryImpl.kt\# Concrete repository implementation  
│   │                   └── presentation/  
│   │                       ├── navigation/  
│   │                       │   ├── Coordinator.kt            \# Core Coordinator interface  
│   │                       │   └── AppCoordinator.kt         \# Routing engine and coordination tree  
│   │                       └── features/  
│   │                           └── inventory/  
│   │                               ├── InventoryScreen.kt    \# Stateless view UI  
│   │                               ├── InventoryViewModel.kt \# Presentation ViewModel state container  
│   │                               └── InventoryActions.kt   \# Interaction events definitions  
│   ├── androidMain/  
│   │   └── kotlin/  
│   │       └── com/  
│   │           └── system/  
│   │               └── app/  
│   │                   └── core/  
│   │                       └── database/  
│   │                           └── DatabaseDriverFactory.kt  \# Android SqliteDriver implementation  
│   └── iosMain/  
│       └── kotlin/  
│           └── com/  
│               └── system/  
│                   └── app/  
│                       └── core/  
│                           └── database/  
│                               └── DatabaseDriverFactory.kt  \# iOS NativeSqliteDriver implementation

## **Complete Execution Trace: Reactive UI Click to Transactional Database Write**

This execution trace demonstrates how the Unidirectional Data Flow pattern processes a user interaction from a button tap to a local database transaction12.

### **Data Flow Execution Path**

\[UI Screen: Click Button\] \===(1)===\> ViewModel::saveItem()  
                                            |  
                                            | (2) Launches ViewModel Coroutine  
                                            v  
\[SQLDatabase: Commit\] \<===(5)=== RepositoryImpl::saveItem() \<===(3)=== UseCase::invoke()  
          |  
          | (6) SQLDelight Auto-Emits Update Flow  
          v  
ViewModel State Collection \===(7)===\> StateFlow Emits Update \===(8)===\> UI Screen Renders State

1. **User Interaction:** The user clicks the "Save Item" button on the stateless InventoryScreen Composable. This fires a callback that triggers viewModel.saveItem()12.  
2. **ViewModel Execution:** InventoryViewModel sets the UI state to isSaving \= true to update the interface. It then launches a coroutine scope and calls UpdateInventoryUseCase12.  
3. **Use Case Execution:** UpdateInventoryUseCase validates the inputs (verifying that the name is not empty and quantity is not negative). If validation passes, it maps the input to an InventoryItem domain model and calls the InventoryRepository interface11.  
4. **Repository Database Execution:** InventoryRepositoryImpl intercepts the use case call and switches to a background thread using withContext(Dispatchers.Default)4. It maps the domain model to an SQLDelight database entity and executes insertOrUpdateItem()17.  
5. **Database Persistence:** SQLDelight writes the record to the device's local database, ensuring complete persistence4.  
6. **State Observation Emission:** Once written, SQLDelight automatically emits the database updates to active coroutine queries. The repository receives this update and maps the database entities back to domain models17.  
7. **ViewModel State Collection:** The ViewModel observes these updates, wraps them in a state update, and publishes the new InventoryUiState to the UI12.  
8. **UI State Collection:** The UI collects the updated state, hiding the loading indicator and displaying a success message on the screen12.

### **Code Execution Specification**

#### **1\. Pure Domain Entity Model**

Kotlin  
package com.system.app.domain.model

data class InventoryItem(  
    val id: String,  
    val name: String,  
    val quantity: Long  
)

#### **2\. Domain Repository Interface Definition**

Kotlin  
package com.system.app.domain.repository

import com.system.app.domain.model.InventoryItem  
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {  
    fun getInventoryItem(id: String): Flow\<InventoryItem?\>  
    suspend fun saveInventoryItem(item: InventoryItem)  
}

#### **3\. Pure Domain Use Case**

Kotlin  
package com.system.app.domain.usecase

import com.system.app.domain.model.InventoryItem  
import com.system.app.domain.repository.InventoryRepository

class UpdateInventoryUseCase(  
    private val inventoryRepository: InventoryRepository  
) {  
    suspend operator fun invoke(id: String, name: String, quantity: Long): Result\<Unit\> {  
        if (name.isBlank()) {  
            return Result.failure(IllegalArgumentException("Name cannot be empty"))  
        }  
        if (quantity \< 0) {  
            return Result.failure(IllegalArgumentException("Quantity cannot be negative"))  
        }  
          
        val updatedItem \= InventoryItem(id \= id, name \= name, quantity \= quantity)  
        return try {  
            inventoryRepository.saveInventoryItem(updatedItem)  
            Result.success(Unit)  
        } catch (e: Exception) {  
            Result.failure(e)  
        }  
    }  
}

#### **4\. Data Layer Repository Implementation (SQLDelight Wrapper)**

Kotlin  
package com.system.app.data.repository

import com.system.app.domain.model.InventoryItem  
import com.system.app.domain.repository.InventoryRepository  
import com.system.app.cache.AppDatabaseQueries // Generated SQLDelight queries class  
import kotlinx.coroutines.flow.Flow  
import kotlinx.coroutines.flow.map  
import app.cash.sqldelight.coroutines.asFlow  
import app.cash.sqldelight.coroutines.mapToOneOrNull  
import kotlinx.coroutines.Dispatchers  
import kotlinx.coroutines.withContext

class InventoryRepositoryImpl(  
    private val dbQueries: AppDatabaseQueries  
) : InventoryRepository {

    override fun getInventoryItem(id: String): Flow\<InventoryItem?\> {  
        return dbQueries.selectItemById(id)  
            .asFlow()  
            .mapToOneOrNull(Dispatchers.Default)  
            .map { entity \-\>  
                entity?.let { InventoryItem(it.id, it.name, it.quantity) }  
            }  
    }

    override suspend fun saveInventoryItem(item: InventoryItem) {  
        withContext(Dispatchers.Default) {  
            dbQueries.insertOrUpdateItem(  
                id \= item.id,  
                name \= item.name,  
                quantity \= item.quantity  
            )  
        }  
    }  
}

#### **5\. Presentation ViewModel (Exposing UDF State Flows)**

Kotlin  
package com.system.app.presentation.features.inventory

import androidx.lifecycle.ViewModel  
import androidx.lifecycle.viewModelScope  
import com.system.app.domain.usecase.UpdateInventoryUseCase  
import kotlinx.coroutines.flow.MutableStateFlow  
import kotlinx.coroutines.flow.StateFlow  
import kotlinx.coroutines.flow.asStateFlow  
import kotlinx.coroutines.flow.update  
import kotlinx.coroutines.launch

data class InventoryUiState(  
    val id: String \= "",  
    val name: String \= "",  
    val quantity: Long \= 0,  
    val isSaving: Boolean \= false,  
    val errorMessage: String? \= null,  
    val isSaveComplete: Boolean \= false  
)

class InventoryViewModel(  
    private val updateInventoryUseCase: UpdateInventoryUseCase,  
    private val itemId: String  
) : ViewModel() {

    private val \_uiState \= MutableStateFlow(InventoryUiState(id \= itemId))  
    val uiState: StateFlow\<InventoryUiState\> \= \_uiState.asStateFlow()

    fun onNameChanged(newName: String) {  
        \_uiState.update { it.copy(name \= newName) }  
    }

    fun onQuantityChanged(newQuantity: Long) {  
        \_uiState.update { it.copy(quantity \= newQuantity) }  
    }

    fun saveItem() {  
        val currentState \= \_uiState.value  
        \_uiState.update { it.copy(isSaving \= true, errorMessage \= null) }  
          
        viewModelScope.launch {  
            updateInventoryUseCase(  
                id \= currentState.id,  
                name \= currentState.name,  
                quantity \= currentState.quantity  
            ).onSuccess {  
                \_uiState.update { it.copy(isSaving \= false, isSaveComplete \= true) }  
            }  
            .onFailure { exception \-\>  
                \_uiState.update {   
                    it.copy(  
                        isSaving \= false,   
                        errorMessage \= exception.message ?: "An unexpected database error occurred"  
                    )   
                }  
            }  
        }  
    }  
}

#### **6\. Composable View Implementation**

Kotlin  
package com.system.app.presentation.features.inventory

import androidx.compose.foundation.layout.\*  
import androidx.compose.material3.\*  
import androidx.compose.runtime.Composable  
import androidx.compose.runtime.collectAsState  
import androidx.compose.runtime.getValue  
import androidx.compose.ui.Alignment  
import androidx.compose.ui.Modifier  
import androidx.compose.ui.unit.dp

@Composable  
fun InventoryScreen(  
    viewModel: InventoryViewModel,  
    onNavigateBack: () \-\> Unit,  
    modifier: Modifier \= Modifier  
) {  
    val state by viewModel.uiState.collectAsState()

    if (state.isSaveComplete) {  
        onNavigateBack()  
    }

    Column(  
        modifier \= modifier  
            .fillMaxSize()  
            .padding(16\.dp),  
        verticalArrangement \= Arrangement.Center,  
        horizontalAlignment \= Alignment.CenterHorizontally  
    ) {  
        TextField(  
            value \= state.name,  
            onValueChange \= { viewModel.onNameChanged(it) },  
            label \= { Text("Item Name") },  
            modifier \= Modifier.fillMaxWidth()  
        )  
          
        Spacer(modifier \= Modifier.height(8\.dp))  
          
        TextField(  
            value \= state.quantity.toString(),  
            onValueChange \= { value \-\>  
                value.toLongOrNull()?.let { viewModel.onQuantityChanged(it) }  
            },  
            label \= { Text("Quantity") },  
            modifier \= Modifier.fillMaxWidth()  
        )  
          
        Spacer(modifier \= Modifier.height(16\.dp))  
          
        if (state.errorMessage \!= null) {  
            Text(  
                text \= state.errorMessage\!\!,  
                color \= MaterialTheme.colorScheme.error,  
                style \= MaterialTheme.typography.bodySmall  
            )  
            Spacer(modifier \= Modifier.height(8\.dp))  
        }  
          
        Button(  
            onClick \= { viewModel.saveItem() },  
            enabled \= \!state.isSaving,  
            modifier \= Modifier.fillMaxWidth()  
        ) {  
            if (state.isSaving) {  
                CircularProgressIndicator(  
                    color \= MaterialTheme.colorScheme.onPrimary,  
                    modifier \= Modifier.size(24\.dp)  
                )  
            } else {  
                Text("Save Item")  
            }  
        }  
    }  
}

#### **Works cited**

1. KMP vs CMP: Logic or Everything? | Teja Punna, [https://tejapunna.in/blog/kmp-vs-cmp-difference-logic-vs-ui-sharing](https://tejapunna.in/blog/kmp-vs-cmp-difference-logic-vs-ui-sharing)  
2. Kotlin Multiplatform – Build Cross-Platform Apps, [https://kotlinlang.org/multiplatform/](https://kotlinlang.org/multiplatform/)  
3. Kotlin Multiplatform: Share Logic Only, or UI with Compose Too? (2026) | Batteries Included, [https://batteriesincluded.io/insights/kotlin-multiplatform-and-compose-multiplatform](https://batteriesincluded.io/insights/kotlin-multiplatform-and-compose-multiplatform)  
4. Kotlin SDK \- PowerSync Docs, [https://docs.powersync.com/client-sdks/reference/kotlin](https://docs.powersync.com/client-sdks/reference/kotlin)  
5. Building an offline-first KMP app \- what SQLDelight \+ expect/actual looks like in practice : r/androiddev \- Reddit, [https://www.reddit.com/r/androiddev/comments/1snauvj/building\_an\_offlinefirst\_kmp\_app\_what\_sqldelight/](https://www.reddit.com/r/androiddev/comments/1snauvj/building_an_offlinefirst_kmp_app_what_sqldelight/)  
6. GitHub \- momintahir/KMP-News-App: This application demonstrates modern Android development with Koin, Ktor, Coroutines, Flows, SQLDelight, Voyager based on Clean Architecture., [https://github.com/momintahir/KMP-News-App](https://github.com/momintahir/KMP-News-App)  
7. Local Database: Comparing Realm, SQLDelight, and Room. | by Tosin (Matt) Onikute, [https://proandroiddev.com/which-local-database-should-you-choose-in-2025-comparing-realm-sqldelight-and-room-4221b354c899](https://proandroiddev.com/which-local-database-should-you-choose-in-2025-comparing-realm-sqldelight-and-room-4221b354c899)  
8. Navigation in Compose | Kotlin Multiplatform Documentation, [https://kotlinlang.org/docs/multiplatform/compose-navigation.html](https://kotlinlang.org/docs/multiplatform/compose-navigation.html)  
9. Building a Fully Adaptive Navigation System in Compose Multiplatform (KMP) | by Meet, [https://proandroiddev.com/building-a-fully-adaptive-navigation-system-in-compose-multiplatform-kmp-e1a8921ca792](https://proandroiddev.com/building-a-fully-adaptive-navigation-system-in-compose-multiplatform-kmp-e1a8921ca792)  
10. FAQ | Kotlin Multiplatform Documentation, [https://kotlinlang.org/docs/multiplatform/faq.html](https://kotlinlang.org/docs/multiplatform/faq.html)  
11. Layered Modular Architecture: Evolving Clean Architecture to Support Multiplatform Development | by zerg 1111 | Medium, [https://medium.com/@b9915034/android-application-architecture-showcase-sunflower-clone-dee729f6e1f2](https://medium.com/@b9915034/android-application-architecture-showcase-sunflower-clone-dee729f6e1f2)  
12. MVI vs MVVM — Real-World Examples with Code | by Prahalad Sharma | Medium, [https://medium.com/@prahaladsharma4u/mvi-vs-mvvm-real-world-example-login-screen-aa1f61f54f39](https://medium.com/@prahaladsharma4u/mvi-vs-mvvm-real-world-example-login-screen-aa1f61f54f39)  
13. MVI vs MVVM in Jetpack Compose: The Ultimate Guide | by praveen sharma \- Medium, [https://medium.com/@sharmapraveen91/mvi-vs-mvvm-in-jetpack-compose-the-ultimate-guide-4c9032916e0d](https://medium.com/@sharmapraveen91/mvi-vs-mvvm-in-jetpack-compose-the-ultimate-guide-4c9032916e0d)  
14. MVVM vs MVI Android: Complete Comparison Guide 2026 | SharpSkill, [https://sharpskill.dev/en/blog/android/mvvm-vs-mvi-architecture](https://sharpskill.dev/en/blog/android/mvvm-vs-mvi-architecture)  
15. MVVM, Clean Architecture and More: Building Maintainable Mobile Apps \- Fastdev, [https://www.fastdev.com/blog/blog/building-maintainable-mobile-apps/](https://www.fastdev.com/blog/blog/building-maintainable-mobile-apps/)  
16. iamIcarus/kotlin-compose-coordinator-example: A flexible navigation pattern for Jetpack ... \- GitHub, [https://github.com/iamIcarus/kotlin-compose-coordinator-example](https://github.com/iamIcarus/kotlin-compose-coordinator-example)  
17. Create a multiplatform app using Ktor and SQLDelight \- Kotlin, [https://kotlinlang.org/docs/multiplatform/multiplatform-ktor-sqldelight.html](https://kotlinlang.org/docs/multiplatform/multiplatform-ktor-sqldelight.html)  
18. MVVM vs MVI Android: A Complete Practical Comparison Guide for 2026 \-, [https://androidstartguide.in/mvvm-vs-mvi-android-guide-2026/](https://androidstartguide.in/mvvm-vs-mvi-android-guide-2026/)  
19. Which navigation library for compose do you suggest? : r/androiddev \- Reddit, [https://www.reddit.com/r/androiddev/comments/101bbvu/which\_navigation\_library\_for\_compose\_do\_you/](https://www.reddit.com/r/androiddev/comments/101bbvu/which_navigation_library_for_compose_do_you/)  
20. Kotlin Multiplatform in Production: What Worked, What Didn't \- STRV, [https://www.strv.com/blog/kotlin-multiplatform-in-production-what-worked-what-didn-t](https://www.strv.com/blog/kotlin-multiplatform-in-production-what-worked-what-didn-t)  
21. Kotlin Multiplatform: Navigation battle \- Troido, [https://troido.com/kotlin-multiplatform-navigation-battle/](https://troido.com/kotlin-multiplatform-navigation-battle/)  
22. What are your thoughts on Decompose in KMP? : r/Kotlin \- Reddit, [https://www.reddit.com/r/Kotlin/comments/18ycsk7/what\_are\_your\_thoughts\_on\_decompose\_in\_kmp/](https://www.reddit.com/r/Kotlin/comments/18ycsk7/what_are_your_thoughts_on_decompose_in_kmp/)  
23. From MVVM to MVI: What Really Works in Compose | by Vaibhav Shakya \- Level Up Coding, [https://levelup.gitconnected.com/from-mvvm-to-mvi-what-really-works-in-compose-68f38cb6c047](https://levelup.gitconnected.com/from-mvvm-to-mvi-what-really-works-in-compose-68f38cb6c047)  
24. Mastering Multiplatform Navigation with Decompose | by Ahmed Omara \- Stackademic, [https://blog.stackademic.com/mastering-multiplatform-navigation-with-decompose-fe077135e695](https://blog.stackademic.com/mastering-multiplatform-navigation-with-decompose-fe077135e695)  
25. Navigating the Waters of Kotlin Multiplatform: Exploring Navigation Solutions | by Thomas Kioko™ | ProAndroidDev, [https://proandroiddev.com/navigating-the-waters-of-kotlin-multiplatform-exploring-navigation-solutions-eef81aaa1a61](https://proandroiddev.com/navigating-the-waters-of-kotlin-multiplatform-exploring-navigation-solutions-eef81aaa1a61)  
26. Has anyone tried to use navigation in compose multiplatform kotlinlang \#compose \- Kotlin Slack, [https://slack-chats.kotlinlang.org/t/16367626/has-anyone-tried-to-use-navigation-in-compose-multiplatform-](https://slack-chats.kotlinlang.org/t/16367626/has-anyone-tried-to-use-navigation-in-compose-multiplatform-)  
27. Navigation and routing | Kotlin Multiplatform Documentation, [https://kotlinlang.org/docs/multiplatform/compose-navigation-routing.html](https://kotlinlang.org/docs/multiplatform/compose-navigation-routing.html)  
28. Compose Multiplatform 1.10.0: Unified @Preview, Navigation 3, and Stable Compose Hot Reload \- The JetBrains Blog, [https://blog.jetbrains.com/kotlin/2026/01/compose-multiplatform-1-10-0/](https://blog.jetbrains.com/kotlin/2026/01/compose-multiplatform-1-10-0/)  
29. The new Compose Multiplatform navigation API is fantastic : r/Kotlin \- Reddit, [https://www.reddit.com/r/Kotlin/comments/1ioacjp/the\_new\_compose\_multiplatform\_navigation\_api\_is/](https://www.reddit.com/r/Kotlin/comments/1ioacjp/the_new_compose_multiplatform_navigation_api_is/)  
30. AI coding skill for Compose Multiplatform ↔ SwiftUI bidirectional interop. \- GitHub, [https://github.com/sorunokoe/swiftui-compose-skill](https://github.com/sorunokoe/swiftui-compose-skill)  
31. We are building a multiplatform shared module which is provi kotlinlang \#touchlab-tools, [https://slack-chats.kotlinlang.org/t/29846360/we-are-building-a-multiplatform-shared-module-which-is-provi](https://slack-chats.kotlinlang.org/t/29846360/we-are-building-a-multiplatform-shared-module-which-is-provi)  
32. Building Scalable Navigation System in Android | by Ahmed Abdelmeged \- ProAndroidDev, [https://proandroiddev.com/building-scalable-navigation-system-in-android-192bab6ddba3](https://proandroiddev.com/building-scalable-navigation-system-in-android-192bab6ddba3)  
33. johnpatrickmorgan/TCACoordinators: Powerful navigation in the Composable Architecture via the coordinator pattern \- GitHub, [https://github.com/johnpatrickmorgan/TCACoordinators](https://github.com/johnpatrickmorgan/TCACoordinators)  
34. How the Coordinator Pattern Broke My Brain | by Michael Sprindzuikate, [https://engineering.matchesfashion.com/how-the-coordinator-pattern-broke-my-brain-470660978a81](https://engineering.matchesfashion.com/how-the-coordinator-pattern-broke-my-brain-470660978a81)  
35. Let go try KMP shared logic separate Native UI (UI Part IOS ) | by Papon Smc \- Medium, [https://medium.com/@paponsmc/let-go-try-kmp-shared-logic-separate-native-ui-ui-part-ios-6289ac0967bd](https://medium.com/@paponsmc/let-go-try-kmp-shared-logic-separate-native-ui-ui-part-ios-6289ac0967bd)  
36. Dagger vs Hilt vs Koin in Android \- TechYourChance, [https://www.techyourchance.com/dagger-vs-hilt-vs-koin/](https://www.techyourchance.com/dagger-vs-hilt-vs-koin/)  
37. Kotlin Inject \- Chris Keenan Codes, [https://chrynan.codes/kotlin-inject/](https://chrynan.codes/kotlin-inject/)