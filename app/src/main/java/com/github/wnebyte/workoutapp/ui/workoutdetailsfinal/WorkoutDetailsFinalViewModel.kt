package com.github.wnebyte.workoutapp.ui.workoutdetailsfinal

import java.util.*
import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.database.Repository
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises

private const val TAG = "WorkoutDetailsFinalViewModel"
private const val WORKOUT_ID_LIVE_DATA_KEY = "WorkoutIdLiveData"

class WorkoutDetailsFinalViewModel(private val state: SavedStateHandle) : ViewModel() {

    private val repository = Repository.get()

    private val workoutIdLiveData = state.getLiveData<UUID>(WORKOUT_ID_LIVE_DATA_KEY)

    val workoutLiveData: LiveData<WorkoutWithExercises?> = (
            Transformations.switchMap(workoutIdLiveData) { workoutId ->
                when (workoutId) {
                    null -> {
                        repository.getMostRecentlyCompletedWorkoutWithExercises()
                    }
                    else -> {
                        repository.getWorkoutWithExercises(workoutId)
                    }
                }
            })
        .distinctUntilChanged()

    fun loadWorkout(workoutId: UUID?) {
        workoutIdLiveData.value = workoutId
    }
}