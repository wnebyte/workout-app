package com.github.wnebyte.workoutapp.ui.workout

import java.lang.IllegalStateException
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.github.wnebyte.workoutapp.widget.ViewPagerFragmentAdapter
import com.github.wnebyte.workoutapp.ui.ViewPagerHostFragment
import com.github.wnebyte.workoutapp.ui.workout.session.SessionFragment
import com.github.wnebyte.workoutapp.ui.workout.stopwatch.StopwatchFragment

class HostFragment : ViewPagerHostFragment() {

    override lateinit var adapter: ViewPagerFragmentAdapter

    private val args: HostFragmentArgs by navArgs()

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
        viewPager.adapter = adapter
        tabLayout.visibility = View.GONE
        if (this.args.pendingIntent) {
            viewPager.currentItem = 1
        }
    }

    private fun copyArgs(): Bundle {
        return args.copy().toBundle()
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

    companion object {
        const val WORKOUT_ID_KEY = "workoutId"
        const val PENDING_INTENT_KEY = "pendingIntent"
    }
}