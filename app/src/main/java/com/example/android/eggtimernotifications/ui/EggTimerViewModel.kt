/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.eggtimernotifications.ui

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.SystemClock
import android.text.format.DateUtils
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.*
import com.example.android.eggtimernotifications.R
import com.example.android.eggtimernotifications.receiver.AlarmReceiver
import kotlinx.coroutines.*

class EggTimerViewModel(app: Application) : AndroidViewModel(app) {

    companion object {
        private const val REQUEST_CODE = 0
        private const val TRIGGER_TIME = "TRIGGER_AT"
        private const val PREFS_NAME = "com.example.android.eggtimernotifications"
    }

    private val timerLengthOptions: IntArray = app.resources.getIntArray(R.array.minutes_array)
    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private var prefs = app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val notifyIntent = Intent(app, AlarmReceiver::class.java)
    private val notifyPendingIntent: PendingIntent

    private val _timeSelection = MutableLiveData<Int>()
    val timeSelection: LiveData<Int>
        get() = _timeSelection

    private val _elapsedTime = MutableLiveData<Long>()
    val elapsedTime: LiveData<Long>
        get() = _elapsedTime

    private var _isAlarmOn = MutableLiveData<Boolean>()
    val isAlarmOn: LiveData<Boolean>
        get() = _isAlarmOn


    private lateinit var timer: CountDownTimer

    init {
        _isAlarmOn.value = getPendingIntent(PendingIntent.FLAG_NO_CREATE) != null

        notifyPendingIntent = getPendingIntent(PendingIntent.FLAG_UPDATE_CURRENT)

        //If alarm is not null, resume the timer back for this alarm
        if (_isAlarmOn.value!!) {
            createTimer()
        }
    }

    fun setAlarm(isChecked: Boolean) {
        when (isChecked) {
            true -> timeSelection.value?.let { startTimer(it) }
            false -> cancelNotification()
        }
    }

    fun setTimeSelected(timerLengthSelection: Int) {
        _timeSelection.value = timerLengthSelection
    }

    private fun getPendingIntent(flags: Int) = PendingIntent.getBroadcast(
        getApplication(),
        REQUEST_CODE,
        notifyIntent,
        flags
    )

    private fun startTimer(timerLengthSelection: Int) {
        _isAlarmOn.value?.let {
            if (!it) {
                _isAlarmOn.value = true
                val selectedInterval = getTimerIntervalInMillis(timerLengthSelection)
                val triggerTime = SystemClock.elapsedRealtime() + selectedInterval

                // TODO: Step 1.5 get an instance of NotificationManager and call sendNotification

                // TODO: Step 1.15 call cancel notification

                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager,
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    notifyPendingIntent
                )

                viewModelScope.launch {
                    saveTime(triggerTime)
                }
            }
        }
        createTimer()
    }

    private fun getTimerIntervalInMillis(timerLengthSelection: Int) = when (timerLengthSelection) {
        0 -> DateUtils.SECOND_IN_MILLIS * 10 //For testing only
        else -> timerLengthOptions[timerLengthSelection] * DateUtils.MINUTE_IN_MILLIS
    }

    private fun createTimer() {
        viewModelScope.launch {
            val triggerTime = loadTime()
            timer = object : CountDownTimer(triggerTime, DateUtils.SECOND_IN_MILLIS) {
                override fun onTick(millisUntilFinished: Long) {
                    _elapsedTime.value = triggerTime - SystemClock.elapsedRealtime()
                    if (_elapsedTime.value!! <= 0) {
                        resetTimer()
                    }
                }

                override fun onFinish() {
                    resetTimer()
                }
            }
            timer.start()
        }
    }

    /**
     * Cancels the alarm, notification and resets the timer
     */
    private fun cancelNotification() {
        resetTimer()
        alarmManager.cancel(notifyPendingIntent)
    }

    /**
     * Resets the timer on screen and sets alarm value false
     */
    private fun resetTimer() {
        timer.cancel()
        _elapsedTime.value = 0
        _isAlarmOn.value = false
    }

    private suspend fun saveTime(triggerTime: Long) =
        withContext(Dispatchers.IO) {
            prefs.edit().putLong(TRIGGER_TIME, triggerTime).apply()
        }

    private suspend fun loadTime(): Long =
        withContext(Dispatchers.IO) {
            prefs.getLong(TRIGGER_TIME, 0)
        }
}