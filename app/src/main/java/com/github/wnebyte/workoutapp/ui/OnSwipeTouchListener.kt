package com.github.wnebyte.workoutapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import java.lang.Exception
import kotlin.math.abs

const val DEBUG_TAG = "Gestures"

abstract class OnSwipeTouchListener(context: Context) : View.OnTouchListener {

    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(context, GestureListener())
    }

    open fun onSwipeLeft() {

    }

    open fun onSwipeRight() {

    }

    open fun onSwipeTop() {

    }

    open fun onSwipeBottom() {

    }

    open fun onLongPress(e: MotionEvent?) {

    }

    open fun onSingleTap(e: MotionEvent?) {

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            Log.d(DEBUG_TAG, "onDown(): $e")
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            this@OnSwipeTouchListener.onLongPress(e)
            super.onLongPress(e)
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            this@OnSwipeTouchListener.onSingleTap(e)
            return super.onSingleTapUp(e)
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            return super.onDoubleTap(e)
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
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

    }

    companion object {

        private const val SWIPE_THRESHOLD = 100

        private const val SWIPE_VELOCITY_THRESHOLD = 100

    }
}