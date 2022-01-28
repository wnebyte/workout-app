package com.github.wnebyte.workoutapp.ui.home

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.wnebyte.workoutapp.databinding.ExerciseCardBinding
import com.github.wnebyte.workoutapp.databinding.FragmentWorkoutOverviewBinding
import com.github.wnebyte.workoutapp.databinding.SetItemBinding
import com.github.wnebyte.workoutapp.util.Extensions.Companion.format
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.Set
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import com.github.wnebyte.workoutapp.ui.AdapterUtil

private const val TAG = "AbstractWorkoutOverviewFragment"

abstract class AbstractWorkoutOverviewFragment : Fragment() {

    protected abstract val vm: AbstractWorkoutOverviewViewModel

    protected val binding get() = _binding!!

    protected val adapter = ExerciseAdapter()

    protected var _binding: FragmentWorkoutOverviewBinding? = null

    protected var workout: WorkoutWithExercises? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutOverviewBinding
            .inflate(layoutInflater, container, false)
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.workoutLiveData.observe(
            viewLifecycleOwner,
            { workout ->
                workout?.let {
                    Log.i(TAG, "got next workout: ${it.workout.id}")
                    this.workout = it
                    updateUI()
                }
            }
        )

    }

    override fun onStop() {
        super.onStop()
        binding.chronometer.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected open fun updateUI() {
        workout?.let { it ->
            it.workout.date?.let {
                val offset = (System.currentTimeMillis() - SystemClock.elapsedRealtime())
                binding.chronometer.base = it.time - offset
                binding.chronometer.start()
            }
            adapter.submitList(it.exercises)
        }
    }

    protected inner class ExerciseHolder(private val binding: ExerciseCardBinding)
        : RecyclerView.ViewHolder(binding.root) {
        private lateinit var exercise: ExerciseWithSets
        private val adapter = SetAdapter()

        init {
            binding.body.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.body.recyclerView.adapter = adapter
        }

        fun bind(exercise: ExerciseWithSets) {
            this.exercise = exercise
            binding.body.title.text = exercise.exercise.name
            adapter.submitList(exercise.sets)
        }
    }

    protected inner class ExerciseAdapter: ListAdapter<ExerciseWithSets, ExerciseHolder>
        (AdapterUtil.DIFF_UTIL_EXERCISE_WITH_SETS_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseHolder {
            val view = ExerciseCardBinding
                .inflate(layoutInflater, parent, false)
            return ExerciseHolder(view)
        }

        override fun onBindViewHolder(holder: ExerciseHolder, position: Int) {
            val exercise = getItem(position)
            return holder.bind(exercise)
        }
    }

    protected inner class SetHolder(private val binding: SetItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var set: Set

        fun bind(set: Set) {
            this.set = set
            "${set.weights} x ${set.reps}".also { binding.tv.text = it }
        }
    }

    protected inner class SetAdapter : ListAdapter<Set, SetHolder>
        (AdapterUtil.DIFF_UTIL_SET_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetHolder {
            val view = SetItemBinding
                .inflate(layoutInflater, parent, false)
            return SetHolder(view)
        }

        override fun onBindViewHolder(holder: SetHolder, position: Int) {
            val set = getItem(position)
            return holder.bind(set)
        }
    }
}