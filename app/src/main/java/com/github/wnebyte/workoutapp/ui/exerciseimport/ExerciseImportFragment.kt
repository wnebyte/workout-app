package com.github.wnebyte.workoutapp.ui.exerciseimport

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Checkable
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.wnebyte.workoutapp.databinding.*
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.Set
import com.github.wnebyte.workoutapp.util.AdapterUtil
import com.google.android.material.card.MaterialCardView
import java.lang.Exception
import java.lang.IllegalStateException

private const val TAG = "ExerciseImportFragment"

class ExerciseImportFragment: Fragment() {

    interface Callbacks {
        fun onFinished()
    }

    private val vm: ExerciseImportViewModel by viewModels()

    private val args: ExerciseImportFragmentArgs by navArgs()

    private val adapter = ExerciseAdapter()

    private val binding get() = _binding!!

    private var _binding: FragmentExerciseImportBinding? = null

    private var callbacks: Callbacks? = null

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
        _binding = FragmentExerciseImportBinding
            .inflate(layoutInflater, container, false)
        binding.recyclerView.adapter = adapter
        binding.fab.setOnClickListener {
            // iterate over the selected positions
            vm.selectedPositions.forEach { i ->
                // create a copy of the selected model item using the specified workoutId
                val exercise = ExerciseWithSets.copyOf(adapter.currentList[i], args.workoutId)
                // save the copy
                vm.saveExercise(exercise)
            }
            callbacks?.onFinished()
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

    override fun onStop() {
        super.onStop()
        vm.saveSelectedPositions()
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class ExerciseHolder(private val binding: ImportableExerciseBinding):
        RecyclerView.ViewHolder(binding.root), View.OnLongClickListener {
        private lateinit var exercise: ExerciseWithSets
        private val adapter = SetAdapter()

        init {
            binding.content.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.content.recyclerView.adapter = adapter
            binding.root.setOnLongClickListener(this)
        }

        fun bind(exercise: ExerciseWithSets) {
            this.exercise = exercise
            binding.content.title.text = exercise.exercise.name
            binding.content.secondaryTitle.text = exercise.exercise.timer.toString()
            binding.root.isChecked = vm.selectedPositions.contains(adapterPosition)
            adapter.submitList(exercise.sets)
        }

        override fun onLongClick(view: View): Boolean {
            if (view is Checkable) {
                val checked: Boolean = view.isChecked
                view.isChecked = !checked

                if (view.isChecked) {
                    Log.i(TAG, "Adding: $adapterPosition")
                    vm.selectedPositions.add(adapterPosition)
                } else {
                    Log.i(TAG, "Removing: $adapterPosition")
                    vm.selectedPositions.remove(adapterPosition)
                }
                return true
            }
            return false
        }
    }

    private inner class ExerciseAdapter : ListAdapter<ExerciseWithSets, ExerciseHolder>
        (AdapterUtil.DIFF_UTIL_EXERCISE_WITH_SETS_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseHolder {
            val view = ImportableExerciseBinding.inflate(layoutInflater, parent, false)
            return ExerciseHolder(view)
        }

        override fun onBindViewHolder(holder: ExerciseHolder, position: Int) {
            val exercise = getItem(position)
            return holder.bind(exercise)
        }
    }

    private inner class SetHolder(private val binding: SetItemBinding):
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