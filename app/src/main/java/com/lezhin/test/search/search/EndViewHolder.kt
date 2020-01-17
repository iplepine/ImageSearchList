package com.lezhin.test.search.search

import android.view.View
import android.widget.TextView
import com.lezhin.test.search.R
import com.lezhin.test.search.api.kakao.response.ImageResult
import com.lezhin.test.search.data.image.ImageRepository

class EndViewHolder(itemView: View) : SearchResultViewHolder(itemView) {
    private val textView = itemView.findViewById<TextView>(R.id.title)

    override fun bind(result: ImageResult?) {
        if (ImageRepository.isMaxPage()) {
            textView.setText(R.string.error_message_max_page)
        } else {
            textView.setText(R.string.error_message_end)
        }
    }
}