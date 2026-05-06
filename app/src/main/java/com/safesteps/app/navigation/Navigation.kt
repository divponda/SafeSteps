package com.safesteps.app.navigation

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.safesteps.app.R
import com.safesteps.app.notifications.SafeStepsNotificationManager
import com.safesteps.app.screens.ContactsScreen
import com.safesteps.app.screens.HomeScreen
import com.safesteps.app.screens.MapScreen
import com.safesteps.app.screens.TimerScreen
import com.safesteps.app.utils.TimerConstants
import kotlinx.coroutines.delay

sealed class Screen(val route: String, @param:StringRes val titleRes: Int, val icon: ImageVector) {
    data object Home : Screen("home", R.string.nav_home, Icons.Default.Home)
    data object Map : Screen("map", R.string.nav_map, Icons.Default.Place)
    data object Timer : Screen("timer", R.string.nav_timer, Icons.Default.Info)
    data object Contacts : Screen("contacts", R.string.nav_contacts, Icons.Default.Person)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Map,
    Screen.Timer,
    Screen.Contacts
)

@Composable
fun SafeStepsNavigation() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val notificationManager = remember {
        SafeStepsNotificationManager(context.applicationContext)
    }
    var selectedTimerMinutes by remember {
        mutableIntStateOf(TimerConstants.DefaultTimerDurationMinutes)
    }
    var remainingTimerSeconds by remember {
        mutableIntStateOf(TimerConstants.DefaultTimerDurationMinutes * TimerConstants.SecondsPerMinute)
    }
    var isTimerRunning by remember { mutableStateOf(false) }
    var recentTimerDurations by remember {
        mutableStateOf(emptyList<Int>())
    }

    LaunchedEffect(Unit) {
        notificationManager.createSafetyTimerChannel()
    }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && remainingTimerSeconds > 0) {
            delay(TimerConstants.CountdownTickMillis)
            remainingTimerSeconds -= 1
        }

        if (isTimerRunning && remainingTimerSeconds == 0) {
            isTimerRunning = false
            notificationManager.showTimerExpiredNotification()
            Toast.makeText(
                context,
                context.getString(R.string.timer_expired_toast),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = androidx.compose.ui.res.dimensionResource(id = R.dimen.card_elevation)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    val screenTitle = stringResource(id = screen.titleRes)
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screenTitle) },
                        label = { Text(screenTitle) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Map.route) { MapScreen() }
            composable(Screen.Timer.route) {
                TimerScreen(
                    selectedMinutes = selectedTimerMinutes,
                    remainingSeconds = remainingTimerSeconds,
                    isRunning = isTimerRunning,
                    recentDurations = recentTimerDurations,
                    onDurationSelected = { minutes ->
                        if (!isTimerRunning) {
                            selectedTimerMinutes = minutes
                            remainingTimerSeconds = minutes * TimerConstants.SecondsPerMinute
                        }
                    },
                    onStartTimer = {
                        remainingTimerSeconds = selectedTimerMinutes * TimerConstants.SecondsPerMinute
                        isTimerRunning = true
                        recentTimerDurations = (
                            listOf(selectedTimerMinutes) + recentTimerDurations
                                .filter { it != selectedTimerMinutes }
                            ).take(TimerConstants.MaxRecentDurations)
                    },
                    onCancelTimer = {
                        isTimerRunning = false
                        remainingTimerSeconds = selectedTimerMinutes * TimerConstants.SecondsPerMinute
                    }
                )
            }
            composable(Screen.Contacts.route) { ContactsScreen() }
        }
    }
}
