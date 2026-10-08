# Toggles design

Source: the theme control in DolomiteByte's `components/SiteHeader.tsx` and `.theme-toggle` styles in `app/globals.css` on dolomitebyte.com, inspected in October 2026.

| Property | Value |
| --- | --- |
| Track | 56 × 32 dp pill, with a 1 dp border |
| Thumb | 24 dp circle with a subtle shadow; 4 dp edge spacing; 24 dp travel |
| Light state | `#FFFFFF` track, navy border at 22% opacity; `#E7F5FF` thumb and cobalt `#0054FF` sun on the right |
| Dark state | `#112540` track, cyan `#33D5EB` border at 65% opacity; `#F4F8FF` thumb and navy `#132B50` moon on the left |
| Motion | 200 ms for thumb position, track, border, thumb, and icon tint |
| Type | Manrope Semibold 14 sp for settings labels; Manrope Regular 12 sp for supporting text |
| Touch and focus | At least 48 dp touch height; visible blue or cyan focus outline |

`AlpineThemeToggle` matches the site's light-right/dark-left behavior. `AlpineToggle` reuses the palette and proportions for a general Boolean setting; its checked thumb moves right, following the usual switch convention. Both are controlled Compose components with switch semantics and English/German state descriptions. Disabled controls use 50% opacity and ignore input. The host app owns theme persistence and application.
