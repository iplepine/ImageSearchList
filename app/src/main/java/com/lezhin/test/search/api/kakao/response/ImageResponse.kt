package com.lezhin.test.search.api.kakao.response

data class ImageResponse(val meta: Meta, var documents: ArrayList<ImageResult>) {
    data class Meta(var total_count: Int, var pageable_count: Int, var is_end: Boolean)
}