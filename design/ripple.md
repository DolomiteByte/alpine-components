# Press feedback

Android clickable actions use a bounded Compose ripple that is clipped to the component shape. The ripple is explicit in the component, so it does not depend on the host app's `LocalIndication` or Material theme.

| Component | Ripple color |
| --- | --- |
| Primary button and FAB | White over the Alpine blue gradient |
| Secondary button | Alpine blue in light mode, cyan in dark mode |
| Bottom tab | White when selected; Alpine blue in light mode and cyan in dark mode when inactive |
| Alert-dialog close icon | Alpine blue in light mode, cyan in dark mode |
| Password visibility icon | Current icon tint |

The dialog close control remains transparent without a border at rest. Its 48 dp circular touch target shows the ripple only during interaction. Disabled and loading actions do not accept presses or display the ripple.
