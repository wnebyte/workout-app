package com.github.wnebyte.workoutapp.ui.sessionlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.github.wnebyte.workoutapp.Repository
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises

private const val TAG = "SessionListViewModel"

class SessionListViewModel(private val state: SavedStateHandle): ViewModel() {

    private val repository = Repository.get()

    val sessionLiveData: LiveData<List<WorkoutWithExercises>> =
        repository.getNonCompletedWorkoutsWithExercises()
            .distinctUntilChanged()

}