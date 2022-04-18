package com.github.wnebyte.workoutapp.ui.progress

import java.lang.Exception
import java.lang.IllegalStateException
import kotlin.math.abs
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.MenuCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.databinding.FragmentProgressBinding
import com.github.wnebyte.workoutapp.databinding.ProgressItemCardAltBinding
import com.github.wnebyte.workoutapp.databinding.ProgressItemCardBinding
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toSign
import com.github.wnebyte.workoutapp.util.TemporalRange
import com.github.wnebyte.workoutapp.ui.AdapterUtil
import com.github.wnebyte.workoutapp.ui.OnSwipeListener
import com.github.wnebyte.workoutapp.model.ProgressItem

private const val TAG = "ProgressFragment"

class ProgressFragment : Fragment(), OnSwipeListener {

    interface Callbacks {
        fun onProgressDetails(progressItem: ProgressItem)
    }

    private val vm: ProgressViewModel by viewModels()

    private val adapter = ProgressItemAdapter()

    private val binding get() = _binding!!

    private val gestureDetector get() = _gestureDetector!!

    private var _binding: FragmentProgressBinding? = null

    private var _gestureDetector: GestureDetectorCompat? = null

    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callbacks = context as Callbacks
        } catch (e: Exception) {
            throw IllegalStateException(
                "Hosting activity need to implement callbacks interface"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_progress, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)
        val tr: TemporalRange = vm.getTemporalRange()
        when (tr.field) {
            TemporalRange.MONTH -> {
                menu.findItem(R.id.by_month).isChecked = true
            }
            TemporalRange.YEAR -> {
                menu.findItem(R.id.by_year).isChecked = true
            }
            TemporalRange.DATE -> {
                when (tr.amount) {
                    30 -> {
                        menu.findItem(R.id.by_30_days).isChecked = true
                    }
                    60 -> {
                        menu.findItem(R.id.by_60_days).isChecked = true
                    }
                    90 -> {
                        menu.findItem(R.id.by_90_days).isChecked = true
                    }
                    180 -> {
                        menu.findItem(R.id.by_180_days).isChecked = true
                    }
                    365 -> {
                        menu.findItem(R.id.by_365_days).isChecked = true
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.forward_arrow -> {
                incrementRange()
                true
            }
            R.id.backward_arrow -> {
                decrementRange()
                true
            }
            R.id.by_month -> {
                item.isChecked = true
                vm.setTemporalRange(
                    TemporalRange.newInstance(TemporalRange.MONTH))
                true
            }
            R.id.by_year -> {
                item.isChecked = true
                vm.setTemporalRange(
                    TemporalRange.newInstance(TemporalRange.YEAR))
                true
            }
            R.id.by_30_days -> {
                item.isChecked = true
                vm.setTemporalRange(
                    TemporalRange.newInstance(TemporalRange.DATE, 30))
                true
            }
            R.id.by_60_days -> {
                item.isChecked = true
                vm.setTemporalRange(
                    TemporalRange.newInstance(TemporalRange.DATE, 60))
                true
            }
            R.id.by_90_days -> {
                item.isChecked = true
                vm.setTemporalRange(
                    TemporalRange.newInstance(TemporalRange.DATE, 90))
                true
            }
            R.id.by_180_days -> {
                item.isChecked = true
                vm.setTemporalRange(
                    TemporalRange.newInstance(TemporalRange.DATE, 180))
                true
            }
            R.id.by_365_days -> {
                item.isChecked = true
                vm.setTemporalRange(
                    TemporalRange.newInstance(TemporalRange.DATE, 365))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressBinding
            .inflate(layoutInflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        _gestureDetector = GestureDetectorCompat(requireContext(), this)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.progressItemListLiveData.observe(
            viewLifecycleOwner,
            { items ->
                items?.let {
                    Log.i(TAG, "got ${it.size} items")
                    updateUI(it)
                }
            }
        )
        binding.root.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }

    }

    override fun onDestroy() {
        _gestureDetector = null
        callbacks = null
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSwipeLeft() {
        incrementRange()
    }

    override fun onSwipeRight() {
        decrementRange()
    }

    private fun updateUI(items: List<ProgressItem>) {
        binding.dateTv.text = vm.getTemporalRange().toString()
        adapter.submitList(items)
    }

    private fun incrementRange() {
        binding.recyclerView.itemAnimator = SlideInRightAnimator().apply {
            addDuration = 350
        }
        vm.incrementRange()
    }

    private fun decrementRange() {
        binding.recyclerView.itemAnimator = SlideInLeftAnimator().apply {
            addDuration = 350
        }
        vm.decrementRange()
    }

    private inner class ProgressItemHolder(private val binding: ProgressItemCardBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        private lateinit var item: ProgressItem

        init {
            binding.body.root.setOnClickListener(this)
        }

        fun bind(item: ProgressItem) {
            this.item = item
            binding.body.nameTv.text = item.name
            binding.body.avgTv.text = String.format("%.2f", item.avgWeights)
                .replace(",", ".")
            binding.body.unitTv.text = item.unit
            (item.change.toSign() + String.format("%.2f", abs(item.change) * 100) + "%")
                .also { binding.body.percentageTv.text = it }
            /*
            when (item.change < 0) {
                true -> {
                    binding.body.percentageTv
                        .setTextColor(resources.getColor(R.color.red_660, theme))
                }
                false -> {
                    binding.body.percentageTv
                        .setTextColor(resources.getColor(R.color.green, theme))
                }
            }
             */
        }

        override fun onClick(v: View?) {
            Log.i(TAG, "height: ${this.itemView.height}")
            navEdit()
        }

        private fun navEdit() {
            callbacks?.onProgressDetails(item)
        }
    }

    private inner class ProgressItemAdapter :
        ListAdapter<ProgressItem, ProgressItemHolder>
            (AdapterUtil.DIFF_UTIL_PROGRESS_ITEM_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressItemHolder {
            val view = ProgressItemCardBinding
                .inflate(layoutInflater, parent, false)
            return ProgressItemHolder(view)
        }

        override fun onBindViewHolder(holder: ProgressItemHolder, position: Int) {
            val item = getItem(position)
            return holder.bind(item)
        }
    }
    
    private inner class ProgressItemAltHolder(private val binding: ProgressItemCardAltBinding):
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        private lateinit var progressItem: ProgressItem

        fun bind(progressItem: ProgressItem) {
            this.progressItem = progressItem
        }

        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }
    }

    private inner class ProgressItemAltAdapter : ListAdapter<ProgressItem, ProgressItemAltHolder>
        (AdapterUtil.DIFF_UTIL_PROGRESS_ITEM_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressItemAltHolder {
            val view = ProgressItemCardAltBinding
                .inflate(layoutInflater, parent, false)
            return ProgressItemAltHolder(view)
        }

        override fun onBindViewHolder(holder: ProgressItemAltHolder, position: Int) {
            val item = getItem(position)
            return holder.bind(item)
        }
    }
}
