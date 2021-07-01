package com.github.wnebyte.workoutapp.ui.workoutdetails

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.wnebyte.workoutapp.databinding.FragmentWorkoutDetailsBinding
import com.github.wnebyte.workoutapp.databinding.WorkoutExerciseItemBinding
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import com.github.wnebyte.workoutapp.util.AdapterUtil
import com.google.android.material.chip.Chip
import java.lang.Exception
import java.lang.IllegalStateException
import java.util.*

private const val TAG = "WorkoutDetailsFragment"

class WorkoutDetailsFragment: Fragment() {

    interface Callbacks {
        fun onFinished()
        fun onImportExercise(workoutId: UUID, currentFragment: Class<out Fragment>)
        fun onCreateExercise(workoutId: UUID, currentFragment: Class<out Fragment>)
    }

    private val vm: WorkoutDetailsViewModel by viewModels()

    private val args: WorkoutDetailsFragmentArgs by navArgs()

    private val adapter = ExerciseAdapter()

    private val binding get() = _binding!!

    private var _binding: FragmentWorkoutDetailsBinding? = null

    private var callbacks: Callbacks? = null

    private lateinit var workout: WorkoutWithExercises

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
        _binding = FragmentWorkoutDetailsBinding
            .inflate(layoutInflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.addButton.setOnClickListener {
            callbacks?.onCreateExercise(workout.workout.id, this::class.java)
        }
        binding.importButton.setOnClickListener {
            callbacks?.onImportExercise(workout.workout.id, this::class.java)
        }
        binding.cancelButton.setOnClickListener {
            callbacks?.onFinished()
        }
        binding.saveButton.setOnClickListener {
            callbacks?.onFinished()
        }
        vm.loadWorkout(args.workoutId)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.workoutLiveData.observe(
            viewLifecycleOwner,
            { workout ->
                workout?.let {
                    Log.i(TAG, "Got workout: ${workout.workout.id}")
                    this.workout = workout
                    updateUI(workout)
                }
            }
        )
    }

    override fun onStop() {
        super.onStop()
        vm.saveWorkout(workout.apply {
            binding.name.editableText?.let {
                this.workout.name = it.toString()
            }
        })
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI(workout: WorkoutWithExercises) {
        binding.name.setText(workout.workout.name, TextView.BufferType.EDITABLE)
        adapter.submitList(workout.exercises)
    }

    private inner class ExerciseHolder(private val binding: WorkoutExerciseItemBinding):
        RecyclerView.ViewHolder(binding.root) {
            private lateinit var exercise: ExerciseWithSets

            init {
                binding.deleteButton.setOnClickListener {
                    vm.deleteExercise(exercise)
                }
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