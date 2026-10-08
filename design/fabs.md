# Floating action button design

The FAB is a native Android extension of DolomiteByte's primary action style. It reuses the Alpine blue gradient, border, Manrope label, and icon tint from `design/primary-button.md`; the site has no FAB to reproduce directly.

| Property | Value |
| --- | --- |
| Fill | Linear gradient `#0A5BE3` to `#0054FF` |
| Shape | 56 dp circular icon-only button; 18 dp rounded extended button |
| Height | At least 56 dp, grows with text scaling |
| Label | Manrope Bold 15.5 sp, white |
| Icon | 24 dp slot; receives white tint |
| Label–icon gap | 10 dp |
| Border | 1 dp `#074BD1` in light mode, translucent `#6CAAFF` in dark mode |
| Shadow | 8 dp light, 10 dp dark; rises on hover and lowers while pressed |
| Focus | 3 dp blue or cyan outline |
| Disabled/loading | Disabled has 50% opacity; loading replaces the icon or adds a spinner and blocks clicks |

`AlpineFabOverlay` places a FAB over arbitrary content. It defaults to bottom-end with 16 dp edge padding and safe drawing insets. Bottom-start, center, top corners, and other alignments are available through Compose `Alignment`; `DpOffset` provides further adjustment. The host app can increase bottom padding to clear `AlpineBottomTabs` or set insets to zero when its parent already applies them.
