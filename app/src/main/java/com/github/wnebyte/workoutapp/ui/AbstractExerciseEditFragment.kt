package com.github.wnebyte.workoutapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.databinding.FragmentExerciseEditBinding
import com.github.wnebyte.workoutapp.databinding.SetBinding
import com.github.wnebyte.workoutapp.util.Extensions.Companion.empty
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toEmptyString
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.Set
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.FadeInRightAnimator

abstract class AbstractExerciseEditFragment: Fragment() {

    protected val adapter = SetAdapter()

    protected val binding get() = _binding!!

    protected var _binding: FragmentExerciseEditBinding? = null

    protected val removedItems: MutableList<Set> = mutableListOf()

    protected lateinit var exercise: ExerciseWithSets

    protected var hashCode: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseEditBinding
            .inflate(layoutInflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.itemAnimator = FadeInRightAnimator().apply {
            addDuration = 250
            removeDuration = 250
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * Binds an instance of [ExerciseWithSets] to the UI.
     */
    protected fun updateUI() {
        binding.name
            .setText(exercise.exercise.name, TextView.BufferType.EDITABLE)
        adapter.submitList(exercise.sets)
    }

    /**
     * Adds a new item to the tail end of the underlying data-set.
     * The adapter will thereafter be notified of an inserted item.
     */
    protected fun dataSetAdd() {
        exercise.sets.add(
            Set.newInstance(exercise = exercise.exercise.id))
        adapter.notifyItemInserted(adapter.itemCount)
    }

    /**
     * Removes the item positioned at [index] from the underlying
     * data-set, and adds it to [removedItems].
     * The adapter will thereafter be notified of a removed item at position index.
     * @param index the index of the to-be removed item.
     */
    protected fun dataSetRemove(index: Int) {
        val item = exercise.sets.removeAt(index)
        removedItems.add(item)
        adapter.notifyItemRemoved(index)
    }

    /**
     * Inserts the specified [item] at position [index] in the underlying data-set, and
     * removes it from [removedItems].
     * The adapter will thereafter be notified of an inserted item at position index.
     * @param index the index of the to-be inserted item.
     * @param item the set to be inserted.
     */
    protected fun dataSetInsert(index: Int, item: Set) {
        removedItems.remove(item)
        exercise.sets.add(item)
        adapter.notifyItemInserted(index)
    }

    @SuppressLint("ClickableViewAccessibility")
    protected inner class SetHolder(private val binding: SetBinding):
        RecyclerView.ViewHolder(binding.root),
        OnSwipeListener,
        View.OnTouchListener {
        private lateinit var set: Set
        private val gestureDetector = GestureDetectorCompat(requireContext(), this)

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

        /**
         * Bind the specified [set] to the ViewHolder.
         * @param set to be bound.
         */
        fun bind(set: Set) {
            this.set = set
            binding.weights
                .setText(set.weights.toEmptyString(), TextView.BufferType.EDITABLE)
            binding.reps
                .setText(set.reps.toEmptyString(), TextView.BufferType.EDITABLE)
        }

        /**
         * Removes the [Set] instance associated with [getAdapterPosition] from the UI,
         * and prompts the display of a Snackbar where the user is presented with the option
         * of undoing said removal.
         */
        private fun removeSet() {
            val index = adapterPosition
            dataSetRemove(index)
            val snackbar = Snackbar.make(binding.root, String.empty(), Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) {
                    dataSetInsert(index, set)
                }
            snackbar.show()
        }

        override fun onSwipeRight() {
            removeSet()
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            gestureDetector.onTouchEvent(event)
            return false
        }
    }

    protected inner class SetAdapter: ListAdapter<Set, SetHolder>
        (AdapterUtil.DIFF_UTIL_SET_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetHolder {
            val view = SetBinding.inflate(layoutInflater, parent, false)
            return SetHolder(view)
        }

        override fun onBindViewHolder(holder: SetHolder, position: Int) {
            val set = getItem(position)
            return holder.bind(set)
        }

        override fun onViewAttachedToWindow(holder: SetHolder) {
            super.onViewAttachedToWindow(holder)
            //  holder.itemView.requestFocus()
        }
    }
}