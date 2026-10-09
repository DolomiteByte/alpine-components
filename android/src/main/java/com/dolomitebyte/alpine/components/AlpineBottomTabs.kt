package com.dolomitebyte.alpine.components

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val GradientStart = Color(0xFF0A5BE3)
private val GradientEnd = Color(0xFF0054FF)
private val LightSurface = Color.White
private val DarkSurface = Color(0xFF181D23)
private val LightInactive = Color(0xFF64748B)
private val DarkInactive = Color(0xFFAAB8C8)
private val Manrope = FontFamily(Font(R.font.manrope_semibold, FontWeight.SemiBold))

/** One destination in [AlpineBottomTabs]. The label describes its decorative icon. */
data class AlpineBottomTabItem(
    val label: String,
    val icon: @Composable (Color) -> Unit,
    val enabled: Boolean = true,
)

/**
 * An Alpine bottom navigation bar with the expanding pill animation used by TheraBuddy.
 *
 * The caller owns [selectedIndex] and performs navigation in [onTabSelected].
 * Three to five destinations are recommended. The selected tab expands to show its
 * label; inactive tabs show only icons. Icons receive the current tint and their
 * semantics are hidden because [AlpineBottomTabItem.label] names the tab.
 * [windowInsets] defaults to the Android navigation bar inset and can be overridden
 * when a parent already applies it. [containerColor] overrides the bar background
 * so host apps can match their own light and dark surfaces.
 */
@Composable
fun AlpineBottomTabs(
    items: List<AlpineBottomTabItem>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme(),
    windowInsets: WindowInsets = WindowInsets.navigationBars,
    containerColor: Color? = null,
) {
    require(items.isNotEmpty()) { "AlpineBottomTabs needs at least one item" }
    require(selectedIndex in items.indices) { "selectedIndex must refer to an item" }

    val surface = containerColor ?: if (darkTheme) DarkSurface else LightSurface
    val inactive = if (darkTheme) DarkInactive else LightInactive

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(surface)
            .windowInsetsPadding(windowInsets),
    ) {
        // Keep the pill inside the bar even with five tabs on a 320 dp screen.
        val maxSelectedWidth = (maxWidth - 16.dp - 48.dp * (items.size - 1))
            .coerceAtLeast(64.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .selectableGroup()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex
                val iconColor = if (isSelected) Color.White else inactive
                val interactions = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .then(if (isSelected) Modifier.widthIn(max = maxSelectedWidth) else Modifier)
                        .alpha(if (item.enabled) 1f else 0.45f)
                        .semantics { contentDescription = item.label }
                        .clip(CircleShape)
                        .then(
                            if (isSelected) Modifier.background(
                                Brush.linearGradient(listOf(GradientStart, GradientEnd)),
                            ) else Modifier,
                        )
                        .selectable(
                            selected = isSelected,
                            enabled = item.enabled,
                            role = Role.Tab,
                            interactionSource = interactions,
                            indication = ripple(
                                color = when {
                                    isSelected -> Color.White
                                    darkTheme -> Color(0xFF33D5EB)
                                    else -> GradientEnd
                                },
                            ),
                            onClick = { onTabSelected(index) },
                        )
                        .padding(horizontal = if (isSelected) 16.dp else 12.dp, vertical = 10.dp)
                        .animateContentSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Box(
                            Modifier.size(24.dp).clearAndSetSemantics {},
                            contentAlignment = Alignment.Center,
                        ) {
                            item.icon(iconColor)
                        }
                        if (isSelected) {
                            Spacer(Modifier.width(8.dp))
                            BasicText(
                                text = item.label,
                                style = TextStyle(
                                    color = Color.White,
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Bottom tabs light", showBackground = true, backgroundColor = 0xFFF5F8FF)
@Preview(
    name = "Bottom tabs dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF090D12,
)
@Composable
private fun AlpineBottomTabsPreview() {
    val dark = isSystemInDarkTheme()
    val items = listOf(
        AlpineBottomTabItem("Start", { color -> BasicText("⌂", style = TextStyle(color = color, fontSize = 20.sp)) }),
        AlpineBottomTabItem("Entdecken", { color -> BasicText("⌕", style = TextStyle(color = color, fontSize = 20.sp)) }),
        AlpineBottomTabItem("Profil", { color -> BasicText("○", style = TextStyle(color = color, fontSize = 20.sp)) }),
    )
    Box(
        Modifier
            .background(if (dark) Color(0xFF090D12) else Color(0xFFF5F8FF))
            .padding(top = 64.dp),
    ) {
        AlpineBottomTabs(
            items = items,
            selectedIndex = 0,
            onTabSelected = {},
            darkTheme = dark,
            containerColor = if (dark) Color.Black else Color.White,
        )
    }
}
