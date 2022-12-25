package com.github.wnebyte.workoutapp.gui.workout.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.wnebyte.workoutapp.databinding.FragmentWorkoutDetails2Binding
import com.github.wnebyte.workoutapp.gui.workout.host.WorkoutHostFragment

class WorkoutDetailsFragment: Fragment() {

    private val binding get() = _binding!!

    private var _binding: FragmentWorkoutDetails2Binding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutDetails2Binding
            .inflate(layoutInflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(bundle: Bundle, callbacks: WorkoutHostFragment.Callbacks?): Fragment {
            val fragment = WorkoutDetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}