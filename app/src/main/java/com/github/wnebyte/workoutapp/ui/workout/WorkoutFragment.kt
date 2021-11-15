package com.github.wnebyte.workoutapp.ui.workout

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.github.wnebyte.workoutapp.ui.ViewPagerFragmentAdapter
import com.github.wnebyte.workoutapp.ui.ViewPagerHostFragment
import com.github.wnebyte.workoutapp.ui.workout.session.SessionFragment
import com.github.wnebyte.workoutapp.ui.workout.stopwatch.StopwatchFragment
import java.lang.IllegalStateException

class WorkoutFragment : ViewPagerHostFragment() {

    override lateinit var adapter: ViewPagerFragmentAdapter

    private val args: ViewPagerFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = copyArgs()
        adapter = Adapter(this, args)
        adapter.addAll(
            SessionFragment
                .newInstance(args),
            StopwatchFragment
                .newInstance(args)
        )
        setAdapter()
    }

    private fun copyArgs(): Bundle {
        return args.copy(workoutId = args.workoutId).toBundle()
    }

    private inner class Adapter(fragment: Fragment, val bundle: Bundle)
        : ViewPagerFragmentAdapter(fragment) {

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    SessionFragment
                        .newInstance(bundle)
                }
                1 -> {
                    StopwatchFragment
                        .newInstance(bundle)
                }
                else ->
                    throw IllegalStateException(
                        "position: $position is not supported."
                    )
            }
        }
    }
}