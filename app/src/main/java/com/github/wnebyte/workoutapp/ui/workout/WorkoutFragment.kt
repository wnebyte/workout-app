package com.github.wnebyte.workoutapp.ui.workout

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.wnebyte.workoutapp.databinding.FragmentWorkoutBinding
import com.github.wnebyte.workoutapp.util.DateUtil.Companion.format
import java.util.concurrent.TimeUnit

private const val TAG = "WorkoutFragment"

class WorkoutFragment: VisibleFragment() {

    private val vm: WorkoutViewModel by viewModels()

    private val initMillisInFuture: Long = 60000L

    private val binding get() = _binding!!

    private var _binding: FragmentWorkoutBinding? = null

    private lateinit var receiver: BroadcastReceiver

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(MyForegroundService.SERVICE_RESULT)
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                receiver,
                filter
            )
    }

    override fun onStop() {
        Log.i(TAG, "onStop()")
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(receiver)
        vm.saveMillisInFuture()
        super.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutBinding
            .inflate(layoutInflater, container, false)
        if (vm.millisInFuture == null) { // brand new??
            Log.i(TAG, "millisInFuture == null")
            vm.millisInFuture = initMillisInFuture
            vm.saveMillisInFuture(vm.millisInFuture!!)
        }
        binding.textViewProgress.text = format(vm.millisInFuture!!, TimeUnit.MILLISECONDS)
        binding.buttonStart.setOnClickListener {
            requireContext()
                .startService(
                    MyForegroundService.newIntent(
                        requireContext(),
                        vm.millisInFuture,
                        1000L
                    ))
        }
        binding.buttonStop.setOnClickListener {
            requireContext()
                .stopService(
                    MyForegroundService.newIntent(requireContext())
                )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        receiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val millis = intent
                    .getLongExtra(MyForegroundService.SERVICE_MESSAGE, -1L)
                vm.saveMillisInFuture(millis)
                binding.textViewProgress.text = format(millis, TimeUnit.MILLISECONDS)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}