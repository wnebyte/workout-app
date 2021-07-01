package com.github.wnebyte.workoutapp.ui.workoutcreate

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.databinding.FragmentWorkoutCreateBinding
import com.github.wnebyte.workoutapp.databinding.WorkoutExerciseItemBinding
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import com.github.wnebyte.workoutapp.util.AdapterUtil
import com.github.wnebyte.workoutapp.util.DateUtil
import com.google.android.material.chip.Chip
import java.lang.Exception
import java.util.*
import com.github.wnebyte.workoutapp.util.DateUtil.Companion.normalize

private const val TAG = "WorkoutCreateFragment"

class WorkoutCreateFragment: Fragment() {

    interface Callbacks {
        fun onFinished()
        fun onImportExercise(workoutId: UUID, currentFragment: Class<out Fragment>)
        fun onCreateExercise(workoutId: UUID, currentFragment: Class<out Fragment>)
    }

    private val vm: WorkoutCreateViewModel by viewModels()

    private val adapter = ExerciseAdapter()

    private val binding get() =_binding!!

    private var _binding: FragmentWorkoutCreateBinding? = null

    private var callbacks: Callbacks? = null

    private var saveWorkout = true

    private var onDateButtonClick: View.OnClickListener? = View.OnClickListener {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val picker = DatePickerDialog(requireContext(), { view, y, m, d ->
            "$y/${normalize(m + 1)}/${normalize(d)}".also {
                vm.date = it
            }
            onTimeButtonClick?.onClick(view)
        }, year, month, day)
        picker.show()
    }

    private var onTimeButtonClick: View.OnClickListener? = View.OnClickListener {
        val calender = Calendar.getInstance()
        val hour = calender.get(Calendar.HOUR_OF_DAY)
        val minutes = calender.get(Calendar.MINUTE)
        val picker = TimePickerDialog(context, { _, hourOfDay, minute ->
            vm.date?.let {
                vm.date += " ${normalize(hourOfDay)}:${normalize(minute)}"
                binding.dateEndButton.text = vm.date
            }
        }, hour, minutes, true)
        picker.show()
    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_workout_create, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_exercise -> {
                callbacks?.onCreateExercise(workout.workout.id, this::class.java)
                true
            }
            R.id.import_exercise -> {
                callbacks?.onImportExercise(workout.workout.id, this::class.java)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutCreateBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.saveButton.setOnClickListener {
            callbacks?.onFinished()
        }
        binding.cancelButton.setOnClickListener {
            saveWorkout = false
            callbacks?.onFinished()
        }
        binding.dateStartButton.setOnClickListener(onDateButtonClick)
        binding.dateEndButton.setOnClickListener(onDateButtonClick)
        vm.loadWorkout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.workoutLiveData.observe(
            viewLifecycleOwner,
            { workout ->
                workout?.let { it->
                    Log.i(TAG, "Got workout: ${it.workout.id}")
                    this.workout = it
                    updateUI(it)
                }
            }
        )
    }

    override fun onStop() {
        super.onStop()
        if (saveWorkout) {
            vm.saveWorkout(workout.apply {
                binding.name.editableText?.let {
                    this.workout.name = it.toString()
                }
                binding.dateEndButton.text?.toString()?.let {
                    this.workout.date = DateUtil.fromString(it)
                }
            })
        } else {
            vm.deleteWorkout(workout)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
        onDateButtonClick = null
        onTimeButtonClick = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI(workout: WorkoutWithExercises) {
        binding.name.setText(workout.workout.name, TextView.BufferType.EDITABLE)
        workout.workout.date?.let {
            binding.dateEndButton.text = DateUtil.fromDate(it)
        }
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