package com.dolomitebyte.alpine.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlpineBottomTabsTest {
    @get:Rule val composeRule = createComposeRule()

    private val items = listOf(
        AlpineBottomTabItem("Start", icon = { BasicText("⌂") }),
        AlpineBottomTabItem("Suche", icon = { BasicText("⌕") }),
        AlpineBottomTabItem("Profil", icon = { BasicText("○") }),
    )

    @Test
    fun selectingTabReportsIndexAndUpdatesSelection() {
        val selected = mutableIntStateOf(0)
        var selectedCallback = -1
        composeRule.setContent {
            AlpineBottomTabs(
                items = items,
                selectedIndex = selected.intValue,
                onTabSelected = { index ->
                    selectedCallback = index
                    selected.intValue = index
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
        }

        composeRule.onNodeWithContentDescription("Start")
            .assertIsSelected()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
        composeRule.onNodeWithText("⌂").assertDoesNotExist()
        composeRule.onNodeWithText("Start").assertExists()
        composeRule.onNodeWithText("Suche").assertDoesNotExist()
        composeRule.onNodeWithContentDescription("Suche")
            .assertIsNotSelected()
            .assertIsEnabled()
            .performClick()

        assertEquals(1, selectedCallback)
        composeRule.onNodeWithContentDescription("Suche").assertIsSelected()
        composeRule.onNodeWithContentDescription("Start").assertIsNotSelected()
        composeRule.onNodeWithText("Suche").assertExists()
        composeRule.onNodeWithText("Start").assertDoesNotExist()
    }

    @Test
    fun disabledTabBlocksTouch() {
        var selectedCallback = -1
        composeRule.setContent {
            AlpineBottomTabs(
                items = items.mapIndexed { index, item ->
                    if (index == 1) item.copy(enabled = false) else item
                },
                selectedIndex = 0,
                onTabSelected = { selectedCallback = it },
                darkTheme = true,
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
        }

        composeRule.onNodeWithContentDescription("Suche")
            .assertIsNotEnabled()
            .performTouchInput { click() }
        assertEquals(-1, selectedCallback)
    }

    @Test
    fun selectedPillExpandsDuringTransition() {
        val selected = mutableIntStateOf(0)
        composeRule.setContent {
            AlpineBottomTabs(
                items = items,
                selectedIndex = selected.intValue,
                onTabSelected = { selected.intValue = it },
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
        }

        composeRule.mainClock.autoAdvance = false
        val before = composeRule.onNodeWithContentDescription("Suche")
            .getUnclippedBoundsInRoot().let { it.right - it.left }
        composeRule.onNodeWithContentDescription("Suche").performClick()
        composeRule.mainClock.advanceTimeBy(80)
        val during = composeRule.onNodeWithContentDescription("Suche")
            .getUnclippedBoundsInRoot().let { it.right - it.left }
        composeRule.mainClock.advanceTimeBy(1_000)
        val after = composeRule.onNodeWithContentDescription("Suche")
            .getUnclippedBoundsInRoot().let { it.right - it.left }

        assertTrue("Selected pill should grow after selection", during > before)
        assertTrue("Selected pill should still be animating at 80 ms", during < after)
    }
}
