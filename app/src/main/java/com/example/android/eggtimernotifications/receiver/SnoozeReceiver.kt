package com.example.android.eggtimernotifications.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.text.format.DateUtils
import com.example.android.eggtimernotifications.service.AlarmService

class SnoozeReceiver : BroadcastReceiver() {

    override fun onReceive(ctx: Context, intent: Intent) {
        val triggerTime = SystemClock.elapsedRealtime() + DateUtils.MINUTE_IN_MILLIS
        AlarmService(ctx).setAlarm(triggerTime)
    }
}