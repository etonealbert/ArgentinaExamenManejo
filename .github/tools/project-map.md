# Project Map

Expected current repository root:

```text
ExamenManejo/
  androidApp/
  iosApp/
  shared/
  docs/
  gradle/
  .github/
```

Preferred shared package map:

```text
shared/src/commonMain/kotlin/<base-package>/
  App.kt
  core/
    di/
    navigation/
    design/
    platform/
    util/
  feature/
    onboarding/
    home/
    study/
    exam/
    result/
    review/
    history/
    settings/
    subscription/
  domain/
    model/
    repository/
    usecase/
  data/
    local/
    repository/
    mapper/
  network/
```

Preferred SQLDelight location depends on Gradle config, commonly:

```text
shared/src/commonMain/sqldelight/<package>/ExamenManejoDatabase.sq
```

Preferred tests:

```text
shared/src/commonTest/kotlin/<base-package>/
```

Platform-specific code:

```text
shared/src/androidMain/kotlin/...   # Android database driver, platform actuals
shared/src/iosMain/kotlin/...       # iOS database driver, platform actuals
```

Do not force this layout if the actual Gradle project uses different source-set names. Inspect first.
