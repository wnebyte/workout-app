package com.github.wnebyte.workoutapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.ui.home.last.LastWorkoutOverviewFragment
import com.github.wnebyte.workoutapp.ui.home.next.NextWorkoutOverviewFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.lang.IllegalStateException

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    private lateinit var adapter: ViewPagerFragmentAdapter

    private lateinit var viewPager: ViewPager2

    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater
            .inflate(R.layout.fragment_workout_view_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById(R.id.pager)
        tabLayout = view.findViewById(R.id.tabDots)
        adapter = ViewPagerFragmentAdapter(this)
        adapter.addAll(
            NextWorkoutOverviewFragment
                .newInstance(),
            LastWorkoutOverviewFragment
                .newInstance()
        )
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }
            .attach()
    }

    private class ViewPagerFragmentAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

        private val fragments: ArrayList<Fragment> = ArrayList()

        fun add(fragment: Fragment) {
            fragments.add(fragment)
        }

        fun addAll(vararg fragments: Fragment) {
            fragments.forEach { add(it) }
        }

        override fun getItemCount(): Int {
            return fragments.size
        }

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