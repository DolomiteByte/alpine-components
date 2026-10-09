# Alpine premium card

Source layout: TheraBuddy's `PremiumDrawerCard`, adapted as a reusable Compose component. Copy, benefits, and the destination action belong to the host app.

| Property | Value |
| --- | --- |
| Surface | Alpine linear gradient `#0A5BE3` → `#0054FF` in both themes |
| Shape | 24 dp rounded corners; 18 dp internal padding |
| Light border | 1 dp `#074BD1` |
| Dark border | 1 dp `rgba(108,170,255,0.45)` |
| Primary text | White Manrope Bold; title 17 sp / 23 sp line height |
| Supporting text | White Manrope; 13 sp / 20 sp line height |
| Accent | Alpine cyan `#33D5EB` for the star and benefit checks; pale tint `#C8F7FF` for the small eyebrow |
| Call to action | White 48 dp minimum-height area; Alpine blue `#074BD1` label and arrow |
| Interaction | Entire card has one button role and a bounded white ripple; disabled state blocks clicks |

The gradient matches the Alpine primary button. Border and shadow change with the theme; hovering increases the shadow and pressing removes it. The inner action area is visual only, so it cannot dispatch a second click. The star icon can be replaced by a decorative Compose icon slot. Text and benefits can wrap on narrow screens.
