package com.github.wnebyte.workoutapp.ui.workoutdetails

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.databinding.ExerciseCardActionableBinding
import com.github.wnebyte.workoutapp.databinding.FragmentWorkoutDetailsBinding
import com.github.wnebyte.workoutapp.databinding.SetItemBinding
import com.github.wnebyte.workoutapp.util.Extensions.Companion.empty
import com.github.wnebyte.workoutapp.util.Extensions.Companion.format
import com.github.wnebyte.workoutapp.util.Extensions.Companion.showDropdown
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toPaddedString
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.Reminder
import com.github.wnebyte.workoutapp.model.Set
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import com.github.wnebyte.workoutapp.ui.AdapterUtil
import com.github.wnebyte.workoutapp.util.DateUtil
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception
import java.lang.IllegalStateException
import java.util.*

private const val TAG = "WorkoutDetailsFragment"

class WorkoutDetailsFragment: Fragment() {

    interface Callbacks {
        fun onFinished()
        fun onImportExercise(workoutId: UUID, currentFragment: Class<out Fragment>)
        fun onCreateExercise(workoutId: UUID, currentFragment: Class<out Fragment>)
        fun onEditExercise(exerciseId: UUID, currentFragment: Class<out Fragment>)
    }

    private val vm: WorkoutDetailsViewModel by viewModels()

    private val args: WorkoutDetailsFragmentArgs by navArgs()

    private val adapter = ExerciseAdapter()

    private val dropdownItems: List<Reminder> = Reminder.CONSTANTS.map { Reminder(it) }

    private val binding get() = _binding!!

    private var _binding: FragmentWorkoutDetailsBinding? = null

    private var callbacks: Callbacks? = null

    private val removedItems: MutableList<ExerciseWithSets> = mutableListOf()

    private lateinit var workout: WorkoutWithExercises

    private lateinit var dropdownAdapter: ArrayAdapter<Reminder>

    private var datePicker: View.OnClickListener? = View.OnClickListener {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val picker = DatePickerDialog(requireContext(), { view, y, m, d ->
            "$y/${(m + 1).toPaddedString()}/${d.toPaddedString()}".also {
                vm.date = it
            }
            timePicker?.onClick(view)
        }, year, month, day)
        picker.show()
    }

    private var timePicker: View.OnClickListener? = View.OnClickListener {
        val calender = Calendar.getInstance()
        val hour = calender.get(Calendar.HOUR_OF_DAY)
        val minutes = calender.get(Calendar.MINUTE)
        val picker = TimePickerDialog(context, { _, hourOfDay, minute ->
            vm.date?.let {
                vm.date += " ${hourOfDay.toPaddedString()}:${minute.toPaddedString()}"
                binding.date.setText(vm.date, TextView.BufferType.NORMAL)
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
        workout = WorkoutWithExercises.newInstance() // will be re-initialized
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_workout_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_exercise -> {
                callbacks?.onCreateExercise(args.workoutId, this::class.java)
                true
            }
            R.id.import_exercise -> {
                callbacks?.onImportExercise(args.workoutId, this::class.java)
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
        _binding = FragmentWorkoutDetailsBinding
            .inflate(layoutInflater, container, false)
        binding.recyclerView.adapter = adapter
        dropdownAdapter =
            ArrayAdapter(requireContext(), R.layout.reminder_list_item, dropdownItems)
        binding.dropdown.setAdapter(dropdownAdapter)
        binding.dropdown.setOnClickListener {
            (it as AutoCompleteTextView)
                .showDropdown(dropdownAdapter)
        }
        binding.date.setOnClickListener(datePicker)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.workoutLiveData.observe(
            viewLifecycleOwner,
            { workout ->
                workout?.let {
                    Log.i(TAG, "Got workout: ${it.workout.id}")
                    this.workout = it
                    updateUI()
                }
            }
        )
        // update local workout name whenever ui changes
        binding.name.doOnTextChanged { text, _, _, _ ->
            if (!TextUtils.isEmpty(text)) {
                workout.workout.name = text.toString()
            }
        }
        // update local workout date whenever ui changes
        binding.date.doOnTextChanged { text, _, _, _ ->
            if (!TextUtils.isEmpty(text)) {
                workout.workout.date = DateUtil.fromString(text.toString())
            }
        }
        // update local workout reminder whenever ui changes
        binding.dropdown.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
            val item = parent.getItemAtPosition(position) as Reminder
            workout.workout.reminder = item.value
        }
        vm.loadWorkout(args.workoutId)
    }

    override fun onStop() {
        super.onStop()
        vm.saveWorkout(workout)
        vm.deleteExercises(removedItems)
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        datePicker = null
        timePicker = null
    }

    /**
     * Binds an instance of [WorkoutWithExercises] to the UI.
     */
    private fun updateUI() {
        // bind workout name to ui
        binding.name
            .setText(workout.workout.name, TextView.BufferType.EDITABLE)
        // bind workout date to ui
        workout.workout.date?.let {
            val date = it.format()
            binding.date
                .setText(date, TextView.BufferType.NORMAL)
        }
        // bind workout reminder to ui
        workout.workout.reminder?.let {
            binding.dropdown
                .setText(Reminder(it).text,false)
        }
        // bind exercises to ui
        adapter.submitList(workout.exercises)
    }

    /**
     * Removes the [ExerciseWithSets] positioned at [index] from the underlying
     * data-set and adds it to [removedItems].
     * The adapter will thereafter be notified of a removed item at position index.
     * @param index the index of the to-be removed item.
     */
    private fun dataSetRemove(index: Int) {
        val exercise = workout.exercises.removeAt(index)
        removedItems.add(exercise)
        adapter.notifyItemRemoved(index)
    }

    /**
     * Inserts the specified [ExerciseWithSets] at position [index] in the underlying data-set, and
     * removes it from [removedItems].
     * The adapter will thereafter be notified of an inserted item at position index.
     * @param index the index of the to-be inserted item.
     * @param exercise the item to be inserted.
     */
    private fun dataSetInsert(index: Int, exercise: ExerciseWithSets) {
        removedItems.remove(exercise)
        workout.exercises.add(index, exercise)
        adapter.notifyItemInserted(index)
    }

    private inner class ExerciseHolder(private val binding: ExerciseCardActionableBinding)
        : RecyclerView.ViewHolder(binding.root) {
        private lateinit var exercise: ExerciseWithSets
        private val adapter = SetAdapter()

        init {
            binding.actionBar.delete.setOnClickListener {
                removeExercise()
            }
            binding.actionBar.edit.setOnClickListener {
                callbacks?.onEditExercise(
                    exercise.exercise.id, this@WorkoutDetailsFragment::class.java)
            }
            binding.body.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.body.recyclerView.adapter = adapter
        }

        /**
         * Bind the specified [exercise] to the ViewHolder.
         * @param exercise the be bound.
         */
        fun bind(exercise: ExerciseWithSets) {
            this.exercise = exercise
            binding.body.title.text = exercise.exercise.name
            adapter.submitList(exercise.sets)
        }

        /**
         * Removes the [ExerciseWithSets] instance associated with [getAdapterPosition] from the UI,
         * and prompts the display of a Snackbar where the user can undo said removal.
         */
        private fun removeExercise() {
            val index = adapterPosition
            dataSetRemove(index)
            val snackbar = Snackbar.make(binding.root, String.empty(), Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) {
                    dataSetInsert(index, exercise)
                }
            snackbar.show()
        }
    }

    private inner class ExerciseAdapter : ListAdapter<ExerciseWithSets, ExerciseHolder>
        (AdapterUtil.DIFF_UTIL_EXERCISE_WITH_SETS_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseHolder {
            val view = ExerciseCardActionableBinding
                .inflate(layoutInflater, parent, false)
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

        /**
         * Binds the specified [set] to the ViewHolder.
         * @param set to be bound.
         */
        fun bind(set: Set) {
            this.set = set
            "${set.weights} x ${set.reps}".also { binding.tv.text = it }
            if (set.completed) {
                binding.tv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
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