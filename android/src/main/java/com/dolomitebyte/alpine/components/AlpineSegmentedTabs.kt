package com.dolomitebyte.alpine.components

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SegmentedTrackLight = Color(0xFFEDF3FA)
private val SegmentedTrackDark = Color(0xFF1A202A)
private val SegmentedInactiveLight = Color(0xFF526680)
private val SegmentedInactiveDark = Color(0xFFC0CDDD)
private val SegmentedBlueStart = Color(0xFF0A5BE3)
private val SegmentedBlueEnd = Color(0xFF0054FF)
private val SegmentedCyan = Color(0xFF33D5EB)
private val SegmentedShape = RoundedCornerShape(20.dp)
private val SegmentedItemShape = RoundedCornerShape(16.dp)
private val SegmentedFont = FontFamily(Font(R.font.manrope_semibold, FontWeight.SemiBold))

/** One text-only destination in [AlpineSegmentedTabs]. */
data class AlpineSegmentedTabItem(
    val label: String,
    val enabled: Boolean = true,
)

/**
 * A compact segmented tab bar for switching between peer views or time ranges.
 *
 * The host owns [selectedIndex] and renders the corresponding content. All segments
 * keep equal widths while the Alpine blue pill slides beneath the selected label.
 * The bar has no individual segment outlines and does not apply system bar insets.
 */
@Composable
fun AlpineSegmentedTabs(
    items: List<AlpineSegmentedTabItem>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme(),
    containerColor: Color? = null,
) {
    require(items.isNotEmpty()) { "AlpineSegmentedTabs needs at least one item" }
    require(selectedIndex in items.indices) { "selectedIndex must refer to an item" }
    require(items.all { it.label.isNotBlank() }) { "Segmented tab labels must not be blank" }

    val track = containerColor ?: if (darkTheme) SegmentedTrackDark else SegmentedTrackLight
    val inactive = if (darkTheme) SegmentedInactiveDark else SegmentedInactiveLight
    val indicatorPosition by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "Alpine segmented tab position",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(SegmentedShape)
            .background(track)
            .drawBehind {
                val inset = 4.dp.toPx()
                val segmentWidth = (size.width - inset * 2f) / items.size
                val left = inset + segmentWidth * indicatorPosition
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(SegmentedBlueStart, SegmentedBlueEnd),
                        startX = left,
                        endX = left + segmentWidth,
                    ),
                    topLeft = Offset(left, inset),
                    size = Size(segmentWidth, size.height - inset * 2f),
                    cornerRadius = CornerRadius(16.dp.toPx()),
                )
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, item ->
                val selected = index == selectedIndex
                val interactions = remember { MutableInteractionSource() }
                val focused by interactions.collectIsFocusedAsState()
                val textColor by animateColorAsState(
                    targetValue = if (selected) Color.White else inactive,
                    label = "Alpine segmented tab text",
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .alpha(if (item.enabled) 1f else 0.45f)
                        .clip(SegmentedItemShape)
                        .then(
                            if (focused) Modifier.border(
                                width = 2.dp,
                                color = if (darkTheme) SegmentedCyan else SegmentedBlueEnd,
                                shape = SegmentedItemShape,
                            ) else Modifier,
                        )
                        .semantics { contentDescription = item.label }
                        .selectable(
                            selected = selected,
                            enabled = item.enabled,
                            role = Role.Tab,
                            interactionSource = interactions,
                            indication = ripple(color = if (selected) Color.White else SegmentedBlueEnd),
                            onClick = { onTabSelected(index) },
                        )
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        text = item.label,
                        modifier = Modifier.clearAndSetSemantics {},
                        style = TextStyle(
                            color = textColor,
                            fontFamily = SegmentedFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
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

@Preview(name = "Segmented tabs light", showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 360)
@Preview(
    name = "Segmented tabs dark",
    showBackground = true,
    backgroundColor = 0xFF090D12,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 360,
)
@Composable
private fun AlpineSegmentedTabsPreview() {
    val dark = isSystemInDarkTheme()
    Box(Modifier.background(if (dark) Color(0xFF090D12) else Color.White).padding(12.dp)) {
        AlpineSegmentedTabs(
            items = listOf("7 T", "30 T", "3 M", "6 M").map(::AlpineSegmentedTabItem),
            selectedIndex = 2,
            onTabSelected = {},
            darkTheme = dark,
        )
    }
}
