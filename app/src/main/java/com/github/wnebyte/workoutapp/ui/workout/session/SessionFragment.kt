package com.github.wnebyte.workoutapp.ui.workout.session

import java.util.*
import java.lang.IllegalStateException
import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.github.wnebyte.workoutapp.util.Extensions.Companion.format
import com.github.wnebyte.workoutapp.model.Set
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.WorkoutWithExercises
import com.github.wnebyte.workoutapp.ui.workout.ForegroundService

private const val TAG = "SessionFragment"

class SessionFragment : Fragment() {

    interface Callbacks {
        fun onFinished()
        fun onEditWorkout(workoutId: UUID, currentFragment: Class<out Fragment>)
        fun onEditCompletedWorkout(workoutId: UUID, currentFragment: Class<out Fragment>)
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
        workout = WorkoutWithExercises.newInstance()
        setHasOptionsMenu(false)
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
            val animationObjects = mutableListOf<AnimationObject>()

            for (i in size - 1 downTo 0) {
                val holder = binding.recyclerView.findViewHolderForAdapterPosition(i) as ExerciseHolder
                val body = holder.itemView.findViewById<LinearLayout>(R.id.body)
                val back = holder.itemView.findViewById<ImageView>(R.id.back)
                val animationObject = AnimationObject(back, body)
                animationObjects.add(animationObject)
            }

            flip(animationObjects)
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

    private fun flip(animationObjects: List<AnimationObject>) {
        val animatorSet = AnimatorSet()
        val animations = mutableListOf<Animator>()

        for (i in animationObjects.indices) {
            val animationObject = animationObjects[i]
            val animation = AnimatorSet()
            val visibleView = animationObject.visibleView
            val invisibleView = animationObject.invisibleView
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
        animatorSet.doOnEnd {
            workout.workout.completed = true
            context?.stopService(
                ForegroundService.newIntent(
                    requireContext(),
                    null,
                    null
                )
            )
            callbacks?.onFinished()
        }
        animatorSet.start()
    }

    private inner class ExerciseHolder(private val binding: ExerciseCardBinding) :
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
            adapter.submitList(exercise.sets)
        }

        override fun onClick(v: View) {
            val set: Set? = exercise.sets.find { s -> !s.completed }
            set?.let {
                set.completed = true
                adapter.notifyDataSetChanged()
                if (exercise.sets.all { s -> s.completed }) {
                    exercise.exercise.completed = true
                    if (workout.exercises.all { e -> e.exercise.completed }) {
                       // workout.workout.completed = true
                        animIn()
                    }
                }

            }
        }

        override fun onLongClick(v: View): Boolean {
            val set = exercise.sets.findLast { s -> s.completed }
            set?.let {
                it.completed = false
                exercise.exercise.completed = false
                workout.workout.completed = false
                adapter.notifyDataSetChanged()
                animOut()
            }
            return true
        }
    }

    private inner class AnimationObject(val visibleView: View, val invisibleView: View)

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

    companion object {
        fun newInstance(bundle: Bundle): SessionFragment {
            val fragment = SessionFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}