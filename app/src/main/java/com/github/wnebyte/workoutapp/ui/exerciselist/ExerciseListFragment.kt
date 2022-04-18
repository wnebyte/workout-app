package com.github.wnebyte.workoutapp.ui.exerciselist

import java.util.*
import java.lang.Exception
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.databinding.*
import com.github.wnebyte.workoutapp.ui.AdapterUtil
import com.github.wnebyte.workoutapp.ui.OnSwipeListener
import com.github.wnebyte.workoutapp.model.Set
import com.github.wnebyte.workoutapp.model.ExerciseWithSets

private const val TAG = "ExerciseListFragment"

class ExerciseListFragment : Fragment() {

    interface Callbacks {
        fun onEditExercise(exerciseId: UUID, currentFragment: Class<out Fragment>)
        fun onCreateExercise()
    }

    private val vm: ExerciseListViewModel by viewModels()

    private val adapter = ExerciseAdapter()

    private val binding get() = _binding!!

    private val removedItems: MutableList<ExerciseWithSets> = mutableListOf()

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
                    Log.i(TAG, "Got exercises: ${it.size}")
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

    override fun onStop() {
        super.onStop()
        vm.deleteExercises(removedItems)
    }

    @SuppressLint("ClickableViewAccessibility")
    private inner class ExerciseHolder(private val binding: ExerciseCardActionableBinding):
        RecyclerView.ViewHolder(binding.root),
        OnSwipeListener,
        View.OnTouchListener {
        private lateinit var exercise: ExerciseWithSets
        private val adapter = SetAdapter()
        private val gestureDetector = GestureDetectorCompat(context, this)

        init {
            binding.body.root.setOnTouchListener(this)
            binding.body.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.body.recyclerView.adapter = adapter
            binding.actionBar.edit.setOnClickListener {
                callbacks
                    ?.onEditExercise(exercise.exercise.id, this@ExerciseListFragment::class.java)
            }
            binding.actionBar.delete.setOnClickListener {
                deleteExercise()
            }
        }

        fun bind(exercise: ExerciseWithSets) {
            this.exercise = exercise
            binding.body.title.text = exercise.exercise.name
            adapter.submitList(exercise.sets)
        }

        private fun deleteExercise() {
            vm.deleteExercise(exercise)
            val snackbar = Snackbar.make(binding.root, R.string.delete_action, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) {
                    vm.saveExercise(exercise)
                }
            snackbar.show()
        }

        override fun onSwipeRight() {
            deleteExercise()
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            return gestureDetector.onTouchEvent(event)
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

        fun bind(set: Set) {
            this.set = set
            "${set.weights} x ${set.reps}".also { binding.tv.text = it }
        }
    }

    private inner class SetAdapter : ListAdapter<Set, SetHolder>
        (AdapterUtil.DIFF_UTIL_SET_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetHolder {
            val view = SetItemBinding
                .inflate(layoutInflater, parent, false)
            return SetHolder(view)
        }

        override fun onBindViewHolder(holder: SetHolder, position: Int) {
            val set = getItem(position)
            return holder.bind(set)
        }
    }
}