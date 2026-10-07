package com.dolomitebyte.alpine.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val GradientStart = Color(0xFF0A5BE3)
private val GradientEnd = Color(0xFF0054FF)
private val LightSurface = Color.White
private val DarkSurface = Color(0xFF181D23)
private val LightBorder = Color(0xFFD2DAE5)
private val DarkBorder = Color(0xFF39414B)
private val LightInactive = Color(0xFF64748B)
private val DarkInactive = Color(0xFFAAB8C8)
private val LightSelected = Color(0xFF0054FF)
private val DarkSelected = Color(0xFF6DE1EF)
private val IconShape = RoundedCornerShape(10.dp)
private val TabShape = RoundedCornerShape(12.dp)
private val Manrope = FontFamily(Font(R.font.manrope_semibold, FontWeight.SemiBold))

/** One destination in [AlpineBottomTabs]. The label describes its decorative icon. */
data class AlpineBottomTabItem(
    val label: String,
    val icon: @Composable (Color) -> Unit,
    val enabled: Boolean = true,
)

/**
 * A compact Alpine bottom navigation bar for persistent app destinations.
 *
 * The caller owns [selectedIndex] and performs navigation in [onTabSelected].
 * Three to five destinations are recommended. Icons receive the current tint;
 * their semantics are hidden because [AlpineBottomTabItem.label] names the tab.
 * [windowInsets] defaults to the Android navigation bar inset and can be overridden
 * when a parent already applies it.
 */
@Composable
fun AlpineBottomTabs(
    items: List<AlpineBottomTabItem>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme(),
    windowInsets: WindowInsets = WindowInsets.navigationBars,
) {
    require(items.isNotEmpty()) { "AlpineBottomTabs needs at least one item" }
    require(selectedIndex in items.indices) { "selectedIndex must refer to an item" }

    val surface = if (darkTheme) DarkSurface else LightSurface
    val border = if (darkTheme) DarkBorder else LightBorder
    val inactive = if (darkTheme) DarkInactive else LightInactive
    val selected = if (darkTheme) DarkSelected else LightSelected

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp)
            .background(surface)
            .windowInsetsPadding(windowInsets),
    ) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(border))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex
                val labelColor = if (isSelected) selected else inactive
                val iconColor = if (isSelected) Color.White else inactive
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 64.dp)
                        .alpha(if (item.enabled) 1f else 0.45f)
                        .clip(TabShape)
                        .selectable(
                            selected = isSelected,
                            enabled = item.enabled,
                            role = Role.Tab,
                            onClick = { onTabSelected(index) },
                        )
                        .padding(horizontal = 2.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    val iconModifier = Modifier
                        .size(width = 38.dp, height = 32.dp)
                        .clip(IconShape)
                        .then(
                            if (isSelected) Modifier.background(
                                Brush.linearGradient(listOf(GradientStart, GradientEnd)),
                            ) else Modifier,
                        )
                    Box(iconModifier, contentAlignment = Alignment.Center) {
                        Box(
                            Modifier.size(20.dp).clearAndSetSemantics {},
                            contentAlignment = Alignment.Center,
                        ) {
                            item.icon(iconColor)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    BasicText(
                        text = item.label,
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            color = labelColor,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            textAlign = TextAlign.Center,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
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
        )
    }
}
