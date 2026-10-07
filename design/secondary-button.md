# Secondary button design

Source: the DolomiteByte website's base `.button-secondary` style, as implemented on dolomitebyte.com in October 2026. This document is the shared reference for platform implementations. The home-page hero has an additional contextual override; this component follows the base style used across the site.

| Property | Value |
| --- | --- |
| Label | Manrope SemiBold, 15.5 px/sp, 1.4 line height, -0.01 em tracking |
| Height | Minimum 52 px/dp; grows with text scaling |
| Padding | 14 px/dp vertical, 22 px/dp horizontal |
| Corner radius | 14 px/dp |
| Light | White `#FFFFFF` background, `#D2DAE5` border, `#24344B` label |
| Light hover | `#F5F8FF` background, `#8CAFF5` border, `#0054FF` label |
| Dark | `#181D23` background, `#39414B` border, `#EDF3FB` label |
| Dark hover | `#202932` background, `#5D849A` border, `#6DE1EF` label |
| Focus ring | 3 px/dp, 3 px/dp outside; `#0054FF` in light mode, `#33D5EB` in dark mode |
| Disabled | 50% opacity, no click or shadow |
| Optional icons | 20 × 20 px/dp slots left and right of label; 12 px/dp gap; icon tint follows label color |
| Loading | Keep label; hide left icon; replace right icon with 20 px/dp rotating spinner; no click |

The border is 1 px/dp in both themes. A subtle shadow raises the enabled button; press removes it. On pointer devices, hover raises the button 1 px/dp. The Android implementation uses native shadow and Compose indication for platform feedback.

The secondary button has no default icon. Disabled and loading states expose their interaction state to accessibility services; loading has a localized state description.
