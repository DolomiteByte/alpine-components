package com.dolomitebyte.alpine.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlpineAlertDialogTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun modalHasHeadingContentCloseAndIndependentActions() {
        var primaryClicks = 0
        var secondaryClicks = 0
        var dismissals = 0
        val closeLabel = InstrumentationRegistry.getInstrumentation()
            .targetContext.getString(R.string.alpine_dialog_close)

        composeRule.setContent {
            AlpineAlertDialog(
                title = "Änderungen speichern?",
                primaryActionText = "Speichern",
                onPrimaryAction = { primaryClicks++ },
                secondaryActionText = "Abbrechen",
                onSecondaryAction = { secondaryClicks++ },
                onDismissRequest = { dismissals++ },
            ) {
                AlpineAlertDialogText("Die Änderungen werden übernommen.")
            }
        }

        composeRule.onNodeWithText("Änderungen speichern?")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
        composeRule.onNodeWithText("Die Änderungen werden übernommen.").assertIsDisplayed()
        composeRule.onNodeWithText("Speichern").performClick()
        composeRule.onNodeWithText("Abbrechen").performClick()
        composeRule.onNodeWithContentDescription(closeLabel)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .performClick()

        assertEquals(1, primaryClicks)
        assertEquals(1, secondaryClicks)
        assertEquals(1, dismissals)
    }

    @Test
    fun loadingAndDisabledActionsDoNotFire() {
        var clicks = 0
        composeRule.setContent {
            AlpineAlertDialogContainer(
                title = "Warten",
                primaryActionText = "Speichern",
                onPrimaryAction = { clicks++ },
                primaryActionLoading = true,
                secondaryActionText = "Abbrechen",
                onSecondaryAction = { clicks++ },
                secondaryActionEnabled = false,
                onClose = {},
            ) {
                AlpineAlertDialogText("Wird verarbeitet")
            }
        }

        composeRule.onNodeWithText("Speichern").assertIsNotEnabled().performTouchInput { click() }
        composeRule.onNodeWithText("Abbrechen").assertIsNotEnabled().performTouchInput { click() }
        assertEquals(0, clicks)
    }

    @Test
    fun longContentKeepsActionsAndCloseVisible() {
        composeRule.setContent {
            AlpineAlertDialogContainer(
                title = "Details",
                primaryActionText = "Bestätigen",
                onPrimaryAction = {},
                secondaryActionText = "Zurück",
                onSecondaryAction = {},
                onClose = {},
                modifier = Modifier.width(320.dp).heightIn(max = 320.dp),
            ) {
                repeat(30) { BasicText("Eintrag $it") }
            }
        }

        composeRule.onNodeWithText("Bestätigen").assertIsDisplayed()
        composeRule.onNodeWithText("Zurück").assertIsDisplayed()
        composeRule.onNodeWithContentDescription(
            InstrumentationRegistry.getInstrumentation()
                .targetContext.getString(R.string.alpine_dialog_close)
        ).assertIsDisplayed()
    }
}
