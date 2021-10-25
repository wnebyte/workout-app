package com.github.wnebyte.workoutapp.ui.workout.stopwatch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val TAG = "StopwatchViewModel"
private const val VALUE_KEY = "Value"
private const val INDEX_KEY = "DisplayedChildIndex"

class StopwatchViewModel(private val state: SavedStateHandle) : ViewModel() {

    var value: Long = state.get<Long>(VALUE_KEY) ?: initializeValue()
        set(value) {
            state.set<Long>(VALUE_KEY, value)
            field = value
        }

    var index: Int = state.get<Int>(INDEX_KEY) ?: initializeIndex()
        set(value) {
            state.set<Int>(INDEX_KEY, value)
            field = value
        }

    private fun initializeValue(): Long {
        val value = 0L
        state.set<Long>(VALUE_KEY, value)
        return value
    }

    private fun initializeIndex(): Int {
        val index = 0
        state.set<Int>(INDEX_KEY, index)
        return index
    }
}