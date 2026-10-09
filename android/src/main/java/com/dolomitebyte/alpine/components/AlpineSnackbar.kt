package com.dolomitebyte.alpine.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/** The visual tone of an [AlpineSnackbar]. */
enum class AlpineSnackbarVariant { Default, Info, Warning }

private val SnackbarShape = RoundedCornerShape(18.dp)
private val ActionShape = RoundedCornerShape(11.dp)
private val SnackbarFont = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold),
)

private data class SnackbarColors(
    val surface: Color,
    val text: Color,
    val icon: Color,
    val actionBackground: Color,
    val actionText: Color,
    val secondaryText: Color,
)

private fun snackbarColors(variant: AlpineSnackbarVariant, dark: Boolean): SnackbarColors = when (variant) {
    AlpineSnackbarVariant.Default -> if (dark) SnackbarColors(
        surface = Color(0xFF181D23), text = Color(0xFFEDF3FB),
        icon = Color(0xFF6DE1EF),
        actionBackground = Color(0xFF0A5BE3), actionText = Color.White,
        secondaryText = Color(0xFFC1EFFF),
    ) else SnackbarColors(
        surface = Color.White, text = Color(0xFF24344B),
        icon = Color(0xFF0054FF),
        actionBackground = Color(0xFF0054FF), actionText = Color.White,
        secondaryText = Color(0xFF0054FF),
    )
    AlpineSnackbarVariant.Info -> if (dark) SnackbarColors(
        surface = Color(0xFF102C3D), text = Color(0xFFE3F8FF),
        icon = Color(0xFF78DCF5),
        actionBackground = Color(0xFF087CB5), actionText = Color.White,
        secondaryText = Color(0xFFB9EFFF),
    ) else SnackbarColors(
        surface = Color(0xFFEBF7FF), text = Color(0xFF163C56),
        icon = Color(0xFF086C9C),
        actionBackground = Color(0xFF087CB5), actionText = Color.White,
        secondaryText = Color(0xFF075781),
    )
    AlpineSnackbarVariant.Warning -> if (dark) SnackbarColors(
        surface = Color(0xFF352719), text = Color(0xFFFFF1D7),
        icon = Color(0xFFFFD08B),
        actionBackground = Color(0xFFA95E0D), actionText = Color.White,
        secondaryText = Color(0xFFFFE4BE),
    ) else SnackbarColors(
        surface = Color(0xFFFFF5E7), text = Color(0xFF5F3E19),
        icon = Color(0xFF9B5808),
        actionBackground = Color(0xFFA55E08), actionText = Color.White,
        secondaryText = Color(0xFF874900),
    )
}

/**
 * A content-width snackbar with no border. Info and warning show their icon by default;
 * set [showIcon] to false to hide it. A custom [icon] works with every variant.
 *
 * Action labels and callbacks must be supplied in pairs. Either action can be omitted.
 * The caller owns visibility and may use [AlpineSnackbarOverlay] for placement and timing.
 */
@Composable
fun AlpineSnackbar(
    message: String,
    modifier: Modifier = Modifier,
    variant: AlpineSnackbarVariant = AlpineSnackbarVariant.Default,
    icon: (@Composable (Color) -> Unit)? = null,
    showIcon: Boolean = icon != null || variant != AlpineSnackbarVariant.Default,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    secondaryActionText: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
) {
    if (message.isBlank()) return
    require((actionText == null && onAction == null) || (!actionText.isNullOrBlank() && onAction != null)) {
        "Provide both actionText and onAction, or neither"
    }
    require(
        (secondaryActionText == null && onSecondaryAction == null) ||
            (!secondaryActionText.isNullOrBlank() && onSecondaryAction != null)
    ) { "Provide both secondaryActionText and onSecondaryAction, or neither" }

    val colors = snackbarColors(variant, darkTheme)
    val hasIcon = showIcon
    val trailingPadding = if (hasIcon) 18.dp else 12.dp
    val hasActions = actionText != null || secondaryActionText != null
    val bothActions = actionText != null && secondaryActionText != null

    BoxWithConstraints(
        modifier = modifier
            .widthIn(max = 560.dp)
            .shadow(if (darkTheme) 12.dp else 8.dp, SnackbarShape)
            .clip(SnackbarShape)
            .background(colors.surface)
            .semantics { liveRegion = if (variant == AlpineSnackbarVariant.Warning) LiveRegionMode.Assertive else LiveRegionMode.Polite },
    ) {
        val inlineActions = hasActions && maxWidth >= 480.dp
        val stackedActions = bothActions && maxWidth < 280.dp
        val messageMaxWidth = when {
            inlineActions && bothActions -> 220.dp
            inlineActions -> 310.dp
            else -> maxWidth - 12.dp - trailingPadding - if (hasIcon) 34.dp else 0.dp
        }.coerceAtLeast(80.dp)

        Column(
            modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = trailingPadding, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (hasIcon) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clearAndSetSemantics {},
                        contentAlignment = Alignment.Center,
                    ) {
                        if (icon != null) {
                            icon(colors.icon)
                        } else {
                            BasicText(
                                if (variant == AlpineSnackbarVariant.Warning) "!" else "i",
                                style = TextStyle(
                                    color = colors.icon,
                                    fontFamily = SnackbarFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp,
                                    lineHeight = 24.sp,
                                ),
                            )
                        }
                    }
                }
                BasicText(
                    text = message,
                    modifier = Modifier.widthIn(max = messageMaxWidth),
                    style = TextStyle(
                        color = colors.text,
                        fontFamily = SnackbarFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                    ),
                )
                if (inlineActions) {
                    SnackbarActions(
                        colors = colors,
                        actionText = actionText,
                        onAction = onAction,
                        secondaryActionText = secondaryActionText,
                        onSecondaryAction = onSecondaryAction,
                    )
                }
            }
            if (hasActions && !inlineActions) {
                Box(modifier = Modifier.align(Alignment.End)) {
                    SnackbarActions(
                        colors = colors,
                        actionText = actionText,
                        onAction = onAction,
                        secondaryActionText = secondaryActionText,
                        onSecondaryAction = onSecondaryAction,
                        stacked = stackedActions,
                    )
                }
            }
        }
    }
}

@Composable
private fun SnackbarActions(
    colors: SnackbarColors,
    actionText: String?,
    onAction: (() -> Unit)?,
    secondaryActionText: String?,
    onSecondaryAction: (() -> Unit)?,
    stacked: Boolean = false,
) {
    if (stacked) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.End) {
            if (secondaryActionText != null) SnackbarAction(secondaryActionText, onSecondaryAction!!, colors, false)
            if (actionText != null) SnackbarAction(actionText, onAction!!, colors, true)
        }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (secondaryActionText != null) SnackbarAction(secondaryActionText, onSecondaryAction!!, colors, false)
            if (actionText != null) SnackbarAction(actionText, onAction!!, colors, true)
        }
    }
}

@Composable
private fun SnackbarAction(text: String, onClick: () -> Unit, colors: SnackbarColors, primary: Boolean) {
    val interactions = remember { MutableInteractionSource() }
    val foreground = if (primary) colors.actionText else colors.secondaryText
    Box(
        modifier = Modifier
            .widthIn(max = 120.dp)
            .defaultMinSize(minHeight = 48.dp)
            .clip(ActionShape)
            .then(if (primary) Modifier.background(colors.actionBackground) else Modifier)
            .clickable(
                interactionSource = interactions,
                indication = ripple(color = foreground),
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                color = foreground,
                fontFamily = SnackbarFont,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            ),
        )
    }
}

/**
 * Draws a timed snackbar over [content]. Use [alignment] for bottom-center, bottom-start,
 * bottom-end, or any custom position, and [offset] to fine-tune it. Null [durationMillis]
 * keeps the snackbar visible until the caller changes [visible]. Action callbacks are independent
 * of dismissal; update [visible] inside them when an action should close the snackbar.
 */
@Composable
fun AlpineSnackbarOverlay(
    visible: Boolean,
    message: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AlpineSnackbarVariant = AlpineSnackbarVariant.Default,
    icon: (@Composable (Color) -> Unit)? = null,
    showIcon: Boolean = icon != null || variant != AlpineSnackbarVariant.Default,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    secondaryActionText: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    alignment: Alignment = Alignment.BottomCenter,
    edgePadding: PaddingValues = PaddingValues(16.dp),
    offset: DpOffset = DpOffset.Zero,
    windowInsets: WindowInsets = WindowInsets.safeDrawing,
    durationMillis: Long? = 4_000L,
    content: @Composable BoxScope.() -> Unit,
) {
    require(durationMillis == null || durationMillis > 0) { "durationMillis must be positive or null" }
    val currentDismiss by rememberUpdatedState(onDismissRequest)
    LaunchedEffect(visible, message, durationMillis) {
        if (visible && durationMillis != null) {
            delay(durationMillis)
            currentDismiss()
        }
    }

    Box(modifier.fillMaxSize()) {
        content()
        Box(Modifier.matchParentSize().windowInsetsPadding(windowInsets)) {
            AnimatedVisibility(
                visible = visible,
                modifier = Modifier
                    .align(alignment)
                    .padding(edgePadding)
                    .offset(x = offset.x, y = offset.y),
                enter = fadeIn(tween(180)) + slideInVertically(tween(220)) { it / 2 },
                exit = fadeOut(tween(150)) + slideOutVertically(tween(180)) { it / 2 },
            ) {
                AlpineSnackbar(
                    message = message,
                    variant = variant,
                    showIcon = showIcon,
                    icon = icon,
                    actionText = actionText,
                    onAction = onAction,
                    secondaryActionText = secondaryActionText,
                    onSecondaryAction = onSecondaryAction,
                    darkTheme = darkTheme,
                )
            }
        }
    }
}

@Preview(name = "Snackbars light", showBackground = true, backgroundColor = 0xFFF5F8FF)
@Preview(name = "Snackbars dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, backgroundColor = 0xFF090D12)
@Composable
private fun AlpineSnackbarPreview() {
    val dark = isSystemInDarkTheme()
    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AlpineSnackbar("Änderungen gespeichert", darkTheme = dark)
        AlpineSnackbar(
            "Die neuen Einstellungen sind jetzt verfügbar.",
            variant = AlpineSnackbarVariant.Info,
            actionText = "Ansehen",
            onAction = {},
            darkTheme = dark,
        )
        AlpineSnackbar(
            "Die Verbindung ist unterbrochen. Bitte versuche es erneut.",
            variant = AlpineSnackbarVariant.Warning,
            actionText = "Erneut",
            onAction = {},
            secondaryActionText = "Später",
            onSecondaryAction = {},
            darkTheme = dark,
        )
    }
}
