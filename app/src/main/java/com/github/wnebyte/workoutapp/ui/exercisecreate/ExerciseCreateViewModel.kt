package com.github.wnebyte.workoutapp.ui.exercisecreate

import android.util.Log
import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.Repository
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import java.util.*

private const val TAG = "ExerciseCreateViewModel"
private const val EXERCISE_ID_LIVE_DATA_KEY = "ExerciseIdLiveData"

class ExerciseCreateViewModel(private val state: SavedStateHandle): ViewModel() {

    private val repository = Repository.get()

    private val exerciseIdLiveData = state.getLiveData<UUID>(EXERCISE_ID_LIVE_DATA_KEY)

    var exerciseLiveData: LiveData<ExerciseWithSets?> = (
            Transformations.switchMap(exerciseIdLiveData) { exerciseId ->
                repository.getExerciseWithSets(exerciseId)
            })
        .distinctUntilChanged()

    fun loadExercise(workoutId: UUID?) {
        if (state.contains(EXERCISE_ID_LIVE_DATA_KEY)) {
            return
        } else {
            val exercise = ExerciseWithSets.newInstance(workoutId)
            saveExercise(exercise)
            exerciseIdLiveData.value = exercise.exercise.id
        }
    }

    fun saveExercise(exercise: ExerciseWithSets) {
        Log.i(TAG, "Saving exercise: ${exercise.exercise.id}")
        repository.saveExercise(exercise.exercise)
        repository.saveSet(exercise.sets)
    }

    fun deleteExercise(exercise: ExerciseWithSets) {
        Log.i(TAG, "Deleting exercise: ${exercise.exercise.id}")
        repository.deleteExercise(exercise.exercise)
        repository.deleteSet(exercise.sets)
    }
}