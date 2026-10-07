# alpine-components

Public DolomiteByte components for Web, Android, and iOS. The first released component is the **Android primary button** for Jetpack Compose. Web components and iOS components will be added as separate installable packages.

## Android

The Android module requires API 23 or later. It follows the device light or dark setting by default and accepts an explicit `darkTheme` value for apps with their own theme switch.

Add [JitPack](https://docs.jitpack.io/android/) to the dependency repositories in `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the versioned Android module to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.DolomiteByte.alpine-components:android:0.1.0")
}
```

Use it in Compose:

```kotlin
import com.dolomitebyte.alpine.components.AlpinePrimaryButton

AlpinePrimaryButton(
    text = "Kontakt aufnehmen",
    onClick = { /* handle primary action */ },
)
```

For an app-controlled theme, pass its current mode:

```kotlin
AlpinePrimaryButton(
    text = "Weiter",
    onClick = { /* continue */ },
    darkTheme = appDarkTheme,
    enabled = formIsValid,
)
```

The button provides click and disabled semantics for accessibility. Its light and dark previews, plus a disabled preview, live alongside the component in `android/src/main`.

## Development

Build and publish the Android AAR to Maven Local:

```sh
./gradlew :android:assembleRelease :android:publishReleasePublicationToMavenLocal
```

Run the UI tests on an Android emulator:

```sh
./gradlew :android:connectedDebugAndroidTest
```

The cross-platform design details are in [design/primary-button.md](design/primary-button.md). The bundled static Manrope Bold font is derived from [Google Fonts Manrope](https://github.com/google/fonts/tree/main/ofl/manrope) and retains its [SIL Open Font License](licenses/OFL-Manrope.txt).

## License

Code is licensed under [Apache-2.0](LICENSE). The Manrope font is licensed under SIL Open Font License 1.1.
