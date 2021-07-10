package com.github.wnebyte.workoutapp.ui.workout

import android.app.Notification
import android.app.NotificationManager
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

private const val TAG = "MyForegroundService"
private const val NOTIFICATION_ID = 1
private const val MILLIS_IN_FUTURE = "MillisInFuture"
private const val COUNT_DOWN_INTERVAL = "CountdownInterval"

class MyForegroundService: Service() {

    private lateinit var broadcast: LocalBroadcastManager

    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate() {
        super.onCreate()
        broadcast = LocalBroadcastManager.getInstance(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i(TAG, "service started: $startId")
        val millIsInFuture = intent.getLongExtra(MILLIS_IN_FUTURE, 30000L)
        val countDownInterval = intent.getLongExtra(COUNT_DOWN_INTERVAL, 1000L)

        countDownTimer = object: CountDownTimer(millIsInFuture, countDownInterval) {

            override fun onTick(millis: Long) {
                Log.i(TAG, "tick: ${millis/1000}")
                sendResult(millis)
                showBackgroundNotification(
                    NOTIFICATION_ID,
                    getNotification("${millis/1000}")
                )
            }

            override fun onFinish() {
                Log.i(TAG, "finished")
                sendResult(0L)
                showBackgroundNotification(
                    NOTIFICATION_ID,
                    getNotification("0")
                )
                stopSelf()
            }

        }.start()
        showBackgroundNotification(
            NOTIFICATION_ID,
            getNotification("${millIsInFuture/1000}")
        )
        return START_STICKY
    }

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

    private fun getNotification(contentText: String): Notification {
        val pendingIntent = MainActivity.newPendingIntent(this)
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
            .setContentTitle(getString(R.string.notification_content_title))
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
          //  .setOngoing(true)
            .setAutoCancel(true)
            .build()
    }

    private fun updateServiceNotification(text: String) {
        val notification = getNotification(text)
        val notificationManager: NotificationManager =
            getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        Log.i(TAG, "Service destroyed")
        countDownTimer.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    fun sendResult(value: Long?) {
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

        fun newIntent(
            context: Context,
            millisInFuture: Long? = null,
            countDownInterval: Long? = null
        ): Intent =
            Intent(context, MyForegroundService::class.java).apply {
                putExtra(MILLIS_IN_FUTURE, millisInFuture)
                putExtra(COUNT_DOWN_INTERVAL, countDownInterval)
            }
    }
}