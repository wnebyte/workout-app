package com.github.wnebyte.workoutapp.ui.workout.session

import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.database.Repository
import com.github.wnebyte.workoutapp.model.Set
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import java.util.*

private const val TAG = "WorkoutViewModel"
private const val WORKOUT_ID_LIVE_DATA_KEY = "WorkoutIdLiveData"

class SessionViewModel(private val state: SavedStateHandle) : ViewModel() {

    private val repository = Repository.get()

    private val workoutIdLiveData = state.getLiveData<UUID>(WORKOUT_ID_LIVE_DATA_KEY)

    var workoutLiveData: LiveData<WorkoutWithExercises?> = (
            Transformations.switchMap(workoutIdLiveData) { workoutId ->
                repository.getWorkoutWithExercises(workoutId)
            })
        .distinctUntilChanged()

    fun loadWorkout(workoutId: UUID) {
        workoutIdLiveData.value = workoutId
    }

    fun saveSet(set: Set) {
        repository.saveSet(set)
    }

}