package com.github.wnebyte.workoutapp.util

import android.os.Handler
import android.os.HandlerThread

abstract class Clock(val tickRate: Long, private var millis: Long = 0L) {

    private lateinit var handlerThread: HandlerThread

    private lateinit var handler: Handler

    var isRunning: Boolean = false

    private fun init() {
        handlerThread = HandlerThread("clockHandler")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    fun start() {
        init()
        isRunning = true
        onTick(millis)
        val r = Ticker(this)
        handler.postDelayed(r, tickRate)
    }

    fun stop() {
        handlerThread.quit()
        isRunning = false
    }

    protected abstract fun onTick(millis: Long)

    private class Ticker(private val clock: Clock) : Runnable {

        override fun run() {
            if (clock.isRunning) {
                clock.millis += clock.tickRate
                clock.onTick(clock.millis)
                clock.handler.postDelayed(this, clock.tickRate)
            }
        }
    }

    companion object {

        fun formatSeconds(value: Long): String {
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

        /**
         * Returns the elapsed time specified by [value] (ms) in the format hh:mm.s.
         */
        fun formatMillis(value: Long): String {
            return when (val seconds = value / 1000) {
                0L -> {
                    "00:00:0"
                }
                else -> {
                    val s = seconds % 60
                    val m = (seconds % 3600) / 60
                    val h = seconds / 3600
                    String.format("%02d:%02d.%01d", h, m, s)
                }
            }
        }
    }
}