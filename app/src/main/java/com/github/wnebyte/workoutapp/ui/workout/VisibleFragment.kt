package com.github.wnebyte.workoutapp.ui.workout

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.fragment.app.Fragment
import com.github.wnebyte.workoutapp.util.ContextHelper

private const val TAG = "VisibleFragment"

abstract class VisibleFragment: Fragment() {

    private val onShowNotification = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(TAG, "canceling notification")
            resultCode = Activity.RESULT_CANCELED
        }
    }

    protected fun registerReceiver() {
        Log.i(TAG, "receiver registered")
        val filter = IntentFilter(ContextHelper
            .prependPackageName(requireContext(), ForegroundService.ACTION_SHOW_NOTIFICATION))
        requireContext().registerReceiver(
            onShowNotification,
            filter,
            ContextHelper.prependPackageName(requireContext(), ForegroundService.PERM_PRIVATE),
            null
        )
    }

    protected fun unregisterReceiver() {
        Log.i(TAG, "receiver unregistered")
        requireContext()
            .unregisterReceiver(onShowNotification)
    }

    /*
    override fun onStart() {
        super.onStart()
        registerReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver()
    }
     */
}