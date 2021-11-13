package com.github.wnebyte.workoutapp.ui.exercisecreate

import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.database.Repository
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.Set
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
            repository.saveExercise(exercise.exercise)
            exerciseIdLiveData.value = exercise.exercise.id
        }
    }

    fun saveExercise(exercise: ExerciseWithSets) {
        repository.updateExercise(exercise.exercise)
        repository.saveSet(exercise.sets)
    }

    fun deleteExercise(exercise: ExerciseWithSets) {
        repository.deleteExercise(exercise.exercise)
        repository.deleteSet(exercise.sets)
    }

    fun deleteSets(sets: List<Set>) {
        repository.deleteSet(sets)
    }
}