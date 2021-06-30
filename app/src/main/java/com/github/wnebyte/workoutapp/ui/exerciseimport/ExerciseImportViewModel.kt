package com.github.wnebyte.workoutapp.ui.exerciseimport

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.github.wnebyte.workoutapp.Repository
import com.github.wnebyte.workoutapp.model.ExerciseWithSets

private const val TAG = "ExerciseImportViewModel"
private const val SELECTED_POSITIONS_KEY = "SelectedPositions"

class ExerciseImportViewModel(private val state: SavedStateHandle): ViewModel() {

    private val repository = Repository.get()

    val exerciseListLiveData: LiveData<List<ExerciseWithSets>> = (
            repository.getOrderedTemplateExercisesWithSets()
            )
        .distinctUntilChanged()

    val selectedPositions: MutableSet<Int> = state.get<MutableSet<Int>>(SELECTED_POSITIONS_KEY) ?:
        mutableSetOf<Int>().apply {
            state.set(SELECTED_POSITIONS_KEY, this)
        }

    fun saveExercise(exercise: ExerciseWithSets) {
        Log.i(TAG, "Saving: ${exercise.exercise.id}")
        repository.saveExercise(exercise.exercise)
        repository.saveSet(exercise.sets)
    }

    fun saveSelectedPositions() {
        Log.i(TAG, "Saving selected positions: $selectedPositions")
        state.set(SELECTED_POSITIONS_KEY, selectedPositions)
    }
}