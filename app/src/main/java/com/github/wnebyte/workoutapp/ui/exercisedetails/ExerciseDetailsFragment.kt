package com.github.wnebyte.workoutapp.ui.exercisedetails

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.databinding.FragmentExerciseDetailsBinding
import com.github.wnebyte.workoutapp.databinding.SetBinding
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.Set
import com.github.wnebyte.workoutapp.ui.AdapterUtil
import java.lang.Exception

private const val TAG = "ExerciseDetailsFragment"

class ExerciseDetailsFragment: Fragment() {

    interface Callbacks {
        fun onFinished()
    }

    private val args: ExerciseDetailsFragmentArgs by navArgs()

    private val vm: ExerciseDetailsViewModel by viewModels()

    private val binding get() = _binding!!

    private var _binding: FragmentExerciseDetailsBinding? = null

    private var callbacks: Callbacks? = null

    private val adapter = SetAdapter()

    private val removedItems: MutableList<Set> = mutableListOf()

    private lateinit var exercise: ExerciseWithSets

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callbacks = context as Callbacks
        } catch (ex: Exception) {
            throw IllegalStateException(
                "Hosting activity needs to implement Callbacks interface"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exercise = ExerciseWithSets.newInstance()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_exercise_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_set -> {
                val set: Set = Set.newInstance(exercise.exercise.id)
                exercise.sets.add(set)
                adapter.notifyItemInserted(adapter.itemCount)
                true
            } else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExerciseDetailsBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.exerciseLiveData.observe(
            viewLifecycleOwner,
            { exercise ->
                exercise?.let { it ->
                    Log.i(TAG, "Got exercise: ${it.exercise.id}")
                    this.exercise = it
                    updateUI()
                }
            }
        )
        binding.name.doOnTextChanged { text, _, _, _ ->
            if (!TextUtils.isEmpty(text)) {
                exercise.exercise.name = text.toString()
            }
        }
        vm.loadExercise(args.exerciseId)
    }

    override fun onStop() {
        super.onStop()
        vm.saveExercise(exercise)
        vm.deleteSets(removedItems)
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI() {
        binding.name
            .setText(exercise.exercise.name, TextView.BufferType.EDITABLE)
        adapter.submitList(exercise.sets)
    }

    private inner class SetHolder(private val binding: SetBinding):
        RecyclerView.ViewHolder(binding.root) {
            private lateinit var set: Set

        init {
            binding.repsLayout.setEndIconOnClickListener {
                removedItems.add(set)
                exercise.sets.remove(set)
                this@ExerciseDetailsFragment.adapter.notifyItemRemoved(adapterPosition)
            }
            binding.weights.doOnTextChanged { text, _, _, _ ->
                if (!TextUtils.isEmpty(text)) {
                    set.weights = text.toString().toDouble()
                }
            }
            binding.reps.doOnTextChanged { text, _, _, _ ->
                if (!TextUtils.isEmpty(text)) {
                    set.reps = text.toString().toInt()
                }
            }
        }
            fun bind(set: Set) {
                this.set = set
                binding.weights
                    .setText(set.weights.toString(), TextView.BufferType.EDITABLE)
                binding.reps
                    .setText(set.reps.toString(), TextView.BufferType.EDITABLE)
            }
        }

    private inner class SetAdapter: ListAdapter<Set, SetHolder>
        (AdapterUtil.DIFF_UTIL_SET_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetHolder {
            val view = SetBinding.inflate(layoutInflater, parent, false)
            return SetHolder(view)
        }

        override fun onBindViewHolder(holder: SetHolder, position: Int) {
            val set = getItem(position)
            return holder.bind(set)
        }
    }
}