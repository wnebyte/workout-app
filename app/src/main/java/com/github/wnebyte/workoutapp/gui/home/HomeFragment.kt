package com.github.wnebyte.workoutapp.gui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.MainActivity
import com.github.wnebyte.workoutapp.databinding.FragmentHomeBinding
import com.github.wnebyte.workoutapp.util.DefaultLifecycleObserver

class HomeFragment : Fragment() {

    private val binding get() = _binding!!

    private var _binding: FragmentHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding
            .inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setup toolbar
        val toolbar = binding.toolbar
        toolbar.inflateMenu(R.menu.fragment_home)
        val activity = requireActivity() as MainActivity
        val created = activity.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)
        if (!created) {
            activity.lifecycle.addObserver(object: DefaultLifecycleObserver {
                override fun onCreated(source: LifecycleOwner) {
                    activity.setupActionBar(toolbar)
                    source.lifecycle.removeObserver(this)
                }

            })
        } else {
            activity.setupActionBar(toolbar)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}