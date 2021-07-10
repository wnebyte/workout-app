package com.github.wnebyte.workoutapp.ui.workout

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.github.wnebyte.workoutapp.MainActivity
import com.github.wnebyte.workoutapp.NOTIFICATION_CHANNEL_ID
import com.github.wnebyte.workoutapp.R

private const val TAG = "WorkoutWorker"

class WorkoutWorker(val context: Context, workerParameters: WorkerParameters):
    Worker(context, workerParameters)
{
    override fun doWork(): Result {

        val intent = MainActivity.newIntent(context)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val resources = context.resources
        val notification = NotificationCompat
            .Builder(context, NOTIFICATION_CHANNEL_ID)
            .setTicker(resources.getString(R.string.notification_ticker))
            .setSmallIcon(R.drawable.ic_barbell_gold)
            .setContentTitle(resources.getString(R.string.notification_content_title))
            .setContentText(resources.getString(R.string.notification_content_text))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        showBackgroundNotification(0, notification)

        return Result.success()
    }

    private fun showBackgroundNotification(
        requestCode: Int,
        notification: Notification
    ) {
        val intent = Intent(ACTION_SHOW_NOTIFICATION).apply {
            putExtra(REQUEST_CODE, requestCode)
            putExtra(NOTIFICATION, notification)
        }
        context.sendOrderedBroadcast(intent, PERM_PRIVATE)
    }

    companion object {
        const val ACTION_SHOW_NOTIFICATION =
            "com.github.wnebyte.workoutapp.SHOW_NOTIFICATION"
        const val PERM_PRIVATE = "com.github.wnebyte.workoutapp.PRIVATE"
        const val REQUEST_CODE = "REQUEST_CODE"
        const val NOTIFICATION = "NOTIFICATION"
    }
}