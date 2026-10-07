# Primary button design

Source: the DolomiteByte website's `.button-primary` style and `/alpine-fold-arrow.svg` asset, as implemented on dolomitebyte.com in October 2026. This document is the shared reference for platform implementations.

| Property | Value |
| --- | --- |
| Fill | Linear gradient, 135°, `#0A5BE3` to `#0054FF` |
| Label | Manrope Bold, 15.5 px/sp, white, 1.4 line height, -0.01 em tracking |
| Height | Minimum 52 px/dp; grows with text scaling |
| Padding | 14 px/dp vertical, 22 px/dp horizontal |
| Corner radius | 14 px/dp |
| Label–icon gap | 12 px/dp |
| Icon | 20 × 20 px/dp Alpine Fold arrow: cyan `#33D5EB` upper path, white lower path |
| Light border | 1 px/dp `#074BD1`; hover `#003DC2` |
| Dark border | 1 px/dp `rgba(108,170,255,0.45)`; hover `#74B3FF` |
| Focus ring | 3 px/dp, 3 px/dp outside; `#0054FF` in light mode, `#33D5EB` in dark mode |
| Disabled | 50% opacity, no click or shadow |
| Optional icons | 20 × 20 px/dp slots left and right of label; 12 px/dp gap; white tint |
| Loading | Keep label; hide left icon; replace right icon or Alpine Fold arrow with 20 px/dp rotating spinner; no click |

The gradient, text, shape, and icon are the same in light and dark mode. The border and shadow change with theme. On pointer devices, hover raises the shadow and shifts the icon 2 px/dp to the right. Press removes the shadow and shifts the icon 1 px/dp. Platforms should follow native touch, focus, and accessibility behavior; the Android implementation uses a native shadow and Compose indication to approximate the website's layered CSS shadows.

The Alpine Fold arrow appears by default. A custom right icon replaces it; the arrow can also be removed without replacement. Disabled and loading states expose their interaction state to accessibility services; loading has a localized state description.
