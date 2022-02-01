package com.github.wnebyte.workoutapp.ui.progress

import android.annotation.SuppressLint
import android.content.Context
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
import com.github.wnebyte.workoutapp.databinding.ProgressItemCardBinding
import com.github.wnebyte.workoutapp.util.Extensions.Companion.month
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toSign
import com.github.wnebyte.workoutapp.util.Extensions.Companion.year
import com.github.wnebyte.workoutapp.model.ProgressItem
import com.github.wnebyte.workoutapp.ui.AdapterUtil
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator
import java.lang.Exception
import java.lang.IllegalStateException
import java.text.DateFormatSymbols

private const val TAG = "ProgressFragment"

class ProgressFragment : Fragment() {

    interface Callbacks {
        fun onProgressDetails(progressItem: ProgressItem)
    }

    private val vm: ProgressViewModel by viewModels()

    private val adapter = ProgressItemAdapter()

    private val dfs = DateFormatSymbols.getInstance()

    private val binding get() = _binding!!

    private var _binding: FragmentProgressBinding? = null

    private var _gestureDetector: GestureDetectorCompat? = null

    private val gestureDetector get() = _gestureDetector!!

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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.forward_arrow -> {
                incrementMonth()
                true
            }
            R.id.backward_arrow -> {
                decrementMonth()
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
        /*
        binding.root.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }
         */
    }

    override fun onDestroy() {
        _gestureDetector = null
        callbacks = null
        super.onDestroy()
    }

    /*
    override fun onSwipeLeft() {
        incrementMonth()
    }

    override fun onSwipeRight() {
        decrementMonth()
    }
     */

    private fun incrementMonth() {
        binding.recyclerView.itemAnimator = SlideInRightAnimator().apply {
            addDuration = 350
        }
        vm.incrementMonth()
    }

    private fun decrementMonth() {
        binding.recyclerView.itemAnimator = SlideInLeftAnimator().apply {
            addDuration = 350
        }
        vm.decrementMonth()
    }

    private fun updateUI(items: List<ProgressItem>) {
        val date = vm.getDate()
        ("${dfs.months[date.month()]} ${date.year()}")
            .also { binding.dateTv.text = it }
        adapter.submitList(items)
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
            binding.body.avgTv.text = String.format("%d(%.2f", item.y.size, item.avgWeights)
            (item.unit + ")").also { binding.body.unitTv.text = it }
            (item.change.toSign() + String.format("%.2f", item.change * 100) + "%")
                .also { binding.body.percentageTv.text = it }
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
}
