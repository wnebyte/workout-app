package com.github.wnebyte.workoutapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.MainActivity
import com.github.wnebyte.workoutapp.databinding.FragmentHomeBinding
import com.github.wnebyte.workoutapp.databinding.FragmentHomeTabbedBinding
import com.github.wnebyte.workoutapp.util.DefaultLifecycleObserver

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    val binding get() = _binding!!

    var _binding: FragmentHomeTabbedBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeTabbedBinding
            .inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = binding.toolbar
        toolbar.inflateMenu(R.menu.fragment_home)
        val activity = requireActivity() as MainActivity
        activity.lifecycle.addObserver(object: DefaultLifecycleObserver {
            override fun onCreated(source: LifecycleOwner) {
                Log.i(TAG, "onCreated")
                activity.setupActionBar(toolbar)
                source.lifecycle.removeObserver(this)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}