package com.lezhin.test.search.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class KakaoApi {
    companion object APP_KEY {
        private val NATIVE_APP_KEY = "4dac325989780b11788273394582f480"
        private val REST_API_KEY = "0316902138303f3ee497df6fb93f531f"

        private fun getRetrofit(): Retrofit {
            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)

            val kakaoClient = OkHttpClient
                .Builder()
                .addInterceptor(KakaoInterceptor())
                .addInterceptor(logInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl("https://dapi.kakao.com")
                .client(kakaoClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }

        fun <T> createService(service: Class<T>): T {
            return getRetrofit().create(service)
        }
    }

    class KakaoInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val newRequest =
                chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "KakaoAK $REST_API_KEY")
                    .build()
            return chain.proceed(newRequest)
        }
    }
}