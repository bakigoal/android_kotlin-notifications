package com.example.android.eggtimernotifications.util

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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

/**
 * Builds and delivers the notification.
 * @param context, activity context.
 */
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    val eggImage = getEggImage(applicationContext)

    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.egg_notification_channel_id)
    )
        // TODO: Step 1.3 set title, text and icon to builder
        .setSmallIcon(R.drawable.cooked_egg)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)

        // TODO: Step 1.13 set content intent
        .setContentIntent(contentPendingIntent(applicationContext))
        .setAutoCancel(true)

        // TODO: Step 2.1 add style to builder
        .setStyle(bigPictureStyle(eggImage))
        .setLargeIcon(eggImage)

        // TODO: Step 2.3 add snooze action
        .addAction(
            R.drawable.egg_icon,
            applicationContext.getString(R.string.snooze),
            snoozePendingIntent(applicationContext)
        )

    // TODO: Step 2.5 set priority


    notify(NOTIFICATION_ID, builder.build())
}

@SuppressLint("UnspecifiedImmutableFlag")
fun snoozePendingIntent(applicationContext: Context): PendingIntent {
    val snoozeIntent = Intent(applicationContext, SnoozeReceiver::class.java)
    return PendingIntent.getBroadcast(
        applicationContext,
        REQUEST_CODE,
        snoozeIntent,
        PendingIntent.FLAG_ONE_SHOT
    )
}

@SuppressLint("UnspecifiedImmutableFlag")
fun contentPendingIntent(applicationContext: Context): PendingIntent {
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    return PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}

private fun bigPictureStyle(image: Bitmap?) =
    NotificationCompat.BigPictureStyle()
        .bigPicture(image)
        .bigLargeIcon(null)

private fun getEggImage(applicationContext: Context) =
    BitmapFactory.decodeResource(applicationContext.resources, R.drawable.cooked_egg)
