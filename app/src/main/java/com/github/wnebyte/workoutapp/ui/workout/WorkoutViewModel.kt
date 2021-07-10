package com.github.wnebyte.workoutapp.ui.workout

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val TAG = "WorkoutViewModel"
private const val MILLIS_KEY = "millis"

class WorkoutViewModel(private val state: SavedStateHandle): ViewModel() {

    var millisInFuture: Long? = state.get<Long>(MILLIS_KEY)

    fun saveMillisInFuture(millisInFuture: Long) {
        this.millisInFuture = millisInFuture
        saveMillisInFuture()
    }

    fun saveMillisInFuture() {
        Log.i(TAG, "Saving millis: $millisInFuture")
        state[MILLIS_KEY] = millisInFuture
    }
}