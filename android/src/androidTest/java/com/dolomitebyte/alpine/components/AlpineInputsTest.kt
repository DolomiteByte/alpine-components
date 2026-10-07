package com.dolomitebyte.alpine.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
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
}
