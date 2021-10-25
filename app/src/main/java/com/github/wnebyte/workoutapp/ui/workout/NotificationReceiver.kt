package com.github.wnebyte.workoutapp.ui.workout

import android.app.Activity
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat

private const val TAG = "NotificationReceiver"

class NotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "received result $resultCode")
        if (resultCode != Activity.RESULT_OK) {
            // a foreground activity canceled the broadcast
            return
        }
        val requestCode = intent.getIntExtra(ForegroundService.REQUEST_CODE, 1)
        val notification: Notification =
            intent.getParcelableExtra(ForegroundService.NOTIFICATION)!!
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(requestCode, notification)
    }

}