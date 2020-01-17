package com.lezhin.test.search.data.image

import android.util.SparseArray
import com.lezhin.test.search.api.kakao.response.ImageResponse
import com.lezhin.test.search.api.kakao.response.ImageResult
import com.lezhin.test.search.data.image.realm.ImageResultRealm
import com.orhanobut.logger.Logger
import io.reactivex.Single
import io.realm.Realm

object ImageRepository {
    var query: String = ""
    var page = 1
    var totalCount = 0
    var isEnd = false

    val pagedImages: SparseArray<ArrayList<ImageResult>> = SparseArray()

    const val IMAGE_COUNT_PER_PAGE = 5

    fun clear() {
        query = ""
        totalCount = 0
        page = 1
        isEnd = false
        pagedImages.clear()
        clearDB()
    }

    fun getImage(index: Int): ImageResult? {
        val page = getPageFromIndex(index)
        val pagedIndex = index % IMAGE_COUNT_PER_PAGE

        return pagedImages?.get(page)?.getOrNull(pagedIndex)
    }

    fun hasPage(page: Int): Boolean {
        return pagedImages.get(page) != null
    }

    fun getPageFromIndex(index: Int): Int {
        return index / IMAGE_COUNT_PER_PAGE + 1
    }

    fun cacheImage(query: String, page: Int, imageResponse: ImageResponse): Single<ImageResponse> {
        if (this.query != query) {
            clear()
        }

        this.query = query
        this.page = page

        if (imageResponse.meta.is_end) {
            this.isEnd = true
        }

        if (pagedImages.get(page) != null) {
            pagedImages.remove(page)
        } else {
            //totalCount += imageResponse.documents.size
        }

        pagedImages.put(page, imageResponse.documents)

        cacheToDB(query, page, imageResponse)

        return Single.just(imageResponse)
    }

    fun cacheToDB(query: String, page: Int, imageResponse: ImageResponse) {
        Realm.getDefaultInstance()?.apply {
            beginTransaction()

            imageResponse.documents
                .map {
                    copyToRealmOrUpdate(ImageResultRealm.newInstance(it))
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