# Bottom tabs design

The Alpine bottom tabs are a native navigation component for compact app screens. The DolomiteByte website does not have an equivalent bottom navigation; this design uses the shared Alpine gradient, Manrope type, and light and dark surfaces from the buttons. The destinations and navigation behavior belong to the host app.

| Property | Value |
| --- | --- |
| Destinations | Prefer 3–5 persistent destinations of equal importance |
| Surface | White `#FFFFFF` in light mode; `#181D23` in dark mode |
| Top divider | 1 dp `#D2DAE5` in light mode; `#39414B` in dark mode |
| Selected icon tile | 38 × 32 dp, 10 dp corners; gradient `#0A5BE3` to `#0054FF` |
| Icon | 20 × 20 dp; white when selected |
| Inactive icon and label | `#64748B` in light mode; `#AAB8C8` in dark mode |
| Selected label | `#0054FF` in light mode; `#6DE1EF` in dark mode |
| Label | Manrope SemiBold, 11 sp, one line with ellipsis |
| Tab target | At least 64 dp tall; equal widths across the bar |
| Disabled destination | 45% opacity and no click |
| System navigation area | Include the Android navigation bar inset by default |

The icon and label stay in place when selection changes. Each tab exposes its selected and enabled state and a tab role to accessibility services. The label is the accessible name; icon semantics are hidden. The bar emits an index when a destination is tapped and leaves navigation and selected-state storage to the app.
