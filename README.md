# alpine-components

Public DolomiteByte components for Web, Android, and iOS. The Android package currently contains primary and secondary buttons, text and email inputs, a message area, and bottom tabs for Jetpack Compose. Web components and iOS components will be added as separate installable packages.

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
    implementation("com.github.DolomiteByte:alpine-components:0.6.0")
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

### Inputs

The DolomiteByte contact form has single-line text fields for name and subject, an email field, and a five-line message area. `AlpineTextField`, `AlpineEmailField`, and `AlpineTextArea` reproduce their underlined Manrope style in light and dark mode. Labels and underlines take the Alpine accent color on focus. The email field requests an email keyboard; the message area accepts multiple lines.

```kotlin
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.dolomitebyte.alpine.components.AlpineEmailField
import com.dolomitebyte.alpine.components.AlpineTextArea
import com.dolomitebyte.alpine.components.AlpineTextField

var name by rememberSaveable { mutableStateOf("") }
var email by rememberSaveable { mutableStateOf("") }
var subject by rememberSaveable { mutableStateOf("") }
var message by rememberSaveable { mutableStateOf("") }

Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
    AlpineTextField(name, { name = it }, label = "Name", placeholder = "Dein Name")
    AlpineEmailField(
        email,
        { email = it },
        label = "Email",
        placeholder = "name@beispiel.de",
        isError = email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(),
        errorMessage = "Bitte eine gültige E-Mail-Adresse eingeben",
    )
    AlpineTextField(subject, { subject = it }, label = "Betreff", placeholder = "Worum geht es?")
    AlpineTextArea(message, { message = it }, label = "Nachricht", placeholder = "Erzähl uns von deinem Vorhaben ...")
}
```

All inputs are controlled: pass their current `value` and update it in `onValueChange`. Use `enabled = false` for a disabled field and `readOnly = true` for a non-editable value. The host app decides when a required field is invalid and passes `isError = true`; `errorMessage` is shown below the underline and announced to accessibility services. An error without a message receives a localized generic announcement. The text field accepts custom `keyboardOptions` and `keyboardActions`, the email field exposes `imeAction`, and the message area accepts `minLines`.

### Bottom tabs

`AlpineBottomTabs` follows TheraBuddy's expanding tab interaction: the selected tab grows into an Alpine blue pill with a white icon and Manrope label, while inactive tabs show only icons. The bar adapts to light and dark mode. Supply the selected index and handle navigation in `onTabSelected`. Three to five destinations are recommended for compact screens.

```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import com.dolomitebyte.alpine.components.AlpineBottomTabItem
import com.dolomitebyte.alpine.components.AlpineBottomTabs

var selectedTab by rememberSaveable { mutableIntStateOf(0) }
val tabs = listOf(
    AlpineBottomTabItem("Start", { tint ->
        Icon(Icons.Default.Home, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
    }),
    AlpineBottomTabItem("Suche", { tint ->
        Icon(Icons.Default.Search, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
    }),
    AlpineBottomTabItem("Profil", { tint ->
        Icon(Icons.Default.Person, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
    }),
)

AlpineBottomTabs(
    items = tabs,
    selectedIndex = selectedTab,
    onTabSelected = { index ->
        selectedTab = index
        // Navigate to your destination for index.
    },
)
```

Place the bar in a `Scaffold`'s `bottomBar`. It applies the Android navigation bar inset by default. Set `darkTheme` if your app theme differs from the device setting, or pass `windowInsets` if a parent handles that inset. Each tab has selection and disabled semantics; the icon is decorative and the label names the destination. The example icons use the same Material 3 and Material icons dependencies noted above.

## Development

Build and publish the Android AAR to Maven Local:

```sh
./gradlew :android:assembleRelease :android:publishReleasePublicationToMavenLocal
```

Run the UI tests on an Android emulator:

```sh
./gradlew :android:connectedDebugAndroidTest
```

The cross-platform design details are in [design/primary-button.md](design/primary-button.md), [design/secondary-button.md](design/secondary-button.md), [design/inputs.md](design/inputs.md), and [design/bottom-tabs.md](design/bottom-tabs.md). The bundled Manrope fonts are derived from [Google Fonts Manrope](https://github.com/google/fonts/tree/main/ofl/manrope) and retain their [SIL Open Font License](licenses/OFL-Manrope.txt).

## License

Code is licensed under [Apache-2.0](LICENSE). The Manrope font is licensed under SIL Open Font License 1.1.
