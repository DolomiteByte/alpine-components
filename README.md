# alpine-components

Public DolomiteByte components for Web, Android, and iOS. The Android package currently contains primary and secondary buttons, floating action buttons, a premium card, alert dialogs, snackbars, text, email, number, and password inputs, a message area, dropdowns, radio and checkbox cards, bottom tabs, and toggles for Jetpack Compose. Web components and iOS components will be added as separate installable packages.

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
    implementation("com.github.DolomiteByte:alpine-components:0.13.3")
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

Clickable Alpine actions use a bounded ripple with a color matched to the component: primary and floating buttons, secondary buttons, bottom tabs, the alert-dialog close icon, and the password visibility icon. Disabled and loading actions do not show a ripple. The dialog close icon remains transparent until pressed.

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

### Floating action buttons

`AlpineFab` accepts an icon, text, or both. Icon-only actions require a `contentDescription`; when text is present, it names the button by default. The compact icon button is a 56 dp circle; text variants are extended pills. All variants use the Alpine blue gradient and support `enabled`, `loading`, and light/dark styling.

```kotlin
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.dolomitebyte.alpine.components.AlpineFab
import com.dolomitebyte.alpine.components.AlpineFabOverlay

AlpineFab(
    onClick = ::createItem,
    icon = { tint -> Icon(Icons.Default.Add, contentDescription = null, tint = tint) },
    contentDescription = "Element hinzufügen",
)
AlpineFab(onClick = ::createItem, text = "Erstellen")
AlpineFab(
    onClick = ::createItem,
    text = "Erstellen",
    icon = { tint -> Icon(Icons.Default.Add, contentDescription = null, tint = tint) },
)

AlpineFabOverlay(
    fab = {
        AlpineFab(
            onClick = ::createItem,
            icon = { tint -> Icon(Icons.Default.Add, contentDescription = null, tint = tint) },
            contentDescription = "Element hinzufügen",
        )
    },
    alignment = Alignment.BottomEnd, // or Alignment.BottomStart
) {
    Box(Modifier.fillMaxSize()) { /* screen content */ }
}
```

`AlpineFabOverlay` floats the button over its content. `Alignment.BottomEnd` and `Alignment.BottomStart` place it in the bottom corners; any other `Alignment` plus `offset = DpOffset(x, y)` gives a custom position. The default 16 dp edge padding and safe drawing insets keep the FAB clear of system bars. Pass `windowInsets = WindowInsets(0, 0, 0, 0)` if the parent already handles them. Increase `edgePadding` or use a negative vertical offset to place it above bottom tabs. The content itself is not inset by the overlay.

### Premium card

`AlpinePremiumCard` follows TheraBuddy's compact premium drawer card with an Alpine blue-to-cyan gradient, white text, solid white circles with transparent star and benefit check cutouts, and a white action area. The background moves from `#0A5BE3` through `#0054FF` to `#33D5EB` and drifts gently over a 40-second round trip. Pass `animateGradient = false` for a static background. The full card is one accessible button with a bounded ripple; the action area is its visual call to action. It accepts custom copy, benefits, and an optional icon slot. A custom icon keeps its Alpine cyan tint on a translucent circle. The card has no border; its shadow adapts to light and dark mode and glows cyan on focus.

```kotlin
import com.dolomitebyte.alpine.components.AlpinePremiumCard

AlpinePremiumCard(
    eyebrow = "7 Tage kostenlos",
    title = "Premium freischalten",
    description = "Ohne Werbung und mit unbegrenzten Dokumentfunktionen.",
    benefits = listOf("Ruhige Nutzung ohne Werbung", "Unbegrenzte Dokumentfunktionen"),
    actionText = "Premium ansehen",
    onClick = ::openPremium,
    darkTheme = appDarkTheme,
)
```

Set `enabled = false` to block interaction. The default star can be replaced with `icon = { tint -> ... }`; decorative icons inside that slot should have no content description.

### Alert dialog

`AlpineAlertDialog` shows a modal Alpine panel with a title, a 48 dp icon-only close target without a background or border, a scrollable Compose content area, and primary and optional secondary actions. The actions reuse `AlpinePrimaryButton` and `AlpineSecondaryButton`. They sit beside each other when space allows and stack on narrow screens. Long content scrolls while the header and actions remain visible.

```kotlin
import com.dolomitebyte.alpine.components.AlpineAlertDialog
import com.dolomitebyte.alpine.components.AlpineAlertDialogText

if (showDialog) {
    AlpineAlertDialog(
        title = "Änderungen speichern?",
        primaryActionText = "Speichern",
        onPrimaryAction = {
            saveChanges()
            showDialog = false
        },
        secondaryActionText = "Abbrechen",
        onSecondaryAction = { showDialog = false },
        onDismissRequest = { showDialog = false },
    ) {
        AlpineAlertDialogText("Deine Änderungen werden sofort sichtbar.")
        // Add other Compose content here when needed.
    }
}
```

The caller owns dialog visibility. The close button, outside tap, and Back call `onDismissRequest`; action callbacks run independently. Omit both secondary parameters for a single-action dialog. `primaryActionEnabled`, `primaryActionLoading`, `secondaryActionEnabled`, and `secondaryActionLoading` control the action states. Pass `darkTheme` for an app-controlled theme; `AlpineAlertDialogText` follows it automatically inside the dialog. `AlpineAlertDialogContainer` provides the same panel without the modal wrapper for custom overlays or previews.

### Snackbars

`AlpineSnackbar` stays only as wide as its content, up to 560 dp. Default, info, and warning variants support an optional icon, a main action, a secondary action, or no actions. The main action is filled; the secondary action is text-only. Info and warning have a built-in tinted icon without a background or border; use `showIcon = false` to hide it. A custom `icon` slot works with any variant. Long messages wrap, and action buttons move below the text on narrow screens.

```kotlin
import com.dolomitebyte.alpine.components.AlpineSnackbar
import com.dolomitebyte.alpine.components.AlpineSnackbarVariant

AlpineSnackbar("Änderungen gespeichert")
AlpineSnackbar(
    message = "Neue Funktionen sind verfügbar.",
    variant = AlpineSnackbarVariant.Info,
    actionText = "Ansehen",
    onAction = ::openUpdates,
)
AlpineSnackbar(
    message = "Die Verbindung ist unterbrochen. Bitte versuche es erneut.",
    variant = AlpineSnackbarVariant.Warning,
    actionText = "Erneut",
    onAction = ::retry,
    secondaryActionText = "Später",
    onSecondaryAction = ::remindLater,
)
```

Use `AlpineSnackbarOverlay` to place a transient message above screen content. `Alignment.BottomCenter` is the default; `Alignment.BottomEnd` and `Alignment.BottomStart` place it in the corners. Any other alignment with a `DpOffset` supports a custom position. The default 16 dp edge padding and safe drawing insets keep it clear of screen edges and system bars. Pass `durationMillis = null` to keep it visible until you hide it yourself.

```kotlin
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.dolomitebyte.alpine.components.AlpineSnackbarOverlay

AlpineSnackbarOverlay(
    visible = showSnackbar,
    message = "Änderungen gespeichert",
    onDismissRequest = { showSnackbar = false },
    alignment = Alignment.BottomEnd,
    offset = DpOffset((-8).dp, (-8).dp),
) {
    ScreenContent()
}
```

The caller owns visibility. The overlay requests dismissal after four seconds by default; action callbacks run independently, so set `showSnackbar = false` in an action callback when appropriate. Snackbar actions have bounded ripple feedback. Pass `darkTheme` when the app controls its own theme.

### Dropdown and selection cards

`AlpineDropdownField` uses the outlined, floating-label form style from the reference. On Android, tapping the field opens a Material 3 modal bottom sheet with the available options. The host owns the selected ID; tapping an option calls `onSelected` and closes the sheet. Back, a downward swipe, or tapping outside dismisses it without changing the value. Empty lists, disabled options, a disabled field, and error messages are supported.

`AlpineRadioCard` represents one option in a single-choice group. `AlpineCheckboxCard` is the matching multi-choice variant. Both cards have an optional supporting line, light and dark styles, enabled and selected states, accessible selection semantics, and ripple feedback. Selected cards use a borderless luminous Alpine gradient with a restrained shadow. Selected radio circles and checked boxes match the input accent: blue in light mode and cyan in dark mode. `AlpineSelectionGroup` adds the heading and spacing shown in the design.

```kotlin
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.dolomitebyte.alpine.components.AlpineCheckboxCard
import com.dolomitebyte.alpine.components.AlpineDropdownField
import com.dolomitebyte.alpine.components.AlpineDropdownOption
import com.dolomitebyte.alpine.components.AlpineRadioCard
import com.dolomitebyte.alpine.components.AlpineSelectionGroup

var unit by rememberSaveable { mutableStateOf("tablets") }
var doseType by rememberSaveable { mutableStateOf("regular") }
var reminders by rememberSaveable { mutableStateOf(false) }

AlpineDropdownField(
    label = "Unit",
    options = listOf(
        AlpineDropdownOption("tablets", "Tablet(s)"),
        AlpineDropdownOption("drops", "Drop(s)"),
    ),
    selectedId = unit,
    onSelected = { unit = it },
)
AlpineSelectionGroup("Dose type") {
    AlpineRadioCard(
        selected = doseType == "regular",
        onClick = { doseType = "regular" },
        title = "Regular",
        supportingText = "For fixed times, days of the week, or intervals",
    )
    AlpineRadioCard(
        selected = doseType == "as_needed",
        onClick = { doseType = "as_needed" },
        title = "As needed",
        supportingText = "No dose is required; actual doses are still recorded",
    )
}
AlpineCheckboxCard(
    checked = reminders,
    onCheckedChange = { reminders = it },
    title = "Reminders",
    supportingText = "Notify me when a dose is due",
)
```

Pass `darkTheme` when an app controls its own theme. Option IDs must be unique, and selected IDs should match one of them. Cards and the dropdown use controlled state so the caller can validate and persist form choices.

### Inputs

The DolomiteByte contact form has single-line text fields for name and subject, an email field, and a five-line message area. `AlpineTextField`, `AlpineEmailField`, and `AlpineTextArea` reproduce their underlined Manrope style in light and dark mode. `AlpineNumberField` and `AlpinePasswordField` extend that design for native forms. Labels and underlines take the Alpine accent color on focus.

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

The new variants use the same controlled API:

```kotlin
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dolomitebyte.alpine.components.AlpineNumberField
import com.dolomitebyte.alpine.components.AlpinePasswordField
import com.dolomitebyte.alpine.components.AlpineTextField

var amount by rememberSaveable { mutableStateOf("") }
var password by rememberSaveable { mutableStateOf("") }

AlpineNumberField(
    value = amount,
    onValueChange = { amount = it },
    label = "Betrag",
    placeholder = "0,00",
    allowDecimal = true,
    allowNegative = true,
)
AlpinePasswordField(
    value = password,
    onValueChange = { password = it },
    label = "Passwort",
    placeholder = "Passwort eingeben",
)
AlpineTextField(
    value = "Lukas",
    onValueChange = {},
    label = "Name",
    leadingIcon = { tint -> Icon(Icons.Default.Person, null, tint = tint, modifier = Modifier.size(20.dp)) },
    trailingIcon = { tint -> Icon(Icons.Default.Check, null, tint = tint, modifier = Modifier.size(20.dp)) },
)
```

All inputs are controlled: pass their current `value` and update it in `onValueChange`. Use `enabled = false` for a disabled field and `readOnly = true` for a non-editable value. The host app decides when a field is invalid and passes `isError = true`; `errorMessage` is shown below the underline and announced to accessibility services. An error without a message receives a localized generic announcement. Every input accepts decorative `leadingIcon` and `trailingIcon` slots that receive the current tint. The password eye remains on the right even when a trailing icon is supplied; it toggles between stars and clear text, with localized accessibility labels. The number field filters edits to digits and optionally one `.` or `,` separator and a leading minus sign, while keeping the value as a `String` so partially entered numbers remain editable. The email field requests an email keyboard, the text field accepts custom `keyboardOptions`, and the message area accepts `minLines`.

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

Place the bar in a `Scaffold`'s `bottomBar`. It applies the Android navigation bar inset by default. Set `darkTheme` if your app theme differs from the device setting, or pass `windowInsets` if a parent handles that inset. Pass `containerColor = MaterialTheme.colorScheme.background` when the bar should blend into your app's canvas; leaving it unset keeps Alpine's default surface. Each tab has selection and disabled semantics; the icon is decorative and the label names the destination. The example icons use the same Material 3 and Material icons dependencies noted above.

### Toggles

`AlpineToggle` is a controlled settings switch with a label and optional supporting text. `AlpineThemeToggle` follows the DolomiteByte header switch: the sun sits on the right in light mode, and the moon moves left in dark mode. Both use the site's 56 × 32 dp track, 24 dp thumb, and 200 ms transition. The whole row has a 48 dp minimum touch height, switch semantics, localized state descriptions, and a disabled state.

```kotlin
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.dolomitebyte.alpine.components.AlpineThemeToggle
import com.dolomitebyte.alpine.components.AlpineToggle

var notifications by rememberSaveable { mutableStateOf(true) }
var darkMode by rememberSaveable { mutableStateOf(false) }

AlpineToggle(
    checked = notifications,
    onCheckedChange = { notifications = it },
    label = "Benachrichtigungen",
    supportingText = "Wichtige Updates erhalten",
    darkTheme = darkMode,
)
AlpineThemeToggle(
    darkMode = darkMode,
    onDarkModeChange = { darkMode = it },
    showModeLabel = true,
)
```

The host app applies `darkMode` to its theme and passes it to the other Alpine components. The toggle only reports the requested change. `enabled = false` dims the control and blocks input. The generic switch places its thumb on the right when checked; the theme switch follows the site's dark-left/light-right convention.

## Development

Build and publish the Android AAR to Maven Local:

```sh
./gradlew :android:assembleRelease :android:publishReleasePublicationToMavenLocal
```

Run the UI tests on an Android emulator:

```sh
./gradlew :android:connectedDebugAndroidTest
```

The cross-platform design details are in [design/primary-button.md](design/primary-button.md), [design/secondary-button.md](design/secondary-button.md), [design/fabs.md](design/fabs.md), [design/alert-dialog.md](design/alert-dialog.md), [design/ripple.md](design/ripple.md), [design/inputs.md](design/inputs.md), [design/choices.md](design/choices.md), [design/bottom-tabs.md](design/bottom-tabs.md), and [design/toggles.md](design/toggles.md). The bundled Manrope fonts are derived from [Google Fonts Manrope](https://github.com/google/fonts/tree/main/ofl/manrope) and retain their [SIL Open Font License](licenses/OFL-Manrope.txt).

## License

Code is licensed under [Apache-2.0](LICENSE). The Manrope font is licensed under SIL Open Font License 1.1.
