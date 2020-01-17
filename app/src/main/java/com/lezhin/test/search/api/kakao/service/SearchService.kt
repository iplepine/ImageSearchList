package com.lezhin.test.search.api.kakao.service

import com.lezhin.test.search.api.kakao.response.ImageResponse
import com.lezhin.test.search.data.image.ImageRepository
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {
    companion object {
        object SortType {
            val ACCURACY = "accuracy"
            val RECENCY = "recency"
        }
    }

    @GET("/v2/search/image")
    fun getImages(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = ImageRepository.IMAGE_COUNT_PER_PAGE,
        @Query("sort") sort: String = SortType.ACCURACY
    ): Single<ImageResponse>
}