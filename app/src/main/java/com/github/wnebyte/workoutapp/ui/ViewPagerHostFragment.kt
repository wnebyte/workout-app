package com.github.wnebyte.workoutapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.github.wnebyte.workoutapp.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

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
            .inflate(R.layout.fragment_workout_view_pager, container, false)
        viewPager = view.findViewById(R.id.pager)
        tabLayout = view.findViewById(R.id.tabDots)
        return view
    }

    /**
     * Should be called by subclasses somewhere after onCreateView().
     */
    protected fun setAdapter() {
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }
            .attach()
    }
}