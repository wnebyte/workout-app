package com.github.wnebyte.workoutapp.ui.workout.stopwatch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.navArgs
import com.github.wnebyte.workoutapp.databinding.FragmentWorkoutStopwatchBinding
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toChar
import com.github.wnebyte.workoutapp.ui.workout.ForegroundService
import com.github.wnebyte.workoutapp.ui.workout.VisibleFragment
import com.github.wnebyte.workoutapp.util.Clock

private const val TAG = "StopwatchFragment"

class StopwatchFragment : VisibleFragment() {

    private val vm: StopwatchViewModel by viewModels()

    private val args: StopwatchFragmentArgs by navArgs()

    private var _binding: FragmentWorkoutStopwatchBinding? = null

    private val binding get() = _binding!!

    private var _digits: Array<TextView>? = null

    private val digits get() = _digits!!

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
        vm.index = binding.vf.displayedChild
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutStopwatchBinding
            .inflate(layoutInflater, container, false)
        _digits = arrayOf(
            binding.i0, binding.i1, binding.i2,
            binding.i3, binding.i4, binding.i5, binding.i6
        )
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val value = intent.getLongExtra(
                    ForegroundService.SERVICE_MESSAGE, 0L
                )
                vm.value = value
                vm.index = 1
                updateUI()
            }
        }
        // re-register onClickListeners whenever the view-flipper's layout changes
        binding.vf.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
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
        _digits = null
    }

    private fun updateUI() {
        binding.vf.displayedChild = vm.index
        val values = Clock.formatMMSSMS(vm.value).toCharArray()

        for (i in values.indices) {
            val tv = digits[i]
            val currentChar = tv.text.toChar()
            val newChar = values[i]
            if (currentChar != newChar) {
                tv.text = newChar.toString()
            }
        }
    }

    private fun registerOnClickListeners() {
        when (binding.vf.displayedChild) {
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
                    binding.vf.displayedChild = 1
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
                    binding.vf.displayedChild = 2
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
                    binding.vf.displayedChild = 1
                }
                binding.buttonBar2.resetButton.setOnClickListener {
                    vm.value = 0L
                    vm.index = 0
                    updateUI()
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