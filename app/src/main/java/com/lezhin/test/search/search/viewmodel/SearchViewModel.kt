package com.lezhin.test.search.search.viewmodel

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lezhin.test.search.api.KakaoApi
import com.lezhin.test.search.api.kakao.response.ImageResponse
import com.lezhin.test.search.api.kakao.response.ImageResult
import com.lezhin.test.search.api.kakao.service.SearchService
import com.lezhin.test.search.data.image.ImageRepository
import com.orhanobut.logger.Logger
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SearchViewModel : ViewModel() {
    val imageResponse = MutableLiveData<ImageResponse>()
    val isSearching = MutableLiveData<Boolean>().apply { value = false }

    val emptyMessageVisible = MutableLiveData<Boolean>().apply { value = false }

    var query = ""
    var page = 1

    var scrollState: Parcelable? = null

    init {
        clear()
    }

    fun clear() {
        query = ""
        page = 1
        isSearching.value = false
        imageResponse.value = null
        ImageRepository.clear()
    }

    fun search(query: String, page: Int = 1): Single<ImageResponse> {
        this.query = query
        this.page = page

        isSearching.postValue(true)

        Logger.d(
            "try search\n" +
                    "query : %s     page : %d", query, page
        )

        return KakaoApi.createService(SearchService::class.java)
            .getImages(query, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { ImageRepository.cacheImage(query, page, it) }
            .doOnSuccess {
                onSearchSuccess(it)
            }
    }

    fun searchMore(position: Int): Single<ImageResponse> {
        val page = ImageRepository.getPageFromIndex(position)
        return search(query, page + 1)
    }

    private fun onSearchSuccess(imageResponse: ImageResponse) {
        this.imageResponse.value = imageResponse
        isSearching.postValue(false)
        checkEmpty()

        Logger.i(
            "search result : $query\n     page : $page" +
                    "meta : ${imageResponse.meta}"
        )
    }

    fun needSearchMore(position: Int): Boolean {
        val currentPage = ImageRepository.getPageFromIndex(position)
        return !ImageRepository.isEnd && !ImageRepository.hasPage(currentPage + 1)
    }

    private fun checkEmpty() {
        emptyMessageVisible.value = isEmptyResult()
    }

    fun isEmptyResult(): Boolean {
        return ImageRepository.isEnd && ImageRepository.totalCount == 0
    }

    fun isEnd(): Boolean {
        return ImageRepository.isEnd && ImageRepository.totalCount != 0
    }

    fun getImage(position: Int): ImageResult? {
        return ImageRepository.getImage(position)
    }
}