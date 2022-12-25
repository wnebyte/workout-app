package com.github.wnebyte.workoutapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.github.wnebyte.workoutapp.R

abstract class ViewPagerHostFragment : Fragment() {

    protected abstract val adapter: FragmentStateAdapter

    protected lateinit var viewPager: ViewPager2

    protected lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater
            .inflate(R.layout.fragment_view_pager, container, false)
        viewPager = view.findViewById(R.id.pager)
        tabLayout = view.findViewById(R.id.tab_layout)
        return view
    }

    protected fun setAdapter() {
        viewPager.adapter = adapter
    }

    protected fun attachTabLayoutMediator() {
        val conf: TabLayoutMediator.TabConfigurationStrategy =
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = "DETAILS"
                    }
                    1 -> {
                        tab.text = "EXERCISES"
                    }
                }
            }
        TabLayoutMediator(tabLayout, viewPager,
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
}