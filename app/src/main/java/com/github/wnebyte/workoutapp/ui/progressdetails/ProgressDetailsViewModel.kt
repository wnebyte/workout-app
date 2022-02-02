package com.github.wnebyte.workoutapp.ui.progressdetails

import java.util.*
import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.database.Repository
import com.github.wnebyte.workoutapp.model.ExerciseWithSets

private const val TAG = "ProgressDetailsViewModel"

private const val EXERCISE_ID_LIVE_DATA_KEY = "ExerciseIdLiveData"

class ProgressDetailsViewModel(private val state: SavedStateHandle) : ViewModel() {

    private val repository = Repository.get()

    private val exerciseIdLiveData = state.getLiveData<UUID>(EXERCISE_ID_LIVE_DATA_KEY)

    val exerciseLiveData: LiveData<ExerciseWithSets?> = (
            Transformations.switchMap(exerciseIdLiveData) { id ->
                repository.getExerciseWithSets(id)
            })
        .distinctUntilChanged()

    fun loadExercise(exerciseId: UUID) {
        exerciseIdLiveData.value = exerciseId
    }
}