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
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
import com.github.wnebyte.workoutapp.util.Extensions.Companion.year

private const val TAG = "ProgressDetailsFragment"

class ProgressDetailsFragment :
    Fragment(), OnChartValueSelectedListener {

    private val vm: ProgressDetailsViewModel by viewModels()

    private val args: ProgressDetailsFragmentArgs by navArgs()

    private var _binding: FragmentProgressDetailsBinding? = null

    private val binding get() = _binding!!

    private var _chart: LineChart? = null

    private val chart get() = _chart!!

    private var _mv: MarkerView? = null

    private val mv get() = _mv!!

    private var _titleTv: TextView? = null

    private val titleTv get() = _titleTv!!

    private var _recyclerView: RecyclerView? = null

    private val recyclerView get() = _recyclerView!!

    private lateinit var progressItem: ProgressItem

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressDetailsBinding
            .inflate(inflater, container, false)
        _chart = binding.chart
        _mv = MarkerView(requireContext(), R.layout.exercise_card)
        _titleTv = mv.findViewById(R.id.title)
        _recyclerView = mv.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        progressItem = args.progressItem
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "got: ${progressItem.data.size} data-points")
        vm.exerciseLiveData.observe(
            viewLifecycleOwner,
            { exercise ->
                exercise?.let {
                    Log.i(TAG, "got exercise: ${it.exercise.id}")
                    updateMarkerView(it)
                }
            }
        )
        val data: List<DataPoint> = progressItem.data
        val name = progressItem.name
        initGraph(data, name)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _chart = null
        _mv = null
        _titleTv = null
        _recyclerView = null
    }

    // Todo: move out initialization
    private fun updateMarkerView(exercise: ExerciseWithSets) {
        val adapter = SetAdapter()
        recyclerView.adapter = adapter
        titleTv.text = exercise.exercise.name
        adapter.submitList(exercise.sets)
    }

    private fun initGraph(
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

        val isEnabled: Boolean = data.size > 1
        chart.isDragEnabled = isEnabled
        chart.isScaleXEnabled = isEnabled
        chart.isScaleYEnabled = isEnabled
        chart.setPinchZoom(isEnabled)

        val xAxis: XAxis = chart.xAxis
        val xMin =  x.minOf { v -> v }
        val xMax = x.maxOf { v -> v }
        val sdf = "yy/MM/dd"
        xAxis.axisMinimum = xMin.toFloat()
        xAxis.axisMaximum = xMax.toFloat()
        xAxis.labelRotationAngle = 90f
        xAxis.valueFormatter = object: ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toLong().toDate().format(sdf)
            }
        }

        val yAxis: YAxis = chart.axisLeft
        // disable dual axis (only use LEFT axis)
        chart.axisRight.isEnabled = false

        yAxis.axisMinimum = y.minOf { v -> v } / 2f
        yAxis.axisMaximum = y.maxOf { v -> v } * 1.5f

        // add data
        populateGraph(data,null, name)
        chart.animateX(1250)

        // get the legend (only possible after adding data)
        val legend = chart.legend
        legend.form = Legend.LegendForm.LINE
    }

    private fun populateGraph(
        data: List<DataPoint>,
        icon: Drawable? = null,
        name: String = "Dataset"
    ) {
        val len = data.size
        val entries: List<Entry> = List(len) { i ->
            Entry(
                data[i].x.toFloat(),
                data[i].y,
                icon,
                data[i].id
            )
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
            set.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
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
            val lineData = LineData(dataSets)
            // set data
            chart.data = lineData
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