package com.enpassio.reactiveway

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ServiceGenerator {

    val API_BASE_URL = "https://api.github.com"

    private val httpClient = OkHttpClient.Builder()

    fun <S> createService(serviceClass: Class<S>): S {
        return createService(serviceClass, null)
    }

    fun <S> createService(
            serviceClass: Class<S>, authToken: String?): S {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        val client = httpClient
                .addInterceptor(interceptor)
                .addInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                            .header("Accept", "application/vnd.github.v3+json")
                            .header("Authorization", String.format("Bearer %s", authToken))
                            .method(original.method(), original.body())
                    val request = requestBuilder.build()
                    Log.v("my_tag", "url is: " + request.url()!!.toString())
                    chain.proceed(request)
                }
                .build()
        val retrofit = Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).client(client).build()
        return retrofit.create(serviceClass)
    }
}