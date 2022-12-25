package com.github.wnebyte.workoutapp.gui.exercise.host

import java.lang.Exception
import java.lang.IllegalStateException
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.tabs.TabLayoutMediator
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.MainActivity
import com.github.wnebyte.workoutapp.databinding.FragmentExerciseHostBinding
import com.github.wnebyte.workoutapp.gui.exercise.details.ExerciseDetailsFragment
import com.github.wnebyte.workoutapp.gui.exercise.statistics.ExerciseStatisticsFragment
import com.github.wnebyte.workoutapp.util.DefaultLifecycleObserver
import com.github.wnebyte.workoutapp.widget.ViewPagerFragmentAdapter

class ExerciseHostFragment : Fragment() {

    interface Callbacks {
        fun onFinished()
    }

    private val binding get() = _binding!!

    private var _binding: FragmentExerciseHostBinding? = null

    private var callbacks: Callbacks? = null

    private lateinit var adapter: Adapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callbacks = context as Callbacks
        } catch (e: Exception) {
            throw IllegalStateException(
                "Hosting activity needs to implement ExerciseHostFragment.Callbacks"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExerciseHostBinding
            .inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setup toolbar
        binding.toolbar.inflateMenu(R.menu.fragment_exercise_host)
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
        adapter = Adapter(this, Bundle.EMPTY, callbacks)
        adapter.addAll(
            ExerciseDetailsFragment
                .newInstance(Bundle.EMPTY, callbacks),
            ExerciseStatisticsFragment
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
                        tab.text = "STATISTICS"
                    }
                }
            }).attach()
    }

    private inner class Adapter(
        fragment: Fragment,
        val bundle: Bundle,
        val callbacks: Callbacks?
        ) : ViewPagerFragmentAdapter(fragment) {

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    ExerciseDetailsFragment
                        .newInstance(bundle, callbacks)
                }
                1 -> {
                    ExerciseStatisticsFragment
                        .newInstance(bundle, callbacks)
                }
                else ->
                    throw IllegalStateException(
                        "position: $position is not supported."
                    )
            }
        }
    }

}