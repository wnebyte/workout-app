package com.github.wnebyte.workoutapp.ui.progress

import java.util.*
import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.database.Repository
import com.github.wnebyte.workoutapp.model.*
import com.github.wnebyte.workoutapp.util.TemporalRange
import com.github.wnebyte.workoutapp.util.Extensions.Companion.avg

private const val TAG = "ProgressViewModel"

private const val TEMPORAL_RANGE_KEY = "Range"

class ProgressViewModel(private val state: SavedStateHandle) : ViewModel() {

    private val repository = Repository.get()

    private val cache: MutableMap<TemporalRange, List<ProgressItem>> = mutableMapOf()

    val rangeLiveData: MutableLiveData<TemporalRange> = state.getLiveData(TEMPORAL_RANGE_KEY)

    val progressItemListLiveData: LiveData<List<ProgressItem>> = (
            Transformations.switchMap(rangeLiveData) { range ->
                when (cache.containsKey(range)) {
                    true -> {
                        MutableLiveData(cache[range])
                    }
                    false -> {
                        repository.getWorkoutsWithExercisesCompletedBetween(
                            range.lower, range.upper
                        ).switchMap { workouts ->
                            val data = transf(workouts, range)
                            cache[range] = data
                            MutableLiveData(data)
                        }
                    }
                }
            })

    init {
        if (!state.contains(TEMPORAL_RANGE_KEY)) {
            setTemporalRange(
                TemporalRange.newInstance(TemporalRange.MONTH))
        }
    }

    /**
     * Adjusts down the temporal range.
     */
    fun decrementRange() {
        val value = rangeLiveData.value
        value?.let {
            setTemporalRange(it.adjustDown())
        }
    }

    /**
     * Adjusts up the temporal range.
     */
    fun incrementRange() {
        val value = rangeLiveData.value
        value?.let {
            setTemporalRange(it.adjustUp())
        }
    }

    /**
     * Returns the temporal range.
     * @return the temporal range.
     */
    fun getTemporalRange(): TemporalRange {
        return rangeLiveData.value!!
    }

    /**
     * Sets the temporal range.
     * @param range the temporal range.
     */
    fun setTemporalRange(range: TemporalRange) {
        state[TEMPORAL_RANGE_KEY] = range
        rangeLiveData.value = range
    }

    private fun transf(
        workouts: List<WorkoutWithExercises>,
        range: TemporalRange
    ): List<ProgressItem> {
        val list = mutableListOf<ProgressItem>()
        val shards: Map<Boolean, List<ExerciseTuple>> = shard(workouts, range)
        val shard: List<ExerciseTuple>? = shards[true]

        // all tuples whose date lies after the "midway" point of the temporal range
        shard?.let {
            // iterate over each distinct exercise name
            for (name in getDistinctNames(it)) {
                // get all the tuples in the shard with the given exercise name
                val l: List<ExerciseTuple> = it
                    .filter { e -> e.exercise.exercise.name == name }
                // get their ids
                val id: List<UUID> = l.map { e -> e.exercise.exercise.id }
                // get their dates (x)
                val x: List<Long> = l.map { e -> e.date.time }
                // get their weight averages (y)
                val y: List<Float> = l.map { e -> e.exercise.sets.map { s -> s.weights }
                    .avg().toFloat() }
                // wrap their aforementioned values in a DataPoint
                val data: List<DataPoint> = List(id.size) { i ->
                    DataPoint(id[i], x[i], y[i])
                }

                // get avg weights for this "range period"
                val avgThis: Float = y.avg()
                // get avg weights for the previous "range period"
                val yLast = shards[false]
                    ?.filter { e -> e.exercise.exercise.name == name }
                    ?.map { e -> e.exercise.sets.map { s -> s.weights }.avg() } ?: listOf()
                val avgLast: Float = yLast.avg().toFloat()
                // get avg reps for this "range period"
                val avgReps = l.map { e -> e.exercise.sets.map { s -> s.reps }.average() }
                    .average().toFloat()

                val progressItem = ProgressItem(
                    name = name,
                    data = data,
                    avgWeights = avgThis,
                    avgReps = avgReps,
                    unit = "kg",
                    change = if (avgThis == 0.0f || avgLast == 0.0f) {
                        0.0f
                    } else {
                        val f: Float = (avgThis / avgLast)
                        val r: Float = if (f > 1.0f) {
                            f - 1.0f
                        } else {
                            -1 * (1.0f - f)
                        }
                        r
                    }
                )
                list.add(progressItem)
            }
        }

        return list
    }

    /**
     * Transforms the specified [workouts] and shards the transformation target type on
     * [TemporalRange.after].
     * @param workouts to be transformed.
     * @param range to be used to partition the specified `workouts`.
     * @return the result.
     */
    private fun shard(workouts: List<WorkoutWithExercises>, range: TemporalRange)
    : Map<Boolean, List<ExerciseTuple>> {
        val map: MutableMap<Boolean, MutableList<ExerciseTuple>> = mutableMapOf()
        for (w in workouts) {
            val date = w.workout.date
            date?.let {
                val bool = range.after(it)
                if (!map.containsKey(bool)) {
                    val l = mutableListOf<ExerciseTuple>()
                    for (e in w.exercises) {
                        l.add(ExerciseTuple(it, e))
                    }
                    map[bool] = l
                } else {
                    for (e in w.exercises) {
                        map[bool]!!.add(ExerciseTuple(it, e))
                    }
                }
            }
        }
        return map
    }

    /**
     * Returns the distinct names of the specified [exercises] sorted in ascending order.
     * @param exercises to be used.
     * @return the result.
     */
    private fun getDistinctNames(exercises: List<ExerciseTuple>?): Set<String> {
        val set = mutableSetOf<String>()
        exercises?.let {
            val l: List<String> = it.map { e -> e.exercise.exercise.name }.sorted()
            set.addAll(l)
        }
        return set
    }

    private data class ExerciseTuple(
        val date: Date,
        val exercise: ExerciseWithSets
    )
}