package com.lezhin.test.search.api.kakao.response

data class ImageResult(
    var collection: String,
    var thumbnail_url: String,
    var image_url: String,
    var width: Int,
    var height: Int,
    var display_sitename: String,
    var doc_url: String,
    var datetime: String
) {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}