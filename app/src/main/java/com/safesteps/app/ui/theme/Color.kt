package com.safesteps.app.ui.theme

import androidx.compose.ui.graphics.Color

// SafeSteps color palette - calm safety colors with red reserved for emergencies.
val EmergencyRed = Color(0xFFD32F2F)
val EmergencyRedDark = Color(0xFF9F1D1D)
val SafetyBlue = Color(0xFF1D6FB8)
val SafetyBlueDark = Color(0xFF0E355D)
val CalmTeal = Color(0xFF00796B)
val CalmTealDark = Color(0xFF003D36)
val WarningAmber = Color(0xFFE58A00)
val SuccessGreen = Color(0xFF2E7D57)

// Light Theme Colors
val PrimaryLight = SafetyBlue
val OnPrimaryLight = Color.White
val PrimaryContainerLight = Color(0xFFD8EAFE)
val OnPrimaryContainerLight = SafetyBlueDark

val SecondaryLight = CalmTeal
val OnSecondaryLight = Color.White
val SecondaryContainerLight = Color(0xFFCDEFE9)
val OnSecondaryContainerLight = CalmTealDark

val TertiaryLight = SuccessGreen
val OnTertiaryLight = Color.White
val TertiaryContainerLight = Color(0xFFD6F3E2)
val OnTertiaryContainerLight = Color(0xFF123B28)

val BackgroundLight = Color(0xFFF7FAFC)
val OnBackgroundLight = Color(0xFF16212B)
val SurfaceLight = Color.White
val OnSurfaceLight = Color(0xFF16212B)
val SurfaceVariantLight = Color(0xFFE4EDF4)
val OnSurfaceVariantLight = Color(0xFF536270)
val OutlineLight = Color(0xFFB9C7D3)

val ErrorLight = EmergencyRed
val OnErrorLight = Color.White
val ErrorContainerLight = Color(0xFFFFDAD7)
val OnErrorContainerLight = EmergencyRedDark

// Dark Theme Colors
val PrimaryDark = Color(0xFFA8D1FF)
val OnPrimaryDark = SafetyBlueDark
val PrimaryContainerDark = Color(0xFF164E83)
val OnPrimaryContainerDark = Color(0xFFBBDEFB)

val SecondaryDark = Color(0xFF8FDAD0)
val OnSecondaryDark = CalmTealDark
val SecondaryContainerDark = Color(0xFF005E54)
val OnSecondaryContainerDark = Color(0xFFB2DFDB)

val TertiaryDark = Color(0xFF93D9AE)
val OnTertiaryDark = Color(0xFF123B28)
val TertiaryContainerDark = Color(0xFF1F5B3C)
val OnTertiaryContainerDark = Color(0xFFD6F3E2)

val BackgroundDark = Color(0xFF0F151A)
val OnBackgroundDark = Color(0xFFE4ECF2)
val SurfaceDark = Color(0xFF172027)
val OnSurfaceDark = Color(0xFFE4ECF2)
val SurfaceVariantDark = Color(0xFF23313B)
val OnSurfaceVariantDark = Color(0xFFC2CDD6)
val OutlineDark = Color(0xFF71808D)

val ErrorDark = Color(0xFFFFB4AD)
val OnErrorDark = EmergencyRedDark
val ErrorContainerDark = Color(0xFF7E1E1E)
val OnErrorContainerDark = Color(0xFFFFCDD2)
