package com.github.wnebyte.workoutapp.gui.workout.exercises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.wnebyte.workoutapp.databinding.FragmentWorkoutExercisesBinding
import com.github.wnebyte.workoutapp.gui.workout.host.WorkoutHostFragment

class WorkoutExercisesFragment : Fragment() {

    private val binding get() = _binding!!

    private var _binding: FragmentWorkoutExercisesBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutExercisesBinding
            .inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            bundle: Bundle,
            callbacks: WorkoutHostFragment.Callbacks?
        ): Fragment {
            val fragment = WorkoutExercisesFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}