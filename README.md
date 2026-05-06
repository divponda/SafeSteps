# SafeSteps

SafeSteps is a personal safety Android app for quick emergency actions while walking alone or feeling unsafe. It focuses on fast SOS access, local emergency contacts, location sharing, a safety map, and a safety timer that automatically alerts emergency contacts if the user does not check in.

## Implemented Main Features

- One-tap SOS flow with confirmation before opening the user's messaging app
- Emergency contacts stored locally with Jetpack DataStore
- Add, delete, and call emergency contacts
- Live location sharing through Android share intents
- Safety map using Google Maps Compose
- Current-location marker and automatic nearby demo safe-place markers for police, hospital, pharmacy, fire station, library, transit station, hotel, and shopping center
- Nearby Safe Places list with distance, category, address, rating, open status, refresh, and navigation actions
- Safety timer with minute-by-minute duration selection, recent durations, countdown, cancel/check-in behavior, tab-safe state, and automatic emergency SMS alerts when the timer expires
- Runtime location, SMS, and notification permission handling

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
- Android `SmsManager` for automatic emergency SMS sending

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

The current project uses a demo safe-places repository generated around the user's current location. For real live place results, enable Places API / Places SDK for Android and replace the demo repository with a Places-backed repository.

## Demo Checklist

1. Launch the app on Pixel 4 AVD.
2. Add an emergency contact from the Contacts screen.
3. Return to Home and tap SOS.
4. Confirm that the messaging app opens with a SafeSteps emergency message.
5. Grant location permission and test Share Live Location.
6. Open Safety Map and verify the current-location marker, blue location dot, safe-place markers, and Nearby Safe Places list.
7. Tap a safe place in the list and confirm the map focuses on that marker.
8. Tap Refresh and confirm the list reloads.
9. Start a 3-minute Safety Timer, switch to Contacts, then return to Timer and confirm it is still running.
10. Grant SMS permission, cancel the timer once and confirm no alert sends, then let another timer expire to verify automatic emergency SMS sending.
11. Use the phone icon on a contact to confirm the Android dialer opens.
12. Restart the app and confirm saved contacts are still present.

## Known Limitations

- Safety map uses demo safe-place results generated around the current/default location instead of live Places API results.
- Safety timer expiry uses `SmsManager` to send emergency SMS alerts to saved emergency contacts when SMS permission is granted.
- Timer expiry alerting is intended for foreground/class demo use and is not a full background service.
- SOS also opens the messaging app for user-controlled sending; it does not send SMS automatically.
- Location sharing uses the last known location when available, with a Madrid fallback for emulator demos.
- Automatic SMS sending requires a device or emulator image with SMS support and a valid telephony/SMS environment.

## Safety Timer Emergency Alert Behavior

When the Safety Timer reaches zero, SafeSteps loads locally saved emergency contacts and attempts to send an emergency SMS to every valid saved phone number using Android `SmsManager`. If location permission is granted and a current or last known location is available, the message includes a Google Maps link. If location is unavailable, the message uses a clear fallback sentence. `SEND_SMS` permission is requested before the timer starts, and the app shows an in-app result dialog plus a notification when possible after sending is attempted.

## Team Work Distribution

- Divya: UI + Navigation + Timer
- Saransh: Maps + Location Services
- Smit: Storage + Notifications
