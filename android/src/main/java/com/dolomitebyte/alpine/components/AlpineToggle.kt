package com.dolomitebyte.alpine.components

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

private val LightTrack = Color(0xF0FFFFFF)
private val DarkTrack = Color(0xFF112540)
private val LightBorder = Color(0x38132B50)
private val DarkBorder = Color(0xA633D5EB)
private val LightThumb = Color(0xFFB9DDF5)
private val DarkThumb = Color(0xFFF4F8FF)
private val LightThumbBorder = Color(0xFF668FB0)
private val Cobalt = Color(0xFF0054FF)
private val Navy = Color(0xFF132B50)
private val Cyan = Color(0xFF33D5EB)
private val LightLabel = Color(0xFF132B50)
private val DarkLabel = Color(0xFFF4F8FF)
private val LightSupporting = Color(0xBD132B50)
private val DarkSupporting = Color(0xC7E4EEFF)
private val Manrope = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
)

/**
 * A controlled settings switch inspired by the DolomiteByte theme control.
 *
 * The host owns [checked] and updates it in [onCheckedChange]. The entire row is a
 * 48 dp or taller switch target; [label] names it for accessibility services.
 */
@Composable
fun AlpineToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    darkTheme: Boolean = isSystemInDarkTheme(),
    supportingText: String? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val stateText = stringResource(if (checked) R.string.alpine_toggle_on else R.string.alpine_toggle_off)
    val accessibleLabel = if (supportingText.isNullOrBlank()) label else "$label. $supportingText"
    val labelColor = if (darkTheme) DarkLabel else LightLabel
    val supportingColor = if (darkTheme) DarkSupporting else LightSupporting

    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .semantics(mergeDescendants = true) {
                contentDescription = accessibleLabel
                stateDescription = stateText
            }
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null,
                onValueChange = onCheckedChange,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).clearAndSetSemantics {}) {
            BasicText(
                text = label,
                style = TextStyle(
                    color = labelColor,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
            )
            if (!supportingText.isNullOrBlank()) {
                BasicText(
                    text = supportingText,
                    style = TextStyle(
                        color = supportingColor,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                    ),
                )
            }
        }
        Spacer(Modifier.width(16.dp))
        AlpineToggleTrack(checked = checked, themeIcon = false, darkOnLeft = false, focused = focused)
    }
}

/**
 * DolomiteByte's sun/moon theme switch. Light mode places the sun on the right;
 * dark mode places the moon on the left, matching the site header.
 *
 * The host owns [darkMode] and applies the requested theme in [onDarkModeChange].
 */
@Composable
fun AlpineThemeToggle(
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showModeLabel: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val description = stringResource(R.string.alpine_theme_dark_mode)
    val modeLabel = stringResource(
        if (darkMode) R.string.alpine_theme_dark_mode else R.string.alpine_theme_light_mode,
    )
    val stateText = stringResource(if (darkMode) R.string.alpine_toggle_on else R.string.alpine_toggle_off)

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .semantics(mergeDescendants = true) {
                contentDescription = description
                stateDescription = stateText
            }
            .toggleable(
                value = darkMode,
                enabled = enabled,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null,
                onValueChange = onDarkModeChange,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showModeLabel) {
            BasicText(
                text = modeLabel,
                modifier = Modifier.clearAndSetSemantics {},
                style = TextStyle(
                    color = if (darkMode) DarkLabel else LightLabel,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
            )
            Spacer(Modifier.width(16.dp))
        }
        AlpineToggleTrack(checked = darkMode, themeIcon = true, darkOnLeft = true, focused = focused)
    }
}

@Composable
private fun AlpineToggleTrack(
    checked: Boolean,
    themeIcon: Boolean,
    darkOnLeft: Boolean,
    focused: Boolean,
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) DarkTrack else LightTrack,
        animationSpec = tween(200),
        label = "Alpine toggle track",
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) DarkBorder else LightBorder,
        animationSpec = tween(200),
        label = "Alpine toggle border",
    )
    val thumbColor by animateColorAsState(
        targetValue = if (checked) DarkThumb else LightThumb,
        animationSpec = tween(200),
        label = "Alpine toggle thumb",
    )
    val thumbBorderColor by animateColorAsState(
        targetValue = if (checked) Color.Transparent else LightThumbBorder,
        animationSpec = tween(200),
        label = "Alpine toggle thumb border",
    )
    val iconColor by animateColorAsState(
        targetValue = if (checked) Navy else Cobalt,
        animationSpec = tween(200),
        label = "Alpine toggle icon",
    )
    val thumbX by animateDpAsState(
        targetValue = if (checked == darkOnLeft) 4.dp else 28.dp,
        animationSpec = tween(200),
        label = "Alpine toggle position",
    )

    Box(
        modifier = Modifier.size(64.dp, 48.dp).clearAndSetSemantics {},
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp, 40.dp)
                .then(if (focused) Modifier.border(2.dp, if (checked) Cyan else Cobalt, CircleShape) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp, 32.dp)
                    .shadow(2.dp, CircleShape)
                    .clip(CircleShape)
                    .background(trackColor)
                    .border(1.dp, borderColor, CircleShape),
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = thumbX, top = 4.dp)
                        .size(24.dp)
                        .shadow(1.dp, CircleShape)
                        .clip(CircleShape)
                        .background(thumbColor)
                        .border(1.5.dp, thumbBorderColor, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (themeIcon) AlpineThemeIcon(dark = checked, color = iconColor)
                }
            }
        }
    }
}

@Composable
private fun AlpineThemeIcon(dark: Boolean, color: Color) {
    Canvas(Modifier.size(14.dp)) {
        val unit = size.minDimension / 14f
        if (dark) {
            val moon = Path().apply {
                moveTo(10.7f * unit, 1.5f * unit)
                cubicTo(9.5f * unit, 4.5f * unit, 10.4f * unit, 7.3f * unit, 12.7f * unit, 9f * unit)
                cubicTo(11.7f * unit, 11.6f * unit, 9.1f * unit, 12.9f * unit, 6.5f * unit, 12.2f * unit)
                cubicTo(3.5f * unit, 11.4f * unit, 1.7f * unit, 8.7f * unit, 2.4f * unit, 5.8f * unit)
                cubicTo(3f * unit, 3f * unit, 6.1f * unit, 1.2f * unit, 10.7f * unit, 1.5f * unit)
                close()
            }
            drawPath(moon, color)
        } else {
            val center = Offset(7f * unit, 7f * unit)
            val stroke = Stroke(width = 1.35f * unit, cap = StrokeCap.Round)
            drawCircle(color, radius = 2.25f * unit, center = center, style = stroke)
            for (step in 0 until 8) {
                val angle = step * Math.PI / 4
                val dx = cos(angle).toFloat()
                val dy = sin(angle).toFloat()
                drawLine(
                    color = color,
                    start = Offset(center.x + dx * 4.8f * unit, center.y + dy * 4.8f * unit),
                    end = Offset(center.x + dx * 6.1f * unit, center.y + dy * 6.1f * unit),
                    strokeWidth = 1.35f * unit,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

@Preview(name = "Toggles light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Preview(name = "Toggles dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, backgroundColor = 0xFF090D12)
@Composable
private fun AlpineTogglesPreview() {
    val dark = isSystemInDarkTheme()
    var notifications by remember { mutableStateOf(true) }
    var themeDark by remember { mutableStateOf(dark) }
    Column(
        modifier = Modifier
            .background(if (dark) Color(0xFF090D12) else Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        AlpineToggle(
            checked = notifications,
            onCheckedChange = { notifications = it },
            label = "Benachrichtigungen",
            supportingText = "Wichtige Updates erhalten",
            darkTheme = dark,
        )
        AlpineThemeToggle(
            darkMode = themeDark,
            onDarkModeChange = { themeDark = it },
            showModeLabel = true,
        )
        AlpineToggle(
            checked = false,
            onCheckedChange = {},
            label = "Deaktiviert",
            enabled = false,
            darkTheme = dark,
        )
    }
}
