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

import android.util.Log
import okio.Buffer

interface ApiService {
    @POST("api/v1/med-requests")
    suspend fun postMedRequest(
        @Body request: CamRequestUrlModel
    ): Response<ResponseUrlModel>

    @POST("api/v1/med-requests/list-url")
    suspend fun listUrl(
        @Body request: CamRequestListUrlModel
    ): Response<ResponseUrlModel>
}


object RetrofitClient {
    private val BASE_URL: String
        get() = SilverguardCam.getBaseUrl()

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

    private val loggingInterceptor = Interceptor { chain ->
        val request = chain.request()

        // log request
        try {
            val requestBody = request.body
            var requestBodyString: String? = null
            if (requestBody != null) {
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                requestBodyString = buffer.readUtf8()
            }

            val headers = request.headers.names().joinToString(separator = "\n") { name ->
                val value = if (name.equals("Authorization", ignoreCase = true)) "REDACTED" else request.header(name)
                "$name: $value"
            }

            Log.i("ApiLogging", "REQUEST --> ${request.method} ${request.url}\nHeaders:\n${headers}\nBody:${requestBodyString ?: "<empty>"}")
        } catch (t: Throwable) {
            Log.w("ApiLogging", "Failed to log request", t)
        }

        val response = chain.proceed(request)

        // log response
        try {
            val responseBody = response.body
            val content = responseBody?.string()
            val contentType = responseBody?.contentType()
            Log.i("ApiLogging", "RESPONSE <-- ${response.code} ${response.message} for ${response.request.url}\nBody:${content ?: "<empty>"}")

            // need to recreate response body because .string() consumes it
            val newResponseBody = content?.let { okhttp3.ResponseBody.create(contentType, it) }
            return@Interceptor response.newBuilder().body(newResponseBody).build()
        } catch (t: Throwable) {
            Log.w("ApiLogging", "Failed to log response", t)
            return@Interceptor response
        }
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
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