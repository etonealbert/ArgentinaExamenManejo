# **Comprehensive Platform Strategy and Architectural Specification for Compose Multiplatform: iOS 26+ and Android 16–17 Alignment**

The selection of a cross-platform mobile architecture requires a detailed alignment of native development toolchains, execution environments, and distribution guidelines1. In modern mobile systems, establishing a unified codebase using Compose Multiplatform (CMP) and Kotlin Multiplatform (KMP) involves reconciling fundamentally different platform paradigms2.  
On iOS, the system-wide introduction of the "Liquid Glass" visual framework requires shifting navigation responsibilities from Compose-driven layouts to a native SwiftUI container system3. On Android, the introduction of Android 17 and Android Gradle Plugin (AGP) 9.2 introduces an adaptive-first standard that alters how applications manage UI layout changes, state transitions, and module dependencies6.  
This technical report provides a detailed blueprint for aligning a Compose Multiplatform application with the constraints of iOS 26+ and the latest two stable Android major versions: Android 16 (API Level 36\) and Android 17 (API Level 37\)6.

## **iOS Platform Specification and Toolchain Baseline**

### **App Store Submissions and Xcode Baselines**

To maintain access to the Apple ecosystem, the development pipeline must adhere to Apple's strict compilation baselines11. Starting April 28, 2026, Apple enforces a mandatory build baseline for all new applications and updates uploaded to App Store Connect11. Submissions must be compiled using Xcode 26 or later and target the iOS 26 SDK11.  
Applications built using older versions of Xcode or targeting older SDK baselines face automated rejection at the upload phase11. Additionally, developers must comply with the expanded age rating classification system (which includes 13+, 16+, and 18+ tiers) and implement third-party privacy manifests (PrivacyInfo.xcprivacy) that declare the usage of required-reason APIs, such as system user defaults and file timestamp records15.

### **Hardware Compatibility and Runtime Environment**

The launch of iOS 26 establishes a hardware baseline requiring devices equipped with the Apple A13 Bionic system-on-chip (SoC) or superior17. This requirement deprecates several legacy devices, removing support for the iPhone XS, XS Max, and XR generations17.

| Hardware Category | Supported iPhone Models (iOS 26+) | Chipset Baseline | RAM / Apple Intelligence Support |
| :---- | :---- | :---- | :---- |
| **Premium AI Tier** | iPhone 15 Pro, iPhone 15 Pro Max, iPhone 16 Series, iPhone 17 Series18 | A17 Pro / A18 / A1918 | 8 GB+ (Full Apple Intelligence capability)18 |
| **Standard Tier** | iPhone 11 through iPhone 15, iPhone SE (2nd/3rd Gen)18 | A13 Bionic to A16 Bionic17 | 4 GB \- 6 GB (No Apple Intelligence support)18 |
| **Unsupported** | iPhone XS, iPhone XS Max, iPhone XR and older17 | A12 Bionic and older17 | Legacy Architectures (Deprecated)17 |

### **The "Liquid Glass" Visual Language and SwiftUI Shell Integration**

iOS 26 introduces a significant shift in visual aesthetics with the "Liquid Glass" design system5. This aesthetic uses real-world optical characteristics, glassmorphic translucency, 3D refraction, and dynamic color shading to add depth and visual hierarchy to system interfaces1.  
Early iterations of the operating system revealed potential readability issues, prompting Apple to refine Liquid Glass by increasing text contrast, adjusting blur opacity, and standardizing background treatments based on accessibility guidelines1.  
Furthermore, because these real-time refractive effects are computationally expensive, the operating system disables them on older, non-premium devices23. To handle this variation in non-native environments, the application can use community libraries like Nadeem Iqbal's liquid-glass23. This library implements an automated quality fallback model based on the target hardware and operating system version24:

| Quality Tier | Blur Radius | Saturation | Backdrop Target | Target Environments |
| :---- | :---- | :---- | :---- | :---- |
| **Full** | **![][image1]** \[cite: 23, 25\] | ![][image2] \[cite: 23, 25\] | Full Resolution23 | Android 12+ (non-low-RAM), iOS 17+, Desktop, Web24 |
| **Medium** | **![][image3]** \[cite: 24, 25\] | ![][image4] \[cite: 24\] | ![][image5] Downsampled24 | iOS 15 to iOS 1625 |
| **Fallback** | **![][image6]** \[cite: 24, 25\] | ![][image7] \[cite: 24\] | Flat Tint Layer24 | Android \< 12, Low-RAM Devices, iOS \< 1525 |

Because Liquid Glass relies on system-level view compositing and real-time backdrop shaders, a pure canvas-bound Compose Multiplatform app cannot render these effects natively3. To adopt Liquid Glass, a hybrid design must be implemented3. In this architecture, a native SwiftUI shell manages top-level navigation structures (such as TabView and NavigationStack), while Compose Multiplatform renders the screen-specific content3.

* **Navigation Model:** SwiftUI requires target paths to be Hashable and Identifiable3. The application maps shared Kotlin routes (defined as sealed interfaces in common code) to a Swift structural wrapper (RouteWrapper) that uses a unique identifier to ensure that pushing duplicate screens onto the navigation stack is handled correctly3. Swift-based coordinators track navigation state using @Observable controllers3.  
* **Kotlin ViewController Handshakes:** The shared iOS source set (iosMain) exposes two primary configuration overloads for ComposeUIViewController3:  
  * MainViewController(topLevelRoute, onNavigate, onGoBack, onActivate) renders the root screen of each native tab3. It runs Compose composition but delegates navigation actions to SwiftUI coordinators through callbacks3.  
  * ScreenViewController(route, onNavigate, onGoBack) isolates detail screens3. It sets a CompositionLocal parameter (LocalUseNativeNavigation to true), notifying the internal Compose UI to omit duplicate header elements, navigation bars, or back buttons, allowing SwiftUI to draw them natively with Liquid Glass materials3.  
* **Pre-iOS 26 Fallback:** For older iOS versions where Liquid Glass is unavailable, the application can fall back to standard, fully Compose-driven navigation without SwiftUI callbacks to ensure compatibility across targets3.

### **KMP Deployment Target and Swift Interoperability**

Configuring a minimum deployment target of iOS 26.0 within Kotlin Multiplatform simplifies the compilation pipeline by allowing the build toolchain to bypass legacy architectures. Most notably, the 32-bit iOS ARM targets (iosArm32), which were deprecated in Kotlin 1.8.20 and fully removed in Kotlin 1.9.20, are completely omitted26. Building exclusively for standard 64-bit platforms—iosArm64 for physical devices and iosSimulatorArm64 for Apple Silicon-based Mac hosts—eliminates compilation friction in resource generation plugins (such as Moko-Resources) that historically attempted to parse configurations for deprecated slices26.  
For asynchronous data flow across the Objective-C/Swift boundary, the toolchain must utilize advanced interop compilers like SKIE2. SKIE bridges the gap between Kotlin Coroutine Flow types and Swift’s AsyncSequence, preserving bidirectional cancellation and preventing memory leaks or main-thread blocks during async/await operations28.

### **Testing Requirements on Simulator and Physical Devices**

Testing on iOS must combine local simulator environments with real-device validation to ensure performance and layout consistency under real-world conditions14.

* **Local Simulator Validation:** Local automated verification utilizes Apple Silicon-based execution targets (iosSimulatorArm64) to confirm compilation success, dependency linking, and core visual layout rendering29.  
* **Physical Device Testing:** Real device testing is necessary to profile memory consumption, analyze Liquid Glass shader rendering performance using Apple's Instruments, and verify biometric authentication flows12.  
* **Test Automation Tooling:** The testing architecture must account for the limitations of open-source automation systems. While YAML-based systems like Maestro provide simple test authoring, they do not support physical iOS devices in their open-source versions31. To run automated end-to-end tests on real physical devices, the engineering team must use cloud-based testing platforms or native testing tools31.

## **Android Platform Specification and Distribution Context**

### **Version Matrix and API Definitions**

The application architecture must support the latest two stable major Android versions available in 2026: Android 17 (API level 37, 'Cinnamon Bun') and Android 16 (API level 36, 'Baklava')6.

* **Android 17 (API Level 37):** Shipped as the primary stable release on June 16, 20266.  
* **Android 16 (API Level 36):** Released on June 10, 2025, representing the previous stable major version9.

Android OS Major Version Tracking Index (2025 \- 2026\)  
┌──────────────────────┬───────────┬──────────────┬───────────────────┐  
│ OS Platform Release  │ API Level │ Release Date │ Security Support  │  
├──────────────────────┼───────────┼──────────────┼───────────────────┤  
│ Android 17           │ API 37    │ Jun 16, 2026 │ Yes               │  
│ Android 16           │ API 36    │ Jun 10, 2025 │ Yes               │  
│ Android 15           │ API 35    │ Sep 03, 2024 │ Yes               │  
│ Android 14           │ API 34    │ Oct 04, 2023 │ Yes (Active Patch)│  
└──────────────────────┴───────────┴──────────────┴───────────────────┘

(Data compiled from modern Android OS lifecycles and security patching records9)

### **SDK Target Recommendations**

To target the latest system capabilities while maintaining compatibility with the required version range, the Android-specific build configuration should be defined as follows:

Kotlin  
android {  
    compileSdk \= 37    // Targets the latest Android 17 APIs \[cite: 8, 35\]  
    targetSdk \= 37     // Ensures compliance with Android 17 runtime changes \[cite: 6, 8\]  
    minSdk \= 36        // Restricts execution to Android 16 and Android 17  
}

### **Strategic Market Restrictiveness Analysis**

Restricting a commercial application to a minimum SDK of 36 is highly restrictive and presents a significant commercial risk for mainstream consumer-facing products36. The fragmentation of the Android ecosystem in 2026 highlights a slow adoption curve for newer platform releases36.

Android OS Version Distribution (Mid-2026)  
┌──────────────────────────────────────────────┬───────────┐  
│ OS Version (API Level)                       │ Share (%) │  
├──────────────────────────────────────────────┼───────────┤  
│ Android 17 (API 37\)                          │ \<1.0%     │  
│ Android 16 (API 36\)                          │ 7.5%      │  
│ Android 15 (API 35\)                          │ 19.3%     │  
│ Android 14 (API 34\)                          │ 17.2%     │  
│ Android 13 (API 33\)                          │ 13.9%     │  
│ Android 12 & Older (API 31 and below)        │ 42.1%     │  
└──────────────────────────────────────────────┴───────────┘

(Data compiled from 2026 Google and industry ecosystem distribution surveys9)  
Establishing minSdk \= 36 excludes approximately 92.5% of active Android devices globally36. This decision is only technically or commercially viable under specific scenarios:

1. **High-Performance Enterprise Fleets:** Systems deployed to corporate-managed, modern hardware (e.g., Pixel 9/10, Samsung Galaxy S25/S26 Series) where OS updates are strictly mandated6.  
2. **On-Device Generative AI Exclusives:** Applications whose core functionality depends strictly on Neural Processing Unit (NPU) APIs and dynamic memory allocations introduced natively in Android 16/1717.  
3. **Developer-Facing Tools:** Niche applications targeting cutting-edge engineering audiences running preview and early stable releases.

For standard commercial consumer applications, the standard industry recommendation is to target a minSdk \= 24 (Nougat) or minSdk \= 26 (Oreo) to capture over 95% of the addressable global market, while using runtime checks to execute advanced features on Android 16 and 179.

### **Google Play Store Target SDK Mandates**

Google Play enforces an annual rolling baseline requirement to maintain app discoverability and security37. By **August 31, 2026**, all new applications and major updates submitted to the Google Play Store must target **Android 16 (API level 36\)** or higher37. Applications targeting outdated APIs are restricted from publishing updates and lose visibility on the store for users running newer Android versions37.

### **Android 17 Behavior and Runtime Adjustments**

To achieve stability on Android 17, several system behavior changes must be addressed6:

* **Adaptive Layout Mandate:** For apps targeting API Level 37, Android 17 removes the option to restrict orientation and aspect ratios on large screens (devices with a smallest width greater than 600 dp)6. System properties like screenOrientation and resizeableActivity=false are completely ignored6. The interface must adapt dynamically to multi-window configurations, desktop environments, and foldable state transitions6.  
* **Activity Recreation Optimizations:** Android 17 optimizes system-level performance by changing how it handles certain configuration updates6. By default, the operating system no longer restarts active instances during changes that do not require a full layout redraw, such as physical keyboard attachments or color space adjustments6. Instead, these updates are dispatched directly to the existing instance via the onConfigurationChanged() callback6. If the application's lifecycle logic requires a full restart for these events, developers must explicitly opt in using the android:recreateOnConfigChanges manifest attribute6.  
* **Task Continuation:** The "Continue On" framework helps users seamlessly transition active tasks between Android devices (e.g., from a phone to a tablet) by implementing setHandoffEnabled(true)6.  
* **Stricter Memory Limits:** To improve system stability, Android 17 enforces stricter runtime memory limits that scale based on the host device's total RAM capacity38. If an application exceeds these thresholds, the OS terminates it immediately to prioritize overall system performance38.  
* **System Keystore Restraints:** Non-system applications targeting API Level 37 are limited to a maximum of 50,000 active keys in the Android Keystore to prevent resource exhaustion42. If an application exceeds this limit, attempts to generate new cryptographic keys throw an exception with the error code ERROR\_TOO\_MANY\_KEYS42.  
* **ACCESS\_LOCAL\_NETWORK Permission:** Applications targeting Android 17 must request the ACCESS\_LOCAL\_NETWORK runtime permission before connecting to local network resources, such as smart home hardware or casting targets38. Alternatively, they can use built-in system device pickers, which are exempt from this requirement43.  
* **SMS One-Time Password (OTP) Delays:** To prevent credential hijacking, Android 17 restricts programmatic access to SMS messages for standard OTP parsing42. Applications trying to read standard verification messages without domain-specific WebOTP metadata face a three-hour access delay, during which background SMS broadcasts are blocked42. To avoid this restriction, applications should use the official SMS Retriever or SMS User Consent APIs42.  
* **Dynamic Code Loading Security:** Applications targeting Android 17 or higher must mark native libraries loaded via System.load() as read-only43. If the system detects that a native library is modifiable, it blocks execution and throws an UnsatisfiedLinkError to protect against runtime injection attacks43.

## **Kotlin Multiplatform Compilation and Architectural Impact**

### **The AGP 9.2 Build Pipeline**

Historically, multiplatform library modules could apply both the org.jetbrains.kotlin.multiplatform plugin and the com.android.application (or com.android.library) plugins in the same Gradle configuration7. Under AGP 9.x, this configuration is strictly incompatible7.  
To compile a KMP application under the modern AGP 9.2 pipeline, developers must extract the Android application entry point into an isolated, standalone Gradle module (typically named :androidApp), which applies the com.android.application plugin7. This module leverages AGP 9's built-in Kotlin compilation, eliminating the need for a separate kotlin-android plugin7. The shared core logic and Compose Multiplatform UI code must reside in a separate KMP library module (typically named :composeApp), which applies the org.jetbrains.kotlin.multiplatform plugin alongside the newly introduced com.android.kotlin.multiplatform.library plugin7.

AGP 9.x De-Coupled Multiplatform Module Dependency Graph  
┌────────────────────────────────────────────────────────┐  
│                      :androidApp                       │  
│     (com.android.application \+ Built-in Kotlin)       │  
└───────────────────────────┬────────────────────────────┘  
                            │ (Depends on)  
                            ▼  
┌────────────────────────────────────────────────────────┐  
│                      :composeApp                       │  
│  (org.jetbrains.kotlin.multiplatform)                  │  
│  (com.android.kotlin.multiplatform.library)            │  
└────────────────────────────────────────────────────────┘

(Architectural schema illustrating the AGP 9.x separated compilation model7)

### **Root-Level Build Configuration (build.gradle.kts)**

The root configuration defines the toolchain plugin versions using the Gradle version catalog48.

Kotlin  
plugins {  
    alias(libs.plugins.androidApplication) apply false  
    alias(libs.plugins.androidKmpLibrary) apply false  
    alias(libs.plugins.kotlinMultiplatform) apply false  
    alias(libs.plugins.composeMultiplatform) apply false  
    alias(libs.plugins.composeCompiler) apply false  
}

### **Shared Module Configuration (composeApp/build.gradle.kts)**

The KMP shared module configures the cross-platform targets, compilation options, and dependencies across common and platform-specific source sets49.

Kotlin  
plugins {  
    alias(libs.plugins.kotlinMultiplatform)  
    alias(libs.plugins.androidKmpLibrary)  
    alias(libs.plugins.composeMultiplatform)  
    alias(libs.plugins.composeCompiler)  
    alias(libs.plugins.kotlinxSerialization)  
}

kotlin {  
    // Configures the Android KMP target under AGP 9.x  
    android {  
        namespace \= "com.enterprise.app.shared"  
        compileSdk \= 37  
        minSdk \= 36  
        withJava() // Enables compilation of Java source files if necessary \[cite: 35\]  
          
        compilerOptions {  
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM\_17)  
        }  
    }  
      
    // Explicit iOS architecture definitions  
    listOf(  
        iosArm64(),  
        iosSimulatorArm64()  
    ).forEach { iosTarget \-\>  
        iosTarget.binaries.framework {  
            baseName \= "SharedAppFramework"  
            isStatic \= true // Required for swift package binary distribution \[cite: 27\]  
            linkerOpts.add("-lsqlite3") // Dynamically links system SQLite for NativeSQLiteDriver  
        }  
    }  
      
    sourceSets {  
        commonMain.dependencies {  
            implementation(compose.runtime)  
            implementation(compose.foundation)  
            implementation(compose.material3)  
            implementation(libs.ktor.client.core)  
            implementation(libs.ktor.client.content.negotiation)  
            implementation(libs.ktor.serialization.kotlinx.json)  
            implementation(libs.kotlinx.coroutines.core)  
            implementation(libs.kotlinx.datetime)  
            implementation(libs.koin.core)  
            implementation(libs.androidx.room.runtime)  
            implementation(libs.sqlite.bundled) // For bundled SQLite driver configuration  
            implementation(libs.grant.permissions) // Professional logic-first permissions  
        }  
          
        androidMain.dependencies {  
            implementation(libs.ktor.client.okhttp) // High-performance Android network engine \[cite: 53\]  
        }  
          
        iosMain.dependencies {  
            implementation(libs.ktor.client.darwin) // Native Apple network engine \[cite: 49\]  
        }  
    }  
}

### **Application Module Entry Point (androidApp/build.gradle.kts)**

The standalone Android application module manages the application manifest, package signing, and initializes the target runtime44.

Kotlin  
plugins {  
    alias(libs.plugins.androidApplication)  
    alias(libs.plugins.composeCompiler)  
}

android {  
    namespace \= "com.enterprise.app"  
    compileSdk \= 37  
      
    defaultConfig {  
        applicationId \= "com.enterprise.app"  
        minSdk \= 36  
        targetSdk \= 37  
        versionCode \= 100  
        versionName \= "1.0.0"  
        testInstrumentationRunner \= "androidx.test.runner.AndroidJUnitRunner" \[cite: 29\]  
    }  
      
    compileOptions {  
        sourceCompatibility \= JavaVersion.VERSION\_17  
        targetCompatibility \= JavaVersion.VERSION\_17  
    }  
}

dependencies {  
    implementation(project(":composeApp")) // Pulls in the shared KMP library code  
    implementation(libs.androidx.activity.compose)  
    implementation(libs.koin.android)  
    androidTestImplementation(libs.androidx.ui.test.junit4) \[cite: 29\]  
    debugImplementation(libs.androidx.ui.test.manifest) \[cite: 29\]  
}

### **Database Driver Topography**

Managing local SQLite databases across targets involves specific differences in driver configurations and package imports51.

┌────────────────────────────────────────────────────────┐  
│                        :shared                         │  
│                    (Database.kt)                       │  
│     (Abstract AppDatabase : RoomDatabase)              │  
└───────────────────────────┬────────────────────────────┘  
                            │ (Implements Platform Factory)  
              ┌─────────────┴─────────────┐  
              ▼                           ▼  
┌───────────────────────────┐┌───────────────────────────┐  
│        androidMain        ││          iosMain          │  
│   (Database.android.kt)   ││     (Database.ios.kt)     │  
│   (AndroidSQLiteDriver/   ││   (NativeSQLiteDriver/    │  
│   BundledSQLiteDriver)    ││   BundledSQLiteDriver)    │  
└───────────────────────────┘└───────────────────────────┘

(Architectural comparison of multiplatform persistence compilation boundaries51)

| Local Persistence Engine | Target Source Set | Dependency Artifact Coordinate | Active Runtime SQLite Driver |
| :---- | :---- | :---- | :---- |
| **Room Database KMP** | commonMain | androidx.room3:room3-runtime \[cite: 56\] | androidx.sqlite:sqlite-bundled \[cite: 51\] |
|  | androidMain | androidx.room3:room3-runtime \[cite: 56\] | AndroidSQLiteDriver (System SQLite)51 |
|  | iosMain | androidx.room3:room3-runtime \[cite: 56\] | NativeSQLiteDriver (Requires \-lsqlite3)51 |
| **SQLDelight KMP** | commonMain | app.cash.sqldelight:coroutines-extensions \[cite: 57\] | Core Schema Definition57 |
|  | androidMain | app.cash.sqldelight:android-driver \[cite: 57\] | AndroidSqliteDriver (Requires Context)57 |
|  | iosMain | app.cash.sqldelight:native-driver \[cite: 57\] | NativeSqliteDriver \[cite: 57, 58\] |

### **Modern Permission Architecture (Grant API)**

Handling runtime permissions in multiplatform projects can introduce significant friction, frequently leading to application hangs or crashes due to minor differences in OS lifecycles52. In 2026, the standard enterprise architecture leverages **Grant**, a production-hardened KMP permission manager that operates directly within ViewModels or business logic without requiring reference hooks to Android Activities or iOS ViewControllers52.

* **iOS Framework Isolation:** Grant dynamically routes permission handlers based on compile targets52. Unused iOS framework dependencies (such as Location, Bluetooth, or HealthKit) are strictly isolated52. This prevents the Xcode linker from automatically pulling unused frameworks into the final binary, eliminating "phantom" usage declarations inside Info.plist that trigger automated App Store rejections52.  
* **iOS Crash-Guard:** Grant intercepts permission request routines on iOS to verify that the required NSUsageDescription keys exist within the runtime environment before execution52. If a key is missing, it logs a graceful validation error rather than triggering a fatal SIGABRT exception in production52.  
* **Android Process-Death Resilience:** Under memory-constrained scenarios, the Android OS frequently kills background application processes59. Grant integrates directly with Android's SavedStateHandle inside standard ViewModel scopes52. It persists transactional permission request states across process-death boundaries, eliminating timeouts or deadlocked callbacks when the application returns to the foreground52.

## **Detailed Architectural Deliverables**

### **Technical SDK Baseline Strategy**

The platform baseline configurations must be defined as follows to ensure compatibility across the target OS versions:

| Platform Target | Recommended SDK Baseline | Target Runtime Baseline | Minimum Deployment Option |
| :---- | :---- | :---- | :---- |
| **Android JVM Target** | API Level 37 (Cinnamon Bun)9 | API Level 37 (Android 17\)6 | API Level 36 (Android 16\)9 |
| **Apple iOS Target** | iOS 26 SDK (or later)11 | Xcode 26 Toolchain11 | iOS 26.014 |

### **Comprehensive Device Test Matrix**

To ensure visual and functional consistency across both physical and virtual devices, the QA pipeline must validate against a representative matrix of target screens and hardware14:

| Device Category | Model Designation | OS Version / API Level | Execution Type | Core Verification Focus |
| :---- | :---- | :---- | :---- | :---- |
| **Android Flagship** | Google Pixel 9 / 10 | Android 17 (API 37\)6 | Physical Device | Desktop Mode transitions, Adaptive Layout changes, Task Hand-offs6 |
| **Android Foldable** | Samsung Galaxy Fold 7 | Android 16 (API 36\)10 | Physical Device | Split-Screen multi-window behavior, resizability, aspect ratio scaling6 |
| **Android Virtual** | Standard AOSP Emulator | Android 17 (API 37\)9 | x86\_64 Emulator | App Bubbles, Bubble Bar UI boundaries, Keystore key limits6 |
| **iOS Premium AI** | iPhone 17 Pro / Air61 | iOS 27 (Beta / RC)18 | Physical Device | SwiftUI Liquid Glass Refraction Shaders, Live Translate performance3 |
| **iOS Standard** | iPhone 11 / 13 | iOS 26.561 | Physical Device | Thermal throttling, Liquid Glass fallback UI rendering3 |
| **iOS Virtual** | iPhone 16 Simulator | iOS 26.011 | Simulator (arm64) | NavigationStack transition syncing, CoreData/Room SQLite driver operations3 |

### **Continuous Integration (CI) Automation Matrix**

Executing a multiplatform CI pipeline requires separating tasks between Linux runners (for Android JVM testing and building) and macOS runners (which are required for Apple compilations and framework linking)30.

                 CI Pipeline Execution Flow (GitHub Actions / TeamCity)  
                   
                                 ┌──────────────────────┐  
                                 │     Commit Event     │  
                                 └──────────┬───────────┘  
                                            ▼  
                                 ┌──────────────────────┐  
                                 │     common-tests     │  
                                 │ (Ubuntu Run: JVM)    │  
                                 └──────────┬───────────┘  
                                            │  
                     ┌──────────────────────┴──────────────────────┐  
                     ▼ (On Success)                                ▼ (On Success)  
          ┌──────────────────────┐                      ┌──────────────────────┐  
          │    android-build     │                      │      ios-build       │  
          │  (Ubuntu Run: AGP)   │                      │  (macOS Run: Xcode)  │  
          └──────────────────────┘                      └──────────────────────┘

(CI pipeline structure mapping dependencies to optimize host billing and verification speed30)

#### **1\. Unit & Logic Verification (common-tests)**

* **Runner Environment:** Ubuntu Virtual Machine30  
* **Toolchain Base:** Java 17 (JDK Temurin)62, Gradle 9.4.18  
* **Target Task:** Compiles the common main source set (commonMain) and executes unit tests, ensuring the Koin DI graph resolves successfully30.  
* **Execution Script:** ./gradlew :composeApp:jvmTest  
  \[cite: 29, 62\]

#### **2\. Android Artifact Verification (android-build)**

* **Runner Environment:** Ubuntu Virtual Machine30  
* **Toolchain Base:** Android SDK platform tools, API Level 37 libraries8  
* **Target Task:** Executes Android unit tests using Robolectric and Roborazzi for local screenshot validation, compiling the final release Android App Bundle37.  
* **Execution Script:** ./gradlew :androidApp:bundleRelease :androidApp:testReleaseUnitTest

#### **3\. iOS Toolchain Verification (ios-build)**

* **Runner Environment:** macOS Virtual Machine30  
* **Toolchain Base:** Xcode 26, iOS 26 SDK11  
* **Target Task:** Compiles and links the static KMP framework (SharedAppFramework), executes simulator unit tests, and verifies the native SwiftUI shell compilation3.  
* **Execution Script:**  
  Bash  
  ./gradlew :composeApp:iosSimulatorArm64Test  
  xcodebuild build \-project iosApp/iosApp.xcodeproj \-scheme iosApp \-sdk iphonesimulator \-configuration Release \[cite: 62\]

## **Technical Platform Risk Assessment**

Architectural decisions involve compromises. Below is a structured assessment of technical challenges identified within this modern platform configuration.

### **1\. Extreme Market Exclusion**

* **Classification:** Commercial Risk  
* **Impact Level:** High  
* **Technical Context:** Setting the minimum SDK to 36 excludes over 92% of the active global Android market36.  
* **Mitigation Strategy:** Set the minimum SDK to 26 (Oreo) and use conditional checks (Build.VERSION.SDK\_INT \>= 36\) to unlock Android 16/17 specific features, such as background audio hardening or task hand-offs, only on supported hardware6.

### **2\. Mandatory Aspect Ratio and Resizability Scaling**

* **Classification:** UI Layout Risk  
* **Impact Level:** High  
* **Technical Context:** Applications targeting API Level 37 cannot opt out of resizability and orientation changes on screens larger than 600 dp6.  
* **Mitigation Strategy:** Implement adaptive layout structures. Avoid using absolute screen coordinate measurements; instead, use window size metrics (WindowWidthSizeClass) and query available bounding areas dynamically66. Validate layout behavior under extreme aspect ratio shifts using device override configurations inside unit tests60.

### **3\. SwiftUI Navigation Syncing**

* **Classification:** Architecture Risk  
* **Impact Level:** High  
* **Technical Context:** Running a native SwiftUI shell requires syncing navigation states between two distinct platforms: Swift’s declarative NavigationStack and Kotlin's multiplatform state management3. A synchronization failure can lead to duplicate screen pushes, broken gestures, or memory leaks3.  
* **Mitigation Strategy:** Establish SwiftUI as the single source of truth for navigation on iOS3. When a user triggers navigation within a Compose view, intercept the action, map it to a Swift route, push it directly onto SwiftUI's NavigationStack, and immediately clear it from Compose's internal backstack3.

### **4\. SMS Programmatic OTP Delays**

* **Classification:** Authentication Risk  
* **Impact Level:** Medium  
* **Technical Context:** Android 17 delays access to SMS messages for standard OTP parsing by three hours unless they contain domain-specific WebOTP metadata42.  
* **Mitigation Strategy:** Migrate the registration flow to use Google's official SMS Retriever API or SMS User Consent API, which are exempt from this security delay42.

### **5\. Low-Memory Terminations**

* **Classification:** Stability Risk  
* **Impact Level:** Medium  
* **Technical Context:** Android 17 monitors application memory usage more aggressively based on target device configurations and terminates processes that exceed strict dynamic limits to prevent system-wide stuttering38.  
* **Mitigation Strategy:** Integrate memory profile monitors. When utilizing heavy backdrop rendering or large local datasets, enforce strict memory optimizations. If necessary, configure a fallback visual quality tier (e.g., swapping translucent Liquid Glass backdrop blurs for flat solid color layers) on memory-constrained devices by querying ActivityManager.isLowRamDevice() at startup23.

## **App Store Connect Submission Checklist**

Verify the following requirements are met before uploading a build to App Store Connect to prevent automated rejections11:

* \[ \] **Xcode Version Verification:** Ensure the final build is archived on a Mac running Xcode 26.0 or higher, with the base SDK explicitly set to the iOS 26 SDK11.  
* \[ \] **Privacy Manifest Integration:** Ensure a valid PrivacyInfo.xcprivacy file exists in the root directory of the application bundle15. This manifest must declare:  
  * All usages of required-reason APIs (such as file timestamps, user defaults, active keyboards, or disk space APIs)16.  
  * The categories of user data collected by the application and any integrated third-party SDKs15.  
* \[ \] **Third-Party Signature Verification:** Confirm that all external libraries are updated to versions containing signed privacy manifests15.  
* \[ \] **Visual Layout Constraints:** Verify that interactive elements have a hit target of at least ![][image8] density-independent points to comply with accessibility standards15.  
* \[ \] **In-App Purchase Architecture:** Ensure that all digital purchases utilize Apple's official StoreKit framework15. Including links to external payment processors within the app is prohibited15.  
* \[ \] **Alternative Marketplace Configuration:** If distributing within the Brazilian storefront (on iOS 26.5+), verify if alternative app marketplaces are enabled67. Ensure the mandatory 5% Core Technology Commission (CTC) fee structure is configured if opting out of standard in-app purchase systems67.  
* \[ \] **AI-Generated Content Protections:** If the application utilizes external generative AI APIs (such as OpenAI, Claude, or Gemini)15:  
  * Present a clear consent screen during onboarding, naming the provider and detailing what data is shared14.  
  * Provide functional user moderation tools, including "Report" and "Block" features for inappropriate content12.  
* \[ \] **Age Rating Compliance:** Verify that the updated age rating questionnaire (incorporating the 13+, 16+, and 18+ tiers introduced in the July 2025 guidelines) is completed14.  
* \[ \] **Reviewer Access Credentials:** Provide active, non-expired test account credentials in the Reviewer Notes field in App Store Connect, ensuring any backend dependencies are reachable during the review process12.

## **Google Play Console Submission Checklist**

Verify the following technical and policy requirements are met before submitting the production build to the Google Play Store37:

* \[ \] **App Bundle Packaging:** Verify the build artifact is compiled as an Android App Bundle (.aab)37. Legacy .apk formats are rejected for standard distribution37.  
* \[ \] **Target SDK Level Validation:** Ensure that the bundle specifies targetSdkVersion \= 36 (or higher) to pass Play Store verification37.  
* \[ \] **16KB Page Alignment:** If the application includes native libraries (.so files), ensure they are compiled with 16KB page alignment37. The older 4KB alignment is incompatible with modern Android 16/17 device architectures13.  
* \[ \] **Permission Access Justifications:** Audit the final manifest for high-risk system permissions37:  
  * If background location access is requested (ACCESS\_BACKGROUND\_LOCATION), upload a video to the Play Console demonstrating a valid use-case37.  
  * If reading contacts is required, verify that the application uses the secure, permission-free Android Contacts Picker API instead of requesting the dangerous READ\_CONTACTS manifest permission37.  
* \[ \] **Data Safety Alignment:** The Data Safety declarations in the console must match the actual data transmission patterns of the application and all embedded analytics, advertising, and crash-reporting SDKs37.  
* \[ \] **Account Deletion Pipeline:** If the application supports account creation, provide an in-app option to request account deletion and remove associated user data from remote servers15. Additionally, configure a functional external HTTPS web deletion page in the console37.  
* \[ \] **Personal Account Testing Compliance:** If publishing from a personal developer account created after November 2023, verify that the application has completed a closed testing phase with at least 12 (or 20, depending on precise region guidelines) unique testers who have kept the app installed continuously for 14 days37.  
* \[ \] **Corporate Sandbox Requirements:** If the application handles financial transactions, health monitoring, or governmental actions, confirm that the console profile is registered as a Corporate Organization rather than a Personal developer profile37.

## **Conclusion**

Deploying a multiplatform application across iOS 26+ and modern Android versions requires a detailed alignment of native development toolchains, execution environments, and distribution guidelines2. Rather than treating the target operating systems as a generic graphics canvas, the recommended approach is to adopt a hybrid architecture1.  
By using a native SwiftUI shell to host iOS 26's Liquid Glass materials natively, developers can preserve platform-specific aesthetics while sharing business logic and local storage services in Kotlin2.  
On Android, adapting to AGP 9.2's separated module architecture early prevents compile-time breaking changes7. Finally, to maximize market reach, developers should avoid setting minSdk \= 36 in production, choosing instead to target a broader device base while dynamically unlocking Android 16/17 capabilities on supported hardware6. This approach ensures a highly performant, visually consistent, and robust codebase across both ecosystems.

#### **Works cited**

1. Apple Liquid Glass: Flutter, React Native & Compose MP \- Vagary Tech, [https://vagary.tech/blog/apple-liquid-glass-flutter-react-native-compose-mp](https://vagary.tech/blog/apple-liquid-glass-flutter-react-native-compose-mp)  
2. Kotlin Multiplatform in 2026: The Developer's Deep Dive into KMP vs Native vs Flutter, [https://medium.com/@johnsafwat362/kotlin-multiplatform-in-2026-the-developers-deep-dive-into-kmp-vs-native-vs-flutter-ada63a569597](https://medium.com/@johnsafwat362/kotlin-multiplatform-in-2026-the-developers-deep-dive-into-kmp-vs-native-vs-flutter-ada63a569597)  
3. Liquid Glass in a Compose Multiplatform app \- Kotlin, [https://kotlinlang.org/docs/multiplatform/ios-liquid-glass.html](https://kotlinlang.org/docs/multiplatform/ios-liquid-glass.html)  
4. Kotlin Multiplatform (KMP): The Ultimate Guide (2026) \- commonMain.dev, [https://commonmain.dev/kotlin-multiplatform/](https://commonmain.dev/kotlin-multiplatform/)  
5. iOS 26: Everything We Know | MacRumors, [https://www.macrumors.com/roundup/ios-26/](https://www.macrumors.com/roundup/ios-26/)  
6. Android 17 is here \- Android Developers Blog, [https://android-developers.googleblog.com/2026/06/Android-17.html](https://android-developers.googleblog.com/2026/06/Android-17.html)  
7. Update your Kotlin projects for Android Gradle Plugin 9.0 \- The JetBrains Blog, [https://blog.jetbrains.com/kotlin/2026/01/update-your-projects-for-agp9/](https://blog.jetbrains.com/kotlin/2026/01/update-your-projects-for-agp9/)  
8. Android Gradle plugin 9.2.0 (April 2026\) | Android Studio, [https://developer.android.com/build/releases/agp-9-2-0-release-notes](https://developer.android.com/build/releases/agp-9-2-0-release-notes)  
9. Android OS \- endoflife.date, [https://endoflife.date/android](https://endoflife.date/android)  
10. Android version history \- Wikipedia, [https://en.wikipedia.org/wiki/Android\_version\_history](https://en.wikipedia.org/wiki/Android_version_history)  
11. Apple Mandates Xcode 26 for App Store in 2026 \- Seasia Infotech, [https://www.seasiainfotech.com/blog/apple-mandates-xcode-26-for-app-store-submissions](https://www.seasiainfotech.com/blog/apple-mandates-xcode-26-for-app-store-submissions)  
12. How to Submit Your App to the App Store \[ 2026 Best Guide \], [https://premiumappdeveloper.com/how-to-submit-your-app-to-the-app-store/](https://premiumappdeveloper.com/how-to-submit-your-app-to-the-app-store/)  
13. Xcode 26 for App Store Submissions (Effective April 28, 2026\) \- HCL Support Portal, [https://support.hcl-software.com/csm?id=kb\_article\&sysparm\_article=KB0129040](https://support.hcl-software.com/csm?id=kb_article&sysparm_article=KB0129040)  
14. Apple App Store Submission Changes — April 2026 | by Neeshu Kumar \- Medium, [https://medium.com/@thakurneeshu280/apple-app-store-submission-changes-april-2026-5fa8bc265bbe](https://medium.com/@thakurneeshu280/apple-app-store-submission-changes-april-2026-5fa8bc265bbe)  
15. Apple App Store Rejection Guide 2026: The 15 Most Common Reasons and How to Fix Each \- OpenSpace Services, [https://www.openspaceservices.com/blog/mobile-app-development/apple-app-store-rejection-guide-2026-the-15-most-common-reasons-and-how-to-fix-each](https://www.openspaceservices.com/blog/mobile-app-development/apple-app-store-rejection-guide-2026-the-15-most-common-reasons-and-how-to-fix-each)  
16. How to Publish Your App to the App Store in 2026 \- Cynoteck Technology Solutions, [https://www.cynoteck.com/blog-post/how-to-publish-app-to-the-app-store](https://www.cynoteck.com/blog-post/how-to-publish-app-to-the-app-store)  
17. iOS 26 \- Wikipedia, la enciclopedia libre, [https://es.wikipedia.org/wiki/IOS\_26](https://es.wikipedia.org/wiki/IOS_26)  
18. iOS 27 Release Date: Sept 14, 2026 Launch Timeline \- eCorpIT, [https://ecorpit.com/when-is-ios-27-dropping/](https://ecorpit.com/when-is-ios-27-dropping/)  
19. iOS 26: qué novedades tuvo, iPhone compatibles y más sobre el sistema operativo de Apple \- Applesfera, [https://www.applesfera.com/nuevo/ios-19-informacion](https://www.applesfera.com/nuevo/ios-19-informacion)  
20. iOS 19 Release Date, Supported Devices, Features, Other Details\! \- Cashify, [https://www.cashify.in/ios-19-release-date-supported-devices-features-other-details](https://www.cashify.in/ios-19-release-date-supported-devices-features-other-details)  
21. Así puedes elegir entre iPhone 15, 16 o 17 según precio, cámara, batería y diseño, [https://www.infobae.com/tecno/2026/06/21/asi-puedes-elegir-entre-iphone-15-16-o-17-segun-precio-camara-bateria-y-diseno/](https://www.infobae.com/tecno/2026/06/21/asi-puedes-elegir-entre-iphone-15-16-o-17-segun-precio-camara-bateria-y-diseno/)  
22. iOS 27 Guide: All the new features coming to compatible iPhones in 2026 \- Macworld, [https://www.macworld.com/article/2986799/ios-27-new-iphone-features-release-date-beta-compatiblity-apple-intelligence-siri.html](https://www.macworld.com/article/2986799/ios-27-new-iphone-features-release-date-beta-compatiblity-apple-intelligence-siri.html)  
23. Frosted glass for Compose Multiplatform without OOMing on low-end Android, [https://dev.to/nadeemiqbal/frosted-glass-for-compose-multiplatform-without-ooming-on-low-end-android-5hkj](https://dev.to/nadeemiqbal/frosted-glass-for-compose-multiplatform-without-ooming-on-low-end-android-5hkj)  
24. GitHub \- NadeemIqbal/liquid-glass: iOS 26-style frosted glass surfaces for Compose Multiplatform — Modifier.liquidGlass \+ GlassCard/Button/NavBar with auto-tiered low-end-device fallback., [https://github.com/NadeemIqbal/liquid-glass](https://github.com/NadeemIqbal/liquid-glass)  
25. liquid-glass: iOS 26 frosted glass for Compose Multiplatform with a zero-alloc fallback for low-RAM Android : r/KotlinMultiplatform \- Reddit, [https://www.reddit.com/r/KotlinMultiplatform/comments/1teuck4/liquidglass\_ios\_26\_frosted\_glass\_for\_compose/](https://www.reddit.com/r/KotlinMultiplatform/comments/1teuck4/liquidglass_ios_26_frosted_glass_for_compose/)  
26. Finally xcode 26 \- DEV Community, [https://dev.to/redbugmilk/finally-xcode-26-4i3l](https://dev.to/redbugmilk/finally-xcode-26-4i3l)  
27. Building a Kotlin Multiplatform Library from Scratch — A Beginner's Guide \- Vivart Pandey, [https://vivart.medium.com/building-a-kotlin-multiplatform-library-from-scratch-a-beginners-guide-936a7b6037a7](https://vivart.medium.com/building-a-kotlin-multiplatform-library-from-scratch-a-beginners-guide-936a7b6037a7)  
28. Share more logic between iOS and Android | Kotlin Multiplatform Documentation, [https://kotlinlang.org/docs/multiplatform/multiplatform-upgrade-app.html](https://kotlinlang.org/docs/multiplatform/multiplatform-upgrade-app.html)  
29. Testing Compose Multiplatform UI \- Kotlin, [https://kotlinlang.org/docs/multiplatform/compose-test.html](https://kotlinlang.org/docs/multiplatform/compose-test.html)  
30. Every Commit on the Clock: CI/CD for Kotlin Multiplatform with GitHub Actions | KMP Bits, [https://www.kmpbits.com/posts/drafts/kmp-github-actions](https://www.kmpbits.com/posts/drafts/kmp-github-actions)  
31. Best Cross Platform Testing Tools in 2026: Web, Mobile, and Cloud Compared \- Drizz, [https://www.drizz.dev/post/best-cross-platform-testing-tools](https://www.drizz.dev/post/best-cross-platform-testing-tools)  
32. Best Mobile Test Automation Frameworks (2026): When to Use Each \- Drizz, [https://www.drizz.dev/post/best-mobile-test-automation-frameworks-2026-when-to-choose-drizz](https://www.drizz.dev/post/best-mobile-test-automation-frameworks-2026-when-to-choose-drizz)  
33. 12 best mobile testing tools for Android and iOS \- Adapty, [https://adapty.io/blog/mobile-testing-tools/](https://adapty.io/blog/mobile-testing-tools/)  
34. Latest updates \- Android Developers, [https://developer.android.com/latest-updates](https://developer.android.com/latest-updates)  
35. Set up the Android Gradle library plugin for KMP | Kotlin, [https://developer.android.com/kotlin/multiplatform/plugin](https://developer.android.com/kotlin/multiplatform/plugin)  
36. Android Update Distribution Figures 2026: Who's Still Behind? \- Nokia Power User, [https://nokiapoweruser.com/android-update-distribution-figures-2026-what-the-numbers-reveal/](https://nokiapoweruser.com/android-update-distribution-figures-2026-what-the-numbers-reveal/)  
37. Google Play Store App Publishing Requirements (2026 Guide), [https://www.ridvanbilgin.com/2026/06/google-play-store-app-publishing.html?m=1](https://www.ridvanbilgin.com/2026/06/google-play-store-app-publishing.html?m=1)  
38. Android 17 features and changes list, [https://developer.android.com/about/versions/17/summary](https://developer.android.com/about/versions/17/summary)  
39. Google Play Target API Level 2026: SDK 35 Requirements Explained \- Intuition Softech, [https://intuitionsoftech.com.au/google-play-target-api-level-requirement-2026-new-apps-updates-target-sdk-35-explained/](https://intuitionsoftech.com.au/google-play-target-api-level-requirement-2026-new-apps-updates-target-sdk-35-explained/)  
40. [https://foresightmobile.com/blog/complete-guide-to-android-app-publishing-in-2026\#:\~:text=For%202026%2C%20the%20rule%20is,15%20(API%20level%2035).](https://foresightmobile.com/blog/complete-guide-to-android-app-publishing-in-2026#:~:text=For%202026%2C%20the%20rule%20is,15%20\(API%20level%2035\).)  
41. Behavior changes: Apps targeting Android 17 or higher, [https://developer.android.com/about/versions/17/behavior-changes-17](https://developer.android.com/about/versions/17/behavior-changes-17)  
42. Behavior changes: all apps \- Android Developers, [https://developer.android.com/about/versions/17/behavior-changes-all](https://developer.android.com/about/versions/17/behavior-changes-all)  
43. What's new in Android 17? Anti-theft tools, scam detection, and parental controls \- Help Net Security, [https://www.helpnetsecurity.com/2026/06/17/android-17-security-and-privacy-features/](https://www.helpnetsecurity.com/2026/06/17/android-17-security-and-privacy-features/)  
44. Updating multiplatform projects with Android apps to use AGP 9 \- Kotlin, [https://kotlinlang.org/docs/multiplatform/multiplatform-project-agp-9-migration.html](https://kotlinlang.org/docs/multiplatform/multiplatform-project-agp-9-migration.html)  
45. Android Gradle plugin 9.0.1 (January 2026), [https://developer.android.com/build/releases/agp-9-0-0-release-notes](https://developer.android.com/build/releases/agp-9-0-0-release-notes)  
46. Migrate to built-in Kotlin | Android Studio, [https://developer.android.com/build/migrate-to-built-in-kotlin](https://developer.android.com/build/migrate-to-built-in-kotlin)  
47. Version Compatibility Matrix for KMP AGP 9.0 Migration \- GitHub, [https://github.com/kotlin/kotlin-agent-skills/blob/main/skills/kotlin-tooling-agp9-migration/references/VERSION-MATRIX.md](https://github.com/kotlin/kotlin-agent-skills/blob/main/skills/kotlin-tooling-agp9-migration/references/VERSION-MATRIX.md)  
48. Configure a Gradle project | Kotlin Documentation, [https://kotlinlang.org/docs/gradle-configure-project.html](https://kotlinlang.org/docs/gradle-configure-project.html)  
49. Create a multiplatform app using Ktor and SQLDelight \- Kotlin, [https://kotlinlang.org/docs/multiplatform/multiplatform-ktor-sqldelight.html](https://kotlinlang.org/docs/multiplatform/multiplatform-ktor-sqldelight.html)  
50. tunjid/compose-multiplatform-ios-bug-03-26 \- GitHub, [https://github.com/tunjid/compose-multiplatform-ios-bug-03-26](https://github.com/tunjid/compose-multiplatform-ios-bug-03-26)  
51. Set up Room Database for KMP | Kotlin \- Android Developers, [https://developer.android.com/kotlin/multiplatform/room](https://developer.android.com/kotlin/multiplatform/room)  
52. GitHub \- brewkits/Grant: Kotlin Multiplatform permission library for Android & iOS. No Fragment/Activity needed, ViewModel-first, Compose Multiplatform ready. Fixes Android dead clicks & iOS deadlocks., [https://github.com/brewkits/grant](https://github.com/brewkits/grant)  
53. Room Database for Kotlin Multiplatform: 7 Powerful Async Facts \- Progressive Robot, [https://www.progressiverobot.com/2026/04/26/room-database-kmp/](https://www.progressiverobot.com/2026/04/26/room-database-kmp/)  
54. Room database setup for Kotlin Multiplatform (KMP) | ProAndroidDev, [https://proandroiddev.com/room-in-kotlin-multiplatform-kmp-with-koin-d7716bdd8783](https://proandroiddev.com/room-in-kotlin-multiplatform-kmp-with-koin-d7716bdd8783)  
55. Room 3.0 \- Modernizing the Room \- Android Developers Blog, [https://android-developers.googleblog.com/2026/03/room-30-modernizing-room.html](https://android-developers.googleblog.com/2026/03/room-30-modernizing-room.html)  
56. SQLDelight in Kotlin Multiplatform (KMP) \- FunkyMuse, [https://funkymuse.dev/posts/sql-delight-kmp/](https://funkymuse.dev/posts/sql-delight-kmp/)  
57. Zero to Hero in KMM: Real App Development with Compose and SwiftUI | by Mahdi Abbasian | Stackademic, [https://blog.stackademic.com/zero-to-hero-in-kmm-real-app-development-with-compose-and-swiftui-7157b7de8528](https://blog.stackademic.com/zero-to-hero-in-kmm-real-app-development-with-compose-and-swiftui-7157b7de8528)  
58. Grant: The “No-Nonsense” Permission Manager for Kotlin Multiplatform \- DEV Community, [https://dev.to/brewkits/grant-the-no-nonsense-permission-manager-for-kotlin-multiplatform-4n1a](https://dev.to/brewkits/grant-the-no-nonsense-permission-manager-for-kotlin-multiplatform-4n1a)  
59. Prepare your app for the resizability and orientation changes in Android 17, [https://android-developers.googleblog.com/2026/02/prepare-your-app-for-resizability-and.html](https://android-developers.googleblog.com/2026/02/prepare-your-app-for-resizability-and.html)  
60. iOS 26 \- Wikipedia, [https://en.wikipedia.org/wiki/IOS\_26](https://en.wikipedia.org/wiki/IOS_26)  
61. Configure TeamCity for a Kotlin Multiplatform application, [https://kotlinlang.org/docs/multiplatform/configure-teamcity-for-kmp.html](https://kotlinlang.org/docs/multiplatform/configure-teamcity-for-kmp.html)  
62. emanueltns/kmp-ktor-template: End to End KMP Template ( Android, iOS and server) \- GitHub, [https://github.com/emanueltns/kmp-ktor-template](https://github.com/emanueltns/kmp-ktor-template)  
63. About Android Gradle plugin | Android Studio, [https://developer.android.com/build/releases/about-agp](https://developer.android.com/build/releases/about-agp)  
64. Using Roborazzi for Android Screenshot Testing \- Thomas Kioko, [https://thomaskioko.me/posts/android\_screenshot\_testing/](https://thomaskioko.me/posts/android_screenshot_testing/)  
65. Meet Google Play's target API level requirement | Other Play guides \- Android Developers, [https://developer.android.com/google/play/requirements/target-sdk](https://developer.android.com/google/play/requirements/target-sdk)  
66. Apple cambia las reglas en Brasil: aplicaciones de iOS se podrán descargar fuera de la App Store, [https://www.infobae.com/tecno/2026/06/19/apple-cambia-las-reglas-en-brasil-aplicaciones-de-ios-se-podran-descargar-fuera-de-la-app-store/](https://www.infobae.com/tecno/2026/06/19/apple-cambia-las-reglas-en-brasil-aplicaciones-de-ios-se-podran-descargar-fuera-de-la-app-store/)  
67. APK Guide 2026: Android App Publishing, AAB & Google Play \- Foresight Mobile, [https://foresightmobile.com/blog/complete-guide-to-android-app-publishing-in-2026](https://foresightmobile.com/blog/complete-guide-to-android-app-publishing-in-2026)  
68. Announcements \- Play Console Help \- Google Help, [https://support.google.com/googleplay/android-developer/announcements/13412212?hl=en](https://support.google.com/googleplay/android-developer/announcements/13412212?hl=en)  
69. ios-26 \- Skip, [https://skip.dev/blog/tags/ios-26/](https://skip.dev/blog/tags/ios-26/)

[image1]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACsAAAAVCAYAAADfLRcdAAACDUlEQVR4Xu2VP0gdQRDGR6KFEFQMSEKEp2IjYhECCYRUkjY2SkRIl0ZrQcHGSmzEIqSQNGKfhIggFiEcsVEEIU0CokUkaiEqFloo/vm+7Oy7vXn+OS9iXsAPfrAzN3sz7M7uitzqVv9MZeABeAIemm+XqhX8BpNgFpyAA1ASBhmVgynw0n5IoTtgRVyeK8//AZ7pmAW+AcegPR9RqNeSMZkqkozzuYqD4raH4qrNgDlQ6YMCNYKfYEsyJFNFkrFYTmIb1Ae+frAHHgc+L7ZLjbiEV06miiRjsUx81/iGwAZoMH6uarO4+EjSJWOPdokrbhN8B9/U9vO7war6BsArHZNlueD8+H7MGX8n+KjjtMX2ivvXO+NfVL+dT9+4xC1JPQVHgZ1XnbiTyhWw/nnwSO20xbK3eVhfGH8k5xfLFgxVqv4CfRFXbJPxj4lbJa+0xR7K2b0fSfpiqUSxXPb3oDrw0W7RMfspZF3cD7bVHtU4K8Zca7Fs3j4wHX/7Ix6CWuPz4gv0SwqTWfGQ/m0bsL58sWxeGpYP4vrFioU+BzugR+2qRESsHFgSF+tVIXGOYUkeJr8TbWpzp7+CERq+ec/irU6wsnFkIhGR1H3wCSyAz2BN4medhC3i83KhePPsg11x19+NijvAO52J76ltd4TF+jYI44tSYbFFK16VHeKK5XPOcdHKP7ch/69OAfmilymZzgdHAAAAAElFTkSuQmCC>

[image2]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACUAAAAWCAYAAABHcFUAAAABZ0lEQVR4Xu2VMS8EURSFzwYJIVEoRKURjUJHotaIiF78ALVG529IdKIRKo1CpVAo/ADRCAkFNY2Cc3L37b6582Z2ZkIiMl/yJTv3zbw9c9/bt0BLy+/ToWu+WIMtek+n/UBdZugXvaNPdC87XJkR2DyPsDl/jCM0D7VNP/GHQs3RW/qOdCh1cdzVPJO+EGgSSh06p2MoDiXW6YMvwsIew4InaRLqmi50P5eFEgq2FF2HQIdRLUfdUHq7neh6UCihboVgIVBhl0SdULOwLsVUCSU2YT8KLflAqoYapgf0AhYg+EGf6SId7d2dRRv6jN7QFTeWpGoo7YUT2LkWq3NKvtDV3t1ZFCgsmQ7aeI8lKQv1Bjtg5/1ARNnhGTrk94/CK1yGDmwS7ZFLetr97CcOXdh1dT0/hf4/wyvs7ePlU2f1wlryFAqW6dgE+l/ojSnqlJ6/Qv7ZjeieZbqPfJdiND7kiy0t/5Jvu7RT4OIpN3EAAAAASUVORK5CYII=>

[image3]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACsAAAAVCAYAAADfLRcdAAAB9ElEQVR4Xu2WzStEURjGX2FhgygfWUzZWVko9rKwYSXyH9grShY2UrKSslGyUPIVxU66sVFKWZGPBSXFQhQb+Xie3nNmzpyZ271Gk1Hz1K97z/O+d8577/kakaKK+jOVg0bQDpq8WKRKQC3oBl9gND2cphrwBMZBH7gELWkZ0SoF16J99XixSN2IPnhurmHFVoE9kDDtCtH8yWRGfAWSY7GuwootA/Pg0/HqRfNnHS+uAsljsSyIsWU/kKMCyWOxh6KxMbADbo3H+Rcl5gyKPv8ITsGBadtih0R/0/bRb+7Jlei6ylBYsS+isVfH49TgtBhwPF/Dos/Nef6J8f0vS29RdMew6gAfTjupsGJZJGO7nk/vSHTxZdOZ6At1eX4g4cX6/fOj0M9QtmTqXjTmLyZ6/Oqtnm/1LvqibZ4fSPxiqR8Vyy0rW4xetmKswuKB5LHYCdHYgufT41dv9nwrxn47Dbi4ksXy9OLxlzDmqrmvtgmS2mfdic6cC3MNk83hqWdVKdoPmZL0xUSPI9Fr2jwx98GMTQgk9bDLkk0w4iJaAWsm9iDxjtoGsAmOwRa4k9RWSNwpwjbXxTrYAG/gWeJtkRnikIyAadDpxaLE0asT7diOpjt6FIu108DNL0i5xRasOJ34L47Fbpv7gpU9bl3+r74BYxiY04+dnKEAAAAASUVORK5CYII=>

[image4]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACUAAAAWCAYAAABHcFUAAAABeUlEQVR4Xu2VsS4FURCGR0QhJFoqiUKpkRCiFCEKBQnPobg6Uas0ItGJCm8gHkB4AdEIjUI8gEjw/5mdmDN319rdK1Hsl3zJ3Tl7d/+dc/asSEvL39MHV2KxhGm4D4/hLOxPh+sxBj/hHXyCO+lwIXyAAzjoatei17pytcacyO9DTcBnOOVqy/BDNFjPqBJqDr7BM/meMnb9UbpDDcChUIuMxIJRJRQZlnQNWfdiKLIKH2JRNOypaPBcqoaKbIkGYgfzYLAZd2yB+JIU0jTUjWioThxwsFsWzAIVdok0CbUO96TkBhlr8F3SN7eQuqEWRdcSt4kyuKAvRLeP+TCWS51QnAoG2nQ1hiyCgWzK7iVdY7n8FOpFdIOddLVReAiX4DjcEF3st+4cwzoUp5cPwHAJbDn3F170Ep5nv1nzcAHT7eyYN+HObXUv14uHbxkf+CjUDQZLOsa9Jl7U9MROLYjePP6HvmbnGPwm7kp3lzwc78l3s6Xl3/MFyFtSr4OhPsMAAAAASUVORK5CYII=>

[image5]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACUAAAAWCAYAAABHcFUAAAABzUlEQVR4Xu2VzStEURjGX6GIktgghcTGTpGvjSyUZGlBNjZWlEJZ2PkHLCglZKPYWtjJSqxJUrKSLKyUj8LzzLln5pz3OjPTxEb3V7+a+5479zxz7nvOiCQk/B3FcAiuwTlY4w8HaYL1sCi6LoV9cMreUCgl8Bl2Rdd88HZUy0Un/HK8hYOSCVkwPfBQTDhLP/yQ3A9nqAW4DhfF/KBfYRUuq1oLfIANqq5hqDpd/AGGrdBFRZX9UAsv4VJmLAUnuoejqq5hKAZvgxOw0R/2GIF3uigm7J44q2wnD4XSdQ1DncJjOA1fxfRjaFUYzPYusYE2nVpw8lBdw1Djkum9STENv5G+Iw5XywazgbxeDE0equfCtgODZWNMzEYq1wOkGl5IfPJ8e6pVzBlnqYQnkj0UG/oAnsFeNZaCxwCPg1AoHg0h2uEjnHFq+YRiIPvKbsTvsTQd8AiWObVh+Cn+OfUEr8XsNNIMr8R/BeyxFzH3auwK6bOM/yQM58GJ3+BAdM1fy5D6wfbUno+u+T0emOnzBeyIuYd1F+6yXQlvAAaLrdi+mO3Mg/RcTCD+XbjolSJcJb5CTrgF3+Gs+H1GuuGKxFfJheP6ewkJ/5Nvf8JXGr46iMoAAAAASUVORK5CYII=>

[image6]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACIAAAAWCAYAAAClrE55AAABrklEQVR4Xu2VPSiFURzGH6EoJVHIoGS0KfK1yMAgg8FqsyiDwcduNSiLlAzKQDHYxUbZLCaUDAaT8s3z3HPOvce5jvuF1PXUr+77vKf3/5xz/udc4F9FpHrSSDrCF9mqlAyQJTJFaj++zlpvHjmrjNwiNYtysma9XFVF9pFnkC6yBRPIqZc8kRLPy0YFBVkg84HXQq5JU+BnUt5B6sgpmQl8NdwFGQ78UOqtK5jCN2SEHNhnp0XyaL0xcm5/ix43yBWMBQl9X+qlFTJLKq23jfRm7SR31nsm1TBbru1/IaMaFCsY831Nw3zc7yOdthPr+9qw3lzgv8KsZLRgzHdyzayZ+or1yLr1wq12PmrIMdILZuoR+fpAoUF0SBJjdWR1dGNBNPPPNITvCZJcEamN7JGK5GtgEGb//P33pQmsIr1grEdcwfHAf4B3caqYjD77rFkpWKKJvlAzOSPdnueaUugO0smSXJBL+yy1wpyaSc/DJrmHudyOYEL0+wMiaoBZOfXZDpkgh0htW7sd54KoBTR2FyaEasRW/UcU65Ff158IoptV26Egy8j9/6sI9Q5x5XyKaEH5/gAAAABJRU5ErkJggg==>

[image7]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACUAAAAWCAYAAABHcFUAAAABdUlEQVR4Xu2VsUoDQRRFn6iFKFgLgmCRxsrGgFhaKBZ+gB9gr4Wdnb2FELARsRDUDxC/QPwCsRFsLCzEQgiIxHuZfevM25kko4lY7IED2Te7mTtvJxORmprhMwLXbbEHM3AXHsNlOBoO/wx+aQfewye4Fw53ZVFcmPHiugnfYau8YwCcSv+hxuA5XDG1K/jq1X5NTqh5+AxnTf1AXOd92MlJU7NM24KSE2pb3OR8/T58nvUpU9+Aj6ZGGPZMvrdAhZxQOnkqlK0TBlvyrjUQ92WSYYci7JYG00DJLpG/CEU24QecsAMxckJtSXzy1J5SuKEv4a24c60nOaF4FHC1qVA8HmIwkL6yBwn3WJRuoV7EHbCN4pqn/xFcLe9wr+Na4ueUdsjuHz7PcAH8cq52Dt7Ai+Kz7QBXT3e82oK4+3WiNfgJD8s7HPyVccEtU1cYLOgY371OaPWxnVLe4J24PdaGJ1I9KJtwX6pd8uH4QP43a2r+PV8SPlASI06/rQAAAABJRU5ErkJggg==>

[image8]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEIAAAAZCAYAAACFHfjcAAABx0lEQVR4Xu2VzytEURTHj1AK5UcJkZJslLKyYWdjYadspLek7JTyT4iFsrFgRfIvaPwoZUfKxoZkobC2EN/v3Du6zrx3571yZ3U/9Wne/THnnTlz7nsikUgkUpxueKknUxiGs3qyzgTLdRp+wm+9oFgUs2dOL9SRoLmewlfxBx+B9/BNCgb/Z4Ll2gC34IH4gx/BdViSfMEZt11POnC9EzbqBQ+hci2ftXN7vS/ZwVnhMdgmBYKDATEJ8Qe4cHwDh9S8j2C5MpkNMRUmWcGb4Z69zh3c4Vr+FoOfiRQrQtBcJ+EF7LHjrOAL8MRe5w7uwK5wi5GI6YYiBMuVCe3AJmcuLfguXHPGuYJnMAgf4aheqEHQXCfgspj27LMeiwnOa55HJvAA58X8q5zn+buCS3bcIflgrFV4K6bFizwgg+Y6A58cn+GXmOAcH8JWtYe+2D3vdrwptakUgcehF56JOSZ5qWeuv22k203DyrK9ve3mwCKkvR1YkLS3SR5C5SotcBzeSXW7uXB+Cn7AFTtObTcLv59IdREqVDpD38dHqFzLsGIM6loSU3kXvYfyoZUFb7ytJx26xKz36wUPoXKNRCKRSCQSKcQP7Cecp0shKyAAAAAASUVORK5CYII=>