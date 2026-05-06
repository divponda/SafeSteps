package com.safesteps.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.safesteps.app.R
import com.safesteps.app.alerts.EmergencySmsResult
import com.safesteps.app.utils.NotificationConstants

class SafeStepsNotificationManager(private val context: Context) {

    fun createSafetyTimerChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            NotificationConstants.SafetyTimerChannelId,
            context.getString(R.string.notification_channel_safety_timer_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.notification_channel_safety_timer_desc)
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    fun showTimerExpiredNotification() {
        showEmergencySmsResultNotification(EmergencySmsResult.PermissionDenied)
    }

    fun showEmergencySmsResultNotification(result: EmergencySmsResult) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val body = when (result) {
            is EmergencySmsResult.Sent -> {
                when {
                    result.sentCount == result.totalContacts ->
                        context.getString(R.string.notification_sms_sent_all)
                    result.sentCount > 0 ->
                        context.getString(
                            R.string.notification_sms_sent_partial,
                            result.sentCount,
                            result.totalContacts
                        )
                    else -> context.getString(R.string.notification_sms_failed)
                }
            }
            EmergencySmsResult.NoContacts -> context.getString(R.string.timer_alert_no_contacts)
            EmergencySmsResult.NoValidContacts -> context.getString(R.string.timer_alert_no_valid_contacts)
            EmergencySmsResult.PermissionDenied -> context.getString(R.string.notification_sms_permission_denied)
        }

        val notification = NotificationCompat.Builder(
            context,
            NotificationConstants.SafetyTimerChannelId
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.notification_are_you_safe_title))
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(
            NotificationConstants.SafetyTimerExpiredNotificationId,
            notification
        )
    }
}
