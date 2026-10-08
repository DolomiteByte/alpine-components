package com.dolomitebyte.alpine.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class AlpineSnackbarTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun defaultInfoAndWarningSupportIndependentOptionalActions() {
        var primaryClicks = 0
        var secondaryClicks = 0
        composeRule.setContent {
            Column {
                AlpineSnackbar("Gespeichert")
                AlpineSnackbar(
                    "Neue Funktionen verfügbar",
                    variant = AlpineSnackbarVariant.Info,
                    actionText = "Ansehen",
                    onAction = { primaryClicks++ },
                )
                AlpineSnackbar(
                    "Verbindung unterbrochen",
                    variant = AlpineSnackbarVariant.Warning,
                    actionText = "Erneut",
                    onAction = { primaryClicks++ },
                    secondaryActionText = "Später",
                    onSecondaryAction = { secondaryClicks++ },
                )
                AlpineSnackbar(
                    "Hinweis ohne primäre Aktion",
                    secondaryActionText = "Schließen",
                    onSecondaryAction = { secondaryClicks++ },
                )
            }
        }

        composeRule.onNodeWithText("Gespeichert").assertIsDisplayed()
        composeRule.onNodeWithText("Ansehen").performClick()
        composeRule.onNodeWithText("Erneut").performClick()
        composeRule.onNodeWithText("Später").performClick()
        composeRule.onNodeWithText("Schließen").performClick()
        assertEquals(2, primaryClicks)
        assertEquals(2, secondaryClicks)
    }

    @Test
    fun shortMessageWrapsContentAndLongMessageUsesMultipleLines() {
        composeRule.setContent {
            Column(Modifier.width(400.dp)) {
                AlpineSnackbar("Bereit", modifier = Modifier.testTag("short"))
                AlpineSnackbar(
                    "Die Einstellungen wurden gespeichert und werden bei der nächsten Synchronisierung auf allen Geräten übernommen.",
                    modifier = Modifier.testTag("long"),
                )
            }
        }

        val short = composeRule.onNodeWithTag("short").getUnclippedBoundsInRoot()
        val long = composeRule.onNodeWithTag("long").getUnclippedBoundsInRoot()
        assertTrue(short.right - short.left < 400.dp)
        assertTrue(long.bottom - long.top > short.bottom - short.top)
    }

    @Test
    fun presetIconCanBeHiddenWithoutChangingMessageWidth() {
        composeRule.setContent {
            Column {
                AlpineSnackbar("Hinweis", modifier = Modifier.testTag("plain"))
                AlpineSnackbar("Hinweis", modifier = Modifier.testTag("info"), variant = AlpineSnackbarVariant.Info)
                AlpineSnackbar(
                    "Hinweis",
                    modifier = Modifier.testTag("warning-without-icon"),
                    variant = AlpineSnackbarVariant.Warning,
                    showIcon = false,
                )
            }
        }

        val plain = composeRule.onNodeWithTag("plain").getUnclippedBoundsInRoot()
        val info = composeRule.onNodeWithTag("info").getUnclippedBoundsInRoot()
        val warningWithoutIcon = composeRule.onNodeWithTag("warning-without-icon").getUnclippedBoundsInRoot()
        assertTrue(info.right - info.left > plain.right - plain.left)
        assertEquals(plain.right - plain.left, warningWithoutIcon.right - warningWithoutIcon.left)
    }

    @Test
    fun narrowMultilineSnackbarKeepsBothActionsBelowMessage() {
        composeRule.setContent {
            AlpineSnackbar(
                "Die Verbindung ist unterbrochen. Bitte überprüfe deine Internetverbindung und versuche es erneut.",
                modifier = Modifier.width(280.dp),
                variant = AlpineSnackbarVariant.Warning,
                actionText = "Erneut",
                onAction = {},
                secondaryActionText = "Später",
                onSecondaryAction = {},
            )
        }

        val message = composeRule.onNodeWithText(
            "Die Verbindung ist unterbrochen. Bitte überprüfe deine Internetverbindung und versuche es erneut."
        ).getUnclippedBoundsInRoot()
        val primary = composeRule.onNodeWithText("Erneut").getUnclippedBoundsInRoot()
        val secondary = composeRule.onNodeWithText("Später").getUnclippedBoundsInRoot()
        assertTrue(primary.top >= message.bottom)
        assertTrue(secondary.top >= message.bottom)
    }

    @Test
    fun overlaySupportsBottomCenterBothCornersAndCustomOffset() {
        val alignment = mutableStateOf<Alignment>(Alignment.BottomCenter)
        val offset = mutableStateOf(DpOffset.Zero)
        composeRule.setContent {
            AlpineSnackbarOverlay(
                visible = true,
                message = "Position prüfen",
                onDismissRequest = {},
                modifier = Modifier.size(320.dp),
                alignment = alignment.value,
                offset = offset.value,
                edgePadding = PaddingValues(0.dp),
                windowInsets = WindowInsets(0, 0, 0, 0),
                durationMillis = null,
            ) {}
        }

        val center = composeRule.onNodeWithText("Position prüfen").getUnclippedBoundsInRoot()
        composeRule.runOnIdle { alignment.value = Alignment.BottomStart }
        val left = composeRule.onNodeWithText("Position prüfen").getUnclippedBoundsInRoot()
        composeRule.runOnIdle { alignment.value = Alignment.BottomEnd }
        val right = composeRule.onNodeWithText("Position prüfen").getUnclippedBoundsInRoot()
        assertTrue(left.left < center.left)
        assertTrue(center.left < right.left)
        assertEquals(left.top, right.top)

        composeRule.runOnIdle {
            alignment.value = Alignment.Center
            offset.value = DpOffset(18.dp, (-12).dp)
        }
        val custom = composeRule.onNodeWithText("Position prüfen").getUnclippedBoundsInRoot()
        assertTrue(custom.top < center.top)
    }

    @Test
    fun overlayRequestsDismissalAfterItsDuration() {
        val visible = mutableStateOf(true)
        val dismissals = AtomicInteger(0)
        composeRule.setContent {
            AlpineSnackbarOverlay(
                visible = visible.value,
                message = "Zeitgesteuerte Meldung",
                onDismissRequest = {
                    dismissals.incrementAndGet()
                    visible.value = false
                },
                durationMillis = 200L,
            ) {}
        }

        composeRule.waitUntil(5_000) { dismissals.get() == 1 }
        composeRule.waitForIdle()
        assertEquals(1, dismissals.get())
    }
}
