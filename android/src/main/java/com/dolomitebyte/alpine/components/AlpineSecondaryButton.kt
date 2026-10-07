package com.dolomitebyte.alpine.components

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LightBackground = Color.White
private val LightHoverBackground = Color(0xFFF5F8FF)
private val LightBorder = Color(0xFFD2DAE5)
private val LightHoverBorder = Color(0xFF8CAFF5)
private val LightText = Color(0xFF24344B)
private val LightHoverText = Color(0xFF0054FF)
private val DarkBackground = Color(0xFF181D23)
private val DarkHoverBackground = Color(0xFF202932)
private val DarkBorder = Color(0xFF39414B)
private val DarkHoverBorder = Color(0xFF5D849A)
private val DarkText = Color(0xFFEDF3FB)
private val DarkHoverText = Color(0xFF6DE1EF)
private val ButtonShape = RoundedCornerShape(14.dp)
private val Manrope = FontFamily(Font(R.font.manrope_semibold, FontWeight.SemiBold))

/**
 * DolomiteByte's secondary action button, based on the button on dolomitebyte.com.
 *
 * [darkTheme] defaults to the device setting. Pass your app's current theme mode if
 * it offers a theme switch that differs from the system setting.
 */
@Composable
fun AlpineSecondaryButton(
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

    val targetBackground = when {
        darkTheme && activeHover -> DarkHoverBackground
        darkTheme -> DarkBackground
        activeHover -> LightHoverBackground
        else -> LightBackground
    }
    val targetBorder = when {
        darkTheme && activeHover -> DarkHoverBorder
        darkTheme -> DarkBorder
        activeHover -> LightHoverBorder
        else -> LightBorder
    }
    val targetText = when {
        darkTheme && activeHover -> DarkHoverText
        darkTheme -> DarkText
        activeHover -> LightHoverText
        else -> LightText
    }
    val background by animateColorAsState(targetBackground, tween(180), label = "Alpine secondary background")
    val border by animateColorAsState(targetBorder, tween(180), label = "Alpine secondary border")
    val textColor by animateColorAsState(targetText, tween(180), label = "Alpine secondary text")
    val elevation by animateDpAsState(
        targetValue = if (!enabled || pressed) 0.dp else if (darkTheme) 2.dp else 1.dp,
        label = "Alpine secondary shadow",
    )
    val hoverOffset by animateDpAsState(
        targetValue = if (activeHover) (-1).dp else 0.dp,
        label = "Alpine secondary hover offset",
    )
    val focusColor = if (darkTheme) Color(0xFF33D5EB) else Color(0xFF0054FF)
    val outlineSize = with(LocalDensity.current) { 3.dp.toPx() }
    val outlineGap = with(LocalDensity.current) { 3.dp.toPx() }
    val outlineRadius = with(LocalDensity.current) { 18.dp.toPx() }

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 52.dp)
            .offset { IntOffset(0, hoverOffset.roundToPx()) }
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
            .background(background)
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
        BasicText(
            text = text,
            style = TextStyle(
                color = textColor,
                fontFamily = Manrope,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.5.sp,
                lineHeight = 21.7.sp,
                letterSpacing = (-0.155).sp,
                textAlign = TextAlign.Center,
            ),
        )
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
private fun AlpineSecondaryButtonPreview() {
    val dark = isSystemInDarkTheme()
    Box(Modifier.padding(24.dp)) {
        AlpineSecondaryButton(text = "Mehr erfahren", onClick = {}, darkTheme = dark)
    }
}

@Preview(name = "Disabled", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AlpineSecondaryButtonDisabledPreview() {
    Box(Modifier.padding(24.dp)) {
        AlpineSecondaryButton(text = "Nicht verfügbar", onClick = {}, enabled = false)
    }
}
