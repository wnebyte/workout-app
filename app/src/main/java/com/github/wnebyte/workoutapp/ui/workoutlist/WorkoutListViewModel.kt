package com.github.wnebyte.workoutapp.ui.workoutlist

import android.util.Log
import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.database.Repository
import com.github.wnebyte.workoutapp.model.Workout

private const val TAG = "WorkoutListViewModel"

private const val FILTER_KEY = "FilteredData"

class WorkoutListViewModel(private val state: SavedStateHandle): ViewModel() {

    private val repository = Repository.get()

    val workoutListLiveData: LiveData<List<Workout>> =
        state.getLiveData<String>(FILTER_KEY).switchMap { filter ->
            when (filter) {
                "completed" -> {
                    repository.getCompletedWorkoutsOrderByDate(false)
                }
                "uncompleted" -> {
                    repository.getNonCompletedWorkoutsOrderByDate(false)
                }
                else -> {
                    repository.getWorkoutsOrderByDate(false)
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

    fun deleteWorkout(workout: Workout) {
        Log.i(TAG, "Deleting workout: ${workout.id}")
        repository.deleteWorkout(workout)
    }
}