package com.github.wnebyte.workoutapp.ui.workoutlist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.databinding.*
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.Set
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import com.github.wnebyte.workoutapp.util.AdapterUtil
import com.google.android.material.chip.Chip
import java.util.*

private const val TAG = "WorkoutListFragment"

class WorkoutListFragment : Fragment() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_workout_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.reorder -> {
                true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutListBinding
            .inflate(layoutInflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.fab.setOnClickListener {
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

    private inner class WorkoutHolder(private val binding: WorkoutBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnLongClickListener {
        private lateinit var workout: WorkoutWithExercises

        init {
            binding.root.setOnLongClickListener(this)
            binding.delete.setOnClickListener {
                vm.deleteWorkout(workout)
            }
        }

        fun bind(workout: WorkoutWithExercises) {
            this.workout = workout
            binding.name.text = workout.workout.name
            binding.date.text = workout.workout.date?.toString()
        }

        override fun onLongClick(view: View): Boolean {
            callbacks?.onEditWorkout(workout.workout.id)
            return true
        }

    }

    private inner class WorkoutAdapter : ListAdapter<WorkoutWithExercises, WorkoutHolder>
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

}