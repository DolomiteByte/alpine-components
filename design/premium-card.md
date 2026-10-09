# Alpine premium card

Source layout: TheraBuddy's `PremiumDrawerCard`, adapted as a reusable Compose component. Copy, benefits, and the destination action belong to the host app.

| Property | Value |
| --- | --- |
| Surface | Alpine diagonal gradient `#0A5BE3` → `#0054FF` at 62% → `#33D5EB` in both themes; 20-second drift each way |
| Shape | 24 dp rounded corners; 18 dp internal padding |
| Light border | 1 dp `#074BD1` |
| Dark border | 1 dp `rgba(108,170,255,0.45)` |
| Primary text | White Manrope Bold; title 17 sp / 23 sp line height |
| Supporting text | White Manrope; 13 sp / 20 sp line height |
| Accent | Alpine cyan `#33D5EB` for the star; solid white benefit circles with transparent check cutouts; pale tint `#C8F7FF` for the small eyebrow |
| Call to action | White 48 dp minimum-height area; Alpine blue `#074BD1` label and arrow |
| Interaction | Entire card has one button role and a bounded white ripple; disabled state blocks clicks |

The gradient starts with the Alpine primary button's blue and ends in Alpine cyan, visibly brightening toward the lower right. It drifts horizontally by a small amount over 20 seconds, then returns over 20 seconds. The blue portion stays behind the white copy, and the cyan end sits mostly around the white action area. `animateGradient = false` keeps it static; disabled cards are static too. Android's system animation scale applies to the motion. Border and shadow change with the theme; hovering increases the shadow and pressing removes it. The inner action area is visual only, so it cannot dispatch a second click. The star icon can be replaced by a decorative Compose icon slot. Text and benefits can wrap on narrow screens.
