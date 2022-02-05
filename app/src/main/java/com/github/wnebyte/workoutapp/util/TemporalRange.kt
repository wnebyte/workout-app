package com.github.wnebyte.workoutapp.util

import java.util.*
import java.lang.IllegalStateException
import java.text.DateFormatSymbols
import android.os.Parcel
import android.os.Parcelable
import com.github.wnebyte.workoutapp.util.Extensions.Companion.month
import com.github.wnebyte.workoutapp.util.Extensions.Companion.year
import com.github.wnebyte.workoutapp.util.Extensions.Companion.format
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toDate

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
        return when ((field == YEAR) || (field == MONTH)) {
            true -> {
                copy(
                    field = field,
                    amount = amount,
                    lower = DateAdjuster(lower)
                        .subtract(field, amount)
                        .setMinimum(*slice(field))
                        .adjust(),
                    upper = DateAdjuster(upper)
                        .subtract(field, amount)
                        .setMaximum(*slice(field))
                        .adjust()
                )
            }
            false -> {
                copy(
                    field = field,
                    amount = amount,
                    lower = DateAdjuster(lower)
                        .subtract(field, amount)
                        .adjust(),
                    upper = DateAdjuster(upper)
                        .subtract(field, amount)
                        .adjust()
                )
            }
        }
    }

    override fun adjustUp(): TemporalRange {
        return when ((field == YEAR) || (field == MONTH)) {
            true -> {
                copy(
                    field = field,
                    amount = amount,
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
                    amount = amount,
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


    /**
     * Returns whether the specified [date] lies at or after the midway point represented by
     * this range.
     */
    fun after(date: Date): Boolean {
        return !date.before(
            DateAdjuster(lower)
                .add(field, amount)
                .adjust()
        )
    }

    override fun toString(): String {
        return when (field) {
            YEAR -> {
                "${upper.year()}"
            }
            MONTH -> {
                "${dfs.months[upper.month()]} ${upper.year()}"
            }
            DATE -> {
                val mid = DateAdjuster(upper)
                    .subtract(field, amount)
                    .adjust()
                "${mid.format("yyyy/MM/dd")} - ${upper.format("yyyy/MM/dd")}"
            }
            else -> throw IllegalStateException(
                "Error (Range): does not recognize field: '$field'."
            )
        }
    }

    companion object {

        const val YEAR: Int = Calendar.YEAR

        const val MONTH: Int = Calendar.MONTH

        const val DATE: Int = Calendar.DATE

        private const val HOUR_OF_DAY: Int = Calendar.HOUR_OF_DAY

        private const val MINUTE: Int = Calendar.MINUTE

        private const val SECOND: Int = Calendar.SECOND

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

        private val dfs: DateFormatSymbols = DateFormatSymbols.getInstance()

        private fun slice(field: Int): IntArray {
            return FIELDS.sliceArray(IntRange(FIELDS.indexOf(field) + 1, FIELDS.size - 1))
        }

        fun newInstance(
            field: Int,
            amount: Int = 1
        ): TemporalRange {
            val now = Date()
            return when ((field == YEAR) || (field == MONTH)) {
                true -> {
                    TemporalRange(
                        field = field,
                        amount = amount,
                        lower = DateAdjuster(now)
                            .subtract(field, amount)
                            .setMinimum(*slice(field))
                            .adjust(),
                        upper = DateAdjuster(now)
                            .setMaximum(*slice(field))
                            .adjust()
                    )
                }
                false -> {
                    TemporalRange(
                        field = field,
                        amount = amount,
                        lower = DateAdjuster(now)
                            .subtract(field, 2 * amount)
                            .adjust(),
                        upper = now
                    )
                }
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