package com.github.wnebyte.workoutapp.ui.workout

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.wnebyte.workoutapp.databinding.FragmentWorkoutBinding

class WorkoutFragment: Fragment() {

    private val vm: WorkoutViewModel by viewModels()

    private val binding get() = _binding!!

    private var _binding: FragmentWorkoutBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutBinding
            .inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}