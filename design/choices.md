# Dropdowns and selection cards

The reference form uses a floating-label outlined dropdown above rounded single-selection cards. The Android implementation keeps those proportions and uses Alpine blue for selection. A matching checkbox card supports independent choices.

## Dropdown field

- Height: 64 dp including the floating label; the outlined field is 56 dp high.
- Shape: 12 dp corners, 1 dp resting outline and 2 dp focused, open, or error outline.
- Horizontal content padding: 16 dp. The selected label is Manrope 16 sp and the floating label is Manrope 12 sp.
- Tapping anywhere in the field opens an Android Material 3 modal bottom sheet. The sheet has a drag handle, heading, and scrollable list of the same Alpine selection cards. It can be dismissed by Back, swipe, or outside tap without selecting.
- The selected option is kept in the caller's state through its unique ID. Disabled options cannot be selected. A disabled field cannot open the sheet. An empty list shows a localized empty state.
- Errors color the outline and label; optional error text appears beneath the field and is announced to accessibility services.

## Choice cards

- Minimum height: 76 dp. Corners: 24 dp. Horizontal inset: 20 dp. Indicator: 20 dp with 12 dp gap to text.
- Title: Manrope SemiBold 14 sp; supporting text: Manrope 12 sp. The heading in `AlpineSelectionGroup` is Manrope Bold 20 sp with an 18 dp gap before the cards and 8 dp between cards.
- Dark mode: inactive `#1B1D22`; selected cards glow from `#2B67B3` to `#184477`. Light mode: inactive `#F2F5F9`; selected cards glow from `#DCEEFF` to `#C8E1FF`. Selected cards have no border and gain a soft shadow. The selected radio indicator remains `#8AB8FF` in dark mode and Alpine blue `#0054FF` in light mode.
- Checked boxes use the same accent as Alpine inputs: `#0054FF` in light mode and `#33D5EB` in dark mode. The check is white on blue and dark on cyan for contrast.
- Radio cards expose `RadioButton` selection semantics. Checkbox cards expose `Checkbox` checked semantics. The entire card is the touch target. Disabled cards are dimmed and cannot change state.
- A bounded ripple appears within the rounded card on tap. Selection background animates over 180 ms.

The reference screen depicts radio cards: only one dose type may be active. Checkbox cards use the same visual family for fields where several values may be active. A future Web or iOS implementation should keep the control semantics and layout; iOS should use the platform's native sheet for dropdown options.
