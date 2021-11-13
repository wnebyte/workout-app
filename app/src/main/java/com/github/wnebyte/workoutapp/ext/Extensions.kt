package com.github.wnebyte.workoutapp.ext

import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.github.wnebyte.workoutapp.ext.Extensions.Companion.toLastOfLastMonth
import com.github.wnebyte.workoutapp.ext.Extensions.Companion.toLastOfTheMonth
import java.lang.IllegalStateException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Extensions {

    companion object {

        fun Date.format(
            sdf: String = "yyyy/MM/dd HH:mm"
        ): String {
            return SimpleDateFormat(sdf, Locale.getDefault()).format(this)
        }

        fun dateToString(
            date: String,
            sdf: String = "yyyy/MM/dd HH:mm"
        ): Date? {
            return try {
                SimpleDateFormat(sdf, Locale.getDefault()).parse(date)
            } catch (e: ParseException) {
                null
            }
        }

        fun Date.toYear(): Int {
            val calendar = Calendar.getInstance()
            calendar.time = this
            return calendar.get(Calendar.YEAR)
        }

        fun Date.toMonth(): Int {
            val calendar = Calendar.getInstance()
            calendar.time = this
            return calendar.get(Calendar.MONTH)
        }

        fun Date.toDay(): Int {
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

        fun Float.toSign(): String {
            return if (isPositive()) {
                "+"
            } else {
                "-"
            }
        }

        fun List<Double>.avg(): Double {
            return this.sumByDouble { it } / this.size
        }
    }
}