package com.example.android.eggtimernotifications.util

import android.annotation.SuppressLint
import android.app.Notification
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

private const val NOTIFICATION_ID = 0
private const val REQUEST_CODE = 0

fun Context.notificationManager() =
    ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager

fun NotificationManager.sendNotification(messageBody: String, appContext: Context) {
    notify(NOTIFICATION_ID, createNotification(messageBody, appContext))
}

fun createNotification(messageBody: String, appContext: Context): Notification {
    val builder = NotificationCompat.Builder(
        appContext,
        appContext.getString(R.string.egg_notification_channel_id)
    )
        // set title, text and icon
        .setSmallIcon(R.drawable.cooked_egg)
        .setContentTitle(appContext.getString(R.string.notification_title))
        .setContentText(messageBody)

        // add style
        .setStyle(bigPictureStyle(appContext))
        .setLargeIcon(getEggImage(appContext))

        // set content intent
        .setContentIntent(contentPendingIntent(appContext))
        .setAutoCancel(true)

        // add snooze action
        .addAction(
            R.drawable.egg_icon,
            appContext.getString(R.string.snooze),
            snoozePendingIntent(appContext)
        )

        .setPriority(NotificationCompat.PRIORITY_HIGH)

    return builder.build()
}

@SuppressLint("UnspecifiedImmutableFlag")
fun snoozePendingIntent(appContext: Context): PendingIntent {
    val snoozeIntent = Intent(appContext, SnoozeReceiver::class.java)
    return PendingIntent.getBroadcast(
        appContext,
        REQUEST_CODE,
        snoozeIntent,
        PendingIntent.FLAG_ONE_SHOT
    )
}

@SuppressLint("UnspecifiedImmutableFlag")
fun contentPendingIntent(appContext: Context): PendingIntent {
    val contentIntent = Intent(appContext, MainActivity::class.java)
    return PendingIntent.getActivity(
        appContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}

private fun bigPictureStyle(appContext: Context) =
    NotificationCompat.BigPictureStyle()
        .bigPicture(getEggImage(appContext))
        .bigLargeIcon(null)

private fun getEggImage(appContext: Context) =
    BitmapFactory.decodeResource(appContext.resources, R.drawable.cooked_egg)
