package com.github.wnebyte.workoutapp.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.ui.workout.session.SessionFragment
import com.github.wnebyte.workoutapp.ui.workout.stopwatch.StopwatchFragment
import java.lang.IllegalStateException
import kotlin.collections.ArrayList

class ViewPagerFragment : Fragment() {

    private val args: ViewPagerFragmentArgs by navArgs()

    private lateinit var fragmentArgs: Bundle

    private lateinit var viewPagerFragmentAdapter: ViewPagerFragmentAdapter

    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workout_view_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager = view.findViewById(R.id.pager)
        fragmentArgs = args.copy().toBundle()
        fragmentArgs.remove(PENDING_INTENT_KEY)
        viewPagerFragmentAdapter = ViewPagerFragmentAdapter(this, fragmentArgs)
        viewPagerFragmentAdapter.addFragment(SessionFragment.newInstance(fragmentArgs))
        viewPagerFragmentAdapter.addFragment(StopwatchFragment.newInstance(fragmentArgs))
        viewPager.adapter = viewPagerFragmentAdapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        if (args.pendingIntent) {
            viewPager.currentItem = 1
        }
    }

    private class ViewPagerFragmentAdapter(fragment: Fragment, val bundle: Bundle) :
        FragmentStateAdapter(fragment) {

        private val fragments: ArrayList<Fragment> = ArrayList()

        fun getFragment(position: Int): Fragment {
            return fragments[position]
        }

        fun addFragment(fragment: Fragment) {
            fragments.add(fragment)
        }

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    SessionFragment.newInstance(bundle)
                }
                1 -> {
                    StopwatchFragment.newInstance(bundle)
                }
                else ->
                    throw IllegalStateException(
                        "Failed to create fragment of position: $position"
                    )
            }
        }
    }

    companion object {
        const val WORKOUT_ID_KEY = "workoutId"
        const val PENDING_INTENT_KEY = "pendingIntent"
    }
}