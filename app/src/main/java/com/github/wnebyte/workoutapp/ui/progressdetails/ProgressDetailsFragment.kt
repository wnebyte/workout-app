package com.github.wnebyte.workoutapp.ui.progressdetails

import java.util.*
import kotlin.collections.ArrayList
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.databinding.FragmentProgressDetailsBinding
import com.github.wnebyte.workoutapp.databinding.SetItemBinding
import com.github.wnebyte.workoutapp.model.DataPoint
import com.github.wnebyte.workoutapp.model.ExerciseWithSets
import com.github.wnebyte.workoutapp.model.ProgressItem
import com.github.wnebyte.workoutapp.model.Set
import com.github.wnebyte.workoutapp.ui.AdapterUtil
import com.github.wnebyte.workoutapp.util.Extensions.Companion.format
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toDate

private const val TAG = "ProgressDetailsFragment"

class ProgressDetailsFragment :
    Fragment(), OnChartValueSelectedListener {

    private val vm: ProgressDetailsViewModel by viewModels()

    private val args: ProgressDetailsFragmentArgs by navArgs()

    private val adapter = SetAdapter()

    private var _binding: FragmentProgressDetailsBinding? = null

    private val binding get() = _binding!!

    private lateinit var chart: LineChart

    private lateinit var progressItem: ProgressItem

    private lateinit var mv: MarkerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressDetailsBinding
            .inflate(inflater, container, false)
        this.chart = binding.chart
        this.progressItem = args.progressItem
        this.mv = MarkerView(requireContext(), R.layout.exercise_card)
        return binding.root
    }

    /*
    SHOULD DISPLAY:
    graph of exercise specific data for a given month
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "x: ${progressItem.data.size}")
        val x = progressItem.data.map { v -> v.x }
        val y = progressItem.data.map { v -> v.y }

        for (i in x.indices) {
            Log.i(TAG, "x: ${x[i].toDate().format()}, y: ${y[i]}")
        }

        vm.workoutLiveData.observe(
            viewLifecycleOwner,
            { exercise ->
                exercise?.let {
                    Log.i(TAG, "got exercise: ${it.exercise.id}")
                    updateMarker(it)
                }
            }
        )

        init(progressItem.data, progressItem.name)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Todo: move out initialization
    private fun updateMarker(exercise: ExerciseWithSets) {
        val titleTv: TextView = mv.findViewById(R.id.title)
        val recyclerView: RecyclerView = mv.findViewById(R.id.recycler_view)
        val adapter = SetAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        titleTv.text = exercise.exercise.name
        adapter.submitList(exercise.sets)
    }

    private fun init(
        data: List<DataPoint>,
        name: String
    ) {
        val x: List<Long> = data.map { v -> v.x }
        val y: List<Float> = data.map { v -> v.y }
        chart.setBackgroundColor(Color.WHITE)
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        // set listeners
        chart.setOnChartValueSelectedListener(this)
        chart.setDrawGridBackground(false)

        mv.chartView = chart
        chart.marker = mv

        chart.isDragEnabled = true
        chart.isScaleXEnabled = true
        chart.isScaleYEnabled = true
        chart.setPinchZoom(true)

        val xAxis: XAxis = chart.xAxis
        xAxis.axisMinimum = x.minOf { v -> v }.toFloat()
        xAxis.axisMaximum = x.maxOf { v -> v }.toFloat()
        xAxis.valueFormatter = object: ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toLong().toDate().format("MM/dd")
            }
        }

        val yAxis: YAxis = chart.axisLeft
        // disable dual axis (only use LEFT axis)
        chart.axisRight.isEnabled = false

        // horizontal grid lines
      //  yAxis.enableGridDashedLine(10f, 10f, 0f)

        yAxis.axisMinimum = y.minOf { v -> v } / 2f
        yAxis.axisMaximum = y.maxOf { v -> v } * 1.5f

        // add data
        populateData(data,null, name)
        chart.animateX(1250)

        // get the legend (only possible after adding data)
        val legend = chart.legend
        legend.form = Legend.LegendForm.LINE
    }

    private fun populateData(
        data: List<DataPoint>,
        icon: Drawable? = null,
        name: String = "Dataset"
    ) {
        val len = data.size
        val entries: ArrayList<Entry> = ArrayList(len)

        for (i in data.indices) {
            val entry = Entry(data[i].x.toFloat(), data[i].y, icon, data[i].id)
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
           // set.enableDashedLine(10f, 5f, 0f)
            // black lines and points
            set.color = R.color.secondaryLight
            set.setCircleColor(R.color.secondaryLight)
            // line thickness and point size
            set.lineWidth = 1f
            set.circleRadius = 3f
            // draw points as solid circles
            set.setDrawCircleHole(false)
            // customize legend entry
            set.formLineWidth = 1f
           // set.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set.formSize = 18f
            // text size of values
            set.valueTextSize = 9f
            // draw selection line as dashed
            set.enableDashedHighlightLine(10f, 5f, 0f)
            // set the filled area
            set.setDrawFilled(true)
            set.fillFormatter = IFillFormatter { _, _ ->
                chart.axisLeft.axisMinimum
            }
            /*
            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.gradient_line)
                 set.fillDrawable = drawable
            } else {
                set.fillColor = R.color.colorAccent
            }
             */
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

    override fun onValueSelected(e: Entry, h: Highlight?) {
        Log.i(TAG, "value selected: ${e.data as UUID}")
        val id: UUID = e.data as UUID
        vm.loadExercise(id)
    }

    override fun onNothingSelected() {
        Log.i(TAG, "nothing selected")
       // binding.card.root.visibility = View.GONE
    }

    private inner class SetHolder(private val binding: SetItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var set: Set

        fun bind(set: Set) {
            this.set = set
            "${set.weights} x ${set.reps}".also { binding.tv.text = it }
        }
    }

    private inner class SetAdapter : ListAdapter<Set, SetHolder>
        (AdapterUtil.DIFF_UTIL_SET_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetHolder {
            val view = SetItemBinding
                .inflate(layoutInflater, parent, false)
            return SetHolder(view)
        }

        override fun onBindViewHolder(holder: SetHolder, position: Int) {
            val set = getItem(position)
            return holder.bind(set)
        }
    }
}