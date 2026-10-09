package com.dolomitebyte.alpine.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlpineChoicesTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun radioCardsExposeSelectionAndChooseOneValue() {
        val selected = mutableStateOf("regular")
        composeRule.setContent {
            AlpineSelectionGroup("Dose type", darkTheme = true) {
                AlpineRadioCard(
                    selected = selected.value == "regular",
                    onClick = { selected.value = "regular" },
                    title = "Regular",
                    supportingText = "At fixed times",
                    darkTheme = true,
                )
                AlpineRadioCard(
                    selected = selected.value == "as_needed",
                    onClick = { selected.value = "as_needed" },
                    title = "As needed",
                    supportingText = "When required",
                    darkTheme = true,
                )
            }
        }

        composeRule.onNodeWithContentDescription("Regular. At fixed times")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
        composeRule.onNodeWithContentDescription("As needed. When required").performClick()
        assertEquals("as_needed", selected.value)
        composeRule.onNodeWithContentDescription("Regular. At fixed times")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, false))
        composeRule.onNodeWithContentDescription("As needed. When required")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
    }

    @Test
    fun checkboxCardTogglesAndDisabledCardBlocksInput() {
        val checked = mutableStateOf(false)
        var disabledClicks = 0
        composeRule.setContent {
            AlpineSelectionGroup("Options") {
                AlpineCheckboxCard(
                    checked = checked.value,
                    onCheckedChange = { checked.value = it },
                    title = "Reminder",
                    supportingText = "Notify me",
                )
                AlpineCheckboxCard(
                    checked = false,
                    onCheckedChange = { disabledClicks++ },
                    title = "Locked",
                    enabled = false,
                )
            }
        }

        composeRule.onNodeWithContentDescription("Reminder. Notify me")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.Off))
            .performClick()
        assertTrue(checked.value)
        composeRule.onNodeWithContentDescription("Reminder. Notify me")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.On))
            .performClick()
        assertFalse(checked.value)
        composeRule.onNodeWithContentDescription("Locked")
            .assertIsNotEnabled()
            .performTouchInput { click() }
        assertEquals(0, disabledClicks)
    }

    @Test
    fun dropdownOpensNativeSheetAndUpdatesControlledValue() {
        val selected = mutableStateOf("tablets")
        composeRule.setContent {
            AlpineDropdownField(
                label = "Unit",
                options = listOf(
                    AlpineDropdownOption("tablets", "Tablet(s)"),
                    AlpineDropdownOption("drops", "Drop(s)", "Liquid medicine"),
                ),
                selectedId = selected.value,
                onSelected = { selected.value = it },
                darkTheme = true,
            )
        }

        composeRule.onNodeWithContentDescription("Unit")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Tablet(s)"))
            .performClick()
        composeRule.onNodeWithContentDescription("Drop(s). Liquid medicine").performClick()
        assertEquals("drops", selected.value)
        composeRule.onNodeWithContentDescription("Unit")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Drop(s)"))
    }

    @Test
    fun disabledDropdownDoesNotOpenSheet() {
        composeRule.setContent {
            AlpineDropdownField(
                label = "Disabled unit",
                options = listOf(AlpineDropdownOption("one", "One")),
                selectedId = null,
                onSelected = {},
                enabled = false,
            )
        }
        composeRule.onNodeWithContentDescription("Disabled unit")
            .assertIsNotEnabled()
            .performTouchInput { click() }
        composeRule.onNodeWithContentDescription("One").assertDoesNotExist()
    }
}
