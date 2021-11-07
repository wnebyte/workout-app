package com.github.wnebyte.workoutapp.ui

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import java.lang.Exception
import kotlin.math.abs

private const val TAG = "OnSwipeListener"

interface OnSwipeListener : GestureDetector.OnGestureListener {

    override fun onDown(e: MotionEvent?): Boolean {
        Log.i(TAG, "onDown()")
        return true
    }

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {

    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        Log.i(TAG, "velocityX: $velocityX, velocityY: $velocityY")
        var result = false
        try {
            val diffY = e2.y - e1.y
            val diffX = e2.x - e1.x
            if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                    result = true
                }
            }
            else if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeBottom()
                } else {
                    onSwipeTop()
                }
                result = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun onSwipeLeft() {

    }

    fun onSwipeRight() {

    }

    fun onSwipeTop() {

    }

    fun onSwipeBottom() {

    }

    companion object {

        private const val SWIPE_THRESHOLD = 25

        private const val SWIPE_VELOCITY_THRESHOLD = 25

    }
}