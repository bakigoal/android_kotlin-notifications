package com.example.android.eggtimernotifications.util

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.android.eggtimernotifications.MainActivity
import com.example.android.eggtimernotifications.R
import com.example.android.eggtimernotifications.receiver.SnoozeReceiver

// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

fun Context.notificationManager() =
    ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager

// TODO: Step 1.1 extension function to send messages (GIVEN)
/**
 * Builds and delivers the notification.
 *
 * @param context, activity context.
 */
@SuppressLint("UnspecifiedImmutableFlag")
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    // Create the content intent for the notification, which launches
    // this activity
    // TODO: Step 1.11 create intent
    val contentIntent = Intent(applicationContext, MainActivity::class.java)

    // TODO: Step 1.12 create PendingIntent
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // TODO: Step 2.0 add style

    // TODO: Step 2.2 add snooze action

    // TODO: Step 1.2 get an instance of NotificationCompat.Builder
    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.egg_notification_channel_id)
    )

    // TODO: Step 1.8 use the new 'breakfast' notification channel

    // TODO: Step 1.3 set title, text and icon to builder
    builder
        .setSmallIcon(R.drawable.cooked_egg)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)

    // TODO: Step 1.13 set content intent
    builder
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)

    // TODO: Step 2.1 add style to builder

    // TODO: Step 2.3 add snooze action

    // TODO: Step 2.5 set priority

    // TODO: Step 1.4 call notify
    notify(NOTIFICATION_ID, builder.build())
}
