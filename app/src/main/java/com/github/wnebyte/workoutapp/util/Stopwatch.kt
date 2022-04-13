package com.github.wnebyte.workoutapp.util

import android.os.Handler
import android.os.HandlerThread
import java.lang.IllegalArgumentException

private const val HANDLER_THREAD_NAME = "StopwatchHandler"

abstract class Stopwatch(val tickRate: Long, private var value: Long = 0L) {

    private lateinit var handlerThread: HandlerThread

    private lateinit var handler: Handler

    var isRunning: Boolean = false

    private fun init() {
        handlerThread = HandlerThread(HANDLER_THREAD_NAME)
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    fun start() {
        if (isRunning) {
            stop()
        }
        init()
        isRunning = true
        onTick(value)
        val r = Ticker()
        handler.postDelayed(r, tickRate)
    }

    fun stop() {
        handlerThread.quit()
        isRunning = false
    }

    protected abstract fun onTick(value: Long)

    private inner class Ticker : Runnable {

        override fun run() {
            if (isRunning) {
                value += tickRate
                onTick(value)
                handler.postDelayed(this, tickRate)
            }
        }
    }

    companion object {

        // Todo: move format companion functions elsewhere

        /**
         * @param value time elapsed in seconds.
         * @param format hh:mm:ss or mm:ss
         */
        fun formatSeconds(value: Long, format: String = "hh:mm:ss"): String {
            when (format) {
                "hh:mm:ss" -> {
                    return when (value) {
                        0L -> {
                            "00:00:0"
                        }
                        else -> {
                            val s = value % 60
                            val m = (value % 3600) / 60
                            val h = value / 3600
                            String.format("%02d:%02d.%01d", h, m, s)
                        }
                    }
                }
                "mm:ss" -> {
                    return when (value) {
                        0L -> {
                            "00:00:0"
                        }
                        else -> {
                            val s = value % 60
                            val m = (value % 3600) / 60
                            String.format("%02d:%02d", m, s)
                        }
                    }
                }
                else -> {
                    throw IllegalArgumentException(
                        ""
                    )
                }
            }
        }

        /**
         * @param ms time elapsed (ms)
         * @return mm:ss.f
         */
        fun formatMMSSMS(ms: Long): String {
            return when (val value: Float = ms.toFloat() / 1000) {
                0.0F -> {
                    "00:00.0"
                }
                else -> {
                    val m: Long = (value / 60).toLong()
                    val s: Float = value % 60
                    var fraction: Float = value * 1000
                    fraction %= 1000
                    fraction /= 100
                    String.format("%02d:%02d.%01d", m, s.toLong(), fraction.toLong())
                }
            }
        }

        /**
         * Returns the elapsed time specified by [value] (ms) in the format hh:mm.s.
         */
        fun formatMillis(value: Long, format: String = "hh:mm:ss"): String {
            return formatSeconds(value / 1000, format)
        }
    }
}