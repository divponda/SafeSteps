package com.safesteps.app.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.safesteps.app.ui.components.SafeStepsPrimaryButton
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.screen_timer_padding)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.timer_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = stringResource(id = R.string.timer_desc),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.spacing_large))
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_huge)))

        Text(
            text = stringResource(id = R.string.timer_duration_label, selectedMinutes),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium
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
                    OutlinedButton(
                        onClick = { onDurationSelected(minutes) },
                        enabled = !isRunning
                    ) {
                        Text(text = stringResource(id = R.string.timer_recent_duration, minutes))
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
            }
        )

        if (isRunning) {
            val minutes = remainingSeconds / TimerConstants.SecondsPerMinute
            val seconds = remainingSeconds % TimerConstants.SecondsPerMinute
            Text(
                text = stringResource(id = R.string.timer_remaining_time, minutes, seconds),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = dimensionResource(id = R.dimen.spacing_large)),
                fontWeight = FontWeight.Bold
            )
        }

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
