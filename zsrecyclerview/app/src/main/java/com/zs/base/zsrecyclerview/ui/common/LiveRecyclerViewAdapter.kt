package com.zs.base.zsrecyclerview.ui.common

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference


abstract class LiveViewAdapter<T : LiveViewHolder>(lifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<T>() {

    private val lifecycleOwner = WeakReference(lifecycleOwner)
    protected val items = ArrayList<LiveItemViewModel>()

    fun setData(data: List<LiveItemViewModel>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    fun getItems(): List<LiveItemViewModel> {
        return items
    }

    override fun getItemCount(): Int {
        return getItems().size
    }

    override fun onBindViewHolder(holder: T, position: Int) {
        val item = getItems()[position]
        holder.bind(item)
    }

    override fun onViewDetachedFromWindow(holder: T) {
        super.onViewDetachedFromWindow(holder)
        holder.binding.lifecycleOwner = lifecycleOwner.get()
    }

    override fun onViewAttachedToWindow(holder: T) {
        super.onViewAttachedToWindow(holder)
        // 라이프사이클 오우너를 등록해줘야함
        holder.binding.lifecycleOwner = null
    }
}
