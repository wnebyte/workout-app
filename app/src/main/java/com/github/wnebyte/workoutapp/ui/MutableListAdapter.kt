package com.github.wnebyte.workoutapp.ui

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class MutableListAdapter<T, VH : RecyclerView.ViewHolder?>(
    private val dataSet: MutableList<T>, diffUtil: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(diffUtil) {

    val removedItems: ArrayList<T> = arrayListOf()

    fun insert(item: T, index: Int) {
        removedItems.remove(item)
        dataSet.add(index, item)
        notifyItemInserted(index)
    }

    fun remove(index: Int) {
        val item = dataSet.removeAt(index)
        removedItems.add(item)
        notifyItemRemoved(index)
    }

    fun add(item: T) {
        dataSet.add(item)
        notifyItemInserted(itemCount)
    }
}