package com.enpassio.reactiveway.flightapp.network

import com.enpassio.reactiveway.flightapp.app.Const
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


object ApiClient {
    private val TAG = ApiClient::class.java.simpleName
    private var retrofit: Retrofit? = null
    private val REQUEST_TIMEOUT = 60
    private var okHttpClient: OkHttpClient? = null


    val client: Retrofit
        get() {

            if (okHttpClient == null)
                initOkHttp()

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl(Const.BASE_URL)
                        .client(okHttpClient!!)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
            }
            return retrofit!!
        }

    private fun initOkHttp() {
        val httpClient = OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        httpClient.addInterceptor(interceptor)

        httpClient.addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                        .addHeader("Accept", "application/json")
                        .addHeader("Request-Type", "Android")
                        .addHeader("Content-Type", "application/json")

                val request = requestBuilder.build()
                return chain.proceed(request)
            }
        })

        okHttpClient = httpClient.build()
    }

    fun resetApiClient() {
        retrofit = null
        okHttpClient = null
    }
}