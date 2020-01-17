package com.lezhin.test.search.data.image.realm

import com.lezhin.test.search.api.kakao.response.ImageResult
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class ImageResultRealm : RealmObject() {
    open var collection: String = ""
    open var thumbnail_url: String = ""
    open var image_url: String = ""
    open var width: Int = 0
    open var height: Int = 0
    open var display_sitename: String = ""
    open var doc_url: String = ""
    open var datetime: String = ""

    @PrimaryKey
    @Required
    open var pk: String = ""
    open var index : Int = 0

    companion object {
        fun newInstance(index: Int, imageResult: ImageResult): ImageResultRealm {
            return ImageResultRealm().apply {
                collection = imageResult.collection
                thumbnail_url = imageResult.thumbnail_url
                image_url = imageResult.image_url
                width = imageResult.width
                height = imageResult.height
                display_sitename = imageResult.display_sitename
                doc_url = imageResult.doc_url
                datetime = imageResult.datetime

                pk = image_url + datetime
                this.index = index
            }
        }
    }

    fun toImageResult(): ImageResult {
        return ImageResult(
            collection,
            thumbnail_url,
            image_url,
            width,
            height,
            display_sitename,
            doc_url,
            datetime
        )
    }
}