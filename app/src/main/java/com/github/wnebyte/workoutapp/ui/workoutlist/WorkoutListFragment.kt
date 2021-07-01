package com.github.wnebyte.workoutapp.ui.workoutlist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.wnebyte.workoutapp.databinding.FragmentWorkoutListBinding
import com.github.wnebyte.workoutapp.databinding.WorkoutBinding
import com.github.wnebyte.workoutapp.databinding.WorkoutExerciseItemBinding
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import com.github.wnebyte.workoutapp.util.AdapterUtil
import com.google.android.material.chip.Chip
import java.util.*

private const val TAG = "WorkoutListFragment"

class WorkoutListFragment: Fragment() {

    interface Callbacks {
        fun onEditWorkout(workoutId: UUID)
        fun onCreateWorkout()
    }

    private val vm: WorkoutListViewModel by viewModels()

    private val adapter = WorkoutAdapter()

    private val binding get() = _binding!!

    private var callbacks: Callbacks? = null

    private var _binding: FragmentWorkoutListBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callbacks = context as Callbacks
        } catch (ex: Exception) {
            throw IllegalStateException(
                "Hosting activity needs to implement callbacks interface"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutListBinding.inflate(layoutInflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.fab.setOnClickListener{
            callbacks?.onCreateWorkout()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.workoutListLiveData.observe(
            viewLifecycleOwner,
            { workouts ->
                workouts?.let { it ->
                    Log.i(TAG, "Got workouts: ${it.size}")
                    adapter.submitList(it)
                }
            }
        )
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class WorkoutHolder(private val binding: WorkoutBinding):
        RecyclerView.ViewHolder(binding.root) {
            private lateinit var workout: WorkoutWithExercises
            private val adapter = ExerciseAdapter()

            init {
                binding.recyclerView.layoutManager = LinearLayoutManager(context)
                binding.recyclerView.adapter = adapter
                binding.delete.setOnClickListener {
                    vm.deleteWorkout(workout)
                }
                binding.edit.setOnClickListener {
                    callbacks?.onEditWorkout(workout.workout.id)
                }
            }

            fun bind(workout: WorkoutWithExercises) {
                this.workout = workout
                binding.title.text = workout.workout.name
                adapter.submitList(workout.exercises)
            }
        }

    private inner class WorkoutAdapter: ListAdapter<WorkoutWithExercises, WorkoutHolder>
        (AdapterUtil.DIFF_UTIL_WORKOUT_WITH_EXERCISES_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutHolder {
            val view = WorkoutBinding.inflate(layoutInflater, parent, false)
            return WorkoutHolder(view)
        }

        override fun onBindViewHolder(holder: WorkoutHolder, position: Int) {
            val workout = getItem(position)
            return holder.bind(workout)
        }
    }

    private inner class ExerciseHolder(val binding: WorkoutExerciseItemBinding):
        RecyclerView.ViewHolder(binding.root) {
            private lateinit var exercise: ExerciseWithSets

            init {
                binding.root.removeView(binding.deleteButton)
            }

            fun bind(exercise: ExerciseWithSets) {
                this.exercise = exercise
                binding.title.text = exercise.exercise.name
                binding.timer.text = exercise.exercise.timer.toString()
                exercise.sets.forEach { set ->
                    val chip = Chip(context)
                    "${set.weights} x ${set.reps}".also { chip.text = it }
                    binding.chipGroup.addView(chip)
                }
            }
        }

    private inner class ExerciseAdapter: ListAdapter<ExerciseWithSets, ExerciseHolder>
        (AdapterUtil.DIFF_UTIL_EXERCISE_WITH_SETS_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseHolder {
            val view = WorkoutExerciseItemBinding.inflate(layoutInflater, parent, false)
            return ExerciseHolder(view)
        }

        override fun onBindViewHolder(holder: ExerciseHolder, position: Int) {
            val exercise = getItem(position)
            return holder.bind(exercise)
        }
    }
}