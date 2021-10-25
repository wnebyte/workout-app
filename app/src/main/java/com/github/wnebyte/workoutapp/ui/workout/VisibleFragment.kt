package com.github.wnebyte.workoutapp.ui.workout

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.fragment.app.Fragment

private const val TAG = "VisibleFragment"

abstract class VisibleFragment: Fragment() {

    private val onShowNotification = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(TAG, "canceling notification")
            resultCode = Activity.RESULT_CANCELED
        }
    }

    protected fun registerReceiver() {
        if (context != null) {
            Log.i(TAG, "Receiver registered")
            val filter = IntentFilter(ForegroundService.ACTION_SHOW_NOTIFICATION)
            requireContext().registerReceiver(
                onShowNotification,
                filter,
                ForegroundService.PERM_PRIVATE,
                null
            )
        }
    }

    protected fun unregisterReceiver() {
        if (context != null) {
            Log.i(TAG, "Receiver unregistered")
            requireContext().unregisterReceiver(onShowNotification)
        }
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