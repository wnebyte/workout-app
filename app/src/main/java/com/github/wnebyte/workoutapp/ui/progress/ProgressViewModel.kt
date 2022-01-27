package com.github.wnebyte.workoutapp.ui.progress

import android.util.Log
import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.database.Repository
import com.github.wnebyte.workoutapp.ext.Extensions.Companion.avg
import com.github.wnebyte.workoutapp.ext.Extensions.Companion.format
import com.github.wnebyte.workoutapp.ext.Extensions.Companion.toFirstOfLastMonth
import com.github.wnebyte.workoutapp.ext.Extensions.Companion.toLastOfLastMonth
import com.github.wnebyte.workoutapp.ext.Extensions.Companion.toLastOfNextMonth
import com.github.wnebyte.workoutapp.ext.Extensions.Companion.month
import com.github.wnebyte.workoutapp.model.ProgressItem
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import java.util.*
import kotlin.math.max
import kotlin.math.min

private const val TAG = "ProgressViewModel"

private const val DATE_KEY = "Date"

class ProgressViewModel(private val state: SavedStateHandle) : ViewModel() {

    private val repository = Repository.get()

    var dateLiveData: MutableLiveData<Date> = state.get<Long>(DATE_KEY).let {
        return@let when (it) {
            null -> {
                MutableLiveData(Date())
            }
            else -> {
                MutableLiveData(Date(it))
            }
        }
    }

    var progressItemListLiveData: LiveData<List<ProgressItem>> = (
            Transformations.switchMap(dateLiveData) { to ->
                val from = to.toFirstOfLastMonth()
                Log.i(TAG, "from: ${from.format()}, to: ${to.format()}")
                repository.getWorkoutsWithExercisesCompletedBetween(
                    from, to
                ).switchMap {
                        MutableLiveData(transform(it, to.month()))
                    }
            })

    private fun setDate(date: Date) {
        state[DATE_KEY] = date.time
        dateLiveData.value = date
    }

    fun decrementMonthlyRange() {
        val value = dateLiveData.value
        value?.let {
            setDate(it.toLastOfLastMonth())
        }
    }

    fun incrementMonthlyRange() {
        val value = dateLiveData.value
        value?.let {
            setDate(it.toLastOfNextMonth())
        }
    }

    fun getDate(): Date {
        return dateLiveData.value!!
    }

    private fun transform(
        workouts: List<WorkoutWithExercises>,
        refMonth: Int,
    ): List<ProgressItem> {
        val list = mutableListOf<ProgressItem>()
        val partitions: Map<Int, List<ExerciseWithSets>> = partitionByMonth(workouts)
        val partition = partitions[refMonth]

        partition?.let {
            for (name in distinct(partition)) {
                val avg0 = partition
                    .filter { e -> e.exercise.name == name }
                    .map { e -> e.sets.map { s -> s.weights }.avg() }.avg()
                val avg1 = partitions[decrementMonth(refMonth)]
                    ?.filter { e -> e.exercise.name == name }
                    ?.map { e -> e.sets.map { s -> s.weights }.avg() }?.avg() ?: 0.0
                list.add(
                    ProgressItem(
                        name = name,
                        avg = avg0,
                        unit = "kg",
                        change = if (avg0 == 0.0 || avg1 == 0.0) {
                            0.0f
                        } else {
                            val f: Float = (avg0 / avg1).toFloat()
                            (max(f, 1.0f) - min(f, 1.0f)) * 100
                        }
                    )
                )
            }
        }
        return list
    }

    /**
     * Returns a Map consisting of a List of [ExerciseWithSets] associated with the
     * Month that their respective [WorkoutWithExercises] was scheduled to take place.
     */
    private fun partitionByMonth(workouts: List<WorkoutWithExercises>)
    : Map<Int, List<ExerciseWithSets>> {
        val map: MutableMap<Int, MutableList<ExerciseWithSets>> = mutableMapOf()

        for (workout in workouts) {
            val date = workout.workout.date
            date?.let {
                val month = it.month()
                if (!map.containsKey(month)) {
                    map[month] = workout.exercises
                } else {
                    map[month]!!.addAll(workout.exercises)
                }
            }
        }
        return map
    }

    private fun distinct(exercises: List<ExerciseWithSets>?): Set<String> {
        val set = mutableSetOf<String>()
        exercises?.let {
            set.addAll(it.map { e -> e.exercise.name })
        }
        return set
    }

    private fun decrementMonth(value: Int): Int {
        return when (value) {
            0 -> {
                11
            }
            else -> {
                value - 1
            }
        }
    }
}