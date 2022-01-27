package com.github.wnebyte.workoutapp.ui.progress

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.wnebyte.workoutapp.R
import com.github.wnebyte.workoutapp.databinding.FragmentProgressBinding
import com.github.wnebyte.workoutapp.databinding.ProgressItemBinding
import com.github.wnebyte.workoutapp.util.Extensions.Companion.month
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toSign
import com.github.wnebyte.workoutapp.util.Extensions.Companion.year
import com.github.wnebyte.workoutapp.model.ProgressItem
import com.github.wnebyte.workoutapp.ui.AdapterUtil
import com.github.wnebyte.workoutapp.ui.OnSwipeListener
import java.text.DateFormatSymbols

private const val TAG = "ProgressFragment"

class ProgressFragment : Fragment(), OnSwipeListener {

    private val vm: ProgressViewModel by viewModels()

    private val adapter = ProgressItemAdapter()

    private val dfs = DateFormatSymbols.getInstance()

    private val binding get() = _binding!!

    private var _binding: FragmentProgressBinding? = null

    private var gestureDetector: GestureDetectorCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.forward_arrow -> {
                vm.incrementMonth()
                true
            }
            R.id.backward_arrow -> {
                vm.decrementMonth()
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
        gestureDetector = GestureDetectorCompat(requireContext(), this)
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
            gestureDetector?.onTouchEvent(event)
            true
        }
    }

    override fun onSwipeLeft() {
        vm.incrementMonth()
    }

    override fun onSwipeRight() {
        vm.decrementMonth()
    }

    override fun onDestroy() {
        gestureDetector = null
        super.onDestroy()
    }

    private fun updateUI(items: List<ProgressItem>) {
        val date = vm.getDate()
        ("${dfs.months[date.month()]} ${date.year()}")
            .also { binding.dateTv.text = it }
        adapter.submitList(items)
    }

    private inner class ProgressItemHolder(private val binding: ProgressItemBinding):
        RecyclerView.ViewHolder(binding.root) {
            private lateinit var item: ProgressItem

            fun bind(item: ProgressItem) {
                this.item = item
                binding.nameTv.text = item.name
                binding.avgTv.text = String.format("%.2f", item.avg)
                binding.unitTv.text = item.unit
                (item.change.toSign() + String.format("%.2f", item.change * 100) + "%")
                    .also {  binding.percentageTv.text = it }
            }
        }

    private inner class ProgressItemAdapter:
        ListAdapter<ProgressItem, ProgressItemHolder>
            (AdapterUtil.DIFF_UTIL_PROGRESS_ITEM_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressItemHolder {
            val view = ProgressItemBinding
                .inflate(layoutInflater, parent, false)
            return ProgressItemHolder(view)
        }

        override fun onBindViewHolder(holder: ProgressItemHolder, position: Int) {
            val item = getItem(position)
            return holder.bind(item)
        }
    }
}
