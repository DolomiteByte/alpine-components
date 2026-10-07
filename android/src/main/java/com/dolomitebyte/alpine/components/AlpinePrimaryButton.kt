package com.dolomitebyte.alpine.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val GradientStart = Color(0xFF0A5BE3)
private val GradientEnd = Color(0xFF0054FF)
private val LightBorder = Color(0xFF074BD1)
private val LightHoverBorder = Color(0xFF003DC2)
private val DarkBorder = Color(0x736CAAFF)
private val DarkHoverBorder = Color(0xFF74B3FF)
private val AlpineCyan = Color(0xFF33D5EB)
private val ButtonShape = RoundedCornerShape(14.dp)
private val Manrope = FontFamily(Font(R.font.manrope_bold, FontWeight.Bold))

/**
 * DolomiteByte's primary action button, based on the button on dolomitebyte.com.
 *
 * [darkTheme] defaults to the device setting. Pass your app's current theme mode if
 * it offers a theme switch that differs from the system setting.
 */
@Composable
fun AlpinePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    darkTheme: Boolean = isSystemInDarkTheme(),
) {
    val interactions = remember { MutableInteractionSource() }
    val pressed by interactions.collectIsPressedAsState()
    val hovered by interactions.collectIsHoveredAsState()
    val focused by interactions.collectIsFocusedAsState()
    val activeHover = enabled && hovered && !pressed
    val border = when {
        darkTheme && activeHover -> DarkHoverBorder
        darkTheme -> DarkBorder
        activeHover -> LightHoverBorder
        else -> LightBorder
    }
    val elevation by animateDpAsState(
        targetValue = when {
            !enabled || pressed -> 0.dp
            activeHover -> if (darkTheme) 8.dp else 6.dp
            darkTheme -> 5.dp
            else -> 3.dp
        },
        label = "Alpine button shadow",
    )
    val arrowOffset by animateDpAsState(
        targetValue = when {
            !enabled -> 0.dp
            activeHover -> 2.dp
            pressed -> 1.dp
            else -> 0.dp
        },
        label = "Alpine arrow offset",
    )
    val focusColor = if (darkTheme) AlpineCyan else GradientEnd
    val outlineSize = with(LocalDensity.current) { 3.dp.toPx() }
    val outlineGap = with(LocalDensity.current) { 3.dp.toPx() }
    val outlineRadius = with(LocalDensity.current) { 18.dp.toPx() }

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 52.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .drawWithContent {
                drawContent()
                if (focused && enabled) {
                    val inset = outlineGap + outlineSize / 2f
                    drawRoundRect(
                        color = focusColor,
                        topLeft = Offset(-inset, -inset),
                        size = Size(size.width + inset * 2f, size.height + inset * 2f),
                        cornerRadius = CornerRadius(outlineRadius),
                        style = Stroke(outlineSize),
                    )
                }
            }
            .shadow(elevation, ButtonShape, clip = false)
            .clip(ButtonShape)
            .background(Brush.linearGradient(listOf(GradientStart, GradientEnd)))
            .border(1.dp, border, ButtonShape)
            .hoverable(interactionSource = interactions, enabled = enabled)
            .clickable(
                interactionSource = interactions,
                indication = LocalIndication.current,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 22.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(
                text = text,
                style = TextStyle(
                    color = Color.White,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.5.sp,
                    lineHeight = 21.7.sp,
                    letterSpacing = (-0.155).sp,
                    textAlign = TextAlign.Center,
                ),
            )
            AlpineArrow(Modifier.offset { IntOffset(arrowOffset.roundToPx(), 0) }.size(20.dp))
        }
    }
}

@Composable
private fun AlpineArrow(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val x = size.width / 24f
        val y = size.height / 24f
        val upper = Path().apply {
            moveTo(4f * x, 5f * y)
            lineTo(12f * x, 5f * y)
            lineTo(21f * x, 12f * y)
            lineTo(13f * x, 12f * y)
            close()
        }
        val lower = Path().apply {
            moveTo(13f * x, 12f * y)
            lineTo(21f * x, 12f * y)
            lineTo(12f * x, 19f * y)
            lineTo(4f * x, 19f * y)
            close()
        }
        drawPath(upper, AlpineCyan)
        drawPath(lower, Color.White)
    }
}

@Preview(name = "Light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF090D12,
)
@Composable
private fun AlpinePrimaryButtonPreview() {
    val dark = isSystemInDarkTheme()
    Box(Modifier.padding(24.dp)) {
        AlpinePrimaryButton(text = "Kontakt aufnehmen", onClick = {}, darkTheme = dark)
    }
}

@Preview(name = "Disabled", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AlpinePrimaryButtonDisabledPreview() {
    Box(Modifier.padding(24.dp)) {
        AlpinePrimaryButton(text = "Nicht verfügbar", onClick = {}, enabled = false)
    }
}
