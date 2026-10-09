package com.dolomitebyte.alpine.components

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ChoiceBlue = Color(0xFF0054FF)
private val ChoiceDarkAccent = Color(0xFF8AB8FF)
private val ChoiceDarkInputAccent = Color(0xFF33D5EB)
private val ChoiceLightInk = Color(0xFF132B50)
private val ChoiceDarkInk = Color(0xFFF4F8FF)
private val ChoiceLightMuted = Color(0xFF60728B)
private val ChoiceDarkMuted = Color(0xFFAAB8C8)
private val ChoiceLightSurface = Color.White
private val ChoiceDarkSurface = Color(0xFF090D12)
private val ChoiceLightCard = Color(0xFFF2F5F9)
private val ChoiceDarkCard = Color(0xFF1B1D22)
private val ChoiceLightSelectedStart = Color(0xFFDCEEFF)
private val ChoiceLightSelectedEnd = Color(0xFFC8E1FF)
private val ChoiceDarkSelectedStart = Color(0xFF2B67B3)
private val ChoiceDarkSelectedEnd = Color(0xFF184477)
private val ChoiceDarkSelectedMuted = Color(0xFFE4F1FF)
private val ChoiceLightOutline = Color(0xFF8A94A4)
private val ChoiceDarkOutline = Color(0xFF868D98)
private val ChoiceLightError = Color(0xFFB42318)
private val ChoiceDarkError = Color(0xFFFF8A80)
private val DropdownShape = RoundedCornerShape(12.dp)
private val ChoiceCardShape = RoundedCornerShape(24.dp)
private val ChoiceFont = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold),
)

/** A stable option for [AlpineDropdownField]. IDs must be unique within a dropdown. */
data class AlpineDropdownOption(
    val id: String,
    val label: String,
    val supportingText: String? = null,
    val enabled: Boolean = true,
)

/**
 * An outlined, controlled dropdown. Tapping it opens Android's Material 3 modal bottom sheet.
 * The host owns [selectedId] and updates it in [onSelected]. Back, swipe, or an outside tap
 * dismisses the sheet without changing the selection. [containerColor] colors both the field
 * and the sheet so unselected option cards remain distinct in dark mode. Supply both
 * [primaryActionLabel] and [onPrimaryActionClick] to place a primary button before the options;
 * tapping it closes the sheet without changing the selection.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlpineDropdownField(
    label: String,
    options: List<AlpineDropdownOption>,
    selectedId: String?,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    containerColor: Color? = null,
    primaryActionLabel: String? = null,
    onPrimaryActionClick: (() -> Unit)? = null,
) {
    require(label.isNotBlank()) { "AlpineDropdownField needs a label" }
    require(options.all { it.id.isNotBlank() && it.label.isNotBlank() }) { "Dropdown options need an ID and label" }
    require(options.map { it.id }.distinct().size == options.size) { "Dropdown option IDs must be unique" }
    require((primaryActionLabel == null) == (onPrimaryActionClick == null)) {
        "A dropdown primary action needs both a label and a click handler"
    }
    require(primaryActionLabel == null || primaryActionLabel.isNotBlank()) {
        "A dropdown primary action needs a non-blank label"
    }

    var sheetVisible by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val interactions = remember { MutableInteractionSource() }
    val focused by interactions.collectIsFocusedAsState()
    val surface = containerColor ?: if (darkTheme) ChoiceDarkSurface else ChoiceLightSurface
    val ink = if (darkTheme) ChoiceDarkInk else ChoiceLightInk
    val muted = if (darkTheme) ChoiceDarkMuted else ChoiceLightMuted
    val accent = if (darkTheme) ChoiceDarkAccent else ChoiceBlue
    val errorColor = if (darkTheme) ChoiceDarkError else ChoiceLightError
    val selectedLabel = options.firstOrNull { it.id == selectedId }?.label
    val displayValue = selectedLabel ?: placeholder ?: stringResource(R.string.alpine_dropdown_placeholder)
    val invalidDescription = stringResource(R.string.alpine_input_invalid)
    val outline by animateColorAsState(
        targetValue = when {
            isError -> errorColor
            focused || sheetVisible -> accent
            darkTheme -> ChoiceDarkOutline
            else -> ChoiceLightOutline
        },
        animationSpec = tween(180),
        label = "Alpine dropdown outline",
    )

    LaunchedEffect(enabled) { if (!enabled) sheetVisible = false }

    Column(modifier = modifier) {
        Box(Modifier.fillMaxWidth().height(64.dp).alpha(if (enabled) 1f else 0.5f)) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(DropdownShape)
                    .background(surface)
                    .border(if (focused || sheetVisible || isError) 2.dp else 1.dp, outline, DropdownShape)
                    .semantics(mergeDescendants = true) {
                        contentDescription = label
                        stateDescription = displayValue
                        if (isError) error(errorMessage?.takeIf(String::isNotBlank) ?: invalidDescription)
                    }
                    .clickable(
                        interactionSource = interactions,
                        indication = ripple(color = accent),
                        enabled = enabled,
                        role = Role.Button,
                        onClick = { sheetVisible = true },
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicText(
                    text = displayValue,
                    modifier = Modifier.weight(1f).clearAndSetSemantics {},
                    style = TextStyle(
                        color = if (selectedLabel == null) muted else ink,
                        fontFamily = ChoiceFont,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.width(12.dp))
                Canvas(Modifier.size(18.dp).clearAndSetSemantics {}) {
                    val stroke = 1.8.dp.toPx()
                    drawLine(
                        muted,
                        Offset(size.width * 0.27f, size.height * 0.42f),
                        Offset(size.width * 0.5f, size.height * 0.64f),
                        stroke,
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        muted,
                        Offset(size.width * 0.5f, size.height * 0.64f),
                        Offset(size.width * 0.73f, size.height * 0.42f),
                        stroke,
                        cap = StrokeCap.Round,
                    )
                }
            }
            BasicText(
                text = label,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 12.dp)
                    .background(surface)
                    .padding(horizontal = 4.dp)
                    .clearAndSetSemantics {},
                style = TextStyle(
                    color = when {
                        isError -> errorColor
                        focused || sheetVisible -> accent
                        else -> muted
                    },
                    fontFamily = ChoiceFont,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                ),
            )
        }
        if (isError && !errorMessage.isNullOrBlank()) {
            BasicText(
                text = errorMessage,
                modifier = Modifier.padding(top = 4.dp).clearAndSetSemantics {},
                style = TextStyle(color = errorColor, fontFamily = ChoiceFont, fontSize = 12.sp, lineHeight = 16.sp),
            )
        }
    }

    if (sheetVisible && enabled) {
        ModalBottomSheet(
            onDismissRequest = { sheetVisible = false },
            sheetState = sheetState,
            containerColor = surface,
            contentColor = ink,
            dragHandle = { BottomSheetDefaults.DragHandle(color = muted) },
        ) {
            BasicText(
                text = label,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp).semantics { heading() },
                style = TextStyle(
                    color = ink,
                    fontFamily = ChoiceFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                ),
            )
            if (primaryActionLabel != null && onPrimaryActionClick != null) {
                AlpinePrimaryButton(
                    text = primaryActionLabel,
                    onClick = {
                        sheetVisible = false
                        onPrimaryActionClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
                    darkTheme = darkTheme,
                )
            }
            if (options.isEmpty()) {
                BasicText(
                    text = stringResource(R.string.alpine_dropdown_empty),
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 32.dp),
                    style = TextStyle(color = muted, fontFamily = ChoiceFont, fontSize = 14.sp, lineHeight = 20.sp),
                )
            } else {
                val actionHeight = if (primaryActionLabel == null) 0.dp else 68.dp
                val maxListHeight = (LocalConfiguration.current.screenHeightDp.dp - 160.dp - actionHeight)
                    .coerceAtLeast(200.dp)
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = maxListHeight),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(options, key = { it.id }) { option ->
                        AlpineRadioCard(
                            selected = option.id == selectedId,
                            onClick = {
                                onSelected(option.id)
                                sheetVisible = false
                            },
                            title = option.label,
                            supportingText = option.supportingText,
                            enabled = option.enabled,
                            darkTheme = darkTheme,
                        )
                    }
                }
            }
        }
    }
}

/** A single-choice card. Selecting it never clears an already selected value. */
@Composable
fun AlpineRadioCard(
    selected: Boolean,
    onClick: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    enabled: Boolean = true,
    darkTheme: Boolean = isSystemInDarkTheme(),
) {
    AlpineChoiceCard(
        selected = selected,
        onToggle = onClick,
        title = title,
        modifier = modifier,
        supportingText = supportingText,
        enabled = enabled,
        darkTheme = darkTheme,
        radio = true,
    )
}

/** A multi-choice card with a checkbox and a controlled checked state. */
@Composable
fun AlpineCheckboxCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    enabled: Boolean = true,
    darkTheme: Boolean = isSystemInDarkTheme(),
) {
    AlpineChoiceCard(
        selected = checked,
        onToggle = { onCheckedChange(!checked) },
        title = title,
        modifier = modifier,
        supportingText = supportingText,
        enabled = enabled,
        darkTheme = darkTheme,
        radio = false,
    )
}

/** A heading and evenly spaced choice cards, matching the reference form layout. */
@Composable
fun AlpineSelectionGroup(
    title: String,
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable ColumnScope.() -> Unit,
) {
    require(title.isNotBlank()) { "AlpineSelectionGroup needs a title" }
    Column(modifier = modifier) {
        BasicText(
            text = title,
            modifier = Modifier.semantics { heading() },
            style = TextStyle(
                color = if (darkTheme) ChoiceDarkInk else ChoiceLightInk,
                fontFamily = ChoiceFont,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 28.sp,
            ),
        )
        Spacer(Modifier.height(18.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}

@Composable
private fun AlpineChoiceCard(
    selected: Boolean,
    onToggle: () -> Unit,
    title: String,
    modifier: Modifier,
    supportingText: String?,
    enabled: Boolean,
    darkTheme: Boolean,
    radio: Boolean,
) {
    require(title.isNotBlank()) { "Alpine choice cards need a title" }
    val interactions = remember { MutableInteractionSource() }
    val focused by interactions.collectIsFocusedAsState()
    val accent = if (darkTheme) ChoiceDarkInputAccent else ChoiceBlue
    val muted = if (darkTheme) ChoiceDarkMuted else ChoiceLightMuted
    val surfaceStart by animateColorAsState(
        targetValue = when {
            selected && darkTheme -> ChoiceDarkSelectedStart
            selected -> ChoiceLightSelectedStart
            darkTheme -> ChoiceDarkCard
            else -> ChoiceLightCard
        },
        animationSpec = tween(180),
        label = "Alpine choice surface start",
    )
    val surfaceEnd by animateColorAsState(
        targetValue = when {
            selected && darkTheme -> ChoiceDarkSelectedEnd
            selected -> ChoiceLightSelectedEnd
            darkTheme -> ChoiceDarkCard
            else -> ChoiceLightCard
        },
        animationSpec = tween(180),
        label = "Alpine choice surface end",
    )
    val accessibleLabel = if (supportingText.isNullOrBlank()) title else "$title. $supportingText"
    val selectionModifier = if (radio) {
        Modifier.selectable(
            selected = selected,
            enabled = enabled,
            role = Role.RadioButton,
            interactionSource = interactions,
            indication = ripple(color = accent),
            onClick = onToggle,
        )
    } else {
        Modifier.toggleable(
            value = selected,
            enabled = enabled,
            role = Role.Checkbox,
            interactionSource = interactions,
            indication = ripple(color = accent),
            onValueChange = { onToggle() },
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 76.dp)
            .alpha(if (enabled) 1f else 0.48f)
            .shadow(
                elevation = if (focused) 4.dp else if (selected) 2.dp else 0.dp,
                shape = ChoiceCardShape,
                ambientColor = accent.copy(alpha = 0.10f),
                spotColor = accent.copy(alpha = 0.10f),
            )
            .clip(ChoiceCardShape)
            .background(Brush.horizontalGradient(listOf(surfaceStart, surfaceEnd)))
            .semantics(mergeDescendants = true) { contentDescription = accessibleLabel }
            .then(selectionModifier)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(Modifier.size(20.dp).clearAndSetSemantics {}) {
            val stroke = 2.dp.toPx()
            if (radio) {
                drawCircle(
                    color = if (selected) accent else muted,
                    radius = size.minDimension / 2f - stroke / 2f,
                    style = Stroke(stroke),
                )
                if (selected) drawCircle(accent, radius = size.minDimension * 0.25f)
            } else {
                val radius = CornerRadius(5.dp.toPx())
                if (selected) drawRoundRect(color = accent, cornerRadius = radius)
                drawRoundRect(
                    color = if (selected) accent else muted,
                    cornerRadius = radius,
                    style = Stroke(stroke),
                )
                if (selected) {
                    drawLine(
                        if (darkTheme) ChoiceDarkSurface else Color.White,
                        Offset(size.width * 0.25f, size.height * 0.51f),
                        Offset(size.width * 0.43f, size.height * 0.68f),
                        stroke,
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        if (darkTheme) ChoiceDarkSurface else Color.White,
                        Offset(size.width * 0.43f, size.height * 0.68f),
                        Offset(size.width * 0.76f, size.height * 0.32f),
                        stroke,
                        cap = StrokeCap.Round,
                    )
                }
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            BasicText(
                text = title,
                modifier = Modifier.clearAndSetSemantics {},
                style = TextStyle(
                    color = if (darkTheme) ChoiceDarkInk else ChoiceLightInk,
                    fontFamily = ChoiceFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                ),
            )
            if (!supportingText.isNullOrBlank()) {
                BasicText(
                    text = supportingText,
                    modifier = Modifier.clearAndSetSemantics {},
                    style = TextStyle(
                        color = if (selected && darkTheme) ChoiceDarkSelectedMuted else muted,
                        fontFamily = ChoiceFont,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                    ),
                )
            }
        }
    }
}

@Preview(name = "Dropdown and choices light", showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 360)
@Preview(
    name = "Dropdown and choices dark",
    showBackground = true,
    backgroundColor = 0xFF090D12,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 360,
)
@Composable
private fun AlpineChoicesPreview() {
    val dark = isSystemInDarkTheme()
    var unit by remember { mutableStateOf("tablets") }
    var doseType by remember { mutableStateOf("regular") }
    var reminder by remember { mutableStateOf(true) }
    Column(
        Modifier
            .fillMaxSize()
            .background(if (dark) ChoiceDarkSurface else ChoiceLightSurface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        AlpineDropdownField(
            label = "Unit",
            options = listOf(
                AlpineDropdownOption("tablets", "Tablet(s)"),
                AlpineDropdownOption("drops", "Drop(s)"),
                AlpineDropdownOption("ml", "Millilitres"),
            ),
            selectedId = unit,
            onSelected = { unit = it },
            darkTheme = dark,
            containerColor = if (dark) Color.Black else Color.White,
            primaryActionLabel = "Add unit",
            onPrimaryActionClick = {},
        )
        AlpineSelectionGroup(title = "Dose type", darkTheme = dark) {
            AlpineRadioCard(
                selected = doseType == "regular",
                onClick = { doseType = "regular" },
                title = "Regular",
                supportingText = "For fixed times, days of the week, or intervals",
                darkTheme = dark,
            )
            AlpineRadioCard(
                selected = doseType == "as_needed",
                onClick = { doseType = "as_needed" },
                title = "As needed",
                supportingText = "No dose is required; actual doses are still recorded",
                darkTheme = dark,
            )
        }
        AlpineCheckboxCard(
            checked = reminder,
            onCheckedChange = { reminder = it },
            title = "Reminder",
            supportingText = "Notify me when a dose is due",
            darkTheme = dark,
        )
    }
}
