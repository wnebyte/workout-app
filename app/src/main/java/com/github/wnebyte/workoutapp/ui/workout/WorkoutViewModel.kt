package com.github.wnebyte.workoutapp.ui.workout

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class WorkoutViewModel(private val state: SavedStateHandle): ViewModel() {

    private var countDownTimer: CountDownTimer? = null

    private val _isRunning = MutableLiveData(false)

    private val _seconds = MutableLiveData(0)

    private val _hours = MutableLiveData(0)

    val isRunning: LiveData<Boolean> get() = _isRunning

    val seconds: LiveData<Int> get() = _seconds

    val hours: LiveData<Int> get() = _hours



}