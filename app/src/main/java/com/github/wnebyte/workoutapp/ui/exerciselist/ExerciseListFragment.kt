package com.github.wnebyte.workoutapp.ui.exerciselist

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
import com.github.wnebyte.workoutapp.databinding.ExerciseBinding
import com.github.wnebyte.workoutapp.databinding.FragmentExerciseListBinding
import com.github.wnebyte.workoutapp.databinding.SetItemBinding
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.Set
import com.github.wnebyte.workoutapp.util.AdapterUtil
import java.lang.Exception
import java.util.*

private const val TAG = "ExerciseListFragment"

class ExerciseListFragment : Fragment() {

    interface Callbacks {
        fun onEditExercise(exerciseId: UUID)
        fun onCreateExercise()
    }

    private val vm: ExerciseListViewModel by viewModels()

    private val binding get() = _binding!!

    private val adapter = ExerciseAdapter()

    private var callbacks: Callbacks? = null

    private var _binding: FragmentExerciseListBinding? = null

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
        _binding = FragmentExerciseListBinding
            .inflate(layoutInflater, container, false)
        binding.recyclerView.adapter = adapter
        binding.fab.setOnClickListener {
            callbacks?.onCreateExercise()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.exerciseListLiveData.observe(
            viewLifecycleOwner,
            { exercises ->
                exercises?.let {
                    Log.i(TAG, "Got exercises: ${exercises.size}")
                    adapter.submitList(exercises)
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

    private inner class ExerciseHolder(private val binding: ExerciseBinding):
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var exercise: ExerciseWithSets
        private val adapter = SetAdapter()

        init {
            binding.content.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.content.recyclerView.adapter = adapter
            binding.actionBar.edit.setOnClickListener {
                callbacks?.onEditExercise(exercise.exercise.id)
            }
            binding.actionBar.delete.setOnClickListener {
                vm.deleteExercise(exercise)
            }
        }

        fun bind(exercise: ExerciseWithSets) {
            this.exercise = exercise
            binding.content.title.text = exercise.exercise.name
            binding.content.secondaryTitle.text = exercise.exercise.timer.toString()
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