package com.lezhin.test.search.data.image

import android.util.SparseArray
import com.lezhin.test.search.api.KakaoApi
import com.lezhin.test.search.api.kakao.response.ImageResponse
import com.lezhin.test.search.api.kakao.response.ImageResult
import com.lezhin.test.search.api.kakao.service.SearchService
import com.lezhin.test.search.data.image.realm.ImageResultRealm
import com.orhanobut.logger.Logger
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlin.math.min

object ImageRepository {
    val IMAGE_COUNT_PER_PAGE = min(50, SearchService.MAX_SIZE)

    var query: String = ""
    var page = 1
    var totalCount = 0
    var isEnd = false

    private val pagedImages: SparseArray<ArrayList<ImageResult>> = SparseArray()

    fun clear() {
        query = ""
        totalCount = 0
        page = 1
        isEnd = false
        pagedImages.clear()
    }

    fun getImages(query: String, page: Int = 1): Single<ImageResponse> {
        if (this.query != query) {
            clear()
        }

        this.query = query
        this.page = page

        return KakaoApi.createService(SearchService::class.java)
            .getImages(query, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { cacheImages(query, page, it) }
    }

    fun getMoreImages(): Single<ImageResponse> {
        return getImages(query, page + 1)
    }

    fun getImage(index: Int): ImageResult? {
        val page = getPageFromIndex(index)
        val pagedIndex = index % IMAGE_COUNT_PER_PAGE

        return pagedImages?.get(page)?.getOrNull(pagedIndex)
    }

    fun hasPage(page: Int): Boolean {
        return pagedImages.get(page) != null
    }

    fun isMaxPage(): Boolean {
        return SearchService.MAX_PAGE <= page
    }

    fun getPageFromIndex(index: Int): Int {
        return index / IMAGE_COUNT_PER_PAGE + 1
    }

    fun cacheImages(query: String, page: Int, imageResponse: ImageResponse) {
        if (this.query != query) {
            clear()
        }

        this.query = query
        this.page = page

        if (imageResponse.meta.is_end || isMaxPage()) {
            this.isEnd = true
        }

        if (pagedImages.get(page) != null) {
            pagedImages.remove(page)
        } else {
            totalCount += imageResponse.documents.size
        }

        pagedImages.put(page, imageResponse.documents)
    }

    fun cacheToDB(query: String, page: Int, imageResponse: ImageResponse) {
        Realm.getDefaultInstance()?.apply {
            beginTransaction()

            imageResponse.documents
                .map {
                    copyToRealmOrUpdate(ImageResultRealm.newInstance(totalCount++, it))
                    Logger.i(
                        "save to realm : $query\n" +
                                "$it"
                    )
                }

            totalCount = where(ImageResultRealm::class.java)
                .findAll()
                .size

            Logger.i("cache to realm, total count : $totalCount")

            commitTransaction()
        }
    }

    fun clearDB() {
        Realm.getDefaultInstance()?.apply {
            beginTransaction()
            delete(ImageResultRealm::class.java)
            commitTransaction()
        }
    }
}