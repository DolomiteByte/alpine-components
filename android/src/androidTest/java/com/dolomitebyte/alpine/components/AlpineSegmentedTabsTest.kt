package com.dolomitebyte.alpine.components

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class AlpineSegmentedTabsTest {
    @get:Rule val composeRule = createComposeRule()

    private val items = listOf("7 T", "30 T", "3 M", "6 M").map { AlpineSegmentedTabItem(it) }

    @Test
    fun selectionChangesWithoutMovingOrResizingSegments() {
        val selected = mutableIntStateOf(2)
        composeRule.setContent {
            AlpineSegmentedTabs(
                items = items,
                selectedIndex = selected.intValue,
                onTabSelected = { selected.intValue = it },
                modifier = Modifier.width(320.dp),
                darkTheme = true,
            )
        }

        val before = items.map { composeRule.onNodeWithContentDescription(it.label).getUnclippedBoundsInRoot() }
        composeRule.onNodeWithContentDescription("3 M")
            .assertIsSelected()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
        composeRule.onNodeWithContentDescription("30 T").performClick()
        assertEquals(1, selected.intValue)
        composeRule.onNodeWithContentDescription("30 T").assertIsSelected()
        composeRule.onNodeWithContentDescription("3 M").assertIsNotSelected()

        val after = items.map { composeRule.onNodeWithContentDescription(it.label).getUnclippedBoundsInRoot() }
        before.zip(after).forEach { (old, new) ->
            assertTrue(abs((old.left - new.left).value) < 0.5f)
            assertTrue(abs((old.right - new.right).value) < 0.5f)
        }
    }

    @Test
    fun fourSegmentsFitNarrowWidthWithFullTouchTargets() {
        composeRule.setContent {
            AlpineSegmentedTabs(items, selectedIndex = 0, onTabSelected = {}, modifier = Modifier.width(320.dp))
        }

        val bounds = items.map { composeRule.onNodeWithContentDescription(it.label).getUnclippedBoundsInRoot() }
        bounds.forEach { rect ->
            assertTrue(rect.right - rect.left >= 48.dp)
            assertTrue(rect.bottom - rect.top >= 48.dp)
        }
        bounds.zipWithNext().forEach { (left, right) ->
            assertTrue(left.right <= right.left + 0.5.dp)
        }
    }

    @Test
    fun disabledSegmentDoesNotChangeSelection() {
        var clicks = 0
        composeRule.setContent {
            AlpineSegmentedTabs(
                items = items.mapIndexed { index, item -> if (index == 3) item.copy(enabled = false) else item },
                selectedIndex = 2,
                onTabSelected = { clicks++ },
            )
        }

        composeRule.onNodeWithContentDescription("6 M")
            .assertIsNotEnabled()
            .performTouchInput { click() }
        assertEquals(0, clicks)
    }
}
