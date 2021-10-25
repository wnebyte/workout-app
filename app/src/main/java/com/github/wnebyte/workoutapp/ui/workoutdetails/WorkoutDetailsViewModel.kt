package com.github.wnebyte.workoutapp.ui.workoutdetails

import android.util.Log
import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.database.Repository
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import java.util.*

private const val TAG = "WorkoutDetailsViewModel"
private const val WORKOUT_ID_LIVE_DATA_KEY = "WorkoutIdLiveData"
private const val DATE_KEY = "Date"

class WorkoutDetailsViewModel(private val state: SavedStateHandle): ViewModel() {

    private val repository = Repository.get()

    private val workoutIdLiveData = state.getLiveData<UUID>(WORKOUT_ID_LIVE_DATA_KEY)

    var workoutLiveData: LiveData<WorkoutWithExercises?> = (
            Transformations.switchMap(workoutIdLiveData) { workoutLiveData ->
                repository.getWorkoutWithExercises(workoutLiveData)
            })
        .distinctUntilChanged()

    var date: String? = state.get<String?>(DATE_KEY)

    fun saveDate() {
        state.set(DATE_KEY, date)
    }

    fun loadWorkout(workoutId: UUID) {
        Log.i(TAG, "Loading: $workoutId")
        workoutIdLiveData.value = workoutId
    }

    fun saveWorkout(workout: WorkoutWithExercises) {
        Log.i(TAG, "Saving: ${workout.workout.id}")
        repository.saveWorkout(workout.workout)
        workout.exercises.forEach { exercise ->
            repository.saveExercise(exercise.exercise)
            repository.saveSet(exercise.sets)
        }
    }

    fun deleteExercise(exercise: ExerciseWithSets) {
        Log.i(TAG, "Deleting exercise: ${exercise.exercise.id}")
        repository.deleteExercise(exercise.exercise)
    }
}