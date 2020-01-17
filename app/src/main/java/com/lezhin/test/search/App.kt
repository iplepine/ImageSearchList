package com.lezhin.test.search

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.listener.RequestListener
import com.facebook.imagepipeline.listener.RequestLoggingListener
import com.facebook.imagepipeline.request.ImageRequest
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.fresco.FrescoImageLoader
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import io.realm.Realm
import io.realm.RealmConfiguration

class App : Application() {
    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        initLogger()

        initRealm()
        initFresco()
        BigImageViewer.initialize(FrescoImageLoader.with(applicationContext))
    }

    private fun initLogger() {
        Logger.addLogAdapter(AndroidLogAdapter())
    }

    private fun initRealm() {
        Realm.init(applicationContext)
        val realmConfig = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfig)
    }

    private fun initFresco() {
        val listeners = HashSet<RequestListener>().apply {
            add(MyRequestLogginListener())
        }

        val imagePipelineConfig = ImagePipelineConfig.newBuilder(this)
            .setRequestListeners(listeners)
            .build()

        Fresco.initialize(applicationContext, imagePipelineConfig)
    }

    class MyRequestLogginListener : RequestLoggingListener() {
        override fun onRequestFailure(
            request: ImageRequest?,
            requestId: String?,
            throwable: Throwable?,
            isPrefetch: Boolean
        ) {
            super.onRequestFailure(request, requestId, throwable, isPrefetch)
            Logger.e(
                "failed to load image\n" +
                        "request : %s\n" +
                        "error : %s", request.toString(), throwable.toString()
            )
        }
    }
}