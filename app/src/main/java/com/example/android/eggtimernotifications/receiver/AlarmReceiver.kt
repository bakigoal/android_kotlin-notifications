package com.example.android.eggtimernotifications.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.android.eggtimernotifications.R
import com.example.android.eggtimernotifications.util.sendNotification

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(ctx: Context, intent: Intent) {
        notificationManager(ctx).sendNotification(ctx.getText(R.string.eggs_ready).toString(), ctx)
    }

    private fun notificationManager(ctx: Context) =
        ContextCompat.getSystemService(ctx, NotificationManager::class.java) as NotificationManager

}