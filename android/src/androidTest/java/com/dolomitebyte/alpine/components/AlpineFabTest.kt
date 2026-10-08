package com.dolomitebyte.alpine.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlpineFabTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun iconOnlyFabHasAccessibleNameAndTouchTarget() {
        var clicks = 0
        composeRule.setContent {
            AlpineFab(
                onClick = { clicks++ },
                icon = { BasicText("+") },
                contentDescription = "Hinzufügen",
            )
        }

        val fab = composeRule.onNodeWithContentDescription("Hinzufügen")
        fab.assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
        val bounds = fab.getUnclippedBoundsInRoot()
        assertTrue(bounds.right - bounds.left >= 56.dp)
        assertTrue(bounds.bottom - bounds.top >= 56.dp)
        fab.performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun textOnlyAndExtendedFabUseTheirTextAsAccessibleName() {
        var textClicks = 0
        var extendedClicks = 0
        composeRule.setContent {
            Column {
                AlpineFab(text = "Erstellen", onClick = { textClicks++ })
                AlpineFab(
                    text = "Neue Notiz",
                    icon = { BasicText("+") },
                    onClick = { extendedClicks++ },
                )
            }
        }

        composeRule.onNodeWithContentDescription("Erstellen").performClick()
        composeRule.onNodeWithContentDescription("Neue Notiz").performClick()
        assertEquals(1, textClicks)
        assertEquals(1, extendedClicks)
    }

    @Test
    fun disabledAndLoadingFabsBlockClicks() {
        var clicks = 0
        val loadingDescription = InstrumentationRegistry.getInstrumentation()
            .targetContext.getString(R.string.alpine_button_loading)
        composeRule.setContent {
            Column {
                AlpineFab(text = "Deaktiviert", onClick = { clicks++ }, enabled = false)
                AlpineFab(text = "Warten", onClick = { clicks++ }, loading = true)
            }
        }

        composeRule.onNodeWithContentDescription("Deaktiviert")
            .assertIsNotEnabled()
            .performTouchInput { click() }
        composeRule.onNodeWithContentDescription("Warten")
            .assertIsNotEnabled()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, loadingDescription))
            .performTouchInput { click() }
        assertEquals(0, clicks)
    }

    @Test
    fun overlaySupportsBothBottomCornersAndCustomAlignmentWithOffset() {
        val alignment = mutableStateOf<Alignment>(Alignment.BottomEnd)
        val offset = mutableStateOf(DpOffset.Zero)
        composeRule.setContent {
            AlpineFabOverlay(
                fab = {
                    AlpineFab(
                        onClick = {},
                        icon = { BasicText("+") },
                        contentDescription = "Hinzufügen",
                    )
                },
                modifier = Modifier.size(200.dp),
                alignment = alignment.value,
                edgePadding = PaddingValues(0.dp),
                offset = offset.value,
                windowInsets = WindowInsets(0, 0, 0, 0),
            ) {}
        }

        val right = composeRule.onNodeWithContentDescription("Hinzufügen").getUnclippedBoundsInRoot()
        composeRule.runOnIdle { alignment.value = Alignment.BottomStart }
        val left = composeRule.onNodeWithContentDescription("Hinzufügen").getUnclippedBoundsInRoot()
        assertTrue(right.left > left.left)
        assertEquals(right.top, left.top)

        composeRule.runOnIdle {
            alignment.value = Alignment.Center
            offset.value = DpOffset(20.dp, (-12).dp)
        }
        val custom = composeRule.onNodeWithContentDescription("Hinzufügen").getUnclippedBoundsInRoot()
        assertTrue(custom.left > left.left)
        assertTrue(custom.top < left.top)
    }
}
