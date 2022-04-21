package com.github.wnebyte.workoutapp.util

import java.util.*
import java.lang.IllegalStateException
import java.text.ParseException
import java.text.SimpleDateFormat
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.github.wnebyte.workoutapp.util.Extensions.Companion.hour
import java.lang.IllegalArgumentException
import kotlin.math.abs

class Extensions {

    companion object {

        /*
        ###########################
        #           DATE          #
        ###########################
        */

        fun Date.format(
            sdf: String = "yyyy/MM/dd HH:mm",
            locale: Locale = Locale.getDefault()
        ): String {
            return SimpleDateFormat(sdf, locale).format(this)
        }

        fun String.toDate(
            sdf: String = "yyyy/MM/dd HH:mm",
            locale: Locale = Locale.getDefault()
        ): Date? {
            return try {
                SimpleDateFormat(sdf, locale).parse(this)
            } catch (e: ParseException) {
                null
            }
        }

        fun Long.toDate(): Date {
            return Date(this)
        }

        fun Date.year(): Int {
            val calendar = Calendar.getInstance()
            calendar.time = this
            return calendar.get(Calendar.YEAR)
        }

        fun Date.month(): Int {
            val calendar = Calendar.getInstance()
            calendar.time = this
            return calendar.get(Calendar.MONTH)
        }

        fun Date.date(): Int {
            val calendar = Calendar.getInstance()
            calendar.time = this
            return calendar.get(Calendar.DATE)
        }

        fun Date.hour(): Int {
            val calendar = Calendar.getInstance()
            calendar.time = this
            return calendar.get(Calendar.HOUR_OF_DAY)
        }

        fun Date.minute(): Int {
            val calendar = Calendar.getInstance()
            calendar.time = this
            return calendar.get(Calendar.MINUTE)
        }

        fun Date.second(): Int {
            val calendar = Calendar.getInstance()
            calendar.time = this
            return calendar.get(Calendar.SECOND)
        }

        fun Date.millisecond(): Int {
            val calendar = Calendar.getInstance()
            calendar.time = this
            return calendar.get(Calendar.MILLISECOND)
        }

        // Todo: hh/mm/ss are not set correctly
        fun Date.toLastOfThisMonth(): Date {
            val calendar = Calendar.getInstance()
            calendar.time = this
            var max = calendar.getActualMaximum(Calendar.DATE)
            calendar.set(Calendar.DATE, max)
            max = calendar.getActualMaximum(Calendar.HOUR_OF_DAY)
            calendar.set(Calendar.HOUR_OF_DAY, max)
            max = calendar.getActualMaximum(Calendar.MINUTE)
            calendar.set(Calendar.MINUTE, max)
            max = calendar.getActualMaximum(Calendar.SECOND)
            calendar.set(Calendar.SECOND, max)
            return calendar.time
        }

        fun Date.toLastOfLastMonth(): Date {
            val calendar = Calendar.getInstance()
            calendar.time = this
            calendar.add(Calendar.MONTH, -1)
            val max = calendar.getActualMaximum(Calendar.DATE)
            calendar.set(Calendar.DATE, max)
            return calendar.time
        }

        fun Date.toLastOfNextMonth(): Date {
            val calendar = Calendar.getInstance()
            calendar.time = this
            calendar.add(Calendar.MONTH, +1)
            val max = calendar.getActualMaximum(Calendar.DATE)
            calendar.set(Calendar.DATE, max)
            return calendar.time
        }

        fun Date.toFirstOfLastMonth(): Date {
            val calendar = Calendar.getInstance()
            calendar.time = this
            calendar.add(Calendar.MONTH, -1)
            val min = calendar.getActualMinimum(Calendar.DATE)
            calendar.set(Calendar.DATE, min)
            return calendar.time
        }

        fun <T> AutoCompleteTextView.showDropdown(adapter: ArrayAdapter<T>?) {
            if (!TextUtils.isEmpty(this.text.toString())) {
                adapter?.filter?.filter(null)
            }
        }

        /**
         * @return this CharSequence as a Char.
         */
        fun CharSequence.toChar(): Char {
            if (this.length != 1) {
                throw IllegalStateException(
                    ""
                )
            }
            return this[0]
        }

        /**
         * @return an empty String if this Int equals 0,
         * else [Any.toString].
         */
        fun Int.toEmptyString(): String {
            return when (this) {
                0 -> {
                    ""
                }
                else -> {
                    this.toString()
                }
            }
        }

        /**
         * @return an empty String if this Double equals 0.0,
         * else [Any.toString].
         */
        fun Double.toEmptyString(): String {
            return when (this) {
                0.0 -> {
                    ""
                }
                else -> {
                    this.toString()
                }
            }
        }

        /**
         * @return `"0".concat(this.toString())` if this Int lies within the inclusive range (0,9),
         * else [Any.toString].
         */
        fun Int.toPaddedString(): String {
            return when (this.toString().length) {
                1 -> {
                    ("0$this")
                }
                else -> {
                    this.toString()
                }
            }
        }

        /**
         * @return an empty String.
         */
        fun String.Companion.empty(): String {
            return ""
        }

        fun Float.isPositive(): Boolean {
            return this >= 0.0f
        }

        fun Float.isNegative(): Boolean {
            return !isPositive()
        }

        /**
         * Returns `+` if this `Float` is positive or `0.0`,
         * otherwise `-`.
         */
        fun Float.toSign(): String {
            return if (isPositive()) {
                "+"
            } else {
                "-"
            }
        }

        fun Collection<Double>.avg(): Double {
            return when (this.size) {
                0 -> {
                    0.0
                } else -> {
                    this.sumByDouble { it } / this.size
                }
            }
        }

        fun Collection<Float>.avg(): Float {
            return when (this.size) {
                0 -> {
                    0.0f
                } else -> {
                    (this.sumByDouble { it.toDouble() } / this.size).toFloat()
                }
            }
        }

        fun Int.toBoolean(): Boolean {
            return when (this) {
                0 -> {
                    false
                }
                1 -> {
                    true
                }
                else -> {
                    throw IllegalArgumentException(
                        "Value must represent 0 or 1."
                    )
                }
            }
        }
    }
}