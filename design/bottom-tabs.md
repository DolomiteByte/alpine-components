# Bottom tabs design

The Alpine bottom tabs are a native navigation component for compact app screens. Their expanding selected tab follows the interaction in TheraBuddy's Android navigation. The Alpine gradient, Manrope type, and light and dark surfaces match the buttons. The destinations and navigation behavior belong to the host app.

| Property | Value |
| --- | --- |
| Destinations | Prefer 3–5 persistent destinations of equal importance |
| Surface | White `#FFFFFF` in light mode; `#181D23` in dark mode |
| Bar | 80 dp tall, with 8 dp horizontal inset and evenly spaced items |
| Selected pill | Fully rounded; gradient `#0A5BE3` to `#0054FF`; 16 dp horizontal and 10 dp vertical padding |
| Icon | 24 × 24 dp slot; white when selected |
| Inactive item | Icon only, transparent background, 12 dp horizontal and 10 dp vertical padding |
| Inactive icon | `#64748B` in light mode; `#AAB8C8` in dark mode |
| Selected label | White Manrope SemiBold, 14 sp, one line with ellipsis; 8 dp after icon |
| Selection motion | `animateContentSize()` expands or shrinks the pill as its label appears or disappears |
| Disabled destination | 45% opacity and no click |
| System navigation area | Include the Android navigation bar inset by default |

The selected item shows its icon and label together. Inactive items show only their icons. Each tab exposes its selected and enabled state and a tab role to accessibility services. The label remains the accessible name for every tab; icon semantics are hidden. The bar emits an index when a destination is tapped and leaves navigation and selected-state storage to the app.
