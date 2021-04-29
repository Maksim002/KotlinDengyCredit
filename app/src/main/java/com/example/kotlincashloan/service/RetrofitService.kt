package com.timelysoft.tsjdomcom.service

import com.bumptech.glide.util.Util
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitService {

    private val authInterceptor = Interceptor { chain ->
        val newUrl = chain.request().url
            .newBuilder()
            .build()

        val newRequest = chain.request()
            .newBuilder()
            .addHeader("Authorization", "bearer ")
            .url(newUrl)
            .build()

        chain.proceed(newRequest)
    }

    private val client =
        OkHttpClient().newBuilder()
            .protocols((listOf(Protocol.HTTP_1_1)))
            .addInterceptor(authInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .build()

    private fun retrofit(baseUrl: String = AppPreferences.urlApi.toString()): Retrofit =
        Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

    fun apiService(): ApiService {
        return retrofit().create(ApiService::class.java)
    }
}