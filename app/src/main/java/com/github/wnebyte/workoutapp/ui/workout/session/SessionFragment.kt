package com.github.wnebyte.workoutapp.ui.workout.session

import java.lang.IllegalStateException
import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import android.animation.*
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.databinding.*
import com.github.wnebyte.workoutapp.ui.AdapterUtil
import com.github.wnebyte.workoutapp.ui.workout.ForegroundService
import com.github.wnebyte.workoutapp.util.Extensions.Companion.format
import com.github.wnebyte.workoutapp.model.Set
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises

private const val TAG = "SessionFragment"

class SessionFragment : Fragment() {

    interface Callbacks {
        fun onFinished()
    }

    private val vm: SessionViewModel by viewModels()

    private val args: SessionFragmentArgs by navArgs()

    private val adapter = ExerciseAdapter()

    private val binding get() = _binding!!

    private var _binding: FragmentWorkoutSessionTestBinding? = null

    private var callbacks: Callbacks? = null

    private lateinit var workout: WorkoutWithExercises

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callbacks = context as Callbacks
        } catch (e: Exception) {
            throw IllegalStateException(
                "Hosting activity needs to implement Callbacks interface"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workout = WorkoutWithExercises.newInstance() // will be re-initialized
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutSessionTestBinding
            .inflate(layoutInflater, container, false)
        binding.recyclerView.adapter = adapter
        binding.fab.setOnClickListener {
            val size = binding.recyclerView.size
            val l = mutableListOf<Pair<View, View>>()
            for (i in size - 1 downTo 0) {
                val holder = binding.recyclerView.findViewHolderForAdapterPosition(i) as ExerciseHolder
                val body = holder.itemView.findViewById<View>(R.id.body)
                val back = holder.itemView.findViewById<View>(R.id.back)
                val pair = Pair(back, body)
                l.add(pair)
            }
            val animation = getFlipAnimation(l)
            animation.doOnEnd {
                context?.let {
                    it.stopService(
                        ForegroundService.newIntent(
                            it, null, null
                        )
                    )
                }
                workout.workout.completed = true
                callbacks?.onFinished()
            }
            animation.start()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.workoutLiveData.observe(
            viewLifecycleOwner,
            { workout ->
                workout?.let {
                    Log.i(TAG, "got workout: ${it.workout.id}")
                    this.workout = it
                    updateUI()
                }
            }
        )
        vm.loadWorkout(args.workoutId)
    }

    override fun onStop() {
        super.onStop()
        vm.saveWorkout(workout)
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
        // bind workout name to ui
        binding.name
            .setText(workout.workout.name, TextView.BufferType.NORMAL)
        // bind workout date to ui
        binding.date
            .setText(workout.workout.date?.format(), TextView.BufferType.NORMAL)
        // bind exercises to the ui
        adapter.submitList(workout.exercises)
        // update fab pos
        if (workout.exercises.all { e -> e.exercise.completed }) {
            if (binding.fab.x < 0) {
                animIn()
            }
        } else {
            if (binding.fab.x > 0) {
                animOut()
            }
        }
    }

    private fun dipToPx(dip: Float): Float {
        val r: Resources = resources
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            r.displayMetrics
        )
        return px
    }

    private fun animIn() {
        val view = binding.fab
        val start = resources.getDimensionPixelSize(R.dimen.fab_anim_x_start)
        val end = resources.displayMetrics.widthPixels
        val offset = dipToPx(16f)
        val translate = (start + end - offset)
        view.visibility = View.VISIBLE
        ObjectAnimator.ofFloat(view, "translationX", translate).apply {
            duration = 1000
            start()
        }
    }

    private fun animOut() {
        val view = binding.fab
        val start = resources.getDimensionPixelSize(R.dimen.fab_anim_x_start)
        val end = resources.displayMetrics.widthPixels
        val offset = dipToPx(16f)
        val translate = (start + end - offset)
        ObjectAnimator.ofFloat(view, "translationX", -translate).apply {
            duration = 1250
            doOnEnd {
                view.visibility = View.GONE
            }
            start()
        }
    }

    private fun getFlipAnimation(l: List<Pair<View, View>>): AnimatorSet {
        val animatorSet = AnimatorSet()
        val animations = mutableListOf<Animator>()

        for (i in l.indices) {
            val pair = l[i]
            val animation = AnimatorSet()
            val visibleView = pair.first
            val invisibleView = pair.second
            val scale = requireContext().resources.displayMetrics.density
            val cameraDistance = 8000 * scale
            visibleView.cameraDistance = cameraDistance
            invisibleView.cameraDistance = cameraDistance
            val flipOutAnimatorSet = AnimatorInflater.loadAnimator(
                context,
                R.animator.flip_out
            ) as AnimatorSet
            flipOutAnimatorSet.setTarget(invisibleView)
            flipOutAnimatorSet.doOnEnd {
                invisibleView.visibility = View.INVISIBLE
            }
            val flipInAnimatorSet = AnimatorInflater.loadAnimator(
                context,
                R.animator.flip_in
            ) as AnimatorSet
            flipInAnimatorSet.setTarget(visibleView)
            flipInAnimatorSet.doOnStart {
                visibleView.visibility = View.VISIBLE
            }
            animation.startDelay = i * 250L
            animation.playTogether(flipOutAnimatorSet, flipInAnimatorSet)
            animations.add(animation)
        }

        animatorSet.playTogether(animations)
        return animatorSet
    }

    private fun flip(view: View): AnimatorSet {
        val animation = AnimatorSet()
        val scale = requireContext().resources.displayMetrics.density
        val cameraDistance = 8000 * scale
        view.cameraDistance = cameraDistance
        val flipOutAnimatorSet = AnimatorInflater.loadAnimator(
            context,
            R.animator.flip_out
        ) as AnimatorSet
        flipOutAnimatorSet.setTarget(view)
        val flipInAnimatorSet = AnimatorInflater.loadAnimator(
            context,
            R.animator.flip_in
        ) as AnimatorSet
        flipInAnimatorSet.setTarget(view)
        flipInAnimatorSet.startDelay = 750L
        animation.playTogether(flipOutAnimatorSet, flipInAnimatorSet)
        return animation
    }

    private fun getAnimation(resId: Int, v: View): AnimatorSet {
        context?.let {
            val scale = it.resources.displayMetrics.density
            val cameraDistance = 8000 * scale
            v.cameraDistance = cameraDistance
        }
        val animation = AnimatorInflater.loadAnimator(
            context,
            resId
        ) as AnimatorSet
        animation.setTarget(v)
        return animation
    }

    private inner class ExerciseHolder(private val binding: ExerciseCardClickableBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener,
        View.OnLongClickListener {
        private lateinit var exercise: ExerciseWithSets
        private val adapter = SetAdapter()

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)
            binding.body.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.body.recyclerView.adapter = adapter
        }

        fun bind(exercise: ExerciseWithSets) {
            this.exercise = exercise
            binding.body.title.text = exercise.exercise.name
            binding.root.isChecked = exercise.exercise.completed
            adapter.submitList(exercise.sets)
        }

        override fun onClick(v: View) {
            val set: Set? = exercise.sets.find { s -> !s.completed }
            set?.let {
                set.completed = true
                adapter.notifyDataSetChanged()
                if (exercise.sets.all { s -> s.completed }) {
                    val view = binding.body.root
                    val flipOut = getAnimation(R.animator.flip_out_single_view, view)
                    val flipIn = getAnimation(R.animator.flip_in_single_view, view)
                    flipIn.doOnStart {
                        exercise.exercise.completed = true
                        adapter.notifyDataSetChanged()
                        if (workout.exercises.all { e -> e.exercise.completed }) {
                            animIn()
                        }
                    }
                    val animation = AnimatorSet()
                    animation.doOnEnd {
                        this@SessionFragment.adapter.notifyDataSetChanged()
                    }
                    animation.playSequentially(flipOut, flipIn)
                    animation.start()
                }
            }
        }

        override fun onLongClick(v: View): Boolean {
            val set = exercise.sets.findLast { s -> s.completed }
            set?.let {
                it.completed = false
                adapter.notifyDataSetChanged()
                val completed = exercise.exercise.completed
                exercise.exercise.completed = false
                workout.workout.completed = false
                if (completed) {
                    this@SessionFragment.adapter.notifyDataSetChanged()
                    animOut()
                }
            }
            return true
        }

        private inner class SetHolder(private val binding: SetItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
            private lateinit var set: Set

            fun bind(set: Set) {
                this.set = set
                "${set.weights} x ${set.reps}".also { binding.tv.text = it }
                if (!exercise.exercise.completed && set.completed) {
                    binding.tv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    binding.tv.paintFlags = 0
                }
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

    private inner class ExerciseAdapter : ListAdapter<ExerciseWithSets, ExerciseHolder>
        (AdapterUtil.DIFF_UTIL_EXERCISE_WITH_SETS_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseHolder {
            val view = ExerciseCardClickableBinding
                .inflate(layoutInflater, parent, false)
            return ExerciseHolder(view)
        }

        override fun onBindViewHolder(holder: ExerciseHolder, position: Int) {
            val exercise = getItem(position)
            return holder.bind(exercise)
        }
    }

    companion object {
        fun newInstance(bundle: Bundle): SessionFragment {
            val fragment = SessionFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}