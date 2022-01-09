package com.example.android.eggtimernotifications.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import com.example.android.eggtimernotifications.receiver.AlarmReceiver

class AlarmService(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notifyIntent = Intent(context, AlarmReceiver::class.java)
    private var notifyPendingIntent: PendingIntent? = null

    fun isAlarmOn():Boolean = getPendingIntent(PendingIntent.FLAG_NO_CREATE) != null

    fun setAlarm(triggerTime: Long) {
        notifyPendingIntent = getPendingIntent(PendingIntent.FLAG_UPDATE_CURRENT)
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerTime,
            notifyPendingIntent!!
        )
    }

    fun cancelAlarm() {
        notifyPendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    private fun getPendingIntent(flags: Int): PendingIntent? {
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            notifyIntent,
            flags
        )
    }

    companion object {
        private const val REQUEST_CODE = 0
    }
}