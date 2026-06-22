---
applyTo: "**/*.{kts,gradle,plist,swift,kt}"
---

# Platform Target Instructions

## Target intent

The app is intended for:

- iOS 26+;
- latest two stable Android major versions.

Before changing SDK values, verify current official platform versions and store requirements. Do not hardcode outdated assumptions.

## Android guidance

When editing Android Gradle settings:

- keep `compileSdk` and `targetSdk` aligned with current stable/store requirements;
- decide `minSdk` intentionally because latest-two-only can severely reduce user reach;
- document any change in an ADR or implementation note;
- test on at least one latest Android emulator and one previous-major emulator.

## iOS guidance

When editing iOS settings:

- confirm Xcode/iOS SDK compatibility;
- use iOS simulator and real-device testing where possible;
- verify Kotlin/Native and Compose Multiplatform compatibility;
- handle safe areas and system gestures.

## KMP source sets

Put platform-specific code behind expected abstractions:

```text
commonMain: interfaces / expect declarations
androidMain: Android actuals
iosMain: iOS actuals
```

Examples:

- database driver factory;
- platform logging;
- file/resource access;
- store billing later.
