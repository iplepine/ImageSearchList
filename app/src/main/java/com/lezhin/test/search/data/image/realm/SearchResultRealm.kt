package com.lezhin.test.search.data.image.realm

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class SearchResultRealm : RealmObject {
    @PrimaryKey
    @Required
    open var pk: String = ""

    open var query: String = ""
    open var page: Int = 1
    open var images: RealmList<ImageResultRealm>? = null

    constructor() : super()

    constructor(query: String, page: Int, images: RealmList<ImageResultRealm>?) : super() {
        this.pk = query + page

        this.query = query
        this.page = page
        this.images = images
    }
}