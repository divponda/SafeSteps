# SafeSteps - Intermediate Review Report

## 1. Introduction

SafeSteps is a personal safety Android application designed to provide users with quick access to emergency services and safety features. The app aims to enhance personal security by offering one-tap SOS alerts, live location sharing, safety timers, and a map displaying nearby safety locations such as police stations and hospitals. The primary objective is to give users peace of mind and immediate assistance capabilities in emergency situations.

## 2. Changes with Respect to Initial Proposal

No significant changes have been made to the initial proposal. The core features remain the same:
- One-tap SOS alert
- Live location sharing
- Safety timer
- Safety map with nearby locations
- Emergency contacts management

## 3. Development Status

### 3.1 Completed Features (30% Implementation)

#### UI/UX Implementation
- **App Theme**: Custom color scheme designed for safety applications
  - Emergency red for SOS features
  - Safety blue for primary actions
  - Calm teal for secondary elements
  - Full Material 3 design system implementation
  - Support for both light and dark themes

- **Navigation**: Bottom navigation bar with 4 main sections
  - Home (SOS and quick actions)
  - Safety Map
  - Safety Timer
  - Emergency Contacts

#### Core Features Implemented

1. **Home Screen with SOS Button**
   - Large, prominent SOS button with visual feedback
   - Quick action to call emergency services (112)
   - Location sharing capability
   - Permission handling for SMS and Location

2. **Emergency Contacts Management**
   - Full CRUD operations using DataStore
   - Persistent local storage
   - Contact details: name, phone, relationship, primary status
   - JSON serialization for data persistence
   - Add/Delete functionality with dialog UI

3. **Safety Map (Basic Implementation)**
   - Google Maps integration using Jetpack Compose
   - Current location display
   - Sample markers for safety locations
   - Map controls (zoom, my location button)

4. **Safety Timer (UI Placeholder)**
   - Screen structure created
   - Full implementation planned for next iteration

### 3.2 Technical Implementation

#### Architecture
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Navigation**: Navigation Compose with bottom navigation
- **State Management**: Compose State and ViewModel-ready structure

#### Dependencies Added
```kotlin
// Navigation
androidx.navigation:navigation-compose:2.7.7

// Maps & Location
com.google.maps.android:maps-compose:4.3.3
com.google.android.gms:play-services-location:21.2.0
com.google.android.gms:play-services-maps:18.2.0

// DataStore for persistent storage
androidx.datastore:datastore-preferences:1.0.0

// Permissions
com.google.accompanist:accompanist-permissions:0.34.0
```

#### Permissions Configured
- `ACCESS_FINE_LOCATION` - Precise location for SOS and map
- `ACCESS_COARSE_LOCATION` - Approximate location
- `ACCESS_BACKGROUND_LOCATION` - Background location updates
- `SEND_SMS` - Send emergency alerts
- `CALL_PHONE` - Emergency calls
- `INTERNET` - Maps and services

## 4. Detailed Roadmap

### 4.1 Remaining Features (70%)

#### Sprint 1: Safety Timer (Week 8-9)
- [ ] Countdown timer with customizable duration
- [ ] Check-in mechanism
- [ ] Automatic SOS trigger on timer expiry
- [ ] Notification reminders before expiry
- [ ] Background service for timer operation

#### Sprint 2: Enhanced Map Features (Week 9-10)
- [ ] Integration with Google Places API
- [ ] Real-time nearby police stations and hospitals
- [ ] Safety score for locations
- [ ] Route planning to nearest safe location
- [ ] Offline map caching

#### Sprint 3: Location Sharing (Week 10-11)
- [ ] Real-time location tracking
- [ ] Share location via SMS with contacts
- [ ] Location history tracking
- [ ] Battery-optimized location updates

#### Sprint 4: SOS Enhancement (Week 11-12)
- [ ] Automatic SMS to all emergency contacts
- [ ] Include location coordinates in SMS
- [ ] Audio recording during SOS
- [ ] Fake call feature for uncomfortable situations
- [ ] Integration with local emergency services where available

#### Sprint 5: Polish & Testing (Week 12-13)
- [ ] Comprehensive testing on multiple devices
- [ ] UI/UX refinements
- [ ] Performance optimization
- [ ] Accessibility improvements
- [ ] App icon and branding assets

### 4.2 Work Distribution

| Team Member | Primary Responsibilities |
|-------------|-------------------------|
| Member 1 | Safety Timer, Background Services |
| Member 2 | Map Enhancement, Places API Integration |
| Member 3 | Location Sharing, SMS Integration |
| Member 4 | SOS Enhancement, Testing |

*Note: Adjust based on actual team size and preferences*

## 5. AVD Configuration

### Recommended Emulator Settings
- **Device**: Pixel 7 Pro or Pixel 6
- **System Image**: Android 14.0 (API 34) - Google Play
- **RAM**: 4GB minimum
- **Storage**: 4GB minimum
- **Features**: GPS, Camera (for future features)

### Testing Requirements
- Location permissions must be granted
- SMS functionality requires device with telephony or SMS app
- Maps require Google Play Services

### Google Maps API Key Setup
1. Go to Google Cloud Console
2. Create a new project or select existing
3. Enable Maps SDK for Android
4. Create API credentials
5. Add the key to ignored `local.properties` as `MAPS_API_KEY=your_key_here`

## 6. References

1. Android Developers Documentation - https://developer.android.com/
2. Jetpack Compose Documentation - https://developer.android.com/jetpack/compose
3. Google Maps SDK for Android - https://developers.google.com/maps/documentation/android-sdk
4. DataStore Documentation - https://developer.android.com/topic/libraries/architecture/datastore
5. Material Design 3 - https://m3.material.io/

---

**Project Repository**: [Add your GitHub/GitLab link]

**Google Drive Link**: [Add link to exported ZIP file]

**Team Members**: [List team member names and emails]

**Date**: March 2026
