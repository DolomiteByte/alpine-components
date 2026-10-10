# Segmented tabs design

Alpine segmented tabs switch between peer views or time ranges within a screen. A calm shared track carries a single moving Alpine blue selection. The layout replaces individually outlined segments and keeps every label in the same position when the selection changes.

| Property | Value |
| --- | --- |
| Height | 56 dp, including 4 dp inset on all sides |
| Segments | Equal widths; prefer enough horizontal space for at least 48 dp per tab |
| Track | `#EDF3FA` in light mode; `#1A202A` in dark mode; 20 dp corners |
| Selected indicator | Gradient `#0A5BE3` to `#0054FF`; 16 dp corners; 48 dp high |
| Label | Manrope SemiBold, 14 sp, one line with ellipsis; white when selected |
| Inactive label | `#526680` in light mode; `#C0CDDD` in dark mode |
| Motion | Spring slide without bounce; text color follows selection |
| Interaction | Bounded ripple within each tab; 2 dp focus outline only while focused |
| Disabled tab | 45% opacity and no selection callback |

The host owns the selected index and its content. The component emits an index when a tab is tapped. Each tab exposes its label, selected state, disabled state, and tab role to accessibility services. There are no system bar insets, since the bar is placed in page content rather than bottom navigation.
