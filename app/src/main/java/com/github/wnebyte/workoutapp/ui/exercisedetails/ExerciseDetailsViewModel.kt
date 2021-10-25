package com.github.wnebyte.workoutapp.ui.exercisedetails

import android.util.Log
import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.database.Repository
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.Set
import java.util.*

private const val TAG = "ExerciseDetailsViewModel"
private const val EXERCISE_ID_LIVE_DATA_KEY = "ExerciseIdLiveData"

class ExerciseDetailsViewModel(private val state: SavedStateHandle): ViewModel() {

    private val repository = Repository.get()

    private val exerciseIdLiveData = state.getLiveData<UUID>(EXERCISE_ID_LIVE_DATA_KEY)

    var exerciseLiveData: LiveData<ExerciseWithSets?> = (
            Transformations.switchMap(exerciseIdLiveData) { exerciseId ->
                repository.getExerciseWithSets(exerciseId)
            })
        .distinctUntilChanged()

    fun loadExercise(exerciseId: UUID) {
        Log.i(TAG, "Loading exercise: $exerciseId")
        exerciseIdLiveData.value = exerciseId
    }

    fun saveExercise(exercise: ExerciseWithSets) {
        Log.i(TAG, "Saving exercise: ${exercise.exercise.id}")
        repository.saveExercise(exercise.exercise)
        repository.saveSet(exercise.sets)
    }

    fun deleteSet(set: Set) {
        Log.i(TAG, "Deleting set: ${set.id}")
        repository.deleteSet(set)
    }

    fun saveSet(set: Set) {
        Log.i(TAG, "Saving set: ${set.id}")
        repository.saveSet(set)
    }
}