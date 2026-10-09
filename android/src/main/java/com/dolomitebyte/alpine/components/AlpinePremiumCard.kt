package com.dolomitebyte.alpine.components

import android.content.res.Configuration
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val PremiumCardShape = RoundedCornerShape(24.dp)
private val PremiumActionShape = RoundedCornerShape(13.dp)
private val PremiumBlueStart = Color(0xFF0A5BE3)
private val PremiumBlueEnd = Color(0xFF0054FF)
private val PremiumCyan = Color(0xFF33D5EB)
private val PremiumCyanLight = Color(0xFFC8F7FF)
private val PremiumActionInk = Color(0xFF074BD1)
private val PremiumLightBorder = Color(0xFF074BD1)
private val PremiumDarkBorder = Color(0x736CAAFF)
private val PremiumFont = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold),
)

/**
 * A compact upgrade card based on TheraBuddy's drawer card, using the Alpine palette.
 *
 * The entire card is one action. Its visual call to action shares [onClick] and does not
 * create a second touch target. [icon] receives the Alpine cyan tint in a translucent circle;
 * when omitted, a transparent star is cut out of a white circle. [darkTheme] controls the border
 * and shadow while retaining the brand gradient.
 * [animateGradient] slowly shifts the background; pass false for a static surface.
 */
@Composable
fun AlpinePremiumCard(
    title: String,
    description: String,
    actionText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    benefits: List<String> = emptyList(),
    icon: (@Composable (Color) -> Unit)? = null,
    enabled: Boolean = true,
    darkTheme: Boolean = isSystemInDarkTheme(),
    animateGradient: Boolean = true,
) {
    require(title.isNotBlank()) { "AlpinePremiumCard needs a title" }
    require(actionText.isNotBlank()) { "AlpinePremiumCard needs actionText" }

    val visibleBenefits = benefits.filter(String::isNotBlank)
    val interactions = remember { MutableInteractionSource() }
    val pressed by interactions.collectIsPressedAsState()
    val hovered by interactions.collectIsHoveredAsState()
    val focused by interactions.collectIsFocusedAsState()
    val gradientDrift = if (animateGradient && enabled) {
        rememberInfiniteTransition(label = "Alpine premium gradient").animateFloat(
            initialValue = -0.08f,
            targetValue = 0.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 20_000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "Alpine premium gradient drift",
        )
    } else {
        null
    }
    val elevation by animateDpAsState(
        targetValue = when {
            !enabled || pressed -> 0.dp
            hovered -> if (darkTheme) 16.dp else 12.dp
            darkTheme -> 12.dp
            else -> 8.dp
        },
        label = "Alpine premium card shadow",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.5f)
            .shadow(elevation, PremiumCardShape)
            .clip(PremiumCardShape)
            .drawBehind {
                val drift = gradientDrift?.value ?: 0f
                drawRect(
                    brush = Brush.linearGradient(
                        0f to PremiumBlueStart,
                        0.62f to PremiumBlueEnd,
                        1f to PremiumCyan,
                        start = Offset(size.width * drift, 0f),
                        end = Offset(size.width * (1f + drift), size.height),
                    ),
                )
            }
            .border(
                if (focused && enabled) 2.dp else 1.dp,
                if (focused && enabled) PremiumCyan else if (darkTheme) PremiumDarkBorder else PremiumLightBorder,
                PremiumCardShape,
            )
            .hoverable(interactionSource = interactions, enabled = enabled)
            .clickable(
                interactionSource = interactions,
                indication = ripple(color = Color.White),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(18.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.16f))
                            .clearAndSetSemantics {},
                        contentAlignment = Alignment.Center,
                    ) {
                        icon(PremiumCyan)
                    }
                } else {
                    PremiumStar(Modifier.size(44.dp).clearAndSetSemantics {})
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    eyebrow?.takeIf(String::isNotBlank)?.let {
                        BasicText(
                            text = it,
                            style = TextStyle(
                                color = PremiumCyanLight,
                                fontFamily = PremiumFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                            ),
                        )
                    }
                    BasicText(
                        text = title,
                        style = TextStyle(
                            color = Color.White,
                            fontFamily = PremiumFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            lineHeight = 23.sp,
                        ),
                    )
                }
            }
            if (description.isNotBlank()) {
                BasicText(
                    text = description,
                    style = TextStyle(
                        color = Color.White.copy(alpha = 0.91f),
                        fontFamily = PremiumFont,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                    ),
                )
            }
            if (visibleBenefits.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    visibleBenefits.forEach { benefit ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            PremiumCheck(Modifier.size(17.dp).clearAndSetSemantics {})
                            BasicText(
                                text = benefit,
                                style = TextStyle(
                                    color = Color.White.copy(alpha = 0.95f),
                                    fontFamily = PremiumFont,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.5.sp,
                                    lineHeight = 18.sp,
                                ),
                            )
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
                    .clip(PremiumActionShape)
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicText(
                        text = actionText,
                        style = TextStyle(
                            color = PremiumActionInk,
                            fontFamily = PremiumFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            lineHeight = 20.sp,
                        ),
                    )
                    PremiumActionArrow(Modifier.size(16.dp).clearAndSetSemantics {})
                }
            }
        }
    }
}

@Composable
private fun PremiumStar(modifier: Modifier = Modifier) {
    Canvas(modifier.graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }) {
        val outer = size.minDimension * 0.25f
        val inner = outer * 0.47f
        val cx = size.width / 2f
        val cy = size.height / 2f
        val path = Path().apply {
            repeat(10) { point ->
                val angle = -PI / 2.0 + point * PI / 5.0
                val radius = if (point % 2 == 0) outer else inner
                val x = cx + (cos(angle) * radius).toFloat()
                val y = cy + (sin(angle) * radius).toFloat()
                if (point == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawCircle(Color.White, radius = size.minDimension / 2f)
        drawPath(path, Color.Transparent, blendMode = BlendMode.Clear)
    }
}

@Composable
private fun PremiumCheck(modifier: Modifier = Modifier) {
    // Confine Clear to the icon layer so the card gradient shows through the check.
    Canvas(modifier.graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }) {
        val stroke = 2.dp.toPx()
        drawCircle(Color.White, radius = size.minDimension / 2f)
        drawLine(
            Color.Transparent,
            Offset(size.width * 0.25f, size.height * 0.52f),
            Offset(size.width * 0.43f, size.height * 0.68f),
            stroke,
            cap = StrokeCap.Round,
            blendMode = BlendMode.Clear,
        )
        drawLine(
            Color.Transparent,
            Offset(size.width * 0.43f, size.height * 0.68f),
            Offset(size.width * 0.76f, size.height * 0.33f),
            stroke,
            cap = StrokeCap.Round,
            blendMode = BlendMode.Clear,
        )
    }
}

@Composable
private fun PremiumActionArrow(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = 2.dp.toPx()
        val mid = size.height / 2f
        drawLine(PremiumActionInk, Offset(size.width * 0.12f, mid), Offset(size.width * 0.86f, mid), stroke, cap = StrokeCap.Round)
        drawLine(PremiumActionInk, Offset(size.width * 0.58f, size.height * 0.23f), Offset(size.width * 0.86f, mid), stroke, cap = StrokeCap.Round)
        drawLine(PremiumActionInk, Offset(size.width * 0.58f, size.height * 0.77f), Offset(size.width * 0.86f, mid), stroke, cap = StrokeCap.Round)
    }
}

@Preview(name = "Premium card light", showBackground = true, backgroundColor = 0xFFF5F8FF, widthDp = 340)
@Preview(
    name = "Premium card dark",
    showBackground = true,
    backgroundColor = 0xFF090D12,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 340,
)
@Composable
private fun AlpinePremiumCardPreview() {
    val dark = isSystemInDarkTheme()
    Box(Modifier.padding(16.dp)) {
        AlpinePremiumCard(
            title = "TheraBuddy Premium freischalten",
            description = "Sieben Tage kostenlos testen: ohne Werbung und mit unbegrenzten Dokumentfunktionen.",
            actionText = "Premium ansehen",
            onClick = {},
            eyebrow = "7 Tage kostenlos",
            benefits = listOf("Ruhige Nutzung ohne Werbung", "Unbegrenzte Dokumentfunktionen"),
            darkTheme = dark,
        )
    }
}
