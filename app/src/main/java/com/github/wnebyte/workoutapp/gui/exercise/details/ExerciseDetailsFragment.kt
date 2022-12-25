package com.github.wnebyte.workoutapp.gui.exercise.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.wnebyte.workoutapp.gui.exercise.host.ExerciseHostFragment

class ExerciseDetailsFragment : Fragment() {

    companion object {
        fun newInstance(
            bundle: Bundle,
            callbacks: ExerciseHostFragment.Callbacks?
        ): Fragment {
            val fragment = ExerciseDetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}