# Alpine snackbars

`AlpineSnackbar` is a compact, content-width Jetpack Compose message surface. It has no border and uses Manrope typography, rounded corners, a soft shadow, and optional actions. The `Default`, `Info`, and `Warning` variants work in light and dark mode.

| Variant | Light surface | Dark surface | Accent |
| --- | --- | --- | --- |
| Default | White `#FFFFFF` | Slate `#181D23` | Alpine blue |
| Info | Ice blue `#EBF7FF` | Deep teal `#102C3D` | Clear blue / cyan |
| Warning | Pale amber `#FFF5E7` | Warm charcoal `#352719` | Amber |

Info and warning show a standalone tinted icon without a background or border. Set `showIcon = false` to hide it. Supply the `icon` slot for any variant, including Default; it receives the variant's icon tint. The icon is decorative to accessibility services.

When an icon is shown, the trailing inset is 18 dp instead of 12 dp, balancing the icon's visible inset from the left edge. Without an icon, both horizontal insets remain 12 dp.

The main and secondary action buttons are optional independently. The main action is filled; the secondary action is text-only with no resting background or border. Both have at least a 48 dp touch target and a bounded ripple. Labels and callbacks must be supplied together. Multiline messages wrap naturally. At wide widths actions sit beside the message; at narrower widths they move below it. A short snackbar stays as wide as its content, up to 560 dp.

`AlpineSnackbarOverlay` draws the snackbar over arbitrary Compose content and animates it in and out. `Alignment.BottomCenter`, `Alignment.BottomEnd`, and `Alignment.BottomStart` cover the requested bottom placements. Any `Alignment` plus `DpOffset` gives a custom position. The overlay applies safe drawing insets and 16 dp edge padding by default. The caller owns `visible`; `durationMillis` requests dismissal after 4 seconds by default, or `null` keeps it visible. Action callbacks do not dismiss automatically, so callers can finish work before hiding the snackbar.
