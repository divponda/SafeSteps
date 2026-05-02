# SafeSteps

SafeSteps is a personal safety Android app for quick emergency actions while walking alone or feeling unsafe. It focuses on fast SOS access, local emergency contacts, location sharing, a safety map, and a check-in timer.

## Implemented Main Features

- One-tap SOS flow with confirmation before opening the user's messaging app
- Emergency contacts stored locally with Jetpack DataStore
- Add, delete, and call emergency contacts
- Live location sharing through Android share intents
- Safety map using Google Maps Compose
- Current-location marker and nearby demo safety markers for police, hospital, pharmacy, fire station, library, university security, and a well-lit public area
- Bottom-left Safe Places button that opens an editable/reorderable safe-place list
- Safety timer with minute-by-minute duration selection, recent durations, countdown, cancel/check-in behavior, tab-safe state, and expiry notification
- Runtime location and notification permission handling

## Implemented Secondary Features

- Material 3 Jetpack Compose UI
- Bottom navigation across Home, Safety Map, Safety Timer, and Contacts
- Dark mode support through the app theme
- Centralized string resources, dimensions, and constants
- Shared Compose components for repeated cards, buttons, headers, and empty states

## Technologies Used

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Google Maps Compose
- Google Play Services Location
- Jetpack DataStore Preferences
- Kotlin Serialization
- Android local notifications

## AVD Used

- Device: Pixel 4
- Recommended system image: Android 14 or newer with Google Play services

## Setup Instructions

1. Open this repository in Android Studio.
2. Let Gradle sync finish.
3. Create or select a Pixel 4 AVD with Google Play services.
4. Add a Google Maps API key if you want the real map tiles to load.
5. Run the `app` configuration on the Pixel 4 emulator.

## Google Maps API Key Setup

Do not commit API keys to the repository.

Create or edit `local.properties` in the project root and add:

```properties
MAPS_API_KEY=your_google_maps_api_key_here
```

In Google Cloud Console, enable Maps SDK for Android for the key. Without a valid key, the app still builds, but the Google map may not render real tiles.

## Demo Checklist

1. Launch the app on Pixel 4 AVD.
2. Add an emergency contact from the Contacts screen.
3. Return to Home and tap SOS.
4. Confirm that the messaging app opens with a SafeSteps emergency message.
5. Grant location permission and test Share Live Location.
6. Open Safety Map and verify the current-location marker, blue location dot, safe-place markers, and Safe Places button.
7. Open the Safe Places popup and test edit/reorder/delete/add.
8. Start a 3-minute Safety Timer, switch to Contacts, then return to Timer and confirm it is still running.
9. Cancel the timer, then let another timer expire to verify the local notification.
10. Use the phone icon on a contact to confirm the Android dialer opens.
11. Restart the app and confirm saved contacts are still present.

## Known Limitations

- Safety map uses demo safe-place markers instead of a live Places API query.
- Safety timer notification is intended for demo use and is not a full background service.
- SOS opens the messaging app for user-controlled sending; it does not send SMS automatically.
- Location sharing uses the last known location when available, with a Madrid fallback for emulator demos.

## Team Work Distribution

- Divya: UI + Navigation + Timer
- Saransh: Maps + Location Services
- Smit: Storage + Notifications
