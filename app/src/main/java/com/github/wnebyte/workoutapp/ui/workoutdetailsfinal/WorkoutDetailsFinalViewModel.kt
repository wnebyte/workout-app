package com.github.wnebyte.workoutapp.ui.workoutdetailsfinal

import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.database.Repository
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import java.util.*

private const val TAG = "WorkoutDetailsFinalViewModel"
private const val WORKOUT_ID_LIVE_DATA_KEY = "WorkoutIdLiveData"

class WorkoutDetailsFinalViewModel(private val state: SavedStateHandle) : ViewModel() {

    private val repository = Repository.get()

    private val workoutIdLiveData = state.getLiveData<UUID>(WORKOUT_ID_LIVE_DATA_KEY)

    var workoutListLiveData: LiveData<WorkoutWithExercises?> = (
            Transformations.switchMap(workoutIdLiveData) { workoutLiveData ->
                when (workoutLiveData) {
                    null -> {
                        repository.getMostRecentlyCompletedWorkoutWithExercises()
                    }
                    else -> {
                        repository.getWorkoutWithExercises(workoutLiveData)
                    }
                }
            })
        .distinctUntilChanged()

    fun loadWorkout(workoutId: UUID?) {
        workoutIdLiveData.value = workoutId
    }
}