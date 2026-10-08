package com.dolomitebyte.alpine.components

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

private val LightSurface = Color.White
private val DarkSurface = Color(0xFF181D23)
private val LightBorder = Color(0xFFD2DAE5)
private val DarkBorder = Color(0xFF39414B)
private val LightTitle = Color(0xFF132B50)
private val DarkTitle = Color(0xFFF4F8FF)
private val LightBody = Color(0xFF455A73)
private val DarkBody = Color(0xFFC9D9EA)
private val LightFocus = Color(0xFF0054FF)
private val DarkFocus = Color(0xFF33D5EB)
private val DialogShape = RoundedCornerShape(24.dp)
private val Manrope = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_bold, FontWeight.Bold),
)
private val LocalDialogDarkTheme = staticCompositionLocalOf<Boolean?> { null }

/**
 * A modal Alpine dialog. The caller owns visibility and updates it in [onDismissRequest].
 * Outside taps, Back, and the close icon all call [onDismissRequest]. Action callbacks are
 * separate, so the caller can validate or finish work before closing the dialog.
 *
 * Pass both [secondaryActionText] and [onSecondaryAction] to show a secondary action.
 * [content] can contain any Compose UI; long content scrolls while actions stay visible.
 */
@Composable
fun AlpineAlertDialog(
    title: String,
    primaryActionText: String,
    onPrimaryAction: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryActionText: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    primaryActionEnabled: Boolean = true,
    primaryActionLoading: Boolean = false,
    secondaryActionEnabled: Boolean = true,
    secondaryActionLoading: Boolean = false,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        AlpineAlertDialogContainer(
            title = title,
            primaryActionText = primaryActionText,
            onPrimaryAction = onPrimaryAction,
            onClose = onDismissRequest,
            modifier = modifier,
            secondaryActionText = secondaryActionText,
            onSecondaryAction = onSecondaryAction,
            primaryActionEnabled = primaryActionEnabled,
            primaryActionLoading = primaryActionLoading,
            secondaryActionEnabled = secondaryActionEnabled,
            secondaryActionLoading = secondaryActionLoading,
            darkTheme = darkTheme,
            content = content,
        )
    }
}

/** The reusable dialog panel for custom overlays and Compose previews. */
@Composable
fun AlpineAlertDialogContainer(
    title: String,
    primaryActionText: String,
    onPrimaryAction: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryActionText: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    primaryActionEnabled: Boolean = true,
    primaryActionLoading: Boolean = false,
    secondaryActionEnabled: Boolean = true,
    secondaryActionLoading: Boolean = false,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable ColumnScope.() -> Unit,
) {
    require(
        (secondaryActionText == null && onSecondaryAction == null) ||
            (!secondaryActionText.isNullOrBlank() && onSecondaryAction != null)
    ) {
        "Provide both secondaryActionText and onSecondaryAction, or neither"
    }

    val surface = if (darkTheme) DarkSurface else LightSurface
    val border = if (darkTheme) DarkBorder else LightBorder
    val titleColor = if (darkTheme) DarkTitle else LightTitle
    val maxHeight = (LocalConfiguration.current.screenHeightDp.dp - 48.dp).coerceAtLeast(240.dp)

    Column(
        modifier = modifier
            .widthIn(max = 440.dp)
            .fillMaxWidth()
            .heightIn(max = maxHeight)
            .shadow(24.dp, DialogShape)
            .clip(DialogShape)
            .background(surface)
            .border(1.dp, border, DialogShape)
            .padding(24.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            BasicText(
                text = title,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp, end = 12.dp)
                    .semantics { heading() },
                style = TextStyle(
                    color = titleColor,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    lineHeight = 28.sp,
                    letterSpacing = (-0.22).sp,
                ),
            )
            AlpineDialogCloseButton(onClick = onClose, darkTheme = darkTheme)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp, bottom = 24.dp),
        ) {
            CompositionLocalProvider(LocalDialogDarkTheme provides darkTheme) {
                content()
            }
        }

        BoxWithConstraints(Modifier.fillMaxWidth()) {
            if (secondaryActionText != null && maxWidth >= 340.dp) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AlpineSecondaryButton(
                        text = secondaryActionText,
                        onClick = onSecondaryAction!!,
                        modifier = Modifier.weight(1f),
                        enabled = secondaryActionEnabled,
                        loading = secondaryActionLoading,
                        darkTheme = darkTheme,
                    )
                    AlpinePrimaryButton(
                        text = primaryActionText,
                        onClick = onPrimaryAction,
                        modifier = Modifier.weight(1f),
                        enabled = primaryActionEnabled,
                        loading = primaryActionLoading,
                        showDefaultArrow = false,
                        darkTheme = darkTheme,
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (secondaryActionText != null) {
                        AlpineSecondaryButton(
                            text = secondaryActionText,
                            onClick = onSecondaryAction!!,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = secondaryActionEnabled,
                            loading = secondaryActionLoading,
                            darkTheme = darkTheme,
                        )
                    }
                    AlpinePrimaryButton(
                        text = primaryActionText,
                        onClick = onPrimaryAction,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = primaryActionEnabled,
                        loading = primaryActionLoading,
                        showDefaultArrow = false,
                        darkTheme = darkTheme,
                    )
                }
            }
        }
    }
}

/** Body copy with the dialog's Manrope type and light/dark colors. */
@Composable
fun AlpineAlertDialogText(
    text: String,
    modifier: Modifier = Modifier,
    darkTheme: Boolean? = null,
) {
    val useDarkTheme = darkTheme ?: LocalDialogDarkTheme.current ?: isSystemInDarkTheme()
    BasicText(
        text = text,
        modifier = modifier,
        style = TextStyle(
            color = if (useDarkTheme) DarkBody else LightBody,
            fontFamily = Manrope,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 23.sp,
        ),
    )
}

@Composable
private fun AlpineDialogCloseButton(onClick: () -> Unit, darkTheme: Boolean) {
    val interactions = remember { MutableInteractionSource() }
    val focused by interactions.collectIsFocusedAsState()
    val iconColor = if (darkTheme) DarkTitle else LightTitle
    val focusColor = if (darkTheme) DarkFocus else LightFocus
    val closeLabel = stringResource(R.string.alpine_dialog_close)

    Box(
        modifier = Modifier
            .size(48.dp)
            .drawWithContent {
                drawContent()
                if (focused) {
                    val stroke = 3.dp.toPx()
                    val inset = 3.dp.toPx() + stroke / 2f
                    drawRoundRect(
                        color = focusColor,
                        topLeft = Offset(-inset, -inset),
                        size = Size(size.width + inset * 2f, size.height + inset * 2f),
                        cornerRadius = CornerRadius(15.dp.toPx()),
                        style = Stroke(stroke),
                    )
                }
            }
            .semantics { contentDescription = closeLabel }
            .clickable(
                interactionSource = interactions,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(18.dp).clearAndSetSemantics {}) {
            val stroke = 2.dp.toPx()
            val inset = stroke / 2f
            drawLine(
                color = iconColor,
                start = Offset(inset, inset),
                end = Offset(size.width - inset, size.height - inset),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = iconColor,
                start = Offset(size.width - inset, inset),
                end = Offset(inset, size.height - inset),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Preview(name = "Alert dialog light", showBackground = true, backgroundColor = 0xFFF5F8FF)
@Preview(
    name = "Alert dialog dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF090D12,
)
@Composable
private fun AlpineAlertDialogPreview() {
    val dark = isSystemInDarkTheme()
    Box(Modifier.padding(16.dp)) {
        AlpineAlertDialogContainer(
            title = "Änderungen speichern?",
            primaryActionText = "Speichern",
            onPrimaryAction = {},
            secondaryActionText = "Abbrechen",
            onSecondaryAction = {},
            onClose = {},
            darkTheme = dark,
        ) {
            AlpineAlertDialogText("Deine Änderungen werden übernommen und sind sofort sichtbar.")
        }
    }
}
