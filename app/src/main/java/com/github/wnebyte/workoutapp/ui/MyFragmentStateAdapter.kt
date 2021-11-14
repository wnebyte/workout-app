package com.github.wnebyte.workoutapp.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

abstract class MyFragmentStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments: ArrayList<Fragment> = arrayListOf()

    fun add(fragment: Fragment) {
        fragments.add(fragment)
    }

    fun addAll(vararg fragments: Fragment) {
        fragments.forEach { add(it) }
    }

    fun get(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size
    }
}