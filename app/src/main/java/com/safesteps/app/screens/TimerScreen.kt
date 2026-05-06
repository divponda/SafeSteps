package com.safesteps.app.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.safesteps.app.R
import com.safesteps.app.ui.components.SafeStepsScreenTitle
import com.safesteps.app.ui.theme.EmergencyRed
import com.safesteps.app.ui.theme.SuccessGreen
import com.safesteps.app.utils.TimerConstants

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TimerScreen(
    selectedMinutes: Int,
    remainingSeconds: Int,
    isRunning: Boolean,
    recentDurations: List<Int>,
    onDurationSelected: (Int) -> Unit,
    onStartTimer: () -> Unit,
    onCancelTimer: () -> Unit
) {
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )
    val smsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    val totalSeconds = selectedMinutes * TimerConstants.SecondsPerMinute
    val rawFraction = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds.toFloat() else 1f

    val animatedFraction by animateFloatAsState(
        targetValue = rawFraction,
        animationSpec = tween(durationMillis = 600),
        label = "timer_fraction"
    )

    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = when {
        rawFraction > 0.5f -> MaterialTheme.colorScheme.primary
        rawFraction > 0.25f -> WarningColor
        else -> EmergencyRed
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                    1f to MaterialTheme.colorScheme.background
                )
            )
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        SafeStepsScreenTitle(titleRes = R.string.timer_title)

        Text(
            text = stringResource(id = R.string.timer_desc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Circular arc timer
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = 18.dp.toPx()
                val inset = stroke / 2f
                val arcSize = Size(size.width - stroke, size.height - stroke)
                val topLeft = Offset(inset, inset)

                drawArc(
                    color = trackColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
                if (animatedFraction > 0f) {
                    drawArc(
                        color = progressColor,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedFraction,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AnimatedContent(
                    targetState = formatTime(
                        if (isRunning) remainingSeconds
                        else selectedMinutes * TimerConstants.SecondsPerMinute
                    ),
                    transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) },
                    label = "time_display"
                ) { timeText ->
                    Text(
                        text = timeText,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = if (isRunning) "remaining" else "duration",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                if (isRunning) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "● ACTIVE",
                        style = MaterialTheme.typography.labelSmall,
                        color = progressColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }
        }

        // Duration slider — hidden while timer is running
        if (!isRunning) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.timer_duration_label, selectedMinutes),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "$selectedMinutes min",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = selectedMinutes.toFloat(),
                        onValueChange = { value -> onDurationSelected(value.toInt()) },
                        valueRange = TimerConstants.MinimumTimerDurationMinutes.toFloat()..
                                TimerConstants.MaximumTimerDurationMinutes.toFloat(),
                        steps = TimerConstants.SliderSteps,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        }

        // Recent duration chips
        if (recentDurations.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.timer_recent_durations),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(recentDurations) { minutes ->
                        FilterChip(
                            selected = minutes == selectedMinutes && !isRunning,
                            onClick = { if (!isRunning) onDurationSelected(minutes) },
                            label = {
                                Text(
                                    text = stringResource(id = R.string.timer_recent_duration, minutes),
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            enabled = !isRunning,
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Start / Stop button
        Button(
            onClick = {
                if (isRunning) {
                    onCancelTimer()
                } else {
                    requestNotificationPermissionIfNeeded(context, notificationPermissionLauncher)
                    requestSmsPermissionIfNeeded(context, smsPermissionLauncher)
                    if (!locationPermissionState.status.isGranted) {
                        locationPermissionState.launchPermissionRequest()
                    }
                    onStartTimer()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning) EmergencyRed else SuccessGreen
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Text(
                text = if (isRunning)
                    stringResource(id = R.string.btn_stop_timer)
                else
                    stringResource(id = R.string.btn_start_timer),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

private val WarningColor = Color(0xFFFF8F00)

private fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / TimerConstants.SecondsPerMinute
    val seconds = totalSeconds % TimerConstants.SecondsPerMinute
    return "%02d:%02d".format(minutes, seconds)
}

private fun requestNotificationPermissionIfNeeded(
    context: android.content.Context,
    launcher: androidx.activity.result.ActivityResultLauncher<String>
) {
    if (
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
        PackageManager.PERMISSION_GRANTED
    ) {
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

private fun requestSmsPermissionIfNeeded(
    context: android.content.Context,
    launcher: androidx.activity.result.ActivityResultLauncher<String>
) {
    if (
        ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) !=
        PackageManager.PERMISSION_GRANTED
    ) {
        launcher.launch(Manifest.permission.SEND_SMS)
    }
}
