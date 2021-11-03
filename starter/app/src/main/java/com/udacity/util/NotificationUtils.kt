package com.udacity.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R

// Notification ID.
private const val NOTIFICATION_ID = 0
private const val REQUEST_CODE = 0

/**
 * Builds and delivers the notification.
 *
 * @param context, activity context.
 */
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    // Create the intent for the notification, which launches DetailActivity

    val intent = Intent(applicationContext, DetailActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    intent.putExtra("intentExtra", "I made it!")

    val openActivityIntent =
        PendingIntent.getActivity(applicationContext, REQUEST_CODE, intent, PendingIntent.FLAG_ONE_SHOT)

    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
        .setContentTitle(
            applicationContext
                .getString(R.string.notification_title)
        )
        .setContentText(applicationContext.getString(R.string.notification_description))
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_baseline_cloud_download_24,
            "Check the status",
            openActivityIntent
        )
        .setPriority(NotificationCompat.PRIORITY_LOW)

    notify(NOTIFICATION_ID, builder.build())
}

/**
 * Cancels all notifications.
 *
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}
