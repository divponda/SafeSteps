# Final Milestone Notes

## Improvements After Milestone 2 Review

- Moved user-facing hardcoded text into `app/src/main/res/values/strings.xml`.
- Moved repeated UI spacing, sizing, and elevation values into `app/src/main/res/values/dimens.xml`.
- Moved logic values such as emergency number, map coordinates, timer defaults, notification IDs, and storage keys into `AppConstants.kt`.
- Added shared Compose components for repeated UI patterns.
- Removed an unused duplicate `app/src/res/values/strings.xml` resource file.
- Replaced the hardcoded Google Maps API key placeholder with a manifest placeholder loaded from `local.properties` or an environment variable.

## String Resource Cleanup

The refactor covers screen titles, bottom navigation labels, contact dialog fields, buttons, Toast messages, SOS dialog text, map marker labels, permission messages, and notification text.

## Repeated Values and UI Refactor

- `SafeStepsCard` now handles shared card styling.
- `SafeStepsPrimaryButton` centralizes full-width primary button sizing.
- `ScreenHeader` and `EmptyStateMessage` support reusable screen/empty-state layouts.
- Timer, location, notification, storage, animation, and SOS constants are grouped by purpose.

## Features Ready For Demo

- Home screen with SOS confirmation and emergency message intent
- Emergency call dialer shortcut
- Location sharing action
- Emergency contacts persistence with DataStore
- Add/delete/call contacts
- Safety map with permission handling, current-location marker, automatic nearby safe-place markers, refresh, navigation, and a visible places list
- Safety timer countdown with minute-by-minute selection, recent durations, tab-safe state, and expiry notification
- Dark/light theme support

## Suggested Final Report Screenshots

1. Home screen showing the SOS button and quick actions.
2. SOS confirmation dialog.
3. Contacts screen with at least one saved emergency contact.
4. Add Emergency Contact dialog.
5. Safety Map screen with markers and location permission enabled.
6. Safety Map Nearby Safe Places list.
7. Safety Timer screen while a 3-minute countdown is running.
8. Contacts screen showing the call icon.
9. Timer expiry notification in the notification shade.
10. Android Studio Pixel 4 AVD run configuration or emulator launch view.

## Verification Notes

- `assembleDebug` passed using Android Studio's bundled Java runtime.
- `testDebugUnitTest` passed.
- Latest `assembleDebug` and `testDebugUnitTest` passed after the map, timer, and contact-call improvements.
- `lintDebug` started but hung during analysis and was stopped cleanly; run Android Studio lint before final packaging.
- Safety Map was manually verified by the project owner after adding a valid Google Maps API key.
- Safety Map currently uses a demo safe-places repository; live Google Places results can be added later by enabling Places API / Places SDK for Android.
- ADB found no running emulator in this shell session, so remaining full-app manual testing should be completed from Android Studio on the Pixel 4 AVD.
- On Android 13+, allow notification permission to see timer expiry notifications.
- Keep `MAPS_API_KEY` in `local.properties` only; do not commit the API key.
