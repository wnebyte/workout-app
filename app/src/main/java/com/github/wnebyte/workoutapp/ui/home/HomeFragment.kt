package com.github.wnebyte.workoutapp.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.github.wnebyte.workoutapp.ui.ViewPagerHostFragment
import com.github.wnebyte.workoutapp.ui.ViewPagerFragmentAdapter
import com.github.wnebyte.workoutapp.ui.home.last.LastWorkoutOverviewFragment
import com.github.wnebyte.workoutapp.ui.home.next.NextWorkoutOverviewFragment
import java.lang.IllegalStateException

private const val TAG = "HomeFragment"

class HomeFragment : ViewPagerHostFragment() {

    override lateinit var adapter: ViewPagerFragmentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = Adapter(this)
        adapter.addAll(
            NextWorkoutOverviewFragment
                .newInstance(),
            LastWorkoutOverviewFragment
                .newInstance()
        )
        setAdapter()
    }

    private inner class Adapter(fragment: Fragment) : ViewPagerFragmentAdapter(fragment) {

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    NextWorkoutOverviewFragment
                        .newInstance()
                }
                1 -> {
                    LastWorkoutOverviewFragment
                        .newInstance()
                }
                else -> {
                    throw IllegalStateException(
                        "position: $position is not supported."
                    )
                }
            }
        }
    }
}