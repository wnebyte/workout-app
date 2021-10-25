package com.github.wnebyte.workoutapp.util

import java.lang.IllegalArgumentException
import java.lang.Math.pow
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class DateUtil {

    companion object {

        fun fromString(date: String): Date? {
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
            return try {
                sdf.parse(date)
            } catch (ex: ParseException) {
                null
            }
        }

        fun fromDate(date: Date): String? {
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
            return sdf.format(date)
        }

        fun normalize(num: Int): String =
            when (num.toString().length) {
                1 -> {
                    ("0$num")
                }
                else -> {
                    num.toString()
                }
            }

        /**
         * Returns a string from the specified elapsed long in the format mm:ss.
         * @param value the long measured in seconds
         */
        private fun formatT(value: Long): String {
            return when (value) {
                0L -> {
                    "00:00"
                }
                else -> {
                    val s = value % 60
                    val m = (value / 60) % 60
                    String.format("%02d:%02d", m, s)
                }
            }
        }

        /**
         * Returns a string from the specified elapsed long in the format mm:ss.
         * @param value the long
         * @param unit the TimeUnit of the specified long
         */
        fun formatT(value: Long, unit: TimeUnit = TimeUnit.SECONDS): String {
            return when (unit) {
                TimeUnit.SECONDS -> {
                    formatT(value)
                }
                TimeUnit.MILLISECONDS -> {
                    formatT(value / 1000)
                }
                TimeUnit.MICROSECONDS -> {
                    formatT(value / 1000000)
                }
                TimeUnit.NANOSECONDS -> {
                    formatT(value / 1000000000)
                }
                else -> {
                    throw IllegalArgumentException(
                        "TimeUnit is not supported."
                    )
                }
            }
        }

        /**
         * Returns a string from the specified long in the format hh:mm.s.
         * @param value the long measured in seconds
         */
        private fun format(value: Long): String {
            return when (value) {
                0L -> {
                    "00:00.0"
                }
                else -> {
                    val s = value % 60
                    val m = (value / 60) % 60
                    val h = (value / (60 * 60)) % 24
                    String.format("%02d:%02d.%01d", h, m, s)
                }
            }
        }

        /**
         * Returns a string from the specified elapsed long in the format hh:mm.s.
         * @param value the long
         * @param unit the TimeUnit of the specified long
         */
        fun format(value: Long, unit: TimeUnit = TimeUnit.SECONDS): String {
            return when (unit) {
                TimeUnit.SECONDS -> {
                    format(value)
                }
                TimeUnit.MILLISECONDS -> {
                    format(value / 1000)
                }
                TimeUnit.MICROSECONDS -> {
                    format(value / 1000000)
                }
                TimeUnit.NANOSECONDS -> {
                    format(value / 1000000000)
                }
                else -> {
                    throw IllegalArgumentException(
                        "TimeUnit is not supported."
                    )
                }
            }
        }
    }
}