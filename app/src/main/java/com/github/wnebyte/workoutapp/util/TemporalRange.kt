package com.github.wnebyte.workoutapp.util

import java.util.*
import java.lang.IllegalStateException
import java.text.DateFormatSymbols
import android.os.Parcel
import android.os.Parcelable
import com.github.wnebyte.workoutapp.util.Extensions.Companion.addDays
import com.github.wnebyte.workoutapp.util.Extensions.Companion.format
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toDate
import com.github.wnebyte.workoutapp.util.Extensions.Companion.month
import com.github.wnebyte.workoutapp.util.Extensions.Companion.subtractDays
import com.github.wnebyte.workoutapp.util.Extensions.Companion.year
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toFirstOfLastYear
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toFirstOfNextMonth
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toFirstOfLastMonth
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toFirstOfNextYear
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toLastOfLastMonth
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toLastOfLastYear
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toLastOfThisYear
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toLastOfNextMonth
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toLastOfNextYear
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toLastOfThisMonth
import kotlin.math.abs

data class TemporalRange(
    val field: Int,
    val amount: Int = 1,
    val lower: Date,
    val upper: Date
): Parcelable, ITemporalRange {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong().toDate(),
        parcel.readLong().toDate()
    )

    override fun adjustDown(): TemporalRange {
        return when (field) {
            MONTH -> {
                copy(
                    field = field,
                    lower = lower.toFirstOfLastMonth(),
                    upper = upper.toLastOfLastMonth()
                )
            }
            YEAR -> {
                copy(
                    field = field,
                    lower = lower.toFirstOfLastYear(),
                    upper = upper.toLastOfLastYear()
                )
            }
            DATE -> {
                copy(
                    field = field,
                    amount = amount,
                    lower = lower.subtractDays(amount),
                    upper = upper.subtractDays(amount)
                )
            }
            else -> this
        }
    }

    fun testDown(): TemporalRange {
        return copy(
            field = field,
            lower = lower,
            upper = upper
        )
    }

    fun testUp(): TemporalRange {
        val limit = true
        return when (limit) {
            true -> {
                copy(
                    field = field,
                    lower = DateAdjuster(lower)
                        .add(field, amount)
                        .setMinimum(*slice(field))
                        .adjust(),
                    upper = DateAdjuster(upper)
                        .add(field, amount)
                        .setMaximum(*slice(field))
                        .adjust()
                )
            }
            false -> {
                copy(
                    field = field,
                    lower = DateAdjuster(lower)
                        .add(field, amount)
                        .adjust(),
                    upper = DateAdjuster(upper)
                        .add(field, amount)
                        .adjust()
                )
            }
        }
    }

    override fun adjustUp(): TemporalRange {
        return when (field) {
            MONTH -> {
                copy(
                    field = field,
                    // 01/01 -> 01/02
                    lower = lower.toFirstOfNextMonth(),
                    // 30/02 -> 31/03
                    upper = upper.toLastOfNextMonth()
                )
            }
            YEAR -> {
                copy(
                    field = field,
                    // 01/01/20 -> 01/01/21
                    lower = lower.toFirstOfNextYear(),
                    // 31/12/21 -> 31/12/22
                    upper = upper.toLastOfNextYear()
                )
            }
            DATE -> {
                copy(
                    field = field,
                    amount = amount,
                    lower = lower.addDays(amount),
                    upper = upper.addDays(amount)
                )
            }
            else -> throw IllegalStateException(
                "Error (TemporalRange): does not recognize flag: '$field'."
            )
        }
    }

    fun shard(l: Long): Boolean {
        return when (field) {
            MONTH -> {
                l.toDate().month() == upper.month()
            }
            YEAR -> {
                l.toDate().year() == upper.year()
            }
            DATE -> {
                l >= lower.addDays(amount).time
            }
            else -> throw IllegalStateException(
                "Error (Range): does not recognize flag: '$field'."
            )
        }
    }

    override fun toString(): String {
        return when (field) {
            MONTH -> {
                "${DateFormatSymbols().months[upper.month()]} ${upper.year()}"
            }
            YEAR -> {
                "${upper.year()}"
            }
            // Todo: Should describe the midway-point of the temporal range
            DATE -> {
                "${upper.subtractDays(amount).format("yyyy/MM/dd")} - ${upper.format("yyyy/MM/dd")}"
            }
            else -> throw IllegalStateException(
                "Error (Range): does not recognize flag: '$field'."
            )
        }
    }

    companion object {

        const val YEAR: Int = Calendar.YEAR

        const val MONTH: Int = Calendar.MONTH

        const val DATE: Int = Calendar.DATE

        const val HOUR_OF_DAY: Int = Calendar.HOUR_OF_DAY

        const val MINUTE: Int = Calendar.MINUTE

        const val SECOND: Int = Calendar.SECOND

        private const val MILLISECOND: Int = Calendar.MILLISECOND

        private val FIELDS: IntArray = intArrayOf(
            YEAR,
            MONTH,
            DATE,
            HOUR_OF_DAY,
            MINUTE,
            SECOND,
            MILLISECOND
        )

        private fun slice(field: Int): IntArray {
            return FIELDS.sliceArray(IntRange(FIELDS.indexOf(field) + 1, FIELDS.size))
        }

        fun newInstance(flag: Int, amount: Int = 1): TemporalRange {
            val now = Date()
            return when (flag) {
                MONTH -> {
                    TemporalRange(
                        MONTH,
                        amount = 1,
                        now.toFirstOfLastMonth(),
                        now.toLastOfThisMonth()
                    )
                }
                YEAR -> {
                    TemporalRange(
                        YEAR,
                        amount = 1,
                        now.toFirstOfLastYear(),
                        now.toLastOfThisYear()
                    )
                }
                DATE -> {
                    TemporalRange(
                        DATE,
                        amount,
                        now.subtractDays(2 * amount),
                        now
                    )
                }
                else -> throw IllegalStateException(
                    "Error (Range): does not recognize flag: '$flag'."
                )
            }
        }

        @JvmField
        val CREATOR = object : Parcelable.Creator<TemporalRange> {

            override fun createFromParcel(source: Parcel): TemporalRange {
                return TemporalRange(source)
            }

            override fun newArray(size: Int): Array<TemporalRange> {
                return arrayOf()
            }

        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(field)
        dest.writeInt(amount)
        dest.writeLong(lower.time)
        dest.writeLong(upper.time)
    }
}