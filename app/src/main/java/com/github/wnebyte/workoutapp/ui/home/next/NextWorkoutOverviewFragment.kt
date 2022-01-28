package com.github.wnebyte.workoutapp.ui.home.next

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.fragment.app.viewModels
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.ui.home.AbstractWorkoutOverviewFragment
import com.github.wnebyte.workoutapp.ui.home.AbstractWorkoutOverviewViewModel

class NextWorkoutOverviewFragment : AbstractWorkoutOverviewFragment() {

    override val vm: AbstractWorkoutOverviewViewModel by viewModels<NextWorkoutOverviewViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.title.text = resources.getString(R.string.next_workout_title)
        binding.chronometer.isCountDown = true
    }

    companion object {

        fun newInstance(): NextWorkoutOverviewFragment {
            return NextWorkoutOverviewFragment()
        }
    }
}