package com.lezhin.test.search.search.viewmodel

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lezhin.test.search.api.kakao.response.ImageResponse
import com.lezhin.test.search.api.kakao.response.ImageResult
import com.lezhin.test.search.data.image.ImageRepository
import io.reactivex.Single

class SearchViewModel : ViewModel() {
    val isSearching = MutableLiveData<Boolean>().apply { value = false }

    val emptyMessageVisible = MutableLiveData<Boolean>().apply { value = false }

    var scrollState: Parcelable? = null

    init {
        clear()
    }

    fun clear() {
        isSearching.value = false
        ImageRepository.clear()
    }

    fun search(query: String): Single<ImageResponse> {
        isSearching.postValue(true)
        return ImageRepository.getImages(query).doOnSuccess { onSearchSuccess() }
    }

    fun searchMore(): Single<ImageResponse> {
        return ImageRepository.getMoreImages().doOnSuccess { onSearchSuccess() }
    }

    private fun onSearchSuccess() {
        isSearching.postValue(false)
        checkEmpty()
    }

    fun needSearchMore(position: Int): Boolean {
        if (ImageRepository.isMaxPage()) {
            return false
        }

        val currentPage = ImageRepository.getPageFromIndex(position)
        return !ImageRepository.isEnd && !ImageRepository.hasPage(currentPage + 1)
    }

    private fun checkEmpty() {
        emptyMessageVisible.value = isEmptyResult()
    }

    fun isEmptyResult(): Boolean {
        return ImageRepository.isEnd && ImageRepository.totalCount == 0
    }

    fun getImage(position: Int): ImageResult? {
        return ImageRepository.getImage(position)
    }
}