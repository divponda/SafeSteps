package com.safesteps.app.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import com.safesteps.app.R
import com.safesteps.app.ui.components.SafeStepsCard
import com.safesteps.app.ui.components.SafeStepsPrimaryButton
import com.safesteps.app.ui.components.StatusPill
import com.safesteps.app.utils.TimerConstants

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
    val totalSeconds = selectedMinutes * TimerConstants.SecondsPerMinute
    val timerProgress = if (isRunning && totalSeconds > 0) {
        remainingSeconds.toFloat() / totalSeconds.toFloat()
    } else {
        0f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(dimensionResource(id = R.dimen.screen_timer_padding)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SafeStepsCard(elevation = dimensionResource(id = R.dimen.contact_card_elevation)) {
            StatusPill(
                text = if (isRunning) {
                    stringResource(id = R.string.timer_status_active)
                } else {
                    stringResource(id = R.string.timer_status_ready)
                },
                color = if (isRunning) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
            Text(
                text = stringResource(id = R.string.timer_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = dimensionResource(id = R.dimen.spacing_medium))
            )
            Text(
                text = stringResource(id = R.string.timer_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = dimensionResource(id = R.dimen.spacing_small))
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_xlarge)))

        SafeStepsCard {
            Box(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.timer_progress_size))
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { timerProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = if (isRunning) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isRunning) {
                            val minutes = remainingSeconds / TimerConstants.SecondsPerMinute
                            val seconds = remainingSeconds % TimerConstants.SecondsPerMinute
                            stringResource(id = R.string.timer_remaining_time, minutes, seconds)
                        } else {
                            stringResource(id = R.string.timer_duration_label, selectedMinutes)
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(id = R.string.timer_countdown_label),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_large)))

            Text(
                text = stringResource(id = R.string.timer_duration_label, selectedMinutes),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Slider(
                value = selectedMinutes.toFloat(),
                onValueChange = { value -> onDurationSelected(value.toInt()) },
                valueRange = TimerConstants.MinimumTimerDurationMinutes.toFloat()..
                    TimerConstants.MaximumTimerDurationMinutes.toFloat(),
                steps = TimerConstants.SliderSteps,
                enabled = !isRunning,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = R.dimen.spacing_large))
            )

            if (recentDurations.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.timer_recent_durations),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth()
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(id = R.dimen.spacing_small)),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_small))
                ) {
                    items(recentDurations) { minutes ->
                        FilterChip(
                            selected = selectedMinutes == minutes,
                            onClick = { onDurationSelected(minutes) },
                            enabled = !isRunning,
                            label = {
                                Text(text = stringResource(id = R.string.timer_recent_duration, minutes))
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_xlarge)))

        SafeStepsPrimaryButton(
            textRes = if (isRunning) R.string.btn_stop_timer else R.string.btn_start_timer,
            onClick = {
                if (isRunning) {
                    onCancelTimer()
                } else {
                    requestNotificationPermissionIfNeeded(context, notificationPermissionLauncher)
                    onStartTimer()
                }
            },
            colors = if (isRunning) {
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            } else {
                null
            }
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_xxlarge)))

    }
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
