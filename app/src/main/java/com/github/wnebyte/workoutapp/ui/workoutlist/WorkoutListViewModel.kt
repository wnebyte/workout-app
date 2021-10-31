package com.github.wnebyte.workoutapp.ui.workoutlist

import android.util.Log
import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.database.Repository
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises

private const val TAG = "WorkoutListViewModel"

private const val FILTER_KEY = "FilteredData"

class WorkoutListViewModel(private val state: SavedStateHandle): ViewModel() {

    private val repository = Repository.get()

    val workoutListLiveData: LiveData<List<WorkoutWithExercises>> =
        state.getLiveData<String>(FILTER_KEY).switchMap { filter ->
            when (filter) {
                "completed" -> {
                    repository.getCompletedWorkoutsWithExercises()
                }
                "uncompleted" -> {
                    repository.getUnCompletedWorkoutsWithExercises()
                }
                else -> {
                    repository.getWorkoutsWithExercises()
                }
            }
        }

    init {
        if (!state.contains(FILTER_KEY)) {
            setFilter("all")
        }
    }

    fun setFilter(filter: String) {
        state.set(FILTER_KEY, filter)
    }

    fun deleteWorkout(workout: WorkoutWithExercises) {
        Log.i(TAG, "Deleting workout: ${workout.workout.id}")
        repository.deleteWorkout(workout)
        workout.exercises.forEach { exercise ->
            repository.deleteExercise(exercise.exercise)
            repository.deleteSet(exercise.sets)
        }
    }
}