package com.github.wnebyte.workoutapp.ui.exerciselist

import android.util.Log
import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.database.Repository
import com.github.wnebyte.workoutapp.model.ExerciseWithSets

private const val TAG = "ExerciseListViewModel"

class ExerciseListViewModel(private val state: SavedStateHandle): ViewModel() {

    private val repository = Repository.get()

    val exerciseListLiveData: LiveData<List<ExerciseWithSets>> =
        repository.getOrderedTemplateExercisesWithSets()
            .apply {
                distinctUntilChanged()
            }

    fun saveExercise(exercise: ExerciseWithSets) {
        repository.saveExercise(exercise.exercise)
        repository.saveSet(exercise.sets)
    }

    fun deleteExercise(exercise: ExerciseWithSets) {
        Log.i(TAG, "Deleting: ${exercise.exercise.id}")
        repository.deleteExercise(exercise.exercise)
        repository.deleteSet(exercise.sets)
    }

    fun deleteExercises(exercises: List<ExerciseWithSets>) {
        exercises.forEach { exercise ->
            repository.deleteExercise(exercise.exercise)
            repository.deleteSet(exercise.sets)
        }
    }
}