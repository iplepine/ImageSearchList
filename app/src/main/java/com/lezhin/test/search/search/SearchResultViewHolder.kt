package com.lezhin.test.search.search

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lezhin.test.search.api.kakao.response.ImageResult

open class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open fun bind(result: ImageResult?) {}
}