package com.dolomitebyte.alpine.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlpineInputsTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun textFieldAcceptsInputAndKeepsLabelAccessible() {
        val value = mutableStateOf("")
        composeRule.setContent {
            AlpineTextField(
                value = value.value,
                onValueChange = { value.value = it },
                label = "Name",
                placeholder = "Dein Name",
            )
        }

        composeRule.onNodeWithContentDescription("Name")
            .assertIsEnabled()
            .performClick()
        composeRule.onNodeWithContentDescription("Name").assertIsFocused()
        composeRule.onNodeWithContentDescription("Name").performTextInput("Lukas")
        assertEquals("Lukas", value.value)
    }

    @Test
    fun emailFieldAcceptsEmail() {
        val value = mutableStateOf("")
        composeRule.setContent {
            AlpineEmailField(
                value = value.value,
                onValueChange = { value.value = it },
                label = "Email",
                darkTheme = true,
            )
        }

        composeRule.onNodeWithContentDescription("Email").performTextInput("name@beispiel.de")
        assertEquals("name@beispiel.de", value.value)
    }

    @Test
    fun textAreaAcceptsMultipleLinesAndHasMessageHeight() {
        val value = mutableStateOf("")
        composeRule.setContent {
            AlpineTextArea(
                value = value.value,
                onValueChange = { value.value = it },
                label = "Nachricht",
            )
        }

        val bounds = composeRule.onNodeWithContentDescription("Nachricht").getUnclippedBoundsInRoot()
        assertTrue("Message area should be at least 136 dp tall", bounds.bottom - bounds.top >= 136.dp)
        composeRule.onNodeWithContentDescription("Nachricht").performTextInput("Erste Zeile\nZweite Zeile")
        assertEquals("Erste Zeile\nZweite Zeile", value.value)
    }

    @Test
    fun errorAndDisabledStatesAreAccessible() {
        var disabledValue = ""
        composeRule.setContent {
            Column {
                AlpineTextField(
                    value = "",
                    onValueChange = {},
                    label = "Betreff",
                    isError = true,
                    errorMessage = "Pflichtfeld",
                )
                AlpineTextField(
                    value = disabledValue,
                    onValueChange = { disabledValue = it },
                    label = "Deaktiviert",
                    enabled = false,
                )
            }
        }

        composeRule.onNodeWithContentDescription("Betreff")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Pflichtfeld"))
        composeRule.onNodeWithContentDescription("Deaktiviert")
            .assertIsNotEnabled()
        assertEquals("", disabledValue)
    }

    @Test
    fun decorativeIconsDoNotReplaceTheAccessibleLabel() {
        val value = mutableStateOf("")
        composeRule.setContent {
            AlpineTextField(
                value = value.value,
                onValueChange = { value.value = it },
                label = "Name",
                leadingIcon = { BasicText("L") },
                trailingIcon = { BasicText("R") },
            )
        }

        composeRule.onNodeWithText("L").assertDoesNotExist()
        composeRule.onNodeWithText("R").assertDoesNotExist()
        composeRule.onNodeWithContentDescription("Name").performTextInput("Lukas")
        assertEquals("Lukas", value.value)
    }

    @Test
    fun numberFieldAcceptsSignedDecimalsAndRejectsOtherCharacters() {
        val value = mutableStateOf("")
        composeRule.setContent {
            AlpineNumberField(
                value = value.value,
                onValueChange = { value.value = it },
                label = "Betrag",
                allowDecimal = true,
                allowNegative = true,
                isError = true,
                errorMessage = "Ungültiger Betrag",
            )
        }

        composeRule.onNodeWithContentDescription("Betrag")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Ungültiger Betrag"))
            .performTextInput("-12,5")
        assertEquals("-12,5", value.value)
        composeRule.onNodeWithContentDescription("Betrag").performTextInput("a")
        assertEquals("-12,5", value.value)
        composeRule.onNodeWithContentDescription("Betrag").performTextInput(".3")
        assertEquals("-12,5", value.value)
    }

    @Test
    fun passwordEyeTogglesVisibilityAndDisabledEyeCannotBeUsed() {
        val value = mutableStateOf("geheim")
        val enabled = mutableStateOf(true)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val showPassword = context.getString(R.string.alpine_password_show)
        val hidePassword = context.getString(R.string.alpine_password_hide)
        composeRule.setContent {
            AlpinePasswordField(
                value = value.value,
                onValueChange = { value.value = it },
                label = "Passwort",
                enabled = enabled.value,
                leadingIcon = { BasicText("L") },
            )
        }

        composeRule.onNodeWithContentDescription("Passwort")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Password))
        composeRule.onNodeWithContentDescription(showPassword)
            .assertIsEnabled()
            .performClick()
        composeRule.onNodeWithContentDescription(hidePassword).assertIsEnabled()
        composeRule.onNodeWithContentDescription(hidePassword).performClick()
        composeRule.onNodeWithContentDescription(showPassword).assertExists()

        composeRule.runOnIdle { enabled.value = false }
        composeRule.onNodeWithContentDescription(showPassword).assertIsNotEnabled()
    }
}
