package com.github.wnebyte.workoutapp.ui.exercisedetails

import java.util.*
import android.util.Log
import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.ui.AbstractExerciseEditViewModel

class ExerciseDetailsViewModel(private val state: SavedStateHandle) :
    AbstractExerciseEditViewModel(state) {

    override val TAG = "ExerciseDetailsViewModel"

    fun loadExercise(exerciseId: UUID) {
        Log.i(TAG, "Loading exercise: $exerciseId")
        exerciseIdLiveData.value = exerciseId
    }
}