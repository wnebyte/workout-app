package com.github.wnebyte.workoutapp.widget

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class MutableListAdapter<T, VH : RecyclerView.ViewHolder?>(
    diffUtil: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(diffUtil) {

    private lateinit var dataSet: MutableList<T>

    val removedItems: ArrayList<T> = arrayListOf()

    fun setDataSet(dataSet: MutableList<T>) {
        this.dataSet = dataSet
    }

    fun dataSetInsert(index: Int, item: T) {
        removedItems.remove(item)
        dataSet.add(index, item)
        notifyItemInserted(index)
    }

    fun dataSetRemove(index: Int) {
        val item = dataSet.removeAt(index)
        removedItems.add(item)
        notifyItemRemoved(index)
    }

    fun dataSetAdd(item: T) {
        dataSet.add(item)
        notifyItemInserted(itemCount)
    }
}