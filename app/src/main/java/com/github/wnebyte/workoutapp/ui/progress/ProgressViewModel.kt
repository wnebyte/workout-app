package com.github.wnebyte.workoutapp.ui.progress

import java.util.*
import android.util.Log
import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.database.Repository
import com.github.wnebyte.workoutapp.util.Extensions.Companion.format
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toFirstOfLastMonth
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toLastOfLastMonth
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toLastOfNextMonth
import com.github.wnebyte.workoutapp.util.Extensions.Companion.month
import com.github.wnebyte.workoutapp.model.ProgressItem
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises

private const val TAG = "ProgressViewModel"

private const val DATE_KEY = "Date"

class ProgressViewModel(private val state: SavedStateHandle) : ViewModel() {

    private val repository = Repository.get()

    val dateLiveData: MutableLiveData<Date> = state.get<Long>(DATE_KEY).let {
        return@let when (it) {
            null -> {
                MutableLiveData(Date())
            }
            else -> {
                MutableLiveData(Date(it))
            }
        }
    }

    val progressItemListLiveData: LiveData<List<ProgressItem>> = (
            Transformations.switchMap(dateLiveData) { to ->
                val from = to.toFirstOfLastMonth()
                Log.i(TAG, "from: ${from.format()}, to: ${to.format()}")
                repository.getWorkoutsWithExercisesCompletedBetween(
                    from, to
                ).switchMap { workouts ->
                        MutableLiveData(transform(workouts, to.month()))
                    }
            })

    fun decrementMonth() {
        val value = dateLiveData.value
        value?.let {
            setDate(it.toLastOfLastMonth())
        }
    }

    fun incrementMonth() {
        val value = dateLiveData.value
        value?.let {
            setDate(it.toLastOfNextMonth())
        }
    }

    fun getDate(): Date {
        return dateLiveData.value!!
    }

    private fun setDate(date: Date) {
        state[DATE_KEY] = date.time
        dateLiveData.value = date
    }

    private fun transform(
        workouts: List<WorkoutWithExercises>,
        refMonth: Int,
    ): List<ProgressItem> {
        val list = mutableListOf<ProgressItem>()
        val shards: Map<Int, List<ExerciseTuple>> = shard(workouts)
        val shard = shards[refMonth]

        shard?.let { exercises ->
            for (name in getDistinctNames(exercises.map { t -> t.exercise })) {
                // get all the exercises in the shard with the given name
                val l: List<ExerciseTuple> = exercises
                    .filter { e -> e.exercise.exercise.name == name }
                // get the dates (x)
                val x: List<Long> = l.map { e -> e.date.time }
                // get their averages (y)
                val y: List<Float> = l.map { e -> e.exercise.sets.map { s -> s.weights }
                    .average().toFloat() }

                val avg0 = y.average().toFloat()
                val avg1 = (shards[decrementMonth(refMonth)]
                    ?.filter { e -> e.exercise.exercise.name == name }
                    ?.map { e -> e.exercise.sets.map { s -> s.weights }.average() }
                    ?.average() ?: 0.0f).toFloat()
                val avg2 = l.map { e -> e.exercise.sets.map { s -> s.reps }.average() }
                    .average().toFloat()

                list.add(
                    ProgressItem(
                        name = name,
                        x = x,
                        y = y,
                        avgWeights = avg0,
                        avgReps = avg2,
                        unit = "kg",
                        change = if (avg0 == 0.0f || avg1 == 0.0f) {
                            0.0f
                        } else {
                            val f: Float = (avg0 / avg1)
                            val r: Float = if (f > 1.0f) {
                                f - 1.0f
                            } else {
                                -1 * (1.0f - f)
                            }
                            r
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
    private fun shardByMonth(workouts: List<WorkoutWithExercises>)
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

    private fun shard(workouts: List<WorkoutWithExercises>)
    : Map<Int, List<ExerciseTuple>> {
        val map: MutableMap<Int, MutableList<ExerciseTuple>> = mutableMapOf()

        for (w in workouts) {
            val date = w.workout.date
            date?.let {
                val month = date.month()
                if (!map.containsKey(month)) {
                    val l = mutableListOf<ExerciseTuple>()
                    for (e in w.exercises) {
                        l.add(ExerciseTuple(date, e))
                    }
                    map[month] = l
                } else {
                    for (e in w.exercises) {
                        map[month]!!.add(ExerciseTuple(date, e))
                    }
                }
            }
        }
        return map
    }

    private fun getDistinctNames(exercises: List<ExerciseWithSets>?): Set<String> {
        val set = mutableSetOf<String>()
        exercises?.let {
            val l: List<String> = it.map { e -> e.exercise.name }.sorted()
            set.addAll(l)
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

    private data class ExerciseTuple(
        val date: Date,
        val exercise: ExerciseWithSets
    )
}