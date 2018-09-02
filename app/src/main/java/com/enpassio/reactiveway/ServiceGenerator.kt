package com.enpassio.reactiveway

import android.text.TextUtils
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


object ServiceGenerator {
    val API_BASE_URL = "https://api.github.com/"
    private var retrofit: Retrofit? = null


    fun <S> createService(serviceClass: Class<S>): S {
        return createService(serviceClass, null)
    }

    fun <S> createService(
            serviceClass: Class<S>, authToken: String?): S {
        if (!TextUtils.isEmpty(authToken)) {

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request = chain.request()
                            .newBuilder()
                            .header("Accept", "application/json")
                            .build()
                    Log.v("my_tag", "token is: " + authToken)
                    return chain.proceed(request)
                }
            }).build()

            retrofit = Retrofit.Builder()
                    .baseUrl(ServiceGenerator.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
        }
        return retrofit?.create(serviceClass)!!
    }
}