package com.example.android.eggtimernotifications.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.android.eggtimernotifications.R
import com.example.android.eggtimernotifications.util.notificationManager
import com.example.android.eggtimernotifications.util.sendNotification

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(ctx: Context, intent: Intent) {
        ctx.notificationManager().sendNotification(ctx.getText(R.string.eggs_ready).toString(), ctx)
    }
}