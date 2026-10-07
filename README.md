# alpine-components

Public DolomiteByte components for Web, Android, and iOS. The Android package currently contains primary and secondary buttons for Jetpack Compose. Web components and iOS components will be added as separate installable packages.

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
    implementation("com.github.DolomiteByte:alpine-components:0.3.0")
}
```

Use it in Compose:

```kotlin
import com.dolomitebyte.alpine.components.AlpinePrimaryButton
import com.dolomitebyte.alpine.components.AlpineSecondaryButton

AlpinePrimaryButton(
    text = "Kontakt aufnehmen",
    onClick = { /* handle primary action */ },
)

AlpineSecondaryButton(
    text = "Mehr erfahren",
    onClick = { /* handle secondary action */ },
)
```

For an app-controlled theme, pass its current mode:

```kotlin
AlpineSecondaryButton(
    text = "Weiter",
    onClick = { /* continue */ },
    darkTheme = appDarkTheme,
    enabled = formIsValid,
)
```

Both buttons provide click and disabled semantics for accessibility. Their light and dark previews, plus disabled previews, live alongside the components in `android/src/main`.

### Button states and icons

Both buttons accept `enabled`, `loading`, `leadingIcon`, and `trailingIcon`. Icons are optional Compose slots. Each slot receives the current content color, so it follows the button theme and hover state. Use a 20 dp icon inside the slot:

The example below uses Material 3 `Icon` and the default Material icons. Add `androidx.compose.material3:material3` and `androidx.compose.material:material-icons-core` to your app if you use these imports. Any Compose icon can be used instead.

```kotlin
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

AlpinePrimaryButton(
    text = "Speichern",
    onClick = ::save,
    enabled = formIsValid,
    loading = saving,
    leadingIcon = { tint ->
        Icon(Icons.Default.Add, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
    },
    trailingIcon = { tint ->
        Icon(Icons.Default.Check, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
    },
)

AlpineSecondaryButton(
    text = "Mehr erfahren",
    onClick = ::showDetails,
    leadingIcon = { tint ->
        Icon(Icons.Default.Add, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
    },
)
```

The primary button shows its Alpine Fold arrow on the right by default. `trailingIcon` replaces that arrow; `showDefaultArrow = false` removes it. The secondary button has no default icon. `enabled = false` dims either button to 50% opacity and blocks clicks. `loading = true` keeps the label, hides the leading icon, replaces right-side content with a spinner, and blocks clicks. Loading is announced to accessibility services in English or German according to the device locale. Icons alongside a text label are decorative; pass `contentDescription = null` to Compose `Icon`.

## Development

Build and publish the Android AAR to Maven Local:

```sh
./gradlew :android:assembleRelease :android:publishReleasePublicationToMavenLocal
```

Run the UI tests on an Android emulator:

```sh
./gradlew :android:connectedDebugAndroidTest
```

The cross-platform design details are in [design/primary-button.md](design/primary-button.md) and [design/secondary-button.md](design/secondary-button.md). The bundled static Manrope Bold and SemiBold fonts are derived from [Google Fonts Manrope](https://github.com/google/fonts/tree/main/ofl/manrope) and retain their [SIL Open Font License](licenses/OFL-Manrope.txt).

## License

Code is licensed under [Apache-2.0](LICENSE). The Manrope font is licensed under SIL Open Font License 1.1.
