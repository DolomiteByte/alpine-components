package com.dolomitebyte.alpine.components

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LightText = Color(0xFF132B50)
private val DarkText = Color(0xFFF4F8FF)
private val LightMuted = Color(0xBD132B50)
private val DarkMuted = Color(0xC7E4EEFF)
private val LightUnderline = Color(0x38132B50)
private val DarkUnderline = Color(0x38FFFFFF)
private val LightAccent = Color(0xFF0054FF)
private val DarkAccent = Color(0xFF33D5EB)
private val LightError = Color(0xFFB42318)
private val DarkError = Color(0xFFFF8A80)
private val Manrope = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_bold, FontWeight.Bold),
)

/** A single-line input following the underlined fields on dolomitebyte.com. */
@Composable
fun AlpineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: (@Composable (Color) -> Unit)? = null,
    trailingIcon: (@Composable (Color) -> Unit)? = null,
) {
    AlpineInputField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        errorMessage = errorMessage,
        darkTheme = darkTheme,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        multiline = false,
        minLines = 1,
    )
}

/** The same field with an email keyboard and a Next IME action by default. */
@Composable
fun AlpineEmailField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: (@Composable (Color) -> Unit)? = null,
    trailingIcon: (@Composable (Color) -> Unit)? = null,
) {
    AlpineInputField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        errorMessage = errorMessage,
        darkTheme = darkTheme,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = imeAction),
        keyboardActions = keyboardActions,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        multiline = false,
        minLines = 1,
    )
}

/** An integer or decimal input with a numeric keyboard and filtered edits. */
@Composable
fun AlpineNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    allowDecimal: Boolean = false,
    allowNegative: Boolean = false,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: (@Composable (Color) -> Unit)? = null,
    trailingIcon: (@Composable (Color) -> Unit)? = null,
) {
    AlpineInputField(
        value = value,
        onValueChange = { candidate ->
            if (isValidNumberInput(candidate, allowDecimal, allowNegative)) onValueChange(candidate)
        },
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        errorMessage = errorMessage,
        darkTheme = darkTheme,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (allowDecimal) KeyboardType.Decimal else KeyboardType.Number,
            imeAction = imeAction,
        ),
        keyboardActions = keyboardActions,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        multiline = false,
        minLines = 1,
    )
}

/** A masked input with a built-in, accessible show/hide button. */
@Composable
fun AlpinePasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: (@Composable (Color) -> Unit)? = null,
    trailingIcon: (@Composable (Color) -> Unit)? = null,
) {
    var passwordVisible by remember { mutableStateOf(false) }
    AlpineInputField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        errorMessage = errorMessage,
        darkTheme = darkTheme,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        keyboardActions = keyboardActions,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        trailingAction = { tint ->
            AlpinePasswordVisibilityButton(
                visible = passwordVisible,
                enabled = enabled,
                tint = tint,
                onClick = { passwordVisible = !passwordVisible },
            )
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation('*'),
        multiline = false,
        minLines = 1,
    )
}

/** A multiline input following the message field on dolomitebyte.com. */
@Composable
fun AlpineTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    minLines: Int = 5,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: (@Composable (Color) -> Unit)? = null,
    trailingIcon: (@Composable (Color) -> Unit)? = null,
) {
    require(minLines > 0) { "minLines must be positive" }
    AlpineInputField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        errorMessage = errorMessage,
        darkTheme = darkTheme,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        multiline = true,
        minLines = minLines,
    )
}

private fun isValidNumberInput(value: String, allowDecimal: Boolean, allowNegative: Boolean): Boolean {
    var separatorSeen = false
    return value.withIndex().all { (index, character) ->
        when {
            character.isDigit() -> true
            character == '-' && allowNegative && index == 0 -> true
            (character == '.' || character == ',') && allowDecimal && !separatorSeen -> {
                separatorSeen = true
                true
            }
            else -> false
        }
    }
}

@Composable
private fun AlpineInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier,
    placeholder: String,
    enabled: Boolean,
    readOnly: Boolean,
    isError: Boolean,
    errorMessage: String?,
    darkTheme: Boolean,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    leadingIcon: (@Composable (Color) -> Unit)? = null,
    trailingIcon: (@Composable (Color) -> Unit)? = null,
    trailingAction: (@Composable (Color) -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    multiline: Boolean,
    minLines: Int,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val ink = if (darkTheme) DarkText else LightText
    val muted = if (darkTheme) DarkMuted else LightMuted
    val accent = if (darkTheme) DarkAccent else LightAccent
    val errorColor = if (darkTheme) DarkError else LightError
    val invalidDescription = stringResource(R.string.alpine_input_invalid)
    val labelColor by animateColorAsState(
        targetValue = when {
            isError -> errorColor
            focused && enabled -> accent
            else -> muted
        },
        animationSpec = tween(180),
        label = "Alpine input label color",
    )
    val lineColor by animateColorAsState(
        targetValue = when {
            isError -> errorColor
            focused && enabled -> accent
            darkTheme -> DarkUnderline
            else -> LightUnderline
        },
        animationSpec = tween(180),
        label = "Alpine input underline color",
    )
    val lineWidth by animateDpAsState(
        targetValue = if ((focused && enabled) || isError) 3.dp else 1.dp,
        animationSpec = tween(180),
        label = "Alpine input underline width",
    )

    Column(modifier = modifier.alpha(if (enabled) 1f else 0.5f)) {
        BasicText(
            text = label,
            modifier = Modifier.clearAndSetSemantics {},
            style = TextStyle(
                color = labelColor,
                fontFamily = Manrope,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                letterSpacing = 0.26.sp,
            ),
        )
        Spacer(Modifier.height(7.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawWithContent {
                    drawContent()
                    val stroke = lineWidth.toPx()
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, size.height - stroke / 2f),
                        end = Offset(size.width, size.height - stroke / 2f),
                        strokeWidth = stroke,
                    )
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .semantics {
                        contentDescription = label
                        if (isError) error(errorMessage?.takeIf { it.isNotBlank() } ?: invalidDescription)
                    },
                enabled = enabled,
                readOnly = readOnly,
                textStyle = TextStyle(
                    color = ink,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                ),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                visualTransformation = visualTransformation,
                singleLine = !multiline,
                minLines = minLines,
                maxLines = if (multiline) Int.MAX_VALUE else 1,
                cursorBrush = SolidColor(accent),
                interactionSource = interactionSource,
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = if (multiline) 136.dp else 48.dp),
                        verticalAlignment = if (multiline) Alignment.Top else Alignment.CenterVertically,
                    ) {
                        if (leadingIcon != null) {
                            Box(
                                modifier = Modifier
                                    .offset(y = if (multiline) 9.dp else 0.dp)
                                    .size(20.dp)
                                    .clearAndSetSemantics {},
                                contentAlignment = Alignment.Center,
                            ) {
                                leadingIcon(labelColor)
                            }
                            Spacer(Modifier.width(12.dp))
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 9.dp, bottom = 14.dp),
                            contentAlignment = Alignment.TopStart,
                        ) {
                            if (value.isEmpty() && placeholder.isNotEmpty()) {
                                BasicText(
                                    text = placeholder,
                                    modifier = Modifier.clearAndSetSemantics {},
                                    style = TextStyle(
                                        color = muted.copy(alpha = muted.alpha * 0.7f),
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp,
                                    ),
                                )
                            }
                            innerTextField()
                        }
                        if (trailingIcon != null) {
                            Spacer(Modifier.width(12.dp))
                            Box(
                                modifier = Modifier
                                    .offset(y = if (multiline) 9.dp else 0.dp)
                                    .size(20.dp)
                                    .clearAndSetSemantics {},
                                contentAlignment = Alignment.Center,
                            ) {
                                trailingIcon(labelColor)
                            }
                        }
                    }
                },
            )
            trailingAction?.invoke(labelColor)
        }
        if (isError && !errorMessage.isNullOrBlank()) {
            Spacer(Modifier.height(4.dp))
            BasicText(
                text = errorMessage,
                modifier = Modifier.clearAndSetSemantics {},
                style = TextStyle(
                    color = errorColor,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                ),
            )
        }
    }
}

@Composable
private fun AlpinePasswordVisibilityButton(
    visible: Boolean,
    enabled: Boolean,
    tint: Color,
    onClick: () -> Unit,
) {
    val interactions = remember { MutableInteractionSource() }
    val description = stringResource(
        if (visible) R.string.alpine_password_hide else R.string.alpine_password_show,
    )
    Box(
        modifier = Modifier
            .size(48.dp)
            .semantics { contentDescription = description }
            .clip(CircleShape)
            .clickable(
                interactionSource = interactions,
                indication = ripple(color = tint),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(24.dp).clearAndSetSemantics {}) {
            val unit = size.minDimension / 24f
            val outline = Path().apply {
                moveTo(2f * unit, 12f * unit)
                cubicTo(4.7f * unit, 8.2f * unit, 8.2f * unit, 6f * unit, 12f * unit, 6f * unit)
                cubicTo(15.8f * unit, 6f * unit, 19.3f * unit, 8.2f * unit, 22f * unit, 12f * unit)
                cubicTo(19.3f * unit, 15.8f * unit, 15.8f * unit, 18f * unit, 12f * unit, 18f * unit)
                cubicTo(8.2f * unit, 18f * unit, 4.7f * unit, 15.8f * unit, 2f * unit, 12f * unit)
                close()
            }
            val stroke = Stroke(width = 1.8f * unit, cap = StrokeCap.Round, join = StrokeJoin.Round)
            drawPath(outline, tint, style = stroke)
            drawCircle(tint, radius = 2.8f * unit, center = Offset(12f * unit, 12f * unit), style = stroke)
            if (visible) {
                drawLine(
                    color = tint,
                    start = Offset(3f * unit, 3f * unit),
                    end = Offset(21f * unit, 21f * unit),
                    strokeWidth = 1.8f * unit,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

@Preview(name = "Inputs light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Preview(name = "Inputs dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, backgroundColor = 0xFF090D12)
@Composable
private fun AlpineInputsPreview() {
    val dark = isSystemInDarkTheme()
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        AlpineTextField("", {}, "Name", placeholder = "Dein Name", darkTheme = dark)
        AlpineEmailField(
            "name@beispiel.de", {}, "Email", darkTheme = dark,
            leadingIcon = { tint -> BasicText("@", style = TextStyle(color = tint, fontSize = 18.sp)) },
            trailingIcon = { tint -> BasicText("✓", style = TextStyle(color = tint, fontSize = 18.sp)) },
        )
        AlpineNumberField("12,5", {}, "Betrag", allowDecimal = true, allowNegative = true, darkTheme = dark)
        AlpinePasswordField("Alpine2026!", {}, "Passwort", darkTheme = dark)
        AlpineTextField("", {}, "Betreff", placeholder = "Worum geht es?", isError = true, errorMessage = "Pflichtfeld", darkTheme = dark)
        AlpineTextArea("", {}, "Nachricht", placeholder = "Erzähl uns von deinem Vorhaben ...", darkTheme = dark)
        AlpineTextField("", {}, "Deaktiviert", placeholder = "Nicht verfügbar", enabled = false, darkTheme = dark)
    }
}
