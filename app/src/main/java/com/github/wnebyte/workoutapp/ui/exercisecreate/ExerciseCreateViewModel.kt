package com.github.wnebyte.workoutapp.ui.exercisecreate

import java.util.*
import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.ui.AbstractExerciseEditViewModel

class ExerciseCreateViewModel(private val state: SavedStateHandle):
    AbstractExerciseEditViewModel(state) {

    override val TAG = "ExerciseCreateViewModel"

    fun loadExercise(workoutId: UUID?) {
        if (state.contains(EXERCISE_ID_LIVE_DATA_KEY)) {
            return
        } else {
            val exercise = ExerciseWithSets.newInstance(workoutId)
            repository.saveExercise(exercise.exercise)
            exerciseIdLiveData.value = exercise.exercise.id
        }
    }

    fun deleteExercise(exercise: ExerciseWithSets) {
        repository.deleteExercise(exercise.exercise)
        repository.deleteSet(exercise.sets)
    }
}