package com.github.wnebyte.workoutapp.ui.progressdetails

import java.util.*
import android.util.Log
import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.database.Repository
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import com.github.wnebyte.workoutapp.util.Extensions.Companion.format
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toFirstOfTheMonth
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toLastOfTheMonth

private const val TAG = "ProgressDetailsViewModel"

private const val DATE_KEY = "Date"

class ProgressDetailsViewModel(private val state: SavedStateHandle): ViewModel() {

    private val repository = Repository.get()

    private val dateLiveData: MutableLiveData<Date> = state.get<Long>(DATE_KEY).let {
        return@let when (it) {
            null -> {
                MutableLiveData()
            }
            else -> {
                MutableLiveData(Date(it))
            }
        }
    }

    val exerciseListLiveData: LiveData<List<WorkoutWithExercises>> = (
            Transformations.switchMap(dateLiveData) { date ->
                val from = date.toFirstOfTheMonth()
                val to = date.toLastOfTheMonth()
                Log.i(TAG, "from: ${from.format()}, to: ${date.format()}")
                repository.getWorkoutsWithExercisesCompletedBetweenContainingExercise(
                    "Squat", from, to
                )
            })

    fun loadExercises(date: Date) {
        state[DATE_KEY] = date.time
        dateLiveData.value = date
    }
}