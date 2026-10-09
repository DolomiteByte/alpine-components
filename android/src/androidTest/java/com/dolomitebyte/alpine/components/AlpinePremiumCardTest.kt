package com.dolomitebyte.alpine.components

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlpinePremiumCardTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun cardShowsContentAndHasOneAction() {
        var clicks = 0
        composeRule.setContent {
            AlpinePremiumCard(
                title = "TheraBuddy Premium freischalten",
                description = "Sieben Tage kostenlos testen.",
                actionText = "Premium ansehen",
                onClick = { clicks++ },
                modifier = Modifier.width(280.dp).testTag("premium-card"),
                eyebrow = "7 Tage kostenlos",
                benefits = listOf("Ohne Werbung", "Unbegrenzte Dokumentfunktionen"),
            )
        }

        composeRule.onNodeWithText("7 Tage kostenlos").assertIsDisplayed()
        composeRule.onNodeWithText("TheraBuddy Premium freischalten").assertIsDisplayed()
        composeRule.onNodeWithText("Ohne Werbung").assertIsDisplayed()
        composeRule.onNodeWithText("Unbegrenzte Dokumentfunktionen").assertIsDisplayed()
        composeRule.onNodeWithText("Premium ansehen").assertIsDisplayed()
        composeRule.onAllNodes(hasClickAction()).assertCountEquals(1)
        composeRule.onNodeWithTag("premium-card").performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun disabledDarkCardBlocksClick() {
        var clicks = 0
        composeRule.setContent {
            AlpinePremiumCard(
                title = "Premium",
                description = "Weitere Funktionen",
                actionText = "Ansehen",
                onClick = { clicks++ },
                modifier = Modifier.testTag("premium-card"),
                enabled = false,
                darkTheme = true,
            )
        }

        composeRule.onNodeWithTag("premium-card").assertIsNotEnabled().performTouchInput { click() }
        assertEquals(0, clicks)
    }
}
