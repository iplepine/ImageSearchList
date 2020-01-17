package com.lezhin.test.search.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.lezhin.test.search.R
import com.lezhin.test.search.api.kakao.response.ImageResult
import com.lezhin.test.search.data.image.ImageRepository
import com.lezhin.test.search.search.viewmodel.SearchViewModel
import com.orhanobut.logger.Logger

class ImageResultAdapter(context: Context, var searchViewModel: SearchViewModel) :
    RecyclerView.Adapter<SearchResultViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    var itemClickListener: OnItemClickListener? = null
    var loadMoreListener: LoadMoreListener? = null

    object ViewType {
        const val IMAGE = 0
        const val END = 1
    }

    interface OnItemClickListener {
        fun onClickItem(position: Int)
    }

    interface LoadMoreListener {
        fun onLoadMore(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        return if (viewType == ViewType.IMAGE) {
            val view = inflater.inflate(R.layout.item_search_image, parent, false)
            ImageResultViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_search_end, parent, false)
            EndViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        val imageCount = ImageRepository.totalCount

        return when {
            imageCount != 0 && ImageRepository.isEnd -> imageCount + 1
            else -> imageCount
        }
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        checkLoadMore(position)
        if (getItemViewType(position) == ViewType.IMAGE) {
            holder.bind(ImageRepository.getImage(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemCount - 1 == position) {
            ViewType.END
        } else
            ViewType.IMAGE
    }

    private fun checkLoadMore(position: Int) {
        if (searchViewModel.needSearchMore(position)) {
            loadMoreListener?.onLoadMore(position)
        }
    }

    inner class ImageResultViewHolder : SearchResultViewHolder {
        private val image = itemView.findViewById<SimpleDraweeView>(R.id.image)

        constructor(itemView: View) : super(itemView) {
            itemView.setOnClickListener {
                itemClickListener?.onClickItem(adapterPosition)
            }
        }

        override fun bind(result: ImageResult?) {
            if (result == null) {
                bindErrorImage()
            } else {
                image.aspectRatio = result.width.toFloat() / result.height
                image.setImageURI(result.image_url)

                Logger.d(
                    "bind image    position : $adapterPosition\n" +
                            "url : ${result.image_url}\n" +
                            "width : ${result.width}   height : ${result.height}\n" +
                            "aspectRatio : ${image.aspectRatio}"
                )
            }
        }

        private fun bindErrorImage() {
            image.setActualImageResource(android.R.drawable.ic_menu_report_image)
            Logger.e("bind progress image")
        }
    }
}