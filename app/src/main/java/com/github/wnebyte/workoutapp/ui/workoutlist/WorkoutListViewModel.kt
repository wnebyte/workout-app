package com.github.wnebyte.workoutapp.ui.workoutlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.github.wnebyte.workoutapp.Repository
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import java.util.*

private const val TAG = "WorkoutListViewModel"
private const val WORKOUTLIST_LIVE_DATA_KEY = "WorkoutListLiveData"

class WorkoutListViewModel(private val state: SavedStateHandle): ViewModel() {

    private val repository = Repository.get()

    val workoutListLiveData: LiveData<List<WorkoutWithExercises>> =
        repository.getTemplateWorkoutsWithExercises()
            .distinctUntilChanged()

    fun deleteWorkout(workout: WorkoutWithExercises) {
        repository.deleteWorkout(workout)
        workout.exercises.forEach { exercise ->
            repository.deleteExercise(exercise.exercise)
            repository.deleteSet(exercise.sets)
        }
    }
}