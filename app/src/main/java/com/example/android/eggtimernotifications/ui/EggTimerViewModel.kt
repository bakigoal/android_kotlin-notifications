package com.example.android.eggtimernotifications.ui

import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import android.os.SystemClock
import android.text.format.DateUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.eggtimernotifications.R
import com.example.android.eggtimernotifications.service.AlarmService
import com.example.android.eggtimernotifications.util.notificationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EggTimerViewModel(private val app: Application) : AndroidViewModel(app) {

    companion object {
        private const val TRIGGER_TIME = "TRIGGER_AT"
        private const val PREFS_NAME = "com.example.android.eggtimernotifications"
    }

    private val timerLengthOptions: IntArray = app.resources.getIntArray(R.array.minutes_array)
    private val alarmService = AlarmService(app)
    private var prefs = app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val notificationManager = app.notificationManager()

    private lateinit var timer: CountDownTimer

    // LiveData properties
    private val _timeSelection = MutableLiveData<Int>()
    val timeSelection: LiveData<Int>
        get() = _timeSelection

    private val _elapsedTime = MutableLiveData<Long>()
    val elapsedTime: LiveData<Long>
        get() = _elapsedTime

    private var _isAlarmOn = MutableLiveData<Boolean>()
    val isAlarmOn: LiveData<Boolean>
        get() = _isAlarmOn

    init {
        _isAlarmOn.value = alarmService.isAlarmOn()
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

    private fun startTimer(timerLengthSelection: Int) {
        _isAlarmOn.value?.let {
            if (!it) {
                val selectedInterval = getTimerIntervalInMillis(timerLengthSelection)
                val triggerTime = SystemClock.elapsedRealtime() + selectedInterval

                notificationManager.cancelAll()
                alarmService.setAlarm(triggerTime)
                _isAlarmOn.value = true

                viewModelScope.launch {
                    saveTime(triggerTime)
                }
            }
        }
        createTimer()
    }

    private fun getTimerIntervalInMillis(timerSelection: Int) =
        when (timerSelection) {
            0 -> DateUtils.SECOND_IN_MILLIS * 10 //For testing only
            else -> timerLengthOptions[timerSelection] * DateUtils.MINUTE_IN_MILLIS
        }

    private fun createTimer() {
        viewModelScope.launch {
            val triggerTime = loadTime()
            timer = object : CountDownTimer(triggerTime, DateUtils.SECOND_IN_MILLIS) {

                override fun onTick(millisUntilFinished: Long) {
                    _elapsedTime.value = triggerTime - SystemClock.elapsedRealtime()
                    if (_elapsedTime.value!! <= 0) {
                        onFinish()
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
        alarmService.cancelAlarm()
    }

    private fun resetTimer() {
        timer.cancel()
        _elapsedTime.value = 0
        _isAlarmOn.value = false
    }

    // Shared Preferences methods

    private suspend fun saveTime(triggerTime: Long) = withContext(Dispatchers.IO) {
        prefs.edit().putLong(TRIGGER_TIME, triggerTime).apply()
    }

    private suspend fun loadTime(): Long = withContext(Dispatchers.IO) {
        prefs.getLong(TRIGGER_TIME, 0)
    }
}