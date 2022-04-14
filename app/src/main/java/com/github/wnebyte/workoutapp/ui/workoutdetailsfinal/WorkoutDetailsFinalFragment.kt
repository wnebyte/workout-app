package com.github.wnebyte.workoutapp.ui.workoutdetailsfinal

import android.graphics.Paint
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
import com.github.wnebyte.workoutapp.databinding.ExerciseCardBinding
import com.github.wnebyte.workoutapp.databinding.FragmentWorkoutDetailsFinalBinding
import com.github.wnebyte.workoutapp.databinding.SetItemBinding
import com.github.wnebyte.workoutapp.ui.AdapterUtil
import com.github.wnebyte.workoutapp.util.Extensions.Companion.format
import com.github.wnebyte.workoutapp.model.Set
import com.github.wnebyte.workoutapp.model.Reminder
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises

private const val TAG = "WorkoutDetailsFinalFragment"

class WorkoutDetailsFinalFragment : Fragment() {

    private val vm: WorkoutDetailsFinalViewModel by viewModels()

    private val args: WorkoutDetailsFinalFragmentArgs by navArgs()

    private val adapter = ExerciseAdapter()

    private val binding get() = _binding!!

    private var _binding: FragmentWorkoutDetailsFinalBinding? = null

    private lateinit var workout: WorkoutWithExercises

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workout = WorkoutWithExercises.newInstance() // will be re-initialized
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutDetailsFinalBinding.
                inflate(layoutInflater, container, false)
        binding.body.recyclerView.adapter = adapter
        // disable name, date, dropdown
        binding.body.name.isEnabled = false
        binding.body.date.isEnabled = false
        binding.body.dropdown.isEnabled = false
        binding.body.dropdownLayout.isEndIconCheckable = false
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
        vm.loadWorkout(args.workoutId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI() {
        binding.body.name
            .setText(workout.workout.name, TextView.BufferType.NORMAL)
        workout.workout.date?.let { date ->
            binding.body.date
                .setText(date.format(), TextView.BufferType.NORMAL)
        }
        workout.workout.reminder?.let {
            binding.body.dropdown
                .setText(Reminder(it).text,false)
        }
        // bind exercises to ui
        adapter.submitList(workout.exercises)
    }

    private inner class ExerciseHolder(private val binding: ExerciseCardBinding)
        : RecyclerView.ViewHolder(binding.root) {
        private lateinit var exercise: ExerciseWithSets
        private val adapter = SetAdapter()

        init {
            binding.body.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.body.recyclerView.adapter = adapter
            binding.root.isClickable = false
            binding.root.isLongClickable = false
            binding.root.isFocusable = false
        }

        fun bind(exercise: ExerciseWithSets) {
            this.exercise = exercise
            binding.body.title.text = exercise.exercise.name
            adapter.submitList(exercise.sets)
        }
    }

    private inner class ExerciseAdapter : ListAdapter<ExerciseWithSets, ExerciseHolder>
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

    private inner class SetHolder(private val binding: SetItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var set: Set

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