package com.github.wnebyte.workoutapp.ui.workoutcreate

import com.github.wnebyte.workoutapp.Repository
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import android.util.Log
import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import java.util.*

private const val TAG = "WorkoutCreateViewModel"
private const val WORKOUT_ID_LIVE_DATA_KEY = "WorkoutIdLiveData"
private const val DATE_KEY = "Date"
private const val REMINDER_KEY = "Reminder"

class WorkoutCreateViewModel(private val state: SavedStateHandle): ViewModel() {

    private val repository = Repository.get()

    private val workoutIdLiveData = state.getLiveData<UUID>(WORKOUT_ID_LIVE_DATA_KEY)

    var workoutLiveData: LiveData<WorkoutWithExercises?> = (
            Transformations.switchMap(workoutIdLiveData) { workoutId ->
                repository.getWorkoutWithExercises(workoutId)
            })
        .distinctUntilChanged()

    var date: String? = state.get<String?>(DATE_KEY)

    var reminder: Long? = state.get<Long>(REMINDER_KEY)

    private fun loadWorkout(workoutId: UUID) {
        workoutIdLiveData.value = workoutId
    }
    fun saveDate() {
        state.set(DATE_KEY, date)
    }

    fun loadWorkout() {
        if (state.contains(WORKOUT_ID_LIVE_DATA_KEY)) {
            return
        } else {
            val workout = WorkoutWithExercises.newInstance()
            saveWorkout(workout)
            loadWorkout(workout.workout.id)
        }
    }

    fun saveWorkout(workout: WorkoutWithExercises) {
        Log.i(TAG, "Saving: ${workout.workout.id}")
        repository.saveWorkout(workout.workout)
        workout.exercises.forEach { exercise ->
            repository.saveExercise(exercise.exercise)
            repository.saveSet(exercise.sets)
        }
    }

    fun deleteWorkout(workout: WorkoutWithExercises) {
        Log.i(TAG, "Deleting: ${workout.workout.id}")
        repository.deleteWorkout(workout.workout)
        workout.exercises.forEach { exercise ->
            repository.deleteExercise(exercise.exercise)
            repository.deleteSet(exercise.sets)
        }
    }

    fun deleteExercise(exercise: ExerciseWithSets) {
        Log.i(TAG, "Deleting exercise: ${exercise.exercise.id}")
        repository.deleteExercise(exercise.exercise)
    }
}