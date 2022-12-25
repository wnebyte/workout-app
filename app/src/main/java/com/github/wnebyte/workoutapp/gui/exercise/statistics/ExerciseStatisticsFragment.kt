package com.github.wnebyte.workoutapp.gui.exercise.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.wnebyte.workoutapp.databinding.FragmentExerciseStatisticsBinding
import com.github.wnebyte.workoutapp.gui.exercise.host.ExerciseHostFragment

class ExerciseStatisticsFragment : Fragment() {

    private val binding get() = _binding!!

    private var _binding: FragmentExerciseStatisticsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExerciseStatisticsBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            bundle: Bundle,
            callbacks: ExerciseHostFragment.Callbacks?
        ): Fragment {
            val fragment = ExerciseStatisticsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}