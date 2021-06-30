package com.github.wnebyte.workoutapp.ui.sessionlist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.wnebyte.workoutapp.databinding.FragmentSessionListBinding

private const val TAG = "SessionListFragment"

class SessionListFragment: Fragment() {

    private val vm: SessionListViewModel by viewModels()

    private val binding get() = _binding!!

    private var _binding: FragmentSessionListBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionListBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.sessionLiveData.observe(
            viewLifecycleOwner,
            { workouts ->
                workouts?.let {
                    Log.i(TAG, "Got workouts: ${workouts.size}")
                }
            }
        )
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}