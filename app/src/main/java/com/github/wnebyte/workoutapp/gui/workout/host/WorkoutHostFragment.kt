package com.github.wnebyte.workoutapp.gui.workout.host

import java.util.*
import java.lang.Exception
import java.lang.IllegalStateException
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.github.wnebyte.workoutapp.MainActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.databinding.FragmentWorkoutHostBinding
import com.github.wnebyte.workoutapp.gui.workout.details.WorkoutDetailsFragment
import com.github.wnebyte.workoutapp.gui.workout.exercises.WorkoutExercisesFragment
import com.github.wnebyte.workoutapp.util.DefaultLifecycleObserver
import com.github.wnebyte.workoutapp.widget.ViewPagerFragmentAdapter

private const val TAG = "WorkoutHostFragment"

class WorkoutHostFragment : Fragment() {

    interface Callbacks {
        fun onFinished()
        fun onImportExercise(workoutId: UUID, currentFragment: Class<out Fragment>)
        fun onCreateExercise(workoutId: UUID, currentFragment: Class<out Fragment>)
        fun onEditExercise(exerciseId: UUID, currentFragment: Class<out Fragment>)
    }

    private val binding get() = _binding!!

    private var _binding: FragmentWorkoutHostBinding? = null

    private var callbacks: Callbacks? = null

    private lateinit var adapter: Adapter

    /*
    override val menuResId = R.menu.fragment_home

    override lateinit var toolbar: Toolbar
     */

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callbacks = context as Callbacks
        } catch (e: Exception) {
            throw IllegalStateException(
                "Hosting activity does not implement WorkoutHostFragment.Callbacks"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutHostBinding
            .inflate(layoutInflater, container, false)
       // toolbar = binding.toolbar
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setup toolbar
        binding.toolbar.inflateMenu(R.menu.fragment_workout_host)
        val size = binding.toolbar.menu.size()
        Log.i(TAG, "$size")
        val activity = requireActivity() as MainActivity
        val created = activity.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)
        if (!created) {
            activity.lifecycle.addObserver(object: DefaultLifecycleObserver {
                override fun onCreated(source: LifecycleOwner) {
                    activity.setupActionBar(binding.toolbar)
                    source.lifecycle.removeObserver(this)
                }

            })
        } else {
            activity.setupActionBar(binding.toolbar)
        }
        // setup adapter
        adapter = Adapter(this, Bundle.EMPTY)
        adapter.addAll(
            WorkoutDetailsFragment
                .newInstance(Bundle.EMPTY, callbacks),
            WorkoutExercisesFragment
                .newInstance(Bundle.EMPTY, callbacks)
        )
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager, true, true,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = "DETAILS"
                    }
                    1 -> {
                        tab.text = "EXERCISES"
                    }
                }
            }).attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class Adapter(fragment: Fragment, val bundle: Bundle) :
        ViewPagerFragmentAdapter(fragment) {

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    WorkoutDetailsFragment
                        .newInstance(bundle, this@WorkoutHostFragment.callbacks)
                }
                1 -> {
                    WorkoutExercisesFragment
                        .newInstance(bundle, this@WorkoutHostFragment.callbacks)
                }
                else ->
                    throw IllegalStateException(
                        "position: $position is not supported."
                    )
            }
        }
    }
}