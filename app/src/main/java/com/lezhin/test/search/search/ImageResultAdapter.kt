package com.lezhin.test.search.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.lezhin.test.search.R
import com.lezhin.test.search.api.kakao.response.ImageResult
import com.lezhin.test.search.data.image.ImageRepository
import com.lezhin.test.search.search.viewmodel.SearchViewModel
import com.orhanobut.logger.Logger

class ImageResultAdapter(context: Context, var searchViewModel: SearchViewModel) :
    RecyclerView.Adapter<ImageResultAdapter.ImageResultViewHolder>() {
    private val inflater = LayoutInflater.from(context)

    var itemClickListener: OnItemClickListener? = null
    var loadMoreListener: LoadMoreListener? = null

    interface OnItemClickListener {
        fun onClickItem(position: Int)
    }

    interface LoadMoreListener {
        fun onLoadMore(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageResultViewHolder {
        val view = inflater.inflate(R.layout.item_search_image, parent, false)
        return ImageResultViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (ImageRepository.query == null) {
            return 0
        }

        return ImageRepository.totalCount
    }

    override fun onBindViewHolder(holder: ImageResultViewHolder, position: Int) {
        checkLoadMore(position)
        holder.bind(ImageRepository.getImage(position))
    }

    private fun checkLoadMore(position: Int) {
        if (searchViewModel.needSearchMore(position)) {
            loadMoreListener?.onLoadMore(position)
        }
    }

    inner class ImageResultViewHolder : RecyclerView.ViewHolder {
        val image = itemView.findViewById<SimpleDraweeView>(R.id.image)
        val title = itemView.findViewById<TextView>(R.id.title)
        val date = itemView.findViewById<TextView>(R.id.date)

        constructor(itemView: View) : super(itemView) {
            itemView.setOnClickListener {
                itemClickListener?.onClickItem(adapterPosition)
            }
        }

        fun bind(item: ImageResult?) {
            if (item == null) {
                bindProgress()
            } else {
                image.aspectRatio = item.width.toFloat() / item.height
                image.setImageURI(item.image_url)
                title.text = item.display_sitename
                date.text = item.datetime

                Logger.d(
                    "bind image\n" +
                            "url : ${item.image_url}\n" +
                            "width : ${item.width}   height : ${item.height}\n" +
                            "aspectRatio : ${image.aspectRatio}"
                )
            }
        }

        private fun bindProgress() {
            image.setActualImageResource(android.R.drawable.ic_menu_report_image)
            Logger.e("bind progress image")
        }
    }
}