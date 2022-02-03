package com.github.wnebyte.workoutapp.ui.workoutlist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.MenuCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.databinding.FragmentWorkoutListBinding
import com.github.wnebyte.workoutapp.databinding.WorkoutCardBinding
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import com.github.wnebyte.workoutapp.ui.AdapterUtil
import com.github.wnebyte.workoutapp.util.Extensions.Companion.empty
import com.github.wnebyte.workoutapp.util.Extensions.Companion.format
import com.google.android.material.snackbar.Snackbar
import java.util.*

private const val TAG = "WorkoutListFragment"

class WorkoutListFragment : Fragment() {

    interface Callbacks {
        fun onEditWorkout(workoutId: UUID, currentFragment: Class<out Fragment>)
        fun onEditCompletedWorkout(workoutId: UUID, currentFragment: Class<out Fragment>)
        fun onCreateWorkout()
        fun onWorkout(workoutId: UUID)
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
        MenuCompat.setGroupDividerEnabled(menu, true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_all -> {
                vm.setFilter(Filter.ALL)
                true
            }
            R.id.show_completed -> {
                vm.setFilter(Filter.COMPLETED)
                true
            }
            R.id.show_uncompleted -> {
                vm.setFilter(Filter.UNCOMPLETED)
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
        val filter = FilterPreferences.getStoredFilter(requireContext())
        vm.setFilter(filter)
    }

    override fun onStop() {
        super.onStop()
        val filter = vm.getFilter()
        FilterPreferences.setStoredFilter(requireContext(), filter)
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private inner class WorkoutHolder(private val binding: WorkoutCardBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener,
        View.OnLongClickListener {
        private lateinit var workout: WorkoutWithExercises

        init {
            binding.body.root.setOnClickListener(this)
            binding.body.root.setOnLongClickListener(this)
            binding.body.editButton.setOnClickListener { navEdit() }
            binding.body.workoutButton.setOnClickListener { navWorkout() }
            binding.body.deleteButton.setOnClickListener { deleteWorkout() }
        }

        fun bind(workout: WorkoutWithExercises) {
            this.workout = workout
            val iv: AppCompatImageView = binding.body.iv
            binding.body.nameTv.text = workout.workout.name
            binding.body.dateTv.text = workout.workout.date?.format("EEE, d MMM yyyy HH:mm")
            when (workout.workout.completed) {
                true -> {
                    binding.body.workoutButton.visibility = View.GONE
                    iv.setImageResource(R.drawable.ic_baseline_check_24)
                }
                false -> {
                    binding.body.workoutButton.visibility = View.VISIBLE
                    iv.setImageResource(R.drawable.ic_baseline_incomplete_circle_24)
                }
            }
        }

        override fun onClick(view: View) {
            navEdit()
        }

        override fun onLongClick(view: View): Boolean {
            navWorkout()
            return true
        }

        private fun deleteWorkout() {
            vm.deleteWorkout(workout)
            val snackbar = Snackbar.make(binding.root, String.empty(), Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) {
                    vm.saveWorkout(workout)
                }
            snackbar.show()
        }

        private fun navEdit() {
            Log.i(TAG, "height: ${this.itemView.height}")
            when (workout.workout.completed) {
                true -> {
                    callbacks?.onEditCompletedWorkout(
                        workout.workout.id, this@WorkoutListFragment::class.java)
                }
                false -> {
                    callbacks?.onEditWorkout(
                        workout.workout.id, this@WorkoutListFragment::class.java)
                }
            }
        }

        private fun navWorkout() {
            if (!workout.workout.completed) {
                callbacks?.onWorkout(workout.workout.id)
            }
        }
    }

    private inner class WorkoutAdapter : ListAdapter<WorkoutWithExercises, WorkoutHolder>
        (AdapterUtil.DIFF_UTIL_WORKOUT_WITH_EXERCISES_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutHolder {
            val view = WorkoutCardBinding
                .inflate(layoutInflater, parent, false)
            return WorkoutHolder(view)
        }

        override fun onBindViewHolder(holder: WorkoutHolder, position: Int) {
            val workout = getItem(position)
            return holder.bind(workout)
        }
    }
}