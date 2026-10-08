package com.dolomitebyte.alpine.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val GradientStart = Color(0xFF0A5BE3)
private val GradientEnd = Color(0xFF0054FF)
private val LightBorder = Color(0xFF074BD1)
private val DarkBorder = Color(0x736CAAFF)
private val LightFocus = Color(0xFF0054FF)
private val DarkFocus = Color(0xFF33D5EB)
private val Manrope = FontFamily(Font(R.font.manrope_bold, FontWeight.Bold))

/**
 * A floating Alpine action with an icon, text, or both.
 *
 * Pass [contentDescription] for icon-only actions. The icon receives the white
 * content tint and is decorative to accessibility services. [loading] blocks
 * clicks and replaces the icon (or adds a spinner after text).
 */
@Composable
fun AlpineFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: (@Composable (Color) -> Unit)? = null,
    contentDescription: String? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    darkTheme: Boolean = isSystemInDarkTheme(),
) {
    val label = text?.takeIf { it.isNotBlank() }
    require(label != null || icon != null) { "AlpineFab needs text or an icon" }
    val accessibleLabel = contentDescription?.takeIf { it.isNotBlank() } ?: label
    require(accessibleLabel != null) { "Icon-only AlpineFab needs a contentDescription" }

    val compact = label == null
    val shape = if (compact) CircleShape else RoundedCornerShape(18.dp)
    val interactive = enabled && !loading
    val interactions = remember { MutableInteractionSource() }
    val pressed by interactions.collectIsPressedAsState()
    val hovered by interactions.collectIsHoveredAsState()
    val focused by interactions.collectIsFocusedAsState()
    val elevation by animateDpAsState(
        targetValue = when {
            !interactive -> 0.dp
            pressed -> 3.dp
            hovered -> 12.dp
            darkTheme -> 10.dp
            else -> 8.dp
        },
        label = "Alpine FAB shadow",
    )
    val loadingDescription = stringResource(R.string.alpine_button_loading)
    val focusColor = if (darkTheme) DarkFocus else LightFocus

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = if (compact) 56.dp else 0.dp, minHeight = 56.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .drawWithContent {
                drawContent()
                if (focused && interactive) {
                    val gap = 3.dp.toPx()
                    val stroke = 3.dp.toPx()
                    val inset = gap + stroke / 2f
                    drawRoundRect(
                        color = focusColor,
                        topLeft = Offset(-inset, -inset),
                        size = Size(size.width + inset * 2f, size.height + inset * 2f),
                        cornerRadius = CornerRadius(if (compact) size.height / 2f + inset else 21.dp.toPx()),
                        style = Stroke(stroke),
                    )
                }
            }
            .shadow(elevation, shape, clip = false)
            .clip(shape)
            .background(Brush.linearGradient(listOf(GradientStart, GradientEnd)))
            .border(1.dp, if (darkTheme) DarkBorder else LightBorder, shape)
            .hoverable(interactionSource = interactions, enabled = interactive)
            .semantics(mergeDescendants = true) {
                this.contentDescription = accessibleLabel
                if (loading) stateDescription = loadingDescription
            }
            .clickable(
                interactionSource = interactions,
                indication = ripple(color = Color.White),
                enabled = interactive,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = if (compact) 16.dp else 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clearAndSetSemantics {},
        ) {
            when {
                loading && icon != null -> AlpineButtonLoadingIndicator(Color.White)
                icon != null -> Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                    icon(Color.White)
                }
            }
            if (label != null) {
                BasicText(
                    text = label,
                    style = TextStyle(
                        color = Color.White,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.5.sp,
                        lineHeight = 24.sp,
                        letterSpacing = (-0.155).sp,
                    ),
                )
                if (loading && icon == null) AlpineButtonLoadingIndicator(Color.White)
            }
        }
    }
}

/**
 * Draws [content] and floats [fab] above it. [alignment] supports bottom-end,
 * bottom-start, or any custom [Alignment]. [offset] fine-tunes the chosen position.
 * Safe drawing insets are applied to the FAB layer by default; set [windowInsets]
 * to zero when a parent has already handled them.
 */
@Composable
fun AlpineFabOverlay(
    fab: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.BottomEnd,
    edgePadding: PaddingValues = PaddingValues(16.dp),
    offset: DpOffset = DpOffset.Zero,
    windowInsets: WindowInsets = WindowInsets.safeDrawing,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier.fillMaxSize()) {
        content()
        Box(Modifier.matchParentSize().windowInsetsPadding(windowInsets)) {
            Box(
                modifier = Modifier
                    .align(alignment)
                    .padding(edgePadding)
                    .offset(x = offset.x, y = offset.y),
            ) {
                fab()
            }
        }
    }
}

@Preview(name = "FABs light", showBackground = true, backgroundColor = 0xFFF5F8FF)
@Preview(name = "FABs dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, backgroundColor = 0xFF090D12)
@Composable
private fun AlpineFabPreview() {
    val dark = isSystemInDarkTheme()
    Box(
        Modifier
            .background(if (dark) Color(0xFF090D12) else Color(0xFFF5F8FF))
            .padding(24.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AlpineFab(
                onClick = {},
                icon = { tint -> BasicText("+", style = TextStyle(color = tint, fontSize = 24.sp)) },
                contentDescription = "Hinzufügen",
                darkTheme = dark,
            )
            AlpineFab(onClick = {}, text = "Erstellen", darkTheme = dark)
            AlpineFab(
                onClick = {},
                text = "Erstellen",
                icon = { tint -> BasicText("+", style = TextStyle(color = tint, fontSize = 24.sp)) },
                darkTheme = dark,
            )
        }
    }
}
