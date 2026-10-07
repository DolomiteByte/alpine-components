package com.dolomitebyte.alpine.components

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlpineSecondaryButtonTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun enabledButtonExposesClickAndRunsCallback() {
        var clicks = 0
        composeRule.setContent {
            AlpineSecondaryButton(text = "More", onClick = { clicks++ })
        }

        composeRule.onNodeWithText("More")
            .assertIsEnabled()
            .assertHasClickAction()
            .performClick()

        assertEquals(1, clicks)
    }

    @Test
    fun disabledButtonIsExposedAsDisabled() {
        var clicks = 0
        composeRule.setContent {
            AlpineSecondaryButton(text = "More", onClick = { clicks++ }, enabled = false)
        }

        composeRule.onNodeWithText("More").assertIsNotEnabled()
        assertEquals(0, clicks)
    }
}
