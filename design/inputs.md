# Inputs design

Source: the DolomiteByte website's contact form and `.form-field` style, as implemented on dolomitebyte.com in October 2026. Its visible input types are single-line text (name and subject), email, and a five-row multiline message. The Android components follow that design while using native Compose text editing and keyboard behavior.

| Property | Value |
| --- | --- |
| Font | Manrope Bold 13 sp for labels; Manrope Regular 16 sp for input and placeholder |
| Label | 18 sp line height, 0.02 em tracking; 7 dp before the input |
| Text | `#132B50` in light mode; `#F4F8FF` in dark mode |
| Label, unfocused | `rgba(19,43,80,0.74)` in light mode; `rgba(228,238,255,0.78)` in dark mode |
| Placeholder | The corresponding muted label color at 70% opacity |
| Surface | Transparent, with no enclosing border or rounded corners |
| Underline | 1 dp; `rgba(19,43,80,0.22)` in light mode; `rgba(255,255,255,0.22)` in dark mode |
| Focus | Label and 3 dp underline change over 180 ms to `#0054FF` in light mode or `#33D5EB` in dark mode |
| Text field | 48 dp minimum edit area; 9 dp top and 14 dp bottom padding |
| Message area | Five lines and at least 136 dp edit area; grows with content |
| Optional icons | 20 dp slots to the left or right of the text, with a 12 dp gap; decorative to accessibility services |
| Password eye | 24 dp icon in a 48 dp touch target; starts masked with `*`, toggles to clear text |

The site relies on browser validation for its required fields. The Android library lets the host show a validation error with `isError` and `errorMessage`; the label and underline become red, and the message is visible and announced. Disabled fields have 50% opacity and cannot be edited. Read-only fields keep the standard appearance.

Number fields request a numeric keyboard. They accept digits, one decimal separator (`.` or `,`) when `allowDecimal = true`, and a leading minus when `allowNegative = true`. They preserve incomplete values such as `-` and `1,` for editing. The caller owns parsing and range validation. Password fields request a password keyboard and use `PasswordVisualTransformation('*')` until the user activates the eye button. That button has localized show/hide descriptions and is disabled with its field.

Number and password types, icon slots, and error or disabled styles are native form extensions; the website does not show those separate variants.
