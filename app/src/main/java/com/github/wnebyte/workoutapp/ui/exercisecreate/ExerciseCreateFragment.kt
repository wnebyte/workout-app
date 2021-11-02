package com.github.wnebyte.workoutapp.ui.exercisecreate

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.databinding.FragmentExerciseCreateBinding
import com.github.wnebyte.workoutapp.databinding.SetBinding
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.Set
import com.github.wnebyte.workoutapp.ui.AdapterUtil
import com.github.wnebyte.workoutapp.ui.OnSwipeListener

private const val TAG = "ExerciseCreateFragment"

class ExerciseCreateFragment: Fragment() {

    interface Callbacks {
        fun onFinished()
    }

    private val vm: ExerciseCreateViewModel by viewModels()

    private val args: ExerciseCreateFragmentArgs by navArgs()

    private val adapter = SetAdapter()

    private val binding get() = _binding!!

    private var _binding: FragmentExerciseCreateBinding? = null

    private var callbacks: Callbacks? = null

    private var saveExercise = true

    private lateinit var exercise: ExerciseWithSets

    private val removedItems: MutableList<Set> = mutableListOf()

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
        exercise = ExerciseWithSets.newInstance(args.workoutId)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_exercise_create, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_set -> {
                val set: Set = Set.newInstance(exercise.exercise.id)
                exercise.sets.add(set)
                adapter.notifyItemInserted(adapter.itemCount)
                true
            } else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExerciseCreateBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.buttonBar.save.setOnClickListener {
            saveExercise = true
            callbacks?.onFinished()
        }
        binding.buttonBar.cancel.setOnClickListener {
            saveExercise = false
            callbacks?.onFinished()
        }
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
                    updateUI(it)
                }
            }
        )
        binding.name.doOnTextChanged { text, _, _, _ ->
            if (!TextUtils.isEmpty(text)) {
                exercise.exercise.name = text.toString()
            }
        }
        vm.loadExercise(args.workoutId)
    }

    override fun onStop() {
        super.onStop()
        if (saveExercise) {
            vm.saveExercise(exercise)
            vm.deleteSets(removedItems)
        } else {
            vm.deleteExercise(exercise)
            vm.deleteSets(removedItems)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI(exercise: ExerciseWithSets) {
        binding.name
            .setText(exercise.exercise.name, TextView.BufferType.EDITABLE)
        adapter.submitList(exercise.sets)
    }

    @SuppressLint("ClickableViewAccessibility")
    private inner class SetHolder(private val binding: SetBinding):
        RecyclerView.ViewHolder(binding.root),
        OnSwipeListener,
        View.OnTouchListener {
        private lateinit var set: Set
        private val gestureDetector: GestureDetectorCompat =
            GestureDetectorCompat(requireContext(), this)

        init {
            binding.weights.setOnTouchListener(this)
            binding.reps.setOnTouchListener(this)
            binding.repsLayout.setEndIconOnClickListener {
                removeSet()
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

        private fun removeSet() {
            removedItems.add(set)
            exercise.sets.remove(set)
            this@ExerciseCreateFragment.adapter.notifyItemRemoved(adapterPosition)
        }

        override fun onClick() {
            itemView.requestFocus()
        }

        override fun onSwipeLeft() {
            Log.i(TAG, "onSwipeLeft()")
        }

        override fun onSwipeRight() {
            removeSet()
            // Todo: add animation
        }

        override fun onSwipeTop() {
            Log.i(TAG, "onSwipeTop()")
        }

        override fun onSwipeBottom() {
            Log.i(TAG, "onSwipeBottom()")
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            return gestureDetector.onTouchEvent(event)
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