package com.dolomitebyte.alpine.components

import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.down
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.up
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlpineRippleTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun borderlessDialogCloseShowsRippleWhilePressed() {
        val closeLabel = InstrumentationRegistry.getInstrumentation()
            .targetContext.getString(R.string.alpine_dialog_close)
        composeRule.setContent {
            AlpineAlertDialogContainer(
                title = "Details",
                primaryActionText = "OK",
                onPrimaryAction = {},
                onClose = {},
                darkTheme = false,
            ) { AlpineAlertDialogText("Inhalt") }
        }

        val close = composeRule.onNodeWithContentDescription(closeLabel)
        val before = close.captureToImage().toPixelMap()
        close.performTouchInput { down(center) }
        try {
            composeRule.waitUntil(timeoutMillis = 2_000) {
                val pressed = close.captureToImage().toPixelMap()
                (0 until before.width step 3).any { x ->
                    (0 until before.height step 3).any { y -> before[x, y] != pressed[x, y] }
                }
            }
        } finally {
            close.performTouchInput { up() }
        }
    }
}
