package com.dolomitebyte.alpine.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun AlpineButtonContent(
    text: String,
    color: Color,
    fontFamily: FontFamily,
    fontWeight: FontWeight,
    leadingIcon: (@Composable (Color) -> Unit)?,
    trailingIcon: (@Composable (Color) -> Unit)?,
    loading: Boolean,
    defaultTrailingIcon: (@Composable () -> Unit)? = null,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!loading && leadingIcon != null) {
            Box(Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                leadingIcon(color)
            }
        }
        BasicText(
            text = text,
            style = TextStyle(
                color = color,
                fontFamily = fontFamily,
                fontWeight = fontWeight,
                fontSize = 15.5.sp,
                lineHeight = 21.7.sp,
                letterSpacing = (-0.155).sp,
                textAlign = TextAlign.Center,
            ),
        )
        when {
            loading -> AlpineButtonLoadingIndicator(color)
            trailingIcon != null -> Box(Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                trailingIcon(color)
            }
            defaultTrailingIcon != null -> defaultTrailingIcon()
        }
    }
}

@Composable
internal fun AlpineButtonLoadingIndicator(color: Color) {
    val rotation by rememberInfiniteTransition(label = "Alpine loading").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 900, easing = LinearEasing)),
        label = "Alpine loading rotation",
    )
    Canvas(Modifier.size(20.dp)) {
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        drawArc(color.copy(alpha = 0.28f), 0f, 360f, false, style = stroke)
        drawArc(color, rotation - 90f, 250f, false, style = stroke)
    }
}
