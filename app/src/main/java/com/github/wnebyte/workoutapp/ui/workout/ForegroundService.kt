package com.github.wnebyte.workoutapp.ui.workout

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.wnebyte.workoutapp.MainActivity
import com.github.wnebyte.workoutapp.NOTIFICATION_CHANNEL_ID
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.util.Clock
import java.util.*

private const val TAG = "ForegroundService"

private const val NOTIFICATION_ID = 1

private const val WORKOUT_ID_EXTRA = "WorkoutId"

private const val START_VALUE_EXTRA = "StartValue"

class ForegroundService : Service() {

    private lateinit var broadcast: LocalBroadcastManager

    private lateinit var clock: Clock

    override fun onCreate() {
        super.onCreate()
        broadcast = LocalBroadcastManager.getInstance(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand(startId: $startId)")
        val workoutId: UUID = intent.getSerializableExtra(WORKOUT_ID_EXTRA) as UUID
        val startValue: Long = intent.getLongExtra(START_VALUE_EXTRA, 0L)
        val tickRate = 100L
        clock = object : Clock(tickRate, startValue) {

            override fun onTick(value: Long) {
                sendResult(value)
                if (value % 1000 == 0L) {
                    showBackgroundNotification(
                        NOTIFICATION_ID,
                        getNotification(formatMillis(value, "mm:ss"), workoutId)
                    )
                }
            }
        }
        clock.start()
        return START_STICKY
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy()")
        clock.stop()
        stopSelf()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun showBackgroundNotification(
        requestCode: Int,
        notification: Notification
    ) {
        val intent = Intent(ACTION_SHOW_NOTIFICATION).apply {
            putExtra(REQUEST_CODE, requestCode)
            putExtra(NOTIFICATION, notification)
        }
        this.sendOrderedBroadcast(intent, PERM_PRIVATE)
    }

    private fun getNotification(
        contentText: String,
        workoutId: UUID
    ): Notification {
        val pendingIntent = MainActivity.newPendingWorkoutIntent(this, workoutId)
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_timer_24)
            .setContentTitle(resources.getString(R.string.nav_workout_stopwatch))
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setSilent(true)
            .setTimeoutAfter(1500)
            .build()
    }

    private fun sendResult(value: Long?) {
        val intent = Intent(SERVICE_RESULT)
        value?.let {
            intent.putExtra(SERVICE_MESSAGE, value)
        }
        broadcast.sendBroadcast(intent)
    }

    companion object {
        const val SERVICE_MESSAGE = "message"
        const val SERVICE_RESULT = "result"

        const val ACTION_SHOW_NOTIFICATION = "com.github.wnebyte.workoutapp.SHOW_NOTIFICATION"
        const val PERM_PRIVATE = "com.github.wnebyte.workoutapp.PRIVATE"
        const val REQUEST_CODE = "REQUEST_CODE"
        const val NOTIFICATION = "NOTIFICATION"

        /**
         * Creates a new intent where the [workoutId] will be used when the application is
         * relaunched from the navigation drawer.
         */
        fun newIntent(
            context: Context,
            workoutId: UUID?,
            startValue: Long?
        ): Intent =
            Intent(context, ForegroundService::class.java).apply {
                putExtra(WORKOUT_ID_EXTRA, workoutId)
                putExtra(START_VALUE_EXTRA, startValue)
            }
    }
}