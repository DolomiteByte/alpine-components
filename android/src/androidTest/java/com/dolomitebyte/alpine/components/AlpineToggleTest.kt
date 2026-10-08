package com.dolomitebyte.alpine.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlpineToggleTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun settingsToggleReportsStateAndChangesWhenRowIsTapped() {
        val checked = mutableStateOf(false)
        composeRule.setContent {
            AlpineToggle(
                checked = checked.value,
                onCheckedChange = { checked.value = it },
                label = "Benachrichtigungen",
                supportingText = "Wichtige Updates",
            )
        }

        composeRule.onNodeWithContentDescription("Benachrichtigungen. Wichtige Updates")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.Off))
            .performClick()
        assertTrue(checked.value)
        composeRule.onNodeWithContentDescription("Benachrichtigungen. Wichtige Updates")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.On))
        composeRule.onNodeWithText("Wichtige Updates").assertDoesNotExist()
    }

    @Test
    fun disabledToggleDoesNotChange() {
        var changes = 0
        composeRule.setContent {
            AlpineToggle(
                checked = true,
                onCheckedChange = { changes++ },
                label = "Gesperrt",
                enabled = false,
                darkTheme = true,
            )
        }

        composeRule.onNodeWithContentDescription("Gesperrt")
            .assertIsNotEnabled()
            .performTouchInput { click() }
        assertEquals(0, changes)
    }

    @Test
    fun themeToggleNamesDarkModeAndUpdatesHostState() {
        val darkMode = mutableStateOf(false)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val darkDescription = context.getString(R.string.alpine_theme_dark_mode)
        val lightLabel = context.getString(R.string.alpine_theme_light_mode)
        composeRule.setContent {
            AlpineThemeToggle(
                darkMode = darkMode.value,
                onDarkModeChange = { darkMode.value = it },
                showModeLabel = true,
            )
        }

        composeRule.onNodeWithContentDescription(darkDescription)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.Off))
        composeRule.onNodeWithText(lightLabel).assertDoesNotExist()
        composeRule.onNodeWithContentDescription(darkDescription).performClick()
        assertTrue(darkMode.value)
        composeRule.onNodeWithContentDescription(darkDescription)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.On))
    }

    @Test
    fun themeToggleKeepsAtLeastFortyEightDpTouchHeight() {
        composeRule.setContent {
            AlpineThemeToggle(darkMode = false, onDarkModeChange = {})
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val height = composeRule.onNodeWithContentDescription(context.getString(R.string.alpine_theme_dark_mode))
            .getUnclippedBoundsInRoot().let { it.bottom - it.top }
        assertTrue(height >= 48.dp)
    }
}
