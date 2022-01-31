package com.github.wnebyte.workoutapp.ui.progressdetails

import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.databinding.FragmentProgressDetailsBinding
import com.github.wnebyte.workoutapp.model.ProgressItem
import com.github.wnebyte.workoutapp.util.Extensions.Companion.day
import com.github.wnebyte.workoutapp.util.Extensions.Companion.format
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toDate
import kotlin.collections.ArrayList

private const val TAG = "ProgressDetailsFragment"

class ProgressDetailsFragment :
    Fragment(), OnChartValueSelectedListener {

    private val vm: ProgressDetailsViewModel by viewModels()

    private val args: ProgressDetailsFragmentArgs by navArgs()

    private var _binding: FragmentProgressDetailsBinding? = null

    private val binding get() = _binding!!

    private lateinit var chart: LineChart

    private lateinit var progressItem: ProgressItem

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressDetailsBinding
            .inflate(inflater, container, false)
        this.chart = binding.chart
        this.progressItem = args.progressItem
        return binding.root
    }

    /*
    SHOULD DISPLAY:
    graph of exercise specific data for a given month
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "x: ${progressItem.x.size}, y: ${progressItem.y.size}")
        val x = progressItem.x
        val y = progressItem.y

        for (i in x.indices) {
            Log.i(TAG, "x: ${x[i].toDate().format()}, y: ${y[i]}")
        }

        init(progressItem.x, progressItem.y, progressItem.name)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun init(
        x: List<Long>,
        y: List<Float>,
        name: String
    ) {
        chart.setBackgroundColor(Color.WHITE)
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        // set listeners
        chart.setOnChartValueSelectedListener(this)
        chart.setDrawGridBackground(false)
        chart.isDragEnabled = true
        chart.isScaleXEnabled = true
        chart.isScaleYEnabled = true
        chart.setPinchZoom(true)

        val xAxis: XAxis = chart.xAxis

        val yAxis: YAxis = chart.axisLeft
        // disable dual axis (only use LEFT axis)
        chart.axisRight.isEnabled = false

        // horizontal grid lines
        yAxis.enableGridDashedLine(10f, 10f, 0f)

        yAxis.axisMinimum = y.minOf { v -> v }
        yAxis.axisMaximum = y.maxOf { v -> v }

        // add data
        populateData(x.map { l -> l.toDate().day().toLong() }, y, null, name)
        chart.animateX(1500)

        // get the legend (only possible after adding data)
        val legend = chart.legend
        legend.form = Legend.LegendForm.LINE
    }

    private fun populateData(
        x: List<Long>,
        y: List<Float>,
        icon: Drawable? = null,
        name: String = "Dataset"
    ) {
        val len = x.size
        val entries: ArrayList<Entry> = ArrayList(len)

        for (i in x.indices) {
            val entry = Entry(x[i].toFloat(), y[i], icon)
            entries.add(entry)
        }

        val set: LineDataSet

        if (chart.data != null && chart.data.dataSetCount > 0) {
            set = chart.data.getDataSetByIndex(0) as LineDataSet
            set.values = entries
            set.notifyDataSetChanged()
            chart.data.notifyDataChanged()
        } else {
            // create a dataset and give it a type
            set = LineDataSet(entries, name)
            set.setDrawIcons(false)
            // draw dashed line
            set.enableDashedLine(10f, 5f, 0f)
            // black lines and points
            set.color = Color.BLACK
            set.setCircleColor(Color.BLACK)
            // line thickness and point size
            set.lineWidth = 1f
            set.circleRadius = 3f
            // draw points as solid circles
            set.setDrawCircleHole(false)
            // customize legend entry
            set.formLineWidth = 1f
            set.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set.formSize = 15f
            // text size of values
            set.valueTextSize = 9f
            // draw selection line as dashed
            set.enableDashedHighlightLine(10f, 5f, 0f)
            // set the filled area
            set.setDrawFilled(true)
            set.fillFormatter = IFillFormatter { _, _ ->
                chart.axisLeft.axisMinimum
            }
            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.gradient_line)
                 set.fillDrawable = drawable
            } else {
                set.fillColor = R.color.colorAccent
            }
            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(set) // add the data sets
            // create a data object with the data sets
            val data = LineData(dataSets)
            // set data
            chart.data = data
        }
    }

    private fun init() {
        chart.setBackgroundColor(Color.WHITE)
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)

        // set listeners
        chart.setOnChartValueSelectedListener(this)

        chart.setDrawGridBackground(false)

        /*
        val mv = MarkerView(requireContext(), R.layout.exercise_card)
        mv.chartView = chart
        chart.marker = mv
        */

        chart.isDragEnabled = true
        chart.isScaleXEnabled = true
        chart.isScaleYEnabled = true

        chart.setPinchZoom(true)

        val xAxis: XAxis = chart.xAxis
        // vertical grid lines
        xAxis.enableGridDashedLine(10f, 10f, 0f)

        val yAxis: YAxis = chart.axisLeft
        // disable dual axis (only use LEFT axis)
        chart.axisRight.isEnabled = false
        // horizontal grid lines
        yAxis.enableGridDashedLine(10f, 10f, 0f)

        yAxis.axisMinimum = -50f
        yAxis.axisMaximum = 200f

        // Create Limit Lines
        val llXAxis = LimitLine(9f, "Index 10")
        llXAxis.lineWidth = 4f
        llXAxis.enableDashedLine(10f, 10f, 0f)
        llXAxis.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
        llXAxis.textSize = 10f
        // llXAxis.typeface = tfRegular

        val ll1 = LimitLine(150f, "Upper Limit")
        ll1.lineWidth = 4f
        ll1.enableDashedLine(10f, 10f, 0f)
        ll1.labelPosition = LimitLabelPosition.RIGHT_TOP
        ll1.textSize = 10f
        // ll1.typeface = tfRegular

        val ll2 = LimitLine(-30f, "Lower Limit")
        ll2.lineWidth = 4f
        ll2.enableDashedLine(10f, 10f, 0f)
        ll2.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
        ll2.textSize = 10f
        // ll2.typeface = tfRegular

        // draw limit lines behind data instead of on top
        yAxis.setDrawLimitLinesBehindData(true)
        xAxis.setDrawLimitLinesBehindData(true)

        // add limit lines
        yAxis.addLimitLine(ll1)
        yAxis.addLimitLine(ll2)
        //xAxis.addLimitLine(llXAxis);

        // add data
        setData(45, 180f)
        chart.animateX(1500)

        // get the legend (only possible after adding data)
        val legend = chart.legend
        legend.form = Legend.LegendForm.LINE
    }

    private fun setData(count: Int, range: Float) {
        val entries: ArrayList<Entry> = arrayListOf()
        var i = 0.0f

        while (i < count) {
            val value: Float = ((Math.random() * range) - 30).toFloat()
            val entry = Entry(i, value, null)
            entries.add(entry)
            i++
        }

        val set: LineDataSet

        if (chart.data != null && chart.data.dataSetCount > 0) {
            set = chart.data.getDataSetByIndex(0) as LineDataSet
            set.values = entries
            set.notifyDataSetChanged()
            chart.data.notifyDataChanged()
        } else {
            // create a dataset and give it a type
            set = LineDataSet(entries, "DataSet 1")
            set.setDrawIcons(false)
            // draw dashed line
            set.enableDashedLine(10f, 5f, 0f)
            // black lines and points
            set.color = Color.BLACK
            set.setCircleColor(Color.BLACK)
            // line thickness and point size
            set.lineWidth = 1f
            set.circleRadius = 3f
            // draw points as solid circles
            set.setDrawCircleHole(false)
            // customize legend entry
            set.formLineWidth = 1f
            set.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set.formSize = 15f
            // text size of values
            set.valueTextSize = 9f
            // draw selection line as dashed
            set.enableDashedHighlightLine(10f, 5f, 0f)
            // set the filled area
            set.setDrawFilled(true)
            set.fillFormatter = IFillFormatter { _, _ ->
                chart.axisLeft.axisMinimum
            }
            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                val drawable = ContextCompat.getDrawable(requireContext(), android.R.drawable.star_on)
               // set.fillDrawable = drawable
                set.fillColor = Color.BLACK
            } else {
                set.fillColor = Color.BLACK
            }
            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(set) // add the data sets
            // create a data object with the data sets
            val data = LineData(dataSets)
            // set data
            chart.data = data
        }
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        Log.i(TAG, "value selected: $e")
       // binding.card.root.visibility = View.VISIBLE
    }

    override fun onNothingSelected() {
        Log.i(TAG, "nothing selected")
       // binding.card.root.visibility = View.GONE
    }
}