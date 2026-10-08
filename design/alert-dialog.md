# Alert dialog design

The Android dialog extends Alpine's existing button, type, and surface language. It uses the published primary and secondary action components directly.

| Property | Value |
| --- | --- |
| Panel | White in light mode, `#181D23` in dark mode |
| Border | 1 dp `#D2DAE5` light, `#39414B` dark |
| Shape | 24 dp corners, 24 dp shadow |
| Width | Up to 440 dp, with 24 dp inner padding |
| Header | Manrope Bold 22 sp; `#132B50` light, `#F4F8FF` dark |
| Close | 48 × 48 dp icon-only button with localized accessible name |
| Content | Arbitrary Compose slot; Manrope Regular 15 sp helper text |
| Actions | Existing Alpine primary and secondary buttons; side by side at 340 dp or more of inner width, stacked below that |

The content area scrolls when needed, leaving the title, close control, and actions visible. Back and outside taps use the modal dialog's dismiss callback. Primary and secondary callbacks are independent so validation or loading can finish before the host closes the dialog.
