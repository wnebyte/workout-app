package com.github.wnebyte.workoutapp.ui.workoutcreate

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.databinding.ExerciseBinding
import com.github.wnebyte.workoutapp.databinding.FragmentWorkoutCreateBinding
import com.github.wnebyte.workoutapp.databinding.SetItemBinding
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.Reminder
import com.github.wnebyte.workoutapp.model.Set
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import com.github.wnebyte.workoutapp.ui.AdapterUtil
import com.github.wnebyte.workoutapp.util.DateUtil
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

    private val reminderItems: List<Reminder> = Reminder.CONSTANTS.map { Reminder(it) }

    private val binding get() =_binding!!

    private var _binding: FragmentWorkoutCreateBinding? = null

    private var callbacks: Callbacks? = null

    private var saveWorkout = true

    private lateinit var reminderAdapter: ArrayAdapter<Reminder>

    private lateinit var workout: WorkoutWithExercises

    private var displayDatePicker: View.OnClickListener? = View.OnClickListener {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val picker = DatePickerDialog(requireContext(), { view, y, m, d ->
            "$y/${normalize(m + 1)}/${normalize(d)}".also {
                vm.date = it
            }
            displayTimePicker?.onClick(view)
        }, year, month, day)
        picker.show()
    }

    private var displayTimePicker: View.OnClickListener? = View.OnClickListener {
        val calender = Calendar.getInstance()
        val hour = calender.get(Calendar.HOUR_OF_DAY)
        val minutes = calender.get(Calendar.MINUTE)
        val picker = TimePickerDialog(context, { _, hourOfDay, minute ->
            vm.date?.let {
                vm.date += " ${normalize(hourOfDay)}:${normalize(minute)}"
                binding.date.setText(vm.date, TextView.BufferType.EDITABLE)
            }
        }, hour, minutes, true)
        picker.show()
    }

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
        workout = WorkoutWithExercises.newInstance()
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
        _binding = FragmentWorkoutCreateBinding
            .inflate(inflater, container, false)
        binding.recyclerView.adapter = adapter
        reminderAdapter = ArrayAdapter(requireContext(), R.layout.reminder_list_item, reminderItems)
        binding.reminder.setAdapter(reminderAdapter)
        binding.reminder.onItemClickListener = AdapterView.OnItemClickListener {
                parent, _, position, _ ->
            val item = parent.getItemAtPosition(position) as Reminder
            workout.workout.reminder = item.value
        }
        binding.buttonBar.save.setOnClickListener {
            callbacks?.onFinished()
        }
        binding.buttonBar.cancel.setOnClickListener {
            saveWorkout = false
            callbacks?.onFinished()
        }
        binding.date.setOnClickListener(displayDatePicker)
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
        binding.name.doOnTextChanged { text, _, _, count ->
            if ((text != null) && (0 < count)) {
                workout.workout.name = text.toString()
            }
        }
        binding.date.doOnTextChanged { text, _, _, count ->
            if ((text != null) && (0 < count)) {
                workout.workout.date = DateUtil.fromString(text.toString())
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (saveWorkout) {
            vm.saveWorkout(workout)
            vm.saveDate()
        } else {
            vm.deleteWorkout(workout)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
        displayDatePicker = null
        displayTimePicker = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI(workout: WorkoutWithExercises) {
        binding.name.setText(workout.workout.name, TextView.BufferType.EDITABLE)
        workout.workout.date?.let {
            val date = DateUtil.fromDate(it)
            binding.date.setText(date, TextView.BufferType.EDITABLE)
        }
        workout.workout.reminder?.let {
            binding.reminder.setText(Reminder(it).text,false)
        }
        adapter.submitList(workout.exercises)
    }

    private inner class ExerciseHolder(private val binding: ExerciseBinding):
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var exercise: ExerciseWithSets
        private val adapter = SetAdapter()

        init {
            binding.content.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.content.recyclerView.adapter = adapter
            binding.actionBar.edit.setOnClickListener {
                //   callbacks?.onEditExercise(exercise.exercise.id)
            }
            binding.actionBar.delete.setOnClickListener {
                vm.deleteExercise(exercise)
            }
        }

        fun bind(exercise: ExerciseWithSets) {
            this.exercise = exercise
            binding.content.title.text = exercise.exercise.name
            adapter.submitList(exercise.sets)
        }
    }

    private inner class ExerciseAdapter : ListAdapter<ExerciseWithSets, ExerciseHolder>
        (AdapterUtil.DIFF_UTIL_EXERCISE_WITH_SETS_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseHolder {
            val view = ExerciseBinding.inflate(layoutInflater, parent, false)
            return ExerciseHolder(view)
        }

        override fun onBindViewHolder(holder: ExerciseHolder, position: Int) {
            val exercise = getItem(position)
            return holder.bind(exercise)
        }
    }

    private inner class SetHolder(private val binding: SetItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var set: Set

        fun bind(set: Set) {
            this.set = set
            "${set.weights} x ${set.reps}".also { binding.textView.text = it }
        }
    }

    private inner class SetAdapter : ListAdapter<Set, SetHolder>
        (AdapterUtil.DIFF_UTIL_SET_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetHolder {
            val view = SetItemBinding.inflate(layoutInflater, parent, false)
            return SetHolder(view)
        }

        override fun onBindViewHolder(holder: SetHolder, position: Int) {
            val set = getItem(position)
            return holder.bind(set)
        }
    }
}