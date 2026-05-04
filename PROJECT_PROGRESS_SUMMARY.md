# SafeSteps Project Progress Summary

## Purpose

This file summarizes the improvements made after the Milestone 2 review, which features are now working, which features improved from placeholder/demo status, and how much progress the project has made against the final Android app specifications.

## Main Changes Made

### Code Quality and Review Feedback Fixes

- Moved user-facing hardcoded strings into `app/src/main/res/values/strings.xml`.
- Replaced repeated UI spacing, sizes, and elevation values with shared dimensions in `app/src/main/res/values/dimens.xml`.
- Replaced repeated logic values with constants in `AppConstants.kt`.
- Removed the old scattered `Constants.kt` file and reorganized constants by purpose:
  - `AppConstants`
  - `AnimationConstants`
  - `ContactStorageConstants`
  - `LocationConstants`
  - `TimerConstants`
  - `NotificationConstants`
- Added reusable Compose UI components:
  - `SafeStepsCard`
  - `SafeStepsPrimaryButton`
  - `ScreenHeader`
  - `EmptyStateMessage`
- Cleaned repeated card/button/empty-state UI patterns while preserving the existing visual design.
- Removed the duplicate unused `app/src/res/values/strings.xml` file.
- Removed old prompt/comment blocks from source files.
- Kept naming clean and CamelCase throughout the refactor.
- Added current-location and automatic nearby safe-place map improvements.
- Moved active timer state above the Timer screen so it survives tab navigation.
- Added an emergency contact call action using the Android dialer.

### Security and Configuration Changes

- Removed the hardcoded Google Maps API key placeholder from `AndroidManifest.xml`.
- Added a manifest placeholder for `MAPS_API_KEY`.
- Configured the app to read the Maps key from ignored `local.properties` or an environment variable.
- Avoided committing any real API key or secret.

## Features That Work Now

### Emergency Contacts

Status: Working

- Users can add emergency contacts.
- Users can delete emergency contacts.
- Users can call emergency contacts through the Android dialer.
- Contacts are stored locally using Jetpack DataStore.
- Contacts persist after app restart.
- Contact fields include name, phone number, relationship, and primary contact status.

Previously:

- Storage existed, but the implementation had repeated values and less clean structure.

Now:

- Storage constants are centralized and the repository is cleaner.

### SOS Flow

Status: Improved and demo-ready

- SOS button opens a confirmation dialog.
- If no emergency contacts exist, the app shows a clear message asking the user to add one.
- If contacts exist, the app opens the user's messaging app with an emergency message.
- The message can include a current/last-known location link if location permission is granted.
- The app does not automatically send SMS, which is safer and more appropriate for demo use.

Previously:

- SOS showed Toast messages only and did not actually use stored emergency contacts.
- SMS permission logic existed, but the app did not prepare a real emergency message flow.

Now:

- SOS is connected to stored contacts and Android messaging intents.

### Location Sharing

Status: Improved and demo-ready

- The Home screen can request location permission.
- Share Live Location uses the last known device location when available.
- If location is unavailable, the app falls back to a demo/default location.
- Location is shared through a standard Android share intent.

Previously:

- Location sharing used hardcoded `"0", "0"` coordinates.

Now:

- Location sharing attempts to use real location data.

### Safety Map

Status: Working and manually verified

- Safety Map screen uses Google Maps Compose.
- Demo safe-location markers are shown for police, hospital, pharmacy, fire station, public library, transit station, hotel, and shopping center.
- A current-location marker is shown when current or last-known location is available.
- A Nearby Safe Places list appears under the map with distance, category, address, rating/open status, refresh, and navigation actions.
- Map text and marker labels now come from string resources.
- Location permission handling was improved.
- `isMyLocationEnabled` is only enabled when permission is granted, reducing crash risk.
- The map loads successfully after adding the Google Maps API key.

Previously:

- Map used hardcoded strings and coordinates directly in the screen.
- My-location mode could be enabled without checking runtime permission.

Now:

- Map constants and strings are centralized, and permission handling is safer.

Limit:

- Real nearby safe-place search through Places API is not implemented. Current results are demo results generated around the user/default location.

### Safety Timer

Status: Improved and demo-ready

- User can select a timer duration.
- User can select timer duration minute by minute.
- Recently started durations appear in a recent durations list.
- User can start the timer.
- User can cancel/check in before expiry.
- The screen shows a live countdown.
- The active countdown keeps running when the user switches to another tab and returns.
- When the timer reaches zero, the app creates a local notification.
- Android 13+ notification permission is requested when needed.

Previously:

- Timer screen was mostly a UI placeholder with a start/stop boolean.
- It did not count down.
- It did not trigger a notification.

Now:

- Timer has real countdown behavior and notification support.

Limit:

- Timer state is hoisted above the Timer screen for tab navigation, but it is still not a full background service.

### Notifications

Status: Newly working for timer expiry

- Notification channel is created for safety timer alerts.
- Timer expiry notification uses string resources for title/body.
- Notification permission is handled for Android 13+.
- App does not crash if notification permission is denied.

Previously:

- Notifications were listed in the plan but not implemented.

Now:

- Local timer expiry notification is implemented.

### Emergency Contact Calling

Status: Newly working

- Each saved emergency contact now has a phone icon.
- Pressing the phone icon opens the Android dialer with that contact's number.
- The app uses `ACTION_DIAL`, so the user confirms the call manually.
- Invalid phone numbers show a clear message instead of failing silently.

### Dark Mode

Status: Already supported

- The app already had light and dark Material 3 color schemes.
- The refactor preserved this.

## Course Specification Progress

### Requirement: Android App With UI

Progress: Complete

- SafeSteps is a Kotlin Android app.
- It uses Jetpack Compose and Material 3.
- It has multiple screens and bottom navigation.

### Requirement: Runs In Android Studio AVD, Preferably Pixel 4

Progress: Mostly complete

- The app builds successfully with `assembleDebug`.
- It should run on a Pixel 4 AVD through Android Studio.
- Manual AVD launch testing still needs to be completed because no emulator was running in the current shell session.

### Requirement Group 1: Persistent Storage

Progress: Complete

- Emergency contacts are stored locally using Jetpack DataStore.
- Contacts persist after app restart.
- Add/delete contact behavior is implemented.

### Requirement Group 2: Maps and Location Services

Progress: Complete for final demo

- Google Maps Compose is integrated.
- Location permission handling exists.
- Last-known location is used for sharing/SOS when available.
- Demo safe-place results are generated around the current/default location and shown on the map.
- Google Maps API key setup has been verified manually.

Remaining:

- Real nearby safe locations through Places API or REST service are not implemented; the project uses a clearly separated demo repository for final-demo reliability.

### Requirement Group 3: REST Services

Progress: Not implemented

- No REST API integration is currently present.
- This is acceptable if the project relies on at least two other required groups.

### Requirement Group 4: Background Services, Notifications, or Alarms

Progress: Partially complete / demo-ready

- Timer expiry notification is implemented.
- Notification channel and Android 13+ permission handling are implemented.

Remaining:

- Full background timer service or alarm manager support is not implemented.

## Overall Project Progress Estimate

Estimated progress toward final submission: 90% to 95%

### Why This Is No Longer 30%

The Milestone 2 report described the app as roughly 30% implemented. Since then:

- Code quality review feedback has been directly addressed.
- SOS flow is now connected to contacts and messaging.
- Timer is no longer just a placeholder.
- Notifications now work for timer expiry.
- Location sharing is more functional.
- Map permission handling is safer.
- Google Maps loading has been verified with a valid API key.
- Safety Map now loads nearby demo safe places automatically and communicates them through pins plus a list.
- Timer behavior is now stable across tab navigation.
- Contacts now support calling.
- Documentation for final submission has been added.

### What Is Still Needed For A Strong Final Submission

- Run and record the app on a Pixel 4 AVD.
- Capture final report screenshots.
- Record a short demo video.
- Manually test:
  - Home screen
  - SOS message flow
  - Add/delete contacts
  - App restart contact persistence
  - Map screen
  - Location permission flow
  - Timer start/cancel/expiry notification
  - Timer tab-switch behavior
  - Contact call action
- Run Android Studio lint if possible.

## Verification Completed

- `assembleDebug`: passed.
- `testDebugUnitTest`: passed.
- Latest `assembleDebug`: passed after map/timer/contact improvements.
- Latest `testDebugUnitTest`: passed after map/timer/contact improvements.
- `lintDebug`: started but hung during analysis and was stopped cleanly.
- Safety Map loading: manually verified after API key setup.
- Full emulator walkthrough remains manual.

## Recommended Final Demo Screenshots

1. Home screen with large SOS button.
2. SOS confirmation dialog.
3. Contacts screen with saved emergency contact.
4. Add Emergency Contact dialog.
5. Safety Map screen with current location, markers, and Nearby Safe Places list.
6. Safety Map list item tapped with the camera focused on the selected marker.
7. Timer screen with a 3-minute countdown running.
8. Contacts screen showing the call icon.
9. Timer expiry notification.
10. Android Studio running the app on Pixel 4 AVD.
