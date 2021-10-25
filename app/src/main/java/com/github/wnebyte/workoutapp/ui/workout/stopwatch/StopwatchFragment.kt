package com.github.wnebyte.workoutapp.ui.workout.stopwatch

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
import androidx.navigation.fragment.navArgs
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.databinding.FragmentWorkoutStopwatchBinding
import com.github.wnebyte.workoutapp.ui.workout.ForegroundService
import com.github.wnebyte.workoutapp.ui.workout.VisibleFragment
import com.github.wnebyte.workoutapp.util.Clock

private const val TAG = "StopwatchFragment"

class StopwatchFragment : VisibleFragment() {

    private val vm: StopwatchViewModel by viewModels()

    private val args: StopwatchFragmentArgs by navArgs()

    private var _binding: FragmentWorkoutStopwatchBinding? = null

    private val binding get() = _binding!!

    private lateinit var receiver: BroadcastReceiver

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ForegroundService.SERVICE_RESULT)
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                receiver,
                filter
            )
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(receiver)
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        super.registerReceiver()
    }

    override fun onPause() {
        super.onPause()
        super.unregisterReceiver()
        val index = binding.viewFlipper.displayedChild
        vm.index = index
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutStopwatchBinding
            .inflate(layoutInflater, container, false)
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val value = intent.getLongExtra(
                    ForegroundService.SERVICE_MESSAGE, 0L
                )
                binding.stopwatch.text = Clock.formatMillis(value)
                vm.value = value
                binding.viewFlipper.displayedChild = 1
            }
        }
        // update the ui whenever the view-flipper's layout changes
        binding.viewFlipper.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            registerOnClickListeners()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
        registerOnClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI() {
        binding.viewFlipper.displayedChild = vm.index
        binding.stopwatch.text = Clock.formatMillis(vm.value)
    }

    private fun registerOnClickListeners() {
        when (binding.viewFlipper.displayedChild) {
            0 -> {
                binding.buttonBar0.startButton.setOnClickListener {
                    requireContext()
                        .startService(
                            ForegroundService.newIntent(
                                requireContext(),
                                args.workoutId,
                                0L
                            )
                        )
                    binding.viewFlipper.displayedChild = 1
                }
            }
            1 -> {
                binding.buttonBar1.stopButton.setOnClickListener {
                    requireContext()
                        .stopService(
                            ForegroundService.newIntent(
                                requireContext(),
                                null,
                                null
                            )
                        )
                    binding.viewFlipper.displayedChild = 2
                }
            }
            2 -> {
                binding.buttonBar2.continueButton.setOnClickListener {
                    requireContext()
                        .startService(
                            ForegroundService.newIntent(
                                requireContext(),
                                args.workoutId,
                                vm.value
                            )
                        )
                    binding.viewFlipper.displayedChild = 1
                }
                binding.buttonBar2.resetButton.setOnClickListener {
                    binding.stopwatch.text = resources.getString(R.string.stopwatch_start_time)
                    vm.value = 0L
                    binding.viewFlipper.displayedChild = 0
                }
            }
            else -> {
                throw IllegalStateException(
                    "No such child exists"
                );
            }
        }
    }

    companion object {

        fun newInstance(bundle: Bundle) : StopwatchFragment {
            val fragment = StopwatchFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}