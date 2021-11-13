package com.github.wnebyte.workoutapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.wnebyte.workoutapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    val vm: HomeViewModel by viewModels()

    val binding get() = _binding!!

    var _binding: FragmentHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding
            .inflate(layoutInflater, container, false)
        return binding.root
    }
}