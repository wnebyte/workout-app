package com.github.wnebyte.workoutapp.ui

import java.util.*
import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.database.Repository
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.Set

abstract class AbstractExerciseEditViewModel(private val state: SavedStateHandle): ViewModel() {

    protected open val TAG = "AbstractExerciseEditViewModel"

    protected val EXERCISE_ID_LIVE_DATA_KEY = "ExerciseIdLiveData"

    protected val repository = Repository.get()

    protected val exerciseIdLiveData = state.getLiveData<UUID>(EXERCISE_ID_LIVE_DATA_KEY)

    var exerciseLiveData: LiveData<ExerciseWithSets?> = (
            Transformations.switchMap(exerciseIdLiveData) { exerciseId ->
                repository.getExerciseWithSets(exerciseId)
            })
        .distinctUntilChanged()

    open fun saveExercise(exercise: ExerciseWithSets) {
        repository.updateExercise(exercise.exercise)
        repository.saveSet(exercise.sets)
    }

    open fun deleteSets(sets: List<Set>) {
        repository.deleteSet(sets)
    }
}