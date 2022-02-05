package com.github.wnebyte.workoutapp.ui.progress

import java.util.*
import android.util.Log
import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.database.Repository
import com.github.wnebyte.workoutapp.model.*
import com.github.wnebyte.workoutapp.util.TemporalRange
import com.github.wnebyte.workoutapp.util.Extensions.Companion.format

private const val TAG = "ProgressViewModel"

private const val TEMPORAL_RANGE_KEY = "Range"

class ProgressViewModel(private val state: SavedStateHandle) : ViewModel() {

    private val repository = Repository.get()

    val rangeLiveData: MutableLiveData<TemporalRange> = state.getLiveData(TEMPORAL_RANGE_KEY)

    val progressItemListLiveData: LiveData<List<ProgressItem>> = (
            Transformations.switchMap(rangeLiveData) { range ->
                repository.getWorkoutsWithExercisesCompletedBetween(
                    range.lower, range.upper
                ).switchMap { workouts ->
                    MutableLiveData(transf(workouts, range))
                }
            })

    init {
        if (!state.contains(TEMPORAL_RANGE_KEY)) {
            setTemporalRange(
                TemporalRange.newInstance(TemporalRange.MONTH))
        }
    }

    fun decrementRange() {
        val value = rangeLiveData.value
        value?.let {
            setTemporalRange(it.adjustDown())
        }
    }

    fun incrementRange() {
        val value = rangeLiveData.value
        value?.let {
            setTemporalRange(it.adjustUp())
        }
    }

    fun getTemporalRange(): TemporalRange {
        return rangeLiveData.value!!
    }

    fun setTemporalRange(range: TemporalRange) {
        state[TEMPORAL_RANGE_KEY] = range
        rangeLiveData.value = range
        Log.i(TAG, "lower: ${range.lower.format("yyyy/MM/dd")} " +
                "upper: ${range.upper.format("yyyy/MM/dd")}")
    }

    private fun transf(
        workouts: List<WorkoutWithExercises>,
        range: TemporalRange
    ): List<ProgressItem> {
        val list = mutableListOf<ProgressItem>()
        val shards: Map<Boolean, List<ExerciseTuple>> = shard(workouts, range)
        val shard = shards[true]

        shard?.let {
            for (name in getDistinctNames(it.map { t -> t.exercise })) {
                // get all the exercises in the shard with the given name
                val l: List<ExerciseTuple> = it
                    .filter { e -> e.exercise.exercise.name == name }
                // get the ids
                val id: List<UUID> = l.map { e -> e.exercise.exercise.id }
                // get the dates (x)
                val x: List<Long> = l.map { e -> e.date.time }
                // get their averages (y)
                val y: List<Float> = l.map { e -> e.exercise.sets.map { s -> s.weights }
                    .average().toFloat() }
                // wrap values
                val data: List<DataPoint> = List(id.size) { i ->
                    DataPoint(id[i], x[i], y[i])
                }

                // get avg weights for this month
                val avg0 = y.average().toFloat()
                // get avg weights for previous month
                val avg1 = (shards[false]
                    ?.filter { e -> e.exercise.exercise.name == name }
                    ?.map { e -> e.exercise.sets.map { s -> s.weights }.average() }
                    ?.average() ?: 0.0f).toFloat()
                // get avg reps for this month
                val avg2 = l.map { e -> e.exercise.sets.map { s -> s.reps }.average() }
                    .average().toFloat()

                list.add(
                    ProgressItem(
                        name = name,
                        data = data,
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

    private fun shard(workouts: List<WorkoutWithExercises>, ref: TemporalRange)
    : Map<Boolean, List<ExerciseTuple>> {
        val map: MutableMap<Boolean, MutableList<ExerciseTuple>> = mutableMapOf()
        for (w in workouts) {
            val date = w.workout.date
            date?.let {
                val bool = ref.after(date)
                if (!map.containsKey(bool)) {
                    val l = mutableListOf<ExerciseTuple>()
                    for (e in w.exercises) {
                        l.add(ExerciseTuple(date, e))
                    }
                    map[bool] = l
                } else {
                    for (e in w.exercises) {
                        map[bool]!!.add(ExerciseTuple(date, e))
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

    private data class ExerciseTuple(
        val date: Date,
        val exercise: ExerciseWithSets
    )
}