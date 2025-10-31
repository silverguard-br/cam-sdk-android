package com.silverguard.cam.core.network

import com.silverguard.cam.core.config.SilverguardCam
import com.silverguard.cam.core.model.CamRequestListUrlModel
import com.silverguard.cam.core.model.CamRequestUrlModel
import com.silverguard.cam.core.model.ResponseUrlModel
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/v1/med-requests")
    suspend fun postMedRequest(
        @Body request: CamRequestUrlModel
    ): Response<ResponseUrlModel>

    @POST("/api/v1/med-requests/list-url")
    suspend fun listUrl(
        @Body request: CamRequestListUrlModel
    ): Response<ResponseUrlModel>
}


object RetrofitClient {
    private const val BASE_URL = "https://test.camapi.sosgolpe.com.br/"

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("Authorization", "Bearer ${SilverguardCam.getApiKey()}")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .addHeader("Cache-Control", "no-cache")
            .addHeader("Connection", "keep-alive")
        val request = requestBuilder.build()
        chain.proceed(request)
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}