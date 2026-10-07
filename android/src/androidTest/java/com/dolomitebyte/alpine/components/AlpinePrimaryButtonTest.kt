package com.dolomitebyte.alpine.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlpinePrimaryButtonTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun enabledButtonExposesClickAndRunsCallback() {
        var clicks = 0
        composeRule.setContent {
            AlpinePrimaryButton(text = "Continue", onClick = { clicks++ })
        }

        composeRule.onNodeWithText("Continue")
            .assertIsEnabled()
            .assertHasClickAction()
            .performClick()

        assertEquals(1, clicks)
    }

    @Test
    fun disabledButtonIsExposedAsDisabled() {
        var clicks = 0
        composeRule.setContent {
            AlpinePrimaryButton(text = "Continue", onClick = { clicks++ }, enabled = false)
        }

        composeRule.onNodeWithText("Continue")
            .assertIsNotEnabled()
            .performTouchInput { click() }
        assertEquals(0, clicks)
    }

    @Test
    fun iconsRenderOnBothSidesOfLabel() {
        composeRule.setContent {
            AlpinePrimaryButton(
                text = "Continue",
                onClick = {},
                leadingIcon = { BasicText("L", Modifier.testTag("leading")) },
                trailingIcon = { BasicText("R", Modifier.testTag("trailing")) },
            )
        }

        val leading = composeRule.onNodeWithTag("leading", useUnmergedTree = true)
            .fetchSemanticsNode().boundsInRoot.left
        val label = composeRule.onNodeWithText("Continue", useUnmergedTree = true)
            .fetchSemanticsNode().boundsInRoot.left
        val trailing = composeRule.onNodeWithTag("trailing", useUnmergedTree = true)
            .fetchSemanticsNode().boundsInRoot.left
        assertTrue(leading < label && label < trailing)
    }

    @Test
    fun loadingBlocksClickUntilFinished() {
        val loading = mutableStateOf(true)
        var clicks = 0
        val loadingDescription = InstrumentationRegistry.getInstrumentation()
            .targetContext.getString(R.string.alpine_button_loading)
        composeRule.setContent {
            AlpinePrimaryButton(text = "Continue", onClick = { clicks++ }, loading = loading.value)
        }

        val button = composeRule.onNodeWithText("Continue")
        button.assertIsNotEnabled()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, loadingDescription))
            .performTouchInput { click() }
        assertEquals(0, clicks)

        composeRule.runOnIdle { loading.value = false }
        button.assertIsEnabled().performClick()
        assertEquals(1, clicks)
    }
}
