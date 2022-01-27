package com.github.wnebyte.workoutapp.util

import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import java.lang.IllegalStateException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

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

        fun Date.day(): Int {
            val calendar = Calendar.getInstance()
            calendar.time = this
            return calendar.get(Calendar.DATE)
        }

        fun Date.toLastOfTheMonth(): Date {
            val calendar = Calendar.getInstance()
            calendar.time = this
            val max = calendar.getActualMaximum(Calendar.DATE)
            calendar.set(Calendar.DATE, max)
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

        fun Float.toSign(): String {
            return if (isPositive()) {
                "+"
            } else {
                ""
            }
        }

        fun Collection<Double>.avg(): Double {
            return this.sumByDouble { it } / this.size
        }
    }
}